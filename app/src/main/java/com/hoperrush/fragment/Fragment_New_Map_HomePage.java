package com.hoperrush.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
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
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.InflateException;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.hoperrush.Interface.MapFragmentcall;
import com.hoperrush.R;
import com.hoperrush.adapter.CustomPagerAdapter;
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
import com.hoperrush.pojo.MoreTaskerarray;
import com.hoperrush.pojo.ProvidersListPojo;
import com.hoperrush.utils.ConnectionDetector;
import com.hoperrush.utils.CurrencySymbolConverter;
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
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;

/**
 * Created by user145 on 8/3/2017.
 */
public class Fragment_New_Map_HomePage extends FragmentHockeyApp implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, Iconstant, MapFragmentcall, GoogleMap.OnMapLongClickListener, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerDragListener {
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
    public static String city = "";
    public static String state = "";
    String country = "";
    public static String postalCode = "";
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
    private static final String TAG_Same_latitude = "samelatitude";
    private static final String TAG_Same_Longintude = "samelongintude";
    private String Str_bookingId = "", Str_Taskid = "";
    String main_category_id = "", sub_category_id = "";
    public static String minimum_amount = "", hourly_amount = "";
    String Current_lat = "", Current_long = "";
    public static String UserID = "";
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
    private String book_now_time = "";
    private String book_now_date = "";
    private String current_time = "";
    private String booking_date = "";
    private String instruction = "";
    private LoadingDialog mLoadingDialog1;
    private FrameLayout custom_marker_view;
    public static String SselectedLocation = "";
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
    public static boolean taskerselect_status = false;
    GeometricProgressView progressView;
    private boolean refreshingmap = false;
    //---------------------New--------------------------------------------
    ArrayList<MoreTaskerarray> moretasker_list = new ArrayList<MoreTaskerarray>();
    ArrayList<MoreTaskerarray> new_samelist = new ArrayList<MoreTaskerarray>();
    ArrayList<String> mylist = new ArrayList<>();
    ArrayList<MarkerData> subArraylist = new ArrayList<MarkerData>();
    private int count = 1;
    private String tasker_count_latitude = "";
    private String tasker_count_longintude = "";
    private View moretaskerview;
    private RelativeLayout count_layout;
    private TextView count_text;
    String same_location_lat = "";
    String same_location_lng = "";
    private CustomPagerAdapter myAdapter;
    private AutoScrollViewPager myViewPager;
    public static ImageView right_arrow;
    private FragmentActivity myActivityContext;
    public static ImageView left_arrow;

    public static String book_now_taskid = "";
    public static String book_now_taskerid = "";
    public static String book_now_taskername = "";
    FragmentManager manager;
    private boolean location_select_subcategory = false;
//---------------------------Map Track------------------

    private String sLatitude = "";
    private String sLongitude = "";
    public static boolean map_track = false;
    Handler handler;

    private CardView circle_progress_layout;
    private ProgressBar circular_progress_bar;
    private RelativeLayout loding_layout;
    private boolean map_refresh = false;

