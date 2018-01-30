package com.hoperrush.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.github.clans.fab.FloatingActionButton;
import com.hoperrush.R;
import com.hoperrush.adapter.CategoryAdapter;
import com.hoperrush.adapter.CitySelectionAdapter;
import com.hoperrush.app.CategoriesDetailPage;
import com.hoperrush.app.ChatListPage;
import com.hoperrush.app.NavigationDrawer;
import com.hoperrush.core.dialog.PkDialog;
import com.hoperrush.core.dialog.PkLoadingDialog;
import com.hoperrush.core.socket.SocketHandler;
import com.hoperrush.core.volley.ServiceRequest;
import com.hoperrush.hockeyapp.FragmentHockeyApp;
import com.hoperrush.iconstant.Iconstant;
import com.hoperrush.pojo.CategoryPojo;
import com.hoperrush.pojo.CitySelectionPojo;
import com.hoperrush.utils.ConnectionDetector;
import com.hoperrush.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;
import me.drakeet.materialdialog.MaterialDialog;

/**
 * Casperon Technology on 12/10/2015.
 */
public class Fragment_HomePage extends FragmentHockeyApp {

    private ConnectionDetector cd;
    private boolean isInternetPresent = false;
    private SessionManager sessionManager;

    private RelativeLayout Rl_drawer;
    private ImageView Im_drawerIcon;
    private TextView Tv_headerTitle;
    private Context context;

    private ListView listView;
    private LinearLayout Ll_CitySelect;
    private RelativeLayout Rl_NoInternet, Rl_Main;
    private TextView Tv_selectedCity;
    private WaveSwipeRefreshLayout swipeToRefresh;
    private String Str_Refresh_Name = "normal";
    private PkLoadingDialog mLoadingDialog;
    private ArrayList<CategoryPojo> catItemList;
    private ArrayList<CitySelectionPojo> cityItemList;
    private boolean asCategory = false;
    private boolean asLocation = false;
    private CategoryAdapter adapter;
    private ServiceRequest mRequest;

    private String Str_SelectedCity_Name = "", Str_SelectedCity_Id = "";
    BroadcastReceiver logoutReciver;

