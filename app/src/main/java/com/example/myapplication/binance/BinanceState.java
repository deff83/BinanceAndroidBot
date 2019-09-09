package com.example.myapplication.binance;

import com.binance.api.client.domain.account.Account;
import com.binance.api.client.domain.account.Order;
import com.binance.api.client.domain.market.OrderBookEntry;
import com.binance.api.client.domain.market.TickerPrice;

import java.util.ArrayList;
import java.util.List;

public class BinanceState {
    private static final BinanceState ourInstance = new BinanceState();

    public synchronized static BinanceState getInstance() {
        return ourInstance;
    }

    private BinanceState() {
    }

    private List<OrderBookEntry> priceSell = new ArrayList<>();
    private List<OrderBookEntry> priceBuy = new ArrayList<>();
    private List<TickerPrice> tikPrice = new ArrayList<>();

    public List<Order> getMyOrdersTek() {
        return myOrdersTek;
    }

    public void setMyOrdersTek(List<Order> myOrdersTek) {
        this.myOrdersTek = myOrdersTek;
    }

    private List<Order> myOrdersTek = new ArrayList<>();

    private Account account = null;

    private String tekVal = "USDT";

    private int tekCount = 20;

    public int getTekCount() {
        return tekCount;
    }

    public void setTekCount(int tekCount) {
        this.tekCount = tekCount;
    }

    public String getTekVal() {
        return tekVal;
    }

    public void setTekVal(String tekVal) {
        this.tekVal = tekVal;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String getTekPara() {
        return tekPara;
    }

    public void setTekPara(String tekPara) {
        this.tekPara = tekPara;
    }

    private String tekPara = "COCOSUSDT";

    public List<TickerPrice> getTikPrice() {
        return tikPrice;
    }

    public void setTikPrice(List<TickerPrice> tikPrice) {
        this.tikPrice = tikPrice;
    }

    public List<OrderBookEntry> getPriceSell() {
        return priceSell;
    }

    public void setPriceSell(List<OrderBookEntry> priceSell) {
        this.priceSell = priceSell;
    }

    public List<OrderBookEntry> getPriceBuy() {
        return priceBuy;
    }

    public void setPriceBuy(List<OrderBookEntry> priceBuy) {
        this.priceBuy = priceBuy;
    }

    public Order getOrderById(int id){
        for (int i=0; i<myOrdersTek.size(); i++){
            Order myOrderItem = myOrdersTek.get(i);
            if(myOrderItem.getOrderId() == (long)id){
                return myOrderItem;
            }
        }
        return null;
    };
    @Override
    public String toString() {
        return "BinanceState{" +
                "priceSell=" + priceSell +
                ", priceBuy=" + priceBuy +
                '}';
    }
}
