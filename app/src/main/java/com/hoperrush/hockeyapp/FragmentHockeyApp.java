package com.hoperrush.hockeyapp;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
 * Casperon Technology on 11/12/2015.
 */
public class FragmentHockeyApp extends Fragment {
    private static String APP_ID = "a0edd6450fc641bead62a9bed17c39cc";
    private SocketHandler socketHandler;
    private SessionManager session;
    private ServiceRequest mRequest;
    private String UserID = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkForUpdates();
        socketHandler = SocketHandler.getInstance(getActivity());
        session = new SessionManager(getActivity());
        HashMap<String, String> user = session.getUserDetails();
        UserID = user.get(SessionManager.KEY_USER_ID);
        return null;

    }

    @Override
    public void onResume() {
        super.onResume();
        socketHandler = SocketHandler.getInstance(getActivity());
        session = new SessionManager(getActivity());
        if(session.isLoggedIn()){
            if (!socketHandler.getSocketManager().isConnected) {
                Log.e("connect", "Socket");
                socketHandler.getSocketManager().connect();
            }
            postRequest_ModeChange("socket");
            if (!ChatMessageService.isStarted()) {
                Intent intent = new Intent(getActivity(), ChatMessageService.class);
                getActivity().startService(intent);
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
    public void onPause() {
        super.onPause();
        Log.e("onPause", String.valueOf(getActivity()));
        session = new SessionManager(getActivity());
        HashMap<String, String> user = session.getUserDetails();
        UserID = user.get(SessionManager.KEY_USER_ID);
        if (isAppIsInBackground(getActivity())) {
            postRequest_ModeChange("gcm");
        }
        unregisterManagers();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterManagers();
        Intent intent = new Intent("restartApps");
        getActivity().sendBroadcast(intent);
    }

    private void checkForCrashes() {
        CrashManager.register(getActivity(), APP_ID);
    }

    private void checkForUpdates() {
        // Remove this for store builds!
        UpdateManager.register(getActivity(), APP_ID);
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


        mRequest = new ServiceRequest(getActivity());
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

