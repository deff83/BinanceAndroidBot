package com.example.myapplication.customObjects;



import com.binance.api.client.domain.market.TickerPrice;
import com.example.myapplication.binance.BinanceState;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Preobr {

    private static final Preobr ourInstance = new Preobr();

    public static Preobr getInstance() {
        return ourInstance;
    }

    private Preobr() { }

    private List<TickerPrice> ticcker = null;

    public String getDataMyFormat(long timestamp){
        Date date = new Date(timestamp);
        SimpleDateFormat formatForDateNow = new SimpleDateFormat("E yyyy.MM.dd ', время' hh:mm:ss a zzz");
        return formatForDateNow.format(date);
    };

    public List<TickerPrice> getTiccker() {
        if (ticcker == null) return BinanceState.getInstance().getTikPrice();
        return ticcker;
    }

    public void setTiccker(List<TickerPrice> ticcker) {
        this.ticcker = ticcker;
    }
}
