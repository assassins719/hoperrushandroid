package com.hoperrush.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Handler;
import android.os.Looper;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.InflateException;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hoperrush.R;
import com.hoperrush.adapter.Map_main_category_adapter;
import com.hoperrush.adapter.Map_sub_category_adapter;
import com.hoperrush.app.AppointmentConfirmationPage;
import com.hoperrush.app.ChatPage;
import com.hoperrush.app.LogInPage;
import com.hoperrush.app.Map_Location_Search;
import com.hoperrush.app.NavigationDrawer;
import com.hoperrush.app.NewAppointmentpage;
import com.hoperrush.app.PartnerProfilePage;
import com.hoperrush.core.dialog.LoadingDialog;
import com.hoperrush.core.dialog.PkDialog;
import com.hoperrush.core.dialog.PkLoadingDialog;
import com.hoperrush.core.gps.GPSTracker;
import com.hoperrush.core.socket.ChatMessageService;
import com.hoperrush.core.socket.SocketHandler;
import com.hoperrush.core.volley.ServiceRequest;
import com.hoperrush.hockeyapp.FragmentHockeyApp;
import com.hoperrush.iconstant.Iconstant;
import com.hoperrush.pojo.CategoryDetailPojo;
import com.hoperrush.pojo.CategoryPojo;
import com.hoperrush.pojo.CitySelectionPojo;
import com.hoperrush.pojo.MarkerData;
import com.hoperrush.pojo.ProvidersListPojo;
import com.hoperrush.utils.ConnectionDetector;
import com.hoperrush.utils.HorizontalListView;
import com.hoperrush.utils.SessionManager;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import net.bohush.geometricprogressview.GeometricProgressView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import cn.refactor.lib.colordialog.ColorDialog;
import dmax.dialog.SpotsDialog;


