package com.example.narendra.lpd;


import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Narendra on 25-12-2017.
 */

public class request_data {

        private Double srclat,srclong,deslat,deslong;
        private String sender,receiver,date,status,time,maincat,subcat;
        private Float cost,am;
        private String srcaddr,desaddr,cont;
        private int packid,reqid;

    public request_data(Context mConx, Double srclat, Double srclong, Double deslat, Double deslong,
                        String sender, String receiver, String date, String status, Float cost, int
                                packid, int reqid, String cont, String time, String maincat, String subcat,Float am) {
            this.srclat = srclat;
            this.srclong = srclong;
            this.deslat = deslat;
            this.deslong = deslong;
            this.sender = sender;
            this.receiver = receiver;
            this.date = date;
            this.status = status;
            this.cost = cost;
            this.srclat = srclat;
            this.srclong = srclong;
            this.deslat = deslat;
            this.deslong = deslong;
            this.packid=packid;
            this.reqid=reqid;
            this.cont=cont;
            this.time=time;
            this.maincat = maincat;
            this.subcat = subcat;
            this.am = am;


            Geocoder geocoder;
            geocoder=new Geocoder(mConx,Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(srclat, srclong, 1);
                List<Address> addresses2 = geocoder.getFromLocation(deslat, deslong, 1);
                this.srcaddr= addresses.get(0).getAddressLine(0);
                this.desaddr= addresses2.get(0).getAddressLine(0);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        public String getSrcaddr() {
            return srcaddr;
        }

        public String getDesaddr() {
            return desaddr;
        }

        public String getSender() {
            return sender;
        }

        public String getReceiver() {
            return receiver;
        }

        public String getDate() {
            return date;
        }

        public String getStatus() {
            return status;
        }

        public Float getCost() {
            return cost;
        }

        public Double getSrclat(){return srclat;}
        public Double getSrclong(){return srclong;}
        public Double getDeslat(){return deslat;}
        public Double getDeslong(){return deslong;}
    public String getCont() {
        return cont;
    }

    public String getTime() {
        return time;
    }

    public int getPackid(){return packid;}
    public int getReqid() {return reqid;}

    public String getMaincat() {
        return maincat;
    }

    public String getSubcat() {
        return subcat;
    }

    public Float getAm() {
        return am;
    }
}
