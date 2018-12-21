package com.example.narendra.lpd;

import android.app.Activity;
import android.os.Handler;

import org.json.JSONObject;

/**
 * Created by Narendra on 13-01-2018.
 */

public class SetRequest {
    String status;
    int reqid;
    Activity activity;

    public SetRequest(String status, int reqid, Activity activity ) {
        this.status = status;
        this.reqid = reqid;
        this.activity=activity;
        setreq(status, reqid);
     //   OnSetRequest setreq= new OnSetRequest();
     //   setreq.execute(status, String.valueOf(reqid));
    }

    private void setreq(final String status, final int reqid){
        Handler handler=new Handler();
        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                webservices wb=new webservices();
                try {
                    JSONObject jsobj=new JSONObject();
                    jsobj.put("reqid",reqid);
                    jsobj.put("status",status);
                    wb.webservices("setRequestStatus",jsobj);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        handler.post(runnable);
    }

  /*  public class OnSetRequest extends AsyncTask<String,String,JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            webservices wb=new webservices();
            try {
                JSONObject jsobj=new JSONObject();
                jsobj.put("reqid",Integer.parseInt(params[1]));
                jsobj.put("status",params[0]);
                wb.webservices("setRequestStatus",jsobj);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return wb.jsonres;
        }
        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try {
                JSONArray resjarr = jsonObject.getJSONArray("response");
                JSONObject getjobj = resjarr.getJSONObject(0);
                int code = getjobj.getInt("code");
                String msg = getjobj.getString("msg");
                if (code==1){

                }else {
                    Toast.makeText(activity,msg, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }*/
}
