package com.example.swt369.minesweeper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by swt369 on 2017/7/18.
 * 统计地雷个数并显示
 */

class MineCounter extends CounterView {
    public MineCounter(Context context, Bitmap[] bitmapForNum) {
        super(context, bitmapForNum);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(width != 0 && height != 0){
            int delta = Brick.mineCount - Brick.flagCount;
            drawNumber(canvas,delta >= 0 ? delta : 0);
        }
    }


}
