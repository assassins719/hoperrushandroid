package com.hoperrush.core.socket;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


import com.hoperrush.R;
import com.hoperrush.app.ChatPage;
import com.hoperrush.pojo.ReceiveMessageEvent;
import com.hoperrush.pojo.SendMessageEvent;
import com.hoperrush.utils.SessionManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by user145 on 4/7/2017.
 */
public class ChatMessageService extends Service {

    static ChatMessageSocketManager manager;
    static public ChatMessageService service;
    private ActiveSocketDispatcher dispatcher;
    public static Context context;
    public static String task_id ="";
    public static String tasker_id="";
    private String mCurrentUserId;
  //  private MessageDBHelper messageDB;

    public static final long MIN_GET_OFFLINE_MESSAGES_TIME = 60 * 1000; // 60 seconds

    public static boolean isStarted() {
        if (service == null)
            return false;
        return true;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SendMessageEvent event) {
     manager.send(event.getMessageObject(), event.getEventName());
    }

    @Override
    public void onCreate() {
        super.onCreate();

        EventBus.getDefault().register(this);

        manager = new ChatMessageSocketManager(this, callBack);
        service = this;
        context = this;
        dispatcher = new ActiveSocketDispatcher();
        SessionManager session = new SessionManager(ChatMessageService.this);
        HashMap<String, String> userDetails = session.getUserDetails();
        mCurrentUserId = userDetails.get(session.KEY_USER_ID);

        manager.connect();
    }

    public static void checkSocketConnected() {
        if (!manager.isConnected()) {
        }
    }

    ChatMessageSocketManager.SocketCallBack callBack = new ChatMessageSocketManager.SocketCallBack() {
        @Override
        public void onSuccessListener(String eventName, Object... response) {
            ReceiveMessageEvent me = new ReceiveMessageEvent();
            me.setEventName(eventName);
            me.setObjectsArray(response);

            switch (eventName) {

                case ChatMessageSocketManager.EVENT_CONNECT:
                    if (!manager.isConnected()) {
                        manager.connect();
                        Log.e("CHAT MANAGER", "CHAT SOCKET RECONNECTED");
                    }
                    break;

                case ChatMessageSocketManager.EVENT_DISCONNECT:
                    manager.connect();
                    break;

                case ChatMessageSocketManager.EVENT_UPDATE_CHAT:

                        PushNotification(response[0].toString());


                     break;

                case ChatMessageSocketManager.EVENT_TYPING:
                     break;

                case ChatMessageSocketManager.EVENT_STOP_TYPING:
                      break;

                case ChatMessageSocketManager.EVENT_SINGLE_MESSAGE_STATUS:
                     break;


            }

            dispatcher.addwork(me);
        }
    };

    public class ActiveSocketDispatcher {
        private BlockingQueue<Runnable> dispatchQueue
                = new LinkedBlockingQueue<Runnable>();

        public ActiveSocketDispatcher() {
            Thread mThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            dispatchQueue.take().run();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            mThread.start();
        }

        private void addwork(final Object packet) {
            try {
                dispatchQueue.put(new Runnable() {
                    @Override
                    public void run() {
                        EventBus.getDefault().post(packet);
                    }
                });
            }
            catch (Exception e) {
            }
        }

    }

    public void PushNotification(String msgData){
        SessionManager session = new SessionManager(ChatMessageService.this);
        HashMap<String, String> chatid = session.getUserDetails();
        String  Chat_Tasker_Id = chatid.get(SessionManager.KEY_Chat_userid);
        HashMap<String, String> taskids = session.getUserDetails();
        String Chat_task_id = taskids.get(SessionManager.KEY_TASK_ID);
        long when = System.currentTimeMillis();
        JSONObject object = null;
        String message="";
        String msg_id="";
        String confirm_msg_id="";
        String message_from="";
        try {
            object = new JSONObject(msgData);
             String user = object.getString("user");
             task_id = object.getString("task");
             tasker_id = object.getString("tasker");
             JSONArray array =object.getJSONArray("messages");
             JSONObject msg_object=array.getJSONObject(0);
             msg_id=msg_object.getString("_id");
             message_from=msg_object.getString("from");
            if(!msg_id.equalsIgnoreCase(confirm_msg_id)){
                  confirm_msg_id=msg_id;
               message=msg_object.getString("message");

            }


            NotificationManager mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
//            RemoteViews contentView = new RemoteViews
//                    (getPackageName(), R.layout.custom_notification);


            PendingIntent contentIntent = null;
            Intent intent = new Intent(context, ChatPage.class);
            intent.putExtra("TaskId", task_id);
            intent.putExtra("TaskerId", tasker_id);
            contentIntent = PendingIntent.getActivity(context, 0, intent
                    , 0);
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.mipmap.hopericon)

                            .setContentTitle("Chat Message from partner")
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(message))
                            .setContentText(message).setAutoCancel(true)
                            .setSound(Uri.parse("android.resource://com.maidac/"+R.raw.notifysnd));

               if(!mCurrentUserId.equalsIgnoreCase(message_from)){

              if (!ChatPage.isChatPageAvailable || !Chat_task_id.equalsIgnoreCase(task_id) || !Chat_Tasker_Id.equalsIgnoreCase(tasker_id)) {

                  mBuilder.setPriority(Notification.PRIORITY_HIGH);
                  mBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
                  mBuilder.setContentIntent(contentIntent);
                  mNotificationManager.notify(1, mBuilder.build());
                  if(ChatPage.isChatPageAvailable){
                      ChatPage.chat_activity.finish();
                  }

              }
          }


        } catch (JSONException e) {
            e.printStackTrace();
        }



    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Toast.makeText(this, "SERVICE IS START_STICKY " + intent, Toast.LENGTH_LONG).show();
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        service = null;
        manager.disconnect();
        //Toast.makeText(this, "SERVICE IS DESROYERD ", Toast.LENGTH_LONG).show();
        super.onDestroy();
    }

}
