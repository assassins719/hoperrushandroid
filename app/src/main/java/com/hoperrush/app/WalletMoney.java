package com.hoperrush.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.hoperrush.R;
import com.hoperrush.core.dialog.LoadingDialog;
import com.hoperrush.core.dialog.PkDialog;
import com.hoperrush.core.volley.ServiceRequest;
import com.hoperrush.hockeyapp.ActivityHockeyApp;
import com.hoperrush.iconstant.Iconstant;
import com.hoperrush.utils.ConnectionDetector;
import com.hoperrush.utils.CurrencySymbolConverter;
import com.hoperrush.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
/**
 * Casperon Technology on 1/5/2016.
 */
public class WalletMoney extends ActivityHockeyApp {
    private ConnectionDetector cd;
    private boolean isInternetPresent = false;
    private SessionManager sessionManager;
    private String UserID = "";
    private static Context context;

    private RelativeLayout Rl_back;
    private ImageView Im_backIcon;
    private TextView Tv_headerTitle,empty_text;

    private static TextView Tv_plumbalMoney_current_balance,or_Txt;
    private static Button Bt_plumbalMoney_minimum_amount;
    private static Button Bt_plumbalMoney_maximum_amount;
    private static Button Bt_plumbalMoney_between_amount;
    private Button Bt_add_plumbalMoney;
    private static EditText Et_plumbalMoney_enterAmount;
    private RelativeLayout Rl_current_transaction;

    private ServiceRequest mRequest;
    private LoadingDialog mLoadingDialog;
    private boolean isRechargeAvailable = false;
    private String sAuto_charge_status = "";
    private String sCurrentBalance = "", sMinimum_amt = "", sMaximum_amt = "", sMiddle_amt = "", sCurrencySymbol = "", sCurrency_code = "";
    private Button myPaypalBTN;
    ArrayList<String> payment_list = new ArrayList<>();
    String str_stripe = "",str_paypal = "";
    private String inputLine;


    public class RefreshReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.package.ACTION_CLASS_PLUMBAL_MONEY_REFRESH")) {
                if (isInternetPresent) {
                    postRequest_CabilyMoney(WalletMoney.this, Iconstant.plumbal_money_url);
                }
            }
        }
    }

    private RefreshReceiver refreshReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plumbal_money);
        context = WalletMoney.this;
        initializeHeaderBar();
        initialize();


        Rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // close keyboard
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(Rl_back.getWindowToken(), 0);

                onBackPressed();
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        Rl_current_transaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WalletMoney.this, MaidacMoneyTransaction.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        Et_plumbalMoney_enterAmount.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    CloseKeyboard(Et_plumbalMoney_enterAmount);
                }
                return false;
            }
        });

        Bt_plumbalMoney_minimum_amount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Et_plumbalMoney_enterAmount.setText(sMinimum_amt);
                Bt_plumbalMoney_minimum_amount.setBackgroundColor(getResources().getColor(R.color.app_color_mixing_dark));
                Bt_plumbalMoney_between_amount.setBackground(getResources().getDrawable(R.drawable.grey_border_background));
                Bt_plumbalMoney_maximum_amount.setBackground(getResources().getDrawable(R.drawable.grey_border_background));
                Et_plumbalMoney_enterAmount.setSelection(Et_plumbalMoney_enterAmount.getText().length());
            }
        });

        Bt_plumbalMoney_between_amount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Et_plumbalMoney_enterAmount.setText(sMiddle_amt);
                Bt_plumbalMoney_between_amount.setBackgroundColor(getResources().getColor(R.color.app_color_mixing_dark));
                Bt_plumbalMoney_minimum_amount.setBackground(getResources().getDrawable(R.drawable.grey_border_background));
                Bt_plumbalMoney_maximum_amount.setBackground(getResources().getDrawable(R.drawable.grey_border_background));
                Et_plumbalMoney_enterAmount.setSelection(Et_plumbalMoney_enterAmount.getText().length());
            }
        });

        Bt_plumbalMoney_maximum_amount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Et_plumbalMoney_enterAmount.setText(sMaximum_amt);
                Bt_plumbalMoney_maximum_amount.setBackgroundColor(getResources().getColor(R.color.app_color_mixing_dark));
                Bt_plumbalMoney_minimum_amount.setBackground(getResources().getDrawable(R.drawable.grey_border_background));
                Bt_plumbalMoney_between_amount.setBackground(getResources().getDrawable(R.drawable.grey_border_background));
                Et_plumbalMoney_enterAmount.setSelection(Et_plumbalMoney_enterAmount.getText().length());
            }
        });

        Bt_add_plumbalMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredValue = Et_plumbalMoney_enterAmount.getText().toString();
