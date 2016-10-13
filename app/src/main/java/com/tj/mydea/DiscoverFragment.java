package com.tj.mydea;

import android.content.Context;
import android.os.Bundle;
import android.os.Build;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Scene;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Scanner;
import android.os.StrictMode;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import 	java.util.concurrent.ExecutionException;

public class DiscoverFragment extends Fragment {
    private String[] ideaNames = {"hello"};
    private String[] descriptions = {"hello"};
    private String[] authors = {"hello"};
    private ArrayList<Idea> ideas;
    private RecyclerView discoverRecyclerView;
    private ideaAdapter adapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DiscoverFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        JSONArray ideasObj = null;
        try {
            ideasObj = new GetIdeas().execute("https://mydea-db.herokuapp.com/ideas").get();
        }
        catch (ExecutionException | InterruptedException ex) {
            ex.printStackTrace();
        }
        ideas = new ArrayList<>();
        for (int i = 0; i < ideaNames.length; i++) {
            Idea idea = new Idea();
            idea.setideaName(ideaNames[i]);
            idea.setauthor(authors[i]);
            idea.setdescription(descriptions[i]);
            ideas.add(idea);
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
        updateUI();
        return view;
    }

    private void updateUI() {
        adapter = new ideaAdapter(ideas);
        discoverRecyclerView.setAdapter(adapter);
    }

    private class ideaHolder extends RecyclerView.ViewHolder {
        private Idea idea;
        public TextView authorTextView;
        public TextView ideaNameTextView;
        public TextView descriptionTextView;

        public ideaHolder(View itemView) {
            super(itemView);
            authorTextView = (TextView) itemView.findViewById(R.id.textview_author);
            ideaNameTextView = (TextView) itemView.findViewById(R.id.textview_ideaName);
            descriptionTextView = (TextView) itemView.findViewById(R.id.textview_description);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(),
                            idea.getideaName() + " clicked!", Toast.LENGTH_SHORT).show();
                }
            });
        }

        public void bindData(Idea id) {
            idea = id;
            authorTextView.setText(id.getauthor());
            ideaNameTextView.setText(id.getideaName());
            descriptionTextView.setText(id.getdescription());
        }
    }

    private class ideaAdapter extends RecyclerView.Adapter<ideaHolder> {
        private ArrayList<Idea> mideas;

        public ideaAdapter(ArrayList<Idea> ideas) {
            mideas = ideas;
        }

        @Override
        public ideaHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.discover_list_item_1, parent, false);
            return new ideaHolder(view);
        }

        @Override
        public void onBindViewHolder(ideaHolder holder, int position) {
            Idea id = mideas.get(position);
            holder.bindData(id);
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    /*public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(DummyItem item);
    }*/
    public static JSONArray requestWebService(String serviceUrl) {
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
            Log.v("test", response);
            return new JSONArray(response);
            //return new JSONObject(getResponseText(in));

        } catch (MalformedURLException e) {
            // URL is invalid
        } catch (IOException e) {
            // could not read response body
            // (could not create input stream)
        } catch (JSONException e) {
            // response body is no valid JSON string
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
        if (Integer.parseInt(Build.VERSION.SDK)
                < Build.VERSION_CODES.FROYO) {
            System.setProperty("http.keepAlive", "false");
        }
    }

    private static String getResponseText(InputStream inStream) {
        // very nice trick from
        // http://weblogs.java.net/blog/pat/archive/2004/10/stupid_scanner_1.html
        return new Scanner(inStream).useDelimiter("\\A").next();
    }

    public class GetIdeas extends AsyncTask<String, Void, JSONArray> {
        protected JSONArray doInBackground(String... strings) {
            return requestWebService(strings[0]);
        }
        protected void onPostExecute(JSONArray result) {
            Log.v("test", result.toString());
            try {
                for (int i = 0; i < result.size(); i++) {
                    JSONObject jsonobject = result.getJSONObject(i);
                    Log.v("ada", jsonobject.toString());
                    String idea_name = jsonobject.getString("idea_name");
                    String description = jsonobject.getString("description");
                    String user_name = jsonobject.getString("user_name");
                    ideaNames[i] = idea_name;
                    descriptions[i] = description;
                    authors[i] = user_name;
                }
            }
            catch (JSONException ex) {
                ex.printStackTrace();
            }
        }
    }
}