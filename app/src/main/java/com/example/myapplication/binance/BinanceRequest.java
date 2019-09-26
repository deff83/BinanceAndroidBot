package com.example.myapplication.binance;

import com.binance.api.client.domain.account.Order;
import com.binance.api.client.domain.market.Candlestick;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;

public class BinanceRequest {
    private static final BinanceRequest ourInstance = new BinanceRequest();

    public static BinanceRequest getInstance() {
        return ourInstance;
    }

    private BinanceRequest() {
    }

    public void cancelOrder(Order order){

    }

    public List<Candlestick> getCndleticks(String para){
        List<Candlestick> lisyCandle = new ArrayList<>();

        OkHttpClient client = new OkHttpClient();


        return lisyCandle;

    }



}
