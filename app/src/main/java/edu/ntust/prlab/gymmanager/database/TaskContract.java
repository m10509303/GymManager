package edu.ntust.prlab.gymmanager.database;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * 這邊跟課堂上的例題一致，用來提供存取Task的ContentProvider需要的資訊
 */
public class TaskContract {

    static final String TABLE_NAME = "tasks";

    static final String CONTENT_AUTHORITY = "edu.ntust.prlab.gymmanager";

    public static final Uri CONTENT_URI = new Uri.Builder().scheme("content")
            .authority(CONTENT_AUTHORITY)
            .appendPath(TABLE_NAME)
            .build();

    /**
     * 紀錄取資料需要的欄位名稱
     */
    public static abstract class TaskEntry implements BaseColumns {
        public static final String TITLE = "title";
        public static final String TIME = "remindTime";
        public static final String IS_ENABLED = "isEnabled";
    }

    private TaskContract() {
    }

}
