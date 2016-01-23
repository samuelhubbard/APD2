// Release Date
// Samuel Hubbard

package com.samuelhubbard.android.releasedate.Utility.Notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.samuelhubbard.android.releasedate.ListViewElements.GameObject;
import com.samuelhubbard.android.releasedate.Utility.ApiHandler;
import com.samuelhubbard.android.releasedate.Utility.FileManager;
import com.samuelhubbard.android.releasedate.Utility.VerifyConnection;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class NotificationService extends Service {
    private ArrayList<GameObject> mList;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // TODO: REMOVE TOAST FOR FINAL VERSION - FOR TESTING PURPOSES
        Toast.makeText(this, "Service opened", Toast.LENGTH_SHORT).show();

        // pull in the array from file
        String filename = "trackedgames.bin";
        mList = FileManager.loadFromFile(new File(this.getFilesDir(), filename));

        // ensure the device has an internet connection
        ConnectivityManager manager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean isConnected = VerifyConnection.checkNetwork(manager);

        if (isConnected) {
            // run the async task as long as there is an internet connection
            CheckReleaseDates backgroundTask = new CheckReleaseDates();
            backgroundTask.execute();
        } else {
            Toast.makeText(this, "No connection - Games not updated", Toast.LENGTH_SHORT).show();
            stopSelf();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        // TODO: REMOVE TOAST FOR FINAL VERSION - FOR TESTING PURPOSES
        Toast.makeText(this, "Service closed.", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    private class CheckReleaseDates extends AsyncTask<Void, Void, ArrayList<GameObject>> {

        @Override
        protected ArrayList<GameObject> doInBackground(Void... params) {
            // create array list
            ArrayList<GameObject> updateList = new ArrayList<>();

            // check to see if the device is online
            ConnectivityManager manager = (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);

            boolean isConnected = VerifyConnection.checkNetwork(manager);

            if (isConnected && mList != null) {
                // for loop that pulls information for each game
                for (int i = 0; i < mList.size(); i++) {
                    // String that holds the game raw data
                    String rawData = ApiHandler.checkForUpdates(mList.get(i));

                    if (rawData != null) {
                        // parse raw data string into a workable object
                        GameObject game = ApiHandler.parseGame(rawData);

                        if (game != null) {
                            // add that new object to the temp array
                            updateList.add(game);
                        }
                    }
                }
                // as long as everything worked out, return the temp array
                return updateList;
            }

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<GameObject> gameObjects) {
            super.onPostExecute(gameObjects);

            if (gameObjects != null) {

                if (gameObjects.size() > 0) {
                    for (int i = 0; i < gameObjects.size(); i++) {

                        // instance variables
                        Calendar calendar = Calendar.getInstance();
                        PendingIntent pendingIntent;

                        // setting
                        int month = Integer.parseInt(gameObjects.get(i).getMonth());
                        int fixedMonth = month - 1;
                        int day = Integer.parseInt(gameObjects.get(i).getDay());
                        int year = Integer.parseInt(gameObjects.get(i).getYear());

                        // setting when the notification will fire
                        calendar.set(Calendar.MONTH, fixedMonth);
                        calendar.set(Calendar.DAY_OF_MONTH, day);
                        calendar.set(Calendar.YEAR, year);

                        calendar.set(Calendar.HOUR_OF_DAY, 6);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.AM_PM, Calendar.PM);

                        // creating the id for the pending intent
                        int id = Integer.parseInt(gameObjects.get(i).getGameId());

                        // set the intent and pending intent for the alarm
                        Intent intent2 = new Intent(NotificationService.this, NotificationReceiver.class);
                        intent2.setAction("com.samuelhubbard.android.releasedate.ShowNotification");
                        intent2.putExtra("GAME", gameObjects.get(i));
                        pendingIntent = PendingIntent.getBroadcast(NotificationService.this, id, intent2, 0);

                        // create the alarm and send it to the OS
                        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    }
                    // TODO: REMOVE TOAST FOR FINAL VERSION - FOR TESTING ONLY
                    Toast.makeText(NotificationService.this, "Should have worked!", Toast.LENGTH_SHORT).show();

                    // update games
                    for (int i = 0; i < mList.size(); i++) {
                        // loop through the file array and simply it to the date from the temp array
                        mList.get(i).setDay(gameObjects.get(i).getDay());
                        mList.get(i).setMonth(gameObjects.get(i).getMonth());
                        mList.get(i).setYear(gameObjects.get(i).getYear());

                        // if anything no longer has a date (means the game released), remove it from the array
                        if (Objects.equals(mList.get(i).getDay(), "null") &&
                                Objects.equals(mList.get(i).getMonth(), "null") &&
                                Objects.equals(mList.get(i).getYear(), "null")) {
                            mList.remove(i);
                        }
                    }

                    // save those updates
                    boolean saveUpdates = FileManager.updateFile(mList, NotificationService.this);

                    // stop the service
                    stopSelf();
                }
            }
        }
    }
}
