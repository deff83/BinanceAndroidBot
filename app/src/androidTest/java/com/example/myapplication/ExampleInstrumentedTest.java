package com.example.myapplication;

import android.content.Context;

import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;
import com.example.myapplication.binance.APIBinance;
import com.example.myapplication.bot.BotFunction;

import org.junit.Test;
import org.junit.runner.RunWith;

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


}
