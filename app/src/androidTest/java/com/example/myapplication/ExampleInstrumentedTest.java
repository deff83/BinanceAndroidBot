package com.example.myapplication;

import android.content.Context;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import com.binance.api.client.BinanceApiCallback;
import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.domain.event.AccountUpdateEvent;
import com.binance.api.client.domain.event.AggTradeEvent;
import com.binance.api.client.domain.event.OrderTradeUpdateEvent;
import com.binance.api.client.domain.event.UserDataUpdateEvent;
import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;
import com.example.myapplication.binance.APIBinance;
import com.example.myapplication.bot.BotFunction;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.Closeable;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    private BinanceApiRestClient client;
    private APIBinance apiBinance;
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.example.myapplication", appContext.getPackageName());
    }
    @Test
    public void getCandle(){

        apiBinance = APIBinance.getInstance();
        client = apiBinance.getClient();
        List<Candlestick> candlesticks = client.getCandlestickBars("WPRBTC", CandlestickInterval.WEEKLY);
        System.out.println(candlesticks);
    }

    @Test
    public void testSocket(){
    BinanceApiWebSocketClient clientn = BinanceApiClientFactory.newInstance().newWebSocketClient();

    String symbol = "BTCUSDT";
    Log.d("testSocket","start");
    Closeable ws = clientn.onAggTradeEvent(symbol.toLowerCase(), new BinanceApiCallback<AggTradeEvent>() {
        @Override
        public void onResponse(final AggTradeEvent response) {
            System.out.println("responseTest"+response);
        }

        @Override
        public void onFailure(final Throwable cause) {
            System.err.println("Web socket failed");
            cause.printStackTrace(System.err);
        }
    });

    // some time later...
        try {
        Thread.sleep(5*60*1000);


        ws.close();

        }catch(Exception e){
            e.printStackTrace();
        }
    }
    @Test
    public void testAccautEvent(){
        apiBinance = APIBinance.getInstance();
        client = apiBinance.getClient();
        BinanceApiWebSocketClient clientn = BinanceApiClientFactory.newInstance().newWebSocketClient();
        String listenKey = client.startUserDataStream();
        Log.d("testAccautEvent", listenKey);
        clientn.onUserDataUpdateEvent(listenKey, response -> {
            if (response.getEventType() == UserDataUpdateEvent.UserDataUpdateEventType.ACCOUNT_UPDATE) {
                AccountUpdateEvent accountUpdateEvent = response.getAccountUpdateEvent();

                // Print new balances of every available asset
                System.out.println("testAccautEvent - testAccautUpdate:"+accountUpdateEvent.getBalances());
            } else {
                OrderTradeUpdateEvent orderTradeUpdateEvent = response.getOrderTradeUpdateEvent();
                if (orderTradeUpdateEvent != null) {
                    // Print details about an order/trade
                    System.out.println("testAccautEvent1:" + orderTradeUpdateEvent);

                    // Print original quantity
                    System.out.println("testAccautEvent2:" + orderTradeUpdateEvent.getOriginalQuantity());

                    // Or price
                    System.out.println("testAccautEvent3:" + orderTradeUpdateEvent.getPrice());
                }
            }
        });
        try {
            System.out.println("testAccautEvent7:");
            Thread.sleep(60*60*1000);

            System.out.println("testAccautEvent8:");
            clientn.close();

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void testVibro(){
        Context appContext = InstrumentationRegistry.getTargetContext();
        long[] pattern = { 0, 300, 400, 200, 100, 200 , 100, 200 };
        Vibrator vibrator = (Vibrator) appContext.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(pattern, -1);
        }

        try {
            System.out.println("waiting:");
            Thread.sleep(60*1000);




        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
