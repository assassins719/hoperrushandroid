package com.hoperrush.app;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.countrycodepicker.CountryPicker;
import com.countrycodepicker.CountryPickerListener;
import com.hoperrush.R;
import com.hoperrush.hockeyapp.FragmentActivityHockeyApp;
import com.hoperrush.iconstant.Iconstant;
import com.hoperrush.core.dialog.LoadingDialog;
import com.hoperrush.core.dialog.PkDialog;
import com.hoperrush.core.gps.GPSTracker;
import com.hoperrush.core.pushnotification.GCMInitializer;
import com.hoperrush.core.volley.ServiceRequest;
import com.hoperrush.utils.ConnectionDetector;
import com.hoperrush.utils.CountryDialCode;
import com.hoperrush.utils.HideSoftKeyboard;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Casperon Technology on 12/8/2015.
 */
public class RegisterPage extends FragmentActivityHockeyApp {
    private MaterialEditText Et_email, Et_password, Et_userName, Et_countryCode, Et_phoneNumber, Et_referralCode,Et_firstname,Et_lastname,Et_confirmpassword;
    private ImageView Iv_help;
    private RelativeLayout Rl_back;
    private Button Bt_submit;
    private ConnectionDetector cd;
    private boolean isInternetPresent = false;
    private LoadingDialog mLoadingDialog;

    //-------GCM Initialization-----
    private String GCM_Id = "";
    private Handler mHandler;

    CountryPicker picker;
    private GPSTracker gpsTracker;
    private String sCheckClass = "";
    CheckBox checkbox;
    TextView terms_conditions,privacy_policy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_page);
        initialize();

        HideSoftKeyboard.setupUI(
                RegisterPage.this.getWindow().getDecorView(),
                RegisterPage.this);
        Iv_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Referral_information();
            }
        });

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


        Bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String confirmpassword=Et_confirmpassword.getText().toString();

                if(Et_firstname.getText().toString().length()==0){
                    errorEdit(Et_firstname, getResources().getString(R.string.register_label_alert_firstname));
                }
                else if (Et_lastname.getText().toString().length()==0) {
                    errorEdit(Et_lastname, getResources().getString(R.string.register_label_alert_lastname));
                }

                else if (Et_userName.getText().toString().length() == 0) {
                    errorEdit(Et_userName, getResources().getString(R.string.register_label_alert_username));
                }
