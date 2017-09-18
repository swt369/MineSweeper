package com.example.swt369.minesweeper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private final Brick[][][] bricks = new Brick[1][][];
    private Timer timer;
    private MineCounter mineCounter;
    private RadioGroup radioGroup;
    private Handler handler;
    private GameView gameView;
    private GameController gameController;
    private File destDir;
    private File fileTxt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeHighScore();

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if(msg.what == Code.CODE_INVALIDATE_TIMER){
                    timer.invalidate();
                    return true;
                }else if(msg.what == Code.CODE_INVALIDATE_MINECOUNTER){
                    mineCounter.invalidate();
                    return true;
                }else if(msg.what == Code.CODE_DIED){
                    timer.pause();
                    gameController.setAlive(false);
                    gameView.invalidate();
                    return true;
                }else if(msg.what == Code.CODE_WIN){
                    timer.pause();
                    gameController.setAlive(false);
                    gameView.invalidate();
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

        Button buttonNext = new Button(this);
        buttonNext.setBackgroundColor(0x7f040000);
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
        int[] lastHighScores = getHighScore();
        int lastHighScore = 999;
        switch (radioGroup.getCheckedRadioButtonId() % 3){
            case 1:
                lastHighScore = lastHighScores[0];
                break;
            case 2:
                lastHighScore = lastHighScores[1];
                break;
            case 0:
                lastHighScore = lastHighScores[2];
        }
        AlertDialog builder = new AlertDialog.Builder(this).create();
        builder.setTitle("你赢了！");
        if(timer.getTime() < lastHighScore){
            builder.setMessage(String.format("%s新纪录!\n用时:   %d秒\n原纪录： %d秒",getDifficultyText(),timer.getTime(),lastHighScore));
            refreshHighScore(timer.getTime());
        }else {
            builder.setMessage(String.format("用时:   %d秒\n原纪录： %d秒",timer.getTime(),lastHighScore));
        }
        builder.show();
    }

    private void initializeHighScore(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            destDir = new File(this.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getPath());
        }else {
            destDir = new File(this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getPath());
        }
        if (!destDir.exists()) {
            destDir.mkdirs();
            Log.i("created","dir");
        }
        fileTxt = new File(destDir.getPath() + File.separator + "highscore.txt");
        if(!fileTxt.exists()){
            BufferedWriter bufferedWriter = null;
            try {
                bufferedWriter = new BufferedWriter(new FileWriter(fileTxt));
                bufferedWriter.write("999\n999\n999");
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                try {
                    if (bufferedWriter != null) {
                        bufferedWriter.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private int[] getHighScore(){
        if(!fileTxt.exists() || !destDir.exists()){
            initializeHighScore();
        }
        int[] highScores = new int[]{999,999,999};
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(fileTxt));
            for(int i = 0 ; i < 3 ; i++){
                highScores[i] = Integer.parseInt(bufferedReader.readLine());
            }
            return highScores;
        } catch (Exception e) {
            return new int[]{999,999,999};
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void refreshHighScore(int highScore){
        BufferedWriter bufferedWriter = null;
        String res;
        try {
            int[] highScores = getHighScore();
            switch (radioGroup.getCheckedRadioButtonId() % 3){
                case 1:
                    highScores[0] = highScore;
                    break;
                case 2:
                    highScores[1] = highScore;
                    break;
                case 0:
                    highScores[2] = highScore;
                    break;
            }
            res = String.format("%d\n%d\n%d",highScores[0],highScores[1],highScores[2]);
        } catch (Exception e) {
            return;
        }
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(fileTxt));
            bufferedWriter.write(res);
        } catch (IOException ignored) {
        }finally {
            try {
                if (bufferedWriter != null) {
                    bufferedWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
