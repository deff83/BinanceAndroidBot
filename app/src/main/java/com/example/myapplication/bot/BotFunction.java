package com.example.myapplication.bot;

import android.content.Context;
import android.widget.Toast;

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.account.Account;
import com.binance.api.client.domain.account.AssetBalance;
import com.binance.api.client.domain.account.Order;
import com.binance.api.client.domain.general.ExchangeInfo;
import com.binance.api.client.domain.general.FilterType;
import com.binance.api.client.domain.general.SymbolFilter;
import com.binance.api.client.domain.general.SymbolInfo;
import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;
import com.binance.api.client.domain.market.OrderBookEntry;
import com.binance.api.client.domain.market.TickerPrice;
import com.example.myapplication.ObservableSave;
import com.example.myapplication.Widjet;
import com.example.myapplication.binance.APIBinance;
import com.example.myapplication.binance.BinanceState;
import com.example.myapplication.binance.Pokazatel;
import com.example.myapplication.customObjects.Preobr;
import com.google.android.gms.common.api.Api;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimerTask;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BotFunction {
    private Context context;
    private int colPer = 1;
    public BotFunction(Context context) {
        this.context = context;
    }
    public void downOrder(Order order, List<OrderBookEntry> listOrderBookEntry){
        System.out.println("down");
        double price = Double.parseDouble(order.getPrice());
        for (int i = 0; i < listOrderBookEntry.size(); i++){
            OrderBookEntry orderentry =  listOrderBookEntry.get(i);
            double priceEntry = Double.parseDouble(orderentry.getPrice());

            if(order.getSide()== OrderSide.BUY){
                if(priceEntry<price){
                    if ((i+colPer-1)>listOrderBookEntry.size()) return;
                    OrderBookEntry orderentryGoto =  listOrderBookEntry.get(i+colPer-1);
                    ActionBot_CancelOrder orderAction = new ActionBot_CancelOrder(order);
                    Keeper.getInstance().addAction(orderAction, context);
                    System.out.println("testfff "+orderentryGoto.getPrice()+" getOrigQty:"+(order.getOrigQty())+" getIcebergQty:"+(order.getIcebergQty())+" getExecutedQty:"+(order.getExecutedQty()));
                    AddOrder newAddOrder = new AddOrder(Double.parseDouble(orderentryGoto.getPrice()+""), Double.parseDouble(order.getOrigQty()+""), order.getSide()+"", order.getSymbol());

                    ActionBot_LimitOrder orderAction2 = new ActionBot_LimitOrder(newAddOrder, 0);
                    Keeper.getInstance().addAction(orderAction2, context);
                    return;
                }
            }
            if(order.getSide()== OrderSide.SELL){
                //System.out.println(priceEntry);
                if(priceEntry>price){
                    if ((i-colPer-1)<0) return;
                    OrderBookEntry orderentryGoto =  listOrderBookEntry.get(i-colPer-1);
                    ActionBot_CancelOrder orderAction = new ActionBot_CancelOrder(order);
                    Keeper.getInstance().addAction(orderAction, context);
                    System.out.println("testfff "+orderentryGoto.getPrice()+" getOrigQty:"+(order.getOrigQty())+" getIcebergQty:"+(order.getIcebergQty())+" getExecutedQty:"+(order.getExecutedQty()));
                    AddOrder newAddOrder = new AddOrder(Double.parseDouble(orderentryGoto.getPrice()+""), Double.parseDouble(order.getOrigQty()+""), order.getSide()+"", order.getSymbol());

                    ActionBot_LimitOrder orderAction2 = new ActionBot_LimitOrder(newAddOrder, 0);
                    Keeper.getInstance().addAction(orderAction2, context);
                    return;
                }
            }
        }
    }
    public void upOrder(Order order, List<OrderBookEntry> listOrderBookEntry){
        System.out.println("down");
        double price = Double.parseDouble(order.getPrice());
        for (int i = 0; i < listOrderBookEntry.size(); i++){
            OrderBookEntry orderentry =  listOrderBookEntry.get(i);
            double priceEntry = Double.parseDouble(orderentry.getPrice());

            if(order.getSide()== OrderSide.BUY){
                if(priceEntry<price){
                    if ((i-colPer-1)<0) return;
                    OrderBookEntry orderentryGoto =  listOrderBookEntry.get(i-colPer-1);
                    ActionBot_CancelOrder orderAction = new ActionBot_CancelOrder(order);
                    Keeper.getInstance().addAction(orderAction, context);

                    if (order.getSide() == OrderSide.BUY){
                        //количество токенов на покупку пересчитать
                        order.setOrigQty(Math.floor(Double.parseDouble(order.getOrigQty())*Double.parseDouble(order.getPrice())/Double.parseDouble(orderentryGoto.getPrice()))+"");
                    }

                    System.out.println("testfff "+orderentryGoto.getPrice()+" getOrigQty:"+(order.getOrigQty())+" getIcebergQty:"+(order.getIcebergQty())+" getExecutedQty:"+(order.getExecutedQty()));
                    AddOrder newAddOrder = new AddOrder(Double.parseDouble(orderentryGoto.getPrice()+""), Double.parseDouble(order.getOrigQty()+""), order.getSide()+"", order.getSymbol());

                    ActionBot_LimitOrder orderAction2 = new ActionBot_LimitOrder(newAddOrder, 0);
                    Keeper.getInstance().addAction(orderAction2, context);
                    return;
                }
            }
            if(order.getSide()== OrderSide.SELL){
                //System.out.println(priceEntry);
                if(priceEntry>price){
                    if ((i+colPer-1)>listOrderBookEntry.size()) return;
                    OrderBookEntry orderentryGoto =  listOrderBookEntry.get(i+colPer);
                    ActionBot_CancelOrder orderAction = new ActionBot_CancelOrder(order);
                    Keeper.getInstance().addAction(orderAction, context);
                    System.out.println("testfff "+orderentryGoto.getPrice()+" getOrigQty:"+(order.getOrigQty())+" getIcebergQty:"+(order.getIcebergQty())+" getExecutedQty:"+(order.getExecutedQty()));
                    AddOrder newAddOrder = new AddOrder(Double.parseDouble(orderentryGoto.getPrice()+""), Double.parseDouble(order.getOrigQty()+""), order.getSide()+"", order.getSymbol());

                    ActionBot_LimitOrder orderAction2 = new ActionBot_LimitOrder(newAddOrder, 0);
                    Keeper.getInstance().addAction(orderAction2, context);
                    return;
                }
            }
        }
    }
    public void perestanovOrder(Order order, OrderBookEntry entrybook){
        ActionBot_CancelOrder orderAction = new ActionBot_CancelOrder(order);
        Keeper.getInstance().addAction(orderAction, context);

        if (order.getSide() == OrderSide.BUY){
            //количество токенов на покупку пересчитать
            order.setOrigQty(Math.floor(Double.parseDouble(order.getOrigQty())*Double.parseDouble(order.getPrice())/Double.parseDouble(entrybook.getPrice()))+"");
        }

        AddOrder newAddOrder = new AddOrder(Double.parseDouble(entrybook.getPrice()+""), Double.parseDouble(order.getOrigQty()+""), order.getSide()+"", order.getSymbol());
        ActionBot_LimitOrder orderAction2 = new ActionBot_LimitOrder(newAddOrder, 0);
        Keeper.getInstance().addAction(orderAction2, context);

    }



    public void perestanovOrderLuch(Order order, OrderBookEntry entrybook){
        double tick = getTick(order.getSymbol());
        String newPrice = String.format(Locale.ROOT,"%.8f", Double.parseDouble(entrybook.getPrice())+tick);
        if (order.getSide() == OrderSide.SELL) newPrice = String.format(Locale.ROOT,"%.8f", Double.parseDouble(entrybook.getPrice()) - tick);;

        entrybook.setPrice(newPrice);

        System.out.println("perestanovOrderLuch:"+entrybook.getPrice()+"");

        perestanovOrder(order, entrybook);
    }
    public void perestanovOrderPered(Order order){
        ActionGetLuchPrice actionGetLuchPrice = new ActionGetLuchPrice(order.getSymbol());
        Keeper.getInstance().addAction(actionGetLuchPrice, context);

        ActionBot_CancelOrder orderAction = new ActionBot_CancelOrder(order);
        Keeper.getInstance().addAction(orderAction, context);
        AddOrder newAddOrder = new AddOrder(Double.parseDouble(order.getPrice()+""), Double.parseDouble(order.getOrigQty()+""), order.getSide()+"", order.getSymbol());
        ActionBot_LimitOrder orderAction2 = new ActionBot_LimitOrder(newAddOrder, 1);
        Keeper.getInstance().addAction(orderAction2, context);

    }

    public int getColPer() {
        return colPer;
    }

    public static Double getTick(String para){
        ExchangeInfo exchangeInfo = BinanceState.getInstance().getExchangeInfo();
        if (exchangeInfo == null) return 0.0;
        // Obtain symbol information
        SymbolInfo symbolInfo = exchangeInfo.getSymbolInfo(para);
        SymbolFilter pricefilter = symbolInfo.getSymbolFilter(FilterType.PRICE_FILTER);
        double tick = Double.parseDouble(pricefilter.getTickSize());
        return tick;
    }

    public static Double getLotSize(String para){
        ExchangeInfo exchangeInfo = BinanceState.getInstance().getExchangeInfo();
        if (exchangeInfo == null) return 0.0;
        // Obtain symbol information
        SymbolInfo symbolInfo = exchangeInfo.getSymbolInfo(para);
        SymbolFilter pricefilter = symbolInfo.getSymbolFilter(FilterType.LOT_SIZE);

        double tick = Double.parseDouble(pricefilter.getStepSize());
        return tick;
    }

    public static Double getMinNational(String para){
        ExchangeInfo exchangeInfo = BinanceState.getInstance().getExchangeInfo();
        if (exchangeInfo == null) return 0.0;
        // Obtain symbol information
        SymbolInfo symbolInfo = exchangeInfo.getSymbolInfo(para);
        SymbolFilter pricefilter = symbolInfo.getSymbolFilter(FilterType.MIN_NOTIONAL);

        double tick = Double.parseDouble(pricefilter.getMinNotional());
        return tick;
    }

    public static Double getSize(String para){
        ExchangeInfo exchangeInfo = BinanceState.getInstance().getExchangeInfo();
        if (exchangeInfo == null) return 0.0;
        // Obtain symbol information
        SymbolInfo symbolInfo = exchangeInfo.getSymbolInfo(para);
        SymbolFilter pricefilter = symbolInfo.getSymbolFilter(FilterType.LOT_SIZE);

        double tick = Double.parseDouble(pricefilter.getStepSize());
        return tick;
    }




    public void setColPer(int colPer) {
        this.colPer = colPer;
    }

    public void changeFindPara(String findpara){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                System.out.println("changeFindPara:"+findpara);
                List<TickerPrice> listTickerPrice = new ArrayList<>();
                List<TickerPrice> listTickerPriceAll = BinanceState.getInstance().getTikPrice();
                if(findpara.equals("")){
                    listTickerPrice = listTickerPriceAll;
                    Preobr.getInstance().setTiccker(listTickerPrice);
                    ObservableSave.getObs().update(8);
                    return;
                }
                for (int i = 0; i<listTickerPriceAll.size(); i++){
                    TickerPrice tickItem = listTickerPriceAll.get(i);

                    String upperParaFind = findpara.toUpperCase();
                    String paratick = tickItem.getSymbol();
                    String parastr = paratick.substring(0, (upperParaFind.length()<=paratick.length())?upperParaFind.length():paratick.length());
                    if(upperParaFind.equals(parastr)) listTickerPrice.add(tickItem);
                }

                Preobr.getInstance().setTiccker(listTickerPrice);
                ObservableSave.getObs().update(8);

            }
        };
        new Thread(runnable).start();
    }

    public void getCandlestickBars(){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                System.out.println("candlesticks:"+BinanceState.getInstance().getTekPara());
                APIBinance apiBinance = APIBinance.getInstance();
                BinanceApiRestClient client = apiBinance.getClient();
                //try {
                    //List<Candlestick> candlesticks = client.getCandlestickBars("WPRBTC", CandlestickInterval.WEEKLY,  10, null, null);
                    //BinanceState.getInstance().setCandlesticks(candlesticks);
                //candlesticks.get(0).
                //System.out.println("getStatuse:"+getStatuse());
                List<Candlestick> candlesticks = getCndlestickList(BinanceState.getInstance().getTekPara(), BinanceState.getInstance().getIntervalCandle());
                BinanceState.getInstance().setCandlesticks(candlesticks);
                System.out.println("getCandlestr:"+candlesticks.size());

                    ObservableSave.getObs().update(9);
                //}catch (Exception e){System.out.println("candlesticks Error:"+e.getMessage());};
            }
        };
        new Thread(runnable).start();
    }

    public String getStatuse(){
        try{
            APIBinance apiBinance = APIBinance.getInstance();
            OkHttpClient clientOkkHttp = new OkHttpClient();
            long timestamp = new Date().getTime();
            timestamp = apiBinance.getClient().getServerTime();
            String param_for_sign = "timestamp="+timestamp;
            String signature = apiBinance.gettHmacSHA256Pri(param_for_sign);
            System.out.println(signature);
            String params = "?timestamp="+timestamp+"&signature="+signature;
            Request request = new Request.Builder()
                    .addHeader("X-MBX-APIKEY", APIBinance.getInstance().getAPI_KEY())
                    .url("https://api.binance.com/wapi/v3/apiTradingStatus.html"+params)
                    .build();
            try(Response response = clientOkkHttp.newCall(request).execute()){
                return response.body().string();
            }
        }catch (Exception e){e.printStackTrace();}
        return "";
    }

    public String getCandlestr(String symbol, CandlestickInterval intervalcandle, long startTime, long endTime, int limit){
        try{
            if(limit == 0) {
                String interval = intervalcandle.getIntervalId();
                APIBinance apiBinance = APIBinance.getInstance();
                OkHttpClient clientOkkHttp = new OkHttpClient();
                //long timestamp = new Date().getTime();
                //timestamp = apiBinance.getClient().getServerTime();
                String param_for_sign = "symbol=" + symbol + "interval=" + interval;
                String signature = apiBinance.gettHmacSHA256Pri(param_for_sign);
                String params = "?symbol=" + symbol + "&interval=" + interval;
                Request request = new Request.Builder()
                        .addHeader("X-MBX-APIKEY", APIBinance.getInstance().getAPI_KEY())
                        .url("https://api.binance.com/api/v1/klines" + params)
                        .build();
                try (Response response = clientOkkHttp.newCall(request).execute()) {
                    return response.body().string();
                }
            }
            if(limit > 0) {
                String interval = intervalcandle.getIntervalId();
                APIBinance apiBinance = APIBinance.getInstance();
                OkHttpClient clientOkkHttp = new OkHttpClient();
                //long timestamp = new Date().getTime();
                //timestamp = apiBinance.getClient().getServerTime();
                String param_for_sign = "symbol=" + symbol + "&interval=" + interval+"&startTime="+startTime+"&endTime="+endTime+"&limit="+limit;
                String signature = apiBinance.gettHmacSHA256Pri(param_for_sign);
                String params = "?symbol=" + symbol + "&interval=" + interval+"&startTime="+startTime+"&endTime="+endTime+"&limit="+limit;
                Request request = new Request.Builder()
                        .addHeader("X-MBX-APIKEY", APIBinance.getInstance().getAPI_KEY())
                        .url("https://api.binance.com/api/v1/klines" + params)
                        .build();
                try (Response response = clientOkkHttp.newCall(request).execute()) {
                    return response.body().string();
                }
            }
        }catch (Exception e){e.printStackTrace();}
        return "";
    }

    public List<Candlestick> getCndlestickList(String symbol, CandlestickInterval intervalcandle, long startTime, long endTime, int limit){
        List<Candlestick> listcandle = new ArrayList<>();

        String respocetext = getCandlestr(symbol, intervalcandle, startTime, endTime, limit);

        JsonElement elementCandleJson = new JsonParser().parse(respocetext);
        JsonArray listCandleJson = elementCandleJson.getAsJsonArray();
        for (int i = 0; i < listCandleJson.size(); i++) {
            JsonArray cndleItem = listCandleJson.get(i).getAsJsonArray();
            System.out.println("cndleItem"+cndleItem.get(0).toString());
            Candlestick stick = new Candlestick();
            stick.setOpenTime(cndleItem.get(0).getAsLong());
            stick.setOpen(cndleItem.get(1).getAsString());
            stick.setHigh(cndleItem.get(2).getAsString());
            stick.setLow(cndleItem.get(3).getAsString());
            stick.setClose(cndleItem.get(4).getAsString());
            stick.setVolume(cndleItem.get(5).getAsString());
            stick.setCloseTime(cndleItem.get(6).getAsLong());
            stick.setQuoteAssetVolume(cndleItem.get(7).getAsString());
            stick.setNumberOfTrades(cndleItem.get(8).getAsLong());
            stick.setTakerBuyBaseAssetVolume(cndleItem.get(9).getAsString());
            stick.setTakerBuyQuoteAssetVolume(cndleItem.get(10).getAsString());
            listcandle.add(stick);

        }


        return listcandle;
    };
    public List<Candlestick> getCndlestickList(String symbol, CandlestickInterval intervalcandle){
        List<Candlestick> listcandle = new ArrayList<>();

        String respocetext = getCandlestr(symbol, intervalcandle, 0, 0, 0);

        JsonElement elementCandleJson = new JsonParser().parse(respocetext);
        JsonArray listCandleJson = elementCandleJson.getAsJsonArray();
        for (int i = 0; i < listCandleJson.size(); i++) {
            JsonArray cndleItem = listCandleJson.get(i).getAsJsonArray();
            //System.out.println("cndleItem"+cndleItem.get(0).toString());
            Candlestick stick = new Candlestick();
            stick.setOpenTime(cndleItem.get(0).getAsLong());
            stick.setOpen(cndleItem.get(1).getAsString());
            stick.setHigh(cndleItem.get(2).getAsString());
            stick.setLow(cndleItem.get(3).getAsString());
            stick.setClose(cndleItem.get(4).getAsString());
            stick.setVolume(cndleItem.get(5).getAsString());
            stick.setCloseTime(cndleItem.get(6).getAsLong());
            stick.setQuoteAssetVolume(cndleItem.get(7).getAsString());
            stick.setNumberOfTrades(cndleItem.get(8).getAsLong());
            stick.setTakerBuyBaseAssetVolume(cndleItem.get(9).getAsString());
            stick.setTakerBuyQuoteAssetVolume(cndleItem.get(10).getAsString());
            listcandle.add(stick);

        }


        return listcandle;
    };

    public Double getMaxVolume(Double price, String para, String side, int percent){
        MaxVol maxVol = new MaxVol();
        if (side.equals("BUY")){
            //узнать количество валюты
            APIBinance apiBinance = APIBinance.getInstance();


            Account account = apiBinance.getClient().getAccount(5000l, apiBinance.getClient().getServerTime());
            AssetBalance assb = account.getAssetBalance(Pokazatel.getInstance().getOtPara(para));
            System.out.println("getMaxVolume:"+assb.getFree());
            Double tickLotsize = getLotSize(para);
            System.out.println("getMaxVolume:"+ tickLotsize);

            System.out.println("getMaxVolume:"+ Pokazatel.getInstance().getFromPara(para));
            Double setVol = Math.floor(((Double.parseDouble(assb.getFree())/price)*percent/100)/tickLotsize)*tickLotsize;




            maxVol.setMaxVol(setVol);
        }
        if (side.equals("SELL")){
            //узнать количество крипты
            APIBinance apiBinance = APIBinance.getInstance();


            Account account = apiBinance.getClient().getAccount(5000l, apiBinance.getClient().getServerTime());
            AssetBalance assb = account.getAssetBalance(Pokazatel.getInstance().getFromPara(para));
            System.out.println("getMaxVolume:"+assb.getFree());
            Double tickLotsize = getLotSize(para);
            System.out.println("getMaxVolume:"+ tickLotsize);

            System.out.println("getMaxVolume:"+ Pokazatel.getInstance().getFromPara(para));
            Double setVol = Math.floor((Double.parseDouble(assb.getFree())*percent/100)/tickLotsize)*tickLotsize;

            maxVol.setMaxVol(setVol);
        }

        if (percent==0){
            Double minNotional = getMinNational(para);
            double sizestep = getSize(para);
            System.out.println("getMaxVolume:size"+ sizestep);


            System.out.println("getMaxVolume:"+ Math.ceil((minNotional/price)/sizestep)*sizestep);
            maxVol.setMaxVol(Math.ceil((minNotional/price)/sizestep)*sizestep);
        }

        BinanceState.getInstance().setMaxVol(maxVol);
        ObservableSave.getObs().update(10);
        return 0.0;
    }

}
