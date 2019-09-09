package com.example.myapplication.binance;

import com.binance.api.client.domain.account.Order;

public class BinanceRequest {
    private static final BinanceRequest ourInstance = new BinanceRequest();

    public static BinanceRequest getInstance() {
        return ourInstance;
    }

    private BinanceRequest() {
    }

    public void cancelOrder(Order order){

    }
}
