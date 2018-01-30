package com.hoperrush.app;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.hoperrush.R;
import com.hoperrush.core.dialog.LoadingDialog;
import com.hoperrush.core.dialog.PkDialog;
import com.hoperrush.core.volley.ServiceRequest;
import com.hoperrush.hockeyapp.ActivityHockeyApp;
import com.hoperrush.iconstant.Iconstant;
import com.hoperrush.utils.ConnectionDetector;
import com.hoperrush.utils.CurrencySymbolConverter;
import com.hoperrush.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Casperon Technology on 1/4/2016.
 */
public class InviteAndEarn extends ActivityHockeyApp implements View.OnClickListener {
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private SessionManager sessionManager;

    private RelativeLayout Rl_back;
    private ImageView Im_backIcon;
    private TextView Tv_headerTitle;

    String Fburl = "";
    final int PERMISSION_REQUEST_CODES = 222;
    private TextView Tv_friends_earn, Tv_you_earn, Tv_referral_code;
    private RelativeLayout Rl_whatsApp, Rl_messenger, Rl_sms, Rl_email, Rl_twitter, Rl_facebook;
    private ServiceRequest mRequest;
    private LoadingDialog mLoadingDialog;

    private RelativeLayout Rl_layout_othersshare;

    private String UserID = "";
    private boolean isDataPresent = false;
    private String sCurrencySymbol = "";
    private String sStatus = "", sFriend_earn_amount = "", sYou_earn_amount = "", sFriends_rides = "", sCurrencyCode = "", sReferral_code = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inviteandearn);
        initializeHeaderBar();
        initialize();

        Rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }

    private void initializeHeaderBar() {
        RelativeLayout headerBar = (RelativeLayout) findViewById(R.id.headerBar_layout);
        Rl_back = (RelativeLayout) headerBar.findViewById(R.id.headerBar_left_layout);
        Im_backIcon = (ImageView) headerBar.findViewById(R.id.headerBar_imageView);
        Tv_headerTitle = (TextView) headerBar.findViewById(R.id.headerBar_title_textView);

        Tv_headerTitle.setText(getResources().getString(R.string.invite_earn_label_shareAndEarn));
        Im_backIcon.setImageResource(R.drawable.back_arrow);
    }

    private void initialize() {
        cd = new ConnectionDetector(InviteAndEarn.this);
        isInternetPresent = cd.isConnectingToInternet();
        sessionManager = new SessionManager(InviteAndEarn.this);

        Rl_whatsApp = (RelativeLayout) findViewById(R.id.invite_earn_whatsApp_layout);
        Rl_messenger = (RelativeLayout) findViewById(R.id.invite_earn_messenger_layout);
        Rl_sms = (RelativeLayout) findViewById(R.id.invite_earn_sms_layout);
        Rl_email = (RelativeLayout) findViewById(R.id.invite_earn_email_layout);
        Rl_twitter = (RelativeLayout) findViewById(R.id.invite_earn_twitter_layout);
        Rl_facebook = (RelativeLayout) findViewById(R.id.invite_earn_facebook_layout);
        Tv_friends_earn = (TextView) findViewById(R.id.invite_earn_friend_earn_textView);
        Tv_you_earn = (TextView) findViewById(R.id.invite_earn_you_earn_textView);
        Tv_referral_code = (TextView) findViewById(R.id.invite_earn_referral_code_textView);
        Rl_layout_othersshare = (RelativeLayout) findViewById(R.id.invite_earn_others_layout);

        Rl_whatsApp.setOnClickListener(this);
        Rl_messenger.setOnClickListener(this);
        Rl_sms.setOnClickListener(this);
        Rl_email.setOnClickListener(this);
        Rl_twitter.setOnClickListener(this);
        Rl_facebook.setOnClickListener(this);
        Rl_layout_othersshare.setOnClickListener(this);

        // get user data from session
        HashMap<String, String> user = sessionManager.getUserDetails();
        UserID = user.get(SessionManager.KEY_USER_ID);

        if (isInternetPresent) {
            displayInvite_Request(InviteAndEarn.this, Iconstant.invite_earn_friends_url);
        } else {
            alert(getResources().getString(R.string.action_no_internet_title), getResources().getString(R.string.action_no_internet_message));
        }
    }

    @Override
    public void onClick(View v) {

        String text = getResources().getString(R.string.invite_earn_label_share_message1) + " " + sCurrencySymbol + "" + sYou_earn_amount + " " + getResources().getString(R.string.invite_earn_label_share_message2) + " " + " " + sCurrencySymbol + "" + sFriend_earn_amount + " " + getResources().getString(R.string.invite_earn_label_share_message3) + " " + '"' + sReferral_code + '"' + " " + "and earn money in your wallet !Download: http://hoperrush.com";
        if (v == Rl_whatsApp) {
            whatsApp_sendMsg(text);
        } else if (v == Rl_messenger) {
            messenger_sendMsg(text);
        } else if (v == Rl_sms) {
            sms_sendMsg(text);
        } else if (v == Rl_email) {
            sendEmail(text);
        } else if (v == Rl_twitter) {
            String twitter_text = getResources().getString(R.string.invite_earn_label_share_message1) + " " + sCurrencySymbol + "" + sYou_earn_amount + " " + getResources().getString(R.string.invite_earn_label_share_message2) + " " + " " + sCurrencySymbol + "" + sFriend_earn_amount + " " + getResources().getString(R.string.invite_earn_label_share_message3) + " " + '"' + sReferral_code + '"' + " " + "and earn money in your wallet !Download: http://hoperrush.com";
            Uri imageUri = null;
            try {
                imageUri = Uri.parse(MediaStore.Images.Media.insertImage(this.getContentResolver(),
                        BitmapFactory.decodeResource(getResources(), R.mipmap.hopericon), null, null));
            } catch (NullPointerException e) {
            }
            shareTwitter(twitter_text, imageUri);
        } else if (v == Rl_facebook) {
            String facebook_text = getResources().getString(R.string.invite_earn_label_share_message1) + " " + sCurrencySymbol + "" + sYou_earn_amount + " " + getResources().getString(R.string.invite_earn_label_share_message2) + " " + " " + sCurrencySymbol + "" + sFriend_earn_amount + " " + getResources().getString(R.string.invite_earn_label_share_message3) + " " + '"' + sReferral_code + '"' + " " + "and earn money in your wallet !Download: http://hoperrush.com";
            Uri imageUri = null;
            try {
                imageUri = Uri.parse(MediaStore.Images.Media.insertImage(this.getContentResolver(),
                        BitmapFactory.decodeResource(getResources(), R.mipmap.hopericon), null, null));
            } catch (NullPointerException e) {
            }
            shareFacebookLink(Fburl);
        } else if (v == Rl_layout_othersshare) {
            shareIt();
        }

    }

    //--------------Alert Method-----------
    private void alert(String title, String alert) {

        final PkDialog mDialog = new PkDialog(InviteAndEarn.this);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(alert);
        mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }


    private void shareIt() {
//sharing implementation here
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Hoper Rush App Invitation");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, getResources().getString(R.string.invite_earn_label_share_message1) + " " + sCurrencySymbol + "" + sYou_earn_amount + " " + getResources().getString(R.string.invite_earn_label_share_message2) + " " + " " + sCurrencySymbol + "" + sFriend_earn_amount + " " + getResources().getString(R.string.invite_earn_label_share_message3) + " " + '"' + sReferral_code + '"' + " " + "and earn money in your wallet !Download: http://hoperrush.com");
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }


    //--------Sending message on WhatsApp Method------
    private void whatsApp_sendMsg(String text) {
        PackageManager pm = InviteAndEarn.this.getPackageManager();
        try {
            Intent waIntent = new Intent(Intent.ACTION_SEND);
            waIntent.setType("text/plain");
            PackageInfo info = pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);  //Check if package exists or not. If not then codein catch block will be called
            waIntent.setPackage("com.whatsapp");
            waIntent.putExtra(Intent.EXTRA_TEXT, text);
            startActivity(Intent.createChooser(waIntent, "Share with"));
        } catch (PackageManager.NameNotFoundException e) {
            alert(getResources().getString(R.string.action_sorry), getResources().getString(R.string.invite_earn_label_whatsApp_not_installed));
        }
    }

    //--------Sending message on Facebook Messenger Method------
    private void messenger_sendMsg(String text) {
        PackageManager pm = InviteAndEarn.this.getPackageManager();
        try {
            Intent waIntent = new Intent(Intent.ACTION_SEND);
            waIntent.setType("text/plain");
            PackageInfo info = pm.getPackageInfo("com.facebook.orca", PackageManager.GET_META_DATA);  //Check if package exists or not. If not then coding catch block will be called
            waIntent.setPackage("com.facebook.orca");
            waIntent.putExtra(Intent.EXTRA_TEXT, text);
            startActivity(Intent.createChooser(waIntent, "Share with"));
        } catch (PackageManager.NameNotFoundException e) {
            alert(getResources().getString(R.string.action_sorry), getResources().getString(R.string.invite_earn_label_messenger_not_installed));
        }
    }

    //--------Sending message on SMS Method------
    private void sms_sendMsg(String text) {
        if (Build.VERSION.SDK_INT >= 23) {
            // Marshmallow+
            if (!checkSmsPermission()) {
                requestPermissionSMS();
            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.putExtra("sms_body", text);
                intent.setData(Uri.parse("smsto:" + ""));
                startActivity(intent);
            }
        } else {
            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
            sendIntent.putExtra("sms_body", text);
            sendIntent.putExtra("address","");
            sendIntent.setType("vnd.android-dir/mms-sms");
            startActivity(sendIntent);
        }
    }


    //----------Sending message on Email Method--------
    protected void sendEmail(String text) {
        String[] TO = {""};
        String[] CC = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Hoper Rush App Invitation");
        emailIntent.putExtra(Intent.EXTRA_TEXT, text);
        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            alert(getResources().getString(R.string.action_sorry), getResources().getString(R.string.invite_earn_label_email_not_installed));
        }
    }


    //----------Share Image and Text on Twitter Method--------
    protected void shareTwitter(String text, Uri image) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_STREAM, image);
        intent.setType("image/jpeg");
        intent.setPackage("com.twitter.android");

        try {
            startActivity(intent);
        } catch (android.content.ActivityNotFoundException ex) {
            alert(getResources().getString(R.string.action_sorry), getResources().getString(R.string.invite_earn_label_twitter_not_installed));
        }
    }

    private void requestPermissionSMS() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, PERMISSION_REQUEST_CODES);
    }
    private boolean checkSmsPermission() {
        int result = ContextCompat.checkSelfPermission(InviteAndEarn.this, Manifest.permission.SEND_SMS);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }



    private void shareFacebookLink(String link) {
        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, link);
        // Intent pacakage1 = getPackageManager().getLaunchIntentForPackage("com.facebook.orca");
        Intent pacakage2 = getPackageManager().getLaunchIntentForPackage("com.facebook.katana");
        Intent pacakage3 = getPackageManager().getLaunchIntentForPackage("com.example.facebook");
        Intent pacakage4 = getPackageManager().getLaunchIntentForPackage("com.facebook.android");
