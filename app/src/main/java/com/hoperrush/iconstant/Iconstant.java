package com.hoperrush.iconstant;

/**
 */
public interface Iconstant {

//-----------------------------------------------Zoplay Live Url---------------------------------------------------
//
//    String BaseUrl = "http://handyforall.zoplay.com/mobile/";
//    String XMPP_HOST_URL = "45.55.241.170";
//    String XMPP_SERVICE_NAME = "messaging.dectar.com";
//    String SOCKET_HOST_URL = "http://handyforall.zoplay.com/notify";
//    String SOCKET_CHAT_URL = "http://handyforall.zoplay.com/chat";

//-------------------------------------------Maidac Live Url---------------------------------
    String BaseUrl = "http://45.63.22.34:3005/mobile/";
    String XMPP_HOST_URL = "45.55.241.170";
    String XMPP_SERVICE_NAME = "messaging.dectar.com";
    String SOCKET_HOST_URL = "http://45.63.22.34:3005/notify";
    String SOCKET_CHAT_URL = "http://45.63.22.34:3005/chat";

    //-------------------------------------------Local Url---------------------------------
//    String BaseUrl = "http://192.168.0.84:3002/mobile/";
//    String XMPP_HOST_URL = "45.55.241.170";
//    String XMPP_SERVICE_NAME = "messaging.dectar.com";
//    String SOCKET_HOST_URL = "http://192.168.0.84:3002/notify";
//    String SOCKET_CHAT_URL = "http://192.168.0.84:3002/chat";
  //-------------------------------------------Local Url---------------------------------

    String loginUrl = BaseUrl + "app/login";
    String RegisterUrl = BaseUrl + "app/check-user";
    String OtpUrl = BaseUrl + "app/register";
    String DisplayCityUrl = BaseUrl + "app/get-location";
    String Aboutus_Url = BaseUrl +  "app/mobile/aboutus";
    String Terms_Conditions_Url = BaseUrl+"app/mobile/termsandconditions";
    String Privacy_Polocy = BaseUrl + "app/mobile/privacypolicy";
            String SelectCityUrl = BaseUrl + "app/set-user-location";
    /* String Fare_summary_url=BaseUrl+"app/paymentlist/history";*/
    String paybywallet_url = BaseUrl + "app/payment/by-wallet";
    String paybycah_url = BaseUrl + "app/payment/by-cash";
    String BookJob = BaseUrl + "app/user-booking";
    String paypalurl = BaseUrl + "app/payment/paypalPayment";
    String paymentpageurl = BaseUrl + "app/paymentlist/history";
    String forgot_password_url = BaseUrl + "app/user/reset-password";
    String reset_password_url = BaseUrl + "app/user/update-reset-password";
    String couponcode_url = BaseUrl + "app/payment/couponmob";
    String CategoriesUrl = BaseUrl + "app/categories";
    String Categories_Detail_Url = BaseUrl + "app/categories";
    String Emergeny_contact = BaseUrl + "app/user/alert-emergency-contact";
    String emergencyContact_add_url = BaseUrl + "app/user/set-emergency-contact";
    String emergencyContact_view_url = BaseUrl + "app/user/view-emergency-contact";
    String emergencyContact_delete_url = BaseUrl + "app/user/delete-emergency-contact";
    String Mobile_Id_url = BaseUrl + "app/payment/by-gateway";
    String invite_earn_friends_url = BaseUrl + "app/get-invites";
    String Notification_mode = BaseUrl + "user/notification_mode";
    String address_list_url = BaseUrl + "app/list_address";
    String add_address_url = BaseUrl + "app/insert-address";
    String delete_address_url = BaseUrl + "app/delete_address";
    String book_job_url = BaseUrl + "app/book-job";
    String couponCode_apply_url = BaseUrl + "app/apply-coupon";
    String App_Info = BaseUrl + "app/mobile/appinfo";
    String plumbal_money_url = BaseUrl + "app/get-money-page";
    String plumbal_add_money_url = BaseUrl + "mobile/wallet-recharge/stripe-process?";
    String plumbal_money_webView_url = BaseUrl + "mobile/wallet-recharge/payform?user_id=";
    String plumbal_money_transaction_url = BaseUrl + "app/get-trans-list";
   /* String makePayment_cash_url = BaseUrl + "app/payment/by-cash";
    String makePayment_wallet_url = BaseUrl + "app/payment/by-wallet";
    String makePayment_autoDetect_url = BaseUrl + "app/payment/by-auto-detect";
    String makePayment_Get_webView_mobileId_url = BaseUrl + "app/payment/by-gateway";
    String makePayment_webView_url = BaseUrl + "proceed-payment?mobileId=";*/

    String List_Address_Url = BaseUrl + "app/list_addressforandroid";
    String Card_webview_url = BaseUrl + "mobile/stripe-manual-payment-form?mobileId=";

    //Partner Profile URl
    String PROFILEINFO_URL = BaseUrl + "provider/provider-info";
    String MYPROFILE_REVIWES_URL = BaseUrl + "provider/provider-rating";

    String GETMESSAGECHAT_URL = BaseUrl + "app/getmessage";

    String Mobile_ID_get = BaseUrl + "app/payment/by-gateway";

