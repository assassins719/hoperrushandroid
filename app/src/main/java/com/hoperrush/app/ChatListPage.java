package com.hoperrush.app;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.hoperrush.R;
import com.hoperrush.adapter.ChatListAdapter;
import com.hoperrush.core.dialog.LoadingDialog;
import com.hoperrush.core.dialog.PkDialog;
import com.hoperrush.core.volley.ServiceRequest;
import com.hoperrush.hockeyapp.ActivityHockeyApp;
import com.hoperrush.iconstant.Iconstant;
import com.hoperrush.pojo.ChatListPojo;
import com.hoperrush.utils.ConnectionDetector;
import com.hoperrush.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Casperon Technology on 2/3/2016.
 */
public class ChatListPage extends ActivityHockeyApp {
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private SessionManager sessionManager;

    private RelativeLayout Rl_back;
    private ImageView Im_backIcon;
    private TextView Tv_headerTitle;

    private TextView Tv_emptyChat;
    private ListView listView;
    private ChatListAdapter adapter;
    private ArrayList<ChatListPojo> chatList;
    private String sUserID = "";

    private ServiceRequest mRequest;
    private boolean isChatAvailable = false;
    private boolean isDataAvailable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_list_page);
        initializeHeaderBar();
        initialize();

        Rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
                overridePendingTransition(R.anim.fab_scale_up, R.anim.fab_scale_down);
            }
        });
    }

    private void initializeHeaderBar() {
        RelativeLayout headerBar = (RelativeLayout) findViewById(R.id.headerBar_layout);
        Rl_back = (RelativeLayout) headerBar.findViewById(R.id.headerBar_left_layout);
        Im_backIcon = (ImageView) headerBar.findViewById(R.id.headerBar_imageView);
        Tv_headerTitle = (TextView) headerBar.findViewById(R.id.headerBar_title_textView);

        Tv_headerTitle.setText(getResources().getString(R.string.chat_list_page_label_chats));
        Im_backIcon.setImageResource(R.drawable.back_arrow);
    }

    private void initialize() {
        cd = new ConnectionDetector(ChatListPage.this);
        isInternetPresent = cd.isConnectingToInternet();
        sessionManager = new SessionManager(ChatListPage.this);
        chatList = new ArrayList<ChatListPojo>();

        listView = (ListView) findViewById(R.id.chat_list_listView);
        Tv_emptyChat = (TextView) findViewById(R.id.chat_list_empty_textView);

        // get user data from session
        HashMap<String, String> user = sessionManager.getUserDetails();
        sUserID = user.get(SessionManager.KEY_USER_ID);

        if (isInternetPresent) {
            postRequest_ChatList(Iconstant.chat_list_url);
        } else {
            alert(getResources().getString(R.string.action_no_internet_title), getResources().getString(R.string.action_no_internet_message));
        }
    }

    //------Alert Method-----
    private void alert(String title, String message) {
        final PkDialog mDialog = new PkDialog(ChatListPage.this);
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


    //-----------------------ChatList Post Request-----------------
    private void postRequest_ChatList(String Url) {

        final LoadingDialog mLoading = new LoadingDialog(ChatListPage.this);
        mLoading.setLoadingTitle(getResources().getString(R.string.action_loading));
        mLoading.show();

        System.out.println("-------------ChatList Url----------------" + Url);
        System.out.println("-------------ChatList user_id----------------" + sUserID);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("id", sUserID);
        jsonParams.put("user_type", "user");

        mRequest = new ServiceRequest(ChatListPage.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("-------------ViewProfile Response----------------" + response);

                String sStatus = "";
                try {
                    JSONObject object = new JSONObject(response);
                    sStatus = object.getString("status");
                    if (sStatus.equalsIgnoreCase("1")) {

                        Object check_response_object = object.get("response");
                        if (check_response_object instanceof JSONObject) {

                            JSONObject response_object = object.getJSONObject("response");
                            if (response_object.length() > 0) {

                                Object check_list_object = response_object.get("list");
                                if (check_list_object instanceof JSONArray) {
                                    JSONArray list_array = response_object.getJSONArray("list");
                                    if (list_array.length() > 0) {
                                        chatList.clear();
                                        for (int i = 0; i < list_array.length(); i++) {
                                            JSONObject list_object = list_array.getJSONObject(i);
                                            ChatListPojo pojo = new ChatListPojo();
                                            pojo.setUserName(list_object.getString("name"));
                                            pojo.setUserImage(list_object.getString("image"));
                                            pojo.setProviderId(list_object.getString("p_id"));
                                            pojo.setJobId(list_object.getString("job_id"));
                                            pojo.setMessage(list_object.getString("msg"));
                                            pojo.setMessageTime(list_object.getString("msg_time"));

                                            chatList.add(pojo);
                                        }
                                        isChatAvailable = true;
                                    } else {
                                        isChatAvailable = false;
                                    }
                                } else {
                                    isChatAvailable = false;
                                }
                                isDataAvailable = true;
                            } else {
                                isDataAvailable = false;
                            }
                        } else {
                            isDataAvailable = false;
                        }

                    } else {
                        String sResponse = object.getString("response");
                        alert(getResources().getString(R.string.action_sorry), sResponse);
                    }

                    if (sStatus.equalsIgnoreCase("1") && isDataAvailable) {
                        if (isChatAvailable) {
                            if (chatList.size() > 0) {
                                Tv_emptyChat.setVisibility(View.GONE);
                                adapter = new ChatListAdapter(ChatListPage.this, chatList);
                                listView.setAdapter(adapter);
                            } else {
                                Tv_emptyChat.setVisibility(View.VISIBLE);
                            }
                        } else {
                            Tv_emptyChat.setVisibility(View.VISIBLE);
                        }
                    }

                } catch (JSONException e) {
                    mLoading.dismiss();
                    e.printStackTrace();
                }
                mLoading.dismiss();
            }

            @Override
            public void onErrorListener() {
                mLoading.dismiss();
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
            onBackPressed();
            finish();
            overridePendingTransition(R.anim.fab_scale_up, R.anim.fab_scale_down);
            return true;
        }
        return false;
    }
}
