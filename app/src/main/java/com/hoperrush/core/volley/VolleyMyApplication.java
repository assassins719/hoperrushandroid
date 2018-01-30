package com.hoperrush.core.volley;

import android.app.Application;
import android.content.Context;

public class VolleyMyApplication extends Application 
{
    private static VolleyMyApplication mInstance;
    private static Context mAppContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        this.setAppContext(getApplicationContext());
    }

    public static VolleyMyApplication getInstance(){
        return mInstance;
    }
    public static Context getAppContext() {
        return mAppContext;
    }
    public void setAppContext(Context mAppContext) {
        VolleyMyApplication.mAppContext = mAppContext;
    }
}
