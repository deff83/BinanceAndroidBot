package com.example.myapplication;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.binance.api.client.constant.BinanceApiConstants;
import com.binance.api.client.domain.account.Account;
import com.binance.api.client.domain.account.AssetBalance;
import com.binance.api.client.domain.account.Order;
import com.binance.api.client.domain.account.request.OrderRequest;
import com.binance.api.client.domain.market.OrderBook;
import com.binance.api.client.domain.market.TickerPrice;
import com.example.myapplication.binance.APIBinance;
import com.example.myapplication.binance.BinanceState;
import com.example.myapplication.bot.ActionBotInterface;
import com.example.myapplication.bot.ActionBot_CancelOrder;
import com.example.myapplication.bot.Keeper;
import com.example.myapplication.bot.Performer;
import com.example.myapplication.customObjects.SaverInstruct;

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
    private ScheduledExecutorService executor, executor2, executor3, executor4, executor5, executor6;
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

        binanceState = BinanceState.getInstance();
        apibinance = APIBinance.getInstance();
        Toast.makeText(this, "Create", Toast.LENGTH_SHORT).show();
        List<ActionBotInterface> listSaverInstruct = SaverInstruct.getInstance().loadArrayList("listKeeper", this);
        if (listSaverInstruct == null) listSaverInstruct = new ArrayList<>();
        System.out.println("LOADINGSaverInstruct"+ listSaverInstruct);

        Keeper.getInstance().setListActionBot(listSaverInstruct);

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
                    ObservableSave.getObs().update(2);
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

                    Account account = apibinance.getClient().getAccount(5000l, apibinance.getClient().getServerTime());

                    binanceState.setAccount(account);
                    account = null;
                    ObservableSave.getObs().update(2);
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

        executor.scheduleWithFixedDelay(runn, 0, 5, TimeUnit.SECONDS);
        executor2.scheduleWithFixedDelay(runnAcc, 0, 50, TimeUnit.SECONDS);
        executor3.scheduleWithFixedDelay(runnTikPr, 0, 50, TimeUnit.SECONDS);
        executor4.scheduleWithFixedDelay(runnMyOrder, 0, 10, TimeUnit.SECONDS);
        executor5.scheduleWithFixedDelay(runnListBot, 0, 5, TimeUnit.SECONDS);
        executor6.scheduleWithFixedDelay(runnPerfomListBot, 0, 5, TimeUnit.SECONDS);

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
