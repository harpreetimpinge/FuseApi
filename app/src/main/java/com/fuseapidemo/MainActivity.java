package com.fuseapidemo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends Activity
        implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    // private FusedLocationProviderApi providerApi = LocationServices.FusedLocationApi;
    private final String TAG = getClass().getName();
    private Context context = this;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isGooglePlayServicesAvailable()) {
            Toast.makeText(context, "Please install \"Google Play Services\" first.", Toast.LENGTH_LONG).show();
            return;
        }


        locationRequest = this.createLocationRequest();
        //Setup API Configurations
        googleApiClient = this.googleConnectionApi();
        Log.e(TAG, "googleApiClient: " + googleApiClient.toString());

        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.txt);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Connect to Api(s)
        googleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();

        this.stopLocationUpdates();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (googleApiClient != null && googleApiClient.isConnected()) {
            this.startLocationUpdates();
            Log.d(TAG, "Location update resumed .....................");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Remove the Api before stop
        googleApiClient.disconnect();

    }

    private GoogleApiClient googleConnectionApi() {
        GoogleApiClient.Builder googleApiBuilder = new GoogleApiClient.Builder(context);
        googleApiBuilder.addApi(LocationServices.API);
        googleApiBuilder.addConnectionCallbacks(this);
        googleApiBuilder.addOnConnectionFailedListener(this);

        return googleApiBuilder.build();
    }

    private LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10 * 1000);
        mLocationRequest.setFastestInterval(8 * 1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        return mLocationRequest;
    }

    @SuppressWarnings("deprecation")
    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }


    protected PendingResult<Status> startLocationUpdates()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    &&
                    checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
                return null;
            }
        }
        return LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient, locationRequest, this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == 101)
        {
            this.startLocationUpdates();
        }
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                googleApiClient, this);
        Log.d(TAG, "Location update stopped .......................");
    }

    @Override
    public void onConnected(Bundle bundle) {
        this.startLocationUpdates();
        Log.d(TAG, "Connection stable ... with Bundle:: " + bundle);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed: " + connectionResult.toString());
    }


    @Override
    public void onLocationChanged(Location location) {

        String text = "___ LOCATION ___\n"
                + "Latitude: " + location.getLatitude()
                + "\n"
                + "Longitude: " + location.getLongitude()
                + "\n"
                + "Provider: " + location.getProvider();

        textView.setText(text);

    }

}