    class receiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("com.refresh.map_page")) {
                taskerselect_status = false;
                if (mark != null) {
                    mark.hideInfoWindow();
                }
                if (!sub_category_id.equalsIgnoreCase("")) {
                    refreshingmap = true;
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
        myActivityContext = getActivity();
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

                    CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(MyCurrent_lat, MyCurrent_long)).zoom(10).build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                } else {
                    enableGpsService();
                }
            }
        });

        if (CheckPlayService()) {

            System.out.println("isCameraChangeListener3" + " " + isCameraChangeListener);
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
                map_track = false;
                map_refresh = false;
                location_select_subcategory = false;
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
                refreshingmap = false;
                map_track = false;
                map_refresh = false;
                location_select_subcategory = false;
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
                        same_location_lat = marker_data.get(TAG_Same_latitude);
                        same_location_lng = marker_data.get(TAG_Same_Longintude);

                        if (category_id.equalsIgnoreCase("")) {
                            alert(getResources().getString(R.string.action_select_tasker), getResources().getString(R.string.select_category));
                        } else if (sub_category_id.equalsIgnoreCase("")) {
                            alert(getResources().getString(R.string.action_select_tasker), getResources().getString(R.string.select_subcategory));
                        } else {
                            if (!Tasker_lat.equalsIgnoreCase(same_location_lat) && !Tasker_long.equalsIgnoreCase(same_location_lng)) {
                                moreAddressDialog(mini_cost, hour_cost, user_image, user_name, Tasker_id, Tasker_rating, Tasker_lat, Tasker_long, Str_Taskid, tasker_address, arg0);
                            } else {
                                new_samelist.clear();
                                for (int i = 0; i < moretasker_list.size(); i++) {

                                    if (Tasker_lat.equalsIgnoreCase(moretasker_list.get(i).getLatitude()) &&
                                            Tasker_long.equalsIgnoreCase(moretasker_list.get(i).getLongitude())) {

                                        MoreTaskerarray same_data = new MoreTaskerarray();
                                        same_data.setTitle(moretasker_list.get(i).getTitle());
                                        same_data.setMini_cost(moretasker_list.get(i).getMini_cost());
                                        same_data.setHourly_cost(moretasker_list.get(i).getHourly_cost());
                                        same_data.setUrlimage(moretasker_list.get(i).getUrlimage());
                                        same_data.setTasker_id(moretasker_list.get(i).getTasker_id());
                                        same_data.setRating(moretasker_list.get(i).getRating());
                                        same_data.setLatitude(moretasker_list.get(i).getLatitude());
                                        same_data.setLongitude(moretasker_list.get(i).getLongitude());
                                        same_data.setAddress(moretasker_list.get(i).getAddress());

                                        new_samelist.add(same_data);
                                    }
                                }

                                if (new_samelist.size() > 0) {
                                    SameLocationAlert(new_samelist, arg0);
                                } else {
                                    moreAddressDialog(mini_cost, hour_cost, user_image, user_name, Tasker_id, Tasker_rating, Tasker_lat, Tasker_long, Str_Taskid, tasker_address, arg0);
                                }

                            }
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
                    i.putExtra("location", SselectedLocation);
                    i.putExtra("city", city);
                    i.putExtra("state", state);
                    i.putExtra("country", country);
                    i.putExtra("postalcode", postalCode);
                    i.putExtra("latitude", Current_lat);
                    i.putExtra("longintude", Current_long);
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

        moretaskerview = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.more_tasker_layout, null);
        count_layout = (RelativeLayout) moretaskerview.findViewById(R.id.count_layout);
        count_text = (TextView) moretaskerview.findViewById(R.id.count_text);
        location_select_subcategory = false;
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
        progressView = (GeometricProgressView) rootview.findViewById(R.id.progressView);
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
        loding_layout = (RelativeLayout) rootview.findViewById(R.id.loding_layout);

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
            }
        }
        if (CheckPlayService()) {
            googleMap.setOnMarkerDragListener(this);
            googleMap.setOnMapLongClickListener(this);

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

                CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(Dlatitude, Dlongitude)).zoom(10).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            } else {
                System.out.println("isCameraChangeListener3" + " " + isCameraChangeListener);

                enableGpsService();
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
                Toast.makeText(getActivity(), "Install Google Play service To View Location !!!", Toast.LENGTH_LONG).show();
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

    //---------------------------------------------Camera Location Changed----------------------------------------------------------------
    GoogleMap.OnCameraChangeListener mOnCameraChangeListener = new GoogleMap.OnCameraChangeListener() {
        @Override
        public void onCameraChange(CameraPosition cameraPosition) {

            final double latitude = cameraPosition.target.latitude;
            final double longitude = cameraPosition.target.longitude;

            cd = new ConnectionDetector(getActivity());
            isInternetPresent = cd.isConnectingToInternet();

            Log.e("camerachange lat-->", "" + latitude);
            Log.e("on_camera_change lon-->", "" + longitude);

            if (latitude != 0.0) {

                googleMap.clear();
                Recent_lat = latitude;
                Recent_long = longitude;
                Current_lat = String.valueOf(latitude);
                Current_long = String.valueOf(longitude);

                if (isInternetPresent) {
                    if (mRequest != null) {
                        mRequest.cancelRequest();
                    }

                    String address = getAddress(latitude, longitude);
                    textview_address_text.setText(address);
                    if (!location_select_subcategory) {

                        if (!sub_category_id.equalsIgnoreCase("")) {
                            map_refresh = true;
                            Location_Subcategory_Select(Iconstant.Map_boooking);
                        } else if (!category_id.equalsIgnoreCase("")) {
                            map_refresh = true;
                            Location_Base_Maincategorytasker(Iconstant.Map_boooking);

                            // LocationChangedMainCategory(getActivity(), Iconstant.Categories_Detail_Url);
                        } else {
                            map_refresh = true;
                            GetRequest(Iconstant.Map_boooking);
                        }
                    }

                } else {

                }
            }

        }
    };

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        LatLng fromPosition = marker.getPosition();
        Log.d(getClass().getSimpleName(), "Drag start at: " + fromPosition);
    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        LatLng toPosition = marker.getPosition();
        Toast.makeText(
                getActivity(),
                "Marker " + marker.getTitle() + " dragged from " + ""
                        + " to " + toPosition, Toast.LENGTH_LONG).show();

    }


    //-----------------Same Location Tasker Show Alert Dialog----------------------------------------------------------

    private void SameLocationAlert(ArrayList<MoreTaskerarray> moretasker_list, final Marker arg0) {

        mark = arg0;
        DisplayMetrics metrics = getActivity().getResources().getDisplayMetrics();
        int screenWidth = (int) (metrics.widthPixels * 0.80);//fill only 80% of the screen
        moreAddressView = View.inflate(getActivity(), R.layout.more_tasker_view_pager, null);
        moreAddressDialog = new Dialog(getActivity());
        moreAddressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        moreAddressDialog.setContentView(moreAddressView);
        moreAddressDialog.setCanceledOnTouchOutside(false);
        moreAddressDialog.setCancelable(false);
        moreAddressDialog.getWindow().setLayout(screenWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        moreAddressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myViewPager = (AutoScrollViewPager) moreAddressView.findViewById(R.id.tasker_VWPGR);
        left_arrow = (ImageView) moreAddressView.findViewById(R.id.left_arrow);
        right_arrow = (ImageView) moreAddressView.findViewById(R.id.right_arrow);
        myAdapter = new CustomPagerAdapter(getActivity(), moretasker_list, mark, Str_Taskid, moreAddressDialog, Current_lat, Current_long, this);
        myViewPager.setAdapter(myAdapter);
        myViewPager.startAutoScroll();
        myViewPager.setInterval(2900);
        moreAddressDialog.show();
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
        booking_date = bookingdate.getText().toString();
        current_time = getCurrentTimes();

        confirm_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isInternetPresent) {
                    instruction = instructions.getText().toString();
                    Book_job_Request(getActivity(), book_now_taskerid, booking_date, instruction, current_time);
                } else {
                    alert(getResources().getString(R.string.action_no_internet_title), getResources().getString(R.string.action_no_internet_message));
                }
                moreAddressDialog.dismiss();

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
        HashMap<String, String> aAmountMap = sessionManager.getWalletDetails();
        String aCurrencyCode = aAmountMap.get(SessionManager.KEY_CURRENCY_CODE);
        final String myCurrencySymbol = CurrencySymbolConverter.getCurrencySymbol(aCurrencyCode);

        minicost.setText(" Hourly Cost  : " + " " + myCurrencySymbol + hour_cost);
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
            chat_text.setTextColor(Color.parseColor("#202020"));
            detail_text.setTextColor(Color.parseColor("#202020"));
        } else {
            tasker_select.setVisibility(View.GONE);
            select_tasker.setVisibility(View.GONE);
            unselect_tasker.setText("Close");
            chat.setEnabled(false);
            detail.setEnabled(false);
//            chat_text.setTextColor(Color.parseColor("#DCDCDC"));
//            detail_text.setTextColor(Color.parseColor("#DCDCDC"));
            chat_text.setTextColor(Color.parseColor("#202020"));
            detail_text.setTextColor(Color.parseColor("#202020"));
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
                i.putExtra("minimumamount", myCurrencySymbol + minimum_amount);
                i.putExtra("hourlyamount", myCurrencySymbol + hour_cost);
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
                    select_tasker.setText("You have selected" + " " + user_name);
                }

                return v;

            }

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
        jsonParams.put("country", country);

        Log.e("params", String.valueOf(jsonParams));

        jsonParams = checkParams(jsonParams);
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


    private HashMap<String, String> checkParams(HashMap<String, String> map) {
        Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> pairs = (Map.Entry<String, String>) it.next();
            if (pairs.getValue() == null) {
                map.put(pairs.getKey(), "");
            }
        }
        return map;
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