public class Fragment_Map_Home_Page extends FragmentHockeyApp implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, Iconstant {
    private RelativeLayout Rl_drawer;
    private ImageView Im_drawerIcon;
    private TextView Tv_headerTitle;
    private Context context;
    private SessionManager sessionManager;
    private SocketHandler socketHandler;
    private int search_status = 0;
    private int placeSearch_request_code = 200;
    private static final int REQUEST_CODE_RECOVER_PLAY_SERVICES = 1001;
    private LinearLayout layout_address_text;
    private TextView textview_address_text;
    private String SselectedLatitude = "";
    private String SselectedLongitude = "";
    private String SdestinationLatitude = "";
    private String SdestinationLongitude = "";
    private boolean isCameraChangeListener = true, isLoading = false;
    String address1 = "";
    String city = "";
    String state = "";
    String country = "";
    String postalCode = "";
    private double MyCurrent_lat = 0.0, MyCurrent_long = 0.0;
    private double Recent_lat = 0.0, Recent_long = 0.0;
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private GoogleMap googleMap;
    GPSTracker gps;
    private ArrayList<CategoryPojo> catItemList;
    private ArrayList<CategoryDetailPojo> sub_catItemList;
    private ArrayList<CitySelectionPojo> cityItemList;
    private Map_main_category_adapter adapter;
    private Map_sub_category_adapter sub_adapter;
    private ImageView center_marker, currentLocation_image;
    private boolean asCategory = false;
    private boolean asLocation = false;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    private ServiceRequest mRequest;
    PendingResult<LocationSettingsResult> result;
    final static int REQUEST_LOCATION = 299;
    private PkLoadingDialog mLoadingDialog;
    private String address = "";
    private String Str_SelectedCity_Name = "", Str_SelectedCity_Id = "";
    private String category_id = "", category_name = "", category_image = "";
    private HorizontalListView listView;
    private HorizontalListView subcategory_listview;
    private RelativeLayout subcategory_layout;
    //-----------------Map Tasker List-----------
    private static final String TAG_Location = "location_id";
    private static final String TAG_minicost = "mini_cost";
    private static final String TAG_hourlycost = "hourly_cost";
    private static final String TAG_url_image = "url_image";
    private static final String TAG_tasker_id = "tasker_id";
    private static final String TAG_rating = "rating";
    private static final String TAG_latitude = "latitude";
    private static final String TAG_longitude = "longitude";
    private static final String TAG_tasker_address = "address";
    private static final String LOG_TAG = "ExampleApp";
    private String Str_bookingId = "", Str_Taskid = "";
    String main_category_id = "", sub_category_id = "";
    private String minimum_amount = "", hourly_amount = "";
    String Current_lat = "", Current_long = "";
    String UserID = "";
    String mini_cost = "";
    String hour_cost = "";
    String user_image = "";
    String user_name = "";
    String Tasker_id = "";
    String Tasker_rating = "";
    String Tasker_lat = "";
    String Tasker_long = "";
    String tasker_address = "";
    private String SUser_Id = "", Saddress1 = "", StrcatergoryId = "", Spickup_date = "", Spickuptime = "", Sinstruction = "", StrService_id = "", Str_lattitude = "", SAddress = "", Str_longitude = "";
    ArrayList<MarkerData> markersArray = new ArrayList<MarkerData>();
    ArrayList<HashMap<String, String>> latarray = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> lngarray = new ArrayList<HashMap<String, String>>();
    HashMap<String, HashMap> extraMarkerInfo = new HashMap<String, HashMap>();
    Marker marker;
    private View mCustomMarkerView;
    private ImageView mMarkerImageView;
    private RelativeLayout available_service;
    private View moreAddressView;
    public static Dialog moreAddressDialog;
    private Button book_later;
    private Button book_now;
    private String book_now_taskid = "";
    private String book_now_taskerid = "";
    private String book_now_taskername = "";
    private String book_now_time = "";
    private String book_now_date = "";
    private String current_time = "";
    private String booking_date="";
    private String instruction="";
    private LoadingDialog mLoadingDialog1;
    private FrameLayout custom_marker_view;
    String SselectedLocation = "";
    private LinearLayout booking_layout;
    private LinearLayout login_layout;
    private Button login_button;
    String ShouseNo = "";
    private static View view;
    private TextView selected_text;
    AlertDialog dialog;
    final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    public static boolean loading = false;
    public static Marker mark;
    public boolean taskerselect_status = false;
    GeometricProgressView progressView;
    private boolean refreshingmap=false;
    class receiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("com.refresh.map_page")) {
                taskerselect_status=false;
                if (mark != null) {
                    mark.hideInfoWindow();
                }
                if (!sub_category_id.equalsIgnoreCase("")) {
                    refreshingmap=true;
                    Sub_category_show_tasker(Iconstant.Map_boooking);
                }

                book_now_taskerid = "";
                book_now_taskid = "";
                available_service.setVisibility(View.GONE);
                selected_text.setText("Service not available");
            }
        }
    }

    receiver receive;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.activity_fragment__map__home__page, container, false);
        } catch (InflateException e) {
        }
        context = getActivity();
        initializeHeaderBar(view);
        initialize(view);
        initializeMap();

        Rl_drawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationDrawer.openDrawer();
            }
        });

        layout_address_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sessionManager.isLoggedIn()) {
                    search_status = 0;
                    Intent intent = new Intent(getActivity(), Map_Location_Search.class);
                    startActivityForResult(intent, placeSearch_request_code);
                    getActivity().overridePendingTransition(R.anim.slideup, R.anim.slidedown);
                } else {
                    alert(getResources().getString(R.string.action_select_tasker), getResources().getString(R.string.login_alert));
                }
            }
        });
        currentLocation_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cd = new ConnectionDetector(getActivity());
                isInternetPresent = cd.isConnectingToInternet();
                gps = new GPSTracker(getActivity());

                if (gps.canGetLocation() && gps.isgpsenabled()) {

                    MyCurrent_lat = gps.getLatitude();
                    MyCurrent_long = gps.getLongitude();

                    CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(MyCurrent_lat, MyCurrent_long)).zoom(17).build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                } else {
                    enableGpsService();
                }
            }
        });

        if (CheckPlayService()) {

            System.out.println("isCameraChangeListener3" + " " + isCameraChangeListener);
            //  googleMap.setOnCameraChangeListener(mOnCameraChangeListener);
            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    String tittle = marker.getTitle();
                    return true;
                }
            });

        } else {
        }

        //---------------------------------Main category listview click-----------------------------------------

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {


                if (sessionManager.isLoggedIn()) {

                    sub_category_id = "";
                    for (int i = 0; i < catItemList.size(); i++) {
                        if (position == i) {
                            catItemList.get(position).setcategorySelected(true);

                        } else {
                            catItemList.get(i).setcategorySelected(false);
                        }

                    }
                    if (catItemList.size() > 0) {
                        category_id = catItemList.get(position).getCat_id();
                        category_name = catItemList.get(position).getCat_name();
                        category_image = catItemList.get(position).getCat_image();
                    }

                    if (isInternetPresent) {

                        postDisplayMainCategory_DetailRequest(getActivity(), Iconstant.Categories_Detail_Url);
                    } else {
                        alert(getResources().getString(R.string.action_no_internet_title), getResources().getString(R.string.action_no_internet_message));
                    }
                    adapter.notifyDataSetChanged();

                } else {

                    alert(getResources().getString(R.string.action_select_tasker), getResources().getString(R.string.login_alert));
                }

            }
        });

        //---------------------------------Sub category listview click-----------------------------------------

        subcategory_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                refreshingmap=false;
                for (int i = 0; i < sub_catItemList.size(); i++) {
                    if (position == i) {
                        sub_catItemList.get(position).setcategorySelected(true);

                    } else {
                        sub_catItemList.get(i).setcategorySelected(false);
                    }

                }
                sub_adapter.notifyDataSetChanged();
                sub_category_id = sub_catItemList.get(position).getCat_id();

                if (isInternetPresent) {
                    Sub_category_show_tasker(Iconstant.Map_boooking);
                } else {
                    alert(getResources().getString(R.string.action_no_internet_title), getResources().getString(R.string.action_no_internet_message));
                }

            }
        });

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), LogInPage.class);
                intent.putExtra("IntentClass", "1");
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        //---------------------------------------------marker info window click---------------------------
        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
            }
        });

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker arg0) {
                System.out.println("Markerid" + arg0.getId());

                if (!arg0.getTitle().equalsIgnoreCase("Location")) {
                    HashMap<String, String> marker_data = extraMarkerInfo.get(arg0.getId());
                    if (!marker_data.equals(null)) {
                        mini_cost = marker_data.get(TAG_minicost);
                        hour_cost = marker_data.get(TAG_hourlycost);
                        user_image = marker_data.get(TAG_url_image);
                        user_name = marker_data.get(TAG_Location);
                        Tasker_id = marker_data.get(TAG_tasker_id);
                        Tasker_rating = marker_data.get(TAG_rating);
                        Tasker_lat = marker_data.get(TAG_latitude);
                        Tasker_long = marker_data.get(TAG_longitude);
                        tasker_address = marker_data.get(TAG_tasker_address);

                        if (category_id.equalsIgnoreCase("")) {
                            alert(getResources().getString(R.string.action_select_tasker), getResources().getString(R.string.select_category));
                        } else if (sub_category_id.equalsIgnoreCase("")) {
                            alert(getResources().getString(R.string.action_select_tasker), getResources().getString(R.string.select_subcategory));
                        } else {
                            moreAddressDialog(mini_cost, hour_cost, user_image, user_name, Tasker_id, Tasker_rating, Tasker_lat, Tasker_long, Str_Taskid, tasker_address, arg0);
                        }

                    }
                }

                return true;
            }
        });

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng arg0) {
                if (mark != null) {
                    if (taskerselect_status) {
                        mark.showInfoWindow();
                    }

                } else {
                    if (!taskerselect_status) {
                        if (mark != null) {
                            mark.hideInfoWindow();
                        }

                    }

                }

            }

        });

        book_later.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (category_id.equalsIgnoreCase("")) {
                    alert(getResources().getString(R.string.action_select_main_category), getResources().getString(R.string.action_select_main_category_message));
                } else if (sub_category_id.equalsIgnoreCase("")) {
                    alert(getResources().getString(R.string.action_select_Sub_category), getResources().getString(R.string.action_select_sub_category_message));
                } else {
                    Intent i = new Intent(getActivity(), NewAppointmentpage.class);
                    i.putExtra("location",SselectedLocation);
                    i.putExtra("city",city);
                    i.putExtra("state",state);
                    i.putExtra("country",country);
                    i.putExtra("postalcode",postalCode);
                    i.putExtra("latitude",Current_lat);
                    i.putExtra("longintude",Current_long);
                    i.putExtra("IntentCategoryID", sub_category_id);
                    i.putExtra("IntentServiceID", category_id);
                    startActivity(i);
                }


            }
        });

        book_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (category_id.equalsIgnoreCase("")) {
                    alert(getResources().getString(R.string.action_select_main_category), getResources().getString(R.string.action_select_main_category_message));
                } else if (sub_category_id.equalsIgnoreCase("")) {
                    alert(getResources().getString(R.string.action_select_Sub_category), getResources().getString(R.string.action_select_sub_category_message));
                } else if (book_now_taskerid.equalsIgnoreCase("") && book_now_taskid.equalsIgnoreCase("")) {
                    alert(getResources().getString(R.string.action_select_task_id), getResources().getString(R.string.action_message_booking_alert));
                } else {

                    ConfirmBookingAlert();

                }
            }
        });
        return view;
    }


    private void initializeHeaderBar(View rootView) {
        RelativeLayout headerBar = (RelativeLayout) rootView.findViewById(R.id.headerBar_noShadow_layout);
        Rl_drawer = (RelativeLayout) headerBar.findViewById(R.id.headerBar_noShadow_left_layout);
        Im_drawerIcon = (ImageView) headerBar.findViewById(R.id.headerBar_noShadow_imageView);
        Tv_headerTitle = (TextView) headerBar.findViewById(R.id.headerBar_noShadow_title_textView);

        Tv_headerTitle.setText(getResources().getString(R.string.homepage_label_title));
        Im_drawerIcon.setImageResource(R.drawable.drawer_icon);
    }

    private void initialize(View rootview) {
        mCustomMarkerView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_custom_marker, null);
        mMarkerImageView = (ImageView) mCustomMarkerView.findViewById(R.id.profile_image);
        custom_marker_view = (FrameLayout) mCustomMarkerView.findViewById(R.id.custom_marker_view);
        gps = new GPSTracker(getActivity());
        cd = new ConnectionDetector(getActivity());
        isInternetPresent = cd.isConnectingToInternet();
        mRequest = new ServiceRequest(getActivity());
        catItemList = new ArrayList<CategoryPojo>();
        cityItemList = new ArrayList<CitySelectionPojo>();
        sub_catItemList = new ArrayList<CategoryDetailPojo>();
        socketHandler = SocketHandler.getInstance(getActivity());
        sessionManager = new SessionManager(getActivity());
        HashMap<String, String> user = sessionManager.getUserDetails();
        UserID = user.get(SessionManager.KEY_USER_ID);
        progressView = (GeometricProgressView)rootview.findViewById(R.id.progressView);
        layout_address_text = (LinearLayout) rootview.findViewById(R.id.map_layout_address_text);
        book_later = (Button) rootview.findViewById(R.id.book_later);
        book_now = (Button) rootview.findViewById(R.id.book_now);
        booking_layout = (LinearLayout) rootview.findViewById(R.id.booking_layout);
        login_layout = (LinearLayout) rootview.findViewById(R.id.login_layout);
        login_button = (Button) rootview.findViewById(R.id.login_button);
        available_service = (RelativeLayout) rootview.findViewById(R.id.available_service);
        textview_address_text = (TextView) rootview.findViewById(R.id.map_address_text);
        center_marker = (ImageView) rootview.findViewById(R.id.book_my_ride_center_marker);
        currentLocation_image = (ImageView) rootview.findViewById(R.id.book_current_location_imageview);
        listView = (HorizontalListView) rootview.findViewById(R.id.horizontal_listview);
        subcategory_listview = (HorizontalListView) rootview.findViewById(R.id.horizontal_subcategory_listview);
        subcategory_layout = (RelativeLayout) rootview.findViewById(R.id.subcategory_layout);
        selected_text = (TextView) rootview.findViewById(R.id.selected_text);
        HashMap<String, String> location = sessionManager.getLocationDetails();
        Str_SelectedCity_Id = location.get(SessionManager.KEY_LOCATION_ID);
        Str_SelectedCity_Name = location.get(SessionManager.KEY_LOCATION_NAME);
        subcategory_layout.setVisibility(View.GONE);
        if (sessionManager.isLoggedIn()) {
            gps = new GPSTracker(getActivity());
            if (gps.isgpsenabled() && gps.canGetLocation()) {
                booking_layout.setVisibility(View.VISIBLE);
            } else {
                enableGpsService();
            }
        } else {
            login_layout.setVisibility(View.VISIBLE);
            available_service.setBackgroundColor(Color.parseColor("#000000"));
            selected_text.setText("Please Login");
        }

        if (isInternetPresent) {
            postCategoryRequest(getActivity(), Iconstant.CategoriesUrl);
        } else {
            alert(getResources().getString(R.string.action_no_internet_title), getResources().getString(R.string.action_no_internet_message));
        }

        receive = new receiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.refresh.map_page");
        getActivity().registerReceiver(receive, filter);

    }


