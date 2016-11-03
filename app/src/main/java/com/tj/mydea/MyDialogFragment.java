package com.tj.mydea;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sendbird.android.GroupChannel;
import com.sendbird.android.SendBirdException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by joecampbell on 10/21/16.
 */

@SuppressWarnings("DefaultFileTemplate")
public class MyDialogFragment extends DialogFragment {

    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sample_dialog, container, false);

        final String title = getArguments().getString("title");
        String author = getArguments().getString("author");
        String description = getArguments().getString("description");
        final String author_id = getArguments().getString("author_id");
        String comments = getArguments().getString("comments");


        getDialog().setTitle(title);


        TextView title_view = (TextView) rootView.findViewById(R.id.title);
        TextView author_view = (TextView) rootView.findViewById(R.id.author);
        TextView description_view = (TextView) rootView.findViewById(R.id.description);
        ExpandableListView exlistView = (ExpandableListView) rootView.findViewById(R.id.lvExp);

        title_view.setText(title);
        author_view.setText(author);
        description_view.setText("Description: " + description);

        Button dismiss = (Button) rootView.findViewById(R.id.dismiss);
        dismiss.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        set_up_comments(exlistView, comments);

        final JSONObject object = new JSONObject();
        try {
            object.put("idea_name", title);
            object.put("user_name", author);
            Button like = (Button) rootView.findViewById(R.id.like);
            like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    postLike(object);
                    dismiss();
                }
            });
            Button message = (Button) rootView.findViewById(R.id.message);
            message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    @SuppressWarnings("ArraysAsListWithZeroOrOneArgument") List<String> userIds = Collections.singletonList(author_id);
                    GroupChannel.createChannelWithUserIds(userIds, false, title, null, null, new GroupChannel.GroupChannelCreateHandler() {
                        @Override
                        public void onResult(GroupChannel groupChannel, SendBirdException e) {
                            if (e != null) {
                                Toast.makeText(getActivity(), "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            } else {Log.v("create channel", "success");}
                        }
                    });
                    Intent intent = new Intent(getActivity(), MessageFragment.class);
                    startActivity(intent);
                }
            });

        }
        catch (JSONException e) {Log.v("LoginActivity", e.toString());}


        return rootView;
    }

    private void postLike(final JSONObject object) {
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    String query = "https://mydea-db.herokuapp.com/likes";

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

    private void set_up_comments(ExpandableListView exlistView, String comments) {
        // preparing list data
        prepareListData(comments);

        ExpandableListAdapter listAdapter = new ExpandableListAdapter(getContext(), listDataHeader, listDataChild);

        // setting list adapter
        exlistView.setAdapter(listAdapter);
    }

    /*
     * Preparing the list data
     */
    private void prepareListData(String comments_add) {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

        // Adding child data
        listDataHeader.add("Comments");

        // Adding child data
        List<String> comments = new ArrayList<>();
        for (int i  = 1; i < comments_add.length(); i++) {
            String to_add = "";
            while (comments_add.charAt(i) != ',' && comments_add.charAt(i) != ']'){
                if (comments_add.charAt(i) != '"') {
                    to_add += comments_add.charAt(i);
                }
                i++;
            }
            Log.v("to_add", to_add);
            comments.add(to_add);
        }

        listDataChild.put(listDataHeader.get(0), comments);
    }
}
