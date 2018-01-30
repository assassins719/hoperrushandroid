package com.hoperrush.app;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;
import com.hoperrush.R;
import com.hoperrush.adapter.RatingAdapter;
import com.hoperrush.core.dialog.LoadingDialog;
import com.hoperrush.core.dialog.PkDialog;
import com.hoperrush.core.volley.AppController;
import com.hoperrush.core.volley.ServiceRequest;
import com.hoperrush.core.volley.VolleyMultipartRequest;
import com.hoperrush.core.widgets.CircularImageView;
import com.hoperrush.iconstant.Iconstant;
import com.hoperrush.pojo.RatingPojo;
import com.hoperrush.utils.ConnectionDetector;
import com.hoperrush.utils.SessionManager;
import com.hoperrush.utils.SubClassActivity;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Casperon Technology on 1/23/2016.
 */
public class RatingPage extends SubClassActivity {
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private SessionManager sessionManager;

    private RelativeLayout Rl_skip;
    private RelativeLayout Rl_submit;
    private EditText Et_comment;
    private ImageView image_upload;

    private SessionManager session;

    Dialog dialog;
    private ServiceRequest mRequest;
    private LoadingDialog mLoadingDialog;

    final int PERMISSION_REQUEST_CODE = 111;

    private Dialog photo_dialog;
    private int REQUEST_TAKE_PHOTO = 1;
    private int galleryRequestCode = 2;
    private Uri camera_FileUri;
    Bitmap bitMapThumbnail;
    private byte[] byteArray;
    private static final String IMAGE_DIRECTORY_NAME = "QuickRabbit";
    private static final String TAG = "";

