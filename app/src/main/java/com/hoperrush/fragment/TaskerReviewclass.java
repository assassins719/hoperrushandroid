package com.hoperrush.fragment;

import android.content.Context;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.hoperrush.R;
import com.hoperrush.adapter.MyProfile_Reviwes_Adapter;
import com.hoperrush.core.dialog.LoadingDialog;
import com.hoperrush.core.dialog.PkDialog;
import com.hoperrush.core.socket.SocketHandler;
import com.hoperrush.core.volley.ServiceRequest;
import com.hoperrush.hockeyapp.FragmentHockeyApp;
import com.hoperrush.iconstant.Iconstant;
import com.hoperrush.pojo.Myprofile_Reviwes_Pojo;
import com.hoperrush.utils.ConnectionDetector;
import com.hoperrush.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskerReviewclass extends FragmentHockeyApp {

    private Context context;
    private SessionManager session;
    private Boolean isInternetPresent = false;
    private boolean show_progress_status = false;
    private ConnectionDetector cd;

    private SwipeRefreshLayout swipeRefreshLayout = null;

    MyProfile_Reviwes_Adapter adapter;

    private String Str_PageDateCount = "";

    private ListView listview;
    private RelativeLayout Rl_layout_nointernet, Rl_layout_main, Rl_layout_empty_list;

    private String provider_id = "";

    private String asyntask_name = "normal";
    private boolean loadingMore = false;

    private Handler mHandler;
    private LoadingDialog dialog;
    private SocketHandler socketHandler;

    private ArrayList<Myprofile_Reviwes_Pojo> reviweslist;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.activity_tasker_reviewclass, container, false);
        init(rootview);

        listview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                int threshold = 1;
                int count = listview.getCount();

                if (scrollState == SCROLL_STATE_IDLE) {
                    if (listview.getLastVisiblePosition() >= count - threshold && !(loadingMore)) {
                        if (swipeRefreshLayout.isRefreshing()) {
                            //nothing happen(code to block loadMore functionality when swipe to refresh is loading)
                        } else {
                            if (show_progress_status) {
                                ConnectionDetector cd = new ConnectionDetector(getActivity());
                                boolean isInternetPresent = cd.isConnectingToInternet();

                                if (isInternetPresent) {
                                    Rl_layout_nointernet.setVisibility(View.GONE);
                                    Rl_layout_main.setVisibility(View.VISIBLE);
                                    Rl_layout_empty_list.setVisibility(View.GONE);
                                    myprofileReviwesLoadmorePostRequest(getActivity(), Iconstant.MYPROFILE_REVIWES_URL);
                                    System.out.println("--------------reviwesloadmore-------------------" + Iconstant.MYPROFILE_REVIWES_URL);
                                } else {

                                    Rl_layout_nointernet.setVisibility(View.VISIBLE);
                                    Rl_layout_main.setVisibility(View.GONE);
                                    Rl_layout_empty_list.setVisibility(View.GONE);

                                }
                            }
                        }
                    }
                }

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                boolean enable = false;
                if (firstVisibleItem == 0) {
                    swipeRefreshLayout.setEnabled(true);
                } else {
                    swipeRefreshLayout.setEnabled(false);
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ConnectionDetector cd = new ConnectionDetector(getActivity());
                boolean isInternetPresent = cd.isConnectingToInternet();

                if (isInternetPresent) {
                    Rl_layout_main.setVisibility(View.VISIBLE);
                    Rl_layout_nointernet.setVisibility(View.GONE);


                    swipeRefreshLayout.setRefreshing(false);
                    asyntask_name = "swipe";
                    myprofileReviwesPostRequest(getActivity(), Iconstant.MYPROFILE_REVIWES_URL);
                } else {
                    swipeRefreshLayout.setRefreshing(false);

                    Rl_layout_main.setVisibility(View.GONE);
                    Rl_layout_nointernet.setVisibility(View.VISIBLE);
                    Rl_layout_empty_list.setVisibility(View.GONE);

                }
            }
        });


        return rootview;

    }


    private void init(View rootview) {
        cd = new ConnectionDetector(getActivity());
        session = new SessionManager(getActivity());
        mHandler = new Handler();
        reviweslist = new ArrayList<Myprofile_Reviwes_Pojo>();
        socketHandler = SocketHandler.getInstance(getActivity());

        HashMap<String, String> user = session.getUserDetails();
        provider_id = session.getProviderID();

        listview = (ListView) rootview.findViewById(R.id.profilereviwes_listView);
        Rl_layout_nointernet = (RelativeLayout) rootview.findViewById(R.id.layout_profilereviwes_noInternet);
        Rl_layout_main = (RelativeLayout) rootview.findViewById(R.id.layout_profilereviwes_main);
        Rl_layout_empty_list = (RelativeLayout) rootview.findViewById(R.id.layout_profilereviwes_empty);

        isInternetPresent = cd.isConnectingToInternet();

        if (isInternetPresent) {
            Rl_layout_nointernet.setVisibility(View.GONE);
            Rl_layout_main.setVisibility(View.VISIBLE);
            myprofileReviwesPostRequest(getActivity(), Iconstant.MYPROFILE_REVIWES_URL);
            System.out.println("reviwesuurl----------" + Iconstant.MYPROFILE_REVIWES_URL);
        } else {
            Rl_layout_nointernet.setVisibility(View.VISIBLE);
            Rl_layout_main.setVisibility(View.GONE);
            Rl_layout_empty_list.setVisibility(View.GONE);

        }
        swipeRefreshLayout = (SwipeRefreshLayout) rootview.findViewById(R.id.profile_reviwes_swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeRefreshLayout.setEnabled(true);


    }

    private void loadingDialog() {

        if (asyntask_name.equalsIgnoreCase("normal")) {
            dialog = new LoadingDialog(getActivity());
            dialog.setLoadingTitle(getResources().getString(R.string.loading_in));
            dialog.show();
        } else {
            swipeRefreshLayout.setRefreshing(true);
        }
    }

    private void dismissDialog() {

        if (asyntask_name.equalsIgnoreCase("normal")) {
            dialog.dismiss();
        } else {
            swipeRefreshLayout.setRefreshing(false);
        }
    }


    //--------------Alert Method-----------
    private void Alert(String title, String message) {
        final PkDialog mDialog = new PkDialog(getActivity());
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(message);
        mDialog.setPositiveButton(getResources().getString(R.string.server_ok_lable_header), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }


    private void myprofileReviwesPostRequest(Context mContext, String url) {
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("provider_id", provider_id);
        jsonParams.put("perPage", "100");

        System.out.println("provider_id-----------" + provider_id);

        loadingDialog();

        ServiceRequest mservicerequest = new ServiceRequest(mContext);

        mservicerequest.makeServiceRequest(url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {

            @Override
            public void onCompleteListener(String response) {
                Log.e("reviwesprofile", response);

                String Str_status = "", Str_response = "";
                try {
                    JSONObject jobject = new JSONObject(response);

                    Str_status = jobject.getString("status");

                    if (Str_status.equalsIgnoreCase("1")) {
                        reviweslist.clear();
                        Rl_layout_empty_list.setVisibility(View.GONE);
                        JSONObject object = jobject.getJSONObject("response");
                        Str_PageDateCount = object.getString("perPage");

                        JSONArray jarry = object.getJSONArray("rated_users");

                        if (jarry.length() > 0) {

                            for (int i = 0; i < jarry.length(); i++) {
                                JSONObject object2 = jarry.getJSONObject(i);
                                Myprofile_Reviwes_Pojo pojo = new Myprofile_Reviwes_Pojo();

                                pojo.setName(object2.getString("user_name"));
                                pojo.setRating(object2.getString("ratings"));
                                pojo.setProfilimg(object2.getString("user_image"));
                                pojo.setReviwe_description(object2.getString("comments"));
                                pojo.setRating_time(object2.getString("rating_time"));

                                String ratingimage = object2.getString("ratting_image");
                                pojo.setratingimage(object2.getString("ratting_image"));
                                reviweslist.add(pojo);

                            }
                            show_progress_status = true;
                        } else {

                            show_progress_status = false;
                        }

                    } else {


                        Str_response = jobject.getString("message");
                        Rl_layout_empty_list.setVisibility(View.VISIBLE);
                        // alert(getResources().getString(R.string.review_not_available), Str_response);
                    }

                    if (Str_status.equalsIgnoreCase("1")) {

                        adapter = new MyProfile_Reviwes_Adapter(getActivity(), reviweslist);
                        listview.setAdapter(adapter);

                        if (show_progress_status) {
                            Rl_layout_empty_list.setVisibility(View.GONE);
                        } else {
                            Rl_layout_empty_list.setVisibility(View.VISIBLE);
                            listview.setEmptyView(Rl_layout_empty_list);
                        }

                    } else {
                        //  Alert(getResources().getString(R.string.server_lable_header), Str_response);
                    }
                    if (swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                } catch (Exception e) {
                    dismissDialog();
                    e.printStackTrace();
                }

                dismissDialog();
            }

            @Override
            public void onErrorListener() {
                dismissDialog();
            }
        });

    }

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

    //--------------------------load more--------------------------------
    private void myprofileReviwesLoadmorePostRequest(Context mContext, String url) {
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("provider_id", provider_id);
        jsonParams.put("perPage", Str_PageDateCount);

        System.out.println("provider_id-----------" + provider_id);

        loadingDialog();

        ServiceRequest mservicerequest = new ServiceRequest(mContext);

        mservicerequest.makeServiceRequest(url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {

            @Override
            public void onCompleteListener(String response) {
                Log.e("reviwesprofile", response);

                String Str_status = "", Str_response = "";
                try {

                    loadingMore = true;

                    JSONObject jobject = new JSONObject(response);

                    Str_status = jobject.getString("status");
                    reviweslist.clear();

                    if (Str_status.equalsIgnoreCase("1")) {

                        JSONObject object = jobject.getJSONObject("response");
                        Str_PageDateCount = object.getString("perPage");

                        JSONArray jarry = object.getJSONArray("rated_users");

                        if (jarry.length() > 0) {

                            for (int i = 0; i < jarry.length(); i++) {
                                JSONObject object2 = jarry.getJSONObject(i);
                                Myprofile_Reviwes_Pojo pojo = new Myprofile_Reviwes_Pojo();

                                pojo.setName(object2.getString("user_name"));
                                pojo.setRating(object2.getString("ratings"));
                                pojo.setProfilimg(object2.getString("user_image"));
                                pojo.setReviwe_description(object2.getString("comments"));
                                pojo.setRating_time(object2.getString("rating_time"));
                                reviweslist.add(pojo);

                            }
                            show_progress_status = true;
                        } else {

                            show_progress_status = false;
                        }

                    } else {

                        Str_response = jobject.getString("response");
                    }

                    if (Str_status.equalsIgnoreCase("1")) {

                        adapter = new MyProfile_Reviwes_Adapter(getActivity(), reviweslist);
                        listview.setAdapter(adapter);

                        if (show_progress_status) {
                            Rl_layout_empty_list.setVisibility(View.GONE);
                        } else {
                            Rl_layout_empty_list.setVisibility(View.VISIBLE);
                            listview.setEmptyView(Rl_layout_empty_list);
                        }

                    } else {

                        Alert(getResources().getString(R.string.server_lable_header), Str_response);

                    }

                } catch (Exception e) {
                    dismissDialog();
                    e.printStackTrace();
                }
                dismissDialog();
                loadingMore = false;
            }

            @Override
            public void onErrorListener() {
                dismissDialog();

            }
        });

    }


    @Override
    public void onResume() {
        super.onResume();
      /*  if (!socketHandler.getSocketManager().isConnected){
            socketHandler.getSocketManager().connect();
        }*/
    }


}
