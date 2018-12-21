package com.example.narendra.lpd;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.method.KeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class userprofile extends Fragment implements View.OnClickListener{

    EditText name,contact,pin;
    String strname,strcon,stremail,strpin,strimage;
    ImageView profile;
    TextView edit,chpwd,email;
    Uri fileuri;
    Bitmap image;
    LinearLayout main;
    SharedPreferences sh;
    SharedPreferences.Editor shed;
    ProgressDialog progressDialog;
    Button up;
    int code,id,upcode;
    KeyListener litener;

    public userprofile() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_userprofile, container, false);
        intil(v);
        return v;
    }

    private void intil(View v) {
        main=(LinearLayout)v.findViewById(R.id.Mainlayout);
        name=(EditText) v.findViewById(R.id.edname);
        contact= (EditText) v.findViewById(R.id.edcon);
        email=(TextView)v.findViewById(R.id.txtemail);
        pin= (EditText) v.findViewById(R.id.edpin);
        profile=(ImageView)v.findViewById(R.id.profile);
        edit=(TextView)v.findViewById(R.id.btnedit);
        edit.setOnClickListener(this);
        chpwd=(TextView)v.findViewById(R.id.btnpwd);
        chpwd.setOnClickListener(this);
        up= (Button) v.findViewById(R.id.btnup);

        litener=name.getKeyListener();
        name.setKeyListener(null);
        contact.setKeyListener(null);
        pin.setKeyListener(null);

        sh=getActivity().getSharedPreferences("user", MODE_PRIVATE);
        strcon=sh.getString("con",null);
        strpin=sh.getString("pin",null);
        strname=sh.getString("name",null);
        strimage=sh.getString("image",null);
        stremail=sh.getString("email",null);
        id=sh.getInt("login_id",-1);

        name.setText(strname);contact.setText(strcon);email.setText(stremail);pin.setText(strpin);
        Glide.with(getActivity()).load(getString(R.string.imgupload)+strimage).apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true)).into(profile);
    }

    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity activity=(AppCompatActivity)getActivity();
        ActionBar actionBar= activity.getSupportActionBar();
        actionBar.setTitle(R.string.Profile);
    }

    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.btnedit) {
            profile.setOnClickListener(this);
            up.setClickable(true);
            up.setVisibility(v.VISIBLE);
            up.setOnClickListener(this);
            name.setKeyListener(litener);
            name.requestFocus();
            contact.setKeyListener(litener);
            pin.setKeyListener(litener);
        }
        else if(v.getId()==R.id.btnpwd) {
            Intent toforget = new Intent(getActivity(),forgetpassword.class);
            startActivity(toforget);
        }
        else if(v.getId()==R.id.profile) {
           boolean set= new permision().permision(getActivity(),1);
            if(!set){
                Toast.makeText(getActivity(), "Problem occurred", Toast.LENGTH_SHORT).show();
            }
            else {
                CharSequence item[] = new CharSequence[]{"Camera", "Gallery"};
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setItems(item, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, 1);
                        } else if (which == 1) {
                            Intent intent;
                            intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            intent.setType("image/*");
                            startActivityForResult(Intent.createChooser(intent, "select Picture"), 2);
                        }
                    }
                });
                builder.show();
            }
        }
        else if(v.getId()==R.id.btnup){

            if(!new validate().valiName(name.getText().toString()))
            {
                name.setError("Valid Name Format Required");
                name.requestFocus();
            }
            else if(!new validate().valiContact(contact.getText().toString()))
            {
                contact.setError(getString(R.string.error_number));
                contact.requestFocus();
            }
            else if(!new validate().valipin(pin.getText().toString()))
            {
                pin.setError("Invalid PinCode");
                pin.requestFocus();
            }
            else{
                userprofile.webservice calws=new webservice();
                calws.execute();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK)
        {
            Bundle extra=data.getExtras();
            fileuri=data.getData();
            image=(Bitmap)extra.get("data");
            profile.setImageBitmap(image);

            File file=new File(getActivity().getExternalCacheDir(),String.valueOf(System.currentTimeMillis())+".png");
            try {
                FileOutputStream out=new FileOutputStream(file);
                image.compress(Bitmap.CompressFormat.PNG,100,out);
                fileuri=Uri.fromFile(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        else if(requestCode==2 && resultCode==RESULT_OK)
        {
            fileuri=data.getData();
            try {
                image = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(),fileuri);
                profile.setImageBitmap(image);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    class  webservice extends AsyncTask<Void,String,JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog= ProgressDialog.show(getContext(),"Updating","Please Wait...");

            strname = name.getText().toString();
            strcon = contact.getText().toString();
            stremail = email.getText().toString();
            strpin = pin.getText().toString();
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            webservices wb=new webservices();
            try {
                JSONObject jsobj=new JSONObject();
                jsobj.put("name", strname);
                jsobj.put("con", strcon);
                jsobj.put("pin", strpin);
                jsobj.put("logid",id);

                wb.webservices("update",jsobj);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return wb.jsonres;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try {
                code=jsonObject.getInt("code");

                if(code==1&&fileuri!=null) {
                    userprofile.upimg oimg=new upimg();
                    oimg.execute();
                }
                else {
                    OnPost();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class  upimg extends AsyncTask<Void,String,JSONObject> {

        @Override
        protected JSONObject doInBackground(Void... params) {
            uploadimage up=new uploadimage();
            try{
                up.uploadingimage(getContext(),fileuri,id);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return up.jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try {
                upcode=jsonObject.getInt("code");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            OnPost();
        }
    }
    private void OnPost()
    {
        progressDialog.dismiss();

        if(code==1)
        {
            AlertDialog.Builder intru = new AlertDialog.Builder(getActivity());
            intru.setTitle("Update");
            intru.setMessage("Profile Updated");
            intru.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(upcode==1||!strimage.equalsIgnoreCase("-1.png")){
                        setShared(id+".png");
                    }else{
                        setShared("-1.png");
                    }
                    Fragment frm=getActivity().getSupportFragmentManager().findFragmentByTag("Profile");
                    android.support.v4.app.FragmentManager mn = getActivity().getSupportFragmentManager();
                    mn.beginTransaction().detach(frm).attach(frm).commit();
                }
            });
            AlertDialog alert = new Functions().funAlertShow(intru);
        }
    }
    private void setShared(String s){
        sh=getActivity().getSharedPreferences("user",MODE_PRIVATE);
        shed=sh.edit();
        shed.putString("name",strname);
        shed.putString("image",s);
        shed.putString("con",strcon);
        shed.putString("pin",strpin);
        shed.apply();
        ImageView img = new Home().getProimg();
        Glide.with(getActivity()).load(getString(R.string.imgupload)+s).apply(new RequestOptions().skipMemoryCache(true)).into(img);
        new Home().getUsername().setText(strname);
    }
}