//-----------------------------------Google Map Initialize-------------------------------------------------------------------

    private void initializeMap() {
        if (googleMap == null) {
            googleMap = ((MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.book_my_ride_mapview)).getMap();
            // check if map is created successfully or not
            if (googleMap == null) {
                //  Toast.makeText(getActivity(), "Sorry! unable to create maps", Toast.LENGTH_SHORT).show();
            }
        }
        if (CheckPlayService()) {
            // Changing map type
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            //    googleMap.setTrafficEnabled(true);
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setZoomControlsEnabled(false);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            googleMap.getUiSettings().setCompassEnabled(false);
            googleMap.getUiSettings().setRotateGesturesEnabled(true);
            googleMap.getUiSettings().setZoomGesturesEnabled(true);
            googleMap.getUiSettings().setAllGesturesEnabled(true);
            if (gps.canGetLocation() && gps.isgpsenabled()) {
                double Dlatitude = gps.getLatitude();
                double Dlongitude = gps.getLongitude();
                MyCurrent_lat = Dlatitude;
                MyCurrent_long = Dlongitude;
                Current_lat = String.valueOf(MyCurrent_lat);
                Current_long = String.valueOf(MyCurrent_long);
                SselectedLatitude = String.valueOf(MyCurrent_lat);
                SselectedLongitude = String.valueOf(MyCurrent_long);
                SselectedLocation = getAddress(MyCurrent_lat, MyCurrent_long);
                textview_address_text.setText(SselectedLocation);

                if (isCameraChangeListener == true) {
                    System.out.println("isCameraChangeListener current " + " " + isCameraChangeListener);
                    Recent_lat = Dlatitude;
                    Recent_long = Dlongitude;
                }
                CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(Dlatitude, Dlongitude)).zoom(17).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            } else {
                System.out.println("isCameraChangeListener3" + " " + isCameraChangeListener);

                enableGpsService();
            }


        } else {

            final PkDialog mDialog = new PkDialog(getActivity());
            mDialog.setDialogTitle(getActivity().getResources().getString(R.string.alert_label_title));
            mDialog.setDialogMessage(getActivity().getResources().getString(R.string.action_unable_to_create_map));
            mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialog.dismiss();
                    getActivity().finish();
                }
            });
            mDialog.show();
        }
    }

    //------Alert Method-----
    private void alert(String title, String message) {
        final PkDialog mDialog = new PkDialog(getActivity());
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

    public void SlideToAbove() {
        Animation slide = null;
        slide = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, -5.0f);

        slide.setDuration(400);
        slide.setFillAfter(true);
        slide.setFillEnabled(true);
        subcategory_layout.startAnimation(slide);

        slide.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                subcategory_layout.clearAnimation();

                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                        subcategory_layout.getWidth(), subcategory_layout.getHeight());
                // lp.setMargins(0, 0, 0, 0);
                lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                subcategory_layout.setLayoutParams(lp);

            }

        });

    }

    public void SlideToDown() {
        Animation slide = null;
        slide = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 5.2f);

        slide.setDuration(400);
        slide.setFillAfter(true);
        slide.setFillEnabled(true);
        subcategory_layout.startAnimation(slide);

        slide.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                subcategory_layout.clearAnimation();

                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                        subcategory_layout.getWidth(), subcategory_layout.getHeight());
                lp.setMargins(0, subcategory_layout.getWidth(), 0, 0);
                lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                subcategory_layout.setLayoutParams(lp);

            }

        });

    }


    //------------------------------------------------------------Color Alert Dialog-------------------

    private void colorAlert() {
        DisplayMetrics metrics = getActivity().getResources().getDisplayMetrics();
        int screenWidth = (int) (metrics.widthPixels * 0.80);//fill only 80% of the screen
        moreAddressView = View.inflate(getActivity(), R.layout.maptasker_show_layout, null);
        ColorDialog dialog = new ColorDialog(getActivity());

        dialog.setContentView(moreAddressView);
        dialog.getWindow().setLayout(screenWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setPositiveListener("CHAT", new ColorDialog.OnPositiveListener() {
            @Override
            public void onClick(ColorDialog dialog) {
                Toast.makeText(getActivity(), dialog.getPositiveText().toString(), Toast.LENGTH_SHORT).show();
            }
        })
                .setNegativeListener("VIEW DETAILS", new ColorDialog.OnNegativeListener() {
                    @Override
                    public void onClick(ColorDialog dialog) {
                        Toast.makeText(getActivity(), dialog.getNegativeText().toString(), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }).show();
    }

    //------------------------------------------------------------Confirm Booking Alert-----------------------------------------------

    public void ConfirmBookingAlert() {
        DisplayMetrics metrics = getActivity().getResources().getDisplayMetrics();
        int screenWidth = (int) (metrics.widthPixels * 0.80);//fill only 80% of the screen
        moreAddressView = View.inflate(getActivity(), R.layout.book_now_alert_map, null);
        moreAddressDialog = new Dialog(getActivity());
        moreAddressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        moreAddressDialog.setContentView(moreAddressView);
        moreAddressDialog.setCanceledOnTouchOutside(false);
        moreAddressDialog.getWindow().setLayout(screenWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        moreAddressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView taskername = (TextView) moreAddressView.findViewById(R.id.tasker_name);
        final TextView bookingtime = (TextView) moreAddressView.findViewById(R.id.booking_time);
        final TextView bookingdate = (TextView) moreAddressView.findViewById(R.id.booking_date);
        RelativeLayout confirm_book = (RelativeLayout) moreAddressView.findViewById(R.id.confirm_book);
        RelativeLayout cancel_book = (RelativeLayout) moreAddressView.findViewById(R.id.cancel_book);
        final EditText instructions = (EditText) moreAddressView.findViewById(R.id.booking_page_instruction_editText);
        taskername.setText(book_now_taskername);
        bookingtime.setText(getCurrentTime());
        bookingdate.setText(getCurrentDate());
        booking_date=bookingdate.getText().toString();
        current_time = getCurrentTimes();

        confirm_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (instructions.getText().toString().length() == 0) {
                    alert(getResources().getString(R.string.instruction_header), getResources().getString(R.string.instruction));
                } else {

                   if (isInternetPresent) {
                       instruction=instructions.getText().toString();
                       Book_job_Request(getActivity(), book_now_taskerid, booking_date, instruction, current_time);
                    } else {
                        alert(getResources().getString(R.string.action_no_internet_title), getResources().getString(R.string.action_no_internet_message));
                    }
                    moreAddressDialog.dismiss();
                }
            }
        });

        cancel_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moreAddressDialog.dismiss();
            }
        });
        moreAddressDialog.show();

    }

    //-----------------------------------------------------------Map Tasker Alert Show----------------------------------------
    private void moreAddressDialog(final String mini_cost, final String hour_cost, String image, final String user_name, final String tasker_id, final String tasker_rating, final String latitude, final String longitude, final String task_id, String tasker_address, final Marker arg0) {
        //--------Adjusting Dialog width-----

        mark = arg0;
        DisplayMetrics metrics = getActivity().getResources().getDisplayMetrics();
        int screenWidth = (int) (metrics.widthPixels * 0.80);
        moreAddressView = View.inflate(getActivity(), R.layout.map_tasker_select, null);
        moreAddressDialog = new Dialog(getActivity());
        moreAddressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        moreAddressDialog.setContentView(moreAddressView);
        moreAddressDialog.setCanceledOnTouchOutside(false);
        moreAddressDialog.setCancelable(false);
        moreAddressDialog.getWindow().setLayout(screenWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        moreAddressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView minicost = (TextView) moreAddressView.findViewById(R.id.mini_cost);
        TextView hourcost = (TextView) moreAddressView.findViewById(R.id.hour_cost);
        TextView username = (TextView) moreAddressView.findViewById(R.id.user_name);
        ImageView userimage = (ImageView) moreAddressView.findViewById(R.id.user_image);
        TextView address = (TextView) moreAddressView.findViewById(R.id.tasker_address);
        ImageView tasker_close = (ImageView) moreAddressView.findViewById(R.id.tasker_close);
        ImageView tasker_select = (ImageView) moreAddressView.findViewById(R.id.tasker_select);
        RelativeLayout chat = (RelativeLayout) moreAddressView.findViewById(R.id.chat);
        RelativeLayout detail = (RelativeLayout) moreAddressView.findViewById(R.id.tasker_det);
        TextView chat_text = (TextView) moreAddressView.findViewById(R.id.chat_text);
        TextView detail_text = (TextView) moreAddressView.findViewById(R.id.detail_text);
        TextView select_tasker = (TextView) moreAddressView.findViewById(R.id.select_tasker);
        TextView unselect_tasker = (TextView) moreAddressView.findViewById(R.id.unselect_tasker);
        RatingBar rating = (RatingBar) moreAddressView.findViewById(R.id.rating_image);
        sessionManager.setminimum_amount(mini_cost);
        sessionManager.sethourly_amount(hour_cost);
        if (sub_category_id.equalsIgnoreCase("")) {
            minicost.setVisibility(View.GONE);
        } else {
            minicost.setVisibility(View.VISIBLE);
        }
        minicost.setText(" Hourly Cost  : " + " " + "$" + hour_cost);
//        hourcost.setText("Hour_Cost : "+ " " + "$"+ hour_cost);
        username.setText(user_name);
        Picasso.with(getActivity()).load(image).error(R.drawable.placeholder_icon)
                .placeholder(R.drawable.placeholder_icon).memoryPolicy(MemoryPolicy.NO_CACHE).into(userimage);
        Double lati = Double.valueOf(latitude);
        Double longi = Double.valueOf(longitude);
        address.setText(tasker_address);
        rating.setRating(Float.parseFloat(tasker_rating));


        if (!sub_category_id.equalsIgnoreCase("")) {
            tasker_select.setVisibility(View.VISIBLE);
            select_tasker.setVisibility(View.VISIBLE);
            unselect_tasker.setText("Unselect");
            chat.setEnabled(true);
            detail.setEnabled(true);
            chat_text.setTextColor(Color.parseColor("#526CA5"));
            detail_text.setTextColor(Color.parseColor("#526CA5"));
        } else {
            tasker_select.setVisibility(View.GONE);
            select_tasker.setVisibility(View.GONE);
            unselect_tasker.setText("Close");
            chat.setEnabled(false);
            detail.setEnabled(false);
            chat_text.setTextColor(Color.parseColor("#DCDCDC"));
            detail_text.setTextColor(Color.parseColor("#DCDCDC"));
        }


        tasker_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                taskerselect_status = false;
                if (!book_now_taskerid.equalsIgnoreCase("") && !book_now_taskid.equalsIgnoreCase("")) {
                    if (book_now_taskid.equalsIgnoreCase(task_id) && book_now_taskerid.equalsIgnoreCase(tasker_id)) {
                        book_now_taskid = "";
                        book_now_taskerid = "";
                        arg0.setTitle("");
                        arg0.hideInfoWindow();
                        available_service.setVisibility(View.GONE);
                        selected_text.setText("Service not available");
                        moreAddressDialog.dismiss();
                    } else {
                        book_now_taskid = "";
                        book_now_taskerid = "";
                        moreAddressDialog.dismiss();

                    }
                } else {
                    book_now_taskid = "";
                    book_now_taskerid = "";
                    moreAddressDialog.dismiss();
                    available_service.setVisibility(View.GONE);
                    selected_text.setText("Service not available");
                }

            }
        });
        tasker_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                taskerselect_status = true;
                book_now_taskid = task_id;
                book_now_taskerid = tasker_id;
                book_now_taskername = user_name;
                //  available_service.setVisibility(View.VISIBLE);
                available_service.setBackgroundColor(Color.parseColor("#526CA5"));
                markershow(arg0, user_name);
                if (mark != null) {
                    mark.showInfoWindow();
                } else {
                    mark.hideInfoWindow();
                }
                moreAddressDialog.dismiss();

            }
        });

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChatMessageService.tasker_id = "";
                ChatMessageService.task_id = "";
                Intent chat = new Intent(getActivity(), ChatPage.class);
                chat.putExtra("TaskerId", tasker_id);
                chat.putExtra("TaskId", task_id);
                startActivity(chat);
                moreAddressDialog.dismiss();
            }
        });

        detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), PartnerProfilePage.class);
                sessionManager.putProvideID(tasker_id);
                sessionManager.putProvideScreenType(PROVIDER);
                i.putExtra("userid", UserID);
                i.putExtra("task_id", task_id);
                i.putExtra("address", Saddress1);
                i.putExtra("taskerid", tasker_id);
                i.putExtra("minimumamount", "$" + minimum_amount);
                i.putExtra("hourlyamount", "$" + hour_cost);
                i.putExtra("Page", "map_page");
                i.putExtra("lat", Current_lat);
                i.putExtra("long", Current_long);
                i.putExtra("location", SselectedLocation);
                i.putExtra("city", city);
                i.putExtra("state", state);
                i.putExtra("postalcode", postalCode);
                startActivity(i);
                moreAddressDialog.dismiss();
            }
        });


        moreAddressDialog.show();

    }

    private void markershow(Marker arg1, final String user_name) {
        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            // Use default InfoWindow frame
            @Override
            public View getInfoWindow(Marker arg1) {
                View v = View.inflate(getActivity(), R.layout.taskerselect_window_show, null);
                if (arg1 != null) {

                    RelativeLayout layout = (RelativeLayout) v.findViewById(R.id.available_tasker);
                    TextView select_tasker = (TextView) v.findViewById(R.id.selected_tasker_text);
                    select_tasker.setText("You have selected " + user_name);
                }

                return v;

            }

            // Defines the contents of the InfoWindow
            @Override
            public View getInfoContents(Marker arg0) {

                return null;

            }
        });
    }


    //---------------------------------------------------------------------Current date Time------------------------------------------------------------------

    private String getCurrentDate() {
        String aCurrentDateStr = "";
        try {
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
            aCurrentDateStr = df.format(c.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return aCurrentDateStr;
    }


    private String getCurrentTime() {
        String currenttime = "";
        try {
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("hh:mm a");
            currenttime = df.format(c.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return currenttime;
    }

    private String getCurrentTimes() {
        String currenttime = "";
        try {
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("HH:mm");
            currenttime = df.format(c.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return currenttime;
    }
    //----------------------------------------------------------------------Book_now-----------------------------------------------------------

    public void Book_job_Request(Context mContext, final String TaskerId, String date, String instruction, String time) {

        mLoadingDialog1 = new LoadingDialog(mContext);
        mLoadingDialog1.setLoadingTitle(getResources().getString(R.string.action_processing));
        mLoadingDialog1.show();

        System.out.println("-----------user_id------------------" + UserID);
        System.out.println("-----------taskid------------------" + book_now_taskid);
        System.out.println("-----------taskerid------------------" + book_now_taskerid);
        System.out.println("-----------location------------------" + Saddress1);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", UserID);
        jsonParams.put("taskid", book_now_taskid);
        jsonParams.put("taskerid", book_now_taskerid);
        jsonParams.put("location", "");
        jsonParams.put("instruction", instruction);
        jsonParams.put("pickup_date", date);
        jsonParams.put("pickup_time", time);
        //map insert =====
        jsonParams.put("locality", SselectedLocation);
        jsonParams.put("street", SselectedLocation);
        jsonParams.put("landmark", "");
        jsonParams.put("city", city);
        jsonParams.put("state", state);
        jsonParams.put("zipcode", postalCode);
        jsonParams.put("lat", SselectedLatitude);
        jsonParams.put("lng", SselectedLongitude);
        jsonParams.put("country",country);
        mRequest = new ServiceRequest(mContext);
        mRequest.makeServiceRequest(Iconstant.MapuserBooking, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
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
                            if (responseObject.has("description")) {
                                sDescription = responseObject.getString("description");
                            }

                            sServiceType = responseObject.getString("service_type");
                            sNote = responseObject.getString("note");
                            sBookingDate = responseObject.getString("booking_date");
                            sJobDate = responseObject.getString("job_date");

                            Intent intent = new Intent(getActivity(), AppointmentConfirmationPage.class);
                            intent.putExtra("IntentJobID", sJobId);
                            intent.putExtra("IntentMessage", sMessage);
                            intent.putExtra("IntentOrderDate", sBookingDate);
                            intent.putExtra("IntentJobDate", sJobDate);
                            intent.putExtra("IntentDescription", sDescription);
                            intent.putExtra("IntentServiceType", sServiceType);
                            intent.putExtra("IntentNote", sNote);
                            startActivity(intent);
                            getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
                        }
                    } else {
                        sResponse = object.getString("response");
                        alert(getResources().getString(R.string.action_sorry), sResponse);
                    }


                    if (sStatus.equalsIgnoreCase("1")) {
                        System.out.println("---------provider list TaskerId id--------" + TaskerId);

                        sessionManager = new SessionManager(getActivity());

                        sessionManager.setjobid(sJobId);
                        HashMap<String, String> task = sessionManager.getSocketTaskId();
                        String sTask = task.get(SessionManager.KEY_TASK_ID);

                        if (sTask != null && sTask.length() > 0) {
                            if (!sTask.equalsIgnoreCase(TaskerId)) {
                                sessionManager.setSocketTaskId(TaskerId);
                                System.out.println("---------Room Switched--------" + TaskerId);
                                SocketHandler.getInstance(context).getSocketManager().createSwitchRoom(TaskerId);
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


    //-------------Common Main_Category Post Request---------------
    private void postCategoryRequest(Context mContext, String url) {
        loading = false;
        startLoading();


        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("location_id", Str_SelectedCity_Id);

        System.out.println("---------Category location_id------------" + Str_SelectedCity_Id);

        System.out.println("---------Category url------------" + url);

        mRequest.makeServiceRequest(url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("---------Category response------------" + response);

                String Str_status = "";
                try {
                    JSONObject object = new JSONObject(response);
                    Str_status = object.getString("status");

                    if (Str_status.equalsIgnoreCase("1")) {
                        JSONObject response_Object = object.getJSONObject("response");
                        if (response_Object.length() > 0) {
                            Object check_category_object = response_Object.get("category");
                            if (check_category_object instanceof JSONArray) {

                                JSONArray cat_Array = response_Object.getJSONArray("category");
                                if (cat_Array.length() > 0) {
                                    catItemList.clear();
                                    for (int i = 0; i < cat_Array.length(); i++) {
                                        JSONObject cat_Object = cat_Array.getJSONObject(i);
                                        CategoryPojo pojo = new CategoryPojo();

                                        pojo.setCat_id(cat_Object.getString("cat_id"));
                                        main_category_id = cat_Object.getString("cat_id");
                                        pojo.setCat_name(cat_Object.getString("cat_name"));
                                        pojo.setCat_image(cat_Object.getString("inactive_icon"));
                                        pojo.setIcon_normal(cat_Object.getString("active_icon"));
                                        pojo.setHasChild(cat_Object.getString("hasChild"));
                                        pojo.setCheck_mark("unchecked");

                                        catItemList.add(pojo);
                                    }
                                    asCategory = true;
                                } else {
                                    asCategory = false;
                                }
                            } else {
                                asCategory = false;
                            }


                            Object check_locations_object = response_Object.get("locations");
                            if (check_locations_object instanceof JSONArray) {
                                JSONArray location_Array = response_Object.getJSONArray("locations");
                                if (location_Array.length() > 0) {
                                    cityItemList.clear();
                                    for (int i = 0; i < location_Array.length(); i++) {
                                        JSONObject location_Object = location_Array.getJSONObject(i);
                                        CitySelectionPojo pojo = new CitySelectionPojo();
                                        pojo.setLocationId(location_Object.getString("id"));
                                        pojo.setLocationName(location_Object.getString("city"));

                                        cityItemList.add(pojo);
                                    }
                                    asLocation = true;
                                } else {
                                    asLocation = false;
                                }
                            } else {
                                asLocation = false;
                            }
                        }
                    }


                    if (Str_status.equalsIgnoreCase("1")) {

                        //Adding location to session
                        sessionManager.createLocationSession(Str_SelectedCity_Id, Str_SelectedCity_Name);

                        if (asCategory) {
                            adapter = new Map_main_category_adapter(getActivity(), catItemList);
                            listView.setAdapter(adapter);
                        }
                        postProvidersRequest(Iconstant.Map_boooking);


                        if (asLocation) {
                            //Tv_selectedCity.setText(Str_SelectedCity_Name);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                stopLoading();

            }

            @Override
            public void onErrorListener() {
                stopLoading();

            }
        });
    }


    //-------------Main Category Click Post Request---------------
    private void postDisplayMainCategory_DetailRequest(Context mContext, String url) {

        dialog = new SpotsDialog(context, R.style.Custom);
        dialog.show();

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("category", category_id);
        jsonParams.put("location_id", Str_SelectedCity_Id);

        System.out.println("---------Category Detail url------------" + url);

        mRequest.makeServiceRequest(url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("---------Category Detail response------------" + response);

                String Str_status = "";
                try {
                    JSONObject object = new JSONObject(response);
                    Str_status = object.getString("status");

                    if (Str_status.equalsIgnoreCase("1")) {
                        subcategory_layout.setVisibility(View.VISIBLE);
                        JSONObject response_Object = object.getJSONObject("response");
                        if (response_Object.length() > 0) {
                            Object check_category_object = response_Object.get("category");
                            if (check_category_object instanceof JSONArray) {

                                JSONArray cat_Array = response_Object.getJSONArray("category");
                                if (cat_Array.length() > 0) {
                                    sub_catItemList.clear();
                                    for (int i = 0; i < cat_Array.length(); i++) {
                                        JSONObject cat_Object = cat_Array.getJSONObject(i);
                                        CategoryDetailPojo pojo = new CategoryDetailPojo();

                                        pojo.setCat_id(cat_Object.getString("cat_id"));
                                        pojo.setCat_name(cat_Object.getString("cat_name"));
                                        pojo.setCat_image(cat_Object.getString("inactive_icon"));
                                        pojo.setIcon_normal(cat_Object.getString("active_icon"));
                                        pojo.setHasChild(cat_Object.getString("hasChild"));

                                        sub_catItemList.add(pojo);
                                    }
                                    asCategory = true;
                                } else {
                                    asCategory = false;
                                }
                            } else {
                                asCategory = false;
                            }
                        }
                    } else {
                        // SlideToDown();

                        subcategory_layout.setVisibility(View.GONE);
                    }

                    if (Str_status.equalsIgnoreCase("1")) {
                        if (asCategory) {
                            sub_adapter = new Map_sub_category_adapter(getActivity(), sub_catItemList);
                            subcategory_listview.setAdapter(sub_adapter);

                            // SlideToAbove();
                        }
                        Main_category_show_tasker(Iconstant.Map_boooking);
                    } else {

                        Main_category_show_tasker(Iconstant.Map_boooking);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                dialog.dismiss();
            }

            @Override
            public void onErrorListener() {
                dialog.dismiss();
            }
        });
    }

    //--------------------------------------------------------Common Map Tasker lIst--------------------------------------------------------------------

    public void postProvidersRequest(String url) {

        if (loading) {
            startLoading();
        }

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", UserID);
        jsonParams.put("lat", Current_lat);
        jsonParams.put("long", Current_long);
        mRequest.makeServiceRequest(url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("respionse--------------" + response);
                String Str_status = "", Str_response = "", Scurrency = "";
                try {

                    JSONObject object = new JSONObject(response);
                    Str_status = object.getString("status");

                    if (Str_status.equalsIgnoreCase("1")) {

//                        Str_bookingId = object.getString("booking_id");
//                        Str_Taskid = object.getString("task_id");
                        markersArray.clear();
                        JSONArray jarry = object.getJSONArray("response");
                        if (jarry.length() > 0) {
                            for (int i = 0; i < jarry.length(); i++) {
                                JSONObject jobject = jarry.getJSONObject(i);
                                ProvidersListPojo pojo = new ProvidersListPojo();
                                MarkerData mark = new MarkerData();

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
                                minimum_amount = jobject.getString("min_amount");
                                hourly_amount = jobject.getString("hourly_amount");
                                markersArray.add(mark);
                            }
                            sessionManager.setminimum_amount(minimum_amount);
                            sessionManager.sethourly_amount(hourly_amount);
                            sessionManager.settaskid(Str_Taskid);
                            available_service.setVisibility(View.GONE);
                            Addmarker();

                        } else {

                        }
                    } else {
                        Str_response = object.getString("response");
                        googleMap.clear();
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(Double.parseDouble(Current_lat), Double.parseDouble(Current_long))).zoom(17).build();
                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        center_marker.setVisibility(View.VISIBLE);
                        available_service.setVisibility(View.VISIBLE);
                        available_service.setBackgroundColor(Color.parseColor("#000000"));
                        selected_text.setText("Service not available");
                        book_now_taskerid = "";
                        book_now_taskid = "";
                    }

                } catch (JSONException e) {
                    e.printStackTrace();

                }
                if (loading) {
                    stopLoading();
                }


            }

            @Override
            public void onErrorListener() {
                stopLoading();
            }
        });

    }


    //--------------------------------------------------------Main_category_Wise_Show_taskerlist--------------------------------------------------------------------

    public void Main_category_show_tasker(String url) {

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", UserID);
        jsonParams.put("lat", Current_lat);
        jsonParams.put("long", Current_long);
        jsonParams.put("maincategory", category_id);
        mRequest.makeServiceRequest(url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("respionse--------------" + response);
                String Str_status = "", Str_response = "", Scurrency = "";
                try {

                    JSONObject object = new JSONObject(response);
                    Str_status = object.getString("status");

                    if (Str_status.equalsIgnoreCase("1")) {

//                        Str_bookingId = object.getString("booking_id");
//                        Str_Taskid = object.getString("task_id");
                        markersArray.clear();
                        JSONArray jarry = object.getJSONArray("response");
                        if (jarry.length() > 0) {
                            for (int i = 0; i < jarry.length(); i++) {
                                JSONObject jobject = jarry.getJSONObject(i);
                                ProvidersListPojo pojo = new ProvidersListPojo();
                                MarkerData mark = new MarkerData();

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
                                minimum_amount = jobject.getString("min_amount");
                                hourly_amount = jobject.getString("hourly_amount");
                                markersArray.add(mark);
                            }
                            sessionManager.setminimum_amount(minimum_amount);
                            sessionManager.sethourly_amount(hourly_amount);
                            sessionManager.settaskid(Str_Taskid);
                            available_service.setVisibility(View.GONE);
                            Addmarker();

                        } else {

                        }
                    } else {
                        Str_response = object.getString("response");
                        googleMap.clear();
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(Double.parseDouble(Current_lat), Double.parseDouble(Current_long))).zoom(17).build();
                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        center_marker.setVisibility(View.VISIBLE);
                        available_service.setVisibility(View.VISIBLE);
                        available_service.setBackgroundColor(Color.parseColor("#000000"));
                        selected_text.setText("Service not available");
                        book_now_taskerid = "";
                        book_now_taskid = "";
                    }

                } catch (JSONException e) {
                    e.printStackTrace();

                }


            }

            @Override
            public void onErrorListener() {

            }
        });

    }

    //--------------------------------------------------------Sub_category_Wise_Show_taskerlist--------------------------------------------------------------------

    public void Sub_category_show_tasker(String url) {

        if(refreshingmap){
            dialog = new SpotsDialog(context, R.style.Refresh);
            dialog.show();
        }
        else{
            dialog = new SpotsDialog(context, R.style.Custom);
            dialog.show();
        }

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", UserID);
        jsonParams.put("lat", Current_lat);
        jsonParams.put("long", Current_long);
        jsonParams.put("category", sub_category_id);
        mRequest.makeServiceRequest(url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("respionse--------------" + response);
                String Str_status = "", Str_response = "", Scurrency = "";
                try {

                    JSONObject object = new JSONObject(response);
                    Str_status = object.getString("status");
                    markersArray.clear();
                    book_now_taskerid = "";
                    book_now_taskid = "";
                    if (Str_status.equalsIgnoreCase("1")) {

                        Str_bookingId = object.getString("booking_id");
                        Str_Taskid = object.getString("task_id");
                        if (object.has("minimum_amount")) {
                            minimum_amount = object.getString("minimum_amount");
                        }

                        JSONArray jarry = object.getJSONArray("response");
                        if (jarry.length() > 0) {
                            for (int i = 0; i < jarry.length(); i++) {
                                JSONObject jobject = jarry.getJSONObject(i);
                                ProvidersListPojo pojo = new ProvidersListPojo();
                                MarkerData mark = new MarkerData();

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
                                if (jobject.has("worklocation")) {
                                    mark.setSetAddress(jobject.getString("worklocation"));
                                }

                                hourly_amount = jobject.getString("hourly_amount");
                                markersArray.add(mark);
                            }
//                            sessionManager.setminimum_amount(minimum_amount);
//                            sessionManager.sethourly_amount(hourly_amount);
                            sessionManager.settaskid(Str_Taskid);
                            available_service.setVisibility(View.GONE);
                            Addmarker();
                            dialog.dismiss();

                        } else {

                        }
                    } else {
                        Str_response = object.getString("response");
                        dialog.dismiss();
                        googleMap.clear();
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(Double.parseDouble(Current_lat), Double.parseDouble(Current_long))).zoom(17).build();
                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        center_marker.setVisibility(View.VISIBLE);
                        available_service.setVisibility(View.VISIBLE);
                        available_service.setBackgroundColor(Color.parseColor("#000000"));
                        selected_text.setText("Service not available");
                        book_now_taskerid = "";
                        book_now_taskid = "";

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    dialog.dismiss();
                }


            }

            @Override
            public void onErrorListener() {
                dialog.dismiss();
            }
        });

    }


//-------------------Loading start and stop-------------------------------------------------

    private void startLoading() {

        mLoadingDialog = new PkLoadingDialog(getActivity());
        mLoadingDialog.show();
    }

    private void stopLoading() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                mLoadingDialog.dismiss();

            }
        }, 500);
    }


    //-----------Check Google Play Service--------
    private boolean CheckPlayService() {
        final int connectionStatusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
        if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
            return false;
        }
        return true;
    }

    void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                final Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
                        connectionStatusCode, getActivity(), REQUEST_CODE_RECOVER_PLAY_SERVICES);
                if (dialog == null) {
                    Toast.makeText(getActivity(), "incompatible version of Google Play Services", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //Enabling Gps Service
    private void enableGpsService() {

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
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
                            status.startResolutionForResult(getActivity(), REQUEST_LOCATION);
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


    //------------------------------Map_icon_Tracking_Address------------------------------------------------

    GoogleMap.OnCameraChangeListener mOnCameraChangeListener = new GoogleMap.OnCameraChangeListener() {
        @Override
        public void onCameraChange(CameraPosition cameraPosition) {


            double latitude = cameraPosition.target.latitude;
            double longitude = cameraPosition.target.longitude;

            cd = new ConnectionDetector(getActivity());
            isInternetPresent = cd.isConnectingToInternet();

            Log.e("camerachange lat-->", "" + latitude);
            Log.e("on_camera_change lon-->", "" + longitude);

            if (latitude != 0.0) {
                googleMap.clear();


                if (isCameraChangeListener == true) {
                    Recent_lat = latitude;
                    Recent_long = longitude;
                    address = getAddress(Recent_lat, Recent_long);
                    textview_address_text.setText(address);
                    System.out.println("camerachangelistener" + " " + isCameraChangeListener);
                }

                if (isInternetPresent) {
//                    if (mRequest != null) {
//                        mRequest.cancelRequest();
//                    }


                    isLoading = true;

                } else {

                }
            }
        }
    };


    //-----------------------Submit Address Post Request-----------------
    private void submitAddressRequest(Context mContext, String Url) {

        System.out.println("-------------submitAddressRequest Url----------------" + Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", UserID);
        jsonParams.put("locality", SselectedLocation);
        jsonParams.put("street", SselectedLocation);
        jsonParams.put("landmark", "");
        jsonParams.put("city", city);
        jsonParams.put("state", state);
        jsonParams.put("zipcode", postalCode);
        jsonParams.put("lat", SselectedLatitude);
        jsonParams.put("lng", SselectedLongitude);
//        jsonParams.put("state", "");
//        jsonParams.put("line1", "chennai");

        System.out.println("" + SselectedLatitude);


        System.out.println("locality-----------" + UserID);
        System.out.println("street-----------" + "");
        System.out.println("landmark-----------" + "");
        System.out.println("city-----------" + city);

        System.out.println("zipcode-----------" + postalCode);

        System.out.println("lat-----------" + SselectedLatitude);
        System.out.println("lng-----------" + SselectedLongitude);


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


                    } else {
                        //  mLoadingDialog1.dismiss();
                        alert(getResources().getString(R.string.action_sorry), sResponse);
                    }
                } catch (JSONException e) {
                    // mLoadingDialog1.dismiss();
                    e.printStackTrace();
                }
            }

            @Override
            public void onErrorListener() {
                // mLoadingDialog1.dismiss();
            }
        });
    }

