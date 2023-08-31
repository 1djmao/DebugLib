package com.idjmao.lib;

import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.ListPopupWindow;

import com.idjmao.lib.utils.DimenUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FloatWindowService extends Service {

    WindowManager windowManager;
    View floatView;
    private static ScrollView textScroll;
    private static TextView textView;
    TextView clearTv;

    TextView floatBotton;

    public FloatWindowService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(this)) {
            addFloatingWindow();
            addFloatButton();
            hideFloatLayout();
            handler = new Handler();

            watchLog(TestClient.getLogFilterLevel());

        }
        return super.onStartCommand(intent, flags, startId);
    }


    boolean stopWatchLog=false;
    private void watchLog(String level) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Process mLogcatProc = null;
                BufferedReader reader = null;
                try {
                    //获取logcat日志信息
                    mLogcatProc = Runtime.getRuntime().exec(new String[]{"logcat","-s","*:V"});
                    reader = new BufferedReader(new InputStreamReader(mLogcatProc.getInputStream()));
                    String line;

                    String head = "kkkkk";
                    String logTag = "";

                    while ((line = reader.readLine()) != null) {
//                        Log.i("kkkkkk", "run: line="+line);

                        line=line.replaceAll("  "," ");

                        if (!line.startsWith(head)){
                            String[] lines=line.split(" ");
                            if (lines!=null&&lines.length>6){
                                String date=lines[0];
                                String time=lines[1];
                                String progress=lines[2];
                                String thread=lines[3];
                                String level=lines[4];
                                String tag=lines[5];

                                if (!logLevelEnable(level)){
                                    continue;
                                }

                                int index=tag.indexOf(":");
                                if (index>0){
                                    logTag=tag.substring(0,index);
                                }else {
                                    logTag=tag;
                                }

                                head=line.substring(0,line.indexOf(logTag)+logTag.length());
                                if (TextUtils.isEmpty(TestClient.getLogTag())||logTag.equals(TestClient.getLogTag())){
                                    appendStr(head, Color.RED);
                                }
                            }
                        }
                        if (TextUtils.isEmpty(TestClient.getLogTag())||logTag.equals(TestClient.getLogTag())){
                            int index=line.indexOf(":",head.length());
                            if (index>0){
                                appendStr(line.substring(index+1));
                            }else {
                                appendStr(line.substring(head.length()));
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    mLogcatProc.destroy();
                    if (reader!=null){
                        try {
                            reader.close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }).start();
    }

    private boolean logLevelEnable(String level){
        List<String> levels=new ArrayList<>();
        switch (TestClient.getLogFilterLevel()){
            case "V":
                Collections.addAll(levels, new String[]{"V", "D", "I", "W", "E"});
                return levels.contains(level);

            case "D":
                Collections.addAll(levels, new String[]{ "D", "I", "W", "E"});
                return levels.contains(level);

            case "I":
                Collections.addAll(levels, new String[]{ "I", "W", "E"});
                return levels.contains(level);

            case "W":
                Collections.addAll(levels, new String[]{ "W", "E"});
                return levels.contains(level);

            case "E":
                return level.equals("E");
        }

        return false;
    }
    TextView levelTv;
    TextView tagTv;
    private void addFloatingWindow() {
        // 获取WindowManager服务
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        // log 布局
        floatView = LayoutInflater.from(this).inflate(R.layout.float_window, null, false);
        textView = floatView.findViewById(R.id.log_text);
        clearTv = floatView.findViewById(R.id.clear_text);
        textScroll = floatView.findViewById(R.id.text_scroll);

        levelTv=floatView.findViewById(R.id.level_tv);
        tagTv=floatView.findViewById(R.id.tag_tv);

        levelTv.setText(TestClient.getLogFilterLevel());
        tagTv.setText(TestClient.getLogTag());


        TextView closeLayout = floatView.findViewById(R.id.close_text);
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

        levelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectLevelPopulWindow();
            }
        });
        tagTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectTagPopulWindow();
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
        windowLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        windowLayoutParams.gravity = Gravity.BOTTOM;
        windowLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        windowLayoutParams.height = 1000;
        windowLayoutParams.x = 0;
        windowLayoutParams.y = 0;

        // 将悬浮窗控件添加到WindowManager
        windowManager.addView(floatView, windowLayoutParams);

    }

    private void showSelectLevelPopulWindow() {
        final String[] list = getBaseContext().getResources().getStringArray(R.array.log_level);
        final ListPopupWindow listPopupWindow;
        listPopupWindow = new ListPopupWindow(this);
        listPopupWindow.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, list));//用android内置布局，或设计自己的样式
        listPopupWindow.setAnchorView(levelTv);//以哪个控件为基准，在该处以mEditText为基准
        listPopupWindow.setModal(true);

        listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {//设置项点击监听
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                levelTv.setText(list[i]);//把选择的选项内容展示在EditText上
                TestClient.setLogFilterLevel(list[i].substring(0,1));
                listPopupWindow.dismiss();//如果已经选择了，隐藏起来
            }
        });
        listPopupWindow.show();//把ListPopWindow展示出来
        listPopupWindow.getListView().setBackgroundColor(Color.WHITE);
    }
    private void showSelectTagPopulWindow() {
        final String[] list = TestClient.logTagList.toArray(new String[TestClient.logTagList.size()]);//要填充的数据
        final ListPopupWindow listPopupWindow;
        listPopupWindow = new ListPopupWindow(this);
        listPopupWindow.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, list));//用android内置布局，或设计自己的样式
        listPopupWindow.setAnchorView(tagTv);//以哪个控件为基准，在该处以mEditText为基准
        listPopupWindow.setModal(true);

        listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {//设置项点击监听
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                tagTv.setText(list[i]);//把选择的选项内容展示在EditText上
                TestClient.setDefLogTag(list[i]);
                listPopupWindow.dismiss();//如果已经选择了，隐藏起来
            }
        });
        listPopupWindow.show();//把ListPopWindow展示出来
        listPopupWindow.getListView().setBackgroundColor(Color.WHITE);
    }

    private void addFloatButton() {
        //打开 log 按钮
        floatBotton = new TextView(this);
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
        layoutParams.width = DimenUtils.dip2px(getApplicationContext(), 40);
        layoutParams.height = DimenUtils.dip2px(getApplicationContext(), 40);
        layoutParams.x = 30;
        layoutParams.y = 300;
        layoutParams.gravity = Gravity.RIGHT | Gravity.BOTTOM;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        // 将悬浮窗控件添加到WindowManager
        windowManager.addView(floatBotton, layoutParams);
        floatBotton.setOnTouchListener(new FloatingOnTouchListener());
    }

    private void showFloatLayout() {
        floatView.setVisibility(View.VISIBLE);
        floatBotton.setVisibility(View.GONE);
    }

    private void hideFloatLayout() {
        floatView.setVisibility(View.GONE);
        floatBotton.setVisibility(View.VISIBLE);
    }

    private static Handler handler;

    public static void appendStr(String text) {
        appendStr(text, Color.WHITE);
//        textView.append(new SpannableStringBuilder("\n" + text));
    }

    public static void appendStr(String text, int color) {
        if (textView != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {

                    SpannableStringBuilder stringBuilder = new SpannableStringBuilder("\n" + text+" ");
                    stringBuilder.setSpan(new ForegroundColorSpan(color), 0, text.length()+1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

                    textView.append(stringBuilder);
                    if (textView.getMeasuredHeight() <= (textScroll.getScrollY() + textScroll.getHeight())) {
                        textScroll.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                textScroll.scrollTo(0, textView.getMeasuredHeight() - textScroll.getHeight());
                            }
                        }, 100);
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
        textView = null;
        textScroll = null;
        handler = null;
    }

    private class FloatingOnTouchListener implements View.OnTouchListener {
        private int x;
        private int y;

        boolean move = false;

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
                    if (movedX < 30 && movedY < 30 && !move) {
                        return false;
                    }
                    x = nowX;
                    y = nowY;
                    WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) view.getLayoutParams();
                    layoutParams.x = layoutParams.x - movedX;
                    layoutParams.y = layoutParams.y - movedY;

                    // 更新悬浮窗控件布局
                    windowManager.updateViewLayout(view, layoutParams);
                    move = true;
                    break;
                case MotionEvent.ACTION_UP:
                    if (move) {
                        move = false;
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