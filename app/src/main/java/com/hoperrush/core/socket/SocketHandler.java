package com.hoperrush.core.socket;

import android.content.Context;
import android.os.Messenger;

import com.hoperrush.utils.SessionManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class SocketHandler {
    private Context context;
    private SocketManager manager;
    private SocketParser parser;
    private static SocketHandler instance;
    private ArrayList<Messenger> listenerMessenger;
    private SessionManager sessionManager;
     public static String user;
     private SocketHandler(Context context) {
        this.context = context;
        this.manager = new SocketManager(callBack,context);

        this.parser = new SocketParser(context);
        this.listenerMessenger = new ArrayList<Messenger>();
        sessionManager=new SessionManager(context);
    }

    public static SocketHandler getInstance(Context context) {
        if (instance == null) {
            instance = new SocketHandler(context);
        }
        return instance;
    }

    public void addChatListener(Messenger messenger) {
        listenerMessenger.add(messenger);
    }

    public SocketManager getSocketManager() {
        HashMap<String,String> taskId=sessionManager.getSocketTaskId();
        String sTaskID=taskId.get(SessionManager.KEY_TASK_ID);
        HashMap<String,String> userid=sessionManager.getUserDetails();
        String useridd=userid.get(SessionManager.KEY_USER_ID);
        manager.setTaskId(sTaskID,useridd);
        return manager;
    }

    public SocketManager.SocketCallBack callBack = new SocketManager.SocketCallBack() {
        boolean isChat = false;

        @Override
        public void onSuccessListener(Object response) {
            if (response instanceof JSONObject) {
                JSONObject jsonObject = (JSONObject) response;
                System.out.println("-----------SocketHandlerjsonObject------------"+jsonObject);
                try {
                    String username = jsonObject.getString("username");
                    String message = jsonObject.getString("message");
                    android.os.Message chatMessage = android.os.Message.obtain();
                    chatMessage.obj = jsonObject.toString();
                    isChat = true;
                    for (Messenger m : listenerMessenger) {
                        if (m != null)
                            m.send(chatMessage);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    isChat = false;
                }
                if (isChat) {
                    parser.onHandleSocketJSON(jsonObject);
                }
            }
        }
    };


}
