package com.example.narendra.lpd;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

public class forgetpassword extends AppCompatActivity {

    EditText email;
    Button forget;
    String stremail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgetpassword);
        Intil();
    }

    private void Intil() {
        email=(EditText)findViewById(R.id.edemail);
        new Functions().funSetFilter(email);
        forget=(Button)findViewById(R.id.btnforget);
    }

    public void OnForget(View v)
    {
        if(!new validate().valiEmail(email.getText().toString())) {
            email.setError(getString(R.string.error_invalid_email));
            email.requestFocus();
        }
        else {
            forgetpassword.webservice calws = new webservice();
            calws.execute();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ActionBar actionBar= this.getSupportActionBar();
        actionBar.setTitle(R.string.Forget);
    }

    class  webservice extends AsyncTask<Void,String,JSONObject> {
        ProgressDialog progressDialog;
        @Override
        protected JSONObject doInBackground(Void... params) {
            webservices wb=new webservices();
            try {

                JSONObject jsobj=new JSONObject();
                jsobj.put("email", stremail);

                wb.webservices("forgetpwd",jsobj);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return wb.jsonres;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog  = ProgressDialog.show(forgetpassword.this, "Forgot Password", "Please Wait...");

            StrictMode.ThreadPolicy pol=new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(pol);
            stremail=email.getText().toString();
        }
        String res;
        int code;
        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            progressDialog.dismiss();

            try {
                res = jsonObject.getString("res");
                code = jsonObject.getInt("code");

                if (code == 1) {
                    AlertDialog.Builder intru = new AlertDialog.Builder(forgetpassword.this);
                    intru.setTitle("Forgot Password");
                    intru.setMessage(res).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent=new Intent(forgetpassword.this,MainActivity.class);
                            startActivity(intent);
                        }
                    });
                    AlertDialog alert = new Functions().funAlertShow(intru);
                }
                else if(code==3) {
                    email.setError(res);
                    email.requestFocus();
                }
                else {
                    Toast.makeText(forgetpassword.this,res, Toast.LENGTH_SHORT).show();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent log=new Intent(this,MainActivity.class);
        log.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        log.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        log.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(log);
    }
}