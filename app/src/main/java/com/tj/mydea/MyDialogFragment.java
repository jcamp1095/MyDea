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
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.sendbird.android.GroupChannel;
import com.sendbird.android.SendBirdException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by joecampbell on 10/21/16.
 */

@SuppressWarnings("DefaultFileTemplate")
public class MyDialogFragment extends DialogFragment {


    private List<String> listDataHeader = new ArrayList<>();
    private HashMap<String, List<String>> listDataChild = new HashMap<>();
    List<String> comments_list = new ArrayList<>();

    ScrollView scrollView;


    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sample_dialog, container, false);
        scrollView = (ScrollView) rootView.findViewById(R.id.scrollViewDrawer);

        final String title = getArguments().getString("title");
        final String author = getArguments().getString("author");
        String description = getArguments().getString("description");
        final String author_id = getArguments().getString("author_id");
        final String comments = getArguments().getString("comments");

        getDialog().setTitle(title);

        TextView title_view = (TextView) rootView.findViewById(R.id.title);
        TextView author_view = (TextView) rootView.findViewById(R.id.author);
        TextView description_view = (TextView) rootView.findViewById(R.id.description);
        final ExpandableListView exlistView = (ExpandableListView) rootView.findViewById(R.id.lvExp);

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
        Log.v("comments", comments);
        set_up_comments(exlistView, comments);



        final JSONObject comment_obj = new JSONObject();
        Button comment_button = (Button) rootView.findViewById(R.id.comment_btn);
        comment_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText comment = (EditText)getView().findViewById(R.id.sendComment);
                String comment_str = comment.getText().toString();
                try {
                    comment_obj.put("comments", comment_str);
                    comment_obj.put("idea_name", title);
                    comment_obj.put("user_name", author);
                    Post post = new Post();
                    post.send(comment_obj, "/comment");
                    append_comment(comment_str, exlistView);
                    comment.setText("");
                }
                catch (JSONException e) {Log.v("LoginActivity", e.toString());}
            }
        });

        final JSONObject object = new JSONObject();
        try {
            object.put("idea_name", title);
            object.put("user_name", author);
            Button like = (Button) rootView.findViewById(R.id.like);
            like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Post post = new Post();
                    post.send(object, "/likes");
                    dismiss();
                }
            });

            Button message = (Button) rootView.findViewById(R.id.message);
            message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<String> userIds = Collections.singletonList(author_id);
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

    private void append_comment(String comment_str, ExpandableListView exlistView) {

        comments_list.add(comment_str);
        listDataChild.put(listDataHeader.get(0), comments_list);
        ExpandableListAdapter listAdapter = new ExpandableListAdapter(getContext(), listDataHeader, listDataChild);

        // setting list adapter
        exlistView.setAdapter(listAdapter);

    }




    private void set_up_comments(ExpandableListView exlistView, String comments) {
        // preparing list data
        prepareListData(comments);

        ExpandableListAdapter listAdapter = new ExpandableListAdapter(getContext(), listDataHeader, listDataChild);

        // setting list adapter
        exlistView.setAdapter(listAdapter);

        exlistView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                setListViewHeight(parent, groupPosition);
                return false;
            }
        });
    }


    private void setListViewHeight(ExpandableListView listView,
                                   int group) {
        ExpandableListAdapter listAdapter = (ExpandableListAdapter) listView.getExpandableListAdapter();
        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
                View.MeasureSpec.EXACTLY);
        for (int i = 0; i < listAdapter.getGroupCount(); i++) {
            View groupItem = listAdapter.getGroupView(i, false, null, listView);
            groupItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);

            totalHeight += groupItem.getMeasuredHeight();

            if (((listView.isGroupExpanded(i)) && (i != group))
                    || ((!listView.isGroupExpanded(i)) && (i == group))) {
                for (int j = 0; j < listAdapter.getChildrenCount(i); j++) {
                    View listItem = listAdapter.getChildView(i, j, false, null,
                            listView);
                    listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);

                    totalHeight += listItem.getMeasuredHeight();

                }
            }
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        int height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getGroupCount() - 1));
        if (height < 10)
            height = 200;
        params.height = height;
        listView.setLayoutParams(params);
        listView.requestLayout();

    }

    /*
     * Preparing the list data
     */
    private void prepareListData(String comments_add) {
        //listDataHeader = new ArrayList<>();
        //listDataChild = new HashMap<>();

        // Adding child data
        listDataHeader.add("Comments");

        // Adding child data
        //List<String> comments = new ArrayList<>();
        for (int i  = 1; i < comments_add.length(); i++) {
            String to_add = "";
            while (comments_add.charAt(i) != ',' && comments_add.charAt(i) != ']'){
                if (comments_add.charAt(i) != '"') {
                    to_add += comments_add.charAt(i);
                }
                i++;
            }
            Log.v("to_add", to_add);
            comments_list.add(to_add);
        }

        listDataChild.put(listDataHeader.get(0), comments_list);
    }
}
