package com.hoperrush.core.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hoperrush.R;


/**
 * Casperon Technology on 1/18/2016.
 */
public class PkNotificationDialog
{
    private Context mContext;
    private Button Bt_action;
    private TextView alert_title, alert_message;
    private Dialog dialog;
    private View view;


    public PkNotificationDialog(Context context) {
        this.mContext = context;

        //--------Adjusting Dialog width-----
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        int screenWidth = (int) (metrics.widthPixels * 0.75);//fill only 75% of the screen

        view = View.inflate(mContext, R.layout.custom_notification_dialog, null);
        dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(screenWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        alert_title = (TextView) view.findViewById(R.id.custom_dialog_notification_library_title_textView);
        alert_message = (TextView) view.findViewById(R.id.custom_dialog_notification_library_message_textView);
        Bt_action = (Button) view.findViewById(R.id.custom_dialog_notification_library_ok_button);
    }


    public void showNotificationMessage() {
        dialog.show();
    }


    public void dismissNotificationMessage() {
        dialog.dismiss();
    }


    public void setNotificationDialogTitle(String title) {
        alert_title.setText(title);
    }


    public void setNotificationDialogMessage(String message) {
        alert_message.setText(message);
    }


    public void setNotificationCancelOnTouchOutside(boolean value) {
        dialog.setCanceledOnTouchOutside(value);
    }


    /*Action Button for Dialog*/
    public void setPositiveButton(String text, final View.OnClickListener listener) {
        Bt_action.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/Roboto-Medium.ttf"));
        Bt_action.setText(text);
        Bt_action.setOnClickListener(listener);
    }

}
