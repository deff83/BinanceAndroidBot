package com.example.myapplication.bot;

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.OrderStatus;
import com.binance.api.client.domain.account.Order;
import com.binance.api.client.domain.account.request.CancelOrderRequest;
import com.binance.api.client.domain.account.request.CancelOrderResponse;
import com.binance.api.client.domain.account.request.OrderStatusRequest;
import com.example.myapplication.ObservableSave;
import com.example.myapplication.binance.APIBinance;

public class ActionBot_CancelOrder implements ActionBotInterface {

    private String name = "CancelOrder";
    private Order order;
    private boolean didAction = false;
    private BinanceApiRestClient client;
    private APIBinance apiBinance;

    private ActionBot_CancelOrder() {}
    public ActionBot_CancelOrder(Order order) {
        this.order = order;
        apiBinance = APIBinance.getInstance();
        client = apiBinance.getClient();
    }

    @Override
    public void doAction() {
        CancelOrderRequest orderStatusRequest = new CancelOrderRequest(order.getSymbol(), order.getOrderId());
        orderStatusRequest.recvWindow(5000l);
        orderStatusRequest.timestamp(client.getServerTime());
        try {
            CancelOrderResponse cancelOrderResponse = client.cancelOrder(orderStatusRequest);
            System.out.println("cancelOrderResponse "+cancelOrderResponse);
        }catch (Exception e){
            System.out.println("cancelOrderResponse Error:"+e.getMessage());
            errorActiom("cancelOrderResponse Error:"+e.getMessage());
        }
        System.out.println("didAction = true;");
        didAction = true;
    }

    @Override
    public boolean getStateResult() {
        OrderStatusRequest orderStatusRequest = new OrderStatusRequest(order.getSymbol(), order.getOrderId());
        orderStatusRequest.recvWindow(5000l);
        orderStatusRequest.timestamp(client.getServerTime());
        Order order = client.getOrderStatus(orderStatusRequest);
        if (order == null) return false;
        System.out.println("APICancelOrder"+order);
        if (order.getStatus() != OrderStatus.NEW && order.getStatus() != OrderStatus.PARTIALLY_FILLED && order.getStatus() != OrderStatus.PENDING_CANCEL){
            return true;
        }

        return false;
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
    public void errorActiom(String message) {
        ObservableSave.getObs().sendError(message);
    }

    @Override
    public void setDidAction(boolean actionbooldid) {
        this.didAction = actionbooldid;
    }

    @Override
    public String toString() {
        return "ActionBot_CancelOrder{" +
                "name='" + name + '\'' +
                ", order=" + order.toString() +
                ", didAction=" + didAction +
                '}';
    }
}
