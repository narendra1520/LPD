package com.example.narendra.lpd;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.KeyEvent;
import android.widget.EditText;

/**
 * Created by Narendra on 09-03-2018.
 */

public class Functions {
    public String funSetFilter(EditText eText) {
        eText.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                return String.valueOf(source).toLowerCase();
            }
        }});
        return null;
    }
    public void funSetCancleable(AlertDialog alert){
        alert.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if(keyCode==KeyEvent.KEYCODE_BACK && event.getAction()==KeyEvent.ACTION_UP){
                    return true;
                }else {
                    return false;
                }
            }
        });
    }
    public AlertDialog funAlertShow(AlertDialog.Builder intru){
        AlertDialog dialog=intru.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        return dialog;
    }
    public void newActivity(Intent intent){
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
    }
}
