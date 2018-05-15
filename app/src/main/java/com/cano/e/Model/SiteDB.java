package com.cano.e.Model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Cano on 2018/4/6.
 */

public class SiteDB {
    SQLiteOpenHelper sqLiteOpenHelper;
    SQLiteDatabase db;
    static SiteDB siteDB;

    public SiteDB(Context context) {
        sqLiteOpenHelper = new SQLiteOpenHelper(context, "ftp.db", null, 1) {

            @Override
            public void onCreate(SQLiteDatabase db) {
                String commomd = "CREATE TABLE ftpsite(" +
                        "name VARCHAR(20) PRIMARY KEY," +
                        "site VARCHAR(20)," +
                        "port INTEGER," +
                        "coding VARCHAR(64)," +
                        "user VARCHAR(64)," +
                        "password VARCHAR(64))";
                db.execSQL(commomd);
            }

            @Override
            public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            }
        };
        db = sqLiteOpenHelper.getWritableDatabase();
        siteDB = this;
    }

    public List<Map<String, Object>> getAll() {
        List<Map<String, Object>> list = new ArrayList<>();
        Cursor cursor = db.query("ftpsite", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Map<String, Object> map = new HashMap<>();
                map.put("name", cursor.getString(cursor.getColumnIndex("name")));
                map.put("site", cursor.getString(cursor.getColumnIndex("site")));
                map.put("port", cursor.getInt(cursor.getColumnIndex("port")));
                map.put("coding", cursor.getString(cursor.getColumnIndex("coding")));
                map.put("user", cursor.getString(cursor.getColumnIndex("user")));
                map.put("password", cursor.getString(cursor.getColumnIndex("password")));
                list.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public void insert(ContentValues values) {
        db.insert("ftpsite", null, values);
    }

    public void modify(ContentValues values) {
        db.update("ftpsite", values, "name = ?", new String[]{values.get("name").toString()});
    }

    public void delete(String name) {
        db.delete("ftpsite", "name = ?", new String[]{name});
    }

}
