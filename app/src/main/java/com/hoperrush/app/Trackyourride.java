package com.hoperrush.app;

import android.app.Dialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.hoperrush.R;
import com.hoperrush.core.dialog.PkDialog;
import com.hoperrush.core.googlemapdrawline.GMapV2GetRouteDirection;
import com.hoperrush.core.gps.GPSTracker;
import com.hoperrush.core.socket.SocketManager;
import com.hoperrush.core.volley.ServiceRequest;
import com.hoperrush.hockeyapp.ActivityHockeyApp;
import com.hoperrush.utils.ConnectionDetector;
import com.hoperrush.utils.LatLngInterpolator;
import com.hoperrush.utils.MarkerAnimation;
import com.hoperrush.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import android.os.Handler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


import cz.msebera.android.httpclient.Header;

public class Trackyourride extends ActivityHockeyApp implements View.OnClickListener, com.google.android.gms.location.LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private GoogleMap googleMap;
    private MarkerOptions marker;
    private GPSTracker gps;
    private double MyCurrent_lat = 0.0, MyCurrent_long = 0.0;
    private double Tasker_current_lat=0.0,Tasker_current_long=0.0;
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private String  driverLat = "", driverLong = "";
    private boolean isReasonAvailable = false;
    private ServiceRequest mRequest;
    private Dialog dialog;
    private SessionManager session;
    private MarkerOptions markerOptions;
    private LatLng fromPosition;
    private LatLng toPosition;
    private static Marker curentDriverMarker;
    private Location currentLocation;
    private String usercurrentlat="";
    private String usercurrentlong="";
    private SessionManager sessionManager;
    private String sUserID = "", sJobID = "";
    RelativeLayout backbutton;
    private SocketManager manager;
    private TextView time;
    private TextView kmeter;
    private TextView tasker_location;
    private String Current_Address="";
    private ImageView track_ride;
    private Circle mCircle;
    private LatLng newLatLng, oldLatLng;
    private LatLngInterpolator mLatLngInterpolator;
    private Location oldLocation;
    private double myMovingDistance = 0.0;
    private float bearingValue;
    double radiusInMeters = 100.0;
    int strokeColor = 0xffff0000; //Color Code you want
    int shadeColor = 0x44ff0000; //opaque red fill


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trackyourride);
       initialize();
       initializeheader();

        manager = new SocketManager(this,callBack);
        manager.Trackridelocation(callBack);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

    initializemap();

        track_ride.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String voice_curent_lat_long = usercurrentlat + "," + usercurrentlong;
                String voice_destination_lat_long = driverLat + "," + driverLong;
                System.out.println("----------fromPosition---------------" + voice_curent_lat_long);
                System.out.println("----------toPosition---------------" + voice_destination_lat_long);
                String locationUrl = "http://maps.google.com/maps?saddr=" + voice_curent_lat_long + "&daddr=" + voice_destination_lat_long;
                System.out.println("----------locationUrl---------------" + locationUrl);
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(locationUrl));
                startActivity(intent);
            }
        });

    }

//-----------------------------Socket CallBack Method----------------------------------------------------

    public SocketManager.SocketConnectCallBack callBack = new SocketManager.SocketConnectCallBack() {
        @Override
        public void onSuccessListener(Object response) {
            if (response instanceof JSONObject) {
                JSONObject jsonObject = (JSONObject) response;
                System.out.println("-----------SocketLocationUpdate------------" + jsonObject);


                try {
                     driverLat = jsonObject.getString("lat");
                     driverLong = jsonObject.getString("lng");

                    final double driverLat1= Double.parseDouble(driverLat);
                    final double driverLong2= Double.parseDouble(driverLong);

                    fromPosition = new LatLng(driverLat1,driverLong2);
                    toPosition = new LatLng(Double.parseDouble(usercurrentlat),Double.parseDouble(usercurrentlong));

                    updatetaskerlocation(driverLat1, driverLong2, Double.parseDouble(usercurrentlat), Double.parseDouble(usercurrentlong));
                    GetDistance(fromPosition,toPosition);
                    Current_Address=getCompleteAddressString(driverLat1,driverLong2);
                    tasker_location.setText(Current_Address);

                } catch (Exception e) {
                    e.printStackTrace();

                }
            }
        }
    };

//-----------------------------Socket CallBack Method----------------------------------------------------


    public void initialize(){
        gps = new GPSTracker(Trackyourride.this);
        sessionManager = new SessionManager(Trackyourride.this);
        HashMap<String, String> user = sessionManager.getUserDetails();
        sUserID = user.get(SessionManager.KEY_USER_ID);
        time=(TextView)findViewById(R.id.time);
        kmeter=(TextView)findViewById(R.id.kilometer);
        tasker_location=(TextView)findViewById(R.id.tasker_location);
        track_ride=(ImageView)findViewById(R.id.track_ride);
        Intent s= getIntent();
        driverLat=s.getStringExtra("lati");
        driverLong=s.getStringExtra("logi");
        usercurrentlat=s.getStringExtra("usercurrentlat");
        usercurrentlong=s.getStringExtra("usercurrentlong");

        Current_Address= getCompleteAddressString(Double.parseDouble(driverLat),Double.parseDouble(driverLong));
        tasker_location.setText(Current_Address);

    }

