package com.example.swt369.minesweeper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LinearLayout layout = (LinearLayout)findViewById(R.id.layout);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout layoutTop = new LinearLayout(this);
        layoutTop.addView(new Button(this));
        layoutTop.addView(new Button(this));
        layoutTop.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0,10));
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
                BitmapFactory.decodeResource(getResources(),R.drawable.bomb));
        final Brick[][][] bricks = {Brick.initializeBricks(10, 10, 10)};
        final GameView gameView = new GameView(this, bricks[0]);
        gameView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0,60));
        ImageButton buttonNext = new ImageButton(this);
        buttonNext.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.arrowright));
        buttonNext.setScaleType(ImageView.ScaleType.FIT_CENTER);
        buttonNext.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0,30));
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bricks[0] = Brick.initializeBricks(10,10,10);
                gameView.refreshBricks(bricks[0]);
            }
        });
        layout.addView(layoutTop);
        layout.addView(gameView);
        layout.addView(buttonNext);
        Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                return false;
            }
        });
        gameView.setOnTouchListener(new GameController(handler,gameView));
    }
}
