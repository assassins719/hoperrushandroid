package com.hoperrush.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hoperrush.R;
import com.hoperrush.hockeyapp.ActivityHockeyApp;
import com.hoperrush.utils.SessionManager;

import java.util.HashMap;

/**
 * Casperon Technology on 1/19/2016.
 */
public class PushNotificationAlert extends ActivityHockeyApp {
    private String sTitle = "", sMessage = "", sOrderId = "";

    TextView Tv_ok, Tv_title;
    RelativeLayout Rl_ok;
    TextView message;
    private SessionManager sessionManager;

    public class RefreshReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.package.finish.PushNotificationAlert")) {
                finish();
            }
        }
    }

    private RefreshReceiver finishReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pushnotification_alert);
        initialize();

        Rl_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sessionManager = new SessionManager(PushNotificationAlert.this);

                // get My Jobs Details Class Open or not from session
                HashMap<String, String> isOpen = sessionManager.getMyJobDetailOpen();
                String sIsMyJobsClassOpen = isOpen.get(SessionManager.KEY_CHECK_MY_JOB_DETAIL_CLASS_OPEN);

                if (sTitle.equalsIgnoreCase(getResources().getString(R.string.xmpp_notification_label_accept))) {

                    if (sIsMyJobsClassOpen.equalsIgnoreCase("Opened")) {
                        Intent refreshBroadcastIntent = new Intent();
                        refreshBroadcastIntent.setAction("com.package.refresh.MyJobDetails");
                        sendBroadcast(refreshBroadcastIntent);

                    } else {
                        Intent intent = new Intent(PushNotificationAlert.this, MyJobDetail.class);
                        intent.putExtra("JOB_ID_INTENT", sOrderId);
                        startActivity(intent);
                    }

                } else if (sTitle.equalsIgnoreCase(getResources().getString(R.string.xmpp_notification_label_arrived))) {

                    if (sIsMyJobsClassOpen.equalsIgnoreCase("Opened")) {
                        Intent refreshBroadcastIntent = new Intent();
                        refreshBroadcastIntent.setAction("com.package.refresh.MyJobDetails");
                        sendBroadcast(refreshBroadcastIntent);

                    } else {
                        Intent intent = new Intent(PushNotificationAlert.this, MyJobDetail.class);
                        intent.putExtra("JOB_ID_INTENT", sOrderId);
                        startActivity(intent);
                    }
                } else if (sTitle.equalsIgnoreCase(getResources().getString(R.string.xmpp_notification_label_startoff))) {

                    if (sIsMyJobsClassOpen.equalsIgnoreCase("Opened")) {
                        Intent refreshBroadcastIntent = new Intent();
                        refreshBroadcastIntent.setAction("com.package.refresh.MyJobDetails");
                        sendBroadcast(refreshBroadcastIntent);

                    } else {
                        Intent intent = new Intent(PushNotificationAlert.this, MyJobDetail.class);
                        intent.putExtra("JOB_ID_INTENT", sOrderId);
                        startActivity(intent);
                    }
                } else if (sTitle.equalsIgnoreCase(getResources().getString(R.string.xmpp_notification_label_completed))) {
                    if (sIsMyJobsClassOpen.equalsIgnoreCase("Opened")) {
                        Intent refreshBroadcastIntent = new Intent();
                        refreshBroadcastIntent.setAction("com.package.refresh.MyJobDetails");
                        sendBroadcast(refreshBroadcastIntent);

                    } else {
                        Intent intent = new Intent(PushNotificationAlert.this, MyJobDetail.class);
                        intent.putExtra("JOB_ID_INTENT", sOrderId);
                        startActivity(intent);
                    }


                }

                else if (sTitle.equalsIgnoreCase(getResources().getString(R.string.xmpp_notification_label_started))) {
                    if (sIsMyJobsClassOpen.equalsIgnoreCase("Opened")) {
                        Intent refreshBroadcastIntent = new Intent();
                        refreshBroadcastIntent.setAction("com.package.refresh.MyJobDetails");
                        sendBroadcast(refreshBroadcastIntent);

                    } else {
                        Intent intent = new Intent(PushNotificationAlert.this, MyJobDetail.class);
                        intent.putExtra("JOB_ID_INTENT", sOrderId);
                        startActivity(intent);
                    }


                }
                else if (sMessage.equalsIgnoreCase(getResources().getString(R.string.job_cancel))) {
                    if (sIsMyJobsClassOpen.equalsIgnoreCase("Opened")) {
                        Intent refreshBroadcastIntent = new Intent();
                        refreshBroadcastIntent.setAction("com.package.finish.pushnotification");
                        sendBroadcast(refreshBroadcastIntent);
                        finish();
                    } else {
                        Intent refreshBroadcastIntent = new Intent();
                        refreshBroadcastIntent.setAction("com.package.finish.pushnotification");
                        sendBroadcast(refreshBroadcastIntent);
                        finish();
                        finish();
                    }

                } else if (sTitle.equalsIgnoreCase(getResources().getString(R.string.xmpp_notification_label_declined))) {

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

                } else if (sTitle.equalsIgnoreCase(getResources().getString(R.string.xmpp_notification_label_reAssign))) {
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
                }
                else if (sTitle.equalsIgnoreCase(getResources().getString(R.string.xmpp_notification_payment_request))) {
                   Intent i1 = new Intent(getApplicationContext(), PaymentNew.class);
                   i1.putExtra("JobID_INTENT", sOrderId);
                   i1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                   startActivity(i1);
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
                else if (sTitle.equalsIgnoreCase(getResources().getString(R.string.admin_notification))) {
                    finish();
                }

                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

            }
        });
    }

    private void initialize() {

        Tv_ok = (TextView) findViewById(R.id.pushNotification_alert_ok_textView);
        Tv_title = (TextView) findViewById(R.id.pushNotification_alert_message_label);
        message = (TextView) findViewById(R.id.pushNotification_alert_message_textView);
        Rl_ok = (RelativeLayout) findViewById(R.id.pushNotification_alert_ok_layout);

        Intent intent = getIntent();
        sTitle = intent.getStringExtra("TITLE_INTENT");
        sMessage = intent.getStringExtra("MESSAGE_INTENT");
        sOrderId = intent.getStringExtra("ORDER_ID_INTENT");
        System.out.println("STitle----------" + sTitle);
        System.out.println("Smessage----------" + sMessage);
        System.out.println("orderId----------" + sOrderId);

        System.out.println("orderId----------" + sOrderId);

        message.setText(sMessage);
        Tv_title.setText(sTitle);


        // -----code to refresh drawer using broadcast receiver-----
        finishReceiver = new RefreshReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.package.finish.PushNotificationAlert");
        registerReceiver(finishReceiver, intentFilter);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        // Unregister the logout receiver
        unregisterReceiver(finishReceiver);
        super.onDestroy();
    }

    //-----------------Move Back on pressed phone back button------------------
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)) {
            finish();
            return true;
        }
        return false;
    }
}
