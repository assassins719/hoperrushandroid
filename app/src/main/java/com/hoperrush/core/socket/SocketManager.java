package com.hoperrush.core.socket;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.hoperrush.iconstant.Iconstant;
import com.hoperrush.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.HashSet;


public class SocketManager {

    private Activity activity;
    private SocketCallBack callBack;

    public static final String EVENT_NEW_MESSAGE = "notification";
    private HashSet<Object> uniqueBucket;
    public boolean isConnected;
    private SocketConnectCallBack location;
    public static final String JOIN_NETWORK = "join network";
    public static final String NETWORK_CREATED = "network created";
    public static final String ROOM_STRING_SWITCH = "switch room";
    public static final String EVENT_LOCATION = "tasker tracking";
    private Context context;
    SessionManager session;
    public static String Userid="";

    private String sTaskID = "";


    public static interface SocketCallBack {
        void onSuccessListener(Object response);
    }

  public static interface SocketConnectCallBack {
        void onSuccessListener(Object response);
    }

    public SocketManager(Activity activity, SocketConnectCallBack location) {
        this.activity = activity;
        this.location = location;
      //  this.uniqueBucket = new HashSet<Object>();
    }


    public void Trackridelocation(SocketConnectCallBack location){

        this.location = location;
        mSocket.on(EVENT_LOCATION, onupdatelocation);
    }

    public void setSocketConnectListenre(SocketConnectCallBack arg){
        this.location = arg;
    }

    public SocketManager(SocketCallBack callBack, Context mContext) {
        this.callBack = callBack;
        this.context = mContext;
       // session=new SessionManager(activity);
//        HashMap<String,String> useris=session.getUserDetails();
//        userid=useris.get(SessionManager.KEY_USER_ID);
    }


    public void setTaskId(String task,String userid) {
        sTaskID = task;
        Userid=userid;
        Log.d("SOCKET MANAGER", "USER_ID " + sTaskID);
    }

    private Socket mSocket;

    {
        try {
            mSocket = IO.socket(Iconstant.SOCKET_HOST_URL);
            mSocket.io().reconnection(true);
        }
        catch (URISyntaxException e) {
        }
    }

    public void connect() {
        Log.d("SOCKET MANAGER", "Connecting..");
        try {
            mSocket.off(EVENT_NEW_MESSAGE, onNewMessage);
            mSocket.on(EVENT_NEW_MESSAGE, onNewMessage);
            mSocket.on(Socket.EVENT_CONNECT, onConnectMessage);
            mSocket.on(Socket.EVENT_DISCONNECT, onDisconnectMessage);
            mSocket.on(NETWORK_CREATED, onNetworkJoinListener);
            mSocket.connect();


        }
        catch (Exception e) {
        }
    }

    private Emitter.Listener onNetworkJoinListener = new Emitter.Listener() {

        @Override
        public void call(Object... args) {
            Log.d("SOCKET MANAGER", "Joined To Network");

        }
    };
    private Emitter.Listener onupdatelocation = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.d("SOCKET MANAGER", "onUpdateLocation = " + args[0]);

            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (location != null) {
                            location.onSuccessListener(args[0]);
                        }
                    }
                });
            } else {
                if (location != null) {
                    location.onSuccessListener(args[0]);
                }
            }
        }
    };

    private Emitter.Listener onConnectMessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET MANAGER", "Connected"+Userid);
            JSONObject object = new JSONObject();
            if (Userid != null && Userid.length() > 0) {
                try {

                    object.put("user", Userid);
                    createRoom(object);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            isConnected = true;
        }
    };

    private Emitter.Listener onDisconnectMessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET MANAGER", "DISCONNECTED");
            isConnected = false;
        }
    };

    public void disconnect() {
        try {
            mSocket.off(EVENT_NEW_MESSAGE, onNewMessage);
            mSocket.disconnect();
        } catch (Exception e) {
        }
    }

    public void sendMessage(String message) {
        if (TextUtils.isEmpty(message)) {
            return;
        }
        mSocket.emit(EVENT_NEW_MESSAGE, message);
    }

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.d("SOCKET MANAGER", "onNewMessage = " + args[0]);

            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (callBack != null) {
                            callBack.onSuccessListener(args[0]);
                        }
                    }
                });
            } else {
                if (callBack != null) {
                    callBack.onSuccessListener(args[0]);
                }
            }
        }
    };

    public void createRoom(Object userID) {
        if (userID == null) {
            return;
        }
        mSocket.emit(JOIN_NETWORK, userID);

    }


    public void createSwitchRoom(String userID) {
        if (TextUtils.isEmpty(userID)) {
            return;
        }
        mSocket.emit(ROOM_STRING_SWITCH, userID);
    }

}
