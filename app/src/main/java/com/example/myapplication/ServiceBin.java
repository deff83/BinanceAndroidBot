package com.example.myapplication;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.constant.BinanceApiConstants;
import com.binance.api.client.domain.ExecutionType;
import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.account.Account;
import com.binance.api.client.domain.account.AssetBalance;
import com.binance.api.client.domain.account.Order;
import com.binance.api.client.domain.account.request.OrderRequest;
import com.binance.api.client.domain.event.AccountUpdateEvent;
import com.binance.api.client.domain.event.OrderTradeUpdateEvent;
import com.binance.api.client.domain.event.UserDataUpdateEvent;
import com.binance.api.client.domain.general.ExchangeInfo;
import com.binance.api.client.domain.general.SymbolInfo;
import com.binance.api.client.domain.market.OrderBook;
import com.binance.api.client.domain.market.TickerPrice;
import com.example.myapplication.binance.APIBinance;
import com.example.myapplication.binance.BinanceState;
import com.example.myapplication.bot.ActionBotInterface;
import com.example.myapplication.bot.ActionBot_CancelOrder;
import com.example.myapplication.bot.BotFunction;
import com.example.myapplication.bot.Keeper;
import com.example.myapplication.bot.Performer;
import com.example.myapplication.botOpiration.BotOpiration;
import com.example.myapplication.customObjects.SaverInstruct;