//        if (!location_select_subcategory) {
//            dialog = new SpotsDialog(context, R.style.Custom);
//            dialog.show();
//        }
        loding_layout.setVisibility(View.VISIBLE);
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
                    loding_layout.setVisibility(View.GONE);

                } catch (JSONException e) {
                    e.printStackTrace();
                    loding_layout.setVisibility(View.GONE);
                    if (!location_select_subcategory) {
                        // dialog.dismiss();
                    }
                }
                if (!location_select_subcategory) {
                    // dialog.dismiss();
                }

            }

            @Override
            public void onErrorListener() {
                if (!location_select_subcategory) {
                    // dialog.dismiss();
                    loding_layout.setVisibility(View.GONE);
                }
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
                                mark.setHourly_cost(jobject.getString("hourly_amount"));
                                mark.setTaskerId(jobject.getString("taskerid"));
                                mark.setRating(jobject.getString("rating"));
                                //minimum_amount = jobject.getString("min_amount");
                                hourly_amount = jobject.getString("hourly_amount");
                                markersArray.add(mark);
                            }
                            // sessionManager.setminimum_amount(minimum_amount);
                            sessionManager.sethourly_amount(hourly_amount);
                            sessionManager.settaskid(Str_Taskid);
                            available_service.setVisibility(View.GONE);
                            Addmarker();

                        } else {

                        }
                    } else {
                        Str_response = object.getString("response");
                        googleMap.clear();
                        if (location_select_subcategory) {
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(Current_lat),
                                    Double.parseDouble(Current_long)), 10.0f));
                        }
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
                                mark.setHourly_cost(jobject.getString("hourly_amount"));
                                mark.setTaskerId(jobject.getString("taskerid"));
                                mark.setRating(jobject.getString("rating"));
                                // minimum_amount = jobject.getString("min_amount");
                                hourly_amount = jobject.getString("hourly_amount");
                                markersArray.add(mark);
                            }
                            //sessionManager.setminimum_amount(minimum_amount);
                            sessionManager.sethourly_amount(hourly_amount);
                            sessionManager.settaskid(Str_Taskid);
                            available_service.setVisibility(View.GONE);
                            Addmarker();

                        } else {

                        }
                    } else {
                        Str_response = object.getString("response");
                        googleMap.clear();
                        if (location_select_subcategory) {
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(Current_lat),
                                    Double.parseDouble(Current_long)), 10.0f));
                        }
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

