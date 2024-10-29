package com.example.diary.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.diary.model.Note;
import java.util.ArrayList;
import java.util.List;

public class NoteDao {
    private DiaryDbHelper dbHelper;

    public NoteDao(Context context) {
        dbHelper = new DiaryDbHelper(context);
    }

    public long insertNote(Note note) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NoteContract.NoteEntry.COLUMN_TITLE, note.getTitle());
        values.put(NoteContract.NoteEntry.COLUMN_CONTENT, note.getContent());
        values.put(NoteContract.NoteEntry.COLUMN_CREATE_TIME, note.getCreateTime());
        values.put(NoteContract.NoteEntry.COLUMN_UPDATE_TIME, note.getUpdateTime());
        values.put(NoteContract.NoteEntry.COLUMN_CATEGORY, note.getCategory());
        values.put(NoteContract.NoteEntry.COLUMN_IS_ENCRYPTED, note.isEncrypted() ? 1 : 0);

        return db.insert(NoteContract.NoteEntry.TABLE_NAME, null, values);
    }

    public int updateNote(Note note) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NoteContract.NoteEntry.COLUMN_TITLE, note.getTitle());
        values.put(NoteContract.NoteEntry.COLUMN_CONTENT, note.getContent());
        values.put(NoteContract.NoteEntry.COLUMN_UPDATE_TIME, System.currentTimeMillis());
        values.put(NoteContract.NoteEntry.COLUMN_CATEGORY, note.getCategory());
        values.put(NoteContract.NoteEntry.COLUMN_IS_ENCRYPTED, note.isEncrypted() ? 1 : 0);

        return db.update(NoteContract.NoteEntry.TABLE_NAME, values,
                NoteContract.NoteEntry._ID + " = ?",
                new String[]{String.valueOf(note.getId())});
    }

    public Note getNoteById(long id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                NoteContract.NoteEntry.TABLE_NAME,
                null,
                NoteContract.NoteEntry._ID + " = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null
        );

        Note note = null;
        if (cursor != null && cursor.moveToFirst()) {
            note = cursorToNote(cursor);
            cursor.close();
        }
        return note;
    }

    public List<Note> getAllNotes() {
        List<Note> notes = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                NoteContract.NoteEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                NoteContract.NoteEntry.COLUMN_UPDATE_TIME + " DESC"
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                notes.add(cursorToNote(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return notes;
    }

    private Note cursorToNote(Cursor cursor) {
        Note note = new Note();
        note.setId(cursor.getLong(cursor.getColumnIndexOrThrow(NoteContract.NoteEntry._ID)));
        note.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(NoteContract.NoteEntry.COLUMN_TITLE)));
        note.setContent(cursor.getString(cursor.getColumnIndexOrThrow(NoteContract.NoteEntry.COLUMN_CONTENT)));
        note.setCreateTime(cursor.getLong(cursor.getColumnIndexOrThrow(NoteContract.NoteEntry.COLUMN_CREATE_TIME)));
        note.setUpdateTime(cursor.getLong(cursor.getColumnIndexOrThrow(NoteContract.NoteEntry.COLUMN_UPDATE_TIME)));
        note.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(NoteContract.NoteEntry.COLUMN_CATEGORY)));
        note.setEncrypted(cursor.getInt(cursor.getColumnIndexOrThrow(NoteContract.NoteEntry.COLUMN_IS_ENCRYPTED)) == 1);
        return note;
    }

    public void deleteNote(long id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(NoteContract.NoteEntry.TABLE_NAME,
                NoteContract.NoteEntry._ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    public List<Note> searchNotes(String query) {
        List<Note> notes = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        // 构建搜索条件
        String selection = NoteContract.NoteEntry.COLUMN_TITLE + " LIKE ? OR " +
                          NoteContract.NoteEntry.COLUMN_CONTENT + " LIKE ?";
        String[] selectionArgs = new String[]{"%" + query + "%", "%" + query + "%"};
        
        Cursor cursor = db.query(
                NoteContract.NoteEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                NoteContract.NoteEntry.COLUMN_UPDATE_TIME + " DESC"
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                notes.add(cursorToNote(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        
        return notes;
    }
} 