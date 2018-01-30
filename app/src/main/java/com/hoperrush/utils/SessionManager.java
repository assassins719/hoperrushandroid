package com.hoperrush.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.hoperrush.app.NavigationDrawer;

import java.util.HashMap;

/**
 * Casperon Technology on 11/26/2015.
 */

public class SessionManager {

    SharedPreferences pref;
    // Editor for Shared preferences
    Editor editor;
    // Context
    Context _context;
    // Shared pref mode
    int PRIVATE_MODE = 0;
    // Shared preferences file name
    private static final String PREF_NAME = "PremKumar";
    public static final String KEY_Chat_userid = "chatuserid";
    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_USER_ID = "userId";
    public static final String KEY_GCM_ID = "gcmId";
    public static final String KEY_USERNAME = "userName";
    public static final String KEY_USER_IMAGE = "userImage";

    public static final String KEY_COUNTRY_CODE = "countryCode";
    public static final String KEY_PHONE_NUMBER = "phoneNumber";
    public static final String KEY_LOCATION_ID = "locationId";
    public static final String KEY_LOCATION_NAME = "locationName";
    public static final String KEY_CATEGORY_ID = "categoryID";
    public static final String KEY_REFERRAL_CODE = "referralCode";

    public static final String KEY_WALLET_AMOUNT = "walletAmount";
    public static final String KEY_CURRENCY_CODE = "currencyCode";

    public static final String KEY_XMPP_USER_ID = "xmppUserId";
    public static final String KEY_XMPP_SEC_KEY = "xmppSecKey";

    public static final String KEY_CHECK_MY_JOB_DETAIL_CLASS_OPEN = "myJobsDetailOpen";
    public static final String KEY_CHECK_MAKE_PAYMENT_CLASS_OPEN = "MakePaymentOpen";

    public static final String KEY_Appinfo_email = "Appinfo_email";
    public static final String KEY_TASK_ID = "taskID";

    public static final String KEY_JOB_ID = "jobID";
    public static final String TASK_ID = "taskid";
    public static final String CHAT_TASK_ID = "taskid";

    public static String JOB_ID = "jobid";

    public static String displayAddress_name="displayaddress";

    public static final String minimum_amount="minimum_amount";
    public static final String hourly_amount="hourly_amount";

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     */
    public void createLoginSession(String userId, String userName, String email, String userImage, String countryCode, String phoneNumber, String categoryId, String referralCode) {
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_USERNAME, userName);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_USER_IMAGE, userImage);
        editor.putString(KEY_COUNTRY_CODE, countryCode);
        editor.putString(KEY_PHONE_NUMBER, phoneNumber);
        editor.putString(KEY_CATEGORY_ID, categoryId);
        editor.putString(KEY_REFERRAL_CODE, referralCode);
        //editor.putString(KEY_GCM_ID, gcmID);

        // commit changes
        editor.commit();
    }


    public void createLocationSession(String locationId, String locationName) {
        editor.putString(KEY_LOCATION_ID, locationId);
        editor.putString(KEY_LOCATION_NAME, locationName);

        // commit changes
        editor.commit();
    }

    public void createWalletSession(String walletAmount, String currencyCode) {
        editor.putString(KEY_WALLET_AMOUNT, walletAmount);
        editor.putString(KEY_CURRENCY_CODE, currencyCode);

        // commit changes
        editor.commit();
    }

    //------ Xmpp Connect Code-----
    public void setXmppKey(String userId, String secretKey) {
        editor.putString(KEY_XMPP_USER_ID, userId);
        editor.putString(KEY_XMPP_SEC_KEY, secretKey);
        editor.commit();
    }

    //------username update code-----
    public void setUserNameUpdate(String name) {
        editor.putString(KEY_USERNAME, name);
        editor.commit();
    }

