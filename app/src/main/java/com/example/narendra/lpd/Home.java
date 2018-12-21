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
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONObject;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    FragmentTransaction ft;
    FragmentManager fm=getSupportFragmentManager();
    SharedPreferences sh;
    SharedPreferences.Editor shed;
    TextView email;
    static TextView username;
    int login_id; boolean set;
    double latitude,longitude;
    String log_eml,name,imgfile=null,con,pin;
    View header;
    NavigationView navigationView;
    static ImageView proimg;
    double lat,lng;
    CheckingNetwork checkingNetwork =new CheckingNetwork();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerReceiver(checkingNetwork,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        header=navigationView.getHeaderView(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ActionBar actionBar=this.getSupportActionBar();
        actionBar.setTitle(R.string.Home);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(checkingNetwork);
      //  new gpstracker(Home.this,getParent(), login_id).stopUsingGPS();
        finish();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        sh=getSharedPreferences("user",MODE_PRIVATE);
        login_id=sh.getInt("login_id",-1);
        Home.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                set= new permision().permision(Home.this,2);
            }
        });
        Home.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                set= new permision().permision(Home.this,1);
            }
        });
        if(set){
            /*gpstracker gps=new gpstracker(Home.this,getParent(), login_id);
           if( gps.canGetLocation){
               latitude=gps.getLatitude();
               longitude=gps.getLongitude();
           }else {
               Toast.makeText(this, "Failed to Update Location ", Toast.LENGTH_SHORT).show();
           }*/
            intil();
        }

    }


    public void intil()
    {
        username=(TextView)header.findViewById(R.id.txtuser);
        email=(TextView)header.findViewById(R.id.txtemail);
        proimg=(ImageView)header.findViewById(R.id.imgprofile);

        fm.beginTransaction().replace(R.id.include,new NewRequest()).commit();
        Home.websrc cal=new websrc();
        cal.execute();
    }


    public class  websrc extends AsyncTask<Void,String,JSONObject> {

        @Override
        protected JSONObject doInBackground(Void... params) {
            webservices wb=new webservices();
            try {
                JSONObject jsobj=new JSONObject();
                jsobj.put("log_id",login_id);
                wb.webservices("getdata",jsobj);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return wb.jsonres;
        }
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog= ProgressDialog.show(Home.this,"Loding","Please Wait...");

            sh=getSharedPreferences("user",MODE_PRIVATE);
            login_id=sh.getInt("login_id",-1);
            log_eml=sh.getString("email",null);
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            progressDialog.dismiss();
            try {
                name=jsonObject.getString("username");
                imgfile=jsonObject.getString("image");
                con=jsonObject.getString("con");
                pin=jsonObject.getString("pin");
            } catch (Exception e) {
                e.printStackTrace();
            }
            sh=getSharedPreferences("user",MODE_PRIVATE);
            shed=sh.edit();
            shed.putString("name",name);
            shed.putString("image",imgfile);
            shed.putString("con",con);
            shed.putString("pin",pin);
            shed.apply();

            username.setText(name);
            Glide.with(header.getContext()).load(getString(R.string.imgupload)+imgfile).apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true)).into(proimg);
            email.setText(log_eml);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else if(getSupportFragmentManager().getBackStackEntryCount()==0) {
            AlertDialog.Builder intru = new AlertDialog.Builder(Home.this);
            intru.setTitle("Exit");
            intru.setMessage("Are You Sure ?").setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finishAndRemoveTask();
                }
            }).setNegativeButton("Stay Here !",null);
            AlertDialog alert = new Functions().funAlertShow(intru);
        }
        else if(getSupportFragmentManager().getBackStackEntryCount()>0) {
            getSupportFragmentManager().popBackStack();
        }
        else {
            super.onBackPressed();
            this.finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.nav1_Logout){

            sh=getSharedPreferences("user", Context.MODE_PRIVATE);
            shed=sh.edit();
            shed.clear();
            shed.apply();

            logOut();
            Toast.makeText(this,"You are logged out", Toast.LENGTH_SHORT).show();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        //On click

        if(id==R.id.nav1_profile) {
            fm.beginTransaction().replace(R.id.include,new userprofile(),"Profile").commit();
        }
        else if (id==R.id.nav1_new_request){
            fm.beginTransaction().replace(R.id.include,new NewRequest()).commit();
        }
        else if (id==R.id.nav1_request){
            fm.beginTransaction().replace(R.id.include,new request_view(),"Req_View").commit();
        }
        else if(id==R.id.nav1_history){
            Bundle b=new Bundle();
            b.putInt("for",1);
            Fragment frm=new User_History();
            frm.setArguments(b);
            fm.beginTransaction().replace(R.id.include,frm,"History").commit();
        }
        else if (id==R.id.nav1_Logout_menu){
            sh=getSharedPreferences("user", Context.MODE_PRIVATE);
            shed=sh.edit();
            shed.clear();
            shed.apply();

            logOut();
            Toast.makeText(this,"You are logged out", Toast.LENGTH_SHORT).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logOut() {
        Intent intent=new Intent(Home.this,MainActivity.class);
        new Functions().newActivity(intent);
        startActivity(intent);
        finish();
    }

    public TextView getUsername() {
        return username;
    }

    public ImageView getProimg() {
        return proimg;
    }

    public String getImgfile() {
        return imgfile;
    }
}