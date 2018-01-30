package com.hoperrush.app;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ParseException;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.volley.Request;
import com.appyvet.rangebar.RangeBar;
import com.hoperrush.R;
import com.hoperrush.adapter.ProvidersListAdapter;
import com.hoperrush.core.dialog.LoadingDialog;
import com.hoperrush.core.dialog.PkDialog;
import com.hoperrush.core.dialog.PkLoadingDialog;
import com.hoperrush.core.socket.SocketHandler;
import com.hoperrush.core.volley.ServiceRequest;
import com.hoperrush.hockeyapp.FragmentActivityHockeyApp;
import com.hoperrush.iconstant.Iconstant;
import com.hoperrush.pojo.ProvidersListPojo;
import com.hoperrush.utils.ConnectionDetector;
import com.hoperrush.utils.CurrencySymbolConverter;
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
import java.util.Locale;

/**
 * Created by user88 on 6/24/2016.
 */
public class ProvidersList extends FragmentActivityHockeyApp implements Iconstant {

    private String UserID = "";
    private static Context context;

    private ConnectionDetector cd;
    private boolean isInternetPresent = false;
    private SessionManager sessionManager;

    private RelativeLayout Rl_back, Rl_Filterproviders;
    private ImageView Im_backIcon;
    private TextView Tv_headerTitle;

    private ListView listView;
    private RelativeLayout Rl_NoInternet, Rl_Main;

    private boolean loadingMore = false;

    private TextView Tv_empty;
    private String Str_Refresh_Name = "normal";
    private LoadingDialog mLoadingDialog1;

    private int checkPagePos = 0;
    private ArrayList<ProvidersListPojo> providersList;
    private ProvidersListAdapter adapter;
    private boolean isproviderAvailable = false;
    private String Str_bookingId = "", Str_Taskid = "",low_hourlyrate = "",high_hourlyrate = "";
    private String STaskerId = "";

    private String SUser_Id = "", Saddress1 = "", StrcatergoryId = "", Spickup_date = "", Spickuptime = "", Sinstruction = "", StrService_id = "", Str_lattitude = "", SAddress = "", Str_longitude = "";

    private Dialog sort_dialog;
    private ImageView Img_filter;

    private String datevalue = "1";

    private ServiceRequest mRequest;
    private String sUserID = "";
    private PkLoadingDialog mLoadingDialog;

    //Declaration for TimeDialog
    ArrayList<String> timeArray = new ArrayList<String>();
    ArrayList<String> newTimeArray;
    private Dialog timeDialog;
    private View timeView;
    private String sSplitTime = "";

    private RelativeLayout sorting_cancel, sorting_apply, sortingDate, sortingname, Ascending_orderby, Descending_Orderby;
    private ImageView sorting_checkename, sorting_checkeddate, sorting_checkedthree, sorting_ascendingimg, sorting_descinngimg;
    private String Sselected_sorting = "";
    private String Sselected_ordrby = "";
    private TextView Tvfrom_date, Tvto_date, Tv_time;
    private EditText distance_edit;

    private RatingBar ratingbar;

    private RelativeLayout Rl_layout_fromdate, Rl_layout_to_date, Rl_layout_time, Rl_layout_cancel;
    private CaldroidFragment dialogCaldroidFragment;
    final SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");

    private SocketHandler socketHandler;