//    //------UserImage update code-----
//    public void setUserImageUpdate(String image) {
//        editor.putString(KEY_USER_IMAGE, image);
//        editor.commit();
//    }

    public void UpdateUserImage(String image){
        editor.putString(KEY_USER_IMAGE, image);
        editor.commit();
    }

    //------MobileNumber update code-----
    public void setMobileNumberUpdate(String code, String mobile) {
        editor.putString(KEY_COUNTRY_CODE, code);
        editor.putString(KEY_PHONE_NUMBER, mobile);
        editor.commit();
    }


    //------Check MyJobsDetail Class Opened-----
    public void setMyJobsDetailOpen(String isOpen) {
        editor.putString(KEY_CHECK_MY_JOB_DETAIL_CLASS_OPEN, isOpen);
        editor.commit();
    }

    //------Check Make Payment Class Opened-----
    public void setMakePaymentOpen(String isOpen) {
        editor.putString(KEY_CHECK_MAKE_PAYMENT_CLASS_OPEN, isOpen);
        editor.commit();
    }


    //------Set Task ID-----
    public void setSocketTaskId(String taskID) {
        editor.putString(KEY_TASK_ID, taskID);
        editor.commit();
    }

    public void setminimum_amount(String amount){
        editor.putString(minimum_amount,amount);
        editor.commit();


    }

    public void sethourly_amount(String amount){
        editor.putString(hourly_amount,amount);
        editor.commit();


    }


    /**
     * Get stored session data
     */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, ""));
        user.put(KEY_USER_ID, pref.getString(KEY_USER_ID, ""));
        user.put(KEY_USERNAME, pref.getString(KEY_USERNAME, ""));
        user.put(KEY_USER_IMAGE, pref.getString(KEY_USER_IMAGE, ""));
        user.put(KEY_COUNTRY_CODE, pref.getString(KEY_COUNTRY_CODE, ""));
        user.put(KEY_PHONE_NUMBER, pref.getString(KEY_PHONE_NUMBER, ""));
        user.put(KEY_LOCATION_ID, pref.getString(KEY_LOCATION_ID, ""));
        user.put(KEY_LOCATION_NAME, pref.getString(KEY_LOCATION_NAME, ""));
        user.put(KEY_CATEGORY_ID, pref.getString(KEY_CATEGORY_ID, ""));
        user.put(KEY_REFERRAL_CODE, pref.getString(KEY_REFERRAL_CODE, ""));
        user.put(KEY_GCM_ID, pref.getString(KEY_GCM_ID, ""));
        user.put(KEY_Appinfo_email, pref.getString(KEY_Appinfo_email, ""));
        user.put(KEY_Chat_userid, pref.getString(KEY_Chat_userid, ""));
        user.put(displayAddress_name,pref.getString(displayAddress_name,""));
        user.put(minimum_amount, pref.getString(minimum_amount, ""));
        user.put(hourly_amount, pref.getString(hourly_amount, ""));
        user.put(CHAT_TASK_ID, pref.getString(CHAT_TASK_ID, ""));
        user.put(KEY_TASK_ID, pref.getString(KEY_TASK_ID, ""));
        return user;
    }

    public HashMap<String, String> getKEY_JOB_ID() {
        HashMap<String, String> isTaskID = new HashMap<String, String>();
        isTaskID.put(KEY_JOB_ID, pref.getString(KEY_JOB_ID, ""));
        return isTaskID;
    }


    public HashMap<String, String> getLocationDetails() {
        HashMap<String, String> location = new HashMap<String, String>();
        location.put(KEY_LOCATION_ID, pref.getString(KEY_LOCATION_ID, ""));
        location.put(KEY_LOCATION_NAME, pref.getString(KEY_LOCATION_NAME, ""));

        return location;
    }

    public HashMap<String, String> getWalletDetails() {
        HashMap<String, String> wallet = new HashMap<String, String>();
        wallet.put(KEY_WALLET_AMOUNT, pref.getString(KEY_WALLET_AMOUNT, ""));
        wallet.put(KEY_CURRENCY_CODE, pref.getString(KEY_CURRENCY_CODE, ""));
        return wallet;
    }

    //-----------Get XMPP Secret Key-----
    public HashMap<String, String> getXmppKey() {
        HashMap<String, String> code = new HashMap<String, String>();
        code.put(KEY_XMPP_USER_ID, pref.getString(KEY_XMPP_USER_ID, ""));
        code.put(KEY_XMPP_SEC_KEY, pref.getString(KEY_XMPP_SEC_KEY, ""));
        return code;
    }


    //-----------Get MyJobsDetail Class Opened Status-----
    public HashMap<String, String> getMyJobDetailOpen() {
        HashMap<String, String> isOpen = new HashMap<String, String>();
        isOpen.put(KEY_CHECK_MY_JOB_DETAIL_CLASS_OPEN, pref.getString(KEY_CHECK_MY_JOB_DETAIL_CLASS_OPEN, ""));
        return isOpen;
    }


    //-----------Get Make Payment Class Opened Status-----
    public HashMap<String, String> getMakePaymentOpen() {
        HashMap<String, String> isOpen = new HashMap<String, String>();
        isOpen.put(KEY_CHECK_MAKE_PAYMENT_CLASS_OPEN, pref.getString(KEY_CHECK_MAKE_PAYMENT_CLASS_OPEN, ""));
        return isOpen;
    }


    public HashMap<String, String> getSocketTaskId() {
        HashMap<String, String> isTaskID = new HashMap<String, String>();
        isTaskID.put(KEY_TASK_ID, pref.getString(KEY_TASK_ID, ""));
        return isTaskID;
    }


    /**
     * Clear session details
     */
    public void logoutUser() {
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, NavigationDrawer.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // Staring Login Activity
        _context.startActivity(i);

    }

    public void setjobid(String jobid) {
        editor.putString(KEY_JOB_ID, jobid);
        editor.commit();
    }


    public void settaskid(String taskid) {
        editor.putString(TASK_ID, taskid);
        editor.commit();
    }

    public void setchattaskid(String taskid) {
        editor.putString(KEY_TASK_ID, taskid);
        editor.commit();
    }



    public HashMap<String, String> gettaskid() {
        HashMap<String, String> taskid = new HashMap<String, String>();
        taskid.put(TASK_ID, pref.getString(TASK_ID, ""));
        return taskid;
    }

    public void setjob(String job) {
        this.JOB_ID = job;

    }


    public HashMap<String, String> getjob() {
        HashMap<String, String> job = new HashMap<String, String>();
        job.put(JOB_ID, pref.getString(JOB_ID, ""));
        return job;
    }

    /**
     * Quick check for login
     **/
    // Get Login State
    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }


    public void Setemailappinfo(String email) {
        editor.putString(KEY_Appinfo_email, email);
        editor.commit();


    }

    public void setchatuserid(String id) {
        editor.putString(KEY_Chat_userid, id);
        editor.commit();

    }
    public void settasksid(String taskid) {
        editor.putString(CHAT_TASK_ID, taskid);
        editor.commit();
    }



    public void setDisplayAddress_name(String displayAddress_name){
        this.displayAddress_name=displayAddress_name;

    }

    public String getDisplayAddress_name(){

        return displayAddress_name;
    }

    /**
     * Put the provider Id
     *
     * @param aProvider
     */
    public void putProvideID(String aProvider) {

        editor.putString("Providerid", aProvider);
        editor.commit();
    }


    public String getProviderID() {
        return pref.getString("Providerid", "");
    }

    /**
     * Put the provider Id
     *
     * @param aJobID
     */
    public void putChatProfileJobID(String aJobID) {

        editor.putString("Jobid", aJobID);
        editor.commit();
    }


    public String getChatProfileJobID() {
        return pref.getString("Jobid", "");
    }


    /**
     * Put the Tasker Id
     *
     * @param aTasker
     */
    public void putChatProfileTaskerID(String aTasker) {

        editor.putString("Taskerid", aTasker);
        editor.commit();
    }


    public String getChatProfileTaskerID() {
        return pref.getString("Taskerid", "");
    }

    /**
     * Put the provider Id
     *
     * @param aTaskID
     */
    public void putChatProfileTaskID(String aTaskID) {

        editor.putString("Taskid", aTaskID);
        editor.commit();
    }


    public String getChatProfileTaskID() {
        return pref.getString("Taskid", "");
    }




    /**
     * Put the provider type
     *
     * @param aScreenType
     */
    public void putProvideScreenType(String aScreenType) {

        editor.putString("screentype", aScreenType);
        editor.commit();
    }


    public String getProviderScreenType() {
        return pref.getString("screentype", "");
    }
}
