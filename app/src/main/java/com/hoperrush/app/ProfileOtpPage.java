package com.hoperrush.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.hoperrush.R;
import com.hoperrush.hockeyapp.ActivityHockeyApp;
import com.hoperrush.iconstant.Iconstant;
import com.hoperrush.utils.ConnectionDetector;
import com.hoperrush.utils.SessionManager;
import com.hoperrush.core.dialog.LoadingDialog;
import com.hoperrush.core.dialog.PkDialog;
import com.hoperrush.core.volley.ServiceRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Casperon Technology on 1/19/2016.
 */
public class ProfileOtpPage extends ActivityHockeyApp {
    private Context context;
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private SessionManager session;

    private RelativeLayout back;
    private EditText Eotp;
    private Button send;

    private ServiceRequest mRequest;
    LoadingDialog mLoadingDialog;

    private String Suserid = "", Sphone = "", ScountryCode = "";
    private String Sotp_Status = "", Sotp = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_otp_page);
        context = getApplicationContext();
        initialize();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // close keyboard
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(back.getWindowToken(), 0);

                onBackPressed();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Eotp.getText().toString().length() == 0) {
                    erroredit(Eotp, getResources().getString(R.string.otp_label_alert_otp));
                } else if (!Sotp.equals(Eotp.getText().toString())) {
                    erroredit(Eotp, getResources().getString(R.string.otp_label_alert_invalid));
                } else {
                    if (isInternetPresent) {
                        PostRequest(Iconstant.profile_edit_mobileNo_url);
                    } else {
                        Alert(getResources().getString(R.string.action_no_internet_title), getResources().getString(R.string.action_no_internet_message));
                    }
                }
            }
        });


        Eotp.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(Eotp.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
                return false;
            }
        });
    }

    private void initialize() {
        session = new SessionManager(ProfileOtpPage.this);
        cd = new ConnectionDetector(ProfileOtpPage.this);
        isInternetPresent = cd.isConnectingToInternet();

        back = (RelativeLayout) findViewById(R.id.profile_otp_header_back_layout);
        Eotp = (EditText) findViewById(R.id.profile_otp_password_editText);
        send = (Button) findViewById(R.id.profile_otp_submit_button);

        Eotp.addTextChangedListener(EditorWatcher);

        Intent intent = getIntent();
        Suserid = intent.getStringExtra("UserID");
        Sphone = intent.getStringExtra("Phone");
        ScountryCode = intent.getStringExtra("CountryCode");
        Sotp_Status = intent.getStringExtra("Otp_Status");
        Sotp = intent.getStringExtra("Otp");

        if (Sotp_Status.equalsIgnoreCase("development")) {
            Eotp.setText(Sotp);
        } else {
            Eotp.setText("");
        }
    }

    //--------------Alert Method-----------
    private void Alert(String title, String alert) {

        final PkDialog mDialog = new PkDialog(ProfileOtpPage.this);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(alert);
        mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    //----------------------Code for TextWatcher-------------------------
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
            if (Eotp.getText().length() > 0) {
                Eotp.setError(null);
            }
        }
    };

    //--------------------Code to set error for EditText-----------------------
    private void erroredit(EditText editname, String msg) {
        Animation shake = AnimationUtils.loadAnimation(ProfileOtpPage.this, R.anim.shake);
        editname.startAnimation(shake);

        ForegroundColorSpan fgcspan = new ForegroundColorSpan(Color.parseColor("#CC0000"));
        SpannableStringBuilder ssbuilder = new SpannableStringBuilder(msg);
        ssbuilder.setSpan(fgcspan, 0, msg.length(), 0);
        editname.setError(ssbuilder);
    }

    //-----------------Move Back on pressed phone back button------------------
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)) {

            // close keyboard
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(back.getWindowToken(), 0);

            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            return true;
        }
        return false;
    }


    // -----------------code for OTP Verification Post Request----------------
    private void PostRequest(String Url) {

        mLoadingDialog = new LoadingDialog(ProfileOtpPage.this);
        mLoadingDialog.setLoadingTitle(getResources().getString(R.string.action_otp));
        mLoadingDialog.show();

        System.out.println("--------------Otp url-------------------" + Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", Suserid);
        jsonParams.put("country_code", ScountryCode);
        jsonParams.put("phone_number", Sphone);
        jsonParams.put("otp", Sotp);

        mRequest = new ServiceRequest(ProfileOtpPage.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("--------------Otp reponse-------------------" + response);
                String Sstatus = "", Smessage = "", Scountry_code = "", Sphone_number = "";
                try {

                    JSONObject object = new JSONObject(response);

                    Sstatus = object.getString("status");
                    Smessage = object.getString("response");

                    if (Sstatus.equalsIgnoreCase("1")) {
                        Scountry_code = object.getString("country_code");
                        Sphone_number = object.getString("phone_number");
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if (Sstatus.equalsIgnoreCase("1")) {
                    UserProfilePage.updateMobileDialog(Scountry_code, Sphone_number);
                    session.setMobileNumberUpdate(Scountry_code, Sphone_number);

                    final PkDialog mDialog = new PkDialog(ProfileOtpPage.this);
                    mDialog.setDialogTitle(getResources().getString(R.string.action_success));
                    mDialog.setDialogMessage(getResources().getString(R.string.profile_label_mobile_success));
                    mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDialog.dismiss();
                            finish();
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                        }
                    });
                    mDialog.show();

                } else {

                    final PkDialog mDialog = new PkDialog(ProfileOtpPage.this);
                    mDialog.setDialogTitle(getResources().getString(R.string.action_error));
                    mDialog.setDialogMessage(Smessage);
                    mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDialog.dismiss();
                        }
                    });
                    mDialog.show();
                }

                // close keyboard
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(Eotp.getWindowToken(), 0);

                mLoadingDialog.dismiss();
            }

            @Override
            public void onErrorListener() {
                mLoadingDialog.dismiss();
            }
        });
    }

}
