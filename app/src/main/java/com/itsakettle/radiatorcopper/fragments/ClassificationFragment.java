package com.itsakettle.radiatorcopper.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

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
            Log.e(TAG,e.getMessage());
        }

        this.tvText = (TextView) getView().findViewById(R.id.classification_fragment_text);


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
        String[] initialButtonText = {"nothing", "set", "yet", "Correct"};
        setNumberOfButtons(initialButtonText);
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
            Log.e(TAG, e.getMessage());
        }
    }

    private void loadNextObservation() {
        // First get the boilerroom observation
        BoilerRoom br = new BoilerRoom(ssl);
        BoilerRoomObservation bro = br.nextObservation(,,);
        HashMap<Integer, String> choices = bro.getChoices();
        setNumberOfButtons((String[]) choices.values().toArray());
        tvText.setText(bro.getText());
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
        InputStream caInput = getResources().openRawResource(R.raw.itsakettle_cert);
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
