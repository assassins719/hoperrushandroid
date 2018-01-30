package com.hoperrush.app;

/**
 * Created by Prem Kumar on 10/1/2015.
 */

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.android.volley.Request;
import com.countrycodepicker.CountryPicker;
import com.countrycodepicker.CountryPickerListener;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.hoperrush.R;
import com.hoperrush.hockeyapp.FragmentActivityHockeyApp;
import com.hoperrush.iconstant.Iconstant;
import com.hoperrush.core.dialog.PkDialog;
import com.hoperrush.core.facebook.Util;
import com.hoperrush.core.gps.GPSTracker;
import com.hoperrush.core.pushnotification.GCMInitializer;
import com.hoperrush.core.volley.ServiceRequest;
import com.hoperrush.utils.ConnectionDetector;
import com.hoperrush.utils.CountryDialCode;
import com.hoperrush.utils.CurrencySymbolConverter;
import com.hoperrush.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class RegisterFacebook extends FragmentActivityHockeyApp {
    private String email="",profile_image="",username1="",userid="",media="";
    private SessionManager session;

    private RelativeLayout back;
    private EditText Eusername, Epassword, Eemail, EphoneNo, Ereferalcode;
    private Button submit;
    private ImageView help;
    private RelativeLayout Rl_countryCode;
    private TextView Tv_countryCode;

    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private Context context;

    private ServiceRequest mRequest;
    Dialog dialog;
    Handler mHandler;
    //------------------GCM Initialization------------------
    private GoogleCloudMessaging gcm;
    private String GCM_Id = "";

    CountryPicker picker;
    private GPSTracker gpsTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facebook_register);
        context = getApplicationContext();
        initialize();

        help.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Referral_information();
            }
        });

        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // close keyboard
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(back.getWindowToken(), 0);

                logoutFromFacebook();

                onBackPressed();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });

        Rl_countryCode.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                picker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
            }
        });

        picker.setListener(new CountryPickerListener() {
            @Override
            public void onSelectCountry(String name, String code, String dialCode) {
                picker.dismiss();
                Tv_countryCode.setText(dialCode);

                // close keyboard
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(Rl_countryCode.getWindowToken(), 0);
            }
        });

        submit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isValidEmail(Eemail.getText().toString())) {
                    erroredit(Eemail, getResources().getString(R.string.register_label_alert_email));
                } else if (!isValidPassword(Epassword.getText().toString())) {
                    erroredit(Epassword, getResources().getString(R.string.register_label_alert_password));
                } else if (Eusername.getText().toString().length() == 0) {
                    erroredit(Eusername, getResources().getString(R.string.register_label_alert_username));
                } else if (!isValidPhoneNumber(EphoneNo.getText().toString())) {
                    erroredit(EphoneNo, getResources().getString(R.string.register_label_alert_phoneNo));
                } else if (Tv_countryCode.getText().toString().equalsIgnoreCase("code")) {
                    erroredit(EphoneNo, getResources().getString(R.string.register_label_alert_country_code));
                } else {

                    cd = new ConnectionDetector(RegisterFacebook.this);
                    isInternetPresent = cd.isConnectingToInternet();

                    if (isInternetPresent) {

                        mHandler.post(dialogRunnable);

                        //---------Getting GCM Id----------
                        GCMInitializer initializer = new GCMInitializer(RegisterFacebook.this, new GCMInitializer.CallBack() {
                            @Override
                            public void onRegisterComplete(String registrationId) {

                                GCM_Id = registrationId;
                                PostRequest(Iconstant.facebook_register_url);
                            }

                            @Override
                            public void onError(String errorMsg) {
                                PostRequest(Iconstant.facebook_register_url);
                            }
                        });
                        initializer.init();

                    } else {
                        Alert(getResources().getString(R.string.alert_nointernet), getResources().getString(R.string.alert_nointernet));
                    }
                }
            }
        });


        Eusername.setOnEditorActionListener(new OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    CloseKeyboard(Eusername);
                }
                return false;
            }
        });


        Epassword.setOnEditorActionListener(new OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    CloseKeyboard(Epassword);
                }
                return false;
            }
        });

        Eemail.setOnEditorActionListener(new OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    CloseKeyboard(Eemail);
                }
                return false;
            }
        });


        EphoneNo.setOnEditorActionListener(new OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    CloseKeyboard(EphoneNo);
                }
                return false;
            }
        });
        Ereferalcode.setOnEditorActionListener(new OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    CloseKeyboard(Ereferalcode);
                }
                return false;
            }
        });


    }

    private void initialize() {
        session = new SessionManager(RegisterFacebook.this);
        cd = new ConnectionDetector(RegisterFacebook.this);
        isInternetPresent = cd.isConnectingToInternet();
        mHandler = new Handler();
        picker = CountryPicker.newInstance("Select Country");

        back = (RelativeLayout) findViewById(R.id.register_header_back_layout);
        Eusername = (EditText) findViewById(R.id.register_username_edittext_facebook);
        Epassword = (EditText) findViewById(R.id.register_password_edittext_facebook);
        Eemail = (EditText) findViewById(R.id.register_email_edittext_facebook);
        EphoneNo = (EditText) findViewById(R.id.register_phoneNumber_edittext_facebook);
        Ereferalcode = (EditText) findViewById(R.id.register_referral_code_edittext_facebook);
        help = (ImageView) findViewById(R.id.register_referral_code_help_image_facebook);
        Tv_countryCode = (TextView) findViewById(R.id.register_country_code_edittext_facebook);
        submit = (Button) findViewById(R.id.register_submit_button_facebook);
        submit.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Medium.ttf"));


        Intent intent = getIntent();
        userid = intent.getStringExtra("userId");
        username1 = intent.getStringExtra("userName");
        email = intent.getStringExtra("userEmail");
        media = intent.getStringExtra("media");
        profile_image=intent.getStringExtra("userimage");
        if(!username1.equalsIgnoreCase(""))
        {
            Eusername.setText(username1);
        }
        if(!email.equalsIgnoreCase(""))
        {
            Eemail.setText(email);
        }

        //code to make password editText as dot
        Epassword.setTransformationMethod(new PasswordTransformationMethod());

        Eusername.addTextChangedListener(loginEditorWatcher);
        Epassword.addTextChangedListener(loginEditorWatcher);


        gpsTracker = new GPSTracker(RegisterFacebook.this);
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
                        Tv_countryCode.setText(Str_countyCode);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void Referral_information() {
        final PkDialog refDialog = new PkDialog(RegisterFacebook.this);
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

    private void CloseKeyboard(EditText edittext) {
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(edittext.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void logoutFromFacebook() {
        Util.clearCookies(RegisterFacebook.this);
        // your sharedPrefrence
        SharedPreferences.Editor editor = context.getSharedPreferences("CASPreferences",Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();
    }

    //--------------Alert Method-----------
    private void Alert(String title, String alert) {

        final PkDialog mDialog = new PkDialog(RegisterFacebook.this);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(alert);
        mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();

    }

    //----------------------Code for TextWatcher-------------------------
    private final TextWatcher loginEditorWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            //clear error symbol after entering text
            if (Eusername.getText().length() > 0) {
                Eusername.setError(null);
            }
            if (Epassword.getText().length() > 0) {
                Epassword.setError(null);
            }
            if (Eemail.getText().length() > 0) {
                Eemail.setError(null);
            }
            if (EphoneNo.getText().length() > 0) {
                EphoneNo.setError(null);
            }

        }
    };


    //-------------------------code to Check Email Validation-----------------------
    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    //--------------------Code to set error for EditText-----------------------
    private void erroredit(EditText editname, String msg) {
        Animation shake = AnimationUtils.loadAnimation(RegisterFacebook.this, R.anim.shake);
        editname.startAnimation(shake);

        ForegroundColorSpan fgcspan = new ForegroundColorSpan(Color.parseColor("#CC0000"));
        SpannableStringBuilder ssbuilder = new SpannableStringBuilder(msg);
        ssbuilder.setSpan(fgcspan, 0, msg.length(), 0);
        editname.setError(ssbuilder);
    }

    // validating password with retype password
    private boolean isValidPassword(String pass) {
        if (pass.length() < 6) {
            return false;
        }
            /*
             * else if(!pass.matches("(.*[A-Z].*)")) { return false; }
			 */
       /* else if (!pass.matches("(.*[a-z].*)")) {
            return false;
        } else if (!pass.matches("(.*[0-9].*)")) {
            return false;
        }*/
            /*
             * else if(!pass.matches(
			 * "(.*[,~,!,@,#,$,%,^,&,*,(,),-,_,=,+,[,{,],},|,;,:,<,>,/,?].*$)")) {
			 * return false; }
			 */
        else {
            return true;
        }

    }

    // validating Phone Number
    public static final boolean isValidPhoneNumber(CharSequence target) {
        if (target == null || TextUtils.isEmpty(target) || target.length() <= 5) {
            return false;
        } else {
            return android.util.Patterns.PHONE.matcher(target).matches();
        }
    }

    //--------Handler Method------------
    Runnable dialogRunnable = new Runnable() {
        @Override
        public void run() {
            dialog = new Dialog(RegisterFacebook.this);
            dialog.getWindow();
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.custom_loading);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

            TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
            dialog_title.setText(getResources().getString(R.string.action_verifying));
        }
    };


    // -------------------------code for Login Post Request----------------------------------
    private void PostRequest(String Url) {

        System.out.println("--------------facebook register url-------------------" + Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_name", Eusername.getText().toString());
        jsonParams.put("prof_pic", profile_image );
        jsonParams.put("email_id", Eemail.getText().toString());
       // jsonParams.put("password", Epassword.getText().toString());
        jsonParams.put("phone", EphoneNo.getText().toString());
        jsonParams.put("country_code", Tv_countryCode.getText().toString());
        //jsonParams.put("referal_code", Ereferalcode.getText().toString());
        jsonParams.put("gcm_id", GCM_Id);
        jsonParams.put("fb_id", media);

        mRequest = new ServiceRequest(RegisterFacebook.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("--------------facebook register reponse-------------------" + response);

//                String Sstatus = "", Smessage = "", Sotp_status = "", Sotp = "";
                String Sstatus = "", Smessage = "", Suser_image = "", Suser_id = "", Suser_name = "",
                        Semail = "", Scountry_code = "", SphoneNo = "", Sreferal_code = "", Scategory = "",
                        SsecretKey = "", SwalletAmount = "", ScurrencyCode = "";
                String sCurrencySymbol="";

                String gcmId="";


                try {

                    JSONObject object = new JSONObject(response);
                    Sstatus = object.getString("status");
                    Smessage = object.getString("message");

                    if (Sstatus.equalsIgnoreCase("1")) {
                        Suser_image = object.getString("user_image");
                        Suser_id = object.getString("user_id");
                        Suser_name = object.getString("user_name");
                        Semail = object.getString("email");
                        Scountry_code = object.getString("country_code");
                        SphoneNo = object.getString("phone_number");
                        Sreferal_code = object.getString("referal_code");
                        Scategory = object.getString("category");
                        SsecretKey = object.getString("sec_key");
                        SwalletAmount = object.getString("wallet_amount");
                        ScurrencyCode = object.getString("currency");

                        //gcmId = object.getString("key");

                        sCurrencySymbol = CurrencySymbolConverter.getCurrencySymbol(ScurrencyCode);
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if (Sstatus.equalsIgnoreCase("1")) {



                } else {

                    final PkDialog mDialog = new PkDialog(RegisterFacebook.this);
                    mDialog.setDialogTitle(getResources().getString(R.string.action_error));
                    mDialog.setDialogMessage(Smessage);
                    mDialog.setCancelOnTouchOutside(false);
                    mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDialog.dismiss();
                        }
                    });
                    mDialog.show();
                }


               /* if (Sstatus.equalsIgnoreCase("1")) {
                    Intent intent = new Intent(context, FacebookOtpPage.class);
                    intent.putExtra("Otp_Status", Sotp_status);
                    intent.putExtra("Otp", Sotp);
                    intent.putExtra("UserName", Eusername.getText().toString());
                    intent.putExtra("Email", Eemail.getText().toString());
                    intent.putExtra("Password", Epassword.getText().toString());
                    intent.putExtra("Phone", EphoneNo.getText().toString());
                    intent.putExtra("CountryCode", Tv_countryCode.getText().toString());
                    intent.putExtra("ReferalCode", Ereferalcode.getText().toString());
                    intent.putExtra("GcmID", GCM_Id);
                    intent.putExtra("MediaId", userid);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                } else {
                    Alert(getResources().getString(R.string.login_label_alert_register_failed), Smessage);
                }*/

                // close keyboard
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(Eusername.getWindowToken(), 0);

                dialog.dismiss();
            }

            @Override
            public void onErrorListener() {
                dialog.dismiss();
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        logoutFromFacebook();
    }

    //-----------------Move Back on pressed phone back button------------------
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)) {

            // close keyboard
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(back.getWindowToken(), 0);

            logoutFromFacebook();

            RegisterFacebook.this.finish();
            RegisterFacebook.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            return true;
        }
        return false;
    }
}
