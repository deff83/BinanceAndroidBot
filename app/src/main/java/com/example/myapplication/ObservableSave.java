package com.example.myapplication;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.ArrayList;
import java.util.List;

public class ObservableSave {

    private int notify_int = 1000;
    private boolean network = false;

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

    public boolean isNetwork() {
        return network;
    }

    public void setNetwork(boolean network) {
        this.network = network;
    }

    public List<String> getList_message() {
        return list_message;
    }
    public String getLastError(){
        return list_message.get(list_message.size()-1);
    }

    //ObservableSave.getObs().doNotify(1, R.mipmap.ic_btc_icon, "title", "texy", getApplicationContext());
    public void doNotify(int number, int res, String texttitle, String text, Context context){

        if (number == -1){
            notify_int ++;
            number = notify_int;
        }

        Intent intent_receiver = new Intent(context, MainActivity.class);
        PendingIntent pi_receiver = PendingIntent.getActivity(context, number, intent_receiver, PendingIntent.FLAG_ONE_SHOT);
        int notificationId = number;

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context)
                        .setContentIntent(pi_receiver)
                        .setSmallIcon(res)
                        .setContentTitle(texttitle)
                        .setContentText(text)
                        .setTicker(texttitle)
                        .setAutoCancel(true)
                        .setDefaults(NotificationCompat.DEFAULT_SOUND);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(notificationId, notificationBuilder.build());
    }

    public void opovVibro(Context context){

        long[] pattern = { 0, 300, 400, 200, 100, 200 , 100, 200 };
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(pattern, -1);
        }
    }
}