    RangeBar rangeSeekbar;
    private TextView tv_minprice, tvmaxprice,tv_currency;
    private String minimum_amount = "", hourly_amount = "";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.providers_list);
        context = ProvidersList.this;
        initializeHeaderBar();
        initialize();

        Rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction("com.package.ACTION_CLASS_APPOINTMENT_REFRESH");
                sendBroadcast(broadcastIntent);
                onBackPressed();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });


        Rl_Filterproviders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             /*   Intent intent = new Intent(ProvidersList.this,ProvidersFilter.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);*/

                chooseSortingImage(savedInstanceState);

            }
        });

    }

    //------Alert Method-----
    private void alert(String title, String message) {
        final PkDialog mDialog = new PkDialog(ProvidersList.this);
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


    private void initializeHeaderBar() {
        RelativeLayout headerBar = (RelativeLayout) findViewById(R.id.headerBar_layout);
        Rl_back = (RelativeLayout) headerBar.findViewById(R.id.headerBar_left_layout);
        Im_backIcon = (ImageView) headerBar.findViewById(R.id.headerBar_imageView);
        Tv_headerTitle = (TextView) headerBar.findViewById(R.id.headerBar_title_textView);

        Tv_headerTitle.setText(getResources().getString(R.string.providerslist_head));
        Im_backIcon.setImageResource(R.drawable.back_arrow);

        Rl_Filterproviders = (RelativeLayout) findViewById(R.id.relativeLayout_providers_filter);

    }

    private void initialize() {
        mRequest = new ServiceRequest(ProvidersList.this);
        providersList = new ArrayList<ProvidersListPojo>();
        socketHandler = SocketHandler.getInstance(context);
        cd = new ConnectionDetector(ProvidersList.this);
        isInternetPresent = cd.isConnectingToInternet();
        sessionManager = new SessionManager(ProvidersList.this);


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


        System.out.println("SAddress---------------------------" + SAddress);

        System.out.println("provider_SUser_Id----------" + SUser_Id);
        System.out.println("provider_Saddress1----------" + Saddress1);
        System.out.println("provider_StrcatergoryId----------" + StrcatergoryId);
        System.out.println("provider_Spickup_date----------" + Spickup_date);
        System.out.println("provider_Spickuptime----------" + Spickuptime);
        System.out.println("provider_Sinstruction----------" + Sinstruction);
        System.out.println("provider_StrService_id----------" + StrService_id);
        System.out.println("provider_Str_lattitude----------" + Str_lattitude);
        System.out.println("provider_Str_longitude----------" + Str_longitude);


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
        timeArray.add("8 PM - 9 PM");


        listView = (ListView) findViewById(R.id.providerslist_listView);
        Rl_Main = (RelativeLayout) findViewById(R.id.myJobs_main_layout);
        Tv_empty = (TextView) findViewById(R.id.providerslist_listView_empty_textView);
        Rl_NoInternet = (RelativeLayout) findViewById(R.id.providerslist_noInternet_layout);

        // get user data from session
        HashMap<String, String> user = sessionManager.getUserDetails();
        sUserID = user.get(SessionManager.KEY_USER_ID);

        System.out.println("loinuserid--------" + sUserID);

        if (isInternetPresent) {
            Rl_Main.setVisibility(View.VISIBLE);
            Rl_NoInternet.setVisibility(View.GONE);
            postProvidersRequest(Iconstant.book_job_url);
        } else {
            Rl_Main.setVisibility(View.GONE);
            Rl_NoInternet.setVisibility(View.VISIBLE);
        }

        listviewClickListener();
    }

    private void listviewClickListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cd = new ConnectionDetector(ProvidersList.this);
                isInternetPresent = cd.isConnectingToInternet();
                if (isInternetPresent) {
                    if (providersList.size() > 0) {
                        Intent viewProfileIntent = new Intent(ProvidersList.this, PartnerProfilePage.class);
                        sessionManager.putProvideID(providersList.get(position).getTaskerId());
                        sessionManager.putProvideScreenType(PROVIDER);
                        viewProfileIntent.putExtra("userid", sUserID);
                        viewProfileIntent.putExtra("task_id", Str_Taskid);
                        viewProfileIntent.putExtra("address", Saddress1);
                        viewProfileIntent.putExtra("lat", Str_lattitude);
                        viewProfileIntent.putExtra("long", Str_longitude);
                        viewProfileIntent.putExtra("minimumamount", providersList.get(position).getProvider_mincost());
                        viewProfileIntent.putExtra("hourlyamount", providersList.get(position).getHourly_rate());
                        viewProfileIntent.putExtra("taskerid", providersList.get(position).getTaskerId());
                        viewProfileIntent.putExtra("Page", "list_page");
                        startActivity(viewProfileIntent);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                    }
                } else {
                    alert(getResources().getString(R.string.action_no_internet_title), getResources().getString(R.string.action_no_internet_message));
                }
            }
        });
    }


    private void startLoading() {
        if (Str_Refresh_Name.equalsIgnoreCase("normal")) {
            mLoadingDialog = new PkLoadingDialog(ProvidersList.this);
            mLoadingDialog.show();
        } else {
            // swipeToRefresh.setRefreshing(true);
        }
    }

    private void stopLoading() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (Str_Refresh_Name.equalsIgnoreCase("normal")) {
                    if (mLoadingDialog != null) {
                        mLoadingDialog.dismiss();
                    }
                } else {
                    // swipeToRefresh.setRefreshing(false);
                }
            }
        }, 500);
    }


    public void postProvidersRequest1(String url) {

        startLoading();
        HashMap<String, String> jsonParams = new HashMap<String, String>();

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
                            providersList.clear();
                        }

                    } else {

                        Str_response = object.getString("response");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    stopLoading();
                }
                if (Str_status.equalsIgnoreCase("1") && isproviderAvailable) {
                    adapter = new ProvidersListAdapter(ProvidersList.this, providersList);
                    listView.setAdapter(adapter);
                    Tv_empty.setVisibility(View.GONE);
                } else if (Str_status.equalsIgnoreCase("1") && !isproviderAvailable) {
                    adapter = new ProvidersListAdapter(ProvidersList.this, providersList);
                    listView.setAdapter(adapter);
                    Tv_empty.setVisibility(View.VISIBLE);
                } else if (Str_status.equalsIgnoreCase("0")) {
                    adapter = new ProvidersListAdapter(ProvidersList.this, providersList);
                    listView.setAdapter(adapter);
                    Tv_empty.setVisibility(View.VISIBLE);
                }

                if (providersList != null && providersList.size() == 0) {
                    Tv_empty.setVisibility(View.GONE);
                } else {
                    Tv_empty.setVisibility(View.VISIBLE);
                }

                stopLoading();

            }

            @Override
            public void onErrorListener() {
                stopLoading();
            }
        });

    }


    private void chooseSortingImage(final Bundle savedInstanceState) {
        sort_dialog = new Dialog(ProvidersList.this);
        sort_dialog.getWindow();
        sort_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        sort_dialog.setContentView(R.layout.filter_providers);
        sort_dialog.setCanceledOnTouchOutside(true);
        sort_dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_photo_Picker;
        sort_dialog.show();
        sort_dialog.getWindow().setGravity(Gravity.CENTER);

        sorting_cancel = (RelativeLayout) sort_dialog.findViewById(R.id.cancel_sorting_clearlayout);
        sorting_apply = (RelativeLayout) sort_dialog.findViewById(R.id.provider_filter_apply);
        sorting_checkeddate = (ImageView) sort_dialog.findViewById(R.id.sorting_checkedate);
        Tvfrom_date = (TextView) sort_dialog.findViewById(R.id.from_date_select_textView);
        Tvto_date = (TextView) sort_dialog.findViewById(R.id.todate_select_textView);
        Rl_layout_fromdate = (RelativeLayout) sort_dialog.findViewById(R.id.from_page_date_select_layout);
        Rl_layout_to_date = (RelativeLayout) sort_dialog.findViewById(R.id.todate_select_layout);
        Rl_layout_time = (RelativeLayout) sort_dialog.findViewById(R.id.time_select_layout);
        Tv_time = (TextView) sort_dialog.findViewById(R.id.time_textView);
        ratingbar = (RatingBar) sort_dialog.findViewById(R.id.ratingBar);
        distance_edit=(EditText)sort_dialog.findViewById(R.id.filter_providers_ET_distance);
        rangeSeekbar = (RangeBar) sort_dialog.findViewById(R.id.seekBar);
        tv_minprice = (TextView) sort_dialog.findViewById(R.id.pricemintv);
        tvmaxprice = (TextView) sort_dialog.findViewById(R.id.pricemaxtv);
        tv_currency = (TextView) sort_dialog.findViewById(R.id.currency);


        HashMap<String, String> amount = sessionManager.getWalletDetails();
        String currencyCode = amount.get(SessionManager.KEY_CURRENCY_CODE);
        String currencySymbol = CurrencySymbolConverter.getCurrencySymbol(currencyCode);

        tv_currency.setText("Price in " + currencySymbol);

        if (!low_hourlyrate.equalsIgnoreCase("")) {
            rangeSeekbar.setTickStart(Float.parseFloat(low_hourlyrate));
            rangeSeekbar.setTickEnd(Float.parseFloat(high_hourlyrate));
        }

        // set listener
        rangeSeekbar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {

            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex,
                                              int rightPinIndex,
                                              String leftPinValue, String rightPinValue) {
                System.out.println("------------minValue----------" + leftPinValue);
                System.out.println("------------maxValue----------" + rightPinValue);
                tv_minprice.setText(String.valueOf(leftPinValue));
                tvmaxprice.setText(String.valueOf(rightPinValue));
            }
        });


        Rl_layout_fromdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                datevalue = "1";
                datePicker(savedInstanceState);

            }
        });


        Rl_layout_to_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datevalue = "2";
                datePicker(savedInstanceState);
            }
        });


        Rl_layout_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Tv_time.getText().toString().equalsIgnoreCase(getResources().getString(R.string.appointment_label_select_date))) {
                    alert("", getResources().getString(R.string.appointment_label_select_date_alert));
                } else {
                    String sTodayDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                    if (Tv_time.getText().toString().equalsIgnoreCase(sTodayDate)) {
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


        sorting_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sort_dialog.dismiss();
/*
                finish();
                onBackPressed();*/
            }
        });


        sorting_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               if(isInternetPresent){
                   postProvidersfilterRequest(Iconstant.book_job_url);
               }


                System.out.println("filterprovider---------------" + Iconstant.book_job_url);


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

            if (datevalue.equalsIgnoreCase("1")) {
                Tvfrom_date.setText(formatter.format(date));
            } else {
                Tvto_date.setText(formatter.format(date));
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


    //--------------Time Select Method-----------
    private void timePickerDialog() {

        newTimeArray = new ArrayList<String>();

        /*Function to hide the expired time from the arrayList*/
        String sTodayDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        if (Tv_time.getText().toString().equalsIgnoreCase(sTodayDate)) {
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
        DisplayMetrics metrics = ProvidersList.this.getResources().getDisplayMetrics();
        int screenWidth = (int) (metrics.widthPixels * 0.80);//fill only 80% of the screen

        timeView = View.inflate(ProvidersList.this, R.layout.appointment_time_pick_dialog, null);
        timeDialog = new Dialog(ProvidersList.this);
        timeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        timeDialog.setContentView(timeView);
        timeDialog.setCanceledOnTouchOutside(true);
        timeDialog.getWindow().setLayout(screenWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        timeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ListView timeListView = (ListView) timeView.findViewById(R.id.appointment_time_pick_listView);

        ArrayAdapter<String> timeAdapter = new ArrayAdapter<String>
                (ProvidersList.this, R.layout.appointment_time_picker_dialog_single, R.id.appointment_time_pick_single_textView, newTimeArray);
        timeListView.setAdapter(timeAdapter);

        timeDialog.show();

        timeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Tv_time.setText(newTimeArray.get(position).toString());

                String[] splitTime = newTimeArray.get(position).toString().split("-");
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

                timeDialog.dismiss();
            }
        });

    }


    public void postProvidersRequest_LoadMore(String url, int sPage) {

        startLoading();
        HashMap<String, String> jsonParams = new HashMap<String, String>();

        jsonParams.put("page", String.valueOf(sPage));
        jsonParams.put("perPage", "20");

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

                                providersList.add(pojo);
                            }
                            isproviderAvailable = true;

                        } else {
                            isproviderAvailable = false;
                            providersList.clear();
                        }

                    } else {

                        Str_response = object.getString("response");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    stopLoading();
                }
                if (Str_status.equalsIgnoreCase("1") && isproviderAvailable) {
                    adapter = new ProvidersListAdapter(ProvidersList.this, providersList);
                    listView.setAdapter(adapter);
                    Tv_empty.setVisibility(View.GONE);
                } else if (Str_status.equalsIgnoreCase("1") && !isproviderAvailable) {
                    adapter = new ProvidersListAdapter(ProvidersList.this, providersList);
                    listView.setAdapter(adapter);
                    Tv_empty.setVisibility(View.VISIBLE);
                } else if (Str_status.equalsIgnoreCase("0")) {
                    adapter = new ProvidersListAdapter(ProvidersList.this, providersList);
                    listView.setAdapter(adapter);
                    Tv_empty.setVisibility(View.VISIBLE);
                }
                if (providersList != null && providersList.size() == 0) {
                    Tv_empty.setVisibility(View.GONE);
                } else {
                    Tv_empty.setVisibility(View.VISIBLE);
                }
                stopLoading();

            }

            @Override
            public void onErrorListener() {
                stopLoading();
            }
        });

    }


    public void postProvidersRequest(String url) {

        mLoadingDialog1 = new LoadingDialog(ProvidersList.this);
        mLoadingDialog1.setLoadingTitle(getResources().getString(R.string.action_processing));
        mLoadingDialog1.show();

        System.out.println("-----------user_id------------------" + sUserID);
        System.out.println("------------address_name-----------------" + Saddress1);
        System.out.println("-------------category----------------" + StrcatergoryId);
        System.out.println("-------------pickup_date----------------" + Spickup_date);
        System.out.println("--------------pickup_time---------------" + Spickuptime);
        System.out.println("--------------instruction---------------" + Sinstruction);
        System.out.println("--------------service---------------" + StrService_id);
        System.out.println("--------------lat---------------" + Str_lattitude);
        System.out.println("--------------long---------------" + Str_longitude);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", sUserID);
        jsonParams.put("address_name", Saddress1);
        jsonParams.put("category", StrcatergoryId);
        jsonParams.put("pickup_date", Spickup_date);
        jsonParams.put("pickup_time", Spickuptime);
        // jsonParams.put("code", Et_couponCode.getText().toString());
        jsonParams.put("instruction", Sinstruction);
        // jsonParams.put("try", "");
        // jsonParams.put("job_id", "");
        jsonParams.put("service", StrService_id);
        jsonParams.put("lat", Str_lattitude);
        jsonParams.put("long", Str_longitude);


        mRequest.makeServiceRequest(url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("respionse--------------" + response);
                String Str_status = "", Str_response = "", Scurrency = "";
                try {

                    JSONObject object = new JSONObject(response);
                    Str_status = object.getString("status");

                    if (Str_status.equalsIgnoreCase("1")) {

                        Str_bookingId = object.getString("booking_id");
                        Str_Taskid = object.getString("task_id");
                        low_hourlyrate = object.getString("lowesthourlyrate");
                        high_hourlyrate = object.getString("highesthourlyrate");

                        JSONArray jarry = object.getJSONArray("response");
                        if (jarry.length() > 0) {
                            for (int i = 0; i < jarry.length(); i++) {
                                JSONObject jobject = jarry.getJSONObject(i);
                                ProvidersListPojo pojo = new ProvidersListPojo();
                                pojo.setProvider_name(jobject.getString("name"));
                                pojo.setProvider_company(jobject.getString("company"));
                                pojo.setProvider_rating(jobject.getString("rating"));
                                pojo.setProvider_image(jobject.getString("image_url"));
                                pojo.setTaskerId(jobject.getString("taskerid"));
                                pojo.setReviews(jobject.getString("reviews"));
                                pojo.setRadius(jobject.getString("distance_km"));
                                pojo.setProvider_availble(jobject.getString("availability"));
                                pojo.setProvider_mincost("$" + jobject.getString("min_amount"));
                                minimum_amount = jobject.getString("min_amount");
                                hourly_amount = jobject.getString("hourly_amount");

                                pojo.setHourly_rate("$" + jobject.getString("hourly_amount"));
                                pojo.setTaskId(Str_Taskid);
                                providersList.add(pojo);
                            }

                            sessionManager.settaskid(Str_Taskid);
                            isproviderAvailable = true;
                        } else {
                            isproviderAvailable = false;
                            providersList.clear();
                        }
                    } else {
                        Str_response = object.getString("response");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    stopLoading();
                }

               if (Str_status.equalsIgnoreCase("1") && isproviderAvailable) {
                    adapter = new ProvidersListAdapter(ProvidersList.this, providersList);
                    listView.setAdapter(adapter);
                    Tv_empty.setVisibility(View.GONE);
                } else if (Str_status.equalsIgnoreCase("1") && !isproviderAvailable) {
                    adapter = new ProvidersListAdapter(ProvidersList.this, providersList);
                    listView.setAdapter(adapter);
                    Tv_empty.setVisibility(View.VISIBLE);
                    Rl_Filterproviders.setVisibility(View.GONE);


                } else if (Str_status.equalsIgnoreCase("0")) {
                    adapter = new ProvidersListAdapter(ProvidersList.this, providersList);
                    listView.setAdapter(adapter);
                    Tv_empty.setVisibility(View.VISIBLE);
                }

                mLoadingDialog1.dismiss();

            }

            @Override
            public void onErrorListener() {
                mLoadingDialog1.dismiss();
            }
        });

    }


    //-----------------------Job Booking Post Request-----------------
    public void bookJob_Request(Context mContext, final String TaskerId) {


        final PkDialog mDialog = new PkDialog(ProvidersList.this);
        mDialog.setDialogTitle("Confirm Booking");
        mDialog.setDialogMessage(getResources().getString(R.string.terms_and_conditions));
        mDialog.setPositiveButton("Confirm", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                cd = new ConnectionDetector(ProvidersList.this);
                isInternetPresent = cd.isConnectingToInternet();

                if (isInternetPresent) {
                    Book_job_Request(ProvidersList.this, TaskerId);
                } else {
                    alert(getResources().getString(R.string.action_no_internet_title), getResources().getString(R.string.action_no_internet_message));
                }
            }
        });
        mDialog.setNegativeButton("Cancel", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();

    }


    public void Book_job_Request(Context mContext, final String TaskerId) {

        mLoadingDialog1 = new LoadingDialog(mContext);
        mLoadingDialog1.setLoadingTitle(getResources().getString(R.string.action_processing));
        mLoadingDialog1.show();


        System.out.println("-----------user_id------------------" + sUserID);
        System.out.println("-----------taskid------------------" + Str_Taskid);
        System.out.println("-----------taskerid------------------" + TaskerId);
        System.out.println("-----------location------------------" + Saddress1);


        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", sUserID);
        jsonParams.put("taskid", Str_Taskid);
        jsonParams.put("taskerid", TaskerId);
        jsonParams.put("location", Saddress1);
        jsonParams.put("tasklat", Str_lattitude);
        jsonParams.put("tasklng", Str_longitude);

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

                            Intent intent = new Intent(ProvidersList.this, AppointmentConfirmationPage.class);
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

                        sessionManager = new SessionManager(ProvidersList.this);

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


    //-----------codeing for providers filter--------------------
    public void postProvidersfilterRequest(String url) {

        mLoadingDialog1 = new LoadingDialog(ProvidersList.this);
        mLoadingDialog1.setLoadingTitle(getResources().getString(R.string.action_processing));
        mLoadingDialog1.show();

        System.out.println("-----------user_id------------------" + sUserID);
        System.out.println("------------address_name-----------------" + Saddress1);
        System.out.println("-------------category----------------" + StrcatergoryId);
        System.out.println("-------------pickup_date----------------" + Spickup_date);
        System.out.println("--------------pickup_time---------------" + Spickuptime);
        System.out.println("--------------instruction---------------" + Sinstruction);
        System.out.println("--------------service---------------" + StrService_id);
        System.out.println("--------------lat---------------" + Str_lattitude);
        System.out.println("--------------long---------------" + Str_longitude);
        System.out.println("--------------price---------------" + tvmaxprice.getText().toString());
        System.out.println("--------------rating---------------" + String.valueOf(ratingbar.getRating()));


        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", sUserID);
        jsonParams.put("address_name", Saddress1);
        jsonParams.put("category", StrcatergoryId);
        jsonParams.put("pickup_date", Spickup_date);
        jsonParams.put("pickup_time", Spickuptime);
        // jsonParams.put("code", Et_couponCode.getText().toString());
        jsonParams.put("instruction", Sinstruction);
        // jsonParams.put("try", "");
        // jsonParams.put("job_id", "");
        jsonParams.put("service", StrService_id);
        jsonParams.put("lat", Str_lattitude);
        jsonParams.put("long", Str_longitude);
        jsonParams.put("minrate", tv_minprice.getText().toString());
        jsonParams.put("maxrate", tvmaxprice.getText().toString());
        jsonParams.put("distancefilter",distance_edit.getText().toString());
        jsonParams.put("rating", String.valueOf(ratingbar.getRating()));
        String min = tv_minprice.getText().toString();
        String max = tvmaxprice.getText().toString();

        System.out.println("Price list MINI : " + min + "Max : " + max);

        //jsonParams.put("lat", "13.0826802");
        //jsonParams.put("long", "80.27071840000008");


        mRequest.makeServiceRequest(url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("respionse--------------" + response);
                String Str_status = "", Str_response = "", Scurrency = "";
                try {

                    JSONObject object = new JSONObject(response);
                    Str_status = object.getString("status");

                    if (Str_status.equalsIgnoreCase("1")) {

                        Str_bookingId = object.getString("booking_id");
                        Str_Taskid = object.getString("task_id");

                        JSONArray jarry = object.getJSONArray("response");
                        if (jarry.length() > 0) {

                            providersList.clear();

                            for (int i = 0; i < jarry.length(); i++) {
                                JSONObject jobject = jarry.getJSONObject(i);
                                ProvidersListPojo pojo = new ProvidersListPojo();
                                pojo.setProvider_name(jobject.getString("name"));
                                pojo.setProvider_company(jobject.getString("company"));
                                pojo.setProvider_rating(jobject.getString("rating"));
                                pojo.setProvider_image(jobject.getString("image_url"));
                                pojo.setTaskerId(jobject.getString("taskerid"));
                                pojo.setProvider_availble(jobject.getString("availability"));
                                pojo.setProvider_mincost("$" + jobject.getString("min_amount"));
                                pojo.setReviews(jobject.getString("reviews"));
                                pojo.setRadius(jobject.getString("distance_km"));
                                pojo.setHourly_rate("$" + jobject.getString("hourly_amount"));
                                pojo.setTaskId(Str_Taskid);
                                providersList.add(pojo);
                            }
                            isproviderAvailable = true;

                        } else {
                            isproviderAvailable = false;
                            providersList.clear();
                        }
                    } else {

                        Str_response = object.getString("response");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    stopLoading();
                }
                if (Str_status.equalsIgnoreCase("1") && isproviderAvailable) {
                    adapter = new ProvidersListAdapter(ProvidersList.this, providersList);
                    listView.setAdapter(adapter);
                    Tv_empty.setVisibility(View.GONE);
                } else if (Str_status.equalsIgnoreCase("1") && !isproviderAvailable) {
                    adapter = new ProvidersListAdapter(ProvidersList.this, providersList);
                    listView.setAdapter(adapter);
                    Tv_empty.setVisibility(View.VISIBLE);
                    Rl_Filterproviders.setVisibility(View.GONE);

                } else if (Str_status.equalsIgnoreCase("0")) {
                    providersList.clear();
                    adapter = new ProvidersListAdapter(ProvidersList.this, providersList);
                    listView.setAdapter(adapter);
                    Tv_empty.setVisibility(View.VISIBLE);
                }
                mLoadingDialog1.dismiss();
                sort_dialog.dismiss();
            }

            @Override
            public void onErrorListener() {
                mLoadingDialog1.dismiss();
                sort_dialog.dismiss();
            }
        });

    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction("com.package.ACTION_CLASS_APPOINTMENT_REFRESH");
                sendBroadcast(broadcastIntent);
                finish();
                return true;
        }
        return false;
    }



}






