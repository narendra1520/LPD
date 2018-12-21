package com.example.narendra.lpd;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity{
    String email,password;
    EditText edemail,edpwd;
    CheckBox showpwd;
    SharedPreferences sh;
    String res,role=null;
    int code,login_id;
    SharedPreferences.Editor shed;
    CheckingNetwork checkingNetwork =new CheckingNetwork();

    @Override
  protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getSupportFragmentManager().getBackStackEntryCount()>0) {
            getSupportFragmentManager().popBackStack();
        }

        registerReceiver(checkingNetwork,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        sh=getSharedPreferences("user", Context.MODE_PRIVATE);
        String r=sh.getString("role",null);
            if (this.isLogin()) {
                if(r.equals("user")){
                    Intent intent = new Intent(this, Home.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    Intent intent = new Intent(this, Home2.class);
                    startActivity(intent);
                    finish();
                }
            } else {
                setContentView(R.layout.activity_main);
                Inti();
            }
        }

    @Override
    protected void onResume() {
        super.onResume();
        ActionBar actionBar= this.getSupportActionBar();
        actionBar.setTitle(R.string.Login);
    }

    private void Inti() {
        edemail = (EditText) findViewById(R.id.edemail);
        new Functions().funSetFilter(edemail);
        edpwd = (EditText) findViewById(R.id.edpwd);
        showpwd = (CheckBox) findViewById(R.id.showpwd);
        edpwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
        new permision().permision(MainActivity.this,1);
        showpwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edpwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                 else{
                    edpwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
    }

    public boolean isLogin(){
        sh=getSharedPreferences("user", Context.MODE_PRIVATE);
        return sh.getBoolean("login",false);
    }

    class  websrc extends AsyncTask<Void,String,JSONObject> {

            ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog=ProgressDialog.show(MainActivity.this,"Login","Please Wait...");

                StrictMode.ThreadPolicy pol = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(pol);

                email = edemail.getText().toString();
                password = edpwd.getText().toString();
            }

            @Override
            protected JSONObject doInBackground(Void... params) {
                webservices wb = new webservices();
                try {

                    JSONObject jsobj = new JSONObject();
                    jsobj.put("email", email);
                    jsobj.put("pwd", password);

                    wb.webservices("login", jsobj);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return wb.jsonres;
            }

            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                super.onPostExecute(jsonObject);
                progressDialog.dismiss();
                try {
                    res = jsonObject.getString("res");
                    code=jsonObject.getInt("code");

                    if(code==1)
                    {
                        role=jsonObject.getString("role");
                        edemail.setText("");
                        edpwd.setText("");

                        login_id=jsonObject.getInt("id");
                        sh=getSharedPreferences("user", Context.MODE_PRIVATE);
                        shed=sh.edit();
                        shed.putBoolean("login",true);
                        shed.putString("email",email);
                        shed.putInt("login_id",login_id);
                        shed.putString("role",role);
                        shed.apply();

                        Toast.makeText(MainActivity.this, res, Toast.LENGTH_SHORT).show();

                        if(role.equals("user")) {
                            Intent intent=new Intent(MainActivity.this,Home.class);
                            new Functions().newActivity(intent);
                            startActivity(intent);
                            finishAffinity();
                        }
                        else {
                            Intent intent=new Intent(MainActivity.this,Home2.class);
                            new Functions().newActivity(intent);
                            startActivity(intent);
                            finishAffinity();
                        }
                    }
                    else if(code==2){
                        edpwd.setError(res);
                        edpwd.requestFocus();
                   }
                    else if(code==3){
                        final AlertDialog.Builder intru = new AlertDialog.Builder(MainActivity.this);
                        intru.setTitle("Login");
                        intru.setMessage(res+"\nPlease Register First");
                        intru.setPositiveButton("Register", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent=new Intent(MainActivity.this,Main2Activity.class);
                                startActivity(intent);
                                finish();
                            }
                        }).setNegativeButton("Cancel",null);
                        AlertDialog alert = new Functions().funAlertShow(intru);
                    }
                    else {
                        final AlertDialog.Builder intru = new AlertDialog.Builder(MainActivity.this);
                        intru.setTitle("Login");
                        intru.setMessage(res).setNegativeButton("Ok",null);
                        AlertDialog alert = new Functions().funAlertShow(intru);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    public void OnLogin(View v) {
;
        if(!new validate().valiEmail(edemail.getText().toString()))
        {
            edemail.setError(getString(R.string.error_invalid_email));
            edemail.requestFocus();
        }
        else if(!new validate().valiPwd(edpwd.getText().toString()))
        {
            edpwd.setError("Password length must\nbe 8");
            edpwd.requestFocus();
        } else {
            websrc calws = new websrc();
            calws.execute();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(checkingNetwork);
        finish();
    }

    public void OnToReg(View v) {
        Intent toreg = new Intent(this,Main2Activity.class);
        startActivity(toreg);
    }

    public void OnForget(View v)
    {
        Intent toforget = new Intent(this,forgetpassword.class);
        startActivity(toforget);
    }

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount()>0) {
            getSupportFragmentManager().popBackStack();
        }
        else if(getSupportFragmentManager().getBackStackEntryCount()==0) {
            AlertDialog.Builder intru = new AlertDialog.Builder(this);
            intru.setTitle("Exit");
            intru.setMessage("Are You Sure ?").setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finishAndRemoveTask();
                }
            }).setNegativeButton("Stay Here !",null);
            AlertDialog alert = new Functions().funAlertShow(intru);
        }
       /* else {
            MainActivity.super.onBackPressed();
            finish();
        }*/
    }
}
