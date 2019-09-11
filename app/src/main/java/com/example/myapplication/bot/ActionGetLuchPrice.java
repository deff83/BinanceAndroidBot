package com.example.myapplication.bot;

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.market.OrderBook;
import com.example.myapplication.ObservableSave;
import com.example.myapplication.binance.APIBinance;

public class ActionGetLuchPrice implements ActionBotInterface {
    private String name = "ActionGetLuchPrice";
    private boolean didAction = false;
    private BinanceApiRestClient client;
    private APIBinance apiBinance;
    private String para;

    public ActionGetLuchPrice(String para) {
        apiBinance = APIBinance.getInstance();
        client = apiBinance.getClient();
        this.para = para;
    }

    @Override
    public boolean getStateResult() {
        return true;
    }

    @Override
    public void doAction() {
        OrderBook orderbook = apiBinance.getClient().getOrderBook(para, 5);
        SavedKeeperInfo savedKeeperInfo = Keeper.getInstance().getSavedKeeperInfo();
        savedKeeperInfo.setOrderBook(orderbook);
        Keeper.getInstance().setSavedKeeperInfo(savedKeeperInfo);
        System.out.println("didAction = true;");
        didAction = true;
    }

    @Override
    public boolean didAction() {
        return didAction;
    }

    @Override
    public String getName() {
        return name+":"+para;
    }

    @Override
    public void setDidAction(boolean actionbooldid) {
        this.didAction = actionbooldid;
    }

    @Override
    public void errorActiom(String message) {
        ObservableSave.getObs().sendError(message);
    }


}
