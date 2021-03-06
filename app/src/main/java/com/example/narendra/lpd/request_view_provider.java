package com.example.narendra.lpd;


import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class request_view_provider extends Fragment {

    RecyclerView recyclerView;
    List<request_data> request_datas;
    request_adpater2 request_adpater2;
    SharedPreferences sh;
    SharedPreferences.Editor shed;
    ProgressDialog progressDialog;

    public request_view_provider() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_request_view_provider, container, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.recview_provider);
        request_view_provider.getrequest objreq = new getrequest();
        objreq.execute();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle(R.string.Request);
    }

    public class getrequest extends AsyncTask<Void, String, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(getActivity(), "Loding", "Please Wait...");
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            webservices wb = new webservices();
            try {
                sh = getActivity().getSharedPreferences("user", getActivity().MODE_PRIVATE);
                int login_id = sh.getInt("login_id", -1);

                JSONObject jsobj = new JSONObject();
                jsobj.put("logid", login_id);

                wb.webservices("get_request2", jsobj);
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
                JSONArray resjarr = jsonObject.getJSONArray("response");
                JSONObject getjobj = resjarr.getJSONObject(0);
                int code = getjobj.getInt("code");
                String msg = getjobj.getString("msg");

                if (code == 1) {
                    JSONObject datajobj = resjarr.getJSONObject(1);
                    JSONArray datajarr = datajobj.getJSONArray("d1");
                    JSONArray data2jarr = datajobj.getJSONArray("d2");
                    JSONArray data3jarr = datajobj.getJSONArray("d3");
                    getreq(datajarr, data2jarr,data3jarr);
                } else if (code == 4) {
                    Bundle b = new Bundle();
                    b.putString("msg", msg);
                    Fragment fm = new noData();
                    fm.setArguments(b);
                    android.support.v4.app.FragmentManager mn = getActivity().getSupportFragmentManager();
                    mn.beginTransaction().replace(R.id.include2, fm).commit();
                } else {
                    Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void getreq(JSONArray datajarr, JSONArray data2jarr, JSONArray data3jarr) {
        request_datas = new ArrayList<>();
        try {
            int x = datajarr.length();
            for (int i = x-1; i >= 0; i--) {
                JSONObject outjobj1 = datajarr.getJSONObject(i);
                JSONObject outjobj2 = data2jarr.getJSONObject(i);
                JSONObject outjobj3 = data3jarr.getJSONObject(i);

                request_datas.add(new request_data(getContext(), outjobj2.getDouble("srclat"), outjobj2.getDouble("srclong"), outjobj2.getDouble("deslat"),
                        outjobj2.getDouble("deslong"), outjobj2.getString("sender"), outjobj2.getString("receiver"), outjobj1.getString("date"), outjobj1.getString("status"),
                        BigDecimal.valueOf(outjobj1.getDouble("cost")).floatValue(),outjobj1.getInt("packid"),outjobj1.getInt("reqid"),outjobj2.getString("reccon"),
                        outjobj1.getString("time"),outjobj3.getString("main_cat"),outjobj3.getString("sub_cat"),(float)-1));
            }

            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            request_adpater2 = new request_adpater2(getActivity(), request_datas);
            recyclerView.setAdapter(request_adpater2);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

