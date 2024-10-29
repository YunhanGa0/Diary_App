package com.example.diary.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.diary.model.Note;
import java.util.ArrayList;
import java.util.Arrays;
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

        int moodIndex = cursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_MOOD);
        if (moodIndex != -1) {
            note.setMood(cursor.getInt(moodIndex));
        } else {
            note.setMood(0);
        }

        int imagePathIndex = cursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_IMAGE_PATHS);
        if (imagePathIndex != -1) {
            String imagePaths = cursor.getString(imagePathIndex);
            if (imagePaths != null && !imagePaths.isEmpty()) {
                note.setImagePaths(new ArrayList<>(Arrays.asList(imagePaths.split(";"))));
            }
        }

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
        
        // 获取心情值（如果是心情关键词）
        int moodValue = getMoodValueFromKeyword(query);
        
        // 构建搜索条件
        String selection;
        String[] selectionArgs;
        
        if (moodValue != -1) {
            // 如果是心情关键词，搜索心情值或标题内容
            selection = NoteContract.NoteEntry.COLUMN_MOOD + " = ? OR " +
                       NoteContract.NoteEntry.COLUMN_TITLE + " LIKE ? OR " +
                       NoteContract.NoteEntry.COLUMN_CONTENT + " LIKE ?";
            selectionArgs = new String[]{
                String.valueOf(moodValue),
                "%" + query + "%",
                "%" + query + "%"
            };
        } else {
            // 如果不是心情关键词，只搜索标题和内容
            selection = NoteContract.NoteEntry.COLUMN_TITLE + " LIKE ? OR " +
                       NoteContract.NoteEntry.COLUMN_CONTENT + " LIKE ?";
            selectionArgs = new String[]{
                "%" + query + "%",
                "%" + query + "%"
            };
        }
        
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

    private int getMoodValueFromKeyword(String keyword) {
        // 转换为小写并去除空格，使搜索更灵活
        String trimmedKeyword = keyword.toLowerCase().trim();
        switch (trimmedKeyword) {
            case "非常伤心":
                return 0;
            case "伤心":
                return 1;
            case "一般":
                return 2;
            case "开心":
                return 3;
            case "非常开心":
                return 4;
            case "生气":
                return 5;
            default:
                return -1; // 不是心情关键词
        }
    }

    public void saveNote(Note note) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NoteContract.NoteEntry.COLUMN_TITLE, note.getTitle());
        values.put(NoteContract.NoteEntry.COLUMN_CONTENT, note.getContent());
        values.put(NoteContract.NoteEntry.COLUMN_CREATE_TIME, note.getCreateTime());
        values.put(NoteContract.NoteEntry.COLUMN_UPDATE_TIME, note.getUpdateTime());
        values.put(NoteContract.NoteEntry.COLUMN_CATEGORY, note.getCategory());
        values.put(NoteContract.NoteEntry.COLUMN_IS_ENCRYPTED, note.isEncrypted() ? 1 : 0);
        values.put(NoteContract.NoteEntry.COLUMN_MOOD, note.getMood());
        
        if (note.getImagePaths() != null && !note.getImagePaths().isEmpty()) {
            values.put(NoteContract.NoteEntry.COLUMN_IMAGE_PATHS, 
                    String.join(";", note.getImagePaths()));
        }

        if (note.getId() > 0) {
            db.update(NoteContract.NoteEntry.TABLE_NAME, values,
                    NoteContract.NoteEntry._ID + " = ?",
                    new String[]{String.valueOf(note.getId())});
        } else {
            long id = db.insert(NoteContract.NoteEntry.TABLE_NAME, null, values);
            note.setId(id);
        }
    }
} 