    //newly done by abdul
    String Fare_summary_url = BaseUrl + "app/paymentlist/history";

    String place_search_url = "https://maps.googleapis.com/maps/api/place/autocomplete/json?types=geocode&key=AIzaSyDXVTR_V9gvWep08joXPO2RTJcDBpNsu5I&input=";
    String GetAddressFrom_LatLong_url = "https://maps.googleapis.com/maps/api/place/details/json?key=AIzaSyDXVTR_V9gvWep08joXPO2RTJcDBpNsu5I&placeid=";
    String DISTANCE_MATRIX_API = "https://maps.googleapis.com/maps/api/distancematrix/json?key=AIzaSyC3fkcq8JkaA-rV0cop9Ud4ls3rHz4GVHE&origins=ORIGIN_STRING&destinations=DESTINATION_STRING";
    String MyJobsList_Url = BaseUrl + "app/my-jobs-new";
    String MyJobs_Cancel_Reason_Url = BaseUrl + "app/cancellation-reason";
    String MyJobs_Cancel_Url = BaseUrl + "app/cancel-job";
    String MyJobs_Detail_Url = BaseUrl + "user/view-job";
    String Filter_booking_url = BaseUrl + "user/recentuser-list";
    String changePassword_url = BaseUrl + "app/user/change-password";
    String profile_edit_userName_url = BaseUrl + "app/user/change-name";
    String profile_edit_mobileNo_url = BaseUrl + "app/user/change-mobile";
    String profile_edit_photo_url = BaseUrl + "app/user-profile-pic";
    String logout_url = BaseUrl + "app/user/logout";

    String viewProfile_url = BaseUrl + "user/get-provider-profile";
    String paymentList_url = BaseUrl + "app/payment-list";

    String makePayment_cash_url = BaseUrl + "app/payment/by-cash";
    String makePayment_wallet_url = BaseUrl + "app/payment/by-wallet";
    String makePayment_autoDetect_url = BaseUrl + "app/payment/by-auto-detect";
    String makePayment_Get_webView_mobileId_url = BaseUrl + "app/payment/by-gateway";
    String makePayment_webView_url = BaseUrl + "mobile/proceed-payment?mobileId=";

    String rating_list_url = BaseUrl + "get-rattings-options";
    String rating_submit_url = BaseUrl + "submit-rattings";

    String chat_list_url = BaseUrl + "app/chat/list";
    String chat_detail_url = BaseUrl + "chat/chathistory";
    String chat_availability_url = BaseUrl + "app/chat/availablity";
    String providerslist_url = BaseUrl + "provider/providerList";

    String myjobs_sortingurl = BaseUrl + "jobs-list";

    String social_check_url = BaseUrl + "app/mobile/social-fbcheckUser";

    String facebook_register_url = BaseUrl + "app/mobile/social-register";
    String facebook_login_url = BaseUrl + "app/mobile/social-login";

    //-----------UserAgent--------
    String Plumbal_userAgent = "plumbal-dec2k15";
    String Plumbal_appType = "android";

    //Transaction Module URL
    String TRANSACTION_URL = BaseUrl + "app/user-transaction";
    String TRANSACTION_DETAIL_URL = BaseUrl + "app/userjob-transaction";

    //Notification URl
    String NOTIFICATION_URL = BaseUrl + "app/notification";

    //Review Module URL
    String REVIEW_URL = BaseUrl + "app/get-reviews";
    //wallet Money
    String plumbal_money_paypal_webView_url = BaseUrl + "app/wallet-recharge/mobpaypal";

    //-----------Xmpp Notification Label--------

    String sAccept_action = "job_accepted";
    String sAccept_message = "message";
    String sAccept_key1 = "key1";

    String sStartOff_action = "start_off";
    String sStartOff_message = "message";
    String sStartOff_key1 = "key1";

    String sArrived_action = "provider_reached";
    String sArrived_message = "message";
    String sArrived_key1 = "key1";

    String sJobStarted_action = "job_started";
    String sJobStarted_message = "message";
    String sJobStarted_key1 = "key1";

    String sJobCompleted_action = "job_completed";
    String sJobCompleted_message = "message";
    String sJobCompleted_key1 = "key1";

    String sJobReAssign_action = "job_reassign";
    String sJobReAssign_message = "message";
    String sJobReAssign_key1 = "key1";

    String sRequestPayment_action = "requesting_payment";
    String sRequestPayment_message = "message";
    String sRequestPayment_key1 = "key1";

    String sPaymentPaid_action = "payment_paid";
    String sPaymentPaid_message = "message";
    String sPaymentPaid_key1 = "key1";

    String sReject_action = "rejecting_task";
    String sReject_message = "message";
    String sRejectPaid_key1 = "key1";

    String PROVIDER = "PROVIDER";

    String Admin_Notification="admin_notification";
    //-----------------------------------Map Concept-------------------

    String Map_boooking = BaseUrl + "app/mapbook-job";
    String Map_Insert_Address = BaseUrl + "map/insert-address";
    String MapuserBooking = BaseUrl + "app/mapuser-booking";
//---------------------------Notification Process-----------------------------

    String MODEUPDATE_URL = BaseUrl + "user/notification_mode";
}
