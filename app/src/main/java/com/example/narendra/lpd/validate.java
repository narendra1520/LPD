package com.example.narendra.lpd;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Narendra on 25-10-2017.
 */

public class validate {

    boolean valiPwd(String s) {
        if(!s.isEmpty() && s.length()>=8) {
            return  true;
        }
        else{
            return false;
        }
    }

    boolean valiEmail(String s) {
        if(s.equals("")){
            return false;
        }else {
        String pattern="^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"+"[A-za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pt=Pattern.compile(pattern);
        Matcher mt=pt.matcher(s);
        return mt.matches();}
    }

    boolean valipin(String s) {
        if(s.equals("")){
            return false;
        }else {
            String pattern = "^[0-9]{6}$";
            Pattern pt = Pattern.compile(pattern);
            Matcher mt = pt.matcher(s);
            return mt.matches();
        }
    }

    boolean valiContact(String s) {
        if(s.equals("")){
            return false;
        }else {
            String pattern = "^[6-9]\\d{9}$";
            Pattern pt = Pattern.compile(pattern);
            Matcher mt = pt.matcher(s);
            return mt.matches();
        }
    }

    boolean valiName(String s) {
        if(s.equals("")){
            return false;
        }else {
            String pattern = "^[a-zA-Z\\s]*$";
            Pattern pt = Pattern.compile(pattern);
            Matcher mt = pt.matcher(s);
            return mt.matches();
        }
    }

    boolean valiLic(String s) {
        if(s.equals("")){
            return false;
        }else {
            String pattern = "^[a-zA-Z]{2}[0-9]{13}$";
            Pattern pt = Pattern.compile(pattern);
            Matcher mt = pt.matcher(s);
            return mt.matches();
        }
    }

    boolean valiMinprc(String s) {
        if(s.equals("")){
            return false;
        }else {
            String pattern = "^[0-9]{1,3}([.][0-9]{1,2})?$";
            Pattern pt = Pattern.compile(pattern);
            Matcher mt = pt.matcher(s);
            return mt.matches();
        }
    }

    boolean valiBirth(String s) {
        SimpleDateFormat df=new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date date = null;
        Date cdate=new Date();
        try {
            date=df.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
            if(cdate.before(date)) {
                return false;
            } else {
                return true;
            }
    }

}