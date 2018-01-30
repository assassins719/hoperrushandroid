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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hoperrush.R;


/**
 * Casperon Technology on 11/27/2015.
 */
public class PkDialog {

    private Context mContext;
    private Button Bt_action, Bt_dismiss;
    private TextView alert_title, alert_message;
    private RelativeLayout Rl_button;
    private Dialog dialog;
    private View view;
    private boolean isPositiveAvailable = false;
    private boolean isNegativeAvailable = false;


    public PkDialog(Context context) {
        this.mContext = context;

        //--------Adjusting Dialog width-----
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        int screenWidth = (int) (metrics.widthPixels * 0.80);//fill only 80% of the screen

        view = View.inflate(mContext, R.layout.custom_dialog_library, null);
        dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(screenWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        alert_title = (TextView) view.findViewById(R.id.custom_dialog_library_title_textview);
        alert_message = (TextView) view.findViewById(R.id.custom_dialog_library_message_textview);
        Bt_action = (Button) view.findViewById(R.id.custom_dialog_library_ok_button);
        Bt_dismiss = (Button) view.findViewById(R.id.custom_dialog_library_cancel_button);
        Rl_button = (RelativeLayout) view.findViewById(R.id.custom_dialog_library_button_layout);
    }


    public void show() {

        /*Enable or Disable positive Button*/
        if (isPositiveAvailable) {
            Bt_action.setVisibility(View.VISIBLE);
        } else {
            Bt_action.setVisibility(View.GONE);
        }

        /*Enable or Disable Negative Button*/
        if (isNegativeAvailable) {
            Bt_dismiss.setVisibility(View.VISIBLE);
        } else {
            Bt_dismiss.setVisibility(View.GONE);
        }

        /*Changing color for Button Layout*/
        if (isPositiveAvailable && isNegativeAvailable) {
            Rl_button.setBackgroundColor(mContext.getResources().getColor(R.color.white_color));
        } else {
            Rl_button.setBackgroundColor(mContext.getResources().getColor(R.color.app_color));
        }

        dialog.show();
    }


    public void dismiss() {
        dialog.dismiss();
    }


    public void setDialogTitle(String title) {
        alert_title.setText(title);

        if (title.length() == 0) {
            alert_title.setVisibility(View.GONE);
        } else {
            alert_title.setVisibility(View.VISIBLE);
        }
    }


    public void setDialogMessage(String message) {
        alert_message.setText(message);
    }


    public void setCancelOnTouchOutside(boolean value) {
        dialog.setCanceledOnTouchOutside(value);
    }


    /*Action Button for Dialog*/
    public void setPositiveButton(String text, final View.OnClickListener listener) {
        isPositiveAvailable = true;
        Bt_action.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/Roboto-Medium.ttf"));
        Bt_action.setText(text);
        Bt_action.setOnClickListener(listener);
    }

    public void setNegativeButton(String text, final View.OnClickListener listener) {
        isNegativeAvailable = true;
        Bt_dismiss.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/Roboto-Medium.ttf"));
        Bt_dismiss.setText(text);
        Bt_dismiss.setOnClickListener(listener);
    }

}