import java.security.spec.ECField;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ServiceBin extends Service {
    BinanceState binanceState;
    APIBinance apibinance;
    String listenKey;
    private ScheduledExecutorService executor, executor2, executor3, executor4, executor5, executor6, executor7, executor8, executor9, executor10;
    //Таймер запросов
    private static Timer timer = new Timer();

    public ServiceBin() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        executor = Executors.newScheduledThreadPool(1); //OrderBook
        executor2 = Executors.newScheduledThreadPool(1);    //Account
        executor3 = Executors.newScheduledThreadPool(1);    //List<TickerPrice> getAllPrices
        executor4 = Executors.newScheduledThreadPool(1);    //MyOrders getOpenOrders
        executor5 = Executors.newScheduledThreadPool(1);    //SHOW Bot list
        executor6 = Executors.newScheduledThreadPool(1);    //Perfom Bot list
        executor7 = Executors.newScheduledThreadPool(1);    //Bot if
        executor8 = Executors.newScheduledThreadPool(1);    //Candles
        executor9 = Executors.newScheduledThreadPool(1);    //EventOrder
        executor10 = Executors.newScheduledThreadPool(1);    //Network

        binanceState = BinanceState.getInstance();
        apibinance = APIBinance.getInstance();
        Toast.makeText(this, "Create", Toast.LENGTH_SHORT).show();
        List<ActionBotInterface> listSaverInstruct = SaverInstruct.getInstance().loadArrayList("listKeeper", this);
        if (listSaverInstruct == null) listSaverInstruct = new ArrayList<>();
        System.out.println("LOADINGSaverInstruct"+ listSaverInstruct);

        Keeper.getInstance().setListActionBot(listSaverInstruct);


        NetworkStateReceiver mNetSateReceiver = new NetworkStateReceiver();
        registerReceiver( mNetSateReceiver, new IntentFilter(
                ConnectivityManager.CONNECTIVITY_ACTION ) );


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "onstart", Toast.LENGTH_SHORT).show();

        Runnable runn = new Runnable() {
            @Override
            public void run() {
                try {
                    BinanceState binState = BinanceState.getInstance();
                    OrderBook orderbook = apibinance.getClient().getOrderBook(binState.getTekPara(), binState.getTekCount());

                    //Toast.makeText(ServiceBin.this, "test", Toast.LENGTH_SHORT).show();
                    binanceState.setPriceBuy(orderbook.getBids());
                    binanceState.setPriceSell(orderbook.getAsks());
                    ObservableSave.getObs().update(1);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        };
        Runnable runnTikPr = new Runnable() {
            @Override
            public void run() {
                try {
                    List<TickerPrice> tikPrice = apibinance.getClient().getAllPrices();
                    System.out.println(tikPrice);

                    binanceState.setTikPrice(tikPrice);
                    ObservableSave.getObs().update(5);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        };
        Runnable runnAcc = new Runnable() {
            @Override
            public void run() {
                try {
                    //Account account = apibinance.getClient().getAccount();
                    System.out.println(BinanceApiConstants.DEFAULT_RECEIVING_WINDOW);
                    System.out.println(apibinance.getClient().getServerTime());
                    System.out.println(System.currentTimeMillis());
                    if (BinanceState.getInstance().isMyBalance()) {
                        Account account = apibinance.getClient().getAccount(5000l, apibinance.getClient().getServerTime());
                        binanceState.setAccount(account);
                        ObservableSave.getObs().update(2);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        };
        Runnable runnMyOrder = new Runnable() {
            @Override
            public void run() {
                try {

                    System.out.println(BinanceApiConstants.DEFAULT_RECEIVING_WINDOW);
                    System.out.println(apibinance.getClient().getServerTime());
                    System.out.println(System.currentTimeMillis());
                    OrderRequest orderRequest =  new OrderRequest(BinanceState.getInstance().getTekPara());
                    orderRequest.recvWindow(5000l);
                    orderRequest.timestamp(apibinance.getClient().getServerTime());
                    List<Order> myOrdersTek = apibinance.getClient().getOpenOrders(orderRequest);

                    binanceState.setMyOrdersTek(myOrdersTek);
                    System.out.println("myOrdersTek: "+myOrdersTek);
                    ObservableSave.getObs().update(3);  //myOrder
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        };
        Runnable runnListBot = new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("lisBot:"+Keeper.getInstance().getListActionBot());
                    ObservableSave.getObs().update(4);  //ListBot
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        };
        Runnable runnPerfomListBot = new Runnable() {
            @Override
            public void run() {
                try {
                    Performer.getInstance().startPerformer(getApplicationContext());
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        };
        Runnable runnPerfomBot = new Runnable() {
            @Override
            public void run() {
                try {
                    BotOpiration.getInstance().start();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        };
        Runnable runnCandles = new Runnable() {
            @Override
            public void run() {
                try {

                    if (BinanceState.getInstance().isCandles()) {
                        new BotFunction(getApplicationContext()).getCandlestickBars();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        };
        Runnable runnableExchengInfo = new Runnable() {
            @Override
            public void run() {
                try{
                    while(true) {
                        ExchangeInfo excinfo = null;
                        try {
                            excinfo = apibinance.getClient().getExchangeInfo();
                            BinanceState.getInstance().setExchangeInfo(excinfo);
                        }catch(Exception e){

                        }
                        if(excinfo != null) break;
                    }
                }catch(Exception e){

                }
            }
        };
        Runnable runnEventOrder = new Runnable() {
            @Override
            public void run() {
                try {
                    testAccautEvent();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        };
        Runnable runnNetwork = new Runnable() {
            @Override
            public void run() {
                try {
                    if(ObservableSave.getObs().isNetwork()){
                        testAccautEvent();
                        ObservableSave.getObs().setNetwork(false);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        };
        //Thread threadEventOrder = new Thread(runnEventOrder);
        //threadEventOrder.start();

        new Thread(runnableExchengInfo).start();

        executor.scheduleWithFixedDelay(runn, 0, 5, TimeUnit.SECONDS);
        executor2.scheduleWithFixedDelay(runnAcc, 0, 50, TimeUnit.SECONDS);
        executor3.scheduleWithFixedDelay(runnTikPr, 0, 61, TimeUnit.SECONDS);
        executor4.scheduleWithFixedDelay(runnMyOrder, 0, 10, TimeUnit.SECONDS);
        executor5.scheduleWithFixedDelay(runnListBot, 0, 5, TimeUnit.SECONDS);
        executor6.scheduleWithFixedDelay(runnPerfomListBot, 0, 5, TimeUnit.SECONDS);
        executor7.scheduleWithFixedDelay(runnPerfomBot, 0, 2, TimeUnit.SECONDS);
        executor8.scheduleWithFixedDelay(runnCandles, 0, 5, TimeUnit.SECONDS);
        executor9.scheduleWithFixedDelay(runnEventOrder, 0, 60*30, TimeUnit.SECONDS);
        executor10.scheduleWithFixedDelay(runnNetwork, 0, 5, TimeUnit.SECONDS);

        return Service.START_STICKY;
    }


    public void testAccautEvent(){
        try{
            APIBinance.getInstance().keepAliveUserDataStream(listenKey);
        }catch(Exception ex) {
            BinanceApiWebSocketClient clientn = BinanceApiClientFactory.newInstance().newWebSocketClient();
            listenKey = APIBinance.getInstance().get_listenKey();
            Log.d("testAccautEvent", listenKey);

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
                                ObservableSave.getObs().doNotify(-1, R.mipmap.ic_btc_icon, title_notif, text_notify, getApplicationContext());
                                ObservableSave.getObs().opovVibro(getApplicationContext());
                            } else {
                                String title_notif2 = "Инфо "+orderTradeUpdateEvent.getExecutionType()+" " + orderTradeUpdateEvent.getSymbol();
                                String text_notify2 = "Price:" + orderTradeUpdateEvent.getPrice() + " Qty:" + orderTradeUpdateEvent.getOriginalQuantity();
                                ObservableSave.getObs().doNotify(-1, R.mipmap.ic_btc_icon, title_notif2, text_notify2, getApplicationContext());

                            }

                        }
                    }
                } catch (Exception e) {
                    ObservableSave.getObs().doNotify(-1, R.mipmap.ic_xmr_icon, "Error", e.getMessage() + "", getApplicationContext());

                    e.printStackTrace();
                }
            });
        }
    }










    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
