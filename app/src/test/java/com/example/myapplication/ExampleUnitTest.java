package com.example.myapplication;

import android.util.Log;

import com.binance.api.client.BinanceApiCallback;
import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.domain.ExecutionType;
import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.OrderStatus;
import com.binance.api.client.domain.TimeInForce;
import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.account.NewOrderResponse;
import com.binance.api.client.domain.account.Order;
import com.binance.api.client.domain.account.request.OrderStatusRequest;
import com.binance.api.client.domain.event.AccountUpdateEvent;
import com.binance.api.client.domain.event.AggTradeEvent;
import com.binance.api.client.domain.event.OrderTradeUpdateEvent;
import com.binance.api.client.domain.event.UserDataUpdateEvent;
import com.binance.api.client.domain.general.ExchangeInfo;
import com.binance.api.client.domain.general.FilterType;
import com.binance.api.client.domain.general.SymbolFilter;
import com.binance.api.client.domain.general.SymbolInfo;
import com.binance.api.client.domain.market.AggTrade;
import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;
import com.binance.api.client.domain.market.OrderBook;
import com.binance.api.client.domain.market.OrderBookEntry;
import com.binance.api.client.domain.market.TickerStatistics;
import com.example.myapplication.binance.APIBinance;
import com.example.myapplication.binance.BinanceState;
import com.example.myapplication.bot.ActionBot_CancelOrder;
import com.example.myapplication.bot.AddOrder;
import com.example.myapplication.bot.BotFunction;

import org.junit.Test;

