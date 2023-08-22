package com.idjmao.lib.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DbHelper extends SQLiteOpenHelper {
    public DbHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //获取所有表名
    public List<String> getAllTableNames() {
        List<String> tableNames = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                @SuppressLint("Range") String tableName = cursor.getString(cursor.getColumnIndex("name"));
                tableNames.add(tableName);
                cursor.moveToNext();
            }
        }

        cursor.close();
        return tableNames;
    }

    //获取表结构
    public List<String> getTableMaster(String tableName){
        List<String> masterName = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("PRAGMA  table_info(["+tableName+"])", null);

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex("name"));
                masterName.add(name);
                cursor.moveToNext();
            }
        }

        cursor.close();
        return masterName;
    }

    public List<List<String>> getValue(String table){
        List<List<String>> values=new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+table, null);

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                List<String> value=new ArrayList<>();

                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    value.add(cursor.getString(i));
                }

                values.add(value);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return values;
    }

}
