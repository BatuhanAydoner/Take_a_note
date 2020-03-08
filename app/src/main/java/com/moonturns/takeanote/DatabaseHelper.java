package com.moonturns.takeanote;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "takeanot.db";
    private static final int DATABASE_VERSION = 1;

    private static final String CREATE_TABLE = "CREATE TABLE " + DatabaseContact.NotesEntry.TABLE_NAME
            + " ("
            + DatabaseContact.NotesEntry.COLUMN_ID + " INTEGER PRIMARY KEY, "
            + DatabaseContact.NotesEntry.COLUMN_NOTE + " TEXT, "
            + DatabaseContact.NotesEntry.COLUMN_PRIORITY + " INTEGER"
            + ");";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContact.NotesEntry.TABLE_NAME);
        onCreate(db);
    }
}
