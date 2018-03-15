package com.hoperrush.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.hoperrush.R;
import com.hoperrush.adapter.HomeMenuListAdapter;
import com.hoperrush.core.dialog.LoadingDialog;
import com.hoperrush.core.socket.ChatMessageService;
import com.hoperrush.core.socket.SocketHandler;
import com.hoperrush.core.volley.ServiceRequest;
import com.hoperrush.fragment.Fragment_New_Map_HomePage;
import com.hoperrush.hockeyapp.ActionBarActivityHockeyApp;
import com.hoperrush.core.dialog.PkDialog;
import com.hoperrush.iconstant.Iconstant;
import com.hoperrush.utils.AndroidServiceStartOnBoot;
import com.hoperrush.utils.ConnectionDetector;
import com.hoperrush.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * Casperon Technology on 12/8/2015.
 */
public class NavigationDrawer extends ActionBarActivityHockeyApp {
    private ActionBarDrawerToggle actionBarDrawerToggle;
    static DrawerLayout drawerLayout;
    private static RelativeLayout mDrawer;
    private Context context;
    private ListView mDrawerList;
    private static HomeMenuListAdapter mMenuAdapter;
    private String[] title;
    private int[] icon;
    String Appinfoemail = "";
    private String UserID = "";
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private ServiceRequest mRequest;
    LoadingDialog mLoadingDialog;
    Fragment homePage = new Fragment_New_Map_HomePage();
    private SessionManager session;

    public static NavigationDrawer navigationDrawerClass;


