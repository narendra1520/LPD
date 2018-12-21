package com.example.narendra.lpd;

import android.os.StrictMode;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Narendra on 08-10-2017.
 */

public class webservices {

    JSONObject jsonres;
    String str = null;
    HttpURLConnection httpurl;

    public JSONObject webservices(String file,JSONObject jsonobj) throws Exception {

        StrictMode.ThreadPolicy pol=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(pol);

        URL strcon= new URL("https://narendra1520.000webhostapp.com/all/"+file+".php");
        //URL strcon= new URL("https://narendra1520.cu.ma/all/"+file+".php");
        httpurl=(HttpURLConnection)strcon.openConnection();
        httpurl.setRequestMethod("POST");
        httpurl.setRequestProperty("Content-Type","application/json");
        httpurl.setRequestProperty("accept","application/json");
        httpurl.setDoInput(true);
        httpurl.setDoOutput(true);
        httpurl.connect();

        DataOutputStream out = new DataOutputStream(httpurl.getOutputStream());

        out.write(jsonobj.toString().getBytes());

        BufferedReader in = new BufferedReader(new InputStreamReader(httpurl.getInputStream()));

        StringBuilder strbul = new StringBuilder();

        while ((str = in.readLine()) != null) {
            strbul.append(str);
        }
        jsonres = new JSONObject(strbul.toString());
        return jsonres;
    }
}
