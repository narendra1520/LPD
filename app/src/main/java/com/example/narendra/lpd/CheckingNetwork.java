package com.example.narendra.lpd;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Narendra on 04-01-2018.
 */

public class CheckingNetwork extends BroadcastReceiver{
    boolean connected;
    @Override
    public void onReceive(final Context context, final Intent intent) {
        ConnectivityManager manager= (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo.State Mobile=manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
        NetworkInfo.State wifi=manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        if(Mobile== NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTED ){
            connected=true;
        }else if(intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY,Boolean.FALSE)){
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
            alertDialog.setTitle("No Connection");
            alertDialog.setMessage("No Internet Connection").setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    onReceive(context,intent);
                }
            });
            AlertDialog alert = new Functions().funAlertShow(alertDialog);
            new Functions().funSetCancleable(alert);
        }
    }

}
