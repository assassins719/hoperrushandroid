package com.hoperrush.app;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hoperrush.R;
import com.hoperrush.adapter.MyJobsDetailTimeLineAdapter;
import com.hoperrush.core.dialog.LoadingDialog;
import com.hoperrush.core.dialog.PkDialog;
import com.hoperrush.core.socket.ChatMessageService;
import com.hoperrush.core.volley.ServiceRequest;
import com.hoperrush.core.widgets.RoundedImageView;
import com.hoperrush.fragment.Viewprofilefragment;
import com.hoperrush.iconstant.Iconstant;
import com.hoperrush.pojo.CancelJobPojo;
import com.hoperrush.pojo.MyJobDetailTimeLinePojo;
import com.hoperrush.pojo.MyJobsDetailPojo;
import com.hoperrush.utils.ConnectionDetector;
import com.hoperrush.utils.SessionManager;
import com.hoperrush.utils.SubClassActivity;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Casperon Technology on 1/19/2016.
 */
public class MyJobDetail extends SubClassActivity implements Iconstant {
    private ConnectionDetector cd;
    private boolean isInternetPresent = false;
    private SessionManager sessionManager;

    private RelativeLayout Rl_back;
    private TextView Tv_headerTitle;
    String job_id = "";
    private GoogleMap googleMap;
    private ImageView Iv_request, Iv_assigned, Iv_delivery;

    private Button Bt_detail, Bt_responses;
    private View Vi_detail, Vi_responses;
    private RelativeLayout Rl_detail, Rl_responses;
    String usercurrentlat = "", usercurrentlong = "";
    private TextView Tv_dateAndTime, Tv_location;
    private ExpandableHeightListView listView;
    private RelativeLayout Rl_JobStatus;
    private TextView Tv_jobStatus;
    private TextView Tv_payment;
    private TextView Tv_fare_summary;
    private ImageView Iv_jobStatusArrow;
    private Button canceljob;
    private RoundedImageView Im_userImage;
    private TextView Tv_username, Tv_bio, Tv_email;
    private TextView Tv_noProviderAssigned;
    private RelativeLayout Rl_provider;
    private RatingBar Rb_rating;
    private LinearLayout Ll_chat, Ll_viewProfile;

    private double Slattitude;
    private double Slongitude;

    private double locationlattitude;
    private double locationlongitude;

    private String mTaskerID;
    String address = "";
    RelativeLayout trackvisit;
    Button trackpage;
    private ServiceRequest mRequest;
    private LoadingDialog mLoadingDialog;
    private String sUserID = "", sJobID = "";
    private boolean isDataAvailable = false, isInfoAvailable = false, isTimeLineAvailable = false, isProviderAvailable = false;

    private ArrayList<MyJobsDetailPojo> infoList;
    private ArrayList<MyJobDetailTimeLinePojo> timeLineList;
    private MyJobsDetailTimeLineAdapter adapter;
    private String task_id;
    ArrayList<CancelJobPojo> itemList_reason;
    private boolean isReasonAvailable = false;
    View view;
    String booking_address="";
    private String provider_hourly_rate="";
    private String provider_minimum_rate="";
    private static final int REQUEST_CODE_RECOVER_PLAY_SERVICES = 1001;
    private TextView chat_text,cancel_reason_textview;
    String cancel_reason = "";
    RelativeLayout cancel_layout;
    public static Activity Myjob_page;

