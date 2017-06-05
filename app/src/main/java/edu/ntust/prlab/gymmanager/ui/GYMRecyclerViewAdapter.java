package edu.ntust.prlab.gymmanager.ui;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import edu.ntust.prlab.gymmanager.R;
import edu.ntust.prlab.gymmanager.database.TaskContract;
import edu.ntust.prlab.gymmanager.pojo.Task;

/**
 * 用來幫助呈現Task清單的RecyclerViewAdapter
 */
public class GYMRecyclerViewAdapter extends RecyclerView.Adapter<GYMRecyclerViewAdapter.ViewHolder>
        implements ItemTouchHelperAdapter {

    /**
     * 儲存資料的Cursor。
     */
    private Cursor cursor;
    /**
     * 在使用者滑動的時候，能夠呼叫相對應動作的Callback。
     */
    private OnDragListener onDragListener;
    /**
     * 在使用者選擇啟用與否的時候，能夠呼叫相對應動作的Callback。
     */
    private OnItemCheckedListener listener;

    /**
     * 把相對應的參數傳進來。
     */
    public GYMRecyclerViewAdapter(OnDragListener onDragListener, OnItemCheckedListener listener) {
        this.onDragListener = onDragListener;
        this.listener = listener;
    }

    /**
     * 把Cursor換掉的函數。
     */
    public void swapCursor(Cursor cursor) {
        this.cursor = cursor;
        this.notifyDataSetChanged();
    }

    /**
     * 當ViewHolder不夠用的時候，就會呼叫這邊產生新的ViewHolder。
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GYMRecyclerViewAdapter.ViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gym_list, parent, false));
    }

    /**
     * 當ViewHolder的呈現資料要更新的時候，就呼叫此函式，做相對應的動作把值綁訂到ViewHolder上。
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        cursor.moveToPosition(position);
        Task task = new Task()
                .setId(cursor.getInt(cursor.getColumnIndex(TaskContract.TaskEntry._ID)))
                .setTitle(cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.TITLE)))
                .setTime(cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.TIME)))
                .setEnabled(cursor.getInt(cursor.getColumnIndex(TaskContract.TaskEntry.IS_ENABLED)) > 0);
        holder.gymEnabledCheckBox.setChecked(task.isEnabled());
        holder.gymTimeTextView.setText(task.getTime());
        holder.gymTitleTextView.setText(task.getTitle());
    }

    /**
     * 全部資料的數量。
     */
    @Override
    public int getItemCount() {
        return this.cursor != null ? this.cursor.getCount() : 0;
    }

    /**
     * 配合ItemTouchHelper的Callback
     */
    @Override
    public void onItemDismiss(int position) {
        cursor.moveToPosition(position);
        onDragListener.onDrag(getTask(
                cursor.getInt(cursor.getColumnIndex(TaskContract.TaskEntry._ID)),
                cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.TITLE)),
                cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.TIME)),
                cursor.getInt(cursor.getColumnIndex(TaskContract.TaskEntry.IS_ENABLED)) > 0
        ));
    }

    /**
     * 當頁面滑動的時候，所呼叫的Callback介面
     */
    public interface OnDragListener {
        void onDrag(Task task);
    }

    /**
     * 當點選啟用的Checkbox使用的Callback介面
     */
    public interface OnItemCheckedListener {
        void onItemClicked(Task task);
    }

    /**
     * 用來代表呈現畫面物件的物件
     */
    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView gymTitleTextView;
        private TextView gymTimeTextView;
        private CheckBox gymEnabledCheckBox;

        ViewHolder(View itemView) {
            super(itemView);
            gymTitleTextView = (TextView) itemView.findViewById(R.id.list_gym_title);
            gymTimeTextView = (TextView) itemView.findViewById(R.id.list_gym_time);
            gymEnabledCheckBox = (CheckBox) itemView.findViewById(R.id.list_gym_enabled);
            gymEnabledCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    cursor.moveToPosition(getAdapterPosition());
                    listener.onItemClicked(getTask(
                            cursor.getInt(cursor.getColumnIndex(TaskContract.TaskEntry._ID)),
                            cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.TITLE)),
                            cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.TIME)),
                            isChecked)
                    );
                }
            });
        }

    }

    /**
     * 把資料轉換成Task
     */
    private Task getTask(int id, String title, String time, boolean isEnabled) {
        return new Task()
                .setId(id)
                .setTitle(title)
                .setTime(time)
                .setEnabled(isEnabled);
    }

}
