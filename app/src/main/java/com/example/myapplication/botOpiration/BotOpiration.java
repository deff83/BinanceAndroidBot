package com.example.myapplication.botOpiration;

import android.content.Context;

import com.example.myapplication.customObjects.SaverInstruct;

import java.util.ArrayList;
import java.util.List;

public class BotOpiration {
    private static final BotOpiration ourInstance = new BotOpiration();

    public static BotOpiration getInstance() {
        return ourInstance;
    }

    private BotOpiration() {
    }
    List<OpirationOrder> listOpirationOrder = new ArrayList<>();


    public void start(){
        for (int i = 0; i < listOpirationOrder.size(); i++) {
            OpirationOrder ope = listOpirationOrder.get(i);
            ope.start();
        }
    }

    public void addOpirationOrder(OpirationOrder action, Context context){
        listOpirationOrder.add(action);
        SaverInstruct.getInstance().saveArrayListOpirationOrder("listOpirationOrder",listOpirationOrder, context);
    }
    public void removeOpirationOrder(OpirationOrder action, Context context){
        listOpirationOrder.remove(action);
        SaverInstruct.getInstance().saveArrayListOpirationOrder("listOpirationOrder",listOpirationOrder, context);
    }
}
