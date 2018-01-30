package com.hoperrush.core.pushnotification;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

public class GCMInitializer implements AppConfig {

	private Context context;
	private GoogleCloudMessaging gcm;
	private String regId;
	private CallBack mCallBack;

	public interface CallBack {
		void onRegisterComplete(String registerationId);

		void onError(String errorMsg);
	}

	public GCMInitializer(Context context, CallBack runnable) {
		this.context = context;
		this.mCallBack = runnable;
	}

	public void init() {
		registerGCM();
	}

	private void registerGCM() {
		gcm = GoogleCloudMessaging.getInstance(context);
		regId = getRegistrationId(context);
		if (TextUtils.isEmpty(regId)) {
			registerInBackground();
		} else {
			if (mCallBack != null) {
				mCallBack.onRegisterComplete(regId);
			}
		}
	}

	private void registerInBackground() {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(context);
					}
					regId = gcm.register(PARSE_APPLICATION_ID);
					msg = "Device registered, registration ID=" + regId;
					storeRegistrationId(context, regId);
				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
					if (mCallBack != null) {
						mCallBack.onError(msg);
					}

				}
				if (msg != null && msg.contains("error")) {
					if (mCallBack != null) {
						mCallBack.onError(msg);
					}
				}
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				if (mCallBack != null) {
					mCallBack.onRegisterComplete(regId);
				}
			}
		}.execute(null, null, null);

	}

	@SuppressLint("NewApi")
	private String getRegistrationId(Context context) {
		final SharedPreferences prefs = context.getSharedPreferences(TAG,Context.MODE_PRIVATE);
		String registrationId = prefs.getString(REG_ID, "");
		if (registrationId.isEmpty()) {
			return "";
		}
		int registeredVersion = prefs.getInt(APP_VERSION, Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion) {
			return "";
		}
		return registrationId;
	}

	private int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	private void storeRegistrationId(Context context, String regId) {
		final SharedPreferences prefs = context.getSharedPreferences(TAG,
				Context.MODE_PRIVATE);
		int appVersion = getAppVersion(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(REG_ID, regId);
		editor.putInt(APP_VERSION, appVersion);
		editor.commit();
	}

}
