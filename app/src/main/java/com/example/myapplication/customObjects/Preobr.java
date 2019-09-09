package com.example.myapplication.customObjects;



import java.text.SimpleDateFormat;
import java.util.Date;

public class Preobr {
    public Preobr() {

    }
    public static String getDataMyFormat(long timestamp){
        Date date = new Date(timestamp);
        SimpleDateFormat formatForDateNow = new SimpleDateFormat("E yyyy.MM.dd ', время' hh:mm:ss a zzz");
        return formatForDateNow.format(date);
    };
}
