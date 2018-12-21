package com.example.narendra.lpd;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by Narendra on 07-12-2017.
 */

public class getLocation extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    Context c;
    private Double[] latlng = new Double[2];
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    LocationManager mng;
    Location gps_lc = null;
    Location net_lc = null;
    Location final_lc = null;

    LocationListener netListner = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                latlng[0] = location.getLatitude();
                latlng[1] = location.getLongitude();
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    LocationListener gpsListner = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            mng.removeUpdates(netListner);
            if (location != null) {
                latlng[0] = location.getLatitude();
                latlng[1] = location.getLongitude();
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };


    public getLocation(Context c) {
        this.c = c;
        mng = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);
    }

    protected Double[] createLocationRequest() {

        if (!isEnable()) {
            shoAlert();
        } else {
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(6000);
            mLocationRequest.setFastestInterval(6000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            mGoogleApiClient = new GoogleApiClient.Builder(c)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        return latlng;
    }

    private void shoAlert() {
        AlertDialog.Builder intru = new AlertDialog.Builder(c);
        intru.setTitle("Alert");
        intru.setMessage("Please Turn On GPS");
        intru.setNegativeButton("OK", null);
        AlertDialog alert = new Functions().funAlertShow(intru);
        new Functions().funSetCancleable(alert);
    }

    private boolean isEnable() {
        LocationManager mng = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);
        return mng.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }


    public Double[] getlocations() {
        try {

            // getting GPS status
            boolean isGPSEnabled = mng.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            boolean isNetworkEnabled = mng.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                Toast.makeText(c, "GPS and Network Required", Toast.LENGTH_SHORT).show();
            } else {
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (ActivityCompat.checkSelfPermission(c, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(c, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return latlng;
                    }
                    mng.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 10, gpsListner);
                    if(gps_lc==null){
                        gps_lc= mng.getLastKnownLocation(LocationManager.GPS_PROVIDER);}
                }

                if (isNetworkEnabled) {
                    mng.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 10, netListner);
                    if(net_lc==null){
                    net_lc = mng.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);}
                }

                if(gps_lc !=null && net_lc !=null) {
                    if (gps_lc.getAccuracy() > net_lc.getAccuracy()) {
                        final_lc = gps_lc;
                        latlng[0] = final_lc.getLatitude();
                        latlng[1] = final_lc.getLongitude();
                    } else {
                        final_lc = net_lc;
                        latlng[0] = final_lc.getLatitude();
                        latlng[1] = final_lc.getLongitude();
                    }
                }else{
                    if (gps_lc!=null) {
                        final_lc = gps_lc;
                        latlng[0] = final_lc.getLatitude();
                        latlng[1] = final_lc.getLongitude();
                    } else {
                        final_lc = net_lc;
                        latlng[0] = final_lc.getLatitude();
                        latlng[1] = final_lc.getLongitude();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
            return latlng;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(c, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(c, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        PendingResult<Status> loca = LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, (com.google.android.gms.location.LocationListener) this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
