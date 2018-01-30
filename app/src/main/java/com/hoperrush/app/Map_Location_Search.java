package com.hoperrush.app;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.hoperrush.R;
import com.hoperrush.adapter.PlaceSearchAdapter;
import com.hoperrush.core.dialog.PkDialog;
import com.hoperrush.core.gps.GPSTracker;
import com.hoperrush.core.volley.ServiceRequest;
import com.hoperrush.hockeyapp.ActivityHockeyApp;
import com.hoperrush.iconstant.Iconstant;
import com.hoperrush.utils.ConnectionDetector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Map_Location_Search extends ActivityHockeyApp implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private RelativeLayout back;
    private EditText et_search;
    private ListView listview;
    private RelativeLayout alert_layout;
    private TextView alert_textview;
    private TextView tv_emptyText;
    private ProgressBar progresswheel;
    final static int REQUEST_LOCATION = 299;
    PendingResult<LocationSettingsResult> result;
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private GoogleMap googleMap;
    private GPSTracker gps;
    ProgressBar progressBar;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    private String sLatitude="";
    private String sLongitude="";
    public static final int ActivityDropRequestCode = 6000;
    private static final int REQUEST_CODE_RECOVER_PLAY_SERVICES = 1001;
    private ServiceRequest mRequest;
    Context context;
    ArrayList<String> itemList_location = new ArrayList<String>();
    ArrayList<String> itemList_placeId = new ArrayList<String>();
    private double MyCurrent_lat = 0.0, MyCurrent_long = 0.0;
    private PlaceSearchAdapter adapter;
    private boolean isdataAvailable = false;
    private boolean isEstimateAvailable = false;
    private RelativeLayout done_button;
    private RelativeLayout map_layout;
    private String Slatitude = "", Slongitude = "", Sselected_location = "";
    private TextView selected_your_address;
    private ImageView currentLocation_image;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map__location__search);
        context = getApplicationContext();
        initialize();
        initializeMap();


        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Sselected_location = itemList_location.get(position);

                cd = new ConnectionDetector(Map_Location_Search.this);
                isInternetPresent = cd.isConnectingToInternet();
                if (isInternetPresent) {
                    LatLongRequest(Iconstant.GetAddressFrom_LatLong_url + itemList_placeId.get(position));
                } else {
                    alert_layout.setVisibility(View.VISIBLE);
                    alert_textview.setText(getResources().getString(R.string.alert_nointernet));
                }

            }
        });

        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

               if(s.length()==0){
                   map_layout.setVisibility(View.VISIBLE);
                   listview.setVisibility(View.GONE);
               }
                else{
                   map_layout.setVisibility(View.GONE);
                   listview.setVisibility(View.VISIBLE);



                cd = new ConnectionDetector(Map_Location_Search.this);
                isInternetPresent = cd.isConnectingToInternet();

                if (isInternetPresent) {
                    if (mRequest != null) {
                        mRequest.cancelRequest();
                    }

                    String data = et_search.getText().toString().toLowerCase().replace(" ", "%20");
                    CitySearchRequest(Iconstant.place_search_url + data);
                } else {
                    alert_layout.setVisibility(View.VISIBLE);
                    alert_textview.setText(getResources().getString(R.string.alert_nointernet));
                }}

            }
        });

        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    CloseKeyboard(et_search);
                }
                return false;
            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // close keyboard
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(back.getWindowToken(), 0);

                Map_Location_Search.this.finish();
                Map_Location_Search.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        et_search.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager keyboard = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                keyboard.showSoftInput(et_search, 0);
            }
        }, 200);

        done_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent returnIntent = new Intent();
                returnIntent.putExtra("Selected_Latitude", sLatitude);
                returnIntent.putExtra("Selected_Longitude", sLongitude);
                returnIntent.putExtra("Selected_Location", et_search.getText().toString());
                setResult(RESULT_OK, returnIntent);
                onBackPressed();
                finish();
            }
        });

        currentLocation_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // isCameraChangeListener = true;

                cd = new ConnectionDetector(Map_Location_Search.this);
                isInternetPresent = cd.isConnectingToInternet();
                gps = new GPSTracker(Map_Location_Search.this);

                if (gps.canGetLocation() && gps.isgpsenabled()) {

                    MyCurrent_lat = gps.getLatitude();
                    MyCurrent_long = gps.getLongitude();
                    String address = getCompleteAddressString(MyCurrent_lat, MyCurrent_long);
                    selected_your_address.setText(address);
//                    if (mRequest != null) {
//                        mRequest.cancelRequest();
//                    }
                    // Move the camera to last position with a zoom level
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(MyCurrent_lat, MyCurrent_long)).zoom(17).build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                } else {
                    enableGpsService();
                    //Toast.makeText(getActivity(), "GPS not Enabled !!!", Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    private void initialize() {
        gps=new GPSTracker(Map_Location_Search.this);
        alert_layout = (RelativeLayout) findViewById(R.id.location_search_alert_layout);
        alert_textview = (TextView) findViewById(R.id.location_search_alert_textView);
        back = (RelativeLayout) findViewById(R.id.location_search_back_layout);
        et_search = (EditText) findViewById(R.id.location_search_editText);
        listview = (ListView) findViewById(R.id.location_search_listView);
        progresswheel = (ProgressBar) findViewById(R.id.location_search_progressBar);
        tv_emptyText = (TextView) findViewById(R.id.location_search_empty_textview);
        done_button=(RelativeLayout)findViewById(R.id.done);
        selected_your_address=(TextView)findViewById(R.id.map_address_text);
        currentLocation_image = (ImageView)findViewById(R.id.book_current_location_imageview);
        map_layout=(RelativeLayout)findViewById(R.id.map_layout);
        map_layout.setVisibility(View.VISIBLE);
        listview.setVisibility(View.GONE);

    }

    private void initializeMap() {
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.drop_location_select_view_map)).getMap();
            if (googleMap == null) {
                Toast.makeText(Map_Location_Search.this, "Sorry! unable to create maps", Toast.LENGTH_SHORT).show();
            }
        }
        // Changing map type
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // Showing / hiding your current location
        googleMap.setMyLocationEnabled(false);
        // Enable / Disable zooming controls
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        // Enable / Disable my location button
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        // Enable / Disable Compass icon
        googleMap.getUiSettings().setCompassEnabled(false);
        // Enable / Disable Rotate gesture
        googleMap.getUiSettings().setRotateGesturesEnabled(true);
        // Enable / Disable zooming functionality
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.setMyLocationEnabled(false);
        if (gps.canGetLocation() && gps.isgpsenabled()) {

            double Dlatitude = gps.getLatitude();
            double Dlongitude = gps.getLongitude();

            // Move the camera to last position with a zoom level
            CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(Dlatitude, Dlongitude)).zoom(17).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        } else {
            Alert(getResources().getString(R.string.alert_sorry_label_title), getResources().getString(R.string.alert_gpsEnable));
        }


        if (CheckPlayService()) {
            googleMap.setOnCameraChangeListener(mOnCameraChangeListener);
            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    String tittle = marker.getTitle();
                    return true;
                }
            });
        } else {
            Toast.makeText(Map_Location_Search.this, "Install Google Play service To View Location !!!", Toast.LENGTH_LONG).show();
        }

    }

    private void CloseKeyboard(EditText edittext) {
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(edittext.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    //--------------Alert Method-----------
    private void Alert(String title, String alert) {

        final PkDialog mDialog = new PkDialog(Map_Location_Search.this);
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

    //Enabling Gps Service
    private void enableGpsService() {

        mGoogleApiClient = new GoogleApiClient.Builder(Map_Location_Search.this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        mGoogleApiClient.connect();

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(30 * 1000);
        mLocationRequest.setFastestInterval(5 * 1000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);

        result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                //final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        //...
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(Map_Location_Search.this, REQUEST_LOCATION);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        //...
                        break;
                }
            }
        });
    }



    //-------------------Search Place Request----------------
    private void CitySearchRequest(String Url) {

        progresswheel.setVisibility(View.VISIBLE);
        System.out.println("--------------Search city url-------------------" + Url);

        mRequest = new ServiceRequest(Map_Location_Search.this);
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
                                isdataAvailable = true;
                            } else {
                                itemList_location.clear();
                                itemList_placeId.clear();
                                isdataAvailable = false;
                            }
                        } else {
                            itemList_location.clear();
                            itemList_placeId.clear();
                            isdataAvailable = false;
                        }
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                progresswheel.setVisibility(View.INVISIBLE);
                alert_layout.setVisibility(View.GONE);
                if (isdataAvailable) {
                    tv_emptyText.setVisibility(View.GONE);
                } else {
                    tv_emptyText.setVisibility(View.VISIBLE);
                }
                adapter = new PlaceSearchAdapter(Map_Location_Search.this, itemList_location);
                listview.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onErrorListener() {
                progresswheel.setVisibility(View.INVISIBLE);
                alert_layout.setVisibility(View.GONE);

                // close keyboard
                CloseKeyboard(et_search);
            }
        });
    }


    //-----------Check Google Play Service--------
    private boolean CheckPlayService() {
        final int connectionStatusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(Map_Location_Search.this);
        if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
            return false;
        }
        return true;
    }


    void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
        Map_Location_Search.this.runOnUiThread(new Runnable() {
            public void run() {
                final Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
                        connectionStatusCode,   Map_Location_Search.this, REQUEST_CODE_RECOVER_PLAY_SERVICES);
                if (dialog == null) {
                    Toast.makeText(  Map_Location_Search.this, "incompatible version of Google Play Services", Toast.LENGTH_LONG).show();
                }
            }
        });
    }




    //-------------------------------code for map marker moving-------------------------------
    GoogleMap.OnCameraChangeListener mOnCameraChangeListener = new GoogleMap.OnCameraChangeListener() {
        @Override
        public void onCameraChange(CameraPosition cameraPosition) {
            double latitude = cameraPosition.target.latitude;
            double longitude = cameraPosition.target.longitude;

            cd = new ConnectionDetector(Map_Location_Search.this);
            isInternetPresent = cd.isConnectingToInternet();

            Log.e("camerachange lat-->", "" + latitude);
            Log.e("on_camera_change lon-->", "" + longitude);

            if (googleMap != null) {
                googleMap.clear();

                if (isInternetPresent) {

                    sLatitude = String.valueOf(latitude);
                    sLongitude = String.valueOf(longitude);

                    Map_movingTask asynTask=new Map_movingTask(latitude,longitude);
                    asynTask.execute();
                } else {
                    Alert(getResources().getString(R.string.alert_sorry_label_title), getResources().getString(R.string.no_internet_connection));
                }
            }
        }
    };

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    private class Map_movingTask extends AsyncTask<String, Void, String> {

        String response = "";
        private double dLatitude=0.0;
        private double dLongitude=0.0;
        Map_movingTask(double lat,double lng)
        {
            dLatitude=lat;
            dLongitude=lng;
        }
        @Override
        protected void onPreExecute() {
            progresswheel.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... urls) {
            String address = getCompleteAddressString(dLatitude, dLongitude);
            return address;
        }

        @Override
        protected void onPostExecute(String result) {
            progresswheel.setVisibility(View.GONE);
            if(result!=null)
            {
                selected_your_address.setText(result);
                done_button.setVisibility(View.VISIBLE);
            }else
            {
                selected_your_address.setVisibility(View.GONE);
            }
        }
    }


    //-------------Method to get Complete Address------------
    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(Map_Location_Search.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
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





    //-------------------Get Latitude and Longitude from Address(Place ID) Request----------------
  /*  private void LatLongRequest(String Url) {

        dialog = new Dialog(LocationSearch.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_processing));

        System.out.println("--------------LatLong url-------------------" + Url);

        mRequest = new ServiceRequest(LocationSearch.this);
        mRequest.makeServiceRequest(Url, Request.Method.GET, null, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("--------------LatLong  reponse-------------------" + response);
                String status = "";
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.length() > 0) {

                        status = object.getString("status");
                        JSONObject place_object = object.getJSONObject("result");
                        if (status.equalsIgnoreCase("OK")) {
                            if (place_object.length() > 0) {
                                JSONObject geometry_object = place_object.getJSONObject("geometry");
                                if (geometry_object.length() > 0) {
                                    JSONObject location_object = geometry_object.getJSONObject("location");
                                    if (location_object.length() > 0) {
                                        Slatitude = location_object.getString("lat");
                                        Slongitude = location_object.getString("lng");
                                        isdataAvailable = true;
                                    } else {
                                        isdataAvailable = false;
                                    }
                                } else {
                                    isdataAvailable = false;
                                }
                            } else {
                                isdataAvailable = false;
                            }
                        } else {
                            isdataAvailable = false;
                        }
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if (isdataAvailable) {

                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("Selected_Latitude", Slatitude);
                    returnIntent.putExtra("Selected_Longitude", Slongitude);
                    returnIntent.putExtra("Selected_Location", Sselected_location);
                    setResult(RESULT_OK, returnIntent);
                    onBackPressed();
                    overridePendingTransition(R.anim.slideup, R.anim.slidedown);
                    finish();

                } else {
                    dialog.dismiss();
                    Alert(getResources().getString(R.string.server_lable_header), status);
                }
            }

            @Override
            public void onErrorListener() {
                dialog.dismiss();
            }
        });
    }
*/


    //-------------------Get Latitude and Longitude from Address(Place ID) Request----------------
    private void LatLongRequest(String Url) {
        dialog = new Dialog(Map_Location_Search.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_processing));

        System.out.println("--------------LatLong url-------------------" + Url);

        mRequest = new ServiceRequest(Map_Location_Search.this);
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

                                            isdataAvailable = true;
                                        } else {
                                            isdataAvailable = false;
                                        }
                                    }
                                } else {
                                    isdataAvailable = false;
                                }

                                JSONObject geometry_object = place_object.getJSONObject("geometry");
                                if (geometry_object.length() > 0) {
                                    JSONObject location_object = geometry_object.getJSONObject("location");
                                    if (location_object.length() > 0) {
                                        Slatitude = location_object.getString("lat");
                                        Slongitude = location_object.getString("lng");
                                        isdataAvailable = true;
                                    } else {
                                        isdataAvailable = false;
                                    }
                                } else {
                                    isdataAvailable = false;
                                }
                            } else {
                                isdataAvailable = false;
                            }
                        } else {
                            isdataAvailable = false;
                        }
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                if (isdataAvailable) {

                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("Selected_Latitude", Slatitude);
                    returnIntent.putExtra("Selected_Longitude", Slongitude);
                    returnIntent.putExtra("Selected_Location", Sselected_location);
                    returnIntent.putExtra("HouseNo", sArea);
                    returnIntent.putExtra("City", sCity_Admin2 + sCity_Admin1);
                    returnIntent.putExtra("ZipCode", sPostalCode);
                    returnIntent.putExtra("Location", sLocality);
                    returnIntent.putExtra("Selected_Location", Sselected_location);
                    setResult(RESULT_OK, returnIntent);
                    onBackPressed();
                    overridePendingTransition(R.anim.slideup, R.anim.slidedown);
                    finish();

                } else {
                    dialog.dismiss();
                    Alert(getResources().getString(R.string.server_lable_header), status);
                }

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
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    //-----------------Move Back on pressed phone back button------------------
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)) {

            // close keyboard
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(back.getWindowToken(), 0);

            Map_Location_Search.this.finish();
            Map_Location_Search.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            return true;
        }
        return false;
    }
}

