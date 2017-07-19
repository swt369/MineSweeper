package com.example.swt369.minesweeper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;

/**
 * Created by swt369 on 2017/7/18.
 * 显示游戏时间
 */

class Timer extends CounterView {
    private Handler handler;
    private long startTime;
    private boolean isPause;

    Timer(Context context,Bitmap[] bitmapForNum,Handler handler) {
        super(context,bitmapForNum);
        startTime = System.currentTimeMillis();
        this.handler = handler;
        isPause = false;
        ThreadForTimer thread = new ThreadForTimer();
        thread.start();
    }

    int getTime(){
        return (int)((System.currentTimeMillis() - startTime) / 1000);
    }

    void resetTime(){
        startTime = System.currentTimeMillis();
        resume();
        invalidate();
    }

    void pause(){
        isPause = true;
    }

    void resume(){
        isPause = false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(width != 0 && height != 0){
            drawNumber(canvas,(int)((System.currentTimeMillis() - startTime) / 1000));
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
                    m.what = Code.CODE_INVALIDATE_TIMER;
                    handler.sendMessage(m);
                }
            }
        }
    }
}
