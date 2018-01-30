package com.hoperrush.app;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.hoperrush.R;
import com.hoperrush.adapter.PlaceSearchAdapter;
import com.hoperrush.hockeyapp.ActivityHockeyApp;
import com.hoperrush.iconstant.Iconstant;
import com.hoperrush.core.dialog.PkDialog;
import com.hoperrush.core.volley.ServiceRequest;
import com.hoperrush.utils.ConnectionDetector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Casperon Technology on 11/12/2015.
 */
public class LocationSearch extends ActivityHockeyApp {

    private RelativeLayout back;
    private EditText et_search;
    private ListView listview;
    private RelativeLayout alert_layout;
    private TextView alert_textview;
    private TextView tv_emptyText;
    private ProgressBar progresswheel;

    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;

    private ServiceRequest mRequest;
    Context context;
    ArrayList<String> itemList_location = new ArrayList<String>();
    ArrayList<String> itemList_placeId = new ArrayList<String>();

    private PlaceSearchAdapter adapter;
    private boolean isdataAvailable = false;
    private boolean isEstimateAvailable = false;

    private String Slatitude = "", Slongitude = "", Sselected_location = "";

    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.place_search);
        context = getApplicationContext();
        initialize();


        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Sselected_location = itemList_location.get(position);

                cd = new ConnectionDetector(LocationSearch.this);
                isInternetPresent = cd.isConnectingToInternet();
                if (isInternetPresent) {
                    LatLongRequest(Iconstant.GetAddressFrom_LatLong_url + itemList_placeId.get(position));
                } else {
                    alert_layout.setVisibility(View.VISIBLE);
                    alert_textview.setText(getResources().getString(R.string.alert_nointernet));
                }

            }
        });

        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                cd = new ConnectionDetector(LocationSearch.this);
                isInternetPresent = cd.isConnectingToInternet();

                if (isInternetPresent) {
                    if (mRequest != null) {
                        mRequest.cancelRequest();
                    }
                    String data = et_search.getText().toString().toLowerCase().replace(" ", "%20");
                    CitySearchRequest(Iconstant.place_search_url + data);
                } else {
                    alert_layout.setVisibility(View.VISIBLE);
                    alert_textview.setText(getResources().getString(R.string.alert_nointernet));
                }

            }
        });

        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    CloseKeyboard(et_search);
                }
                return false;
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // close keyboard
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(back.getWindowToken(), 0);

                LocationSearch.this.finish();
                LocationSearch.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        et_search.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager keyboard = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                keyboard.showSoftInput(et_search, 0);
            }
        }, 200);
    }

    private void initialize() {
        alert_layout = (RelativeLayout) findViewById(R.id.location_search_alert_layout);
        alert_textview = (TextView) findViewById(R.id.location_search_alert_textView);
        back = (RelativeLayout) findViewById(R.id.location_search_back_layout);
        et_search = (EditText) findViewById(R.id.location_search_editText);
        listview = (ListView) findViewById(R.id.location_search_listView);
        progresswheel = (ProgressBar) findViewById(R.id.location_search_progressBar);
        tv_emptyText = (TextView) findViewById(R.id.location_search_empty_textview);

    }

    private void CloseKeyboard(EditText edittext) {
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(edittext.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    //--------------Alert Method-----------
    private void Alert(String title, String alert) {

        final PkDialog mDialog = new PkDialog(LocationSearch.this);
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

    //-------------------Search Place Request----------------
    private void CitySearchRequest(String Url) {

        progresswheel.setVisibility(View.VISIBLE);
        System.out.println("--------------Search city url-------------------" + Url);

        mRequest = new ServiceRequest(LocationSearch.this);
        mRequest.makeServiceRequest(Url, Request.Method.GET, null, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("--------------Search city  reponse-------------------" + response);
                String status = "";
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.length() > 0) {

                        status = object.getString("status");
                        JSONArray place_array = object.getJSONArray("predictions");
                        if (status.equalsIgnoreCase("OK")) {
                            if (place_array.length() > 0) {
                                itemList_location.clear();
                                itemList_placeId.clear();
                                for (int i = 0; i < place_array.length(); i++) {
                                    JSONObject place_object = place_array.getJSONObject(i);
                                    itemList_location.add(place_object.getString("description"));
                                    itemList_placeId.add(place_object.getString("place_id"));
                                }
                                isdataAvailable = true;
                            } else {
                                itemList_location.clear();
                                itemList_placeId.clear();
                                isdataAvailable = false;
                            }
                        } else {
                            itemList_location.clear();
                            itemList_placeId.clear();
                            isdataAvailable = false;
                        }
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                progresswheel.setVisibility(View.INVISIBLE);
                alert_layout.setVisibility(View.GONE);
                if (isdataAvailable) {
                    tv_emptyText.setVisibility(View.GONE);
                } else {
                    tv_emptyText.setVisibility(View.VISIBLE);
                }
                adapter = new PlaceSearchAdapter(LocationSearch.this, itemList_location);
                listview.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onErrorListener() {
                progresswheel.setVisibility(View.INVISIBLE);
                alert_layout.setVisibility(View.GONE);

                // close keyboard
                CloseKeyboard(et_search);
            }
        });
    }


    //-------------------Get Latitude and Longitude from Address(Place ID) Request----------------
  /*  private void LatLongRequest(String Url) {

        dialog = new Dialog(LocationSearch.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_processing));

        System.out.println("--------------LatLong url-------------------" + Url);

        mRequest = new ServiceRequest(LocationSearch.this);
        mRequest.makeServiceRequest(Url, Request.Method.GET, null, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("--------------LatLong  reponse-------------------" + response);
                String status = "";
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.length() > 0) {

                        status = object.getString("status");
                        JSONObject place_object = object.getJSONObject("result");
                        if (status.equalsIgnoreCase("OK")) {
                            if (place_object.length() > 0) {
                                JSONObject geometry_object = place_object.getJSONObject("geometry");
                                if (geometry_object.length() > 0) {
                                    JSONObject location_object = geometry_object.getJSONObject("location");
                                    if (location_object.length() > 0) {
                                        Slatitude = location_object.getString("lat");
                                        Slongitude = location_object.getString("lng");
                                        isdataAvailable = true;
                                    } else {
                                        isdataAvailable = false;
                                    }
                                } else {
                                    isdataAvailable = false;
                                }
                            } else {
                                isdataAvailable = false;
                            }
                        } else {
                            isdataAvailable = false;
                        }
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if (isdataAvailable) {

                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("Selected_Latitude", Slatitude);
                    returnIntent.putExtra("Selected_Longitude", Slongitude);
                    returnIntent.putExtra("Selected_Location", Sselected_location);
                    setResult(RESULT_OK, returnIntent);
                    onBackPressed();
                    overridePendingTransition(R.anim.slideup, R.anim.slidedown);
                    finish();

                } else {
                    dialog.dismiss();
                    Alert(getResources().getString(R.string.server_lable_header), status);
                }
            }

            @Override
            public void onErrorListener() {
                dialog.dismiss();
            }
        });
    }
*/


    //-------------------Get Latitude and Longitude from Address(Place ID) Request----------------
    private void LatLongRequest(String Url) {
        dialog = new Dialog(LocationSearch.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_processing));

        System.out.println("--------------LatLong url-------------------" + Url);

        mRequest = new ServiceRequest(LocationSearch.this);
        mRequest.makeServiceRequest(Url, Request.Method.GET, null, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("--------------LatLong  reponse-------------------" + response);
                String status = "", sArea = "", sLocality = "", sCity_Admin1 = "", sCity_Admin2 = "", sPostalCode = "";
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.length() > 0) {

                        status = object.getString("status");
                        JSONObject place_object = object.getJSONObject("result");
                        if (status.equalsIgnoreCase("OK")) {
                            if (place_object.length() > 0) {

                                sArea = place_object.getString("name");
                                JSONArray addressArray = place_object.getJSONArray("address_components");
                                if (addressArray.length() > 0) {
                                    for (int i = 0; i < addressArray.length(); i++) {
                                        JSONObject address_object = addressArray.getJSONObject(i);

                                        JSONArray typesArray = address_object.getJSONArray("types");
                                        if (typesArray.length() > 0) {
                                            for (int j = 0; j < typesArray.length(); j++) {

                                                if (typesArray.get(j).toString().equalsIgnoreCase("locality")) {
                                                    sLocality = address_object.getString("long_name");
                                                } else if (typesArray.get(j).toString().equalsIgnoreCase("administrative_area_level_2")) {
                                                    sCity_Admin2 = address_object.getString("long_name") + ",";
                                                } else if (typesArray.get(j).toString().equalsIgnoreCase("administrative_area_level_1")) {
                                                    sCity_Admin1 = address_object.getString("long_name");
                                                } else if (typesArray.get(j).toString().equalsIgnoreCase("postal_code")) {
                                                    sPostalCode = address_object.getString("long_name");
                                                }
                                            }

                                            isdataAvailable = true;
                                        } else {
                                            isdataAvailable = false;
                                        }
                                    }
                                } else {
                                    isdataAvailable = false;
                                }

                                JSONObject geometry_object = place_object.getJSONObject("geometry");
                                if (geometry_object.length() > 0) {
                                    JSONObject location_object = geometry_object.getJSONObject("location");
                                    if (location_object.length() > 0) {
                                        Slatitude = location_object.getString("lat");
                                        Slongitude = location_object.getString("lng");
                                        isdataAvailable = true;
                                    } else {
                                        isdataAvailable = false;
                                    }
                                } else {
                                    isdataAvailable = false;
                                }
                            } else {
                                isdataAvailable = false;
                            }
                        } else {
                            isdataAvailable = false;
                        }
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                if (isdataAvailable) {

                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("Selected_Latitude", Slatitude);
                    returnIntent.putExtra("Selected_Longitude", Slongitude);
                    returnIntent.putExtra("Selected_Location", Sselected_location);
                    returnIntent.putExtra("HouseNo", sArea);
                    returnIntent.putExtra("City", sCity_Admin2 + sCity_Admin1);
                    returnIntent.putExtra("ZipCode", sPostalCode);
                    returnIntent.putExtra("Location", sLocality);

                    setResult(RESULT_OK, returnIntent);
                    onBackPressed();
                    overridePendingTransition(R.anim.slideup, R.anim.slidedown);
                    finish();

                } else {
                    dialog.dismiss();
                    Alert(getResources().getString(R.string.server_lable_header), status);
                }

            }

            @Override
            public void onErrorListener() {
                dialog.dismiss();
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    //-----------------Move Back on pressed phone back button------------------
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)) {

            // close keyboard
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(back.getWindowToken(), 0);

            LocationSearch.this.finish();
            LocationSearch.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            return true;
        }
        return false;
    }
}

