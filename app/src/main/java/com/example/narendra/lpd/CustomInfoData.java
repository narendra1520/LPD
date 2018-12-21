package com.example.narendra.lpd;

/**
 * Created by Narendra on 21-02-2018.
 */

public class CustomInfoData {
    public String name,contact,img;
    public float price,rate;

    public CustomInfoData(String s, String name, String contact, float price, float rate) {
        this.img = s;
        this.name = name;
        this.contact = contact;
        this.price = price;
        this.rate = rate;
    }
}
