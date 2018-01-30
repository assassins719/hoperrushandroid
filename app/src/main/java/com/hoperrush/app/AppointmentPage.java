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
import android.net.ParseException;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import com.hoperrush.adapter.ProvidersListAdapter;
import com.hoperrush.core.dialog.LoadingDialog;
import com.hoperrush.core.dialog.PkDialog;
import com.hoperrush.core.gps.GPSTracker;
import com.hoperrush.core.volley.ServiceRequest;
import com.hoperrush.iconstant.Iconstant;
import com.hoperrush.pojo.AddressListPojo;
import com.hoperrush.pojo.ProvidersListPojo;
import com.hoperrush.utils.ConnectionDetector;
import com.hoperrush.utils.SessionManager;
import com.hoperrush.utils.SubClassFragementActivity;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Casperon Technology on 1/6/2016.
 */
public class AppointmentPage extends SubClassFragementActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private SessionManager sessionManager;

    private int RequestCode = 2;

    private RelativeLayout Rl_back;
    private ImageView Im_backIcon;
    private TextView Tv_headerTitle;

    private RelativeLayout Rl_SelectDate, Rl_SelectTime;
    private TextView Tv_selectedDate, Tv_selectedTime;
    private RelativeLayout Rl_AddAddress;
    private RelativeLayout Rl_yourAddress, Rl_moreAddress;
    private TextView Tv_yourAddress;
    private MaterialEditText Et_instruction;
    private MaterialEditText Et_couponCode;
    private RelativeLayout Rl_couponApply;
    private Button Bt_bookNow;

    private String UserID = "";
    private LoadingDialog mLoadingDialog;
    private ServiceRequest mRequest;
    private boolean isDataPresent = false;
    private ArrayList<AddressListPojo> addressList;
    private String sDisplayAddress = "";
    private String sSelectedAddressId = "";
    private String sCategoryId = "", sServiceId = "";
    private String sSplitTime = "";

    //Declaration for CaldroidDate
    private CaldroidFragment dialogCaldroidFragment;
    final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");


    //Declaration for TimeDialog
    ArrayList<String> timeArray = new ArrayList<String>();
    ArrayList<String> newTimeArray;
    private Dialog timeDialog;
    private View timeView;

    //Declaration for MoreAddressDialog
    private Dialog moreAddressDialog;
    private View moreAddressView;

    private ArrayList<ProvidersListPojo> providersList;
    private ProvidersListAdapter adapter;
    private boolean isproviderAvailable = false;

    private TextView Tv_empty;
    private ListView listView;

    private String Str_Refresh_Name = "normal";

    GPSTracker gps;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    PendingResult<LocationSettingsResult> result;
    final static int REQUEST_LOCATION = 199;
    private String sLatitude = "", sLongitude = "";
    private AppointmentMoreAddressAdapter addressAdapter;

    final int PERMISSION_REQUEST_CODE = 111;

    public class RefreshReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.package.ACTION_CLASS_APPOINTMENT_REFRESH")) {
                if (isInternetPresent) {

                    // get user data from session
                    HashMap<String, String> user = sessionManager.getUserDetails();
                    UserID = user.get(SessionManager.KEY_USER_ID);

                    addressList_Request(AppointmentPage.this, Iconstant.address_list_url);
                }
            }
        }
    }

    private RefreshReceiver refreshReceiver;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appointment_page);
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

        Rl_SelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker(savedInstanceState);
            }
        });

        Rl_SelectTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Tv_selectedDate.getText().toString().equalsIgnoreCase(getResources().getString(R.string.appointment_label_select_date))) {
                    alert("", getResources().getString(R.string.appointment_label_select_date_alert));
                } else {
                    String sTodayDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                    if (Tv_selectedDate.getText().toString().equalsIgnoreCase(sTodayDate)) {
                        SimpleDateFormat mTime_Formatter = new SimpleDateFormat("HH");
                        String sCurrentTime = mTime_Formatter.format(new Date());
                        if (Integer.parseInt(sCurrentTime) < 8 || Integer.parseInt(sCurrentTime) > 19) {
                            alert(getResources().getString(R.string.action_sorry), getResources().getString(R.string.appointment_label_no_time_slot_alert));
                        } else {
                            timePickerDialog();
                        }
                    } else {
                        timePickerDialog();
                    }
                }
            }
        });

        Rl_AddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (sessionManager.isLoggedIn()) {
                    gps = new GPSTracker(AppointmentPage.this);
                    if (gps.isgpsenabled() && gps.canGetLocation()) {
                        Intent intent = new Intent(AppointmentPage.this, AddAddressPage.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                    } else {
                        enableGpsService();
                    }
                } else {
                    Intent intent = new Intent(AppointmentPage.this, LogInPage.class);
                    intent.putExtra("IntentClass", "2");
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                }

            }
        });

        Rl_moreAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moreAddressDialog();
            }
        });


        Bt_bookNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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


    }

    private void initializeHeaderBar() {
        RelativeLayout headerBar = (RelativeLayout) findViewById(R.id.headerBar_layout);
        Rl_back = (RelativeLayout) headerBar.findViewById(R.id.headerBar_left_layout);
        Im_backIcon = (ImageView) headerBar.findViewById(R.id.headerBar_imageView);
        Tv_headerTitle = (TextView) headerBar.findViewById(R.id.headerBar_title_textView);

        Tv_headerTitle.setText(getResources().getString(R.string.appointment_label_header_textView));
        Im_backIcon.setImageResource(R.drawable.back_arrow);
    }

    private void initialize() {
        cd = new ConnectionDetector(AppointmentPage.this);
        isInternetPresent = cd.isConnectingToInternet();
        sessionManager = new SessionManager(AppointmentPage.this);
        addressList = new ArrayList<AddressListPojo>();
        gps = new GPSTracker(getApplicationContext());

        mGoogleApiClient = new GoogleApiClient.Builder(AppointmentPage.this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        mGoogleApiClient.connect();

        Rl_SelectDate = (RelativeLayout) findViewById(R.id.appointment_page_date_select_layout);
        Rl_SelectTime = (RelativeLayout) findViewById(R.id.appointment_page_time_select_layout);
        Tv_selectedDate = (TextView) findViewById(R.id.appointment_page_date_select_textView);
        Tv_selectedTime = (TextView) findViewById(R.id.appointment_page_time_select_textView);
        Rl_AddAddress = (RelativeLayout) findViewById(R.id.appointment_page_add_address_layout);
        Rl_yourAddress = (RelativeLayout) findViewById(R.id.appointment_page_display_address_layout);
        Rl_moreAddress = (RelativeLayout) findViewById(R.id.appointment_page_more_address_layout);
        Tv_yourAddress = (TextView) findViewById(R.id.appointment_page_your_address_textView);
        Et_instruction = (MaterialEditText) findViewById(R.id.appointment_page_instruction_editText);
        Et_couponCode = (MaterialEditText) findViewById(R.id.appointment_page_coupon_editText);
        Rl_couponApply = (RelativeLayout) findViewById(R.id.appointment_page_coupon_apply_layout);
        Bt_bookNow = (Button) findViewById(R.id.appointment_page_bookNow_button);


        // -----code to refresh drawer using broadcast receiver-----
        refreshReceiver = new RefreshReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.package.ACTION_CLASS_APPOINTMENT_REFRESH");
        registerReceiver(refreshReceiver, intentFilter);

        // get user data from session
        HashMap<String, String> user = sessionManager.getUserDetails();
        UserID = user.get(SessionManager.KEY_USER_ID);

        Intent intent = getIntent();
        sCategoryId = intent.getStringExtra("IntentCategoryID");
        sServiceId = intent.getStringExtra("IntentServiceID");

        System.out.println("sCategoryId------------" + sCategoryId);
        System.out.println("sServiceId------------" + sServiceId);

        if (isInternetPresent) {
            addressList_Request(AppointmentPage.this, Iconstant.address_list_url);
        } else {
            alert(getResources().getString(R.string.action_no_internet_title), getResources().getString(R.string.action_no_internet_message));
        }


        //Adding Time Array
        timeArray.add("8 AM - 9 AM");
        timeArray.add("9 AM - 10 AM");
        timeArray.add("10 AM - 11 AM");
        timeArray.add("11 AM - 12 PM");
        timeArray.add("12 PM - 1 PM");
        timeArray.add("1 PM - 2 PM");
        timeArray.add("2 PM - 3 PM");
        timeArray.add("3 PM - 4 PM");
        timeArray.add("4 PM - 5 PM");
        timeArray.add("5 PM - 6 PM");
        timeArray.add("6 PM - 7 PM");
        timeArray.add("7 PM - 8 PM");
    }


    private void bookJob() {


        Listviewclickaddress(AppointmentPage.this, Iconstant.List_Address_Url);

        gps = new GPSTracker(AppointmentPage.this);
        if (gps.isgpsenabled() && gps.canGetLocation()) {
            // sLatitude = Double.toString(gps.getLatitude());
            //sLongitude = Double.toString(gps.getLongitude());

            if (Tv_selectedDate.getText().toString().equalsIgnoreCase(getResources().getString(R.string.appointment_label_select_date))) {
                alert("", getResources().getString(R.string.appointment_label_select_date_alert));
            } else if (Tv_selectedTime.getText().toString().equalsIgnoreCase(getResources().getString(R.string.appointment_label_select_time))) {
                alert("", getResources().getString(R.string.appointment_label_select_time_alert));
            } else if (Tv_yourAddress.getText().toString().length() == 0) {
                alert("", getResources().getString(R.string.appointment_label_select_address_alert));
            } else if (Et_instruction.getText().toString().length() == 0) {
                alert("", getResources().getString(R.string.appointment_label_select_instruction_alert));
            } else {
                cd = new ConnectionDetector(AppointmentPage.this);
                isInternetPresent = cd.isConnectingToInternet();

                if (isInternetPresent) {
                    // bookJob_Request(AppointmentPage.this, Iconstant.book_job_url);
                    Intent i = new Intent(AppointmentPage.this, ProvidersList.class);
                    i.putExtra("user_id", UserID);
                    i.putExtra("address_name", sSelectedAddressId);
                    i.putExtra("category", sCategoryId);
                    i.putExtra("pickup_date", Tv_selectedDate.getText().toString());
                    i.putExtra("pickup_time", sSplitTime);
                    i.putExtra("instruction", Et_instruction.getText().toString());
                    i.putExtra("service", sServiceId);
                    i.putExtra("lat", sLatitude);
                    i.putExtra("long", sLongitude);
                    startActivity(i);

                    // postProvidersRequest (Iconstant.book_job_url);


                } else {
                    alert(getResources().getString(R.string.action_no_internet_title), getResources().getString(R.string.action_no_internet_message));
                }
            }

        } else {
            enableGpsService();
        }
    }


    //--------------Time Select Method-----------
    private void timePickerDialog() {

        newTimeArray = new ArrayList<String>();

        /*Function to hide the expired time from the arrayList*/
        String sTodayDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        if (Tv_selectedDate.getText().toString().equalsIgnoreCase(sTodayDate)) {
            SimpleDateFormat mTime_Formatter = new SimpleDateFormat("HH");
            String sCurrentTime = mTime_Formatter.format(new Date());

            int timeLength = 0;
            if (Integer.parseInt(sCurrentTime) == 8) {
                timeLength = 1;
            } else if (Integer.parseInt(sCurrentTime) == 9) {
                timeLength = 2;
            } else if (Integer.parseInt(sCurrentTime) == 10) {
                timeLength = 3;
            } else if (Integer.parseInt(sCurrentTime) == 11) {
                timeLength = 4;
            } else if (Integer.parseInt(sCurrentTime) == 12) {
                timeLength = 5;
            } else if (Integer.parseInt(sCurrentTime) == 13) {
                timeLength = 6;
            } else if (Integer.parseInt(sCurrentTime) == 14) {
                timeLength = 7;
            } else if (Integer.parseInt(sCurrentTime) == 15) {
                timeLength = 8;
            } else if (Integer.parseInt(sCurrentTime) == 16) {
                timeLength = 9;
            } else if (Integer.parseInt(sCurrentTime) == 17) {
                timeLength = 10;
            } else if (Integer.parseInt(sCurrentTime) == 18) {
                timeLength = 11;
            } else if (Integer.parseInt(sCurrentTime) == 19) {
                timeLength = 12;
            }

            for (int i = timeLength; i < timeArray.size(); i++) {
                newTimeArray.add(timeArray.get(i));
            }
        } else {
            newTimeArray = timeArray;
        }

        //--------Adjusting Dialog width-----
        DisplayMetrics metrics = AppointmentPage.this.getResources().getDisplayMetrics();
        int screenWidth = (int) (metrics.widthPixels * 0.80);//fill only 80% of the screen

        timeView = View.inflate(AppointmentPage.this, R.layout.appointment_time_pick_dialog, null);
        timeDialog = new Dialog(AppointmentPage.this);
        timeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        timeDialog.setContentView(timeView);
        timeDialog.setCanceledOnTouchOutside(true);
        timeDialog.getWindow().setLayout(screenWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        timeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ListView timeListView = (ListView) timeView.findViewById(R.id.appointment_time_pick_listView);
        ArrayAdapter<String> timeAdapter = new ArrayAdapter<String>
                (AppointmentPage.this, R.layout.appointment_time_picker_dialog_single, R.id.appointment_time_pick_single_textView, newTimeArray);
        timeListView.setAdapter(timeAdapter);

        timeDialog.show();

        timeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Tv_selectedTime.setText(newTimeArray.get(position).toString());

                String[] splitTime = newTimeArray.get(position).toString().split("-");
                String sSplitTimeValue = splitTime[0];
                try {
                    SimpleDateFormat displayFormat = new SimpleDateFormat("HH");
                    SimpleDateFormat parseFormat = new SimpleDateFormat("hh a", Locale.US);
                    Date date = parseFormat.parse(sSplitTimeValue);
                    sSplitTime = "";
                    sSplitTime = displayFormat.format(date) + ":00";

                }
                catch (java.text.ParseException e) {
                    e.printStackTrace();
                }

                timeDialog.dismiss();
            }
        });

    }

    //--------------Date Select Method-----------
    private void datePicker(Bundle savedState) {

        dialogCaldroidFragment = new CaldroidFragment();
        dialogCaldroidFragment.setCaldroidListener(caldroidListener);

        // If activity is recovered from rotation
        final String dialogTag = "CALDROID_DIALOG_FRAGMENT";
        if (savedState != null) {
            dialogCaldroidFragment.restoreDialogStatesFromKey(getSupportFragmentManager(), savedState,
                    "DIALOG_CALDROID_SAVED_STATE", dialogTag);
            Bundle args = dialogCaldroidFragment.getArguments();
            if (args == null) {
                args = new Bundle();
                dialogCaldroidFragment.setArguments(args);
            }
        } else {
            // Setup arguments
            Bundle bundle = new Bundle();
            // Setup dialogTitle
            dialogCaldroidFragment.setArguments(bundle);
        }

        Calendar cal = Calendar.getInstance();
        Date currentDate = null;
        Date maximumDate = null;
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
            String formattedDate = df.format(cal.getTime());
            currentDate = df.parse(formattedDate);

            // Max date is next 7 days
            cal = Calendar.getInstance();
            cal.add(Calendar.DATE, 90);
            maximumDate = cal.getTime();

        } catch (ParseException e1) {
            e1.printStackTrace();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }


        dialogCaldroidFragment.setMinDate(currentDate);
        dialogCaldroidFragment.setMaxDate(maximumDate);
        dialogCaldroidFragment.show(getSupportFragmentManager(), dialogTag);
        dialogCaldroidFragment.refreshView();
    }


    // Setup CaldroidListener
    final CaldroidListener caldroidListener = new CaldroidListener() {
        @Override
        public void onSelectDate(Date date, View view) {
            dialogCaldroidFragment.dismiss();
            Tv_selectedTime.setText(getResources().getString(R.string.appointment_label_select_time));
            Tv_selectedDate.setText(formatter.format(date));
        }

        @Override
        public void onChangeMonth(int month, int year) {
            String text = "month: " + month + " year: " + year;
        }

        @Override
        public void onLongClickDate(Date date, View view) {
        }

        @Override
        public void onCaldroidViewCreated() {
        }
    };


    //--------------More Address Select Method-----------
    private void moreAddressDialog() {
        //--------Adjusting Dialog width-----
        DisplayMetrics metrics = AppointmentPage.this.getResources().getDisplayMetrics();
        int screenWidth = (int) (metrics.widthPixels * 0.80);//fill only 80% of the screen
        moreAddressView = View.inflate(AppointmentPage.this, R.layout.appointment_more_address_dialog, null);
        moreAddressDialog = new Dialog(AppointmentPage.this);
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
                    gps = new GPSTracker(AppointmentPage.this);
                    if (gps.isgpsenabled() && gps.canGetLocation()) {
                        sSelectedAddressId = "";
                        Intent intent = new Intent(AppointmentPage.this, AddAddressPage.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                        getApplicationContext().startActivity(intent);
                        moreAddressDialog.dismiss();
                    } else {
                    }
                } else {
                    Intent intent = new Intent(AppointmentPage.this, LogInPage.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    intent.putExtra("IntentClass", "2");
                    getApplicationContext().startActivity(intent);
                }
            }
        });
