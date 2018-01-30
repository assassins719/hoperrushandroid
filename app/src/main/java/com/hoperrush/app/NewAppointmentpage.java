package com.hoperrush.app;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.hoperrush.R;
import com.hoperrush.adapter.AppointmentMoreAddressAdapter;
import com.hoperrush.core.dialog.LoadingDialog;
import com.hoperrush.core.dialog.PkDialog;
import com.hoperrush.core.gps.GPSTracker;
import com.hoperrush.core.volley.ServiceRequest;
import com.hoperrush.iconstant.Iconstant;
import com.hoperrush.pojo.AddressListPojo;
import com.hoperrush.utils.ConnectionDetector;
import com.hoperrush.utils.SessionManager;
import com.hoperrush.utils.SubClassFragementActivity;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class NewAppointmentpage extends SubClassFragementActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private RelativeLayout time1;
    private RelativeLayout time2;
    private RelativeLayout time3;
    private RelativeLayout time4;
    private RelativeLayout time5;
    private RelativeLayout time6;
    private RelativeLayout time7;
    private RelativeLayout time8;
    private RelativeLayout time9;
    private RelativeLayout time10;
    private RelativeLayout time11;
    private RelativeLayout time12;
    private RadioButton list_type, map_type;
    private RadioGroup viewtype_group;
    private Boolean radio_list = false;
    private Boolean radio_map = false;
    private Boolean radio_button_value = false;
    private TextView texttime1;
    private TextView texttime2;
    private TextView texttime3;
    private TextView texttime4;
    private TextView texttime5;
    private TextView texttime6;
    private TextView texttime7;
    private TextView texttime8;
    private TextView texttime9;
    private TextView texttime10;
    private TextView texttime11;
    private TextView texttime12;
    private TextView Tv_selectedDate;
    private boolean buttoncheck1 = true;
    private boolean buttoncheck2 = true;
    private boolean buttoncheck3 = true;
    private boolean buttoncheck4 = true;
    private boolean buttoncheck5 = true;
    private boolean buttoncheck6 = true;
    private boolean buttoncheck7 = true;
    private boolean buttoncheck8 = true;
    private boolean buttoncheck9 = true;
    private boolean buttoncheck10 = true;
    private boolean buttoncheck11 = true;
    private boolean buttoncheck12 = true;
    String sCurrentTime = "";
    private MaterialCalendarView mcalendar;
    private String SelectDate = "";
    int choosedate;
    int choosemonth;
    final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    final SimpleDateFormat formatter1 = new SimpleDateFormat("dd");
    final SimpleDateFormat formatter2 = new SimpleDateFormat("MM");
    private TextView visibletime, visibletime2, visibletime3, visibletime4, visibletime5, visibletime6, visibletime7, visibletime8, visibletime9, visibletime10, visibletime11, visibletime12;
    private RelativeLayout Rl_AddAddress;
    private RelativeLayout Rl_yourAddress;
    private RelativeLayout Rl_moreAddress;
    private TextView Tv_yourAddress;
    private ScrollView scrollView;
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private SessionManager sessionManager;
    GPSTracker gps;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    PendingResult<LocationSettingsResult> result;
    final static int REQUEST_LOCATION = 199;
    private String sLatitude = "", sLongitude = "";
    private AppointmentMoreAddressAdapter addressAdapter;
    private String UserID = "";

    private LoadingDialog mLoadingDialog;
    private ServiceRequest mRequest;
    private boolean isDataPresent = false;
    private ArrayList<AddressListPojo> addressList;
    private String sDisplayAddress = "";
    private String sSelectedAddressId = "";

    private String DisplayAddressId = "";
    private String sCategoryId = "", sServiceId = "";
    private String selectedlocation="",city="",state="",country="",postalcode="",latitude="",longintude="";
    private String sSplitTime = "";
    private Dialog moreAddressDialog;
    private View moreAddressView;
    final int PERMISSION_REQUEST_CODE = 111;
    private RelativeLayout Rl_back;
    private ImageView Im_backIcon;
    private TextView Tv_headerTitle;
    private String Select_time = "";
    private String Select_Date = "";
    private Button Search_button;
    HashMap<String, String> displayaddress;
    private EditText Et_instruction;
    public static int addresslistposition;
    public static String Address="";
    public static String address="";
    private boolean list_select_value=false;
    String str_scroll_bottom = "";

