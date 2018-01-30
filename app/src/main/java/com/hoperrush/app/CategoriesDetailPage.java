package com.hoperrush.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;
import com.hoperrush.R;
import com.hoperrush.adapter.CategoriesDetailAdapter;
import com.hoperrush.core.dialog.PkDialog;
import com.hoperrush.core.dialog.PkLoadingDialog;
import com.hoperrush.core.volley.ServiceRequest;
import com.hoperrush.iconstant.Iconstant;
import com.hoperrush.pojo.CategoryDetailPojo;
import com.hoperrush.utils.ConnectionDetector;
import com.hoperrush.utils.SessionManager;
import com.hoperrush.utils.SubClassActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Casperon Technology on 1/5/2016.
 */
public class CategoriesDetailPage extends SubClassActivity {
    private ConnectionDetector cd;
    private boolean isInternetPresent = false;
    private SessionManager sessionManager;

    private RelativeLayout Rl_back;
    private ImageView Im_backIcon;
    private TextView Tv_headerTitle;

    private RelativeLayout Rl_NoInternet, Rl_Main;
    private SwipeRefreshLayout swipeToRefresh;
    private ImageView Im_Banner;
    private ExpandableHeightListView listView;

    private String sCategoryID = "", sCategoryName = "", sCategoryImage = "", sLocationID = "";
    private String Str_Refresh_Name = "normal";
    private PkLoadingDialog mLoadingDialog;
    private ServiceRequest mRequest;
    private boolean asCategory = false;

    private ArrayList<CategoryDetailPojo> catItemList;
    private CategoriesDetailAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.categories_detail_page);
        initializeHeaderBar();
        initialize();

        Rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        swipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                cd = new ConnectionDetector(CategoriesDetailPage.this);
                isInternetPresent = cd.isConnectingToInternet();

                if (isInternetPresent) {
                    swipeToRefresh.setEnabled(false);
                    Rl_Main.setVisibility(View.VISIBLE);
                    Rl_NoInternet.setVisibility(View.GONE);
                    Str_Refresh_Name = "swipe";
                    postDisplayCategory_DetailRequest(CategoriesDetailPage.this, Iconstant.Categories_Detail_Url);
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
                Intent intent = new Intent(CategoriesDetailPage.this, NewAppointmentpage.class);
                intent.putExtra("IntentCategoryID", catItemList.get(position).getCat_id());
                intent.putExtra("IntentServiceID", sCategoryID);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });


        listView.setExpanded(true);
    }

    private void initializeHeaderBar() {
        RelativeLayout headerBar = (RelativeLayout) findViewById(R.id.headerBar_noShadow_layout);
        Rl_back = (RelativeLayout) headerBar.findViewById(R.id.headerBar_noShadow_left_layout);
        Im_backIcon = (ImageView) headerBar.findViewById(R.id.headerBar_noShadow_imageView);
        Tv_headerTitle = (TextView) headerBar.findViewById(R.id.headerBar_noShadow_title_textView);

        Im_backIcon.setImageResource(R.drawable.back_arrow);
    }

    private void initialize() {
        cd = new ConnectionDetector(CategoriesDetailPage.this);
        isInternetPresent = cd.isConnectingToInternet();
        sessionManager = new SessionManager(CategoriesDetailPage.this);
        catItemList = new ArrayList<CategoryDetailPojo>();
        mRequest = new ServiceRequest(CategoriesDetailPage.this);

        listView = (ExpandableHeightListView) findViewById(R.id.categories_detailPage_listView);
        swipeToRefresh = (SwipeRefreshLayout) findViewById(R.id.categories_detailPage_swipeToRefresh_layout);
        Rl_NoInternet = (RelativeLayout) findViewById(R.id.categories_detailPage_noInternet_layout);
        Rl_Main = (RelativeLayout) findViewById(R.id.categories_detailPage_main_layout);
        Im_Banner = (ImageView) findViewById(R.id.categories_detailPage_header_image);

        // Configure the refreshing colors
        swipeToRefresh.setColorSchemeResources(android.R.color.holo_red_dark,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeToRefresh.setEnabled(false);

        Intent intent = getIntent();
        sCategoryID = intent.getStringExtra("IntentCatId");
        sCategoryName = intent.getStringExtra("IntentCatName");
        sCategoryImage = intent.getStringExtra("IntentCatImage");
        sLocationID = intent.getStringExtra("IntentLocationID");

        System.out.println("----------sCategoryID------------" + sCategoryID);

        Tv_headerTitle.setText(sCategoryName);
        if (sCategoryImage != null) {
            Picasso.with(CategoriesDetailPage.this).invalidate(sCategoryImage);
            Picasso.with(CategoriesDetailPage.this).load(sCategoryImage).fit().into(Im_Banner);
        }

        if (isInternetPresent) {
            swipeToRefresh.setEnabled(false);
            Rl_Main.setVisibility(View.VISIBLE);
            Rl_NoInternet.setVisibility(View.GONE);
            postDisplayCategory_DetailRequest(CategoriesDetailPage.this, Iconstant.Categories_Detail_Url);
        } else {
            swipeToRefresh.setEnabled(true);
            Rl_Main.setVisibility(View.GONE);
            Rl_NoInternet.setVisibility(View.VISIBLE);
        }
    }

    //------Alert Method-----
    private void alert(String title, String message) {
        final PkDialog mDialog = new PkDialog(CategoriesDetailPage.this);
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

    private void startLoading() {
        if (Str_Refresh_Name.equalsIgnoreCase("normal")) {
            mLoadingDialog = new PkLoadingDialog(CategoriesDetailPage.this);
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

    //-------------Display Category Post Request---------------
    private void postDisplayCategory_DetailRequest(Context mContext, String url) {

        startLoading();

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("category", sCategoryID);
        jsonParams.put("location_id", sLocationID);

        System.out.println("---------Category Detail url------------" + url);

        mRequest.makeServiceRequest(url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("---------Category Detail response------------" + response);

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
                                        CategoryDetailPojo pojo = new CategoryDetailPojo();

                                        pojo.setCat_id(cat_Object.getString("cat_id"));
                                        pojo.setCat_name(cat_Object.getString("cat_name"));
                                        pojo.setCat_image(cat_Object.getString("image"));
                                        pojo.setIcon_normal(cat_Object.getString("image"));
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
                        }
                    }

                    if (Str_status.equalsIgnoreCase("1")) {
                        if (asCategory) {
                            adapter = new CategoriesDetailAdapter(CategoriesDetailPage.this, catItemList);
                            listView.setAdapter(adapter);
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

    //-----------------Move Back on pressed phone back button-------------
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
