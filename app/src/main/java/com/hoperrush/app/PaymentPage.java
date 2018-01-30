package com.hoperrush.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;
import com.hoperrush.R;
import com.hoperrush.adapter.PaymentListAdapter;
import com.hoperrush.core.dialog.LoadingDialog;
import com.hoperrush.core.dialog.PkDialog;
import com.hoperrush.core.volley.ServiceRequest;
import com.hoperrush.core.widgets.CircularImageView;
import com.hoperrush.iconstant.Iconstant;
import com.hoperrush.pojo.PaymentListPojo;
import com.hoperrush.utils.ConnectionDetector;
import com.hoperrush.utils.CurrencySymbolConverter;
import com.hoperrush.utils.SessionManager;
import com.hoperrush.utils.SubClassActivity;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Casperon Technology on 1/23/2016.
 */
public class PaymentPage extends SubClassActivity {
    private ConnectionDetector cd;
    private boolean isInternetPresent = false;
    private SessionManager sessionManager;

    private RelativeLayout Rl_back;
    private ServiceRequest mRequest;
    private LoadingDialog mLoadingDialog;
    private String sUserID = "", sJobID = "";

    ArrayList<PaymentListPojo> itemList;
    private boolean isPaymentAvailable = false;
    private boolean isInfoAvailable = false;
    private PaymentListAdapter adapter;

    private CircularImageView Iv_plumberImage;
    private TextView Tv_totalAmount, Tv_jobDate, Tv_jobTime;
    private ExpandableHeightListView listView;

    private String sProviderImage = "", sTotalAmount = "", sJobDate = "", sJobTime = "", sCurrencyCode = "";
    private String sPaymentCode = "";

