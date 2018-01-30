package com.hoperrush.app;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.hoperrush.R;
import com.hoperrush.adapter.ChatAdapter;
import com.hoperrush.core.dialog.LoadingDialog;
import com.hoperrush.core.dialog.PkDialog;
import com.hoperrush.core.socket.ChatMessageService;
import com.hoperrush.core.socket.ChatMessageSocketManager;
import com.hoperrush.core.volley.ServiceRequest;
import com.hoperrush.core.widgets.RoundedImageView;
import com.hoperrush.hockeyapp.ActivityHockeyApp;
import com.hoperrush.iconstant.Iconstant;
import com.hoperrush.pojo.ChatPojo;
import com.hoperrush.pojo.ReceiveMessageEvent;
import com.hoperrush.pojo.SendMessageEvent;
import com.hoperrush.utils.ConnectionDetector;
import com.hoperrush.utils.HideSoftKeyboard;
import com.hoperrush.utils.SessionManager;
import com.hoperrush.utils.TouchImageView;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 */
public class ChatPage extends ActivityHockeyApp implements View.OnClickListener {
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private SessionManager sessionManager;
    private static Context mContext;
    public static String Chat_Id = "";
    public static String task_id = "";
    private RelativeLayout Rl_back;
    private TextView Tv_senderName;
    private TextView chat_state;
    private static TextView Tv_status;
    private RoundedImageView Iv_senderImage;
    public static boolean isChatPageAvailable;
    private static ListView listView;
    private EditText Et_message;
    private ImageView Iv_send;
    private RelativeLayout Rl_ActiveChat, Rl_deActiveChat;
    public static Activity chat_activity;
    private String sSenderName = "", sSenderID = "", sSenderImage = "";
    private static String sReceiverStatus = "online";
    private String sChatStatus = "open";
    private String sJobID = "";
    private String sToID = "";
    private String sUserID = "", sTaskerId = "", STaskId = "";
    private MediaPlayer mediaPlayer;

    private ServiceRequest mRequest;
    private LoadingDialog mLoadingDialog;

