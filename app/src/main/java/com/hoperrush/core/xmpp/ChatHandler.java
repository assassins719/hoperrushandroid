package com.hoperrush.core.xmpp;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.hoperrush.app.PaymentPage;
import com.hoperrush.app.PushNotificationAlert;
import com.hoperrush.app.RatingPage;
import com.hoperrush.utils.SessionManager;

import java.util.HashMap;

/**
 * Created by Prem on 11/4/2015.
 */
public class ChatHandler {
    private Context context;
    private IntentService service;
    private SessionManager sessionManager;

    public ChatHandler(Context context, IntentService service) {
        this.context = context;
        this.service = service;
    }

    private void showMessage(String title, String message, String orderID) {

        Intent finish_PushNotificationAlert = new Intent();
        finish_PushNotificationAlert.setAction("com.package.finish.PushNotificationAlert");
        context.sendBroadcast(finish_PushNotificationAlert);

        Intent intent = new Intent(context, PushNotificationAlert.class);
        intent.putExtra("TITLE_INTENT", title);
        intent.putExtra("MESSAGE_INTENT", message);
        intent.putExtra("ORDER_ID_INTENT", orderID);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void requestPayment(String sJobID) {
        sessionManager = new SessionManager(context);

        // get Make Payment Class Open or not from session
        HashMap<String, String> isOpen = sessionManager.getMakePaymentOpen();
        String sIsMakePaymentClassOpen = isOpen.get(SessionManager.KEY_CHECK_MAKE_PAYMENT_CLASS_OPEN);

        if (sIsMakePaymentClassOpen.equalsIgnoreCase("Opened")) {
            Intent refreshBroadcastIntent = new Intent();
            refreshBroadcastIntent.setAction("com.package.refresh.MakePayment");
            context.sendBroadcast(refreshBroadcastIntent);
        } else {
            Intent i1 = new Intent(context, PaymentPage.class);
            i1.putExtra("JobID_INTENT", sJobID);
            i1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i1);
        }
    }

    private void paymentPaid(String sJobID) {
        Intent i1 = new Intent(context, RatingPage.class);
        i1.putExtra("JobID", sJobID);
        i1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i1);
    }
}
