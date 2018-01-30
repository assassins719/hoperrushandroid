package com.hoperrush.app;

import android.content.Context;

import com.android.volley.Request;
import com.hoperrush.iconstant.Iconstant;
import com.hoperrush.core.volley.ServiceRequest;
import com.hoperrush.utils.SessionManager;

import java.util.HashMap;

/**
 * Casperon Technology on 2/23/2016.
 */
public class ChatAvailabilityCheck {
    private Context context;
    private ServiceRequest mRequest;
    private SessionManager sessionManager;
    private String sMode = "";
    private String sUserID = "";

    public ChatAvailabilityCheck(Context mContext, String mode) {
        this.context = mContext;
        this.sMode = mode;
        sessionManager = new SessionManager(mContext);
        mRequest = new ServiceRequest(mContext);

        // get user data from session
        HashMap<String, String> user = sessionManager.getUserDetails();
        sUserID = user.get(SessionManager.KEY_USER_ID);
    }

    public void postChatRequest() {

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_type", "user");
        jsonParams.put("id", sUserID);
        jsonParams.put("mode", sMode);

        mRequest.makeServiceRequest(Iconstant.chat_availability_url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {
            }
            @Override
            public void onErrorListener() {
            }
        });
    }
}
