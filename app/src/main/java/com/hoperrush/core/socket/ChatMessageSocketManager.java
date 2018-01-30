package com.hoperrush.core.socket;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.hoperrush.iconstant.Iconstant;
import com.hoperrush.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by user145 on 4/7/2017.
 */
public class ChatMessageSocketManager {

    private String TAG = "SOCKET MANAGER";

    //---------------------------------Chat------------------------------------------------------------------


    public static final String EVENT_NEW_MESSAGE = "new message";
    public static final String EVENT_NEW_IMAGE = "start file upload";
    public static final String EVENT_LOCATION = "tasker tracking";
    public static final String EVENT_TYPING = "start typing";
    public static final String EVENT_STOP_TYPING = "stop typing";
    public static final String EVENT_UPDATE_CHAT = "updatechat";
    public static final String EVENT_MESSAGE_STATUS = "message status";
    public static final String EVENT_ROOM = "create room";
   public static String userid = "58174871cc590d9015ba966d";
    public static final String EVENT_SINGLE_MESSAGE_STATUS = "single message status";
    public static final String EVENT_CONNECT = "connect";
    public static final String EVENT_DISCONNECT = "disconnect";

    //---------------------------------Chat------------------------------------------------------------------

    private Activity activity;
    private SocketCallBack callBack;

    public static final String EVENT_GET_OFFLINE_MESSAGES = "getofflinemsg";
    private HashSet<Object> uniqueBucket;
    private boolean isConnected;
    String ip = Iconstant.SOCKET_CHAT_URL;

    private String provider_id = "";
    private Context context;

    private String sProviderID = "";
    private String mCurrentUserId;

    public interface SocketCallBack {
        void onSuccessListener(String eventName, Object... response);
    }

    public ChatMessageSocketManager(Activity activity, SocketCallBack callBack) {
        this.activity = activity;
        this.callBack = callBack;
        this.uniqueBucket = new HashSet<Object>();
        System.out.println("provider_idsession-----------" + provider_id);
    }

    public ChatMessageSocketManager(Context context, SocketCallBack callBack) {
        this.callBack = callBack;
        this.context = context;
        getSocketIp();

        SessionManager session = new SessionManager(context);
        HashMap<String, String> userDetails = session.getUserDetails();
        mCurrentUserId = userDetails.get(session.KEY_USER_ID);
    }

    private void getSocketIp() {
        try {
            if (mSocket != null) {
                mSocket.close();
                mSocket.off();
                mSocket = null;
            }
            mSocket = IO.socket(ip);
            mSocket.io().reconnection(true);

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }


    public void setTaskId(String providerID) {
        sProviderID = providerID;
        System.out.println("sTaskID partner session--------------" + sProviderID);
    }

    private static Socket mSocket;

    public void connect() {
        try {

            if (!mSocket.connected()) {
                mSocket.off();
                mSocket.on(Socket.EVENT_CONNECT, onConnectMessage);
                mSocket.on(Socket.EVENT_DISCONNECT, onDisconnectMessage);
                mSocket.connect();
            } else {
                onSocketConnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onSocketConnect() {
        addListener();
        createRoom();
        getOfflineMessages();
    }

    private void getOfflineMessages() {

        try {
            JSONObject object = new JSONObject();
            object.put("user", mCurrentUserId);
            send(object, EVENT_GET_OFFLINE_MESSAGES);
            Log.d("OfflineMsg", "Offline Event Called"+mCurrentUserId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public boolean isConnected() {
        return isConnected;
    }

    private Emitter.Listener onConnectMessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("CHATMANAGER-connect", "Connected");
            isConnected = true;
            removeAllListener();
            onSocketConnect();
            mSocket.on(EVENT_UPDATE_CHAT, onUpdteChat);
            invokeCallBack(Socket.EVENT_CONNECT, args);
        }
    };

    public void createRoom() {
        SessionManager session = new SessionManager(context);
        String userId = session.getUserDetails().get(session.KEY_USER_ID);
        JSONObject object=new JSONObject();
        try {
            object.put("user",userId);
            mSocket.emit(EVENT_ROOM, object);
            Log.d("CHATROOM CREATED", "CHAT Single Room Created"+userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private Emitter.Listener onDisconnectMessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("CHAT MANAGER", "DISCONNECTED");
            invokeCallBack(Socket.EVENT_DISCONNECT, args);
        }
    };


 //----------------------------------------------------------------CHAT LISTENER--------------------------------------------------------

    private Emitter.Listener onupdatelocation = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d(TAG, "UPDATE LOCATION");
            invokeCallBack(EVENT_LOCATION, args);

        }
    };


    private Emitter.Listener onUpdteChat = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d(TAG, "UPDATE CHAT");
            invokeCallBack(EVENT_UPDATE_CHAT, args);
        }
    };
    private Emitter.Listener onSingleMessageStatus = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d(TAG, "Single Message Status");
            invokeCallBack(EVENT_SINGLE_MESSAGE_STATUS, args);
        }
    };

