package com.hoperrush.app;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.hoperrush.R;
import com.hoperrush.core.facebook.Util;
import com.hoperrush.core.socket.ChatMessageService;
import com.hoperrush.hockeyapp.ActivityHockeyApp;
import com.hoperrush.iconstant.Iconstant;
import com.hoperrush.core.dialog.LoadingDialog;
import com.hoperrush.core.dialog.PkDialog;
import com.hoperrush.core.facebook.AsyncFacebookRunner;
import com.hoperrush.core.facebook.DialogError;
import com.hoperrush.core.facebook.Facebook;
import com.hoperrush.core.facebook.FacebookError;
import com.hoperrush.core.pushnotification.GCMInitializer;
import com.hoperrush.core.socket.SocketHandler;
import com.hoperrush.core.volley.ServiceRequest;
import com.hoperrush.utils.ConnectionDetector;
import com.hoperrush.utils.CurrencySymbolConverter;
import com.hoperrush.utils.HideSoftKeyboard;
import com.hoperrush.utils.SessionManager;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;

/**
 * Casperon Technology on 11/27/2015.
 */
public class LogInPage extends ActivityHockeyApp implements View.OnClickListener {
    private MaterialEditText Et_email, Et_password;
    private RelativeLayout Rl_back;
    private RelativeLayout Rl_forgotPassword;
    private Button Bt_submit;
    private ConnectionDetector cd;
    private boolean isInternetPresent = false;
    private SessionManager sessionManager;
    private LoadingDialog mLoadingDialog;
    private Dialog dialog;
    private RelativeLayout Rl_layout_register;

    //-------GCM Initialization-----
    private String GCM_Id = "";
    private Handler mHandler;

    private String sCheckClass = "";

    public static LogInPage logInPageClass;
    private SocketHandler socketHandler;
    // private static String APP_ID = "644498489050139";

    //  private static String APP_ID = "338145559883580";
    private static String APP_ID = "229602460782482";
    // Instance of FaceBook Class
    private final Facebook facebook = new Facebook(APP_ID);

    private Button facebooklayout;
    private String socialLoginCheck = "";

    AsyncFacebookRunner mAsyncRunner;
    private SharedPreferences mPrefs;
    private Context context;

    private String sMediaId = "";
    private String email = "", profile_image = "", username1 = "", userid = "";
    private ServiceRequest mRequest;
    private String sCurrencySymbol = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        HideSoftKeyboard.setupUI(
                LogInPage.this.getWindow().getDecorView(),
                LogInPage.this);
        logInPageClass = LogInPage.this;
        context = getApplicationContext();
        mAsyncRunner = new AsyncFacebookRunner(facebook);
        initialize();

