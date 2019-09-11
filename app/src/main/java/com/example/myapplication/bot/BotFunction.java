package com.example.myapplication.bot;

import android.content.Context;

import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.account.Order;
import com.binance.api.client.domain.general.ExchangeInfo;
import com.binance.api.client.domain.general.FilterType;
import com.binance.api.client.domain.general.SymbolFilter;
import com.binance.api.client.domain.general.SymbolInfo;
import com.binance.api.client.domain.market.OrderBookEntry;
import com.example.myapplication.Widjet;
import com.example.myapplication.binance.BinanceState;

import java.util.List;
import java.util.Locale;

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
        ExchangeInfo exchangeInfo = BinanceState.getInstance().getExchangeInfo();
        if (exchangeInfo == null) return;
        // Obtain symbol information
        SymbolInfo symbolInfo = exchangeInfo.getSymbolInfo(order.getSymbol());
        SymbolFilter pricefilter = symbolInfo.getSymbolFilter(FilterType.PRICE_FILTER);
        double tick = Double.parseDouble(pricefilter.getTickSize());
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

    public void setColPer(int colPer) {
        this.colPer = colPer;
    }
}
