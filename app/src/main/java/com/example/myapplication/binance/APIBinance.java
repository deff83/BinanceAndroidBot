package com.example.myapplication.binance;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;

public class APIBinance {
    private BinanceApiRestClient client;
    private String API_KEY = "91gOvraoYNNdG06sHeEyYbP02VHQn5AK3esuw9tqWYMcOL9PXFloyaKhIlPxu3ZS";
    private String SECRET = "OQ4qlZXW2oGJDvTU0OWjMG4IiFu28ffrJ6bbzFOY2kOC480Vget3icjkhFgUV5MG";

    private static final APIBinance ourInstance = new APIBinance();

    public static APIBinance getInstance() {
        return ourInstance;
    }

    private APIBinance() {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance(API_KEY, SECRET);
        client = factory.newRestClient();
    }

    public BinanceApiRestClient getClient() {return client;}


}