//    public ScrollView getScrollView() {
//        return scrollView;
//    }

    public class RefreshReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.package.ACTION_CLASS_APPOINTMENT_REFRESH")) {
                list_select_value=true;
                sSelectedAddressId="";
                if (isInternetPresent) {
                    HashMap<String, String> user = sessionManager.getUserDetails();
                    UserID = user.get(SessionManager.KEY_USER_ID);
                    addressList_Request(NewAppointmentpage.this, Iconstant.address_list_url);
                }
            }
        }
    }

    private RefreshReceiver refreshReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_appointmentpage);

        initializeHeaderBar();
        initialize();

        Rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        time1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (texttime2.getVisibility() == View.VISIBLE) {
                    time2.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime2.setTextColor(Color.GRAY);
                }
                if (texttime3.getVisibility() == View.VISIBLE) {

                    time3.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime3.setTextColor(Color.GRAY);

                }
                if (texttime4.getVisibility() == View.VISIBLE) {

                    time4.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime4.setTextColor(Color.GRAY);

                }
                if (texttime5.getVisibility() == View.VISIBLE) {

                    time5.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime5.setTextColor(Color.GRAY);

                }
                if (texttime6.getVisibility() == View.VISIBLE) {

                    time6.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime6.setTextColor(Color.GRAY);

                }
                if (texttime7.getVisibility() == View.VISIBLE) {

                    time7.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime7.setTextColor(Color.GRAY);

                }
                if (texttime8.getVisibility() == View.VISIBLE) {

                    time8.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime8.setTextColor(Color.GRAY);

                }
                if (texttime9.getVisibility() == View.VISIBLE) {

                    time9.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime9.setTextColor(Color.GRAY);

                }
                if (texttime10.getVisibility() == View.VISIBLE) {

                    time10.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime10.setTextColor(Color.GRAY);

                }
                if (texttime11.getVisibility() == View.VISIBLE) {

                    time11.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime11.setTextColor(Color.GRAY);

                }
                if (texttime12.getVisibility() == View.VISIBLE) {

                    time12.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime12.setTextColor(Color.GRAY);

                }

                time1.setBackgroundColor(Color.parseColor("#526CA5"));
                texttime1.setTextColor(Color.WHITE);
                Select_time = texttime1.getText().toString();

                String[] splitTime = Select_time.split("-");
                String sSplitTimeValue = splitTime[0];
                try {
                    SimpleDateFormat displayFormat = new SimpleDateFormat("HH");
                    SimpleDateFormat parseFormat = new SimpleDateFormat("hh a", Locale.US);
                    Date date = parseFormat.parse(sSplitTimeValue);
                    sSplitTime = "";
                    sSplitTime = displayFormat.format(date) + ":00";

                } catch (java.text.ParseException e) {
                    e.printStackTrace();
                }

            }
        });
        time2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (texttime1.getVisibility() == View.VISIBLE) {
                    time1.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime1.setTextColor(Color.GRAY);
                }
                if (texttime3.getVisibility() == View.VISIBLE) {

                    time3.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime3.setTextColor(Color.GRAY);

                }
                if (texttime4.getVisibility() == View.VISIBLE) {

                    time4.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime4.setTextColor(Color.GRAY);

                }
                if (texttime5.getVisibility() == View.VISIBLE) {

                    time5.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime5.setTextColor(Color.GRAY);

                }
                if (texttime6.getVisibility() == View.VISIBLE) {

                    time6.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime6.setTextColor(Color.GRAY);

                }
                if (texttime7.getVisibility() == View.VISIBLE) {

                    time7.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime7.setTextColor(Color.GRAY);

                }
                if (texttime8.getVisibility() == View.VISIBLE) {

                    time8.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime8.setTextColor(Color.GRAY);

                }
                if (texttime9.getVisibility() == View.VISIBLE) {

                    time9.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime9.setTextColor(Color.GRAY);

                }
                if (texttime10.getVisibility() == View.VISIBLE) {

                    time10.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime10.setTextColor(Color.GRAY);

                }
                if (texttime11.getVisibility() == View.VISIBLE) {

                    time11.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime11.setTextColor(Color.GRAY);

                }
                if (texttime12.getVisibility() == View.VISIBLE) {

                    time12.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime12.setTextColor(Color.GRAY);

                }

                time2.setBackgroundColor(Color.parseColor("#526CA5"));
                texttime2.setTextColor(Color.WHITE);

                Select_time = texttime2.getText().toString();

                String[] splitTime = Select_time.split("-");
                String sSplitTimeValue = splitTime[0];
                try {
                    SimpleDateFormat displayFormat = new SimpleDateFormat("HH");
                    SimpleDateFormat parseFormat = new SimpleDateFormat("hh a", Locale.US);
                    Date date = parseFormat.parse(sSplitTimeValue);
                    sSplitTime = "";
                    sSplitTime = displayFormat.format(date) + ":00";

                } catch (java.text.ParseException e) {
                    e.printStackTrace();
                }

            }
        });

        time3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (texttime1.getVisibility() == View.VISIBLE) {
                    time1.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime1.setTextColor(Color.GRAY);
                }
                if (texttime2.getVisibility() == View.VISIBLE) {

                    time2.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime2.setTextColor(Color.GRAY);

                }
                if (texttime4.getVisibility() == View.VISIBLE) {

                    time4.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime4.setTextColor(Color.GRAY);

                }
                if (texttime5.getVisibility() == View.VISIBLE) {

                    time5.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime5.setTextColor(Color.GRAY);

                }
                if (texttime6.getVisibility() == View.VISIBLE) {

                    time6.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime6.setTextColor(Color.GRAY);

                }
                if (texttime7.getVisibility() == View.VISIBLE) {

                    time7.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime7.setTextColor(Color.GRAY);

                }
                if (texttime8.getVisibility() == View.VISIBLE) {

                    time8.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime8.setTextColor(Color.GRAY);

                }
                if (texttime9.getVisibility() == View.VISIBLE) {

                    time9.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime9.setTextColor(Color.GRAY);

                }
                if (texttime10.getVisibility() == View.VISIBLE) {

                    time10.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime10.setTextColor(Color.GRAY);

                }
                if (texttime11.getVisibility() == View.VISIBLE) {

                    time11.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime11.setTextColor(Color.GRAY);

                }
                if (texttime12.getVisibility() == View.VISIBLE) {

                    time12.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime12.setTextColor(Color.GRAY);

                }

                time3.setBackgroundColor(Color.parseColor("#526CA5"));
                texttime3.setTextColor(Color.WHITE);

                Select_time = texttime3.getText().toString();

                String[] splitTime = Select_time.split("-");
                String sSplitTimeValue = splitTime[0];
                try {
                    SimpleDateFormat displayFormat = new SimpleDateFormat("HH");
                    SimpleDateFormat parseFormat = new SimpleDateFormat("hh a", Locale.US);
                    Date date = parseFormat.parse(sSplitTimeValue);
                    sSplitTime = "";
                    sSplitTime = displayFormat.format(date) + ":00";

                } catch (java.text.ParseException e) {
                    e.printStackTrace();
                }

            }
        });
        time4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (texttime1.getVisibility() == View.VISIBLE) {
                    time1.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime1.setTextColor(Color.GRAY);
                }
                if (texttime3.getVisibility() == View.VISIBLE) {

                    time3.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime3.setTextColor(Color.GRAY);

                }
                if (texttime2.getVisibility() == View.VISIBLE) {

                    time2.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime2.setTextColor(Color.GRAY);

                }
                if (texttime5.getVisibility() == View.VISIBLE) {

                    time5.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime5.setTextColor(Color.GRAY);

                }
                if (texttime6.getVisibility() == View.VISIBLE) {

                    time6.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime6.setTextColor(Color.GRAY);

                }
                if (texttime7.getVisibility() == View.VISIBLE) {

                    time7.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime7.setTextColor(Color.GRAY);

                }
                if (texttime8.getVisibility() == View.VISIBLE) {

                    time8.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime8.setTextColor(Color.GRAY);

                }
                if (texttime9.getVisibility() == View.VISIBLE) {

                    time9.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime9.setTextColor(Color.GRAY);

                }
                if (texttime10.getVisibility() == View.VISIBLE) {

                    time10.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime10.setTextColor(Color.GRAY);

                }
                if (texttime11.getVisibility() == View.VISIBLE) {

                    time11.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime11.setTextColor(Color.GRAY);

                }
                if (texttime12.getVisibility() == View.VISIBLE) {

                    time12.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime12.setTextColor(Color.GRAY);

                }

                time4.setBackgroundColor(Color.parseColor("#526CA5"));
                texttime4.setTextColor(Color.WHITE);

                Select_time = texttime4.getText().toString();

                String[] splitTime = Select_time.split("-");
                String sSplitTimeValue = splitTime[0];
                try {
                    SimpleDateFormat displayFormat = new SimpleDateFormat("HH");
                    SimpleDateFormat parseFormat = new SimpleDateFormat("hh a", Locale.US);
                    Date date = parseFormat.parse(sSplitTimeValue);
                    sSplitTime = "";
                    sSplitTime = displayFormat.format(date) + ":00";

                } catch (java.text.ParseException e) {
                    e.printStackTrace();
                }

            }
        });
        time5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (texttime1.getVisibility() == View.VISIBLE) {
                    time1.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime1.setTextColor(Color.GRAY);
                }
                if (texttime3.getVisibility() == View.VISIBLE) {

                    time3.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime3.setTextColor(Color.GRAY);

                }
                if (texttime4.getVisibility() == View.VISIBLE) {

                    time4.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime4.setTextColor(Color.GRAY);

                }
                if (texttime2.getVisibility() == View.VISIBLE) {

                    time2.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime2.setTextColor(Color.GRAY);

                }
                if (texttime6.getVisibility() == View.VISIBLE) {

                    time6.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime6.setTextColor(Color.GRAY);

                }
                if (texttime7.getVisibility() == View.VISIBLE) {

                    time7.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime7.setTextColor(Color.GRAY);

                }
                if (texttime8.getVisibility() == View.VISIBLE) {

                    time8.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime8.setTextColor(Color.GRAY);

                }
                if (texttime9.getVisibility() == View.VISIBLE) {

                    time9.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime9.setTextColor(Color.GRAY);

                }
                if (texttime10.getVisibility() == View.VISIBLE) {

                    time10.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime10.setTextColor(Color.GRAY);

                }
                if (texttime11.getVisibility() == View.VISIBLE) {

                    time11.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime11.setTextColor(Color.GRAY);

                }
                if (texttime12.getVisibility() == View.VISIBLE) {

                    time12.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime12.setTextColor(Color.GRAY);

                }

                time5.setBackgroundColor(Color.parseColor("#526CA5"));
                texttime5.setTextColor(Color.WHITE);
                Select_time = texttime5.getText().toString();

                String[] splitTime = Select_time.split("-");
                String sSplitTimeValue = splitTime[0];
                try {
                    SimpleDateFormat displayFormat = new SimpleDateFormat("HH");
                    SimpleDateFormat parseFormat = new SimpleDateFormat("hh a", Locale.US);
                    Date date = parseFormat.parse(sSplitTimeValue);
                    sSplitTime = "";
                    sSplitTime = displayFormat.format(date) + ":00";

                } catch (java.text.ParseException e) {
                    e.printStackTrace();
                }

            }
        });
        time6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (texttime1.getVisibility() == View.VISIBLE) {
                    time1.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime1.setTextColor(Color.GRAY);
                }
                if (texttime3.getVisibility() == View.VISIBLE) {

                    time3.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime3.setTextColor(Color.GRAY);

                }
                if (texttime4.getVisibility() == View.VISIBLE) {

                    time4.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime4.setTextColor(Color.GRAY);

                }
                if (texttime5.getVisibility() == View.VISIBLE) {

                    time5.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime5.setTextColor(Color.GRAY);

                }
                if (texttime2.getVisibility() == View.VISIBLE) {

                    time2.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime2.setTextColor(Color.GRAY);

                }
                if (texttime7.getVisibility() == View.VISIBLE) {

                    time7.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime7.setTextColor(Color.GRAY);

                }
                if (texttime8.getVisibility() == View.VISIBLE) {

                    time8.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime8.setTextColor(Color.GRAY);

                }
                if (texttime9.getVisibility() == View.VISIBLE) {

                    time9.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime9.setTextColor(Color.GRAY);

                }
                if (texttime10.getVisibility() == View.VISIBLE) {

                    time10.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime10.setTextColor(Color.GRAY);

                }
                if (texttime11.getVisibility() == View.VISIBLE) {

                    time11.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime11.setTextColor(Color.GRAY);

                }
                if (texttime12.getVisibility() == View.VISIBLE) {

                    time12.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime12.setTextColor(Color.GRAY);

                }

                time6.setBackgroundColor(Color.parseColor("#526CA5"));
                texttime6.setTextColor(Color.WHITE);

                Select_time = texttime6.getText().toString();

                String[] splitTime = Select_time.split("-");
                String sSplitTimeValue = splitTime[0];
                try {
                    SimpleDateFormat displayFormat = new SimpleDateFormat("HH");
                    SimpleDateFormat parseFormat = new SimpleDateFormat("hh a", Locale.US);
                    Date date = parseFormat.parse(sSplitTimeValue);
                    sSplitTime = "";
                    sSplitTime = displayFormat.format(date) + ":00";

                } catch (java.text.ParseException e) {
                    e.printStackTrace();
                }

            }
        });
        time7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (texttime1.getVisibility() == View.VISIBLE) {
                    time1.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime1.setTextColor(Color.GRAY);
                }
                if (texttime3.getVisibility() == View.VISIBLE) {

                    time3.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime3.setTextColor(Color.GRAY);

                }
                if (texttime4.getVisibility() == View.VISIBLE) {

                    time4.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime4.setTextColor(Color.GRAY);

                }
                if (texttime5.getVisibility() == View.VISIBLE) {

                    time5.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime5.setTextColor(Color.GRAY);

                }
                if (texttime6.getVisibility() == View.VISIBLE) {

                    time6.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime6.setTextColor(Color.GRAY);

                }
                if (texttime2.getVisibility() == View.VISIBLE) {

                    time2.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime2.setTextColor(Color.GRAY);

                }
                if (texttime8.getVisibility() == View.VISIBLE) {

                    time8.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime8.setTextColor(Color.GRAY);

                }
                if (texttime9.getVisibility() == View.VISIBLE) {

                    time9.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime9.setTextColor(Color.GRAY);

                }
                if (texttime10.getVisibility() == View.VISIBLE) {

                    time10.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime10.setTextColor(Color.GRAY);

                }
                if (texttime11.getVisibility() == View.VISIBLE) {

                    time11.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime11.setTextColor(Color.GRAY);

                }
                if (texttime12.getVisibility() == View.VISIBLE) {

                    time12.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime12.setTextColor(Color.GRAY);

                }

                time7.setBackgroundColor(Color.parseColor("#526CA5"));
                texttime7.setTextColor(Color.WHITE);

                Select_time = texttime7.getText().toString();

                String[] splitTime = Select_time.split("-");
                String sSplitTimeValue = splitTime[0];
                try {
                    SimpleDateFormat displayFormat = new SimpleDateFormat("HH");
                    SimpleDateFormat parseFormat = new SimpleDateFormat("hh a", Locale.US);
                    Date date = parseFormat.parse(sSplitTimeValue);
                    sSplitTime = "";
                    sSplitTime = displayFormat.format(date) + ":00";

                } catch (java.text.ParseException e) {
                    e.printStackTrace();
                }

            }
        });
        time8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (texttime1.getVisibility() == View.VISIBLE) {
                    time1.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime1.setTextColor(Color.GRAY);
                }
                if (texttime2.getVisibility() == View.VISIBLE) {

                    time2.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime2.setTextColor(Color.GRAY);

                }
                if (texttime3.getVisibility() == View.VISIBLE) {

                    time3.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime3.setTextColor(Color.GRAY);

                }
                if (texttime4.getVisibility() == View.VISIBLE) {

                    time4.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime4.setTextColor(Color.GRAY);

                }
                if (texttime5.getVisibility() == View.VISIBLE) {

                    time5.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime5.setTextColor(Color.GRAY);

                }
                if (texttime6.getVisibility() == View.VISIBLE) {

                    time6.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime6.setTextColor(Color.GRAY);

                }
                if (texttime7.getVisibility() == View.VISIBLE) {

                    time7.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime7.setTextColor(Color.GRAY);

                }

                if (texttime9.getVisibility() == View.VISIBLE) {

                    time9.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime9.setTextColor(Color.GRAY);

                }
                if (texttime10.getVisibility() == View.VISIBLE) {

                    time10.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime10.setTextColor(Color.GRAY);

                }
                if (texttime11.getVisibility() == View.VISIBLE) {

                    time11.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime11.setTextColor(Color.GRAY);

                }
                if (texttime12.getVisibility() == View.VISIBLE) {

                    time12.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime12.setTextColor(Color.GRAY);

                }

                time8.setBackgroundColor(Color.parseColor("#526CA5"));
                texttime8.setTextColor(Color.WHITE);

                Select_time = texttime8.getText().toString();

                String[] splitTime = Select_time.split("-");
                String sSplitTimeValue = splitTime[0];
                try {
                    SimpleDateFormat displayFormat = new SimpleDateFormat("HH");
                    SimpleDateFormat parseFormat = new SimpleDateFormat("hh a", Locale.US);
                    Date date = parseFormat.parse(sSplitTimeValue);
                    sSplitTime = "";
                    sSplitTime = displayFormat.format(date) + ":00";

                } catch (java.text.ParseException e) {
                    e.printStackTrace();
                }


            }
        });
        time9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (texttime1.getVisibility() == View.VISIBLE) {
                    time1.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime1.setTextColor(Color.GRAY);
                }
                if (texttime2.getVisibility() == View.VISIBLE) {

                    time2.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime2.setTextColor(Color.GRAY);

                }
                if (texttime3.getVisibility() == View.VISIBLE) {

                    time3.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime3.setTextColor(Color.GRAY);

                } else if (texttime4.getVisibility() == View.VISIBLE) {

                    time4.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime4.setTextColor(Color.GRAY);

                }
                if (texttime5.getVisibility() == View.VISIBLE) {

                    time5.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime5.setTextColor(Color.GRAY);

                }
                if (texttime6.getVisibility() == View.VISIBLE) {

                    time6.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime6.setTextColor(Color.GRAY);

                }
                if (texttime7.getVisibility() == View.VISIBLE) {

                    time7.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime7.setTextColor(Color.GRAY);

                }
                if (texttime8.getVisibility() == View.VISIBLE) {

                    time8.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime8.setTextColor(Color.GRAY);

                }

                if (texttime10.getVisibility() == View.VISIBLE) {

                    time10.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime10.setTextColor(Color.GRAY);

                }
                if (texttime11.getVisibility() == View.VISIBLE) {

                    time11.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime11.setTextColor(Color.GRAY);

                }
                if (texttime12.getVisibility() == View.VISIBLE) {

                    time12.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime12.setTextColor(Color.GRAY);

                }
                time9.setBackgroundColor(Color.parseColor("#526CA5"));
                texttime9.setTextColor(Color.WHITE);

                Select_time = texttime9.getText().toString();

                String[] splitTime = Select_time.split("-");
                String sSplitTimeValue = splitTime[0];
                try {
                    SimpleDateFormat displayFormat = new SimpleDateFormat("HH");
                    SimpleDateFormat parseFormat = new SimpleDateFormat("hh a", Locale.US);
                    Date date = parseFormat.parse(sSplitTimeValue);
                    sSplitTime = "";
                    sSplitTime = displayFormat.format(date) + ":00";

                } catch (java.text.ParseException e) {
                    e.printStackTrace();
                }

            }
        });
        time10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (texttime1.getVisibility() == View.VISIBLE) {
                    time1.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime1.setTextColor(Color.GRAY);
                }
                if (texttime3.getVisibility() == View.VISIBLE) {

                    time3.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime3.setTextColor(Color.GRAY);

                }
                if (texttime4.getVisibility() == View.VISIBLE) {

                    time4.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime4.setTextColor(Color.GRAY);

                }
                if (texttime5.getVisibility() == View.VISIBLE) {

                    time5.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime5.setTextColor(Color.GRAY);

                }
                if (texttime6.getVisibility() == View.VISIBLE) {

                    time6.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime6.setTextColor(Color.GRAY);

                }
                if (texttime7.getVisibility() == View.VISIBLE) {

                    time7.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime7.setTextColor(Color.GRAY);

                }
                if (texttime8.getVisibility() == View.VISIBLE) {

                    time8.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime8.setTextColor(Color.GRAY);

                }
                if (texttime9.getVisibility() == View.VISIBLE) {

                    time9.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime9.setTextColor(Color.GRAY);

                }
                if (texttime2.getVisibility() == View.VISIBLE) {

                    time2.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime2.setTextColor(Color.GRAY);

                }
                if (texttime11.getVisibility() == View.VISIBLE) {

                    time11.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime11.setTextColor(Color.GRAY);

                }
                if (texttime12.getVisibility() == View.VISIBLE) {

                    time12.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime12.setTextColor(Color.GRAY);

                }
                time10.setBackgroundColor(Color.parseColor("#526CA5"));
                texttime10.setTextColor(Color.WHITE);

                Select_time = texttime10.getText().toString();

                String[] splitTime = Select_time.split("-");
                String sSplitTimeValue = splitTime[0];
                try {
                    SimpleDateFormat displayFormat = new SimpleDateFormat("HH");
                    SimpleDateFormat parseFormat = new SimpleDateFormat("hh a", Locale.US);
                    Date date = parseFormat.parse(sSplitTimeValue);
                    sSplitTime = "";
                    sSplitTime = displayFormat.format(date) + ":00";

                } catch (java.text.ParseException e) {
                    e.printStackTrace();
                }

            }
        });

        time11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (texttime1.getVisibility() == View.VISIBLE) {
                    time1.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime1.setTextColor(Color.GRAY);
                }
                if (texttime3.getVisibility() == View.VISIBLE) {

                    time3.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime3.setTextColor(Color.GRAY);

                }
                if (texttime4.getVisibility() == View.VISIBLE) {

                    time4.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime4.setTextColor(Color.GRAY);

                }
                if (texttime5.getVisibility() == View.VISIBLE) {

                    time5.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime5.setTextColor(Color.GRAY);

                }
                if (texttime6.getVisibility() == View.VISIBLE) {

                    time6.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime6.setTextColor(Color.GRAY);

                }
                if (texttime7.getVisibility() == View.VISIBLE) {

                    time7.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime7.setTextColor(Color.GRAY);

                }
                if (texttime8.getVisibility() == View.VISIBLE) {

                    time8.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime8.setTextColor(Color.GRAY);

                }
                if (texttime9.getVisibility() == View.VISIBLE) {

                    time9.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime9.setTextColor(Color.GRAY);

                }
                if (texttime10.getVisibility() == View.VISIBLE) {

                    time10.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime10.setTextColor(Color.GRAY);

                }
                if (texttime2.getVisibility() == View.VISIBLE) {

                    time2.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime2.setTextColor(Color.GRAY);

                }
                if (texttime12.getVisibility() == View.VISIBLE) {

                    time12.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime12.setTextColor(Color.GRAY);

                }
                time11.setBackgroundColor(Color.parseColor("#526CA5"));
                texttime11.setTextColor(Color.WHITE);

                Select_time = texttime11.getText().toString();

                String[] splitTime = Select_time.split("-");
                String sSplitTimeValue = splitTime[0];
                try {
                    SimpleDateFormat displayFormat = new SimpleDateFormat("HH");
                    SimpleDateFormat parseFormat = new SimpleDateFormat("hh a", Locale.US);
                    Date date = parseFormat.parse(sSplitTimeValue);
                    sSplitTime = "";
                    sSplitTime = displayFormat.format(date) + ":00";

                } catch (java.text.ParseException e) {
                    e.printStackTrace();
                }

            }
        });


        time12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (texttime1.getVisibility() == View.VISIBLE) {
                    time1.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime1.setTextColor(Color.GRAY);
                }
                if (texttime3.getVisibility() == View.VISIBLE) {

                    time3.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime3.setTextColor(Color.GRAY);

                }
                if (texttime4.getVisibility() == View.VISIBLE) {

                    time4.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime4.setTextColor(Color.GRAY);

                }
                if (texttime5.getVisibility() == View.VISIBLE) {

                    time5.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime5.setTextColor(Color.GRAY);

                }
                if (texttime6.getVisibility() == View.VISIBLE) {

                    time6.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime6.setTextColor(Color.GRAY);

                }
                if (texttime7.getVisibility() == View.VISIBLE) {

                    time7.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime7.setTextColor(Color.GRAY);

                }
                if (texttime8.getVisibility() == View.VISIBLE) {

                    time8.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime8.setTextColor(Color.GRAY);

                }
                if (texttime9.getVisibility() == View.VISIBLE) {

                    time9.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime9.setTextColor(Color.GRAY);

                }
                if (texttime10.getVisibility() == View.VISIBLE) {

                    time10.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime10.setTextColor(Color.GRAY);

                }
                if (texttime11.getVisibility() == View.VISIBLE) {

                    time11.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime11.setTextColor(Color.GRAY);

                }
                if (texttime2.getVisibility() == View.VISIBLE) {

                    time2.setBackgroundColor(Color.parseColor("#f7fbfc"));
                    texttime2.setTextColor(Color.GRAY);

                }
                time12.setBackgroundColor(Color.parseColor("#526CA5"));
                texttime12.setTextColor(Color.WHITE);

                Select_time = texttime12.getText().toString();

                String[] splitTime = Select_time.split("-");
                String sSplitTimeValue = splitTime[0];
                try {
                    SimpleDateFormat displayFormat = new SimpleDateFormat("HH");
                    SimpleDateFormat parseFormat = new SimpleDateFormat("hh a", Locale.US);
                    Date date = parseFormat.parse(sSplitTimeValue);
                    sSplitTime = "";
                    sSplitTime = displayFormat.format(date) + ":00";

                } catch (java.text.ParseException e) {
                    e.printStackTrace();
                }

            }
        });


        mcalendar.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {


                SelectDate = String.valueOf(formatter.format(date.getDate()));
                choosedate = Integer.parseInt(formatter1.format(date.getDate()));
                choosemonth = Integer.parseInt(formatter2.format(date.getDate()));
                System.out.println("Date : " + SelectDate);
                Dateselect();
            }
        });


        Rl_AddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (sessionManager.isLoggedIn()) {
                    gps = new GPSTracker(NewAppointmentpage.this);
                    if (gps.isgpsenabled() && gps.canGetLocation()) {
                        Intent intent = new Intent(NewAppointmentpage.this, AddAddressPage.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                    } else {
                        enableGpsService();
                    }
                } else {
                    Intent intent = new Intent(NewAppointmentpage.this, LogInPage.class);
                    intent.putExtra("IntentClass", "2");
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                }

            }
        });


        Rl_moreAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(addressList.size()>0){
                    moreAddressDialog();
                }


            }
        });

        Search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= 23) {
                    // Marshmallow+
                    if (!checkAccessFineLocationPermission() || !checkAccessCoarseLocationPermission()) {
                        requestPermission();
                    } else {
                        bookJob();
                    }
                } else {
                    bookJob();
                }
            }
        });