    public class RefreshReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.package.NAVIGATIONUPDATE_REFRESH")) {
                session = new SessionManager(NavigationDrawer.this);
                if (session.isLoggedIn()) {

                    title = new String[]{"username", getResources().getString(R.string.navigation_label_home),
                            getResources().getString(R.string.navigation_label_myOrders),
                            getResources().getString(R.string.navigation_label_money),
                            getResources().getString(R.string.navigation_label_transactions1),
                            getResources().getString(R.string.navigation_label_chat),
                            getResources().getString(R.string.navigation_label_notification1),
                            getResources().getString(R.string.navigation_label_review1),
                            getResources().getString(R.string.navigation_label_invite),
                            getResources().getString(R.string.navigation_label_emergency),
                            getResources().getString(R.string.navigation_label_report_issue),
                            getResources().getString(R.string.navigation_label_aboutUs),
                            "Terms and Condition",
                            "Privacy Policy",
                            getResources().getString(R.string.navigation_label_logout),

                    };

                    icon = new int[]{R.drawable.no_profile_image_avatar_icon, R.drawable.home_icon,
                            R.drawable.my_orders_icon, R.drawable.plumbal_money,R.drawable.icon_menu_transaction, R.drawable.icon_menu_chat,R.drawable.icon_menu_notification, R.drawable.icon_menu_review,
                            R.drawable.invite_and_earn, R.drawable.emergency_contact, R.drawable.report_issue_icon, R.drawable.aboutus_icon,
                            R.drawable.aboutus_icon,R.drawable.aboutus_icon,R.drawable.logout};
                    mMenuAdapter = new HomeMenuListAdapter(context, title, icon);
                    mDrawerList.setAdapter(mMenuAdapter);
                    mMenuAdapter.notifyDataSetChanged();
                } else {
                    title = new String[]{"SignIn", getResources().getString(R.string.navigation_label_home)};
                    icon = new int[]{R.drawable.no_profile_image_avatar_icon
                            , R.drawable.home_icon};

                    mMenuAdapter = new HomeMenuListAdapter(context, title, icon);
                    mDrawerList.setAdapter(mMenuAdapter);
                    mMenuAdapter.notifyDataSetChanged();
                }


            }
        }
    }

    private RefreshReceiver refreshReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_drawer);
        navigationDrawerClass = NavigationDrawer.this;
        context = getApplicationContext();
        session = new SessionManager(NavigationDrawer.this);
        HashMap<String, String> infomail = session.getUserDetails();
        Appinfoemail = infomail.get(SessionManager.KEY_Appinfo_email);

        HashMap<String, String> user = session.getUserDetails();
        UserID = user.get(SessionManager.KEY_USER_ID);
        // -----code to refresh drawer using broadcast receiver-----
        refreshReceiver = new RefreshReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.package.NAVIGATIONUPDATE_REFRESH");
        registerReceiver(refreshReceiver, intentFilter);
        drawerLayout = (DrawerLayout) findViewById(R.id.navigation_drawer);
        mDrawer = (RelativeLayout) findViewById(R.id.drawer);
        mDrawerList = (ListView) findViewById(R.id.drawer_listView);

        if (savedInstanceState == null) {
            FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
            tx.replace(R.id.content_frame, new Fragment_New_Map_HomePage());
            tx.commit();
        }

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.app_name, R.string.app_name);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();


        if (session.isLoggedIn()) {

            if(!ChatMessageService.isStarted()) {
                Intent intent = new Intent(NavigationDrawer.this, ChatMessageService.class);
                startService(intent);
            }

            title = new String[]{"username", getResources().getString(R.string.navigation_label_home),
                    getResources().getString(R.string.navigation_label_myOrders),
                    getResources().getString(R.string.navigation_label_money),
                    getResources().getString(R.string.navigation_label_transactions1),
                    getResources().getString(R.string.navigation_label_chat),
                    getResources().getString(R.string.navigation_label_notification1),
                    getResources().getString(R.string.navigation_label_review1),
                    getResources().getString(R.string.navigation_label_invite),
                    getResources().getString(R.string.navigation_label_emergency),
                    getResources().getString(R.string.navigation_label_report_issue),
                    "Terms and Condition",
                    getResources().getString(R.string.navigation_label_aboutUs),
                    getResources().getString(R.string.navigation_label_logout),
            };

            icon = new int[]{R.drawable.no_profile_image_avatar_icon, R.drawable.home_icon,
                    R.drawable.my_orders_icon, R.drawable.plumbal_money,R.drawable.icon_menu_transaction, R.drawable.icon_menu_chat,R.drawable.icon_menu_notification, R.drawable.icon_menu_review,
                    R.drawable.invite_and_earn, R.drawable.emergency_contact, R.drawable.report_issue_icon, R.drawable.aboutus_icon,
                    R.drawable.aboutus_icon,R.drawable.logout};

            mMenuAdapter = new HomeMenuListAdapter(context, title, icon);
            mDrawerList.setAdapter(mMenuAdapter);
            mMenuAdapter.notifyDataSetChanged();
        } else {
            title = new String[]{"SignIn", getResources().getString(R.string.navigation_label_home)};
            icon = new int[]{R.drawable.no_profile_image_avatar_icon
                    , R.drawable.home_icon};

            mMenuAdapter = new HomeMenuListAdapter(context, title, icon);
            mDrawerList.setAdapter(mMenuAdapter);
            mMenuAdapter.notifyDataSetChanged();
        }
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                cd = new ConnectionDetector(NavigationDrawer.this);
                isInternetPresent = cd.isConnectingToInternet();

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

                switch (position) {

                    case 0:
                        if (session.isLoggedIn()) {
                            Intent profile_intent = new Intent(NavigationDrawer.this, UserProfilePage.class);
                            startActivity(profile_intent);
                            overridePendingTransition(R.anim.enter, R.anim.exit);
                        } else {
                            Intent profile_intent = new Intent(NavigationDrawer.this, LogInPage.class);
                            profile_intent.putExtra("IntentClass", "1");
                            startActivity(profile_intent);
                            overridePendingTransition(R.anim.enter, R.anim.exit);
                        }

                        break;
                    case 1:
                        ft.replace(R.id.content_frame, homePage);
                        break;
                    case 2:
                        if (isInternetPresent) {
                            Intent myJob_intent = new Intent(NavigationDrawer.this, MyJobs.class);
                            myJob_intent.putExtra("status","open");
                            startActivity(myJob_intent);
                            overridePendingTransition(R.anim.enter, R.anim.exit);
                        } else {
                            alert(getResources().getString(R.string.action_no_internet_title), getResources().getString(R.string.action_no_internet_message));
                        }
                        break;

                    case 3:
                        if (isInternetPresent) {
                            Intent plumbalMoney_intent = new Intent(NavigationDrawer.this, WalletMoney.class);
                            startActivity(plumbalMoney_intent);
                            overridePendingTransition(R.anim.enter, R.anim.exit);
                        } else {
                            alert(getResources().getString(R.string.action_no_internet_title), getResources().getString(R.string.action_no_internet_message));
                        }
                        break;
                    case 4:
                        if (isInternetPresent) {

                            Intent aMessageIntent = new Intent(NavigationDrawer.this, TransactionTabActivity.class);
                            startActivity(aMessageIntent);
                            overridePendingTransition(R.anim.enter, R.anim.exit);

//                            Intent aMessageIntent = new Intent(NavigationDrawer.this, TransactionMenuActivity.class);
//                            startActivity(aMessageIntent);
//                            overridePendingTransition(R.anim.enter, R.anim.exit);
                        } else {
                            alert(getResources().getString(R.string.action_no_internet_title), getResources().getString(R.string.action_no_internet_message));
                        }
                        break;

                    case 5:
                        if (isInternetPresent) {
                            Intent aMessageIntent = new Intent(NavigationDrawer.this, GetMessageChatActivity.class);
                            startActivity(aMessageIntent);
                            overridePendingTransition(R.anim.enter, R.anim.exit);
                        } else {
                            alert(getResources().getString(R.string.action_no_internet_title), getResources().getString(R.string.action_no_internet_message));
                        }
                        break;
                    case 6:
                        if (isInternetPresent) {
                            Intent invite_intent = new Intent(NavigationDrawer.this, NotificationMenuActivity.class);
                            startActivity(invite_intent);
                            overridePendingTransition(R.anim.enter, R.anim.exit);
                        } else {
                            alert(getResources().getString(R.string.action_no_internet_title), getResources().getString(R.string.action_no_internet_message));
                        }
                        break;
                    case 7:
                        if (isInternetPresent) {
                            Intent invite_intent = new Intent(NavigationDrawer.this, ReviewMenuActivity.class);
                            startActivity(invite_intent);
                            overridePendingTransition(R.anim.enter, R.anim.exit);
                        } else {
                            alert(getResources().getString(R.string.action_no_internet_title), getResources().getString(R.string.action_no_internet_message));
                        }
                        break;
                    case 8:
                        if (isInternetPresent) {
                            Intent invite_intent = new Intent(NavigationDrawer.this, InviteAndEarn.class);
                            startActivity(invite_intent);
                            overridePendingTransition(R.anim.enter, R.anim.exit);
                        } else {
                            alert(getResources().getString(R.string.action_no_internet_title), getResources().getString(R.string.action_no_internet_message));
                        }
                        break;
                    case 9:
                        if (isInternetPresent) {
                            Intent emergency_intent = new Intent(NavigationDrawer.this, EmergencyContact.class);
                            startActivity(emergency_intent);
                            overridePendingTransition(R.anim.enter, R.anim.exit);
                        } else {
                            alert(getResources().getString(R.string.action_no_internet_title), getResources().getString(R.string.action_no_internet_message));
                        }
                        break;
                    case 10:
                        //sendEmail();
                        shareToGMail("quickrabbit@gmail.com", "Report on Hoper Rush Android App", "Sent from my Android");
                        break;

                    case 11:
                        Intent intentTerm = new Intent(NavigationDrawer.this, Terms_Conditions.class);
                        startActivity(intentTerm);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                        break;
/*                    case 12:
                        Intent intentPolicy = new Intent(NavigationDrawer.this, Privacy_Policy.class);
                        startActivity(intentPolicy);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                        break;*/
                    case 12:
                        if (isInternetPresent) {
//                            Intent plumbalproviders_intent = new Intent(NavigationDrawer.this, AboutUs.class);
//                            startActivity(plumbalproviders_intent);

                            String url = Iconstant.Aboutus_Url;
                            Intent intent = new Intent(NavigationDrawer.this, AboutUs.class);
                            intent.putExtra("url",url);
                            intent.putExtra("header", getResources().getString(R.string.about_text));
                            startActivity(intent);
                            overridePendingTransition(R.anim.enter, R.anim.exit);

                        } else {
                            alert(getResources().getString(R.string.action_no_internet_title), getResources().getString(R.string.action_no_internet_message));
                        }
                        break;
                    case 13:
                        if (isInternetPresent) {

                            final PkDialog mDialog = new PkDialog(NavigationDrawer.this);
                            mDialog.setDialogTitle(getResources().getString(R.string.profile_page_signOut_textView));
                            mDialog.setDialogMessage(getResources().getString(R.string.profile_label_logout_message));
                            mDialog.setPositiveButton(getResources().getString(R.string.profile_label_logout_no), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mDialog.dismiss();



                                }
                            });
                            mDialog.setNegativeButton(getResources().getString(R.string.profile_label_logout_yes), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mDialog.dismiss();
                                    postRequest_Logout(Iconstant.logout_url);
                                }
                            });
                            mDialog.show();

                        } else {
                            alert(getResources().getString(R.string.action_no_internet_title), getResources().getString(R.string.action_no_internet_message));
                        }
                }
                ft.commit();
                mDrawerList.setItemChecked(position, true);
                drawerLayout.closeDrawer(mDrawer);

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
            /*int id = item.getItemId();

	        //noinspection SimplifiableIfStatement
	        if (id == R.id.action_settings) {
	            return true;
	        }*/

        return super.onOptionsItemSelected(item);
    }

    public static void openDrawer() {
        drawerLayout.openDrawer(mDrawer);
    }


    //--------------Alert Method-----------
    private void alert(String title, String message) {
        final PkDialog mDialog = new PkDialog(NavigationDrawer.this);
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

    //----------Method to Send Email--------
    protected void sendEmail() {
        String[] TO = {"info@handyforall.com"};
        String[] CC = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Issue");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Type Your Message");
        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(NavigationDrawer.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }


    public void shareToGMail(String email, String subject, String content) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        //  emailIntent.putExtra(Intent.EXTRA_EMAIL, email);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{Appinfoemail});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_TEXT, content);
        final PackageManager pm = getApplicationContext().getPackageManager();
        final List<ResolveInfo> matches = pm.queryIntentActivities(emailIntent, 0);
        ResolveInfo best = null;
        for (final ResolveInfo info : matches)
            if (info.activityInfo.packageName.endsWith(".gm") || info.activityInfo.name.toLowerCase().contains("gmail"))
                best = info;
        if (best != null)
            emailIntent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
        startActivity(emailIntent);
    }


    //-----------------------Logout Request-----------------
    private void postRequest_Logout(String Url) {
        showDialog(getResources().getString(R.string.action_logging_out));
        System.out.println("---------------LogOut Url-----------------" + Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", UserID);
        jsonParams.put("device_type", "android");
        // jsonParams.put("provider_id", sTaskerID);

        mRequest = new ServiceRequest(NavigationDrawer.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("---------------LogOut Response-----------------" + response);
                String sStatus = "", sResponse = "";
                try {

                    JSONObject object = new JSONObject(response);
                    sStatus = object.getString("status");
                    sResponse = object.getString("response");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mLoadingDialog.dismiss();
                if (sStatus.equalsIgnoreCase("1")) {
                    session.logoutUser();
                    Intent local = new Intent();
                    local.setAction("com.app.logout");
                    NavigationDrawer.this.sendBroadcast(local);

                    SocketHandler.getInstance(NavigationDrawer.this).getSocketManager().disconnect();
                    Intent i=new Intent(NavigationDrawer.this,ChatMessageService.class);
                    stopService(i);
                    onBackPressed();
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();
                } else {
                    alert(getResources().getString(R.string.action_error), sResponse);
                }
            }

            @Override
            public void onErrorListener() {
                mLoadingDialog.dismiss();
            }
        });
    }

    private void showDialog(String data) {
        mLoadingDialog = new LoadingDialog(NavigationDrawer.this);
        mLoadingDialog.setLoadingTitle(data);
        mLoadingDialog.show();
    }

    public static void navigationNotifyChange() {
        mMenuAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        // Unregister the logout receiver
        unregisterReceiver(refreshReceiver);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            Intent i = new Intent(context, AndroidServiceStartOnBoot.class);
            startService(i);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

