package com.fa_aanchalbansal_c0846324_android;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseManager extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "product_database";

    private static final String TABLE_NAME = "places";
    private static final String COLUMN_ID = "placeId";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_ADDRESS = "address";
    private static final String COLUMN_LAT = "lat";
    private static final String COLUMN_LONG = "long";
    private static final String COLUMN_VISITED = "visited";


    public DatabaseManager(Context context) {
        super(context, DATABASE_NAME,null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_NAME + "(" +
                COLUMN_ID + " INTEGER NOT NULL CONSTRAINT employee_pk PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " VARCHAR(20) NOT NULL, " +
                COLUMN_ADDRESS + " VARCHAR(20) NOT NULL, " +
                COLUMN_LAT + " DOUBLE NOT NULL, " +
                COLUMN_LONG + " DOUBLE NOT NULL, " +
                COLUMN_VISITED + " INTEGER NOT NULL);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // drop table and then create it
        String sql = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";
        db.execSQL(sql);
        onCreate(db);
    }

    // insert
    public boolean addPlace(String title, String address, double lat , double lng , int visited) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_TITLE, title);
        contentValues.put(COLUMN_ADDRESS, address);
        contentValues.put(COLUMN_LAT, String.valueOf(lat));
        contentValues.put(COLUMN_LONG, String.valueOf(lng));
        contentValues.put(COLUMN_VISITED, visited);


        return sqLiteDatabase.insert(TABLE_NAME, null, contentValues) != -1;
    }

    public Cursor getAllPlaces() {
        // we need a readable instance of database
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        return sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }

    // Update
    public boolean updatePlace(int id, String title, String address, double lat, double lng , int visited) {
        // we need a writeable instance of database
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_TITLE, title);
        contentValues.put(COLUMN_ADDRESS, address);
        contentValues.put(COLUMN_LAT, String.valueOf(lat));
        contentValues.put(COLUMN_LONG, String.valueOf(lng));
        contentValues.put(COLUMN_VISITED, String.valueOf(visited));

        return sqLiteDatabase.update(TABLE_NAME,
                contentValues,
                COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}) > 0;
    }

    public boolean deletePlace(int id) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        return sqLiteDatabase.delete(TABLE_NAME,
                COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}) > 0;
    }

}