//        Et_instruction.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//
//                scrollView.smoothScrollTo(0, Et_instruction.getBottom());
//                scrollView.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        Et_instruction.requestFocus();
//
//                    }
//                });
//                return false;
//            }
//        });

        viewtype_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int selectedid = radioGroup.getCheckedRadioButtonId();
                if (selectedid == list_type.getId()) {
                    radio_button_value = true;
                    radio_list = true;
                    radio_map = false;
                } else if (selectedid == map_type.getId()) {
                    radio_button_value = true;
                    radio_map = true;
                    radio_list = false;
                }
            }
        });

    }


    private void initializeHeaderBar() {
        RelativeLayout headerBar = (RelativeLayout) findViewById(R.id.headerBar_layout);
        Rl_back = (RelativeLayout) headerBar.findViewById(R.id.headerBar_left_layout);
        Im_backIcon = (ImageView) headerBar.findViewById(R.id.headerBar_imageView);
        Tv_headerTitle = (TextView) headerBar.findViewById(R.id.headerBar_title_textView);

        Tv_headerTitle.setText(getResources().getString(R.string.appointment_label_header_textView));
        Im_backIcon.setImageResource(R.drawable.back_arrow);
    }


    public void initialize() {

        cd = new ConnectionDetector(NewAppointmentpage.this);
        isInternetPresent = cd.isConnectingToInternet();
        sessionManager = new SessionManager(NewAppointmentpage.this);
        addressList = new ArrayList<AddressListPojo>();
        gps = new GPSTracker(getApplicationContext());

        mGoogleApiClient = new GoogleApiClient.Builder(NewAppointmentpage.this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        mGoogleApiClient.connect();


        HashMap<String, String> user = sessionManager.getUserDetails();
        UserID = user.get(SessionManager.KEY_USER_ID);

        displayaddress = sessionManager.getUserDetails();
        String dis = displayaddress.get(SessionManager.displayAddress_name);

        //--------------------Get Intent Value--------------
        Intent intent = getIntent();
        selectedlocation=intent.getExtras().getString("location");
        city=intent.getExtras().getString("city");
        state=intent.getExtras().getString("state");
        country=intent.getExtras().getString("country");
        postalcode=intent.getExtras().getString("postalcode");
        sLatitude=intent.getExtras().getString("latitude");
        sLongitude=intent.getExtras().getString("longintude");
        sCategoryId = intent.getStringExtra("IntentCategoryID");
        sServiceId = intent.getStringExtra("IntentServiceID");
        System.out.println("sServiceId------------" + sServiceId);
        System.out.println("sCategoryId------------" + sCategoryId);

        list_type = (RadioButton) findViewById(R.id.radio_list);
        viewtype_group = (RadioGroup) findViewById(R.id.type_group);
        map_type = (RadioButton) findViewById(R.id.radio_map);
        time1 = (RelativeLayout) findViewById(R.id.time1);
        time2 = (RelativeLayout) findViewById(R.id.time2);
        time3 = (RelativeLayout) findViewById(R.id.time3);
        time4 = (RelativeLayout) findViewById(R.id.time4);
        time5 = (RelativeLayout) findViewById(R.id.time5);
        time6 = (RelativeLayout) findViewById(R.id.time6);
        time7 = (RelativeLayout) findViewById(R.id.time7);
        time8 = (RelativeLayout) findViewById(R.id.time8);
        time9 = (RelativeLayout) findViewById(R.id.time9);
        time10 = (RelativeLayout) findViewById(R.id.time10);
        time11 = (RelativeLayout) findViewById(R.id.time11);
        time12 = (RelativeLayout) findViewById(R.id.time12);

        texttime1 = (TextView) findViewById(R.id.texttime1);
        texttime2 = (TextView) findViewById(R.id.texttime2);
        texttime3 = (TextView) findViewById(R.id.texttime3);
        texttime4 = (TextView) findViewById(R.id.texttime4);
        texttime5 = (TextView) findViewById(R.id.texttime5);
        texttime6 = (TextView) findViewById(R.id.texttime6);
        texttime7 = (TextView) findViewById(R.id.texttime7);
        texttime8 = (TextView) findViewById(R.id.texttime8);
        texttime9 = (TextView) findViewById(R.id.texttime9);
        texttime10 = (TextView) findViewById(R.id.texttime10);
        texttime11 = (TextView) findViewById(R.id.texttime11);
        texttime12 = (TextView) findViewById(R.id.texttime12);
        mcalendar = (MaterialCalendarView) findViewById(R.id.appointment_page_calendarview);
        visibletime = (TextView) findViewById(R.id.texttimevisible);
        visibletime2 = (TextView) findViewById(R.id.texttimevisible2);
        visibletime3 = (TextView) findViewById(R.id.texttimevisible3);
        visibletime4 = (TextView) findViewById(R.id.texttimevisible4);
        visibletime5 = (TextView) findViewById(R.id.texttimevisible5);
        visibletime6 = (TextView) findViewById(R.id.texttimevisible6);
        visibletime7 = (TextView) findViewById(R.id.texttimevisible7);
        visibletime8 = (TextView) findViewById(R.id.texttimevisible8);
        visibletime9 = (TextView) findViewById(R.id.texttimevisible9);
        visibletime10 = (TextView) findViewById(R.id.texttimevisible10);
        visibletime11 = (TextView) findViewById(R.id.texttimevisible11);
        visibletime12 = (TextView) findViewById(R.id.texttimevisible12);
        Rl_AddAddress = (RelativeLayout) findViewById(R.id.add_address);
        Rl_yourAddress = (RelativeLayout) findViewById(R.id.appointment_page_display_address_layout);
        Tv_yourAddress = (TextView) findViewById(R.id.appointment_page_your_address_textView);
        Rl_moreAddress = (RelativeLayout) findViewById(R.id.appointment_page_more_address_layout);
        Search_button = (Button) findViewById(R.id.searchbutton);
        Et_instruction = (EditText) findViewById(R.id.appointment_page_instruction_editText);
        scrollView = (ScrollView) findViewById(R.id.scrollview);
        String sTodayDate = new SimpleDateFormat("dd").format(new Date());
        Rl_yourAddress.setVisibility(View.VISIBLE);
        String selected_address= getCompleteAddressString(Double.parseDouble(sLatitude),Double.parseDouble(sLongitude));
        Tv_yourAddress.setText(selected_address);

        // -----code to refresh drawer using broadcast receiver-----
        refreshReceiver = new RefreshReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.package.ACTION_CLASS_APPOINTMENT_REFRESH");
        registerReceiver(refreshReceiver, intentFilter);


        if (isInternetPresent) {
            addressList_Request(NewAppointmentpage.this, Iconstant.address_list_url);
        } else {
            alert(getResources().getString(R.string.action_no_internet_title), getResources().getString(R.string.action_no_internet_message));
        }

        timeset();
        mcalendar.setSelectedDate(new Date());

        SelectDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        mcalendar.setDateSelected(new Date(), true);
        mcalendar.state().edit()
                .setMinimumDate(new Date())
                .setFirstDayOfWeek(Calendar.MONDAY)
                .commit();

    }


    public void timeset() {

        String sTodayDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        System.out.println("CurrentDate : " + sTodayDate);

        SimpleDateFormat mTime_Formatter = new SimpleDateFormat("HH");
        sCurrentTime = mTime_Formatter.format(new Date());


//        if (sTodayDate.equalsIgnoreCase(sTodayDate)) {
//            SimpleDateFormat mTime_Formatter = new SimpleDateFormat("HH");
//            String sCurrentTime = mTime_Formatter.format(new Date());
//
//
//    }

        if (Integer.parseInt(sCurrentTime) == 8) {
            time1.setEnabled(false);
            texttime1.setVisibility(View.INVISIBLE);
            visibletime.setVisibility(View.VISIBLE);
            time2.setBackgroundColor(Color.parseColor("#526CA5"));
            texttime2.setTextColor(Color.WHITE);
            buttoncheck1 = false;

            Select_time = texttime2.getText().toString();

            String[] splitTime = Select_time.split("-");
            String sSplitTimeValue = splitTime[0];
            try {
                SimpleDateFormat displayFormat = new SimpleDateFormat("HH");
                SimpleDateFormat parseFormat = new SimpleDateFormat("hh a", Locale.US);
                Date date = parseFormat.parse(sSplitTimeValue);
                sSplitTime = "";
                sSplitTime = displayFormat.format(date) + ":00";

            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }

        } else if (Integer.parseInt(sCurrentTime) == 9) {
            time1.setEnabled(false);
            texttime1.setVisibility(View.INVISIBLE);
            visibletime.setVisibility(View.VISIBLE);
            time2.setEnabled(false);
            texttime2.setVisibility(View.INVISIBLE);
            visibletime2.setVisibility(View.VISIBLE);
            time3.setBackgroundColor(Color.parseColor("#526CA5"));
            texttime3.setTextColor(Color.WHITE);
            buttoncheck12 = false;

            Select_time = texttime3.getText().toString();

            String[] splitTime = Select_time.split("-");
            String sSplitTimeValue = splitTime[0];
            try {
                SimpleDateFormat displayFormat = new SimpleDateFormat("HH");
                SimpleDateFormat parseFormat = new SimpleDateFormat("hh a", Locale.US);
                Date date = parseFormat.parse(sSplitTimeValue);
                sSplitTime = "";
                sSplitTime = displayFormat.format(date) + ":00";

            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }


        } else if (Integer.parseInt(sCurrentTime) == 10) {
            time1.setEnabled(false);
            texttime1.setVisibility(View.INVISIBLE);
            visibletime.setVisibility(View.VISIBLE);
            time2.setEnabled(false);
            texttime2.setVisibility(View.INVISIBLE);
            visibletime2.setVisibility(View.VISIBLE);
            time3.setEnabled(false);
            texttime3.setVisibility(View.INVISIBLE);
            visibletime3.setVisibility(View.VISIBLE);
            time4.setBackgroundColor(Color.parseColor("#526CA5"));
            texttime4.setTextColor(Color.WHITE);
            buttoncheck3 = false;

            Select_time = texttime4.getText().toString();

            String[] splitTime = Select_time.split("-");
            String sSplitTimeValue = splitTime[0];
            try {
                SimpleDateFormat displayFormat = new SimpleDateFormat("HH");
                SimpleDateFormat parseFormat = new SimpleDateFormat("hh a", Locale.US);
                Date date = parseFormat.parse(sSplitTimeValue);
                sSplitTime = "";
                sSplitTime = displayFormat.format(date) + ":00";

            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }

        } else if (Integer.parseInt(sCurrentTime) == 11) {
            time1.setEnabled(false);
            texttime1.setVisibility(View.INVISIBLE);
            visibletime.setVisibility(View.VISIBLE);
            time2.setEnabled(false);
            texttime2.setVisibility(View.INVISIBLE);
            visibletime2.setVisibility(View.VISIBLE);
            time3.setEnabled(false);
            texttime3.setVisibility(View.INVISIBLE);
            visibletime3.setVisibility(View.VISIBLE);
            time4.setEnabled(false);
            texttime4.setVisibility(View.INVISIBLE);
            visibletime4.setVisibility(View.VISIBLE);

            time5.setBackgroundColor(Color.parseColor("#526CA5"));
            texttime5.setTextColor(Color.WHITE);
            buttoncheck4 = false;

            Select_time = texttime5.getText().toString();

            String[] splitTime = Select_time.split("-");
            String sSplitTimeValue = splitTime[0];
            try {
                SimpleDateFormat displayFormat = new SimpleDateFormat("HH");
                SimpleDateFormat parseFormat = new SimpleDateFormat("hh a", Locale.US);
                Date date = parseFormat.parse(sSplitTimeValue);
                sSplitTime = "";
                sSplitTime = displayFormat.format(date) + ":00";

            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }

        } else if (Integer.parseInt(sCurrentTime) == 12) {
            time1.setEnabled(false);
            texttime1.setVisibility(View.INVISIBLE);
            visibletime.setVisibility(View.VISIBLE);
            time2.setEnabled(false);
            texttime2.setVisibility(View.INVISIBLE);
            visibletime2.setVisibility(View.VISIBLE);
            time3.setEnabled(false);
            texttime3.setVisibility(View.INVISIBLE);
            visibletime3.setVisibility(View.VISIBLE);
            time4.setEnabled(false);
            texttime4.setVisibility(View.INVISIBLE);
            visibletime4.setVisibility(View.VISIBLE);
            time5.setEnabled(false);
            texttime5.setVisibility(View.INVISIBLE);
            visibletime5.setVisibility(View.VISIBLE);

            time6.setBackgroundColor(Color.parseColor("#526CA5"));
            texttime6.setTextColor(Color.WHITE);
            buttoncheck5 = false;

            Select_time = texttime6.getText().toString();

            String[] splitTime = Select_time.split("-");
            String sSplitTimeValue = splitTime[0];
            try {
                SimpleDateFormat displayFormat = new SimpleDateFormat("HH");
                SimpleDateFormat parseFormat = new SimpleDateFormat("hh a", Locale.US);
                Date date = parseFormat.parse(sSplitTimeValue);
                sSplitTime = "";
                sSplitTime = displayFormat.format(date) + ":00";

            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }


        } else if (Integer.parseInt(sCurrentTime) == 13) {
            time1.setEnabled(false);
            texttime1.setVisibility(View.INVISIBLE);
            visibletime.setVisibility(View.VISIBLE);
            time2.setEnabled(false);
            texttime2.setVisibility(View.INVISIBLE);
            visibletime2.setVisibility(View.VISIBLE);
            time3.setEnabled(false);
            texttime3.setVisibility(View.INVISIBLE);
            visibletime3.setVisibility(View.VISIBLE);
            time4.setEnabled(false);
            texttime4.setVisibility(View.INVISIBLE);
            visibletime4.setVisibility(View.VISIBLE);
            time5.setEnabled(false);
            texttime5.setVisibility(View.INVISIBLE);
            visibletime5.setVisibility(View.VISIBLE);
            time6.setEnabled(false);
            texttime6.setVisibility(View.INVISIBLE);
            visibletime6.setVisibility(View.VISIBLE);

            time7.setBackgroundColor(Color.parseColor("#526CA5"));
            texttime7.setTextColor(Color.WHITE);

            buttoncheck6 = false;

            Select_time = texttime7.getText().toString();

            String[] splitTime = Select_time.split("-");
            String sSplitTimeValue = splitTime[0];
            try {
                SimpleDateFormat displayFormat = new SimpleDateFormat("HH");
                SimpleDateFormat parseFormat = new SimpleDateFormat("hh a", Locale.US);
                Date date = parseFormat.parse(sSplitTimeValue);
                sSplitTime = "";
                sSplitTime = displayFormat.format(date) + ":00";

            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }
        } else if (Integer.parseInt(sCurrentTime) == 14) {
            time1.setEnabled(false);
            texttime1.setVisibility(View.INVISIBLE);
            visibletime.setVisibility(View.VISIBLE);
            time2.setEnabled(false);
            texttime2.setVisibility(View.INVISIBLE);
            visibletime2.setVisibility(View.VISIBLE);
            time3.setEnabled(false);
            texttime3.setVisibility(View.INVISIBLE);
            visibletime3.setVisibility(View.VISIBLE);
            time4.setEnabled(false);
            texttime4.setVisibility(View.INVISIBLE);
            visibletime4.setVisibility(View.VISIBLE);
            time5.setEnabled(false);
            texttime5.setVisibility(View.INVISIBLE);
            visibletime5.setVisibility(View.VISIBLE);
            time6.setEnabled(false);
            texttime6.setVisibility(View.INVISIBLE);
            visibletime6.setVisibility(View.VISIBLE);
            time7.setEnabled(false);
            texttime7.setVisibility(View.INVISIBLE);
            visibletime7.setVisibility(View.VISIBLE);

            time8.setBackgroundColor(Color.parseColor("#526CA5"));
            texttime8.setTextColor(Color.WHITE);
            buttoncheck7 = false;

            Select_time = texttime8.getText().toString();

            String[] splitTime = Select_time.split("-");
            String sSplitTimeValue = splitTime[0];
            try {
                SimpleDateFormat displayFormat = new SimpleDateFormat("HH");
                SimpleDateFormat parseFormat = new SimpleDateFormat("hh a", Locale.US);
                Date date = parseFormat.parse(sSplitTimeValue);
                sSplitTime = "";
                sSplitTime = displayFormat.format(date) + ":00";

            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }

        } else if (Integer.parseInt(sCurrentTime) == 15) {
            time1.setEnabled(false);
            texttime1.setVisibility(View.INVISIBLE);
            visibletime.setVisibility(View.VISIBLE);
            time2.setEnabled(false);
            texttime2.setVisibility(View.INVISIBLE);
            visibletime2.setVisibility(View.VISIBLE);
            time3.setEnabled(false);
            texttime3.setVisibility(View.INVISIBLE);
            visibletime3.setVisibility(View.VISIBLE);
            time4.setEnabled(false);
            texttime4.setVisibility(View.INVISIBLE);
            visibletime4.setVisibility(View.VISIBLE);
            time5.setEnabled(false);
            texttime5.setVisibility(View.INVISIBLE);
            visibletime5.setVisibility(View.VISIBLE);
            time6.setEnabled(false);
            texttime6.setVisibility(View.INVISIBLE);
            visibletime6.setVisibility(View.VISIBLE);
            time7.setEnabled(false);
            texttime7.setVisibility(View.INVISIBLE);
            visibletime7.setVisibility(View.VISIBLE);
            time8.setEnabled(false);
            texttime8.setVisibility(View.INVISIBLE);
            visibletime8.setVisibility(View.VISIBLE);

            time9.setBackgroundColor(Color.parseColor("#526CA5"));
            texttime9.setTextColor(Color.WHITE);
            buttoncheck8 = false;

            Select_time = texttime9.getText().toString();

            String[] splitTime = Select_time.split("-");
            String sSplitTimeValue = splitTime[0];
            try {
                SimpleDateFormat displayFormat = new SimpleDateFormat("HH");
                SimpleDateFormat parseFormat = new SimpleDateFormat("hh a", Locale.US);
                Date date = parseFormat.parse(sSplitTimeValue);
                sSplitTime = "";
                sSplitTime = displayFormat.format(date) + ":00";

            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }

        } else if (Integer.parseInt(sCurrentTime) == 16) {
            time1.setEnabled(false);
            texttime1.setVisibility(View.INVISIBLE);
            visibletime.setVisibility(View.VISIBLE);
            time2.setEnabled(false);
            texttime2.setVisibility(View.INVISIBLE);
            visibletime2.setVisibility(View.VISIBLE);
            time3.setEnabled(false);
            texttime3.setVisibility(View.INVISIBLE);
            visibletime3.setVisibility(View.VISIBLE);
            time4.setEnabled(false);
            texttime4.setVisibility(View.INVISIBLE);
            visibletime4.setVisibility(View.VISIBLE);
            time5.setEnabled(false);
            texttime5.setVisibility(View.INVISIBLE);
            visibletime5.setVisibility(View.VISIBLE);
            time6.setEnabled(false);
            texttime6.setVisibility(View.INVISIBLE);
            visibletime6.setVisibility(View.VISIBLE);
            time7.setEnabled(false);
            texttime7.setVisibility(View.INVISIBLE);
            visibletime7.setVisibility(View.VISIBLE);
            time8.setEnabled(false);
            texttime8.setVisibility(View.INVISIBLE);
            visibletime8.setVisibility(View.VISIBLE);
            time9.setEnabled(false);
            texttime9.setVisibility(View.INVISIBLE);
            visibletime9.setVisibility(View.VISIBLE);

            time10.setBackgroundColor(Color.parseColor("#526CA5"));
            texttime10.setTextColor(Color.WHITE);
            buttoncheck9 = false;

            Select_time = texttime10.getText().toString();

            String[] splitTime = Select_time.split("-");
            String sSplitTimeValue = splitTime[0];
            try {
                SimpleDateFormat displayFormat = new SimpleDateFormat("HH");
                SimpleDateFormat parseFormat = new SimpleDateFormat("hh a", Locale.US);
                Date date = parseFormat.parse(sSplitTimeValue);
                sSplitTime = "";
                sSplitTime = displayFormat.format(date) + ":00";

            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }

        } else if (Integer.parseInt(sCurrentTime) == 17) {
            time1.setEnabled(false);
            texttime1.setVisibility(View.INVISIBLE);
            visibletime.setVisibility(View.VISIBLE);
            time2.setEnabled(false);
            texttime2.setVisibility(View.INVISIBLE);
            visibletime2.setVisibility(View.VISIBLE);
            time3.setEnabled(false);
            texttime3.setVisibility(View.INVISIBLE);
            visibletime3.setVisibility(View.VISIBLE);
            time4.setEnabled(false);
            texttime4.setVisibility(View.INVISIBLE);
            visibletime4.setVisibility(View.VISIBLE);
            time5.setEnabled(false);
            texttime5.setVisibility(View.INVISIBLE);
            visibletime5.setVisibility(View.VISIBLE);
            time6.setEnabled(false);
            texttime6.setVisibility(View.INVISIBLE);
            visibletime6.setVisibility(View.VISIBLE);
            time7.setEnabled(false);
            texttime7.setVisibility(View.INVISIBLE);
            visibletime7.setVisibility(View.VISIBLE);
            time8.setEnabled(false);
            texttime8.setVisibility(View.INVISIBLE);
            visibletime8.setVisibility(View.VISIBLE);
            time9.setEnabled(false);
            texttime9.setVisibility(View.INVISIBLE);
            visibletime9.setVisibility(View.VISIBLE);
            time10.setEnabled(false);
            texttime10.setVisibility(View.INVISIBLE);
            visibletime10.setVisibility(View.VISIBLE);

            time11.setBackgroundColor(Color.parseColor("#526CA5"));
            texttime11.setTextColor(Color.WHITE);
            buttoncheck10 = false;

            Select_time = texttime12.getText().toString();

            String[] splitTime = Select_time.split("-");
            String sSplitTimeValue = splitTime[0];
            try {
                SimpleDateFormat displayFormat = new SimpleDateFormat("HH");
                SimpleDateFormat parseFormat = new SimpleDateFormat("hh a", Locale.US);
                Date date = parseFormat.parse(sSplitTimeValue);
                sSplitTime = "";
                sSplitTime = displayFormat.format(date) + ":00";

            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }

        } else if (Integer.parseInt(sCurrentTime) == 18) {
            time1.setEnabled(false);
            texttime1.setVisibility(View.INVISIBLE);
            visibletime.setVisibility(View.VISIBLE);
            time2.setEnabled(false);
            texttime2.setVisibility(View.INVISIBLE);
            visibletime2.setVisibility(View.VISIBLE);
            time3.setEnabled(false);
            texttime3.setVisibility(View.INVISIBLE);
            visibletime3.setVisibility(View.VISIBLE);
            time4.setEnabled(false);
            texttime4.setVisibility(View.INVISIBLE);
            visibletime4.setVisibility(View.VISIBLE);
            time5.setEnabled(false);
            texttime5.setVisibility(View.INVISIBLE);
            visibletime5.setVisibility(View.VISIBLE);
            time6.setEnabled(false);
            texttime6.setVisibility(View.INVISIBLE);
            visibletime6.setVisibility(View.VISIBLE);
            time7.setEnabled(false);
            texttime7.setVisibility(View.INVISIBLE);
            visibletime7.setVisibility(View.VISIBLE);
            time8.setEnabled(false);
            texttime8.setVisibility(View.INVISIBLE);
            visibletime8.setVisibility(View.VISIBLE);
            time9.setEnabled(false);
            texttime9.setVisibility(View.INVISIBLE);
            visibletime9.setVisibility(View.VISIBLE);
            time10.setEnabled(false);
            texttime10.setVisibility(View.INVISIBLE);
            visibletime10.setVisibility(View.VISIBLE);
            time11.setEnabled(false);
            texttime11.setVisibility(View.INVISIBLE);
            visibletime11.setVisibility(View.VISIBLE);

            time12.setBackgroundColor(Color.parseColor("#526CA5"));
            texttime12.setTextColor(Color.WHITE);

            buttoncheck11 = false;

            Select_time = texttime12.getText().toString();

            String[] splitTime = Select_time.split("-");
            String sSplitTimeValue = splitTime[0];
            try {
                SimpleDateFormat displayFormat = new SimpleDateFormat("HH");
                SimpleDateFormat parseFormat = new SimpleDateFormat("hh a", Locale.US);
                Date date = parseFormat.parse(sSplitTimeValue);
                sSplitTime = "";
                sSplitTime = displayFormat.format(date) + ":00";

            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }

        } else if (Integer.parseInt(sCurrentTime) == 19) {
            time1.setEnabled(false);
            texttime1.setVisibility(View.INVISIBLE);
            visibletime.setVisibility(View.VISIBLE);
            time2.setEnabled(false);
            texttime2.setVisibility(View.INVISIBLE);
            visibletime2.setVisibility(View.VISIBLE);
            time3.setEnabled(false);
            texttime3.setVisibility(View.INVISIBLE);
            visibletime3.setVisibility(View.VISIBLE);
            time4.setEnabled(false);
            texttime4.setVisibility(View.INVISIBLE);
            visibletime4.setVisibility(View.VISIBLE);
            time5.setEnabled(false);
            texttime5.setVisibility(View.INVISIBLE);
            visibletime5.setVisibility(View.VISIBLE);
            time6.setEnabled(false);
            texttime6.setVisibility(View.INVISIBLE);
            visibletime6.setVisibility(View.VISIBLE);
            time7.setEnabled(false);
            texttime7.setVisibility(View.INVISIBLE);
            visibletime7.setVisibility(View.VISIBLE);
            time8.setEnabled(false);
            texttime8.setVisibility(View.INVISIBLE);
            visibletime8.setVisibility(View.VISIBLE);
            time9.setEnabled(false);
            texttime9.setVisibility(View.INVISIBLE);
            visibletime9.setVisibility(View.VISIBLE);
            time10.setEnabled(false);
            texttime10.setVisibility(View.INVISIBLE);
            visibletime10.setVisibility(View.VISIBLE);
            time11.setEnabled(false);
            texttime11.setVisibility(View.INVISIBLE);
            visibletime11.setVisibility(View.VISIBLE);

            time12.setEnabled(false);
            texttime12.setVisibility(View.INVISIBLE);
            visibletime12.setVisibility(View.VISIBLE);

            alert("Sorry", getResources().getString(R.string.time_not_avilable));


        } else if (Integer.parseInt(sCurrentTime) > 19) {

            time1.setEnabled(false);
            texttime1.setVisibility(View.INVISIBLE);
            visibletime.setVisibility(View.VISIBLE);
            time2.setEnabled(false);
            texttime2.setVisibility(View.INVISIBLE);
            visibletime2.setVisibility(View.VISIBLE);
            time3.setEnabled(false);
            texttime3.setVisibility(View.INVISIBLE);
            visibletime3.setVisibility(View.VISIBLE);
            time4.setEnabled(false);
            texttime4.setVisibility(View.INVISIBLE);
            visibletime4.setVisibility(View.VISIBLE);
            time5.setEnabled(false);
            texttime5.setVisibility(View.INVISIBLE);
            visibletime5.setVisibility(View.VISIBLE);
            time6.setEnabled(false);
            texttime6.setVisibility(View.INVISIBLE);
            visibletime6.setVisibility(View.VISIBLE);
            time7.setEnabled(false);
            texttime7.setVisibility(View.INVISIBLE);
            visibletime7.setVisibility(View.VISIBLE);
            time8.setEnabled(false);
            texttime8.setVisibility(View.INVISIBLE);
            visibletime8.setVisibility(View.VISIBLE);
            time9.setEnabled(false);
            texttime9.setVisibility(View.INVISIBLE);
            visibletime9.setVisibility(View.VISIBLE);
            time10.setEnabled(false);
            texttime10.setVisibility(View.INVISIBLE);
            visibletime10.setVisibility(View.VISIBLE);
            time11.setEnabled(false);
            texttime11.setVisibility(View.INVISIBLE);
            visibletime11.setVisibility(View.VISIBLE);

            time12.setEnabled(false);
            texttime12.setVisibility(View.INVISIBLE);
            visibletime12.setVisibility(View.VISIBLE);

            alert("Sorry", getResources().getString(R.string.time_not_avilable));
        }

    }

    //--------------More Address Select Method-----------
    private void moreAddressDialog() {
        //--------Adjusting Dialog width-----
        DisplayMetrics metrics = NewAppointmentpage.this.getResources().getDisplayMetrics();
        int screenWidth = (int) (metrics.widthPixels * 0.80);//fill only 80% of the screen
        moreAddressView = View.inflate(NewAppointmentpage.this, R.layout.appointment_more_address_dialog, null);
        moreAddressDialog = new Dialog(NewAppointmentpage.this);
        moreAddressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        moreAddressDialog.setContentView(moreAddressView);
        moreAddressDialog.setCanceledOnTouchOutside(true);
        moreAddressDialog.getWindow().setLayout(screenWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        moreAddressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ListView addressListView = (ListView) moreAddressView.findViewById(R.id.appointment_more_address_listView);
        moreAddressView.findViewById(R.id.addAddressLayout);
        ImageView add_address = (ImageView) moreAddressView.findViewById(R.id.add_address);
        Button addbutton = (Button) moreAddressView.findViewById(R.id.image_upload);

        add_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sessionManager.isLoggedIn()) {
                    gps = new GPSTracker(NewAppointmentpage.this);
                    if (gps.isgpsenabled() && gps.canGetLocation()) {
                        if(addressList.size()<5){
                            sSelectedAddressId = "";
                            Intent intent = new Intent(NewAppointmentpage.this, AddAddressPage.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                            getApplicationContext().startActivity(intent);
                            moreAddressDialog.dismiss();
                        }
                        else{
                            alert(getResources().getString(R.string.action_sorry), getResources().getString(R.string.choose_address_warning_alert));
                        }

                    } else {
                    }
                } else {
                    Intent intent = new Intent(NewAppointmentpage.this, LogInPage.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    intent.putExtra("IntentClass", "2");
                    getApplicationContext().startActivity(intent);
                }
            }
        });

        addressAdapter = new AppointmentMoreAddressAdapter(NewAppointmentpage.this, addressList);
        addressListView.setAdapter(addressAdapter);

        System.out.println("addresslist---------------" + addressList);

        moreAddressDialog.show();

        addressListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                   list_select_value=true;
                for (int i = 0; i < addressList.size(); i++) {
                    if (position == i) {

                        addresslistposition = position;
                        sSelectedAddressId = addressList.get(position).getAddress_name();

                        DisplayAddressId = addressList.get(position).getAddress_name();
                        sessionManager.setDisplayAddress_name(DisplayAddressId);
                        addressList.get(i).setAddressSelected(true);

                        //addressList_Request(AppointmentPage.this, Iconstant.address_list_url);
                        Listviewclickaddress(NewAppointmentpage.this, Iconstant.List_Address_Url);
                    } else {
                        addressList.get(i).setAddressSelected(false);
                    }
                }
                sSelectedAddressId = addressList.get(position).getAddress_name();
                String aAddress = "";
                Address=getCompleteAddressString(Double.parseDouble(addressList.get(position).getLatitude()),Double.parseDouble(addressList.get(position).getLongitude()));

                if (addressList.get(position).getLandmark().equals("")) {


                    if (addressList.get(position).getLocality().equals("")) {

                        aAddress = addressList.get(position).getName() + "\n" + addressList.get(position).getStreet();
//                                + "\n" + addressList.get(position).getCity() + "\n" + "Zipcode"
//                                + "-" + addressList.get(position).getZipCode();

                    } else {
                        aAddress = addressList.get(position).getName() + "\n" + addressList.get(position).getStreet();
//                                + "\n" + addressList.get(position).getCity() + "\n" + addressList.get(position).getLocality() + "\n" + "Zipcode"
//                                + "-" + addressList.get(position).getZipCode();

                    }


                } else if (addressList.get(position).getLocality().equals("")) {

                    aAddress = addressList.get(position).getName() + "\n" + addressList.get(position).getStreet();
//                            + "\n" + addressList.get(position).getCity() + "\n" + "Zipcode"
//                            + "-" + addressList.get(position).getZipCode() + getResources().getString(R.string.appointment_label_landmark)
//                            + " " + addressList.get(position).getLandmark();

                } else


                {

                    aAddress = addressList.get(position).getName() + "\n" + addressList.get(position).getStreet();
//                            + "\n" + addressList.get(position).getCity() + "\n" + addressList.get(position).getLocality() + "\n" + "Zipcode"
//                            + "-" + addressList.get(position).getZipCode()
//
//                            + getResources().getString(R.string.appointment_label_landmark)
//                            + " " + addressList.get(position).getLandmark();
                }
                sLatitude = addressList.get(position).getLatitude();
                sLongitude = addressList.get(position).getLongitude();
                Tv_yourAddress.setText(Address);
                moreAddressDialog.dismiss();
                addressAdapter.notifyDataSetChanged();
            }
        });
    }


    //-----------------------Display Address List Post Request-----------------
    private void Listviewclickaddress(Context mContext, String Url) {

        mLoadingDialog = new LoadingDialog(mContext);
        mLoadingDialog.setLoadingTitle(getResources().getString(R.string.action_loading));
        mLoadingDialog.show();

        System.out.println("-------------addressList_Request Url----------------" + Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", UserID);
        jsonParams.put("address_name", sSelectedAddressId);

        System.out.println("address name---------------" + sSelectedAddressId);

        mRequest = new ServiceRequest(mContext);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("-------------addressList_Request Response----------------" + response);
                Log.e("addressList Response", response);


                mLoadingDialog.dismiss();
            }

            @Override
            public void onErrorListener() {
                mLoadingDialog.dismiss();
            }
        });
    }


    public void deleteAddressDialog(String sAddressName) {
        cd = new ConnectionDetector(NewAppointmentpage.this);
        isInternetPresent = cd.isConnectingToInternet();

        if (isInternetPresent) {
            deleteAddress_Request(NewAppointmentpage.this, Iconstant.delete_address_url, sAddressName);
        } else {
            alert(getResources().getString(R.string.action_no_internet_title), getResources().getString(R.string.action_no_internet_message));
        }
    }


    //--------------Alert Method-----------
    private void alert(String title, String alert) {

        final PkDialog mDialog = new PkDialog(NewAppointmentpage.this);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(alert);
        mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                if (str_scroll_bottom.equalsIgnoreCase("scroll bottom")) {
                    str_scroll_bottom = "";
                    ScrollToBottom();
                }
            }
        });
        mDialog.show();
    }



    private void ScrollToBottom() {
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }


    //-------------------Delete Address Post Request----------------

    private void deleteAddress_Request(Context mContext, String Url, String sAddressName) {
        System.out.println("--------------Delete Address code url-------------------" + Url);
        System.out.println("--------------Delete Address sAddressName-------------------" + sAddressName);

        mLoadingDialog = new LoadingDialog(mContext);
        mLoadingDialog.setLoadingTitle(getResources().getString(R.string.appointment_action_delete_address));
        mLoadingDialog.show();

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", UserID);
        jsonParams.put("address_name", sAddressName);

        System.out.println("user_id----" + UserID);

        System.out.println("address_name---------" + sAddressName);

        mRequest = new ServiceRequest(mContext);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("--------------Delete Address code response-------------------" + response);

                try {
                    JSONObject object = new JSONObject(response);
                    if (object.length() > 0) {
                        String status = object.getString("status");
                        if (status.equalsIgnoreCase("1")) {
                            refreshDeleteAddress_Request(NewAppointmentpage.this, Iconstant.address_list_url);
                        } else {
                            String sResponse = object.getString("response");
                            alert(getResources().getString(R.string.action_sorry), sResponse);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    mLoadingDialog.dismiss();
                }
            }

            @Override
            public void onErrorListener() {
                mLoadingDialog.dismiss();
            }
        });
    }

    //-------------------Refresh Delete Address Post Request----------------

    private void refreshDeleteAddress_Request(Context mContext, String Url) {

        System.out.println("--------------refresh Delete Address Url-------------------" + Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", UserID);

        mRequest = new ServiceRequest(mContext);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("--------------refresh Delete Address code response-------------------" + response);

                String sStatus = "";
                try {

                    JSONObject object = new JSONObject(response);
                    sStatus = object.getString("status");
                    if (sStatus.equalsIgnoreCase("1")) {


                        if (object.length() > 0) {
                            Object check_response_object = object.get("response");
                            if (check_response_object instanceof JSONArray) {
                                JSONArray response_Array = object.getJSONArray("response");
                                if (response_Array.length() > 0) {
                                    addressList.clear();
                                    for (int i = 0; i < response_Array.length(); i++) {
                                        JSONObject response_Object = response_Array.getJSONObject(i);
                                        AddressListPojo pojo = new AddressListPojo();
                                        pojo.setAddress_name(response_Object.getString("address_name"));
                                        pojo.setName(response_Object.getString("name"));
                                        pojo.setEmail(response_Object.getString("email"));
                                        //    pojo.setCountry_code(getStringForJSON("country_code", response_Object));
                                        pojo.setMobile(response_Object.getString("mobile"));
                                        pojo.setStreet(response_Object.getString("street"));
                                        pojo.setCity(response_Object.getString("city"));
                                        pojo.setLandmark(response_Object.getString("landmark"));
                                        pojo.setLocality(response_Object.getString("locality"));
                                        pojo.setZipCode(response_Object.getString("zipcode"));
                                        pojo.setLongitude(response_Object.getString("lng"));
                                        pojo.setLatitude(response_Object.getString("lat"));
                                        Address=getCompleteAddressString(Double.parseDouble(response_Object.getString("lat")),Double.parseDouble(response_Object.getString("lng")));
                                        pojo.setAddress(Address);
                                        if (i == 0) {
                                            pojo.setAddressSelected(true);
                                            String countrycode = getStringForJSON("country_code", response_Object);
                                            sSelectedAddressId = response_Object.getString("address_name");

                                            sDisplayAddress = response_Object.getString("name") + "\n" + response_Object.getString("street");
//                                                    + "\n" + response_Object.getString("city") + "\n" + response_Object.getString("locality") + "\n" + "Zipcode"
//                                                    + "-" + response_Object.getString("zipcode") + "\n" + countrycode
//                                                    + "-" + response_Object.getString("mobile") + "\n"
//                                                    + getResources().getString(R.string.appointment_label_landmark)
//                                                    + " " + response_Object.getString("landmark");
                                        } else {
                                            pojo.setAddressSelected(false);
                                        }

                                        addressList.add(pojo);
                                    }
                                    isDataPresent = true;
                                } else {
                                    isDataPresent = false;
                                }
                            } else {
                                isDataPresent = false;
                            }
                        } else {
                            isDataPresent = false;
                        }

                    } else if (sStatus.equalsIgnoreCase("0")) {
                        isDataPresent = false;
                    }
                    System.out.println("isDataPresent-------------" + isDataPresent);

                    System.out.println("sStatus-------------" + sStatus);

                    if (isDataPresent) {
                        addressAdapter.notifyDataSetChanged();

                        if (addressList.size() > 0 && addressList != null) {
                            Rl_yourAddress.setVisibility(View.VISIBLE);
                            Tv_yourAddress.setText(selectedlocation);
                        } else {
                            moreAddressDialog.dismiss();
                            Rl_yourAddress.setVisibility(View.GONE);
                            Rl_AddAddress.setVisibility(View.VISIBLE);
                            //Tv_yourAddress.setText("");
                        }
                        alert(getResources().getString(R.string.action_success), getResources().getString(R.string.appointment_label_remove_address));

                    } else if (!isDataPresent && sStatus.equals("0")) {
                        addressAdapter.notifyDataSetChanged();
                        Rl_AddAddress.setVisibility(View.VISIBLE);
                        moreAddressDialog.dismiss();
                        Rl_yourAddress.setVisibility(View.GONE);
                        Tv_yourAddress.setText("");

                        alert(getResources().getString(R.string.action_success), getResources().getString(R.string.appointment_label_remove_address));
                    }


                } catch (JSONException e) {
                    mLoadingDialog.dismiss();
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


    //-------------------------------------------------------------------------------Date Selected-----------------------------------------------------

    public void Dateselect() {

        String sTodayDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        System.out.println("CurrentDate : " + sTodayDate);

//        SimpleDateFormat mTime_Formatter = new SimpleDateFormat("HH");
//        sCurrentTime = mTime_Formatter.format(new Date());
        String previousdate = new SimpleDateFormat("dd").format(new Date());

        int prevdate = Integer.parseInt(previousdate);
        String month = new SimpleDateFormat("MM").format(new Date());
        int monthdetails = Integer.parseInt(month);


        if (prevdate > choosedate && choosemonth <= monthdetails) {

//---------------------------------------------------Background-------------------------------------------
            time1.setBackgroundColor(Color.parseColor("#f7fbfc"));
            texttime1.setTextColor(Color.GRAY);
            time2.setBackgroundColor(Color.parseColor("#f7fbfc"));
            texttime2.setTextColor(Color.GRAY);
            time3.setBackgroundColor(Color.parseColor("#f7fbfc"));
            texttime3.setTextColor(Color.GRAY);
            time4.setBackgroundColor(Color.parseColor("#f7fbfc"));
            texttime4.setTextColor(Color.GRAY);
            time5.setBackgroundColor(Color.parseColor("#f7fbfc"));
            texttime5.setTextColor(Color.GRAY);
            time6.setBackgroundColor(Color.parseColor("#f7fbfc"));
            texttime6.setTextColor(Color.GRAY);
            time7.setBackgroundColor(Color.parseColor("#f7fbfc"));
            texttime7.setTextColor(Color.GRAY);
            time8.setBackgroundColor(Color.parseColor("#f7fbfc"));
            texttime8.setTextColor(Color.GRAY);
            time9.setBackgroundColor(Color.parseColor("#f7fbfc"));
            texttime9.setTextColor(Color.GRAY);
            time10.setBackgroundColor(Color.parseColor("#f7fbfc"));
            texttime10.setTextColor(Color.GRAY);
            time11.setBackgroundColor(Color.parseColor("#f7fbfc"));
            texttime11.setTextColor(Color.GRAY);
            time12.setBackgroundColor(Color.parseColor("#f7fbfc"));
            texttime12.setTextColor(Color.GRAY);

            //---------------------------------------------------Disable Date-------------------------------------------
            time1.setEnabled(false);
            texttime1.setVisibility(View.INVISIBLE);
            visibletime.setVisibility(View.VISIBLE);
            time2.setEnabled(false);
            texttime2.setVisibility(View.INVISIBLE);
            visibletime2.setVisibility(View.VISIBLE);
            time3.setEnabled(false);
            texttime3.setVisibility(View.INVISIBLE);
            visibletime3.setVisibility(View.VISIBLE);
            time4.setEnabled(false);
            texttime4.setVisibility(View.INVISIBLE);
            visibletime4.setVisibility(View.VISIBLE);
            time5.setEnabled(false);
            texttime5.setVisibility(View.INVISIBLE);
            visibletime5.setVisibility(View.VISIBLE);
            time6.setEnabled(false);
            texttime6.setVisibility(View.INVISIBLE);
            visibletime6.setVisibility(View.VISIBLE);
            time7.setEnabled(false);
            texttime7.setVisibility(View.INVISIBLE);
            visibletime7.setVisibility(View.VISIBLE);
            time8.setEnabled(false);
            texttime8.setVisibility(View.INVISIBLE);
            visibletime8.setVisibility(View.VISIBLE);
            time9.setEnabled(false);
            texttime9.setVisibility(View.INVISIBLE);
            visibletime9.setVisibility(View.VISIBLE);
            time10.setEnabled(false);
            texttime10.setVisibility(View.INVISIBLE);
            visibletime10.setVisibility(View.VISIBLE);
            time11.setEnabled(false);
            texttime11.setVisibility(View.INVISIBLE);
            visibletime11.setVisibility(View.VISIBLE);

            time12.setEnabled(false);
            texttime12.setVisibility(View.INVISIBLE);
            visibletime12.setVisibility(View.VISIBLE);
            alert("Sorry", getResources().getString(R.string.time_not_avilable));
            //Toast.makeText(getApplicationContext(),"Time is Not Available in you Selected Date",Toast.LENGTH_LONG).show();
        } else {

            if (sTodayDate.equalsIgnoreCase(SelectDate)) {
                SimpleDateFormat mTime_Formatter = new SimpleDateFormat("HH");
                String sCurrentTime = mTime_Formatter.format(new Date());


                time1.setEnabled(true);
                texttime1.setEnabled(true);
                time2.setEnabled(true);
                texttime2.setEnabled(true);
                time3.setEnabled(true);
                texttime3.setEnabled(true);
                time4.setEnabled(true);
                texttime4.setEnabled(true);
                time5.setEnabled(true);
                texttime5.setEnabled(true);
                time6.setEnabled(true);
                texttime6.setEnabled(true);
                time7.setEnabled(true);
                texttime7.setEnabled(true);
                time8.setEnabled(true);
                texttime8.setEnabled(true);
                time9.setEnabled(true);
                texttime9.setEnabled(true);
                time10.setEnabled(true);
                texttime10.setEnabled(true);
                time11.setEnabled(true);
                texttime11.setEnabled(true);
                time12.setEnabled(true);
                texttime12.setEnabled(true);

                texttime1.setVisibility(View.VISIBLE);
                texttime2.setVisibility(View.VISIBLE);
                texttime3.setVisibility(View.VISIBLE);
                texttime4.setVisibility(View.VISIBLE);
                texttime5.setVisibility(View.VISIBLE);
                texttime6.setVisibility(View.VISIBLE);
                texttime7.setVisibility(View.VISIBLE);
                texttime8.setVisibility(View.VISIBLE);
                texttime9.setVisibility(View.VISIBLE);
                texttime10.setVisibility(View.VISIBLE);
                texttime11.setVisibility(View.VISIBLE);
                texttime12.setVisibility(View.VISIBLE);

                visibletime.setVisibility(View.INVISIBLE);
                visibletime2.setVisibility(View.INVISIBLE);
                visibletime3.setVisibility(View.INVISIBLE);
                visibletime4.setVisibility(View.INVISIBLE);
                visibletime5.setVisibility(View.INVISIBLE);
                visibletime6.setVisibility(View.INVISIBLE);
                visibletime7.setVisibility(View.INVISIBLE);
                visibletime8.setVisibility(View.INVISIBLE);
                visibletime9.setVisibility(View.INVISIBLE);
                visibletime10.setVisibility(View.INVISIBLE);
                visibletime11.setVisibility(View.INVISIBLE);
                visibletime12.setVisibility(View.INVISIBLE);


                time1.setBackgroundColor(Color.parseColor("#f7fbfc"));
                texttime1.setTextColor(Color.GRAY);
                time2.setBackgroundColor(Color.parseColor("#f7fbfc"));
                texttime2.setTextColor(Color.GRAY);
                time3.setBackgroundColor(Color.parseColor("#f7fbfc"));
                texttime3.setTextColor(Color.GRAY);
                time4.setBackgroundColor(Color.parseColor("#f7fbfc"));
                texttime4.setTextColor(Color.GRAY);
                time5.setBackgroundColor(Color.parseColor("#f7fbfc"));
                texttime5.setTextColor(Color.GRAY);
                time6.setBackgroundColor(Color.parseColor("#f7fbfc"));
                texttime6.setTextColor(Color.GRAY);
                time7.setBackgroundColor(Color.parseColor("#f7fbfc"));
                texttime7.setTextColor(Color.GRAY);
                time8.setBackgroundColor(Color.parseColor("#f7fbfc"));
                texttime8.setTextColor(Color.GRAY);
                time9.setBackgroundColor(Color.parseColor("#f7fbfc"));
                texttime9.setTextColor(Color.GRAY);
                time10.setBackgroundColor(Color.parseColor("#f7fbfc"));
                texttime10.setTextColor(Color.GRAY);
                time11.setBackgroundColor(Color.parseColor("#f7fbfc"));
                texttime11.setTextColor(Color.GRAY);
                time12.setBackgroundColor(Color.parseColor("#f7fbfc"));
                texttime12.setTextColor(Color.GRAY);


                if (Integer.parseInt(sCurrentTime) == 8) {
                    time1.setEnabled(false);
                    texttime1.setVisibility(View.INVISIBLE);
                    visibletime.setVisibility(View.VISIBLE);
                    time2.setBackgroundColor(Color.parseColor("#526CA5"));
                    texttime2.setTextColor(Color.WHITE);
                    buttoncheck1 = false;

                    Select_time = texttime2.getText().toString();

                    String[] splitTime = Select_time.split("-");
                    String sSplitTimeValue = splitTime[0];
                    try {
                        SimpleDateFormat displayFormat = new SimpleDateFormat("HH");
                        SimpleDateFormat parseFormat = new SimpleDateFormat("hh a", Locale.US);
                        Date date = parseFormat.parse(sSplitTimeValue);
                        sSplitTime = "";
                        sSplitTime = displayFormat.format(date) + ":00";

                    } catch (java.text.ParseException e) {
                        e.printStackTrace();
                    }


                } else if (Integer.parseInt(sCurrentTime) == 9) {
                    time1.setEnabled(false);
                    texttime1.setVisibility(View.INVISIBLE);
                    visibletime.setVisibility(View.VISIBLE);
                    time2.setEnabled(false);
                    texttime2.setVisibility(View.INVISIBLE);
                    visibletime2.setVisibility(View.VISIBLE);
                    time3.setBackgroundColor(Color.parseColor("#526CA5"));
                    texttime3.setTextColor(Color.WHITE);
                    buttoncheck12 = false;

                    Select_time = texttime3.getText().toString();

                    String[] splitTime = Select_time.split("-");
                    String sSplitTimeValue = splitTime[0];
                    try {
                        SimpleDateFormat displayFormat = new SimpleDateFormat("HH");
                        SimpleDateFormat parseFormat = new SimpleDateFormat("hh a", Locale.US);
                        Date date = parseFormat.parse(sSplitTimeValue);
                        sSplitTime = "";
                        sSplitTime = displayFormat.format(date) + ":00";

                    } catch (java.text.ParseException e) {
                        e.printStackTrace();
                    }


                } else if (Integer.parseInt(sCurrentTime) == 10) {
                    time1.setEnabled(false);
                    texttime1.setVisibility(View.INVISIBLE);
                    visibletime.setVisibility(View.VISIBLE);
                    time2.setEnabled(false);
                    texttime2.setVisibility(View.INVISIBLE);
                    visibletime2.setVisibility(View.VISIBLE);
                    time3.setEnabled(false);
                    texttime3.setVisibility(View.INVISIBLE);
                    visibletime3.setVisibility(View.VISIBLE);
                    time4.setBackgroundColor(Color.parseColor("#526CA5"));
                    texttime4.setTextColor(Color.WHITE);
                    buttoncheck3 = false;

                    Select_time = texttime4.getText().toString();

                    String[] splitTime = Select_time.split("-");
                    String sSplitTimeValue = splitTime[0];
                    try {
                        SimpleDateFormat displayFormat = new SimpleDateFormat("HH");
                        SimpleDateFormat parseFormat = new SimpleDateFormat("hh a", Locale.US);
                        Date date = parseFormat.parse(sSplitTimeValue);
                        sSplitTime = "";
                        sSplitTime = displayFormat.format(date) + ":00";

                    } catch (java.text.ParseException e) {
                        e.printStackTrace();
                    }

                } else if (Integer.parseInt(sCurrentTime) == 11) {
                    time1.setEnabled(false);
                    texttime1.setVisibility(View.INVISIBLE);
                    visibletime.setVisibility(View.VISIBLE);
                    time2.setEnabled(false);
                    texttime2.setVisibility(View.INVISIBLE);
                    visibletime2.setVisibility(View.VISIBLE);
                    time3.setEnabled(false);
                    texttime3.setVisibility(View.INVISIBLE);
                    visibletime3.setVisibility(View.VISIBLE);
                    time4.setEnabled(false);
                    texttime4.setVisibility(View.INVISIBLE);
                    visibletime4.setVisibility(View.VISIBLE);

                    time5.setBackgroundColor(Color.parseColor("#526CA5"));
                    texttime5.setTextColor(Color.WHITE);
                    buttoncheck4 = false;

                    Select_time = texttime5.getText().toString();

                    String[] splitTime = Select_time.split("-");
                    String sSplitTimeValue = splitTime[0];
                    try {
                        SimpleDateFormat displayFormat = new SimpleDateFormat("HH");
                        SimpleDateFormat parseFormat = new SimpleDateFormat("hh a", Locale.US);
                        Date date = parseFormat.parse(sSplitTimeValue);
                        sSplitTime = "";
                        sSplitTime = displayFormat.format(date) + ":00";

                    } catch (java.text.ParseException e) {
                        e.printStackTrace();
                    }

                } else if (Integer.parseInt(sCurrentTime) == 12) {
                    time1.setEnabled(false);
                    texttime1.setVisibility(View.INVISIBLE);
                    visibletime.setVisibility(View.VISIBLE);
                    time2.setEnabled(false);
                    texttime2.setVisibility(View.INVISIBLE);
                    visibletime2.setVisibility(View.VISIBLE);
                    time3.setEnabled(false);
                    texttime3.setVisibility(View.INVISIBLE);
                    visibletime3.setVisibility(View.VISIBLE);
                    time4.setEnabled(false);
                    texttime4.setVisibility(View.INVISIBLE);
                    visibletime4.setVisibility(View.VISIBLE);
                    time5.setEnabled(false);
                    texttime5.setVisibility(View.INVISIBLE);
                    visibletime5.setVisibility(View.VISIBLE);

                    time6.setBackgroundColor(Color.parseColor("#526CA5"));
                    texttime6.setTextColor(Color.WHITE);
                    buttoncheck5 = false;

                    Select_time = texttime6.getText().toString();

                    String[] splitTime = Select_time.split("-");
                    String sSplitTimeValue = splitTime[0];
                    try {
                        SimpleDateFormat displayFormat = new SimpleDateFormat("HH");
                        SimpleDateFormat parseFormat = new SimpleDateFormat("hh a", Locale.US);
                        Date date = parseFormat.parse(sSplitTimeValue);
                        sSplitTime = "";
                        sSplitTime = displayFormat.format(date) + ":00";

                    } catch (java.text.ParseException e) {
                        e.printStackTrace();
                    }


                } else if (Integer.parseInt(sCurrentTime) == 13) {
                    time1.setEnabled(false);
                    texttime1.setVisibility(View.INVISIBLE);
                    visibletime.setVisibility(View.VISIBLE);
                    time2.setEnabled(false);
                    texttime2.setVisibility(View.INVISIBLE);
                    visibletime2.setVisibility(View.VISIBLE);
                    time3.setEnabled(false);
                    texttime3.setVisibility(View.INVISIBLE);
                    visibletime3.setVisibility(View.VISIBLE);
                    time4.setEnabled(false);
                    texttime4.setVisibility(View.INVISIBLE);
                    visibletime4.setVisibility(View.VISIBLE);
                    time5.setEnabled(false);
                    texttime5.setVisibility(View.INVISIBLE);
                    visibletime5.setVisibility(View.VISIBLE);
                    time6.setEnabled(false);
                    texttime6.setVisibility(View.INVISIBLE);
                    visibletime6.setVisibility(View.VISIBLE);

                    time7.setBackgroundColor(Color.parseColor("#526CA5"));
                    texttime7.setTextColor(Color.WHITE);

                    buttoncheck6 = false;

                    Select_time = texttime7.getText().toString();

                    String[] splitTime = Select_time.split("-");
                    String sSplitTimeValue = splitTime[0];
                    try {
                        SimpleDateFormat displayFormat = new SimpleDateFormat("HH");
                        SimpleDateFormat parseFormat = new SimpleDateFormat("hh a", Locale.US);
                        Date date = parseFormat.parse(sSplitTimeValue);
                        sSplitTime = "";
                        sSplitTime = displayFormat.format(date) + ":00";

                    } catch (java.text.ParseException e) {
                        e.printStackTrace();
                    }
                } else if (Integer.parseInt(sCurrentTime) == 14) {
                    time1.setEnabled(false);
                    texttime1.setVisibility(View.INVISIBLE);
                    visibletime.setVisibility(View.VISIBLE);
                    time2.setEnabled(false);
                    texttime2.setVisibility(View.INVISIBLE);
                    visibletime2.setVisibility(View.VISIBLE);
                    time3.setEnabled(false);
                    texttime3.setVisibility(View.INVISIBLE);
                    visibletime3.setVisibility(View.VISIBLE);
                    time4.setEnabled(false);
                    texttime4.setVisibility(View.INVISIBLE);
                    visibletime4.setVisibility(View.VISIBLE);
                    time5.setEnabled(false);
                    texttime5.setVisibility(View.INVISIBLE);
                    visibletime5.setVisibility(View.VISIBLE);
                    time6.setEnabled(false);
                    texttime6.setVisibility(View.INVISIBLE);
                    visibletime6.setVisibility(View.VISIBLE);
                    time7.setEnabled(false);
                    texttime7.setVisibility(View.INVISIBLE);
                    visibletime7.setVisibility(View.VISIBLE);

                    time8.setBackgroundColor(Color.parseColor("#526CA5"));
                    texttime8.setTextColor(Color.WHITE);
                    buttoncheck7 = false;

                    Select_time = texttime8.getText().toString();

                    String[] splitTime = Select_time.split("-");
                    String sSplitTimeValue = splitTime[0];
                    try {
                        SimpleDateFormat displayFormat = new SimpleDateFormat("HH");
                        SimpleDateFormat parseFormat = new SimpleDateFormat("hh a", Locale.US);
                        Date date = parseFormat.parse(sSplitTimeValue);
                        sSplitTime = "";
                        sSplitTime = displayFormat.format(date) + ":00";

                    } catch (java.text.ParseException e) {
                        e.printStackTrace();
                    }

                } else if (Integer.parseInt(sCurrentTime) == 15) {
                    time1.setEnabled(false);
                    texttime1.setVisibility(View.INVISIBLE);
                    visibletime.setVisibility(View.VISIBLE);
                    time2.setEnabled(false);
                    texttime2.setVisibility(View.INVISIBLE);
                    visibletime2.setVisibility(View.VISIBLE);
                    time3.setEnabled(false);
                    texttime3.setVisibility(View.INVISIBLE);
                    visibletime3.setVisibility(View.VISIBLE);
                    time4.setEnabled(false);
                    texttime4.setVisibility(View.INVISIBLE);
                    visibletime4.setVisibility(View.VISIBLE);
                    time5.setEnabled(false);
                    texttime5.setVisibility(View.INVISIBLE);
                    visibletime5.setVisibility(View.VISIBLE);
                    time6.setEnabled(false);
                    texttime6.setVisibility(View.INVISIBLE);
                    visibletime6.setVisibility(View.VISIBLE);
                    time7.setEnabled(false);
                    texttime7.setVisibility(View.INVISIBLE);
                    visibletime7.setVisibility(View.VISIBLE);
                    time8.setEnabled(false);
                    texttime8.setVisibility(View.INVISIBLE);
                    visibletime8.setVisibility(View.VISIBLE);

                    time9.setBackgroundColor(Color.parseColor("#526CA5"));
                    texttime9.setTextColor(Color.WHITE);
                    buttoncheck8 = false;

                    Select_time = texttime9.getText().toString();

                    String[] splitTime = Select_time.split("-");
                    String sSplitTimeValue = splitTime[0];
                    try {
                        SimpleDateFormat displayFormat = new SimpleDateFormat("HH");
                        SimpleDateFormat parseFormat = new SimpleDateFormat("hh a", Locale.US);
                        Date date = parseFormat.parse(sSplitTimeValue);
                        sSplitTime = "";
                        sSplitTime = displayFormat.format(date) + ":00";

                    } catch (java.text.ParseException e) {
                        e.printStackTrace();
                    }

                } else if (Integer.parseInt(sCurrentTime) == 16) {
                    time1.setEnabled(false);
                    texttime1.setVisibility(View.INVISIBLE);
                    visibletime.setVisibility(View.VISIBLE);
                    time2.setEnabled(false);
                    texttime2.setVisibility(View.INVISIBLE);
                    visibletime2.setVisibility(View.VISIBLE);
                    time3.setEnabled(false);
                    texttime3.setVisibility(View.INVISIBLE);
                    visibletime3.setVisibility(View.VISIBLE);
                    time4.setEnabled(false);
                    texttime4.setVisibility(View.INVISIBLE);
                    visibletime4.setVisibility(View.VISIBLE);
                    time5.setEnabled(false);
                    texttime5.setVisibility(View.INVISIBLE);
                    visibletime5.setVisibility(View.VISIBLE);
                    time6.setEnabled(false);
                    texttime6.setVisibility(View.INVISIBLE);
                    visibletime6.setVisibility(View.VISIBLE);
                    time7.setEnabled(false);
                    texttime7.setVisibility(View.INVISIBLE);
                    visibletime7.setVisibility(View.VISIBLE);
                    time8.setEnabled(false);
                    texttime8.setVisibility(View.INVISIBLE);
                    visibletime8.setVisibility(View.VISIBLE);
                    time9.setEnabled(false);
                    texttime9.setVisibility(View.INVISIBLE);
                    visibletime9.setVisibility(View.VISIBLE);

                    time10.setBackgroundColor(Color.parseColor("#526CA5"));
                    texttime10.setTextColor(Color.WHITE);
                    buttoncheck9 = false;

                    Select_time = texttime10.getText().toString();

                    String[] splitTime = Select_time.split("-");
                    String sSplitTimeValue = splitTime[0];
                    try {
                        SimpleDateFormat displayFormat = new SimpleDateFormat("HH");
                        SimpleDateFormat parseFormat = new SimpleDateFormat("hh a", Locale.US);
                        Date date = parseFormat.parse(sSplitTimeValue);
                        sSplitTime = "";
                        sSplitTime = displayFormat.format(date) + ":00";

                    } catch (java.text.ParseException e) {
                        e.printStackTrace();
                    }

                } else if (Integer.parseInt(sCurrentTime) == 17) {
                    time1.setEnabled(false);
                    texttime1.setVisibility(View.INVISIBLE);
                    visibletime.setVisibility(View.VISIBLE);
                    time2.setEnabled(false);
                    texttime2.setVisibility(View.INVISIBLE);
                    visibletime2.setVisibility(View.VISIBLE);
                    time3.setEnabled(false);
                    texttime3.setVisibility(View.INVISIBLE);
                    visibletime3.setVisibility(View.VISIBLE);
                    time4.setEnabled(false);
                    texttime4.setVisibility(View.INVISIBLE);
                    visibletime4.setVisibility(View.VISIBLE);
                    time5.setEnabled(false);
                    texttime5.setVisibility(View.INVISIBLE);
                    visibletime5.setVisibility(View.VISIBLE);
                    time6.setEnabled(false);
                    texttime6.setVisibility(View.INVISIBLE);
                    visibletime6.setVisibility(View.VISIBLE);
                    time7.setEnabled(false);
                    texttime7.setVisibility(View.INVISIBLE);
                    visibletime7.setVisibility(View.VISIBLE);
                    time8.setEnabled(false);
                    texttime8.setVisibility(View.INVISIBLE);
                    visibletime8.setVisibility(View.VISIBLE);
                    time9.setEnabled(false);
                    texttime9.setVisibility(View.INVISIBLE);
                    visibletime9.setVisibility(View.VISIBLE);
                    time10.setEnabled(false);
                    texttime10.setVisibility(View.INVISIBLE);
                    visibletime10.setVisibility(View.VISIBLE);

                    time11.setBackgroundColor(Color.parseColor("#526CA5"));
                    texttime11.setTextColor(Color.WHITE);
                    buttoncheck10 = false;

                    Select_time = texttime11.getText().toString();

                    String[] splitTime = Select_time.split("-");
                    String sSplitTimeValue = splitTime[0];
                    try {
                        SimpleDateFormat displayFormat = new SimpleDateFormat("HH");
                        SimpleDateFormat parseFormat = new SimpleDateFormat("hh a", Locale.US);
                        Date date = parseFormat.parse(sSplitTimeValue);
                        sSplitTime = "";
                        sSplitTime = displayFormat.format(date) + ":00";

                    } catch (java.text.ParseException e) {
                        e.printStackTrace();
                    }

                } else if (Integer.parseInt(sCurrentTime) == 18) {
                    time1.setEnabled(false);
                    texttime1.setVisibility(View.INVISIBLE);
                    visibletime.setVisibility(View.VISIBLE);
                    time2.setEnabled(false);
                    texttime2.setVisibility(View.INVISIBLE);
                    visibletime2.setVisibility(View.VISIBLE);
                    time3.setEnabled(false);
                    texttime3.setVisibility(View.INVISIBLE);
                    visibletime3.setVisibility(View.VISIBLE);
                    time4.setEnabled(false);
                    texttime4.setVisibility(View.INVISIBLE);
                    visibletime4.setVisibility(View.VISIBLE);
                    time5.setEnabled(false);
                    texttime5.setVisibility(View.INVISIBLE);
                    visibletime5.setVisibility(View.VISIBLE);
                    time6.setEnabled(false);
                    texttime6.setVisibility(View.INVISIBLE);
                    visibletime6.setVisibility(View.VISIBLE);
                    time7.setEnabled(false);
                    texttime7.setVisibility(View.INVISIBLE);
                    visibletime7.setVisibility(View.VISIBLE);
                    time8.setEnabled(false);
                    texttime8.setVisibility(View.INVISIBLE);
                    visibletime8.setVisibility(View.VISIBLE);
                    time9.setEnabled(false);
                    texttime9.setVisibility(View.INVISIBLE);
                    visibletime9.setVisibility(View.VISIBLE);
                    time10.setEnabled(false);
                    texttime10.setVisibility(View.INVISIBLE);
                    visibletime10.setVisibility(View.VISIBLE);
                    time11.setEnabled(false);
                    texttime11.setVisibility(View.INVISIBLE);
                    visibletime11.setVisibility(View.VISIBLE);

                    time12.setBackgroundColor(Color.parseColor("#526CA5"));
                    texttime12.setTextColor(Color.WHITE);

                    buttoncheck11 = false;

                    Select_time = texttime12.getText().toString();

                    String[] splitTime = Select_time.split("-");
                    String sSplitTimeValue = splitTime[0];
                    try {
                        SimpleDateFormat displayFormat = new SimpleDateFormat("HH");
                        SimpleDateFormat parseFormat = new SimpleDateFormat("hh a", Locale.US);
                        Date date = parseFormat.parse(sSplitTimeValue);
                        sSplitTime = "";
                        sSplitTime = displayFormat.format(date) + ":00";

                    } catch (java.text.ParseException e) {
                        e.printStackTrace();
                    }

                } else if (Integer.parseInt(sCurrentTime) == 19) {
                    time1.setEnabled(false);
                    texttime1.setVisibility(View.INVISIBLE);
                    visibletime.setVisibility(View.VISIBLE);
                    time2.setEnabled(false);
                    texttime2.setVisibility(View.INVISIBLE);
                    visibletime2.setVisibility(View.VISIBLE);
                    time3.setEnabled(false);
                    texttime3.setVisibility(View.INVISIBLE);
                    visibletime3.setVisibility(View.VISIBLE);
                    time4.setEnabled(false);
                    texttime4.setVisibility(View.INVISIBLE);
                    visibletime4.setVisibility(View.VISIBLE);
                    time5.setEnabled(false);
                    texttime5.setVisibility(View.INVISIBLE);
                    visibletime5.setVisibility(View.VISIBLE);
                    time6.setEnabled(false);
                    texttime6.setVisibility(View.INVISIBLE);
                    visibletime6.setVisibility(View.VISIBLE);
                    time7.setEnabled(false);
                    texttime7.setVisibility(View.INVISIBLE);
                    visibletime7.setVisibility(View.VISIBLE);
                    time8.setEnabled(false);
                    texttime8.setVisibility(View.INVISIBLE);
                    visibletime8.setVisibility(View.VISIBLE);
                    time9.setEnabled(false);
                    texttime9.setVisibility(View.INVISIBLE);
                    visibletime9.setVisibility(View.VISIBLE);
                    time10.setEnabled(false);
                    texttime10.setVisibility(View.INVISIBLE);
                    visibletime10.setVisibility(View.VISIBLE);
                    time11.setEnabled(false);
                    texttime11.setVisibility(View.INVISIBLE);
                    visibletime11.setVisibility(View.VISIBLE);

                    time12.setEnabled(false);
                    texttime12.setVisibility(View.INVISIBLE);
                    visibletime12.setVisibility(View.VISIBLE);

                    alert("Sorry", getResources().getString(R.string.time_not_avilable));
                }

            } else {
                time1.setBackgroundColor(Color.parseColor("#526CA5"));
                texttime1.setTextColor(Color.WHITE);
                time1.setEnabled(true);
                texttime1.setEnabled(true);
                time2.setEnabled(true);
                texttime2.setEnabled(true);
                time3.setEnabled(true);
                texttime3.setEnabled(true);
                time4.setEnabled(true);
                texttime4.setEnabled(true);
                time5.setEnabled(true);
                texttime5.setEnabled(true);
                time6.setEnabled(true);
                texttime6.setEnabled(true);
                time7.setEnabled(true);
                texttime7.setEnabled(true);
                time8.setEnabled(true);
                texttime8.setEnabled(true);
                time9.setEnabled(true);
                texttime9.setEnabled(true);
                time10.setEnabled(true);
                texttime10.setEnabled(true);
                time11.setEnabled(true);
                texttime11.setEnabled(true);
                time12.setEnabled(true);
                texttime12.setEnabled(true);

                texttime1.setVisibility(View.VISIBLE);
                texttime2.setVisibility(View.VISIBLE);
                texttime3.setVisibility(View.VISIBLE);
                texttime4.setVisibility(View.VISIBLE);
                texttime5.setVisibility(View.VISIBLE);
                texttime6.setVisibility(View.VISIBLE);
                texttime7.setVisibility(View.VISIBLE);
                texttime8.setVisibility(View.VISIBLE);
                texttime9.setVisibility(View.VISIBLE);
                texttime10.setVisibility(View.VISIBLE);
                texttime11.setVisibility(View.VISIBLE);
                texttime12.setVisibility(View.VISIBLE);

                visibletime.setVisibility(View.INVISIBLE);
                visibletime2.setVisibility(View.INVISIBLE);
                visibletime3.setVisibility(View.INVISIBLE);
                visibletime4.setVisibility(View.INVISIBLE);
                visibletime5.setVisibility(View.INVISIBLE);
                visibletime6.setVisibility(View.INVISIBLE);
                visibletime7.setVisibility(View.INVISIBLE);
                visibletime8.setVisibility(View.INVISIBLE);
                visibletime9.setVisibility(View.INVISIBLE);
                visibletime10.setVisibility(View.INVISIBLE);
                visibletime11.setVisibility(View.INVISIBLE);
                visibletime12.setVisibility(View.INVISIBLE);


                time2.setBackgroundColor(Color.parseColor("#f7fbfc"));
                texttime2.setTextColor(Color.GRAY);
                time3.setBackgroundColor(Color.parseColor("#f7fbfc"));
                texttime3.setTextColor(Color.GRAY);
                time4.setBackgroundColor(Color.parseColor("#f7fbfc"));
                texttime4.setTextColor(Color.GRAY);
                time5.setBackgroundColor(Color.parseColor("#f7fbfc"));
                texttime5.setTextColor(Color.GRAY);
                time6.setBackgroundColor(Color.parseColor("#f7fbfc"));
                texttime6.setTextColor(Color.GRAY);
                time7.setBackgroundColor(Color.parseColor("#f7fbfc"));
                texttime7.setTextColor(Color.GRAY);
                time8.setBackgroundColor(Color.parseColor("#f7fbfc"));
                texttime8.setTextColor(Color.GRAY);
                time9.setBackgroundColor(Color.parseColor("#f7fbfc"));
                texttime9.setTextColor(Color.GRAY);
                time10.setBackgroundColor(Color.parseColor("#f7fbfc"));
                texttime10.setTextColor(Color.GRAY);
                time11.setBackgroundColor(Color.parseColor("#f7fbfc"));
                texttime11.setTextColor(Color.GRAY);
                time12.setBackgroundColor(Color.parseColor("#f7fbfc"));
                texttime12.setTextColor(Color.GRAY);

                Select_time = texttime1.getText().toString();

                String[] splitTime = Select_time.split("-");
                String sSplitTimeValue = splitTime[0];
                try {
                    SimpleDateFormat displayFormat = new SimpleDateFormat("HH");
                    SimpleDateFormat parseFormat = new SimpleDateFormat("hh a", Locale.US);
                    Date date = parseFormat.parse(sSplitTimeValue);
                    sSplitTime = "";
                    sSplitTime = displayFormat.format(date) + ":00";

                } catch (java.text.ParseException e) {
                    e.printStackTrace();
                }

            }

        }


    }

    private void bookJob() {

        Listviewclickaddress(NewAppointmentpage.this, Iconstant.List_Address_Url);

        gps = new GPSTracker(NewAppointmentpage.this);
        if (gps.isgpsenabled() && gps.canGetLocation()) {

            if (SelectDate.equalsIgnoreCase("")) {
                alert("", getResources().getString(R.string.appointment_label_select_date_alert));
            } else if (Select_time.equalsIgnoreCase("")) {
                alert("", getResources().getString(R.string.appointment_label_select_time_alert));
            } else if (Tv_yourAddress.getText().toString().length() == 0) {
                alert("", getResources().getString(R.string.appointment_label_select_address_alert));
            } else if (Et_instruction.getText().toString().length() == 0) {
                str_scroll_bottom = "scroll bottom";
                alert("", getResources().getString(R.string.appointment_label_select_instruction_alert));
            }

            else {

                cd = new ConnectionDetector(NewAppointmentpage.this);
                isInternetPresent = cd.isConnectingToInternet();
                Intent i = new Intent(NewAppointmentpage.this, ProvidersList.class);
                i.putExtra("user_id", UserID);
                i.putExtra("address_name", sSelectedAddressId);
                i.putExtra("category", sCategoryId);
                i.putExtra("pickup_date", SelectDate);
                i.putExtra("pickup_time", sSplitTime);
                i.putExtra("instruction", Et_instruction.getText().toString());
                i.putExtra("service", sServiceId);
                i.putExtra("lat", sLatitude);
                i.putExtra("long", sLongitude);
                startActivity(i);
                Et_instruction.setText("");
            }

        } else {
            enableGpsService();
        }
    }

    private void enableGpsService() {

        mGoogleApiClient = new GoogleApiClient.Builder(NewAppointmentpage.this)
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
                            status.startResolutionForResult(NewAppointmentpage.this, REQUEST_LOCATION);
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


    //-------------------------------------------------------------------Address_list_Request-----------------------------------------------


    //-----------------------Display Address List Post Request-----------------
    private void addressList_Request(Context mContext, String Url) {

        mLoadingDialog = new LoadingDialog(mContext);
        mLoadingDialog.setLoadingTitle(getResources().getString(R.string.action_loading));
        mLoadingDialog.show();

        System.out.println("-------------addressList_Request Url----------------" + Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", UserID);
        jsonParams.put("address_name", sSelectedAddressId);

        System.out.println("address name---------------" + sSelectedAddressId);

        mRequest = new ServiceRequest(mContext);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("-------------addressList_Request Response----------------" + response);
                Log.e("addressList Response", response);

                String sStatus = "";
                String country_code = "";
                try {

                    JSONObject object = new JSONObject(response);
                    sStatus = object.getString("status");
                    if (object.length() > 0) {
                        Object check_response_object = object.get("response");
                        if (check_response_object instanceof JSONArray) {
                            JSONArray response_Array = object.getJSONArray("response");
                            if (response_Array.length() > 0) {

                                addressList.clear();
                                for (int i = 0; i < response_Array.length(); i++) {
                                    JSONObject response_Object = response_Array.getJSONObject(i);
                                    AddressListPojo pojo = new AddressListPojo();

                                    pojo.setAddress_name(getStringForJSON("address_name", response_Object));
                                    pojo.setName(getStringForJSON("name", response_Object));
                                    pojo.setAddressstatus(getStringForJSON("address_status", response_Object));
                                    pojo.setEmail(getStringForJSON("email", response_Object));
//                                    if (response_Object.has("country_code")) {
//                                        pojo.setCountry_code(getStringForJSON("country_code", response_Object));
//                                    }
                                    pojo.setCity(getStringForJSON("city", response_Object));
                                    pojo.setLandmark(getStringForJSON("landmark", response_Object));
                                    pojo.setLocality(getStringForJSON("locality", response_Object));
                                    pojo.setMobile(getStringForJSON("mobile", response_Object));
                                    pojo.setStreet(getStringForJSON("street", response_Object));
                                    pojo.setZipCode(getStringForJSON("zipcode", response_Object));
                                    pojo.setLongitude(getStringForJSON("lng", response_Object));
                                    pojo.setLatitude(getStringForJSON("lat", response_Object));
                                    Address=getCompleteAddressString(Double.parseDouble(response_Object.getString("lat")),Double.parseDouble(response_Object.getString("lng")));
                                    pojo.setAddress(Address);

                                    if (i == 0) {
                                        pojo.setAddressSelected(true);
                                       address=getCompleteAddressString(Double.parseDouble(response_Object.getString("lat")),Double.parseDouble(response_Object.getString("lng")));

                                        sSelectedAddressId = response_Object.getString("address_name");
                                        latitude = response_Object.getString("lat");
                                        longintude = response_Object.getString("lng");
                                        if (response_Object.has("country_code")) {
                                            country_code = getStringForJSON("country_code", response_Object);
                                        }
                                        if (!getStringForJSON("state", response_Object).equals("") && !getStringForJSON("country", response_Object).equals("")) {


                                            if (response_Object.getString("locality").equals("")) {
                                                sDisplayAddress =
                                                        response_Object.getString("name") + "\n" +
                                                                response_Object.getString("street");
//                                                                + "\n" + response_Object.getString("city") + "\n" + "Zipcode"
//                                                                + "-" + response_Object.getString("zipcode") + "\n" + response_Object.getString("state") +
//                                                                "," + response_Object.getString("country");


                                            } else {
                                                sDisplayAddress =
                                                        response_Object.getString("name") + "\n" +
                                                                response_Object.getString("street");
//                                                                + "\n" + response_Object.getString("city") + "\n" + response_Object.getString("locality") + "\n" + "Zipcode"
//                                                                + "-" + response_Object.getString("zipcode") + "\n" + response_Object.getString("state") +
//                                                                "," + response_Object.getString("country");


                                            }


                                        } else if (response_Object.getString("locality").equals("") || response_Object.getString("landmark").equals("")) {

                                            sDisplayAddress =
                                                    response_Object.getString("name") + "\n" +
                                                            response_Object.getString("street");
//                                                            + "\n" + response_Object.getString("city") + "\n" + "Zipcode"
//                                                            + "-" + response_Object.getString("zipcode");

                                        } else {
                                            sDisplayAddress =
                                                    response_Object.getString("name") + "\n" +
                                                            response_Object.getString("street");
//                                                            + "\n" + response_Object.getString("city") + "\n" + response_Object.getString("locality") + "\n" + "Zipcode"
//                                                            + "-" + response_Object.getString("zipcode") + "\n" + country_code
//                                                            + getResources().getString(R.string.appointment_label_landmark)
//                                                            + " " + response_Object.getString("landmark");
                                        }
                                    } else {
                                        pojo.setAddressSelected(false);
                                    }

                                    System.out.println("sDisplayAddress------------" + sDisplayAddress);

                                    System.out.println("sSelectedAddressId-------------" + sSelectedAddressId);

                                    pojo.setAddressSelected(false);
                                    addressList.add(pojo);
                                }
                                isDataPresent = true;
                            } else {
                                isDataPresent = false;
                            }
                        } else {
                            isDataPresent = false;
                        }
                    } else {
                        isDataPresent = false;
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if (isDataPresent && sStatus.equalsIgnoreCase("1")) {
                    if (addressList.size() > 0 && addressList != null) {
                         Rl_yourAddress.setVisibility(View.VISIBLE);
                        Rl_moreAddress.setVisibility(View.VISIBLE);
                        if(list_select_value){
                         Tv_yourAddress.setText(address);
                        }
                        Rl_AddAddress.setVisibility(View.GONE);
                        System.out.println("Tv_yourAddress------------" + Tv_yourAddress);

                    }
                } else {
                    Rl_moreAddress.setVisibility(View.GONE);
                    //Rl_yourAddress.setVisibility(View.GONE);
                   // Rl_AddAddress.setVisibility(View.VISIBLE);
                    // Tv_yourAddress.setText("");
                }
                mLoadingDialog.dismiss();
            }

            @Override
            public void onErrorListener() {
                mLoadingDialog.dismiss();
            }
        });
    }


    //-----------------------------------------------------------------------------Display_Address------------------------------------------------------------


    private void Display_Address(Context mContext, String Url) {

        mLoadingDialog = new LoadingDialog(mContext);
        mLoadingDialog.setLoadingTitle(getResources().getString(R.string.action_loading));
        mLoadingDialog.show();

        System.out.println("-------------addressList_Request Url----------------" + Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", UserID);
        jsonParams.put("address_name", sSelectedAddressId);

        System.out.println("address name---------------" + sSelectedAddressId);

        mRequest = new ServiceRequest(mContext);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("-------------addressList_Request Response----------------" + response);
                Log.e("addressList Response", response);

                String sStatus = "";
                String country_code = "";
                try {

                    JSONObject object = new JSONObject(response);
                    sStatus = object.getString("status");
                    if (object.length() > 0) {
                        Object check_response_object = object.get("response");
                        if (check_response_object instanceof JSONArray) {
                            JSONArray response_Array = object.getJSONArray("response");
                            if (response_Array.length() > 0) {

                                addressList.clear();
                                for (int i = 0; i < response_Array.length(); i++) {
                                    JSONObject response_Object = response_Array.getJSONObject(i);
                                    AddressListPojo pojo = new AddressListPojo();
                                    pojo.setAddressstatus(getStringForJSON("address_status", response_Object));
                                    if (i == 0) {


                                        sSelectedAddressId = response_Object.getString("address_name");
                                        sLatitude = response_Object.getString("lat");
                                        sLongitude = response_Object.getString("lng");
                                        if (response_Object.has("country_code")) {
                                            country_code = getStringForJSON("country_code", response_Object);
                                        }
                                        if (!getStringForJSON("state", response_Object).equals("") && !getStringForJSON("country", response_Object).equals("")) {


                                            if (response_Object.getString("locality").equals("")) {
                                                sDisplayAddress =
                                                        response_Object.getString("name") + "\n" +
                                                                response_Object.getString("street")
                                                                + "\n" + response_Object.getString("city") + "\n" + "Zipcode"
                                                                + "-" + response_Object.getString("zipcode") + "\n" + response_Object.getString("state") +
                                                                "," + response_Object.getString("country");


                                            } else {
                                                sDisplayAddress =
                                                        response_Object.getString("name") + "\n" +
                                                                response_Object.getString("street")
                                                                + "\n" + response_Object.getString("city") + "\n" + response_Object.getString("locality") + "\n" + "Zipcode"
                                                                + "-" + response_Object.getString("zipcode") + "\n" + response_Object.getString("state") +
                                                                "," + response_Object.getString("country");


                                            }


                                        } else if (response_Object.getString("locality").equals("") || response_Object.getString("landmark").equals("")) {

                                            sDisplayAddress =
                                                    response_Object.getString("name") + "\n" +
                                                            response_Object.getString("street")
                                                            + "\n" + response_Object.getString("city") + "\n" + "Zipcode"
                                                            + "-" + response_Object.getString("zipcode");

                                        } else {
                                            sDisplayAddress =
                                                    response_Object.getString("name") + "\n" +
                                                            response_Object.getString("street")
                                                            + "\n" + response_Object.getString("city") + "\n" + response_Object.getString("locality") + "\n" + "Zipcode"
                                                            + "-" + response_Object.getString("zipcode") + "\n" + country_code
                                                            + getResources().getString(R.string.appointment_label_landmark)
                                                            + " " + response_Object.getString("landmark");
                                        }
                                    } else {

                                    }

                                    System.out.println("sDisplayAddress------------" + sDisplayAddress);

                                    System.out.println("sSelectedAddressId-------------" + sSelectedAddressId);

                                    addressList.add(pojo);

                                }
                                isDataPresent = true;
                            } else {
                                isDataPresent = false;
                            }
                        } else {
                            isDataPresent = false;
                        }
                    } else {
                        isDataPresent = false;
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
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


    //-----------------------Submit Address Post Request-----------------
    private void submitAddressRequest(Context mContext, String Url) {

        System.out.println("-------------submitAddressRequest Url----------------" + Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", UserID);
        jsonParams.put("locality", selectedlocation);
        jsonParams.put("street", selectedlocation);
        jsonParams.put("landmark", "");
        jsonParams.put("city", city);
        jsonParams.put("state", state);
        jsonParams.put("zipcode", postalcode);
        jsonParams.put("lat", sLatitude);
        jsonParams.put("lng", sLongitude);

        System.out.println("" + selectedlocation);
        System.out.println("locality-----------" + UserID);
        System.out.println("street-----------" + "");
        System.out.println("landmark-----------" + "");
        System.out.println("city-----------" + city);
        System.out.println("zipcode-----------" + postalcode);
        System.out.println("lat-----------" + latitude);
        System.out.println("lng-----------" + longintude);
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

                        Addressidget(NewAppointmentpage.this, Iconstant.address_list_url);

                    } else {

                        alert(getResources().getString(R.string.action_sorry), getResources().getString(R.string.choose_address_alert));
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



    //-----------------------Display Address List Post Request-----------------
    private void Addressidget(Context mContext, String Url) {

//        mLoadingDialog = new LoadingDialog(mContext);
//        mLoadingDialog.setLoadingTitle(getResources().getString(R.string.action_loading));
//        mLoadingDialog.show();

        System.out.println("-------------addressList_Request Url----------------" + Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", UserID);
        jsonParams.put("address_name", sSelectedAddressId);

        System.out.println("address name---------------" + sSelectedAddressId);

        mRequest = new ServiceRequest(mContext);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("-------------addressList_Request Response----------------" + response);
                Log.e("addressList Response", response);

                String sStatus = "";
                String country_code = "";
                try {

                    JSONObject object = new JSONObject(response);
                    sStatus = object.getString("status");
                    if (object.length() > 0) {
                        Object check_response_object = object.get("response");
                        if (check_response_object instanceof JSONArray) {
                            JSONArray response_Array = object.getJSONArray("response");
                            if (response_Array.length() > 0) {

                                addressList.clear();
                                for (int i = 0; i < response_Array.length(); i++) {
                                    JSONObject response_Object = response_Array.getJSONObject(i);
                                    AddressListPojo pojo = new AddressListPojo();

                                    pojo.setAddress_name(getStringForJSON("address_name", response_Object));
                                    pojo.setName(getStringForJSON("name", response_Object));
                                    pojo.setAddressstatus(getStringForJSON("address_status", response_Object));
                                    pojo.setEmail(getStringForJSON("email", response_Object));

                                    pojo.setCity(getStringForJSON("city", response_Object));
                                    pojo.setLandmark(getStringForJSON("landmark", response_Object));
                                    pojo.setLocality(getStringForJSON("locality", response_Object));
                                    pojo.setMobile(getStringForJSON("mobile", response_Object));
                                    pojo.setStreet(getStringForJSON("street", response_Object));
                                    pojo.setZipCode(getStringForJSON("zipcode", response_Object));
                                    pojo.setLongitude(getStringForJSON("lng", response_Object));
                                    pojo.setLatitude(getStringForJSON("lat", response_Object));
                                    Address=getCompleteAddressString(Double.parseDouble(response_Object.getString("lat")),Double.parseDouble(response_Object.getString("lng")));
                                    pojo.setAddress(Address);

                                    if (i == 0) {
                                        pojo.setAddressSelected(true);
                                        address=getCompleteAddressString(Double.parseDouble(response_Object.getString("lat")),Double.parseDouble(response_Object.getString("lng")));

                                        sSelectedAddressId = response_Object.getString("address_name");
                                        latitude = response_Object.getString("lat");
                                        longintude = response_Object.getString("lng");
                                        if (response_Object.has("country_code")) {
                                            country_code = getStringForJSON("country_code", response_Object);
                                        }

                                    } else {
                                        pojo.setAddressSelected(false);
                                    }

                                    System.out.println("sDisplayAddress------------" + sDisplayAddress);

                                    System.out.println("sSelectedAddressId-------------" + sSelectedAddressId);

                                    pojo.setAddressSelected(false);
                                    addressList.add(pojo);
                                }
                                isDataPresent = true;
                            } else {
                                isDataPresent = false;
                            }
                        } else {
                            isDataPresent = false;
                        }
                    } else {
                        isDataPresent = false;
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if (isDataPresent && sStatus.equalsIgnoreCase("1")) {
                    if (addressList.size() > 0 && addressList != null) {
                        Rl_yourAddress.setVisibility(View.VISIBLE);
                        Rl_moreAddress.setVisibility(View.VISIBLE);
                        //Tv_yourAddress.setText(address);
                        Rl_AddAddress.setVisibility(View.GONE);
                        System.out.println("Tv_yourAddress------------" + Tv_yourAddress);

                    }
                    Intent i = new Intent(NewAppointmentpage.this, ProvidersList.class);
                    i.putExtra("user_id", UserID);
                    i.putExtra("address_name", sSelectedAddressId);
                    i.putExtra("category", sCategoryId);
                    i.putExtra("pickup_date", SelectDate);
                    i.putExtra("pickup_time", sSplitTime);
                    i.putExtra("instruction", Et_instruction.getText().toString());
                    i.putExtra("service", sServiceId);
                    i.putExtra("lat", sLatitude);
                    i.putExtra("long", sLongitude);
                    startActivity(i);
                    Et_instruction.setText("");
                    Et_instruction.setText("");
                  //  mLoadingDialog.dismiss();
                } else {
                    Rl_moreAddress.setVisibility(View.GONE);
                    //Rl_yourAddress.setVisibility(View.GONE);
                    // Rl_AddAddress.setVisibility(View.VISIBLE);
                    // Tv_yourAddress.setText("");
                  //  mLoadingDialog.dismiss();
                }

            }

            @Override
            public void onErrorListener() {
                //mLoadingDialog.dismiss();
            }
        });
    }



    //---------------------------------------------------------Get_String_json--------------------------------------

    private String getStringForJSON(String name, JSONObject response_Object) {

        try {
            return response_Object.getString(name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_LOCATION:
                switch (resultCode) {
                    case Activity.RESULT_OK: {
                        Toast.makeText(NewAppointmentpage.this, "Location enabled!", Toast.LENGTH_LONG).show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                gps = new GPSTracker(NewAppointmentpage.this);
                                //  sLatitude = String.valueOf(gps.getLatitude());
                                // sLongitude = String.valueOf(gps.getLongitude());
                            }
                        }, 2000);

                        break;
                    }
                    case Activity.RESULT_CANCELED: {
                        break;
                    }
                    default: {
                        break;
                    }
                }
                break;
        }
    }


    private boolean checkAccessFineLocationPermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private boolean checkAccessCoarseLocationPermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }


    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    bookJob();
                }
                break;
        }
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


    //-------------Method to get Complete Address------------
    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(NewAppointmentpage.this, Locale.getDefault());
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


