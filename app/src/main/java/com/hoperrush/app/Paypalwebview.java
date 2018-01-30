package com.hoperrush.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hoperrush.R;
import com.hoperrush.core.dialog.PkDialog;
import com.hoperrush.hockeyapp.ActivityHockeyApp;
import com.hoperrush.iconstant.Iconstant;
import com.hoperrush.utils.ConnectionDetector;
import com.hoperrush.utils.SessionManager;

import java.util.HashMap;

public class Paypalwebview extends ActivityHockeyApp {
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private SessionManager sessionManager;

    private RelativeLayout Rl_back;
    private ImageView Im_backIcon;
    private TextView Tv_headerTitle;

    private WebView webview;
    private ProgressBar progressBar;

    private String sMobileID = "";
    private String UserID = "";
    private String sJobID = "";
    private String payurl="";
    RelativeLayout backbutton;

    public class RefreshReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.package.finish.paypalwebview")) {
                finish();
            }
        }
    }

    private RefreshReceiver finishReceiver;







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paypalwebview);
        initialize();
        initializeheader();


        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final PkDialog mDialog = new PkDialog(Paypalwebview.this);
                mDialog.setDialogTitle(getResources().getString(R.string.plumbal_webView_label_cancel_transaction));
                mDialog.setDialogMessage(getResources().getString(R.string.plumbal_webView_label_cancel_transaction_proceed));
                mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();

                        // close keyboard
//                        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                        mgr.hideSoftInputFromWindow(Rl_back.getWindowToken(), 0);

                        onBackPressed();
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                        finish();
                    }
                });
                mDialog.setNegativeButton(getResources().getString(R.string.action_cancel), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                    }
                });
                mDialog.show();

            }
        });
    }
    private void initialize() {
        cd = new ConnectionDetector(Paypalwebview.this);
        isInternetPresent = cd.isConnectingToInternet();
        sessionManager = new SessionManager(Paypalwebview.this);

        webview = (WebView) findViewById(R.id.cabily_money_webview);

        progressBar = (ProgressBar) findViewById(R.id.cabily_money_webview_progressbar);

        // Enable Javascript to run in WebView
        webview.getSettings().setJavaScriptEnabled(true);

        // Allow Zoom in/out controls
        webview.getSettings().setBuiltInZoomControls(true);
        webview.getSettings().setDomStorageEnabled(true);

        // Zoom out the best fit your screen
        webview.getSettings().setLoadWithOverviewMode(true);
        webview.getSettings().setUseWideViewPort(true);

        // get user data from session
        HashMap<String, String> user = sessionManager.getUserDetails();
        UserID = user.get(SessionManager.KEY_USER_ID);

        Intent i = getIntent();
        //  sMobileID = i.getStringExtra("MobileID");
        payurl = i.getStringExtra("weburl");
        sJobID=i.getStringExtra("job_id");

        startWebView(payurl);

        System.out.println("sMobileID-----------" + sMobileID);

        System.out.println("paymentwebview----------" + Iconstant.Card_webview_url + sMobileID);

        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress) {
                if (progress < 100 && progressBar.getVisibility() == ProgressBar.GONE) {
                    progressBar.setVisibility(ProgressBar.VISIBLE);
                }
                progressBar.setProgress(progress);

                if (progress == 100) {
                    progressBar.setVisibility(ProgressBar.GONE);
                }
            }
        });

        finishReceiver = new RefreshReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.package.finish.paypalwebview");

        registerReceiver(finishReceiver, intentFilter);
    }

    public void initializeheader(){

        backbutton=(RelativeLayout)findViewById(R.id.cabily_money_webview_header_back_layout);
    }

    private void startWebView(String url) {
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            //Show loader on url load
            @Override
            public void onLoadResource(WebView view, String url) {
            }
//
//            @Override
//            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
//                handler.proceed(); // Ignore SSL certificate errors
//            }

            @Override
            public void onPageFinished(WebView view, String url) {

                System.out.println("------------url-$$$$$------" + url);

                try {

                    if (url.contains("failed")) {

                        alertPay(getResources().getString(R.string.action_sorry), getResources().getString(R.string.make_payment_webView_label_transaction_failed));
                        //finishMethod();
                    } else if (url.contains("paypalsucess")) {
                        //webview.clearHistory();

                        alertPaySuccess(getResources().getString(R.string.action_success), getResources().getString(R.string.make_payment_webView_label_transaction_success));
//                        Intent broadcastIntent = new Intent();
//                        broadcastIntent.setAction("com.package.ACTION_CLASS_CARDLIST_REFRESH");
//                        sendBroadcast(broadcastIntent);
//                        finishMethod();
                    }

                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        });

        //Load url in webView
        webview.loadUrl(url);
    }
    public void finishMethod() {
        //----changing button of cabily money page----
        // Cardlist.changeButton();
        finish();
        onBackPressed();
        overridePendingTransition(R.anim.enter, R.anim.exit);
    }

    private void alertPay(String title, String alert) {

        final PkDialog mDialog = new PkDialog(Paypalwebview.this);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(alert);
        mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                finish();
                onBackPressed();
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });
        mDialog.show();

    }



    private void alertPaySuccess(String title, String alert) {

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("com.package.ACTION_CLASS_MY_JOBS_REFRESH");
        sendBroadcast(broadcastIntent);

        final PkDialog mDialog = new PkDialog(Paypalwebview.this);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(alert);
        mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                Intent intent = new Intent(Paypalwebview.this, RatingPage.class);
                intent.putExtra("JobID", sJobID);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);

                 finish();
//                Intent finishBroadcastIntent = new Intent();
//                finishBroadcastIntent.setAction("com.package.finish.MyJobDetails");
//                sendBroadcast(finishBroadcastIntent);

//                Intent finishPaymentBroadcastIntent = new Intent();
//                finishPaymentBroadcastIntent.setAction("com.package.finish.PaymentPageDetails");
//                sendBroadcast(finishPaymentBroadcastIntent);
            }
        });
        mDialog.show();
    }

}
