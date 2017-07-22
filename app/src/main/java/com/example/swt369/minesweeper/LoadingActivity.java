package com.example.swt369.minesweeper;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;

public class LoadingActivity extends AppCompatActivity {
    private static boolean loaded = false;
    private ProgressBar progressBar;
    private int progress = 0;
    private int CODE_UPDATE_PROGRESSBAR = 0x123;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if(msg.what == CODE_UPDATE_PROGRESSBAR){
                progressBar.setProgress(msg.arg1);
                return true;
            }
            return false;
        }
    });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        if(loaded){
            progressBar.setProgress(progressBar.getMax());
            return;
        }
        progressBar.setMax(120);
        progressBar.setProgress(progress);

        LoadingThread loadingThread = new LoadingThread();
        loadingThread.start();
    }

    private class LoadingThread extends Thread{

        @Override
        public void run() {
            try {
                Class.forName("Brick");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            sendMessageForUpdateProgress(progress += 10);

            int[] imgIdsForNum = new int[]{
                    R.drawable.mine1,
                    R.drawable.mine2,
                    R.drawable.mine3,
                    R.drawable.mine4,
                    R.drawable.mine5,
                    R.drawable.mine6,
                    R.drawable.mine7,
                    R.drawable.mine8,
                    R.drawable.mine9,
            };
            Bitmap[] bitmapsForNum = new Bitmap[10];
            bitmapsForNum[0] = null;
            for(int i = 1 ; i < 10 ; i++){
                bitmapsForNum[i] = BitmapFactory.decodeResource(getResources(),imgIdsForNum[i - 1]);
                sendMessageForUpdateProgress(progress += 5);
            }
            Brick.setBitMaps(
                    bitmapsForNum,
                    BitmapFactory.decodeResource(getResources(),R.drawable.flag),
                    BitmapFactory.decodeResource(getResources(),R.drawable.bomb),
                    BitmapFactory.decodeResource(getResources(),R.drawable.brick));
            sendMessageForUpdateProgress(progress += 15);

            int[] imgsIdForCounter = new int[]{
                    R.drawable.timer0,
                    R.drawable.timer1,
                    R.drawable.timer2,
                    R.drawable.timer3,
                    R.drawable.timer4,
                    R.drawable.timer5,
                    R.drawable.timer6,
                    R.drawable.timer7,
                    R.drawable.timer8,
                    R.drawable.timer9,
            };
            Bitmap[] bitmapsForCounter = new Bitmap[10];
            for(int i = 0 ; i < 10 ; i ++){
                bitmapsForCounter[i] = BitmapFactory.decodeResource(getResources(),imgsIdForCounter[i]);
                sendMessageForUpdateProgress(progress += 5);
            }
            ((MineSweeperApplication)getApplication()).bitmapsForCounter = bitmapsForCounter;

            loaded = true;

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent(LoadingActivity.this,MainActivity.class);
            startActivity(intent);
        }
    }

    private void sendMessageForUpdateProgress(int progress){
        Message message = handler.obtainMessage();
        message.what = CODE_UPDATE_PROGRESSBAR;
        message.arg1 = progress;
        handler.sendMessage(message);
    }

}