//                call_psgi();
                if (sMinimum_amt != null && sMinimum_amt.length() > 0) {
                    if (enteredValue.length() == 0) {
                        alert(getResources().getString(R.string.action_error), getResources().getString(R.string.action_loading_plumbal_money_empty_field));
                    } else if (Integer.parseInt(enteredValue) < Integer.parseInt(sMinimum_amt) || Integer.parseInt(enteredValue) > Integer.parseInt(sMaximum_amt)) {
                        alert(getResources().getString(R.string.action_error), getResources().getString(R.string.plumbalMoney_label_rechargeMoney_alert) + " " + sCurrencySymbol + sMinimum_amt + " " + "-" + " " + sCurrencySymbol + sMaximum_amt);
                    } else {
                        cd = new ConnectionDetector(WalletMoney.this);
                        isInternetPresent = cd.isConnectingToInternet();

                        if (isInternetPresent) {
                            if (sAuto_charge_status.equalsIgnoreCase("1")) {
                                postRequest_AddMoney(WalletMoney.this, Iconstant.plumbal_add_money_url);

                                System.out.println("WalletMoney-------------" + Iconstant.plumbal_add_money_url);

                            } else {

                                System.out.println("payrechargrweb---------");
                                Intent intent = new Intent(WalletMoney.this, MaidacMoneyWebView.class);
                                intent.putExtra("cabilyMoney_recharge_amount", Et_plumbalMoney_enterAmount.getText().toString());
                                intent.putExtra("cabilyMoney_currency_symbol", sCurrencySymbol);
                                intent.putExtra("cabilyMoney_currentBalance", sCurrentBalance);
                                startActivity(intent);
                                overridePendingTransition(R.anim.enter, R.anim.exit);
                            }
                        } else {
                            alert(getResources().getString(R.string.action_no_internet_title), getResources().getString(R.string.action_no_internet_message));
                        }
                    }
                }
            }
        });

        myPaypalBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredValue = Et_plumbalMoney_enterAmount.getText().toString();

                if (sMinimum_amt != null && sMinimum_amt.length() > 0) {
                    if (enteredValue.length() == 0) {
                        alert(getResources().getString(R.string.action_error), getResources().getString(R.string.action_loading_plumbal_money_empty_field));
                    } else if (Integer.parseInt(enteredValue) < Integer.parseInt(sMinimum_amt) || Integer.parseInt(enteredValue) > Integer.parseInt(sMaximum_amt)) {
                        alert(getResources().getString(R.string.action_error), getResources().getString(R.string.plumbalMoney_label_rechargeMoney_alert) + " " + sCurrencySymbol + sMinimum_amt + " " + "-" + " " + sCurrencySymbol + sMaximum_amt);
                    } else {
                        cd = new ConnectionDetector(WalletMoney.this);
                        isInternetPresent = cd.isConnectingToInternet();

                        if (isInternetPresent) {
                            postRequest_AddMoney_paypal(WalletMoney.this, Iconstant.plumbal_money_paypal_webView_url);

                            System.out.println("MaidacMoney-------------" + Iconstant.plumbal_add_money_url);

                            System.out.println("payrechargrweb---------");
                        } else {
                            alert(getResources().getString(R.string.action_no_internet_title), getResources().getString(R.string.action_no_internet_message));
                        }
                    }
                }
            }
        });
    }

    private void initializeHeaderBar() {
        RelativeLayout headerBar = (RelativeLayout) findViewById(R.id.headerBar_layout);
        Rl_back = (RelativeLayout) headerBar.findViewById(R.id.headerBar_left_layout);
        Im_backIcon = (ImageView) headerBar.findViewById(R.id.headerBar_imageView);
        Tv_headerTitle = (TextView) headerBar.findViewById(R.id.headerBar_title_textView);

        Tv_headerTitle.setText(getResources().getString(R.string.plumbal_label_header_textView));
        Im_backIcon.setImageResource(R.drawable.back_arrow);
    }

    private void initialize() {
        cd = new ConnectionDetector(WalletMoney.this);
        isInternetPresent = cd.isConnectingToInternet();
        sessionManager = new SessionManager(WalletMoney.this);

        Bt_add_plumbalMoney = (Button) findViewById(R.id.plumbal_money_add_money_button);
        Et_plumbalMoney_enterAmount = (EditText) findViewById(R.id.plumbal_money_enter_amount_editText);
        Bt_plumbalMoney_minimum_amount = (Button) findViewById(R.id.plumbal_money_minimum_amt_button);
        Bt_plumbalMoney_maximum_amount = (Button) findViewById(R.id.plumbal_money_maximum_amt_button);
        Bt_plumbalMoney_between_amount = (Button) findViewById(R.id.plumbal_money_between_amt_button);
        Tv_plumbalMoney_current_balance = (TextView) findViewById(R.id.plumbal_money_your_balance_textView);
        Rl_current_transaction = (RelativeLayout) findViewById(R.id.plumbal_money_current_balance_layout);
        myPaypalBTN = (Button) findViewById(R.id.plumbal_money_add_money_button_paypal);
        or_Txt = (TextView)findViewById(R.id.plumbal_money_add_plumbal_TXT_or);
        empty_text = (TextView)findViewById(R.id.empty_text);
        Et_plumbalMoney_enterAmount.addTextChangedListener(EditorWatcher);

        // get user data from session
        HashMap<String, String> user = sessionManager.getUserDetails();
        UserID = user.get(SessionManager.KEY_USER_ID);

        // -----code to refresh drawer using broadcast receiver-----
        refreshReceiver = new RefreshReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.package.ACTION_CLASS_PLUMBAL_MONEY_REFRESH");
        registerReceiver(refreshReceiver, intentFilter);

        if (isInternetPresent) {
            postRequest_CabilyMoney(WalletMoney.this, Iconstant.plumbal_money_url);
        } else {
            alert(getResources().getString(R.string.action_no_internet_title), getResources().getString(R.string.action_no_internet_message));
        }
    }

    //----------------------Code for TextWatcher-------------------------
    private final TextWatcher EditorWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {

            String strEnteredVal = Et_plumbalMoney_enterAmount.getText().toString();
            if (!strEnteredVal.equals("")) {
                if (Et_plumbalMoney_enterAmount.getText().toString().equals(sMinimum_amt)) {
                    Bt_plumbalMoney_minimum_amount.setBackgroundColor(getResources().getColor(R.color.app_color_mixing_dark));
                    Bt_plumbalMoney_between_amount.setBackground(getResources().getDrawable(R.drawable.grey_border_background));
                    Bt_plumbalMoney_maximum_amount.setBackground(getResources().getDrawable(R.drawable.grey_border_background));
                } else if (Et_plumbalMoney_enterAmount.getText().toString().equals(sMiddle_amt)) {
                    Bt_plumbalMoney_between_amount.setBackgroundColor(getResources().getColor(R.color.app_color_mixing_dark));
                    Bt_plumbalMoney_minimum_amount.setBackground(getResources().getDrawable(R.drawable.grey_border_background));
                    Bt_plumbalMoney_maximum_amount.setBackground(getResources().getDrawable(R.drawable.grey_border_background));
                } else if (Et_plumbalMoney_enterAmount.getText().toString().equals(sMaximum_amt)) {
                    Bt_plumbalMoney_maximum_amount.setBackgroundColor(getResources().getColor(R.color.app_color_mixing_dark));
                    Bt_plumbalMoney_minimum_amount.setBackground(getResources().getDrawable(R.drawable.grey_border_background));
                    Bt_plumbalMoney_between_amount.setBackground(getResources().getDrawable(R.drawable.grey_border_background));
                } else {
                    Bt_plumbalMoney_minimum_amount.setBackground(getResources().getDrawable(R.drawable.grey_border_background));
                    Bt_plumbalMoney_between_amount.setBackground(getResources().getDrawable(R.drawable.grey_border_background));
                    Bt_plumbalMoney_maximum_amount.setBackground(getResources().getDrawable(R.drawable.grey_border_background));
                }
            }
        }
    };

    //--------------Alert Method-----------
    private void alert(String title, String alert) {

        final PkDialog mDialog = new PkDialog(WalletMoney.this);
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

    //--------------Close KeyBoard Method-----------
    private void CloseKeyboard(EditText edittext) {
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(edittext.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }


    public static void changeButton() {
        Et_plumbalMoney_enterAmount.setText("");
        Bt_plumbalMoney_minimum_amount.setBackground(context.getResources().getDrawable(R.drawable.grey_border_background));
        Bt_plumbalMoney_between_amount.setBackground(context.getResources().getDrawable(R.drawable.grey_border_background));
        Bt_plumbalMoney_maximum_amount.setBackground(context.getResources().getDrawable(R.drawable.grey_border_background));
    }


    //-----------------------Plumbal Money Post Request-----------------
    private void postRequest_CabilyMoney(Context mContext, String Url) {
        mLoadingDialog = new LoadingDialog(mContext);
        mLoadingDialog.setLoadingTitle(getResources().getString(R.string.action_loading));
        mLoadingDialog.show();

        System.out.println("-------------WalletMoney Url----------------" + Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", UserID);
        mRequest = new ServiceRequest(mContext);
        mRequest.makeServiceRequest(Url, com.android.volley.Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("-------------WalletMoney Response----------------" + response);

                String sStatus = "";

                try {
                    JSONObject object = new JSONObject(response);
                    sStatus = object.getString("status");
                    sAuto_charge_status = object.getString("auto_charge_status");
                    if (sStatus.equalsIgnoreCase("1")) {
                        JSONObject response_object = object.getJSONObject("response");
                        if (response_object.length() > 0) {
                            sCurrency_code = response_object.getString("currency");
                            sCurrentBalance = response_object.getString("current_balance");
                            sCurrencySymbol = CurrencySymbolConverter.getCurrencySymbol(sCurrency_code);

                            Object check_recharge_boundary_object = response_object.get("recharge_boundary");
                            if (check_recharge_boundary_object instanceof JSONObject) {
                                JSONObject recharge_object = response_object.getJSONObject("recharge_boundary");
                                if (recharge_object.length() > 0) {
                                    sMinimum_amt = recharge_object.getString("min_amount");
                                    sMaximum_amt = recharge_object.getString("max_amount");
                                    sMiddle_amt = recharge_object.getString("middle_amount");
                                    isRechargeAvailable = true;
                                } else {
                                    isRechargeAvailable = false;
                                }
                            } else {
                                isRechargeAvailable = false;
                            }
                        }


                        JSONArray payment_array = object.getJSONArray("Payment");
                        if (payment_array.length()>0) {
                            payment_list.clear();
                            for (int j=0;j<payment_array.length();j++) {
                               JSONObject payment_object = payment_array.getJSONObject(j);
                                String name = payment_object.getString("name");
                                String code = payment_object.getString("code");

                                payment_list.add(name);
                                payment_list.add(code);
                            }
                        }

                    } else {
                        isRechargeAvailable = false;
                    }


                    for (int i = 0; i < payment_list.size(); i++) {
                        String name = payment_list.get(i);
                        String code = payment_list.get(i);
                        if (name.equalsIgnoreCase("Credit card")) {
                            str_stripe = name;
                        } else if (name.equalsIgnoreCase("Pay by PayPal")) {
                            str_paypal = name;
                        }
                    }

                    if (str_stripe.equalsIgnoreCase("Credit card")) {
                        Bt_add_plumbalMoney.setVisibility(View.VISIBLE);
                    } else {
                        Bt_add_plumbalMoney.setVisibility(View.GONE);
                    }


                    if (str_paypal.equalsIgnoreCase("Pay by PayPal")) {
                        myPaypalBTN.setVisibility(View.VISIBLE);
                    } else {
                        myPaypalBTN.setVisibility(View.GONE);
                    }

                    if (Bt_add_plumbalMoney.getVisibility() == View.VISIBLE && myPaypalBTN.getVisibility() == View.VISIBLE) {
                        or_Txt.setVisibility(View.VISIBLE);
                    } else {
                        or_Txt.setVisibility(View.GONE);
                    }

                    if (payment_list.size() != 0) {
                        empty_text.setVisibility(View.GONE);
                    } else {
                        empty_text.setVisibility(View.VISIBLE);
                    }


                    if (sStatus.equalsIgnoreCase("1") && isRechargeAvailable) {
                        sessionManager.createWalletSession(sCurrentBalance, sCurrency_code);
                        NavigationDrawer.navigationNotifyChange();

                        Bt_plumbalMoney_minimum_amount.setText(sCurrencySymbol + sMinimum_amt);
                        Bt_plumbalMoney_maximum_amount.setText(sCurrencySymbol + sMaximum_amt);
                        Bt_plumbalMoney_between_amount.setText(sCurrencySymbol + sMiddle_amt);
                        Tv_plumbalMoney_current_balance.setText(sCurrencySymbol + sCurrentBalance);
                        Et_plumbalMoney_enterAmount.setHint(getResources().getString(R.string.plumbalMoney_label_rechargeMoney_editText_hint) + " " + sCurrencySymbol + sMinimum_amt + " " + "-" + " " + sCurrencySymbol + sMaximum_amt);
                    } else {
                        String sResponse = object.getString("response");
                        alert(getResources().getString(R.string.action_sorry), sResponse);
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                mLoadingDialog.dismiss();
            }

            @Override
            public void onErrorListener() {
                mLoadingDialog.dismiss();
            }
        });
    }


    //-----------------------Plumbal Money Add Post Request-----------------
    private void postRequest_AddMoney(Context mContext, String Url) {
        mLoadingDialog = new LoadingDialog(mContext);
        mLoadingDialog.setLoadingTitle(getResources().getString(R.string.action_processing));
        mLoadingDialog.show();

        System.out.println("-------------Plumbal ADD Money Url----------------" + Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", UserID);
        jsonParams.put("total_amount", Et_plumbalMoney_enterAmount.getText().toString());

        System.out.println("user_id---------------" + UserID);

        System.out.println("total_amount---------------" + Et_plumbalMoney_enterAmount.getText().toString());


        mRequest = new ServiceRequest(mContext);
        mRequest.makeServiceRequest(Url, com.android.volley.Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {
                System.out.println("-------------Plumbal ADD Money Response----------------" + response);

                String sStatus = "", sMessage = "", sWallet_money = "";

                try {
                    JSONObject object = new JSONObject(response);
                    sStatus = object.getString("status");
                    sMessage = object.getString("msg");
                    sWallet_money = object.getString("wallet_amount");

                    if (sStatus.equalsIgnoreCase("1")) {
                        sessionManager.createWalletSession(sWallet_money, sCurrency_code);
                        NavigationDrawer.navigationNotifyChange();

                        alert(getResources().getString(R.string.action_success), getResources().getString(R.string.action_loading_plumbalMoney_transaction_wallet_success));
                        Et_plumbalMoney_enterAmount.setText("");
                        Tv_plumbalMoney_current_balance.setText(sCurrencySymbol + sWallet_money);
                        Bt_plumbalMoney_minimum_amount.setBackground(getResources().getDrawable(R.drawable.grey_border_background));
                        Bt_plumbalMoney_between_amount.setBackground(getResources().getDrawable(R.drawable.grey_border_background));
                        Bt_plumbalMoney_maximum_amount.setBackground(getResources().getDrawable(R.drawable.grey_border_background));
                    } else {
                        alert(getResources().getString(R.string.action_sorry), sMessage);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mLoadingDialog.dismiss();
            }

            @Override
            public void onErrorListener() {
                mLoadingDialog.dismiss();
            }
        });
    }

    //------------------------------------------------------------Add HandyForAll Wallet money via paypal request--------------------------------

    private void postRequest_AddMoney_paypal(Context mContext, String Url) {
        mLoadingDialog = new LoadingDialog(mContext);
        mLoadingDialog.setLoadingTitle(getResources().getString(R.string.action_processing));
        mLoadingDialog.show();

        System.out.println("-------------Plumbal ADD Money Url----------------" + Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", UserID);
        jsonParams.put("total_amount", Et_plumbalMoney_enterAmount.getText().toString());

        System.out.println("user_id---------------" + UserID);

        System.out.println("total_amount---------------" + Et_plumbalMoney_enterAmount.getText().toString());


        mRequest = new ServiceRequest(mContext);
        mRequest.makeServiceRequest(Url, com.android.volley.Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {
                System.out.println("-------------Plumbal ADD Money Response----------------" + response);
                Log.e("paypal response", response);

                String sStatus = "", sMessage = "", sWallet_money = "", aRedirectUrlstr = "";

                try {
                    JSONObject object = new JSONObject(response);
                    sStatus = object.getString("status");

                    if (sStatus.equalsIgnoreCase("1")) {
                        sessionManager.createWalletSession(sWallet_money, sCurrency_code);
                        NavigationDrawer.navigationNotifyChange();
                        aRedirectUrlstr = object.getString("redirectUrl");
                        Intent intent = new Intent(WalletMoney.this, MaidacPaypalMoneyWebView.class);
                        intent.putExtra("REDIRECT_URL", aRedirectUrlstr);
                        startActivity(intent);
                        overridePendingTransition(R.anim.enter, R.anim.exit);


//                        alert(getResources().getString(R.string.action_success), getResources().getString(R.string.action_loading_plumbalMoney_transaction_wallet_success));
//                        Et_plumbalMoney_enterAmount.setText("");
//                        Tv_plumbalMoney_current_balance.setText(sCurrencySymbol + sWallet_money);
//                        Bt_plumbalMoney_minimum_amount.setBackground(getResources().getDrawable(R.drawable.grey_border_background));
//                        Bt_plumbalMoney_between_amount.setBackground(getResources().getDrawable(R.drawable.grey_border_background));
//                        Bt_plumbalMoney_maximum_amount.setBackground(getResources().getDrawable(R.drawable.grey_border_background));
                    } else {
                        alert(getResources().getString(R.string.action_sorry), sMessage);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
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
    }

    //-----------------Move Back on pressed phone back button------------------
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)) {

            // close keyboard
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(Rl_back.getWindowToken(), 0);

            onBackPressed();
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            return true;
        }
        return false;
    }

    @Override
    public void onDestroy() {
        // Unregister the logout receiver
        unregisterReceiver(refreshReceiver);
        super.onDestroy();
    }

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    OkHttpClient client = new OkHttpClient();

    String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .post(body)
                .build();
        okhttp3.Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public void call_psgi(){
//        URL url = null;
//        try {
//            url = new URL("https://staging.psigate.com:27989/Messenger/XMLMessenger");
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
//        HttpURLConnection connection = null;
//        try {
//            connection = (HttpURLConnection)url.openConnection();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        connection.setDoInput(true);
//        connection.setDoOutput(true);
//        PrintWriter out = null;
//        try {
//            out = new PrintWriter(connection.getOutputStream());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        String strData = "";
//        strData = "<Order>\r\n";
//        strData = strData + "<StoreID>teststore</StoreID>\r\n";
//        strData = strData + "<Passphrase>psigate1234</Passphrase>\r\n";
//        strData = strData + "<PaymentType>CC</PaymentType>\r\n";
//        strData = strData + "<Subtotal>10.00</Subtotal>\r\n";
//        strData = strData + "<CardAction>0</CardAction>\r\n";
//        strData = strData + "<CardNumber>4111111111111111</CardNumber>\r\n";
//        strData = strData + "<CardExpYear>02</CardExpYear>\r\n";
//        strData = strData + "<CardExpMonth>18</CardExpMonth>\r\n";
//        strData = strData + "<CardIDNumber>123</CardIDNumber>\r\n";
//        strData = strData + "</Order>";

//        JSONObject json = new JSONObject();
//        try {
//            json.put("orderXML",strData);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        try {
//            String response = post(String.valueOf(url), String.valueOf(json));
//            Log.d("Response",response);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        RequestQueue requestQueue;
//        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
//        Log.d("Reqbody", json.toString());
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, String.valueOf(url), null,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        Log.d("response",response.toString());
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//            }
//        });
//        requestQueue.add(jsonObjectRequest);

        URL url = null;
        try {
            url = new URL("https://dev.psigate.com:7989/Messenger/XMLMessenger");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection)url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        connection.setDoInput(true);
        connection.setDoOutput(true);
        PrintWriter out = null;
        try {
            out = new PrintWriter(connection.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        String strData = "";
        strData = "<Order>\r\n";
        strData = strData + "	<StoreID>teststore055</StoreID>\r\n";
        strData = strData + "   <Passphrase>testpass055</Passphrase>\r\n";
        strData = strData + "	<PaymentType>CC</PaymentType>\r\n";
        strData = strData + "	<TestResult>R</TestResult>\r\n";
        strData = strData + "   <OrderID>Test123</OrderID>\r\n";
        strData = strData + "	<Userid>User1</Userid>\r\n";
        strData = strData + "	<Baddress1>123 Main St.</Baddress1>\r\n";
        strData = strData + "	<Tax1>1</Tax1>\r\n";
        strData = strData + "	<Tax2>2</Tax2>\r\n";
        strData = strData + "	<Tax3>3</Tax3>\r\n";
        strData = strData + "	<Tax4>4</Tax4>\r\n";
        strData = strData + "	<Tax5>5</Tax5>\r\n";
        strData = strData + "	<Shippingtotal>2.00</Shippingtotal>\r\n";
        strData = strData + "	<Subtotal>10.00</Subtotal>\r\n";
        strData = strData + "	<CardAction>0</CardAction>\r\n";
        strData = strData + "	<CardHolderName>Somebody</CardHolderName>\r\n";

        strData = strData + "	<CardNumber>4005550000000019</CardNumber>\r\n";
        strData = strData + "	<CardExpYear>08</CardExpYear>\r\n";
        strData = strData + "	<CardExpMonth>08</CardExpMonth>\r\n";
        strData = strData + "	<TransRefNumber></TransRefNumber>\r\n";
        strData = strData + "	<CustomerIP>216.220.59.201</CustomerIP>\r\n";
        strData = strData + "	<CardIDNumber>999</CardIDNumber>\r\n";
//				strData = strData + "	<CardXid>cardxid</CardXid>\r\n";
//				strData = strData + "	<CardECI>cardeci</CardECI>\r\n";
//				strData = strData + "	<CardCavv>cardcavv</CardCavv>\r\n";
//				strData = strData + "	<CardLevel2PO>cardlevel2po</CardLevel2PO>\r\n";
//				strData = strData + "	<CardLevel2Tax>cardlevel2tax</CardLevel2Tax>\r\n";
//				strData = strData + "	<CardLevel2TaxExempt>cardlevel2taxexempt</CardLevel2TaxExempt>\r\n";
//				strData = strData + "	<CardLevel2ShiptoZip>CardLevel2ShiptoZip</CardLevel2ShiptoZip>\r\n";
//				strData = strData + "	<AuthorizationNumber>123456</AuthorizationNumber>\r\n";	
        //			strData = strData + "	<CardRefNumber>99999</CardRefNumber>\r\n";	

        strData = strData + "	<Item>\r\n";
        strData = strData + "			<ItemID>apple</ItemID>\r\n";
        strData = strData + "			<ItemDescription>delicious apple</ItemDescription>\r\n";
        strData = strData + "			<ItemQty>2</ItemQty>\r\n";
        strData = strData + "			<ItemPrice>1.5</ItemPrice>\r\n";
        strData = strData + "	                  <Option>\r\n";
        strData = strData + "	                      <Colour1>Red</Colour1>\r\n";
        strData = strData + "	                      <Size1>11</Size1>\r\n";
        strData = strData + "	                      <Maker1>PSiGate11</Maker1>\r\n";
        strData = strData + "	                  </Option>\r\n";
        strData = strData + "		</Item>\r\n";
        strData = strData + "		<Item>\r\n";
        strData = strData + "			<ItemID>book</ItemID>\r\n";
        strData = strData + "			<ItemDescription>good book</ItemDescription>\r\n";
        strData = strData + "			<ItemQty>3</ItemQty>\r\n";
        strData = strData + "			<ItemPrice>2.5</ItemPrice>\r\n";
        strData = strData + "	                  <Option>\r\n";
        strData = strData + "	                      <Colour21>Red</Colour21>\r\n";
        strData = strData + "	                      <Size21>21</Size21>\r\n";
        strData = strData + "	                      <Maker21>PSiGate21</Maker21>\r\n";
        strData = strData + "	                  </Option>\r\n";
        strData = strData + "		</Item>\r\n";
        strData = strData + "		<Item>\r\n";
        strData = strData + "			<ItemID>computer</ItemID>\r\n";
        strData = strData + "			<ItemDescription>IBM computer</ItemDescription>\r\n";
        strData = strData + "			<ItemQty>1</ItemQty>\r\n";
        strData = strData + "			<ItemPrice>12</ItemPrice>\r\n";
        strData = strData + "	                  <Option>\r\n";
        strData = strData + "	                      <Colour33>Red</Colour33>\r\n";
        strData = strData + "	                      <Size33>33</Size33>\r\n";
        strData = strData + "	                      <Maker33>PSiGate33</Maker33>\r\n";
        strData = strData + "	                  </Option>\r\n";
        strData = strData + "		</Item>\r\n";

        strData = strData + "</Order>";
        out.println(strData);

        out.flush();
        out.close();

        // code to read response


        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        StringBuffer strReturn = new StringBuffer();

        boolean xxx = false;
        try {
            while ((inputLine = in.readLine()) != null)
            {
                xxx = true;
                strReturn.append("\r\n" + inputLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("strReturn = " + strReturn);
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        connection.disconnect();
    }
}
