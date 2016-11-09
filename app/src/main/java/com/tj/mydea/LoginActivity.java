package com.tj.mydea;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

import io.fabric.sdk.android.Fabric;

public class LoginActivity extends AppCompatActivity {

    private CallbackManager callbackManager;

    @Override
    public void onBackPressed() {
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        //Intent intent = getIntent();
        SendBird.init("8C25C95D-2021-4A1D-B6C3-77C8E14EF727", LoginActivity.this);
        FacebookSdk.sdkInitialize(getApplicationContext(), new FacebookSdk.InitializeCallback() {
            @Override
            public void onInitialized() {
                if(AccessToken.getCurrentAccessToken() != null) {
                    GraphRequest request = GraphRequest.newMeRequest(
                            AccessToken.getCurrentAccessToken(),
                            new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(JSONObject object, GraphResponse response) {
                                    try {
                                        object.put("user_id", object.get("id"));
                                        object.remove("id");
                                        object.put("user_name", object.get("name"));
                                        object.remove("name");
                                        postUser(object);
                                        connect_to_sendbird(object.get("user_id").toString(), object.get("user_name").toString());
                                        Intent intent = new Intent(LoginActivity.this, NaviActivity.class);
                                        intent.putExtra("user_id", (String) object.get("user_id"));
                                        intent.putExtra("user_name", (String) object.get("user_name"));
                                        intent.putExtra("email", (String) object.get("email"));
                                       // intent.putExtra("imageURI", object.getJSONObject("picture").getJSONObject("data").getString("url"));
                                        startActivity(intent);
                                    }
                                    catch (JSONException e) {Log.v("LoginActivity", e.toString());}
                                }
                            });
                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id,name,email");
                    request.setParameters(parameters);
                    request.executeAsync();
                } else {Log.v("WARNING", "Not logged in");}
            }
        });

        setContentView(R.layout.activity_login);

        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email"));

        loginButton.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object, GraphResponse response) {
                                        try {
                                            object.put("user_id", object.get("id"));
                                            object.remove("id");
                                            object.put("user_name", object.get("name"));
                                            object.remove("name");
                                            postUser(object);
                                            connect_to_sendbird(object.get("user_id").toString(), object.get("user_name").toString());
                                            Intent intent = new Intent(LoginActivity.this, NaviActivity.class);
                                            intent.putExtra("user_id", (String) object.get("user_id"));
                                            intent.putExtra("user_name", (String) object.get("user_name"));
                                            intent.putExtra("email", (String) object.get("email"));
                                            startActivity(intent);
                                        }
                                        catch (JSONException e) {Log.v("LoginActivity", e.toString());}
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email");
                        request.setParameters(parameters);
                        request.executeAsync();

                    }

                    @Override
                    public void onCancel() {
                        Toast toast = Toast.makeText(getApplicationContext(), "Login cancelled.", Toast.LENGTH_SHORT);
                        toast.show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast toast = Toast.makeText(getApplicationContext(), "There was an error. Please try again later.", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
    }

    private void connect_to_sendbird(final String id, final String name)
    {
        SendBird.connect(id, new SendBird.ConnectHandler() {
            @Override
            public void onConnected(User user, SendBirdException e) {
                if (e != null) {
                    Log.v("sendbird", "error");
                    Toast.makeText(LoginActivity.this, "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                SendBird.updateCurrentUserInfo(name, null, new SendBird.UserInfoUpdateHandler() {
                    @Override
                    public void onUpdated(SendBirdException e) {
                        if (e != null) {
                            Toast.makeText(LoginActivity.this, "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        SharedPreferences.Editor editor = getPreferences(Context.MODE_PRIVATE).edit();
                        editor.putString("user_id", id);
                        editor.putString("nickname", name);
                        editor.apply();
                    }
                });
            }
        });
    }
    private void postUser(final JSONObject object) {
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    String query = "https://mydea-db.herokuapp.com/sendUser";

                    URL url = new URL(query);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(5000);
                    conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.setRequestMethod("POST");

                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                    wr.write(object.toString());
                    Log.v("POSTING", object.toString());
                    wr.flush();

                    if (conn.getResponseCode() != 200) {
                        throw new RuntimeException("Failed : HTTP error code : "
                                + conn.getResponseCode());
                    }

                    /*BufferedReader br = new BufferedReader(new InputStreamReader(
                            (conn.getInputStream())));*/

                    /*String output;
                    System.out.println("Output from Server .... \n");
                    while ((output = br.readLine()) != null) {
                        System.out.println(output);
                    }*/

                    conn.disconnect();

                }
                catch (IOException e) {
                    Log.v("LoginActivity", e.toString());}
            }
        });

        t.start();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
