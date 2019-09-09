package com.example.myapplication;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.PixelFormat;
import android.graphics.Shader;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.print.PrintAttributes;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.account.Account;
import com.binance.api.client.domain.account.AssetBalance;
import com.binance.api.client.domain.account.Order;
import com.binance.api.client.domain.market.OrderBook;
import com.binance.api.client.domain.market.OrderBookEntry;
import com.binance.api.client.domain.market.TickerPrice;
import com.example.myapplication.binance.BinanceState;
import com.example.myapplication.binance.Pokazatel;
import com.example.myapplication.binance.SortAsset;
import com.example.myapplication.bot.ActionBotInterface;
import com.example.myapplication.bot.ActionBot_CancelOrder;
import com.example.myapplication.bot.ActionBot_LimitOrder;
import com.example.myapplication.bot.AddOrder;
import com.example.myapplication.bot.BotFunction;
import com.example.myapplication.bot.Keeper;
import com.example.myapplication.bot.Performer;
import com.example.myapplication.customObjects.MyWidgetButton;
import com.example.myapplication.customObjects.Preobr;
import com.example.myapplication.customObjects.SaverInstruct;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Widjet extends Service implements ModelObs {
    SharedPreferences pref;
    SharedPreferences.Editor editor = null;

    WindowManager wm;
    WindowManager.LayoutParams myParams, myParamsBalance, changemyOrder, botListwm, addmyOrder, dopFunction, myOrderList;
    LinearLayout tr, trBalance, trChangeOrder, trWidgetbot, trAddOrder, trWidgetDopFunctio, trMyOrder;
    LinearLayout widgetScrLay, settingLay, balanceLay, myOrderLay, layBotList;
    View.OnClickListener myOrderclickListener, onClickListBook;
    int argSt = 1;
    Comparator balanceComparator = new SortAsset();
    boolean settingbool = true;
    Handler handler;
    public Widjet() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("onCreate");
        //settings UP
        pref = getSharedPreferences("SETTINGSUP", Context.MODE_PRIVATE);
        editor = pref.edit();

        String tekPara = pref.getString("tekPara", "COCOSUSDT");
        BinanceState.getInstance().setTekPara(tekPara);

        int tekCount= pref.getInt("tekCount", 20);
        BinanceState.getInstance().setTekCount(tekCount);


        //Toast.makeText(this, , Toast.LENGTH_SHORT).show();

        //window trade
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        myParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        myParamsBalance = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        changemyOrder = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);
        addmyOrder = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);

        dopFunction = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);


        botListwm = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        myOrderList = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        myOrderList.gravity = Gravity.RIGHT | Gravity.TOP;

        botListwm.gravity = Gravity.LEFT | Gravity.BOTTOM;

        changemyOrder.gravity = Gravity.CENTER;


        myParamsBalance.gravity = Gravity.LEFT | Gravity.BOTTOM;
        myParams.gravity = Gravity.RIGHT;

        //myParams.verticalMargin = 20.0f;
        LayoutInflater inflater1 = LayoutInflater.from(this);
        tr = (LinearLayout) inflater1.inflate(R.layout.widgetlay, null);
        LayoutInflater inflater2 = LayoutInflater.from(this);
        trBalance = (LinearLayout) inflater2.inflate(R.layout.widjetlaybalance, null);
        LayoutInflater inflater3 = LayoutInflater.from(this);
        trChangeOrder = (LinearLayout) inflater3.inflate(R.layout.widget_change_order, null);
        LayoutInflater inflater4 = LayoutInflater.from(this);
        trAddOrder = (LinearLayout) inflater4.inflate(R.layout.widjet_add_order, null);
        LayoutInflater inflater5 = LayoutInflater.from(this);
        trWidgetbot= (LinearLayout) inflater5.inflate(R.layout.widgetbot, null);
        LayoutInflater inflater6 = LayoutInflater.from(this);
        trWidgetDopFunctio= (LinearLayout) inflater6.inflate(R.layout.widget_dop_function, null);
        LayoutInflater inflater7 = LayoutInflater.from(this);
        trMyOrder= (LinearLayout) inflater7.inflate(R.layout.layout_my_orders, null);





        Button butGrav = (Button) tr.findViewById(R.id.buttonGrav);
        Button butChange = (Button) tr.findViewById(R.id.buttonChange);
        widgetScrLay = (LinearLayout) tr.findViewById(R.id.widgetScrLay);
        settingLay = (LinearLayout)  tr.findViewById(R.id.settingLay);
        myOrderLay = (LinearLayout)  trMyOrder.findViewById(R.id.myOrderLay);
        balanceLay = (LinearLayout)  trBalance.findViewById(R.id.balanceLay);

        layBotList = (LinearLayout)  trWidgetbot.findViewById(R.id.layBotList);

        butGrav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myParams.gravity == Gravity.RIGHT) {myParams.gravity = Gravity.LEFT;}
                else {myParams.gravity = Gravity.RIGHT;}
                wm.updateViewLayout(tr, myParams);
            }
        });
        butChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Widjet.this, "this", Toast.LENGTH_SHORT).show();
                if (settingbool) {
                    Spinner spinner = new Spinner(Widjet.this);

                    List<TickerPrice> ticcker = BinanceState.getInstance().getTikPrice();
                    int selectitem = 0;
                    ArrayList<String> arrayList = new ArrayList<>();
                    for (int i=0; i<ticcker.size(); i++){
                        String symbol = ticcker.get(i).getSymbol();
                        arrayList.add(symbol);
                        //System.out.println(symbol);
                        if (symbol.equals(BinanceState.getInstance().getTekPara())){
                            selectitem = i;

                        }
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Widjet.this, android.R.layout.simple_spinner_item, arrayList);
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(arrayAdapter);
                    spinner.setSelection(selectitem);

                    Spinner spinnerCount = new Spinner(Widjet.this);

                    int tekCount = BinanceState.getInstance().getTekCount();


                    ArrayList<String> arrayListCount = new ArrayList<>(Arrays.asList("5", "10", "20", "50", "100", "500", "1000", "5000"));

                    ArrayAdapter<String> arrayAdapterCount = new ArrayAdapter<String>(Widjet.this, android.R.layout.simple_spinner_item, arrayListCount);
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCount.setAdapter(arrayAdapterCount);

                    for (int i=0; i<arrayListCount.size(); i++){
                        if (arrayListCount.get(i).equals(BinanceState.getInstance().getTekCount()+"")){
                            spinnerCount.setSelection(i);
                        }
                    }

                    settingLay.addView(spinner);
                    settingLay.addView(spinnerCount);
                    settingbool = false;

                    //listener
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            String tutorialsName = parent.getItemAtPosition(position).toString();
                            Toast.makeText(parent.getContext(), "Selected: " + tutorialsName,          Toast.LENGTH_LONG).show();
                            BinanceState.getInstance().setTekPara(tutorialsName);
                            editor.putString("tekPara", tutorialsName);
                            editor.commit();
                        }
                        @Override
                        public void onNothingSelected(AdapterView <?> parent) {
                        }
                    });

                    //listener
                    spinnerCount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            String tutorialsName = parent.getItemAtPosition(position).toString();
                            Toast.makeText(parent.getContext(), "Selected: " + tutorialsName,          Toast.LENGTH_LONG).show();
                            BinanceState.getInstance().setTekCount(Integer.parseInt(tutorialsName));
                            editor.putInt("tekCount", Integer.parseInt(tutorialsName));
                            editor.commit();
                        }
                        @Override
                        public void onNothingSelected(AdapterView <?> parent) {
                        }
                    });





                }else{
                    settingLay.removeAllViews();
                    settingbool = true;
                }
            }
        });
        wm.addView(trBalance, myParamsBalance);
        wm.addView(trWidgetbot, botListwm);
        wm.addView(trMyOrder, myOrderList);
        wm.addView(tr, myParams);

        myOrderclickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int idOrder = view.getId();
                BinanceState binanceState = BinanceState.getInstance();
                Order orderclick = binanceState.getOrderById(idOrder);
                //Toast.makeText(Widjet.this, idOrder+":"+orderclick.getPrice(), Toast.LENGTH_SHORT).show();
                showDiologOrder(orderclick);
            }
        };
        onClickListBook = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int idOrder = view.getId();
                BinanceState binanceState = BinanceState.getInstance();
                List<OrderBookEntry> priceSell = binanceState.getPriceSell();
                List<OrderBookEntry> priceBuy = binanceState.getPriceBuy();
                //BinanceState binanceState = BinanceState.getInstance();
                //Order orderclick = binanceState.getOrderById(idOrder);
                System.out.println(idOrder-10000);
                OrderBookEntry sellorderbook = null;
                String typeAdd = "BUY";
                if (idOrder < 20000){
                    sellorderbook = priceSell.get(idOrder-10000);
                    typeAdd = "SELL";
                }
                if (idOrder >= 20000){
                    sellorderbook = priceBuy.get(idOrder-20000);
                    typeAdd = "BUY";
                }
                if (sellorderbook != null){
                    Toast.makeText(Widjet.this, sellorderbook.getPrice()+"", Toast.LENGTH_SHORT).show();
                }

                showDiologAddOrder(sellorderbook, typeAdd);
            }
        };

        //добавим в слушателя в список
        ObservableSave.getObs().addModel(this);



    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //получаем с состояния бинанс цен
        BinanceState binanceState = BinanceState.getInstance();
        List<OrderBookEntry> priceSell = binanceState.getPriceSell();
        List<OrderBookEntry> priceBuy = binanceState.getPriceBuy();
        Account account = binanceState.getAccount();
        List<Order> listOrder = binanceState.getMyOrdersTek();
        List<Keeper> listBot = Performer.getInstance().getListKeepers();

        switch(argSt){
            case -1:
                Toast.makeText(this, ObservableSave.getObs().getLastError(), Toast.LENGTH_SHORT).show();
                //System.out.println("gerty"+ObservableSave.getObs().getLastError());
                break;
            case 1:
                getPricecList(priceSell, priceBuy);
                break;
            case 2:
                setAccountB(account);
                break;
            case 3:
                setMyOrder(listOrder);
                break;
            case 4:
                setListBot(listBot);
                break;

        }




        return super.onStartCommand(intent, flags, startId);
    }

    public void setMyOrder(List<Order> listOrder){
        myOrderLay.removeAllViews();
        BinanceState binanceState = BinanceState.getInstance();
        List<Order> myOrdersTek = binanceState.getMyOrdersTek();
        for(int i = 0; i<myOrdersTek.size(); i++){
            Order myorder = myOrdersTek.get(i);
            MyWidgetButton butttest1 = new MyWidgetButton(this);

            butttest1.setTextSize(12.0f);
            butttest1.setBackgroundColor(Color.BLACK);
            butttest1.setPadding(5, 0, 5, 0);
            butttest1.setMaxHeight(20);
            butttest1.setText(myorder.getSide()+":"+myorder.getPrice());
            butttest1.setId((int)(myorder.getOrderId()%1000000000));

            butttest1.setOnClickListener(myOrderclickListener);

            myOrderLay.addView(butttest1);
        }


    }

    public void setListBot(List<Keeper> listBot){
        System.out.println(listBot);
        layBotList.removeAllViews();
        System.out.println("removeeeeeeeeeeeeeeeeee");

        for (Keeper keeper:listBot){
            View.OnClickListener listenerAction = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDopFunctionKeeper(keeper);
                }
            };
            LinearLayout linKeeper = new LinearLayout(this);
            linKeeper.setOrientation(LinearLayout.VERTICAL);
            List<ActionBotInterface> listActionBot = keeper.getListActionBot();
            if(listActionBot == null) return;
            if (listActionBot.size()==0){
                MyWidgetButton butttestVal = new MyWidgetButton(this);
                butttestVal.setTextColor(Color.BLACK);
                butttestVal.setBackgroundColor(Color.rgb(255,255,255));
                butttestVal.setTextSize(12.0f);
                butttestVal.setPadding(5, 0, 5, 0);
                butttestVal.setText("no action");
                butttestVal.setMaxHeight(20);
                butttestVal.setOnClickListener(listenerAction);
                linKeeper.addView(butttestVal);
                layBotList.addView(linKeeper);
                continue;
            }



            for (int i = 0; i<listActionBot.size(); i++){
                ActionBotInterface actionBot = listActionBot.get(i);
                MyWidgetButton butttestVal = new MyWidgetButton(this);
                butttestVal.setTextColor(Color.BLACK);
                butttestVal.setBackgroundColor(Color.rgb(255,255,255));
                butttestVal.setTextSize(12.0f);
                butttestVal.setPadding(5, 0, 5, 0);
                butttestVal.setText(actionBot.getName()+"");
                butttestVal.setMaxHeight(20);
                butttestVal.setOnClickListener(listenerAction);
                linKeeper.addView(butttestVal);
            }
            layBotList.addView(linKeeper);
        }
    }

    public void setAccountB(Account acc){
        if (acc != null) {
            System.out.println(acc.getBalances());
            List<AssetBalance> listbalance = acc.getBalances();
            BinanceState binanceState = BinanceState.getInstance();
            String balanc = acc.toString();

            Collections.sort(listbalance, balanceComparator);
            Pokazatel pok = Pokazatel.getInstance();
            balanceLay.removeAllViews();
            MyWidgetButton butttest1 = new MyWidgetButton(this);

            butttest1.setTextSize(12.0f);
            butttest1.setBackgroundColor(Color.BLACK);
            butttest1.setPadding(5, 0, 5, 0);
            butttest1.setMaxHeight(20);
            balanceLay.addView(butttest1);
            double summBalance = 0.0;
            for (int i=0; i<5; i++) {
                AssetBalance assetbalance = listbalance.get(i);
                Double pokazTek = pok.getTekValBalance(Double.parseDouble(assetbalance.getFree()), assetbalance.getAsset(), binanceState.getTikPrice(), binanceState.getTekVal() );
                summBalance += pokazTek;
                MyWidgetButton butttest = new MyWidgetButton(this);
                butttest.setText(assetbalance.getAsset()+":"+assetbalance.getFree() +"("+ pokazTek +")");
                butttest.setTextSize(12.0f);
                butttest.setBackgroundColor(Color.BLACK);
                butttest.setPadding(5, 0, 5, 0);
                butttest.setMaxHeight(20);
                balanceLay.addView(butttest);
            }
            butttest1.setText(summBalance+" : ");



        }
    }

    public void getPricecList(List<OrderBookEntry> priceSell, List<OrderBookEntry> priceBuy){
        //System.out.println("getPrice");
        widgetScrLay.removeAllViews();
        double maxPriceSell = 0;
        double maxPriceBuy = 0;
        for (int i = priceSell.size()-1; i!=-1; i--){
            double val = Double.parseDouble(priceSell.get(i).getQty());
            if (maxPriceSell<val) maxPriceSell = val;
        }
        for (int i = 0; i<priceBuy.size(); i++){
            double val = Double.parseDouble(priceBuy.get(i).getQty());
            if (maxPriceBuy<val) maxPriceBuy = val;
        }
        List<Order> listMyOrdes = BinanceState.getInstance().getMyOrdersTek();

        for (int i = priceSell.size()-1; i!=-1; i--){
            OrderBookEntry sellorderbook = priceSell.get(i);
            String priceText = sellorderbook.getPrice();
            double priceTe = Double.parseDouble(priceText);
            LinearLayout ll = new LinearLayout(this);
            ll.setOrientation(LinearLayout.HORIZONTAL);
            MyWidgetButton butttest = new MyWidgetButton(this);
            //LinearGradient lg = new LinearGradient(0, 0, 10, 10,
             //       new int[]{Color.GREEN, Color.GREEN, Color.WHITE, Color.WHITE},
             //       new float[]{0,0.5f,.55f,1}, Shader.TileMode.REPEAT);
            butttest.setId(10000+i);
            butttest.setOnClickListener(onClickListBook);
            butttest.setTextColor(Color.rgb(255,100,100));
            butttest.setBackgroundColor(Color.BLACK);

            for(int j=0; j<listMyOrdes.size(); j++){
                Order myItemOrder = listMyOrdes.get(j);
                String tupeOrder = myItemOrder.getSide().name();
                if (tupeOrder.equals("SELL")){
                    if (priceText.equals(myItemOrder.getPrice())){
                        butttest.setBackgroundColor(Color.rgb(255, 215, 0));
                    }
                }
            }

            butttest.setTextSize(12.0f);

            butttest.setPadding(5, 0, 5, 0);
            butttest.setText(priceTe+"");
            butttest.setMaxHeight(20);
            ll.addView(butttest);

            double val = Double.parseDouble(priceSell.get(i).getQty());
            MyWidgetButton butttestVal = new MyWidgetButton(this);
            butttestVal.setTextColor(Color.BLACK);
            butttestVal.setBackgroundColor(Color.rgb((int) (val/maxPriceSell*255),100,100));
            butttestVal.setTextSize(12.0f);
            butttestVal.setPadding(5, 0, 5, 0);
            butttestVal.setText(Math.round(val*priceTe)*100/100+"");
            butttestVal.setMaxHeight(20);
            ll.addView(butttestVal);

            widgetScrLay.addView(ll);
        }
        for (int i = 0; i<priceBuy.size(); i++){
            String priceText = priceBuy.get(i).getPrice();
            double priceTe = Double.parseDouble(priceText);
            LinearLayout ll = new LinearLayout(this);
            ll.setOrientation(LinearLayout.HORIZONTAL);
            MyWidgetButton butttest = new MyWidgetButton(this);
            butttest.setTextColor(Color.GREEN);
            butttest.setBackgroundColor(Color.BLACK);
            butttest.setId(20000+i);
            butttest.setOnClickListener(onClickListBook);

            for(int j=0; j<listMyOrdes.size(); j++){
                Order myItemOrder = listMyOrdes.get(j);
                String tupeOrder = myItemOrder.getSide().name();
                if (tupeOrder.equals("BUY")){
                    if (priceText.equals(myItemOrder.getPrice())){
                        butttest.setBackgroundColor(Color.rgb(255, 215, 0));
                    }
                }
            }


            butttest.setText(priceTe+"");
            butttest.setTextSize(12.0f);
            butttest.setMaxHeight(20);
            butttest.setPadding(5, 0, 5, 0);
            ll.addView(butttest);

            double val = Double.parseDouble(priceBuy.get(i).getQty());
            MyWidgetButton butttestVal = new MyWidgetButton(this);
            butttestVal.setTextColor(Color.rgb(255,100,100));
            butttestVal.setTextColor(Color.BLACK);
            butttestVal.setBackgroundColor(Color.rgb(100,(int) (val/maxPriceBuy*255),100));
            butttestVal.setTextSize(12.0f);
            butttestVal.setPadding(5, 0, 5, 0);
            butttestVal.setText(Math.round(val*priceTe*100)/100+"");
            butttestVal.setMaxHeight(20);
            ll.addView(butttestVal);

            widgetScrLay.addView(ll);
        }

    }

    public void showDiologOrder(Order order){
        try{wm.removeView(trChangeOrder);}catch(Exception e){};
        if (order == null) return;

        wm.addView(trChangeOrder, changemyOrder);
        TextView textTitle = (TextView) trChangeOrder.findViewById(R.id.textViewTitleMyOrder);
        textTitle.setText("Order:"+order.getClientOrderId());
        if(order.getSide().toString().equals("BUY")) textTitle.setBackgroundColor(Color.rgb(200,255,200));
        if(order.getSide().toString().equals("SELL")) textTitle.setBackgroundColor(Color.rgb(255,200,200));
        Button buttoncancel = (Button) trChangeOrder.findViewById(R.id.buttonCancel);
        Button buttonCancelOrder = (Button) trChangeOrder.findViewById(R.id.buttonCancelOrder);
        Button buttonDownOrder = (Button) trChangeOrder.findViewById(R.id.buttonDown);
        Button buttonUpOrder = (Button) trChangeOrder.findViewById(R.id.buttonUp);
        EditText colperEditText = (EditText) trChangeOrder.findViewById(R.id.editTextColPer);
        colperEditText.setText("1");

        View.OnClickListener onclickListenrmyOrder = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.buttonCancel:
                        try{wm.removeView(trChangeOrder);}catch(Exception e){};
                        break;
                    case R.id.buttonCancelOrder:
                        ActionBot_CancelOrder orderAction = new ActionBot_CancelOrder(order);
                        Keeper.getInstance().addAction(orderAction, Widjet.this);
                        try{wm.removeView(trChangeOrder);}catch(Exception e){};
                        break;
                    case R.id.buttonDown:
                        BinanceState binanceState = BinanceState.getInstance();
                        BotFunction botFunction = new BotFunction(Widjet.this);
                        if(colperEditText.getText().toString().equals("")) colperEditText.setText(1+"");
                        botFunction.setColPer(Integer.parseInt(colperEditText.getText()+""));
                        if (order.getSide() == OrderSide.BUY){

                            botFunction.downOrder(order, binanceState.getPriceBuy());
                        }
                        if (order.getSide() == OrderSide.SELL){
                            botFunction.downOrder(order, binanceState.getPriceSell());
                        }

                        try{wm.removeView(trChangeOrder);}catch(Exception e){};
                        break;
                    case R.id.buttonUp:
                        BinanceState binanceState2 = BinanceState.getInstance();
                        BotFunction botFunction2 = new BotFunction(Widjet.this);
                        if(colperEditText.getText().toString().equals("")) colperEditText.setText(1+"");
                        botFunction2.setColPer(Integer.parseInt(colperEditText.getText()+""));
                        if (order.getSide() == OrderSide.BUY){
                            botFunction2.upOrder(order, binanceState2.getPriceBuy());
                        }
                        if (order.getSide() == OrderSide.SELL){
                            botFunction2.upOrder(order, binanceState2.getPriceSell());
                        }

                        try{wm.removeView(trChangeOrder);}catch(Exception e){};
                        break;

                }

            }
        };
        buttoncancel.setOnClickListener(onclickListenrmyOrder);
        buttonCancelOrder.setOnClickListener(onclickListenrmyOrder);
        buttonDownOrder.setOnClickListener(onclickListenrmyOrder);
        buttonUpOrder.setOnClickListener(onclickListenrmyOrder);

        LinearLayout llOrderInfo = (LinearLayout) trChangeOrder.findViewById(R.id.layOrderInfo);
        llOrderInfo.removeAllViews();

        TextView textOrder_Side = new TextView(this);
        textOrder_Side.setText(order.getSide().toString()+" "+order.getSymbol());
        textOrder_Side.setTextColor(Color.BLACK);
        llOrderInfo.addView(textOrder_Side);

        TextView textOrder_Value = new TextView(this);
        textOrder_Value.setText("Value: "+order.getExecutedQty()+"("+Double.parseDouble(order.getExecutedQty())*Double.parseDouble(order.getPrice())+")"+"/"+order.getOrigQty()+"("+Double.parseDouble(order.getOrigQty())*Double.parseDouble(order.getPrice())+")");
        textOrder_Value.setTextColor(Color.BLACK);
        llOrderInfo.addView(textOrder_Value);

        TextView textOrder_Price = new TextView(this);
        textOrder_Price.setText("Price: "+order.getPrice());
        textOrder_Price.setTextColor(Color.BLACK);
        llOrderInfo.addView(textOrder_Price);

        TextView textOrder_Data = new TextView(this);
        textOrder_Data.setText("Time: "+ Preobr.getDataMyFormat(order.getTime()));
        textOrder_Data.setTextColor(Color.BLACK);
        llOrderInfo.addView(textOrder_Data);



    }

    public void showDiologAddOrder(OrderBookEntry sellorderbook, String typeAdd){
        try{wm.removeView(trAddOrder);}catch(Exception e){};
        if (sellorderbook == null) return;
        AddOrder newAddOrder = new AddOrder(Double.parseDouble(sellorderbook.getPrice()), Double.parseDouble(sellorderbook.getQty()), typeAdd, BinanceState.getInstance().getTekPara());
        TextView textTitle = (TextView) trAddOrder.findViewById(R.id.textTitleAdd);


        textTitle.setText("Add Order: "+typeAdd);

        LinearLayout llOrderInfo = (LinearLayout) trAddOrder.findViewById(R.id.layAdInfo);
        llOrderInfo.removeAllViews();


        TextView textOrder_Value = new TextView(this);
        textOrder_Value.setText("Value: "+sellorderbook.getQty()+"("+Double.parseDouble(sellorderbook.getQty())*Double.parseDouble(sellorderbook.getPrice())+")");
        textOrder_Value.setTextColor(Color.BLACK);
        llOrderInfo.addView(textOrder_Value);


        if(typeAdd.equals("BUY")) textTitle.setBackgroundColor(Color.rgb(200,255,200));
        if(typeAdd.equals("SELL")) textTitle.setBackgroundColor(Color.rgb(255,200,200));
        Button buttoncancel = (Button) trAddOrder.findViewById(R.id.buttonCancelad);
        Button goOrderAddLimit = (Button) trAddOrder.findViewById(R.id.goOrderAddLimit);
        Button buttonmaxVal = (Button) trAddOrder.findViewById(R.id.buttonmaxVal);

        EditText editTextVal = (EditText)  trAddOrder.findViewById(R.id.editTextVal);
        editTextVal.setText(sellorderbook.getQty());


        editTextVal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                System.out.println("eddddddde"+editTextVal.getText());
                if(editTextVal.getText().toString().equals(""))return;
                textOrder_Value.setText("Value: "+editTextVal.getText()+"("+Double.parseDouble(editTextVal.getText().toString())*Double.parseDouble(sellorderbook.getPrice())+")");
                newAddOrder.setValue(Double.parseDouble(editTextVal.getText()+""));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        View.OnClickListener onclickListenrmyOrder = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.buttonCancelad:
                        try{wm.removeView(trAddOrder);}catch(Exception e){};
                        break;
                    case R.id.buttonmaxVal:
                        editTextVal.setText(Math.floor(75/Double.parseDouble(sellorderbook.getPrice()))+"");
                        break;
                    case R.id.goOrderAddLimit:
                        ActionBot_LimitOrder orderAction2 = new ActionBot_LimitOrder(newAddOrder);
                        Keeper.getInstance().addAction(orderAction2, Widjet.this);
                        try{wm.removeView(trAddOrder);}catch(Exception e){};
                        break;

                }

            }
        };
        buttoncancel.setOnClickListener(onclickListenrmyOrder);
        goOrderAddLimit.setOnClickListener(onclickListenrmyOrder);
        buttonmaxVal.setOnClickListener(onclickListenrmyOrder);

        TextView textOrder_Price = new TextView(this);
        textOrder_Price.setText("Price: "+sellorderbook.getPrice());
        textOrder_Price.setTextColor(Color.BLACK);
        llOrderInfo.addView(textOrder_Price);
        //выбор заявки для ее перестановки
        Spinner spinner = new Spinner(Widjet.this);

        List<Order> orders = BinanceState.getInstance().getMyOrdersTek();
        if (orders.size()>0){
            ArrayList<String> arrayList = new ArrayList<>();
            for(int i = 0; i<orders.size(); i++){
                Order orderMy = orders.get(i);
                arrayList.add(orderMy.getSide()+":"+orderMy.getPrice());
            }
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Widjet.this, android.R.layout.simple_spinner_item, arrayList);
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(arrayAdapter);



            LinearLayout linlayPerestanov = new LinearLayout(this);
            linlayPerestanov.setOrientation(LinearLayout.HORIZONTAL);
            Button perestanovButton = new Button(this);
            perestanovButton.setText(R.string.pereOrder);

            perestanovButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int itemPosition = spinner.getSelectedItemPosition();
                    new BotFunction(Widjet.this).perestanovOrder(orders.get(itemPosition), sellorderbook);
                    try{wm.removeView(trAddOrder);}catch(Exception e){};
                }
            });

            linlayPerestanov.addView(spinner);
            linlayPerestanov.addView(perestanovButton);
            llOrderInfo.addView(linlayPerestanov);
        }





        wm.addView(trAddOrder, addmyOrder);

    }

    public void showDopFunctionKeeper(Keeper keep){
        Toast.makeText(this, "keep", Toast.LENGTH_SHORT).show();
        try{wm.removeView(trWidgetDopFunctio);}catch(Exception e){};
        if (keep == null) return;

        wm.addView(trWidgetDopFunctio, dopFunction);

        Button buttonCancelDop = (Button) trWidgetDopFunctio.findViewById(R.id.cancelDop);
        buttonCancelDop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{wm.removeView(trWidgetDopFunctio);}catch(Exception e){};
            }
        });

    }

    @Override
    public void onDestroy() {
        ObservableSave.getObs().removeModel(this);
        super.onDestroy();
    }

    @Override
    public void doUpdate(int arg) {
        argSt = arg;
        //Обновить виджет;
        System.out.println(BinanceState.getInstance().toString());
        Intent intet = new Intent(this,Widjet.class);
        switch (arg){
            case -1:
                this.startService(intet);
                break;
            case 1 :
                this.startService(intet);
                break;
            case 2:
                this.startService(intet);
                break;
            case 3:     //myOrder
                this.startService(intet);
                break;
            case 4:     //listBot
                this.startService(intet);
                break;

        }
    }
}
