package com.hoperrush.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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

/**
 * Casperon Technology on 1/23/2016.
 */
public class PaymentWebView extends ActivityHockeyApp {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_webview);
        initializeHeaderBar();
        initialize();

        Rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final PkDialog mDialog = new PkDialog(PaymentWebView.this);
                mDialog.setDialogTitle(getResources().getString(R.string.plumbal_webView_label_cancel_transaction));
                mDialog.setDialogMessage(getResources().getString(R.string.plumbal_webView_label_cancel_transaction_proceed));
                mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();

                        // close keyboard
                        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        mgr.hideSoftInputFromWindow(Rl_back.getWindowToken(), 0);

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

        // Show the progress bar
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
    }

    private void initializeHeaderBar() {
        RelativeLayout headerBar = (RelativeLayout) findViewById(R.id.headerBar_noShadow_layout);
        Rl_back = (RelativeLayout) headerBar.findViewById(R.id.headerBar_noShadow_left_layout);
        Im_backIcon = (ImageView) headerBar.findViewById(R.id.headerBar_noShadow_imageView);
        Tv_headerTitle = (TextView) headerBar.findViewById(R.id.headerBar_noShadow_title_textView);

        Tv_headerTitle.setText(getResources().getString(R.string.make_payment_webView_label_payment_transaction));
        Im_backIcon.setImageResource(R.drawable.back_arrow);
    }

    private void initialize() {
        cd = new ConnectionDetector(PaymentWebView.this);
        isInternetPresent = cd.isConnectingToInternet();
        sessionManager = new SessionManager(PaymentWebView.this);

        webview = (WebView) findViewById(R.id.payment_webView_web);
        progressBar = (ProgressBar) findViewById(R.id.payment_webView_progressbar);

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
        sMobileID = i.getStringExtra("MobileID");
        sJobID = i.getStringExtra("JobID");

        startWebView(Iconstant.makePayment_webView_url + sMobileID);

        System.out.println("sMobileID-----------" + sMobileID);

        System.out.println("paymentwebview----------" + Iconstant.makePayment_webView_url + sMobileID);

        System.out.println("paymentwebview--------------" + Iconstant.makePayment_webView_url + sMobileID);

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

            @Override
            public void onPageFinished(WebView view, String url) {

                System.out.println("url--------------" + url);

                try {
                    if (url.contains("failed")) {
                        alertPay(getResources().getString(R.string.action_sorry), getResources().getString(R.string.make_payment_webView_label_transaction_failed));
                    } else if (url.contains("pay-completed")) {
                        alertPaySuccess(getResources().getString(R.string.action_success), getResources().getString(R.string.make_payment_webView_label_transaction_success));
                    } else if (url.contains("pay-cancel")) {
                        alertPay(getResources().getString(R.string.action_sorry), getResources().getString(R.string.make_payment_webView_label_transaction_cancel));
                    } else if (url.contains("payment-process")) {
                        alertPaySuccess(getResources().getString(R.string.action_success), getResources().getString(R.string.make_payment_webView_label_transaction_success));
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        });

        //Load url in webView
        webview.loadUrl(url);
    }

    //--------------Alert Method-----------
    private void alert(String title, String alert) {

        final PkDialog mDialog = new PkDialog(PaymentWebView.this);
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

    //--------------Alert Pay Transaction Method-----------
    private void alertPay(String title, String alert) {

        final PkDialog mDialog = new PkDialog(PaymentWebView.this);
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

    //--------------Alert Pay Transaction Success Method-----------
    private void alertPaySuccess(String title, String alert) {

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("com.package.ACTION_CLASS_MY_JOBS_REFRESH");
        sendBroadcast(broadcastIntent);

        final PkDialog mDialog = new PkDialog(PaymentWebView.this);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(alert);
        mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                System.out.println("rat----------------");

                Intent intent = new Intent(PaymentWebView.this, RatingPage.class);
                intent.putExtra("JobID", sJobID);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.enter, R.anim.exit);


                Intent finishBroadcastIntent = new Intent();
                finishBroadcastIntent.setAction("com.package.finish.MyJobDetails");
                sendBroadcast(finishBroadcastIntent);

                Intent finishPaymentBroadcastIntent = new Intent();
                finishPaymentBroadcastIntent.setAction("com.package.finish.PaymentPageDetails");
                sendBroadcast(finishPaymentBroadcastIntent);
            }
        });
        mDialog.show();
    }

    @Override
    public void onBackPressed() {
        if (webview.canGoBack()) {
            webview.goBack();
        } else {
            // Let the system handle the back button
            super.onBackPressed();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    //-----------------Move Back on pressed phone back button------------------
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)) {

            final PkDialog mDialog = new PkDialog(PaymentWebView.this);
            mDialog.setDialogTitle(getResources().getString(R.string.plumbal_webView_label_cancel_transaction));
            mDialog.setDialogMessage(getResources().getString(R.string.plumbal_webView_label_cancel_transaction_proceed));
            mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialog.dismiss();

                    // close keyboard
                    InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.hideSoftInputFromWindow(Rl_back.getWindowToken(), 0);

                    onBackPressed();
                    finish();
                    overridePendingTransition(R.anim.enter, R.anim.exit);

                }
            });
            mDialog.setNegativeButton(getResources().getString(R.string.action_cancel), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialog.dismiss();
                }
            });
            mDialog.show();

            return true;
        }
        return false;
    }
}
