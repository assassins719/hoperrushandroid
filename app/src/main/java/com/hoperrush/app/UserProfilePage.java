package com.hoperrush.app;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.countrycodepicker.CountryPicker;
import com.countrycodepicker.CountryPickerListener;
import com.github.nkzawa.socketio.client.Socket;
import com.hoperrush.R;
import com.hoperrush.core.dialog.LoadingDialog;
import com.hoperrush.core.dialog.PkDialog;
import com.hoperrush.core.socket.SocketHandler;
import com.hoperrush.core.volley.AppController;
import com.hoperrush.core.volley.ServiceRequest;
import com.hoperrush.core.volley.VolleyMultipartRequest;
import com.hoperrush.core.widgets.CircularImageView;
import com.hoperrush.hockeyapp.FragmentActivityHockeyApp;
import com.hoperrush.iconstant.Iconstant;
import com.hoperrush.utils.ConnectionDetector;
import com.hoperrush.utils.SessionManager;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


/**
 * Casperon Technology on 1/19/2016.
 */
public class UserProfilePage extends FragmentActivityHockeyApp {

    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private SessionManager session;
    private RelativeLayout layout_changePassword, back;
    private ImageView Iv_addImage;
    private Button logout;
    private TextView tv_email;
    private EditText Et_name;
    private static EditText Et_mobileNo;
    private static RelativeLayout Rl_country_code;
    private static TextView Tv_countryCode;
    private String UserID = "", UserName = "", UserMobileNo = "", UserCountyCode = "", UserEmail = "", User_Image = "";
    private ServiceRequest mRequest;
    LoadingDialog mLoadingDialog;

    private String sTaskerID = "";

    Dialog dialog;
    Bitmap bitMapThumbnail;
    private byte[] byteArray;

    CountryPicker picker;
    private CircularImageView userImage;
    private TextView Tv_TopUserName;

    private Dialog photo_dialog;
    private int REQUEST_TAKE_PHOTO = 1;
    private int galleryRequestCode = 2;
    private Uri camera_FileUri;
    private static final String TAG = "";
    // directory name to store captured images and videos
    private static final String IMAGE_DIRECTORY_NAME = "Plumbal";

    private String mSelectedFilePath = "";


    final int PERMISSION_REQUEST_CODE = 111;

    private Socket mSocket;
    private static int PICK_IMAGE = 1;
    private static int CAMERA_REQUEST_2 = 22;
    Bitmap finalPic = null;
    String encode;
    private static int CAMERA_PIC_REQUEST = 1337;
    private Uri mImageCaptureUri;
    Uri outputUri = null;

    File captured_image;
    String imagePath;
    String appDirectoryName;
    File imageRoot;
    Uri selectedImage;

