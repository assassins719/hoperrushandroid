package com.hoperrush.app;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ParseException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.hoperrush.R;
import com.hoperrush.adapter.MyJobsListAdapter;
import com.hoperrush.core.dialog.LoadingDialog;
import com.hoperrush.core.dialog.PkDialog;
import com.hoperrush.core.dialog.PkLoadingDialog;
import com.hoperrush.core.socket.ChatMessageService;
import com.hoperrush.core.volley.ServiceRequest;
import com.hoperrush.iconstant.Iconstant;
import com.hoperrush.pojo.CancelJobPojo;
import com.hoperrush.pojo.MyJobsListPojo;
import com.hoperrush.utils.ConnectionDetector;
import com.hoperrush.utils.SessionManager;
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

import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;

/**
 * Casperon Technology on 1/11/2016.
 */
public class MyJobs extends FragmentActivity {
    private ConnectionDetector cd;
    private boolean isInternetPresent = false;
    private SessionManager sessionManager;

    private RelativeLayout Rl_back;
    private ImageView Im_backIcon;
    private TextView Tv_headerTitle;

    private RelativeLayout Rl_layout_filter;
    final int PERMISSION_REQUEST_CODE = 111;
    String callphone = "";
    String callcountrycode = "";
    final int PERMISSION_REQUEST_CODES = 222;

    private ServiceRequest mRequest;
    private PkLoadingDialog mLoadingDialog;

    private ListView listView;
    private RelativeLayout Rl_NoInternet, Rl_Main;
    private WaveSwipeRefreshLayout swipeToRefresh;
    private String Str_Refresh_Name = "normal";
    private TextView Tv_empty;
    private LinearLayout Ll_All, Ll_Closed, Ll_Cancelled;
    private TextView Tv_All, Tv_Closed, Tv_Cancelled;

    private String sTabSelectedCheck = "1";
    private String sUserID = "";
    private boolean isJobAvailable = false;
    private ArrayList<MyJobsListPojo> jobsList;
    private MyJobsListAdapter adapter;

    private String Filter_booking_type="0";
    private String Filter_type="no";
    private String seleceteddate = "1";
    private String Filter_completed="0";
    private String Filter_cancelled="0";


    private RelativeLayout Rl_layoyt_datefrom, Rl_layout_to;
    private TextView Tv_fromdaate, Tv_todate;

    private RelativeLayout Rl_LoadMore;
    private boolean loadingMore = false;
    private int checkPagePos = 0;

    private Dialog sort_dialog;
    ArrayList<CancelJobPojo> itemList_reason;
    private boolean isReasonAvailable = false;

    private ImageView Img_filter;

    private RelativeLayout sorting_cancel, sorting_apply, sortingDate, sortingname, Ascending_orderby, Descending_Orderby;
    private ImageView sorting_checkename, sorting_checkeddate, sorting_checkedthree, sorting_ascendingimg, sorting_descinngimg;
    private String Sselected_sorting = "";
    private String Sselected_ordrby = "";
    private RelativeLayout today_booking,recent_booking,upcoming_booking;
    private ImageView today_booking_image,recent_booking_image,upcoming_booking_image;

    private CaldroidFragment dialogCaldroidFragment;
    final SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");

