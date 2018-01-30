package com.hoperrush.utils;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;


public class HideSoftKeyboard {
    /**
     * Hide keyboard on touch screen outside keyboard
     *
     * @param aView
     * @param aActivity
     */
    public static void setupUI(View aView, final Activity aActivity) {

        // Set up touch com.cumi.connect.listener for non-text box views to hide keyboard
        if (!(aView instanceof EditText)) {

            aView.setOnTouchListener(new OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {

                    // Hide soft keyboard
                    hideSoftKeyboard(aActivity);
                    return false;
                }

            });
        }

        // If a layout container, iterate over children and seed recursion
        if (aView instanceof ViewGroup) {

            for (int aCount = 0; aCount < ((ViewGroup) aView).getChildCount(); aCount++) {

                View aInnerView = ((ViewGroup) aView).getChildAt(aCount);

                setupUI(aInnerView, aActivity);
            }
        }
    }


    /**
     * Function to hide soft key board
     *
     * @param activity Current Activity
     */
    public static void hideSoftKeyboard(Activity activity) {

        try {
            if (activity.getCurrentFocus() != null) {
                InputMethodManager inputMethodManager = (InputMethodManager) activity
                        .getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(activity
                        .getCurrentFocus().getWindowToken(), 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
