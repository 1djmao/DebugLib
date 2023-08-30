package com.idjmao.debuglib;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;

import com.idjmao.lib.TestClient;

public class MApp extends Application implements Thread.UncaughtExceptionHandler {
    @Override
    public void onCreate() {
        super.onCreate();
        initSpTest();
        initDataBase();
        Thread.setDefaultUncaughtExceptionHandler(this);

        TestClient.init(this);
        TestClient.logTagList.add("Mytest");
        TestClient.logTagList.add("aaa");
        TestClient.logTagList.add("bbb");
        TestClient.logTagList.add("ccc");
        TestClient.logTagList.add("ddd");

        TestClient.setDefLogTag("Mytest");

        TestClient.setDbName("aaaa.db");

//        TestClient.checkError();

    }

    private void initSpTest() {
        SharedPreferences.Editor editor1=getSharedPreferences("sp1", Context.MODE_PRIVATE).edit();
        editor1.putString("sss","ssssio");
        editor1.putBoolean("iiooo",true);
        editor1.putFloat("ukff",10.1f);
        editor1.putInt("uued",123);
        editor1.putLong("sss",456l);
        editor1.commit();

        SharedPreferences.Editor editor2=getSharedPreferences("sp2", Context.MODE_PRIVATE).edit();
        editor2.putString("sss","ssssio");
        editor2.putBoolean("iiooo",true);
        editor2.putFloat("ukff",10.1f);
        editor2.putInt("uued",123);
        editor2.putLong("sss",456l);
        editor2.commit();


    }

    private void initDataBase(){
        SQLiteOpenHelper dataHelper=new SQLiteOpenHelper(this,"aaaa.db",null,1) {
            @Override
            public void onCreate(SQLiteDatabase db) {

            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            }
        };

        String createUserTableQuery = "CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY, name TEXT)";
        String createProductTableQuery = "CREATE TABLE IF NOT EXISTS products (id INTEGER PRIMARY KEY, name TEXT)";

        dataHelper.getWritableDatabase().execSQL(createUserTableQuery);
        dataHelper.getWritableDatabase().execSQL(createProductTableQuery);

        for (int i = 0; i < 10; i++) {
            dataHelper.getWritableDatabase().execSQL("replace INTO users (id,name) VALUES ("+i+",'user555');");
        }


    }

    @Override
    public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
        TestClient.saveError(e);
        restartApp();
    }

    public void restartApp() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        System.exit(0);
    }
}
