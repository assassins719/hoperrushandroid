package com.hoperrush.app;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.hoperrush.R;
import com.hoperrush.core.socket.ChatMessageService;
import com.hoperrush.hockeyapp.ActivityHockeyApp;
import com.hoperrush.core.dialog.PkDialog;
import com.hoperrush.core.gps.GPSTracker;
import com.hoperrush.core.socket.SocketHandler;
import com.hoperrush.utils.ConnectionDetector;
import com.hoperrush.utils.SessionManager;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

/**
 * Casperon Technology on 11/26/2015.
 */
public class SplashScreen extends ActivityHockeyApp implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    // Splash screen timer
    private static int SPLASH_TIME_OUT = 2000;
    SessionManager sessionManager;
    private String locationID = "";

    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;

    GPSTracker gps;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    PendingResult<LocationSettingsResult> result;
    final static int REQUEST_LOCATION = 199;
    final int PERMISSION_REQUEST_CODE = 111;

    private SocketHandler socketHandler;
    public static String latitude="";
    public static String longitude="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        sessionManager = new SessionManager(SplashScreen.this);
        gps = new GPSTracker(getApplicationContext());
        socketHandler = SocketHandler.getInstance(this);

        //Set MyJobsDetail and MakePayment Class as Closed in SessionManager
        sessionManager.setMakePaymentOpen("Closed");
        sessionManager.setMyJobsDetailOpen("Closed");

        mGoogleApiClient = new GoogleApiClient.Builder(SplashScreen.this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        mGoogleApiClient.connect();

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        }
        catch (PackageManager.NameNotFoundException e) {

        }
        catch (NoSuchAlgorithmException e) {

        }

        HashMap<String, String> location = sessionManager.getLocationDetails();
        locationID = location.get(SessionManager.KEY_LOCATION_ID);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= 23) {
                    // Marshmallow+
                    if (!checkAccessFineLocationPermission() || !checkAccessCoarseLocationPermission() || !checkWriteExternalStoragePermission() || !checksmssend() || !checkphonecall()) {
                        requestPermission();
                    } else {
                        setLocation();
                    }
                } else {
                    setLocation();
                }
            }
        }, SPLASH_TIME_OUT);

    }

    @Override
    public void onConnected(Bundle bundle) {
        // enableGpsService();
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    private void setLocation() {
        cd = new ConnectionDetector(SplashScreen.this);
        isInternetPresent = cd.isConnectingToInternet();

        if (isInternetPresent) {
            if (gps.isgpsenabled() && gps.canGetLocation()) {
                double lat=gps.getLatitude();
                double longi=gps.getLongitude();
                latitude= String.valueOf(lat);
                longitude= String.valueOf(longi);
                if (sessionManager.isLoggedIn()) {
                    if (locationID.length() > 0) {
                        Intent intent = new Intent(SplashScreen.this, NavigationDrawer.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                    } else {
                        Intent intent = new Intent(SplashScreen.this, NavigationDrawer.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                    }
                } else {
                    Intent intent = new Intent(SplashScreen.this, NavigationDrawer.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                }

              /*  if (sessionManager.isLoggedIn()) {
                    if (locationID.length() > 0) {
                        Intent intent = new Intent(SplashScreen.this, NavigationDrawer.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                    } else {
                        Intent intent = new Intent(SplashScreen.this, CitySelection.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                    }
                } else {
                    Intent intent = new Intent(SplashScreen.this, SignInAndSignUp.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                }*/

            } else {
                enableGpsService();
            }
        } else {

            final PkDialog mDialog = new PkDialog(SplashScreen.this);
            mDialog.setDialogTitle(getResources().getString(R.string.action_no_internet_title));
            mDialog.setDialogMessage(getResources().getString(R.string.action_no_internet_message));
            mDialog.setPositiveButton(getResources().getString(R.string.action_retry), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialog.dismiss();
                    setLocation();
                }
            });
            mDialog.setNegativeButton(getResources().getString(R.string.action_cancel_small), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialog.dismiss();
                    finish();
                }
            });
            mDialog.show();

        }
    }


    //Enabling Gps Service
    private void enableGpsService() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(30 * 1000);
        mLocationRequest.setFastestInterval(5 * 1000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);

        result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                //final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        //...
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(SplashScreen.this, REQUEST_LOCATION);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:

                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        //...
                        break;
                }
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_LOCATION:
                switch (resultCode) {
                    case Activity.RESULT_OK: {
                        Toast.makeText(SplashScreen.this, "Location enabled!", Toast.LENGTH_LONG).show();

                        if (sessionManager.isLoggedIn()) {
                            if (locationID.length() > 0) {
                                Intent intent = new Intent(SplashScreen.this, NavigationDrawer.class);
                                startActivity(intent);
                                finish();
                                overridePendingTransition(R.anim.enter, R.anim.exit);
                            } else {
                                Intent intent = new Intent(SplashScreen.this, NavigationDrawer.class);
                                startActivity(intent);
                                finish();
                                overridePendingTransition(R.anim.enter, R.anim.exit);
                            }
                        } else {
                            Intent intent = new Intent(SplashScreen.this, NavigationDrawer.class);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(R.anim.enter, R.anim.exit);
                        }


                      /*  if (sessionManager.isLoggedIn()) {
                            if (locationID.length() > 0) {
                                Intent intent = new Intent(SplashScreen.this, NavigationDrawer.class);
                                startActivity(intent);
                                finish();
                                overridePendingTransition(R.anim.enter, R.anim.exit);
                            } else {
                                Intent intent = new Intent(SplashScreen.this, CitySelection.class);
                                startActivity(intent);
                                finish();
                                overridePendingTransition(R.anim.enter, R.anim.exit);
                            }
                        } else {
                            Intent intent = new Intent(SplashScreen.this, NavigationDrawer.class);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(R.anim.enter, R.anim.exit);
                        }*/
                        break;
                    }
                    case Activity.RESULT_CANCELED: {
                        finish();
                        break;
                    }
                    default: {
                        break;
                    }
                }
                break;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (sessionManager.isLoggedIn()) {
            if (!socketHandler.getSocketManager().isConnected) {
                socketHandler.getSocketManager().connect();
            }

            if(!ChatMessageService.isStarted()) {
                Intent intent = new Intent(SplashScreen.this, ChatMessageService.class);
                startService(intent);
            }
        }
    }



    private boolean checkAccessFineLocationPermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private boolean checkAccessCoarseLocationPermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
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

    private boolean checksmssend() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
    private boolean checkphonecall() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.SEND_SMS,Manifest.permission.CALL_PHONE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setLocation();
                } else {
                    finish();
                }
                break;
        }
    }
}
