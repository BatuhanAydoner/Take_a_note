package com.moonturns.takeanote;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class NoteProvider extends ContentProvider {

    private SQLiteDatabase db;

    static final UriMatcher matcher;

    static final String CONTENT_AUTHORITY = "com.moonturns.takeanote.NoteProvider";
    static final String PATH_NOTES = "notes";
    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_NOTES);

    static {
        matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(CONTENT_AUTHORITY, PATH_NOTES, 1);
    }

    @Override
    public boolean onCreate() {

        DatabaseHelper helper = new DatabaseHelper(getContext());
        db = helper.getWritableDatabase();

        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        switch (matcher.match(uri)) {
            case 1:
                Cursor cursor = db.query(DatabaseContact.NotesEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
                return cursor;
        }

        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        switch (matcher.match(uri)) {
            case 1:
                long addedItem = db.insert(DatabaseContact.NotesEntry.TABLE_NAME, null, values);
                if (addedItem > 0) {
                    Uri addeUri = ContentUris.withAppendedId(CONTENT_URI, addedItem);
                    return addeUri;
                }
                break;
        }

        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int deletedItem = 0;
        switch (matcher.match(uri)) {
            case 1:
                deletedItem = db.delete(DatabaseContact.NotesEntry.TABLE_NAME, selection, selectionArgs);
                break;
        }
        return deletedItem;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int updatedItem = 0;
        switch (matcher.match(uri)) {
            case 1:
                updatedItem = db.update(DatabaseContact.NotesEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
        }

        return updatedItem;
    }
}
