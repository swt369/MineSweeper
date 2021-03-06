package com.example.swt369.minesweeper;

import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by swt369 on 2017/7/17.
 * 控制器，负责接收触摸事件
 */

final class GameController implements View.OnTouchListener {
    private Handler mHandler;
    private GameView mView;

    GameController(Handler handler, GameView view){
        this.mHandler = handler;
        this.mView = view;
    }
    private boolean isAlive = true;
    private long mLastClickTime;
    private Thread mLongTouchThread;
    private float mLastScreenX;
    private float mLastScreenY;
    private Brick mLastClickedBrick;
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(!isAlive){
            return true;
        }
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                mLastScreenX = event.getX();
                mLastScreenY = event.getY();
                mLastClickedBrick = mView.getBrickByPixelXY((int)mLastScreenX,(int)mLastScreenY);
                if(mLastClickedBrick == null){
                    return false;
                }
                mLastClickTime = System.currentTimeMillis();
                mLongTouchThread = new LongTouchThread(mLastClickedBrick);
                mHandler.postDelayed(mLongTouchThread,Settings.LONG_CLICK_TIME_IN_MILLS);
                return true;
            case MotionEvent.ACTION_UP:
                if(mLastClickedBrick == null){
                    return false;
                }
                if(System.currentTimeMillis() - mLastClickTime < Settings.LONG_CLICK_TIME_IN_MILLS){
                    mHandler.removeCallbacks(mLongTouchThread);
                    if(!isMoved(event.getX(),event.getY()) && mLastClickedBrick.clicked()){
                        mView.invalidate();
                    }
                }
                return true;
        }
        return false;
    }

    void setAlive(boolean isAlive){
        this.isAlive = isAlive;
    }

    boolean isAlive(){
        return isAlive;
    }

    private boolean isMoved(float curX,float curY){
        float offsetX = Math.abs(curX - mLastScreenX);
        float offsetY = Math.abs(curY - mLastScreenY);
        return offsetX >= 100 || offsetY >= 100;
    }

    private class LongTouchThread extends Thread{
        Brick brick;
        private LongTouchThread(Brick brick){
            this.brick = brick;
        }

        @Override
        public void run() {
            if(brick.reverseFlag()){
                mView.invalidate();
            }
        }
    }

    private static class Settings{
        private static final long LONG_CLICK_TIME_IN_MILLS = 400;
    }
}
