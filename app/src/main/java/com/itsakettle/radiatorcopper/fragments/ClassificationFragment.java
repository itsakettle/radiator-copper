package com.itsakettle.radiatorcopper.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.itsakettle.radiatorcopper.R;
import com.itsakettle.radiatorcopper.boilerroom.BoilerRoom;
import com.itsakettle.radiatorcopper.boilerroom.BoilerRoomObservation;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashMap;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link ClassificationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ClassificationFragment extends Fragment {

    private static final String TAG = "ClassificationFragment";

    private SSLContext ssl;
    private TextView tvText;

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
        try {
            this.ssl = sslContext();
        } catch(Exception e) {
            Log.e(TAG, "on create get ssl", e);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_classification, container, false);
        this.tvText = (TextView) root.findViewById(R.id.classification_fragment_text);
        return root;
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
        loadNextObservation();
    }

    /**
     * Method to set a variable number of buttons, but with no real thought for how they'll look!
     *
     * @param arrButtonText
     */
    private void setNumberOfButtons(String[] arrButtonText) {
        Context con = getActivity();
        LinearLayout llButtons = (LinearLayout) getView().findViewById(
                R.id.classification_fragment_button_linear_layout);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, 1);
        try {
            for (String s : arrButtonText) {
                Button b = new Button(con);
                b.setId(View.generateViewId());
                b.setText(s);
                b.setLayoutParams(lp);
                llButtons.addView(b);
            }
        } catch (Exception e) {
            Log.e(TAG,"setNumberOfButtons",e);
        }
    }

    private void loadNextObservation() {
        // First get the boilerroom observation
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        String u = sharedPref.getString(getResources().getString(R.string.username_key), "");
        String p = sharedPref.getString(getResources().getString(R.string.password_key), "");

        if(u.isEmpty() || p.isEmpty()) {
            Toast toast = Toast.makeText(this.getActivity(),
                    "No username or Password", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        BoilerRoom br = new BoilerRoom(this, ssl);
        br.nextObservation(1,u,p);
    }


    public void setTextButtons(String text, String[] choices ) {
        setNumberOfButtons(choices);
        tvText.setText(text);
    }

    /**
     * Method to generate the ssl context that must be passed to the Boiler room class.
     *
     * Got more or less all of this from
     * http://developer.android.com/training/articles/security-ssl.html
     * @return An SSLContext
     * @throws java.security.cert.CertificateException
     * @throws IOException
     * @throws java.security.KeyStoreException
     * @throws java.security.NoSuchAlgorithmException
     * @throws java.security.KeyManagementException
     */
    private SSLContext sslContext()
            throws java.security.cert.CertificateException, IOException,
            java.security.KeyStoreException, java.security.NoSuchAlgorithmException,
            java.security.KeyManagementException {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        // From https://www.washington.edu/itconnect/security/ca/load-der.crt
        InputStream caInput = getResources().openRawResource(R.raw.itsakettlecert);
        Certificate ca;

        try {
            ca = cf.generateCertificate(caInput);
            System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
        } finally {
            caInput.close();
        }

        // Create a KeyStore containing our trusted CAs
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", ca);

        // Create a TrustManager that trusts the CAs in our KeyStore
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);

        // Create an SSLContext that uses our TrustManager
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, tmf.getTrustManagers(), null);
        return context;
    }

}
