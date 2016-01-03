package com.itsakettle.radiatorcopper.fragments;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.itsakettle.radiatorcopper.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link ClassificationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ClassificationFragment extends Fragment {

    private static final String TAG = "ClassificationFragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ClassificationFragment.
     */
    public static ClassificationFragment newInstance() {
        return new ClassificationFragment();
    }

    public ClassificationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_classification, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onStart() {
        super.onStart();
        String[] initialButtonText = {"nothing","set","yet","Correct"};
        setNumberOfButtons(initialButtonText);
    }

    /**
     * Method to set a variable number of buttons, but with no real thought for how they'll look!
     * @param arrButtonText
     */
    private void setNumberOfButtons(String[] arrButtonText) {
        Context con = getActivity();
        LinearLayout llButtons = (LinearLayout) getView().findViewById(
                R.id.classification_fragment_button_linear_layout);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,1);
        try {
            for (String s : arrButtonText) {
                Button b = new Button(con);
                b.setId(View.generateViewId());
                b.setText(s);
                b.setLayoutParams(lp);
                llButtons.addView(b);
            }
        }
        catch (Exception e) {
            Log.e(TAG,e.getMessage());
        }
    }

}
