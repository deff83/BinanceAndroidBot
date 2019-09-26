package com.example.myapplication.customObjects;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.OrderStatus;
import com.binance.api.client.domain.OrderType;
import com.binance.api.client.domain.TimeInForce;
import com.binance.api.client.domain.account.Order;
import com.example.myapplication.bot.ActionBotInterface;
import com.example.myapplication.bot.ActionBot_CancelOrder;
import com.example.myapplication.bot.ActionBot_LimitOrder;
import com.example.myapplication.bot.AddOrder;
import com.example.myapplication.botOpiration.OpirationOrder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SaverInstruct {
    private static final SaverInstruct ourInstance = new SaverInstruct();

    public class QuestionaireList {
        public List<ActionBotInterface> List;
    }

    public static SaverInstruct getInstance() {
        return ourInstance;
    }

    private SaverInstruct() {
    }

    public void saveArrayList(String name, List<ActionBotInterface> list, Context context) {
        SharedPreferences prefs = context.getSharedPreferences("SETTINGSUP", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String jsonList =  gson.toJson(list);
        System.out.println("save:"+jsonList);
        System.out.println("savelist:"+list);
        editor.putString(name, jsonList).apply();
    }

    public void saveArrayListOpirationOrder(String name, List<OpirationOrder> list, Context context) {
        SharedPreferences prefs = context.getSharedPreferences("SETTINGSUP", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String jsonList =  gson.toJson(list);
        System.out.println("save:"+jsonList);
        System.out.println("savelist:"+list);
        editor.putString(name, jsonList).apply();
    }
    private Order getOrderFromJson(JsonObject jsonOrder){
        Order orderjs = new Order();

        String clientOrderId = jsonOrder.get("clientOrderId").getAsString();
        orderjs.setClientOrderId(clientOrderId);
        String executedQty = jsonOrder.get("clientOrderId").getAsString();
        orderjs.setExecutedQty(executedQty);
        String icebergQty = jsonOrder.get("clientOrderId").getAsString();
        orderjs.setIcebergQty(icebergQty);
        long orderId = jsonOrder.get("orderId").getAsLong();
        orderjs.setOrderId(orderId);
        String origQty = jsonOrder.get("clientOrderId").getAsString();
        orderjs.setOrigQty(origQty);
        String price = jsonOrder.get("price").getAsString();
        orderjs.setPrice(price);
        String side = jsonOrder.get("side").getAsString();
        if (side.equals("SELL")) orderjs.setSide(OrderSide.SELL);
        if (side.equals("BUY")) orderjs.setSide(OrderSide.BUY);

        String status = jsonOrder.get("status").getAsString();
        switch (status) {
            case "NEW":
                orderjs.setStatus(OrderStatus.NEW);
                break;
            case "PARTIALLY_FILLED":
                orderjs.setStatus(OrderStatus.PARTIALLY_FILLED);
                break;
            case "FILLED":
                orderjs.setStatus(OrderStatus.FILLED);
                break;
            case "CANCELED":
                orderjs.setStatus(OrderStatus.CANCELED);
                break;
            case "PENDING_CANCEL":
                orderjs.setStatus(OrderStatus.PENDING_CANCEL);
                break;
            case "REJECTED":
                orderjs.setStatus(OrderStatus.REJECTED);
                break;
            case "EXPIRED":
                orderjs.setStatus(OrderStatus.EXPIRED);
                break;
        }

        String stopPrice = jsonOrder.get("stopPrice").getAsString();
        orderjs.setStopPrice(stopPrice);
        String symbol = jsonOrder.get("symbol").getAsString();
        orderjs.setSymbol(symbol);
        String type = jsonOrder.get("type").getAsString();
        switch(type) {
            case "LIMIT":
                orderjs.setType(OrderType.LIMIT);
                break;
            case "MARKET":
                orderjs.setType(OrderType.LIMIT);
                break;
            case "STOP_LOSS":
                orderjs.setType(OrderType.LIMIT);
                break;
            case "STOP_LOSS_LIMIT":
                orderjs.setType(OrderType.LIMIT);
                break;
            case "TAKE_PROFIT":
                orderjs.setType(OrderType.LIMIT);
                break;
            case "TAKE_PROFIT_LIMIT":
                orderjs.setType(OrderType.LIMIT);
                break;
            case "LIMIT_MAKER":
                orderjs.setType(OrderType.LIMIT);
                break;
        }
        String timeInForce = jsonOrder.get("timeInForce").getAsString();
        orderjs.setTimeInForce(TimeInForce.IOC);
        switch(timeInForce) {
            case "FOK":
                orderjs.setTimeInForce(TimeInForce.FOK);
                break;
            case "IOC":
                orderjs.setTimeInForce(TimeInForce.IOC);
                break;
            case "GTC":
                orderjs.setTimeInForce(TimeInForce.GTC);
                break;
        }

        long time = jsonOrder.get("time").getAsLong();
        orderjs.setTime(time);
        return orderjs;
    }

    private AddOrder getaddOrderFromJson(JsonObject jsonOrder){
        AddOrder addOrder = new AddOrder(jsonOrder.get("price").getAsDouble(), jsonOrder.get("value").getAsDouble(), jsonOrder.get("typeOrder").getAsString(), jsonOrder.get("para").getAsString());
        return  addOrder;
    }

    public List<ActionBotInterface> loadArrayList(String name, Context context) {
        List<ActionBotInterface> actionBotInterfaceList = new ArrayList<>();
        SharedPreferences prefs = context.getSharedPreferences("SETTINGSUP", context.MODE_PRIVATE);
        String textSaver = prefs.getString(name, "");
        /*String[] strings = prefs.getString(name, "").split("<s>");
        ArrayList<String> list = new ArrayList<>();
        list.addAll(Arrays.asList(strings));*/
        if (textSaver.equals("")) return null;
        System.out.println("loadArrayList:"+textSaver);
        JsonArray jsonobg = new JsonParser().parse(textSaver).getAsJsonArray();
        for (int i = 0; i<jsonobg.size(); i++){
            JsonObject jsonobgItem = jsonobg.get(i).getAsJsonObject();
            System.out.println("loadArrayList"+jsonobg.get(i).toString());

            String strName = jsonobgItem.get("name").getAsString();
            if (strName.equals("CancelOrder")){
                JsonObject jsonOrder = jsonobgItem.get("order").getAsJsonObject();
                Order ordern = getOrderFromJson(jsonOrder);
                ActionBot_CancelOrder actiCancel = new ActionBot_CancelOrder(ordern);
                boolean didaction = jsonobgItem.get("didAction").getAsBoolean();
                actiCancel.setDidAction(didaction);
                actionBotInterfaceList.add(actiCancel);
            }
            if (strName.equals("LimitOrder")){
                JsonObject jsonaddOrder = jsonobgItem.get("addorder").getAsJsonObject();
                AddOrder ordern = getaddOrderFromJson(jsonaddOrder);
                ActionBot_LimitOrder actiCancel = new ActionBot_LimitOrder(ordern, 0);
                boolean didaction = jsonobgItem.get("didAction").getAsBoolean();
                actiCancel.setDidAction(didaction);
                actionBotInterfaceList.add(actiCancel);
            }





        }

        //Toast.makeText(context, prefs.getString(name, "NO"), Toast.LENGTH_SHORT).show();
        return actionBotInterfaceList;
    }

}
