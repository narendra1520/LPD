package com.example.narendra.lpd;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class frag_provider extends Fragment implements View.OnClickListener, View.OnFocusChangeListener {

    LinearLayout group;
    ProgressDialog progressDialog;
    EditText name,contact,email,pin,password,minprice,birthdate,license;
    String strname,strcon,stremail,strpin,strpwd,strbrth,strlic;
    String msg;
    Double latitude,longitude;
    Uri fileuri;
    int code,id;
    ImageView profile;
    int y,m,d;
    Float minprc;
    Bitmap image;
    CheckBox showpwd;
    JSONArray resjarr;
    JSONObject getjobj;

    public frag_provider() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v=inflater.inflate(R.layout.fragment_frag_provider, container, false);
        intl(v);
        Button OnRegister=(Button)v.findViewById(R.id.btnreg);
        OnRegister.setOnClickListener(this);
        birthdate.setOnFocusChangeListener(this);
        profile.setOnClickListener(this);
        return v;
    }


    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity activity=(AppCompatActivity)getActivity();
        ActionBar actionBar= activity.getSupportActionBar();
        actionBar.setTitle(R.string.Provider);
    }

    public void intl(View v)
    {
        group=(LinearLayout)v.findViewById(R.id.Mainlayout);
        name=(EditText)v.findViewById(R.id.edname);
        contact=(EditText)v.findViewById(R.id.edcon);
        email=(EditText)v.findViewById(R.id.edemail);
        new Functions().funSetFilter(email);
        pin=(EditText)v.findViewById(R.id.edpin);
        password=(EditText)v.findViewById(R.id.edpwd);
        minprice=(EditText)v.findViewById(R.id.edprice);
        birthdate=(EditText)v.findViewById(R.id.edbdate);
        license=(EditText)v.findViewById(R.id.edlicense);
        profile=(ImageView)v.findViewById(R.id.profile);
        showpwd=(CheckBox)v.findViewById(R.id.showpwd);
        password.setTransformationMethod(PasswordTransformationMethod.getInstance());

        showpwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                else{
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        boolean set=false;
        if(v.getId()==R.id.profile) {
            set= new permision().permision(getActivity(),1);
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
        if(v.getId()==R.id.btnreg) {
            set= new permision().permision(getActivity(),2);
            if(!set){
                Toast.makeText(getActivity(), "Problem Occurred", Toast.LENGTH_SHORT).show();
            }
            else {
                MyLocation gt=new MyLocation(getContext());
                if(gt.canGetLocation){
                    latitude=gt.getLatitude();
                    longitude=gt.getLongitude();
                }

                ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo net = cm.getActiveNetworkInfo();
                boolean isCon = net != null && net.isConnectedOrConnecting();

                if (!new validate().valiName(name.getText().toString())) {
                    name.setError("Valid Name Required");
                    name.requestFocus();
                } else if (!new validate().valiContact(contact.getText().toString())) {
                    contact.setError(getString(R.string.error_number));
                    contact.requestFocus();
                } else if ((birthdate.getText().toString()).isEmpty()) {
                    birthdate.setError("Enter BirthDate");
                    birthdate.requestFocus();
                } else if (!new validate().valipin(pin.getText().toString())) {
                    pin.setError("Invalid PinCode");
                    pin.requestFocus();
                } else if (!new validate().valiEmail(email.getText().toString())) {
                    email.setError(getString(R.string.error_invalid_email));
                    email.requestFocus();
                } else if (!new validate().valiPwd(password.getText().toString())) {
                    password.setError("Password length must\nbe 8");
                    password.requestFocus();
                } else if (!new validate().valiMinprc(minprice.getText().toString())) {
                    minprice.setError("Invalid Price");
                    minprice.requestFocus();
                } else if (!new validate().valiLic(license.getText().toString())) {
                    license.setError("Invalid License Format(eg.MH0020170000001)");
                    license.requestFocus();
                }else if (!new validate().valiBirth(birthdate.getText().toString())) {
                    birthdate.setError("BirthDate Greater than Today");
                } else if (!isCon) {
                    Toast.makeText(getActivity(), "No Internet", Toast.LENGTH_SHORT).show();
                } else {
                    frag_provider.webservice calws = new webservice();
                    calws.execute();
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK)
        {
            Bundle extra=data.getExtras();
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

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(hasFocus)
        {
            final Calendar c=Calendar.getInstance();
            y=c.get(Calendar.YEAR);
            m=c.get(Calendar.MONTH);
            d=c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog= new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    birthdate.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                }
            },y,m,d);
            datePickerDialog.show();
        }
    }

    class  webservice extends AsyncTask<Void,String,JSONObject> {
        @Override
        protected JSONObject doInBackground(Void... params) {
            webservices wb=new webservices();
            try{
             JSONObject jsobj=new JSONObject();

             jsobj.put("name", strname);
             jsobj.put("con", strcon);
             jsobj.put("email", stremail);
             jsobj.put("pin", strpin);
             jsobj.put("pwd", strpwd);
                jsobj.put("minprice",minprc);
                jsobj.put("brdate", strbrth);
                jsobj.put("license", strlic);
                jsobj.put("llat",latitude);
                jsobj.put("llong",longitude);

                wb.webservices("provider",jsobj);
         }
         catch (Exception e)
         {
             e.printStackTrace();
         }
         return wb.jsonres;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=ProgressDialog.show(getContext(),"Registering","Please Wait...");

            strname = name.getText().toString();
            strcon = contact.getText().toString();
            stremail = email.getText().toString();
            strpin = pin.getText().toString();
            strpwd = password.getText().toString();
            minprc=Float.parseFloat(minprice.getText().toString());
            strlic=license.getText().toString();
            strbrth=birthdate.getText().toString();
        }
        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try {
                resjarr=jsonObject.getJSONArray("response");
                getjobj=resjarr.getJSONObject(0);
                code=getjobj.getInt("code");
                msg=getjobj.getString("msg");

                if(code==1&&fileuri!=null) {
                    JSONObject datajobj = resjarr.getJSONObject(1);
                    JSONArray dataarr = datajobj.getJSONArray("d1");
                    JSONObject outdata = dataarr.getJSONObject(0);
                    id=outdata.getInt("login_id");
                    frag_provider.upimg oimg=new upimg();
                    oimg.execute();
                }else {
                    OnPost();
                }
            } catch (JSONException e) {
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
            OnPost();
        }
    }
    private void OnPost()
    {
        progressDialog.dismiss();

        if(code==1)
        {
            AlertDialog.Builder intru = new AlertDialog.Builder(getActivity());
            intru.setTitle("Registration");
            intru.setMessage(msg);
            intru.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent=new Intent(getActivity(),MainActivity.class);
                    new Functions().newActivity(intent);
                    startActivity(intent);
                    getActivity().finishAndRemoveTask();
                }
            });
            AlertDialog alert = new Functions().funAlertShow(intru);
        }
        else if(code==3) {
            email.setError(msg);
            email.requestFocus();
        }
        else{
            Toast.makeText(getContext(),msg, Toast.LENGTH_SHORT).show();
        }
    }
}
