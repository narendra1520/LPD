package com.example.narendra.lpd;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.WindowManager;

import org.json.JSONObject;

public class gpstracker extends Service{// implements LocationListener {

    //private final Context mContext;

    // flag for GPS status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    int login_id;

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 10 * 1; // 10 seconds

    // Declaring a Location Manager
    protected LocationManager locationManager;
    private LocationListener listener;

    @SuppressLint("MissingPermission")
    public void checkNet(){
            locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled || !isNetworkEnabled) {
                showSettingsAlert();
            }
            else {
                listener = new LocationListener() {
                    @Override
                    public void onLocationChanged(final Location location) {
                        final Location location1=location;
                        if(location!=null){
                            onUpdate(location);
                           // new websrc().execute(location.getLatitude(),location.getLongitude());
                        }
                        else if(location.getLatitude()==0){getLast();}
                        else {getLast();}
                    }

                    @Override
                    public void onStatusChanged(String s, int i, Bundle bundle) {

                    }

                    @Override
                    public void onProviderEnabled(String s) {

                    }

                    @Override
                    public void onProviderDisabled(String s) {
                        showSettingsAlert();
                    }
                };
                locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

                if (locationManager != null) {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, listener);
                }
            }
    }

    @SuppressLint("MissingPermission")
    public void getLast(){
        //noinspection MissingPermission
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                MIN_TIME_BW_UPDATES,
                MIN_DISTANCE_CHANGE_FOR_UPDATES, listener);
        //noinspection MissingPermission
        Location location=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if(location!=null){
            onUpdate(location);
        }
    }

    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("GPS settings");
        alertDialog.setMessage("GPS is not enabled!").setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                checkNet();
            }
        });
        AlertDialog alert = alertDialog.create();
        alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alert.setCanceledOnTouchOutside(false);
        new Functions().funSetCancleable(alert);
        alert.show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        login_id=intent.getIntExtra("login_id",-1);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        checkNet();
    }
    @Override
    public void onDestroy() {
        if(listener!=null) {
            locationManager.removeUpdates(listener);
        }
    }

    public void onUpdate(final Location location){ Handler handler=new Handler();
        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                webservices wb=new webservices();
                try {
                    JSONObject jsobj=new JSONObject();
                    jsobj.put("log_id",login_id);
                    jsobj.put("llat",location.getLatitude());
                    jsobj.put("llong",location.getLongitude());
                    wb.webservices("locationupdate",jsobj);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        handler.post(runnable);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

}