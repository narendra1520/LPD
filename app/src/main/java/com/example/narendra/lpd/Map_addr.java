package com.example.narendra.lpd;


import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Map_addr extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener {
    ProgressDialog progressDialog;
    GoogleMap mMap;
    JSONArray maparr, detarr,prcarr,ratearr;
    SharedPreferences sh, sh2;
    ArrayList<LatLng> listPoints;
    SharedPreferences.Editor shed;
    SupportMapFragment frg;
    Snackbar snb;
    boolean snbs = false;
    int mid;
    Double deslt, desln, srclt, srcln;
    JSONObject mapobj, detobj,prcobj,rateobj;
    Marker desti, srci;
    String distance="0 km";
    RoutethePolyLine routethePolyLine=new RoutethePolyLine();
    private String time;
    ImageButton refresh;TextView dist;
    HashMap<Object, CustomInfoData> infoDataHashMap=new HashMap<>();
    CustomInfoData holder;

    public Map_addr() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_map_addr, container, false);
        refresh= (ImageButton) v.findViewById(R.id.btnrefresh1);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRefresh();
            }
        });
        dist= (TextView) v.findViewById(R.id.txtdist1);
        listPoints = new ArrayList<>();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle(R.string.Selectprovider);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapmarker calws = new mapmarker();
        calws.execute();
    }

    public void onRefresh(){
        String url = routethePolyLine.getRequestUrl(new LatLng(srclt,srcln), new LatLng(deslt,desln));
        TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
        taskRequestDirections.execute(url);
    }

    ArrayList<LatLng> latlng = new ArrayList<>();
    ArrayList<String> con = new ArrayList<>();
    ArrayList<String> name = new ArrayList<>();
    ArrayList<Integer> pid = new ArrayList<>();
    ArrayList<Float> price = new ArrayList<>();
    ArrayList<Float> rate = new ArrayList<>();
    ArrayList<Integer> rateid = new ArrayList<>();
    ArrayList<Integer> proid = new ArrayList<>();
    ArrayList<String> img = new ArrayList<>();

    MarkerOptions option = new MarkerOptions();

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(this);
        mMap.setOnMarkerClickListener(this);

        sh = getActivity().getSharedPreferences("package", Context.MODE_PRIVATE);
        deslt = Double.longBitsToDouble(sh.getLong("deslat", 0));
        desln = Double.longBitsToDouble(sh.getLong("deslong", 0));
        srclt = Double.longBitsToDouble(sh.getLong("srclat", 0));
        srcln = Double.longBitsToDouble(sh.getLong("srclong", 0));

        desti = googleMap.addMarker(new MarkerOptions().position(new LatLng(deslt, desln)).icon(BitmapDescriptorFactory.fromResource(R.drawable.destipick)).title("Destination"));
        srci = googleMap.addMarker(new MarkerOptions().position(new LatLng(srclt, srcln)).icon(BitmapDescriptorFactory.fromResource(R.drawable.sourcepick)).title("Source"));

        for(int i = 0; i < ratearr.length(); i++){
            try {
                rateobj=ratearr.getJSONObject(i);
                rate.add(BigDecimal.valueOf(rateobj.getDouble("rate")).floatValue());
                rateid.add(rateobj.getInt("rateid"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < maparr.length(); i++) {
            try {
                mapobj = maparr.getJSONObject(i);
                detobj = detarr.getJSONObject(i);
                prcobj=prcarr.getJSONObject(i);

                Double lat = mapobj.getDouble("lat");
                Double lng = mapobj.getDouble("long");

                latlng.add(new LatLng(lat, lng));
                pid.add(mapobj.getInt("id"));
                name.add(detobj.getString("name"));
                con.add(detobj.getString("con"));
                img.add(detobj.getString("img"));
                proid.add(prcobj.getInt("proid"));
                price.add(BigDecimal.valueOf(prcobj.getDouble("prc")).floatValue());

               /* for(int j = 0; j < ratearr.length(); j++){
                    try {
                        rateobj=ratearr.getJSONObject(i);
                        if(mapobj.getInt("id")==rateobj.getInt("rateid")){
                            rate.add(BigDecimal.valueOf(rateobj.getDouble("rate")).floatValue());
                        }else {
                            rate.add((float) -1);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }*/

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        for (Integer id:proid) {
            if(!rateid.contains(id)){
                rateid.add(proid.indexOf(id),id);
                rate.add(proid.indexOf(id), (float) -1);
            }
        }

        for (int i = 0; i < latlng.size(); i++) {
            option.position(latlng.get(i));
            option.icon(BitmapDescriptorFactory.fromResource(R.drawable.proivderpick));
            Marker mk=googleMap.addMarker(option);mk.setTag(i);
            CustomInfoData customInfoData=new CustomInfoData(img.get(i),name.get(i),con.get(i),price.get(i),rate.get(i));
            infoDataHashMap.put(mk.getTag(),customInfoData);
        }

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        mMap.setMyLocationEnabled(true);
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(srci.getPosition()).include(desti.getPosition());
        LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 100);
        mMap.animateCamera(cu);
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(final Marker marker) {
                if(marker.getTitle()==null){
                    View view = getActivity().getLayoutInflater().inflate(R.layout.cutominfo, null);

                    TextView tvName = (TextView) view.findViewById(R.id.tv_name);
                    TextView tvContact = (TextView) view.findViewById(R.id.tv_contact);
                    TextView tvPrice = (TextView) view.findViewById(R.id.tv_price);
                    TextView tvRate = (TextView) view.findViewById(R.id.tv_rate);
                    ImageView tvImg= (ImageView) view.findViewById(R.id.tv_image);

                    holder = infoDataHashMap.get(marker.getTag());

                    tvName.setText(holder.name);
                    tvContact.setText(holder.contact);
                    tvPrice.setText(String.valueOf(holder.price+"(Min.)"));
                    Glide.with(getActivity()).load(getActivity().getString(R.string.imgupload)+holder.img).listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            if(marker.isInfoWindowShown()){
                                marker.hideInfoWindow();
                                marker.showInfoWindow();
                            }
                            return false;
                        }
                    }).into(tvImg);
                    if(holder.rate<0){
                        tvRate.setText("-");
                    }else {
                        tvRate.setText(String.valueOf(holder.rate));
                    }
                    return view;
                }else {
                    View view = getActivity().getLayoutInflater().inflate(R.layout.custominfo_other, null);
                    TextView tvOther = (TextView) view.findViewById(R.id.tv_othermarker);
                    tvOther.setText(marker.getTitle());
                    return view;
                }

            }
        });
        onRefresh();
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
            TaskParser taskParser = new TaskParser();
            taskParser.execute(s);
        }
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
                polylineOptions.color(Color.BLUE);
                polylineOptions.geodesic(true);
            }

            if (polylineOptions!=null) {
                mMap.addPolyline(polylineOptions);
            } else {
                Toast.makeText(getActivity(), "Direction not found!", Toast.LENGTH_SHORT).show();
            }
            dist.setText("Distance: "+distance);
        }
    }
    float cost=0;
    @Override
    public boolean onMarkerClick(final Marker marker) {
        if(marker.equals(desti)||marker.equals(srci))
        {return false;}
        else {
            Float prc=price.get((int) marker.getTag());
            cost= Float.parseFloat(distance.substring(0,distance.indexOf("km")));
            if(cost==0){
                showsnackForNot();
            }
            else if(cost>=0&&cost<=4){
                cost=cost*10+5+prc;showsnack(marker);
            }else if(cost>4&&cost<=10){
                cost=cost*9+10+prc;showsnack(marker);
            }else if(cost>10&&cost<=20){
                cost=cost*8+20+prc;showsnack(marker);
            }else if(cost>20&&cost<=30){
                cost=cost*7+30+prc;showsnack(marker);
            }else if(cost>30&&cost<=40){
                cost=cost*7+40+prc;showsnack(marker);
            }else if(cost>40){
                showsnackForNot();
            }
        return false;
        }
    }

    private void showsnackForNot() {
        snb = Snackbar.make(getActivity().findViewById(android.R.id.content), "Services Not Available for too Long Distance Or Getting Error", Snackbar.LENGTH_INDEFINITE);
        snbs = true;
        snb.show();
    }

    void showsnack(final Marker marker){
        snb = Snackbar.make(getActivity().findViewById(android.R.id.content), "Estimated Cost:" +cost+" RS", Snackbar.LENGTH_INDEFINITE)
                .setAction("Confirm", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mid = (int) marker.getTag();
                        UpPack pck = new UpPack();
                        pck.execute();
                    }
                }).setActionTextColor(Color.WHITE);
        snbs = true;
        snb.show();
    }

    /**
     * Called when the fragment is no longer in use.  This is called
     * after {@link #onStop()} and before {@link #onDetach()}.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (snbs){
            snb.dismiss();
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (snbs){
            snb.dismiss();
        }
    }

    private class UpPack extends AsyncTask<Void,String,JSONObject>{

        @Override
        protected JSONObject doInBackground(Void... params) {
            webservices wb=new webservices();
            try {
                sh2=getActivity().getSharedPreferences("user",Context.MODE_PRIVATE);
                JSONObject jsobj=new JSONObject();
                jsobj.put("snd",sh.getString("snd",null))
                        .put("rcv",sh.getString("rcv",null))
                        .put("srclat",srclt).put("srclong",srcln)
                        .put("deslat",deslt).put("deslong",desln)
                        .put("maincat",sh.getString("maincat",null))
                        .put("subcat",sh.getString("subcat",null))
                        .put("reccon",sh.getString("reccon",null))
                        .put("pid",pid.get(mid)).put("uid",sh2.getInt("login_id",-1))
                        .put("cost",cost);
                wb.webservices("package",jsobj);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return wb.jsonres;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog= ProgressDialog.show(getContext(),"Package","Requesting.....");
        }
        int code;
        String res;
        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            progressDialog.dismiss();
            try {
                res=jsonObject.getString("res");
                code=jsonObject.getInt("code");
                if(code==1){
                    AlertDialog.Builder intru = new AlertDialog.Builder(getActivity());
                    intru.setTitle("Package");
                    intru.setMessage(res);
                    intru.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sh=getActivity().getSharedPreferences("package",Context.MODE_PRIVATE);
                            shed=sh.edit();
                            shed.clear();
                            shed.apply();
                            FragmentManager fm=getActivity().getSupportFragmentManager();
                            for(int i=0;i<fm.getBackStackEntryCount();++i);{
                                fm.popBackStack();
                            }
                            android.support.v4.app.FragmentManager mn = getActivity().getSupportFragmentManager();
                            mn.beginTransaction().replace(R.id.include, new request_view()).commit();
                        }
                    });
                    AlertDialog alert = new Functions().funAlertShow(intru);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class mapmarker extends AsyncTask<Void,String,JSONObject>{

        @Override
        protected JSONObject doInBackground(Void... params) {
            webservices wb=new webservices();
            try {
                JSONObject jsobj=new JSONObject();
                wb.webservices("mapmarker",jsobj);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return wb.jsonres;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog= ProgressDialog.show(getContext(),"Map","Please Wait...");
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            progressDialog.dismiss();
            try {
                JSONArray resjarr=jsonObject.getJSONArray("response");
                JSONObject getjobj=resjarr.getJSONObject(0);
                int code=getjobj.getInt("code");
                String msg=getjobj.getString("msg");

                if(code==1) {
                    JSONObject datajobj = resjarr.getJSONObject(1);
                    maparr = datajobj.getJSONArray("d1");
                    detarr = datajobj.getJSONArray("d2");
                    prcarr=datajobj.getJSONArray("d3");
                    ratearr=datajobj.getJSONArray("d4");
                    frg = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map1);
                    frg.getMapAsync(Map_addr.this);
                }else {
                    Toast.makeText(getActivity(),msg, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean getSnack(){return snbs;}
    public Snackbar getSnackbar(){return snb;}
}
