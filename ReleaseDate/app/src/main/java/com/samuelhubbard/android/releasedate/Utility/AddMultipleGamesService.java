// Release date
// Samuel Hubbard

package com.samuelhubbard.android.releasedate.Utility;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.widget.Toast;

import com.samuelhubbard.android.releasedate.ListViewElements.GameListObject;
import com.samuelhubbard.android.releasedate.ListViewElements.GameObject;
import com.samuelhubbard.android.releasedate.Utility.Notifications.NotificationReceiver;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

public class AddMultipleGamesService extends IntentService {
    Handler mHandler;

    public AddMultipleGamesService() {
        super("AddMultipleGamesService");
        mHandler = new Handler(); // instantiate a new handler to handle the toast notification
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        // pull in the array from the intent / set the name of the file
        ArrayList<GameListObject> a = (ArrayList<GameListObject>) intent.getSerializableExtra("GAMES");
        final String filename = "trackedgames.bin";

        // run a for loop that determines if any of the games in the array are already tracked
        // if the are, it removes them from the array
        for (int u = 0; u < a.size(); u++) {
            boolean isTracked = FileManager.isTracked(new File(this.getFilesDir(), filename), a.get(u).getGameId());

            if (isTracked) {
                a.remove(u);
            }
        }

        // Make sure the device is online
        ConnectivityManager manager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean isConnected = VerifyConnection.checkNetwork(manager);

        if (isConnected) {

            for (int i = 0; i < a.size(); i++) {
                // pull in raw game data
                String rawGameData = ApiHandler.retrieveGameDetail(a.get(i).getGameId());

                if (rawGameData != null) {
                    // parse raw game data into workable object
                    GameObject parsedObject = ApiHandler.parseGame(rawGameData);

                    if (parsedObject != null) {
                        boolean saveGame = FileManager.saveToFile(parsedObject, this);


                        // setting up to create the alarm for notification
                        Calendar calendar = Calendar.getInstance();
                        PendingIntent pendingIntent;

                        // turning all appropriate elements from the object into integers
                        int month = Integer.parseInt(parsedObject.getMonth());
                        int fixedMonth = month - 1;
                        int day = Integer.parseInt(parsedObject.getDay());
                        int fixedDay = day - 1;
                        int year = Integer.parseInt(parsedObject.getYear());

                        // setting the notification
                        calendar.set(Calendar.MONTH, fixedMonth);
                        calendar.set(Calendar.DAY_OF_MONTH, fixedDay);
                        calendar.set(Calendar.YEAR, year);

                        calendar.set(Calendar.HOUR_OF_DAY, 6);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.AM_PM,Calendar.PM);

                        // creating the id for the pending intent
                        int id = Integer.parseInt(parsedObject.getGameId());

                        // create the intent and pending intent for the alarm manager
                        Intent notifyIntent = new Intent(this, NotificationReceiver.class);
                        notifyIntent.setAction("com.samuelhubbard.android.releasedate.ShowNotification");
                        notifyIntent.putExtra("GAME", parsedObject);
                        pendingIntent = PendingIntent.getBroadcast(this, id, notifyIntent, 0);

                        // create the alarm through the alarm manager and send it to the OS
                        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    }
                }
            }
        }

        // send the toast notification
        mHandler.post(new DisplayToast(this, "Games Added!"));

        // stops the service
        stopSelf();
    }

    // start a runnable to display a toast message
    public class DisplayToast implements Runnable {
        private final Context mContext;
        String mMessage;

        public DisplayToast(Context mContext, String message) {
            this.mContext = mContext;
            mMessage = message;
        }

        @Override
        public void run() {
            Toast.makeText(mContext, mMessage, Toast.LENGTH_SHORT).show();
        }
    }
}
