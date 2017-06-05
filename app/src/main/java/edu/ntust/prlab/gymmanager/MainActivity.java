package edu.ntust.prlab.gymmanager;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;

import edu.ntust.prlab.gymmanager.database.TaskContract;
import edu.ntust.prlab.gymmanager.pojo.Task;
import edu.ntust.prlab.gymmanager.schedule.TaskFirebaseUtils;
import edu.ntust.prlab.gymmanager.ui.GYMRecyclerViewAdapter;
import edu.ntust.prlab.gymmanager.utils.TimeParser;

/**
 * 用來顯示主頁面的Activity。
 */
public class MainActivity extends AppCompatActivity implements
        GYMRecyclerViewAdapter.OnDragListener,
        GYMRecyclerViewAdapter.OnItemCheckedListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_SEARCH_TASKS = 87;

    private RecyclerView gymRecyclerViewer;
    private GYMRecyclerViewAdapter gymRecyclerViewAdapter;
    private ItemTouchHelper itemTouchHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        gymRecyclerViewer = (RecyclerView) findViewById(R.id.main_gym_list);
        gymRecyclerViewAdapter = new GYMRecyclerViewAdapter(this, this);
        gymRecyclerViewer.setAdapter(gymRecyclerViewAdapter);
        ItemTouchHelper.Callback callback = new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.START | ItemTouchHelper.END) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                gymRecyclerViewAdapter.onItemDismiss(viewHolder.getAdapterPosition());
            }
        };
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(gymRecyclerViewer);

        setSupportActionBar(toolbar);
        getLoaderManager().restartLoader(LOADER_SEARCH_TASKS, null, this);
    }

    /**
     * 根據URI去查詢成員資料。
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LOADER_SEARCH_TASKS:
                return new CursorLoader(this, TaskContract.CONTENT_URI,
                        null, null, null, TaskContract.TaskEntry.TIME + " ASC");
        }

        return null;
    }

    /**
     * 如果查詢完畢就更新RecyclerViewAdapter的Cursor。
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case LOADER_SEARCH_TASKS:
                gymRecyclerViewAdapter.swapCursor(data);
                break;
        }
    }

    /**
     * 如果查詢失敗就把RecyclerViewAdapter的Cursor清空。
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case LOADER_SEARCH_TASKS:
                gymRecyclerViewAdapter.swapCursor(null);
                break;
        }
    }

    /**
     * 當畫面右下角的按鈕被點擊，顯示出Dialog讓使用者設定~
     */
    public void onAddedButtonClicked(View view) {
        createDialog().show();
    }

    private AlertDialog createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        builder.setMessage(R.string.message_add_task);
        View contentView = inflater.inflate(R.layout.alert_add_task, null);
        builder.setView(contentView);
        final EditText titleEditText = (EditText) contentView.findViewById(R.id.alert_input_title);
        final TimePicker timePicker = (TimePicker) contentView.findViewById(R.id.alert_time_picker);
        builder.setPositiveButton(R.string.message_sure, new DialogInterface.OnClickListener() {
            // 如果使用者確認加入Task，就透過ContentProvider存入資料庫，然後設定提醒~
            public void onClick(DialogInterface dialog, int whichButton) {
                ContentValues values = new ContentValues();
                values.put(TaskContract.TaskEntry.TITLE, String.valueOf(titleEditText.getText()));
                values.put(TaskContract.TaskEntry.TIME, String.valueOf(timePicker.getHour() + TimeParser.SEPARATOR + timePicker.getMinute()));
                values.put(TaskContract.TaskEntry.IS_ENABLED, 1);
                Uri id = getContentResolver().insert(TaskContract.CONTENT_URI, values);
                getLoaderManager().restartLoader(LOADER_SEARCH_TASKS, null, MainActivity.this);
                startAlarm((int) ContentUris.parseId(id), timePicker.getHour(), timePicker.getMinute());
            }
        });
        builder.setNegativeButton(R.string.message_cancel, null);
        return builder.create();
    }

    /**
     * 設定提醒
     * @param id Task的編號
     * @param hour 觸發的小時
     * @param minute 觸發的分鐘
     */
    private void startAlarm(int id, int hour, int minute) {
        TaskFirebaseUtils.scheduleReadyFirebaseJobDispatcherSync(this, id, hour, minute);
    }

    /**
     * 取消提醒
     * @param id Task的編號
     */
    private void cancelAlarm(int id) {
        TaskFirebaseUtils.cancelScheduleReadyFirebaseJobDispatcherSync(this, id);
        TaskFirebaseUtils.cancelScheduleFirebaseJobDispatcherSync(this, id);
    }

    /**
     * 當使用者選擇啟用與否時，根據狀況去開啟/關閉提醒
     */
    @Override
    public void onItemClicked(Task task) {
        Uri uri = ContentUris.withAppendedId(TaskContract.CONTENT_URI, task.getId());
        ContentValues values = new ContentValues();
        values.put(TaskContract.TaskEntry.TITLE, task.getTitle());
        values.put(TaskContract.TaskEntry.TIME, task.getTime());
        values.put(TaskContract.TaskEntry.IS_ENABLED, task.isEnabled() ? 1 : 0);
        getContentResolver().update(uri, values, null, null);
        if (task.isEnabled()) {
            startAlarm(task.getId(),
                    TimeParser.parseHour(task.getTime()),
                    TimeParser.parseMinute(task.getTime()));
        } else {
            cancelAlarm(task.getId());
        }
    }

    /**
     * 當使用者滑動刪除Task，就把提醒取消，並且重新讀取資料庫的Task
     */
    @Override
    public void onDrag(Task task) {
        cancelAlarm(task.getId());
        Uri uri = ContentUris.withAppendedId(TaskContract.CONTENT_URI, task.getId());
        getContentResolver().delete(uri, null, null);
        getLoaderManager().restartLoader(LOADER_SEARCH_TASKS, null, this);
    }

}
