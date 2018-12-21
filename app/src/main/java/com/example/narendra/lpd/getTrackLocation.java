package com.example.narendra.lpd;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Narendra on 21-01-2018.
 */

public class getTrackLocation extends BroadcastReceiver{

    private double[] latlong;
    int pack_id;

    @Override
    public void onReceive(final Context context, final Intent intent) {
        double latlong[]=new double[2];
        String str=null;

        pack_id=intent.getIntExtra("pack_id",0);

        webservices wb=new webservices();
        JSONObject jsobj=new JSONObject();
        try {
            jsobj.put("pack_id",pack_id);
            JSONObject outObj = wb.webservices("getUpLocation", jsobj);
            JSONArray resjarr = outObj.getJSONArray("response");
            JSONObject getjobj = resjarr.getJSONObject(0);
            int code = getjobj.getInt("code");
            str= getjobj.getString("msg");
            if (code==1){
                JSONObject datajobj=resjarr.getJSONObject(1);
                JSONArray datajarr=datajobj.getJSONArray("d1");
                JSONObject outjobj1= datajarr.getJSONObject(0);
                latlong[0]= outjobj1.getDouble("lat");
                latlong[1]= outjobj1.getDouble("long");
            }else {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Toast.makeText(context,str, Toast.LENGTH_SHORT).show();

        /*Intent updateloc=new Intent();
        updateloc.setAction("Update_Loc");
        updateloc.putExtra("latlong",latlong);
        updateloc.putExtra("str",str);
        context.sendBroadcast(updateloc);*/
    }

}
