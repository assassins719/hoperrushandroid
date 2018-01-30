package com.hoperrush.hockeyapp;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.android.volley.Request;
import com.hoperrush.core.socket.ChatMessageService;
import com.hoperrush.core.socket.SocketHandler;
import com.hoperrush.core.volley.ServiceRequest;
import com.hoperrush.iconstant.Iconstant;
import com.hoperrush.utils.SessionManager;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.UpdateManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * Casperon Technology on 11/23/2015.
 */
public class FragmentActivityHockeyApp extends FragmentActivity
{
    private static  String APP_ID = "a0edd6450fc641bead62a9bed17c39cc";
    private SocketHandler socketHandler;
    private SessionManager session;
    private ServiceRequest mRequest;
    private String UserID = "";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        socketHandler = SocketHandler.getInstance(this);
        session = new SessionManager(this);
        HashMap<String, String> user = session.getUserDetails();
        UserID = user.get(SessionManager.KEY_USER_ID);
        checkForUpdates();
    }
    @Override
    protected void onResume() {
        super.onResume();
        socketHandler = SocketHandler.getInstance(this);

        if(session.isLoggedIn()){
            if (!socketHandler.getSocketManager().isConnected) {
                Log.e("connect", "Socket");
                socketHandler.getSocketManager().connect();
            }
            postRequest_ModeChange("socket");
            if (!ChatMessageService.isStarted()) {
                Intent intent = new Intent(this, ChatMessageService.class);
                startService(intent);
            }
        }
        checkForCrashes();
    }

    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterManagers();
        Log.e("onPause", String.valueOf(isAppIsInBackground(this)));
        if (!isAppIsInBackground(this)) {
            postRequest_ModeChange("gcm");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterManagers();
        Intent intent = new Intent("restartApps");
        sendBroadcast(intent);
    }

    private void checkForCrashes() {
        CrashManager.register(this, APP_ID);
    }

    private void checkForUpdates() {
        // Remove this for store builds!
        UpdateManager.register(this, APP_ID);
    }

    private void unregisterManagers() {
        UpdateManager.unregister();
        // unregister other managers if necessary...
    }

    private void postRequest_ModeChange(final String aModeType) {
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user", UserID);
        jsonParams.put("user_type", "user");
        jsonParams.put("mode", aModeType);
        jsonParams.put("type", "android");


        mRequest = new ServiceRequest(this);
        mRequest.makeServiceRequest(Iconstant.MODEUPDATE_URL, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("---------------Mode Response-----------------" + response);
                String sStatus = "", sResponse = "";
                try {

                    JSONObject object = new JSONObject(response);
                    sStatus = object.getString("status");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (sStatus.equalsIgnoreCase("1")) {
                    //    session.logoutUser();
                    if(aModeType.equalsIgnoreCase("socket")){
                        Log.e("MODE UPDATED","SOCKET");
                    }
                    else{
                        Log.e("MODE UPDATED","GCM");
                    }
                    if (!socketHandler.getSocketManager().isConnected) {
                        socketHandler.getSocketManager().disconnect();
                    }
                } else {
                }
            }

            @Override
            public void onErrorListener() {
            }
        });
    }
}

