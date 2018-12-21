package com.example.narendra.lpd;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;


/**
 * A simple {@link Fragment} subclass.
 */
public class Review extends Fragment {

    RatingBar ratingBar;
    EditText review;
    Button done;
    float numrate;
    String textreview,res;
    int reqid,code;
    ProgressDialog progressDialog;
    public Review() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_review, container, false);
        reqid=getArguments().getInt("reqid");
        new getreview().execute();
        intil(v);
        return v;
    }

    private void intil(View v) {
        ratingBar= (RatingBar) v.findViewById(R.id.rate);
        review= (EditText) v.findViewById(R.id.edreview);
        done= (Button) v.findViewById(R.id.btnreview);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numrate=ratingBar.getRating();
                textreview =review.getText().toString();
                new review().execute();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity activity=(AppCompatActivity)getActivity();
        ActionBar actionBar= activity.getSupportActionBar();
        actionBar.setTitle(R.string.review);
    }

    class  review extends AsyncTask<Void,String,JSONObject> {

        @Override
        protected JSONObject doInBackground(Void... params) {
            webservices wb = new webservices();
            try {
                JSONObject jsobj = new JSONObject();
                jsobj.put("req_id", reqid);
                jsobj.put("rate", numrate);
                jsobj.put("review", textreview);

                wb.webservices("review", jsobj);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return wb.jsonres;
        }
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog= ProgressDialog.show(getContext(),"Review","Reviewing...");
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
                    getActivity().getSupportFragmentManager().popBackStack();
                }else {
                    Toast.makeText(getActivity(),res, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    class  getreview extends AsyncTask<Void,String,JSONObject> {

        @Override
        protected JSONObject doInBackground(Void... params) {
            webservices wb = new webservices();
            try {
                JSONObject jsobj = new JSONObject();
                jsobj.put("req_id", reqid);
                wb.webservices("get_review", jsobj);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return wb.jsonres;
        }
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog= ProgressDialog.show(getContext(),"Review","Loading...");
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
                    JSONArray dataarr = datajobj.getJSONArray("d1");
                    JSONObject outdata = dataarr.getJSONObject(0);
                    textreview=outdata.getString("review");
                    numrate= BigDecimal.valueOf(outdata.getDouble("rate")).floatValue();
                    review.setText(textreview);
                    ratingBar.setRating(numrate);
                }else {
                    Toast.makeText(getActivity(),res, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
