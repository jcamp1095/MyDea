package com.tj.mydea;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by joecampbell on 11/4/16.
 */

public class Post {
    public void send(final JSONObject object, final String route) {
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    String query = "https://mydea-db.herokuapp.com" + route;

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

                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            (conn.getInputStream())));

                    String output;
                    System.out.println("Output from Server .... \n");
                    while ((output = br.readLine()) != null) {
                        System.out.println(output);
                    }

                    conn.disconnect();

                }
                catch (IOException e) {
                    Log.v("LoginActivity", e.toString());}
            }
        });

        t.start();
    }
}
