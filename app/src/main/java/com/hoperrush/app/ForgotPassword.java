package com.hoperrush.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
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
import com.hoperrush.core.dialog.LoadingDialog;
import com.hoperrush.core.dialog.PkDialog;
import com.hoperrush.core.volley.ServiceRequest;
import com.hoperrush.utils.HideSoftKeyboard;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Casperon Technology on 2/4/2016.
 */
public class ForgotPassword extends ActivityHockeyApp {
    private RelativeLayout back;
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private ServiceRequest mRequest;
    private LoadingDialog mLoadingDialog;

    private EditText Et_email;
    private Button Bt_submit;

    String sStatus = "", sMessage = "", sSms_status = "", sVerificationCode = "", sEmailAddress = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgotpassword);
        initialize();
        HideSoftKeyboard.setupUI(
                ForgotPassword.this.getWindow().getDecorView(),
                ForgotPassword.this);
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


        Bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cd = new ConnectionDetector(ForgotPassword.this);
                isInternetPresent = cd.isConnectingToInternet();

                if (Et_email.getText().length() == 0) {
                    alert(getResources().getString(R.string.action_sorry), "Please enter your mail");

                } else if (!isValidEmail(Et_email.getText().toString())) {
                    alert(getResources().getString(R.string.action_sorry), getResources().getString(R.string.forgot_password_email_label_enter_valid_email));
                } else {

                    if (isInternetPresent) {
                        PostRequest(Iconstant.forgot_password_url);

                        System.out.println("forgot_password_url-------------" + Iconstant.forgot_password_url);
                    } else {
                        alert(getResources().getString(R.string.action_no_internet_title), getResources().getString(R.string.action_no_internet_message));
                    }
                }
            }
        });

        Et_email.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    CloseKeyboard(Et_email);
                }
                return false;
            }
        });
    }

    private void initialize() {

        back = (RelativeLayout) findViewById(R.id.forgot_password_email_header_back_layout);
        Et_email = (EditText) findViewById(R.id.forgot_password_email_email_editText);
        Bt_submit = (Button) findViewById(R.id.forgot_password_email_submit_button);

        Bt_submit.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Medium.ttf"));
    }

    //-------------------------code to Check Email Validation-----------------------
    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }


    //--------------Close Keyboard Method-----------
    private void CloseKeyboard(EditText edittext) {
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(edittext.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    //--------------Alert Method-----------
    private void alert(String title, String alert) {

        final PkDialog mDialog = new PkDialog(ForgotPassword.this);
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


    // -------------------------code for Forgot Password Post Request----------------------------------

    private void PostRequest(final String Url) {

        mLoadingDialog = new LoadingDialog(ForgotPassword.this);
        mLoadingDialog.setLoadingTitle(getResources().getString(R.string.action_verifying));
        mLoadingDialog.show();

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("email", Et_email.getText().toString());

        mRequest = new ServiceRequest(ForgotPassword.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("--------------Forgot Password response-------------------" + response);
                try {

                    JSONObject object = new JSONObject(response);
                    sStatus = object.getString("status");
                    sMessage = object.getString("response");
                    if (sStatus.equalsIgnoreCase("1")) {
                        sSms_status = object.getString("sms_status");
                        sVerificationCode = object.getString("verification_code");
                        sEmailAddress = object.getString("email_address");
                    }


                    if (sStatus.equalsIgnoreCase("1")) {

                        final PkDialog mDialog = new PkDialog(ForgotPassword.this);
                        mDialog.setDialogTitle(getResources().getString(R.string.action_success));
                        mDialog.setDialogMessage(getResources().getString(R.string.forgot_password_email_label_success));
                        mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mDialog.dismiss();
                                Intent i = new Intent(ForgotPassword.this, ForgotPasswordOtp.class);
                                i.putExtra("Intent_Otp_Status", sSms_status);
                                i.putExtra("Intent_verificationCode", sVerificationCode);
                                i.putExtra("Intent_email", sEmailAddress);
                                startActivity(i);
                                finish();
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            }
                        });
                        mDialog.show();

                    } else {
                        alert(getResources().getString(R.string.action_error), sMessage);
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                // close keyboard
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(Et_email.getWindowToken(), 0);

                mLoadingDialog.dismiss();
            }

            @Override
            public void onErrorListener() {
                mLoadingDialog.dismiss();
            }
        });
    }

    //-----------------Move Back on pressed phone back button------------------
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)) {

            // close keyboard
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(back.getWindowToken(), 0);

            onBackPressed();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
            return true;
        }
        return false;
    }

}
