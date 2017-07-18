package com.example.swt369.minesweeper;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;

import java.util.Random;

/**
 * Created by swt369 on 2017/7/17.
 */

class Brick {
    private static final int[] OFFSET_X = new int[]{0,1,0,-1,-1,1,1,-1};
    private static final int[] OFFSET_Y = new int[]{-1,0,1,0,-1,-1,1,1};
    private int mX;
    private int mY;
    private BrickState mState;
    private boolean hasMine;
    private int surroundMineCount;
    private volatile Rect field;
    private Brick(int x,int y,boolean hasMine){
        this(x,y,hasMine,-1);
    }
    private Brick(int x,int y,boolean hasMine,int surroundMineCount){
        this.mX = x;
        this.mY = y;
        this.hasMine = hasMine;
        this.surroundMineCount = surroundMineCount;
        mState = NormalState.getInstance();
    }

    private static Bitmap[] bitmapsForNum;
    private static Bitmap bitmapForFlag;
    private static Bitmap bitmapForBomb;
    private static Bitmap bitmapForBrick;
    private static Paint paintForNum;
    private static Paint paintForBrick;
    static{
        paintForNum = new Paint();
        paintForNum.setTextSize(40);
        paintForBrick = new Paint();
    }

    static void setBitMaps(Bitmap[] bitmaps,@Nullable Bitmap bitmapForFlag,@Nullable Bitmap bitmapForBomb,@Nullable Bitmap bitmapForBrick){
        if(bitmaps.length == 10){
            Brick.bitmapsForNum = bitmaps;
        }
        Brick.bitmapForFlag = bitmapForFlag;
        Brick.bitmapForBomb = bitmapForBomb;
        Brick.bitmapForBrick = bitmapForBrick;
    }

    private static Handler handler = null;
    static void setHandler(Handler handler){
        Brick.handler = handler;
    }
    private static void sendMessageForUpdateMineCounter(){
        if(Brick.handler != null){
            Message m = handler.obtainMessage();
            m.what = Code.CODE_INVALIDATE_MINECOUNTER;
            handler.sendMessage(m);
        }
    }

    private static Brick[][] bricks;
    static int mineCount;
    static int flagCount;
    static Brick[][] initializeBricks(int rowCount,int columnCount,int mineCount){
        bricks = generateBricks(rowCount,columnCount,mineCount);
        Brick.mineCount = mineCount;
        Brick.flagCount = 0;
        return bricks;
    }
    private static Brick[][] generateBricks(int rowCount,int columnCount,int mineCount){
        Brick[][] bricks = new Brick[rowCount][columnCount];
        int[][] map = MapGenerator.generateMap(rowCount,columnCount,mineCount);
        for(int i = 0 ; i < rowCount ; i++)
            for(int j = 0 ; j < columnCount ; j++){
                if(map[i][j] == MapGenerator.MINE){
                    bricks[i][j] = new Brick(i,j,true);
                }else {
                    bricks[i][j] = new Brick(i,j,false,map[i][j]);
                }
            }
        return bricks;
    }

    private int getSurroundMineCount(){
        return surroundMineCount;
    }

    boolean clicked(){
        return mState.clicked(this);
    }

    boolean reverseFlag(){
        return mState.reverseFlag(this);
    }

    void drawBrick(Canvas canvas,int length,int left,int top){
        mState.drawBrick(canvas,this,length,left,top);
    }

    private interface BrickState{
        boolean clicked(Brick brick);
        boolean reverseFlag(Brick brick);
        void drawBrick(Canvas canvas, Brick brick, int length, int left, int top);
    }

    private static class NormalState implements BrickState{
        private static final NormalState instance = new NormalState();
        private static NormalState getInstance(){
            return instance;
        }
        @Override
        public boolean clicked(Brick brick) {
            brick.mState = OpenedState.getInstance();
            if(brick.getSurroundMineCount() == 0){
                int x = brick.mX;
                int y = brick.mY;
                for(int i = 0 ; i < 8 ; i++){
                    int curX = x + OFFSET_X[i];
                    int curY = y + OFFSET_Y[i];
                    if(curX >= 0 && curX < bricks.length && curY >= 0 && curY < bricks[0].length){
                        bricks[curX][curY].clicked();
                    }
                }
            }
            return true;
        }

        @Override
        public boolean reverseFlag(Brick brick) {
            brick.mState = FlagedState.getInstance();
            Brick.flagCount++;
            Brick.sendMessageForUpdateMineCounter();
            return true;
        }

        @Override
        public void drawBrick(Canvas canvas, Brick brick, int length, int left, int top) {
            if(brick.field == null){
                 brick.field = new Rect(
                         left + length * brick.mY,
                         top + length * brick.mX,
                         left + length * (brick.mY + 1),
                         top + length * (brick.mX + 1));
            }
            if(bitmapForBrick == null){
                canvas.drawRect(brick.field,paintForBrick);
            }else{
                canvas.drawBitmap(
                        bitmapForBrick,
                        new Rect(0,0,bitmapForBrick.getWidth(),bitmapForBrick.getHeight()),
                        brick.field,
                        null);
            }
        }
    }

