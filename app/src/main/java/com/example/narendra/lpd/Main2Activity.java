package com.example.narendra.lpd;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class Main2Activity extends AppCompatActivity{

    Button ur,pr;
    BroadcastReceiver checkingNetwork=new CheckingNetwork();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerReceiver(checkingNetwork,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        setContentView(R.layout.activity_main2);
        ur=(Button)findViewById(R.id.btnuser);
        pr=(Button)findViewById(R.id.btnprovider);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ActionBar actionBar= this.getSupportActionBar();
        actionBar.setTitle(R.string.Register);
    }

    public void user(View v){
        Intent Intuser=new Intent(this,registration.class);
        Intuser.putExtra("reg",1);
        startActivity(Intuser);
        finish();
    }

    public void provider(View v){
        Intent Intpro=new Intent(this,registration.class);
        Intpro.putExtra("reg",2);
        startActivity(Intpro);
        finish();
    }
}