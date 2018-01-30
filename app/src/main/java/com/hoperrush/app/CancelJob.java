package com.hoperrush.app;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;
import com.hoperrush.R;
import com.hoperrush.adapter.CancelJobAdapter;
import com.hoperrush.core.dialog.LoadingDialog;
import com.hoperrush.core.dialog.PkDialog;
import com.hoperrush.core.volley.ServiceRequest;
import com.hoperrush.iconstant.Iconstant;
import com.hoperrush.pojo.CancelJobPojo;
import com.hoperrush.utils.ConnectionDetector;
import com.hoperrush.utils.SessionManager;
import com.hoperrush.utils.SubClassActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Casperon Technology on 1/19/2016.
 */
public class CancelJob extends SubClassActivity {
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private SessionManager sessionManager;
    private String UserID = "";

    private RelativeLayout Rl_back;
    private ImageView Im_backIcon;
    private TextView Tv_headerTitle;

    private ServiceRequest mRequest;
    Dialog dialog;
    ArrayList<CancelJobPojo> itemList;
    CancelJobAdapter adapter;
    private ExpandableHeightListView listView;
    private String sJobId_intent = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cancel_job);
        initialize();
        initializeHeaderBar();

        Rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                cd = new ConnectionDetector(CancelJob.this);
                isInternetPresent = cd.isConnectingToInternet();

                if (isInternetPresent) {

                    final PkDialog mDialog = new PkDialog(CancelJob.this);
                    mDialog.setDialogTitle(getResources().getString(R.string.myJobs_cancel_job_alert_title));
                    mDialog.setDialogMessage(getResources().getString(R.string.myJobs_cancel_job_alert));
                    mDialog.setPositiveButton(getResources().getString(R.string.myJobs_cancel_job_alert_yes), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDialog.dismiss();
                            cancel_MyRide(Iconstant.MyJobs_Cancel_Url, itemList.get(position).getReason());
                        }
                    });
                    mDialog.setNegativeButton(getResources().getString(R.string.myJobs_cancel_job_alert_no), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDialog.dismiss();
                        }
                    });
                    mDialog.show();


                } else {
                    alert(getResources().getString(R.string.action_no_internet_title), getResources().getString(R.string.action_no_internet_message));
                }
            }
        });

    }

    private void initialize() {
        sessionManager = new SessionManager(CancelJob.this);
        cd = new ConnectionDetector(CancelJob.this);
        isInternetPresent = cd.isConnectingToInternet();

        listView = (ExpandableHeightListView) findViewById(R.id.cancel_job_listView);

        // get user data from session
        HashMap<String, String> user = sessionManager.getUserDetails();
        UserID = user.get(SessionManager.KEY_USER_ID);

        Intent intent = getIntent();
        sJobId_intent = intent.getStringExtra("JOB_ID");
        try {
            Bundle bundleObject = getIntent().getExtras();
            itemList = (ArrayList<CancelJobPojo>) bundleObject.getSerializable("Reason");
            adapter = new CancelJobAdapter(CancelJob.this, itemList);
            listView.setAdapter(adapter);
            listView.setExpanded(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeHeaderBar() {
        RelativeLayout headerBar = (RelativeLayout) findViewById(R.id.headerBar_layout);
        Rl_back = (RelativeLayout) headerBar.findViewById(R.id.headerBar_left_layout);
        Im_backIcon = (ImageView) headerBar.findViewById(R.id.headerBar_imageView);
        Tv_headerTitle = (TextView) headerBar.findViewById(R.id.headerBar_title_textView);

        Tv_headerTitle.setText(getResources().getString(R.string.cancel_job_header_textView));
        Im_backIcon.setImageResource(R.drawable.back_arrow);
    }

    //--------------Alert Method-----------
    private void alert(String title, String alert) {

        final PkDialog mDialog = new PkDialog(CancelJob.this);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(alert);
        mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                finish();
            }
        });
        mDialog.show();
    }

    //-----------------------Cancel MyRide Post Request-----------------
    private void cancel_MyRide(String Url, final String reason) {

        final LoadingDialog mLoadingDialog = new LoadingDialog(CancelJob.this);
        mLoadingDialog.setLoadingTitle(getResources().getString(R.string.cancel_job_action_cancel));
        mLoadingDialog.show();
        System.out.println("-------------Cancel Job Url----------------" + Url);
        System.out.println("-------------Cancel user_id----------------" + UserID);
        System.out.println("-------------Cancel job_id---------------" + sJobId_intent);
        System.out.println("-------------Cancel reason----------------" + reason);
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", UserID);
        jsonParams.put("job_id", sJobId_intent);
        jsonParams.put("reason", reason);

        mRequest = new ServiceRequest(CancelJob.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("-------------CanceJob Response----------------" + response);

                String sStatus = "";

                try {
                    JSONObject object = new JSONObject(response);
                    sStatus = object.getString("status");
                    if (sStatus.equalsIgnoreCase("1")) {
                        JSONObject response_object = object.getJSONObject("response");
                        if (response_object.length() > 0) {
                            String message = response_object.getString("message");

                            Intent broadcastIntent = new Intent();
                            broadcastIntent.setAction("com.package.ACTION_CLASS_MY_JOBS_REFRESH");
                            broadcastIntent.putExtra("status","cancelled");
                            if(MyJobs.Myjobs_page!=null){
                                MyJobs.Myjobs_page.finish();
                                sendBroadcast(broadcastIntent);
                            }
                            else{
                                sendBroadcast(broadcastIntent);
                            }


                            Intent broadcastIntent_myjobsdetails = new Intent();
                            broadcastIntent_myjobsdetails.setAction("com.package.finish.MyJobDetails");
                            sendBroadcast(broadcastIntent_myjobsdetails);


                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mLoadingDialog.dismiss();
                                    finish();
                                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                }
                            }, 2000);


                        }
                    } else {
                        mLoadingDialog.dismiss();
                        String sResponse = object.getString("response");
                        alert(getResources().getString(R.string.action_sorry), sResponse);
                    }

                } catch (JSONException e) {
                    mLoadingDialog.dismiss();
                    e.printStackTrace();
                }
            }

            @Override
            public void onErrorListener() {
                mLoadingDialog.dismiss();
            }
        });
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
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            return true;
        }
        return false;
    }
}

