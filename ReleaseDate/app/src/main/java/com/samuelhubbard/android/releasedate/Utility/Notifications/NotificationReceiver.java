// Release Date
// Samuel Hubbard

package com.samuelhubbard.android.releasedate.Utility.Notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        // if the device just restarted
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            // start the service to check for updates
            Intent bootIntent = new Intent(context, BootService.class);
            context.startService(bootIntent);
        // if the intent action is a notification that needs to be displayed
        } else if (intent.getAction().equals("com.samuelhubbard.android.releasedate.ShowNotification")) {
            // create and display the notification
            try {
                NotificationHandler.createNotification(context, intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (intent.getAction().equals("com.samuelhubbard.android.releasedate.RunUpdates")) {
            Intent updateIntent = new Intent(context, UpdateService.class);
            context.startService(updateIntent);
        }

    }
}
