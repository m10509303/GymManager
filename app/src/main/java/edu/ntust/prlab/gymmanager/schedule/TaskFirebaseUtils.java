package edu.ntust.prlab.gymmanager.schedule;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import edu.ntust.prlab.gymmanager.R;

/**
 * 用來發送第一次的提醒訊息以及之後每天的提醒訊息，
 * 之所以分成READY和沒有READY是因為Firebase排程的時間週期必須要固定才行，
 * 因為第一次提醒跟每天提醒的時間週期可能不相同，所以分成兩種。
 */
public class TaskFirebaseUtils {

    /**
     * 要重複的時間週期(小時)
     */
    private static final int REMIND_INTERVAL_HOURS = 24;

    /**
     * 要重複的時間週期(分鐘)
     */
    private static final int REMIND_INTERVAL_SECONDS = (int) TimeUnit.HOURS.toSeconds(REMIND_INTERVAL_HOURS);

    /**
     * 可允許的誤差時間
     */
    private static final int REMIND_FLEXTIME_SECONDS = (int) 30;

    private static final String READY_GYM_SYNC_TAG = "ready-gym-sync";
    private static final String GYM_SYNC_TAG = "gym-sync";

    /**
     * 設定第一次要提醒的事件
     */
    public static void scheduleReadyFirebaseJobDispatcherSync(@NonNull final Context context,
                                                              int taskId,
                                                              int hourOfDay,
                                                              int minute) {
        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        Calendar now = Calendar.getInstance();
        Calendar schedule = Calendar.getInstance();
        schedule.set(Calendar.HOUR_OF_DAY, hourOfDay);
        schedule.set(Calendar.MINUTE, minute);
        schedule.set(Calendar.SECOND, 0);

        //如果要設定的時間小於現在的時間的話，代表是明天的事件
        if (now.after(schedule)) {
            schedule.add(Calendar.DATE, 1);
        }
        int scheduleTime = (int) ((schedule.getTimeInMillis() - now.getTimeInMillis()) / 1000);

        Bundle bundle = new Bundle();
        bundle.putInt(context.getString(R.string.task_id_key), taskId);
        Job syncSunshineJob = dispatcher.newJobBuilder()
                .setService(ReadyTaskFirebaseJobService.class)
                .setTag(READY_GYM_SYNC_TAG + taskId)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setLifetime(Lifetime.FOREVER)
                //不會重複觸發
                .setRecurring(false)
                .setTrigger(Trigger.executionWindow(
                        scheduleTime,
                        scheduleTime + REMIND_FLEXTIME_SECONDS))
                .setReplaceCurrent(true)
                .setExtras(bundle)
                .build();

        /* Schedule the Job with the dispatcher */
        dispatcher.schedule(syncSunshineJob);
    }

    /**
     * 取消第一次要提醒的事件。
     */
    public static void cancelScheduleReadyFirebaseJobDispatcherSync(@NonNull final Context context, int taskId) {
        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);
        dispatcher.cancel(READY_GYM_SYNC_TAG + taskId);
    }

    /**
     * 設定每天要提醒的事件。
     */
    public static void scheduleFirebaseJobDispatcherSync(@NonNull final Context context, int taskId) {
        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        Job syncSunshineJob = dispatcher.newJobBuilder()
                .setService(TaskFirebaseJobService.class)
                .setTag(GYM_SYNC_TAG + taskId)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setLifetime(Lifetime.FOREVER)
                //會重複觸發
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(
                        REMIND_INTERVAL_SECONDS,
                        REMIND_INTERVAL_SECONDS + REMIND_FLEXTIME_SECONDS))
                .setReplaceCurrent(true)
                .build();

        /* Schedule the Job with the dispatcher */
        dispatcher.schedule(syncSunshineJob);
    }

    /**
     * 取消每天要提醒的事件。
     */
    public static void cancelScheduleFirebaseJobDispatcherSync(@NonNull final Context context, int taskId) {
        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);
        dispatcher.cancel(GYM_SYNC_TAG + taskId);
    }

}
