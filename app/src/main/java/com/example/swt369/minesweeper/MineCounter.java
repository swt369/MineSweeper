package com.example.swt369.minesweeper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Handler;

/**
 * Created by swt369 on 2017/7/18.
 */

class MineCounter extends CounterView {
    private final Handler handler;
    public MineCounter(Context context, Bitmap[] bitmapForNum, Handler handler) {
        super(context, bitmapForNum);
        this.handler = handler;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(width != 0 && height != 0){
            int delta = Brick.mineCount - Brick.flagCount;
            drawNumber(canvas,delta >= 0 ? delta : 0);
        }
    }


}
