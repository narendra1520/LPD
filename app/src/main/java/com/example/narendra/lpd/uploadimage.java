package com.example.narendra.lpd;

import android.content.Context;
import android.net.Uri;
import android.os.StrictMode;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Narendra on 25-10-2017.
 */

public class uploadimage {
    JSONObject jsonObject;
    String boundary = "*****";
    String lineEnd = "\r\n";
    String twohyp = "--";
    int bytesAvailable, bytesRead, buffeSize;
    byte[] buffer;
    int maxBufferSize = 1 * 1024 * 1024;
    HttpURLConnection httpurl;

    public JSONObject uploadingimage(Context context, Uri input, int name) throws Exception {

        StrictMode.ThreadPolicy pol=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(pol);

        URL strcon= new URL("https://narendra1520.000webhostapp.com/all/image.php?login_id="+name);
       // URL strcon= new URL("https://narendra1520.cu.ma/all/image.php?login_id="+name);
        InputStream stream = context.getContentResolver().openInputStream(input);

        httpurl = (HttpURLConnection)strcon.openConnection();
        httpurl.setRequestMethod("POST");
        httpurl.setRequestProperty("Accept", "application/json");
        httpurl.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
        httpurl.setRequestProperty("ENCTYPE", "multipart/form-data");
        httpurl.setRequestProperty("Connection", "Keep-Alive");
        httpurl.setDoOutput(true);
        httpurl.setDoInput(true);
        httpurl.connect();

        String reanme=name+".png";
        DataOutputStream dataOutputStream = new DataOutputStream(httpurl.getOutputStream());
        dataOutputStream.writeBytes(twohyp + boundary + lineEnd);
        dataOutputStream.writeBytes("Content-Disposition:form-data;name=\"image\";filename=\"" + reanme + "\"" + lineEnd);
        dataOutputStream.writeBytes(lineEnd);
        bytesAvailable = stream.available();
        buffeSize = Math.min(bytesAvailable, maxBufferSize);
        buffer = new byte[buffeSize];
        bytesRead = stream.read(buffer, 0, buffeSize);
        while (bytesRead > 0) {
            dataOutputStream.write(buffer,0,buffeSize);
            bytesAvailable = stream.available();
            buffeSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[buffeSize];

            bytesRead = stream.read(buffer, 0, buffeSize);
        }
        dataOutputStream.writeBytes(lineEnd);
        dataOutputStream.writeBytes(twohyp + boundary + twohyp + lineEnd);
        stream.close();

        dataOutputStream.flush();
        dataOutputStream.close();

        StringBuilder stringBuilder = new StringBuilder();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpurl.getInputStream()));
                String s;
                while ((s = bufferedReader.readLine()) != null) {
                    stringBuilder.append(s);
            }

        jsonObject = new JSONObject(stringBuilder.toString());
        return jsonObject;
    }
}
