/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.ntust.prlab.gymmanager.schedule;

import android.content.Context;
import android.os.AsyncTask;

import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.firebase.jobdispatcher.RetryStrategy;

import edu.ntust.prlab.gymmanager.utils.NotificationUtils;

public class TaskFirebaseJobService extends JobService {

    private AsyncTask<Void, Void, Void> fetchTaskRemindTask;

    /**
     * 當每天的鬧鐘事件要觸發的時候，就會發出通知告訴使用者。
     */
    @Override
    public boolean onStartJob(final JobParameters jobParameters) {
        fetchTaskRemindTask = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                Context context = getApplicationContext();
                NotificationUtils.notifyUserOfTaskExpired(context);
                jobFinished(jobParameters, false);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                jobFinished(jobParameters, false);
            }
        };

        fetchTaskRemindTask.execute();
        return true;
    }

    /**
     * 取消每天的鬧鐘事件
     */
    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        if (fetchTaskRemindTask != null) {
            fetchTaskRemindTask.cancel(true);
        }
        return true;
    }
}