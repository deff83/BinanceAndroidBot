package com.example.myapplication.binance;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;

import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class APIBinance {
    private BinanceApiRestClient client;
    private String API_KEY = "91gOvraoYNNdG06sHeEyYbP02VHQn5AK3esuw9tqWYMcOL9PXFloyaKhIlPxu3ZoS";
    private String SECRET = "OQ4qlZXW2oGJDvTU0OWjMG4IiFu28ffrJ6bbzFOY2kOC480Vget3icjkhFgUV5MoG";

    private static final APIBinance ourInstance = new APIBinance();

    public static APIBinance getInstance() {
        return ourInstance;
    }

    private APIBinance() {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance(API_KEY, SECRET);
        client = factory.newRestClient();
    }

    public BinanceApiRestClient getClient() {return client;}


    public String gettHmacSHA256(String key, String text) throws NoSuchAlgorithmException, InvalidKeyException {
        Charset asciiiCs = Charset.forName("US-ASCII");
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(asciiiCs.encode(key).array(), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        byte[] mac_data = sha256_HMAC.doFinal(asciiiCs.encode(text).array());
        String result = "";
        for (byte element:mac_data){
            result += Integer.toString((element & 0xff) + 0x100, 16).substring(1);
        }
        return result;
    }
    public String gettHmacSHA256Pri(String textparam) throws NoSuchAlgorithmException, InvalidKeyException{
        return  gettHmacSHA256(SECRET, textparam);
    };

    public String getAPI_KEY() {
        return API_KEY;
    }
}
