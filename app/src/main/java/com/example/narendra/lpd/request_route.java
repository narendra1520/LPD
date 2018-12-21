package com.example.narendra.lpd;


import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class request_route extends Fragment implements OnMapReadyCallback, View.OnClickListener {

    GoogleMap mMap;
    String distance, time,send,recei,cont;
    int reqid;
    TextView txtdist,txttime,slsend,slrecei,slcont;
    Marker desti, srci;
    Double deslt, desln, srclt, srcln;
    SupportMapFragment frg;
    RoutethePolyLine routethePolyLine=new RoutethePolyLine();
    ProgressDialog progressDialog;
    ImageButton btnrefresh;
    Button reqdone,reqcancel;

    public request_route() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_request_route, container, false);

        txtdist= (TextView) v.findViewById(R.id.txtdist);
        txttime= (TextView) v.findViewById(R.id.txttime);
        slsend= (TextView) v.findViewById(R.id.slsend);
        slrecei= (TextView) v.findViewById(R.id.slrecei);
        slcont= (TextView) v.findViewById(R.id.slcont);
        reqdone= (Button) v.findViewById(R.id.btnreqdone);
        reqdone.setOnClickListener(this);
        reqcancel= (Button) v.findViewById(R.id.btnreqcancel);
        reqcancel.setOnClickListener(this);

        btnrefresh= (ImageButton) v.findViewById(R.id.btnrefresh);
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
        send=getArguments().getString("send");
        recei=getArguments().getString("recei");
        cont=getArguments().getString("cont");
        reqid=getArguments().getInt("reqid");

        frg=(SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map_route);
        frg.getMapAsync(request_route.this);
        setSlide();
        OnRefresh();
        new SetRequest(getActivity().getResources().getString(R.string.req_InTransit), reqid,getActivity());
        return v;
    }

    private void setSlide() {
        slsend.setText(send);
        slrecei.setText(recei);
        slcont.setText(cont);
    }

    public void OnRefresh(){
        String url =routethePolyLine.getRequestUrl(new LatLng(srclt,srcln), new LatLng(deslt,desln));
        request_route.TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
        taskRequestDirections.execute(url);
    }

    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle(R.string.route);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        desti = googleMap.addMarker(new MarkerOptions().position(new LatLng(deslt, desln)).icon(BitmapDescriptorFactory.fromResource(R.drawable.destipick)).title("Destination"));
        srci = googleMap.addMarker(new MarkerOptions().position(new LatLng(srclt, srcln)).icon(BitmapDescriptorFactory.fromResource(R.drawable.sourcepick)).title("Pick Up"));

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
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btnreqdone){
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
            alertDialog.setTitle("Request");
            alertDialog.setMessage("Is Request Complete?").setPositiveButton("Done", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Bundle b=new Bundle();
                    b.putInt("reqid", reqid);
                    Fragment fm=new Payment();
                    fm.setArguments(b);
                    android.support.v4.app.FragmentManager mn = getActivity().getSupportFragmentManager();
                    mn.beginTransaction().replace(R.id.include2,fm,"req_pay").addToBackStack(null).commit();
                }
            });
            AlertDialog alert = new Functions().funAlertShow(alertDialog);
        }else if(v.getId()==R.id.btnreqcancel){
            AlertDialog.Builder intru = new AlertDialog.Builder(getActivity());
            intru.setTitle("Request");
            intru.setMessage("You Want to Cancel Request ?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    new SetRequest(getResources().getString(R.string.req_Canceled), getReqid(),getActivity());
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }).setNegativeButton("No", null);
            AlertDialog alert = new Functions().funAlertShow(intru);
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
            request_route.TaskParser taskParser = new TaskParser();
            taskParser.execute(s);
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog= ProgressDialog.show(getContext(),"Route","Finding Route.....");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public int getReqid(){
        return reqid;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