    public class RefreshReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.package.finish.PaymentPageDetails")) {
                finish();
            } else if (intent.getAction().equals("com.package.refresh.MakePayment")) {
                if (isInternetPresent) {
                    if (mRequest != null) {
                        mRequest.cancelRequest();
                        mLoadingDialog.dismiss();
                    }
                    postRequest_PaymentList(Iconstant.paymentList_url);
                }
            }
        }
    }

    private RefreshReceiver finishReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.paymentlist_page);
        initialize();

        Rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cd = new ConnectionDetector(PaymentPage.this);
                isInternetPresent = cd.isConnectingToInternet();

                if (isInternetPresent) {
                    if (itemList.get(position).getPaymentCode().equalsIgnoreCase("cash")) {
                        MakePayment_Cash(Iconstant.makePayment_cash_url);
                    } else if (itemList.get(position).getPaymentCode().equalsIgnoreCase("wallet")) {
                        MakePayment_Wallet(Iconstant.makePayment_wallet_url);
                    } else if (itemList.get(position).getPaymentCode().equalsIgnoreCase("auto_detect")) {
                        MakePayment_Stripe(Iconstant.makePayment_autoDetect_url);
                    } else {
                        sPaymentCode = itemList.get(position).getPaymentCode();
                        MakePayment_WebView_MobileID(Iconstant.makePayment_Get_webView_mobileId_url);
                    }
                } else {
                    alert(getResources().getString(R.string.action_no_internet_title), getResources().getString(R.string.action_no_internet_message));
                }
            }
        });
    }

    private void initialize() {
        cd = new ConnectionDetector(PaymentPage.this);
        isInternetPresent = cd.isConnectingToInternet();
        sessionManager = new SessionManager(PaymentPage.this);
        mRequest = new ServiceRequest(PaymentPage.this);
        itemList = new ArrayList<PaymentListPojo>();

        Rl_back = (RelativeLayout) findViewById(R.id.make_payment_headerBar_left_layout);
        Iv_plumberImage = (CircularImageView) findViewById(R.id.make_payment_profile_ImageView);
        Tv_totalAmount = (TextView) findViewById(R.id.make_payment_totalAmount_textView);
        Tv_jobDate = (TextView) findViewById(R.id.make_payment_job_date_textView);
        Tv_jobTime = (TextView) findViewById(R.id.make_payment_job_time_textView);
        listView = (ExpandableHeightListView) findViewById(R.id.make_payment_listView);

        // get user data from session
        HashMap<String, String> user = sessionManager.getUserDetails();
        sUserID = user.get(SessionManager.KEY_USER_ID);


        // -----code to refresh drawer using broadcast receiver-----
        finishReceiver = new RefreshReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.package.finish.PaymentPageDetails");
        intentFilter.addAction("com.package.refresh.MakePayment");
        registerReceiver(finishReceiver, intentFilter);


        Intent intent = getIntent();
        sJobID = intent.getStringExtra("JobID_INTENT");

        if (isInternetPresent) {
            postRequest_PaymentList(Iconstant.paymentList_url);
        } else {
            alert(getResources().getString(R.string.action_no_internet_title), getResources().getString(R.string.action_no_internet_message));
        }
    }

    //------Alert Method-----
    private void alert(String title, String message) {
        final PkDialog mDialog = new PkDialog(PaymentPage.this);
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

    private void startLoading() {
        mLoadingDialog = new LoadingDialog(PaymentPage.this);
        mLoadingDialog.setLoadingTitle(getResources().getString(R.string.action_processing));
        mLoadingDialog.show();
    }

    private void stopLoading() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mLoadingDialog.dismiss();
            }
        }, 500);
    }


    //-----------------------PaymentList Post Request-----------------
    private void postRequest_PaymentList(String Url) {
        startLoading();

        System.out.println("-------------PaymentList Url----------------" + Url);

        System.out.println("-------------PaymentList user_id----------------" + sUserID);
        System.out.println("-------------PaymentList job_id----------------" + sJobID);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", sUserID);
        jsonParams.put("job_id", sJobID);

        mRequest = new ServiceRequest(PaymentPage.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("-------------PaymentList Response----------------" + response);

                String sStatus = "";
                try {
                    JSONObject object = new JSONObject(response);
                    sStatus = object.getString("status");
                    if (sStatus.equalsIgnoreCase("1")) {

                        Object check_response_object = object.get("response");
                        if (check_response_object instanceof JSONObject) {

                            JSONObject response_object = object.getJSONObject("response");
                            if (response_object.length() > 0) {

                                Object check_info_object = response_object.get("info");
                                if (check_info_object instanceof JSONObject) {

                                    JSONObject info_object = response_object.getJSONObject("info");
                                    if (info_object.length() > 0) {

                                        sProviderImage = info_object.getString("user_image");
                                        sTotalAmount = info_object.getString("payment_amount");
                                        sJobDate = info_object.getString("job_date");
                                        sJobTime = info_object.getString("job_time");
                                        sCurrencyCode = info_object.getString("currency");
                                        sJobID = info_object.getString("job_id");

                                        isInfoAvailable = true;
                                    } else {
                                        isInfoAvailable = false;
                                    }
                                } else {
                                    isInfoAvailable = false;
                                }


                                Object check_payment_object = response_object.get("payment");
                                if (check_payment_object instanceof JSONArray) {
                                    JSONArray payment_array = response_object.getJSONArray("payment");
                                    if (payment_array.length() > 0) {
                                        itemList.clear();
                                        for (int i = 0; i < payment_array.length(); i++) {
                                            JSONObject reason_object = payment_array.getJSONObject(i);
                                            PaymentListPojo pojo = new PaymentListPojo();
                                            pojo.setPaymentName(reason_object.getString("name"));
                                            pojo.setPaymentCode(reason_object.getString("code"));

                                            itemList.add(pojo);
                                        }
                                        isPaymentAvailable = true;
                                    } else {
                                        isPaymentAvailable = false;
                                    }
                                } else {
                                    isPaymentAvailable = false;
                                }

                            }
                        } else {
                            isInfoAvailable = false;
                            isPaymentAvailable = false;
                        }
                    } else {
                        String sResponse = object.getString("response");
                        alert(getResources().getString(R.string.action_sorry), sResponse);
                    }

                    if (sStatus.equalsIgnoreCase("1")) {

                        if (isPaymentAvailable) {
                            adapter = new PaymentListAdapter(PaymentPage.this, itemList);
                            listView.setAdapter(adapter);
                            listView.setExpanded(true);
                        }

                        if (isInfoAvailable) {

                            String currencySymbol = CurrencySymbolConverter.getCurrencySymbol(sCurrencyCode);
                            Tv_totalAmount.setText(currencySymbol + sTotalAmount);
                            Tv_jobDate.setText(sJobDate);
                            Tv_jobTime.setText(sJobTime);

                            System.out.println("------------sProviderImage------------" + sProviderImage);
                            Picasso.with(PaymentPage.this).load(sProviderImage).memoryPolicy(MemoryPolicy.NO_CACHE).fit().into(Iv_plumberImage);
                        }
                    }

                } catch (JSONException e) {
                    stopLoading();
                    e.printStackTrace();
                }
                stopLoading();
            }

            @Override
            public void onErrorListener() {
                stopLoading();
            }
        });
    }


    //-----------------------MakePayment Cash Post Request-----------------
    private void MakePayment_Cash(String Url) {
        startLoading();

        System.out.println("-------------MakePayment Cash Url----------------" + Url);
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", sUserID);
        jsonParams.put("job_id", sJobID);

        System.out.println("user_id------------" + sUserID);
        System.out.println("job_id------------" + sJobID);

        mRequest = new ServiceRequest(PaymentPage.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("-------------MakePayment Cash Response----------------" + response);

                String sStatus = "";
                try {
                    JSONObject object = new JSONObject(response);
                    sStatus = object.getString("status");
                    if (sStatus.equalsIgnoreCase("1")) {

                        Intent broadcastIntent = new Intent();
                        broadcastIntent.setAction("com.package.ACTION_CLASS_MY_JOBS_REFRESH");
                        sendBroadcast(broadcastIntent);

                        Intent broadcast_MyJobsDetail_Intent = new Intent();
                        broadcast_MyJobsDetail_Intent.setAction("com.package.finish.MyJobDetails");
                        sendBroadcast(broadcast_MyJobsDetail_Intent);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                stopLoading();

                                final PkDialog mDialog = new PkDialog(PaymentPage.this);
                                mDialog.setDialogTitle(getResources().getString(R.string.view_profile_sucssslabel_call));
                                mDialog.setDialogMessage(getResources().getString(R.string.make_payment_cash_plumber_confirm_label));
                                mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mDialog.dismiss();
                                        finish();

                                        Intent finishBroadcastIntent = new Intent();
                                        finishBroadcastIntent.setAction("com.package.finish.MyJobDetails");
                                        sendBroadcast(finishBroadcastIntent);

                                        Intent finishpaymentpageBroadcastIntent = new Intent();
                                        finishpaymentpageBroadcastIntent.setAction("com.finish.PaymentPage");
                                        sendBroadcast(finishpaymentpageBroadcastIntent);

                                        // onBackPressed();
                                        Intent intent = new Intent(PaymentPage.this, RatingPage.class);
                                        intent.putExtra("JobID", sJobID);
                                        startActivity(intent);
                                        finish();
                                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                    }
                                });
                                mDialog.show();

                            }
                        }, 2000);

                    } else {
                        stopLoading();
                        String sResponse = object.getString("response");
                        alert(getResources().getString(R.string.action_sorry), sResponse);
                    }

                } catch (JSONException e) {
                    stopLoading();
                    e.printStackTrace();
                }
            }

            @Override
            public void onErrorListener() {
                stopLoading();
            }
        });
    }


    //-----------------------MakePayment Wallet Post Request-----------------

    private void MakePayment_Wallet(String Url) {
        startLoading();

        System.out.println("-------------MakePayment Wallet Url----------------" + Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", sUserID);
        jsonParams.put("job_id", sJobID);

        mRequest = new ServiceRequest(PaymentPage.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("-------------MakePayment Wallet Response----------------" + response);

                String sStatus = "", sCurrency_code = "", sCurrent_wallet_balance = "";
                try {
                    JSONObject object = new JSONObject(response);
                    sStatus = object.getString("status");
                    if (sStatus.equalsIgnoreCase("0")) {
                        stopLoading();
                        alert(getResources().getString(R.string.action_sorry), getResources().getString(R.string.make_payment_empty_wallet));
                    } else if (sStatus.equalsIgnoreCase("1")) {

                        //Updating wallet amount on Navigation Drawer Slide
                        sCurrency_code = object.getString("currency");
                        sCurrent_wallet_balance = object.getString("wallet_amount");

                        sessionManager.createWalletSession(sCurrent_wallet_balance, sCurrency_code);
                        NavigationDrawer.navigationNotifyChange();

                        Intent broadcastIntent = new Intent();
                        broadcastIntent.setAction("com.package.ACTION_CLASS_MY_JOBS_REFRESH");
                        sendBroadcast(broadcastIntent);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                stopLoading();

                                final PkDialog mDialog = new PkDialog(PaymentPage.this);
                                mDialog.setDialogTitle(getResources().getString(R.string.action_success));
                                mDialog.setDialogMessage(getResources().getString(R.string.make_payment_wallet_success));
                                mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mDialog.dismiss();
                                        finish();

                                        Intent finishBroadcastIntent = new Intent();
                                        finishBroadcastIntent.setAction("com.package.finish.MyJobDetails");
                                        sendBroadcast(finishBroadcastIntent);
/*
                                        Intent intent = new Intent(PaymentPage.this, RatingPage.class);
                                        intent.putExtra("JobID", sJobID);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.enter, R.anim.exit);*/
                                    }
                                });
                                mDialog.show();

                            }
                        }, 2000);

                    } else if (sStatus.equalsIgnoreCase("2")) {
                        //Updating wallet amount on Navigation Drawer Slide
                        sCurrency_code = object.getString("currency");
                        sCurrent_wallet_balance = object.getString("wallet_amount");

                        sessionManager.createWalletSession(sCurrent_wallet_balance, sCurrency_code);
                        NavigationDrawer.navigationNotifyChange();

                        Intent broadcastIntent = new Intent();
                        broadcastIntent.setAction("com.package.ACTION_CLASS_MY_JOBS_REFRESH");
                        sendBroadcast(broadcastIntent);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                stopLoading();

                                final PkDialog mDialog = new PkDialog(PaymentPage.this);
                                mDialog.setDialogTitle(getResources().getString(R.string.action_success));
                                mDialog.setDialogMessage(getResources().getString(R.string.make_payment_wallet_success));
                                mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mDialog.dismiss();
                                        postRequest_PaymentList(Iconstant.paymentList_url);
                                    }
                                });
                                mDialog.show();

                            }
                        }, 2000);

                    } else {
                        stopLoading();
                        String sResponse = object.getString("response");
                        alert(getResources().getString(R.string.action_sorry), sResponse);
                    }

                } catch (JSONException e) {
                    stopLoading();
                    e.printStackTrace();
                }
            }

            @Override
            public void onErrorListener() {
                stopLoading();
            }
        });
    }


    //-----------------------MakePayment Auto-Detect Post Request-----------------

    private void MakePayment_Stripe(String Url) {
        startLoading();

        System.out.println("-------------MakePayment Auto-Detect Url----------------" + Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", sUserID);
        jsonParams.put("job_id", sJobID);

        mRequest = new ServiceRequest(PaymentPage.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("-------------MakePayment Auto-Detect Response----------------" + response);

                String sStatus = "";
                try {
                    JSONObject object = new JSONObject(response);
                    sStatus = object.getString("status");
                    if (sStatus.equalsIgnoreCase("1")) {

                        Intent broadcastIntent = new Intent();
                        broadcastIntent.setAction("com.package.ACTION_CLASS_MY_JOBS_REFRESH");
                        sendBroadcast(broadcastIntent);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                stopLoading();

                                final PkDialog mDialog = new PkDialog(PaymentPage.this);
                                mDialog.setDialogTitle(getResources().getString(R.string.action_success));
                                mDialog.setDialogMessage(getResources().getString(R.string.make_payment_cash_success));
                                mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mDialog.dismiss();
                                        finish();

                                        Intent finishBroadcastIntent = new Intent();
                                        finishBroadcastIntent.setAction("com.package.finish.MyJobDetails");
                                        sendBroadcast(finishBroadcastIntent);
/*
                                        Intent intent = new Intent(PaymentPage.this, RatingPage.class);
                                        intent.putExtra("JobID", sJobID);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.enter, R.anim.exit);*/
                                    }
                                });
                                mDialog.show();

                            }
                        }, 2000);

                    } else {
                        stopLoading();
                        String sResponse = object.getString("response");
                        alert(getResources().getString(R.string.action_sorry), sResponse);
                    }

                } catch (JSONException e) {
                    stopLoading();
                    e.printStackTrace();
                }
            }

            @Override
            public void onErrorListener() {
                stopLoading();
            }
        });
    }


    //-----------------------MakePayment WebView-MobileID Post Request-----------------

    private void MakePayment_WebView_MobileID(String Url) {
        startLoading();

        System.out.println("-------------MakePayment WebView-MobileID Url----------------" + Url);
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", sUserID);
        jsonParams.put("job_id", sJobID);
        jsonParams.put("gateway", sPaymentCode);

        mRequest = new ServiceRequest(PaymentPage.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("-------------MakePayment WebView-MobileID Response----------------" + response);

                String sStatus = "";
                try {
                    JSONObject object = new JSONObject(response);
                    sStatus = object.getString("status");
                    if (sStatus.equalsIgnoreCase("1")) {
                        String mobileId = object.getString("mobile_id");
                        Intent intent = new Intent(PaymentPage.this, PaymentWebView.class);
                        intent.putExtra("MobileID", mobileId);
                        intent.putExtra("JobID", sJobID);
                        startActivity(intent);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                    } else {
                        String sResponse = object.getString("response");
                        alert(getResources().getString(R.string.action_sorry), sResponse);
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                stopLoading();
            }

            @Override
            public void onErrorListener() {
                stopLoading();
            }
        });
    }


    @Override
    public void onDestroy() {
        // Unregister the logout receiver
        unregisterReceiver(finishReceiver);
        super.onDestroy();
        sessionManager.setMakePaymentOpen("Closed");
    }

    @Override
    public void onResume() {
        super.onResume();
        sessionManager.setMakePaymentOpen("Opened");
    }

    @Override
    protected void onPause() {
        super.onPause();
        sessionManager.setMakePaymentOpen("Closed");
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