public void initializeheader(){

    backbutton=(RelativeLayout)findViewById(R.id.myJob_detail_headerBar_left_layout);
}
    public void addOtherMessage(String sMessage) {
        String data = "";
        String jsonUSER = "";
        String fromID = "";
        String lat="";
        String log="";
        try {
            JSONObject object = new JSONObject(sMessage);
            jsonUSER = object.getString("user");
//            lat=object.getString("lat");
//            log=object.getString("lng");
            JSONArray messagesArray = object.getJSONArray("messages");
            JSONObject message = (JSONObject) messagesArray.get(0);
            data = (String) message.getString("message");
            lat = message.getString("lat");
            log=message.getString("lng");

            double latitude= Double.parseDouble(lat);
            double longi= Double.parseDouble(log);
          //  updateGoogleMapTrackRide(latitude,longi,MyCurrent_lat,MyCurrent_long);

        }
        catch (Exception e) {
        }

    }


    @Override
    public void onResume() {
        super.onResume();
       manager.setSocketConnectListenre(callBack);
        }




    public void  initializemap(){
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.track_your_ride_mapview)).getMap();
            if (googleMap == null) {
                Toast.makeText(Trackyourride.this, "Sorry! unable to create maps", Toast.LENGTH_SHORT).show();
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
            MyCurrent_lat = Dlatitude;
            MyCurrent_long = Dlongitude;
            // Move the camera to last position with a zoom level
            CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(Dlatitude, Dlongitude)).zoom(15).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        } else {
            Alert(getResources().getString(R.string.action_error), getResources().getString(R.string.alert_gpsEnable));
        }
        //set marker for driver location.

        if (driverLat !=null && driverLong !=null) {


            fromPosition = new LatLng(Double.parseDouble(usercurrentlat),Double.parseDouble(usercurrentlong));
            toPosition = new LatLng(Double.parseDouble(driverLat), Double.parseDouble(driverLong));
            if (fromPosition != null && toPosition != null) {

//                Circle circle = googleMap.addCircle(new CircleOptions()
//                        .center(new LatLng(fromPosition.latitude, fromPosition.longitude))
//                        .radius(1000)
//                        .strokeColor(Color.RED)
//                        .fillColor(Color.BLUE));

                GetDistance(fromPosition,toPosition);
                GetRouteTask draw_route_asyncTask = new GetRouteTask();
                draw_route_asyncTask.execute();
            }

        }




    }




    private void Alert(String title, String alert) {
        final PkDialog mDialog = new PkDialog(Trackyourride.this);
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


    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        this.currentLocation = location;
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }



    //---------------AsyncTask to Draw PolyLine Between Two Point--------------
    public class GetRouteTask extends AsyncTask<String, Void, String> {

        String response = "";
        GMapV2GetRouteDirection v2GetRouteDirection = new GMapV2GetRouteDirection();
        Document document;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... urls) {
            //Get All Route values
            document = v2GetRouteDirection.getDocument(toPosition, fromPosition, GMapV2GetRouteDirection.MODE_DRIVING);
            response = "Success";
            return response;

        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equalsIgnoreCase("Success")) {
                googleMap.clear();
                try {
                    ArrayList<LatLng> directionPoint = v2GetRouteDirection.getDirection(document);
                    PolylineOptions rectLine = new PolylineOptions().width(18).color(getResources().getColor(R.color.ployline_color));
                    for (int i = 0; i < directionPoint.size(); i++) {
                        rectLine.add(directionPoint.get(i));
                    }
                    // Adding route on the map
                    googleMap.addPolyline(rectLine);
//                    markerOptions.position(fromPosition);
//                    markerOptions.position(toPosition);
//                    markerOptions.draggable(true);
//                    googleMap.addMarker(markerOptions);
                    googleMap.addMarker(new MarkerOptions()
                            .position(fromPosition)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.redmark))).setTitle("Current Location");
                    googleMap.addMarker(new MarkerOptions()
                            .position(toPosition)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.greenmark)));
                          curentDriverMarker = googleMap.addMarker(new MarkerOptions()
                            .position(toPosition)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.move_icon)));

                    //Show path in
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    builder.include(fromPosition);
                    builder.include(toPosition);
                    LatLngBounds bounds = builder.build();
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 162));
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //--------------------------------------------------Tasker Update Location Change

    private void updatetaskerlocation(double lat_decimal, double lng_decimal, double pick_lat_decimal, double pick_lng_decimal){
        if (lat_decimal != 0.0&&lng_decimal!=0.0) {

            try {
                LatLng latLng = new LatLng(lat_decimal, lng_decimal);
                //cur_latlong = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());

                Tasker_current_lat = lat_decimal;
                Tasker_current_long = lng_decimal;
                // Toast.makeText(getApplicationContext(),"updatelocation:"+Tasker_current_lat+","+Tasker_current_long,Toast.LENGTH_LONG).show();
                if (oldLatLng == null) {
                    oldLatLng = latLng;
                }
                newLatLng = latLng;

                if (mLatLngInterpolator == null) {
                    mLatLngInterpolator = new LatLngInterpolator.Linear();
                }

                oldLocation = new Location("");
                oldLocation.setLatitude(oldLatLng.latitude);
                oldLocation.setLongitude(oldLatLng.longitude);
                // float bearingValue = bearingValue;
                // Toast.makeText(getApplicationContext(),"Bearing:"+bearingValue,Toast.LENGTH_LONG).show();
                //  myMovingDistance = oldLocation.distanceTo(location);

                // System.out.println("moving distance------------" + myMovingDistance);
                // Toast.makeText(getApplicationContext(),"moving distance:"+myMovingDistance,Toast.LENGTH_LONG).show();
                if (googleMap != null) {

                    if (curentDriverMarker != null) {
                        if (!String.valueOf(bearingValue).equalsIgnoreCase("NaN")) {

                            rotateMarker(curentDriverMarker, bearingValue, googleMap);
                            MarkerAnimation.animateMarkerToGB(curentDriverMarker, latLng, mLatLngInterpolator);
                            float zoom = googleMap.getCameraPosition().zoom;
                            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(zoom).build();
                            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                            final LatLng latLngs = new LatLng(MyCurrent_lat,MyCurrent_lat);
                            final LatLng toLatLng = new LatLng(lat_decimal, lng_decimal);



                        }
                    } else {
                        curentDriverMarker.remove();
                        curentDriverMarker = googleMap.addMarker(new MarkerOptions()
                                .position(latLng)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.carmove))
                                .anchor(0.5f, 0.5f)
                                .rotation(bearingValue)
                                .flat(true));
                    }

                }

                oldLatLng = newLatLng;
            }
            catch (Exception e) {
            }

        }


    }
    //Method to smooth turn marker
    static public void rotateMarker(final Marker marker, final float toRotation, GoogleMap map) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final float startRotation = marker.getRotation();
        final long duration = 1555;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / duration);

                float rot = t * toRotation + (1 - t) * startRotation;

                marker.setRotation(-rot > 180 ? rot / 2 : rot);
                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }
            }
        });
    }





    //-------------------------------------------------------------------------------Distance Calculation-----------------------------------------------------------------

    public void GetDistance(LatLng start,LatLng end){

        String url1="https://maps.googleapis.com/maps/api/distancematrix/json?key=AIzaSyDXVTR_V9gvWep08joXPO2RTJcDBpNsu5I&origins="+ start.latitude + "," + start.longitude+"&destinations="+end.latitude + "," + end.longitude;
        System.out.println("---Url---------"+url1);
        String url = "http://maps.googleapis.com/maps/api/directions/xml?"
                + "origin=" + start.latitude + "," + start.longitude
                + "&destination=" + end.latitude + "," + end.longitude
                + "&sensor=false&units=metric&mode=driving";
        AsyncHttpClient distance=new AsyncHttpClient();
        distance.post(url1, null, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(getApplicationContext(),"Failure",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {

                System.out.println("------------------Response distance-------------"+responseString);
                //  Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_LONG).show();
                GetDuration(responseString);

            }
        });

    }



    public  void  GetDuration(String response){
        System.out.print("-------------------Response-----------------"+response);
        String status=" ";
        // layaddress.setVisibility(View.VISIBLE);
        try {
            JSONObject ob=new JSONObject(response);
            JSONArray dest=ob.getJSONArray("destination_addresses");
            status=ob.getString("status");
            if(status.equalsIgnoreCase("OK")){
                if(dest.length()>0){
                    JSONArray row=ob.getJSONArray("rows");

                    JSONObject object = (JSONObject) row.get(0);
                    JSONArray elementsArray =   object.getJSONArray("elements");
                    JSONObject distance = (JSONObject) elementsArray.get(0);
                    JSONObject distanceObject  = (JSONObject) distance.get("distance");


                    String kilometer=distanceObject.getString("text");
                    kmeter.setText("("+kilometer+")");

                    JSONObject distanceObject1  = (JSONObject) distance.get("duration");
                    // JSONObject duration=distance1.getJSONObject("duration");
                    String dur=distanceObject1.getString("text");
                        time.setText(dur);
//                    arrivedtext1.setText("Driver will be Arriving in : "+dur);
//                    Toast.makeText(getApplicationContext(),"Driver will be Arrived in : "+dur,Toast.LENGTH_LONG).show();
//                    arrtext=arrivedtext1.getText().toString();

                }

            }
//              if(arrtext!=null){
//               Thread background = new Thread() {
//                   public void run() {
//
//                       try {
//                           // Thread will sleep for 5 seconds
//                           sleep(10*1000);
//                           GetDistance(fromPosition,latLng);
//
//                          // finish();
//
//                       } catch (Exception e) {
//
//                       }
//                   }
//               };
//
//               // start thread
//               background.start();
//
//           }


        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }


    //-----------------------------------------------------------Current Address Get-----------------------------------------------------------------

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
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


}
