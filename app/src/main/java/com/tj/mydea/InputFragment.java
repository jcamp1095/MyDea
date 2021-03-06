package com.tj.mydea;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import static com.facebook.FacebookSdk.getApplicationContext;


@SuppressWarnings("EmptyMethod")
public class InputFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    @SuppressWarnings("unused")
    private static final String ARG_PARAM1 = "param1";
    @SuppressWarnings("unused")
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    @SuppressWarnings("unused")
    private String mParam1;
    @SuppressWarnings("unused")
    private String mParam2;

    @SuppressWarnings("unused")
    private OnFragmentInteractionListener mListener;

    @SuppressWarnings("unused")
    public InputFragment() {
        // Required empty public constructor
    }

    @SuppressWarnings("EmptyMethod")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View thisview = inflater.inflate(R.layout.fragment_input, container, false);
        Button send_data = (Button) thisview.findViewById(R.id.send_idea_button);
        send_data.setOnClickListener(this);
        return thisview;
    }

    @Override
    public void onClick(View v) {
        NaviActivity activity = (NaviActivity) getActivity();
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        //noinspection ConstantConditions
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        @SuppressWarnings("ConstantConditions") EditText title = (EditText)getView().findViewById(R.id.editTitle);
        EditText description = (EditText)getView().findViewById(R.id.editDescription);
        String title_str = title.getText().toString();
        String description_str = description.getText().toString();
        Spinner spinner1 = (Spinner) getView().findViewById(R.id.spinner1);
        String spinner_str = spinner1.getSelectedItem().toString();
        if (Objects.equals(title_str, "")) {
            Context context = getApplicationContext();
            CharSequence text = "Please enter a title.";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            return;
        }
        if (Objects.equals(description_str, "")) {
            Context context = getApplicationContext();
            CharSequence text = "Please enter a description.";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            return;
        }
        if (Objects.equals(spinner_str, "Choose a Category…")) {
            Context context = getApplicationContext();
            CharSequence text = "Please select a category.";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            return;
        }

        String user_id = activity.get_user_id();
        String user_name = activity.get_user_name();

        JSONObject object = new JSONObject();
        try {
            object.put("idea_name", title_str);
            object.put("description", description_str);
            object.put("user_id", user_id);
            object.put("user_name", user_name);
            object.put("category", spinner_str);
            Post post = new Post();
            post.send(object, "/sendIdea");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        title.setText("");
        description.setText("");


        Context context = getApplicationContext();
        CharSequence text = "Idea Sent!";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
        DiscoverFragment DiscoverFragment = new DiscoverFragment();
        FragmentManager manager = getFragmentManager();
        manager.beginTransaction().replace(R.id.layout_for_fragments, DiscoverFragment).addToBackStack("discover").commit();
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    @SuppressWarnings("unused")
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
