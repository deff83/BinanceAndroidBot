package com.example.myapplication.binance;

import com.binance.api.client.domain.account.AssetBalance;

import java.util.Comparator;

public class SortAsset implements Comparator<AssetBalance> {
    @Override
    public int compare(AssetBalance assetBalance, AssetBalance t1) {
        BinanceState binanceState = BinanceState.getInstance();
        Double pokazTek = Pokazatel.getInstance().getTekValBalance(Double.parseDouble(assetBalance.getFree()), assetBalance.getAsset(), binanceState.getTikPrice(), binanceState.getTekVal() );
        Double pokazTek1 = Pokazatel.getInstance().getTekValBalance(Double.parseDouble(t1.getFree()), t1.getAsset(), binanceState.getTikPrice(), binanceState.getTekVal() );
        if (pokazTek == null) pokazTek = -1d;
        if (pokazTek1 == null) pokazTek1 = -1d;
        return (int)(Math.round(pokazTek1 - pokazTek));
    }
}
