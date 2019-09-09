package com.example.myapplication.bot;

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.TimeInForce;
import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.account.NewOrderResponse;
import com.example.myapplication.ObservableSave;
import com.example.myapplication.binance.APIBinance;

public class ActionBot_LimitOrder implements ActionBotInterface {
    private AddOrder addorder;

    private String name = "LimitOrder";
    private boolean didAction = false;
    private BinanceApiRestClient client;
    private APIBinance apiBinance;


    public ActionBot_LimitOrder(AddOrder addorder) {
        this.addorder = addorder;
        apiBinance = APIBinance.getInstance();
        client = apiBinance.getClient();
    }

    @Override
    public boolean getStateResult() {
        return true;
    }

    @Override
    public void doAction() {
        System.out.println("adddd "+addorder.getValue()+" "+addorder.getPrice()+" "+addorder.getPriceStr()+"");
        NewOrder newOrderAdd = NewOrder.limitSell(addorder.getPara(), TimeInForce.GTC, addorder.getValue()+"", addorder.getPriceStr()+"");
        if(addorder.getTypeOrder().equals("BUY")) newOrderAdd = NewOrder.limitBuy(addorder.getPara(), TimeInForce.GTC, addorder.getValue()+"", addorder.getPriceStr()+""); ;

        newOrderAdd.recvWindow(5000l);
        newOrderAdd.timestamp(client.getServerTime());
        try {
            NewOrderResponse newOrderResponse = client.newOrder(newOrderAdd);
            System.out.println("newOrderResponse LIMIT: "+newOrderResponse.getTransactTime()+" "+newOrderResponse.getOrderId());
            errorActiom("newOrder LIMIT: "+newOrderResponse.getSymbol()+" "+newOrderResponse.getPrice()+" "+newOrderResponse.getOrigQty());

        }catch(Exception e){
            System.out.println("newOrderResponse LIMIT Error:"+e.getMessage());
            errorActiom("newOrderResponse LIMIT Error:"+e.getMessage());
        }

        System.out.println("didAction = true;");
        didAction = true;
    }

    @Override
    public boolean didAction() {
        return didAction;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setDidAction(boolean actionbooldid) {
        this.didAction = actionbooldid;
    }

    @Override
    public void errorActiom(String message) {
        ObservableSave.getObs().sendError(message);
    }

    @Override
    public String toString() {
        return "ActionBot_LimitOrder{" +
                "name='" + name + '\'' +
                ", addorder=" + addorder.toString() +
                ", didAction=" + didAction +
                '}';
    }
}
