package com.example.myapplication.binance;

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

}