    private static ChatAdapter adapter;
    private static ArrayList<ChatPojo> chatList;
    SimpleDateFormat df_time = new SimpleDateFormat("hh:mm a");
    private String providerID = "new message";
    public static boolean chatnotification = false;
    String data = "";
    String jsonUSER = "";
    String fromID = "", msgid = "",chat_msg_id="",send_msg_id = "";;
    String task = "";
    String tasker = "", aUserStatusStr = "";
    private boolean isDataAvailable = false, isSenderAvailable = false, isReceiverAvailable = false, isChatAvailable = false;
    String resume_user_status="";
    /*Code to receive message*/
    JSONObject message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_page);
        mContext = ChatPage.this;
        ChatPage.isChatPageAvailable = true;
        mediaPlayer = MediaPlayer.create(this, Settings.System.DEFAULT_NOTIFICATION_URI);
        chatnotification = true;
        chat_activity = ChatPage.this;
        initialize();

        Rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ChatMessageService.tasker_id="";
                ChatMessageService.task_id="";
                if(isInternetPresent){
                    postRequest_ChatDetail(Iconstant.chat_detail_url);
                }

                Intent i = new Intent();
                i.setAction("com.refresh.message");
                sendBroadcast(i);
                HideSoftKeyboard.hideSoftKeyboard(ChatPage.this);
                ChatPage.isChatPageAvailable = false;
                finish();
            }
        });

        Iv_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = Et_message.getText().toString();
                try {
                    JSONObject object = new JSONObject();
                    object.put("user", sUserID);
                    object.put("tasker", sTaskerId);
                    object.put("message", data);
                    object.put("task", STaskId);
                    object.put("from", sUserID);
                    // text.setText(object.toString());

                    System.out.println("user------" + sUserID);
                    System.out.println("tasker------" + sTaskerId);
                    System.out.println("message------" + data);
                    System.out.println("task------" + STaskId);
                    System.out.println("from------" + sUserID);
                    SendMessageEvent sendMessageEvent = new SendMessageEvent();
                    sendMessageEvent.setEventName(ChatMessageSocketManager.EVENT_NEW_MESSAGE);
                    sendMessageEvent.setMessageObject(object);
                    EventBus.getDefault().post(sendMessageEvent);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Et_message.getText().clear();
            }
        });
    }


    public void loadChatHistory() {
        String url = Iconstant.BaseUrl + "chat/chathistory";
        mRequest = new ServiceRequest(ChatPage.this);
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user", sUserID);
        jsonParams.put("tasker", sTaskerId);
        jsonParams.put("task", STaskId);
        jsonParams.put("type", "user");
        jsonParams.put("read_status", "user");
        chatList.clear();
        mRequest.makeServiceRequest(url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {

            @Override
            public void onCompleteListener(String response) {
                try {
                    JSONObject responseObject = new JSONObject(response);
                    JSONArray arrayObject = responseObject.getJSONArray("messages");
                    JSONObject user = responseObject.getJSONObject("user");
                    JSONObject tasker = responseObject.getJSONObject("tasker");
                    String userAvatar = (String) user.get("avatar");
                    String taskerAvatar = (String) tasker.get("avatar");
                    for (int i = 0; i < arrayObject.length(); i++) {
                        JSONObject msgObject = (JSONObject) arrayObject.get(i);
                        String fromID = msgObject.getString("from");
                        String data = msgObject.getString("message");
                        ChatPojo pojo = new ChatPojo();
                        pojo.setMessage(data);
                        pojo.setDate(msgObject.getString("date"));
                        if (fromID != null && fromID.equalsIgnoreCase(sUserID)) {
                            pojo.setUrl(userAvatar);
                            pojo.setType("SELF");
                            pojo.setSeenStatus(msgObject.getString("tasker_status"));
                        } else {
                            pojo.setUrl(taskerAvatar);
                            pojo.setType("OTHER");
                        }
                        pojo.setTime("");
                        chatList.add(pojo);
                    }
                } catch (Exception e) {
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onErrorListener() {

            }
        });
    }

    //-----------------------------------------------------------Receive Chat Via Socket Bus------------------------------------------


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ReceiveMessageEvent event) {

        String eventName = event.getEventName();

        switch (eventName) {

            case ChatMessageSocketManager.EVENT_UPDATE_CHAT:
                addOtherMessage(event.getObjectsArray());
                break;

            case ChatMessageSocketManager.EVENT_TYPING:
                Tv_status.setText("typing...");
                break;
            case ChatMessageSocketManager.EVENT_STOP_TYPING:
                Tv_status.setText("");
                break;

            case ChatMessageSocketManager.EVENT_SINGLE_MESSAGE_STATUS:
                addOtherMessage(event.getObjectsArray());
                break;
        }
    }

    public void addOtherMessage(Object[] response) {
        ChatPojo pojo = new ChatPojo();

        try {
            JSONObject object = new JSONObject(response[0].toString());
            jsonUSER = object.getString("user");
            task = object.getString("task");
            tasker = object.getString("tasker");
            if (object.has("messages")) {
                JSONArray messagesArray = object.getJSONArray("messages");
                message = (JSONObject) messagesArray.get(0);
                data = (String) message.getString("message");
                fromID = message.getString("from");
                msgid = (String) message.getString("_id");
                resume_user_status="normal";
            } else {
                aUserStatusStr = object.getString("tasker_status");
                resume_user_status=object.getString("status");
                pojo.setSeenStatus(object.getString("tasker_status"));
                pojo.setDate(getCurrentDateTime());
                pojo.setMessage(object.getString("message"));
                Log.e("size", "" + chatList.size());
                for (int i = 0; i < chatList.size(); i++) {
                    chatList.get(i).setSeenStatus("2");
                }
                adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
        }

        pojo.setMessage(data);

        if (sUserID != null && sUserID.equalsIgnoreCase(fromID)) {

            pojo.setType("SELF");
            if (!msgid.equalsIgnoreCase(send_msg_id)) {
                pojo.setDate(getCurrentDateTime());
                pojo.setSeenStatus("1");
                chatList.add(pojo);
                send_msg_id = msgid;
            }

        }

        else if (data.equalsIgnoreCase("")) {

            pojo.setType("TYPING");
        } else {
            pojo.setType("OTHER");
            pojo.setDate(getCurrentDateTime());
        }
        if (!fromID.equalsIgnoreCase("") && !jsonUSER.equalsIgnoreCase(fromID) && !resume_user_status.equalsIgnoreCase("resume")) {
            if (mediaPlayer != null) {
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                }
            }

        }

        pojo.setTime("");

        if (!pojo.getType().equalsIgnoreCase("TYPING") && !pojo.getType().equalsIgnoreCase("SELF") &&
                Chat_Id.equalsIgnoreCase(tasker) && task_id.equalsIgnoreCase(task)
                && !resume_user_status.equalsIgnoreCase("resume") && !chat_msg_id.equalsIgnoreCase(msgid)) {
            sendSingleMessageStatus();
            try {
                chat_msg_id = (String) message.getString("_id");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            chatList.add(pojo);

        }
        adapter.notifyDataSetChanged();
        listView.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                listView.setSelection(adapter.getCount() - 1);
            }
        });


    }

    private String getCurrentDateTime() {
        String aCurrentDateStr = "";
        try {
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy, hh:mm a");
            aCurrentDateStr = df.format(c.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return aCurrentDateStr;
    }


    private void initialize() {
        cd = new ConnectionDetector(ChatPage.this);
        isInternetPresent = cd.isConnectingToInternet();
        sessionManager = new SessionManager(ChatPage.this);
        //ChatSocketService.disconnect();
        chatList = new ArrayList<ChatPojo>();
        Rl_back = (RelativeLayout) findViewById(R.id.chatPage_headerBar_back_layout);
        Tv_senderName = (TextView) findViewById(R.id.chatPage_headerBar_senderName_status);
        Iv_senderImage = (RoundedImageView) findViewById(R.id.chatPage_header_senderImage);
        listView = (ListView) findViewById(R.id.chatPage_listView);
        Et_message = (EditText) findViewById(R.id.chatPage_message_editText);

        Iv_send = (ImageView) findViewById(R.id.chatPage_send_imageView);
        //chat_state = (TextView) findViewById(R.id.chatPage_headerBar_senderName_status);
        Rl_ActiveChat = (RelativeLayout) findViewById(R.id.chatPage_bottom_layout);
        Rl_deActiveChat = (RelativeLayout) findViewById(R.id.chatPage_noChat_layout);
        Et_message.addTextChangedListener(chatEditorWatcher);
        Tv_status = (TextView) findViewById(R.id.chatPage_headerBar_senderName_textView);
        // get user data from session
        HashMap<String, String> user = sessionManager.getUserDetails();
        sUserID = user.get(SessionManager.KEY_USER_ID);

        Intent intent = getIntent();

        if (intent != null) {
            sJobID = intent.getStringExtra("JobID-Intent");
            System.out.println("-----------Intent sJobID------------" + sJobID);


            sTaskerId = ChatMessageService.tasker_id;
            STaskId =   ChatMessageService.task_id;

            System.out.println("TaskerId-----------" + sTaskerId);


            System.out.println("STaskId-----------" + STaskId);
            if (sTaskerId.equalsIgnoreCase("")) {

                sTaskerId = intent.getStringExtra("TaskerId");
                STaskId = intent.getStringExtra("TaskId");
            }

            sessionManager.setchatuserid(sTaskerId);
            sessionManager.setchattaskid(STaskId);

            HashMap<String, String> chatid = sessionManager.getUserDetails();
            Chat_Id = chatid.get(SessionManager.KEY_Chat_userid);
            HashMap<String, String> taskids = sessionManager.getUserDetails();
            task_id = taskids.get(SessionManager.KEY_TASK_ID);

            if (isInternetPresent) {
                postRequest_ChatDetail(Iconstant.chat_detail_url);
            } else {
                alert(getResources().getString(R.string.action_no_internet_title), getResources().getString(R.string.action_no_internet_message));
            }
        }
        adapter = new ChatAdapter(ChatPage.this, chatList);
        listView.setAdapter(adapter);
        clickListener();
    }

    /**
     * Method for click listeners
     */
    private void clickListener() {
        Iv_senderImage.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chatPage_header_senderImage:
                showProfileImgInDialog();
                break;
        }

    }


    /**
     * Method for show the profile image in the dialog
     */
    private void showProfileImgInDialog() {
        try {
            Dialog aDialog = new Dialog(this);
            aDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            aDialog.setContentView(getLayoutInflater().inflate(R.layout.appointment_profile_imageview
                    , null));
            TouchImageView aProfileIMG = (TouchImageView) aDialog.findViewById(R.id.appointment_profile_imageview_IMG);
            Picasso.with(ChatPage.this).load(sSenderImage).error(R.drawable.placeholder_icon)
                    .placeholder(R.drawable.placeholder_icon).memoryPolicy(MemoryPolicy.NO_CACHE).into(aProfileIMG);
            aDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //--------Alert Method------
    private void alert(String title, String alert) {

        final PkDialog mDialog = new PkDialog(ChatPage.this);
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


    //---Scroll ListView to bottom---
    private static void scrollMyListViewToBottom() {
        listView.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                if (adapter.getCount() > 2)
                    listView.setSelection(adapter.getCount() - 1);
            }
        });
    }


    //-----------Code for TextWatcher----------
    private TextWatcher chatEditorWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {


            String messages = Et_message.getText().toString();
            String message = messages.trim();

            if (message.length() == 0) {
                Iv_send.setImageResource(R.drawable.send_icon_dim);
                Iv_send.setEnabled(false);
            } else {
                Iv_send.setImageResource(R.drawable.send_icon_dark);
                Iv_send.setEnabled(true);
            }
            if (Et_message.getText().toString().length() > 0) {
                showTyping();
            } else {
                hideTyping();
            }

        }
    };


    //----Show typing text----
    private void showTyping() {
        try {
            JSONObject object = new JSONObject();
            object.put("user", sUserID);
            object.put("tasker", sTaskerId);
            object.put("task", STaskId);
            object.put("from", sUserID);
            object.put("to", sTaskerId);
            object.put("type", "type");
            SendMessageEvent event = new SendMessageEvent();
            event.setEventName(ChatMessageSocketManager.EVENT_TYPING);
            event.setMessageObject(object);

            EventBus.getDefault().post(event);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    //----Hide typing text----
    private void hideTyping() {
        try {
            JSONObject object = new JSONObject();
            object.put("user", sUserID);
            object.put("tasker", sTaskerId);
            object.put("task", STaskId);
            object.put("from", sUserID);
            object.put("to", sTaskerId);
            object.put("type", "stop");
            SendMessageEvent event = new SendMessageEvent();
            event.setEventName(ChatMessageSocketManager.EVENT_STOP_TYPING);
            event.setMessageObject(object);

            EventBus.getDefault().post(event);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    //-----------------------Chat Detail Post Request-----------------
    private void postRequest_ChatDetail(String Url) {

        mLoadingDialog = new LoadingDialog(ChatPage.this);
        mLoadingDialog.setLoadingTitle(getResources().getString(R.string.action_loading));
        mLoadingDialog.show();

        System.out.println("-------------Chat Detail Url----------------" + Url);

        System.out.println("-----------id------------" + sUserID);
        System.out.println("-----------job_id------------" + sJobID);


        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user", sUserID);
        jsonParams.put("tasker", sTaskerId);
        jsonParams.put("task", STaskId);
        jsonParams.put("type", "user");
        jsonParams.put("read_status", "user");

        mRequest = new ServiceRequest(ChatPage.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("------------Chat Detail Response----------------" + response);

                String sStatus = "";
                try {
                    JSONObject object = new JSONObject(response);
                    sStatus = object.getString("status");
                    if (sStatus.equalsIgnoreCase("1")) {

                        Object check_response_object = object.get("tasker");
                        if (check_response_object instanceof JSONObject) {
                            JSONObject response_object = object.getJSONObject("tasker");
                            if (response_object.length() > 0) {

//                                Object check_receiver_object = response_object.get("receiver");
//                                if (check_receiver_object instanceof JSONObject) {
//                                    JSONObject receiver_object = response_object.getJSONObject("receiver");
//                                    if (receiver_object.length() > 0) {
                                sSenderID = response_object.getString("_id");
                                sSenderName = response_object.getString("username");
                                sSenderImage = response_object.getString("avatar");

                                isReceiverAvailable = true;
                                isChatAvailable = true;
                                isDataAvailable = true;
                            } else {
                                isReceiverAvailable = false;
                                isDataAvailable = false;
                                isChatAvailable = false;
                            }

//                            }
                        } else {
                            isDataAvailable = false;
                        }
                    }


                    if (sStatus.equalsIgnoreCase("1")) {
                        if (isDataAvailable) {
                            if (isChatAvailable) {
                                if (sReceiverStatus.equalsIgnoreCase("online") && sChatStatus.equalsIgnoreCase("open")) {
                                    Rl_ActiveChat.setVisibility(View.VISIBLE);
                                    Rl_deActiveChat.setVisibility(View.GONE);
                                    Tv_status.setText("");
                                    Tv_status.setTextColor(Color.parseColor("#FFFFFF"));
                                    Tv_status.setVisibility(View.VISIBLE);
                                } else {
                                    Rl_ActiveChat.setVisibility(View.GONE);
                                    Rl_deActiveChat.setVisibility(View.VISIBLE);
                                    Tv_status.setText(getResources().getString(R.string.chat_page_label_offline));
                                    Tv_status.setTextColor(Color.parseColor("#C5C5C5"));
                                    Tv_status.setVisibility(View.VISIBLE);
                                }

                                if (isReceiverAvailable) {

                                    sToID = sSenderID + "@" + Iconstant.XMPP_SERVICE_NAME;
                                    Tv_senderName.setText(sSenderName);
                                    Picasso.with(ChatPage.this).load(sSenderImage).error(R.drawable.placeholder_icon)
                                            .placeholder(R.drawable.placeholder_icon).memoryPolicy(MemoryPolicy.NO_CACHE).into(Iv_senderImage);
                                } else {
                                    final PkDialog mDialog = new PkDialog(ChatPage.this);
                                    mDialog.setDialogTitle(getResources().getString(R.string.action_sorry));
                                    mDialog.setDialogMessage(getResources().getString(R.string.chat_page_label_server_error));
                                    mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            mDialog.dismiss();
                                            onBackPressed();
                                            finish();
                                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                        }
                                    });
                                    mDialog.show();
                                }
                            }
                        }
                    } else {

                    }


                } catch (JSONException e) {
                    mLoadingDialog.dismiss();
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
    protected void onResume() {

        if (!ChatMessageService.isStarted()) {
            Intent intent = new Intent(ChatPage.this, ChatMessageService.class);
            startService(intent);
        }
        ChatPage.isChatPageAvailable = true;
        loadChatHistory();
        sendMessageStatus();
        sendSingleMessageStatus1();
        super.onResume();
    }

    private void sendMessageStatus() {
        try {
            JSONObject aObject = new JSONObject();
            aObject.put("user", sUserID);
            aObject.put("tasker", sTaskerId);
            aObject.put("task", STaskId);
            aObject.put("type", "user");
            SendMessageEvent event = new SendMessageEvent();
            event.setEventName(ChatMessageSocketManager.EVENT_MESSAGE_STATUS);
            event.setMessageObject(aObject);

            EventBus.getDefault().post(event);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendSingleMessageStatus() {
        try {
            JSONObject aObject = new JSONObject();
            aObject.put("user", sUserID);
            aObject.put("tasker", sTaskerId);
            aObject.put("task", STaskId);
            aObject.put("usertype", "user");
            aObject.put("status", "normal");
            aObject.put("currentuserid", Chat_Id);
            aObject.put("_id", msgid);
            aObject.put("from", fromID);
            aObject.put("user_status", "2");
            aObject.put("message", data);
            SendMessageEvent event = new SendMessageEvent();
            event.setEventName(ChatMessageSocketManager.EVENT_SINGLE_MESSAGE_STATUS);
            event.setMessageObject(aObject);

            EventBus.getDefault().post(event);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void sendSingleMessageStatus1() {
        try {
            JSONObject aObject = new JSONObject();
            aObject.put("user", sUserID);
            aObject.put("tasker", sTaskerId);
            aObject.put("task", STaskId);
            aObject.put("usertype", "user");
            aObject.put("status", "resume");
            aObject.put("currentuserid", Chat_Id);
            aObject.put("_id", msgid);
            aObject.put("from", fromID);
            aObject.put("user_status", "2");
            aObject.put("message", data);
            SendMessageEvent event = new SendMessageEvent();
            event.setEventName(ChatMessageSocketManager.EVENT_SINGLE_MESSAGE_STATUS);
            event.setMessageObject(aObject);

            EventBus.getDefault().post(event);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(ChatPage.this);
        ChatPage.isChatPageAvailable = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(ChatPage.this);
        ChatPage.isChatPageAvailable = false;
    }

    @Override
    protected void onPause() {
        ChatPage.isChatPageAvailable = false;
        super.onPause();

    }

    @Override
    protected void onDestroy() {
      System.out.println("manager got disconnected");
        super.onDestroy();

    }


    //-----------------Move Back on pressed phone back button------------------
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)) {

            ChatMessageService.tasker_id="";
            ChatMessageService.task_id="";

            return true;
        }
        return false;
    }


    //-----------------------------------------------Notification Mode-----------------------------------------------------------------
    //-----------------------Chat Detail Post Request-----------------
    private void postRequest_Notificationmode(String Url) {

        mLoadingDialog = new LoadingDialog(ChatPage.this);
        mLoadingDialog.setLoadingTitle(getResources().getString(R.string.action_loading));
        mLoadingDialog.show();

        System.out.println("-------------Chat Detail Url----------------" + Url);

        System.out.println("-----------id------------" + sUserID);
        System.out.println("-----------job_id------------" + sJobID);


        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user", sUserID);
        jsonParams.put("user_type", "user");
        jsonParams.put("mode", "gcm");
        jsonParams.put("type", "android");

        mRequest = new ServiceRequest(ChatPage.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("------------Chat Detail Response----------------" + response);

                String sStatus = "";
                try {
                    JSONObject object = new JSONObject(response);
                    sStatus = object.getString("status");


                } catch (JSONException e) {
                    mLoadingDialog.dismiss();
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


    //------------------------------------------Notification Mode--------------------------------------------------------------------------

}

