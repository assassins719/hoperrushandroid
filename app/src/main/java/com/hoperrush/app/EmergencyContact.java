package com.hoperrush.app;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
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
import com.hoperrush.core.gps.GPSTracker;
import com.hoperrush.core.volley.ServiceRequest;
import com.hoperrush.hockeyapp.FragmentActivityHockeyApp;
import com.hoperrush.iconstant.Iconstant;
import com.hoperrush.utils.ConnectionDetector;
import com.hoperrush.utils.CountryDialCode;
import com.hoperrush.utils.SessionManager;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Casperon Technology on 1/4/2016.
 */

public class EmergencyContact extends FragmentActivityHockeyApp {

    private ConnectionDetector cd;
    private boolean isInternetPresent = false;
    private SessionManager sessionManager;

    private RelativeLayout Rl_back;
    private ImageView Im_backIcon;
    private TextView Tv_headerTitle;
    private GPSTracker gpsTracker;
    private MaterialEditText Et_name, Et_code, Et_phoneNo, Et_emailId;
    private RelativeLayout Rl_save;
    private RelativeLayout Rl_deleteContact;
    private ServiceRequest mRequest;
    LoadingDialog mLoadingDialog;
    private String UserID = "";
    RelativeLayout contactbutton;
    CountryPicker picker;
    String lat=SplashScreen.latitude;
    String longi=SplashScreen.longitude;
    Button contact;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.emergency_contact);
        initializeHeaderBar();
        initialize();

        Rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(Rl_back.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                onBackPressed();
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        Rl_deleteContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cd = new ConnectionDetector(EmergencyContact.this);
                isInternetPresent = cd.isConnectingToInternet();
                if (isInternetPresent) {
                    deleteContact_Request(EmergencyContact.this, Iconstant.emergencyContact_delete_url);
                } else {
                    alert(getResources().getString(R.string.action_no_internet_title), getResources().getString(R.string.action_no_internet_message));
                }
            }
        });

        Rl_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Et_name.getText().toString().length() == 0) {
                    alert(getResources().getString(R.string.action_sorry), getResources().getString(R.string.emergencyContact_label_name_validate_textView));
                } else if (Et_code.getText().toString().length() == 0) {
                    alert(getResources().getString(R.string.action_sorry), getResources().getString(R.string.emergencyContact_label_code_validate_textView));
                } else if (!isValidPhoneNumber(Et_phoneNo.getText().toString())) {
                    alert(getResources().getString(R.string.action_sorry), getResources().getString(R.string.emergencyContact_label_mobile_novalidate_textView));
                } else if (!isValidEmail(Et_emailId.getText().toString())) {
                    alert(getResources().getString(R.string.action_sorry), getResources().getString(R.string.emergencyContact_label_email_validate_textView));
                } else {
                    cd = new ConnectionDetector(EmergencyContact.this);
                    isInternetPresent = cd.isConnectingToInternet();
                    if (isInternetPresent) {
                        updateContact_Request(EmergencyContact.this, Iconstant.emergencyContact_add_url);
                    } else {
                        alert(getResources().getString(R.string.action_no_internet_title), getResources().getString(R.string.action_no_internet_message));
                    }
                }
            }
        });


        Et_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // close keyboard
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(Et_code.getWindowToken(), 0);

                picker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
            }
        });

        Et_code.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    picker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
                }
            }
        });


        picker.setListener(new CountryPickerListener() {
            @Override
            public void onSelectCountry(String name, String code, String dialCode) {
                picker.dismiss();
                Et_code.setText(dialCode);

                //Move cursor from one EditText to Another
                Selection.setSelection((Editable) Et_phoneNo.getText(), Et_code.getSelectionStart());
                Et_phoneNo.requestFocus();

                // close keyboard
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(Et_code.getWindowToken(), 0);
                // close keyboard
                InputMethodManager mgr_username = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr_username.hideSoftInputFromWindow(Et_emailId.getWindowToken(), 0);

            }
        });

        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickcontact(EmergencyContact.this, Iconstant.Emergeny_contact);
            }
        });
    }

    private void initializeHeaderBar() {
        RelativeLayout headerBar = (RelativeLayout) findViewById(R.id.headerBar_layout);
        Rl_back = (RelativeLayout) headerBar.findViewById(R.id.headerBar_left_layout);
        Im_backIcon = (ImageView) headerBar.findViewById(R.id.headerBar_imageView);
        Tv_headerTitle = (TextView) headerBar.findViewById(R.id.headerBar_title_textView);

        Tv_headerTitle.setText(getResources().getString(R.string.emergencyContact_label_header_textView));
        Im_backIcon.setImageResource(R.drawable.back_arrow);
    }

    private void initialize() {
        cd = new ConnectionDetector(EmergencyContact.this);
        isInternetPresent = cd.isConnectingToInternet();
        sessionManager = new SessionManager(EmergencyContact.this);
        picker = CountryPicker.newInstance("Select Country");
        contactbutton=(RelativeLayout)findViewById(R.id.contactbutton);
        Et_name = (MaterialEditText) findViewById(R.id.emergency_contact_name_editText);
        Et_code = (MaterialEditText) findViewById(R.id.emergency_contact_country_code_editText);
        Et_phoneNo = (MaterialEditText) findViewById(R.id.emergency_contact_mobile_editText);
        Et_emailId = (MaterialEditText) findViewById(R.id.emergency_contact_email_editText);
        Rl_save = (RelativeLayout) findViewById(R.id.emergency_contact_save_layout);
        Rl_deleteContact = (RelativeLayout) findViewById(R.id.emergency_contact_delete_contact_layout);
        contact=(Button)findViewById(R.id.contact);
        // get user data from session
        HashMap<String, String> user = sessionManager.getUserDetails();
        UserID = user.get(SessionManager.KEY_USER_ID);


        gpsTracker = new GPSTracker(EmergencyContact.this);
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
                    Et_code.setText(Str_countyCode);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (isInternetPresent) {
            displayContact_Request(EmergencyContact.this, Iconstant.emergencyContact_view_url);
        } else {
            alert(getResources().getString(R.string.action_no_internet_title), getResources().getString(R.string.action_no_internet_message));
        }
    }


    //--------------Close KeyBoard Method-----------
    private void CloseKeyboard(EditText edittext) {
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(edittext.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    //--------------Alert Method-----------
    private void alert(String title, String alert) {

        final PkDialog mDialog = new PkDialog(EmergencyContact.this);
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

    // validating Phone Number
    public static final boolean isValidPhoneNumber(CharSequence target) {
        if (target == null || TextUtils.isEmpty(target) || target.length() <= 6) {
            return false;
        } else {
            return android.util.Patterns.PHONE.matcher(target).matches();
        }
    }

    //---------code to Check Email Validation------
    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }


    //-----------------------Display Emergency Contact Post Request-----------------
    private void displayContact_Request(Context mContext, String Url) {

        mLoadingDialog = new LoadingDialog(mContext);
        mLoadingDialog.setLoadingTitle(getResources().getString(R.string.action_loading));
        mLoadingDialog.show();

        System.out.println("-------------displayContact_Request Url----------------" + Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", UserID);

        mRequest = new ServiceRequest(EmergencyContact.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("-------------displayContact_Request Response----------------" + response);

                String sStatus = "", sMessage = "", sName = "", sMobileNumber = "", sEmail = "", sCountry_code = "";
                try {

                    JSONObject object = new JSONObject(response);
                    sStatus = object.getString("status");
                    sMessage = object.getString("response");

                    Object check_emergency_contact_object = object.get("emergency_contact");
                    if (check_emergency_contact_object instanceof JSONObject) {
                        JSONObject jobject = object.getJSONObject("emergency_contact");
                        sName = jobject.getString("name");
                        sMobileNumber = jobject.getString("mobile");
                        sEmail = jobject.getString("email");
                        sCountry_code = jobject.getString("code");
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if (sStatus.equalsIgnoreCase("1")) {
                    Et_name.setText(sName);
                    Et_emailId.setText(sEmail);
                    Et_code.setText(sCountry_code);
                    Et_phoneNo.setText(sMobileNumber);

                    if (sName.length() == 0) {
                        Rl_deleteContact.setVisibility(View.INVISIBLE);
                        contactbutton.setVisibility(View.GONE);
                    } else {
                        Rl_deleteContact.setVisibility(View.VISIBLE);
                        contactbutton.setVisibility(View.VISIBLE);
                    }
                } else {
                    Rl_deleteContact.setVisibility(View.INVISIBLE);
                    contactbutton.setVisibility(View.GONE);
                }

                mLoadingDialog.dismiss();
            }

            @Override
            public void onErrorListener() {
                mLoadingDialog.dismiss();
            }
        });
    }


    //-----------------------Update Emergency Contact Post Request-----------------
    private void updateContact_Request(Context mContext, String Url) {

        mLoadingDialog = new LoadingDialog(mContext);
        mLoadingDialog.setLoadingTitle(getResources().getString(R.string.action_updating));
        mLoadingDialog.show();

        System.out.println("-------------updateContact Url----------------" + Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", UserID);
        jsonParams.put("em_name", Et_name.getText().toString());
        jsonParams.put("em_email", Et_emailId.getText().toString());
        jsonParams.put("em_mobile_code", Et_code.getText().toString());
        jsonParams.put("em_mobile", Et_phoneNo.getText().toString());

        mRequest = new ServiceRequest(EmergencyContact.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("-------------updateContact Response----------------" + response);
                String sStatus = "", sMessage = "";
                try {

                    JSONObject object = new JSONObject(response);
                    sStatus = object.getString("status");
                    sMessage = object.getString("response");

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if (sStatus.equalsIgnoreCase("1")) {
                    Rl_deleteContact.setVisibility(View.VISIBLE);
                    contactbutton.setVisibility(View.VISIBLE);
                    alert(getResources().getString(R.string.action_success), getResources().getString(R.string.emergencyContact_label_saved_emergencyContacts));
                } else {
                    alert(getResources().getString(R.string.action_sorry), sMessage);
                }

                mLoadingDialog.dismiss();
            }

            @Override
            public void onErrorListener() {
                mLoadingDialog.dismiss();
            }
        });
    }


    //-----------------------Delete Emergency Contact Post Request-----------------
    private void deleteContact_Request(Context mContext, String Url) {

        mLoadingDialog = new LoadingDialog(mContext);
        mLoadingDialog.setLoadingTitle(getResources().getString(R.string.action_deleting));
        mLoadingDialog.show();

        System.out.println("-------------deleteContact Url----------------" + Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", UserID);

        mRequest = new ServiceRequest(EmergencyContact.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("-------------deleteContact Response----------------" + response);
                String sStatus = "", sMessage = "";
                try {

                    JSONObject object = new JSONObject(response);
                    sStatus = object.getString("status");
                    sMessage = object.getString("response");

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if (sStatus.equalsIgnoreCase("1")) {
                    Et_name.setText("");
                    Et_emailId.setText("");
                    Et_code.setText("");
                    Et_phoneNo.setText("");
                    Rl_deleteContact.setVisibility(View.INVISIBLE);
                    contactbutton.setVisibility(View.GONE);

                    alert(getResources().getString(R.string.action_success), getResources().getString(R.string.emergencyContact_label_delete_success_textView));
                } else {
                    alert(getResources().getString(R.string.action_sorry), sMessage);
                }

                mLoadingDialog.dismiss();
            }

            @Override
            public void onErrorListener() {
                mLoadingDialog.dismiss();
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    //-----------------Move Back on pressed phone back button-------------
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)) {

            InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            in.hideSoftInputFromWindow(Rl_back.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

            onBackPressed();
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            return true;
        }
        return false;
    }


    //-------------------------------------------------------------------------------mail send Emergency------------------------------------------
    private void clickcontact(Context mContext, String Url) {

        mLoadingDialog = new LoadingDialog(mContext);
        mLoadingDialog.setLoadingTitle("Sending..");
        mLoadingDialog.show();

        System.out.println("-------------deleteContact Url----------------" + Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", UserID);
        jsonParams.put("latitude",lat);
        jsonParams.put("longitude",longi);

        mRequest = new ServiceRequest(EmergencyContact.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("-------------deleteContact Response----------------" + response);
                String sStatus = "", sMessage = "";
                try {

                    JSONObject object = new JSONObject(response);
                    sStatus = object.getString("status");
                    sMessage = object.getString("response");

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if (sStatus.equalsIgnoreCase("1")) {

                    alert(getResources().getString(R.string.action_success),sMessage);
                } else {
                    alert(getResources().getString(R.string.action_sorry), sMessage);
                }

                mLoadingDialog.dismiss();
            }

            @Override
            public void onErrorListener() {
                mLoadingDialog.dismiss();
            }
        });
    }

}
