package com.hoperrush.app;

import android.location.Address;
import android.location.Geocoder;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.hoperrush.R;
import com.hoperrush.core.dialog.PkDialog;
import com.hoperrush.core.dialog.PkLoadingDialog;
import com.hoperrush.core.volley.ServiceRequest;
import com.hoperrush.hockeyapp.ActivityHockeyApp;
import com.hoperrush.iconstant.Iconstant;
import com.hoperrush.utils.ConnectionDetector;
import com.hoperrush.utils.CurrencySymbolConverter;
import com.hoperrush.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class TransactionDetailActivity extends ActivityHockeyApp {
    private String myUsedIdStr = "", myBookingIdStr = "";
    private TextView myHeaderTitleTXT;
    private ImageView myBackIMG;
    private RelativeLayout myBackLAY;
    private boolean isInternetPresent = false;
    private ConnectionDetector myConnectionDetector;
    private RelativeLayout myInternalLAY;
    private String myRefreshStr = "normal", myCurrencySymbol = "";
    private ServiceRequest myRequest;
    private PkLoadingDialog myLoadingDialog;
    private TextView myCategoryNameTXT, myTaskerNameTXT, myAddressTXT, myTotalHourTXT, myPerhourTXT, myHourlyRateTXT,myCoupan_Price,
            myTaskAmountTXT, myServiceTaxTXT, myTotalAmountTXT, myBookingIDTXT, myBookingTaskTimeTXT,myPaymentModeTXT,material_fees;
    private SessionManager mySession;
    private String lat_address="",long_address="",Address="";
    private LinearLayout material_layout,coupan_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_detail);
        initializeHeaderBar();
        classAndWidgetInitialize();
    }

    private void initializeHeaderBar() {
        RelativeLayout headerBar = (RelativeLayout) findViewById(R.id.headerBar_layout);
        myBackLAY = (RelativeLayout) headerBar.findViewById(R.id.headerBar_left_layout);
        myBackIMG = (ImageView) headerBar.findViewById(R.id.headerBar_imageView);
        myHeaderTitleTXT = (TextView) headerBar.findViewById(R.id.headerBar_title_textView);
        myHeaderTitleTXT.setText("View Task Details");
        myBackIMG.setImageResource(R.drawable.back_arrow);
        myBackLAY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }


    private void classAndWidgetInitialize() {
        myConnectionDetector = new ConnectionDetector(TransactionDetailActivity.this);
        myRequest = new ServiceRequest(TransactionDetailActivity.this);
        mySession = new SessionManager(TransactionDetailActivity.this);
        isInternetPresent = myConnectionDetector.isConnectingToInternet();
        myInternalLAY = (RelativeLayout) findViewById(R.id.transaction_detail_noInternet_layout);
        myCategoryNameTXT = (TextView) findViewById(R.id.screen_transaction_detail_TXT_categoryname);
        myTaskerNameTXT = (TextView) findViewById(R.id.screen_transaction_detail_TXT_taskername);
        myAddressTXT = (TextView) findViewById(R.id.screen_transaction_detail_TXT_address);
        myTotalHourTXT = (TextView) findViewById(R.id.screen_transaction_detail_TXT_totalhours);
        myPerhourTXT = (TextView) findViewById(R.id.screen_transaction_detail_TXT_perhour);
        myHourlyRateTXT = (TextView) findViewById(R.id.screen_transaction_detail_TXT_hourlyrate);
        myTaskAmountTXT = (TextView) findViewById(R.id.screen_transaction_detail_TXT_taskamount);
        myServiceTaxTXT = (TextView) findViewById(R.id.screen_transaction_detail_TXT_servicetax);
        myTotalAmountTXT = (TextView) findViewById(R.id.screen_transaction_detail_TXT_totalamount);
        myBookingIDTXT = (TextView) findViewById(R.id.screen_transaction_detail_TXT_bookingid);
        myCoupan_Price = (TextView) findViewById(R.id.screen_transaction_detail_TXT_coupan_price);
        myBookingTaskTimeTXT = (TextView) findViewById(R.id.screen_transaction_detail_TXT_bookingtasktime);
        myPaymentModeTXT = (TextView) findViewById(R.id.screen_transaction_detail_TXT_payment_mode);
        material_fees=(TextView)findViewById(R.id.material_fees);
        coupan_layout = (LinearLayout)findViewById(R.id.coupan_layout);
        HashMap<String, String> aAmountMap = mySession.getWalletDetails();
        String aCurrencyCode = aAmountMap.get(SessionManager.KEY_CURRENCY_CODE);
        myCurrencySymbol = CurrencySymbolConverter.getCurrencySymbol(aCurrencyCode);
        material_layout=(LinearLayout)findViewById(R.id.material_layout);
        getIntentValues();
        getTransactionDetailsData();
    }


    private void getIntentValues() {
        if (getIntent() != null) {
            myUsedIdStr = getIntent().getExtras().getString("UserId");
            myBookingIdStr = getIntent().getExtras().getString("BookingId");
        }
    }

    private void getTransactionDetailsData() {
        if (isInternetPresent) {
            myInternalLAY.setVisibility(View.GONE);
            getDetailData();
        } else {
            // mySwipeLAY.setEnabled(true);
            myInternalLAY.setVisibility(View.VISIBLE);
        }
    }

    private void getDetailData() {
        startLoading();
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", myUsedIdStr);
        jsonParams.put("booking_id", myBookingIdStr);

        myRequest.makeServiceRequest(Iconstant.TRANSACTION_DETAIL_URL, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {

                    @Override
                    public void onCompleteListener(String response) {
                        Log.e("response", response);
                        String sStatus = "";
                        try {
                            JSONObject aObject = new JSONObject(response);
                            sStatus = aObject.getString("status");
                            if (sStatus.equalsIgnoreCase("1")) {
                                JSONObject response_Object = aObject.getJSONObject("response");
                                if (response_Object.length() > 0) {
                                    JSONArray aJobsArray = response_Object.getJSONArray("jobs");
                                    if (aJobsArray.length() > 0) {
                                        JSONObject aArrayObject = aJobsArray.getJSONObject(0);
                                        myBookingIDTXT.setText(aArrayObject.getString("job_id"));
                                        myCategoryNameTXT.setText(aArrayObject.getString("category_name"));
                                        myTaskerNameTXT.setText(aArrayObject.getString("user_name"));
//                                        myAddressTXT.setText(aArrayObject.getString("location"));
                                        lat_address=aArrayObject.getString("location_lat");
                                        long_address=aArrayObject.getString("location_lng");
                                        myTotalHourTXT.setText(aArrayObject.getString("total_hrs"));
                                        myPerhourTXT.setText(myCurrencySymbol+aArrayObject.getString("per_hour"));
                                        myHourlyRateTXT.setText(myCurrencySymbol + aArrayObject.getString("min_hrly_rate"));
                                        myTaskAmountTXT.setText(myCurrencySymbol + aArrayObject.getString("task_amount"));
                                        myServiceTaxTXT.setText(myCurrencySymbol + aArrayObject.getString("service_tax"));
                                        myTotalAmountTXT.setText(myCurrencySymbol + aArrayObject.getString("total_amount"));
                                        myBookingTaskTimeTXT.setText(aArrayObject.getString("booking_time"));
                                        myPaymentModeTXT.setText(aArrayObject.getString("payment_mode"));
                                        if(aArrayObject.has("material_fee")){
                                            if (!aArrayObject.getString("material_fee").equalsIgnoreCase("")) {
                                                material_layout.setVisibility(View.VISIBLE);
                                                material_fees.setText(myCurrencySymbol + aArrayObject.getString("material_fee"));
                                            } else {
                                                material_layout.setVisibility(View.VISIBLE);
                                                material_fees.setText("---");
                                            }
                                        }
                                        else{
                                            material_layout.setVisibility(View.GONE);
                                            material_fees.setText("---");
                                        }


                                        if(aArrayObject.has("coupon_amount")) {
                                            if (!aArrayObject.getString("coupon_amount").equalsIgnoreCase("")) {
                                                coupan_layout.setVisibility(View.VISIBLE);
                                                myCoupan_Price.setText(myCurrencySymbol + aArrayObject.getString("coupon_amount"));
                                            } else {
                                                coupan_layout.setVisibility(View.GONE);
                                            }
                                        } else {
                                            coupan_layout.setVisibility(View.GONE);
                                        }





                                        if(!lat_address.equalsIgnoreCase("")&&!long_address.equalsIgnoreCase("")){
                                            Address=getCompleteAddressString(Double.parseDouble(lat_address),Double.parseDouble(long_address));
                                            myAddressTXT.setText(Address);
                                        }else{
                                            myAddressTXT.setText("---");
                                        }
                                    }

                                }
                            } else {
                                myBookingIDTXT.setText("---");
                                myCategoryNameTXT.setText("---");
                                myTaskerNameTXT.setText("---");
                                myAddressTXT.setText("---");
                                myTotalHourTXT.setText("---");
                                myPerhourTXT.setText("---");
                                myHourlyRateTXT.setText("---");
                                myTaskAmountTXT.setText("---");
                                myServiceTaxTXT.setText("---");
                                myTotalAmountTXT.setText("---");
                                myBookingTaskTimeTXT.setText("---");
                                myPaymentModeTXT.setText("---");
                                String sResponse = aObject.getString("response");
                                alert(getResources().getString(R.string.action_sorry), sResponse);
                            }
                            // loadInfoData(myTransactionInfoList);
                            stopLoading();
                        } catch (JSONException e) {
                            stopLoading();
                            e.printStackTrace();
                        }
                    }


                    @Override
                    public void onErrorListener() {
                        stopLoading();
                    }
                }

        );
    }

    private void startLoading() {
        if (myRefreshStr.equalsIgnoreCase("normal")) {
            myLoadingDialog = new PkLoadingDialog(TransactionDetailActivity.this);
            myLoadingDialog.show();
        } else {
            //  mySwipeLAY.setRefreshing(true);
        }
    }

    private void stopLoading() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (myRefreshStr.equalsIgnoreCase("normal")) {
                    myLoadingDialog.dismiss();
                } else {
                    //     mySwipeLAY.setRefreshing(false);
                }
            }
        }, 250);
    }

    private void alert(String title, String message) {
        final PkDialog mDialog = new PkDialog(TransactionDetailActivity.this);
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

    //-------------Method to get Complete Address------------
    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(TransactionDetailActivity.this, Locale.getDefault());
        try {
            List<android.location.Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
            } else {
                Log.e("Current loction address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Current loction address", "Canont get Address!");
        }
        return strAdd;
    }

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