//        if (pacakage1 != null) {
//            intent.setPackage("com.facebook.orca");
//        } else
        if (pacakage2 != null) {
            intent.setPackage("com.facebook.katana");
        } else if (pacakage3 != null) {
            intent.setPackage("com.facebook.facebook");
        } else if (pacakage4 != null) {
            intent.setPackage("com.facebook.android");
        } else {
            intent.setPackage("com.facebook.orca");
        }

        try {
            startActivity(intent);
        } catch (android.content.ActivityNotFoundException ex) {
            alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.invite_earn_label_facebook_not_installed));
        }
    }


    //-----------------------Display Invite Amount Post Request-----------------
    private void displayInvite_Request(Context mContext, String Url) {

        mLoadingDialog = new LoadingDialog(mContext);
        mLoadingDialog.setLoadingTitle(getResources().getString(R.string.action_loading));
        mLoadingDialog.show();

        System.out.println("-------------displayInvite_Request Url----------------" + Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", UserID);
        jsonParams.put("username", "gu");

        mRequest = new ServiceRequest(InviteAndEarn.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("-------------displayInvite_Request Response----------------" + response);

                try {

                    JSONObject object = new JSONObject(response);
                    sStatus = object.getString("status");
                    if (object.length() > 0) {

                        Object check_response_object = object.get("response");
                        if (check_response_object instanceof JSONObject) {
                            JSONObject response_Object = object.getJSONObject("response");
                            if (response_Object.length() > 0) {
                                Object check_details_object = response_Object.get("details");
                                if (check_details_object instanceof JSONObject) {
                                    JSONObject detail_object = response_Object.getJSONObject("details");
                                    if (detail_object.length() > 0) {
                                        sFriend_earn_amount = detail_object.getString("friends_earn_amount");
                                        sYou_earn_amount = detail_object.getString("your_earn_amount");
                                        sFriends_rides = detail_object.getString("your_earn");
                                        sReferral_code = detail_object.getString("referral_code");
                                        sCurrencyCode = detail_object.getString("currency");
                                        Fburl = detail_object.getString("link");

                                        isDataPresent = true;
                                    } else {
                                        isDataPresent = false;
                                    }
                                } else {
                                    isDataPresent = false;
                                }
                            } else {
                                isDataPresent = false;
                            }
                        } else {
                            isDataPresent = false;
                        }
                    } else {
                        isDataPresent = false;
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if (isDataPresent) {
                    sCurrencySymbol = CurrencySymbolConverter.getCurrencySymbol(sCurrencyCode);

                    Tv_friends_earn.setText(getResources().getString(R.string.invite_earn_label_friends_earn) + " " + sCurrencySymbol + "" + sFriend_earn_amount);
                    Tv_you_earn.setText(getResources().getString(R.string.invite_earn_label_friends_ride) + "," + getResources().getString(R.string.invite_earn_label_friend_ride) + " " + sCurrencySymbol + "" + sYou_earn_amount);
                    Tv_referral_code.setText(sReferral_code);
                }
                mLoadingDialog.dismiss();
            }

            @Override
            public void onErrorListener() {
                mLoadingDialog.dismiss();
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        //starting XMPP service
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