    public static Activity Myjobs_page;
    public class RefreshReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.package.ACTION_CLASS_MY_JOBS_REFRESH")) {
                if (isInternetPresent) {
                    String job_status=intent.getExtras().getString("status");
                    if(job_status.equalsIgnoreCase("cancelled")){
                        Filter_cancelled="1";
                        sTabSelectedCheck = "5";
                        Ll_All.setBackgroundColor(0xFFFFFFFF);
                        Ll_Closed.setBackgroundColor(0xFFFFFFFF);
                        Ll_Cancelled.setBackgroundColor(0xFF00897B);
                        Tv_All.setTextColor(0xFF00897B);
                        Tv_Closed.setTextColor(0xFF00897B);
                        Tv_Cancelled.setTextColor(0xFFFFFFFF);
                        postJobRequest(Iconstant.MyJobsList_Url, sTabSelectedCheck);
                    }
                    else{

                        postJobRequest(Iconstant.MyJobsList_Url, sTabSelectedCheck);
                    }

                }
            }
        }
    }

    private RefreshReceiver refreshReceiver;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myjobs_list);
        Myjobs_page=MyJobs.this;
        if(MyJobDetail.Myjob_page!=null){
            MyJobDetail.Myjob_page.finish();
        }
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

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

                int threshold = 1;
                int count = listView.getCount();

                if (scrollState == SCROLL_STATE_IDLE) {
                    if (listView.getLastVisiblePosition() >= count - threshold && !(loadingMore)) {

                        if (swipeToRefresh.isRefreshing()) {
                            //nothing happen(code to block loadMore functionality when swipe to refresh is loading)
                        } else {

                            if (isJobAvailable) {
                                ConnectionDetector cd = new ConnectionDetector(MyJobs.this);
                                boolean isInternetPresent = cd.isConnectingToInternet();

                                if (isInternetPresent) {
                                    postJobLoadMoreRequest(Iconstant.MyJobsList_Url, sTabSelectedCheck, (checkPagePos + 1));
                                } else {
                                    alert(getResources().getString(R.string.action_no_internet_title), getResources().getString(R.string.action_no_internet_message));
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0) {
                    swipeToRefresh.setEnabled(true);
                } else {
                    swipeToRefresh.setEnabled(false);
                }
            }
        });


        swipeToRefresh.setOnRefreshListener(new WaveSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (loadingMore) {
                    //nothing happen(code to block swipe functionality when loadmore is loading)
                    swipeToRefresh.setRefreshing(false);
                } else {
                    cd = new ConnectionDetector(MyJobs.this);
                    isInternetPresent = cd.isConnectingToInternet();

                    if (isInternetPresent) {
                        Rl_Main.setVisibility(View.VISIBLE);
                        Rl_NoInternet.setVisibility(View.GONE);
                        Str_Refresh_Name = "swipe";
                        postJobRequest(Iconstant.MyJobsList_Url, sTabSelectedCheck);
                    } else {
                        swipeToRefresh.setEnabled(true);
                        swipeToRefresh.setRefreshing(false);
                        Rl_Main.setVisibility(View.GONE);
                        Rl_NoInternet.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        Ll_All.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Filter_cancelled="0";
                Filter_completed="0";
                sTabSelectedCheck = "1";
                Ll_All.setBackgroundColor(0xFF00897B);
                Ll_Closed.setBackgroundColor(0xFFFFFFFF);
                Ll_Cancelled.setBackgroundColor(0xFFFFFFFF);
                Tv_All.setTextColor(0xFFFFFFFF);
                Tv_Closed.setTextColor(0xFF00897B);
                Tv_Cancelled.setTextColor(0xFF00897B);

                ConnectionDetector cd = new ConnectionDetector(MyJobs.this);
                boolean isInternetPresent = cd.isConnectingToInternet();
                if (isInternetPresent) {
                    Str_Refresh_Name = "normal";
                    postJobRequest(Iconstant.MyJobsList_Url, sTabSelectedCheck);
                } else {
                    alert(getResources().getString(R.string.action_no_internet_title), getResources().getString(R.string.action_no_internet_message));
                }

            }
        });

        Ll_Closed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Filter_completed="1";
                sTabSelectedCheck = "4";
                Ll_All.setBackgroundColor(0xFFFFFFFF);
                Ll_Closed.setBackgroundColor(0xFF00897B);
                Ll_Cancelled.setBackgroundColor(0xFFFFFFFF);
                Tv_All.setTextColor(0xFF00897B);
                Tv_Closed.setTextColor(0xFFFFFFFF);
                Tv_Cancelled.setTextColor(0xFF00897B);

                ConnectionDetector cd = new ConnectionDetector(MyJobs.this);
                boolean isInternetPresent = cd.isConnectingToInternet();
                if (isInternetPresent) {
                    Str_Refresh_Name = "normal";
                    postJobRequest(Iconstant.MyJobsList_Url, sTabSelectedCheck);
                } else {
                    alert(getResources().getString(R.string.action_no_internet_title), getResources().getString(R.string.action_no_internet_message));
                }

            }
        });

        Ll_Cancelled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Filter_cancelled="1";
                sTabSelectedCheck = "5";
                Ll_All.setBackgroundColor(0xFFFFFFFF);
                Ll_Closed.setBackgroundColor(0xFFFFFFFF);
                Ll_Cancelled.setBackgroundColor(0xFF00897B);
                Tv_All.setTextColor(0xFF00897B);
                Tv_Closed.setTextColor(0xFF00897B);
                Tv_Cancelled.setTextColor(0xFFFFFFFF);

                ConnectionDetector cd = new ConnectionDetector(MyJobs.this);
                boolean isInternetPresent = cd.isConnectingToInternet();
                if (isInternetPresent) {
                    Str_Refresh_Name = "normal";
                    postJobRequest(Iconstant.MyJobsList_Url, sTabSelectedCheck);
                } else {
                    alert(getResources().getString(R.string.action_no_internet_title), getResources().getString(R.string.action_no_internet_message));
                }

            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                cd = new ConnectionDetector(MyJobs.this);
                isInternetPresent = cd.isConnectingToInternet();
                if (isInternetPresent) {
                    Intent intent = new Intent(MyJobs.this, MyJobDetail.class);
                    intent.putExtra("JOB_ID_INTENT", jobsList.get(position).getJob_id());
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                } else {
                    alert(getResources().getString(R.string.action_no_internet_title), getResources().getString(R.string.action_no_internet_message));
                }

            }
        });


        Rl_layout_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                chooseSortingImage(savedInstanceState);
            }
        });


    }

    private void initializeHeaderBar() {
        RelativeLayout headerBar = (RelativeLayout) findViewById(R.id.headerBar_noShadow_layout);
        Rl_back = (RelativeLayout) headerBar.findViewById(R.id.headerBar_noShadow_left_layout);
        Im_backIcon = (ImageView) headerBar.findViewById(R.id.headerBar_noShadow_imageView);
        Tv_headerTitle = (TextView) headerBar.findViewById(R.id.headerBar_noShadow_title_textView);


        Tv_headerTitle.setText(getResources().getString(R.string.myJobs_label_title));
        Im_backIcon.setImageResource(R.drawable.back_arrow);
    }

    private void initialize() {
        cd = new ConnectionDetector(MyJobs.this);
        isInternetPresent = cd.isConnectingToInternet();
        sessionManager = new SessionManager(MyJobs.this);
        mRequest = new ServiceRequest(MyJobs.this);
        jobsList = new ArrayList<MyJobsListPojo>();
        itemList_reason = new ArrayList<CancelJobPojo>();

        listView = (ListView) findViewById(R.id.myJobs_listView);
        swipeToRefresh = (WaveSwipeRefreshLayout) findViewById(R.id.myJobs_swipeToRefresh_layout);
        Rl_NoInternet = (RelativeLayout) findViewById(R.id.myJobs_noInternet_layout);
        Rl_Main = (RelativeLayout) findViewById(R.id.myJobs_main_layout);
        Tv_empty = (TextView) findViewById(R.id.myJobs_empty_textView);
        Ll_All = (LinearLayout) findViewById(R.id.myJobs_all_layout);
        Ll_Closed = (LinearLayout) findViewById(R.id.myJobs_closed_layout);
        Ll_Cancelled = (LinearLayout) findViewById(R.id.myJobs_cancelled_layout);
        Tv_All = (TextView) findViewById(R.id.myJobs_all_textView);
        Tv_Closed = (TextView) findViewById(R.id.myJobs_closed_textView);
        Tv_Cancelled = (TextView) findViewById(R.id.myJobs_cancelled_textView);
        Rl_LoadMore = (RelativeLayout) findViewById(R.id.myJobs_loadMore_layout);

        //Img_filter =(ImageView)findViewById(R.id.headerBar_filter_imageView);
        Rl_layout_filter = (RelativeLayout) findViewById(R.id.filter_layout);

        // Configure the refreshing colors
        swipeToRefresh.setColorSchemeColors(Color.WHITE, Color.WHITE);
        swipeToRefresh.setWaveColor(getResources().getColor(R.color.app_color));
        swipeToRefresh.setMaxDropHeight(350);//should Give in Hundreds
        swipeToRefresh.setEnabled(true);

       Intent i=getIntent();
        String job_status=i.getExtras().getString("status");
        if(job_status.equalsIgnoreCase("cancelled")){
            Filter_cancelled="1";
            sTabSelectedCheck = "5";
            Ll_All.setBackgroundColor(0xFFFFFFFF);
            Ll_Closed.setBackgroundColor(0xFFFFFFFF);
            Ll_Cancelled.setBackgroundColor(0xFF00897B);
            Tv_All.setTextColor(0xFF00897B);
            Tv_Closed.setTextColor(0xFF00897B);
            Tv_Cancelled.setTextColor(0xFFFFFFFF);
        }
        else{
            Ll_All.setBackgroundColor(0xFF00897B);
            Tv_All.setTextColor(0xFFFFFFFF);
        }
        // -----code to refresh drawer using broadcast receiver-----
        refreshReceiver = new RefreshReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.package.ACTION_CLASS_MY_JOBS_REFRESH");
        registerReceiver(refreshReceiver, intentFilter);


        // get user data from session
        HashMap<String, String> user = sessionManager.getUserDetails();
        sUserID = user.get(SessionManager.KEY_USER_ID);

        if (isInternetPresent) {
            Rl_Main.setVisibility(View.VISIBLE);
            Rl_NoInternet.setVisibility(View.GONE);
            if(job_status.equalsIgnoreCase("cancelled")){
                Filter_cancelled="1";
                sTabSelectedCheck = "5";
                Ll_All.setBackgroundColor(0xFFFFFFFF);
                Ll_Closed.setBackgroundColor(0xFFFFFFFF);
                Ll_Cancelled.setBackgroundColor(0xFF00897B);
                Tv_All.setTextColor(0xFF00897B);
                Tv_Closed.setTextColor(0xFF00897B);
                Tv_Cancelled.setTextColor(0xFFFFFFFF);
                postJobRequest(Iconstant.MyJobsList_Url, sTabSelectedCheck);
            }
            else{
                postJobRequest(Iconstant.MyJobsList_Url, sTabSelectedCheck);
            }

        } else {
            swipeToRefresh.setEnabled(true);
            Rl_Main.setVisibility(View.GONE);
            Rl_NoInternet.setVisibility(View.VISIBLE);
        }

    }


    private void chooseSortingImage(final Bundle savedInstanceState) {
        sort_dialog = new Dialog(MyJobs.this);
        sort_dialog.getWindow();
        sort_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        sort_dialog.setContentView(R.layout.sorting_layout);
        sort_dialog.setCanceledOnTouchOutside(true);
        sort_dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_photo_Picker;
        sort_dialog.show();
        sort_dialog.getWindow().setGravity(Gravity.CENTER);
        final TextView name_text=(TextView)sort_dialog.findViewById(R.id.name_text);
        final TextView date_text=(TextView)sort_dialog.findViewById(R.id.date_text);
        TextView today_booking_text=(TextView)sort_dialog.findViewById(R.id.today_booking_text);
        TextView upcoming_booking_text=(TextView)sort_dialog.findViewById(R.id.upcoming_booking_text);
        TextView recent_booking_text=(TextView)sort_dialog.findViewById(R.id.recent_booking_text);
        sorting_cancel = (RelativeLayout) sort_dialog.findViewById(R.id.cancel_sorting_clearlayout);
        sorting_apply = (RelativeLayout) sort_dialog.findViewById(R.id.sorting_apply_layout);
        sortingDate = (RelativeLayout) sort_dialog.findViewById(R.id.subcategories_sorting_date_layout);
        Ascending_orderby = (RelativeLayout) sort_dialog.findViewById(R.id.subcategories_sorting_ascending_layout);
        Descending_Orderby = (RelativeLayout) sort_dialog.findViewById(R.id.subcategories_sorting_descending_layout);
        sortingname = (RelativeLayout) sort_dialog.findViewById(R.id.subcategories_sortingname_layout);
        today_booking=(RelativeLayout)sort_dialog.findViewById(R.id.today_booking);
        recent_booking=(RelativeLayout)sort_dialog.findViewById(R.id.recent_booking);
        upcoming_booking=(RelativeLayout)sort_dialog.findViewById(R.id.upcoming_booking);



        Tv_fromdaate = (TextView) sort_dialog.findViewById(R.id.from_date_select_textView_myjobs);
        Tv_todate = (TextView) sort_dialog.findViewById(R.id.todate_select_textViewmyjobs);
        Rl_layoyt_datefrom = (RelativeLayout) sort_dialog.findViewById(R.id.myjooobsfrom_page_date_select_layout);
        Rl_layout_to = (RelativeLayout) sort_dialog.findViewById(R.id.myjobstodate_select_layout);


        sorting_checkeddate = (ImageView) sort_dialog.findViewById(R.id.sorting_checkedate);
        // sorting_checkedthree=(ImageView)sort_dialog.findViewById(R.id.checkedthree);
        sorting_checkename = (ImageView) sort_dialog.findViewById(R.id.subcategories_sorting_checkename);
        sorting_ascendingimg = (ImageView) sort_dialog.findViewById(R.id.subcategories_ascendingsorting_ascending);
        sorting_descinngimg = (ImageView) sort_dialog.findViewById(R.id.checkeddescending);
        today_booking_image=(ImageView)sort_dialog.findViewById(R.id.today_booking_image);
        recent_booking_image=(ImageView)sort_dialog.findViewById(R.id.recent_booking_image);
        upcoming_booking_image=(ImageView)sort_dialog.findViewById(R.id.upcoming_booking_image);


       if(Filter_completed.equalsIgnoreCase("1")){
           today_booking.setEnabled(false);
           recent_booking.setEnabled(false);
           upcoming_booking.setEnabled(false);
           today_booking_text.setTextColor(Color.parseColor("#DCDCDC"));
           recent_booking_text.setTextColor(Color.parseColor("#DCDCDC"));
           upcoming_booking_text.setTextColor(Color.parseColor("#DCDCDC"));
       }
        if(Filter_cancelled.equalsIgnoreCase("1")){

            today_booking.setEnabled(false);
            recent_booking.setEnabled(false);
            upcoming_booking.setEnabled(false);
            today_booking_text.setTextColor(Color.parseColor("#DCDCDC"));
            recent_booking_text.setTextColor(Color.parseColor("#DCDCDC"));
            upcoming_booking_text.setTextColor(Color.parseColor("#DCDCDC"));
        }

        Rl_layoyt_datefrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                seleceteddate = "1";
                datePicker(savedInstanceState);
            }
        });


        Rl_layout_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleceteddate = "2";
                datePicker(savedInstanceState);
            }
        });

        sorting_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sort_dialog.dismiss();

            }
        });


        sortingname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sorting_checkename.setVisibility(View.VISIBLE);
                sorting_checkeddate.setVisibility(View.GONE);

                Sselected_sorting = "name";
            }
        });


        sortingDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sorting_checkename.setVisibility(View.GONE);
                sorting_checkeddate.setVisibility(View.VISIBLE);

                Sselected_sorting = "date";
            }
        });


        Ascending_orderby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sorting_descinngimg.setVisibility(View.GONE);
                sorting_ascendingimg.setVisibility(View.VISIBLE);

                Sselected_ordrby = "-1";
            }
        });


        Descending_Orderby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sorting_descinngimg.setVisibility(View.VISIBLE);
                sorting_ascendingimg.setVisibility(View.GONE);

                Sselected_ordrby = "1";
            }
        });
        today_booking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                today_booking_image.setVisibility(View.VISIBLE);
                recent_booking_image.setVisibility(View.GONE);
                upcoming_booking_image.setVisibility(View.GONE);
                Filter_booking_type="1";
                Filter_type="today";
                sortingname.setEnabled(false);
                sortingDate.setEnabled(false);
                Rl_layoyt_datefrom.setEnabled(false);
                Rl_layout_to.setEnabled(false);
                sorting_checkename.setVisibility(View.GONE);
                sorting_checkeddate.setVisibility(View.GONE);
                name_text.setTextColor(Color.parseColor("#DCDCDC"));
                date_text.setTextColor(Color.parseColor("#DCDCDC"));
                Tv_fromdaate.setTextColor(Color.parseColor("#DCDCDC"));
                Tv_todate.setTextColor(Color.parseColor("#DCDCDC"));

            }
        });
        recent_booking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recent_booking_image.setVisibility(View.VISIBLE);
                today_booking_image.setVisibility(View.GONE);
                upcoming_booking_image.setVisibility(View.GONE);
                Filter_booking_type="1";
                Filter_type="recent";
                sortingname.setEnabled(false);
                sortingDate.setEnabled(false);
                Rl_layoyt_datefrom.setEnabled(false);
                Rl_layout_to.setEnabled(false);
                sorting_checkename.setVisibility(View.GONE);
                sorting_checkeddate.setVisibility(View.GONE);
                name_text.setTextColor(Color.parseColor("#DCDCDC"));
                date_text.setTextColor(Color.parseColor("#DCDCDC"));
                Tv_fromdaate.setTextColor(Color.parseColor("#DCDCDC"));
                Tv_todate.setTextColor(Color.parseColor("#DCDCDC"));
            }
        });
        upcoming_booking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upcoming_booking_image.setVisibility(View.VISIBLE);
                today_booking_image.setVisibility(View.GONE);
                recent_booking_image.setVisibility(View.GONE);
                Filter_booking_type="1";
                Filter_type="upcoming";
                sortingname.setEnabled(false);
                sortingDate.setEnabled(false);
                Rl_layoyt_datefrom.setEnabled(false);
                Rl_layout_to.setEnabled(false);
                sorting_checkename.setVisibility(View.GONE);
                sorting_checkeddate.setVisibility(View.GONE);
                name_text.setTextColor(Color.parseColor("#DCDCDC"));
                date_text.setTextColor(Color.parseColor("#DCDCDC"));
                Tv_fromdaate.setTextColor(Color.parseColor("#DCDCDC"));
                Tv_todate.setTextColor(Color.parseColor("#DCDCDC"));
            }
        });


        sorting_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sort_dialog.dismiss();

                ConnectionDetector cd = new ConnectionDetector(MyJobs.this);
                boolean isInternetPresent = cd.isConnectingToInternet();
                if (isInternetPresent) {

                    if(Filter_booking_type.equalsIgnoreCase("1")){

                        postJobRequestSorting(Iconstant.Filter_booking_url, Filter_type);
                    }else{

                        postJobRequestSorting(Iconstant.MyJobsList_Url, sTabSelectedCheck);

                        System.out.println("---------sortingurl------------" + Iconstant.MyJobsList_Url);
                    }

                } else {
                    alert(getResources().getString(R.string.action_no_internet_title), getResources().getString(R.string.action_no_internet_message));

                }

                Sselected_sorting = "";
            }
        });
    }


    //------Alert Method-----
    private void alert(String title, String message) {
        final PkDialog mDialog = new PkDialog(MyJobs.this);
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
            cal.add(Calendar.DATE, 7);
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
            // Tvto_date.setText(getResources().getString(R.string.appointment_label_select_time));

            if (seleceteddate.equalsIgnoreCase("1")) {
                Tv_fromdaate.setText(formatter.format(date));
            } else {
                Tv_todate.setText(formatter.format(date));
            }
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


    //------Cancel Job Reason Method-----
    public void cancelJobReason(final String jobID, final int position) {
        final PkDialog mDialog = new PkDialog(MyJobs.this);
        mDialog.setDialogTitle(getResources().getString(R.string.myJobs_cancel_job_alert_title));
        mDialog.setDialogMessage(getResources().getString(R.string.myJobs_cancel_job_alert));
        mDialog.setPositiveButton(getResources().getString(R.string.myJobs_cancel_job_alert_yes), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                cd = new ConnectionDetector(MyJobs.this);
                isInternetPresent = cd.isConnectingToInternet();

                if (isInternetPresent) {
                    postRequest_CancelJob_Reason(Iconstant.MyJobs_Cancel_Reason_Url, jobID, position);
                } else {
                    alert(getResources().getString(R.string.action_no_internet_title), getResources().getString(R.string.action_no_internet_message));
                }
            }
        });
        mDialog.setNegativeButton(getResources().getString(R.string.myJobs_cancel_job_alert_no), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    private void startLoading() {
        if (Str_Refresh_Name.equalsIgnoreCase("normal")) {
            mLoadingDialog = new PkLoadingDialog(MyJobs.this);
            mLoadingDialog.show();
        } else {
            swipeToRefresh.setRefreshing(true);
        }
    }

    private void stopLoading() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Str_Refresh_Name.equalsIgnoreCase("normal")) {
                    mLoadingDialog.dismiss();
                } else {
                    swipeToRefresh.setRefreshing(false);
                }
            }
        }, 500);
    }


    //-------------My Jobs Post Request---------------
    private void postJobRequest(String url, final String sType) {

        startLoading();

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", sUserID);
        jsonParams.put("type", sType);
        jsonParams.put("page", "1");
        jsonParams.put("perPage", "20");

        System.out.println("---------My Jobs user_id------------" + sUserID);
        System.out.println("---------My Jobs type------------" + sType);
        System.out.println("---------My Jobs Page page------------" + "1");
        System.out.println("---------My Jobs url------------" + url);

        mRequest.makeServiceRequest(url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("---------My Jobs response------------" + response);

                String Str_status = "";
                try {
                    JSONObject object = new JSONObject(response);
                    Str_status = object.getString("status");

                    if (Str_status.equalsIgnoreCase("1")) {
                        JSONObject response_Object = object.getJSONObject("response");
                        if (response_Object.length() > 0) {

                            checkPagePos = response_Object.getInt("current_page");

                            Object check_jobs_object = response_Object.get("jobs");
                            if (check_jobs_object instanceof JSONArray) {

                                JSONArray jobs_Array = response_Object.getJSONArray("jobs");
                                if (jobs_Array.length() > 0) {
                                    jobsList.clear();
                                    for (int i = 0; i < jobs_Array.length(); i++) {
                                        JSONObject jobs_Object = jobs_Array.getJSONObject(i);
                                        MyJobsListPojo pojo = new MyJobsListPojo();
                                        pojo.setJob_id(jobs_Object.getString("job_id"));
                                        pojo.setJob_time(jobs_Object.getString("job_time"));
                                        pojo.setService_type(jobs_Object.getString("service_type"));
                                        pojo.setService_icon(jobs_Object.getString("service_icon"));
                                        pojo.setBooking_date(jobs_Object.getString("booking_date"));
                                        pojo.setJob_date(jobs_Object.getString("job_date"));
                                        pojo.setJob_status(jobs_Object.getString("job_status"));
                                        pojo.setContact_number(jobs_Object.getString("contact_number"));
                                        pojo.set_countrycode(jobs_Object.getString("country_code"));
                                        pojo.setDoCall(jobs_Object.getString("doCall"));
                                        pojo.setIsSupport(jobs_Object.getString("isSupport"));
                                        pojo.setDoMsg(jobs_Object.getString("doMsg"));
                                        pojo.setDoCancel(jobs_Object.getString("doCancel"));
                                        pojo.setTaskerid(jobs_Object.getString("tasker_id"));
                                        pojo.setTaskid(jobs_Object.getString("task_id"));

                                        System.out.println("task_id----------" + jobs_Object.getString("task_id"));
                                        System.out.println("tasker_id----------" + jobs_Object.getString("tasker_id"));
                                        System.out.println("contact_number-------------" + jobs_Object.getString("contact_number"));

                                        jobsList.add(pojo);
                                    }
                                    isJobAvailable = true;
                                } else {
                                    isJobAvailable = false;
                                    jobsList.clear();
                                }
                            } else {
                                isJobAvailable = false;
                                jobsList.clear();
                            }
                        }
                    } else {
                        jobsList.clear();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    stopLoading();
                    System.out.println("-------JSONException-----------------" + e);
                }

                if (Str_status.equalsIgnoreCase("1") && isJobAvailable) {
                    adapter = new MyJobsListAdapter(MyJobs.this, jobsList);
                    listView.setAdapter(adapter);
                    Tv_empty.setVisibility(View.GONE);
                } else if (Str_status.equalsIgnoreCase("1") && !isJobAvailable) {
                    adapter = new MyJobsListAdapter(MyJobs.this, jobsList);
                    listView.setAdapter(adapter);

                    if (sTabSelectedCheck.equalsIgnoreCase("4")) {
                        System.out.println("typ--------------4");

                        Tv_empty.setText(getResources().getString(R.string.myJobs_label_completedmyJobs_empty));
                        Tv_empty.setVisibility(View.VISIBLE);

                    } else if (sTabSelectedCheck.equalsIgnoreCase("5")) {
                        System.out.println("typ--------------5");

                        Tv_empty.setText(getResources().getString(R.string.myJobs_label_cancelledmyJobs_empty));
                        Tv_empty.setVisibility(View.VISIBLE);
                    } else {

                        Tv_empty.setText(getResources().getString(R.string.myJobs_label_myJobs_empty));
                        Tv_empty.setVisibility(View.VISIBLE);
                    }


                } else if (Str_status.equalsIgnoreCase("0")) {
                    adapter = new MyJobsListAdapter(MyJobs.this, jobsList);
                    listView.setAdapter(adapter);
                    if (sTabSelectedCheck.equalsIgnoreCase("4")) {
                        System.out.println("typ--------------4");

                        Tv_empty.setText(getResources().getString(R.string.myJobs_label_completedmyJobs_empty));
                        Tv_empty.setVisibility(View.VISIBLE);

                    } else if (sTabSelectedCheck.equalsIgnoreCase("5")) {
                        System.out.println("typ--------------5");

                        Tv_empty.setText(getResources().getString(R.string.myJobs_label_cancelledmyJobs_empty));
                        Tv_empty.setVisibility(View.VISIBLE);
                    } else {

                        Tv_empty.setText(getResources().getString(R.string.myJobs_label_myJobs_empty));
                        Tv_empty.setVisibility(View.VISIBLE);
                    }
                }

                stopLoading();
            }

            @Override
            public void onErrorListener() {
                stopLoading();
            }
        });
    }


    //-------------My Jobs Load More Post Request---------------
    private void postJobLoadMoreRequest(String url, String sType, int sPage) {

        Rl_LoadMore.setVisibility(View.VISIBLE);
        loadingMore = true;

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", sUserID);
        jsonParams.put("type", sType);
        jsonParams.put("page", String.valueOf(sPage));
        jsonParams.put("perPage", "20");

        System.out.println("---------My Jobs LoadMore Type------------" + sType);
        System.out.println("---------My Jobs LoadMore Page Pos------------" + sPage);
        System.out.println("---------My Jobs LoadMore url------------" + url);

        mRequest.makeServiceRequest(url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("---------My Jobs LoadMore response------------" + response);

                String Str_status = "";
                try {
                    JSONObject object = new JSONObject(response);
                    Str_status = object.getString("status");

                    if (Str_status.equalsIgnoreCase("1")) {
                        JSONObject response_Object = object.getJSONObject("response");
                        if (response_Object.length() > 0) {

                            checkPagePos = response_Object.getInt("current_page");

                            Object check_jobs_object = response_Object.get("jobs");
                            if (check_jobs_object instanceof JSONArray) {

                                JSONArray jobs_Array = response_Object.getJSONArray("jobs");
                                if (jobs_Array.length() > 0) {
                                    for (int i = 0; i < jobs_Array.length(); i++) {
                                        JSONObject jobs_Object = jobs_Array.getJSONObject(i);
                                        MyJobsListPojo pojo = new MyJobsListPojo();

                                        pojo.setJob_id(jobs_Object.getString("job_id"));
                                        pojo.setJob_time(jobs_Object.getString("job_time"));
                                        pojo.setService_type(jobs_Object.getString("service_type"));
                                        pojo.setService_icon(jobs_Object.getString("service_icon"));
                                        pojo.setBooking_date(jobs_Object.getString("booking_date"));
                                        pojo.setJob_date(jobs_Object.getString("job_date"));
                                        pojo.setJob_status(jobs_Object.getString("job_status"));
                                        pojo.setContact_number(jobs_Object.getString("contact_number"));
                                        pojo.setDoCall(jobs_Object.getString("doCall"));
                                        pojo.setIsSupport(jobs_Object.getString("isSupport"));
                                        pojo.setDoMsg(jobs_Object.getString("doMsg"));
                                        pojo.setDoCancel(jobs_Object.getString("doCancel"));

                                        jobsList.add(pojo);
                                    }
                                    isJobAvailable = true;
                                } else {
                                    isJobAvailable = false;
                                }
                            } else {
                                isJobAvailable = false;
                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    loadingMore = false;
                    Rl_LoadMore.setVisibility(View.GONE);
                    System.out.println("-------JSONException-----------------" + e);
                }

                if (Str_status.equalsIgnoreCase("1") && isJobAvailable) {
                    adapter.notifyDataSetChanged();
                }

                loadingMore = false;
                Rl_LoadMore.setVisibility(View.GONE);
            }

            @Override
            public void onErrorListener() {
                loadingMore = false;
                Rl_LoadMore.setVisibility(View.GONE);
            }
        });
    }


    //-----------------------MyRide Cancel Reason Post Request-----------------
    private void postRequest_CancelJob_Reason(String Url, final String sJobId, int position) {

        final LoadingDialog mLoading = new LoadingDialog(MyJobs.this);
        mLoading.setLoadingTitle(getResources().getString(R.string.action_pleaseWait));
        mLoading.show();


        System.out.println("-------------Cancel Job Reason Url----------------" + Url);
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", sUserID);

        mRequest = new ServiceRequest(MyJobs.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("-------------Cancel Job Reason Response----------------" + response);

                String sStatus = "";

                try {
                    JSONObject object = new JSONObject(response);
                    sStatus = object.getString("status");
                    if (sStatus.equalsIgnoreCase("1")) {
                        JSONObject resp = object.getJSONObject("response");
                        if (resp.length() > 0) {
                            JSONArray response_array = resp.getJSONArray("reason");
                            if (response_array.length() > 0) {
                                itemList_reason.clear();

                                for (int i = 0; i < response_array.length(); i++) {
                                    JSONObject reason_object = response_array.getJSONObject(i);
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

                    } else {
                        String sResponse = object.getString("response");
                        alert(getResources().getString(R.string.action_sorry), sResponse);
                    }


                    if (sStatus.equalsIgnoreCase("1") && isReasonAvailable) {
                        Intent passIntent = new Intent(MyJobs.this, CancelJob.class);
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
        unregisterReceiver(refreshReceiver);
        super.onDestroy();
    }


    @Override
    public void onResume() {
        super.onResume();
        try {
            if (!ChatMessageService.isStarted()) {
                Intent intent = new Intent(MyJobs.this, ChatMessageService.class);
                startService(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //-------------My Jobs Post Request---------------
    private void postJobRequestSorting(String url, String sType) {

        startLoading();
        HashMap<String, String> jsonParams = new HashMap<String, String>();

        if(sType.equalsIgnoreCase("today") ||sType.equalsIgnoreCase("upcoming") || sType.equalsIgnoreCase("recent")  ){


            jsonParams.put("user_id", sUserID);
            jsonParams.put("type", sType);
            jsonParams.put("page", "0");
            jsonParams.put("perPage", "20");
            jsonParams.put("orderby", Sselected_ordrby);
            jsonParams.put("sortby", "");
        }else{

            jsonParams.put("user_id", sUserID);
            jsonParams.put("type", sType);
            jsonParams.put("page", "0");
            jsonParams.put("perPage", "20");
            jsonParams.put("from", Tv_fromdaate.getText().toString());
            jsonParams.put("to", Tv_todate.getText().toString());
            jsonParams.put("orderby", Sselected_ordrby);
            jsonParams.put("sortby", Sselected_sorting);

        }



        System.out.println("---------orderby------------" + Sselected_ordrby);
        System.out.println("---------to------------" + Tv_todate.getText().toString());

        System.out.println("---------from------------" + Tv_fromdaate.getText().toString());
        System.out.println("---------sortby------------" + Sselected_sorting);

        System.out.println("---------My Jobs user_id------------" + sUserID);
        System.out.println("---------My Jobs type------------" + sType);
        System.out.println("---------My Jobs Page page------------" + "1");
        System.out.println("---------My Jobs url------------" + url);

        mRequest.makeServiceRequest(url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("---------My Jobssortingname response------------" + response);

                String Str_status = "";
                try {
                    JSONObject object = new JSONObject(response);
                    Str_status = object.getString("status");

                    if (Str_status.equalsIgnoreCase("1")) {
                        JSONObject response_Object = object.getJSONObject("response");
                        if (response_Object.length() > 0) {

                            checkPagePos = response_Object.getInt("current_page");

                            Object check_jobs_object = response_Object.get("jobs");
                            if (check_jobs_object instanceof JSONArray) {

                                JSONArray jobs_Array = response_Object.getJSONArray("jobs");
                                if (jobs_Array.length() > 0) {
                                    jobsList.clear();
                                    for (int i = 0; i < jobs_Array.length(); i++) {
                                        JSONObject jobs_Object = jobs_Array.getJSONObject(i);
                                        MyJobsListPojo pojo = new MyJobsListPojo();


                                        pojo.setJob_id(jobs_Object.getString("job_id"));
                                        pojo.setJob_time(jobs_Object.getString("job_time"));
                                        pojo.setService_type(jobs_Object.getString("service_type"));
                                        pojo.setService_icon(jobs_Object.getString("service_icon"));
                                        pojo.setBooking_date(jobs_Object.getString("booking_date"));
                                        pojo.setJob_date(jobs_Object.getString("job_date"));
                                        pojo.setJob_status(jobs_Object.getString("job_status"));
                                        pojo.setContact_number(jobs_Object.getString("contact_number"));
                                        pojo.setDoCall(jobs_Object.getString("doCall"));
                                        pojo.setIsSupport(jobs_Object.getString("isSupport"));
                                        pojo.setDoMsg(jobs_Object.getString("doMsg"));
                                        pojo.setDoCancel(jobs_Object.getString("doCancel"));

                                        System.out.println("contact_number-------------" + jobs_Object.getString("contact_number"));

                                        jobsList.add(pojo);
                                    }
                                    isJobAvailable = true;
                                } else {
                                    isJobAvailable = false;
                                    jobsList.clear();
                                }
                            } else {
                                isJobAvailable = false;
                                jobsList.clear();
                            }
                        }
                    } else {
                        jobsList.clear();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    stopLoading();
                    System.out.println("-------JSONException-----------------" + e);
                }

                if (Str_status.equalsIgnoreCase("1") && isJobAvailable) {
                    adapter = new MyJobsListAdapter(MyJobs.this, jobsList);
                    listView.setAdapter(adapter);
                    Tv_empty.setVisibility(View.GONE);
                } else if (Str_status.equalsIgnoreCase("1") && !isJobAvailable) {
                    adapter = new MyJobsListAdapter(MyJobs.this, jobsList);
                    listView.setAdapter(adapter);

                    if (sTabSelectedCheck.equalsIgnoreCase("4")) {
                        System.out.println("typ--------------4");

                        Tv_empty.setText(getResources().getString(R.string.myJobs_label_completedmyJobs_empty));
                        Tv_empty.setVisibility(View.VISIBLE);

                    } else if (sTabSelectedCheck.equalsIgnoreCase("5")) {
                        System.out.println("typ--------------5");

                        Tv_empty.setText(getResources().getString(R.string.myJobs_label_cancelledmyJobs_empty));
                        Tv_empty.setVisibility(View.VISIBLE);
                    } else {

                        Tv_empty.setText(getResources().getString(R.string.myJobs_label_myJobs_empty));
                        Tv_empty.setVisibility(View.VISIBLE);
                    }


                } else if (Str_status.equalsIgnoreCase("0")) {
                    adapter = new MyJobsListAdapter(MyJobs.this, jobsList);
                    listView.setAdapter(adapter);
                    if (sTabSelectedCheck.equalsIgnoreCase("4")) {
                        System.out.println("typ--------------4");

                        Tv_empty.setText(getResources().getString(R.string.myJobs_label_completedmyJobs_empty));
                        Tv_empty.setVisibility(View.VISIBLE);

                    } else if (sTabSelectedCheck.equalsIgnoreCase("5")) {
                        System.out.println("typ--------------5");

                        Tv_empty.setText(getResources().getString(R.string.myJobs_label_cancelledmyJobs_empty));
                        Tv_empty.setVisibility(View.VISIBLE);
                    } else {

                        Tv_empty.setText(getResources().getString(R.string.myJobs_label_myJobs_empty));
                        Tv_empty.setVisibility(View.VISIBLE);
                    }
                }

                stopLoading();
            }

            @Override
            public void onErrorListener() {
                stopLoading();
            }
        });
    }


    public void callposition(String support, final String countrycode, final String mobile) {



        callphone=mobile;
        callcountrycode=countrycode;

        if (support.equalsIgnoreCase("Yes")) {
            final PkDialog mDialog = new PkDialog(getApplicationContext());
            mDialog.setDialogTitle(getResources().getString(R.string.myJobs_label_no_provider_assigned));
            mDialog.setDialogMessage(getResources().getString(R.string.myJobs_label_call_support));
            mDialog.setPositiveButton(getResources().getString(R.string.action_yes), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mobile  != null) {
                        if (Build.VERSION.SDK_INT >= 23) {
                            // Marshmallow+
                            if (!checkCallPhonePermission() || !checkReadStatePermission()) {
                                requestPermission();

                            } else {
                                Intent callIntent = new Intent(Intent.ACTION_CALL);
                                callIntent.setData(Uri.parse("tel:" + countrycode + mobile));
                                startActivity(callIntent);
                            }
                        } else {
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:" + countrycode + mobile));
                            startActivity(callIntent);

                        }
                    } else {
                        Alert(getResources().getString(R.string.rating_header_sorry_textView), getResources().getString(R.string.track_your_ride_alert_content1));
                    }

/*
                        if (data.get(mPosition).getContact_number() != null) {
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:" + data.get(mPosition).getContact_number()));
                            context.startActivity(callIntent);
                        }
                        mDialog.dismiss();*/
                }
            });
            mDialog.setNegativeButton(getResources().getString(R.string.action_no), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialog.dismiss();
                }
            });

            mDialog.show();
        } else {




            if (mobile != null) {
                int version=Build.VERSION.SDK_INT;
                if (Build.VERSION.SDK_INT >= 23) {
                    // Marshmallow+
                    if (!checkCallPhonePermission() || !checkReadStatePermission()) {
                        requestPermission();
                    } else {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + countrycode+ mobile));
                        startActivity(callIntent);
                    }
                }
                else{

                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + countrycode+ mobile));
                    startActivity(callIntent);
                }
            }





        }

    }


    private void Alert(String title, String alert) {
        final PkDialog mDialog = new PkDialog(getApplicationContext());
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




    private boolean checkCallPhonePermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private boolean checkReadStatePermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(MyJobs.this, new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE}, PERMISSION_REQUEST_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + callcountrycode+callphone));
                    startActivity(callIntent);
                }
                break;


            case PERMISSION_REQUEST_CODES:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + callcountrycode+callphone));
                    startActivity(callIntent);
                }
                break;

        }
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
