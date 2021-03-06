package com.tj.mydea;


import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class MyIdeaFragment extends Fragment {
    public MyIdeaFragment() {
        // Required empty public constructor
    }

    // Initiating lists for information included with each idea
    private final List<String> ideaNames = new ArrayList<>();
    private final List<String> descriptions = new ArrayList<>();
    private final List<String> authors = new ArrayList<>();
    private final List<String> dates = new ArrayList<>();
    private final List<String> categories = new ArrayList<>();
    private final List<Integer> likes = new ArrayList<>();
    private final List<String> author_ids = new ArrayList<>();
    private final List<String> comments = new ArrayList<>();

    private final ArrayList<Idea> ideas = new ArrayList<>();
    private RecyclerView discoverRecyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            new MyIdeaFragment.GetIdeas().execute("https://mydea-db.herokuapp.com/user_ideas?user_id=").get();
        }
        catch (ExecutionException | InterruptedException ex) {
            ex.printStackTrace();
        }

        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discover, container, false);
        discoverRecyclerView = (RecyclerView) view.findViewById(R.id.discover_recycler_view);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        discoverRecyclerView.setLayoutManager(layoutManager);
        discoverRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getResources()));
        //updateUI();
        return view;
    }

    private void updateUI() {
        MyIdeaFragment.ideaAdapter adapter = new MyIdeaFragment.ideaAdapter(ideas);
        discoverRecyclerView.setAdapter(adapter);
    }


    private class ideaHolder extends RecyclerView.ViewHolder {
        @SuppressWarnings("unused")
        private Idea idea;
        public final TextView authorTextView;
        public final TextView ideaNameTextView;
        public final TextView descriptionTextView;
        public final TextView dateTextView;
        public final TextView categoryTextView;
        public final TextView likeTextView;
        public final TextView authoridTextView;
        public final TextView commentsTextView;

        public ideaHolder(View itemView) {
            super(itemView);
            authorTextView = (TextView) itemView.findViewById(R.id.textview_author);
            ideaNameTextView = (TextView) itemView.findViewById(R.id.textview_ideaName);
            descriptionTextView = (TextView) itemView.findViewById(R.id.textview_description);
            dateTextView = (TextView) itemView.findViewById(R.id.textview_date);
            categoryTextView = (TextView) itemView.findViewById(R.id.textview_category);
            likeTextView = (TextView) itemView.findViewById(R.id.textview_like);
            authoridTextView = (TextView) itemView.findViewById(R.id.textview_author_id);
            commentsTextView = (TextView) itemView.findViewById(R.id.textview_comments);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentManager fm = getFragmentManager();
                    MyDialogFragment dialogFragment = new MyDialogFragment ();

                    Bundle args = new Bundle();
                    args.putString("author", authorTextView.getText().toString());
                    args.putString("title", ideaNameTextView.getText().toString());
                    args.putString("description", descriptionTextView.getText().toString());
                    args.putString("author_id", authoridTextView.getText().toString());
                    args.putString("comments", commentsTextView.getText().toString());
                    dialogFragment.setArguments(args);
                    dialogFragment.show(fm, "Sample Fragment");
                }
            });
        }

        @SuppressLint("SetTextI18n")
        public void bindData(Idea id) {
            idea = id;
            authorTextView.setText(id.getauthor());
            ideaNameTextView.setText(id.getideaName());
            descriptionTextView.setText(id.getdescription());
            dateTextView.setText("Posted: " + id.getdate());
            categoryTextView.setText(id.getcategory());
            likeTextView.setText("Likes: " + Integer.toString(id.getlike()));
            authoridTextView.setText(id.getauthor_id());
            commentsTextView.setText(id.getcomments());
        }
    }

    private class ideaAdapter extends RecyclerView.Adapter<MyIdeaFragment.ideaHolder> {
        private final ArrayList<Idea> mideas;

        public ideaAdapter(ArrayList<Idea> ideas) {
            mideas = ideas;
        }

        @Override
        public MyIdeaFragment.ideaHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.discover_list_item_1, parent, false);
            return new MyIdeaFragment.ideaHolder(view);
        }

        @Override
        public void onBindViewHolder(MyIdeaFragment.ideaHolder holder, int position) {
            Idea id = mideas.get(position);
            holder.bindData(id);
            final View holder_v = holder.itemView;
            //send like
            final JSONObject object = new JSONObject();
            try {
                object.put("idea_name", id.getideaName());
                object.put("user_name", id.getauthor());
                final ImageButton like_button = (ImageButton) holder_v.findViewById(R.id.like_it);
                like_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        postLike(object);
                        TextView likeTextView = (TextView) holder_v.findViewById(R.id.textview_like);
                        String likes = likeTextView.getText().toString();
                        int like_int = 0;
                        for (int i  = 0; i < likes.length(); i++){
                            if (likes.charAt(i) == ' '){
                                like_int = Integer.parseInt(likes.substring(i+1));
                                break;
                            }
                        }
                        likeTextView.setText("Likes: " + Integer.toString(like_int + 1));
                    }
                });
            }
            catch (JSONException e) {
                Log.v("LoginActivity", e.toString());}
        }

        @Override
        public int getItemCount() {
            return mideas.size();
        }
    }


    /*@Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }*/

    private static JSONArray requestWebService(String serviceUrl) {
        disableConnectionReuseIfNecessary();

        HttpURLConnection urlConnection = null;
        try {
            // create connection
            URL urlToRequest = new URL(serviceUrl);
            urlConnection = (HttpURLConnection)
                    urlToRequest.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);


            if (urlConnection.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + urlConnection.getResponseCode());
            }

            // create JSON object from content
            InputStream in = new BufferedInputStream(
                    urlConnection.getInputStream());
            String response =  getResponseText(in);
            return new JSONArray(response);
            //return new JSONObject(getResponseText(in));

        } catch (JSONException | IOException e) {
            // URL is invalid
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return null;
    }

    /**
     * required in order to prevent issues in earlier Android version.
     */
    private static void disableConnectionReuseIfNecessary() {
        // see HttpURLConnection API doc
        if (Build.VERSION.SDK_INT
                < Build.VERSION_CODES.FROYO) {
            System.setProperty("http.keepAlive", "false");
        }
    }

    private static String getResponseText(InputStream inStream) {
        // very nice trick from
        // http://weblogs.java.net/blog/pat/archive/2004/10/stupid_scanner_1.html
        return new Scanner(inStream).useDelimiter("\\A").next();
    }

    private static int get_num_objects(JSONArray result){
        String result_str = result.toString();
        int counter = 0;
        for (int i = 0; i < result_str.length(); i++){
            if (result_str.charAt(i) == '{') {
                counter += 1;
            }
        }

        return counter;
    }

    public void postLike(final JSONObject object) {
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


    private class GetIdeas extends AsyncTask<String, Void, JSONArray> {
        protected JSONArray doInBackground(String... strings) {
            NaviActivity activity = (NaviActivity) getActivity();
            String user_id = activity.get_user_id();
            return requestWebService(strings[0] + user_id);
        }
        protected void onPostExecute(JSONArray result) {
            int num_entries = get_num_objects(result);
            try {
                for (int i = 0; i < num_entries; i++) {
                    JSONObject jsonobject = result.getJSONObject(i);
                    String idea_name = jsonobject.getString("idea_name");
                    String description = jsonobject.getString("description");
                    String user_name = jsonobject.getString("user_name");
                    String date = jsonobject.getString("created_at");
                    String category = jsonobject.getString("category");
                    Integer like = jsonobject.getInt("likes");
                    String author_id = jsonobject.getString("user_id");
                    String comments_str = jsonobject.getString("comments");
                    ideaNames.add(idea_name);
                    descriptions.add(description);
                    authors.add(user_name);
                    dates.add(date);
                    categories.add(category);
                    likes.add(like);
                    author_ids.add(author_id);
                    comments.add(comments_str);
                }

                for (int i = 0; i < ideaNames.size(); i++) {
                    Idea idea = new Idea();
                    idea.setideaName(ideaNames.get(i));
                    idea.setauthor(authors.get(i));
                    idea.setdescription(descriptions.get(i));
                    idea.setdate(dates.get(i));
                    idea.setcategory(categories.get(i));
                    idea.setlike(likes.get(i));
                    idea.setauthor_id(author_ids.get(i));
                    idea.setcomments(comments.get(i));
                    ideas.add(idea);
                }

                updateUI();
            }
            catch (JSONException ex) {
                ex.printStackTrace();
            }
        }
    }
}
