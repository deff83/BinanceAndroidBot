package com.example.myapplication;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
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
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.account.Account;
import com.binance.api.client.domain.account.AssetBalance;
import com.binance.api.client.domain.account.Order;
import com.binance.api.client.domain.market.Candlestick;
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
import com.example.myapplication.bot.MaxVol;
import com.example.myapplication.bot.Performer;
import com.example.myapplication.botOpiration.BotOpiration;
import com.example.myapplication.botOpiration.OpirationOrder;
import com.example.myapplication.botOpiration.PerestanovOpiration;
import com.example.myapplication.customObjects.CandleView;
import com.example.myapplication.customObjects.MyWidgetButton;
import com.example.myapplication.customObjects.PerCentPrices;
import com.example.myapplication.customObjects.Preobr;
import com.example.myapplication.customObjects.SavedAllPrice;
import com.example.myapplication.customObjects.SaverInstruct;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Widjet extends Service implements ModelObs {
    SharedPreferences pref;
    SharedPreferences.Editor editor = null;
    boolean boolPercentPokz = true;
    WindowManager wm;
    WindowManager.LayoutParams myParams, myParamsBalance, changemyOrder, botListwm, candleListwm,addmyOrder, dopFunction, myOrderList, izmPriceListwm, mySetting;
    LinearLayout tr, trBalance, trChangeOrder, trWidgetbot, trAddOrder, trWidgetDopFunctio, trMyOrder, trIzmPrice, trSetting, trCandle;
    LinearLayout widgetScrLay, settingLay, balanceLay, myOrderLay, layBotList;
    View.OnClickListener myOrderclickListener, onClickListBook;
    int argSt = 1;
    Comparator balanceComparator = new SortAsset();
    boolean settingbool = true;
    Handler handler;
    Spinner spinner;
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
        ////////////////////////////////начальные настройки////////////////////////////////////////
        String tekPara = pref.getString("tekPara", "COCOSUSDT");
        BinanceState.getInstance().setTekPara(tekPara);

        int tekCount= pref.getInt("tekCount", 20);
        BinanceState.getInstance().setTekCount(tekCount);

        //is Balance
        boolean isBalance = pref.getBoolean("isBalance", true);
        BinanceState.getInstance().setMyBalance(isBalance);
        ///////////////////////////////////////////////////////////////////////////////////////
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

        mySetting = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_OVERSCAN,
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
        candleListwm = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        izmPriceListwm = new WindowManager.LayoutParams(
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

        izmPriceListwm.gravity = Gravity.BOTTOM;

        izmPriceListwm.x = 120;

        changemyOrder.gravity = Gravity.CENTER;


        myParamsBalance.gravity = Gravity.LEFT | Gravity.BOTTOM;
        myParams.gravity = Gravity.RIGHT;

        candleListwm.gravity = Gravity.LEFT | Gravity.TOP;

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
        LayoutInflater inflater8 = LayoutInflater.from(this);
        trIzmPrice = (LinearLayout) inflater8.inflate(R.layout.layout_izm_pricez, null);
        LayoutInflater inflater9 = LayoutInflater.from(this);
        trSetting = (LinearLayout) inflater9.inflate(R.layout.widjet_setiing, null);
        LayoutInflater inflater10 = LayoutInflater.from(this);
        trCandle = (LinearLayout) inflater10.inflate(R.layout.widjet_candles, null);






        Button butChange = (Button) tr.findViewById(R.id.buttonChange);
        widgetScrLay = (LinearLayout) tr.findViewById(R.id.widgetScrLay);
        settingLay = (LinearLayout)  trSetting.findViewById(R.id.settingLay);
        myOrderLay = (LinearLayout)  trMyOrder.findViewById(R.id.myOrderLay);
        balanceLay = (LinearLayout)  trBalance.findViewById(R.id.balanceLay);

        layBotList = (LinearLayout)  trWidgetbot.findViewById(R.id.layBotList);


        Button butGaf = (Button) tr.findViewById(R.id.buttonGrav);
        butGaf.setText(R.string.grav);
        butGaf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new BotFunction(Widjet.this).getCandlestickBars();
                BinanceState.getInstance().setCandles(true);
            }
        });

        butChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Widjet.this, "this", Toast.LENGTH_SHORT).show();
                //if (settingbool) {

                    showSetting();


                /*}else{
                    settingLay.removeAllViews();
                    settingbool = true;
                }*/
            }
        });
        wm.addView(trBalance, myParamsBalance);
        wm.addView(trWidgetbot, botListwm);
        wm.addView(trMyOrder, myOrderList);
        wm.addView(tr, myParams);

        wm.addView(trIzmPrice, izmPriceListwm);

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

                MyWidgetButton buttong = (MyWidgetButton) widgetScrLay.findViewById(idOrder) ;
                buttong.setBackgroundColor(Color.MAGENTA);

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
        ObservableSave.getObs().addModel(SavedAllPrice.getInstance());


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
        List<TickerPrice> tikPrice = binanceState.getTikPrice();

        List<PerCentPrices> listpercIzm = SavedAllPrice.getInstance().getListpercIzm();
        List<TickerPrice> tikPriceFind = Preobr.getInstance().getTiccker();
        MaxVol maxVol = BinanceState.getInstance().getMaxVol();
        List<Candlestick> liostCandle = BinanceState.getInstance().getCandlesticks();

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
            case 5:
                setTickAllPricez(tikPrice);
                break;
            case 7:
                setIzmPricez(listpercIzm);
                break;
            case 8:
                setFindParaChange(tikPriceFind);
                break;
            case 9:
                setSetCandleStick(liostCandle);
                break;
            case 10:
                setMaxVol(maxVol);
                break;

        }




        return super.onStartCommand(intent, flags, startId);
    }
    public void setTickAllPricez(List<TickerPrice> tikPrice){
        //все цены
    }

    public void setSetCandleStick(List<Candlestick> liostCandle){
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
   System.out.println("trertertert:");
        try{
            CandleView viewCandle = (CandleView) trCandle.findViewById(R.id.candle_view);
            viewCandle.setListCandleSt(liostCandle);
            viewCandle.setCandles();
            viewCandle.invalidate();
            System.out.println("trertertert:g");
        }catch(Exception e){
            System.out.println("trertertert:"+e.getMessage());
        }

        try {
        Button buttonCancel = (Button) trCandle.findViewById(R.id.buttonCancelXCandle);
        View.OnClickListener onclickListen = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BinanceState.getInstance().setCandles(false);
                try {
                    wm.removeView(trCandle);

                } catch (Exception e) {
                }
                ;
            }
        };
        buttonCancel.setOnClickListener(onclickListen);

        RelativeLayout rellay = (RelativeLayout) trCandle.findViewById(R.id.relativCandle);
        if (BinanceState.getInstance().isCandles()) {
            wm.addView(trCandle, candleListwm);
        }
        CandleView viewCandle = (CandleView) trCandle.findViewById(R.id.candle_view);


        viewCandle.post(new Runnable() {
            @Override
            public void run() {

                viewCandle.setListCandleSt(liostCandle);
                viewCandle.setCandles();
            }
        });

    }catch(Exception e){}

    }

    public  void setMaxVol(MaxVol maxVol){
        try {
            EditText editTextVal = (EditText) trAddOrder.findViewById(R.id.editTextVal);
            //editTextVal.setText(sellorderbook.getQty());
            editTextVal.setText(maxVol.getStrVol() + "");
            Button buttonmaxVal = (Button) trAddOrder.findViewById(R.id.buttonmaxVal);
            Button buttonmaxVal25 = (Button) trAddOrder.findViewById(R.id.buttonmaxVal25);
            Button buttonmaxVal50 = (Button) trAddOrder.findViewById(R.id.buttonmaxVal50);
            Button buttonmaxVal75 = (Button) trAddOrder.findViewById(R.id.buttonmaxVal75);
            Button buttonminVal = (Button) trAddOrder.findViewById(R.id.buttonminVal);
            buttonmaxVal.setEnabled(true);
            buttonmaxVal25.setEnabled(true);
            buttonmaxVal50.setEnabled(true);
            buttonmaxVal75.setEnabled(true);
            buttonminVal.setEnabled(true);
        }catch(Exception e){System.out.println("except setMaxVol:"+e.getMessage());}
    }



    public void showSetting(){
        try{wm.removeView(trSetting);}catch(Exception e){};
        settingLay.removeAllViews();
        Button bottonOk = (Button) trSetting.findViewById(R.id.buttonOk);

        bottonOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try{wm.removeView(trSetting);}catch(Exception e){};
                }
            }
        );

        EditText textFind = new EditText(Widjet.this);
        textFind.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String textch = textFind.getText()+"";
                new BotFunction(Widjet.this).changeFindPara(textch);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        spinner = new Spinner(Widjet.this);

        List<TickerPrice> ticcker = BinanceState.getInstance().getTikPrice();
        int selectitem = 0;
        ArrayList<String> arrayList = new ArrayList<>();
        for (int i=0; i<ticcker.size(); i++){
            String symbol = ticcker.get(i).getSymbol();
            arrayList.add(symbol);
            System.out.println("symbol:"+symbol);
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

        settingLay.addView(textFind);
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

        CheckBox check_myBalance = new CheckBox(Widjet.this);
        check_myBalance.setChecked(BinanceState.getInstance().isMyBalance());

        check_myBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean checked = check_myBalance.isChecked();
                System.out.println("isBalance "+checked);
                editor.putBoolean("isBalance", checked);
                editor.commit();
                BinanceState.getInstance().setMyBalance(checked);
                balanceLay.removeAllViews();
            }
        });

        settingLay.addView(check_myBalance);
        Button butGrav = new Button(Widjet.this);
        butGrav.setText(R.string.grav);
        butGrav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myParams.gravity == Gravity.RIGHT) {myParams.gravity = Gravity.LEFT;}
                else {myParams.gravity = Gravity.RIGHT;}
                wm.updateViewLayout(tr, myParams);
            }
        });
        settingLay.addView(butGrav);

        wm.addView(trSetting, mySetting);
    }

    public void setFindParaChange(List<TickerPrice> tikPriceFind){
        if (spinner != null){
            List<TickerPrice> ticcker = tikPriceFind;
            int selectitem = 0;
            ArrayList<String> arrayList = new ArrayList<>();
            for (int i=0; i<ticcker.size(); i++){
                String symbol = ticcker.get(i).getSymbol();
                arrayList.add(symbol);
                //System.out.println(symbol);

            }
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Widjet.this, android.R.layout.simple_spinner_item, arrayList);
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(arrayAdapter);
            spinner.setSelection(selectitem);

        }
    }

    public void setIzmPricez(List<PerCentPrices> listpercIz) {
        LinearLayout izmLay = (LinearLayout) trIzmPrice.findViewById(R.id.layoutIzmPrice);
        izmLay.removeAllViews();
        if (listpercIz.size() < 1) {
            MyWidgetButton butttest1 = new MyWidgetButton(this);
            butttest1.setTextSize(12.0f);
            butttest1.setBackgroundColor(Color.BLACK);
            butttest1.setPadding(5, 0, 5, 0);
            butttest1.setMaxHeight(20);
            butttest1.setText("none");

            izmLay.addView(butttest1);
            return;
        }

        if (boolPercentPokz) {
            MyWidgetButton butttestSkrit = new MyWidgetButton(this);
            butttestSkrit.setTextSize(12.0f);
            butttestSkrit.setBackgroundColor(Color.BLACK);
            butttestSkrit.setPadding(5, 0, 5, 0);
            butttestSkrit.setMaxHeight(20);
            butttestSkrit.setText(R.string.skrit);
            butttestSkrit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    izmLay.removeAllViews();
                    boolPercentPokz = false;
                    ObservableSave.getObs().update(7);
                }
            });
            izmLay.addView(butttestSkrit);
            for (int i = 0; i < listpercIz.size(); i++) {
                PerCentPrices perCentPrices = listpercIz.get(i);
                LinearLayout linllm = new LinearLayout(Widjet.this);
                linllm.setOrientation(LinearLayout.HORIZONTAL);

                MyWidgetButton butttest1 = new MyWidgetButton(this);
                butttest1.setTextSize(12.0f);
                butttest1.setBackgroundColor(Color.argb(90, 0, 0, 0));

                if (perCentPrices.getPercent() > 0)
                    butttest1.setTextColor(Color.rgb(100, 255, 100));
                if (perCentPrices.getPercent() < 0)
                    butttest1.setTextColor(Color.rgb(255, 100, 100));

                butttest1.setPadding(5, 0, 5, 0);
                butttest1.setMaxHeight(30);
                butttest1.setWidth(80);
                butttest1.setText(perCentPrices.getName() + "");

                butttest1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        BinanceState.getInstance().setTekPara(perCentPrices.getName());
                    }
                });


                MyWidgetButton butttest2 = new MyWidgetButton(this);
                butttest2.setTextSize(12.0f);
                butttest2.setBackgroundColor(Color.argb(90, 0, 0, 0));

                if (perCentPrices.getPercent() > 0)
                    butttest2.setTextColor(Color.rgb(100, 255, 100));
                if (perCentPrices.getPercent() < 0)
                    butttest2.setTextColor(Color.rgb(255, 100, 100));

                butttest2.setPadding(5, 0, 5, 0);
                butttest2.setMaxHeight(30);
                butttest2.setWidth(70);
                butttest2.setText(perCentPrices.getPercent() + "%");
                MyWidgetButton butttest3 = new MyWidgetButton(this);
                butttest3.setTextSize(12.0f);
                butttest3.setBackgroundColor(Color.argb(90, 0, 0, 0));

                if (perCentPrices.getPercent() > 0)
                    butttest3.setTextColor(Color.rgb(100, 255, 100));
                if (perCentPrices.getPercent() < 0)
                    butttest3.setTextColor(Color.rgb(255, 100, 100));

                butttest3.setPadding(5, 0, 5, 0);
                butttest3.setMaxHeight(30);
                butttest3.setText(perCentPrices.getPeriod() + "");
                butttest3.setWidth(40);
                linllm.addView(butttest1);
                linllm.addView(butttest2);
                linllm.addView(butttest3);
                System.out.println("izmLay");
                izmLay.addView(linllm);
            }
        }else{
            MyWidgetButton butttest1 = new MyWidgetButton(this);
            butttest1.setTextSize(12.0f);
            butttest1.setBackgroundColor(Color.BLACK);
            butttest1.setPadding(5, 0, 5, 0);
            butttest1.setMaxHeight(20);
            butttest1.setText("none");
            butttest1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolPercentPokz = true;
                    ObservableSave.getObs().update(7);
                }
            });

            izmLay.addView(butttest1);
            return;
        }
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
            System.out.println("Account balance: "+acc.getBalances());
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

        //текущая пара
        Button grav = (Button) tr.findViewById(R.id.buttonGrav);
        if (BinanceState.getInstance().getTekPara()!= null) {
            grav.setText(BinanceState.getInstance().getTekPara());
            grav.setTextSize(10);
        }
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
        //множитель
        double mnozhitel = 1.0;
        System.out.println("mnozhitel: "+Pokazatel.getInstance().getOtPara(BinanceState.getInstance().getTekPara()));
        mnozhitel = Pokazatel.getInstance().getMnozitel(BinanceState.getInstance().getTekPara());

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


            butttestVal.setText(Math.round(val*priceTe*mnozhitel)*100/100+"");
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
            butttestVal.setText(Math.round(val*priceTe*mnozhitel*100)/100+"");
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

        Button buttonBot = (Button) trChangeOrder.findViewById(R.id.buttonBot);

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
                    case R.id.buttonBot:
                        BotOpiration.getInstance().addOpirationOrder(new PerestanovOpiration(), Widjet.this);
                        break;
                }

            }
        };
        buttoncancel.setOnClickListener(onclickListenrmyOrder);
        buttonCancelOrder.setOnClickListener(onclickListenrmyOrder);
        buttonDownOrder.setOnClickListener(onclickListenrmyOrder);
        buttonUpOrder.setOnClickListener(onclickListenrmyOrder);
        buttonBot.setOnClickListener(onclickListenrmyOrder);

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
        textOrder_Data.setText("Time: "+ Preobr.getInstance().getDataMyFormat(order.getTime()));
        textOrder_Data.setTextColor(Color.BLACK);
        llOrderInfo.addView(textOrder_Data);



    }

    private double colMaxVol = 0.0;

    public void showDiologAddOrder(OrderBookEntry sellorderbook, String typeAdd){
        colMaxVol = 0.0;
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
        Button buttonmaxVal25 = (Button) trAddOrder.findViewById(R.id.buttonmaxVal25);
        Button buttonmaxVal50 = (Button) trAddOrder.findViewById(R.id.buttonmaxVal50);
        Button buttonmaxVal75 = (Button) trAddOrder.findViewById(R.id.buttonmaxVal75);
        Button buttonminVal = (Button) trAddOrder.findViewById(R.id.buttonminVal);

        EditText editTextVal = (EditText)  trAddOrder.findViewById(R.id.editTextVal);
        //editTextVal.setText(sellorderbook.getQty());
        editTextVal.setText(colMaxVol+"");

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
                        buttonmaxVal.setEnabled(false);
                        Runnable runa = new Runnable() {
                            @Override
                            public void run() {
                                Double maxVol = new BotFunction(Widjet.this).getMaxVolume(Double.parseDouble(sellorderbook.getPrice()), BinanceState.getInstance().getTekPara(), typeAdd, 100);

                            }
                        };
                        new Thread(runa).start();
                        break;
                    case R.id.buttonmaxVal25:
                        buttonmaxVal25.setEnabled(false);
                        Runnable runa25 = new Runnable() {
                            @Override
                            public void run() {
                                Double maxVol = new BotFunction(Widjet.this).getMaxVolume(Double.parseDouble(sellorderbook.getPrice()), BinanceState.getInstance().getTekPara(), typeAdd, 25);

                            }
                        };
                        new Thread(runa25).start();
                        break;
                    case R.id.buttonmaxVal50:
                        buttonmaxVal50.setEnabled(false);
                        Runnable runa50 = new Runnable() {
                            @Override
                            public void run() {
                                Double maxVol = new BotFunction(Widjet.this).getMaxVolume(Double.parseDouble(sellorderbook.getPrice()), BinanceState.getInstance().getTekPara(), typeAdd, 50);

                            }
                        };
                        new Thread(runa50).start();
                        break;
                    case R.id.buttonmaxVal75:
                        buttonmaxVal75.setEnabled(false);
                        Runnable runa75 = new Runnable() {
                            @Override
                            public void run() {
                                Double maxVol = new BotFunction(Widjet.this).getMaxVolume(Double.parseDouble(sellorderbook.getPrice()), BinanceState.getInstance().getTekPara(), typeAdd, 75);

                            }
                        };
                        new Thread(runa75).start();
                        break;
                    case R.id.buttonminVal:
                        buttonminVal.setEnabled(false);
                        Runnable runa0 = new Runnable() {
                            @Override
                            public void run() {
                                Double maxVol = new BotFunction(Widjet.this).getMaxVolume(Double.parseDouble(sellorderbook.getPrice()), BinanceState.getInstance().getTekPara(), typeAdd, 0);

                            }
                        };
                        new Thread(runa0).start();
                        break;
                    case R.id.goOrderAddLimit:
                        ActionBot_LimitOrder orderAction2 = new ActionBot_LimitOrder(newAddOrder, 0);
                        Keeper.getInstance().addAction(orderAction2, Widjet.this);
                        try{wm.removeView(trAddOrder);}catch(Exception e){};
                        break;

                }

            }
        };
        buttoncancel.setOnClickListener(onclickListenrmyOrder);
        goOrderAddLimit.setOnClickListener(onclickListenrmyOrder);
        buttonmaxVal.setOnClickListener(onclickListenrmyOrder);
        buttonmaxVal25.setOnClickListener(onclickListenrmyOrder);
        buttonmaxVal50.setOnClickListener(onclickListenrmyOrder);
        buttonmaxVal75.setOnClickListener(onclickListenrmyOrder);
        buttonminVal.setOnClickListener(onclickListenrmyOrder);

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
            LinearLayout linlayPerestanovSpec = new LinearLayout(this);
            linlayPerestanovSpec.setOrientation(LinearLayout.HORIZONTAL);
            //поставить с лучшей ценой
            Button perepostLuche = new Button(Widjet.this);
            perepostLuche.setText(R.string.postFollow);
            perepostLuche.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int itemPosition = spinner.getSelectedItemPosition();
                    new BotFunction(Widjet.this).perestanovOrderLuch(orders.get(itemPosition), sellorderbook);
                    try{wm.removeView(trAddOrder);}catch(Exception e){};
                }
            });
            //поставить с лучшей ценой перед всеми
            Button perepostPeredVsemi = new Button(Widjet.this);
            perepostPeredVsemi.setText(R.string.postNachalo);
            perepostPeredVsemi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int itemPosition = spinner.getSelectedItemPosition();
                    new BotFunction(Widjet.this).perestanovOrderPered(orders.get(itemPosition));
                    try{wm.removeView(trAddOrder);}catch(Exception e){};
                }
            });

            linlayPerestanovSpec.addView(perepostLuche);
            linlayPerestanovSpec.addView(perepostPeredVsemi);
            llOrderInfo.addView(linlayPerestanovSpec);

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
            case 5:     //tickPricezAll getAllPrices
                this.startService(intet);
                break;
            case 7:
                this.startService(intet);
                break;
            case 8: //findPara
                this.startService(intet);
                break;
            case 9: //сфтвдуЫешсл
                this.startService(intet);
                break;
            case 10: //maxVol
                this.startService(intet);
                break;
        }
    }
}
