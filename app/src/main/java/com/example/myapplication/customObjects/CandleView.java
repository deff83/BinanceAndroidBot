package com.example.myapplication.customObjects;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.provider.ContactsContract;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Toast;

import com.binance.api.client.domain.market.Candlestick;
import com.example.myapplication.Widjet;
import com.example.myapplication.binance.BinanceState;
import com.example.myapplication.binance.MaxRectCandle;
import com.example.myapplication.binance.Pokazatel;

import java.util.List;

public class CandleView extends View {
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint paint, mBitmapPaint;
    private float canvasSize;
    private final int horizontalCountOfCells, verticalCountOfCells;
    private final ScaleGestureDetector scaleGestureDetector;
    private final int viewSize;
    private float mScaleFactor;
    private List<Candlestick> listCandleSt;
    private Long startTimeView = 0l, endTimeView = 0l;
    private Long period = 1000*60*5l;

    public List<Candlestick> getListCandleSt() {
        return listCandleSt;
    }

    public void setListCandleSt(List<Candlestick> listCandleSt) {
        this.listCandleSt = listCandleSt;
    }

    public CandleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //размер игрового поля
        horizontalCountOfCells =10;
        verticalCountOfCells =10;
        mScaleFactor=1f;//значение зума по умолчанию
        viewSize=(int)convertDpToPixel(300, context);
        canvasSize=(int)(viewSize*mScaleFactor);//определяем размер канваса
        //в xml разметке позднее пропишем размер вьюхи равный 300dp

        canvasSize=(int)convertDpToPixel(300, context);


        scaleGestureDetector=new ScaleGestureDetector(context, new MyScaleGestureListener());

        mBitmapPaint = new Paint(Paint.DITHER_FLAG);

        //определяем параметры кисти, которой будем рисовать сетку и атомы
        paint =new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(0xffff0505);
        //paint.setStrokeWidth(5f);
        //paint.setStyle(Paint.Style.STROKE);
       // paint.setStrokeJoin(Paint.Join.ROUND);
       // paint.setStrokeCap(Paint.Cap.ROUND);

