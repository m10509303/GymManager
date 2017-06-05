package edu.ntust.prlab.gymmanager.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import edu.ntust.prlab.gymmanager.MainActivity;
import edu.ntust.prlab.gymmanager.R;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NotificationUtils {

    private static final int GYM_NOTIFICATION_ID = 3004;

    /**
     * 用來發送通知給使用者
     */
    public static void notifyUserOfTaskExpired(Context context) {

    // The PendingIntent to launch our activity if the user selects this notification
    PendingIntent contentIntent = PendingIntent.getActivity(context,
            0, new Intent(context, MainActivity.class), 0);

    // Set the info for the views that show in the notification panel.
    Notification notification = new Notification.Builder(context)
            .setSmallIcon(R.drawable.ic_move_black_24dp)  // the status icon
            .setWhen(System.currentTimeMillis())  // the time stamp
            .setContentTitle(context.getText(R.string.message_remind))  // the label of the entry
            .setPriority(Notification.PRIORITY_HIGH)
            .setContentText(context.getText(R.string.message_move_move))  // the contents of the entry
            .setContentIntent(contentIntent)  // The intent to send when the entry is clicked
            .build();

    // Send the notification.
        ((NotificationManager) context.getSystemService(NOTIFICATION_SERVICE))
                .notify(GYM_NOTIFICATION_ID, notification);
    }

}
