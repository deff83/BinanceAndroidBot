package com.example.myapplication.bot;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class Performer {
    private static final Performer ourInstance = new Performer();

    public static Performer getInstance() {
        return ourInstance;
    }

    List<Keeper> listKeepers = new ArrayList<>();

    private Performer() {
        listKeepers.add(Keeper.getInstance());
    }

    public void startPerformer(Context context){
        for (Keeper keeper : listKeepers) {
            System.out.println("startPerformer");
            keeper.startKeeper(context);
        }
    }

    public List<Keeper> getListKeepers() {
        return listKeepers;
    }

    public void setListKeepers(List<Keeper> listKeepers) {
        this.listKeepers = listKeepers;
    }
}
