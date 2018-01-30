package com.hoperrush.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.hoperrush.R;
import com.hoperrush.adapter.MaidacMoneyTransactionAdapter;
import com.hoperrush.core.dialog.LoadingDialog;
import com.hoperrush.core.dialog.PkDialog;
import com.hoperrush.core.volley.ServiceRequest;
import com.hoperrush.hockeyapp.FragmentHockeyApp;
import com.hoperrush.iconstant.Iconstant;
import com.hoperrush.pojo.PlumbalMoneyTransactionPojo;
import com.hoperrush.utils.ConnectionDetector;
import com.hoperrush.utils.CurrencySymbolConverter;
import com.hoperrush.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MyWalletTransaction extends FragmentHockeyApp {
    private ConnectionDetector cd;
    private boolean isInternetPresent = false;
    private SessionManager sessionManager;
    private String UserID = "";
    private static Context context;

    private RelativeLayout Rl_back;
    private ImageView Im_backIcon;
    private TextView Tv_headerTitle;

    private ServiceRequest mRequest;
    private LoadingDialog mLoadingDialog;
    private boolean isTransactionAvailable = false;
    ArrayList<PlumbalMoneyTransactionPojo> itemlist_all;
    ArrayList<PlumbalMoneyTransactionPojo> itemlist_credit;
    ArrayList<PlumbalMoneyTransactionPojo> itemlist_debit;
    MaidacMoneyTransactionAdapter adapter;

    private ListView listview;
    private TextView empty_text;
    private LinearLayout layout_All, layout_Credit, layout_Debit;
    private TextView Tv_All, Tv_Credit, Tv_Debit;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.tab_transcation, container, false);
        initialize(rootview);
        clickListener();
        return rootview;
    }

    private void initialize(View aView) {
        cd = new ConnectionDetector(getActivity());
        isInternetPresent = cd.isConnectingToInternet();
        sessionManager = new SessionManager(getActivity());
        itemlist_all = new ArrayList<PlumbalMoneyTransactionPojo>();
        itemlist_credit = new ArrayList<PlumbalMoneyTransactionPojo>();
        itemlist_debit = new ArrayList<PlumbalMoneyTransactionPojo>();

        listview = (ListView) aView.findViewById(R.id.cabily_money_transaction_listview);
        empty_text = (TextView) aView.findViewById(R.id.cabily_money_transaction_listview_empty_text);
        layout_All = (LinearLayout) aView.findViewById(R.id.cabily_money_transactions_all_layout);
        layout_Credit = (LinearLayout) aView.findViewById(R.id.cabily_money_transactions_credits_layout);
        layout_Debit = (LinearLayout) aView.findViewById(R.id.cabily_money_transactions_debit_layout);
        Tv_All = (TextView) aView.findViewById(R.id.cabily_money_transactions_all_textview);
        Tv_Credit = (TextView) aView.findViewById(R.id.cabily_money_transactions_credits_textview);
        Tv_Debit = (TextView) aView.findViewById(R.id.cabily_money_transactions_debits_textview);

        layout_All.setBackgroundColor(0xFF00897B);
        Tv_All.setTextColor(0xFFFFFFFF);

        // get user data from session
        HashMap<String, String> user = sessionManager.getUserDetails();
        UserID = user.get(SessionManager.KEY_USER_ID);

        if (isInternetPresent) {
            postRequest_CabilyMoney(Iconstant.plumbal_money_transaction_url);
        } else {
            alert(getResources().getString(R.string.action_no_internet_title), getResources().getString(R.string.action_no_internet_message));
        }
    }

    private void clickListener() {

        layout_All.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_All.setBackgroundColor(0xFF00897B);
                layout_Credit.setBackgroundColor(0xFFFFFFFF);
                layout_Debit.setBackgroundColor(0xFFFFFFFF);
                Tv_All.setTextColor(0xFFFFFFFF);
                Tv_Credit.setTextColor(0xFF00897B);
                Tv_Debit.setTextColor(0xFF00897B);
                adapter = new MaidacMoneyTransactionAdapter(getActivity(), itemlist_all);
                listview.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                if (itemlist_all.size() > 0) {
                    empty_text.setVisibility(View.GONE);
                } else {
                    empty_text.setVisibility(View.VISIBLE);
                    listview.setEmptyView(empty_text);
                }

            }
        });

        layout_Credit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_All.setBackgroundColor(0xFFFFFFFF);
                layout_Credit.setBackgroundColor(0xFF00897B);
                layout_Debit.setBackgroundColor(0xFFFFFFFF);
                Tv_All.setTextColor(0xFF00897B);
                Tv_Credit.setTextColor(0xFFFFFFFF);
                Tv_Debit.setTextColor(0xFF00897B);

                adapter = new MaidacMoneyTransactionAdapter(getActivity(), itemlist_credit);
                listview.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                if (itemlist_credit.size() > 0) {
                    empty_text.setVisibility(View.GONE);
                } else {
                    empty_text.setVisibility(View.VISIBLE);
                    listview.setEmptyView(empty_text);
                }
            }
        });

        layout_Debit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_All.setBackgroundColor(0xFFFFFFFF);
                layout_Credit.setBackgroundColor(0xFFFFFFFF);
                layout_Debit.setBackgroundColor(0xFF00897B);
                Tv_All.setTextColor(0xFF00897B);
                Tv_Credit.setTextColor(0xFF00897B);
                Tv_Debit.setTextColor(0xFFFFFFFF);

                adapter = new MaidacMoneyTransactionAdapter(getActivity(), itemlist_debit);
                listview.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                if (itemlist_debit.size() > 0) {
                    empty_text.setVisibility(View.GONE);
                } else {
                    empty_text.setVisibility(View.VISIBLE);
                    listview.setEmptyView(empty_text);
                }
            }
        });
    }

    //--------------Alert Method-----------
    private void alert(String title, String alert) {
        final PkDialog mDialog = new PkDialog(getActivity());
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

    //-----------------------Cabily Money Post Request-----------------
    private void postRequest_CabilyMoney(String Url) {
        mLoadingDialog = new LoadingDialog(getActivity());
        mLoadingDialog.setLoadingTitle(getResources().getString(R.string.action_loading));
        mLoadingDialog.show();

        System.out.println("-------------MaidacMoneyTransaction  Url----------------" + Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", UserID);
        jsonParams.put("type", "all");
         mRequest = new ServiceRequest(getActivity());
         mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("-------------MaidacMoneyTransaction  Response----------------" + response);

                String Sstatus = "";
                try {
                    JSONObject object = new JSONObject(response);
                    Sstatus = object.getString("status");

                    if (Sstatus.equalsIgnoreCase("1")) {
                        JSONObject response_object = object.getJSONObject("response");
                        if (response_object.length() > 0) {
                            JSONArray trans_array = response_object.getJSONArray("trans");
                            if (trans_array.length() > 0) {
                                itemlist_all.clear();
                                for (int i = 0; i < trans_array.length(); i++) {
                                    JSONObject trans_object = trans_array.getJSONObject(i);
                                    PlumbalMoneyTransactionPojo pojo = new PlumbalMoneyTransactionPojo();
                                    pojo.setTrans_type(trans_object.getString("type"));
                                    pojo.setTrans_amount(trans_object.getString("trans_amount"));
                                    pojo.setTitle(trans_object.getString("title"));
                                    pojo.setTrans_date(trans_object.getString("trans_date"));
                                    pojo.setBalance_amount(trans_object.getString("balance_amount"));
                                    pojo.setCurrencySymbol(CurrencySymbolConverter.getCurrencySymbol(response_object.getString("currency")));
                                    itemlist_all.add(pojo);
                                    if (trans_object.getString("type").equalsIgnoreCase("CREDIT")) {
                                        itemlist_credit.add(pojo);
                                    } else {
                                        itemlist_debit.add(pojo);
                                    }
                                }
                                isTransactionAvailable = true;
                            } else {
                                isTransactionAvailable = false;
                            }
                        }

                    }


                    if (Sstatus.equalsIgnoreCase("1")) {
                        if (isTransactionAvailable) {
                            empty_text.setVisibility(View.GONE);
                            adapter = new MaidacMoneyTransactionAdapter(getActivity(), itemlist_all);
                            listview.setAdapter(adapter);
                        } else {
                            empty_text.setVisibility(View.VISIBLE);
                            listview.setEmptyView(empty_text);
                        }
                    } else {
                        empty_text.setVisibility(View.VISIBLE);
                        listview.setEmptyView(empty_text);
                        String Sresponse = object.getString("response");
                        //alert(getResources().getString(R.string.action_sorry), Sresponse);
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

}
