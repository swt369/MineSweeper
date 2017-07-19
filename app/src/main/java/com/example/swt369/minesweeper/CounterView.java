package com.example.swt369.minesweeper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;

/**
 * Created by swt369 on 2017/7/18.
 * 用于构建显示数字的View
 */

abstract class CounterView extends View{
    private static final int MAX_NUM = 999;
    Bitmap[] bitmapForNum = new Bitmap[10];
    int width = 0;
    int height = 0;
    Rect[] rects = new Rect[3];
    Rect rectBitmap;

    public CounterView(Context context,Bitmap[] bitmapForNum) {
        super(context);
        this.bitmapForNum = bitmapForNum;
    }

    void initializeSize() {
        this.width = getWidth();
        this.height = getHeight();
        rects[0] = new Rect(0, 0, width / 3, height);
        rects[1] = new Rect(width / 3, 0, width / 3 * 2, height);
        rects[2] = new Rect(width / 3 * 2, 0, width, height);
        rectBitmap = new Rect(0, 0, bitmapForNum[0].getWidth(), bitmapForNum[0].getHeight());
        invalidate();
    }

    @Override
    protected abstract void onDraw(Canvas canvas);

    void drawNumber(Canvas canvas, int number) {
        if(number > MAX_NUM){
            number = MAX_NUM;
        }
        int digit = 100;
        int drew = 0;
        while (digit > 0) {
            int num = number / digit;
            number %= digit;
            digit /= 10;
            Bitmap bitmap = bitmapForNum[num];
            if (bitmap != null) {
                canvas.drawBitmap(
                        bitmap,
                        rectBitmap,
                        rects[drew],
                        null);
            }
            drew++;
        }
    }
}