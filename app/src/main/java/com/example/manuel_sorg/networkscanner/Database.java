package com.example.manuel_sorg.networkscanner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {

    private final static String TAG = "DataBase";

    private static final String TABLE_NAME = "NetworkDevices";
    private static final String COL1 = "ID";
    private static final String COL2 = "NAME";
    private static final String COL3 = "IP";
    private static final String COL4 = "MAC";
    private static final String COL5 = "HOST";

    public Database(Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " + COL2 + " TEXT, " + COL3 + " TEXT, " + COL4 + " TEXT, " + COL5 + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP IF TABLE EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addData(Geraet geraet) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COL2, geraet.getBezeichnung());
        contentValues.put(COL3, geraet.getIpAddress());
        contentValues.put(COL4, geraet.getMacAddress());
        contentValues.put(COL5, geraet.getHostname());

        //Log.d(TAG, "addData: Adding " + Item + " to " + TABLE_NAME);

        long result = db.insert(TABLE_NAME, null, contentValues);

        db.close();

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean deleteDB() {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_NAME, null, null);

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Cursor getData() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

/*    public Cursor getItemID(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + COL1 + " FROM " + TABLE_NAME + " WHERE " + COL2 + " = '" + name + "'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }*/
}
