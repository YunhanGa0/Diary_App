package com.example.diary.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DiaryDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "diary.db";
    private static final int DATABASE_VERSION = 1;

    public DiaryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_NOTE_TABLE = "CREATE TABLE " + 
                NoteContract.NoteEntry.TABLE_NAME + " (" +
                NoteContract.NoteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NoteContract.NoteEntry.COLUMN_TITLE + " TEXT, " +
                NoteContract.NoteEntry.COLUMN_CONTENT + " TEXT, " +
                NoteContract.NoteEntry.COLUMN_CREATE_TIME + " INTEGER, " +
                NoteContract.NoteEntry.COLUMN_UPDATE_TIME + " INTEGER, " +
                NoteContract.NoteEntry.COLUMN_CATEGORY + " TEXT, " +
                NoteContract.NoteEntry.COLUMN_IS_ENCRYPTED + " INTEGER DEFAULT 0" +
                ");";

        db.execSQL(SQL_CREATE_NOTE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + NoteContract.NoteEntry.TABLE_NAME);
        onCreate(db);
    }
} 