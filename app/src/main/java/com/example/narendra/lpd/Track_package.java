package com.example.narendra.lpd;


import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Track_package extends Fragment implements OnMapReadyCallback{

    GoogleMap mMap;
    String distance, time;
    int reqid;
    TextView txtdist,txttime;
    Marker desti, srci, track=null;
    Double deslt, desln, srclt, srcln;
    SupportMapFragment frg;
    RoutethePolyLine routethePolyLine=new RoutethePolyLine();
    ProgressDialog progressDialog;
    ImageButton btnrefresh;
    double lat,lng;
    final Handler handler=new Handler();
    Runnable r;
    MarkerOptions sts;
    public Track_package() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_track_package, container, false);

        txtdist= (TextView) v.findViewById(R.id.txtdisttrack);
        txttime= (TextView) v.findViewById(R.id.txttimetrack);
        btnrefresh= (ImageButton) v.findViewById(R.id.btnrefreshtrack);
        btnrefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnRefresh();
            }
        });

        srclt=getArguments().getDouble("srclat");
        srcln=getArguments().getDouble("srclong");
        deslt=getArguments().getDouble("deslat");
        desln=getArguments().getDouble("deslong");
        reqid =getArguments().getInt("reqid");
        frg=(SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map_track);
        frg.getMapAsync(Track_package.this);
        OnRefresh();
        intentService();
        return v;
    }
    private void intentService(){
        r = new Runnable() {
            public void run() {
                callServices call=new callServices();
                call.execute();
                handler.postDelayed(this, 5000);
            }
        };
        handler.postDelayed(r, 5000);
    }

    public void OnRefresh(){
        String url =routethePolyLine.getRequestUrl(new LatLng(srclt,srcln), new LatLng(deslt,desln));
        Track_package.TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
        taskRequestDirections.execute(url);
    }

    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle(R.string.track);
    }

    @Override
    public void onPause() {
        super.onPause();

        handler.removeCallbacks(r);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        desti = googleMap.addMarker(new MarkerOptions().position(new LatLng(deslt, desln)).icon(BitmapDescriptorFactory.fromResource(R.drawable.destipick)).title("Destination"));
        srci = googleMap.addMarker(new MarkerOptions().position(new LatLng(srclt, srcln)).icon(BitmapDescriptorFactory.fromResource(R.drawable.sourcepick)).title("Pick Up"));
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(srci.getPosition()).include(desti.getPosition());
        LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 100);
        mMap.animateCamera(cu);
    }

    public class TaskParser extends AsyncTask<String, Void, List<List<HashMap<String, String>>> > {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject = null;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jsonObject = new JSONObject(strings[0]);
                DirectionParser directionsParser = new DirectionParser();
                routes = directionsParser.parse(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            //Get list route and display it into the map
            progressDialog.dismiss();
            ArrayList points = null;

            PolylineOptions polylineOptions = null;

            for (int i=0;i<lists.size();i++) {
                points = new ArrayList();
                polylineOptions = new PolylineOptions();
                List<HashMap<String, String>> path=lists.get(i);

                for (int j=0;j<path.size();j++){
                    HashMap<String, String> point=path.get(j);

                    if(j==0){
                        distance=(String) point.get("distance");
                        continue;
                    }else if(j==1){
                        time=(String) point.get("duration");
                        continue;
                    }

                    double lat = Double.parseDouble(point.get("lat"));
                    double lon = Double.parseDouble(point.get("lon"));

                    points.add(new LatLng(lat,lon));
                }

                polylineOptions.addAll(points);
                polylineOptions.width(12);
                polylineOptions.color(Color.RED);
                polylineOptions.geodesic(true);
            }

            if (polylineOptions!=null) {
                mMap.addPolyline(polylineOptions);
            } else {
                Toast.makeText(getActivity(), "Direction not found!", Toast.LENGTH_SHORT).show();
            }
            txtdist.setText("Distance: "+distance);
            txttime.setText("Estimated Time: "+time);
        }
    }

    public class TaskRequestDirections extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String responseString = "";
            try {
                responseString = routethePolyLine.requestDirection(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return  responseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Parse json here
            Track_package.TaskParser taskParser = new TaskParser();
            taskParser.execute(s);
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog= ProgressDialog.show(getContext(),"Route","Finding Route.....");
        }
    }

    private class callServices extends AsyncTask<Void,Void,JSONObject>{

        @Override
        protected JSONObject doInBackground(Void... params) {
            webservices wb=new webservices();
            JSONObject jsobj=new JSONObject();
            try {
                jsobj.put("req_id", reqid);
                wb.webservices("getUpLocation", jsobj);
            }catch (Exception e) {
                e.printStackTrace();
            }
            return wb.jsonres;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            JSONArray resjarr;
            try {
                resjarr = jsonObject.getJSONArray("response");
                JSONObject getjobj = resjarr.getJSONObject(0);
                int code = getjobj.getInt("code");
                String str= getjobj.getString("msg");
                if (code==1) {
                    sts=new MarkerOptions();
                    JSONObject datajobj = resjarr.getJSONObject(1);
                    JSONArray datajarr = datajobj.getJSONArray("d1");
                    JSONObject outjobj1 = datajarr.getJSONObject(0);
                    lat = outjobj1.getDouble("lat");
                    lng = outjobj1.getDouble("long");
                    LatLng l=new LatLng(lat,lng);
                    sts.position(l);
                    sts.title("Reached here");
                    if(track!=null){
                        track.remove();
                        track=mMap.addMarker(sts);
                    }else {
                    track=mMap.addMarker(sts);}
                }
                else {
                    Toast.makeText(getActivity(),str, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
    }


    @Override
    public void onDestroy(){
        super.onDestroy();
        handler.removeCallbacks(r);
    }
}
