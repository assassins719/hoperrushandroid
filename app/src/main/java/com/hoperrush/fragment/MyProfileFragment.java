package com.hoperrush.fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.hoperrush.R;
import com.hoperrush.adapter.Availabilityadapter;
import com.hoperrush.app.AppointmentConfirmationPage;
import com.hoperrush.app.ChatPage;
import com.hoperrush.core.dialog.LoadingDialog;
import com.hoperrush.core.dialog.PkDialog;
import com.hoperrush.core.socket.SocketHandler;
import com.hoperrush.core.volley.ServiceRequest;
import com.hoperrush.core.widgets.CircularImageView;
import com.hoperrush.hockeyapp.FragmentHockeyApp;
import com.hoperrush.iconstant.Iconstant;
import com.hoperrush.pojo.Availabilitypojo;
import com.hoperrush.utils.ConnectionDetector;
import com.hoperrush.utils.SessionManager;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


/**
 * Created by user88 on 1/6/2016.
 */
public class MyProfileFragment extends FragmentHockeyApp implements Iconstant {

    private Boolean isInternetPresent = false;
    private boolean show_progress_status = false;
    private ConnectionDetector cd;
    private SessionManager session;
    private View moreAddressView;
    private Dialog moreAddressDialog;
    private static CircularImageView profile_img;
    private TextView Tv_profile_name, Tv_profile_email, Tv_profile_desigaination, Tv_profile_mobile_no, Tv_profile_bio, Tv_profile_address, Tv_profile_category;
    private RatingBar profile_rating;
    private RelativeLayout Rl_layout_edit_profile, Rl_layout_main, Rl_layout_profile_nointernet, Rl_layout_profile_bio, Rl_layout_profile_address, Rl_layout_category;
    private TextView rating;
    private static Context context;
    private ServiceRequest mRequest;
    private LoadingDialog mLoadingDialog1;
    private String sUserID = "",Str_Taskid="",Saddress1="",TaskerId="",min_amount,h_amount;
    private String address_lat="",address_long="";
    private String Page="",current_date="",current_time="";
    private String dialcode="";
    private String provider_id = "";
    private static String profile_pic = "";
    ListView list;
    private LoadingDialog dialog;
    Availabilityadapter availadapter;
    private String Str_provider_image = "";
    private ArrayList<Availabilitypojo> availlist;
    private SocketHandler socketHandler;
    final int PERMISSION_REQUEST_CODE = 111;
    final int PERMISSION_REQUEST_CODES = 222;
    private String sMobileNumber = "";
    private LinearLayout Ll_chat, Ll_call;
    private RelativeLayout myChatCallParentLAY;

