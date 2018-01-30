package com.hoperrush.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Handler;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.countrycodepicker.CountryPicker;
import com.countrycodepicker.CountryPickerListener;
import com.hoperrush.R;
import com.hoperrush.core.dialog.LoadingDialog;
import com.hoperrush.core.dialog.PkDialog;
import com.hoperrush.core.facebook.Util;
import com.hoperrush.core.gps.GPSTracker;
import com.hoperrush.core.pushnotification.GCMInitializer;
import com.hoperrush.core.volley.ServiceRequest;
import com.hoperrush.hockeyapp.FragmentActivityHockeyApp;
import com.hoperrush.iconstant.Iconstant;
import com.hoperrush.utils.ConnectionDetector;
import com.hoperrush.utils.CountryDialCode;
import com.hoperrush.utils.CurrencySymbolConverter;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class FBRegisterpage extends FragmentActivityHockeyApp
{
    private MaterialEditText Et_email, Et_password, Et_userName, Et_phoneNumber, Et_referralCode;
    private ImageView Iv_help;
    private RelativeLayout Rl_back;
    private Button Bt_submit;
    private ConnectionDetector cd;
    private boolean isInternetPresent = false;
    private LoadingDialog mLoadingDialog;
    private String email="",profile_image="",username1="",userid="",media="";
    //-------GCM Initialization-----
    private String GCM_Id = "";
    private Handler mHandler;
    private RelativeLayout back;
    private EditText Eusername, Epassword, Eemail, EphoneNo, Ereferalcode;
    private Button submit;
    CountryPicker picker;
    private GPSTracker gpsTracker;
    private String sCheckClass="";
    private ImageView help;
    private RelativeLayout Rl_countryCode;
    private MaterialEditText Tv_countryCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fbregisterpage);
        initialize();

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Referral_information();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
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

        Tv_countryCode.setOnClickListener(new View.OnClickListener() {
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

//                // close keyboard
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(Tv_countryCode.getWindowToken(), 0);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isValidEmail(Eemail.getText().toString())) {
                    erroredit(Eemail, getResources().getString(R.string.register_label_alert_email));
                }
//                else if (!isValidPassword(Epassword.getText().toString())) {
//                    erroredit(Epassword, getResources().getString(R.string.register_label_alert_password));
//                }

                else if (Eusername.getText().toString().length() == 0) {
                    erroredit(Eusername, getResources().getString(R.string.register_label_alert_username));
                }
                else if (!isValidPhoneNumber(EphoneNo.getText().toString())) {
                    erroredit(EphoneNo, getResources().getString(R.string.register_label_alert_phoneNo));
                }
                else if (Tv_countryCode.getText().toString().equalsIgnoreCase("code")) {
                    erroredit(EphoneNo, getResources().getString(R.string.register_label_alert_country_code));
                } else {

                    cd = new ConnectionDetector(FBRegisterpage.this);
                    isInternetPresent = cd.isConnectingToInternet();

                    if (isInternetPresent) {

                        mHandler.post(dialogRunnable);

                        //---------Getting GCM Id----------
                        GCMInitializer initializer = new GCMInitializer(FBRegisterpage.this, new GCMInitializer.CallBack() {
                            @Override
                            public void onRegisterComplete(String registrationId) {

                                GCM_Id = registrationId;
                                postRegisterRequest(getApplicationContext(),Iconstant.social_check_url);
                            }

                            @Override
                            public void onError(String errorMsg) {
                              //  PostRequest(Iconstant.facebook_register_url);
                            }
                        });
                        initializer.init();

                    } else {
                        Alert(getResources().getString(R.string.alert_nointernet), getResources().getString(R.string.alert_nointernet));
                    }
                }
            }
        });


        Eusername.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    CloseKeyboard(Eusername);
                }
                return false;
            }
        });


        Epassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    CloseKeyboard(Epassword);
                }
                return false;
            }
        });

        Eemail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    CloseKeyboard(Eemail);
                }
                return false;
            }
        });


        EphoneNo.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    CloseKeyboard(EphoneNo);
                }
                return false;
            }
        });
        Ereferalcode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    CloseKeyboard(Ereferalcode);
                }
                return false;
            }
        });

    }

    private void initialize() {
        cd = new ConnectionDetector(FBRegisterpage.this);
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
        Tv_countryCode = (MaterialEditText) findViewById(R.id.register_country_code_edittext_facebook);
        submit = (Button) findViewById(R.id.register_submit_button_facebook);
        submit.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Medium.ttf"));

        //code to make password editText as dot
       // Et_password.setTransformationMethod(new PasswordTransformationMethod());

//        Et_email.addTextChangedListener(registerEditorWatcher);
//        Et_password.addTextChangedListener(registerEditorWatcher);
//        Et_userName.addTextChangedListener(registerEditorWatcher);
//        Et_phoneNumber.addTextChangedListener(registerEditorWatcher);
//        Et_referralCode.addTextChangedListener(registerEditorWatcher);

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


        gpsTracker = new GPSTracker(FBRegisterpage.this);
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

    public void logoutFromFacebook() {
        Util.clearCookies(FBRegisterpage.this);
        // your sharedPrefrence
        SharedPreferences.Editor editor = getSharedPreferences("CASPreferences",Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();
    }



    private void Alert(String title, String alert) {

        final PkDialog mDialog = new PkDialog(FBRegisterpage.this);
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



    private void CloseKeyboard(EditText edittext) {
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(edittext.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void erroredit(EditText editname, String msg) {
        Animation shake = AnimationUtils.loadAnimation(FBRegisterpage.this, R.anim.shake);
        editname.startAnimation(shake);

        ForegroundColorSpan fgcspan = new ForegroundColorSpan(Color.parseColor("#CC0000"));
        SpannableStringBuilder ssbuilder = new SpannableStringBuilder(msg);
        ssbuilder.setSpan(fgcspan, 0, msg.length(), 0);
        editname.setError(ssbuilder);
    }


    private void Referral_information()
    {
        final PkDialog refDialog = new PkDialog(FBRegisterpage.this);
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
        Animation shake = AnimationUtils.loadAnimation(FBRegisterpage.this, R.anim.shake);
        editName.startAnimation(shake);
        editName.setError(msg);
    }

    //--------Alert Method--------
    private void alert(String title, String message) {
        final PkDialog mDialog = new PkDialog(FBRegisterpage.this);
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


    // --------validating password with retype password--------
    private boolean isValidPassword(String pass) {
        if (pass.length() < 6) {
            return false;
        }
            /*
             * else if(!pass.matches("(.*[A-Z].*)")) { return false; }
			 */
        else if (!pass.matches("(.*[a-z].*)")) {
            return false;
        } else if (!pass.matches("(.*[0-9].*)")) {
            return false;
        }
            /*
			 * else if(!pass.matches(
			 * "(.*[,~,!,@,#,$,%,^,&,*,(,),-,_,=,+,[,{,],},|,;,:,<,>,/,?].*$)")) {
			 * return false; }
			 */
        else {
            return true;
        }

    }

    // --------validating Phone Number--------
    public static final boolean isValidPhoneNumber(CharSequence target) {
        if (target == null || TextUtils.isEmpty(target) || target.length() <= 9) {
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
            if (Tv_countryCode.getText().length() > 0) {
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
            mLoadingDialog = new LoadingDialog(FBRegisterpage.this);
            mLoadingDialog.setLoadingTitle(getResources().getString(R.string.action_signingUp));
            mLoadingDialog.show();
        }
    };


    //-------------Register Post Request---------------
    private void postRegisterRequest(final Context mContext, String url) {

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("email_id", Eemail.getText().toString());
       // jsonParams.put("prof_pic",profile_image);
        jsonParams.put("user_name", Eusername.getText().toString());
      //  jsonParams.put("first_name", "");
      //  jsonParams.put("last_name", "");
        jsonParams.put("country_code", Tv_countryCode.getText().toString());
        jsonParams.put("phone", EphoneNo.getText().toString());
       // jsonParams.put("fb_id",userid);
        jsonParams.put("deviceToken", "");
        jsonParams.put("gcm_id", GCM_Id);


        System.out.println("email-----------"+Eemail.getText().toString());
       // System.out.println("password-----------"+Et_password.getText().toString());
        System.out.println("user_name-----------"+Eusername.getText().toString());
        System.out.println("country_code-----------"+Tv_countryCode.getText().toString());
        System.out.println("phone_number-----------"+EphoneNo.getText().toString());
        System.out.println("gcm_id-----------"+GCM_Id);



        ServiceRequest mRequest = new ServiceRequest(mContext);
        mRequest.makeServiceRequest(url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("---------register response------------" + response);

                String Sstatus = "", Smessage = "", Suser_image = "", Suser_id = "", Suser_name = "",
                        Semail = "", Scountry_code = "", SphoneNo = "", Sreferal_code = "", Scategory = "",
                        SsecretKey = "", SwalletAmount = "", ScurrencyCode = "";
                String sCurrencySymbol="";
                  String otp="",otp_status="",key="";
                String gcmId="";

                String Str_status = "", Str_message = "";
                try {

                    JSONObject object = new JSONObject(response);
                    Sstatus = object.getString("status");
                    Smessage = object.getString("message");

                    if (Sstatus.equalsIgnoreCase("1")) {

                        Suser_name = object.getString("user_name");
                        Semail = object.getString("email");
                        Scountry_code = object.getString("country_code");
                        SphoneNo = object.getString("phone_number");



                        ScurrencyCode = object.getString("key");
                        otp=object.getString("otp");
                        otp_status=object.getString("otp_status");
                        //gcmId = object.getString("key");

                        sCurrencySymbol = CurrencySymbolConverter.getCurrencySymbol(ScurrencyCode);


                    }

                    else {
                        alert(getResources().getString(R.string.login_label_alert_register_failed), Str_message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (Sstatus.equalsIgnoreCase("1")) {
                    Intent intent = new Intent(getApplicationContext(), Facebookotppage.class);
                    intent.putExtra("Otp_Status", otp_status);
                    intent.putExtra("Otp", otp);
                    intent.putExtra("UserName", Eusername.getText().toString());
                    intent.putExtra("Email", Eemail.getText().toString());
                    intent.putExtra("Phone", EphoneNo.getText().toString());
                    intent.putExtra("CountryCode", Tv_countryCode.getText().toString());
                    intent.putExtra("ReferalCode", Ereferalcode.getText().toString());
                    intent.putExtra("GcmID", GCM_Id);
                    intent.putExtra("MediaId", userid);
                    intent.putExtra("profileimage",profile_image);

                    startActivity(intent);
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);


                } else {

                    final PkDialog mDialog = new PkDialog(FBRegisterpage.this);
                    mDialog.setDialogTitle(getResources().getString(R.string.action_error));
                    mDialog.setDialogMessage(Smessage);
                    mDialog.setCancelOnTouchOutside(false);
                    mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDialog.dismiss();
                        }
                    });
                    mDialog.show();
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
