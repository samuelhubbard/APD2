// Release Date
// Samuel Hubbard

package com.samuelhubbard.android.releasedate.Utility.Notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;

import com.samuelhubbard.android.releasedate.ListViewElements.GameObject;
import com.samuelhubbard.android.releasedate.R;
import com.samuelhubbard.android.releasedate.TrackedGamesActivity;

import java.util.Random;

public class NotificationHandler {

    // member variables
    private static NotificationManager notificationManager;
    private static PendingIntent pendingIntent;

    public static void createNotification(Context c, Intent i) {

        // pull in the object that will be used to populate the notification
        GameObject game = (GameObject) i.getSerializableExtra("GAME");
        // the content string
        String content = game.getName() + " is releasing on " + game.getFullReleaseDay();

        // create the intent and pending intent for the notification
        notificationManager = (NotificationManager)c.getSystemService(c.NOTIFICATION_SERVICE);
        Intent mIntent = new Intent(c, TrackedGamesActivity.class);
        pendingIntent = PendingIntent.getActivity(c, 0, mIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        // building the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(c);
        builder.setAutoCancel(true);
        builder.setContentTitle(game.getName());
        builder.setContentText(content);
        builder.setSmallIcon(R.drawable.ic_stat_notification);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(content));
        Bitmap largeIcon = BitmapFactory.decodeResource(c.getResources(), R.drawable.ic_launcher);
        builder.setLargeIcon(largeIcon);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(alarmSound);
        builder.setContentIntent(pendingIntent);

        Random random = new Random();
        int notificationId = random.nextInt(9999 - 1000) + 1000;

        // apply the build and show it
        notificationManager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId, builder.build());
    }
}
