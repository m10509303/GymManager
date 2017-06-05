package edu.ntust.prlab.gymmanager.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * 實際上操作資料的DBHelper
 */
public class TaskDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "tasks.db";
    private static final int DATABASE_VERSION = 1;

    public TaskDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * 建立資料表需要的SQL語法，有編號、標題、時間、是否啟用等...
     */
    private static final String SQL_CREATE_TABLE_TASKS = String.format("create table %s"
                    +" (%s integer primary key autoincrement, %s text, %s text, %s integer)",
            TaskContract.TABLE_NAME,
            TaskContract.TaskEntry._ID,
            TaskContract.TaskEntry.TITLE,
            TaskContract.TaskEntry.TIME,
            TaskContract.TaskEntry.IS_ENABLED
    );

    /**
     * 需要建立資料庫的時候，呼叫此函式新建表格。
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_TASKS);
    }

    /**
     * 資料庫版本升級的時候，呼叫此函式把舊的刪除，建立新表格。
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TaskContract.TABLE_NAME);
        onCreate(db);
    }
}
