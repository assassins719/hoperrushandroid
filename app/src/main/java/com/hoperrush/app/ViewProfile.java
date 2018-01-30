package com.hoperrush.app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.hoperrush.R;
import com.hoperrush.core.dialog.LoadingDialog;
import com.hoperrush.core.dialog.PkDialog;
import com.hoperrush.core.volley.ServiceRequest;
import com.hoperrush.core.widgets.RoundedImageView;
import com.hoperrush.hockeyapp.ActivityHockeyApp;
import com.hoperrush.iconstant.Iconstant;
import com.hoperrush.utils.ConnectionDetector;
import com.hoperrush.utils.SessionManager;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Casperon Technology on 1/22/2016.
 */
public class ViewProfile extends ActivityHockeyApp {
    private ConnectionDetector cd;
    private boolean isInternetPresent = false;
    private SessionManager sessionManager;

    private RelativeLayout Rl_back;
    private TextView Tv_headerTitle;
    private TextView address;
    private TextView Tv_userName, myMobileNumberTXT;
    private RoundedImageView Iv_userImage;
    private RatingBar Rb_userRating;
    private TextView Tv_bio, Tv_category;
    private LinearLayout Ll_chat, Ll_call;
    private String task_id;

    final int PERMISSION_REQUEST_CODE = 111;

    final int PERMISSION_REQUEST_CODES = 222;


    private ServiceRequest mRequest;
    private LoadingDialog mLoadingDialog;
    private boolean isDataAvailable = false;
    private String mTaskerID;

    private String sUserID = "";
    private String sProviderID = "", sJobID = "";
    private String sProviderName = "", sEmail = "", sBio = "", sCategory = "", sRating = "", sUserImage = "", sMobileNumber = "";