//
//        if (!location_select_subcategory) {
//            if (refreshingmap) {
//                dialog = new SpotsDialog(context, R.style.Refresh);
//                dialog.show();
//            } else {
//                dialog = new SpotsDialog(context, R.style.Custom);
//                dialog.show();
//            }
//        }
        loding_layout.setVisibility(View.VISIBLE);

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
                                // mark.setMini_cost(jobject.getString("min_amount"));
                                mark.setHourly_cost(jobject.getString("hourly_amount"));
                                mark.setTaskerId(jobject.getString("taskerid"));
                                mark.setRating(jobject.getString("rating"));
                                if (jobject.has("worklocation")) {
                                    mark.setSetAddress(jobject.getString("worklocation"));
                                }

                                hourly_amount = jobject.getString("hourly_amount");
                                markersArray.add(mark);
                            }

                            sessionManager.settaskid(Str_Taskid);
                            available_service.setVisibility(View.GONE);
                            Addmarker();
//                            if (!location_select_subcategory) {
//                                dialog.dismiss();
//                            }


                        } else {

                        }
                    } else {
                        Str_response = object.getString("response");
//                        if (!location_select_subcategory) {
//                            dialog.dismiss();
//                        }
                        googleMap.clear();
                        if (location_select_subcategory) {
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(Current_lat),
                                    Double.parseDouble(Current_long)), 10.0f));
                        }
                        center_marker.setVisibility(View.VISIBLE);
                        available_service.setVisibility(View.VISIBLE);
                        available_service.setBackgroundColor(Color.parseColor("#000000"));
                        selected_text.setText("Service not available");
                        book_now_taskerid = "";
                        book_now_taskid = "";

                    }
                    loding_layout.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                    loding_layout.setVisibility(View.GONE);
                    if (!location_select_subcategory) {
                        //dialog.dismiss();
                    }
                }


            }

            @Override
            public void onErrorListener() {
                if (!location_select_subcategory) {
//                    dialog.dismiss();
                }
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

    public void Addmarker() {
        if (map_refresh) {
            loding_layout.setVisibility(View.VISIBLE);
        }
        String TaskerID = "";
        String TaskerID_New = "";
        googleMap.clear();
        moretasker_list.clear();
        latarray.clear();
        lngarray.clear();
        count = 1;
        if (markersArray.size() != 0) {
            subArraylist.clear();
            mylist.clear();
            moretasker_list.clear();
            for (int i = 0; i < markersArray.size(); i++) {
                final String lat = markersArray.get(i).getLatitude();
                final String lng = markersArray.get(i).getLogintude();
                String main_tasker_id = markersArray.get(i).getTaskerId();
                //---------------new Code----------------------------
                subArraylist.clear();
                if (markersArray.size() - 1 != i) {
                    mylist.add(String.valueOf(i));
                    subArraylist.addAll(markersArray);
                    for (int b = 0; b < mylist.size(); b++) {
                        subArraylist.remove(0);
                    }
                    for (int k = 0; k < subArraylist.size(); k++) {
                        if (markersArray.get(i).getLatitude().equalsIgnoreCase(subArraylist.get(k).getLatitude())
                                && markersArray.get(i).getLogintude().equalsIgnoreCase(subArraylist.get(k).getLogintude())) {

                            tasker_count_latitude = lat;
                            tasker_count_longintude = lng;

                            MoreTaskerarray mark_list = new MoreTaskerarray();
                            mark_list.setTitle(markersArray.get(i).getTitle());
                            mark_list.setMini_cost(markersArray.get(i).getMini_cost());
                            mark_list.setHourly_cost(markersArray.get(i).getHourly_cost());
                            mark_list.setUrlimage(markersArray.get(i).getimageurl());
                            mark_list.setTasker_id(markersArray.get(i).getTaskerId());
                            mark_list.setRating(markersArray.get(i).getRating());
                            mark_list.setLatitude(markersArray.get(i).getLatitude());
                            mark_list.setLongitude(markersArray.get(i).getLogintude());
                            mark_list.setAddress(markersArray.get(i).getSetAddress());


                            MoreTaskerarray new_mark_list = new MoreTaskerarray();
                            new_mark_list.setTitle(subArraylist.get(k).getTitle());
                            new_mark_list.setMini_cost(subArraylist.get(k).getMini_cost());
                            new_mark_list.setHourly_cost(subArraylist.get(k).getHourly_cost());
                            new_mark_list.setUrlimage(subArraylist.get(k).getimageurl());
                            new_mark_list.setTasker_id(subArraylist.get(k).getTaskerId());
                            new_mark_list.setRating(subArraylist.get(k).getRating());
                            new_mark_list.setLatitude(subArraylist.get(k).getLatitude());
                            new_mark_list.setLongitude(subArraylist.get(k).getLogintude());
                            new_mark_list.setAddress(subArraylist.get(k).getSetAddress());


                            if (moretasker_list.size() == 0) {

                                moretasker_list.add(mark_list);
                                moretasker_list.add(new_mark_list);

                            } else {

                                TaskerID = markersArray.get(i).getTaskerId();
                                TaskerID_New = subArraylist.get(k).getTaskerId();

                                Log.v("containsId", "" + containsId(moretasker_list, TaskerID));


                                if (!containsId(moretasker_list, TaskerID)) {
                                    moretasker_list.add(mark_list);
                                }

                                if (!containsId(moretasker_list, TaskerID_New)) {
                                    moretasker_list.add(new_mark_list);
                                }

                            }

                            for (int h = 0; h < moretasker_list.size(); h++) {
                                if (moretasker_list.get(h).getLatitude().equalsIgnoreCase(tasker_count_latitude) &&
                                        moretasker_list.get(h).getLongitude().equalsIgnoreCase(tasker_count_longintude)) {
                                }
                            }
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    LatLng sydney = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                                    googleMap.addMarker(new MarkerOptions().position(sydney).anchor(-0.9f, 1.5f)
                                            .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(context, moretaskerview)))).setTitle("Location");
                                }
                            }, 1000);


                            if (main_tasker_id.equalsIgnoreCase(subArraylist.get(k).getTaskerId())) {

                            }
                        }

                    }
                }
                String strImg = markersArray.get(i).getimageurl();
                //"http://45.63.22.34:3005/uploads/images/tasker/1515174250849.png"
                strImg = strImg.replace("localhost:3002","45.63.22.34:3005");
                createMarker(markersArray.get(i).getLatitude(), markersArray.get(i).getLogintude(), markersArray.get(i).getTitle(), markersArray.get(i).getId(),strImg , markersArray.get(i).getMini_cost(), markersArray.get(i).getHourly_cost(), markersArray.get(i).getTaskerId(), markersArray.get(i).getRating(),strImg, markersArray.get(i).getSetAddress(), tasker_count_latitude, tasker_count_longintude);// markersArray.get(i).getimageurl()
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("lat", markersArray.get(i).getLatitude());
                HashMap<String, String> map1 = new HashMap<String, String>();
                map1.put("lng", markersArray.get(i).getLogintude());
                latarray.add(map);
                lngarray.add(map1);
            }
        }
    }

    public static boolean containsId(ArrayList<MoreTaskerarray> list, String id) {
        for (MoreTaskerarray object : list) {
            if (object.getTasker_id() == id) {
                return true;
            }
        }
        return false;
    }

    protected void createMarker(final String latitude, final String longitude, final String title, final String id, final String urlimage, final String mini_cost, final String hourly_cost, final String tasker_id, final String rating, final String category_image, final String address, final String tasker_lat_count, final String tasker_lng_count) {

        final List<Marker> markersList = new ArrayList<Marker>();

        final double lat = Double.parseDouble(latitude);
        final double longi = Double.parseDouble(longitude);
        try {

            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {

                    if (category_image != null) {
                        Glide.with(getActivity()).
                                load(category_image)
                                .asBitmap()
                                .fitCenter()
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(final Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {

                                        if (map_refresh) {
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
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

                                                    center_marker.setVisibility(View.VISIBLE);
                                                    if (location_select_subcategory) {
                                                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(Current_lat),
                                                                Double.parseDouble(Current_long)), 10.0f));
                                                    }

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

                                                    data.put(TAG_Same_latitude, tasker_lat_count);
                                                    data.put(TAG_Same_Longintude, tasker_lng_count);

                                                    extraMarkerInfo.put(marker.getId(), data);
                                                    location_select_subcategory = false;
                                                    if (map_refresh) {
                                                        loding_layout.setVisibility(View.GONE);
                                                    }
                                                }
                                            }, 1000);

                                        } else {
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

                                            center_marker.setVisibility(View.VISIBLE);
                                            if (location_select_subcategory) {
                                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(Current_lat),
                                                        Double.parseDouble(Current_long)), 10.0f));
                                            }

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

                                            data.put(TAG_Same_latitude, tasker_lat_count);
                                            data.put(TAG_Same_Longintude, tasker_lng_count);

                                            extraMarkerInfo.put(marker.getId(), data);
                                            location_select_subcategory = false;
                                        }


                                    }
                                });
                    }
                }

            });
        } catch (NullPointerException e) {
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


    public static Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
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
                    location_select_subcategory = true;
                    Sub_category_show_tasker(Iconstant.Map_boooking);
                } else if (!category_id.equalsIgnoreCase("")) {
                    location_select_subcategory = true;
                    postDisplayMainCategory_DetailRequest(getActivity(), Iconstant.Categories_Detail_Url);
                } else {
                    location_select_subcategory = true;
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


    @Override
    public void more_tasker_markershow(Marker mark, String title, Context myContext, final String tasker_id, final String str_taskid, final String username) {
        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            // Use default InfoWindow frame
            @Override
            public View getInfoWindow(Marker arg1) {
                View v = View.inflate(context, R.layout.taskerselect_window_show, null);
                if (arg1 != null) {

                    RelativeLayout layout = (RelativeLayout) v.findViewById(R.id.available_tasker);
                    TextView select_tasker = (TextView) v.findViewById(R.id.selected_tasker_text);
                    select_tasker.setText("You have selected" + " " + username);
                    book_now_taskername = user_name;
                    book_now_taskerid = tasker_id;
                    book_now_taskid = str_taskid;
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

    //--------------------------------Location Change Map Loading and response Fetching Details---------------------------------------------------

    public void GetRequest(String url) {
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
                                mark.setHourly_cost(jobject.getString("hourly_amount"));
                                mark.setTaskerId(jobject.getString("taskerid"));
                                mark.setRating(jobject.getString("rating"));
                                //minimum_amount = jobject.getString("min_amount");
                                hourly_amount = jobject.getString("hourly_amount");
                                markersArray.add(mark);
                            }
                            // sessionManager.setminimum_amount(minimum_amount);
                            sessionManager.sethourly_amount(hourly_amount);
                            sessionManager.settaskid(Str_Taskid);
                            available_service.setVisibility(View.GONE);
                            Addmarker();
                            circular_progress_bar.setVisibility(View.GONE);

                        } else {

                        }
                    } else {

                        Str_response = object.getString("response");
                        googleMap.clear();
//                        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(Double.parseDouble(Current_lat), Double.parseDouble(Current_long))).zoom(10).build();
//                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
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

    //----------------------------------------Main Category Choose Location Changed--------------------------------------------------------------

    private void LocationChangedMainCategory(Context mContext, String url) {

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
                        Location_Base_Maincategorytasker(Iconstant.Map_boooking);
                    } else {

                        Location_Base_Maincategorytasker(Iconstant.Map_boooking);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    circular_progress_bar.setVisibility(View.GONE);
                }

            }

            @Override
            public void onErrorListener() {

            }
        });
    }