//-----------------------------------------------------Tasker Show in Map concept methods----------------------------------------------------

    public void Addmarker() {

        googleMap.clear();

        if (markersArray.size() != 0) {
            for (int i = 0; i < markersArray.size(); i++) {

                createMarker(markersArray.get(i).getLatitude(), markersArray.get(i).getLogintude(), markersArray.get(i).getTitle(), markersArray.get(i).getId(), markersArray.get(i).getimageurl(), markersArray.get(i).getMini_cost(), markersArray.get(i).getHourly_cost(), markersArray.get(i).getTaskerId(), markersArray.get(i).getRating(), markersArray.get(i).getimageurl(), markersArray.get(i).getSetAddress());

                HashMap<String, String> map = new HashMap<String, String>();
                map.put("lat", markersArray.get(i).getLatitude());
                HashMap<String, String> map1 = new HashMap<String, String>();
                map1.put("lng", markersArray.get(i).getLogintude());
                latarray.add(map);
                lngarray.add(map1);

            }

        }

    }

    protected void createMarker(final String latitude, final String longitude, final String title, final String id, final String urlimage, final String mini_cost, final String hourly_cost, final String tasker_id, final String rating, final String category_image, final String address) {

        final List<Marker> markersList = new ArrayList<Marker>();

        final double lat = Double.parseDouble(latitude);
        final double longi = Double.parseDouble(longitude);


        try {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {

                    Glide.with(getActivity()).
                            load(category_image)
                            .asBitmap()
                            .fitCenter()
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                                    marker = googleMap.addMarker(new MarkerOptions()
                                            .position(new LatLng(lat, longi))
                                            .title(title)
                                            .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(mCustomMarkerView, bitmap))));
                                    markersList.add(marker);
                                    LatLngBounds.Builder builder = new LatLngBounds.Builder();

                                    for (int i = 0; i < latarray.size(); i++) {
                                        String lat = latarray.get(i).get("lat");
                                        String lng = lngarray.get(i).get("lng");
                                        final double lati = Double.parseDouble(lat);
                                        final double longi = Double.parseDouble(lng);
                                        builder.include(new LatLng(lati, longi));
                                    }
                                    if (latarray.size() < 4) {
                                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(Current_lat), Double.parseDouble(Current_long)), 8.0f));
