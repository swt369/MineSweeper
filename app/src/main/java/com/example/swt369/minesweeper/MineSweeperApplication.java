package com.example.swt369.minesweeper;

import android.app.Application;
import android.graphics.Bitmap;

/**
 * Created by swt369 on 2017/7/22.
 */

public final class MineSweeperApplication extends Application{
    public Bitmap[] bitmapsForCounter;

    @Override
    public void onCreate() {
        super.onCreate();
    }

}
