package com.hoperrush.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.hoperrush.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Casperon Technology on 1/7/2016.
 */
public class AddAddressPage extends FragmentActivityHockeyApp {
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private SessionManager sessionManager;
    String address1 = "";
    private RelativeLayout Rl_back;
    private ImageView Im_backIcon;
    private TextView Tv_headerTitle;
    String city = "";
    String state = "";
    String country = "";
    String postalCode = "";
    private EditText Et_name, Et_countryCode, Et_mobile, Et_email;
    private EditText Et_houseNo, Et_landmark, Et_city, Et_zipCode,Et_state,Et_country;
    private Button Bt_done;
    private AutoCompleteTextView autoCompleteTextView_locality;
    private String sUserID = "", sUserName = "", sCountryCode = "", sMobileNo = "", sEmail = "";
    CountryPicker picker;
    GPSTracker gps;
    private ServiceRequest mRequest;
    ArrayList<String> itemList_location = new ArrayList<String>();
    ArrayList<String> itemList_placeId = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    String SselectedLocation="";
    private boolean isDataAvailable = false;
    private boolean isAddressAvailable = false;
    private String sLatitude = "", sLongitude = "", sSelected_location = "";
    private LoadingDialog mLoadingDialog;

    private RelativeLayout Rl_layput_address;

    private EditText TvAdd_address;
    private int search_status = 0;
    private int placeSearch_request_code = 200;