//-------------------------------------------Location Based Subcategory Changed----------------------------------------------------

    public void Location_Subcategory_Select(String url) {

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
                                // mark.setMini_cost(jobject.getString("min_amount"));
                                mark.setHourly_cost(jobject.getString("hourly_amount"));
                                mark.setTaskerId(jobject.getString("taskerid"));
                                mark.setRating(jobject.getString("rating"));
                                if (jobject.has("worklocation")) {
                                    mark.setSetAddress(jobject.getString("worklocation"));
                                }

                                hourly_amount = jobject.getString("hourly_amount");
                                markersArray.add(mark);
                            }

                            sessionManager.settaskid(Str_Taskid);
                            available_service.setVisibility(View.GONE);
                            Addmarker();


                        } else {

                        }
                    } else {

                        Str_response = object.getString("response");
                        googleMap.clear();
                        if (!map_track) {
//                            CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(Double.parseDouble(Current_lat), Double.parseDouble(Current_long))).zoom(10).build();
//                            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        }
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


//--------------------------------Location Based MainCategory Tasker-------------------------------------

    public void Location_Base_Maincategorytasker(String url) {

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
                                mark.setHourly_cost(jobject.getString("hourly_amount"));
                                mark.setTaskerId(jobject.getString("taskerid"));
                                mark.setRating(jobject.getString("rating"));
                                // minimum_amount = jobject.getString("min_amount");
                                hourly_amount = jobject.getString("hourly_amount");
                                markersArray.add(mark);
                            }
                            //sessionManager.setminimum_amount(minimum_amount);
                            sessionManager.sethourly_amount(hourly_amount);
                            sessionManager.settaskid(Str_Taskid);
                            available_service.setVisibility(View.GONE);
                            Addmarker();

                        } else {

                        }
                    } else {
                        Str_response = object.getString("response");
                        googleMap.clear();
                        if (!map_track) {
//                            CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(Double.parseDouble(Current_lat),
//                                    Double.parseDouble(Current_long))).zoom(10).build();
//                            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        }
                        center_marker.setVisibility(View.VISIBLE);
                        available_service.setVisibility(View.VISIBLE);
                        available_service.setBackgroundColor(Color.parseColor("#000000"));
                        selected_text.setText("Service not available");
                        book_now_taskerid = "";
                        book_now_taskid = "";
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    circular_progress_bar.setVisibility(View.GONE);
                }

                circular_progress_bar.setVisibility(View.GONE);
            }

            @Override
            public void onErrorListener() {
                circular_progress_bar.setVisibility(View.GONE);
            }
        });

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
