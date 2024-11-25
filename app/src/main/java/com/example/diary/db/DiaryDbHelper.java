package com.example.diary.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashSet;
import java.util.Set;

public class DiaryDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "diary.db";
    private static final int DATABASE_VERSION = 6;

    public DiaryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_NOTE_TABLE = "CREATE TABLE " + 
                NoteContract.NoteEntry.TABLE_NAME + " (" +
                NoteContract.NoteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NoteContract.NoteEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                NoteContract.NoteEntry.COLUMN_CONTENT + " TEXT, " +
                NoteContract.NoteEntry.COLUMN_CREATE_TIME + " INTEGER, " +
                NoteContract.NoteEntry.COLUMN_UPDATE_TIME + " INTEGER, " +
                NoteContract.NoteEntry.COLUMN_CATEGORY + " TEXT, " +
                NoteContract.NoteEntry.COLUMN_IS_ENCRYPTED + " INTEGER DEFAULT 0, " +
                NoteContract.NoteEntry.COLUMN_PASSWORD + " TEXT, " +
                NoteContract.NoteEntry.COLUMN_IMAGE_PATHS + " TEXT, " +
                NoteContract.NoteEntry.COLUMN_MOOD + " INTEGER DEFAULT 0);";

        db.execSQL(SQL_CREATE_NOTE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 6) {
            // 检查列是否存在
            Cursor cursor = db.rawQuery("PRAGMA table_info(" + 
                    NoteContract.NoteEntry.TABLE_NAME + ")", null);
            Set<String> existingColumns = new HashSet<>();
            if (cursor != null) {
                int nameIndex = cursor.getColumnIndex("name");
                while (cursor.moveToNext()) {
                    existingColumns.add(cursor.getString(nameIndex));
                }
                cursor.close();
            }

            // 添加密码列
            if (!existingColumns.contains(NoteContract.NoteEntry.COLUMN_PASSWORD)) {
                db.execSQL("ALTER TABLE " + NoteContract.NoteEntry.TABLE_NAME + 
                        " ADD COLUMN " + NoteContract.NoteEntry.COLUMN_PASSWORD + 
                        " TEXT");
            }

            // 添加加密标志列
            if (!existingColumns.contains(NoteContract.NoteEntry.COLUMN_IS_ENCRYPTED)) {
                db.execSQL("ALTER TABLE " + NoteContract.NoteEntry.TABLE_NAME + 
                        " ADD COLUMN " + NoteContract.NoteEntry.COLUMN_IS_ENCRYPTED + 
                        " INTEGER DEFAULT 0");
            }
        }
        db.execSQL("DROP TABLE IF EXISTS " + NoteContract.NoteEntry.TABLE_NAME);
        onCreate(db);
    }
} 