package com.hoperrush.utils;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.Timer;

/**
 * Casperon Technology on 1/25/2016.
 */
public class IdentifyAppKilled extends Service {

    private static final String TAG = "UEService";
    private Timer timer;
    private static final int delay = 1000; // delay for 1 sec before first start
    private static final int period = 10000;
    SessionManager session;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("ClearFromRecentService", "Service Started");
        //   Toast.makeText(IdentifyAppKilled.this, "App Started", Toast.LENGTH_SHORT).show();
        session = new SessionManager(IdentifyAppKilled.this);
        if (session.isLoggedIn()) {
            Intent i = new Intent(IdentifyAppKilled.this, AndroidServiceStartOnBoot.class);
            startService(i);
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //  Toast.makeText(IdentifyAppKilled.this, "App Destroyed", Toast.LENGTH_SHORT).show();
        session = new SessionManager(IdentifyAppKilled.this);
        if (session.isLoggedIn()) {
            Intent i = new Intent(IdentifyAppKilled.this, AndroidServiceStartOnBoot.class);
            startService(i);
        }
        //Code to send request to disable chat
        /*ChatAvailabilityCheck chatAvailability = new ChatAvailabilityCheck(this, "unavailable");
        chatAvailability.postChatRequest();*/

        Log.e("ClearFromRecentService", "Service Destroyed");
    }

    public void onTaskRemoved(Intent rootIntent) {
        Log.e("ClearFromRecentService", "END");
        //   Toast.makeText(IdentifyAppKilled.this, "App Killed", Toast.LENGTH_SHORT).show();
        session = new SessionManager(IdentifyAppKilled.this);
        if (session.isLoggedIn()) {
            Intent i = new Intent(IdentifyAppKilled.this, AndroidServiceStartOnBoot.class);
            startService(i);
        }
        // stopSelf();
    }

}