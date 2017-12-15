package com.example.moetaz.chathub.dataStorage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBAdadpter {

    private static final String DB_NAME = "convdatabase.db";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "convtable";
    private static final String MESSAGE = "message";
    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
            + MESSAGE + " TEXT PRIMARY KEY )";
    private static DBAdadpter dbAdadpter;
    private Context context;
    private SQLiteDatabase sqLiteDatabase;

    private DBAdadpter(Context context) {
        this.context = context;
        sqLiteDatabase = new SqlHelpter(this.context, DB_NAME, null, DB_VERSION).getWritableDatabase();

    }

    public static DBAdadpter getDBAdadpterInstance(Context context) {
        if (dbAdadpter == null) {
            dbAdadpter = new DBAdadpter(context);

        }
        return dbAdadpter;
    }

    public long insertMessage(ContentValues values) {
        return sqLiteDatabase.insert(TABLE_NAME, null, values);
    }


    public Cursor getMessages() {
        return sqLiteDatabase.query(TABLE_NAME
                , new String[]{MESSAGE}, null, null, null, null, null, null);
    }


    public int deleteConv() {
        return sqLiteDatabase.delete(TABLE_NAME, null, null);
    }

    private static class SqlHelpter extends SQLiteOpenHelper {

        public SqlHelpter(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

            onCreate(db);

        }

        @Override
        public void onConfigure(SQLiteDatabase db) {
            super.onConfigure(db);
        }
    }
}
