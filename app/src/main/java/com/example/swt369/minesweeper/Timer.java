package com.example.swt369.minesweeper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;

/**
 * Created by swt369 on 2017/7/18.
 */

final class Timer extends View {
    private static final long MAX_TIME_SECOND = 999;
    private Bitmap[] bitmapForNum = new Bitmap[10];
    private Handler handler;
    private long startTime;
    private int width = 0;
    private int height = 0;
    private Rect[] rects = new Rect[3];
    private Rect rectBitmap;
    private boolean isPause;
    private ThreadForTimer thread;
    Timer(Context context,@Nullable Bitmap[] bitmapForNum,Handler handler) {
        super(context);
        startTime = System.currentTimeMillis();
        this.bitmapForNum = bitmapForNum;
        this.handler = handler;
        thread = new ThreadForTimer();
        thread.start();
    }

    void initializeSize(){
        this.width = getWidth();
//        Log.i("width",String.valueOf(width));
        this.height = getHeight();
//        Log.i("height",String.valueOf(height));
        rects[0] = new Rect(0,0,width / 3,height);
        rects[1] = new Rect(width / 3,0,width / 3 * 2,height);
        rects[2] = new Rect(width / 3 * 2,0,width,height);
        rectBitmap = new Rect(0,0,bitmapForNum[0].getWidth(),bitmapForNum[0].getHeight());
        isPause = false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(width == 0 || height == 0){
            return;
        }
        drawTimes(canvas, (System.currentTimeMillis() - startTime) / 1000);
    }

    private void drawTimes(Canvas canvas, long timeSecond){
        if(timeSecond > MAX_TIME_SECOND){
            timeSecond = MAX_TIME_SECOND;
        }
        int digit = 100;
        int drawed = 0;
        while (digit > 0){
            int num = (int)(timeSecond / digit);
            timeSecond %= digit;
            digit /= 10;
            Bitmap bitmap = bitmapForNum[num];
            if(bitmap != null){
                canvas.drawBitmap(
                        bitmap,
                        rectBitmap,
                        rects[drawed],
                        null);
            }
            drawed++;
        }
    }

    private class ThreadForTimer extends Thread{

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()){
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(!isPause){
                    Message m = handler.obtainMessage();
                    m.what = Code.CODE_INVALIDATE;
                    handler.sendMessage(m);
                }
            }
        }
    }
}
