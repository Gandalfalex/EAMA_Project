package com.example.meetforsport.ui.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import androidx.annotation.Nullable;

public class SportDB extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;

    public SportDB(Context context) {
        super(context,  SportEntries.TABLE_NAME , null, DATABASE_VERSION);
    }

    public SportDB(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static class SportEntries implements BaseColumns {
        public static final String TABLE_NAME = "Sports";
        public static final String NAME = "name";
        public static final String MIN_PLAYER = "minPlayer";
        public static final String MAX_PLAYER = "maxPlayer";
    }

    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " +
            SportEntries.TABLE_NAME + " ( " + SportEntries._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            SportEntries.NAME + " TEXT," +
            SportEntries.MIN_PLAYER + " INTEGER," +
            SportEntries.MAX_PLAYER + " INTEGER )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + SportEntries.TABLE_NAME;

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
