// Release Date
// Samuel Hubbard

package com.samuelhubbard.android.releasedate;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.samuelhubbard.android.releasedate.Fragments.TrackedGamesFragment;
import com.samuelhubbard.android.releasedate.ListViewElements.GameObject;
import com.samuelhubbard.android.releasedate.Utility.ApiHandler;
import com.samuelhubbard.android.releasedate.Utility.FileManager;
import com.samuelhubbard.android.releasedate.Utility.VerifyConnection;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class TrackedGamesActivity extends AppCompatActivity implements TrackedGamesFragment.TrackedGamesInterface {

    ArrayList<GameObject> mList;

    boolean mRunning = false;
    boolean mUpdated = false;
    UpdateGameReleaseDates mBackgroundTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracked_games);
        setTitle(R.string.tracked_activity_title);

        // pull in the file
        String filename = "trackedgames.bin";
        mList = FileManager.loadFromFile(new File(this.getFilesDir(), filename));

        // check to see if the device is online
        ConnectivityManager manager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean isConnected = VerifyConnection.checkNetwork(manager);

        // hang the fragment
        if (savedInstanceState == null) {
            TrackedGamesFragment trackedFrag = TrackedGamesFragment.newInstance(mList, this);
            getFragmentManager().beginTransaction()
                    .replace(R.id.tracked_container, trackedFrag, TrackedGamesFragment.TAG)
                    .commit();

            // internet connection -> update game release dates
            if (isConnected) {
                mBackgroundTask = new UpdateGameReleaseDates();
                mBackgroundTask.execute();
            } else {
                // set the appropriate visibilities
                mUpdated = true;
                FrameLayout uiFrame = (FrameLayout) findViewById(R.id.tracked_container);
                FrameLayout progressFrame = (FrameLayout) findViewById(R.id.tracked_progress_bar);

                uiFrame.setVisibility(View.VISIBLE);
                progressFrame.setVisibility(View.GONE);
            }

        } else {
            // pull in the instance state for the async handling booleans
            mRunning = savedInstanceState.getBoolean("RUNNING");
            mUpdated = savedInstanceState.getBoolean("UPDATED");

            // essentially, if the list isn't null and it hasn't been updated
            // update it.. if the async task is currently running and there was a state change
            // cancel and restart the async task as this will cause problems otherwise
            if (!mUpdated && mList != null) {
                if (mRunning) {
                    mBackgroundTask.cancel(false);
                    mBackgroundTask = null;
                    mRunning = false;
                }

                mBackgroundTask = new UpdateGameReleaseDates();
                mBackgroundTask.execute();
            } else {
                FrameLayout uiFrame = (FrameLayout) findViewById(R.id.tracked_container);
                FrameLayout progressFrame = (FrameLayout) findViewById(R.id.tracked_progress_bar);

                uiFrame.setVisibility(View.VISIBLE);
                progressFrame.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // pull in the data from the savedinstancestate
        mList = (ArrayList<GameObject>) savedInstanceState.getSerializable("ARRAY");
        mUpdated = savedInstanceState.getBoolean("UPDATED");
        mRunning = savedInstanceState.getBoolean("RUNNING");
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

        // open the upcoming games activity
        if (!mRunning) {
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

            if (id == R.id.action_test) {
                // create the intent and pending intent for the notification
                NotificationManager notificationManager;
                Intent mIntent = new Intent(this, TrackedGamesActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, mIntent, PendingIntent.FLAG_CANCEL_CURRENT);

                // building the notification
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
                builder.setAutoCancel(true);
                builder.setContentTitle("Test");
                builder.setContentText("This is a test notification");
                builder.setSmallIcon(R.drawable.ic_stat_notification);
                builder.setStyle(new NotificationCompat.BigTextStyle().bigText("This is a test notification"));
                Bitmap largeIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_launcher);
                builder.setLargeIcon(largeIcon);
                Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                builder.setSound(alarmSound);
                builder.setContentIntent(pendingIntent);

                // apply the build and show it
                notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(1, builder.build());
            }
        } else {
            Toast.makeText(this, "Please Wait - Release Dates Updating", Toast.LENGTH_SHORT).show();
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
        Intent i = new Intent(this, UpcomingGameDetailActivity.class);
        i.putExtra("SENTFROM", "Tracked");
        i.putExtra("GAME", game);
        i.putExtra("STATUS", tracked);
        startActivity(i);
    }

    protected void updateListView() {
        // create an instance of the fragment
        TrackedGamesFragment f = (TrackedGamesFragment) getFragmentManager()
                .findFragmentByTag(TrackedGamesFragment.TAG);

        // pull in file
        String filename = "trackedgames.bin";
        ArrayList<GameObject> list = FileManager.loadFromFile(new File(this.getFilesDir(), filename));

        // as long as the array isn't null, update the listview
        if (f != null) {
            f.updateList(list);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // store member variables into savedinstancestate
        outState.putSerializable("ARRAY", mList);
        outState.putBoolean("UPDATED", mUpdated);
        outState.putBoolean("RUNNING", mRunning);

        super.onSaveInstanceState(outState);
    }

    private class UpdateGameReleaseDates extends AsyncTask<Void, Void, ArrayList<GameObject>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // indicate that the thread is now active
            mRunning = true;

            // lock the orientation for the async
            int currentOrientation = getResources().getConfiguration().orientation;
            if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
            }

            // set the visibility to show the progress bar
            FrameLayout uiFrame = (FrameLayout) findViewById(R.id.tracked_container);
            FrameLayout progressFrame = (FrameLayout) findViewById(R.id.tracked_progress_bar);

            uiFrame.setVisibility(View.GONE);
            progressFrame.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<GameObject> doInBackground(Void... voids) {
            ArrayList<GameObject> updateList = new ArrayList<>();

            // check to see if the device is online
            ConnectivityManager manager = (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);

            boolean isConnected = VerifyConnection.checkNetwork(manager);

            if (isConnected && mList != null) {
                for (int i = 0; i < mList.size(); i++) {
                    // pull in the raw data
                    String rawData = ApiHandler.checkForUpdates(mList.get(i));

                    if (rawData != null) {
                        // parse the raw data
                        GameObject game = ApiHandler.parseGame(rawData);

                        if (game != null) {
                            // add that to the array
                            updateList.add(game);
                        }
                    }
                }
                // return the temp array
                return updateList;
            }

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<GameObject> array) {
            super.onPostExecute(array);

            if (array != null) {
                if (array.size() > 0) {
                    for (int i = 0; i < mList.size(); i++) {
                        // set the date on the main array to the date in the temp array
                        mList.get(i).setDay(array.get(i).getDay());
                        mList.get(i).setMonth(array.get(i).getMonth());
                        mList.get(i).setYear(array.get(i).getYear());

                        // if the date is null, remove it from the array (game has released)
                        if (Objects.equals(mList.get(i).getDay(), "null") &&
                                Objects.equals(mList.get(i).getMonth(), "null") &&
                                Objects.equals(mList.get(i).getYear(), "null")) {
                            mList.remove(i);
                        }
                    }
                    // Save the array back to file
                    boolean saveUpdates = FileManager.updateFile(mList, TrackedGamesActivity.this);
                    if (saveUpdates) {
                        Toast.makeText(TrackedGamesActivity.this, "Updated Games", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            // indicate that the thread is no longer running
            mRunning = false;
            mUpdated = true;

            // unlock screen orientation changes
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

            // set the visibility so the list view is visible
            FrameLayout uiFrame = (FrameLayout) findViewById(R.id.tracked_container);
            FrameLayout progressFrame = (FrameLayout) findViewById(R.id.tracked_progress_bar);

            progressFrame.setVisibility(View.GONE);
            uiFrame.setVisibility(View.VISIBLE);
        }
    }
}
