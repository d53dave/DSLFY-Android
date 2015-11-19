package net.d53dev.dslfy.android.events;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by davidsere on 19/11/15.
 */
public class AlarmBroadcastReceived extends BroadcastReceiver {

    public static String NOTIFICATION_ID = "dslfy-notification-id";
    public static String NOTIFICATION = "dsfly-notification";

    public void onReceive(Context context, Intent intent) {

        NotificationManager notificationManager =
                (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = intent.getParcelableExtra(NOTIFICATION);
        int id = intent.getIntExtra(NOTIFICATION_ID, 0);
        notificationManager.notify(id, notification);

    }
}
