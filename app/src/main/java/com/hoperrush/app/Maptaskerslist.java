package com.hoperrush.app;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hoperrush.R;
import com.hoperrush.core.dialog.LoadingDialog;
import com.hoperrush.core.dialog.PkDialog;
import com.hoperrush.core.gps.GPSTracker;
import com.hoperrush.core.socket.SocketHandler;
import com.hoperrush.core.volley.ServiceRequest;
import com.hoperrush.hockeyapp.ActivityHockeyApp;
import com.hoperrush.iconstant.Iconstant;
import com.hoperrush.pojo.MarkerData;
import com.hoperrush.pojo.ProvidersListPojo;
import com.hoperrush.utils.ConnectionDetector;
import com.hoperrush.utils.SessionManager;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Maptaskerslist extends ActivityHockeyApp implements View.OnClickListener, com.google.android.gms.location.LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener , Iconstant {

    private static final String TAG_Location = "location_id";
    private static final String TAG_minicost="mini_cost";
    private static final String TAG_hourlycost="hourly_cost";
    private static final String TAG_url_image="url_image";
    private static final String TAG_tasker_id="tasker_id";
    private static final String TAG_rating="rating";
    String mini_cost="";
    String hour_cost="";
    String user_image="";
    String user_name="";
    String Tasker_id="";
    String Tasker_rating="";
    private ConnectionDetector cd;
    private boolean isInternetPresent = false;
    private SessionManager sessionManager;
    private View moreAddressView;
    private Dialog moreAddressDialog;
    private static final String LOG_TAG = "ExampleApp";
    Marker marker;
    private LoadingDialog mLoadingDialog1;
    private ServiceRequest mRequest;
    private String minimum_amount="",hourly_amount="";
     private ImageView listimage;
    private String Str_bookingId = "", Str_Taskid = "";
    private String STaskerId = "";
    private Circle mCircle;
    private String SUser_Id = "", Saddress1 = "", StrcatergoryId = "", Spickup_date = "", Spickuptime = "", Sinstruction = "", StrService_id = "", Str_lattitude = "", SAddress = "", Str_longitude = "";
    private View mCustomMarkerView;
    private ImageView mMarkerImageView;
    private String ImageUrl = "https://s3.amazonaws.com/uifaces/faces/twitter/jsa/128.jpg";
    HashMap<String, HashMap> extraMarkerInfo = new HashMap<String, HashMap>();
    private static final String SERVICE_URL = "YOUR DRIVE SERVICE URL";
    private GPSTracker gps;
    protected GoogleMap googleMap;
    private double MyCurrent_lat = 0.0, MyCurrent_long = 0.0;
    private static final int REQUEST_CODE_RECOVER_PLAY_SERVICES = 1001;
    Map<String, Integer> mMarkers = new HashMap<String, Integer>();
    ArrayList<MarkerData> markersArray = new ArrayList<MarkerData>();
    ArrayList<HashMap<String,String>> latarray=new ArrayList<HashMap<String,String>>();
    ArrayList<HashMap<String,String>> lngarray=new ArrayList<HashMap<String,String>>();
    private RelativeLayout backicon;
    CameraUpdate cu;
    private String jsons=" [\n" +
            "    {\n" +
            "        \"name\": \"John Doe\",\n" +
            "        \"id\": \"1\",\n" +
            "        \"urlimage\": \"https://s3.amazonaws.com/uifaces/faces/twitter/jsa/128.jpg\",\n" +
            "        \"minicost\": \"$300\",\n" +
            "        \"hourlycost\": \"$30\",\n" +

            "        \"latlng\": [\n" +
            "            12.9674211,\n" +
            "            80.2177429\n" +
            "        ],\n" +
            "        \"population\": \"123\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"name\": \"Jane Doe\",\n" +
            "        \"id\": \"2\",\n" +
            "        \"urlimage\": \"https://s3.amazonaws.com/uifaces/faces/twitter/jsa/128.jpg\",\n" +
            "        \"minicost\": \"$400\",\n" +
            "        \"hourlycost\": \"$40\",\n" +
            "        \"latlng\": [\n" +
            "            13.0734305,\n" +
            "            80.2252417\n" +
            "        ],\n" +
            "        \"population\": \"132\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"name\": \"James Bond\",\n" +
            "        \"id\": \"3\",\n" +
            "        \"urlimage\": \"https://s3.amazonaws.com/uifaces/faces/twitter/jsa/128.jpg\",\n" +
            "        \"minicost\": \"$500\",\n" +
            "        \"hourlycost\": \"$50\",\n" +
            "        \"latlng\": [\n" +
            "            13.0627273,\n" +
            "            80.1437088\n" +
            "        ],\n" +
            "        \"population\": \"123\"\n" +
            "    }\n" +
            "] ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maptaskerslist);
        gps = new GPSTracker(Maptaskerslist.this);
        initViews();
        initializeMap();


        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                moreAddressDialog(mini_cost,hour_cost,user_image,user_name,Tasker_id,Tasker_rating);
            }
        });
        listimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Maptaskerslist.this, ProvidersList.class);
                i.putExtra("user_id", SUser_Id);
                i.putExtra("address_name", Saddress1);
                i.putExtra("category", StrcatergoryId);
                i.putExtra("pickup_date",Spickup_date);
                i.putExtra("pickup_time", Spickuptime);
                i.putExtra("instruction",Sinstruction);
                i.putExtra("service", StrService_id);
                i.putExtra("lat", Str_lattitude);
                i.putExtra("long", Str_longitude);
                startActivity(i);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        backicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }


    private void initViews() {

        mCustomMarkerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_custom_marker, null);
        mMarkerImageView = (ImageView) mCustomMarkerView.findViewById(R.id.profile_image);
        listimage=(ImageView)findViewById(R.id.list_image);
        backicon=(RelativeLayout)findViewById(R.id.myJob_detail_headerBar_left_layout);
         mRequest = new ServiceRequest(Maptaskerslist.this);
         sessionManager = new SessionManager(Maptaskerslist.this);
         cd = new ConnectionDetector(Maptaskerslist.this);
        isInternetPresent = cd.isConnectingToInternet();
        Intent intent = getIntent();
        SUser_Id = intent.getStringExtra("user_id");
        Saddress1 = intent.getStringExtra("address_name");
        StrcatergoryId = intent.getStringExtra("category");
        Spickup_date = intent.getStringExtra("pickup_date");
        Spickuptime = intent.getStringExtra("pickup_time");
        Sinstruction = intent.getStringExtra("instruction");
        StrService_id = intent.getStringExtra("service");
        Str_lattitude = intent.getStringExtra("lat");
        Str_longitude = intent.getStringExtra("long");
        SAddress = intent.getStringExtra("Address");




    }


    private void initializeMap() {
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.myJobs_detail_mapView)).getMap();

            if (googleMap == null) {
                //Toast.makeText(MyJobDetail.this, "Sorry! unable to create maps", Toast.LENGTH_SHORT).show();
            }
        }

        if (CheckPlayService()) {
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            googleMap.setMyLocationEnabled(false);
            googleMap.getUiSettings().setZoomControlsEnabled(false);
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
            googleMap.getUiSettings().setCompassEnabled(false);
            googleMap.getUiSettings().setRotateGesturesEnabled(true);
            googleMap.getUiSettings().setZoomGesturesEnabled(true);
            googleMap.setMyLocationEnabled(false);

            //---------Hiding the bottom layout after success request--------
            googleMap.getUiSettings().setAllGesturesEnabled(true);
            if (gps.canGetLocation() && gps.isgpsenabled()) {
                double Dlatitude = gps.getLatitude();
                double Dlongitude = gps.getLongitude();
                MyCurrent_lat = Dlatitude;
                MyCurrent_long = Dlongitude;
                MarkerOptions options = new MarkerOptions();

//                options.position(new LatLng(MyCurrent_lat, MyCurrent_long));
//                LatLng latLng = new LatLng(MyCurrent_lat, MyCurrent_long);
//                drawMarkerWithCircle(latLng);
                CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(Dlatitude, Dlongitude)).zoom(15).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            } else {
               // Alert(getResources().getString(R.string.action_error), getResources().getString(R.string.alert_gpsEnable));
            }

            if (isInternetPresent) {

                postProvidersRequest(Iconstant.Map_boooking);
            } else {

            }
          //  setUpMap();
        } else {
            final PkDialog mDialog = new PkDialog(Maptaskerslist.this);
            mDialog.setDialogTitle(getResources().getString(R.string.action_sorry));
            mDialog.setDialogMessage(getResources().getString(R.string.myJobs_detail_label_unable_to_create_map));
            mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialog.dismiss();
                    finish();
                }
            });
            mDialog.show();
        }

    }

    private void drawMarkerWithCircle(LatLng position){
        double radiusInMeters = 500.0;
        int strokeColor = 0xffff0000; //red outline
        int shadeColor = 0x44ff0000; //opaque red fill

        CircleOptions circleOptions = new CircleOptions().center(position).radius(radiusInMeters).fillColor(shadeColor).strokeColor(strokeColor).strokeWidth(8);
        mCircle = googleMap.addCircle(circleOptions);

//        MarkerOptions markerOptions = new MarkerOptions().position(position);
//        marker = googleMap.addMarker(markerOptions);
    }


    //--------------------------------------------------------Map Tasker lIst--------------------------------------------------------------------

    public void postProvidersRequest(String url) {

        mLoadingDialog1 = new LoadingDialog(Maptaskerslist.this);
        mLoadingDialog1.setLoadingTitle(getResources().getString(R.string.action_processing));
        mLoadingDialog1.show();

        System.out.println("-----------user_id------------------" + SUser_Id);
        System.out.println("------------address_name-----------------" + Saddress1);
        System.out.println("-------------category----------------" + StrcatergoryId);
        System.out.println("-------------pickup_date----------------" + Spickup_date);
        System.out.println("--------------pickup_time---------------" + Spickuptime);
        System.out.println("--------------instruction---------------" + Sinstruction);
        System.out.println("--------------service---------------" + StrService_id);
        System.out.println("--------------lat---------------" + Str_lattitude);
        System.out.println("--------------long---------------" + Str_longitude);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", "58a6a113adf3bf447ccf149e");
//        jsonParams.put("address_name", "0");
//        jsonParams.put("category", StrcatergoryId);
//        jsonParams.put("pickup_date", "2017-03-03");
//        jsonParams.put("pickup_time", "11:00");
//        // jsonParams.put("code", Et_couponCode.getText().toString());
//        jsonParams.put("instruction", "jvj");
//        // jsonParams.put("try", "");
//        // jsonParams.put("job_id", "");
//        jsonParams.put("service", "5800d4b9b264e42c0fe70ba6");
        jsonParams.put("lat", "12.9232405");
        jsonParams.put("long", "80.1216489");

        //jsonParams.put("lat", "13.0826802");
        //jsonParams.put("long", "80.27071840000008");


        mRequest.makeServiceRequest(url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("respionse--------------Tasker" + response);
                String Str_status = "", Str_response = "", Scurrency = "";
                try {

                    JSONObject object = new JSONObject(response);
                    Str_status = object.getString("status");

                    if (Str_status.equalsIgnoreCase("1")) {

//                        Str_bookingId = object.getString("booking_id");
//                        Str_Taskid = object.getString("task_id");

                        JSONArray jarry = object.getJSONArray("response");
                        if (jarry.length() > 0) {
                            for (int i = 0; i < jarry.length(); i++) {
                                JSONObject jobject = jarry.getJSONObject(i);
                                ProvidersListPojo pojo = new ProvidersListPojo();
                                MarkerData mark=new MarkerData();

                                mark.setlatitude(jobject.getString("lat"));
                                mark.setLogintude(jobject.getString("lng"));
                                mark.settiltle(jobject.getString("name"));
                               // mark.setId(jsonObj.getString("id"));
                                mark.setimageurl(jobject.getString("image_url"));
                                mark.setCategory_imageurl(jobject.getString("cat_img"));
                                mark.setMini_cost(jobject.getString("min_amount"));
                                mark.setHourly_cost(jobject.getString("hourly_amount"));
                                mark.setTaskerId(jobject.getString("taskerid"));
                                mark.setRating(jobject.getString("rating"));


//                                pojo.setProvider_name(jobject.getString("name"));
//                                pojo.setProvider_company(jobject.getString("company"));
//                                pojo.setProvider_rating(jobject.getString("rating"));
//                                pojo.setProvider_image(jobject.getString("image_url"));
//                                pojo.setTaskerId(jobject.getString("taskerid"));
//                                pojo.setReviews(jobject.getString("reviews"));
//                                pojo.setRadius(jobject.getString("radius"));
//                                pojo.setProvider_availble(jobject.getString("availability"));
//                                pojo.setProvider_mincost("$" + jobject.getString("min_amount"));
                                minimum_amount=jobject.getString("min_amount");
                                hourly_amount=jobject.getString("hourly_amount");

//                                pojo.setHourly_rate("$" + jobject.getString("hourly_amount"));
//                                pojo.setTaskId(Str_Taskid);

                                markersArray.add(mark);
                                mLoadingDialog1.dismiss();

                            }
                            sessionManager.setminimum_amount(minimum_amount);
                            sessionManager.sethourly_amount(hourly_amount);

                            sessionManager.settaskid(Str_Taskid);

                        Addmarker();
                            //isproviderAvailable = true;
                        } else {
//                            isproviderAvailable = false;
//                            providersList.clear();
                        }
                    } else {
                        Str_response = object.getString("response");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    mLoadingDialog1.dismiss();
                }

               /* if (Str_status.equalsIgnoreCase("1")){

                    Intent i = new Intent(ProvidersList.this,ProvidersList.class);
                    startActivity(i);
                }*/




                mLoadingDialog1.dismiss();

            }

            @Override
            public void onErrorListener() {
                mLoadingDialog1.dismiss();
            }
        });

    }



    private void setUpMap() {
        new Thread(new Runnable() {
            public void run() {
                try {


                    createMarkersFromJson(jsons);
                    //retrieveAndAddCities();
                }  catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    protected void retrieveAndAddCities() throws IOException {
        HttpURLConnection conn = null;
        final StringBuilder json = new StringBuilder();
        try {

            URL url = new URL(SERVICE_URL);
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());


            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                json.append(buff, 0, read);
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to service", e);
            throw new IOException("Error connecting to service", e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }


        runOnUiThread(new Runnable() {
            public void run() {
                try {
                    createMarkersFromJson(json.toString());
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "Error processing JSON", e);
                }
            }
        });
    }



    private boolean CheckPlayService() {
        final int connectionStatusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(Maptaskerslist.this);
        if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
            return false;
        }
        return true;
    }

    void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
        runOnUiThread(new Runnable() {
            public void run() {
                final Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
                        connectionStatusCode, Maptaskerslist.this, REQUEST_CODE_RECOVER_PLAY_SERVICES);
                if (dialog == null) {
                    Toast.makeText(Maptaskerslist.this, "incompatible version of Google Play Services", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    void createMarkersFromJson(String json) throws JSONException {

        JSONArray jsonArray = new JSONArray(json);
        for (int i = 0; i < jsonArray.length(); i++) {

            JSONObject jsonObj = jsonArray.getJSONObject(i);
            MarkerData mark=new MarkerData();
            mark.setlatitude(jsonObj.getJSONArray("latlng").getString(0));
            mark.setLogintude(jsonObj.getJSONArray("latlng").getString(1));
            mark.settiltle(jsonObj.getString("name"));
            mark.setId(jsonObj.getString("id"));
            mark.setimageurl(jsonObj.getString("urlimage"));
            mark.setMini_cost(jsonObj.getString("minicost"));
            mark.setHourly_cost(jsonObj.getString("hourlycost"));

//            googleMap.addMarker(new MarkerOptions()
//                    .title(jsonObj.getString("name"))
//                    .snippet(Integer.toString(jsonObj.getInt("population")))
//                    .position(new LatLng(
//                            jsonObj.getJSONArray("latlng").getDouble(0),
//                            jsonObj.getJSONArray("latlng").getDouble(1)
//                    ))
//            );
            markersArray.add(mark);
        }

        Addmarker();
    }

    public void Addmarker(){

        if(markersArray.size()!=0){
            for(int i = 0 ; i < markersArray.size() ; i++ ) {

                createMarker(markersArray.get(i).getLatitude(), markersArray.get(i).getLogintude(), markersArray.get(i).getTitle(),markersArray.get(i).getId(),markersArray.get(i).getimageurl(),markersArray.get(i).getMini_cost(),markersArray.get(i).getHourly_cost(),markersArray.get(i).getTaskerId(),markersArray.get(i).getRating(),markersArray.get(i).getimageurl());

                HashMap<String,String> map=new HashMap<String,String>();
                map.put("lat",markersArray.get(i).getLatitude());
                HashMap<String,String> map1=new HashMap<String,String>();
                map1.put("lng",markersArray.get(i).getLogintude());
                latarray.add(map);
                lngarray.add(map1);

            }

        }

    }

    protected void createMarker(String latitude, String longitude, final String title, final String id, final String urlimage,final String mini_cost,final String hourly_cost,final String tasker_id,final String rating,final String category_image)
    {

        final List<Marker> markersList = new ArrayList<Marker>();

        final double lat= Double.parseDouble(latitude);
        final double longi= Double.parseDouble(longitude);


 try{
     Handler handler = new Handler(Looper.getMainLooper());
     handler.post(new Runnable(){
         @Override
         public void run() {

             Glide.with(getApplicationContext()).
                     load(category_image)
                     .asBitmap()
                     .fitCenter()
                     .into(new SimpleTarget<Bitmap>() {
                         @Override
                         public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {

                             marker = googleMap.addMarker(new MarkerOptions()
                                     .position(new LatLng(lat, longi))
                                     .title(title).snippet("jkjjkkjjj")
                                     .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(mCustomMarkerView, bitmap))));
                           // googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, longi), 13f));
                             markersList.add(marker);

                             LatLngBounds.Builder builder = new LatLngBounds.Builder();

                            for(int i=0;i<latarray.size();i++){

                                String lat=latarray.get(i).get("lat");
                                String lng=lngarray.get(i).get("lng");
                                final double lati= Double.parseDouble(lat);
                                final double longi= Double.parseDouble(lng);
                                builder.include(new LatLng(lati,longi));
                            }
                             LatLngBounds bounds = builder.build();
                             googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 162));




                             HashMap<String, String> data = new HashMap<String, String>();

                             data.put(TAG_Location,title);
                             data.put(TAG_minicost,mini_cost);
                             data.put(TAG_hourlycost,hourly_cost);
                             data.put(TAG_url_image,urlimage);
                             data.put(TAG_tasker_id,tasker_id);
                             data.put(TAG_rating,rating);
                             extraMarkerInfo.put(marker.getId(),data);
                         }


                     });

            Showcustominfowindow(title);
         }

                  });




    }


     catch (Exception e){
     Log.e(LOG_TAG, "Error marker", e);
        }


    }
     public void Showcustominfowindow(final String title){

         googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

             // Use default InfoWindow frame
             @Override
             public View getInfoWindow(Marker arg0) {
                 return null;
             }

             // Defines the contents of the InfoWindow
             @Override
             public View getInfoContents(Marker arg0) {

                 View v = getLayoutInflater().inflate(R.layout.info_window_layout, null);
                 HashMap<String, String> marker_data = extraMarkerInfo.get(arg0.getId());

                 // Getting the data from Map
                  String location = marker_data.get(TAG_Location);
                  mini_cost = marker_data.get(TAG_minicost);
                  hour_cost = marker_data.get(TAG_hourlycost);
                  user_image=marker_data.get(TAG_url_image);
                  user_name=marker_data.get(TAG_Location);
                  Tasker_id=marker_data.get(TAG_tasker_id);
                  Tasker_rating=marker_data.get(TAG_rating);
                 Button view_details=(Button)v.findViewById(R.id.view_details);
                 ImageView userimages=(ImageView)v.findViewById(R.id.image);
                 Picasso.with(getApplicationContext()).load(user_image).error(R.drawable.placeholder_icon)
                         .placeholder(R.drawable.placeholder_icon).memoryPolicy(MemoryPolicy.NO_CACHE).into(userimages);
                 RatingBar ratingBar=(RatingBar)v.findViewById(R.id.provider_rating);
                 TextView tvLat = (TextView) v.findViewById(R.id.text);
                 TextView minicost=(TextView)v.findViewById(R.id.mini_cost_text);
                 TextView hourcost=(TextView)v.findViewById(R.id.hour_cost_text);
                 minicost.setText(mini_cost);
                 hourcost.setText(hour_cost);
                 String titles=arg0.getTitle();
                 tvLat.setText(titles);

                 ratingBar.setRating(Float.parseFloat(Tasker_rating));

                 view_details.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View view) {

                     }
                 });

                 return v;

             }
         });



        }

    private void moreAddressDialog(String mini_cost, String hour_cost, String image, String user_name, final String tasker_id,final String tasker_rating) {
        //--------Adjusting Dialog width-----
        DisplayMetrics metrics = Maptaskerslist.this.getResources().getDisplayMetrics();
        int screenWidth = (int) (metrics.widthPixels * 0.80);//fill only 80% of the screen
        moreAddressView = View.inflate(Maptaskerslist.this, R.layout.maptasker_show_layout, null);
        moreAddressDialog = new Dialog(Maptaskerslist.this);
        moreAddressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        moreAddressDialog.setContentView(moreAddressView);
        moreAddressDialog.setCanceledOnTouchOutside(true);
        moreAddressDialog.getWindow().setLayout(screenWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        moreAddressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView minicost=(TextView)moreAddressView.findViewById(R.id.mini_cost);
        TextView hourcost=(TextView)moreAddressView.findViewById(R.id.hour_cost);
        TextView username=(TextView)moreAddressView.findViewById(R.id.user_name);
        TextView rating=(TextView)moreAddressView.findViewById(R.id.rating);
        ImageView userimage=(ImageView)moreAddressView.findViewById(R.id.user_image);
        TextView chat=(TextView)moreAddressView.findViewById(R.id.chat);
        TextView book=(TextView)moreAddressView.findViewById(R.id.book);
        TextView details=(TextView)moreAddressView.findViewById(R.id.details);
        rating.setText(tasker_rating);
        minicost.setText(" Mini_Cost  : "+ " " +"$"+ mini_cost);
        hourcost.setText("Hour_Cost : "+ " " + "$"+ hour_cost);
        username.setText(user_name);
        Picasso.with(getApplicationContext()).load(image).error(R.drawable.placeholder_icon)
                .placeholder(R.drawable.placeholder_icon).memoryPolicy(MemoryPolicy.NO_CACHE).into(userimage);
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                moreAddressDialog.dismiss();
            }
        });
        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final PkDialog mDialog = new PkDialog(Maptaskerslist.this);
                mDialog.setDialogTitle("Confirm Booking");
                mDialog.setDialogMessage(getResources().getString(R.string.terms_and_conditions));
                mDialog.setPositiveButton("Book", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                        cd = new ConnectionDetector(Maptaskerslist.this);
                        isInternetPresent = cd.isConnectingToInternet();

                        if (isInternetPresent) {
                            Book_job_Request(Maptaskerslist.this, tasker_id);
                        } else {
                            alert(getResources().getString(R.string.action_no_internet_title), getResources().getString(R.string.action_no_internet_message));
                        }
                    }
                });
                mDialog.setNegativeButton("No", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                    }
                });
                mDialog.show();





                moreAddressDialog.dismiss();
            }
        });
        details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getApplicationContext(), PartnerProfilePage.class);
                sessionManager.putProvideID(tasker_id);
                sessionManager.putProvideScreenType(PROVIDER);

                i.putExtra("userid",SUser_Id);
                i.putExtra("task_id",Str_Taskid);
                i.putExtra("address",Saddress1);
                i.putExtra("taskerid",tasker_id);
                startActivity(i);
                moreAddressDialog.dismiss();
            }
        });


        moreAddressDialog.show();

    }
    //------Alert Method-----
    private void alert(String title, String message) {
        final PkDialog mDialog = new PkDialog(Maptaskerslist.this);
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


    //------------------------------------------------Tasker Booking Request---------------------------------------------------------


    public void Book_job_Request(Context mContext, final String TaskerId) {

        mLoadingDialog1 = new LoadingDialog(mContext);
        mLoadingDialog1.setLoadingTitle(getResources().getString(R.string.action_processing));
        mLoadingDialog1.show();


        System.out.println("-----------user_id------------------" + SUser_Id);
        System.out.println("-----------taskid------------------" + Str_Taskid);
        System.out.println("-----------taskerid------------------" + TaskerId);
        System.out.println("-----------location------------------" + Saddress1);


        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", SUser_Id);
        jsonParams.put("taskid", Str_Taskid);
        jsonParams.put("taskerid", TaskerId);
        jsonParams.put("location", Saddress1);


        mRequest = new ServiceRequest(mContext);
        mRequest.makeServiceRequest(Iconstant.BookJob, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                // System.out.println("urlbook------------"+Iconstant.BookJob);

                System.out.println("-------------bookjobResponse----------------" + response);

                String sStatus = "", sResponse = "", sJobId = "", sMessage = "", sDescription = "",
                        sServiceType = "", sNote = "", sBookingDate = "", sJobDate = "";
                try {

                    JSONObject object = new JSONObject(response);
                    sStatus = object.getString("status");
                    if (sStatus.equalsIgnoreCase("1")) {
                        JSONObject responseObject = object.getJSONObject("response");
                        if (responseObject.length() > 0) {
                            sJobId = responseObject.getString("job_id");
                            sMessage = responseObject.getString("message");
                            sDescription = responseObject.getString("description");
                            sServiceType = responseObject.getString("service_type");
                            sNote = responseObject.getString("note");
                            sBookingDate = responseObject.getString("booking_date");
                            sJobDate = responseObject.getString("job_date");

                            Intent intent = new Intent(Maptaskerslist.this, AppointmentConfirmationPage.class);
                            intent.putExtra("IntentJobID", sJobId);
                            intent.putExtra("IntentMessage", sMessage);
                            intent.putExtra("IntentOrderDate", sBookingDate);
                            intent.putExtra("IntentJobDate", sJobDate);
                            intent.putExtra("IntentDescription", sDescription);
                            intent.putExtra("IntentServiceType", sServiceType);
                            intent.putExtra("IntentNote", sNote);
                            startActivity(intent);

                            finish();
                            overridePendingTransition(R.anim.enter, R.anim.exit);
                        }
                    } else {
                        sResponse = object.getString("response");
                        alert(getResources().getString(R.string.action_sorry), sResponse);
                    }


                    if (sStatus.equalsIgnoreCase("1")) {
                        System.out.println("---------provider list TaskerId id--------" + TaskerId);

                        sessionManager = new SessionManager(Maptaskerslist.this);

                        sessionManager.setjobid(sJobId);
                        HashMap<String, String> task = sessionManager.getSocketTaskId();
                        String sTask = task.get(SessionManager.KEY_TASK_ID);

                        if (sTask != null && sTask.length() > 0) {
                            if (!sTask.equalsIgnoreCase(TaskerId)) {
                                sessionManager.setSocketTaskId(TaskerId);
                                System.out.println("---------Room Switched--------" + TaskerId);
                                SocketHandler.getInstance(getApplicationContext()).getSocketManager().createSwitchRoom(TaskerId);
                            }
                        } else {
                            System.out.println("---------Room Created--------" + TaskerId);
                            sessionManager.setSocketTaskId(TaskerId);

                            // SocketHandler.getInstance(context).getSocketManager().createRoom(TaskerId);
                        }
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                mLoadingDialog1.dismiss();
            }

            @Override
            public void onErrorListener() {
                mLoadingDialog1.dismiss();
            }
        });
    }





    private Bitmap getMarkerBitmapFromView(View view, Bitmap bitmap) {

        mMarkerImageView.setImageBitmap(bitmap);
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = view.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        view.draw(canvas);
        return returnedBitmap;
    }



    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
