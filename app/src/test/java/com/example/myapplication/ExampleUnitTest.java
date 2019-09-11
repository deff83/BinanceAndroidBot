package com.example.myapplication;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.OrderStatus;
import com.binance.api.client.domain.TimeInForce;
import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.account.NewOrderResponse;
import com.binance.api.client.domain.account.Order;
import com.binance.api.client.domain.account.request.OrderStatusRequest;
import com.binance.api.client.domain.general.ExchangeInfo;
import com.binance.api.client.domain.general.FilterType;
import com.binance.api.client.domain.general.SymbolFilter;
import com.binance.api.client.domain.general.SymbolInfo;
import com.binance.api.client.domain.market.AggTrade;
import com.binance.api.client.domain.market.OrderBook;
import com.binance.api.client.domain.market.OrderBookEntry;
import com.binance.api.client.domain.market.TickerStatistics;
import com.example.myapplication.binance.APIBinance;
import com.example.myapplication.bot.ActionBot_CancelOrder;
import com.example.myapplication.bot.AddOrder;

import org.junit.Test;

import java.util.List;
import java.util.Locale;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    private BinanceApiRestClient client;
    private APIBinance apiBinance;
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public  void cancelOrder(){
        apiBinance = APIBinance.getInstance();
        client = apiBinance.getClient();
        //OrderStatus
//                NEW, стоит заявка
//                PARTIALLY_FILLED, частично исполнена
//                FILLED, исполнена заявка
//                CANCELED, отменена
//                PENDING_CANCEL, ожидание отмены
//                REJECTED, отклонена
//                EXPIRED, ИСТEКШИЙ
        OrderStatusRequest orderStatusRequest = new OrderStatusRequest("COCOSUSDT", 387371l);
        orderStatusRequest.recvWindow(5000l);
        orderStatusRequest.timestamp(client.getServerTime());
        Order order = client.getOrderStatus(orderStatusRequest);
        System.out.println("APICancelOrder"+order);
    }

    @Test
    public  void paramSymbol(){
        apiBinance = APIBinance.getInstance();
        client = apiBinance.getClient();
        //SymbolFilter
        //PRICE_FILTER
        //PERCENT_PRICE
        //LOT_SIZE
        //MIN_NOTIONAL
        //ICEBERG_PARTS
        //MARKET_LOT_SIZE
        //MAX_NUM_ALGO_ORDERS
        try {

            ExchangeInfo exchangeInfo = client.getExchangeInfo();
            SymbolInfo symbolInfo = exchangeInfo.getSymbolInfo("BCPTBTC");
            List<SymbolFilter> listFilters = symbolInfo.getFilters();
            for(SymbolFilter symbolFilter: listFilters){
                System.out.println(symbolFilter.getFilterType());
                //if (symbolFilter.getFilterType() == FilterType.PRICE_FILTER){
                    System.out.println("getTickSize "+symbolFilter.getTickSize());
                    System.out.println("getLimit "+symbolFilter.getLimit());
                    System.out.println("getMaxPrice "+symbolFilter.getMaxPrice());
                    System.out.println("getMaxQty "+symbolFilter.getMaxQty());
                    System.out.println("getMinNotional "+symbolFilter.getMinNotional());
                    System.out.println("getMinPrice "+symbolFilter.getMinPrice());
                    System.out.println("getMinQty "+symbolFilter.getMinQty());
                    System.out.println("getStepSize "+symbolFilter.getStepSize());
                    System.out.println("getMaxNumAlgoOrders "+symbolFilter.getMaxNumAlgoOrders());

                //}
            }

        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        /*SymbolInfo symbolInfo = exchangeInfo.getSymbolInfo("ETHBTC");
        System.out.println(symbolInfo.getStatus());
        SymbolFilter priceFilter = symbolInfo.getSymbolFilter(FilterType.PRICE_FILTER);
        System.out.println(priceFilter.getMinPrice());
        System.out.println(priceFilter.getTickSize());*/

    }
    @Test
    public void setOrder(){
        apiBinance = APIBinance.getInstance();
        client = apiBinance.getClient();

        AddOrder addorder = new AddOrder(2.83E-4, 290000, "SELL", "WINUSDC");
        System.out.println(String.format(Locale.ROOT,"%.8f", addorder.getPrice()));
        addorder.setPriceStr(String.format(Locale.ROOT,"%.8f", addorder.getPrice()));
        NewOrder newOrderAdd = NewOrder.limitSell(addorder.getPara(), TimeInForce.GTC, addorder.getValue()+"", addorder.getPriceStr()+"");
        if(addorder.getTypeOrder().equals("BUY")) newOrderAdd = NewOrder.limitBuy(addorder.getPara(), TimeInForce.GTC, addorder.getValue()+"", addorder.getPriceStr()+""); ;

        newOrderAdd.recvWindow(5000l);
        newOrderAdd.timestamp(client.getServerTime());
        try {
            NewOrderResponse newOrderResponse = client.newOrder(newOrderAdd);
            System.out.println("newOrderResponse LIMIT: "+newOrderResponse.getTransactTime()+" "+newOrderResponse.getOrderId());
            //errorActiom("newOrder LIMIT: "+newOrderResponse.getSymbol()+" "+newOrderResponse.getPrice()+" "+newOrderResponse.getOrigQty());

        }catch(Exception e){
            System.out.println("newOrderResponse LIMIT Error:"+e.getMessage());
            //errorActiom("newOrderResponse LIMIT Error:"+e.getMessage());
        }
    }
}