package com.example.narendra.lpd;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
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

public class Home2 extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FragmentTransaction ft;
    FragmentManager fm=getSupportFragmentManager();
    SharedPreferences sh;
    SharedPreferences.Editor shed;
    static TextView username,email;
    int login_id;
    String log_eml,name,imgfile=null,con,pin,brth,lic;
    View header;
    static ImageView proimg;
    double minprc;
    CheckingNetwork checkingNetwork =new CheckingNetwork();
    boolean set;
    Intent i;
    Fragment frm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerReceiver(checkingNetwork,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        sh=getSharedPreferences("user",MODE_PRIVATE);
        login_id=sh.getInt("login_id",-1);

        setContentView(R.layout.activity_home2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        header=navigationView.getHeaderView(0);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Home2.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                set= new permision().permision(Home2.this,2);
            }
        });
        Home2.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                set= new permision().permision(Home2.this,1);
            }
        });
        if(set) {
            i=new Intent(this,gpstracker.class);
            i.putExtra("login_id",login_id);
            startService(i);
            intil();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        ActionBar actionBar=this.getSupportActionBar();
        actionBar.setTitle(R.string.Home2);
        Fragment chkfrm = getSupportFragmentManager().findFragmentByTag("PProfile");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&!Settings.canDrawOverlays(getApplicationContext())) {
            openOvarlay();
        }else if(getSupportFragmentManager().getBackStackEntryCount()==0){
            websrc cal = new Home2.websrc();
            cal.execute();
            fm.beginTransaction().replace(R.id.include2, new request_view_provider()).commit();
        }
    }

    private void openOvarlay() {
        Intent intent=new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:"+getPackageName()));
        startActivityForResult(intent,0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==0){
            if(!Settings.canDrawOverlays(this)){
                Toast.makeText(this,"Screen Overlay Required !", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(checkingNetwork);
        stopService(i);
        finish();
    }

    public void intil()
    {
        username=(TextView)header.findViewById(R.id.txtuser);
        email=(TextView)header.findViewById(R.id.txtemail);
        proimg=(ImageView)header.findViewById(R.id.main_pro);
    }

    public ImageView getProimg() {
        return proimg;
    }
    public TextView getUsername() {
        return username;
    }

    class  websrc extends AsyncTask<Void,String,JSONObject> {

        @Override
        protected JSONObject doInBackground(Void... params) {
            webservices wb=new webservices();
            try {
                JSONObject jsobj=new JSONObject();
                jsobj.put("log_id",login_id);
                wb.webservices("getdata2",jsobj);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return wb.jsonres;
        }
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog= ProgressDialog.show(Home2.this,"Loding","Please Wait...");
            StrictMode.ThreadPolicy pol = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(pol);

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
                brth=jsonObject.getString("brth");
                lic=jsonObject.getString("lic");
                minprc=jsonObject.getDouble("minprc");
            } catch (Exception e) {
                e.printStackTrace();
            }

            sh=getSharedPreferences("user", Context.MODE_PRIVATE);
            shed=sh.edit();
            shed.putString("con",con);
            shed.putString("pin",pin);
            shed.putString("name",name);
            shed.putString("image",imgfile);
            shed.putString("brth",brth);
            shed.putString("lic",lic);
            shed.putFloat("minprc", (float) minprc);
            shed.apply();

            username.setText(name);
            email.setText(log_eml);
            Glide.with(header.getContext()).load(getString(R.string.imgupload)+imgfile).apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true)).into(proimg);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        frm = getSupportFragmentManager().findFragmentByTag("req_frag_Route");
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else if (frm!= null && frm.isVisible()) {
            reqCancelAlert(frm);
        } else if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            exit(this);
        } else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
            this.finish();
        }
    }

    public void reqCancelAlert(final Fragment frm){

        if(frm==getSupportFragmentManager().findFragmentByTag("req_pay")){
            AlertDialog.Builder intru = new AlertDialog.Builder(frm.getActivity());
            intru.setTitle("Request");
            intru.setMessage("You Can't Cancel At This Stage !").setNegativeButton("OK", null);
            AlertDialog alert = new Functions().funAlertShow(intru);
            new Functions().funSetCancleable(alert);
        }else {
            AlertDialog.Builder intru = new AlertDialog.Builder(frm.getActivity());
            intru.setTitle("Request");
            intru.setMessage("Are You Sure to Cancel Request ?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new SetRequest(getResources().getString(R.string.req_Canceled), ((request_route) frm).getReqid(), frm.getActivity());
                            getSupportFragmentManager().popBackStack();
                        }
                    });
                }
            }).setNegativeButton("No", null);
            AlertDialog alert = new Functions().funAlertShow(intru);
            new Functions().funSetCancleable(alert);
        }
    }

    private void exit(Activity activity){
        AlertDialog.Builder intru = new AlertDialog.Builder(activity);
        intru.setTitle("Exit");
        intru.setMessage("Are You Sure ?").setPositiveButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishAndRemoveTask();
            }
        }).setNegativeButton("Stay Here !", null);
        AlertDialog alert = new Functions().funAlertShow(intru);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.nav2_logout){
            Fragment fr=changeFrg();
            if(fr==null){
                sh=getSharedPreferences("user", Context.MODE_PRIVATE);
                shed=sh.edit();
                shed.clear();
                shed.apply();

                logOut();
                Toast.makeText(this,"You are logged out", Toast.LENGTH_SHORT).show();

                return true;
            }else {
                reqCancelAlert(fr);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void logOut() {
        Intent intent=new Intent(Home2.this,MainActivity.class);
        new Functions().newActivity(intent);
        startActivity(intent);
        finish
                ();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav2_profile) {
            Fragment fr=changeFrg();
            if(fr==null){
                fm.beginTransaction().replace(R.id.include2,new Provider_profile(),"PProfile").addToBackStack(null).commit();
            }else {
                reqCancelAlert(fr);
            }
        }

        else if(id == R.id.nav2_requests){
            Fragment fr=changeFrg();
            if(fr==null){
                fm.beginTransaction().replace(R.id.include2,new request_view_provider(),"req_reqs").commit();
            }else {
                reqCancelAlert(fr);
            }
        }

        else if(id == R.id.nav2_history){
            Fragment fr=changeFrg();
            if (fr==null){
                Bundle b=new Bundle();
                b.putInt("for",2);
                Fragment frm=new User_History();
                frm.setArguments(b);
                fm.beginTransaction().replace(R.id.include2,frm,"his_pro").commit();
            }else{
                reqCancelAlert(fr);
            }
        }

        else if(id == R.id.nav2_Logout_menu){
            Fragment fr=changeFrg();
            if(fr==null){
                sh=getSharedPreferences("user", Context.MODE_PRIVATE);
                shed=sh.edit();
                shed.clear();
                shed.apply();

                logOut();
                Toast.makeText(this,"You are logged out", Toast.LENGTH_SHORT).show();
            }else {
                reqCancelAlert(fr);
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private Fragment changeFrg() {
        Fragment req_pays = getSupportFragmentManager().findFragmentByTag("req_pay");
        Fragment req_route=getSupportFragmentManager().findFragmentByTag("req_frag_Route");
        if(req_route!=null&&req_route.isVisible()){
            return req_route;
        }else if(req_pays!=null&& req_pays.isVisible()){
            return req_pays;
        }else{
            return null;
        }
    }

}
