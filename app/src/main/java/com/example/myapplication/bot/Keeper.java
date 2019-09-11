package com.example.myapplication.bot;

import android.content.Context;

import com.example.myapplication.customObjects.SaverInstruct;

import java.util.ArrayList;
import java.util.List;

public class Keeper {
    private static final Keeper ourInstance = new Keeper();

    public static Keeper getInstance() {
        return ourInstance;
    }

    private Keeper() {
        listActionBot = new ArrayList<>();
    }

    private List<ActionBotInterface> listActionBot;

    private SavedKeeperInfo savedKeeperInfo = new SavedKeeperInfo();

    public void startKeeper( Context context){
        if (listActionBot.size()==0) return;
        ActionBotInterface actionFirst = listActionBot.get(0);
        if(!actionFirst.didAction()){
            actionFirst.doAction();
        }

        if (actionFirst.getStateResult()){
            listActionBot.remove(0);
            SaverInstruct.getInstance().saveArrayList("listKeeper",listActionBot, context);
        }
    }

    public void addAction(ActionBotInterface action, Context context){
        listActionBot.add(action);
        SaverInstruct.getInstance().saveArrayList("listKeeper",listActionBot, context);
    }
    public void removeAction(ActionBotInterface action, Context context){
        listActionBot.remove(action);
        SaverInstruct.getInstance().saveArrayList("listKeeper",listActionBot, context);
    }

    public List<ActionBotInterface> getListActionBot() {
        return listActionBot;
    }

    public void setListActionBot(List<ActionBotInterface> listActionBot) {
        this.listActionBot = listActionBot;
    }

    public SavedKeeperInfo getSavedKeeperInfo() {
        return savedKeeperInfo;
    }

    public void setSavedKeeperInfo(SavedKeeperInfo savedKeeperInfo) {
        System.out.println("Saved keeper");
        this.savedKeeperInfo = savedKeeperInfo;
    }
}
