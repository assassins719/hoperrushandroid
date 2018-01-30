package com.hoperrush.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by user145 on 7/26/2017.
 */
public class RestartService extends BroadcastReceiver {

    private static final String TAG = "RestartService";
    private SessionManager session;

    public RestartService() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "onReceive");
        session = new SessionManager(context);
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Log.e("startuptest", "StartUpBootReceiver BOOT_COMPLETED");
            Intent pushIntent = new Intent(context, AndroidServiceStartOnBoot.class);
            context.startService(pushIntent);
        }
        if (session.isLoggedIn()) {
            Intent i = new Intent(context, AndroidServiceStartOnBoot.class);
            context.startService(i);
        }
    }
}
