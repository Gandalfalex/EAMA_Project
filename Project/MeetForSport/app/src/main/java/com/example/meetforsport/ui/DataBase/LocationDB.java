package com.example.meetforsport.ui.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import androidx.annotation.Nullable;

public class LocationDB extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 3;

    public LocationDB(Context context) {
        super(context,  LocationEntries.TABLE_NAME , null, DATABASE_VERSION);
    }

    public LocationDB(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static class LocationEntries implements BaseColumns {
        public static final String TABLE_NAME = "Locations";
        public static final String LONGITUDE = "long";
        public static final String LATITUDE = "lat";
        public static final String NAME = "name";
        public static final String ADDRESS = "address";
    }

    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " +
            LocationEntries.TABLE_NAME + " ( " + LocationEntries._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            LocationEntries.LONGITUDE + " REAL," +
            LocationEntries.LATITUDE + " REAL," +
            LocationEntries.NAME + " TEXT," +
            LocationEntries.ADDRESS + " TEXT )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + LocationEntries.TABLE_NAME;

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