    private String SselectedLatitude = "";
    private String SselectedLongitude = "";
    ImageButton addlocation;
    ImageButton clearbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_address);
        initializeHeaderBar();
        initialize();

        Rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // close keyboard
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(Rl_back.getWindowToken(), 0);

                onBackPressed();
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        Bt_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Et_name.getText().toString().length() == 0) {
                    alert(getResources().getString(R.string.action_sorry), getResources().getString(R.string.register_label_alert_username));
                } else if (!isValidPhoneNumber(Et_mobile.getText().toString())) {
                    alert(getResources().getString(R.string.action_sorry), getResources().getString(R.string.register_label_alert_phoneNo));
                } else if (Et_countryCode.getText().toString().length() == 0) {
                    alert(getResources().getString(R.string.action_sorry), getResources().getString(R.string.register_label_alert_country_code));
                } else if (!isValidEmail(Et_email.getText().toString())) {
                    alert(getResources().getString(R.string.action_sorry), getResources().getString(R.string.register_label_alert_email));
                } else if (TvAdd_address.getText().toString().length() == 0) {
                    alert(getResources().getString(R.string.action_sorry), getResources().getString(R.string.add_address_label_enter_locality_alert));
                } else if (Et_houseNo.getText().toString().length() == 0) {
                    alert(getResources().getString(R.string.action_sorry), getResources().getString(R.string.add_address_label_enter_houseNo_alert));
                } else if (Et_city.getText().toString().length() == 0) {
                    alert(getResources().getString(R.string.action_sorry), getResources().getString(R.string.add_address_label_enter_city_alert));
                } else if (Et_zipCode.getText().toString().length() == 0) {
                    alert(getResources().getString(R.string.action_sorry), getResources().getString(R.string.add_address_label_enter_zip_alert));
                } else {

                    cd = new ConnectionDetector(AddAddressPage.this);
                    isInternetPresent = cd.isConnectingToInternet();

                    if (isInternetPresent) {
                        submitAddressRequest(AddAddressPage.this, Iconstant.add_address_url);
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

                //Temporialy hide

            //    picker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
            }
        });

        Et_countryCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // close keyboard
                    InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.hideSoftInputFromWindow(Et_email.getWindowToken(), 0);

                    //  picker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
                }
            }
        });


        picker.setListener(new CountryPickerListener() {
            @Override
            public void onSelectCountry(String name, String code, String dialCode) {
                picker.dismiss();
                Et_countryCode.setText(dialCode);

                //Move cursor from one EditText to Another
                Selection.setSelection((Editable) Et_mobile.getText(), Et_countryCode.getSelectionStart());
                Et_mobile.requestFocus();

                // close keyboard
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(Et_countryCode.getWindowToken(), 0);
                // close keyboard
                InputMethodManager mgr_username = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr_username.hideSoftInputFromWindow(Et_name.getWindowToken(), 0);

            }
        });


       /* autoCompleteTextView_locality.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get data associated with the specified position
                // in the list (AdapterView)

                autoCompleteTextView_locality.dismissDropDown();
                sSelected_location = itemList_location.get(position);

                cd = new ConnectionDetector(AddAddressPage.this);
                isInternetPresent = cd.isConnectingToInternet();
                if (isInternetPresent) {
                    LatLongRequest(Iconstant.GetAddressFrom_LatLong_url + itemList_placeId.get(position));

                    System.out.println("LatLongRequest------------"+Iconstant.GetAddressFrom_LatLong_url + itemList_placeId.get(position));

                } else {
                    alert(getResources().getString(R.string.action_no_internet_title), getResources().getString(R.string.action_no_internet_message));
                }
            }
        });*/

        Rl_layput_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                search_status = 0;
                Intent intent = new Intent(AddAddressPage.this, LocationSearch.class);
                startActivityForResult(intent, placeSearch_request_code);
                AddAddressPage.this.overridePendingTransition(R.anim.slideup, R.anim.slidedown);
            }
        });

        addlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isInternetPresent) {
                    if (gps.isgpsenabled() && gps.canGetLocation()) {
                        double lat = gps.getLatitude();
                        double longi = gps.getLongitude();
                        SselectedLatitude = String.valueOf(lat);
                        SselectedLongitude = String.valueOf(longi);

                        SselectedLocation = getAddress(lat, longi);

                        TvAdd_address.setText(SselectedLocation);
                        Et_houseNo.setText(address1);
                        Et_city.setText(city);
                        Et_zipCode.setText(postalCode);
                        Et_state.setText(state);
                        Et_country.setText(country);

                    }
                }
            }
        });

        clearbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TvAdd_address.setText("");
                Et_houseNo.setText("");
                Et_city.setText("");
                Et_zipCode.setText("");
            }
        });

    }

    private void initializeHeaderBar() {
        RelativeLayout headerBar = (RelativeLayout) findViewById(R.id.headerBar_layout);
        Rl_back = (RelativeLayout) headerBar.findViewById(R.id.headerBar_left_layout);
        Im_backIcon = (ImageView) headerBar.findViewById(R.id.headerBar_imageView);
        Tv_headerTitle = (TextView) headerBar.findViewById(R.id.headerBar_title_textView);

        Tv_headerTitle.setText(getResources().getString(R.string.add_address_label_header_textView));
        Im_backIcon.setImageResource(R.drawable.back_arrow);
    }

    private void initialize() {
        gps = new GPSTracker(getApplicationContext());
        cd = new ConnectionDetector(AddAddressPage.this);
        isInternetPresent = cd.isConnectingToInternet();
        sessionManager = new SessionManager(AddAddressPage.this);
        picker = CountryPicker.newInstance("Select Country");
        addlocation = (ImageButton) findViewById(R.id.add_address_BTN_currentaddress);
        clearbutton = (ImageButton) findViewById(R.id.add_address_BTN_clear);

        Et_name = (EditText) findViewById(R.id.add_address_name_editText);
        Et_countryCode = (EditText) findViewById(R.id.add_address_country_code_editText);
        Et_mobile = (EditText) findViewById(R.id.add_address_mobile_editText);
        Et_email = (EditText) findViewById(R.id.add_address_email_editText);
        // autoCompleteTextView_locality = (AutoCompleteTextView) findViewById(R.id.add_address_locality_autoCompleteTextView);
        Et_houseNo = (EditText) findViewById(R.id.add_address_houseNo_editText);
        Et_landmark = (EditText) findViewById(R.id.add_address_landmark_editText);
        Et_city = (EditText) findViewById(R.id.add_address_city_editText);
        Et_zipCode = (EditText) findViewById(R.id.add_address_zipCode_editText);
        Et_state=(EditText)findViewById(R.id.add_address_state_editText);
        Et_country=(EditText)findViewById(R.id.add_address_country_editText);
        Bt_done = (Button) findViewById(R.id.add_address_page_done_button);

        Rl_layput_address = (RelativeLayout) findViewById(R.id.book_address_layout);
        TvAdd_address = (EditText) findViewById(R.id.book_navigation_search_address);
        // TvAdd_address.setText("Enter Location");
        // autoCompleteTextView_locality.setThreshold(0);

        // get user data from session
        HashMap<String, String> user = sessionManager.getUserDetails();
        sUserID = user.get(SessionManager.KEY_USER_ID);
        sUserName = user.get(SessionManager.KEY_USERNAME);
        sCountryCode = user.get(SessionManager.KEY_COUNTRY_CODE);
        sMobileNo = user.get(SessionManager.KEY_PHONE_NUMBER);
        sEmail = user.get(SessionManager.KEY_EMAIL);

        Et_name.setText(sUserName);
        Et_countryCode.setText(sCountryCode);
        Et_mobile.setText(sMobileNo);
        Et_email.setText(sEmail);


    }

    //--------------Alert Method-----------
    private void alert(String title, String alert) {

        final PkDialog mDialog = new PkDialog(AddAddressPage.this);
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

    // --------Code to Check Email Validation--------
    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    // --------validating Phone Number--------
    public static final boolean isValidPhoneNumber(CharSequence target) {
        if (target == null || TextUtils.isEmpty(target) || target.length() <= 6) {
            return false;
        } else {
            return android.util.Patterns.PHONE.matcher(target).matches();
        }
    }

    private void CloseKeyboard(EditText edittext) {
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(edittext.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    //-------------------Search Place Request----------------
    private void CitySearchRequest(String Url) {

        System.out.println("--------------Search city url-------------------" + Url);

        mRequest = new ServiceRequest(AddAddressPage.this);
        mRequest.makeServiceRequest(Url, Request.Method.GET, null, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("--------------Search city  reponse-------------------" + response);
                String status = "";
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.length() > 0) {

                        status = object.getString("status");
                        JSONArray place_array = object.getJSONArray("predictions");
                        if (status.equalsIgnoreCase("OK")) {
                            if (place_array.length() > 0) {
                                itemList_location.clear();
                                itemList_placeId.clear();
                                for (int i = 0; i < place_array.length(); i++) {
                                    JSONObject place_object = place_array.getJSONObject(i);
                                    itemList_location.add(place_object.getString("description"));
                                    itemList_placeId.add(place_object.getString("place_id"));
                                }
                                isDataAvailable = true;
                            } else {
                                itemList_location.clear();
                                itemList_placeId.clear();
                                isDataAvailable = false;
                            }
                        } else {
                            itemList_location.clear();
                            itemList_placeId.clear();
                            isDataAvailable = false;
                        }
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if (isDataAvailable) {
                    adapter = new ArrayAdapter<String>
                            (AddAddressPage.this, R.layout.auto_complete_list_item, R.id.autoComplete_textView, itemList_location);
                    autoCompleteTextView_locality.setAdapter(adapter);
                }
            }

            @Override
            public void onErrorListener() {
            }
        });
    }


    //-------------------Get Latitude and Longitude from Address(Place ID) Request----------------
    private void LatLongRequest(String Url) {

        mLoadingDialog = new LoadingDialog(AddAddressPage.this);
        mLoadingDialog.setLoadingTitle(getResources().getString(R.string.action_processing));
        mLoadingDialog.show();

        System.out.println("--------------LatLong url-------------------" + Url);

        mRequest = new ServiceRequest(AddAddressPage.this);
        mRequest.makeServiceRequest(Url, Request.Method.GET, null, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("--------------LatLong  reponse-------------------" + response);
                String status = "", sArea = "", sLocality = "", sCity_Admin1 = "", sCity_Admin2 = "", sPostalCode = "";
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.length() > 0) {

                        status = object.getString("status");
                        JSONObject place_object = object.getJSONObject("result");
                        if (status.equalsIgnoreCase("OK")) {
                            if (place_object.length() > 0) {

                                sArea = place_object.getString("name");
                                JSONArray addressArray = place_object.getJSONArray("address_components");
                                if (addressArray.length() > 0) {
                                    for (int i = 0; i < addressArray.length(); i++) {
                                        JSONObject address_object = addressArray.getJSONObject(i);

                                        JSONArray typesArray = address_object.getJSONArray("types");
                                        if (typesArray.length() > 0) {
                                            for (int j = 0; j < typesArray.length(); j++) {

                                                if (typesArray.get(j).toString().equalsIgnoreCase("locality")) {
                                                    sLocality = address_object.getString("long_name");
                                                } else if (typesArray.get(j).toString().equalsIgnoreCase("administrative_area_level_2")) {
                                                    sCity_Admin2 = address_object.getString("long_name") + ",";
                                                } else if (typesArray.get(j).toString().equalsIgnoreCase("administrative_area_level_1")) {
                                                    sCity_Admin1 = address_object.getString("long_name");
                                                } else if (typesArray.get(j).toString().equalsIgnoreCase("postal_code")) {
                                                    sPostalCode = address_object.getString("long_name");
                                                }
                                            }

                                            isAddressAvailable = true;
                                        } else {
                                            isAddressAvailable = false;
                                        }
                                    }
                                } else {
                                    isAddressAvailable = false;
                                }

                                JSONObject geometry_object = place_object.getJSONObject("geometry");
                                if (geometry_object.length() > 0) {
                                    JSONObject location_object = geometry_object.getJSONObject("location");
                                    if (location_object.length() > 0) {
                                        sLatitude = location_object.getString("lat");
                                        sLongitude = location_object.getString("lng");
                                        isDataAvailable = true;
                                    } else {
                                        isDataAvailable = false;
                                    }
                                } else {
                                    isDataAvailable = false;
                                }
                            } else {
                                isDataAvailable = false;
                            }
                        } else {
                            isDataAvailable = false;
                        }
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if (isDataAvailable) {
                    System.out.println("-------------sLatitude---------------" + sLatitude);
                    System.out.println("-------------sLongitude---------------" + sLongitude);
                    System.out.println("-------------sSelected_location---------------" + sSelected_location);

                    if (isAddressAvailable) {
                        autoCompleteTextView_locality.setText(sLocality);
                        Et_houseNo.setText(sArea);
                        Et_city.setText(sCity_Admin2 + sCity_Admin1);
                        Et_zipCode.setText(sPostalCode);
                        autoCompleteTextView_locality.dismissDropDown();
                    } else {
                        autoCompleteTextView_locality.setText(sSelected_location);
                        autoCompleteTextView_locality.dismissDropDown();
                    }

                    mLoadingDialog.dismiss();
                } else {
                    mLoadingDialog.dismiss();
                    alert(getResources().getString(R.string.action_sorry), status);
                }
            }

            @Override
            public void onErrorListener() {
                mLoadingDialog.dismiss();
            }
        });
    }


    //-----------------------Submit Address Post Request-----------------
    private void submitAddressRequest(Context mContext, String Url) {

        mLoadingDialog = new LoadingDialog(mContext);
        mLoadingDialog.setLoadingTitle(getResources().getString(R.string.add_address_label_add_address_dialog));
        mLoadingDialog.show();

        System.out.println("-------------submitAddressRequest Url----------------" + Url);

        Et_zipCode = (EditText) findViewById(R.id.add_address_zipCode_editText);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", sUserID);
        jsonParams.put("name", Et_name.getText().toString());
        jsonParams.put("email", Et_email.getText().toString());
        jsonParams.put("country_code", Et_countryCode.getText().toString());
        jsonParams.put("mobile", Et_mobile.getText().toString());
        jsonParams.put("locality", SselectedLocation);
        jsonParams.put("street", SselectedLocation);
        jsonParams.put("landmark", Et_landmark.getText().toString());
        jsonParams.put("city", Et_city.getText().toString());
        jsonParams.put("zipcode", Et_zipCode.getText().toString());
        jsonParams.put("lat", SselectedLatitude);
        jsonParams.put("lng", SselectedLongitude);
        jsonParams.put("state", state);
        jsonParams.put("country",Et_country.getText().toString());

        System.out.println("" + SselectedLatitude);

        System.out.println("user_id-----------" + sUserID);
        System.out.println("name-----------" + Et_name.getText().toString());
        System.out.println("email-----------" + Et_email.getText().toString());
        System.out.println("country_code-----------" + Et_countryCode.getText().toString());
        System.out.println("mobile-----------" + TvAdd_address.getText().toString());
        System.out.println("locality-----------" + sUserID);

        System.out.println("street-----------" + Et_houseNo.getText().toString());
        System.out.println("landmark-----------" + Et_landmark.getText().toString());
        System.out.println("city-----------" + Et_city.getText().toString());

        System.out.println("zipcode-----------" + Et_city.getText().toString());
        System.out.println("city-----------" + Et_zipCode.getText().toString());

        System.out.println("lat-----------" + sLatitude);
        System.out.println("lng-----------" + sLongitude);


        System.out.println("state-----------" + "Tn");
        System.out.println("line1-----------" + "chennai");


        mRequest = new ServiceRequest(mContext);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("-------------submitAddressRequest Response----------------" + response);

                String sStatus = "", sResponse = "";
                try {

                    JSONObject object = new JSONObject(response);
                    sStatus = object.getString("status");
                    sResponse = object.getString("response");

                    if (sStatus.equalsIgnoreCase("1")) {

                        Intent broadcastIntent = new Intent();
                        broadcastIntent.setAction("com.package.ACTION_CLASS_APPOINTMENT_REFRESH");
                        sendBroadcast(broadcastIntent);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mLoadingDialog.dismiss();
                                onBackPressed();
                                finish();
                            }
                        }, 2000);

                    } else {
                        mLoadingDialog.dismiss();
                        alert(getResources().getString(R.string.action_sorry), sResponse);
                    }
                } catch (JSONException e) {
                    mLoadingDialog.dismiss();
                    e.printStackTrace();
                }
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("--------------onActivityResult requestCode----------------" + requestCode);


        if ((requestCode == placeSearch_request_code && resultCode == Activity.RESULT_OK && data != null)) {
            if (search_status == 0) {

                SselectedLatitude = data.getStringExtra("Selected_Latitude");
                SselectedLongitude = data.getStringExtra("Selected_Longitude");

               SselectedLocation = data.getStringExtra("Selected_Location");

                String ShouseNo = data.getStringExtra("HouseNo");
                String Scity = data.getStringExtra("City");
                String SpostalCode = data.getStringExtra("ZipCode");
                String Slocation = data.getStringExtra("Location");

                System.out.println("SselectedLatitude---------" + SselectedLatitude);
                System.out.println("SselectedLongitude---------" + SselectedLongitude);

                System.out.println("ShouseNo-----------" + ShouseNo);
                System.out.println("Scity-----------" + Scity);
                System.out.println("SpostalCode-----------" + SpostalCode);
                System.out.println("Slocation-----------" + Slocation);

               String address=getAddress(Double.parseDouble(SselectedLatitude),Double.parseDouble(SselectedLongitude));
                TvAdd_address.setText(Slocation);
                Et_houseNo.setText(ShouseNo);
                Et_city.setText(city);
                Et_zipCode.setText(SpostalCode);
                Et_state.setText(state);
                Et_country.setText(country);


            } else {

            }

        } else {

        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    //-----------------Move Back on pressed phone back button------------------
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)) {

            // close keyboard
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(Rl_back.getWindowToken(), 0);

            onBackPressed();
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            return true;
        }
        return false;
    }

    //-----------------------------------------------------------------Current Address Get---------------------------------
    private String getAddress(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(AddAddressPage.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);

                address1 = addresses.get(0).getAddressLine(0);
                SselectedLocation=addresses.get(0).getAddressLine(0);// If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                city = addresses.get(0).getLocality();
                state = addresses.get(0).getAdminArea();
                country = addresses.get(0).getCountryName();
                postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName();
                String street = addresses.get(0).getLocality();
                String subadmin = addresses.get(0).getSubAdminArea();
                System.out.println("Geo Address" + street);
                System.out.println("SubAdmin" + subadmin);


                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
            } else {
                Log.e("Current loction address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Current loction address", "Canont get Address!");
        }
        return strAdd;
    }


}