        //рисуем сетку
        /*for(int x=0;x< horizontalCountOfCells +1;x++)
            mCanvas.drawLine((float)x* canvasSize / horizontalCountOfCells, 0, (float)x* canvasSize / horizontalCountOfCells, canvasSize, paint);
        for(int y=0;y< verticalCountOfCells +1;y++)
            mCanvas.drawLine(0, (float)y* canvasSize / verticalCountOfCells, canvasSize, (float)y* canvasSize / verticalCountOfCells, paint);
    */
    }


    public void setCandles(){

        if(mCanvas==null) {
            mBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
        }

        Paint paintClear = new Paint();
        paintClear.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        Rect rectClear=new Rect(0,0,mBitmap.getWidth(),mBitmap.getHeight());
        mCanvas.drawRect(rectClear,paintClear);

        period = BinanceState.getInstance().getLongInterval();
        //canvasSize = ;
        //mScaleFactor = 1f;
        if (endTimeView==0l) endTimeView = listCandleSt.get(listCandleSt.size()-1).getCloseTime();
        if (startTimeView==0l) startTimeView = endTimeView - period*10;


        //test
        long nachTime = startTimeView;
        long endTime = endTimeView;



        MaxRectCandle maxRectCandle = Pokazatel.getInstance().getMaxPrice(listCandleSt, nachTime, endTime);
        double maxPri = maxRectCandle.getMaxPrice();
        double minPri = maxRectCandle.getMinPrice();
        double widthPri = maxPri - minPri;



        long widthTime = endTime - nachTime;

        for (int i = 0; i <listCandleSt.size() ; i++) {
            Candlestick candlestickItem = listCandleSt.get(i);
            //System.out.println("tessssssssdfdg"+":"+i);
            //mCanvas.drawCircle(100, 100, 50, paint);
            long timeOpen = candlestickItem.getOpenTime();
            long timeClose = candlestickItem.getCloseTime();
            if (timeOpen>=nachTime && timeClose<=endTime) {

                double open = Double.parseDouble(candlestickItem.getOpen());
                double close = Double.parseDouble(candlestickItem.getClose());
                double high = Double.parseDouble(candlestickItem.getHigh());
                double low = Double.parseDouble(candlestickItem.getLow());
                if (open > close) {
                    paint.setColor(0xffff0000);
                }
                if (open < close) {
                    paint.setColor(0xff00ff00);
                }
                if (open == close) {
                    paint.setColor(0xffffff00);
                }
                //paint.setStyle(Paint.Style.FILL);
                //System.out.println("tessssssssdfdg"+":"+(int)((timeOpen-nachTime)*getWidth()/widthTime)+":"+(int)((maxPri-open)*getWidth()/widthPri)+":"+(int)((timeClose-nachTime)*getWidth()/widthTime)+":"+(int)((maxPri-close)*getWidth()/widthPri));
                Rect rect = new Rect((int)((timeOpen-nachTime)*getWidth()/widthTime), (int)((maxPri-open)*getHeight()/widthPri),(int)((timeClose-nachTime)*getWidth()/widthTime),(int)((maxPri-close)*getHeight()/widthPri));
                //Rect rect = new Rect(200, 150, 400, 200);
                //mCanvas.drawCircle(10, 100, 50, paint);
                mCanvas.drawRect(rect, paint);
                float seredina = (float)(((int)((timeClose-nachTime)*getWidth()/widthTime) + (int)((timeOpen-nachTime)*getWidth()/widthTime))/2);
                mCanvas.drawLine(seredina,(float) ((maxPri-high)*getHeight()/widthPri),seredina,(float) ((maxPri-low)*getHeight()/widthPri), paint);
            }


        }


    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        //canvas.scale(mScaleFactor, mScaleFactor);//зумируем канвас
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.restore();
    }
    private long point_rast = 0l;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        System.out.println("event:"+event.getAction());



        if (event.getAction()== MotionEvent.ACTION_DOWN){
            float x = event.getRawX();
            long widthtime = endTimeView - startTimeView;
            point_rast = (long) Math.floor((x*widthtime)/getWidth());
        }

        if (event.getAction()== MotionEvent.ACTION_MOVE){
            float x = event.getRawX();
            System.out.println("getRawX:"+x);
            if (point_rast != 0l){
                long widthtime = endTimeView - startTimeView;
                //long point_new = (long) Math.floor((x*widthtime)/getWidth())+ startTimeView;
                long point = point_rast + startTimeView;
                startTimeView = (long) Math.floor(point-((widthtime*x)/getWidth()));
                endTimeView = (long) Math.floor(point+((widthtime*(getWidth()-x))/getWidth()));
                setCandles();
                invalidate();
                point_rast = (long) Math.floor((x*widthtime)/getWidth());
            }




        }


        scaleGestureDetector.onTouchEvent(event);



        return true;
    }



    //переводим dp в пиксели
    public float convertDpToPixel(float dp,Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * (metrics.densityDpi/160f);
    }

    //внутренний класс
    public class MyScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        //обрабатываем "щипок" пальцами
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            float scaleFactor=scaleGestureDetector.getScaleFactor();//получаем значение зума относительно предыдущего состояния
            //получаем координаты фокальной точки - точки между пальцами
            float focusX=scaleGestureDetector.getFocusX();
            float focusY=scaleGestureDetector.getFocusY();
            //следим чтобы канвас не уменьшили меньше исходного размера и не допускаем увеличения больше чем в 2 раза
            //if(mScaleFactor*scaleFactor>1 && mScaleFactor*scaleFactor<2){
                //mScaleFactor *= scaleGestureDetector.getScaleFactor();
                //canvasSize =viewSize*mScaleFactor;//изменяем хранимое в памяти значение размера канваса
                //используется при расчетах
                //по умолчанию после зума канвас отскролит в левый верхний угол.
                //Скролим канвас так, чтобы на экране оставалась
                //область канваса, над которой был жест зума
                //Для получения данной формулы достаточно школьных знаний математики (декартовы координаты).
                /*int scrollX=(int)((getScrollX()+focusX)*scaleFactor-focusX);
                scrollX=Math.min( Math.max(scrollX, 0), (int) canvasSize -viewSize);
                int scrollY=(int)((getScrollY()+focusY)*scaleFactor-focusY);
                scrollY=Math.min( Math.max(scrollY, 0), (int) canvasSize -viewSize);
                scrollTo(scrollX, scrollY);*/

            //}
            //вызываем перерисовку принудительно
            long widthtime = endTimeView - startTimeView;

            long point_rast = (long) Math.floor((focusX*widthtime)/getWidth());


            //long posle_widthtime = (long) Math.floor(widthtime *scaleFactor);
            if (scaleFactor> 1) startTimeView += (long) Math.floor(point_rast/10);
            if (scaleFactor< 1) startTimeView -= (long) Math.floor(point_rast/10);

            if (scaleFactor> 1) endTimeView -= (long) Math.floor((widthtime-point_rast)/10);
            if (scaleFactor< 1) endTimeView += (long) Math.floor((widthtime-point_rast)/10);

            setCandles();
            invalidate();
            return true;
        }


    }


}
