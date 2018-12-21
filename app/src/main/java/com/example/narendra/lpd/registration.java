package com.example.narendra.lpd;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

public class registration extends AppCompatActivity{

    FragmentTransaction ft;
    Integer code;
    FragmentManager fm=getSupportFragmentManager();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        Intent get=getIntent();
        code=get.getIntExtra("reg",0);
        intil();
    }

    public void intil() {
        if (code==1){
            ft=fm.beginTransaction();
            ft.replace(R.id.regframe,new frag_user());
            ft.commit();
        } else if (code==2){
            ft=fm.beginTransaction();
            ft.replace(R.id.regframe,new frag_provider());
            ft.commit();
        }
    }
}