    private RelativeLayout workexperience;
    private RelativeLayout worklocationlayout;
    private RelativeLayout radiuslayout;
    TextView providerwork_experience;
    TextView providerwork_location;
    TextView providerwork_radius;
    TextView hourcost;
    TextView minimumcost;
    String minimum_amount="",hourly_amount="";
    private SessionManager sessionManager;
    private String tasker_name="";
    private String UserID="";
    private String current_lat="",current_long="";
    private String location="",state="",city="",postalcode="";
    private String booking_date="",booking_instruction="";
    public class RefreshReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.package.load.editpage")) {
                myprofilePostRequest(getActivity(), Iconstant.PROFILEINFO_URL);

            }
        }
    }

    private RefreshReceiver finishReceiver;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.taskerprofileimage, container, false);

        init(rootview);

        return rootview;

    }

    private void init(View rootview) {
        cd = new ConnectionDetector(getActivity());
        session = new SessionManager(getActivity());
        context = getActivity();
        socketHandler = SocketHandler.getInstance(getActivity());
        availlist = new ArrayList<Availabilitypojo>();
        HashMap<String, String> user = session.getUserDetails();
        provider_id = session.getProviderID();
        //   profile_pic = user.get(SessionManager.KEY_USERIMAGE);
        HashMap<String, String> mini_amount = session.getUserDetails();
        minimum_amount = mini_amount.get(SessionManager.minimum_amount);
        HashMap<String, String> hour_amount = session.getUserDetails();
        hourly_amount = hour_amount.get(SessionManager.hourly_amount);
        HashMap<String, String> users = session.getUserDetails();
        UserID = users.get(SessionManager.KEY_USER_ID);
        list = (ListView) rootview.findViewById(R.id.list);
        hourcost=(TextView)rootview.findViewById(R.id.h_cost);
        minimumcost=(TextView)rootview.findViewById(R.id.maximum_cost) ;
        rating=(TextView)rootview.findViewById(R.id.rating);
        profile_img = (CircularImageView) rootview.findViewById(R.id.reviwes_profileimg);
        Tv_profile_name = (TextView) rootview.findViewById(R.id.username);
        // Tv_profile_desigaination = (TextView)rootview.findViewById(R.id.profile_desigination_Tv);
        Tv_profile_email = (TextView) rootview.findViewById(R.id.email);
        Tv_profile_mobile_no = (TextView) rootview.findViewById(R.id.mobile);
        // Tv_profile_bio = (TextView) rootview.findViewById(R.id.profile_bio_Tv);
        Tv_profile_address = (TextView) rootview.findViewById(R.id.address);
        Tv_profile_category = (TextView) rootview.findViewById(R.id.category);
        // profile_rating = (RatingBar) rootview.findViewById(R.id.user_ratting);
        Rl_layout_edit_profile = (RelativeLayout) rootview.findViewById(R.id.layout_edit_profile);
        Rl_layout_profile_nointernet = (RelativeLayout) rootview.findViewById(R.id.layout_profile_noInternet);
        Rl_layout_main = (RelativeLayout) rootview.findViewById(R.id.layout_profile_main);
        Ll_call = (LinearLayout) rootview.findViewById(R.id.view_profile_call_layout);
        Ll_chat = (LinearLayout) rootview.findViewById(R.id.view_profile_chat_layout);
        myChatCallParentLAY = (RelativeLayout) rootview.findViewById(R.id.view_profile_bottom_chatAndCall_layout);


        Rl_layout_profile_bio = (RelativeLayout) rootview.findViewById(R.id.profile_bio_layout);
        Rl_layout_profile_address = (RelativeLayout) rootview.findViewById(R.id.profile_address_layout);
        Rl_layout_category = (RelativeLayout) rootview.findViewById(R.id.profile_category_layout);

        providerwork_experience=(TextView)rootview.findViewById(R.id.experience_location);
        providerwork_location=(TextView)rootview.findViewById(R.id.working_location);
        providerwork_radius=(TextView)rootview.findViewById(R.id.radius);
        workexperience=(RelativeLayout)rootview.findViewById(R.id.experience_layout);
        worklocationlayout=(RelativeLayout)rootview.findViewById(R.id.location_layout);

        Intent i=getActivity().getIntent();
        sUserID=i.getExtras().getString("userid");
        address_lat=i.getExtras().getString("lat");
        address_long=i.getExtras().getString("long");
        Str_Taskid=i.getExtras().getString("task_id");
        Saddress1=i.getExtras().getString("address");
        TaskerId=i.getExtras().getString("taskerid");
        min_amount=i.getExtras().getString("minimumamount");
        h_amount=i.getExtras().getString("hourlyamount");
        Page=i.getExtras().getString("Page");
        if(Page.equalsIgnoreCase("map_page")){
            current_lat=i.getExtras().getString("lat");
            current_long=i.getExtras().getString("long");
            location=i.getExtras().getString("location");
            state=i.getExtras().getString("city");
            city=i.getExtras().getString("state");
            postalcode=i.getExtras().getString("postalcode");
        }

        minimumcost.setText(""+""+min_amount);
        hourcost.setText(""+""+h_amount);
        session.setminimum_amount(min_amount);
        session.sethourly_amount(h_amount);
        isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent) {

            Rl_layout_main.setVisibility(View.VISIBLE);
            Rl_layout_profile_nointernet.setVisibility(View.GONE);
            myprofilePostRequest(getActivity(), Iconstant.PROFILEINFO_URL);
            System.out.println("myprofileurl---------" + Iconstant.PROFILEINFO_URL);

        } else {
            Rl_layout_main.setVisibility(View.GONE);
            Rl_layout_profile_nointernet.setVisibility(View.VISIBLE);

        }
        finishReceiver = new RefreshReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.package.load.editpage");
        context.registerReceiver(finishReceiver, intentFilter);
        clickListener();
    }

    private void displayView() {
        try {
            if (session.getProviderScreenType().equals(PROVIDER)) {
                Ll_call.setVisibility(View.GONE);
                myChatCallParentLAY.setVisibility(View.GONE);
                Ll_chat.setVisibility(View.GONE);
            } else {
                Ll_call.setVisibility(View.VISIBLE);
                myChatCallParentLAY.setVisibility(View.VISIBLE);
                Ll_chat.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            context.unregisterReceiver(finishReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clickListener() {
        Ll_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChatPage.class);
                intent.putExtra("JobID-Intent", session.getChatProfileJobID());
                intent.putExtra("TaskerId", TaskerId);
                intent.putExtra("TaskId", Str_Taskid);
                startActivity(intent);
            }
        });

        Ll_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Page.equalsIgnoreCase("map_page")){

                    ConfirmBookingAlert();
                }else {

                    final PkDialog mDialog = new PkDialog(getActivity());
                    mDialog.setDialogTitle("Confirm Booking");
                    mDialog.setDialogMessage(getResources().getString(R.string.terms_and_conditions));
                    mDialog.setPositiveButton("Book", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDialog.dismiss();
                            cd = new ConnectionDetector(getActivity());
                            isInternetPresent = cd.isConnectingToInternet();

                            if (isInternetPresent) {
                                Book_job_Request(getActivity(), TaskerId);
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

                }
            }
        });

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
        final EditText instruction = (EditText) moreAddressView.findViewById(R.id.booking_page_instruction_editText);
        taskername.setText(tasker_name);
        bookingtime.setText(getCurrentTime());
        bookingdate.setText(getCurrentDate());
        current_time = getCurrentTimes();
        confirm_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (instruction.getText().toString().length() == 0) {
                    alert(getResources().getString(R.string.instruction_header), getResources().getString(R.string.instruction));
                } else {

                    if (isInternetPresent) {
                        booking_date=bookingdate.getText().toString();
                        booking_instruction=instruction.getText().toString();
                       // submitAddressRequest(getActivity(), Iconstant.Map_Insert_Address);
                        Book_job_Request(getActivity(), booking_date,booking_instruction, current_time);

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

    //----------------------------------------------------------------Book_Now_page--------------------------------------------------------


    public void Book_job_Request(Context mContext, String date, String instruction, String time) {

        mLoadingDialog1 = new LoadingDialog(mContext);
        mLoadingDialog1.setLoadingTitle(getResources().getString(R.string.action_processing));
        mLoadingDialog1.show();
        System.out.println("-----------user_id------------------" + UserID);
        System.out.println("-----------taskid------------------" + Str_Taskid);
        System.out.println("-----------taskerid------------------" + TaskerId);
        System.out.println("-----------location------------------" + Saddress1);
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", UserID);
        jsonParams.put("taskid", Str_Taskid);
        jsonParams.put("taskerid", TaskerId);
        jsonParams.put("location", "");
        jsonParams.put("instruction", instruction);
        jsonParams.put("pickup_date", date);
        jsonParams.put("pickup_time", time);
        //----------address insert---------
        jsonParams.put("locality", location);
        jsonParams.put("street", location);
        jsonParams.put("landmark", "");
        jsonParams.put("city", state);
        jsonParams.put("state", city);
        jsonParams.put("zipcode", postalcode);
        jsonParams.put("lat", current_lat);
        jsonParams.put("lng", current_long);
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
                        getActivity().finish();

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


    public void Book_job_Request(Context mContext, final String TaskerId){

        mLoadingDialog1 = new LoadingDialog(mContext);
        mLoadingDialog1.setLoadingTitle(getResources().getString(R.string.action_processing));
        mLoadingDialog1.show();


        System.out.println("-----------user_id------------------" + sUserID);
        System.out.println("-----------taskid------------------" + Str_Taskid);
        System.out.println("-----------taskerid------------------" + TaskerId);
        System.out.println("-----------location------------------" + Saddress1);


        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id",  sUserID);
        jsonParams.put("taskid",   Str_Taskid);
        jsonParams.put("taskerid", TaskerId);
        jsonParams.put("location", Saddress1);
        jsonParams.put("tasklat", address_lat);
        jsonParams.put("tasklng", address_long);

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
                            if(responseObject.has("description")){
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

                            getActivity().finish();
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

    private boolean checkCallPhonePermission() {
        int result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private boolean checkReadStatePermission() {
        int result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE}, PERMISSION_REQUEST_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + sMobileNumber));
                    startActivity(callIntent);
                }
                break;


            case PERMISSION_REQUEST_CODES:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + sMobileNumber));
                    startActivity(callIntent);
                }
                break;

        }
    }




    //--------------Alert Method-----------
    private void Alert(String title, String message) {
        final PkDialog mDialog = new PkDialog(getActivity());
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(message);
        mDialog.setPositiveButton(getResources().getString(R.string.server_ok_lable_header), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    private void myprofilePostRequest(Context mContext, String url) {
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("provider_id", provider_id);

        System.out.println("provider_id----------------" + provider_id);

        dialog = new LoadingDialog(getActivity());
        dialog.setLoadingTitle(getResources().getString(R.string.action_gettinginfo));
        dialog.show();


        ServiceRequest mservicerequest = new ServiceRequest(mContext);

        mservicerequest.makeServiceRequest(url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {

            @Override
            public void onCompleteListener(String response) {
                Log.e("profile", response);

                String Str_Status = "", Str_response = "", Str_name = "", Str_designation = "", Str_rating = "", Str_email = "", Str_mobileno = "", Str_bio = "",
                        Str_addrress = "", Str_category = "";
                String availdays = "";
                String morning = "", afternoon = "", evening = "";
                String workingexperience="",radius="",workinglocation="";

                try {
                    JSONObject jobject = new JSONObject(response);
                    Str_Status = jobject.getString("status");

                    if (Str_Status.equalsIgnoreCase("1")) {

                        JSONObject object = jobject.getJSONObject("response");
                        Str_name = object.getString("provider_name");
                        tasker_name=object.getString("provider_name");
                        Str_designation = object.getString("designation");
                        Str_rating = object.getString("avg_review");
                        Str_email = object.getString("email");
                        if(object.has("mobile_number")){
                            Str_mobileno = object.getString("mobile_number");
                            sMobileNumber = object.getString("mobile_number");
                        }
                        if(object.has("dial_code")){
                            dialcode=object.getString("dial_code");
                        }

                        Str_bio = object.getString("bio");
                        workingexperience=object.getString("experience");
                        radius=object.getString("radius");
                        workinglocation=object.getString("Working_location");
                        Str_category = object.getString("category").replace("\\n", "<br/>");
                        Str_provider_image = object.getString("image");
                        Str_addrress = object.getString("address_str");
                        JSONArray array1 = object.getJSONArray("availability_days");
                        availlist.clear();
                        for (int i = 0; i < array1.length(); i++) {

                            JSONObject ob = (JSONObject) array1.get(i);
                            Availabilitypojo pojo = new Availabilitypojo();
                            pojo.setDays(ob.getString("day"));

                            availdays = ob.getString("day");
                            JSONObject hourob = ob.getJSONObject("hour");
                            pojo.setMorning(hourob.getString("morning"));
                            pojo.setAfternoon(hourob.getString("afternoon"));
                            pojo.setEvening(hourob.getString("evening"));
                            morning = hourob.getString("morning");
                            afternoon = hourob.getString("afternoon");
                            evening = hourob.getString("evening");
                            availlist.add(pojo);
                        }

                        availadapter = new Availabilityadapter(getActivity(), availlist);
                        list.setAdapter(availadapter);


                        //JSONObject object_address= object.getJSONObject("address");
                        // Str_addrress = object_address.getString("address");

                    } else {
                        Str_response = jobject.getString("response");
                    }
                    if (Str_Status.equalsIgnoreCase("1")) {

                        System.out.println("---------------Category detail text-------------------"+Html.fromHtml(Str_category));




                        Tv_profile_name.setText(Str_name);
                        // Tv_profile_desigaination.setText(Str_designation);
                        // profile_rating.setRating(Float.parseFloat(Str_rating));
                        Tv_profile_mobile_no.setText(dialcode+ " " +Str_mobileno);
                        if(Str_category.length()>0){

                            Tv_profile_category.setText(Html.fromHtml(Str_category+"<br/>"));

                        }else{
                            Rl_layout_category.setVisibility(View.GONE);

                        }

                        Tv_profile_email.setText(Str_email);
                        Tv_profile_address.setText(Str_addrress);
                        rating.setText(Str_rating);
                        if(workingexperience.length()>0){
                            providerwork_experience.setText(workingexperience);

                        }else{
                            workexperience.setVisibility(View.GONE);

                        }

                        if(workinglocation.length()>0){
                            providerwork_location.setText(workinglocation);

                        }
                        else{

                            worklocationlayout.setVisibility(View.GONE);
                        }

                        if(radius.length()>0){
                            providerwork_radius.setText(radius);
                        }else{

                            //   radiuslayout.setVisibility(View.GONE);
                        }

                        Picasso.with(getActivity()).load(String.valueOf(Str_provider_image)).placeholder(R.drawable.nouserimg).into(profile_img);


                        System.out.println("");

                        if (Str_bio.length()>0){
                            Rl_layout_profile_bio.setVisibility(View.VISIBLE);
                            //  Tv_profile_bio.setText(Str_bio);
                        }else{
                            Rl_layout_profile_bio.setVisibility(View.GONE);
                        }

                    } else {
                        Alert(getResources().getString(R.string.server_lable_header), Str_response);
                    }

                } catch (Exception e) {
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


    @Override
    public void onResume() {
        super.onResume();

      /*  if (!socketHandler.getSocketManager().isConnected){
            socketHandler.getSocketManager().connect();
        }*/
    }

}
