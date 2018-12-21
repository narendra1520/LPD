package com.example.narendra.lpd;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;


/**
 * A simple {@link Fragment} subclass.
 */
public class Payment extends Fragment {

    TextView base,pro,total;
    Button btndone;
    EditText edcash;
    int reqid;
    int code;
    String res;
    ProgressDialog progressDialog;
    Float base_prc,pro_prc;
    Float cash;

    public Payment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_payment, container, false);
        reqid =getArguments().getInt("reqid");
        intly(v);
        new costing().execute();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity activity=(AppCompatActivity)getActivity();
        ActionBar actionBar= activity.getSupportActionBar();
        actionBar.setTitle(R.string.payment);
    }

    private void intly(View v) {
        base= (TextView) v.findViewById(R.id.txtbaseprice);
        pro= (TextView) v.findViewById(R.id.txtprvprice);
        total= (TextView) v.findViewById(R.id.txttotalprice);
        edcash= (EditText) v.findViewById(R.id.payedcash);
        btndone= (Button) v.findViewById(R.id.btnpayok);
        btndone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmpay();
            }
        });
    }

    private void confirmpay() {
        if (!new validate().valiMinprc(edcash.getText().toString())) {
            edcash.setError("Invalid Price");
            edcash.requestFocus();
        }else {
            cash= Float.parseFloat((edcash.getText().toString()));
            new SetRequest(getActivity().getResources().getString(R.string.req_Delivered), reqid,getActivity());
            new paying().execute();
        }
    }

    class  costing extends AsyncTask<Void,String,JSONObject> {

        @Override
        protected JSONObject doInBackground(Void... params) {
            webservices wb = new webservices();
            try {
                JSONObject jsobj = new JSONObject();
                jsobj.put("req_id", reqid);
                wb.webservices("costing", jsobj);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return wb.jsonres;
        }
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(getContext(),"Payment","Loading...");
        }
        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            progressDialog.dismiss();
            try {
                JSONArray resjarr=jsonObject.getJSONArray("response");
                JSONObject getjobj=resjarr.getJSONObject(0);
                code=getjobj.getInt("code");
                res=getjobj.getString("msg");
                if(code==1){
                    JSONObject datajobj = resjarr.getJSONObject(1);
                    JSONArray datarr = datajobj.getJSONArray("d1");
                    JSONObject outobj=datarr.getJSONObject(0);
                    base_prc=BigDecimal.valueOf(outobj.getDouble("base_prc")).floatValue();
                    pro_prc=BigDecimal.valueOf(outobj.getDouble("min_prc")).floatValue();
                    setval();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    class  paying extends AsyncTask<Void,String,JSONObject> {

        @Override
        protected JSONObject doInBackground(Void... params) {
            webservices wb = new webservices();
            try {
                JSONObject jsobj = new JSONObject();
                jsobj.put("req_id", reqid);
                jsobj.put("cash", cash);
                wb.webservices("paying", jsobj);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return wb.jsonres;
        }
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog= ProgressDialog.show(getContext(),"Payment","Paying...");
        }
        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            progressDialog.dismiss();
            try {
                JSONArray resjarr=jsonObject.getJSONArray("response");
                JSONObject getjobj=resjarr.getJSONObject(0);
                code=getjobj.getInt("code");
                res=getjobj.getString("msg");
                if(code==1){
                    Bundle b=new Bundle();
                    b.putInt("for",2);
                    Fragment frm=new User_History();
                    frm.setArguments(b);
                    android.support.v4.app.FragmentManager mn = getActivity().getSupportFragmentManager();
                    getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    mn.beginTransaction().replace(R.id.include2,frm).commit();
                }else {
                    Toast.makeText(getActivity(),res, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void setval() {
        Float t_prc=base_prc+pro_prc;
        base.setText(""+base_prc+" RS");
        pro.setText(""+pro_prc+" RS");
        total.setText(""+t_prc+" RS");
    }
}
