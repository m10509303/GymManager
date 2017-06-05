package edu.ntust.prlab.gymmanager.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class TaskProvider extends ContentProvider {

    private TaskDatabaseHelper mDbHelper;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private static final int TASKS = 100;
    private static final int TASKS_WITH_ID = 101;

    /**
     * 事先加入URI Mapping的狀況~
     */
    static {
        sUriMatcher.addURI(TaskContract.CONTENT_AUTHORITY,
                TaskContract.TABLE_NAME,
                TASKS);

        sUriMatcher.addURI(TaskContract.CONTENT_AUTHORITY,
                TaskContract.TABLE_NAME + "/#",
                TASKS_WITH_ID);
    }

    /**
     * 初始化DBHelper
     */
    @Override
    public boolean onCreate() {
        mDbHelper = new TaskDatabaseHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    /**
     * 根據條件讀取Task
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        switch (sUriMatcher.match(uri)) {
            case TASKS: {
                return mDbHelper.getReadableDatabase().query(
                        TaskContract.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
            }
        }
        return null;
    }

    /**
     * 新增Task，把要新增的資料包裝在ContentValues裡面。
     */
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long _id = -1;
        switch (sUriMatcher.match(uri)) {
            case TASKS: {
                db.beginTransaction();
                try {
                    _id = db.insert(TaskContract.TABLE_NAME, null, values);
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
            }
        }
        if (-1 != _id && getContext() != null && null != getContext().getContentResolver()) {
            getContext().getContentResolver().notifyChange(uri, null);
            return ContentUris.withAppendedId(TaskContract.CONTENT_URI, _id);
        } else {
            return null;
        }
    }

    /**
     * 只接受TASKS_WITH_ID的URI，
     * 根據URI後面指定的ID刪除對應的Task。
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        switch (sUriMatcher.match(uri)) {
            case TASKS_WITH_ID:
                long id = ContentUris.parseId(uri);
                selection = String.format("%s = ?", TaskContract.TaskEntry._ID);
                selectionArgs = new String[]{String.valueOf(id)};
                break;
            default:
                throw new IllegalArgumentException("Illegal delete URI");
        }

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int count = db.delete(TaskContract.TABLE_NAME, selection, selectionArgs);

        if (count > 0 && getContext() != null && null != getContext().getContentResolver()) {
            //Notify observers of the change
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return count;
    }

    /**
     * 只接受TASKS_WITH_ID的URI，
     * 根據URI後面指定的ID修改對應的Task。
     */
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int numUpdated;

        if (values == null) {
            throw new IllegalArgumentException("Cannot have null content values");
        }

        switch (sUriMatcher.match(uri)) {
            case TASKS_WITH_ID: {
                numUpdated = db.update(TaskContract.TABLE_NAME,
                        values,
                        TaskContract.TaskEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        if (getContext() != null && numUpdated > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numUpdated;
    }

}
