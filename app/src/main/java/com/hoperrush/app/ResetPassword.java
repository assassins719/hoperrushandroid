package com.hoperrush.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Casperon Technology on 2/4/2016.
 */
public class ResetPassword extends ActivityHockeyApp {
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private RelativeLayout Rl_back;

    private EditText Et_email, Et_password;
    private Button Bt_send;
    private CheckBox Cb_showPwd;

    private ServiceRequest mRequest;
    private LoadingDialog mLoadingDialog;

    private String SverificationOtp="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resetpassword);
        initialize();

        Rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // close keyboard
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(Rl_back.getWindowToken(), 0);

                onBackPressed();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
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

        Et_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    CloseKeyboard(Et_password);
                }
                return false;
            }
        });

        Cb_showPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (((CheckBox) v).isChecked()) {
                    Et_password.setTransformationMethod(null);
                } else {
                    Et_password.setTransformationMethod(new PasswordTransformationMethod());
                }

                Et_password.setSelection(Et_password.getText().length());
            }
        });

        Bt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cd = new ConnectionDetector(ResetPassword.this);
                isInternetPresent = cd.isConnectingToInternet();

                if (!isValidEmail(Et_email.getText().toString())) {
                    alert(getResources().getString(R.string.action_sorry), getResources().getString(R.string.reset_password_email_label_enter_valid_email));
                } else if (Et_password.getText().toString().length() == 0) {
                    alert(getResources().getString(R.string.action_sorry), getResources().getString(R.string.reset_password_email_label_enter_valid_password));
                } else {
                    if (isInternetPresent) {
                        PostRequest(Iconstant.reset_password_url);
                    } else {
                        alert(getResources().getString(R.string.action_no_internet_title), getResources().getString(R.string.action_no_internet_message));
                    }
                }
            }
        });
    }

    private void initialize() {
        Rl_back = (RelativeLayout) findViewById(R.id.reset_password_header_back_layout);
        Et_email = (EditText) findViewById(R.id.reset_password_email_editText);
        Et_password = (EditText) findViewById(R.id.reset_password_password_editText);
        Bt_send = (Button) findViewById(R.id.reset_password_submit_button);
        Cb_showPwd = (CheckBox) findViewById(R.id.reset_password_show_password_checkBox);

        Et_password.setTransformationMethod(new PasswordTransformationMethod());

        Intent i = getIntent();
        SverificationOtp = i.getStringExtra("Intent_verificationCode");

        System.out.println("SverificationOtp--------------"+SverificationOtp);


    }

    //----------------code to Check Email Validation----------
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

        final PkDialog mDialog = new PkDialog(ResetPassword.this);
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

        mLoadingDialog = new LoadingDialog(ResetPassword.this);
        mLoadingDialog.setLoadingTitle(getResources().getString(R.string.action_processing));
        mLoadingDialog.show();

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("email", Et_email.getText().toString());
        jsonParams.put("password", Et_password.getText().toString());
        jsonParams.put("reset",SverificationOtp);

        System.out.println("email-------"+Et_email.getText().toString());
        System.out.println("password-------"+Et_password.getText().toString());
        System.out.println("reset-------"+SverificationOtp);




        mRequest = new ServiceRequest(ResetPassword.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("--------------Reset Password response-------------------" + response);

                String sStatus = "", sMessage = "";
                try {

                    JSONObject object = new JSONObject(response);
                    sStatus = object.getString("status");
                    sMessage = object.getString("response");
                    if (sStatus.equalsIgnoreCase("1")) {

                        final PkDialog mDialog = new PkDialog(ResetPassword.this);
                        mDialog.setDialogTitle(getResources().getString(R.string.action_success));
                        mDialog.setDialogMessage(sMessage);
                        mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mDialog.dismiss();
                                onBackPressed();
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


    //-----------------Move Back on pressed phone back button-------------
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
