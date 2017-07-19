package com.example.swt369.minesweeper;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;

/**
 * Created by swt369 on 2017/7/17.
 * 视图，用于绘制砖块
 */

final class GameView extends View{
    private Brick[][] bricks;
    private int ROW_COUNT;
    private int COLUMN_COUNT;
    private final int MAX_PIXEL_X;
    private final int MAX_PIXEL_Y;
    private int BRICKS_TOP;
    private int BRICKS_LEFT;
    private int BRICK_LENGTH;
    GameView(Context context,Brick[][] bricks) {
        super(context);
        this.bricks = bricks;

        MAX_PIXEL_X = context.getResources().getDisplayMetrics().widthPixels;
        MAX_PIXEL_Y = (int)(context.getResources().getDisplayMetrics().heightPixels * 0.7f);

        BRICKS_TOP = 0;

        initializeSize();
    }

    private void initializeSize(){
        ROW_COUNT = bricks.length;
        COLUMN_COUNT = bricks[0].length;
        int length_x = (int)(MAX_PIXEL_X * (1 - Settings.BRICKS_LEFT_RATIO - Settings.BRICKS_RIGHT_RATIO) / COLUMN_COUNT);
        int length_y = (int)(MAX_PIXEL_Y * 0.95f / ROW_COUNT);
        BRICK_LENGTH = length_x < length_y ? length_x : length_y;

        BRICKS_LEFT = (MAX_PIXEL_X - BRICK_LENGTH * COLUMN_COUNT) / 2;
    }

    Brick getBrickByPixelXY(int x,int y){
        int column = (x - BRICKS_LEFT) / BRICK_LENGTH;
        int row = (y - BRICKS_TOP) / BRICK_LENGTH;
        if(row >= 0 && row < bricks.length && column >= 0 && column < bricks[0].length){
            return bricks[row][column];
        }else {
            return null;
        }
    }

    synchronized void refreshBricks(Brick[][] bricks){
        this.bricks = bricks;
        initializeSize();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBricks(canvas);
    }

    private void drawBricks(Canvas canvas){
        for(int i = 0 ; i < ROW_COUNT ; i++)
            for(int j = 0 ; j < COLUMN_COUNT ; j++){
                bricks[i][j].drawBrick(canvas,BRICK_LENGTH,BRICKS_LEFT,BRICKS_TOP);
            }
    }

    private static class Settings{
        private static final float BRICKS_LEFT_RATIO = 0.05f;
        private static final float BRICKS_RIGHT_RATIO = 0.05f;
    }
}
