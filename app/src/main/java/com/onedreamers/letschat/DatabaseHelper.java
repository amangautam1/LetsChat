package com.onedreamers.letschat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aman gautam on 01-Apr-17.
 */


public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "social.db";
    private static final String TABLE_NAME = "chats";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        //SQLiteDatabase db = this.getWritableDatabase();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + "(NAME TEXT,MESSAGE TEXT,TIME INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_NAME);

    }

    public Cursor getLast() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "select * from " + TABLE_NAME + " order by time desc limit 1";
        Cursor cursor = db.rawQuery(query, null);

        return cursor;


    }

    public boolean insertData(String name, String message, long time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME", name);
        contentValues.put("MESSAGE", message);
        contentValues.put("TIME", time);
        long result = db.insert(TABLE_NAME, null, contentValues);

        if (result == -1)
            return false;
        else
            return true;

    }

    public List<ChatMessage> getAllMessages() {
        List<ChatMessage> messages = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "select * from " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                ChatMessage cm = new ChatMessage();
                cm.setMessageUser(cursor.getString(0));
                cm.setMessageText(cursor.getString(1));
                cm.setMessageTime(Long.parseLong(cursor.getString(2)));

                String name = cursor.getString(1) + "\n" + cursor.getString(2);
                MainActivity.ArrayofName.add(name);
                // Adding contact to list
                messages.add(cm);
            } while (cursor.moveToNext());

        }
        return  messages;
    }
}
