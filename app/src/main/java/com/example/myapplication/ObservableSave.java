package com.example.myapplication;

import java.util.ArrayList;
import java.util.List;

public class ObservableSave {
    private ObservableSave() {}
    public static ObservableSave obs = new ObservableSave();
    public static ObservableSave getObs() {return obs;}

    List<ModelObs> listModel = new ArrayList<>();
    List<String> list_message = new ArrayList<>();

    public void addModel(ModelObs model){
        if (!listModel.contains(model)) listModel.add(model);
    }
    public void removeModel(ModelObs model){
        if (listModel.contains(model)) listModel.remove(model);
    }
    public void update(int arg){
        for (ModelObs model: listModel){
            model.doUpdate(arg);
        }
    }

    public void addMessage(String mess){
        list_message.add(mess);
    }

    public void sendError(String messErr){
        list_message.add(messErr);
        for (ModelObs model: listModel){
            model.doUpdate(-1);
        }
    }

    public List<String> getList_message() {
        return list_message;
    }
    public String getLastError(){
        return list_message.get(list_message.size()-1);
    }
}
