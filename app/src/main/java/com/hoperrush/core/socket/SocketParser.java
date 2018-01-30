package com.hoperrush.core.socket;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.provider.Settings;

import com.hoperrush.R;
import com.hoperrush.app.PushNotificationAlert;
import com.hoperrush.app.RatingPage;
import com.hoperrush.iconstant.Iconstant;
import com.hoperrush.utils.SessionManager;

import org.json.JSONObject;

import java.util.HashMap;

public class SocketParser implements Iconstant {
    private MediaPlayer mediaPlayer;
    private Context context;
    private SessionManager sessionManager;

    public SocketParser(Context context) {
        this.context = context;
        this.sessionManager = new SessionManager(context);
        mediaPlayer = MediaPlayer.create(context, Settings.System.DEFAULT_NOTIFICATION_URI);
    }

    public void onHandleSocketJSON(JSONObject messageObject) {

        System.out.println("-------------SocketParser message---------------" + messageObject);
        if (mediaPlayer != null) {
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
            }
        }

        try {
           /* String sAction = messageObject.getString("action");
            String sMessage = messageObject.getString("message");
            String sOrderID = messageObject.getString("key1");*/

            JSONObject jobject = messageObject.getJSONObject("message");

            String sAction = jobject.getString("action");
            String sMessage = jobject.getString("message");
            String sOrderID = jobject.getString("key0");

            if (sAction.equalsIgnoreCase(Iconstant.sAccept_action)) {
                showMessage(context.getResources().getString(R.string.xmpp_notification_label_accept), sMessage, sOrderID);
            } else if (sAction.equalsIgnoreCase(Iconstant.sJobReAssign_action)) {
                showMessage(context.getResources().getString(R.string.xmpp_notification_label_reAssign), sMessage, sOrderID);
            } else if (sAction.equalsIgnoreCase(Iconstant.sStartOff_action)) {
                showMessage(context.getResources().getString(R.string.xmpp_notification_label_startOff), sMessage, sOrderID);
            }
            else if (sAction.equalsIgnoreCase(Iconstant.sArrived_action)) {
                showMessage(context.getResources().getString(R.string.xmpp_notification_label_arrived), sMessage, sOrderID);
            } else if (sAction.equalsIgnoreCase(Iconstant.sJobStarted_action)) {
                showMessage(context.getResources().getString(R.string.xmpp_notification_label_started), sMessage, sOrderID);
            }
            else if (sAction.equalsIgnoreCase(Iconstant.sJobCompleted_action)) {
                showMessage(context.getResources().getString(R.string.xmpp_notification_label_completed), sMessage, sOrderID);
            }
            else if (sAction.equalsIgnoreCase(Iconstant.sReject_action)) {
                showMessage(context.getResources().getString(R.string.xmpp_notification_label_declined), sMessage, sOrderID);
            }
            else if (sAction.equalsIgnoreCase(Iconstant.sRequestPayment_action)) {
                requestPayment(context.getResources().getString(R.string.xmpp_notification_payment_request), sMessage,sOrderID);
            }
            else if (sAction.equalsIgnoreCase(Iconstant.sPaymentPaid_action)) {
                paymentPaid(context.getResources().getString(R.string.xmpp_notification_label_payment_completed),sMessage,sOrderID);
            }
            else if (sAction.equalsIgnoreCase(Iconstant.Admin_Notification)) {
                showMessage(context.getResources().getString(R.string.admin_notification), sMessage, sOrderID);
            }

        }
        catch (Exception e) {
        }
    }


    private void showMessage(String title, String message, String orderID) {

        Intent finish_PushNotificationAlert = new Intent();
        finish_PushNotificationAlert.setAction("com.package.finish.PushNotificationAlert");
        context.sendBroadcast(finish_PushNotificationAlert);


//        Intent finish_myjobsdetail = new Intent();
//        finish_myjobsdetail.setAction("com.finish.MyjobsDetailsPage");
//        context.sendBroadcast(finish_myjobsdetail);

        Intent intent = new Intent(context, PushNotificationAlert.class);
        intent.putExtra("TITLE_INTENT", title);
        intent.putExtra("MESSAGE_INTENT", message);
        intent.putExtra("ORDER_ID_INTENT", orderID);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        System.out.println("showsocktOrderId----------" + orderID);
    }

    private void requestPayment(String title, String message,String sJobID) {
        sessionManager = new SessionManager(context);

        // get Make Payment Class Open or not from session
        HashMap<String, String> isOpen = sessionManager.getMakePaymentOpen();
        String sIsMakePaymentClassOpen = isOpen.get(SessionManager.KEY_CHECK_MAKE_PAYMENT_CLASS_OPEN);

        if (sIsMakePaymentClassOpen.equalsIgnoreCase("Opened")) {
            Intent refreshBroadcastIntent = new Intent();
            refreshBroadcastIntent.setAction("com.package.refresh.MakePayment");
            context.sendBroadcast(refreshBroadcastIntent);
        } else {
            Intent intent = new Intent(context, PushNotificationAlert.class);
            intent.putExtra("TITLE_INTENT", title);
            intent.putExtra("MESSAGE_INTENT", message);
            intent.putExtra("ORDER_ID_INTENT", sJobID);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);


//            Intent i1 = new Intent(context, PaymentNew.class);
//            i1.putExtra("JobID_INTENT", sJobID);
//            i1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(i1);
        }
    }

    private void paymentPaid(String title, String message, String orderID) {

if(message.equalsIgnoreCase("Your billing amount paid successfully")){
    Intent finishpaymentpageBroadcastIntent = new Intent();
    finishpaymentpageBroadcastIntent.setAction("com.package.finish.PaymentPageDetails");
    context.sendBroadcast(finishpaymentpageBroadcastIntent);

    Intent i1 = new Intent(context, RatingPage.class);
    i1.putExtra("JobID", orderID);
    i1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    context.startActivity(i1);

}

    }
}
