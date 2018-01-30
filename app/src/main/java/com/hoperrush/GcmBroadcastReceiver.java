package com.hoperrush;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.hoperrush.core.pushnotification.GCMNotificationIntentService;

/**
 * Created by user145 on 7/26/2017.
 */
public class GcmBroadcastReceiver extends WakefulBroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {

        System.out.println("-------------received push notification--------------"+intent);

        ComponentName comp = new ComponentName(context.getPackageName(), GCMNotificationIntentService.class.getName());
        startWakefulService(context, (intent.setComponent(comp)));
        //setResultCode(Activity.RESULT_OK);
    }
}
