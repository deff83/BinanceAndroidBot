package com.example.myapplication.bot;

import android.content.Context;

import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.account.Order;
import com.binance.api.client.domain.market.OrderBookEntry;
import com.example.myapplication.Widjet;
import com.example.myapplication.binance.BinanceState;

import java.util.List;

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

                    ActionBot_LimitOrder orderAction2 = new ActionBot_LimitOrder(newAddOrder);
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

                    ActionBot_LimitOrder orderAction2 = new ActionBot_LimitOrder(newAddOrder);
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
                    System.out.println("testfff "+orderentryGoto.getPrice()+" getOrigQty:"+(order.getOrigQty())+" getIcebergQty:"+(order.getIcebergQty())+" getExecutedQty:"+(order.getExecutedQty()));
                    AddOrder newAddOrder = new AddOrder(Double.parseDouble(orderentryGoto.getPrice()+""), Double.parseDouble(order.getOrigQty()+""), order.getSide()+"", order.getSymbol());

                    ActionBot_LimitOrder orderAction2 = new ActionBot_LimitOrder(newAddOrder);
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

                    ActionBot_LimitOrder orderAction2 = new ActionBot_LimitOrder(newAddOrder);
                    Keeper.getInstance().addAction(orderAction2, context);
                    return;
                }
            }
        }
    }
    public void perestanovOrder(Order order, OrderBookEntry entrybook){
        ActionBot_CancelOrder orderAction = new ActionBot_CancelOrder(order);
        Keeper.getInstance().addAction(orderAction, context);

        AddOrder newAddOrder = new AddOrder(Double.parseDouble(entrybook.getPrice()+""), Double.parseDouble(order.getOrigQty()+""), order.getSide()+"", order.getSymbol());
        ActionBot_LimitOrder orderAction2 = new ActionBot_LimitOrder(newAddOrder);
        Keeper.getInstance().addAction(orderAction2, context);

    }
    public int getColPer() {
        return colPer;
    }

    public void setColPer(int colPer) {
        this.colPer = colPer;
    }
}