    boolean rotate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_page);
        initialize();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();

                Intent refreshBroadcastlogin = new Intent();
                refreshBroadcastlogin.setAction("com.finish.LogInPage");
                sendBroadcast(refreshBroadcastlogin);

                Intent refreshBroadcastregister = new Intent();
                refreshBroadcastregister.setAction("com.finish.RegisterPage");
                sendBroadcast(refreshBroadcastregister);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cd = new ConnectionDetector(UserProfilePage.this);
                isInternetPresent = cd.isConnectingToInternet();

                if (isInternetPresent) {

                    final PkDialog mDialog = new PkDialog(UserProfilePage.this);
                    mDialog.setDialogTitle(getResources().getString(R.string.profile_page_signOut_textView));
                    mDialog.setDialogMessage(getResources().getString(R.string.profile_label_logout_message));
                    mDialog.setPositiveButton(getResources().getString(R.string.profile_label_logout_yes), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDialog.dismiss();
                            postRequest_Logout(Iconstant.logout_url);


                        }
                    });
                    mDialog.setNegativeButton(getResources().getString(R.string.profile_label_logout_no), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDialog.dismiss();
                        }
                    });
                    mDialog.show();

                } else {
                    alert(getResources().getString(R.string.action_no_internet_title), getResources().getString(R.string.action_no_internet_message));
                }
            }
        });


        layout_changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfilePage.this, ChangePassword.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });


        Et_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == Et_name.getId()) {
                    Et_name.setCursorVisible(true);
                }
            }
        });

        Et_name.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;

                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    cd = new ConnectionDetector(UserProfilePage.this);
                    isInternetPresent = cd.isConnectingToInternet();
                    CloseKeyboard(Et_name);

                    if (Et_name.getText().toString().length() == 0) {
                        alert(getResources().getString(R.string.action_error), getResources().getString(R.string.profile_label_error_name));
                    } else {
                        if (isInternetPresent) {
                            postRequest_editUserName(Iconstant.profile_edit_userName_url);
                        } else {
                            alert(getResources().getString(R.string.action_no_internet_title), getResources().getString(R.string.action_no_internet_message));
                        }
                    }
                    handled = true;
                }
                return handled;
            }
        });

        Rl_country_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
            }
        });

        picker.setListener(new CountryPickerListener() {
            @Override
            public void onSelectCountry(String name, String code, String dialCode) {
                picker.dismiss();
                Tv_countryCode.setText(dialCode.replace("+", ""));
                Et_mobileNo.requestFocus();
            }
        });


        Et_mobileNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == Et_mobileNo.getId()) {
                    Et_mobileNo.setCursorVisible(true);
                }
            }
        });


        Et_mobileNo.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    cd = new ConnectionDetector(UserProfilePage.this);
                    isInternetPresent = cd.isConnectingToInternet();
                    CloseKeyboard(Et_name);

                    if (!isValidPhoneNumber(Et_mobileNo.getText().toString())) {
                        alert(getResources().getString(R.string.action_error), getResources().getString(R.string.profile_label_error_mobile));
                    } else if (Tv_countryCode.getText().toString().length() == 0) {
                        alert(getResources().getString(R.string.action_error), getResources().getString(R.string.profile_label_error_mobileCode));
                    } else {
                        if (isInternetPresent) {
                            postRequest_editMobileNumber(Iconstant.profile_edit_mobileNo_url);
                        } else {
                            alert(getResources().getString(R.string.action_no_internet_title), getResources().getString(R.string.action_no_internet_message));
                        }
                    }
                    handled = true;
                }
                return handled;
            }
        });

        Iv_addImage.setOnClickListener(new View.OnClickListener() {
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
        appDirectoryName = getString(R.string.app_name);
        imageRoot = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), appDirectoryName);
        if (!imageRoot.exists()) {
            imageRoot.mkdir();
        } else if (!imageRoot.isDirectory()) {
            imageRoot.delete();
            imageRoot.mkdir();
        }
        String name = dateToString(new Date(), "yyyy-MM-dd-hh-mm-ss");
        captured_image = new File(imageRoot,
                name + ".jpg");
        // captured_image = new
        // File(Environment.getExternalStorageDirectory(),"temp.jpg");

    }
    public String dateToString(Date date, String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(date);
    }
    private void initialize() {
        session = new SessionManager(UserProfilePage.this);
        picker = CountryPicker.newInstance("Select Country");
        cd = new ConnectionDetector(UserProfilePage.this);
        isInternetPresent = cd.isConnectingToInternet();

        tv_email = (TextView) findViewById(R.id.profile_emailId_textView);
        Rl_country_code = (RelativeLayout) findViewById(R.id.profile_textView_country_code_layout);
        Tv_countryCode = (TextView) findViewById(R.id.profile_country_code_textView);
        back = (RelativeLayout) findViewById(R.id.profile_page_headerBar_back_layout);
        Et_mobileNo = (EditText) findViewById(R.id.profile_edit_phoneNo_editText);
        Et_name = (EditText) findViewById(R.id.profile_userName_editText);
        layout_changePassword = (RelativeLayout) findViewById(R.id.profile_changePassword_layout);
        logout = (Button) findViewById(R.id.profile_page_logout_button);
        userImage = (CircularImageView) findViewById(R.id.profile_page_profile_ImageView);
        Tv_TopUserName = (TextView) findViewById(R.id.profile_page_userName_textView);
        Iv_addImage = (ImageView) findViewById(R.id.profile_page_add_ImageView);

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        UserID = user.get(SessionManager.KEY_USER_ID);
        UserName = user.get(SessionManager.KEY_USERNAME);
        UserMobileNo = user.get(SessionManager.KEY_PHONE_NUMBER);
        UserEmail = user.get(SessionManager.KEY_EMAIL);
        UserCountyCode = user.get(SessionManager.KEY_COUNTRY_CODE);
        User_Image = user.get(SessionManager.KEY_USER_IMAGE);

        HashMap<String, String> taskId = session.getSocketTaskId();
        sTaskerID = taskId.get(SessionManager.KEY_TASK_ID);

        System.out.println("sTaskerID-------------" + sTaskerID);

        System.out.println("User_Image-------------" + User_Image);

        //Et_name.setImeActionLabel("Send", KeyEvent.);

        tv_email.setText(UserEmail);
        Et_name.setText(UserName);
        Et_mobileNo.setText(UserMobileNo);
        Tv_countryCode.setText(UserCountyCode.replace("+", ""));

        Picasso.with(UserProfilePage.this).load(User_Image)
                .error(R.drawable.placeholder_icon)
                .placeholder(R.drawable.placeholder_icon).memoryPolicy(MemoryPolicy.NO_CACHE).into(userImage);

        Tv_TopUserName.setText(UserName);

        //----Code to make EditText Cursor at End of the Text------
        Et_name.setSelection(Et_name.getText().length());
        Et_mobileNo.setSelection(Et_mobileNo.getText().length());
    }


    // --------------------Method for choose image--------------------
    private void chooseImage() {
        photo_dialog = new Dialog(UserProfilePage.this);
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
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(captured_image));
        intent.putExtra("android.intent.extras.CAMERA_FACING", 0);
        startActivityForResult(intent, CAMERA_REQUEST_2);
    }

    private void openGallery() {
        Intent ia = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(ia, PICK_IMAGE);
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
                Log.d(TAG, "Oops! Failed create "
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


    //--------------Alert Method-----------
    private void alert(String title, String alert) {

        final PkDialog mDialog = new PkDialog(UserProfilePage.this);
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

    // validating Phone Number
    public static final boolean isValidPhoneNumber(CharSequence target) {
        if (target == null || TextUtils.isEmpty(target) || target.length() <= 6) {
            return false;
        } else {
            return android.util.Patterns.PHONE.matcher(target).matches();
        }
    }

    //--------------Close KeyBoard Method-----------
    private void CloseKeyboard(EditText edittext) {
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(edittext.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    //--------------Show Dialog Method-----------
    private void showDialog(String data) {
        mLoadingDialog = new LoadingDialog(UserProfilePage.this);
        mLoadingDialog.setLoadingTitle(data);
        mLoadingDialog.show();
    }

    //--------------Update Mobile Number From Profile OTP Page Method-----------
    public static void updateMobileDialog(String code, String phone) {
        Et_mobileNo.setText(phone);
        Tv_countryCode.setText(code.replace("+", ""));
    }


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK) {
//            if (requestCode == REQUEST_TAKE_PHOTO) {
//                try {
//                    BitmapFactory.Options options = new BitmapFactory.Options();
//                    options.inSampleSize = 8;
//
//                    final Bitmap bitmap = BitmapFactory.decodeFile(camera_FileUri.getPath(), options);
//                    Bitmap thumbnail = bitmap;
//
//
//
//
//
//                    final String picturePath = camera_FileUri.getPath();
//                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//
//                    File curFile = new File(picturePath);
//                    try {
//                        ExifInterface exif = new ExifInterface(curFile.getPath());
//                        int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
//                        int rotationInDegrees = exifToDegrees(rotation);
//
//                        Matrix matrix = new Matrix();
//                        if (rotation != 0f) {
//                            matrix.preRotate(rotationInDegrees);
//                        }
//                        thumbnail = Bitmap.createBitmap(thumbnail, 0, 0, thumbnail.getWidth(), thumbnail.getHeight(), matrix, true);
//                    } catch (IOException ex) {
//                        Log.e("TAG", "Failed to get Exif data", ex);
//                    }
//                    thumbnail.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
//                    byteArray = byteArrayOutputStream.toByteArray();
//
//                    //------------Code to update----------
//                    bitMapThumbnail = thumbnail;
//                    userImage.setImageBitmap(thumbnail);
//
//                    if (isInternetPresent) {
//                        uploadUserImage(Iconstant.profile_edit_photo_url);
//                        System.out.println("edit-----" + Iconstant.profile_edit_photo_url);
//                    } else {
//                        alert(getResources().getString(R.string.action_no_internet_title), getResources().getString(R.string.action_no_internet_message));
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            } else if (requestCode == galleryRequestCode) {
//
//                Uri selectedImage = data.getData();
//                if (selectedImage.toString().startsWith("content://com.sec.android.gallery3d.provider")) {
//                    String[] filePath = {MediaStore.Images.Media.DATA};
//                    Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
//                    c.moveToFirst();
//                    int columnIndex = c.getColumnIndex(filePath[0]);
//                    final String picturePath = c.getString(columnIndex);
//                    c.close();
//
//                    Picasso.with(UserProfilePage.this).load(picturePath).resize(100, 100).into(new Target() {
//                        @Override
//                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                            Bitmap thumbnail = bitmap;
//                            mSelectedFilePath = picturePath;
//                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                            thumbnail.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
//                            byteArray = byteArrayOutputStream.toByteArray();
//
//                            cd = new ConnectionDetector(UserProfilePage.this);
//                            isInternetPresent = cd.isConnectingToInternet();
//                            if (isInternetPresent) {
//                                uploadUserImage(Iconstant.profile_edit_photo_url);
//                                System.out.println("edit-----" + Iconstant.profile_edit_photo_url);
//                            } else {
//                                alert(getResources().getString(R.string.action_no_internet_title), getResources().getString(R.string.action_no_internet_message));
//                            }
//                        }
//
//                        @Override
//                        public void onBitmapFailed(Drawable errorDrawable) {
//                        }
//
//                        @Override
//                        public void onPrepareLoad(Drawable placeHolderDrawable) {
//                        }
//                    });
//                } else {
//                    String[] filePath = {MediaStore.Images.Media.DATA};
//                    Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
//                    c.moveToFirst();
//
//                    int columnIndex = c.getColumnIndex(filePath[0]);
//                    final String picturePath = c.getString(columnIndex);
//                    Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
//                    Bitmap thumbnail = bitmap; //getResizedBitmap(bitmap, 600);
//                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                    File curFile = new File(picturePath);
//
//                    try {
//                        ExifInterface exif = new ExifInterface(curFile.getPath());
//                        int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
//                        int rotationInDegrees = exifToDegrees(rotation);
//
//                        Matrix matrix = new Matrix();
//                        if (rotation != 0f) {
//                            matrix.preRotate(rotationInDegrees);
//                        }
//                        thumbnail = Bitmap.createBitmap(thumbnail, 0, 0, thumbnail.getWidth(), thumbnail.getHeight(), matrix, true);
//                    } catch (IOException ex) {
//                        Log.e("TAG", "Failed to get Exif data", ex);
//                    }
//                    thumbnail.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
//                    byteArray = byteArrayOutputStream.toByteArray();
//                    c.close();
//
//                    cd = new ConnectionDetector(UserProfilePage.this);
//                    isInternetPresent = cd.isConnectingToInternet();
//                    if (isInternetPresent) {
//                        //------------Code to update----------
//                        bitMapThumbnail = thumbnail;
//                        userImage.setImageBitmap(thumbnail);
//
//                        uploadUserImage(Iconstant.profile_edit_photo_url);
//                        System.out.println("edit-----" + Iconstant.profile_edit_photo_url);
//
//                    } else {
//                        alert(getResources().getString(R.string.action_no_internet_title), getResources().getString(R.string.action_no_internet_message));
//                    }
//                }
//            }
//        }
//    }
//
//
//    private static int exifToDegrees(int exifOrientation) {
//        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
//            return 90;
//        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
//            return 180;
//        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
//            return 270;
//        }
//        return 0;
//    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("resultCode " + resultCode + "request Code" + requestCode);
        System.out.println("outside the resultcode");

        if (resultCode == RESULT_OK) {
            System.out.println("" + requestCode);
            System.out.println("inside the resultcode" + resultCode);
            if (requestCode == CAMERA_REQUEST_2) {
                try {

                    rotate = true;
                    imagePath = captured_image.getAbsolutePath();
                    mImageCaptureUri = Uri.fromFile(new File(imagePath));
                    outputUri = mImageCaptureUri;
                    System.out.println("Image Captured Uri = " + mImageCaptureUri);

                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageCaptureUri);
                    System.out.println("Image Captured Uri bitmap = " + bitmap.toString());

//                    if (bitmap.getWidth() <= 500 && bitmap.getHeight() <= 500) {
//                        Toast.makeText(this, "image is too small", Toast.LENGTH_SHORT).show();
//                    } else {

                        UCrop.Options options = new UCrop.Options();
                        options.setStatusBarColor(getResources().getColor(R.color.facebook_blue_color));
                        options.setToolbarColor(getResources().getColor(R.color.facebook_blue_color));
                        options.setMaxBitmapSize(800);

                        UCrop.of(mImageCaptureUri, outputUri)
                                .withAspectRatio(1, 1)
                                .withMaxResultSize(8000, 8000)
                                .withOptions(options)
                                .start(UserProfilePage.this);

//                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == PICK_IMAGE) {
                // Aviary_Edit(data);
                rotate = false;
                selectedImage = data.getData();

                try {

                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    int wid = bitmap.getWidth();
                    int height = bitmap.getHeight();
                    System.out.println("---------image------width and -----");

//                    if (bitmap.getWidth() <= 500 && bitmap.getHeight() <= 500) {
//                        Toast.makeText(this, "Selected Image too small", Toast.LENGTH_SHORT).show();
//                    } else {

                        if (!imageRoot.exists()) {
                            imageRoot.mkdir();
                        } else if (!imageRoot.isDirectory()) {
                            imageRoot.delete();
                            imageRoot.mkdir();
                        }


                        final File image = new File(imageRoot, System.currentTimeMillis() + ".jpg");
                        outputUri = Uri.fromFile(image);

                        UCrop.Options options = new UCrop.Options();
                        options.setStatusBarColor(getResources().getColor(R.color.facebook_blue_color));
                        options.setToolbarColor(getResources().getColor(R.color.facebook_blue_color));
                        options.setMaxBitmapSize(800);


                        UCrop.of(selectedImage, outputUri)
                                .withAspectRatio(1, 1)
                                .withMaxResultSize(8000, 8000)
                                .withOptions(options)
                                .start(UserProfilePage.this);

//                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == UCrop.REQUEST_CROP) {

                final Uri resultUri = UCrop.getOutput(data);

                Log.d("Crop success", "" + resultUri);
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);

                    if (bitmap.getWidth() == 500 && bitmap.getHeight() == 500) {

                        finalPic = bitmap;

                    } else {

                        int width = bitmap.getWidth();
                        int height = bitmap.getHeight();

                        float bitmapRatio = (float) width / (float) height;
                        if (bitmapRatio > 0) {
                            width = 500;
                            height = (int) (width / bitmapRatio);

                        } else {
                            height = 500;
                            width = (int) (height * bitmapRatio);
                        }


                        finalPic = Bitmap.createScaledBitmap(bitmap, width, height, true);
                        encode = encodeToBase64(finalPic, Bitmap.CompressFormat.JPEG, 100);

                    }

                    if (finalPic == null) {
                        Log.d("Bitmap", "null");
                    } else {
                        Log.d("Bitmap", "not null");
//                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                        finalPic.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//                        byte[] byteArray = stream.toByteArray();
                        //  Log.d("count", "" + img_capture_count);
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();


                        finalPic.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
                        byteArray = byteArrayOutputStream.toByteArray();



                        cd = new ConnectionDetector(UserProfilePage.this);
                        isInternetPresent = cd.isConnectingToInternet();
                        if (isInternetPresent) {

                            //   ------------Code to update----------
                            bitMapThumbnail = finalPic;
                            userImage.setImageBitmap(finalPic);


                            uploadUserImage(Iconstant.profile_edit_photo_url);
                            System.out.println("edit-----" + Iconstant.profile_edit_photo_url);
                        } else {
                            alert(getResources().getString(R.string.action_no_internet_title), getResources().getString(R.string.action_no_internet_message));
                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality) {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }






    //-----------------------Edit UserName Request-----------------
    private void postRequest_editUserName(String Url) {
        showDialog(getResources().getString(R.string.action_updating));
        System.out.println("---------------Edit Username Url-----------------" + Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", UserID);
        jsonParams.put("user_name", Et_name.getText().toString());

        mRequest = new ServiceRequest(UserProfilePage.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("---------------Edit Username Response-----------------" + response);
                String Sstatus = "", Smessage = "";
                try {

                    JSONObject object = new JSONObject(response);
                    Sstatus = object.getString("status");
                    Smessage = object.getString("response");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mLoadingDialog.dismiss();

                if (Sstatus.equalsIgnoreCase("1")) {
                    Tv_TopUserName.setText(Et_name.getText().toString());
                    session.setUserNameUpdate(Et_name.getText().toString());
                    alert(getResources().getString(R.string.action_success), getResources().getString(R.string.profile_label_username_success));
                } else {
                    alert(getResources().getString(R.string.action_error), Smessage);
                }
            }

            @Override
            public void onErrorListener() {
                mLoadingDialog.dismiss();
            }
        });
    }


    //-----------------------Edit MobileNumber Request-----------------
    private void postRequest_editMobileNumber(String Url) {
        showDialog(getResources().getString(R.string.action_updating));
        System.out.println("---------------Edit MobileNumber Url-----------------" + Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", UserID);
        jsonParams.put("country_code", "+" + Tv_countryCode.getText().toString());
        jsonParams.put("phone_number", Et_mobileNo.getText().toString());
        //  jsonParams.put("otp", "");

        mRequest = new ServiceRequest(UserProfilePage.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("---------------Edit MobileNumber Response-----------------" + response);
                String Sstatus = "", Smessage = "", Sotp = "", Sotp_status = "", Scountry_code = "", Sphone_number = "";
                try {

                    JSONObject object = new JSONObject(response);
                    Sstatus = object.getString("status");
                    Smessage = object.getString("response");
                    if (Sstatus.equalsIgnoreCase("1")) {
                        Sotp = object.getString("otp");
                        Sotp_status = object.getString("otp_status");
                        Scountry_code = object.getString("country_code");
                        Sphone_number = object.getString("phone_number");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mLoadingDialog.dismiss();
                if (Sstatus.equalsIgnoreCase("1")) {
                    Intent intent = new Intent(UserProfilePage.this, ProfileOtpPage.class);
                    intent.putExtra("Otp", Sotp);
                    intent.putExtra("Otp_Status", Sotp_status);
                    intent.putExtra("CountryCode", Scountry_code);
                    intent.putExtra("Phone", Sphone_number);
                    intent.putExtra("UserID", UserID);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                } else {
                    alert(getResources().getString(R.string.action_error), Smessage);
                }
            }

            @Override
            public void onErrorListener() {
                mLoadingDialog.dismiss();
            }
        });
    }


    //-----------------------Logout Request-----------------
    private void postRequest_Logout(String Url) {
        showDialog(getResources().getString(R.string.action_logging_out));
        System.out.println("---------------LogOut Url-----------------" + Url);

        System.out.println("---------------LogOut sTaskerID-----------------" + sTaskerID);
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", UserID);
        jsonParams.put("device_type", "android");
        // jsonParams.put("provider_id", sTaskerID);

        mRequest = new ServiceRequest(UserProfilePage.this);
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
                    UserProfilePage.this.sendBroadcast(local);

                    SocketHandler.getInstance(UserProfilePage.this).getSocketManager().disconnect();

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


    //--------------Alert Method-----------
    private void Alert(String title, String alert) {

        final PkDialog mDialog = new PkDialog(UserProfilePage.this);
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


    private void uploadUserImage(String url) {

        dialog = new Dialog(UserProfilePage.this);
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
                    if (sStatus.equalsIgnoreCase("1")) {
                        JSONObject responseObject = jsonObject.getJSONObject("response");
                        SUser_image = responseObject.getString("image");
                        Smsg = responseObject.getString("msg");

                        userImage.setImageBitmap(bitMapThumbnail);
                        session.UpdateUserImage(SUser_image);

                        NavigationDrawer.navigationNotifyChange();

                        Alert(getResources().getString(R.string.action_success), Smsg);

                    } else {
                        sResponse = jsonObject.getString("response");
                        Alert(getResources().getString(R.string.rating_header_sorry_textView), sResponse);
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

                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorMessage = "Request timeout";
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                    }
                } else {
                    String result = new String(networkResponse.data);
                    try {
                        JSONObject response = new JSONObject(result);
                        String status = response.getString("status");
                        String message = response.getString("message");

                        Log.e("Error Status", status);
                        Log.e("Error Message", message);

                        if (networkResponse.statusCode == 404) {
                            errorMessage = "Resource not found";
                        } else if (networkResponse.statusCode == 401) {
                            errorMessage = message + " Please login again";
                        } else if (networkResponse.statusCode == 400) {
                            errorMessage = message + " Check your inputs";
                        } else if (networkResponse.statusCode == 500) {
                            errorMessage = message + " Something is getting wrong";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("Error", errorMessage);
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", UserID);
                System.out.println("user_id---------------" + UserID);
                return params;
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


    @Override
    public void onResume() {
        super.onResume();
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


    //-----------------Move Back on pressed phone back button------------------
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)) {

            // close keyboard
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(back.getWindowToken(), 0);

            onBackPressed();
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            return true;
        }
        return false;
    }
}