import java.io.Closeable;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    private BinanceApiRestClient client;
    private APIBinance apiBinance;
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public  void cancelOrder(){
        apiBinance = APIBinance.getInstance();
        client = apiBinance.getClient();
        //OrderStatus
//                NEW, стоит заявка
//                PARTIALLY_FILLED, частично исполнена
//                FILLED, исполнена заявка
//                CANCELED, отменена
//                PENDING_CANCEL, ожидание отмены
//                REJECTED, отклонена
//                EXPIRED, ИСТEКШИЙ
        OrderStatusRequest orderStatusRequest = new OrderStatusRequest("COCOSUSDT", 387371l);
        orderStatusRequest.recvWindow(5000l);
        orderStatusRequest.timestamp(client.getServerTime());
        Order order = client.getOrderStatus(orderStatusRequest);
        System.out.println("APICancelOrder"+order);
    }

    @Test
    public  void paramSymbol(){
        apiBinance = APIBinance.getInstance();
        client = apiBinance.getClient();
        //SymbolFilter
        //PRICE_FILTER
        //PERCENT_PRICE
        //LOT_SIZE
        //MIN_NOTIONAL
        //ICEBERG_PARTS
        //MARKET_LOT_SIZE
        //MAX_NUM_ALGO_ORDERS
        try {

            ExchangeInfo exchangeInfo = client.getExchangeInfo();
            SymbolInfo symbolInfo = exchangeInfo.getSymbolInfo("BCPTBTC");
            List<SymbolFilter> listFilters = symbolInfo.getFilters();
            for(SymbolFilter symbolFilter: listFilters){
                System.out.println(symbolFilter.getFilterType());
                //if (symbolFilter.getFilterType() == FilterType.PRICE_FILTER){
                    System.out.println("getTickSize "+symbolFilter.getTickSize());
                    System.out.println("getLimit "+symbolFilter.getLimit());
                    System.out.println("getMaxPrice "+symbolFilter.getMaxPrice());
                    System.out.println("getMaxQty "+symbolFilter.getMaxQty());
                    System.out.println("getMinNotional "+symbolFilter.getMinNotional());
                    System.out.println("getMinPrice "+symbolFilter.getMinPrice());
                    System.out.println("getMinQty "+symbolFilter.getMinQty());
                    System.out.println("getStepSize "+symbolFilter.getStepSize());
                    System.out.println("getMaxNumAlgoOrders "+symbolFilter.getMaxNumAlgoOrders());

                //}
            }

        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        /*SymbolInfo symbolInfo = exchangeInfo.getSymbolInfo("ETHBTC");
        System.out.println(symbolInfo.getStatus());
        SymbolFilter priceFilter = symbolInfo.getSymbolFilter(FilterType.PRICE_FILTER);
        System.out.println(priceFilter.getMinPrice());
        System.out.println(priceFilter.getTickSize());*/

    }
    @Test
    public void setOrder(){
        apiBinance = APIBinance.getInstance();
        client = apiBinance.getClient();

        AddOrder addorder = new AddOrder(2.83E-4, 290000, "SELL", "WINUSDC");
        System.out.println(String.format(Locale.ROOT,"%.8f", addorder.getPrice()));
        addorder.setPriceStr(String.format(Locale.ROOT,"%.8f", addorder.getPrice()));
        NewOrder newOrderAdd = NewOrder.limitSell(addorder.getPara(), TimeInForce.GTC, addorder.getValue()+"", addorder.getPriceStr()+"");
        if(addorder.getTypeOrder().equals("BUY")) newOrderAdd = NewOrder.limitBuy(addorder.getPara(), TimeInForce.GTC, addorder.getValue()+"", addorder.getPriceStr()+""); ;

        newOrderAdd.recvWindow(5000l);
        newOrderAdd.timestamp(client.getServerTime());
        try {
            NewOrderResponse newOrderResponse = client.newOrder(newOrderAdd);
            System.out.println("newOrderResponse LIMIT: "+newOrderResponse.getTransactTime()+" "+newOrderResponse.getOrderId());
            //errorActiom("newOrder LIMIT: "+newOrderResponse.getSymbol()+" "+newOrderResponse.getPrice()+" "+newOrderResponse.getOrigQty());

        }catch(Exception e){
            System.out.println("newOrderResponse LIMIT Error:"+e.getMessage());
            //errorActiom("newOrderResponse LIMIT Error:"+e.getMessage());
        }
    }

    @Test
    public void getCandle(){
        apiBinance = APIBinance.getInstance();
        client = apiBinance.getClient();
        //List<Candlestick> candlesticks = client.getCandlestickBars("WPRBTC", CandlestickInterval.WEEKLY);
        List<Candlestick> candlesticks = null;


        System.out.println(candlesticks);
    }

    @Test
    public void testHMacSha256(){
        apiBinance = APIBinance.getInstance();

        try{
            System.out.println(apiBinance.gettHmacSHA256Pri(""));
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Test
    public void testGetStatuse(){
        try{
            apiBinance = APIBinance.getInstance();
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
                System.out.println(response.body().string());
            }
        }catch (Exception e){e.printStackTrace();}

    }

    @Test
    public void testSocket(){
        BinanceApiWebSocketClient client = BinanceApiClientFactory.newInstance().newWebSocketClient();
        String symbol = "BTCUSDT";
        System.out.println("start");
        Closeable ws = client.onAggTradeEvent(symbol.toLowerCase(), new BinanceApiCallback<AggTradeEvent>() {
            @Override
            public void onResponse(final AggTradeEvent response) {
                System.out.println(response);
            }

            @Override
            public void onFailure(final Throwable cause) {
                System.err.println("Web socket failed");
                cause.printStackTrace(System.err);
            }
        });

        // some time later...
        try {
            Thread.sleep(60*1000);
            ws.close();

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void testAccountStream() {
        BinanceApiWebSocketClient clientn = BinanceApiClientFactory.newInstance().newWebSocketClient();
        String listenKey = APIBinance.getInstance().get_listenKey();
        //Log.d("testAccautEvent", listenKey);

        clientn.onUserDataUpdateEvent(listenKey, response -> {
            try {

                if (response.getEventType() == UserDataUpdateEvent.UserDataUpdateEventType.ACCOUNT_UPDATE) {
                    AccountUpdateEvent accountUpdateEvent = response.getAccountUpdateEvent();

                    // Print new balances of every available asset

                    System.out.println("testAccautEvent - testAccautUpdate:" + accountUpdateEvent.getBalances());
                } else {
                    OrderTradeUpdateEvent orderTradeUpdateEvent = response.getOrderTradeUpdateEvent();
                    if (orderTradeUpdateEvent != null) {
                        // Print details about an order/trade

                        BinanceState.getInstance().setOrderTradeUpdateEvent(orderTradeUpdateEvent);
                        ObservableSave.getObs().update(11);
                        if (orderTradeUpdateEvent.getExecutionType() == ExecutionType.TRADE) {
                            String title_notif = "Купили " + orderTradeUpdateEvent.getSymbol();
                            double doubleQty = Double.parseDouble(orderTradeUpdateEvent.getOriginalQuantity()) * Double.parseDouble(orderTradeUpdateEvent.getPrice());
                            String text_notify = "Price:" + orderTradeUpdateEvent.getPrice() + " Qty:" + orderTradeUpdateEvent.getOriginalQuantity() + " (" + doubleQty + ")";
                            if (orderTradeUpdateEvent.getSide() == OrderSide.BUY) {
                                title_notif = "Продали " + orderTradeUpdateEvent.getSymbol();
                            }
                            System.out.println(title_notif + ":" + text_notify);
                            //ObservableSave.getObs().doNotify(-1, R.mipmap.ic_btc_icon, title_notif, text_notify, getApplicationContext());
                        } else {
                            String title_notif2 = "Инфо " + orderTradeUpdateEvent.getSymbol();
                            String text_notify2 = "Price:" + orderTradeUpdateEvent.getPrice() + " Qty:" + orderTradeUpdateEvent.getOriginalQuantity();
                            System.out.println(title_notif2 + ":" + text_notify2);
                            //ObservableSave.getObs().doNotify(-1, R.mipmap.ic_btc_icon, title_notif2, text_notify2, getApplicationContext());

                        }

                    }
                }
            } catch (Exception e) {
                System.out.println("Error" + ":" + e.getMessage());
                //ObservableSave.getObs().doNotify(-1, R.mipmap.ic_xmr_icon, "Error", e.getMessage()+"", getApplicationContext());

                e.printStackTrace();
            }
        });
        try {
            Thread.sleep(60 * 1000);
            APIBinance.getInstance().keepAliveUserDataStream(listenKey.substring(0, listenKey.length() - 2) + "jk");
            Thread.sleep(60 * 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}