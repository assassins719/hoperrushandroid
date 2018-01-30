package com.hoperrush.utils;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.android.volley.Request;
import com.hoperrush.core.socket.SocketHandler;
import com.hoperrush.core.volley.ServiceRequest;
import com.hoperrush.iconstant.Iconstant;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * Created by user145 on 5/4/2017.
 */
public class AndroidServiceStartOnBoot extends Service {
    private Context myContext;
    private String UserID = "";
    private SessionManager session;
    private ServiceRequest mRequest;
    private SocketHandler socketHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        myContext = getApplicationContext();
        socketHandler = SocketHandler.getInstance(this);
        session = new SessionManager(myContext);

        HashMap<String, String> user = session.getUserDetails();
        UserID = user.get(SessionManager.KEY_USER_ID);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //  Log.e("Foreground", "" + isForeground("com.annashmi"));

        //TODO do something useful

        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> services = activityManager
                .getRunningTasks(Integer.MAX_VALUE);
        boolean isActivityFound = false;

        if (services.get(0).topActivity.getPackageName()
                .equalsIgnoreCase(getPackageName())) {
            isActivityFound = true;
        }

        if (isActivityFound) {
            Log.e("running", "app is running");
            socketHandler = SocketHandler.getInstance(this);


            if (!socketHandler.getSocketManager().isConnected) {
                socketHandler.getSocketManager().connect();
                postRequest_Logout(Iconstant.MODEUPDATE_URL, "socket");
            }
            //app is running
        } else {
            postRequest_Logout(Iconstant.MODEUPDATE_URL, "gcm");
            Log.e("closed", "app is closed");
            // applicaiton is not open, notification can come
        }
        return Service.START_STICKY;
    }

    private void postRequest_Logout(String aUrl, String aModeType) {
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user", UserID);
        jsonParams.put("user_type", "user");
        jsonParams.put("mode", aModeType);
        jsonParams.put("type", "android");


        mRequest = new ServiceRequest(myContext);
        mRequest.makeServiceRequest(aUrl, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
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
                    SocketHandler.getInstance(myContext).getSocketManager().disconnect();

                } else {
                }
            }

            @Override
            public void onErrorListener() {
            }
        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        return null;
    }

    public boolean isForeground(String myPackage) {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfo = manager.getRunningTasks(1);
        ComponentName componentInfo = runningTaskInfo.get(0).topActivity;
        return componentInfo.getPackageName().equals(myPackage);
    }


}
