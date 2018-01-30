package com.hoperrush.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.hoperrush.R;
import com.hoperrush.adapter.TransactionListAdapter;
import com.hoperrush.app.TransactionDetailActivity;
import com.hoperrush.core.dialog.PkDialog;
import com.hoperrush.core.dialog.PkLoadingDialog;
import com.hoperrush.core.volley.ServiceRequest;
import com.hoperrush.hockeyapp.FragmentHockeyApp;
import com.hoperrush.iconstant.Iconstant;
import com.hoperrush.pojo.TransactionPojoInfo;
import com.hoperrush.utils.ConnectionDetector;
import com.hoperrush.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;

public class MyTaskTransaction extends FragmentHockeyApp {

    private TextView myHeaderTitleTXT;
    private ImageView myBackIMG;
    private RelativeLayout myBackLAY;
    private boolean isInternetPresent = false;
    private ConnectionDetector myConnectionDetector;
    private Receiver myReceiver;
    private PkLoadingDialog myLoadingDialog;
    private ServiceRequest myRequest;
    private SessionManager mySession;
    private String myUserIdStr = "";
    private String myRefreshStr = "normal";
    private WaveSwipeRefreshLayout mySwipeLAY;
    private ArrayList<TransactionPojoInfo> myTransactionInfoList;
    private TransactionListAdapter myAdapter;
    private ListView myListview;
    private TextView myEmptyTXT;
    private Context myContext;
    private RelativeLayout myInternalLAY;

    private class Receiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("com.refresh.message")) {
                if (isInternetPresent) {
                    getData();
                }
            }
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.activity_my_task_transaction, container, false);
        init(rootview);
        return rootview;

    }

    private void init(View aView) {
        myContext = getActivity();
        myConnectionDetector = new ConnectionDetector(getActivity());
        mySession = new SessionManager(getActivity());
        myRequest = new ServiceRequest(getActivity());
        myTransactionInfoList = new ArrayList<>();
        isInternetPresent = myConnectionDetector.isConnectingToInternet();
        mySwipeLAY = (WaveSwipeRefreshLayout) aView.findViewById(R.id.screen_transaction_LAY_swipe);
        myListview = (ListView) aView.findViewById(R.id.screen_transaction_LV);
        myEmptyTXT = (TextView) aView.findViewById(R.id.screen_transaction_TXT_empty);
        myInternalLAY = (RelativeLayout) aView.findViewById(R.id.transaction_noInternet_layout);

        mySwipeLAY.setColorSchemeColors(Color.WHITE, Color.WHITE);
        mySwipeLAY.setWaveColor(getResources().getColor(R.color.app_color));
        mySwipeLAY.setMaxDropHeight(180);//should Give in Hundreds
        mySwipeLAY.setEnabled(true);

        HashMap<String, String> user = mySession.getUserDetails();
        myUserIdStr = user.get(SessionManager.KEY_USER_ID);
        getTransactionData();
        swipeListener();

        myReceiver = new Receiver();
        IntentFilter intentfilter = new IntentFilter();
        intentfilter.addAction("com.refresh.message");
        getActivity().registerReceiver(myReceiver, intentfilter);
    }

    private void swipeListener() {
        mySwipeLAY.setOnRefreshListener(new WaveSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isInternetPresent) {
                    myInternalLAY.setVisibility(View.GONE);
                    myRefreshStr = "swipe";
                    getData();
                } else {
                    mySwipeLAY.setEnabled(true);
                    mySwipeLAY.setRefreshing(false);
                    myInternalLAY.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void getTransactionData() {
        if (isInternetPresent) {
            myInternalLAY.setVisibility(View.GONE);
            getData();
        } else {
            mySwipeLAY.setEnabled(true);
            myInternalLAY.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            getActivity().unregisterReceiver(myReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getData() {
        startLoading();
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", myUserIdStr);

        myRequest.makeServiceRequest(Iconstant.TRANSACTION_URL, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
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
                                        myTransactionInfoList.clear();
                                        for (int i = 0; i < aJobsArray.length(); i++) {
                                            JSONObject aJsonObject = aJobsArray.getJSONObject(i);
                                            TransactionPojoInfo aTransactionInfo = new TransactionPojoInfo();
                                            aTransactionInfo.setTransactionJobId(aJsonObject.getString("job_id"));
                                            aTransactionInfo.setTransactionCategoryName(aJsonObject.getString("category_name"));
                                            aTransactionInfo.setTransactionTotalAmount(aJsonObject.getString("total_amount"));
                                            myTransactionInfoList.add(aTransactionInfo);
                                        }
                                    }
                                }
                            } else {
                                String sResponse = aObject.getString("response");
                                alert(getResources().getString(R.string.action_sorry), sResponse);
                            }

                            loadInfoData(myTransactionInfoList);
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

    private void loadInfoData(final ArrayList<TransactionPojoInfo> aTransactionInfoList) {
        if (aTransactionInfoList.size() > 0) {
            myEmptyTXT.setVisibility(View.GONE);
            myAdapter = new TransactionListAdapter(myContext, aTransactionInfoList);
            myListview.setAdapter(myAdapter);
            myListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(myContext, TransactionDetailActivity.class);
                    intent.putExtra("UserId", myUserIdStr);
                    intent.putExtra("BookingId", aTransactionInfoList.get(position).getTransactionJobId());
                    myContext.startActivity(intent);
                }
            });
        } else {
            myEmptyTXT.setVisibility(View.VISIBLE);
        }
    }

    private void startLoading() {
        if (myRefreshStr.equalsIgnoreCase("normal")) {
            myLoadingDialog = new PkLoadingDialog(getActivity());
            myLoadingDialog.show();
        } else {
            mySwipeLAY.setRefreshing(true);
        }
    }

    private void stopLoading() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (myRefreshStr.equalsIgnoreCase("normal")) {
                    myLoadingDialog.dismiss();
                } else {
                    mySwipeLAY.setRefreshing(false);
                }
            }
        }, 250);
    }

    //------Alert Method-----

    private void alert(String title, String message) {
        final PkDialog mDialog = new PkDialog(getActivity());
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
}
