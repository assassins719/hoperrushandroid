package com.hoperrush.app;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ParseException;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.hoperrush.R;
import com.hoperrush.hockeyapp.FragmentActivityHockeyApp;
import com.hoperrush.core.dialog.PkDialog;
import com.hoperrush.core.dialog.PkLoadingDialog;
import com.hoperrush.core.volley.ServiceRequest;
import com.hoperrush.utils.ConnectionDetector;
import com.hoperrush.utils.SessionManager;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by user88 on 8/2/2016.
 */
public class ProvidersFilter extends FragmentActivityHockeyApp {

    private String UserID = "";
    private static Context context;

    private ConnectionDetector cd;
    private boolean isInternetPresent = false;
    private SessionManager sessionManager;

    private RelativeLayout Rl_back,Rl_Filterproviders;
    private ImageView Im_backIcon;
    private TextView Tv_headerTitle;


    private String datevalue ="1";

    private ServiceRequest mRequest;
    private String sUserID ="";
    private PkLoadingDialog mLoadingDialog;

    //Declaration for TimeDialog
    ArrayList<String> timeArray = new ArrayList<String>();
    ArrayList<String> newTimeArray;
    private Dialog timeDialog;
    private View timeView;
    private String sSplitTime = "";


    private RelativeLayout sorting_cancel,sorting_apply,sortingDate,sortingname,Ascending_orderby,Descending_Orderby;
    private ImageView sorting_checkename,sorting_checkeddate,sorting_checkedthree,sorting_ascendingimg,sorting_descinngimg;
    private String Sselected_sorting="";
    private String Sselected_ordrby="";
    private TextView Tvfrom_date,Tvto_date,Tv_time,Tv_currency;

    private RelativeLayout Rl_layout_fromdate,Rl_layout_to_date,Rl_layout_time,Rl_layout_cancel;

    private CaldroidFragment dialogCaldroidFragment;
    final SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_providers);
        context = ProvidersFilter.this;
        //initializeHeaderBar();
        initialize();


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

                finish();
                onBackPressed();
            }
        });



        sorting_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



            }
        });




    }



    private void initialize() {
        mRequest = new ServiceRequest(ProvidersFilter.this);

        cd = new ConnectionDetector(ProvidersFilter.this);
        isInternetPresent = cd.isConnectingToInternet();
        sessionManager = new SessionManager(ProvidersFilter.this);

        // get user data from session
        HashMap<String, String> user = sessionManager.getUserDetails();
        sUserID = user.get(SessionManager.KEY_USER_ID);

        System.out.println("loinuserid--------"+sUserID);

        sorting_cancel =  (RelativeLayout)findViewById(R.id.cancel_sorting_clearlayout);
        sorting_apply =  (RelativeLayout)findViewById(R.id.provider_filter_apply);
        sorting_checkeddate=(ImageView)findViewById(R.id.sorting_checkedate);
        Tvfrom_date = (TextView)findViewById(R.id.from_date_select_textView);
        Tvto_date = (TextView)findViewById(R.id. todate_select_textView);
        Rl_layout_fromdate = (RelativeLayout)findViewById(R.id.from_page_date_select_layout);
        Rl_layout_to_date = (RelativeLayout)findViewById(R.id.todate_select_layout);
        Rl_layout_time = (RelativeLayout)findViewById(R.id.time_select_layout);
        Tv_time = (TextView)findViewById(R.id.time_textView);
        Tv_currency = (TextView)findViewById(R.id.currency);

   //     Tv_currency.setText("Price in " + sessionManager.getWalletDetails().get(KEY_CURRENCY_CODE));



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


        sorting_apply.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

               /* ConnectionDetector cd = new ConnectionDetector(ProvidersFilter.this);
                boolean isInternetPresent = cd.isConnectingToInternet();
                if (isInternetPresent){

                    postJobRequestSorting(Iconstant.MyJobsList_Url);

                    System.out.println("---------sortingurl------------" +Iconstant.MyJobsList_Url);

                }else{
                    alert(getResources().getString(R.string.action_no_internet_title), getResources().getString(R.string.action_no_internet_message));

                }*/

            }
        });
    }





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
        DisplayMetrics metrics = ProvidersFilter.this.getResources().getDisplayMetrics();
        int screenWidth = (int) (metrics.widthPixels * 0.80);//fill only 80% of the screen

        timeView = View.inflate(ProvidersFilter.this, R.layout.appointment_time_pick_dialog, null);
        timeDialog = new Dialog(ProvidersFilter.this);
        timeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        timeDialog.setContentView(timeView);
        timeDialog.setCanceledOnTouchOutside(true);
        timeDialog.getWindow().setLayout(screenWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        timeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ListView timeListView = (ListView) timeView.findViewById(R.id.appointment_time_pick_listView);

        ArrayAdapter<String> timeAdapter = new ArrayAdapter<String>
                (ProvidersFilter.this, R.layout.appointment_time_picker_dialog_single, R.id.appointment_time_pick_single_textView, newTimeArray);
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
                    sSplitTime="";
                    sSplitTime=displayFormat.format(date)+":00";

                } catch (java.text.ParseException e) {
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


    //------Alert Method-----
    private void alert(String title, String message) {
        final PkDialog mDialog = new PkDialog(ProvidersFilter.this);
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
            mLoadingDialog = new PkLoadingDialog(ProvidersFilter.this);
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

    // Setup CaldroidListener
    final CaldroidListener caldroidListener = new CaldroidListener() {
        @Override
        public void onSelectDate(Date date, View view) {
            dialogCaldroidFragment.dismiss();
           // Tvto_date.setText(getResources().getString(R.string.appointment_label_select_time));

            if (datevalue.equalsIgnoreCase("1")){
                Tvfrom_date.setText(formatter.format(date));
            }else{
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




    //-------------My Jobs Post Request---------------
    private void postJobRequestSorting(String url) {

        startLoading();

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", sUserID);
        jsonParams.put("page", "0");
        jsonParams.put("perPage", "20");
        jsonParams.put("orderby",Sselected_ordrby);
        jsonParams.put("sortby", Sselected_sorting);

        System.out.println("---------orderby------------" + Sselected_ordrby);
        System.out.println("---------sortby------------" + Sselected_sorting);

        System.out.println("---------My Jobs user_id------------" + sUserID);
        System.out.println("---------My Jobs Page page------------" + "1");
        System.out.println("---------My Jobs url------------" + url);

        mRequest.makeServiceRequest(url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("---------My Jobssortingname response------------" + response);

               /* String Str_status = "";
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

                                        System.out.println("contact_number-------------"+jobs_Object.getString("contact_number"));

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
                    Tv_empty.setVisibility(View.VISIBLE);
                } else if (Str_status.equalsIgnoreCase("0")) {
                    adapter = new MyJobsListAdapter(MyJobs.this, jobsList);
                    listView.setAdapter(adapter);
                    Tv_empty.setVisibility(View.VISIBLE);
                }*/

                stopLoading();
            }

            @Override
            public void onErrorListener() {
                stopLoading();
            }
        });
    }






}