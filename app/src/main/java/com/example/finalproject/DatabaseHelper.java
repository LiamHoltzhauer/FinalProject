package com.example.finalproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "NasaApod.db";
    private static final String TABLE_NAME = "SavedImages";
    private static final String COL_ID = "ID";
    private static final String COL_DATE = "Date";
    private static final String COL_IMAGE_URL = "ImageUrl";
    private static final String COL_HD_URL = "HdUrl";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_DATE + " TEXT, " +
                COL_IMAGE_URL + " TEXT, " +
                COL_HD_URL + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertImage(String date, String imageUrl, String hdUrl) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_DATE, date);
        contentValues.put(COL_IMAGE_URL, imageUrl);
        contentValues.put(COL_HD_URL, hdUrl);

        long result = db.insert(TABLE_NAME, null, contentValues);
        return result != -1;
    }

    public ArrayList<ImageModel> getAllImages() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<ImageModel> imageList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String date = cursor.getString(1);
            String imageUrl = cursor.getString(2);
            String hdUrl = cursor.getString(3);
            imageList.add(new ImageModel(id, date, imageUrl, hdUrl));
        }
        cursor.close();
        return imageList;
    }

    public boolean deleteImage(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_NAME, COL_ID + "=?", new String[]{String.valueOf(id)});
        return result > 0;
    }
}