    private Emitter.Listener onmessagestatus = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d(TAG, "MESSAGE STATUS");
            invokeCallBack(EVENT_MESSAGE_STATUS, args);
        }
    };

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            invokeCallBack(EVENT_NEW_MESSAGE, args);
        }
    };

    private Emitter.Listener onnewimage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            invokeCallBack(EVENT_NEW_IMAGE, args);
        }
    };

    private Emitter.Listener typing = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            invokeCallBack(EVENT_TYPING, args);
        }
    };
    private Emitter.Listener stopTyping = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            invokeCallBack(EVENT_STOP_TYPING, args);
        }
    };



    //----------------------------------------------------------------CHAT LISTENER--------------------------------------------------------

    public void addListener() {
        mSocket.on(EVENT_TYPING, typing);
        mSocket.on(EVENT_STOP_TYPING, stopTyping);
        mSocket.on(EVENT_NEW_MESSAGE, onNewMessage);
        mSocket.on(EVENT_UPDATE_CHAT, onUpdteChat);
        mSocket.on(EVENT_MESSAGE_STATUS, onmessagestatus);
        mSocket.on(EVENT_SINGLE_MESSAGE_STATUS, onSingleMessageStatus);
        mSocket.on(EVENT_NEW_IMAGE, onnewimage);

    }

    public void disconnect() {
        try {
            removeAllListener();

            mSocket.off(Socket.EVENT_CONNECT, onConnectMessage);
            mSocket.off(Socket.EVENT_DISCONNECT, onDisconnectMessage);
            mSocket.disconnect();
        } catch (Exception e) {
        }
    }



    private void removeAllListener() {
        mSocket.off(EVENT_TYPING, typing);
        mSocket.off(EVENT_STOP_TYPING, stopTyping);
        mSocket.off(EVENT_NEW_MESSAGE, onNewMessage);
        mSocket.off(EVENT_NEW_IMAGE, onnewimage);
        mSocket.off(EVENT_UPDATE_CHAT, onUpdteChat);
        mSocket.off(EVENT_LOCATION, onupdatelocation);
        mSocket.off(EVENT_MESSAGE_STATUS, onmessagestatus);
        mSocket.off(EVENT_SINGLE_MESSAGE_STATUS, onSingleMessageStatus);
    }

    public void send(Object message, String eventName) {

        switch (eventName) {


            case EVENT_NEW_MESSAGE:
                if(isConnected){
                    mSocket.emit(EVENT_NEW_MESSAGE, message);
                  }
                else{
                   connect();
                }

                break;

            case EVENT_TYPING:
                mSocket.emit(EVENT_TYPING, message);
                break;

            case EVENT_STOP_TYPING:
                mSocket.emit(EVENT_STOP_TYPING, message);
                break;

            case EVENT_SINGLE_MESSAGE_STATUS:
                mSocket.emit(EVENT_SINGLE_MESSAGE_STATUS, message);
                break;

            case EVENT_MESSAGE_STATUS:
                mSocket.emit(EVENT_MESSAGE_STATUS, message);
                break;

            case EVENT_NEW_IMAGE:
                mSocket.emit(EVENT_NEW_IMAGE, message);
                break;
        }
    }

    public void invokeCallBack(final String eventName, final Object... args) {
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (callBack != null) {
                        callBack.onSuccessListener(eventName, args);
                    }
                }
            });
        } else {
            if (callBack != null) {
                callBack.onSuccessListener(eventName, args);
            }
        }
    }
}
