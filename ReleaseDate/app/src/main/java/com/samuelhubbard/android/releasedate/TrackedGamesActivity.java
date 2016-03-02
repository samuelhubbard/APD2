// Release Date
// Samuel Hubbard

package com.samuelhubbard.android.releasedate;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.samuelhubbard.android.releasedate.Fragments.TrackedGamesFragment;
import com.samuelhubbard.android.releasedate.ListViewElements.GameObject;
import com.samuelhubbard.android.releasedate.Utility.FileManager;
import com.samuelhubbard.android.releasedate.Utility.Notifications.NotificationReceiver;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class TrackedGamesActivity extends AppCompatActivity implements TrackedGamesFragment.TrackedGamesInterface {

    ArrayList<GameObject> mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracked_games);
        setTitle(R.string.tracked_activity_title);

        // pull in the file
        String filename = "trackedgames.bin";
        mList = FileManager.loadFromFile(new File(this.getFilesDir(), filename));

        if (savedInstanceState == null) {
            Intent i = new Intent(this, NotificationReceiver.class);
            i.setAction("com.samuelhubbard.android.releasedate.RunUpdates");
            PendingIntent p = PendingIntent.getBroadcast(this, 404, i, PendingIntent.FLAG_UPDATE_CURRENT);

            // set the time for the update service to start
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, 20);

            // setup the alarm and apply it to the OS
            AlarmManager checkUpdatesAlarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
            checkUpdatesAlarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, p);

            // hang the fragment
            TrackedGamesFragment trackedFrag = TrackedGamesFragment.newInstance(mList, this);
            getFragmentManager().beginTransaction()
                    .replace(R.id.tracked_container, trackedFrag, TrackedGamesFragment.TAG)
                    .commit();
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // pull in the data from the savedinstancestate
        mList = (ArrayList<GameObject>) savedInstanceState.getSerializable("ARRAY");
    }

    @Override
    protected void onResume() {
        super.onResume();

        // update the list view when the activity resumes
        updateListView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // pull which item was tapped
        int id = item.getItemId();

        if (id == R.id.action_upcoming) {
            Intent i = new Intent(this, UpcomingGamesActivity.class);
            startActivityForResult(i, 0);
            return true;
        }

        // open the about app activity
        if (id == R.id.action_about) {
            Intent i = new Intent(this, AboutAppActivity.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void openDetails(GameObject game) {

        // check to see if the game is currently being tracked (even though it definitely is... just in case something
        // funky is happening
        String filename = "trackedgames.bin";
        boolean tracked = FileManager.isTracked(new File(this.getFilesDir(), filename), game.getGameId());

        // create the intent, save all appropriate extras, and start the activity
        Intent i = new Intent(this, GameDetailsActivity.class);
        i.putExtra("SENTFROM", "Tracked");
        i.putExtra("GAME", game);
        i.putExtra("STATUS", tracked);
        startActivity(i);
    }

    @Override
    public void removeMultipleGames(ArrayList<GameObject> a) {
        final ArrayList<GameObject> array = a;

        for (int i = 0; i < array.size(); i++) {
            GameObject game;
            // first for loop
            // starts with the selections array, sets an element from it
            game = array.get(i);

            // loops through the official tracked list
            // if anything matches, remove the notification and object from array for file
            for (int u = 0; u < mList.size(); u++) {
                if (Objects.equals(mList.get(u).getGameId(), game.getGameId())) {
                    int id = Integer.parseInt(mList.get(u).getGameId());

                    // create the intent and pending intent to cancel the alarm
                    Intent cancelIntent = new Intent(TrackedGamesActivity.this, NotificationReceiver.class);
                    cancelIntent.setAction("com.samuelhubbard.android.releasedate.ShowNotification");
                    cancelIntent.putExtra("GAME", mList.get(u));
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(TrackedGamesActivity.this, id, cancelIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);

                    // create the alarm (cancellation) and send it to the OS
                    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                    alarmManager.cancel(pendingIntent);

                    // remove that game from the new array
                    mList.remove(u);
                }
            }
        }

        // update the file
        FileManager.updateFile(mList, TrackedGamesActivity.this);

        updateListView();
    }

    protected void updateListView() {
        // create an instance of the fragment
        TrackedGamesFragment f = (TrackedGamesFragment) getFragmentManager()
                .findFragmentByTag(TrackedGamesFragment.TAG);

        // pull in file
        String filename = "trackedgames.bin";
        mList = FileManager.loadFromFile(new File(this.getFilesDir(), filename));

        // as long as the array isn't null, update the listview
        if (f != null) {
            f.updateList(mList, this);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // store member variables into savedinstancestate
        outState.putSerializable("ARRAY", mList);

        super.onSaveInstanceState(outState);
    }
}
