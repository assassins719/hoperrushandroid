package com.hoperrush.core.pushnotification;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.hoperrush.GcmBroadcastReceiver;
import com.hoperrush.R;
import com.hoperrush.app.ChatPage;
import com.hoperrush.app.MyJobDetail;
import com.hoperrush.app.MyJobs;
import com.hoperrush.app.NavigationDrawer;
import com.hoperrush.app.PaymentNew;

/**
 * @author Anitha
 */

public class GCMNotificationIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    NotificationCompat.Builder builder;
    Context context = GCMNotificationIntentService.this;

    private String driverID = "", driverName = "", driverEmail = "", driverImage = "", driverRating = "",
            driverLat = "", driverLong = "", driverTime = "", rideID = "", driverMobile = "",
            driverCar_no = "", driverCar_model = "", message = "";
    private String myOrderIdStr = "";
    public static boolean isNotificationFlag = false;

    public GCMNotificationIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                //sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                //    sendNotification("Deleted on server: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {

                Log.e("Received: ", "" + extras.toString());

                if (extras != null) {
                    try {
                        message = extras.getString("gcm.notification.body");
                        myOrderIdStr = extras.getString("key0");
                        sendNotification(message, extras);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }


    @SuppressWarnings("deprecation")
    private void sendNotification(String msg, Bundle aResponse) {
        Intent notificationIntent = null;

        if (msg.equalsIgnoreCase("Your provider is on their way") || msg.equalsIgnoreCase("Provider arrived on your place") || msg.equalsIgnoreCase("Provider started your job")
                || msg.equalsIgnoreCase("Your job has been completed") || msg.equalsIgnoreCase("Your job is accepted")) {
            notificationIntent = new Intent(GCMNotificationIntentService.this, MyJobDetail.class);
            notificationIntent.putExtra("JOB_ID_INTENT", myOrderIdStr);
        } else if (msg.equalsIgnoreCase("Provider request payment for his job")) {
            notificationIntent = new Intent(GCMNotificationIntentService.this, PaymentNew.class);
            notificationIntent.putExtra("JobID_INTENT", myOrderIdStr);
        } else if (msg.equalsIgnoreCase("Provider rejected this job")) {
            notificationIntent = new Intent(GCMNotificationIntentService.this, MyJobs.class);
        } else if (aResponse.toString().contains("user")) {
            notificationIntent = new Intent(GCMNotificationIntentService.this, ChatPage.class);
            notificationIntent.putExtra("TaskId", aResponse.getString("task"));
            notificationIntent.putExtra("TaskerId", aResponse.getString("tasker"));
        } else {
            notificationIntent = new Intent(GCMNotificationIntentService.this, NavigationDrawer.class);
        }

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(GCMNotificationIntentService.this, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationManager nm = (NotificationManager) GCMNotificationIntentService.this.getSystemService(Context.NOTIFICATION_SERVICE);
        Resources res = GCMNotificationIntentService.this.getResources();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(GCMNotificationIntentService.this);
        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.mipmap.hopericon)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.hopericon))
                .setTicker(msg)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setLights(0xffff0000, 100, 2000)
                .setPriority(Notification.DEFAULT_SOUND)
                .setContentText(msg)
                .setContentIntent(contentIntent)
                .setSound(Uri.parse("android.resource://com.maidac/"+R.raw.notifysnd));
        isNotificationFlag = true;
        Notification n = builder.getNotification();

        n.defaults |= Notification.DEFAULT_ALL;
        nm.notify(0, n);

    }

}