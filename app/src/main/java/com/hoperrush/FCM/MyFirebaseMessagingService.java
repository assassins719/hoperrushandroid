package com.hoperrush.FCM;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.hoperrush.app.ChatPage;
import com.hoperrush.app.MyJobDetail;
import com.hoperrush.app.MyJobs;
import com.hoperrush.app.NavigationDrawer;
import com.hoperrush.app.PaymentNew;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by user145 on 8/4/2017.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    private NotificationUtils notificationUtils;
    private String Job_status_message="";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());
        System.out.println("--------FCM Received-------"+remoteMessage);
        if (remoteMessage == null)
            return;

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            handleNotification(remoteMessage.getNotification().getBody());
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                handleDataMessage(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    private void handleNotification(String message) {
        Job_status_message=message;

    }

    private void handleDataMessage(JSONObject json) {
        Log.e(TAG, "push json: " + json.toString());
        String imageUrl="";
        String message="";
        String title="";
        String task_id="";
        String tasker_id="";
        String timestamp="";
        String job_id="";
        Intent resultIntent = null;
        JSONObject object = null;
        try {
            JSONObject object1=json.getJSONObject("data");
            if(object1.has("key0")){
                Job_status_message=object1.getString("title");
                System.out.println("------------Job_Message--------"+ Job_status_message);
                if (Job_status_message.equalsIgnoreCase("Your Tasker is on their way") || Job_status_message.equalsIgnoreCase("Tasker arrived on your place") || Job_status_message.equalsIgnoreCase("Tasker started your job")
                        || Job_status_message.equalsIgnoreCase("Your job has been completed") || Job_status_message.equalsIgnoreCase("Your job is accepted") || Job_status_message.equalsIgnoreCase("Tasker request payment for his job"))
                {
                    job_id=object1.getString("key0");
                }
                else{
                    message=object1.getString("key0");
                }
            }
            else{
                object=json.getJSONObject("data");
                JSONArray array=object.getJSONArray("messages");
                JSONObject data = array.getJSONObject(0);

                 title = "Hoper Rush (Chat Notification)";
                 message = data.getString("message");
                 if(data.has("image")){
                    imageUrl = data.getString("image");
                }
                 timestamp = data.getString("date");
                 tasker_id=object.getString("tasker");
                 task_id=object.getString("task");

                Log.e(TAG, "title: " + title);
                Log.e(TAG, "message: " + message);
                Log.e(TAG, "tasker_id: " + tasker_id);
                Log.e(TAG, "task_id: " + task_id);
            }

                if (Job_status_message.equalsIgnoreCase("Your Tasker is on their way") || Job_status_message.equalsIgnoreCase("Tasker arrived on your place") || Job_status_message.equalsIgnoreCase("Tasker started your job")
                        || Job_status_message.equalsIgnoreCase("Your job has been completed") || Job_status_message.equalsIgnoreCase("Your job is accepted"))
                {
                    resultIntent = new Intent(getApplicationContext(), MyJobDetail.class);
                    resultIntent.putExtra("JOB_ID_INTENT", job_id);
                    title = "Hoper Rush (Job Notification)";
                    message=Job_status_message;
                }

                else if(Job_status_message.equalsIgnoreCase("Tasker request payment for his job")){
                    resultIntent = new Intent(getApplicationContext(), PaymentNew.class);
                    resultIntent.putExtra("JobID_INTENT", job_id);
                    title = "Hoper Rush (Request Payment)";
                    message=Job_status_message;

                }
                else if(object.has("user")){
                    resultIntent = new Intent(getApplicationContext(), ChatPage.class);
                    resultIntent.putExtra("TaskId", task_id);
                    resultIntent.putExtra("TaskerId", tasker_id);
                }

                else if(Job_status_message.equalsIgnoreCase("Tasker rejected this job")){
                    title = "Hoper Rush (Cancel Request)";
                    message=Job_status_message;
                    resultIntent = new Intent(getApplicationContext(), MyJobs.class);
                }
                else{
                    title = "Hoper Rush (Admin Notification)";
                    resultIntent = new Intent(getApplicationContext(), NavigationDrawer.class);
                }
                // check for image attachment
                if (TextUtils.isEmpty(imageUrl)) {
                    showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
                } else {
                    // image is present, show notification with image
                    showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl);
                }

        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }
}