    private String Sprovideraddress = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_profile);
        initialize();

        Rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        Ll_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewProfile.this, ChatPage.class);
                intent.putExtra("JobID-Intent", sJobID);
                intent.putExtra("TaskerId", mTaskerID);
                intent.putExtra("TaskId", task_id);

                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        Ll_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (sMobileNumber != null && sMobileNumber.length() > 0) {
                    if (Build.VERSION.SDK_INT >= 23) {
                        // Marshmallow+
                        if (!checkCallPhonePermission() || !checkReadStatePermission()) {
                            requestPermission();
                        } else {
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:" + sMobileNumber));
                            startActivity(callIntent);
                        }
                    } else {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + sMobileNumber));
                        startActivity(callIntent);
                    }

                } else {
                    alert(ViewProfile.this.getResources().getString(R.string.server_lable_header), ViewProfile.this.getResources().getString(R.string.arrived_alert_content1));
                }



             /*   if (sMobileNumber != null && sMobileNumber.length() > 0) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + sMobileNumber));
                    startActivity(callIntent);
                }*/
            }
        });
    }

    private void initialize() {
        cd = new ConnectionDetector(ViewProfile.this);
        isInternetPresent = cd.isConnectingToInternet();
        sessionManager = new SessionManager(ViewProfile.this);
        mRequest = new ServiceRequest(ViewProfile.this);
        address = (TextView) findViewById(R.id.addresstext);
        Rl_back = (RelativeLayout) findViewById(R.id.view_profile_headerBar_left_layout);
        Tv_headerTitle = (TextView) findViewById(R.id.view_profile_headerBar_title_textView);
        Tv_userName = (TextView) findViewById(R.id.view_profile_UserName_TextView);
        Iv_userImage = (RoundedImageView) findViewById(R.id.view_profile_userImageView);
        Rb_userRating = (RatingBar) findViewById(R.id.view_profile_user_ratingBar);
        Tv_bio = (TextView) findViewById(R.id.view_profile_bio_textView);
        myMobileNumberTXT = (TextView) findViewById(R.id.view_profile_TXT_mobilenumber);

        Tv_category = (TextView) findViewById(R.id.view_profile_category_textView);
        Ll_chat = (LinearLayout) findViewById(R.id.view_profile_chat_layout);
        Ll_call = (LinearLayout) findViewById(R.id.view_profile_call_layout);


        // get user data from session
        HashMap<String, String> user = sessionManager.getUserDetails();
        sUserID = user.get(SessionManager.KEY_USER_ID);

        Intent intent = getIntent();
        sProviderID = intent.getStringExtra("PROVIDER_ID_INTENT");
        sJobID = intent.getStringExtra("JobID-Intent");
        mTaskerID = intent.getStringExtra("TaskerId");
        task_id = intent.getStringExtra("TaskId");
        if (isInternetPresent) {
            postRequest_ViewProfile(Iconstant.viewProfile_url);
        } else {
            alert(getResources().getString(R.string.action_no_internet_title), getResources().getString(R.string.action_no_internet_message));
        }
    }


    //------Alert Method-----
    private void alert(String title, String message) {
        final PkDialog mDialog = new PkDialog(ViewProfile.this);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(message);
        mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }


    //-----------------------View Profile Post Request-----------------
    private void postRequest_ViewProfile(String Url) {

        final LoadingDialog mLoading = new LoadingDialog(ViewProfile.this);
        mLoading.setLoadingTitle(getResources().getString(R.string.action_loading));
        mLoading.show();

        System.out.println("-------------ViewProfile Url----------------" + Url);

        System.out.println("-------------ViewProfile user_id----------------" + sUserID);
        System.out.println("-------------ViewProfile provider_id----------------" + sProviderID);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", sUserID);
        jsonParams.put("provider_id", sProviderID);

        mRequest = new ServiceRequest(ViewProfile.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("-------------ViewProfile Response----------------" + response);

                String sStatus = "";
                StringBuilder sb = new StringBuilder();
                try {
                    JSONObject object = new JSONObject(response);
                    sStatus = object.getString("status");
                    if (sStatus.equalsIgnoreCase("1")) {

                        Object check_response_object = object.get("response");
                        if (check_response_object instanceof JSONObject) {

                            JSONObject response_object = object.getJSONObject("response");
                            if (response_object.length() > 0) {

                                sProviderName = response_object.getString("provider_name");
                                sEmail = response_object.getString("email");
                                sBio = response_object.getString("bio");
                                //sCategory = response_object.getString("category").replace("\\n", "<br/>"+"<br/>");
                                sRating = response_object.getString("avg_review");
                                sUserImage = response_object.getString("image");
                                sMobileNumber = response_object.getString("mobile_number");
                                Sprovideraddress = response_object.getString("provider_address");
                                JSONArray jarray = response_object.getJSONArray("category");
                                if (jarray.length() > 0) {
                                    for (int i = 0; i < jarray.length(); i++) {
                                        JSONObject category_Object = jarray.getJSONObject(i);
                                        sCategory = category_Object.getString("name");

                                        sb.append(sCategory);
                                        sb.append(",");
                                    }
                                }


                                isDataAvailable = true;
                            } else {
                                isDataAvailable = false;
                            }
                        } else {
                            isDataAvailable = false;
                        }

                    } else {
                        String sResponse = object.getString("response");
                        alert(getResources().getString(R.string.action_sorry), sResponse);
                    }

                    if (sStatus.equalsIgnoreCase("1") && isDataAvailable) {

                        Tv_headerTitle.setText("");
                        Tv_userName.setText(sProviderName);
                        Tv_bio.setText(sBio);
                        myMobileNumberTXT.setText(sMobileNumber);
                        address.setText(Sprovideraddress);
                        System.out.println("--------------sCategory-----------" + sCategory);

                        Tv_category.setText(Html.fromHtml(sb.toString()));
                        Rb_userRating.setRating(Float.parseFloat(sRating));
                        Picasso.with(ViewProfile.this).load(sUserImage).error(R.drawable.placeholder_icon)
                                .placeholder(R.drawable.placeholder_icon).memoryPolicy(MemoryPolicy.NO_CACHE).fit().into(Iv_userImage);
                    }

                } catch (JSONException e) {
                    mLoading.dismiss();
                    e.printStackTrace();
                }
                mLoading.dismiss();
            }

            @Override
            public void onErrorListener() {
                mLoading.dismiss();
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
    }


    private boolean checkCallPhonePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private boolean checkReadStatePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE}, PERMISSION_REQUEST_CODE);
    }


    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE}, PERMISSION_REQUEST_CODES);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + sMobileNumber));
                    startActivity(callIntent);
                }
                break;


            case PERMISSION_REQUEST_CODES:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + sMobileNumber));
                    startActivity(callIntent);
                }
                break;

        }
    }


    //-----------------Move Back on pressed phone back button------------------
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)) {
            onBackPressed();
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            return true;
        }
        return false;
    }
}