    public class RefreshReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.package.refresh.MyJobDetails")) {
                if (isInternetPresent) {
                    if (mRequest != null) {
                        mRequest.cancelRequest();
                        mLoadingDialog.dismiss();
                    }
                    postJobDetailRequest(Iconstant.MyJobs_Detail_Url);
                }
            } else if (intent.getAction().equals("com.package.finish.MyJobDetails")) {
                if(MyJobs.Myjobs_page!=null){
                    MyJobs.Myjobs_page.finish();
                    Intent i = new Intent(getApplicationContext(), MyJobs.class);
                    i.putExtra("status","cancelled");
                    startActivity(i);
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
                else{
                    Intent i = new Intent(getApplicationContext(), MyJobs.class);
                    i.putExtra("status","cancelled");
                    startActivity(i);
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }

            } else if (intent.getAction().equals("com.package.finish.pushnotification")) {
                Intent i = new Intent(getApplicationContext(), MyJobs.class);
                i.putExtra("status","cancelled");
                startActivity(i);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        }
    }

    private RefreshReceiver finishReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myjobs_detail);
        Myjob_page=MyJobDetail.this;
        initialize();
        initializeMap();

        Rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        Ll_viewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cd = new ConnectionDetector(MyJobDetail.this);
                isInternetPresent = cd.isConnectingToInternet();
                if (isInternetPresent) {
                    Intent viewProfileIntent = new Intent(MyJobDetail.this, Viewprofilefragment.class);
                    sessionManager.putProvideID(infoList.get(0).getProvider_id());
                    sessionManager.putChatProfileJobID(sJobID);
                    sessionManager.putChatProfileTaskerID(mTaskerID);
                    sessionManager.putProvideScreenType("");
                    sessionManager.putChatProfileTaskID(task_id);

                    viewProfileIntent.putExtra("userid",sUserID);
                    viewProfileIntent.putExtra("task_id",task_id);
                    viewProfileIntent.putExtra("address","");
                    viewProfileIntent.putExtra("taskerid",mTaskerID);
                    viewProfileIntent.putExtra("hourl_amount",provider_hourly_rate);
                    viewProfileIntent.putExtra("minimum_amount",provider_minimum_rate);
                    startActivity(viewProfileIntent);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                } else {
                    alert(getResources().getString(R.string.action_no_internet_title), getResources().getString(R.string.action_no_internet_message));
                }
            }
        });

        Rl_JobStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cd = new ConnectionDetector(MyJobDetail.this);
                isInternetPresent = cd.isConnectingToInternet();

                if (isInfoAvailable) {

                    if (isInternetPresent) {
                        if (infoList.get(0).getDo_cancel().equalsIgnoreCase("YES")) {

                            postRequest_CancelJob_Reason(Iconstant.MyJobs_Cancel_Reason_Url, infoList.get(0).getJob_id());

                        } else if (infoList.get(0).getNeed_payment().equalsIgnoreCase("YES")) {

                            Intent paymentIntent = new Intent(MyJobDetail.this, PaymentNew.class);
                            paymentIntent.putExtra("JobID_INTENT", infoList.get(0).getJob_id());
                            paymentIntent.putExtra("TaskId", infoList.get(0).getTaskid());
                            Log.e("tASK", infoList.get(0).getTaskid());
                            startActivity(paymentIntent);
                            overridePendingTransition(R.anim.enter, R.anim.exit);

                        } else if (infoList.get(0).getSubmit_ratings().equalsIgnoreCase("YES")) {

                            Intent broadcastIntent = new Intent();
                            broadcastIntent.setAction("com.package.ACTION_CLASS_MY_JOBS_REFRESH");
                            sendBroadcast(broadcastIntent);

                            Intent intent = new Intent(MyJobDetail.this, RatingPage.class);
                            intent.putExtra("JobID", infoList.get(0).getJob_id());
                            startActivity(intent);
                            finish();
                            overridePendingTransition(R.anim.enter, R.anim.exit);
                        }
                    } else {
                        alert(getResources().getString(R.string.action_no_internet_title), getResources().getString(R.string.action_no_internet_message));
                    }

                }
            }
        });


        canceljob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cd = new ConnectionDetector(MyJobDetail.this);
                isInternetPresent = cd.isConnectingToInternet();

                if (isInfoAvailable) {

                    if (isInternetPresent) {
                        if (infoList.get(0).getDo_cancel().equalsIgnoreCase("YES")) {
                            final PkDialog mDialog = new PkDialog(MyJobDetail.this);
                            mDialog.setDialogTitle(getResources().getString(R.string.myJobs_cancel_job_alert_title));
                            mDialog.setDialogMessage(getResources().getString(R.string.myJobs_cancel_job_alert));
                            mDialog.setPositiveButton(getResources().getString(R.string.myJobs_cancel_job_alert_yes), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mDialog.dismiss();
                                    postRequest_CancelJob_Reason(Iconstant.MyJobs_Cancel_Reason_Url, infoList.get(0).getJob_id());
                                }
                            });
                            mDialog.setNegativeButton(getResources().getString(R.string.myJobs_cancel_job_alert_no), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mDialog.dismiss();
                                }
                            });
                            mDialog.show();
                        } else if (infoList.get(0).getNeed_payment().equalsIgnoreCase("YES")) {

                            Intent paymentIntent = new Intent(MyJobDetail.this, PaymentNew.class);
                            paymentIntent.putExtra("JobID_INTENT", infoList.get(0).getJob_id());
                            paymentIntent.putExtra("TaskId", infoList.get(0).getTaskid());
                            startActivity(paymentIntent);
                            overridePendingTransition(R.anim.enter, R.anim.exit);

                        } else if (infoList.get(0).getSubmit_ratings().equalsIgnoreCase("YES")) {

                            Intent broadcastIntent = new Intent();
                            broadcastIntent.setAction("com.package.ACTION_CLASS_MY_JOBS_REFRESH");
                            sendBroadcast(broadcastIntent);

                            Intent intent = new Intent(MyJobDetail.this, RatingPage.class);
                            intent.putExtra("JobID", infoList.get(0).getJob_id());
                            startActivity(intent);
                            finish();
                            overridePendingTransition(R.anim.enter, R.anim.exit);
                        }
                    } else {
                        alert(getResources().getString(R.string.action_no_internet_title), getResources().getString(R.string.action_no_internet_message));
                    }

                }
            }
        });

        Bt_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Rl_detail.setVisibility(View.VISIBLE);
                Rl_responses.setVisibility(View.GONE);
                Vi_detail.setBackgroundColor(Color.parseColor("#16a085"));
                Vi_responses.setBackgroundColor(Color.parseColor("#252525"));
            }
        });

        Bt_responses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Rl_detail.setVisibility(View.GONE);
                Rl_responses.setVisibility(View.VISIBLE);
                Vi_detail.setBackgroundColor(Color.parseColor("#252525"));
                Vi_responses.setBackgroundColor(Color.parseColor("#16a085"));
            }
        });


        Ll_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatMessageService.tasker_id = "";
                ChatMessageService.task_id = "";
                Intent intent = new Intent(MyJobDetail.this, ChatPage.class);
                intent.putExtra("JobID-Intent", sJobID);
                intent.putExtra("TaskerId", mTaskerID);
                intent.putExtra("TaskId", task_id);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        listView.setExpanded(true);
        Tv_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pay = new Intent(MyJobDetail.this, PaymentNew.class);

                //pay.putExtra("User_id", sUserID);
                pay.putExtra("JobID_INTENT", sJobID);
                pay.putExtra("TaskId", task_id);
                startActivity(pay);
                //  overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        Tv_fare_summary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fareSummary = new Intent(MyJobDetail.this, FareSummary.class);
                fareSummary.putExtra("jobid", job_id);
                startActivity(fareSummary);
            }
        });

        trackpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String latitude = String.valueOf(Slattitude);
                String logintude = String.valueOf(Slongitude);

                Intent i = new Intent(getApplicationContext(), Trackyourride.class);
                i.putExtra("lati", latitude);
                i.putExtra("logi", logintude);
                i.putExtra("usercurrentlat", usercurrentlat);
                i.putExtra("usercurrentlong", usercurrentlong);
                startActivity(i);
            }
        });

        Tv_jobStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Tv_jobStatus.getText().toString().equalsIgnoreCase("MORE INFO")) {
                    Intent fareSummary = new Intent(MyJobDetail.this, FareSummary.class);
                    fareSummary.putExtra("jobid", job_id);
                    startActivity(fareSummary);
                } else {
                    if(isInternetPresent){
                        postRequest_CancelJob_Reason(Iconstant.MyJobs_Cancel_Reason_Url, infoList.get(0).getJob_id());
                    } else {
                        alert(getResources().getString(R.string.action_no_internet_title), getResources().getString(R.string.action_no_internet_message));
                    }
                }
            }
        });
    }

    private void initialize() {
        cd = new ConnectionDetector(MyJobDetail.this);
        isInternetPresent = cd.isConnectingToInternet();
        sessionManager = new SessionManager(MyJobDetail.this);
        mRequest = new ServiceRequest(MyJobDetail.this);
        infoList = new ArrayList<MyJobsDetailPojo>();
        timeLineList = new ArrayList<MyJobDetailTimeLinePojo>();
        itemList_reason = new ArrayList<CancelJobPojo>();
        trackvisit = (RelativeLayout) findViewById(R.id.tracklayout);
        trackpage = (Button) findViewById(R.id.trackpage);
        canceljob = (Button) findViewById(R.id.job_detail_btn1);
        view = (View) findViewById(R.id.view1);
        Rl_back = (RelativeLayout) findViewById(R.id.myJob_detail_headerBar_left_layout);
        Tv_headerTitle = (TextView) findViewById(R.id.myJob_detail_headerBar_title_textView);
        Iv_request = (ImageView) findViewById(R.id.myJob_detail_requestSubmitted_imageView);
        Iv_assigned = (ImageView) findViewById(R.id.myJob_detail_professionalAssigned_imageView);
        Iv_delivery = (ImageView) findViewById(R.id.myJob_detail_serviceDelivery_imageView);
        Bt_detail = (Button) findViewById(R.id.myJob_detail_requestSubmitted_detail_button);
        Bt_responses = (Button) findViewById(R.id.myJob_detail_requestSubmitted_response_button);

        Vi_detail = (View) findViewById(R.id.myJob_detail_requestSubmitted_detail_button_view);
        Vi_responses = (View) findViewById(R.id.myJob_detail_requestSubmitted_response_button_view);
        Rl_detail = (RelativeLayout) findViewById(R.id.myJob_detail_detailView_RelativeLayout);
        Rl_responses = (RelativeLayout) findViewById(R.id.myJob_detail_responseView_RelativeLayout);

        Tv_dateAndTime = (TextView) findViewById(R.id.myJobs_detail_date_and_time_textView);
        Tv_location = (TextView) findViewById(R.id.myJobs_detail_location_textView);
        listView = (ExpandableHeightListView) findViewById(R.id.myJobs_detail_status_listView);
        Rl_JobStatus = (RelativeLayout) findViewById(R.id.myJobs_detail_bottom_status_layout);
        Tv_jobStatus = (TextView) findViewById(R.id.myJobs_detail_bottom_job_status_textView);
        Tv_payment = (TextView) findViewById(R.id.payment);
        Tv_fare_summary = (TextView) findViewById(R.id.faresummayid);
        Iv_jobStatusArrow = (ImageView) findViewById(R.id.myJobs_detail_bottom_job_status_arrow_icon);

        Im_userImage = (RoundedImageView) findViewById(R.id.myJob_detail_responseView_profile_imageView);
        Tv_username = (TextView) findViewById(R.id.myJob_detail_responseView_profileName_textView);
        Tv_email = (TextView) findViewById(R.id.myJob_detail_responseView_profileEmail_textView);
        Tv_bio = (TextView) findViewById(R.id.myJob_detail_responseView_bio_textView);
        Tv_noProviderAssigned = (TextView) findViewById(R.id.myJob_detail_responseView_noProvider_available_TextView);
        Rl_provider = (RelativeLayout) findViewById(R.id.myJob_detail_responseView_provide_layout);
        Rb_rating = (RatingBar) findViewById(R.id.myJob_detail_responseView_ratingBar);
        Ll_chat = (LinearLayout) findViewById(R.id.myJob_detail_responseView_chat_layout);
        Ll_viewProfile = (LinearLayout) findViewById(R.id.myJob_detail_responseView_viewProfile_layout);
        chat_text=(TextView)findViewById(R.id.chat_text);
        cancel_reason_textview = (TextView)findViewById(R.id.myJobs_detail_cancel_reason_textView);
        cancel_layout = (RelativeLayout) findViewById(R.id.myJob_detail_cancel_reason_layout);
        // get user data from session
        HashMap<String, String> user = sessionManager.getUserDetails();
        sUserID = user.get(SessionManager.KEY_USER_ID);

        // -----code to refresh drawer using broadcast receiver-----
        finishReceiver = new RefreshReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.package.refresh.MyJobDetails");
        intentFilter.addAction("com.package.finish.MyJobDetails");
        intentFilter.addAction("com.package.finish.pushnotification");
        registerReceiver(finishReceiver, intentFilter);

        Intent intent = getIntent();
        sJobID = intent.getStringExtra("JOB_ID_INTENT");
        sessionManager.setjob(sJobID);


        if (isInternetPresent) {
            postJobDetailRequest(Iconstant.MyJobs_Detail_Url);
            System.out.println("mjobdeetail---------" + Iconstant.MyJobs_Detail_Url);

        } else {
            alert(getResources().getString(R.string.action_no_internet_title), getResources().getString(R.string.action_no_internet_message));
        }
    }

    private void initializeMap() {
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.myJobs_detail_mapView)).getMap();

            // check if map is created successfully or not
            if (googleMap == null) {
                //Toast.makeText(MyJobDetail.this, "Sorry! unable to create maps", Toast.LENGTH_SHORT).show();
            }
        }

        if (CheckPlayService()) {
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


            //---------Hiding the bottom layout after success request--------
            googleMap.getUiSettings().setAllGesturesEnabled(false);
        } else {
            final PkDialog mDialog = new PkDialog(MyJobDetail.this);
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


    //-----------Check Google Play Service--------
    private boolean CheckPlayService() {
        final int connectionStatusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(MyJobDetail.this);
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
                        connectionStatusCode, MyJobDetail.this, REQUEST_CODE_RECOVER_PLAY_SERVICES);
                if (dialog == null) {
                    Toast.makeText(MyJobDetail.this, "incompatible version of Google Play Services", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //------Alert Method-----
    private void alert(String title, String message) {
        final PkDialog mDialog = new PkDialog(MyJobDetail.this);
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

    private void startLoading() {
        mLoadingDialog = new LoadingDialog(MyJobDetail.this);
        mLoadingDialog.setLoadingTitle(getResources().getString(R.string.action_loading));
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


    //-------------My Jobs Detail Post Request---------------
    private void postJobDetailRequest(String url) {

        startLoading();

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", sUserID);
        jsonParams.put("job_id", sJobID);

        System.out.println("---------My Jobs Detail user_id------------" + sUserID);
        System.out.println("---------My Jobs Detail job_id------------" + sJobID);
        System.out.println("---------My Jobs Detail Url------------" + url);

        mRequest.makeServiceRequest(url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("---------Detailresponse------------" + response);

                String sStatus = "";
                try {
                    JSONObject object = new JSONObject(response);
                    sStatus = object.getString("status");

                    if (sStatus.equalsIgnoreCase("1")) {
                        Object check_response_object = object.get("response");
                        if (check_response_object instanceof JSONObject) {

                            JSONObject response_Object = object.getJSONObject("response");
                            if (response_Object.length() > 0) {

                                Object check_info_object = response_Object.get("info");
                                if (check_info_object instanceof JSONObject) {

                                    infoList.clear();
                                    JSONObject info_Object = response_Object.getJSONObject("info");
                                    if (info_Object.length() > 0) {

                                        MyJobsDetailPojo pojo = new MyJobsDetailPojo();
                                        pojo.setJob_id(info_Object.getString("job_id"));
                                        job_id = info_Object.getString("job_id");

                                        task_id = info_Object.getString("task_id");
                                        pojo.setTaskid(info_Object.getString("task_id"));
                                        pojo.setLocation(info_Object.getString("location"));
                                        pojo.setLat(info_Object.getString("lat"));
                                        pojo.setLng(info_Object.getString("lng"));
                                        pojo.setBook_date(info_Object.getString("book_date"));
                                        pojo.setBook_time(info_Object.getString("book_time"));
                                        pojo.setDate(info_Object.getString("date"));
                                        pojo.setTime(info_Object.getString("time"));
                                        pojo.setBooking_address(info_Object.getString("booking_address"));
                                        booking_address=info_Object.getString("booking_address");
                                        pojo.setJob_status(info_Object.getString("job_status"));
                                        pojo.setService_type(info_Object.getString("service_type"));
                                        pojo.setWork_type(info_Object.getString("work_type"));
                                        pojo.setInstructions(info_Object.getString("instructions"));
                                        pojo.setDo_cancel(info_Object.getString("do_cancel"));
                                        pojo.setNeed_payment(info_Object.getString("need_payment"));
                                        pojo.setSubmit_ratings(info_Object.getString("submit_ratings"));

                                        if (info_Object.has("cancelreason")) {
                                            cancel_reason = info_Object.getString("cancelreason");
                                        }

                                        JSONObject usercurrentlocation = info_Object.getJSONObject("location");
                                        if (usercurrentlocation.length() > 0) {
                                            usercurrentlat = usercurrentlocation.getString("lat");
                                            usercurrentlong = usercurrentlocation.getString("lon");
                                        }

                                        JSONObject providerlat = info_Object.getJSONObject("provider_location");

                                        if (providerlat.length() > 0) {
                                            Slattitude = Double.parseDouble(providerlat.getString("provider_lat"));
                                            Slongitude = Double.parseDouble(providerlat.getString("provider_lng"));

                                        }
                                        JSONObject providerlocation = info_Object.getJSONObject("locality_provider");

                                        if (providerlocation.length() > 0) {
                                            locationlattitude = Double.parseDouble(providerlocation.getString("latitude"));
                                            locationlongitude = Double.parseDouble(providerlocation.getString("longitude"));

                                        }

                                        Object check_provider_object = info_Object.get("provider");
                                        if (check_provider_object instanceof JSONObject) {

                                            JSONObject provider_Object = info_Object.getJSONObject("provider");
                                            if (provider_Object.length() > 0) {
                                                mTaskerID = provider_Object.getString("provider_id");
                                                pojo.setProvider_id(mTaskerID);
                                                pojo.setProvider_name(provider_Object.getString("provider_name"));
                                                pojo.setProvider_email(provider_Object.getString("provider_email"));
                                                pojo.setProvider_mobile(provider_Object.getString("provider_mobile"));
                                                pojo.setProvider_image(provider_Object.getString("provider_image"));
                                                pojo.setProvider_ratings(provider_Object.getString("provider_ratings"));
                                                pojo.setBio(provider_Object.getString("bio"));
                                                provider_hourly_rate=provider_Object.getString("provider_hourlyrate");
                                                provider_minimum_rate=provider_Object.getString("provider_minimumhourlyrate");
                                                String bio = provider_Object.getString("bio");
                                                System.out.println("BIO value------" + bio);

                                                isProviderAvailable = true;
                                            } else {
                                                isProviderAvailable = false;
                                            }
                                        } else {
                                            isProviderAvailable = false;
                                        }

                                        infoList.add(pojo);

                                        isInfoAvailable = true;
                                    } else {
                                        isInfoAvailable = false;
                                    }
                                } else {
                                    infoList.clear();
                                    isInfoAvailable = false;
                                }


                                Object check_timeline_object = response_Object.get("timeline");
                                if (check_timeline_object instanceof JSONArray) {

                                    JSONArray timeline_Array = response_Object.getJSONArray("timeline");
                                    if (timeline_Array.length() > 0) {

                                        timeLineList.clear();
                                        for (int i = 0; i < timeline_Array.length(); i++) {
                                            JSONObject timeline_Object = timeline_Array.getJSONObject(i);
                                            MyJobDetailTimeLinePojo timePojo = new MyJobDetailTimeLinePojo();

                                            timePojo.setTitle(timeline_Object.getString("title"));
                                            timePojo.setDate(timeline_Object.getString("date"));
                                            timePojo.setTime(timeline_Object.getString("time"));

                                            timeLineList.add(timePojo);
                                        }
                                        isTimeLineAvailable = true;
                                    } else {
                                        isTimeLineAvailable = false;
                                    }
                                } else {
                                    isTimeLineAvailable = false;
                                }

                                isDataAvailable = true;

                            } else {
                                isDataAvailable = false;
                            }
                        } else {
                            isDataAvailable = false;
                        }
                    }


                    if (sStatus.equalsIgnoreCase("1")) {

                        if (!cancel_reason.equalsIgnoreCase("")) {
                            cancel_layout.setVisibility(View.VISIBLE);
                            cancel_reason_textview.setText(cancel_reason);
                        } else {
                            cancel_layout.setVisibility(View.GONE);
                        }
                        if (isDataAvailable) {

                            if (isInfoAvailable) {

                                Rl_detail.setVisibility(View.VISIBLE);
                                Rl_responses.setVisibility(View.GONE);
                                Vi_detail.setBackgroundColor(Color.parseColor("#16a085"));
                                Vi_responses.setBackgroundColor(Color.parseColor("#252525"));

                                Tv_headerTitle.setText(infoList.get(0).getService_type()+ " "+ "-" + "ID : "+" "+job_id);
                                Tv_dateAndTime.setText(infoList.get(0).getBook_date() + "," + infoList.get(0).getBook_time());

                                address = getCompleteAddressString(Double.parseDouble(usercurrentlat), Double.parseDouble(usercurrentlong));

                                Tv_location.setText(address);

                                //  Tv_location.setText(infoList.get(0).getLocation());


                                //Set Marker on Map
                                if (infoList.get(0).getLat().length() > 0 && infoList.get(0).getLng().length() > 0) {
                                    double dLatitude = Double.parseDouble(infoList.get(0).getLat());
                                    double dLongitude = Double.parseDouble(infoList.get(0).getLng());

                                    // Move the camera to last position with a zoom level
                                    CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(dLatitude, dLongitude)).zoom(17).build();
                                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                                    // adding marker
                                    MarkerOptions marker = new MarkerOptions().position(new LatLng(dLatitude, dLongitude));
                                    googleMap.addMarker(marker);
                                }


                                //Set the Job Status At Bottom
                                if (infoList.get(0).getDo_cancel().equalsIgnoreCase("Yes")) {

                                        Iv_jobStatusArrow.setVisibility(View.VISIBLE);
                                        Rl_JobStatus.setEnabled(true);
                                        Rl_JobStatus.setBackgroundColor(Color.parseColor("#F44336"));
                                        Tv_jobStatus.setText(getResources().getString(R.string.myJobs_detail_label_cancel_job));

                                }

                                else if (infoList.get(0).getJob_status().equalsIgnoreCase("StartOff")) {
                                    Rl_JobStatus.setVisibility(View.INVISIBLE);
                                    trackvisit.setVisibility(View.VISIBLE);
                                    trackvisit.setEnabled(true);
                                    canceljob.setVisibility(View.GONE);
                                    // trackvisit.setBackgroundColor(Color.parseColor("#F44336"));
                                }
                                else if (infoList.get(0).getNeed_payment().equalsIgnoreCase("Yes")) {
                                    Iv_jobStatusArrow.setVisibility(View.VISIBLE);
                                    Rl_JobStatus.setEnabled(true);
                                    Rl_JobStatus.setBackgroundColor(Color.parseColor("#017500"));
                                    Tv_jobStatus.setVisibility(View.INVISIBLE);
                                    Tv_fare_summary.setVisibility(View.VISIBLE);
                                    Tv_payment.setVisibility(View.VISIBLE);
                                    view.setVisibility(View.VISIBLE);
                                    Iv_jobStatusArrow.setVisibility(View.GONE);
                                    // Tv_jobStatus.setText(getResources().getString(R.string.myJobs_detail_label_make_payment));
                                } else if (infoList.get(0).getSubmit_ratings().equalsIgnoreCase("Yes")) {
                                    Iv_jobStatusArrow.setVisibility(View.VISIBLE);
                                    Rl_JobStatus.setEnabled(true);
                                    Rl_JobStatus.setBackgroundColor(Color.parseColor("#F8B503"));
                                    Tv_jobStatus.setText(getResources().getString(R.string.myJobs_detail_label_provide_rating));
                                } else if (infoList.get(0).getJob_status().equalsIgnoreCase("Arrived")) {
                                    Rl_JobStatus.setVisibility(View.VISIBLE);
                                    Iv_jobStatusArrow.setVisibility(View.GONE);
                                    trackvisit.setVisibility(View.GONE);
                                    Rl_JobStatus.setEnabled(false);
                                    Rl_JobStatus.setBackgroundColor(Color.parseColor("#B8B8B8"));
                                    Tv_jobStatus.setText("ARRIVED");
                                    Tv_jobStatus.setEnabled(false);

                                } else if (infoList.get(0).getJob_status().equalsIgnoreCase("StartJob")) {
                                    Rl_JobStatus.setVisibility(View.VISIBLE);
                                    Iv_jobStatusArrow.setVisibility(View.GONE);
                                    trackvisit.setVisibility(View.GONE);
                                    Rl_JobStatus.setEnabled(false);
                                    Rl_JobStatus.setBackgroundColor(Color.parseColor("#B8B8B8"));
                                    Tv_jobStatus.setText("START JOB");
                                    Tv_jobStatus.setEnabled(false);

                                } else if (infoList.get(0).getJob_status().equalsIgnoreCase("Cancelled")) {
                                    Rl_JobStatus.setEnabled(false);
                                    Rl_JobStatus.setBackgroundColor(Color.parseColor("#A9A9A9"));
                                    Tv_jobStatus.setText("CANCELLED");
                                    chat_text.setTextColor(Color.parseColor("#A9A9A9"));
                                    Ll_chat.setEnabled(false);

                                } else {
                                    Iv_jobStatusArrow.setVisibility(View.INVISIBLE);
                                    Rl_JobStatus.setEnabled(true);
                                    Rl_JobStatus.setBackgroundColor(Color.parseColor("#711da9"));
                                    Tv_jobStatus.setText("MORE INFO");
                                }


                                //Showing Provider Information
                                if (isProviderAvailable) {

                                    Tv_noProviderAssigned.setVisibility(View.GONE);
                                    Rl_provider.setVisibility(View.VISIBLE);

                                    Picasso.with(MyJobDetail.this).load(infoList.get(0).getProvider_image()).error(R.drawable.placeholder_icon)
                                            .placeholder(R.drawable.placeholder_icon).memoryPolicy(MemoryPolicy.NO_CACHE).fit().into(Im_userImage);
                                    Tv_username.setText(infoList.get(0).getProvider_name());
                                    Tv_email.setText(infoList.get(0).getProvider_email());
                                    Tv_bio.setText(infoList.get(0).getBio());
                                    Rb_rating.setRating(Float.parseFloat(infoList.get(0).getProvider_ratings()));
                                } else {
                                    Tv_noProviderAssigned.setVisibility(View.VISIBLE);
                                    Rl_provider.setVisibility(View.GONE);
                                }

                                //Marking Check Status
                                if (infoList.get(0).getJob_status().equalsIgnoreCase("Onprogress")) {
                                    Iv_request.setImageResource(R.drawable.tick_circle_icon);
                                    Iv_assigned.setImageResource(R.drawable.empty_circle_icon);
                                    Iv_delivery.setImageResource(R.drawable.empty_circle_icon);
                                }
                                else if (infoList.get(0).getJob_status().equalsIgnoreCase("Completed")) {
                                    Iv_request.setImageResource(R.drawable.tick_circle_icon);
                                    Iv_assigned.setImageResource(R.drawable.tick_circle_icon);
                                    Iv_delivery.setImageResource(R.drawable.tick_circle_icon);
                                }
                                else if (isProviderAvailable) {
                                    Iv_request.setImageResource(R.drawable.tick_circle_icon);
                                    Iv_assigned.setImageResource(R.drawable.tick_circle_icon);
                                    Iv_delivery.setImageResource(R.drawable.empty_circle_icon);
                                }
                                else {
                                    Iv_request.setImageResource(R.drawable.tick_circle_icon);
                                    Iv_assigned.setImageResource(R.drawable.empty_circle_icon);
                                    Iv_delivery.setImageResource(R.drawable.empty_circle_icon);

                                }
                            } else {
                                Rl_detail.setVisibility(View.GONE);
                                Rl_responses.setVisibility(View.GONE);
                                Vi_detail.setBackgroundColor(Color.parseColor("#252525"));
                                Vi_responses.setBackgroundColor(Color.parseColor("#252525"));
                            }


                            if (isTimeLineAvailable) {
                                listView.setVisibility(View.VISIBLE);
                                adapter = new MyJobsDetailTimeLineAdapter(MyJobDetail.this, timeLineList);
                                listView.setAdapter(adapter);
                            } else {
                                listView.setVisibility(View.GONE);
                            }

                        }
                    } else {
                        String sResponse = object.getString("response");
                        alert(getResources().getString(R.string.action_sorry), sResponse);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    stopLoading();
                }
                stopLoading();
            }

            @Override
            public void onErrorListener() {
                stopLoading();
            }
        });
    }


    //-----------------------MyRide Cancel Reason Post Request-----------------
    private void postRequest_CancelJob_Reason(String Url, final String sJobId) {

        final LoadingDialog mLoading = new LoadingDialog(MyJobDetail.this);
        mLoading.setLoadingTitle(getResources().getString(R.string.action_pleaseWait));
        mLoading.show();


        System.out.println("-------------Cancel Job Reason Url----------------" + Url);
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", sUserID);

        mRequest = new ServiceRequest(MyJobDetail.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("-------------Cancel Job Reason Response----------------" + response);

                String sStatus = "";

                try {
                    JSONObject object = new JSONObject(response);
                    sStatus = object.getString("status");
                    if (sStatus.equalsIgnoreCase("1")) {
                        JSONObject response_object = object.getJSONObject("response");
                        if (response_object.length() > 0) {

                            Object check_reason_object = response_object.get("reason");
                            if (check_reason_object instanceof JSONArray) {

                                JSONArray reason_array = response_object.getJSONArray("reason");
                                if (reason_array.length() > 0) {
                                    itemList_reason.clear();
                                    for (int i = 0; i < reason_array.length(); i++) {
                                        JSONObject reason_object = reason_array.getJSONObject(i);
                                        CancelJobPojo pojo = new CancelJobPojo();
                                        pojo.setReason(reason_object.getString("reason"));
                                        pojo.setReasonId(reason_object.getString("id"));

                                        itemList_reason.add(pojo);
                                    }

                                    isReasonAvailable = true;
                                } else {
                                    isReasonAvailable = false;
                                }
                            }
                        }
                    } else {
                        String sResponse = object.getString("response");
                        alert(getResources().getString(R.string.action_sorry), sResponse);
                    }


                    if (sStatus.equalsIgnoreCase("1") && isReasonAvailable) {
                        Intent passIntent = new Intent(MyJobDetail.this, CancelJob.class);
                        Bundle bundleObject = new Bundle();
                        bundleObject.putSerializable("Reason", itemList_reason);
                        passIntent.putExtras(bundleObject);
                        passIntent.putExtra("JOB_ID", sJobId);
                        startActivity(passIntent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                mLoading.dismiss();
            }

            @Override
            public void onErrorListener() {
                mLoading.dismiss();
            }
        });
    }


    @Override
    public void onDestroy() {
        // Unregister the logout receiver
        unregisterReceiver(finishReceiver);

        sessionManager.setMyJobsDetailOpen("Closed");
        super.onDestroy();
    }


    @Override
    public void onResume() {
        super.onResume();
        sessionManager.setMyJobsDetailOpen("Opened");
        if (!ChatMessageService.isStarted()) {
            Intent intent = new Intent(MyJobDetail.this, ChatMessageService.class);
            startService(intent);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
//        sessionManager.setMyJobsDetailOpen("Closed");
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    //-------------Method to get Complete Address------------
    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(MyJobDetail.this, Locale.getDefault());
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
}
