package com.example.meetforsport.ui.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import androidx.annotation.Nullable;

public class EventDB extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 3;


    public static class EventEntries implements BaseColumns {
        public static final String TABLE_NAME = "Events";
        public static final String LOCATION_ID = "l_id";
        public static final String SPORT_ID = "s_id";
        public static final String USER_ID = "u_id";
        public static final String DESCRIPTION_COLUMN = "descr";
        public static final String TIME = "time";
        public static final String DATE = "date";
        public static final String NAME = "name";
    }

    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " +
            EventEntries.TABLE_NAME + " ( " + EventEntries._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            EventEntries.NAME + " TEXT," +
            EventEntries.LOCATION_ID + " INTEGER," +
            EventEntries.SPORT_ID + " INTEGER," +
            EventEntries.USER_ID + " INTEGER," +
            EventEntries.DESCRIPTION_COLUMN + " TEXT," +
            EventEntries.TIME + " TEXT," +
            EventEntries.DATE + " TEXT )";


    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + EventEntries.TABLE_NAME;



    public EventDB(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public EventDB(Context context) {
        super(context,  EventEntries.TABLE_NAME , null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL(SQL_DELETE_ENTRIES);
        Log.d("DB_SYNC", "database cleared");
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}
