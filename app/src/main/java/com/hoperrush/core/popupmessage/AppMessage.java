package com.hoperrush.core.popupmessage;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;

import com.hoperrush.R;


/**
 * Casperon Technology on 1/18/2016.
 */
public class AppMessage
{
    private Context mContext;
    private PopupWindow popupWindow;
    private View view;

    public AppMessage(Context context) {
        this.mContext = context;
    }

    public void showPopUp()
    {
        view = View.inflate(mContext, R.layout.app_message_layout, null);
        popupWindow = new PopupWindow(view,LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        popupWindow.showAtLocation(view, Gravity.TOP, 10, 10);
        popupWindow.showAsDropDown(view, 50, -30);
    }

    public void dismissPopUp()
    {
        popupWindow.dismiss();
    }
}
