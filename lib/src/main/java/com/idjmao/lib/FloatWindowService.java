package com.idjmao.lib;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.idjmao.lib.utils.DimenUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class FloatWindowService extends Service {

    WindowManager windowManager ;
    View floatView;
    private static ScrollView textScroll;
    private static TextView textView;
    TextView clearTv;

    TextView floatBotton;

    public FloatWindowService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M&&Settings.canDrawOverlays(this)) {
            addFloatingWindow();
            addFloatButton();
            hideFloatLayout();
            handler=new Handler();

            watchLog();

        }
        return super.onStartCommand(intent, flags, startId);
    }



    private void watchLog(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Process mLogcatProc = null;
                BufferedReader reader = null;
                try {
                    //获取logcat日志信息
                    mLogcatProc = Runtime.getRuntime().exec(new String[] { "logcat","*:I" });
                    reader = new BufferedReader(new InputStreamReader(mLogcatProc.getInputStream()));
                    String line;

                    String head="kkkkk";
                    String logTag="";

                    while ((line = reader.readLine()) != null) {

                        //logcat打印信息在这里可以监听到
                        if (line.length()>37+logTag.length()){
                            int logIndex=line.indexOf(":",37)+1;
                            logTag=line.substring(37,logIndex);

                            if (!line.startsWith(head)){
                                head=line.substring(0,37+logTag.length());
                                appendStr(head,Color.RED);
                            }
                            line=line.substring(37+logTag.length());
                        }
                        appendStr(line);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void addFloatingWindow() {
            // 获取WindowManager服务
            windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

            // log 布局
            floatView= LayoutInflater.from(this).inflate(R.layout.float_window,null,false);
            textView=floatView.findViewById(R.id.log_text);
            clearTv=floatView.findViewById(R.id.clear_text);
            textScroll=floatView.findViewById(R.id.text_scroll);
            TextView closeLayout=floatView.findViewById(R.id.close_text);
            closeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideFloatLayout();
                }
            });
            clearTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    textView.setText("");
                }
            });


            // 设置LayoutParam
            WindowManager.LayoutParams windowLayoutParams = new WindowManager.LayoutParams();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                windowLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else {
                windowLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            }
            windowLayoutParams.format = PixelFormat.RGBA_8888;
            windowLayoutParams.flags= WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            windowLayoutParams.gravity= Gravity.BOTTOM;
            windowLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            windowLayoutParams.height = 1000;
            windowLayoutParams.x = 0;
            windowLayoutParams.y = 0;

            // 将悬浮窗控件添加到WindowManager
            windowManager.addView(floatView, windowLayoutParams);

    }

    private void addFloatButton(){
        //打开 log 按钮
        floatBotton=new TextView(this);
        floatBotton.setBackgroundColor(Color.parseColor("#55000000"));
        floatBotton.setBackgroundResource(R.drawable.grey_radius_40);

        floatBotton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFloatLayout();
            }
        });

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.width = DimenUtils.dip2px(getApplicationContext(),40);
        layoutParams.height = DimenUtils.dip2px(getApplicationContext(),40);
        layoutParams.x = 30;
        layoutParams.y = 300;
        layoutParams.gravity=Gravity.RIGHT|Gravity.BOTTOM;
        layoutParams.flags= WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        // 将悬浮窗控件添加到WindowManager
        windowManager.addView(floatBotton, layoutParams);
        floatBotton.setOnTouchListener(new FloatingOnTouchListener());
    }

    private void showFloatLayout(){
        floatView.setVisibility(View.VISIBLE);
        floatBotton.setVisibility(View.GONE);
    }

    private void hideFloatLayout(){
        floatView.setVisibility(View.GONE);
        floatBotton.setVisibility(View.VISIBLE);
    }

    private static Handler handler;

    public static void appendStr(String text){
        appendStr(text,Color.WHITE);
    }

    public static void appendStr(String text,int color){
        if (textView!=null){
            handler.post(new Runnable() {
                @Override
                public void run() {

                    SpannableStringBuilder stringBuilder=new SpannableStringBuilder("\n"+text);
                    stringBuilder.setSpan(new ForegroundColorSpan(color),0,text.length(),Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

                    textView.append(stringBuilder);
                    if (textView.getMeasuredHeight() <= (textScroll.getScrollY() + textScroll.getHeight())) {
                        textScroll.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                textScroll.scrollTo(0,textView.getMeasuredHeight()-textScroll.getHeight());
                            }
                        },100);
                    }
                }
            });
        }
    }




    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        textView=null;
        textScroll=null;
        handler=null;
    }

    private class FloatingOnTouchListener implements View.OnTouchListener {
        private int x;
        private int y;

        boolean move=false;

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x = (int) event.getRawX();
                    y = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int nowX = (int) event.getRawX();
                    int nowY = (int) event.getRawY();
                    int movedX = nowX - x;
                    int movedY = nowY - y;
                    if (movedX<30&&movedY<30&&!move){
                        return false;
                    }
                    x = nowX;
                    y = nowY;
                    WindowManager.LayoutParams layoutParams= (WindowManager.LayoutParams) view.getLayoutParams();
                    layoutParams.x = layoutParams.x - movedX;
                    layoutParams.y = layoutParams.y - movedY;

                    // 更新悬浮窗控件布局
                    windowManager.updateViewLayout(view, layoutParams);
                    move=true;
                    break;
                    case MotionEvent.ACTION_UP:
                        if (move){
                            move=false;
                            return true;
                        }
                        break;
                default:
                    break;
            }
            return false;
        }
    }
}