package com.example.myapplication.binance;

import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.TickerPrice;

import java.util.List;

public class Pokazatel {
    private static final Pokazatel ourInstance = new Pokazatel();

    public static Pokazatel getInstance() {
        return ourInstance;
    }

    private Pokazatel() {
    }
    public Double getTekValBalance(Double priceP, String symbP, List<TickerPrice> tikPrice, String tekVal){
        if (tikPrice == null && tekVal == "" && tekVal == null) return null;
        if (symbP.equals(tekVal)) return priceP;
        System.out.println();
        for (int i=0; i<tikPrice.size(); i++){
            TickerPrice tekp = tikPrice.get(i);
            if ((symbP+tekVal).equals(tekp.getSymbol())){
                return Math.floor(priceP*(Double.parseDouble(tekp.getPrice()))*100)/100;
            }
        }
        return null;
    }
    public String getOtPara(String para){
        String  string3 = para.substring(para.length() - 3, para.length() - 0 );
        String text3 = "USDT";
        if (string3.equals("ETH")){
            text3 = "ETH";
        }
        if (string3.equals("SDC")){
            text3 = "USDC";
        }
        if (string3.equals("SDT")){
            text3 = "USDT";
        }
        if (string3.equals("PAX")){
            text3 = "PAX";
        }
        if (string3.equals("USD")){
            text3 = "TUSD";
        }
        if (string3.equals("BNB")){
            text3 = "BNB";
        }
        if (string3.equals("XRP")){
            text3 = "XRP";
        }
        if (string3.equals("BTC")){
            text3 = "BTC";
        }
        return text3;
    }

    public String getFromPara(String para){
        String otPara = getOtPara(para);
        return para.substring(0, para.length() - otPara.length());
    }

    public double getMnozitel(String para){
        double mnozhitel = 1.0;
        if (getOtPara(para).equals("USDT")) {
            mnozhitel = 1.0;
            return mnozhitel;
        }
        List<TickerPrice> tickPrices = BinanceState.getInstance().getTikPrice();
        if (tickPrices == null ) return  mnozhitel;

        for (int j = 0; j<tickPrices.size(); j++){
            TickerPrice tickerPrice = tickPrices.get(j);
            if (tickerPrice.getSymbol().equals(getOtPara(para)+"USDT")) mnozhitel = Double.parseDouble(tickerPrice.getPrice());
        }

        return mnozhitel;
    }

    public MaxRectCandle getMaxPrice(List<Candlestick> listCandle, Long nachTime, Long endTime){
        double maxPri = 0;
        double minPri = 0;
        for (Candlestick candl: listCandle){
            double priHight =  Double.parseDouble(candl.getHigh());
            double priLow =  Double.parseDouble(candl.getLow());
            long timeOpen = candl.getOpenTime();
            long timeClose = candl.getCloseTime();

            if (timeOpen>=nachTime && timeClose<=endTime) {
                if (maxPri==0) maxPri = priHight;
                if (minPri==0) minPri = priLow;
                if (priHight > maxPri) maxPri = priHight;
                if (priLow < minPri) minPri = priLow;
            }

        }
        return new MaxRectCandle(maxPri, minPri);
    };

}
