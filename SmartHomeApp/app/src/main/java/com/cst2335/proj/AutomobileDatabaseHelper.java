package com.cst2335.proj;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AutomobileDatabaseHelper extends SQLiteOpenHelper {
    protected static String DATABASE_NAME = "Automobile.db";
    public static String TABLE_NAME = "MyTable";
    protected static int VERSION_NUM = 1;

    //table column name
    public final static String KEY_ID = "ID";          //ID
    public final static String ITEM = "ITEM";          //ITEM
    public final static String VALUE = "VALUE";    //VALUE

    //table item name
    public final static String ITEM_KM = "KM";    //KM
    public final static String ITEM_GAS = "GAS";    //GAS

    public AutomobileDatabaseHelper(Context ctx) {
        super(ctx, DATABASE_NAME, null, VERSION_NUM);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + ITEM + " TEXT, " + VALUE + " TEXT" + ");");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}