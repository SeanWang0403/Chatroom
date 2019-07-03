package com.sean.chatroom.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "chatroom";
    private static final int DATABASE_VERSION = 1;
    private static final String USERDATA_TABLE_CREATE = "create table user(_id integer primary key autoincrement," +
            "userID text,name text,phone text,sticker text,background text,id text,id_search integer,qr_code text)";
    private static final String FRIEND_TABLE_CREATE = "create table friend(_id integer primary key autoincrement," +
            "userID text,name text,sticker text,background text,ship integer,remark text,update_time text)";
    private static final String CHAT_TABLE_CREATE = "create table chat(_id integer primary key autoincrement," +
            "room text,sender text,receiver text,message text,msgID text,createtime text,delivertime text," +
            "read integer,message_type integer)";
    private static final String CHATHISTORY_TABLE_CREATE = "create table chathistory(_id integer primary key autoincrement," +
            "room text,sticker text,name text,message text,time text)";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(USERDATA_TABLE_CREATE);
        db.execSQL(FRIEND_TABLE_CREATE);
        db.execSQL(CHAT_TABLE_CREATE);
        db.execSQL(CHATHISTORY_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
