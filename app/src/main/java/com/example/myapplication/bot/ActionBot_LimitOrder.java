package com.example.myapplication.bot;

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.TimeInForce;
import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.account.NewOrderResponse;
import com.binance.api.client.domain.general.ExchangeInfo;
import com.binance.api.client.domain.general.FilterType;
import com.binance.api.client.domain.general.SymbolFilter;
import com.binance.api.client.domain.general.SymbolInfo;
import com.binance.api.client.domain.market.OrderBook;
import com.binance.api.client.domain.market.OrderBookEntry;
import com.example.myapplication.ObservableSave;
import com.example.myapplication.binance.APIBinance;
import com.example.myapplication.binance.BinanceState;

import java.util.List;
import java.util.Locale;

public class ActionBot_LimitOrder implements ActionBotInterface {
    private AddOrder addorder;

    private String name = "LimitOrder";
    private boolean didAction = false;
    private BinanceApiRestClient client;
    private APIBinance apiBinance;
    private int typePostanov = 0;


    public ActionBot_LimitOrder(AddOrder addorder, int typePostanov) {
        this.addorder = addorder;
        apiBinance = APIBinance.getInstance();
        client = apiBinance.getClient();
        this.typePostanov = typePostanov;
    }

    @Override
    public boolean getStateResult() {
        return true;
    }

    @Override
    public void doAction() {

        switch (typePostanov){
            case 0:
                break;
            case 1: //взять цену самую первую в списке
                SavedKeeperInfo savedKeeperInfo = Keeper.getInstance().getSavedKeeperInfo();
                OrderBook orderBook = savedKeeperInfo.getOrderBook();

                List<OrderBookEntry> listBookEntry = orderBook.getAsks();

                if (addorder.getTypeOrder().equals("BUY")){
                    listBookEntry = orderBook.getBids();
                  }

                String luchCen = listBookEntry.get(0).getPrice();
                ExchangeInfo exchangeInfo = BinanceState.getInstance().getExchangeInfo();
                if (exchangeInfo == null) return;
                // Obtain symbol information
                SymbolInfo symbolInfo = exchangeInfo.getSymbolInfo(addorder.getPara());
                SymbolFilter pricefilter = symbolInfo.getSymbolFilter(FilterType.PRICE_FILTER);
                double tick = Double.parseDouble(pricefilter.getTickSize());
                String newPrice = String.format(Locale.ROOT,"%.8f", Double.parseDouble(luchCen)+tick);
                if (addorder.getTypeOrder().equals("SELL")){
                    newPrice = String.format(Locale.ROOT,"%.8f", Double.parseDouble(luchCen)-tick);
                }


                if (addorder.getTypeOrder().equals("BUY")) {
                    //пересчитаь обьем сделки
                    addorder.setValue(Math.floor(addorder.getValue() * addorder.getPrice() / Double.parseDouble(newPrice))-1);

                    System.out.println("teeest:"+addorder.getValue()+" "+addorder.getPrice()+" "+Double.parseDouble(newPrice));
                }

                addorder.setPrice(Double.parseDouble(newPrice));
                addorder.setPriceStr(newPrice);

                break;
        }

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
