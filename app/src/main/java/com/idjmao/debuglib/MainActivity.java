package com.idjmao.debuglib;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;

import com.idjmao.lib.TestClient;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TestClient.checkError(this);

//        new CountDownTimer(100000,1000){
//            @Override
//            public void onTick(long millisUntilFinished) {
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
////                        FloatWindowService.appendStr("日志更新 "+millisUntilFinished, Color.RED);
//                        Log.e("Mytest", "this is a test"+millisUntilFinished+"\n啊辣椒粉拉风\n加客服",new Throwable());
//
//                    }
//                }).start();
//            }
//
//            @Override
//            public void onFinish() {}
//        }.start();

    }

    public void intoDebugTool(View view) {
        TestClient.startTestActivity(this);
    }
}