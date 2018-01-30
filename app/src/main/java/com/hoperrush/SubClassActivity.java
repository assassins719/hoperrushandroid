package com.hoperrush;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.hoperrush.hockeyapp.ActionBarActivityHockeyApp;

/**
 * Created by user88 on 1/11/2016.
 */
public class SubClassActivity extends ActionBarActivityHockeyApp {
    BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getSupportActionBar().hide();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.finish.ReceiveCashPage");
        filter.addAction("com.finish.OtpPage");
        filter.addAction("com.finish.PaymentFareSummeryPage");
        filter.addAction("com.finish.DetailsPage");
        filter.addAction("com.finish.HomePage");
        filter.addAction("com.finish.NewLeadsPage");
        filter.addAction("com.finish.NewLeadsFragmet");
        filter.addAction("com.finish.MissedLeadsFragment");
        filter.addAction("com.finish.StatisticsPage");
        filter.addAction("com.finish.LoadingPage");


        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("com.finish.ReceiveCashPage")) {
                    finish();
                }else if (intent.getAction().equals("com.finish.OtpPage")){
                    finish();
                }else if(intent.getAction().equals("com.finish.PaymentFareSummeryPage")){
                    finish();
                }else if (intent.getAction().equals("com.finish.DetailsPage")){
                   finish();
                }else if(intent.getAction().equals("com.finish.HomePage")){
                    finish();
                }else if(intent.getAction().equals("com.finish.NewLeadsPage")){
                    finish();
                }else if (intent.getAction().equals("com.finish.NewLeadsFragmet")){
                    finish();
                }else if (intent.getAction().equals("com.finish.MissedLeadsFragment")){
                    finish();
                }else if(intent.getAction().equals("com.finish.StatisticsPage")){
                    finish();
                }else if(intent.getAction().equals("com.finish.LoadingPage")){
                    finish();
                }
            }
        };
        registerReceiver(receiver, filter);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

}
