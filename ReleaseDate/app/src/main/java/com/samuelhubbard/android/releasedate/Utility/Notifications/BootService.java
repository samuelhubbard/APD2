// Release Date
// Samuel Hubbard

package com.samuelhubbard.android.releasedate.Utility.Notifications;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.samuelhubbard.android.releasedate.ListViewElements.GameObject;
import com.samuelhubbard.android.releasedate.R;
import com.samuelhubbard.android.releasedate.Utility.ApiHandler;
import com.samuelhubbard.android.releasedate.Utility.FileManager;
import com.samuelhubbard.android.releasedate.Utility.VerifyConnection;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;
import java.util.Random;

public class BootService extends IntentService {

    public BootService() {
        super("BootService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("TESTING", "Boot service started.");
        ArrayList<GameObject> mList;
        ArrayList<GameObject> updateArray;

        String filename = "trackedgames.bin";
        mList = FileManager.loadFromFile(new File(this.getFilesDir(), filename));

        if (mList != null && mList.size() > 0) {
            // pull updates
            updateArray = pullUpdates(mList);

            if (updateArray != null && updateArray.size() > 0) {
                // cross reference arrays to check for updates

                for (int i = 0; i < updateArray.size(); i++) {
                    if (!Objects.equals(mList.get(i).getMonth(), updateArray.get(i).getMonth()) ||
                            !Objects.equals(mList.get(i).getDay(), updateArray.get(i).getDay()) ||
                            !Objects.equals(mList.get(i).getYear(), updateArray.get(i).getYear())) {

                        if (Objects.equals(updateArray.get(i).getDay(), "null") &&
                                Objects.equals(updateArray.get(i).getMonth(), "null") &&
                                Objects.equals(updateArray.get(i).getYear(), "null")) {

                            String removalTitle = mList.get(i).getName();
                            String removalContent = "Game has released! Removed from your tracked games.";
                            sendNotification(removalTitle, removalContent);

                            // remove the game from the tracked list
                            mList.remove(i);
                            updateArray.remove(i);
                        }
                        // update the app file
                        mList.get(i).setDay(updateArray.get(i).getDay());
                        mList.get(i).setMonth(updateArray.get(i).getMonth());
                        mList.get(i).setYear(updateArray.get(i).getYear());

                        String updateTitle = mList.get(i).getName();
                        String updateContent = "Release date update to " + mList.get(i).getFullReleaseDay() + ".";
                        sendNotification(updateTitle, updateContent);
                    }
                }

                // Re-create all of the scheduled notifications
                for (int i = 0; i < mList.size(); i++) {
                    // instance variables
                    Calendar calendar = Calendar.getInstance();
                    PendingIntent updatePendingIntent;

                    // setting
                    int month = Integer.parseInt(mList.get(i).getMonth());
                    int fixedMonth = month - 1;
                    int day = Integer.parseInt(mList.get(i).getDay());
                    int fixedDay = day - 1;
                    int year = Integer.parseInt(mList.get(i).getYear());

                    // setting when the notification will fire
                    calendar.set(Calendar.MONTH, fixedMonth);
                    calendar.set(Calendar.DAY_OF_MONTH, fixedDay);
                    calendar.set(Calendar.YEAR, year);

                    calendar.set(Calendar.HOUR_OF_DAY, 6);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.AM_PM, Calendar.PM);

                    // creating the id for the pending intent
                    int id = Integer.parseInt(mList.get(i).getGameId());

                    // set the intent and pending intent for the alarm
                    Intent updateIntent = new Intent(BootService.this, NotificationReceiver.class);
                    updateIntent.setAction("com.samuelhubbard.android.releasedate.ShowNotification");
                    updateIntent.putExtra("GAME", mList.get(i));
                    updatePendingIntent = PendingIntent.getBroadcast(BootService.this, id, updateIntent, 0);

                    // create the alarm and send it to the OS
                    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), updatePendingIntent);
                }

                // save those updates
                boolean saveUpdates = FileManager.updateFile(mList, BootService.this);
            } else {
                Log.i("TESTING", "Boot service stopped - updateArray returned null.");
            }
        } else {
            Log.i("TESTING", "Boot service stopped - No file.");
        }

        Intent i = new Intent(this, NotificationReceiver.class);
        i.setAction("com.samuelhubbard.android.releasedate.RunUpdates");
        PendingIntent p = PendingIntent.getBroadcast(this, 404, i, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar updateCalendar = Calendar.getInstance();
        updateCalendar.setTimeInMillis(System.currentTimeMillis());
        updateCalendar.set(Calendar.HOUR_OF_DAY, 20);

        AlarmManager checkUpdatesAlarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        checkUpdatesAlarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, updateCalendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, p);
        Log.i("TESTING", "Boot service stopped - Complete with no errors.");
        stopSelf();

    }

    protected ArrayList<GameObject> pullUpdates(ArrayList<GameObject> a) {
        ArrayList<GameObject> updateArray = new ArrayList<>();

        // Make sure the device is online
        ConnectivityManager manager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean isConnected = VerifyConnection.checkNetwork(manager);

        if (isConnected) {
            if (a != null || a.size() > 0) {
                // get updated array created

                for (int i = 0; i < a.size(); i++) {
                    String rawData = ApiHandler.checkForUpdates(a.get(i));

                    if (rawData != null) {
                        // parse the data
                        GameObject game = ApiHandler.parseGame(rawData);

                        if (game != null) {
                            updateArray.add(game);
                        } else {
                            Log.i("TESTING", "Boot service stopped - Problem parsing.");
                            return null;
                        }
                    } else {
                        Log.i("TESTING", "Boot service stopped - API pull failed.");
                        return null;
                    }
                }
            }
            return updateArray;
        } else {
            Log.i("TESTING", "Boot service stopped - No connection.");
            return null;
        }
    }

    protected void sendNotification(String title, String content) {
        NotificationManager notificationManager;
        Intent mIntent = new Intent(this, UpdateService.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, mIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        // building the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setAutoCancel(true);
        builder.setContentTitle(title);
        builder.setContentText(content);
        builder.setSmallIcon(R.drawable.ic_stat_notification);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(content));
        Bitmap largeIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_launcher);
        builder.setLargeIcon(largeIcon);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(alarmSound);
        builder.setContentIntent(pendingIntent);

        Random random = new Random();
        int notificationId = random.nextInt(9999 - 1000) + 1000;

        // apply the build and show it
        notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId, builder.build());
    }
}
