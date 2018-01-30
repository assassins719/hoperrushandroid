package com.hoperrush.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.hoperrush.R;
import com.hoperrush.core.dialog.LoadingDialog;
import com.hoperrush.core.dialog.PkDialog;
import com.hoperrush.core.socket.SocketHandler;
import com.hoperrush.core.volley.ServiceRequest;
import com.hoperrush.hockeyapp.ActivityHockeyApp;
import com.hoperrush.iconstant.Iconstant;
import com.hoperrush.utils.ConnectionDetector;
import com.hoperrush.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Facebookotppage extends ActivityHockeyApp {

    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private SessionManager sessionManager;

    private RelativeLayout Rl_back;
    private EditText Et_otp;
    private Button Bt_send;

    private String Str_username = "", Str_email = "", Str_password = "", Str_phone = "", Str_countryCode = "", Str_referralCode = "", Str_gcmId = "";
    private String Str_otp_Status = "", Str_otp = "";
    private String sCheckClass="";
String profile_image="",fb_id="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebookotppage);
        initialize();

        Rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // close keyboard
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(Rl_back.getWindowToken(), 0);

                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            }
        });

        Bt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Et_otp.getText().toString().length() == 0) {
                    errorEdit(Et_otp, getResources().getString(R.string.otp_label_alert_otp));
                } else if (!Str_otp.equals(Et_otp.getText().toString())) {
                    errorEdit(Et_otp, getResources().getString(R.string.otp_label_alert_invalid));
                } else {
                    cd = new ConnectionDetector(Facebookotppage.this);
                    isInternetPresent = cd.isConnectingToInternet();

                    if (isInternetPresent) {
                        postOtpRequest(Facebookotppage.this, Iconstant.facebook_register_url);

                        System.out.println("otp------------"+Iconstant.OtpUrl);

                    } else {
                        alert(getResources().getString(R.string.action_no_internet_title), getResources().getString(R.string.action_no_internet_message));
                    }
                }
            }
        });
    }

    private void initialize() {
        sessionManager = new SessionManager(Facebookotppage.this);
        cd = new ConnectionDetector(Facebookotppage.this);
        isInternetPresent = cd.isConnectingToInternet();

        Rl_back = (RelativeLayout) findViewById(R.id.otp_header_back_layout);
        Et_otp = (EditText) findViewById(R.id.otp_edittext);
        Bt_send = (Button) findViewById(R.id.otp_submit_button);

        Et_otp.addTextChangedListener(EditorWatcher);

        Intent intent = getIntent();
        Str_username = intent.getStringExtra("UserName");
        Str_email = intent.getStringExtra("Email");

        Str_phone = intent.getStringExtra("Phone");
        Str_countryCode = intent.getStringExtra("CountryCode");
        Str_referralCode = intent.getStringExtra("ReferralCode");
        Str_gcmId = intent.getStringExtra("GcmID");
        Str_otp_Status = intent.getStringExtra("Otp_Status");
        Str_otp = intent.getStringExtra("Otp");
        profile_image=intent.getStringExtra("profileimage");
             fb_id=intent.getStringExtra("MediaId");
        System.out.println("Gcm id-------------"+Str_gcmId);
        if (Str_otp_Status.equalsIgnoreCase("development")) {
            Et_otp.setText(Str_otp);
        } else {
            Et_otp.setText("");
        }

        sCheckClass=intent.getStringExtra("IntentClass");
    }

    //-------Code for TextWatcher------
    private final TextWatcher EditorWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            //clear error symbol after entering text
            if (Et_otp.getText().length() > 0) {
                Et_otp.setError(null);
            }
        }
    };

    // --------Code to set error for EditText--------
    private void errorEdit(EditText editName, String msg) {
        Animation shake = AnimationUtils.loadAnimation(Facebookotppage.this, R.anim.shake);
        editName.startAnimation(shake);
        editName.setError(msg);
    }

    //--------Alert Method--------
    private void alert(String title, String message) {
        final PkDialog mDialog = new PkDialog(Facebookotppage.this);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(message);
        mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }


    //----------Otp Post Request--------
    private void postOtpRequest(final Context mContext, String url) {

        final LoadingDialog mLoadingDialog = new LoadingDialog(mContext);
        mLoadingDialog.setLoadingTitle(getResources().getString(R.string.action_otp));
        mLoadingDialog.show();

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("email_id", Str_email);

        jsonParams.put("user_name", Str_username);
        jsonParams.put("country_code", Str_countryCode);
        jsonParams.put("phone", Str_phone);

        jsonParams.put("gcm_id", Str_gcmId);
        jsonParams.put("fb_id", fb_id);
        jsonParams.put("first_name", "");
        jsonParams.put("last_name", "");
        jsonParams.put("prof_pic", profile_image);

        ServiceRequest mRequest = new ServiceRequest(mContext);
        mRequest.makeServiceRequest(url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("---------otp response------------" + response);

                String Str_status = "", Str_message = "";
                try {
                    JSONObject object = new JSONObject(response);
                    Str_status = object.getString("status");
                    Str_message = object.getString("message");

                    if (Str_status.equalsIgnoreCase("1")) {

                        String userId = object.getString("user_id");
                        String userName = object.getString("user_name");
                        String userEmail = object.getString("email");
                        String userImage = object.getString("prof_pic");
                        String countryCode = object.getString("country_code");
                        String phoneNumber = object.getString("phone_number");
                        String referralCode = object.getString("referal_code");

                        String categoryId = object.getString("category");
                        String walletAmount = object.getString("wallet_amount");
                        String currencyCode = object.getString("currency");
                       String gcmId = object.getString("key");
                        String sSoc_Key = object.getString("soc_key");

                        sessionManager.createLoginSession(userId, userName, userEmail, userImage, countryCode, phoneNumber, categoryId, referralCode);
                        sessionManager.createWalletSession(walletAmount, currencyCode);
                        sessionManager.setXmppKey(userId, sSoc_Key);

                        sessionManager.setSocketTaskId("");
                        SocketHandler.getInstance(Facebookotppage.this).getSocketManager().connect();

                        Intent intent = new Intent(Facebookotppage.this, NavigationDrawer.class);
                        // intent.putExtra("IntentClass",sCheckClass);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                    }

                    if (Str_status.equalsIgnoreCase("0")) {
                        alert(getResources().getString(R.string.otp_label_alert_verification_failed), Str_message);


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mLoadingDialog.dismiss();
            }

            @Override
            public void onErrorListener() {
                mLoadingDialog.dismiss();
            }
        });
    }


    //-------------Move Back on pressed phone back button-----------
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)) {

            // close keyboard
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(Rl_back.getWindowToken(), 0);

            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            return true;
        }
        return false;
    }
}