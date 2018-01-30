package com.hoperrush.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Request;
import com.hoperrush.R;
import com.hoperrush.adapter.CitySelectionAdapter;
import com.hoperrush.hockeyapp.ActivityHockeyApp;
import com.hoperrush.iconstant.Iconstant;
import com.hoperrush.pojo.CitySelectionPojo;
import com.hoperrush.utils.ConnectionDetector;
import com.hoperrush.utils.SessionManager;
import com.hoperrush.core.dialog.LoadingDialog;
import com.hoperrush.core.dialog.PkDialog;
import com.hoperrush.core.volley.ServiceRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Casperon Technology on 12/9/2015.
 */
public class CitySelection extends ActivityHockeyApp {

    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private SessionManager sessionManager;

    private ListView listView;
    private String UserID = "";
    private String locationID = "",locationName="";
    private CitySelectionAdapter adapter;
    private ArrayList<CitySelectionPojo> itemList;
    private boolean isLocationAvailable = false;
    private String sCheckClass="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.city_selection);
        initialize();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                cd = new ConnectionDetector(CitySelection.this);
                isInternetPresent = cd.isConnectingToInternet();

                if (isInternetPresent) {
                    locationID=itemList.get(position).getLocationId();
                    locationName=itemList.get(position).getLocationName();
                   PostCityRequest(CitySelection.this, Iconstant.SelectCityUrl);
/*
                    Intent intent = new Intent(CitySelection.this, NavigationDrawer.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.enter, R.anim.exit);*/

                } else {
                    alert(getResources().getString(R.string.action_no_internet_title), getResources().getString(R.string.action_no_internet_message));
                }

            }
        });
    }

    private void initialize() {
        sessionManager = new SessionManager(CitySelection.this);
        cd = new ConnectionDetector(CitySelection.this);
        isInternetPresent = cd.isConnectingToInternet();
        itemList = new ArrayList<CitySelectionPojo>();

        listView = (ListView) findViewById(R.id.city_selection_listView);

        // get user data from session
        HashMap<String, String> user = sessionManager.getUserDetails();
        UserID = user.get(SessionManager.KEY_USER_ID);



        Intent intent=getIntent();
        sCheckClass=intent.getStringExtra("IntentClass");

        if (isInternetPresent) {
            DisplayCityRequest(CitySelection.this, Iconstant.DisplayCityUrl);

            System.out.println("DisplayCityRequest----------"+Iconstant.DisplayCityUrl);

        } else {
            alert(getResources().getString(R.string.action_no_internet_title), getResources().getString(R.string.action_no_internet_message));
        }
    }

    //--------Alert Method--------
    private void alert(String title, String message) {
        final PkDialog mDialog = new PkDialog(CitySelection.this);
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

    //----------Display City Request--------
    private void DisplayCityRequest(final Context mContext, String url) {

        final LoadingDialog mLoadingDialog = new LoadingDialog(mContext);
        mLoadingDialog.setLoadingTitle(getResources().getString(R.string.action_loading));
        mLoadingDialog.show();

        ServiceRequest mRequest = new ServiceRequest(mContext);
        mRequest.makeServiceRequest(url, Request.Method.GET, null, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("--------- City response------------" + response);

                String Str_status = "", Str_message = "";
                try {
                    JSONObject object = new JSONObject(response);
                    Str_status = object.getString("status");

                    if (Str_status.equalsIgnoreCase("1")) {

                        JSONObject responseObject = object.getJSONObject("response");
                        if (responseObject.length() > 0) {
                            JSONArray locationArray = responseObject.getJSONArray("locations");
                            if (locationArray.length() > 0) {
                                itemList.clear();
                                for (int i = 0; i < locationArray.length(); i++) {
                                    JSONObject locationObject = locationArray.getJSONObject(i);
                                    CitySelectionPojo pojo = new CitySelectionPojo();
                                    pojo.setLocationId(locationObject.getString("id"));
                                    pojo.setLocationName(locationObject.getString("city"));

                                    itemList.add(pojo);
                                }

                                isLocationAvailable = true;
                            }
                        }
                    }

                    if (Str_status.equalsIgnoreCase("1") && isLocationAvailable) {
                        adapter = new CitySelectionAdapter(CitySelection.this, itemList);
                        listView.setAdapter(adapter);
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


    //----------Post City Request--------
    private void PostCityRequest(final Context mContext, String url) {

        final LoadingDialog mLoadingDialog = new LoadingDialog(mContext);
        mLoadingDialog.setLoadingTitle(getResources().getString(R.string.action_pleaseWait));
        mLoadingDialog.show();

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", UserID);
        jsonParams.put("location_id", locationID);

        ServiceRequest mRequest = new ServiceRequest(mContext);
        mRequest.makeServiceRequest(url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("---------Post City response------------" + response);

                String Str_status = "", Str_message = "";
                try {
                    JSONObject object = new JSONObject(response);
                    Str_status = object.getString("status");
                    Str_message = object.getString("message");

                    if (Str_status.equalsIgnoreCase("1")) {
                        String locationId = object.getString("location_id");
                        sessionManager.createLocationSession(locationId,locationName);


                        if(sCheckClass.equalsIgnoreCase("1"))
                        {
                            NavigationDrawer.navigationDrawerClass.finish();
                            Intent intent = new Intent(CitySelection.this, NavigationDrawer.class);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(R.anim.enter, R.anim.exit);
                        }else if(sCheckClass.equalsIgnoreCase("2"))
                        {
                            NavigationDrawer.navigationNotifyChange();
                            Intent broadcastIntent = new Intent();
                            broadcastIntent.setAction("com.package.ACTION_CLASS_APPOINTMENT_REFRESH");
                            sendBroadcast(broadcastIntent);
                            finish();
                            overridePendingTransition(R.anim.enter, R.anim.exit);
                        }
                    } else {
                        alert(getResources().getString(R.string.action_sorry), Str_message);
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


    //-------------Move Back on pressed phone back button-----------
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)) {
            //Nothing should happen
            return true;
        }
        return false;
    }
}
