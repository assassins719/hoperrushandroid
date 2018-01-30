package com.hoperrush.core.socket;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 */
public class NetworkChangeReceiver extends BroadcastReceiver {
    private SocketHandler handler;


    @Override
    public void onReceive(Context context, Intent intent) {
        handler = SocketHandler.getInstance(context);

    }

    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }
}
