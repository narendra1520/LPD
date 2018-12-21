package com.example.narendra.lpd;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by Narendra on 31-12-2017.
 */

public class permision extends Activity {
    Activity c;
    int perCode;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    boolean camera = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean wrstr = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean redstr = grantResults[2] == PackageManager.PERMISSION_GRANTED;

                    if (camera || wrstr || redstr) {
                    } else {
                        ActivityCompat.requestPermissions(c, new String[]{android.Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    }
                }break;
            case 2:
                if (grantResults.length > 0) {
                    boolean gps = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean coarse = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (gps || coarse) {
                    } else {
                    ActivityCompat.requestPermissions(c, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
                    }
                }break;
        }
    }

    public boolean permision(Activity c, int perCode) {
        this.c = c;
        this.perCode = perCode;

        boolean set = false;
        switch (perCode){
            case 1:
                if (ContextCompat.checkSelfPermission(c, android.Manifest.permission.CAMERA) + ContextCompat.checkSelfPermission(c, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        + ContextCompat.checkSelfPermission(c, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(c, android.Manifest.permission.CAMERA) || ActivityCompat.shouldShowRequestPermissionRationale(c, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        ActivityCompat.requestPermissions(c, new String[]{android.Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    } else {
                        ActivityCompat.requestPermissions(c, new String[]{android.Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    }
                }else {
                    set=true;
                }
                break;
            case 2:
                if (ContextCompat.checkSelfPermission(c,android.Manifest.permission.ACCESS_FINE_LOCATION) + ContextCompat.checkSelfPermission(c,android.Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(c, android.Manifest.permission.ACCESS_FINE_LOCATION) || ActivityCompat.shouldShowRequestPermissionRationale(c,android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
                        ActivityCompat.requestPermissions(c, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
                    } else {
                        ActivityCompat.requestPermissions(c, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
                    }
                }else {
                    set=true;
                }
                break;
        }
        return set;
    }
}
