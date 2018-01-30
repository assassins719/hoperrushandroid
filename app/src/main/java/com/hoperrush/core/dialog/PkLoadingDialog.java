package com.hoperrush.core.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;

import com.hoperrush.R;


/**
 * Casperon Technology on 1/13/2016.
 */
public class PkLoadingDialog
{
    private Context mContext;
    private Dialog dialog;

    public PkLoadingDialog(Context context) {
        this.mContext = context;

        dialog = new Dialog(mContext);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.pk_loading_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }

    public void show() {
        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }
}
