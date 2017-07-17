package com.example.swt369.minesweeper;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;

/**
 * Created by swt369 on 2017/7/17.
 */

final class GameView extends View{
    private final Context context;
    private Brick[][] bricks;
    private final int ROW_COUNT;
    private final int COLUMN_COUNT;
    private final int MAX_PIXEL_X;
    private final int MAX_PIXEL_Y;
    private final int BRICKS_TOP;
    private final int BRICKS_LEFT;
    private final int BRICK_LENGTH;
    GameView(Context context,Brick[][] bricks) {
        super(context);
        this.context = context;
        this.bricks = bricks;

        ROW_COUNT = bricks.length;
        COLUMN_COUNT = bricks[0].length;

        MAX_PIXEL_X = context.getResources().getDisplayMetrics().widthPixels;
        MAX_PIXEL_Y = (int)(context.getResources().getDisplayMetrics().heightPixels * 0.6f);

        int length_x = (int)(MAX_PIXEL_X * (1 - Settings.BRICKS_LEFT_RATIO - Settings.BRICKS_RIGHT_RATIO) / COLUMN_COUNT);
        int length_y = (int)(MAX_PIXEL_Y * 1.0f / ROW_COUNT);
        BRICK_LENGTH = length_x < length_y ? length_x : length_y;

        BRICKS_LEFT = (MAX_PIXEL_X - BRICK_LENGTH * COLUMN_COUNT) / 2;
        BRICKS_TOP = 0;

    }

    Brick getBrickByPixelXY(int x,int y){
        int row = (x - BRICKS_LEFT) / BRICK_LENGTH;
        int column = (y - BRICKS_TOP) / BRICK_LENGTH;
        if(row >= 0 && row < bricks.length && column >= 0 && column < bricks[0].length){
            return bricks[row][column];
        }else {
            return null;
        }
    }

    synchronized void refreshBricks(Brick[][] bricks){
        this.bricks = bricks;
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
        private static final float BRICKS_LEFT_RATIO = 0.1f;
        private static final float BRICKS_RIGHT_RATIO = 0.1f;
    }
}
