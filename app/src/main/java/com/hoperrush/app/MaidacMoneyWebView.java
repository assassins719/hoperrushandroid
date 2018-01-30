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
 * Casperon Technology on 1/5/2016.
 */
public class MaidacMoneyWebView extends ActivityHockeyApp {
    private ConnectionDetector cd;
    private boolean isInternetPresent = false;
    private SessionManager sessionManager;
    private String UserID = "";

    private RelativeLayout Rl_back;
    private ImageView Im_backIcon;
    private TextView Tv_headerTitle;

    private WebView webview;
    private ProgressBar progressBar;

    private String Str_recharge_amount = "";
    private String Str_currency_symbol = "";
    private String Str_currentBalance = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plumbal_money_webview);
        initializeHeaderBar();
        initialize();

        Rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final PkDialog mDialog = new PkDialog(MaidacMoneyWebView.this);
                mDialog.setDialogTitle(getResources().getString(R.string.plumbal_webView_label_cancel_transaction));
                mDialog.setDialogMessage(getResources().getString(R.string.plumbal_webView_label_cancel_transaction_proceed));
                mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();

                        // close keyboard
                        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        mgr.hideSoftInputFromWindow(Rl_back.getWindowToken(), 0);

                        //----changing button of cabily money page----
                        WalletMoney.changeButton();

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
        cd = new ConnectionDetector(MaidacMoneyWebView.this);
        isInternetPresent = cd.isConnectingToInternet();
        sessionManager = new SessionManager(MaidacMoneyWebView.this);

        webview = (WebView) findViewById(R.id.plumbal_money_webView);
        progressBar = (ProgressBar) findViewById(R.id.plumbal_money_webView_progressbar);

        // Enable Javascript to run in WebView
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setDomStorageEnabled(true);

        // Allow Zoom in/out controls
        webview.getSettings().setBuiltInZoomControls(true);

        // Zoom out the best fit your screen
        webview.getSettings().setLoadWithOverviewMode(true);
        webview.getSettings().setUseWideViewPort(true);


        // get user data from session
        HashMap<String, String> user = sessionManager.getUserDetails();
        UserID = user.get(SessionManager.KEY_USER_ID);

        Intent i = getIntent();
        Str_recharge_amount = i.getStringExtra("cabilyMoney_recharge_amount");
        Str_currency_symbol = i.getStringExtra("cabilyMoney_currency_symbol");
        Str_currentBalance = i.getStringExtra("cabilyMoney_currentBalance");

        startWebView(Iconstant.plumbal_money_webView_url + UserID + "&total_amount=" + Str_recharge_amount);


        System.out.println("UserID----------" + UserID);

        System.out.println("paymentweb----------" + Iconstant.plumbal_money_webView_url + UserID + "&total_amount=" + Str_recharge_amount);

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

                try {

                    System.out.println("url-------------------" + url);
                    if (url.contains("mobile/failed")) {
                        finishMethod();
                    } else if (url.contains("pay-completed/bycard")) {
                        webview.clearHistory();
                        Intent broadcastIntent = new Intent();
                        broadcastIntent.setAction("com.package.ACTION_CLASS_PLUMBAL_MONEY_REFRESH");
                        sendBroadcast(broadcastIntent);
                        alertPaySuccess(getResources().getString(R.string.action_success), getResources().getString(R.string.make_payment_webView_label_transaction_success));

                      //  finishMethod();
                    } else if (url.contains("/wallet-recharge/pay-cancel")) {
                        finishMethod();
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
        WalletMoney.changeButton();
        finish();
        onBackPressed();
        overridePendingTransition(R.anim.enter, R.anim.exit);
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



    private void alertPaySuccess(String title, String alert) {

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("com.package.ACTION_CLASS_MY_JOBS_REFRESH");
        sendBroadcast(broadcastIntent);

        final PkDialog mDialog = new PkDialog(MaidacMoneyWebView.this);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(alert);
        mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                System.out.println("rat----------------");

//                Intent intent = new Intent(MaidacMoneyWebView.this, RatingPage.class);
//                intent.putExtra("JobID", sJobID);
//                startActivity(intent);
//                finish();
//                overridePendingTransition(R.anim.enter, R.anim.exit);


                Intent finishBroadcastIntent = new Intent();
                finishBroadcastIntent.setAction("com.package.finish.MyJobDetails");
                sendBroadcast(finishBroadcastIntent);

                Intent finishPaymentBroadcastIntent = new Intent();
                finishPaymentBroadcastIntent.setAction("com.package.finish.PaymentPageDetails");
                sendBroadcast(finishPaymentBroadcastIntent);
               finish();
            }
        });
        mDialog.show();
    }














    @Override
    public void onResume() {
        super.onResume();
    }

    //-----------------Move Back on pressed phone back button------------------
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)) {


            final PkDialog mDialog = new PkDialog(MaidacMoneyWebView.this);
            mDialog.setDialogTitle(getResources().getString(R.string.plumbal_webView_label_cancel_transaction));
            mDialog.setDialogMessage(getResources().getString(R.string.plumbal_webView_label_cancel_transaction_proceed));
            mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialog.dismiss();

                    // close keyboard
                    InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.hideSoftInputFromWindow(Rl_back.getWindowToken(), 0);

                    //----changing button of cabily money page----
                    WalletMoney.changeButton();

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