//                                        LatLngBounds bounds = builder.build();
//                                        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 162));
                                    } else {

                                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(Current_lat), Double.parseDouble(Current_long)), 10.0f));
                                    }

                                    center_marker.setVisibility(View.GONE);
                                    LatLng sydney = new LatLng(Double.parseDouble(Current_lat), Double.parseDouble(Current_long));
                                    googleMap.addMarker(new MarkerOptions().position(sydney).icon(BitmapDescriptorFactory.fromResource(R.drawable.map_custom_pin))
                                            .title("Location"));


                                    //  String address = getAddress(Double.parseDouble(latitude), Double.parseDouble(longitude));

                                    HashMap<String, String> data = new HashMap<String, String>();
                                    data.put(TAG_Location, title);
                                    data.put(TAG_minicost, mini_cost);
                                    data.put(TAG_hourlycost, hourly_cost);
                                    data.put(TAG_url_image, urlimage);
                                    data.put(TAG_tasker_id, tasker_id);
                                    data.put(TAG_rating, rating);
                                    data.put(TAG_latitude, latitude);
                                    data.put(TAG_longitude, longitude);
                                    data.put(TAG_tasker_address, address);
                                    extraMarkerInfo.put(marker.getId(), data);
                                }
                            });

                }

            });
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error marker", e);
        }
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("--------------onActivityResult requestCode----------------" + requestCode);


        if ((requestCode == placeSearch_request_code && resultCode == Activity.RESULT_OK && data != null)) {
            if (search_status == 0) {

                SselectedLatitude = data.getStringExtra("Selected_Latitude");
                SselectedLongitude = data.getStringExtra("Selected_Longitude");
                Current_lat = data.getStringExtra("Selected_Latitude");
                Current_long = data.getStringExtra("Selected_Longitude");
                SselectedLocation = data.getStringExtra("Selected_Location");

                ShouseNo = data.getStringExtra("HouseNo");
                String Scity = data.getStringExtra("City");
                String SpostalCode = data.getStringExtra("ZipCode");
                String Slocation = data.getStringExtra("Location");

                System.out.println("SselectedLatitude---------" + SselectedLatitude);
                System.out.println("SselectedLongitude---------" + SselectedLongitude);

                System.out.println("ShouseNo-----------" + ShouseNo);
                System.out.println("Scity-----------" + Scity);
                System.out.println("SpostalCode-----------" + SpostalCode);
                System.out.println("Slocation-----------" + Slocation);
                getAddress(Double.parseDouble(SselectedLatitude), Double.parseDouble(SselectedLongitude));
                if (!SselectedLocation.equalsIgnoreCase("")) {
                    textview_address_text.setText(SselectedLocation);
                } else {
                    String address = getAddress(Double.parseDouble(SselectedLatitude), Double.parseDouble(SselectedLongitude));
                    textview_address_text.setText(address);
                }

                Current_lat = SselectedLatitude;
                Current_long = SselectedLongitude;
                book_now_taskerid = "";
                book_now_taskid = "";
                loading = true;
                if (!sub_category_id.equalsIgnoreCase("")) {

                    Sub_category_show_tasker(Iconstant.Map_boooking);
                } else if (!category_id.equalsIgnoreCase("")) {
                    postDisplayMainCategory_DetailRequest(getActivity(), Iconstant.Categories_Detail_Url);
                } else {
                    postProvidersRequest(Iconstant.Map_boooking);
                }

            } else {

            }

        } else {

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    //-----------------------------------------------------------------Current Address Get---------------------------------
    private String getAddress(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);

                address1 = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
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


    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onClick(View view) {

    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (moreAddressDialog != null) {
                    moreAddressDialog.show();
                }
                return true;
        }
        return false;
    }


}