    private FloatingActionButton fabChat;
    private int mPreviousVisibleItem;
    private SocketHandler socketHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.homepage, container, false);
        initializeHeaderBar(rootView);
        initialize(rootView);


        // Finishing the activity using broadcast
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.app.logout");
        logoutReciver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("com.app.logout")) {
                    getActivity().finish();
                }

            }
        };
        getActivity().registerReceiver(logoutReciver, filter);


                Rl_drawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationDrawer.openDrawer();
            }
        });

        swipeToRefresh.setOnRefreshListener(new WaveSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                cd = new ConnectionDetector(getActivity());
                isInternetPresent = cd.isConnectingToInternet();

                if (isInternetPresent) {
                    Rl_Main.setVisibility(View.VISIBLE);
                    Rl_NoInternet.setVisibility(View.GONE);
                    Str_Refresh_Name = "swipe";
                    postCategoryRequest(getActivity(), Iconstant.CategoriesUrl);
                } else {
                    swipeToRefresh.setEnabled(true);
                    swipeToRefresh.setRefreshing(false);
                    Rl_Main.setVisibility(View.GONE);
                    Rl_NoInternet.setVisibility(View.VISIBLE);
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), CategoriesDetailPage.class);
                intent.putExtra("IntentCatId", catItemList.get(position).getCat_id());
                intent.putExtra("IntentCatName", catItemList.get(position).getCat_name());
                intent.putExtra("IntentCatImage", catItemList.get(position).getCat_image());
                intent.putExtra("IntentLocationID", Str_SelectedCity_Id);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });


        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if (firstVisibleItem == 0) {
                    swipeToRefresh.setEnabled(false);
                } else {
                    swipeToRefresh.setEnabled(false);
                }

                //Show and Hide fab button
               /* if (firstVisibleItem > mPreviousVisibleItem) {
                    fabChat.hide(true);
                } else if (firstVisibleItem < mPreviousVisibleItem) {
                    fabChat.show(true);
                }*/
                mPreviousVisibleItem = firstVisibleItem;
            }
        });


        Ll_CitySelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (asLocation) {
                    citySelectDialog();
                }
            }
        });


        fabChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cd = new ConnectionDetector(getActivity());
                isInternetPresent = cd.isConnectingToInternet();
                if (isInternetPresent) {
                    Intent intent = new Intent(getActivity(), ChatListPage.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.fab_scale_up, R.anim.fab_scale_down);
                } else {
                    alert(getResources().getString(R.string.action_no_internet_title), getResources().getString(R.string.action_no_internet_message));
                }
            }
        });

        return rootView;
    }

    private void initializeHeaderBar(View rootView) {
        RelativeLayout headerBar = (RelativeLayout) rootView.findViewById(R.id.headerBar_noShadow_layout);
        Rl_drawer = (RelativeLayout) headerBar.findViewById(R.id.headerBar_noShadow_left_layout);
        Im_drawerIcon = (ImageView) headerBar.findViewById(R.id.headerBar_noShadow_imageView);
        Tv_headerTitle = (TextView) headerBar.findViewById(R.id.headerBar_noShadow_title_textView);

        Tv_headerTitle.setText(getResources().getString(R.string.homepage_label_title));
        Im_drawerIcon.setImageResource(R.drawable.drawer_icon);
    }

    private void initialize(View rootView) {
        cd = new ConnectionDetector(getActivity());
        isInternetPresent = cd.isConnectingToInternet();

        socketHandler = SocketHandler.getInstance(getActivity());

        sessionManager = new SessionManager(getActivity());
        catItemList = new ArrayList<CategoryPojo>();
        cityItemList = new ArrayList<CitySelectionPojo>();
        mRequest = new ServiceRequest(getActivity());
/*

        Intent broadcastIntentnavigation = new Intent();
        broadcastIntentnavigation.setAction("com.package.finish.AppoimentConfirmation");
        context.sendBroadcast(broadcastIntentnavigation);*/


        listView = (ListView) rootView.findViewById(R.id.homepage_listView);
        Ll_CitySelect = (LinearLayout) rootView.findViewById(R.id.homepage_city_select_layout);
        Tv_selectedCity = (TextView) rootView.findViewById(R.id.homepage_selected_city_textView);
        swipeToRefresh = (WaveSwipeRefreshLayout) rootView.findViewById(R.id.home_swipeToRefresh_layout);
        Rl_NoInternet = (RelativeLayout) rootView.findViewById(R.id.homepage_noInternet_layout);
        Rl_Main = (RelativeLayout) rootView.findViewById(R.id.homepage_main_layout);
        fabChat = (FloatingActionButton) rootView.findViewById(R.id.homepage_chat_fabButton);

        // Configure the refreshing colors
        swipeToRefresh.setColorSchemeColors(Color.WHITE, Color.WHITE);
        swipeToRefresh.setWaveColor(getResources().getColor(R.color.app_color));
        swipeToRefresh.setMaxDropHeight(250);//should Give in Hundreds
        swipeToRefresh.setEnabled(false);


        //Code to show Fab Button
     /*   fabChat.hide(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                fabChat.setShowAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.jump_from_down));
                fabChat.setHideAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.jump_to_down));
                fabChat.show(true);
            }
        }, 500);
*/

        HashMap<String, String> location = sessionManager.getLocationDetails();
        Str_SelectedCity_Id = location.get(SessionManager.KEY_LOCATION_ID);
        Str_SelectedCity_Name = location.get(SessionManager.KEY_LOCATION_NAME);

        Tv_selectedCity.setText(Str_SelectedCity_Name);

        if (isInternetPresent) {
            Rl_Main.setVisibility(View.VISIBLE);
            Rl_NoInternet.setVisibility(View.GONE);
            postCategoryRequest(getActivity(), Iconstant.CategoriesUrl);
        } else {
            swipeToRefresh.setEnabled(true);
            Rl_Main.setVisibility(View.GONE);
            Rl_NoInternet.setVisibility(View.VISIBLE);
        }
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

    private void citySelectDialog() {
        final MaterialDialog dialog = new MaterialDialog(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.home_page_city_selection, null);

        ListView city_listView = (ListView) view.findViewById(R.id.home_page_city_selection_listView);

        CitySelectionAdapter car_adapter = new CitySelectionAdapter(getActivity(), cityItemList);
        city_listView.setAdapter(car_adapter);
        car_adapter.notifyDataSetChanged();

        dialog.setPositiveButton(getActivity().getResources().getString(R.string.homepage_label_select_city_cancel), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                }
        );

        city_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialog.dismiss();
                Str_SelectedCity_Id = cityItemList.get(position).getLocationId();
                Str_SelectedCity_Name = cityItemList.get(position).getLocationName();

                cd = new ConnectionDetector(getActivity());
                isInternetPresent = cd.isConnectingToInternet();

                if (isInternetPresent) {
                    Str_Refresh_Name = "normal";
                    postCategoryRequest(getActivity(), Iconstant.CategoriesUrl);
                } else {
                    alert(getResources().getString(R.string.action_no_internet_title), getResources().getString(R.string.action_no_internet_message));
                }
            }
        });
        dialog.setView(view).show();
    }

    private void startLoading() {
        if (Str_Refresh_Name.equalsIgnoreCase("normal")) {
            mLoadingDialog = new PkLoadingDialog(getActivity());
            mLoadingDialog.show();
        } else {
            swipeToRefresh.setRefreshing(true);
        }
    }

    private void stopLoading() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Str_Refresh_Name.equalsIgnoreCase("normal")) {
                    mLoadingDialog.dismiss();
                } else {
                    swipeToRefresh.setRefreshing(false);
                }
            }
        }, 500);
    }

    //-------------Category Post Request---------------
    private void postCategoryRequest(Context mContext, String url) {

        startLoading();

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("location_id", Str_SelectedCity_Id);

        System.out.println("---------Category location_id------------" + Str_SelectedCity_Id);

        System.out.println("---------Category url------------" + url);

        mRequest.makeServiceRequest(url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("---------Category response------------" + response);

                String Str_status = "";
                try {
                    JSONObject object = new JSONObject(response);
                    Str_status = object.getString("status");

                    if (Str_status.equalsIgnoreCase("1")) {
                        JSONObject response_Object = object.getJSONObject("response");
                        if (response_Object.length() > 0) {
                            Object check_category_object = response_Object.get("category");
                            if (check_category_object instanceof JSONArray) {

                                JSONArray cat_Array = response_Object.getJSONArray("category");
                                if (cat_Array.length() > 0) {
                                    catItemList.clear();
                                    for (int i = 0; i < cat_Array.length(); i++) {
                                        JSONObject cat_Object = cat_Array.getJSONObject(i);
                                        CategoryPojo pojo = new CategoryPojo();

                                        pojo.setCat_id(cat_Object.getString("cat_id"));
                                        pojo.setCat_name(cat_Object.getString("cat_name"));
                                        pojo.setCat_image(cat_Object.getString("image"));
                                        pojo.setIcon_normal(cat_Object.getString("icon_normal"));
                                        pojo.setHasChild(cat_Object.getString("hasChild"));

                                        catItemList.add(pojo);
                                    }
                                    asCategory = true;
                                } else {
                                    asCategory = false;
                                }
                            } else {
                                asCategory = false;
                            }


                            Object check_locations_object = response_Object.get("locations");
                            if (check_locations_object instanceof JSONArray) {
                                JSONArray location_Array = response_Object.getJSONArray("locations");
                                if (location_Array.length() > 0) {
                                    cityItemList.clear();
                                    for (int i = 0; i < location_Array.length(); i++) {
                                        JSONObject location_Object = location_Array.getJSONObject(i);
                                        CitySelectionPojo pojo = new CitySelectionPojo();
                                        pojo.setLocationId(location_Object.getString("id"));
                                        pojo.setLocationName(location_Object.getString("city"));

                                        cityItemList.add(pojo);
                                    }
                                    asLocation = true;
                                } else {
                                    asLocation = false;
                                }
                            } else {
                                asLocation = false;
                            }
                        }
                    }


                    if (Str_status.equalsIgnoreCase("1")) {

                        //Adding location to session
                        sessionManager.createLocationSession(Str_SelectedCity_Id, Str_SelectedCity_Name);

                        if (asCategory) {
                            adapter = new CategoryAdapter(getActivity(), catItemList);
                            listView.setAdapter(adapter);
                        }

                        if (asLocation) {
                            Tv_selectedCity.setText(Str_SelectedCity_Name);
                        }
                    }

                } catch (JSONException e) {
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
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(logoutReciver);
        super.onDestroy();
    }
}
