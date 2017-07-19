package com.example.swt369.minesweeper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class MainActivity extends AppCompatActivity {
    private final Brick[][][] bricks = new Brick[1][][];
    private Timer timer;
    private MineCounter mineCounter;
    private RadioGroup radioGroup;
    private Handler handler;
    private GameView gameView;
    private GameController gameController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if(msg.what == Code.CODE_INVALIDATE_TIMER){
                    if(gameController.isAlive()){
                        timer.invalidate();
                    }
                    return true;
                }else if(msg.what == Code.CODE_INVALIDATE_MINECOUNTER){
                    mineCounter.invalidate();
                    return true;
                }else if(msg.what == Code.CODE_DIED){
                    gameView.invalidate();
                    gameController.setAlive(false);
                    timer.pause();
                    return true;
                }else if(msg.what == Code.CODE_WIN){
                    gameView.invalidate();
                    gameController.setAlive(false);
                    timer.pause();
                    createDialogForWin();
                    return true;
                }
                return false;
            }
        });

        LinearLayout layout = (LinearLayout)findViewById(R.id.layout);
        layout.setOrientation(LinearLayout.VERTICAL);

        initializeBricks();

        gameView = new GameView(this, bricks[0]);
        gameView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0,Settings.GAMEVIEW_WEIGHT));

        layout.addView(initializeLayoutTop());
        layout.addView(gameView);
        layout.addView(initializeLayoutBottom());
        gameController = new GameController(handler,gameView);
        gameView.setOnTouchListener(gameController);
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
        bricks[0] = Brick.initializeBricks(
                Difficulty.INTERMEDIATE_SIZE_WIDTH,
                Difficulty.INTERMEDIATE_SIZE_HEIGHT,
                Difficulty.INTERMEDIATE_MINECOUNT);
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
        timer.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT,Settings.TOP_TIMER_WEIGHT));
        layoutTop.addView(timer);

        Button space = new Button(this);
        space.setVisibility(View.INVISIBLE);
        space.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT,Settings.TOP_SPACE_WEIGHT));
        layoutTop.addView(space);

        mineCounter = new MineCounter(this,bitmaps);
        mineCounter.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT,Settings.TOP_MINECOUNTER_WEIGHT));
        layoutTop.addView(mineCounter);
        layoutTop.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0,Settings.TOP_LAYOUT_WEIGHT));
        return layoutTop;
    }

    private LinearLayout initializeLayoutBottom(){
        LinearLayout layoutBottom = new LinearLayout(this);
        layoutBottom.setOrientation(LinearLayout.VERTICAL);

        radioGroup = new RadioGroup(this);
        RadioButton radioButtonPrimary = new RadioButton(this);
        radioButtonPrimary.setText("初级");
        radioButtonPrimary.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT,1));
        radioButtonPrimary.setTextColor(Color.WHITE);
        RadioButton radioButtonIntermediate = new RadioButton(this);
        radioButtonIntermediate.setText("中级");
        radioButtonIntermediate.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT,1));
        RadioButton radioButtonAdvanced = new RadioButton(this);
        radioButtonIntermediate.setTextColor(Color.WHITE);
        radioButtonAdvanced.setText("高级");
        radioButtonAdvanced.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT,1));
        radioButtonAdvanced.setTextColor(Color.WHITE);
        radioGroup.addView(radioButtonPrimary);
        radioGroup.addView(radioButtonIntermediate);
        radioGroup.addView(radioButtonAdvanced);
        radioGroup.check(radioButtonIntermediate.getId());
        radioGroup.setOrientation(LinearLayout.HORIZONTAL);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                initializeMap();
            }
        });
        radioGroup.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0,Settings.BOTTOM_RADIOGROUP));
        layoutBottom.addView(radioGroup);

        ImageButton buttonNext = new ImageButton(this);
        buttonNext.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.restart));
        buttonNext.setScaleType(ImageView.ScaleType.FIT_XY);
        buttonNext.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0,Settings.BOTTOM_REFRESH_WEIGHT));
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initializeMap();
            }
        });
        layoutBottom.addView(buttonNext);

        layoutBottom.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0,Settings.BOTTOM_LAYOUT_WEIGHT));
        return layoutBottom;
    }

    private void initializeMap(){
        switch (radioGroup.getCheckedRadioButtonId() % 3){
            case 1:
                bricks[0] = Brick.initializeBricks(
                        Difficulty.PRIMARY_SIZE_WIDTH,
                        Difficulty.PRIMARY_SIZE_HEIGHT,
                        Difficulty.PRIMARY_MINECOUNT);
                break;
            case 2:
                bricks[0] = Brick.initializeBricks(
                        Difficulty.INTERMEDIATE_SIZE_WIDTH,
                        Difficulty.INTERMEDIATE_SIZE_HEIGHT,
                        Difficulty.INTERMEDIATE_MINECOUNT);
                break;
            case 0:
                bricks[0] = Brick.initializeBricks(
                        Difficulty.ADVANCED_SIZE_WIDTH,
                        Difficulty.ADVANCED_SIZE_HEIGHT,
                        Difficulty.ADVANCED_MINECOUNT);
                break;
        }
        gameView.refreshBricks(bricks[0]);
        gameController.setAlive(true);
        timer.resetTime();
        mineCounter.invalidate();
    }

    private String getDifficultyText(){
        switch (radioGroup.getCheckedRadioButtonId() % 3){
            case 1:
                return "初级";
            case 2:
                return "中级";
            case 0:
                return "高级";
        }
        return "";
    }

    private void createDialogForWin(){
        AlertDialog builder = new AlertDialog.Builder(this).create();
        builder.setTitle("你赢了！");
        builder.setMessage(String.format("%s  用时%d秒",getDifficultyText(),timer.getTime()));
        builder.show();
    }

    private static class Settings{
        private static final int TOP_TIMER_WEIGHT = 45;
        private static final int TOP_SPACE_WEIGHT = 10;
        private static final int TOP_MINECOUNTER_WEIGHT = 45;
        private static final int TOP_LAYOUT_WEIGHT = 10;
        private static final int GAMEVIEW_WEIGHT = 70;
        private static final int BOTTOM_REFRESH_WEIGHT = 50;
        private static final int BOTTOM_RADIOGROUP = 50;
        private static final int BOTTOM_LAYOUT_WEIGHT = 20;
    }
}