        Rl_layout_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LogInPage.this, RegisterPage.class);
                intent.putExtra("IntentClass", sCheckClass);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });


        facebooklayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                socialLoginCheck = "facebook";
                logoutFromFacebook();
                loginToFacebook();
            }
        });


    }

    private void initialize() {
        cd = new ConnectionDetector(LogInPage.this);
        isInternetPresent = cd.isConnectingToInternet();
        sessionManager = new SessionManager(LogInPage.this);
        Rl_forgotPassword = (RelativeLayout) findViewById(R.id.login_forgot_pwd_layout);
        mHandler = new Handler();

        socketHandler = SocketHandler.getInstance(this);

        Et_email = (MaterialEditText) findViewById(R.id.login_email_edittext);
        Et_password = (MaterialEditText) findViewById(R.id.login_password_edittext);
        Rl_back = (RelativeLayout) findViewById(R.id.login_header_back_layout);
        Bt_submit = (Button) findViewById(R.id.login_submit_button);
        Rl_layout_register = (RelativeLayout) findViewById(R.id.login_forgot_register_layout);
        facebooklayout = (Button) findViewById(R.id.login_facebook_button);


        Bt_submit.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Medium.ttf"));

        //code to make password editText as dot
        Et_password.setTransformationMethod(new PasswordTransformationMethod());

        Intent intent = getIntent();
        sCheckClass = intent.getStringExtra("IntentClass");

        Rl_back.setOnClickListener(this);
        Bt_submit.setOnClickListener(this);
        Rl_forgotPassword.setOnClickListener(this);

        Et_email.addTextChangedListener(loginEditorWatcher);
        Et_password.addTextChangedListener(loginEditorWatcher);

        if (isInternetPresent) {

            Appinfo(LogInPage.this, Iconstant.App_Info);

        }


    }

    @Override
    public void onClick(View v) {

        if (v == Rl_back) {
            // close keyboard
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(Rl_back.getWindowToken(), 0);

            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        } else if (v == Rl_forgotPassword) {
            Intent i = new Intent(LogInPage.this, ForgotPassword.class);
            startActivity(i);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } else if (v == Bt_submit) {

//            if (!Et_email.getText().toString().matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+")) {
//                erroredit(Et_email, getResources().getString(R.string.login_label_alert_invalid_email));

            if (Et_email.getText().toString().length() == 0) {
                Et_email.setError("Email can't be empty");
            } else if (Et_password.getText().toString().length() == 0) {
                Et_password.setError(getResources().getString(R.string.login_label_alert_password));
            } else {
                login();
            }

        }
    }

    public void logoutFromFacebook() {
        Util.clearCookies(LogInPage.this);
        // your sharedPrefrence
        SharedPreferences.Editor editor = context.getSharedPreferences("CASPreferences", Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();
    }


public void login(){
    cd = new ConnectionDetector(LogInPage.this);
    isInternetPresent = cd.isConnectingToInternet();

    if (isInternetPresent) {

        mHandler.post(dialogRunnable);

        //---------Getting GCM Id----------
        GCMInitializer initializer = new GCMInitializer(LogInPage.this, new GCMInitializer.CallBack() {
            @Override
            public void onRegisterComplete(String registrationId) {

                GCM_Id = registrationId;
                postLoginRequest(LogInPage.this, Iconstant.loginUrl);
            }

            @Override
            public void onError(String errorMsg) {
                postLoginRequest(LogInPage.this, Iconstant.loginUrl);
            }
        });
        initializer.init();
    } else {
        alert(getResources().getString(R.string.action_no_internet_title), getResources().getString(R.string.action_no_internet_message));
    }
}

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        facebook.authorizeCallback(requestCode, resultCode, data);
    }


    //--------Handler Method------------
    Runnable dialogRunnable = new Runnable() {
        @Override
        public void run() {
            mLoadingDialog = new LoadingDialog(LogInPage.this);
            mLoadingDialog.setLoadingTitle(getResources().getString(R.string.action_signingIn));
            mLoadingDialog.show();
        }
    };

    //------code to Check Email Validation------
    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }


    //--------------------Code to set error for EditText-----------------------
    private void errorEdit(EditText editName, String msg) {
        Animation shake = AnimationUtils.loadAnimation(LogInPage.this, R.anim.shake);
        editName.startAnimation(shake);
        ForegroundColorSpan fgcspan = new ForegroundColorSpan(
                Color.parseColor("#cc0000"));
        SpannableStringBuilder ssbuilder = new SpannableStringBuilder(msg);
        ssbuilder.setSpan(fgcspan, 0, msg.length(), 0);
        editName.setError(msg);
    }


    private void erroredit(EditText editname, String msg) {
        Animation shake = AnimationUtils.loadAnimation(LogInPage.this,
                R.anim.shake);
        editname.startAnimation(shake);

        ForegroundColorSpan fgcspan = new ForegroundColorSpan(
                Color.parseColor("#cc0000"));
        SpannableStringBuilder ssbuilder = new SpannableStringBuilder(msg);
        ssbuilder.setSpan(fgcspan, 0, msg.length(), 0);
        editname.setError(ssbuilder);
    }

    //------Alert Method-----
    private void alert(String title, String message) {
        final PkDialog mDialog = new PkDialog(LogInPage.this);
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
            if (Et_email.getText().length() > 0) {
                Et_email.setError(null);
            }
            if (Et_password.getText().length() > 0) {
                Et_password.setError(null);
            }
        }
    };


    //-------------Login Post Request---------------
    private void postLoginRequest(Context mContext, String url) {
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("email", Et_email.getText().toString());
        jsonParams.put("password", Et_password.getText().toString());
        jsonParams.put("deviceToken", "");
        jsonParams.put("gcm_id", GCM_Id);

        System.out.println("-----------email--------------" + Et_email.getText().toString());
        System.out.println("-----------password--------------" + Et_password.getText().toString());
        System.out.println("------------deviceToken-------------");
        System.out.println("------------gcm_id-------------" + GCM_Id);

        ServiceRequest mRequest = new ServiceRequest(mContext);
        mRequest.makeServiceRequest(url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("---------loginresponse------------" + response);

                String Sstatus = "", Smessage = "";
                try {
                    JSONObject object = new JSONObject(response);
                    Sstatus = object.getString("status");


                    if (Sstatus.equalsIgnoreCase("1")) {
                        String userId = object.getString("user_id");
                        String userName = object.getString("user_name");
                        String userEmail = object.getString("email");
                        String userImage = object.getString("user_image");
                        String countryCode = object.getString("country_code");
                        String phoneNumber = object.getString("phone_number");
                        String locationId = object.getString("location_id");
                        String locationName = object.getString("location_name");
                        String referralCode = object.getString("referal_code");
                        String categoryId = object.getString("category");
                        String walletAmount = object.getString("wallet_amount");
                        String currencyCode = object.getString("currency");
                        String tasker_notifaication = object.getString("provider_notification");

                        //  String gcmId = object.getString("key");
                        String sSoc_Key = object.getString("soc_key");

                        sessionManager.setSocketTaskId(userId);
                        sessionManager.createLoginSession(userId, userName, userEmail, userImage, countryCode, phoneNumber, categoryId, referralCode);

                        sessionManager.createWalletSession(walletAmount, currencyCode);
                        sessionManager.setXmppKey(userId, sSoc_Key);
                        SocketHandler.getInstance(LogInPage.this).getSocketManager().connect();

                        if(!ChatMessageService.isStarted()) {
                            Intent intent = new Intent(LogInPage.this, ChatMessageService.class);
                            startService(intent);
                        }
                        sessionManager.createLocationSession(locationId, locationName);

                        System.out.println("sCheckClass---------------" + sCheckClass);

                        if (sCheckClass.equalsIgnoreCase("1")) {

                            NavigationDrawer.navigationDrawerClass.finish();
                            Intent intent = new Intent(LogInPage.this, NavigationDrawer.class);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(R.anim.enter, R.anim.exit);
                        } else if (sCheckClass.equalsIgnoreCase("2")) {
                            NavigationDrawer.navigationNotifyChange();
                            Intent broadcastIntent = new Intent();
                            broadcastIntent.setAction("com.package.ACTION_CLASS_APPOINTMENT_REFRESH");
                            sendBroadcast(broadcastIntent);

                            Intent broadcastIntentnavigation = new Intent();
                            broadcastIntentnavigation.setAction("com.package.NAVIGATIONUPDATE_REFRESH");
                            sendBroadcast(broadcastIntentnavigation);

                            finish();
                            overridePendingTransition(R.anim.enter, R.anim.exit);
                        }
                    }

                    else if(Sstatus.equalsIgnoreCase("3")){
                        mLoadingDialog.dismiss();
                        login();
                    }
                    else {
                        Smessage = object.getString("message");
                        alert(getResources().getString(R.string.login_label_alert_signIn_failed), Smessage);
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


    //--------Handler Method------------
    Runnable dialogFacebookRunnable = new Runnable() {
        @Override
        public void run() {
            dialog = new Dialog(LogInPage.this);
            dialog.getWindow();
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.custom_loading);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

            TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
            dialog_title.setText(getResources().getString(R.string.action_loading));
        }
    };


    //--------------------------------code for faceBook------------------------------

    public void loginToFacebook() {

        System.out.println("---------------facebook login1-----------------------");
        mPrefs = context.getSharedPreferences("CASPreferences", Context.MODE_PRIVATE);
        String access_token = mPrefs.getString("access_token", null);
        long expires = mPrefs.getLong("access_expires", 0);

        if (access_token != null) {
            facebook.setAccessToken(access_token);
        }


        System.out.println("---------------facebook expires-----------------------" + expires);

        if (expires != 0) {
            facebook.setAccessExpires(expires);
        }

        System.out.println("---------------facebook isSessionValid-----------------------" + facebook.isSessionValid());
        if (!facebook.isSessionValid()) {
            facebook.authorize(LogInPage.this,
                    new String[]{"email"},
                    new Facebook.DialogListener() {

                        @Override
                        public void onCancel() {
                            // Function to handle cancel event
                            Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onComplete(Bundle values) {
                            // Function to handle complete event
                            // Edit Preferences and update facebook acess_token
                            SharedPreferences.Editor editor = mPrefs.edit();
                            editor.putString("access_token",
                                    facebook.getAccessToken());
                            editor.putLong("access_expires", facebook.getAccessExpires());
                            editor.commit();
                            String accessToken = facebook.getAccessToken();
                            System.out.println("Token----------------" + accessToken);
                            mHandler.post(dialogFacebookRunnable);
                            //---------Getting GCM Id----------
                            GCMInitializer initializer = new GCMInitializer(LogInPage.this, new GCMInitializer.CallBack() {
                                @Override
                                public void onRegisterComplete(String registrationId) {
                                    GCM_Id = registrationId;
                                    // getProfileInformation();

                                    String accessToken1 = facebook.getAccessToken();
                                    JsonRequest("https://graph.facebook.com/me?fields=id,name,picture,email&access_token=" + accessToken1);
                                }

                                @Override
                                public void onError(String errorMsg) {
                                    // getProfileInformation();
                                    Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_LONG).show();
                                    String accessToken1 = facebook.getAccessToken();
                                    // JsonRequest("https://graph.facebook.com/me?fields=id,name,picture,email&access_token=" + accessToken1);
                                }
                            });
                            initializer.init();
                        }

                        @Override
                        public void onError(DialogError error) {
                            // Function to handle error
                            Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onFacebookError(FacebookError fberror) {
                            // Function to handle Facebook errors
                            Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }


    public void getProfileInformation() {
        mAsyncRunner.request("me", new AsyncFacebookRunner.RequestListener() {
            @Override
            public void onComplete(String response, Object state) {
                String json = response;
                try {
                    // Facebook Profile JSON data
                    JSONObject profile = new JSONObject(json);
                    sMediaId = profile.getString("id");

                    System.out.println("FB_ID------------" + sMediaId);
                    LogInPage.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //  PostRequest_facebook(Iconstant.social_check_url);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onIOException(IOException e, Object state) {
            }

            @Override
            public void onFileNotFoundException(FileNotFoundException e,
                                                Object state) {
            }

            @Override
            public void onMalformedURLException(MalformedURLException e,
                                                Object state) {
            }

            @Override
            public void onFacebookError(FacebookError e, Object state) {
            }
        });
    }

    private void JsonRequest(final String Url) {

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        mRequest = new ServiceRequest(LogInPage.this);
        mRequest.makeServiceRequest(Url, Request.Method.GET, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("--------------access token reponse-------------------" + response);

                try {

                    JSONObject object = new JSONObject(response);
                    System.out.println("---------facebook profile------------" + response);


                    sMediaId = object.getString("id");
                    userid = object.getString("id");
                    profile_image = "https://graph.facebook.com/" + object.getString("id") + "/picture?type=large";
                    username1 = object.getString("name");
                    username1 = username1.replaceAll("\\s+", "");


                    if (object.has("email")) {
                        email = object.getString("email");
                    } else {
                        email = "";
                    }
                    System.out.println("-------sMediaId------------------" + sMediaId);
                    System.out.println("-------email------------------" + email);
                    System.out.println("-----------------userid-------------------------------" + userid);
                    System.out.println("----------------profile_image-----------------" + profile_image);
                    System.out.println("-----------username----------" + username1);

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                //post execute
                dialog.dismiss();


                //  PostRequest_facebook(Iconstant.social_check_url);
                PostRequest_facebook_login(Iconstant.facebook_login_url);
            }

            @Override
            public void onErrorListener() {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
    }


    private void PostRequest_facebook(final String Url) {
        final ProgressDialog progress;
        progress = new ProgressDialog(LogInPage.this);
        progress.setMessage(getResources().getString(R.string.action_pleasewait));
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(false);
        progress.show();

        System.out.println("-----------media_id 1------------" + sMediaId);
        System.out.println("-----------deviceToken 1------------" + "");
        System.out.println("-----------gcm_id 1------------" + GCM_Id);
        System.out.println("-----------email 1------------" + email);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("media_id", sMediaId);
        jsonParams.put("deviceToken", "");
        jsonParams.put("gcm_id", GCM_Id);
        jsonParams.put("email", email);

        mRequest = new ServiceRequest(LogInPage.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("--------------Login reponse-------------------" + response);

                String Sstatus = "", Smessage = "", Suser_image = "", Suser_id = "", Suser_name = "",
                        Semail = "", Scountry_code = "", SphoneNo = "", Sreferal_code = "", Scategory = "", SsecretKey = "", SwalletAmount = "", ScurrencyCode = "";

                String gcmId = "";
                String is_alive_other = "";

                try {

                    JSONObject object = new JSONObject(response);
                    Sstatus = object.getString("status");
                    Smessage = object.getString("message");
                    System.out.println("---------Sstatus--------" + Sstatus);
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

                        gcmId = object.getString("key");
                        is_alive_other = object.getString("is_alive_other");

                        ScurrencyCode = object.getString("currency");
                        sCurrencySymbol = CurrencySymbolConverter.getCurrencySymbol(ScurrencyCode);
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


            }

            @Override
            public void onErrorListener() {
                if (progress != null) {
                    progress.dismiss();
                }
            }
        });
    }


    //-----------------------------------------------------------------FaceBook Login url-------------------------------------------------------
    private void PostRequest_facebook_login(final String Url) {
//    final ProgressDialog progress;
//    progress = new ProgressDialog(LogInPage.this);
//    progress.setMessage(getResources().getString(R.string.action_pleasewait));
//    progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//    progress.setIndeterminate(false);
//    progress.show();

        System.out.println("-----------media_id 1------------" + sMediaId);
        System.out.println("-----------deviceToken 1------------" + "");
        System.out.println("-----------gcm_id 1------------" + GCM_Id);
        System.out.println("-----------email 1------------" + email);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("fb_id", sMediaId);
        jsonParams.put("deviceToken", "");
        jsonParams.put("gcm_id", GCM_Id);
        jsonParams.put("email_id", email);
        jsonParams.put("prof_pic", profile_image);

        mRequest = new ServiceRequest(LogInPage.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("--------------Login reponse-------------------" + response);

                String Sstatus = "", Smessage = "", Suser_image = "", Suser_id = "", Suser_name = "",
                        Semail = "", Scountry_code = "", SphoneNo = "", Sreferal_code = "", Scategory = "", SsecretKey = "", SwalletAmount = "", ScurrencyCode = "";

                String gcmId = "";
                String is_alive_other = "";
                String locationname = "", location_id = "";
                try {

                    JSONObject object = new JSONObject(response);
                    Sstatus = object.getString("status");
                    Smessage = object.getString("message");
                    System.out.println("---------Sstatus--------" + Sstatus);
                    if (Sstatus.equalsIgnoreCase("1")) {
                        Suser_image = object.getString("prof_pic");
                        Suser_id = object.getString("user_id");
                        Suser_name = object.getString("user_name");
                        Semail = object.getString("email");
                        Scountry_code = object.getString("country_code");
                        SphoneNo = object.getString("phone_number");
                        Sreferal_code = object.getString("referal_code");
                        Scategory = object.getString("category");
                        SsecretKey = object.getString("soc_key");
                        SwalletAmount = object.getString("wallet_amount");
                        locationname = object.getString("location_name");
                        location_id = object.getString("location_id");
                        // gcmId = object.getString("key");
                        //  is_alive_other = object.getString("is_alive_other");

                        ScurrencyCode = object.getString("currency");
                        sCurrencySymbol = CurrencySymbolConverter.getCurrencySymbol(ScurrencyCode);

                        sessionManager.setSocketTaskId(Suser_id);


                        sessionManager.createLoginSession(Suser_id, Suser_name, Semail, Suser_image, Scountry_code, SphoneNo, Scategory, Sreferal_code);

                        sessionManager.createWalletSession(SwalletAmount, ScurrencyCode);
                        sessionManager.setXmppKey(Suser_id, SsecretKey);
                        SocketHandler.getInstance(LogInPage.this).getSocketManager().connect();
                        sessionManager.createLocationSession(location_id, locationname);
                        Appinfo(LogInPage.this, Iconstant.App_Info);

                        NavigationDrawer.navigationDrawerClass.finish();
                        Intent intent = new Intent(LogInPage.this, NavigationDrawer.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.enter, R.anim.exit);

                    }
                    if (Sstatus.equalsIgnoreCase("0")) {

                        Intent intent = new Intent(LogInPage.this, FBRegisterpage.class);
                        intent.putExtra("userId", userid);
                        intent.putExtra("userName", username1);
                        intent.putExtra("userEmail", email);
                        intent.putExtra("media", sMediaId);
                        intent.putExtra("userimage", profile_image);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//                    progress.dismiss();


                    }

                    // progress.dismiss();


                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                //progress.dismiss();


            }

            @Override
            public void onErrorListener() {
//            if (progress != null) {
//                progress.dismiss();
//            }
            }
        });
    }


//-----------------------------------------------------------------FaceBook Login Url----------------------------------------------------------------


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


    //----------------------------------------------------------------------App Info Url------------------------------------------------------
    private void Appinfo(Context mContext, String url) {


        ServiceRequest mservicerequest = new ServiceRequest(mContext);

        mservicerequest.makeServiceRequest(url, Request.Method.POST, null, new ServiceRequest.ServiceListener() {

            @Override
            public void onCompleteListener(String response) {
                Log.e("loginresponse", response);

                System.out.println("Appinfores---------" + response);
                String Str_status = "", Str_response = "", Str_providerid = "", Str_socky_id = "", Str_provider_name = "", Str_provider_email = "", Str_provider_img = "", Str_key = "";

                String email = "", admincomsn = "", miniamt = "", servicetax = "";

                try {
                    JSONObject jobject = new JSONObject(response);
                    Str_status = jobject.getString("status");

                    if (Str_status.equalsIgnoreCase("1")) {

                        email = jobject.getString("email_address");

                        admincomsn = jobject.getString("admin_commission");
                        miniamt = jobject.getString("minimum_amount");
                        servicetax = jobject.getString("service_tax");
                        sessionManager.Setemailappinfo(email);

                        System.out.println("provider-----------" + Str_providerid);
                        System.out.println("provider_name-----------" + Str_provider_name);
                        System.out.println("email-----------" + Str_provider_email);
                        System.out.println("providerimg-----------" + Str_provider_img);
                        System.out.println("key-----------" + Str_key);
                    } else {


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


                if (Str_status.equalsIgnoreCase("1")) {

                    System.out.println("Emailllll----" + email);

                }


            }

            @Override
            public void onErrorListener() {

            }
        });


    }

    //-----------------------------------------------------------------App Info Url------------------------------------------------------------


}