//                else if (!isValidUsername(Et_userName.getText().toString())) {
//                    errorEdit(Et_userName, getResources().getString(R.string.register_label_alert_username_lowercase));
//                }

                else if (Et_email.getText().toString().length()==0) {
                    errorEdit(Et_email, getResources().getString(R.string.alert_email));
                }

                else if (!isValidEmail(Et_email.getText().toString())) {
                    errorEdit(Et_email, getResources().getString(R.string.register_label_alert_email));
                }
                else if (Et_password.getText().toString().length()==0) {
                    errorEdit(Et_password, getResources().getString(R.string.password_alert));
                }

                 else if (!isValidPassword(Et_password.getText().toString())) {
                    errorEdit(Et_password, getResources().getString(R.string.register_label_alert_password));
                }
                else if (!isValidPassword(confirmpassword)) {
                    errorEdit(Et_confirmpassword, getResources().getString(R.string.confirm_password_alert));
                }
                else if ( !Et_password.getText().toString().equalsIgnoreCase(confirmpassword) ) {
                    errorEdit(Et_confirmpassword, getResources().getString(R.string.confirm_password_alert));
                }
                else if (Et_phoneNumber.getText().toString().length()==0) {
                    errorEdit(Et_phoneNumber, getResources().getString(R.string.alert_phonenumber));
                }

                else if (!isValidPhoneNumber(Et_phoneNumber.getText().toString())) {
                    errorEdit(Et_phoneNumber, getResources().getString(R.string.register_label_alert_phoneNo));
                } else if (Et_countryCode.getText().toString().length() == 0) {
                    errorEdit(Et_phoneNumber, getResources().getString(R.string.register_label_alert_country_code));
                } else if (!checkbox.isChecked()) {
                    alert(getResources().getString(R.string.action_sorry), getResources().getString(R.string.terms_text));
                } else {

                    cd = new ConnectionDetector(RegisterPage.this);
                    isInternetPresent = cd.isConnectingToInternet();

                    if (isInternetPresent) {

                        mHandler.post(dialogRunnable);

                        //---------Getting GCM Id----------
                        GCMInitializer initializer = new GCMInitializer(RegisterPage.this, new GCMInitializer.CallBack() {
                            @Override
                            public void onRegisterComplete(String registrationId) {

                                GCM_Id = registrationId;
                                postRegisterRequest(RegisterPage.this, Iconstant.RegisterUrl);


                                System.out.println("register----------------" + Iconstant.RegisterUrl);
                            }

                            @Override
                            public void onError(String errorMsg) {
                                postRegisterRequest(RegisterPage.this, Iconstant.RegisterUrl);
                            }
                        });
                        initializer.init();

                    } else {
                        alert(getResources().getString(R.string.action_no_internet_title), getResources().getString(R.string.action_no_internet_message));
                    }
                }
            }
        });

        Et_countryCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // close keyboard
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(Et_countryCode.getWindowToken(), 0);

                picker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
            }
        });

        Et_countryCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // close keyboard
                    InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.hideSoftInputFromWindow(Et_email.getWindowToken(), 0);

                    picker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
                }
            }
        });


        picker.setListener(new CountryPickerListener() {
            @Override
            public void onSelectCountry(String name, String code, String dialCode) {
                picker.dismiss();
                Et_countryCode.setText(dialCode);
                if (Et_countryCode.getText().length() > 0) {
                    Et_phoneNumber.setError(null);
                }

                //Move cursor from one EditText to Another
                Selection.setSelection((Editable) Et_phoneNumber.getText(), Et_countryCode.getSelectionStart());
                Et_phoneNumber.requestFocus();

                // close keyboard
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(Et_countryCode.getWindowToken(), 0);
                // close keyboard
                InputMethodManager mgr_username = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr_username.hideSoftInputFromWindow(Et_userName.getWindowToken(), 0);

            }
        });


        terms_conditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = Iconstant.Terms_Conditions_Url;
                Intent intent = new Intent(RegisterPage.this, Terms_Conditions.class);
                intent.putExtra("url", url);
                intent.putExtra("header", getResources().getString(R.string.terms_conditions_text));
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });


        privacy_policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = Iconstant.Privacy_Polocy;
                Intent intent = new Intent(RegisterPage.this, Terms_Conditions.class);
                intent.putExtra("url",url);
                intent.putExtra("header", getResources().getString(R.string.privacy_policy_text));
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

    }

    private void initialize() {
        cd = new ConnectionDetector(RegisterPage.this);
        isInternetPresent = cd.isConnectingToInternet();
        mHandler = new Handler();
        picker = CountryPicker.newInstance("Select Country");

        Et_email = (MaterialEditText) findViewById(R.id.register_email_edittext);
        Et_password = (MaterialEditText) findViewById(R.id.register_password_edittext);
        Et_userName = (MaterialEditText) findViewById(R.id.register_username_edittext);
        Et_countryCode = (MaterialEditText) findViewById(R.id.register_country_code_edittext);
        Et_phoneNumber = (MaterialEditText) findViewById(R.id.register_phoneNumber_edittext);
        Et_referralCode = (MaterialEditText) findViewById(R.id.register_referral_code_edittext);
        Iv_help = (ImageView) findViewById(R.id.register_referral_code_help_image);
        Rl_back = (RelativeLayout) findViewById(R.id.register_header_back_layout);
        Bt_submit = (Button) findViewById(R.id.register_submit_button);
        Et_firstname=(MaterialEditText)findViewById(R.id.register_firstname);
        Et_lastname=(MaterialEditText)findViewById(R.id.register_lastname);
        Et_confirmpassword=(MaterialEditText)findViewById(R.id.register_confirm_password_edittext);

        checkbox = (CheckBox)findViewById(R.id.checkbox);
        terms_conditions = (TextView)findViewById(R.id.terms_text);
        privacy_policy = (TextView)findViewById(R.id.privacy_policy_text);

        //code to make password editText as dot
        Et_password.setTransformationMethod(new PasswordTransformationMethod());

        Et_email.addTextChangedListener(registerEditorWatcher);
        Et_password.addTextChangedListener(registerEditorWatcher);
        Et_userName.addTextChangedListener(registerEditorWatcher);
        Et_phoneNumber.addTextChangedListener(registerEditorWatcher);
        Et_referralCode.addTextChangedListener(registerEditorWatcher);

        Intent intent = getIntent();
        sCheckClass = intent.getStringExtra("IntentClass");
        EditText editor = new EditText(this);
        editor.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        gpsTracker = new GPSTracker(RegisterPage.this);
        if (gpsTracker.canGetLocation() && gpsTracker.isgpsenabled()) {

            double MyCurrent_lat = gpsTracker.getLatitude();
            double MyCurrent_long = gpsTracker.getLongitude();

            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(MyCurrent_lat, MyCurrent_long, 1);
                if (addresses != null && !addresses.isEmpty()) {

                    String Str_getCountryCode = addresses.get(0).getCountryCode();
                    if (Str_getCountryCode.length() > 0 && !Str_getCountryCode.equals(null) && !Str_getCountryCode.equals("null")) {
                        String Str_countyCode = CountryDialCode.getCountryCode(Str_getCountryCode);
                        Et_countryCode.setText(Str_countyCode);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void Referral_information() {
        final PkDialog refDialog = new PkDialog(RegisterPage.this);
        refDialog.setDialogTitle(getResources().getString(R.string.register_label_referral_schema));
        refDialog.setDialogMessage(getResources().getString(R.string.register_label_referral_schema_content1) + "\n\n" + getResources().getString(R.string.register_label_referral_schema_content2));
        refDialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refDialog.dismiss();
            }
        });
        refDialog.show();
    }

    // --------Code to set error for EditText--------
    private void errorEdit(EditText editName, String msg) {
        Animation shake = AnimationUtils.loadAnimation(RegisterPage.this, R.anim.shake);
        editName.startAnimation(shake);
        editName.setError(msg);
    }

    //--------Alert Method--------
    private void alert(String title, String message) {
        final PkDialog mDialog = new PkDialog(RegisterPage.this);
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

    // --------Code to Check Email Validation--------
    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    private boolean isValidUsername(String username){
        if (username.matches("(.*[A-Z].*)")) {
            return false;
        }
        else{
            return true;
        }
    }


    // --------validating password with retype password--------
    private boolean isValidPassword(String pass) {
        if (pass.length() < 6) {
            return false;
        }
        else if (!pass.matches("(.*[a-z].*)")) {
            return false;
        } else if (!pass.matches("(.*[0-9].*)")) {
            return false;
        }
        else {
            return true;
        }

    }

    // --------validating Phone Number--------
    public static final boolean isValidPhoneNumber(CharSequence target) {
        if (target == null || TextUtils.isEmpty(target) || target.length() <= 6) {
            return false;
        } else {
            return android.util.Patterns.PHONE.matcher(target).matches();
        }
    }


    // --------Code for TextWatcher--------
    private final TextWatcher registerEditorWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            //clear error symbol after entering text
            if (Et_email.getText().length() > 0) {
                Et_email.setError(null);
            }
            if (Et_password.getText().length() > 0) {
                Et_password.setError(null);
            }
            if (Et_userName.getText().length() > 0) {
                Et_userName.setError(null);
            }
            if (Et_countryCode.getText().length() > 0) {
                Et_phoneNumber.setError(null);
            }
            if (Et_phoneNumber.getText().length() > 0) {
                Et_phoneNumber.setError(null);
            }
        }
    };


    //--------Handler Method------------
    Runnable dialogRunnable = new Runnable() {
        @Override
        public void run() {
            mLoadingDialog = new LoadingDialog(RegisterPage.this);
            mLoadingDialog.setLoadingTitle(getResources().getString(R.string.action_signingUp));
            mLoadingDialog.show();
        }
    };


    //-------------Register Post Request---------------
    private void postRegisterRequest(final Context mContext, String url) {

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("first_name", Et_firstname.getText().toString());
        jsonParams.put("last_name", Et_lastname.getText().toString());
        jsonParams.put("email", Et_email.getText().toString());
        jsonParams.put("password", Et_password.getText().toString());
        jsonParams.put("user_name", Et_userName.getText().toString());
        jsonParams.put("country_code", Et_countryCode.getText().toString());
        jsonParams.put("phone_number", Et_phoneNumber.getText().toString());
        jsonParams.put("referal_code", Et_referralCode.getText().toString());
        jsonParams.put("deviceToken", "");
        jsonParams.put("gcm_id", GCM_Id);


        System.out.println("email-----------" + Et_email.getText().toString());
        System.out.println("password-----------" + Et_password.getText().toString());
        System.out.println("user_name-----------" + Et_userName.getText().toString());
        System.out.println("country_code-----------" + Et_countryCode.getText().toString());
        System.out.println("phone_number-----------" + Et_phoneNumber.getText().toString());
        System.out.println("gcm_id-----------" + GCM_Id);


        ServiceRequest mRequest = new ServiceRequest(mContext);
        mRequest.makeServiceRequest(url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("---------register response------------" + response);


                String Str_status = "", Str_message = "";
                try {
                    JSONObject object = new JSONObject(response);
                    Str_status = object.getString("status");


                    if (Str_status.equalsIgnoreCase("1")) {
                        String pushNotificationKey = object.getString("key");
                        String otpStatus = object.getString("otp_status");
                        String otp = object.getString("otp");

                        Intent intent = new Intent(mContext, OtpPage.class);
                        intent.putExtra("UserName", Et_userName.getText().toString());
                        intent.putExtra("Email", Et_email.getText().toString());
                        intent.putExtra("Password", Et_password.getText().toString());
                        intent.putExtra("Phone", Et_phoneNumber.getText().toString());
                        intent.putExtra("CountryCode", Et_countryCode.getText().toString());
                        intent.putExtra("ReferralCode", Et_referralCode.getText().toString());
                        intent.putExtra("GcmID", pushNotificationKey);
                        intent.putExtra("Otp_Status", otpStatus);
                        intent.putExtra("Otp", otp);
                        intent.putExtra("IntentClass", sCheckClass);
                        intent.putExtra("firstname",Et_firstname.getText().toString());
                        intent.putExtra("lastname",Et_lastname.getText().toString());
                        startActivity(intent);
                        finish();
                        LogInPage.logInPageClass.finish();
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);


                    } else {
                        Str_message = object.getString("errors");
                        alert(getResources().getString(R.string.login_label_alert_register_failed), Str_message);
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
