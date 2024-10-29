package com.example.diary.db;

import android.provider.BaseColumns;

public final class NoteContract {
    private NoteContract() {}

    public static class NoteEntry implements BaseColumns {
        public static final String TABLE_NAME = "notes";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_CREATE_TIME = "create_time";
        public static final String COLUMN_UPDATE_TIME = "update_time";
        public static final String COLUMN_CATEGORY = "category";
        public static final String COLUMN_IS_ENCRYPTED = "is_encrypted";
    }
} 