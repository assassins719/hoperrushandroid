package com.hoperrush.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.hoperrush.hockeyapp.ActivityHockeyApp;

/**
 * Created by user88 on 1/11/2016.
 */
public class SubClassActivity extends ActivityHockeyApp {
    BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.finish.LogInPage");
        filter.addAction("com.finish.RegisterPage");
        filter.addAction("com.finish.PaymentWebview");
        filter.addAction("com.finish.PaymentDetails");
        filter.addAction("com.finish.AppoimentConfirmation");
        filter.addAction("com.finish.AppoimentPage");
        filter.addAction("com.finish.CategoriesDetailsPage");
        filter.addAction("com.finish.MyjobsDetailsPage");
        filter.addAction("com.finish.PaymentPage");



        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("com.finish.LogInPage")) {
                    finish();
                }else if (intent.getAction().equals("com.finish.RegisterPage")){
                    finish();
                }else if(intent.getAction().equals("com.finish.PaymentWebview")){
                    finish();
                }else if(intent.getAction().equals("com.finish.PaymentDetails")){
                    finish();
                }else if (intent.getAction().equals("com.finish.AppoimentConfirmation")){
                    finish();
                }else if(intent.getAction().equals("com.finish.AppoimentPage")){
                    finish();
                }else if(intent.getAction().equals("com.finish.CategoriesDetailsPage")){
                    finish();
                }else if(intent.getAction().equals("com.finish.MyjobsDetailsPage")){
                    finish();
                }else if(intent.getAction().equals("com.finish.PaymentPage")){
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
