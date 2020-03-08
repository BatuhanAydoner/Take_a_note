package com.moonturns.takeanote;

import android.provider.BaseColumns;

public class DatabaseContact {
    public static final class NotesEntry implements BaseColumns {
        public static String TABLE_NAME = "notes";
        public static String COLUMN_ID = BaseColumns._ID;
        public static String COLUMN_NOTE = "note";
        public static String COLUMN_PRIORITY = "note_priority";
    }
}
