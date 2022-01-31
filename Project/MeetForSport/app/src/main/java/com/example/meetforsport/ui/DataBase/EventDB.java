package com.example.meetforsport.ui.DataBaseEvent;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class EventDB extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String SQL_DATABASE_NAME = "EventDB";
    private static final String EVENT_ID = "e_id";
    private static final String LONGITUDE_COLUMN = "lo_number";
    private static final String LATITUTE_COLUMN = "la_number";
    private static final String SPORT_NAME_COLUMN = "s_id";
    private static final String DESCRIPTION_COLUMN = "description";
    private static final String USER_NAME_COLUMN = "u_id";


    public EventDB(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public EventDB(Context context) {
        super(context, SQL_DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + SQL_DATABASE_NAME + " ("   + EVENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                LONGITUDE_COLUMN + " INT," +
                LATITUTE_COLUMN + " INT," +
                SPORT_NAME_COLUMN + " INT," +
                DESCRIPTION_COLUMN + " TEXT," +
                USER_NAME_COLUMN + " INT )"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SQL_DATABASE_NAME);
        onCreate(sqLiteDatabase);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}
