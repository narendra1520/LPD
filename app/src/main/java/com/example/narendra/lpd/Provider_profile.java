package com.example.narendra.lpd;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
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
public class Provider_profile extends Fragment implements View.OnClickListener {


    EditText name,contact,pin,price;
    String strname,strcon,stremail,strpin,strimage,brthdate,lic;
    static ImageView profile;
    TextView edit,chpwd,email,lice,birth;
    Uri fileuri;
    Bitmap image;
    LinearLayout main;
    SharedPreferences sh;
    SharedPreferences.Editor shed;
    ProgressDialog progressDialog;
    Button up;
    Float minprc;
    int code,id,upcode;
    KeyListener listener;

    public Provider_profile() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_provider_profile, container, false);
        intil(v);
        return v;
    }

    private void intil(View v) {
        main=(LinearLayout)v.findViewById(R.id.Mainlayout);
        name=(EditText)v.findViewById(R.id.pedname);
        listener=name.getKeyListener();
        contact=(EditText)v.findViewById(R.id.pedcon);
        email=(TextView)v.findViewById(R.id.ptxtemail);
        pin=(EditText)v.findViewById(R.id.pedpin);
        price=(EditText)v.findViewById(R.id.pedprice);
        birth=(TextView)v.findViewById(R.id.pedbdate);
        lice=(TextView) v.findViewById(R.id.pedlic);
        profile=(ImageView)v.findViewById(R.id.pprofile);
        edit=(TextView)v.findViewById(R.id.pbtnedit);
        edit.setOnClickListener(this);
        chpwd=(TextView)v.findViewById(R.id.pbtnpwd);
        chpwd.setOnClickListener(this);
        up= (Button) v.findViewById(R.id.pbtnup);

        name.setKeyListener(null);
        contact.setKeyListener(null);
        pin.setKeyListener(null);
        price.setKeyListener(null);
        //birth.setKeyListener(null);
        //lice.setKeyListener(null);

        sh=getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        strcon=sh.getString("con",null);
        strpin=sh.getString("pin",null);
        strname=sh.getString("name",null);
        strimage=sh.getString("image",null);
        stremail=sh.getString("email",null);
        minprc=sh.getFloat("minprc",0);
        brthdate=sh.getString("brth",null);
        lic=sh.getString("lic",null);
        id=sh.getInt("login_id",-1);

        name.setText(strname);contact.setText(strcon);email.setText(stremail);pin.setText(strpin);
        price.setText(String.valueOf(minprc));birth.setText(brthdate);lice.setText(lic);
        Glide.with(getActivity()).load(getString(R.string.imgupload)+strimage).apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true)).into(profile);
    }

    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity activity=(AppCompatActivity)getActivity();
        ActionBar actionBar= activity.getSupportActionBar();
        actionBar.setTitle(R.string.Profile);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.pbtnedit) {
            profile.setOnClickListener(this);
            up.setClickable(true);
            up.setVisibility(v.VISIBLE);
            up.setOnClickListener(this);

          /*  birth.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(hasFocus)
                    {
                        final Calendar c=Calendar.getInstance();
                        int y = c.get(Calendar.YEAR);
                        int m=c.get(Calendar.MONTH);
                        int d=c.get(Calendar.DAY_OF_MONTH);
                        DatePickerDialog datePickerDialog= new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                birth.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                            }
                        },y,m,d);
                        datePickerDialog.show();
                    }
                }
            });*/

            name.setKeyListener(listener);
            name.requestFocus();
            contact.setKeyListener(listener);
            email.setKeyListener(listener);
            pin.setKeyListener(listener);
            price.setKeyListener(listener);
            //birth.setKeyListener(listener);
            //lice.setKeyListener(listener);
        } else if (v.getId() == R.id.pbtnpwd) {
            Intent toforget = new Intent(getActivity(), forgetpassword.class);
            startActivity(toforget);
        } else if (v.getId() == R.id.pprofile) {
            boolean set = new permision().permision(getActivity(), 1);
            if (!set) {
                Toast.makeText(getActivity(), "Problem occurred", Toast.LENGTH_SHORT).show();
            } else {
                CharSequence item[] = new CharSequence[]{"Camera", "Gallery"};
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setItems(item, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, 1);
                        } else if (which == 1) {
                            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            intent.setType("image/*");
                            startActivityForResult(Intent.createChooser(intent, "select Picture"), 2);
                        }
                    }
                });
                builder.show();
            }
        } else if (v.getId() == R.id.pbtnup) {
            if (!new validate().valiName(name.getText().toString())) {
                name.setError("Valid Name Required");
                name.requestFocus();
            } else if (!new validate().valiContact(contact.getText().toString())) {
                contact.setError(getString(R.string.error_number));
                contact.requestFocus();
            }/* else if ((birth.getText().toString()).isEmpty()) {
                birth.setError("Enter BirthDate");
                birth.requestFocus();
            }*/ else if (!new validate().valipin(pin.getText().toString())) {
                pin.setError("Invalid PinCode");
                pin.requestFocus();
            } else if (!new validate().valiMinprc(price.getText().toString())) {
                price.setError("Invalid Price");
                price.requestFocus();
            } /*else if (!new validate().valiLic(lice.getText().toString())) {
                lice.setError("License No must be 15\ncharacter long");
                lice.requestFocus();
            } else if (!new validate().valiBirth(birth.getText().toString())) {
                birth.setError("BirthDate greater than today");
            } */else {
                webservice calws = new webservice();
                calws.execute();
            }
        }
    }

    public static ImageView getProfile() {
        return profile;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK)
        {
            Bundle extra=data.getExtras();
            fileuri=data.getData();
            image=(Bitmap)extra.get("data");
            Provider_profile.getProfile().setImageBitmap(image);

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
                image = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),fileuri);
                Provider_profile.getProfile().setImageBitmap(image);
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
            strpin = pin.getText().toString();
            minprc=Float.parseFloat(price.getText().toString());
           // brthdate=birth.getText().toString();
            //lic=lice.getText().toString();
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            webservices wb=new webservices();
            try {
                JSONObject jsobj=new JSONObject();
                jsobj.put("name", strname);
                jsobj.put("con", strcon);
                jsobj.put("pin", strpin);
                jsobj.put("prc", minprc);
                //jsobj.put("brth", brthdate);
                //jsobj.put("lice", lic);
                jsobj.put("logid",id);

                wb.webservices("update_pro",jsobj);

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
                    upimg oimg=new upimg();
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
                    Fragment frm=getActivity().getSupportFragmentManager().findFragmentByTag("PProfile");
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
        shed.putFloat("minprc",minprc);
        //shed.putString("brth",brthdate);
        //shed.putString("lic",lic);
        shed.apply();
        ImageView img = new Home2().getProimg();
        Glide.with(getActivity()).load(getString(R.string.imgupload)+s).apply(new RequestOptions().skipMemoryCache(true)).into(img);
        new Home2().getUsername().setText(strname);
    }
}