//        moreAddressView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (sessionManager.isLoggedIn()) {
//                    gps = new GPSTracker(AppointmentPage.this);
//                    if (gps.isgpsenabled() && gps.canGetLocation()) {
//                        Intent intent = new Intent(AppointmentPage.this, AddAddressPage.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
//                        getApplicationContext().startActivity(intent);
//                    } else {
//                    }
//                } else {
//                    Intent intent = new Intent(AppointmentPage.this, LogInPage.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
//                    intent.putExtra("IntentClass", "2");
//                    getApplicationContext().startActivity(intent);
//                }
//
//            }
//        });
        addressAdapter = new AppointmentMoreAddressAdapter(AppointmentPage.this, addressList);
        addressListView.setAdapter(addressAdapter);

        System.out.println("addresslist---------------" + addressList);

        moreAddressDialog.show();

        addressListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                for (int i = 0; i < addressList.size(); i++) {
                    if (position == i) {
                        sSelectedAddressId = addressList.get(position).getAddress_name();
                        addressList.get(i).setAddressSelected(true);
                        //addressList_Request(AppointmentPage.this, Iconstant.address_list_url);
                        Listviewclickaddress(AppointmentPage.this, Iconstant.List_Address_Url);
                    } else {
                        addressList.get(i).setAddressSelected(false);
                    }
                }
                sSelectedAddressId = addressList.get(position).getAddress_name();
                String aAddress = "";
                if (addressList.get(position).getLandmark().equals("")) {


                    if (addressList.get(position).getLocality().equals("")) {

                        aAddress = addressList.get(position).getName() + "\n" + addressList.get(position).getStreet()
                                + "\n" + addressList.get(position).getCity() + "\n" + "Zipcode"
                                + "-" + addressList.get(position).getZipCode();

                    } else {
                        aAddress = addressList.get(position).getName() + "\n" + addressList.get(position).getStreet()
                                + "\n" + addressList.get(position).getCity() + "\n" + addressList.get(position).getLocality() + "\n" + "Zipcode"
                                + "-" + addressList.get(position).getZipCode();

                    }


                } else if (addressList.get(position).getLocality().equals("")) {

                    aAddress = addressList.get(position).getName() + "\n" + addressList.get(position).getStreet()
                            + "\n" + addressList.get(position).getCity() + "\n" + "Zipcode"
                            + "-" + addressList.get(position).getZipCode() + getResources().getString(R.string.appointment_label_landmark)
                            + " " + addressList.get(position).getLandmark();

                } else


                {

                    aAddress = addressList.get(position).getName() + "\n" + addressList.get(position).getStreet()
                            + "\n" + addressList.get(position).getCity() + "\n" + addressList.get(position).getLocality() + "\n" + "Zipcode"
                            + "-" + addressList.get(position).getZipCode()

                            + getResources().getString(R.string.appointment_label_landmark)
                            + " " + addressList.get(position).getLandmark();
                }
                sLatitude = addressList.get(position).getLatitude();
                sLongitude = addressList.get(position).getLongitude();
                Tv_yourAddress.setText(aAddress);
                moreAddressDialog.dismiss();
                addressAdapter.notifyDataSetChanged();
            }
        });
    }


    //--------------Delete Address Method-----------
    public void deleteAddressDialog(String sAddressName) {
        cd = new ConnectionDetector(AppointmentPage.this);
        isInternetPresent = cd.isConnectingToInternet();

        if (isInternetPresent) {
            deleteAddress_Request(AppointmentPage.this, Iconstant.delete_address_url, sAddressName);
        } else {
            alert(getResources().getString(R.string.action_no_internet_title), getResources().getString(R.string.action_no_internet_message));
        }
    }


    //--------------Alert Method-----------
    private void alert(String title, String alert) {

        final PkDialog mDialog = new PkDialog(AppointmentPage.this);
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

                                    if (i == 0) {
                                        pojo.setAddressSelected(true);

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
                                        pojo.setAddressSelected(false);
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

                if (isDataPresent && sStatus.equalsIgnoreCase("1")) {
                    if (addressList.size() > 0 && addressList != null) {
                        Rl_yourAddress.setVisibility(View.VISIBLE);
                        Tv_yourAddress.setText(sDisplayAddress);
                        Rl_AddAddress.setVisibility(View.GONE);
                        System.out.println("Tv_yourAddress------------" + Tv_yourAddress);

                    }
                } else {
                    Rl_yourAddress.setVisibility(View.GONE);
                    Rl_AddAddress.setVisibility(View.VISIBLE);
                    Tv_yourAddress.setText("");
                }
                mLoadingDialog.dismiss();
            }

            @Override
            public void onErrorListener() {
                mLoadingDialog.dismiss();
            }
        });
    }

    private String getStringForJSON(String name, JSONObject response_Object) {

        try {
            return response_Object.getString(name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }


    //------------------------------------------------------ListAddress Hit Url---------------------------------------------

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

                String sStatus = "";
                try {

                    JSONObject object = new JSONObject(response);
                    sStatus = object.getString("status");
                    if (object.length() > 0) {
                        Object check_response_object = object.get("response");
                        if (check_response_object instanceof JSONArray) {
                            JSONArray response_Array = object.getJSONArray("response");
//                            if (response_Array.length() > 0) {
//
//                                addressList.clear();
//                                for (int i = 0; i < response_Array.length(); i++) {
//                                    JSONObject response_Object = response_Array.getJSONObject(i);
//                                    AddressListPojo pojo = new AddressListPojo();
//
//                                    pojo.setAddress_name(getStringForJSON("address_name",response_Object));
//                                    pojo.setName(getStringForJSON("name",response_Object));
//                                    pojo.setEmail(getStringForJSON("email",response_Object));
//                                    pojo.setCountry_code(getStringForJSON("country_code",response_Object));
//                                    pojo.setMobile(getStringForJSON("mobile",response_Object));
//                                    pojo.setStreet(getStringForJSON("street",response_Object));
//                                    pojo.setCity(getStringForJSON("city",response_Object));
//                                    pojo.setLandmark(getStringForJSON("landmark",response_Object));
//                                    pojo.setLocality(getStringForJSON("locality",response_Object));
//                                    pojo.setZipCode(getStringForJSON("zipcode",response_Object));
//                                    pojo.setLongitude(getStringForJSON("lng",response_Object));
//                                    pojo.setLatitude(getStringForJSON("lat",response_Object));
//
//                                    if (i == 0) {
//                                        pojo.setAddressSelected(true);
//
//                                        sSelectedAddressId = response_Object.getString("address_name");
//                                        sLatitude = response_Object.getString("lat");
//                                        sLongitude = response_Object.getString("lng");
//
//                                        sDisplayAddress = response_Object.getString("name") + "\n" + response_Object.getString("street")
//                                                + "\n" + response_Object.getString("city") + "\n" + response_Object.getString("locality")
//                                                + "-" + response_Object.getString("zipcode") + "\n" + response_Object.getString("country_code")
//                                                + "-" + response_Object.getString("mobile") + "\n"
//                                                + getResources().getString(R.string.appointment_label_landmark)
//                                                + " " + response_Object.getString("landmark");
//                                    } else {
//                                        pojo.setAddressSelected(false);
//                                    }
//
//                                    System.out.println("sDisplayAddress------------" + sDisplayAddress);
//
//                                    System.out.println("sSelectedAddressId-------------" + sSelectedAddressId);
//
//
//                                    addressList.add(pojo);
//                                }
//                                isDataPresent = true;
//                            } else {
//                                isDataPresent = false;
//                            }
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
                        //  Tv_yourAddress.setText(sDisplayAddress);
                        Rl_AddAddress.setVisibility(View.GONE);
                        System.out.println("Tv_yourAddress------------" + Tv_yourAddress);

                    }
                } else {
                    Rl_yourAddress.setVisibility(View.GONE);
                    Rl_AddAddress.setVisibility(View.VISIBLE);
                    Tv_yourAddress.setText("");
                }
                mLoadingDialog.dismiss();
            }

            @Override
            public void onErrorListener() {
                mLoadingDialog.dismiss();
            }
        });
    }


    //----------------------------------------------------ListAddress Hit Url---------------------------------------------


    //-------------------Coupon Code Post Request----------------

    private void CouponCodeRequest(Context mContext, String Url, String sDate) {
        System.out.println("--------------coupon code url-------------------" + Url);

        mLoadingDialog = new LoadingDialog(mContext);
        mLoadingDialog.setLoadingTitle(getResources().getString(R.string.appointment_action_apply_coupon));
        mLoadingDialog.show();

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", UserID);
        jsonParams.put("code", Et_couponCode.getText().toString());
        jsonParams.put("pickup_date", sDate);

        mRequest = new ServiceRequest(mContext);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("--------------coupon code response-------------------" + response);

                try {
                    JSONObject object = new JSONObject(response);
                    if (object.length() > 0) {
                        String status = object.getString("status");
                        if (status.equalsIgnoreCase("1")) {
                            JSONObject result_object = object.getJSONObject("response");
                            String code = result_object.getString("code");
                            String sMessage = result_object.getString("message");
                            alert(getResources().getString(R.string.action_success), sMessage);
                        } else {
                            String sResponse = object.getString("response");
                            alert(getResources().getString(R.string.action_sorry), sResponse);
                        }
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    mLoadingDialog.dismiss();
                }
                mLoadingDialog.dismiss();
            }

            @Override
            public void onErrorListener() {
                mLoadingDialog.dismiss();
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
                            refreshDeleteAddress_Request(AppointmentPage.this, Iconstant.address_list_url);
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
                                        if (i == 0) {
                                            pojo.setAddressSelected(true);
                                            String countrycode = getStringForJSON("country_code", response_Object);
                                            sSelectedAddressId = response_Object.getString("address_name");

                                            sDisplayAddress = response_Object.getString("name") + "\n" + response_Object.getString("street")
                                                    + "\n" + response_Object.getString("city") + "\n" + response_Object.getString("locality") + "\n" + "Zipcode"
                                                    + "-" + response_Object.getString("zipcode") + "\n" + countrycode
                                                    + "-" + response_Object.getString("mobile") + "\n"
                                                    + getResources().getString(R.string.appointment_label_landmark)
                                                    + " " + response_Object.getString("landmark");
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
                            Tv_yourAddress.setText(sDisplayAddress);
                        } else {
                            moreAddressDialog.dismiss();
                            Rl_yourAddress.setVisibility(View.GONE);
                            Rl_AddAddress.setVisibility(View.VISIBLE);
                            Tv_yourAddress.setText("");
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

/*

    //-----------------------Job Booking Post Request-----------------
    private void bookJob_Request(Context mContext, String Url) {

        mLoadingDialog = new LoadingDialog(mContext);
        mLoadingDialog.setLoadingTitle(getResources().getString(R.string.action_processing));
        mLoadingDialog.show();

        System.out.println("-------------bookJob_Request Url----------------" + Url);


        System.out.println("-----------user_id------------------" + UserID);
        System.out.println("------------address_name-----------------" + sSelectedAddressId);
        System.out.println("-------------category----------------" + sCategoryId);
        System.out.println("-------------pickup_date----------------" + Tv_selectedDate.getText().toString());
        System.out.println("--------------pickup_time---------------" + sSplitTime);
        System.out.println("--------------code---------------" + Et_couponCode.getText().toString());
        System.out.println("--------------instruction---------------" + Et_instruction.getText().toString());
        System.out.println("--------------service---------------" + sServiceId);
        System.out.println("--------------lat---------------" + sLatitude);
        System.out.println("--------------long---------------" + sLongitude);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", UserID);
        jsonParams.put("address_name", "address1");
        jsonParams.put("category", sCategoryId);
        jsonParams.put("pickup_date", Tv_selectedDate.getText().toString());
        jsonParams.put("pickup_time", sSplitTime);
       // jsonParams.put("code", Et_couponCode.getText().toString());
        jsonParams.put("instruction", Et_instruction.getText().toString());
       // jsonParams.put("try", "");
       // jsonParams.put("job_id", "");
        jsonParams.put("service", sServiceId);
        jsonParams.put("lat", sLatitude);
        jsonParams.put("long", sLongitude);

        mRequest = new ServiceRequest(mContext);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("-------------bookJob_Request Response----------------" + response);

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

                            Intent intent = new Intent(AppointmentPage.this, AppointmentConfirmationPage.class);
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
*/


    @Override
    public void onConnected(Bundle bundle) {
        // enableGpsService();
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    //Enabling Gps Service
    private void enableGpsService() {

        mGoogleApiClient = new GoogleApiClient.Builder(AppointmentPage.this)
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
                            status.startResolutionForResult(AppointmentPage.this, REQUEST_LOCATION);
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_LOCATION:
                switch (resultCode) {
                    case Activity.RESULT_OK: {
                        Toast.makeText(AppointmentPage.this, "Location enabled!", Toast.LENGTH_LONG).show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                gps = new GPSTracker(AppointmentPage.this);
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
    public void onResume() {
        super.onResume();
    }

    //-----------------Move Back on pressed phone back button------------------
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)) {
            onBackPressed();
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            return true;
        }
        return false;
    }

    @Override
    public void onDestroy() {
        // Unregister the logout receiver
        unregisterReceiver(refreshReceiver);
        super.onDestroy();
    }

    private void stopLoading() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Str_Refresh_Name.equalsIgnoreCase("normal")) {
                    mLoadingDialog.dismiss();
                } else {
                    // swipeToRefresh.setRefreshing(false);
                }
            }
        }, 500);
    }


    public void postProvidersRequest(String url) {

        mLoadingDialog = new LoadingDialog(AppointmentPage.this);
        mLoadingDialog.setLoadingTitle(getResources().getString(R.string.action_processing));
        mLoadingDialog.show();

        System.out.println("-----------user_id------------------" + UserID);
        System.out.println("------------address_name-----------------" + sSelectedAddressId);
        System.out.println("-------------category----------------" + sCategoryId);
        System.out.println("-------------pickup_date----------------" + Tv_selectedDate.getText().toString());
        System.out.println("--------------pickup_time---------------" + sSplitTime);
        System.out.println("--------------code---------------" + Et_couponCode.getText().toString());
        System.out.println("--------------instruction---------------" + Et_instruction.getText().toString());
        System.out.println("--------------service---------------" + sServiceId);
        System.out.println("--------------lat---------------" + sLatitude);
        System.out.println("--------------long---------------" + sLongitude);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", UserID);
        jsonParams.put("address_name", "address1");
        jsonParams.put("category", sCategoryId);
        jsonParams.put("pickup_date", Tv_selectedDate.getText().toString());
        jsonParams.put("pickup_time", sSplitTime);
        // jsonParams.put("code", Et_couponCode.getText().toString());
        jsonParams.put("instruction", Et_instruction.getText().toString());
        // jsonParams.put("try", "");
        // jsonParams.put("job_id", "");
        jsonParams.put("service", sServiceId);
        jsonParams.put("lat", sLatitude);
        jsonParams.put("long", sLongitude);


        mRequest.makeServiceRequest(url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("respionse--------------" + response);
                String Str_status = "", Str_response = "";
                try {

                    JSONObject object = new JSONObject(response);
                    Str_status = object.getString("status");

                    if (Str_status.equalsIgnoreCase("1")) {

                        JSONArray jarry = object.getJSONArray("response");
                        if (jarry.length() > 0) {
                            for (int i = 0; i < jarry.length(); i++) {
                                JSONObject jobject = jarry.getJSONObject(i);
                                ProvidersListPojo pojo = new ProvidersListPojo();

                                pojo.setProvider_name(jobject.getString("name"));
                                pojo.setProvider_company(jobject.getString("company"));
                                pojo.setProvider_rating(jobject.getString("rating"));
                                pojo.setProvider_image(jobject.getString("image_url"));
                                pojo.setProvider_availble(jobject.getString("availability"));

                                providersList.add(pojo);
                            }
                            isproviderAvailable = true;

                        } else {
                            isproviderAvailable = false;
                            //providersList.clear();
                        }

                    } else {

                        Str_response = object.getString("response");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    stopLoading();
                }

                if (Str_status.equalsIgnoreCase("1")) {

                    Intent i = new Intent(AppointmentPage.this, ProvidersList.class);
                    startActivity(i);
                }


               /* if (Str_status.equalsIgnoreCase("1")&&isproviderAvailable){
                    adapter = new ProvidersListAdapter(AppointmentPage.this, providersList);
                    listView.setAdapter(adapter);
                    Tv_empty.setVisibility(View.GONE);
                }else if (Str_status.equalsIgnoreCase("1") && !isproviderAvailable) {
                    adapter = new ProvidersListAdapter(AppointmentPage.this, providersList);
                    listView.setAdapter(adapter);
                    Tv_empty.setVisibility(View.VISIBLE);
                } else if (Str_status.equalsIgnoreCase("0")) {
                    adapter = new ProvidersListAdapter(AppointmentPage.this, providersList);
                    listView.setAdapter(adapter);
                    Tv_empty.setVisibility(View.VISIBLE);
                }*/

                mLoadingDialog.dismiss();

            }

            @Override
            public void onErrorListener() {
                mLoadingDialog.dismiss();
            }
        });

    }
}
