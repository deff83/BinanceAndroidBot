package com.example.myapplication.customObjects;

import com.binance.api.client.domain.market.TickerPrice;
import com.example.myapplication.ModelObs;
import com.example.myapplication.ObservableSave;
import com.example.myapplication.Widjet;
import com.example.myapplication.binance.BinanceState;
import com.example.myapplication.bot.BotFunction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SavedAllPrice implements ModelObs {
    private static final SavedAllPrice ourInstance = new SavedAllPrice();

    public static SavedAllPrice getInstance() {
        return ourInstance;
    }

    List<PerCentPrices> listpercIzm = new ArrayList<>();

    private BinanceState binanceState;
    private SavedAllPrice() {
        binanceState = BinanceState.getInstance();
    }


    long nowTekData = new Date().getTime();

    List<TickerPrice> tickerPriceList = null;
    List<TickerPrice> tickerPriceList1 = null;
    List<TickerPrice> tickerPriceList5 = null;
    List<TickerPrice> tickerPriceList10 = null;
    List<TickerPrice> tickerPriceList15 = null;

    long nowTekData1 = new Date().getTime();
    long nowTekData5 = new Date().getTime();
    long nowTekData10 = new Date().getTime() + 1000*60*2;
    long nowTekData15 = new Date().getTime() + 1000*60*3;



    public List<TickerPrice> getTickerPriceList() {
        return tickerPriceList;
    }

    public void setTickerPriceList(List<TickerPrice> tickerPriceList) {
        this.tickerPriceList = tickerPriceList;
    }

    public List<PerCentPrices> getListpercIzm() {
        return listpercIzm;
    }

    public void setListpercIzm(List<PerCentPrices> listpercIzm) {
        this.listpercIzm = listpercIzm;
    }

    @Override
    public void doUpdate(int arg) {
        switch (arg){
            case 5:
                long nowTekDataUpdate = new Date().getTime();

                List<TickerPrice> tikPrice = binanceState.getTikPrice();
                tickerPriceList = tikPrice;
                if ((nowTekDataUpdate - nowTekData1)>1000*60*1){
                    sravnCen(tickerPriceList, tickerPriceList1, 1, 3.0);
                    tickerPriceList1 = tickerPriceList;
                    nowTekData1 = nowTekDataUpdate;
                }
                if ((nowTekDataUpdate - nowTekData5)>1000*60*5){
                    sravnCen(tickerPriceList, tickerPriceList5, 5, 3.0);
                    tickerPriceList5 = tickerPriceList;
                    nowTekData5 = nowTekDataUpdate;
                }
                if ((nowTekDataUpdate - nowTekData10)>1000*60*10){
                    sravnCen(tickerPriceList, tickerPriceList10, 10, 3.0);
                    tickerPriceList10 = tickerPriceList;
                    nowTekData10 = nowTekDataUpdate;
                }
                if ((nowTekDataUpdate - nowTekData15)>1000*60*15){
                    sravnCen(tickerPriceList, tickerPriceList15, 15, 3.0);
                    tickerPriceList15 = tickerPriceList;
                    nowTekData15 = nowTekDataUpdate;
                }
                nowTekData = nowTekDataUpdate;

                break;
        }
    }


    private void sravnCen(List<TickerPrice> tikPriceFrom, List<TickerPrice> tikPriceNew, int period, double otsechka){
        if(tikPriceFrom == null || tikPriceNew == null)return;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                for(int i = 0; i<tikPriceFrom.size(); i++){
                    for (int j = 0; j<tikPriceNew.size(); j++){
                        TickerPrice tickerPriceFromItem = tikPriceFrom.get(i);
                        TickerPrice tickerPriceNewItem = tikPriceNew.get(j);
                        if(tickerPriceFromItem.getSymbol().equals(tickerPriceNewItem.getSymbol())){
                            double pricetickerPriceNewItem =  Double.parseDouble(tickerPriceNewItem.getPrice());
                            double pricetickerPriceFromItem =  Double.parseDouble(tickerPriceFromItem.getPrice());
                            double percentizm = ((pricetickerPriceFromItem - pricetickerPriceNewItem)*100) / pricetickerPriceFromItem;
                            //System.out.println("percentizm:"+percentizm+":"+pricetickerPriceNewItem+":"+pricetickerPriceFromItem);
                            if(Math.abs(percentizm)>otsechka) {
                                if (Math.abs(pricetickerPriceFromItem - pricetickerPriceNewItem) > 2*BotFunction.getTick(tickerPriceFromItem.getSymbol())){
                                    PerCentPrices perNew = new PerCentPrices(tickerPriceFromItem.getSymbol(), Math.floor(percentizm * 100) / 100, period);
                                    getListPerPrices(perNew);
                                }

                            }
                        }
                    }
                }

            }
        };
        System.out.println("ttttttttyyyyy");
        new Thread(runnable).start();
    }

    private synchronized  void getListPerPrices(PerCentPrices perCentPrices){
        if (listpercIzm.size()>10) listpercIzm.remove(0);
        listpercIzm.add(perCentPrices);
        ObservableSave.getObs().update(7);
    }

}