    private String mSelectedFilePath = "";
    ArrayList<RatingPojo> itemList;
    RatingAdapter adapter;
    private ExpandableHeightListView listView;
    private String sJobId_intent = "";
    private String UserID = "", User_Image;
    private boolean isDataAvailable = false;
    private TextView myTaskerTXT;
    private ImageView Iv_ratingAddImage;
    private ImageView Img_ratingupload;
    private String partner_image = "";
    private String partner_name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rating_page);
        initialize();

        Intent finishPaymentBroadcastIntent = new Intent();
        finishPaymentBroadcastIntent.setAction("com.package.finish.PaymentPageDetails");
        sendBroadcast(finishPaymentBroadcastIntent);

        Rl_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent finishPaymentwebBroadcastIntent = new Intent();
                finishPaymentwebBroadcastIntent.setAction("com.finish.PaymentWebview");
                sendBroadcast(finishPaymentwebBroadcastIntent);

                Intent finishPaymentweb1BroadcastIntent = new Intent();
                finishPaymentweb1BroadcastIntent.setAction("com.package.finish.PaymentPageDetails");
                sendBroadcast(finishPaymentweb1BroadcastIntent);

                Intent finishpaymentpageBroadcastIntent = new Intent();
                finishpaymentpageBroadcastIntent.setAction("com.package.finish.Cardpage");
                sendBroadcast(finishpaymentpageBroadcastIntent);

                Intent finishpaypalwebview = new Intent();
                finishpaypalwebview.setAction("com.package.finish.paypalwebview");
                sendBroadcast(finishpaypalwebview);

                Intent map_page = new Intent();
                map_page.setAction("com.refresh.map_page");
                sendBroadcast(map_page);


                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);


            }
        });


        Iv_ratingAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
                    // Marshmallow+
                    if (!checkAccessFineLocationPermission() || !checkAccessCoarseLocationPermission() || !checkWriteExternalStoragePermission()) {
                        requestPermission();
                    } else {
                        chooseImage();
                    }
                } else {
                    chooseImage();
                }
            }
        });


        Rl_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean isRatingEmpty = false;

                if (itemList != null) {
                    for (int i = 0; i < itemList.size(); i++) {
                        if (itemList.get(i).getRatingCount().length() == 0 || itemList.get(i).getRatingCount().equalsIgnoreCase("0.0")) {
                            isRatingEmpty = true;
                        }
                    }
                    if (!isRatingEmpty) {
                        if (isInternetPresent) {

                            System.out.println("------------job_id-------------" + sJobId_intent);
                            System.out.println("------------comments-------------" + Et_comment.getText().toString());
                            System.out.println("------------ratingsFor-------------" + "tasker");
                            if (Et_comment.getText().toString().length() > 0) {
                                HashMap<String, String> jsonParams = new HashMap<String, String>();
                                jsonParams.put("comments", Et_comment.getText().toString());
                                jsonParams.put("ratingsFor", "user");
                                jsonParams.put("job_id", sJobId_intent);
                                for (int i = 0; i < itemList.size(); i++) {
                                    jsonParams.put("ratings[" + i + "][option_id]", itemList.get(i).getRatingId());
                                    jsonParams.put("ratings[" + i + "][option_title]", itemList.get(i).getRatingName());
                                    jsonParams.put("ratings[" + i + "][rating]", itemList.get(i).getRatingCount());
                                }
                                System.out.println("------------jsonParams-------------" + jsonParams);


                                if (byteArray != null) {
                                    uploadUserImage(Iconstant.rating_submit_url, jsonParams);

                                } else {
                                    postRequest_SubmitRating(Iconstant.rating_submit_url, jsonParams);

                                }


                            } else {
                                alert(getResources().getString(R.string.rating_header_sorry_textView), getResources().getString(R.string.rating_header_comment_feedback_invalid));
                            }
                        } else {
                            alert(getResources().getString(R.string.action_no_internet_title), getResources().getString(R.string.action_no_internet_message));
                        }
                    } else {
                        alert(getResources().getString(R.string.rating_header_sorry_textView), getResources().getString(R.string.rating_header_enter_all));
                    }

                }
            }
        });

    }

    private void initialize() {

        cd = new ConnectionDetector(RatingPage.this);
        isInternetPresent = cd.isConnectingToInternet();
        sessionManager = new SessionManager(RatingPage.this);
        itemList = new ArrayList<RatingPojo>();
        session = new SessionManager(RatingPage.this);
        image_upload = (ImageView) findViewById(R.id.image_upload);
        Rl_skip = (RelativeLayout) findViewById(R.id.rating_header_skip_layout);
        listView = (ExpandableHeightListView) findViewById(R.id.rating_listView);
        Rl_submit = (RelativeLayout) findViewById(R.id.rating_submit_layout);
        Et_comment = (EditText) findViewById(R.id.rating_comment_editText);
        Iv_ratingAddImage = (ImageView) findViewById(R.id.rating_page_add_ImageView);
        Img_ratingupload = (CircularImageView) findViewById(R.id.ImageView_rating);
        myTaskerTXT = (TextView) findViewById(R.id.rating_page_taskernameTXT);


        // get user data from session
        HashMap<String, String> user = sessionManager.getUserDetails();
        UserID = user.get(SessionManager.KEY_USER_ID);
        User_Image = user.get(SessionManager.KEY_USER_IMAGE);


        Picasso.with(this).load(User_Image).error(R.drawable.placeholder_icon)
                .placeholder(R.drawable.placeholder_icon).memoryPolicy(MemoryPolicy.NO_CACHE).into(Img_ratingupload);
        Intent intent = getIntent();
        sJobId_intent = intent.getStringExtra("JobID");

        if (isInternetPresent) {
            postRequest_RatingList(Iconstant.rating_list_url);
        } else {
            alert(getResources().getString(R.string.action_no_internet_title), getResources().getString(R.string.action_no_internet_message));
        }


    }

    //--------------Alert Method-----------
    private void alert(String title, String alert) {

        final PkDialog mDialog = new PkDialog(RatingPage.this);
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

    //-----------------------Rating List Post Request-----------------
    private void postRequest_RatingList(String Url) {

        mLoadingDialog = new LoadingDialog(RatingPage.this);
        mLoadingDialog.setLoadingTitle(getResources().getString(R.string.action_loading));
        mLoadingDialog.show();

        System.out.println("-------------Rating List Url----------------" + Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("holder_type", "user");
        jsonParams.put("user", UserID);
        jsonParams.put("job_id", sJobId_intent);
        System.out.println("holder_type--------" + "user");

        mRequest = new ServiceRequest(RatingPage.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("-------------Rating List Response----------------" + response);

                String sStatus = "";
                try {
                    JSONObject object = new JSONObject(response);
                    sStatus = object.getString("status");
                    if (sStatus.equalsIgnoreCase("1")) {
                        JSONArray payment_array = object.getJSONArray("review_options");
                        itemList.clear();
                        if (payment_array.length() > 0) {

                            for (int i = 0; i < payment_array.length(); i++) {
                                JSONObject reason_object = payment_array.getJSONObject(i);
                                RatingPojo pojo = new RatingPojo();
                                pojo.setRatingId(reason_object.getString("option_id"));
                                pojo.setRatingName(reason_object.getString("option_name"));
                                pojo.setRatingCount("");
                                partner_image = reason_object.getString("image");
                                partner_name = reason_object.getString("name");
                                itemList.add(pojo);
                            }
                            isDataAvailable = true;

                            Picasso.with(getApplicationContext()).load(String.valueOf(partner_image)).placeholder(R.drawable.nouserimg).into(Img_ratingupload);
                        } else {
                            isDataAvailable = false;
                        }
                    }


                    if (sStatus.equalsIgnoreCase("1") && isDataAvailable) {
                        adapter = new RatingAdapter(RatingPage.this, itemList);
                        listView.setAdapter(adapter);
                        listView.setExpanded(true);
                    }

                    if (sStatus.equalsIgnoreCase("1")) {
                        myTaskerTXT.setText(partner_name);
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


    //-----------------------Submit Rating Post Request-----------------
    private void postRequest_SubmitRating(String Url, final HashMap<String, String> jsonParams) {
        mLoadingDialog = new LoadingDialog(RatingPage.this);
        mLoadingDialog.setLoadingTitle(getResources().getString(R.string.action_pleaseWait));
        mLoadingDialog.show();

        System.out.println("-------------Submit Rating Url----------------" + Url);

        mRequest = new ServiceRequest(RatingPage.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("------------Submit Rating Response----------------" + response);

                String sStatus = "";
                try {
                    JSONObject object = new JSONObject(response);
                    sStatus = object.getString("status");
                    if (sStatus.equalsIgnoreCase("1")) {

                        final PkDialog mDialog = new PkDialog(RatingPage.this);
                        mDialog.setDialogTitle(getResources().getString(R.string.action_success));
                        mDialog.setDialogMessage(getResources().getString(R.string.rating_submit_successfully));
                        mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mDialog.dismiss();

                                Intent finishPaymentwebBroadcastIntent = new Intent();
                                finishPaymentwebBroadcastIntent.setAction("com.finish.PaymentWebview");
                                sendBroadcast(finishPaymentwebBroadcastIntent);

                                Intent finishPaymentweb1BroadcastIntent = new Intent();
                                finishPaymentweb1BroadcastIntent.setAction("com.package.finish.PaymentPageDetails");
                                sendBroadcast(finishPaymentweb1BroadcastIntent);

                                Intent finishpaymentpageBroadcastIntent = new Intent();
                                finishpaymentpageBroadcastIntent.setAction("com.package.finish.Cardpage");
                                sendBroadcast(finishpaymentpageBroadcastIntent);

                                Intent finishpaypalwebview = new Intent();
                                finishpaypalwebview.setAction("com.package.finish.paypalwebview");
                                sendBroadcast(finishpaypalwebview);

                                Intent map_page = new Intent();
                                map_page.setAction("com.refresh.map_page");
                                sendBroadcast(map_page);


                                finish();
                                //onBackPressed();
                                //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            }
                        });
                        mDialog.show();
                    } else {
                        String sResponse = object.getString("response");
                        alert(getResources().getString(R.string.action_sorry), sResponse);
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


//-----------------rate img-------------


    private void uploadUserImage(String url, final HashMap<String, String> jsonParams) {

        dialog = new Dialog(RatingPage.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_loading));
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, url, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                System.out.println("------------- image response-----------------" + response.data);
                String resultResponse = new String(response.data);
                System.out.println("-------------  response-----------------" + resultResponse);
                String sStatus = "", sResponse = "", SUser_image = "", Smsg = "";
                try {
                    JSONObject jsonObject = new JSONObject(resultResponse);
                    sStatus = jsonObject.getString("status");
                    sResponse = jsonObject.getString("response");

                    if (sStatus.equalsIgnoreCase("1")) {

                        final PkDialog mDialog = new PkDialog(RatingPage.this);
                        mDialog.setDialogTitle(getResources().getString(R.string.action_success));
                        mDialog.setDialogMessage(getResources().getString(R.string.rating_submit_successfully));
                        mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mDialog.dismiss();
                                Intent finishPaymentwebBroadcastIntent = new Intent();
                                finishPaymentwebBroadcastIntent.setAction("com.finish.PaymentWebview");
                                sendBroadcast(finishPaymentwebBroadcastIntent);

                                Intent finishPaymentweb1BroadcastIntent = new Intent();
                                finishPaymentweb1BroadcastIntent.setAction("com.package.finish.PaymentPageDetails");
                                sendBroadcast(finishPaymentweb1BroadcastIntent);

                                Intent finishpaymentpageBroadcastIntent = new Intent();
                                finishpaymentpageBroadcastIntent.setAction("com.package.finish.Cardpage");
                                sendBroadcast(finishpaymentpageBroadcastIntent);

                                Intent finishpaypalwebview = new Intent();
                                finishpaypalwebview.setAction("com.package.finish.paypalwebview");
                                sendBroadcast(finishpaypalwebview);

                                finish();
                                //onBackPressed();
                                //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            }
                        });
                        mDialog.show();
                    } else {
                        //String sResponse = object.getString("response");
                        alert(getResources().getString(R.string.action_sorry), sResponse);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dialog.dismiss();

//                NetworkResponse networkResponse = error.networkResponse;
//                String errorMessage = "Unknown error";
//                if (networkResponse == null) {
//                    if (error.getClass().equals(TimeoutError.class)) {
//                        errorMessage = "Request timeout";
//                    } else if (error.getClass().equals(NoConnectionError.class)) {
//                        errorMessage = "Failed to connect server";
//                    }
//                } else {
//                    String result = new String(networkResponse.data);
//                    try {
//                        JSONObject response = new JSONObject(result);
//                        String status = response.getString("status");
//                        String message = response.getString("message");
//
//                        Log.e("Error Status", status);
//                        Log.e("Error Message", message);
//
//                        if (networkResponse.statusCode == 404) {
//                            errorMessage = "Resource not found";
//                        } else if (networkResponse.statusCode == 401) {
//                            errorMessage = message + " Please login again";
//                        } else if (networkResponse.statusCode == 400) {
//                            errorMessage = message + " Check your inputs";
//                        } else if (networkResponse.statusCode == 500) {
//                            errorMessage = message + " Something is getting wrong";
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//                Log.i("Error", errorMessage);
//                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                return jsonParams;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();


                params.put("file", new DataPart("maidac.jpg", byteArray));

                System.out.println("photo--------edit------" + byteArray);

                return params;
            }
        };

        //to avoid repeat request Multiple Time
        DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        multipartRequest.setRetryPolicy(retryPolicy);
        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        multipartRequest.setShouldCache(false);
        AppController.getInstance().addToRequestQueue(multipartRequest);

    }


    private boolean checkAccessFineLocationPermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private boolean checkAccessCoarseLocationPermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private boolean checkWriteExternalStoragePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    chooseImage();
                } else {
                    finish();
                }
                break;
        }
    }


    // --------------------Method for choose image--------------------
    private void chooseImage() {
        photo_dialog = new Dialog(RatingPage.this);
        photo_dialog.getWindow();
        photo_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        photo_dialog.setContentView(R.layout.photo_picker_dialog);
        photo_dialog.setCanceledOnTouchOutside(true);
        photo_dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_photo_Picker;
        photo_dialog.show();
        photo_dialog.getWindow().setGravity(Gravity.CENTER);

        RelativeLayout camera = (RelativeLayout) photo_dialog
                .findViewById(R.id.photo_picker_camera_layout);
        RelativeLayout gallery = (RelativeLayout) photo_dialog
                .findViewById(R.id.photo_picker_gallery_layout);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
                photo_dialog.dismiss();
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
                photo_dialog.dismiss();
            }
        });

    }


    private void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        camera_FileUri = getOutputMediaFileUri(1);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, camera_FileUri);
        // start the image capture Intent
        startActivityForResult(intent, REQUEST_TAKE_PHOTO);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, galleryRequestCode);
    }


    /**
     * Creating file uri to store image/video
     */
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * returning image / video
     */
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "eOops! Failed creat "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == 1) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putParcelable("file_uri", camera_FileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        camera_FileUri = savedInstanceState.getParcelable("file_uri");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_TAKE_PHOTO) {
                try {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 8;

                    final Bitmap bitmap = BitmapFactory.decodeFile(camera_FileUri.getPath(), options);
                    Bitmap thumbnail = bitmap;
                    final String picturePath = camera_FileUri.getPath();
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                    File curFile = new File(picturePath);
                    try {
                        ExifInterface exif = new ExifInterface(curFile.getPath());
                        int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                        int rotationInDegrees = exifToDegrees(rotation);

                        Matrix matrix = new Matrix();
                        if (rotation != 0f) {
                            matrix.preRotate(rotationInDegrees);
                        }
                        thumbnail = Bitmap.createBitmap(thumbnail, 0, 0, thumbnail.getWidth(), thumbnail.getHeight(), matrix, true);
                    } catch (IOException ex) {
                        Log.e("TAG", "Failed to get Exif data", ex);
                    }
                    thumbnail.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
                    byteArray = byteArrayOutputStream.toByteArray();

                    //------------Code to update----------
                    bitMapThumbnail = thumbnail;
                    image_upload.setImageBitmap(thumbnail);

                    /*if (isInternetPresent) {
                        uploadUserImage (Iconstant.profile_edit_photo_url);
                        System.out.println("edit-----"+Iconstant.profile_edit_photo_url);
                    } else {
                        alert(getResources().getString(R.string.action_no_internet_title), getResources().getString(R.string.action_no_internet_message));
                    }*/

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (requestCode == galleryRequestCode) {

                Uri selectedImage = data.getData();
                if (selectedImage.toString().startsWith("content://com.sec.android.gallery3d.provider")) {
                    String[] filePath = {MediaStore.Images.Media.DATA};
                    Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                    c.moveToFirst();
                    int columnIndex = c.getColumnIndex(filePath[0]);
                    final String picturePath = c.getString(columnIndex);
                    c.close();

                    Picasso.with(RatingPage.this).load(picturePath).resize(100, 100).into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            Bitmap thumbnail = bitmap;
                            mSelectedFilePath = picturePath;
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            thumbnail.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
                            byteArray = byteArrayOutputStream.toByteArray();

                            //------------Code to update----------
                            bitMapThumbnail = thumbnail;
                            image_upload.setImageBitmap(thumbnail);

/*
                            cd = new ConnectionDetector(RatingPage.this);
                            isInternetPresent = cd.isConnectingToInternet();
                            if (isInternetPresent) {
                                uploadUserImage (Iconstant.profile_edit_photo_url);
                                System.out.println("edit-----"+Iconstant.profile_edit_photo_url);
                            } else {
                                alert(getResources().getString(R.string.action_no_internet_title), getResources().getString(R.string.action_no_internet_message));
                            }*/
                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) {
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                        }
                    });
                } else {
                    String[] filePath = {MediaStore.Images.Media.DATA};
                    Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                    c.moveToFirst();

                    int columnIndex = c.getColumnIndex(filePath[0]);
                    final String picturePath = c.getString(columnIndex);
                    Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
                    Bitmap thumbnail = bitmap; //getResizedBitmap(bitmap, 600);
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    File curFile = new File(picturePath);

                    try {
                        ExifInterface exif = new ExifInterface(curFile.getPath());
                        int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                        int rotationInDegrees = exifToDegrees(rotation);

                        Matrix matrix = new Matrix();
                        if (rotation != 0f) {
                            matrix.preRotate(rotationInDegrees);
                        }
                        thumbnail = Bitmap.createBitmap(thumbnail, 0, 0, thumbnail.getWidth(), thumbnail.getHeight(), matrix, true);
                    } catch (IOException ex) {
                        Log.e("TAG", "Failed to get Exif data", ex);
                    }
                    thumbnail.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
                    byteArray = byteArrayOutputStream.toByteArray();
                    c.close();

                    bitMapThumbnail = thumbnail;
                    image_upload.setImageBitmap(thumbnail);

                   /* cd = new ConnectionDetector(RatingPage.this);
                    isInternetPresent = cd.isConnectingToInternet();
                   if (isInternetPresent) {
                        //------------Code to update----------
                        bitMapThumbnail = thumbnail;
                        Img_profilepic.setImageBitmap(thumbnail);

                        uploadUserImage (Iconstant.profile_edit_photo_url);
                        System.out.println("edit-----"+Iconstant.profile_edit_photo_url);

                    } else {
                        alert(getResources().getString(R.string.action_no_internet_title), getResources().getString(R.string.action_no_internet_message));
                    }*/
                }
            }
        }
    }


    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    //-----------------Move Back on pressed phone back button------------------
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)) {
            //Do nothing
            return true;
        }
        return false;
    }
}
