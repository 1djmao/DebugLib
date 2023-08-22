package com.idjmao.lib;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.TextUtils;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.idjmao.lib.utils.CopyLinkTextHelper;
import com.idjmao.lib.view.LibMainActivity;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestClient {

    private static final String TEST_LIB_SP="testLib";
    private static Application app;
    private static List<String> spNameList =new ArrayList<>();

    private static String dbName;

    private static List<File> dirList =new ArrayList<>();
    private static List<String> dirNameList=new ArrayList<>();

    public static void init(Application app){
        TestClient.app=app;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //读取 sp 文件夹下的文件自动加入 sp列表
            File spDir=new File(app.getDataDir().getPath()+"/shared_prefs");
            File[] spFiles= spDir.listFiles();
            for (int i = 0; i < spFiles.length; i++) {
                if (spFiles[i].getName().endsWith(".xml")){
                    addSpName(spFiles[i].getName().replace(".xml",""));
                }
            }

            //内部存储加入文件数据
            dirList.add(app.getDataDir());
            dirNameList.add("内部存储");
        }

        dirList.add(app.getExternalFilesDir(null));
        dirNameList.add("外部文件目录");

        dirList.add(app.getExternalCacheDir());
        dirNameList.add("外部 Cache 目录");

        dirList.add(app.getExternalMediaDirs()[0]);
        dirNameList.add("Media 目录");

        dirList.add(app.getObbDir());
        dirNameList.add("Obb 目录");


    }

    public static void startTestActivity(Activity activity){
        activity.startActivity(new Intent(app, LibMainActivity.class));
    }

    public static void saveError(Throwable error){
        StringWriter sw = new StringWriter();
        try (PrintWriter pw = new PrintWriter(sw)) {
            error.printStackTrace(pw);
            saveError(sw.toString());
        }

    }

    public static void saveError(String error){
        SharedPreferences sharedPreferences=app.getSharedPreferences(TEST_LIB_SP, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("error",error).commit();
    }

    public static void checkError(Activity activity){
        SharedPreferences sharedPreferences=activity.getSharedPreferences(TEST_LIB_SP, Context.MODE_PRIVATE);
        String error=sharedPreferences.getString("error","");
        if (TextUtils.isEmpty(error)){
            return;
        }

//        Dialog alert = new Dialog(app);
//        alert.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
//        View view = LayoutInflater.from(app).inflate(R.layout.dialog,null);
//        alert.setContentView(view);
//        alert.show();

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("异常退出");
        builder.setMessage("检测到上次异常退出,错误信息:\n"+error);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("复制", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CopyLinkTextHelper.getInstance(activity).CopyText(error);
                dialog.dismiss();
            }
        });
        AlertDialog dialog=builder.create();
//        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        dialog.show();

        sharedPreferences.edit().putString("error","").commit();

    }


    public static void addSpName(String name){
        spNameList.add(name);
    }

    public static List<String> getSpName() {
        return spNameList;
    }

    public static void setDbName(String dbName) {
        TestClient.dbName = dbName;
    }

    public static String getDbName() {
        return dbName;
    }

    public static List<File> getDirList() {
        return dirList;
    }

    public static List<String> getDirNameList() {
        return dirNameList;
    }
}
