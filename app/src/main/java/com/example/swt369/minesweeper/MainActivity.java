package com.example.swt369.minesweeper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {
    private final Brick[][][] bricks = new Brick[1][][];
    private Timer timer;
    private MineCounter mineCounter;
    private Handler handler;
    private GameView gameView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if(msg.what == Code.CODE_INVALIDATE_TIMER){
                    timer.invalidate();
                    return true;
                }else if(msg.what == Code.CODE_INVALIDATE_MINECOUNTER){
                    mineCounter.invalidate();
                    return true;
                }
                return false;
            }
        });

        LinearLayout layout = (LinearLayout)findViewById(R.id.layout);
        layout.setOrientation(LinearLayout.VERTICAL);

        initializeBricks();

        gameView = new GameView(this, bricks[0]);
        gameView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0,60));

        ImageButton buttonNext = new ImageButton(this);
        buttonNext.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.arrowright));
        buttonNext.setScaleType(ImageView.ScaleType.FIT_CENTER);
        buttonNext.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0,30));
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bricks[0] = Brick.initializeBricks(12,12,18);
                gameView.refreshBricks(bricks[0]);
            }
        });

        layout.addView(initializeLayoutTop());
        layout.addView(gameView);
        layout.addView(buttonNext);
        gameView.setOnTouchListener(new GameController(handler,gameView));
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        timer.initializeSize();
        mineCounter.initializeSize();
    }

    private void initializeBricks(){
        Bitmap[] bitmapsForNum = new Bitmap[]{
                null,
                BitmapFactory.decodeResource(getResources(),R.drawable.mine1),
                BitmapFactory.decodeResource(getResources(),R.drawable.mine2),
                BitmapFactory.decodeResource(getResources(),R.drawable.mine3),
                BitmapFactory.decodeResource(getResources(),R.drawable.mine4),
                BitmapFactory.decodeResource(getResources(),R.drawable.mine5),
                BitmapFactory.decodeResource(getResources(),R.drawable.mine6),
                BitmapFactory.decodeResource(getResources(),R.drawable.mine7),
                BitmapFactory.decodeResource(getResources(),R.drawable.mine8),
                BitmapFactory.decodeResource(getResources(),R.drawable.mine9)};
        Brick.setBitMaps(bitmapsForNum,
                BitmapFactory.decodeResource(getResources(),R.drawable.flag),
                BitmapFactory.decodeResource(getResources(),R.drawable.bomb),
                BitmapFactory.decodeResource(getResources(),R.drawable.brick));
        bricks[0] = Brick.initializeBricks(12, 12, 18);
        Brick.setHandler(handler);
    }

    private LinearLayout initializeLayoutTop(){
        LinearLayout layoutTop = new LinearLayout(this);

        Bitmap[] bitmaps = new Bitmap[]{
                BitmapFactory.decodeResource(getResources(),R.drawable.timer0),
                BitmapFactory.decodeResource(getResources(),R.drawable.timer1),
                BitmapFactory.decodeResource(getResources(),R.drawable.timer2),
                BitmapFactory.decodeResource(getResources(),R.drawable.timer3),
                BitmapFactory.decodeResource(getResources(),R.drawable.timer4),
                BitmapFactory.decodeResource(getResources(),R.drawable.timer5),
                BitmapFactory.decodeResource(getResources(),R.drawable.timer6),
                BitmapFactory.decodeResource(getResources(),R.drawable.timer7),
                BitmapFactory.decodeResource(getResources(),R.drawable.timer8),
                BitmapFactory.decodeResource(getResources(),R.drawable.timer9)
        };
        timer = new Timer(this,bitmaps,handler);
        timer.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT,50));
        layoutTop.addView(timer);

        mineCounter = new MineCounter(this,bitmaps,handler);
        mineCounter.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT,50));
        layoutTop.addView(mineCounter);
        layoutTop.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0,10));
        return layoutTop;
    }

}
