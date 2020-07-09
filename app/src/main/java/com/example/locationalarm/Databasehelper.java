package com.example.locationalarm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class Databasehelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME="EnterDetails.db";
    public static final String TABLE_NAME ="tbl_detail";
    public static final String Col_1 = "ID";
    public static final String Col_2 = "NAME";
    public static final String Col_3 = "LATITUDE";
    public static final String Col_4 = "LONGITUDE";
    public static final String Col_5 = "DESCRIPTION";
    public static final String Col_6 = "STATUS";


    public Databasehelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+TABLE_NAME+ "(ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT ,LATITUDE DOUBLE ,LONGITUDE DOUBLE , DESCRIPTION TEXT, STATUS VARCHAR NOT NULL DEFAULT 'true')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String name,double latitude,double longitude,String Desc){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Col_2,name);
        contentValues.put(Col_3,latitude);
        contentValues.put(Col_4,longitude);
        contentValues.put(Col_5,Desc);

        long result = db.insert(TABLE_NAME,null,contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public boolean updateData(String id,String name,Double latitude,Double longitude,String Desc ){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Col_1,id);
        contentValues.put(Col_2,name);
        contentValues.put(Col_3,latitude);
        contentValues.put(Col_4,longitude);
        contentValues.put(Col_5,Desc);
        db.update(TABLE_NAME,contentValues,"ID=?",new String[]{id});

        return true;
    }

    public Cursor getRemainderData()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from "+TABLE_NAME,null);
        return cursor;
    }

    public Cursor getRemainder(int id)
    {
        SQLiteDatabase db = this.getReadableDatabase() ;
        String qry = "select * from "+ TABLE_NAME + " where ID = " +id;
        Log.e("CursorQuery = ",qry);
        Cursor cursor = db.rawQuery(qry,null);
        return cursor;
    }

    public void deleteRemainder(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME,"ID=?",new String[]{id});
    }

    public void updateStatus(int id , String status){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Col_6,status);
        db.update(TABLE_NAME,contentValues,"ID= " +id,null);
    }
}
