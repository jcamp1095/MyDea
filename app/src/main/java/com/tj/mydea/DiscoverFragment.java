package com.tj.mydea;

import android.content.Context;
import android.os.Bundle;
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

public class DiscoverFragment extends Fragment {
    private String[] ideaNames = {"Harambe", "Pepe"};
    private String[] descriptions = {"D**** Out", "The real pepe"};
    private String[] authors = {"Tommy", "Joe"};
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
        ideas = new ArrayList<>();
        for(int i = 0; i < ideaNames.length; i++)
        {
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
    private void updateUI(){
        adapter = new ideaAdapter(ideas);
        discoverRecyclerView.setAdapter(adapter);
    }
    private class ideaHolder extends RecyclerView.ViewHolder{
        private Idea idea;
        public TextView authorTextView;
        public TextView ideaNameTextView;
        public TextView descriptionTextView;
        public ideaHolder(View itemView){
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
        public void bindData(Idea id){
            idea = id;
            authorTextView.setText(id.getauthor());
            ideaNameTextView.setText(id.getideaName());
            descriptionTextView.setText(id.getdescription());
        }
    }
    private class ideaAdapter extends RecyclerView.Adapter<ideaHolder>{
        private ArrayList<Idea> mideas;
        public ideaAdapter(ArrayList<Idea> ideas){
            mideas = ideas;
        }
        @Override
        public ideaHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.discover_list_item_1,parent,false);
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
}
