package com.example.myapplication.binance;

import com.binance.api.client.domain.account.Account;
import com.binance.api.client.domain.account.Order;
import com.binance.api.client.domain.event.OrderTradeUpdateEvent;
import com.binance.api.client.domain.general.ExchangeInfo;
import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;
import com.binance.api.client.domain.market.OrderBookEntry;
import com.binance.api.client.domain.market.TickerPrice;
import com.example.myapplication.bot.MaxVol;

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
    private List<Candlestick> candlesticks = new ArrayList<>();
    private MaxVol maxVol = new MaxVol();
    private CandlestickInterval intervalCandle = CandlestickInterval.FIVE_MINUTES;
    private OrderTradeUpdateEvent orderTradeUpdateEvent;

    private ExchangeInfo exchangeInfo = null;
    private  boolean isMyBalance = true;
    private boolean isCandles = false;

    public boolean isCandles() {    return isCandles;    }

    public void setCandles(boolean candles) {     isCandles = candles;    }

    public Long getLongInterval(){
        Long retLong = 0l;
        switch (intervalCandle){
            case FIVE_MINUTES:
                retLong = 1000*60*5l;
                break;
            case DAILY:
                retLong = 1000*60*60*24l;
                break;
            case HOURLY:
                retLong = 1000*60*60l;
                break;
            case WEEKLY:
                retLong = 1000*60*60*24*7l;
                break;
            case MONTHLY://месяц
                retLong = 1000*60*60*24*30l;
                break;
            case ONE_MINUTE:
                retLong = 1000*60*1l;
                break;
            case SIX_HOURLY:
                retLong = 1000*60*60*6l;
                break;
            case FOUR_HOURLY:
                retLong = 1000*60*60*4l;
                break;
            case HALF_HOURLY:
                retLong = 1000*60*30l;
                break;
            case THREE_DAILY:
                retLong = 1000*60*60*24*3l;
                break;
            case EIGHT_HOURLY:
                retLong = 1000*60*60*8l;
                break;
            case THREE_MINUTES:
                retLong = 1000*60*3l;
                break;
            case TWELVE_HOURLY:
                retLong = 1000*60*60*12l;
                break;
            case FIFTEEN_MINUTES:
                retLong = 1000*60*15l;
                break;
        }
        return retLong;
    }

    public CandlestickInterval getIntervalCandle() {  return intervalCandle;   }

    public void setIntervalCandle(CandlestickInterval intervalCandle) {   this.intervalCandle = intervalCandle;  }

    public MaxVol getMaxVol() { return maxVol; }

    public void setMaxVol(MaxVol maxVol) {    this.maxVol = maxVol; }

    public ExchangeInfo getExchangeInfo() {
        return exchangeInfo;
    }

    public void setExchangeInfo(ExchangeInfo exchangeInfo) {
        this.exchangeInfo = exchangeInfo;
    }

    public boolean isMyBalance() {
        return isMyBalance;
    }

    public void setMyBalance(boolean myBalance) {
        isMyBalance = myBalance;
    }

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

    public List<Candlestick> getCandlesticks() {
        return candlesticks;
    }

    public void setCandlesticks(List<Candlestick> candlesticks) {
        this.candlesticks = candlesticks;
    }

    public void addListCandl(List<Candlestick> listaddcandle){
        if (listaddcandle != null && !listaddcandle.isEmpty()) {
            List<Candlestick> result = new ArrayList<>(listaddcandle.size() + candlesticks.size());
            result.addAll(listaddcandle);
            result.addAll(candlesticks);

            candlesticks = result;
        }
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

    public OrderTradeUpdateEvent getOrderTradeUpdateEvent() {   return orderTradeUpdateEvent; }

    public void setOrderTradeUpdateEvent(OrderTradeUpdateEvent orderTradeUpdateEvent) {    this.orderTradeUpdateEvent = orderTradeUpdateEvent; }

    @Override
    public String toString() {
        return "BinanceState{" +
                "priceSell=" + priceSell +
                ", priceBuy=" + priceBuy +
                '}';
    }
}