    private static class FlagedState implements BrickState{
        private static final FlagedState instance = new FlagedState();
        private static FlagedState getInstance(){
            return instance;
        }
        @Override
        public boolean clicked(Brick brick) {
            return false;
        }

        @Override
        public boolean reverseFlag(Brick brick) {
            brick.mState = NormalState.getInstance();
            Brick.flagCount--;
            Brick.sendMessageForUpdateMineCounter();
            return true;
        }

        @Override
        public void drawBrick(Canvas canvas, Brick brick, int length, int left, int top) {
            if(bitmapForBrick == null){
                canvas.drawRect(brick.field,paintForBrick);
            }else{
                canvas.drawBitmap(
                        bitmapForBrick,
                        new Rect(0,0,bitmapForBrick.getWidth(),bitmapForBrick.getHeight()),
                        brick.field,
                        null);
            }
            canvas.drawBitmap(
                    bitmapForFlag,
                    new Rect(0,0,bitmapForFlag.getWidth(),bitmapForFlag.getHeight()),
                    brick.field,
                    null);
        }
    }

    private static class OpenedState implements BrickState{
        private static final OpenedState instance = new OpenedState();
        private static OpenedState getInstance(){
            return instance;
        }
        @Override
        public boolean clicked(Brick brick) {
            if(brick.getSurroundMineCount() == 0 || brick.hasMine){
                return false;
            }
            int x = brick.mX;
            int y = brick.mY;
            int left = brick.getSurroundMineCount();
            for(int i = 0 ; i < 8 ; i++){
                int curX = x + OFFSET_X[i];
                int curY = y + OFFSET_Y[i];
                if(curX >= 0 && curX < bricks.length && curY >= 0 && curY < bricks[0].length){
                    if(bricks[curX][curY].mState == FlagedState.getInstance()){
                        left--;
                    }
                }
            }
            if(left > 0){
                return false;
            }else {
                for(int i = 0 ; i < 8 ; i++){
                    int curX = x + OFFSET_X[i];
                    int curY = y + OFFSET_Y[i];
                    if(curX >= 0 && curX < bricks.length && curY >= 0 && curY < bricks[0].length){
                        Brick curBrick = bricks[curX][curY];
                        if(curBrick.mState == NormalState.getInstance()){
                            if(curBrick.getSurroundMineCount() == 0){
                                curBrick.clicked();
                            }else {
                                curBrick.mState = OpenedState.getInstance();
                            }
                        }
                    }
                }
            }
            return true;
        }

        @Override
        public boolean reverseFlag(Brick brick) {
            return false;
        }

        @Override
        public void drawBrick(Canvas canvas, Brick brick, int length, int left, int top) {
            if(brick.hasMine){
                if(Brick.bitmapsForNum == null){
                    drawMine(canvas,brick);
                }else {
                    canvas.drawBitmap(
                            bitmapForBomb,
                            new Rect(0,0,bitmapForBomb.getWidth(),bitmapForBomb.getHeight()),
                            brick.field,
                            null);
                }
                return;
            }
            if(brick.getSurroundMineCount() > 0){
                Bitmap bitmap = bitmapsForNum[brick.getSurroundMineCount()];
                if(bitmap == null){
                    canvas.drawText(
                            String.valueOf(brick.getSurroundMineCount()),
                            (brick.field.left + brick.field.right) / 2,
                            (brick.field.top + brick.field.bottom) / 2,
                            paintForNum);
                }else{
                    canvas.drawBitmap(
                            bitmap,
                            new Rect(0,0,bitmap.getWidth(),bitmap.getHeight()),
                            brick.field,
                            null);
                }
            }
        }

        private void drawMine(Canvas canvas,Brick brick){
            canvas.drawLine(
                    brick.field.left,
                    brick.field.top,
                    brick.field.right,
                    brick.field.bottom,
                    new Paint());
            canvas.drawLine(
                    brick.field.left,
                    brick.field.bottom,
                    brick.field.right,
                    brick.field.top,
                    new Paint());
        }
    }

    private static final class MapGenerator {
        static final int MINE = -1;
        private static final Random RANDOM = new Random();
        private MapGenerator(){

        }

        private static int[][] generateMap(int rowCount,int columnCount,int mineCount){
            int[][] map = new int[rowCount][columnCount];

            while(mineCount > 0){
                generateOneMine(map,rowCount,columnCount);
                mineCount--;
            }

            for(int i = 0 ; i < rowCount ; i++)
                for(int j = 0 ; j < columnCount ; j++){
                    if(map[i][j] != MINE){
                        continue;
                    }
                    for(int k = 0 ; k < OFFSET_X.length ; k++){
                        int x = i + OFFSET_X[k];
                        int y = j + OFFSET_Y[k];
                        if(x < 0 || x >= rowCount || y < 0 || y >= columnCount){
                            continue;
                        }
                        if(map[x][y] == MINE){
                            continue;
                        }
                        map[x][y]++;
                    }
                }

            return map;
        }

        private static void generateOneMine(int[][] map,int rowCount,int columnCount){
            int row = RANDOM.nextInt(rowCount);
            int column = RANDOM.nextInt(columnCount);
            while(map[row][column] == MINE){
                row = RANDOM.nextInt(rowCount);
                column = RANDOM.nextInt(columnCount);
            }
            map[row][column] = MINE;
        }

    }
}
