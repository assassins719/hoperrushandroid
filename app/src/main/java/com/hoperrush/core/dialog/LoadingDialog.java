package com.hoperrush.core.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.TextView;

import com.hoperrush.R;


/**
 * Casperon Technology on 12/8/2015.
 */
public class LoadingDialog {
    private Context mContext;
    private Dialog dialog;
    private TextView dialog_title;

    public LoadingDialog(Context context) {
        this.mContext = context;
        dialog = new Dialog(mContext);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
    }

    public void show() {
        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }

    public void setLoadingTitle(String title) {
        dialog_title.setText(title);
    }
}
