// Release Date
// Samuel Hubbard

package com.samuelhubbard.android.releasedate;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.samuelhubbard.android.releasedate.Fragments.DetailScreen.DetailButtonFragment;
import com.samuelhubbard.android.releasedate.Fragments.DetailScreen.DetailCoreFragment;
import com.samuelhubbard.android.releasedate.Fragments.DetailScreen.DetailDescriptionFragment;
import com.samuelhubbard.android.releasedate.Fragments.DetailScreen.DetailGalleryFragment;
import com.samuelhubbard.android.releasedate.Fragments.DetailScreen.DetailImageFragment;
import com.samuelhubbard.android.releasedate.ListViewElements.GameObject;
import com.samuelhubbard.android.releasedate.Utility.ApiHandler;
import com.samuelhubbard.android.releasedate.Utility.FileManager;
import com.samuelhubbard.android.releasedate.Utility.Notifications.NotificationReceiver;
import com.samuelhubbard.android.releasedate.Utility.VerifyConnection;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class UpcomingGameDetailActivity extends AppCompatActivity implements DetailButtonFragment.GameDetailActionInterface {

    // member variables
    String mGameId;
    GameObject mGame;
    boolean mIsTracked;

    boolean mRunning = false;
    RetrieveGameInfo mBackgroundTask;
    String mParentActivity;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_detail);

        // pull in the data from the intent
        Intent i = getIntent();
        mParentActivity = i.getStringExtra("SENTFROM");
        mIsTracked = i.getBooleanExtra("STATUS", false);

        if (Objects.equals(mParentActivity, "Tracked")) {
            mGame = (GameObject) i.getSerializableExtra("GAME");
        }

        // ensure the device is online
        ConnectivityManager manager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean isConnected = VerifyConnection.checkNetwork(manager);

        if (savedInstanceState == null) {
            // hang all of the fragments
            DetailImageFragment imageFrag = DetailImageFragment.newInstance();
            getFragmentManager().beginTransaction()
                    .replace(R.id.detail_image_container, imageFrag, DetailImageFragment.TAG)
                    .commit();

            DetailCoreFragment coreFrag = DetailCoreFragment.newInstance();
            getFragmentManager().beginTransaction()
                    .replace(R.id.detail_core_container, coreFrag, DetailCoreFragment.TAG)
                    .commit();

            DetailDescriptionFragment descriptionFrag = DetailDescriptionFragment.newInstance();
            getFragmentManager().beginTransaction()
                    .replace(R.id.detail_description_container, descriptionFrag, DetailDescriptionFragment.TAG)
                    .commit();

            DetailGalleryFragment galleryFrag = DetailGalleryFragment.newInstance();
            getFragmentManager().beginTransaction()
                    .replace(R.id.detail_gallery_container, galleryFrag, DetailGalleryFragment.TAG)
                    .commit();

            DetailButtonFragment buttonFrag = DetailButtonFragment.newInstance();
            getFragmentManager().beginTransaction()
                    .replace(R.id.detail_button_container, buttonFrag, DetailButtonFragment.TAG)
                    .commit();

            // if this came from the upcoming games list, run the async to populate the screen
            if (Objects.equals(mParentActivity, "Upcoming")) {
                mGameId = i.getStringExtra("ID");
                if (isConnected) {
                    mBackgroundTask = new RetrieveGameInfo();
                    mBackgroundTask.execute();
                } else {
                    Toast.makeText(this, "No network connection", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            // make the UI visible
            ScrollView detailView = (ScrollView) findViewById(R.id.detail_view);
            detailView.setVisibility(View.VISIBLE);

            // check to see if the async needs to be restarted
            if (Objects.equals(mParentActivity, "Upcoming")) {
                if (mGame == null) {
                    if (mRunning) {
                        mBackgroundTask.cancel(false);
                        mBackgroundTask = null;
                        mRunning = false;
                    }

                    mBackgroundTask = new RetrieveGameInfo();
                    mBackgroundTask.execute();
                }
            }
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // pull in everything from the savedinstancestate
        mGame = (GameObject) savedInstanceState.getSerializable("GAME");
        mRunning = savedInstanceState.getBoolean("RUNNING");
        mIsTracked = savedInstanceState.getBoolean("TRACKED");
    }

    @Override
    protected void onResume() {
        super.onResume();

        // as long as the game object isn't null, populate the screen
        if (mGame != null) {
            setTitle(mGame.getName());
            populateScreen(mGame);
        }
    }

    @Override
    public void trackGame() {

        // save the game to the file
        boolean saveFile = FileManager.saveToFile(mGame, this);

        if (saveFile) {
            Toast.makeText(this, R.string.tracked_game, Toast.LENGTH_SHORT).show();

            // create an instance of the button fragment
            DetailButtonFragment buttonFrag = (DetailButtonFragment) getFragmentManager()
                    .findFragmentByTag(DetailButtonFragment.TAG);

            // set the button behavior to indicate that the game is being tracked
            buttonFrag.setButtonBehavior(true);

            // setting up to create the alarm for notification
            Calendar calendar = Calendar.getInstance();
            PendingIntent pendingIntent;

            // turning all appropriate elements from the object into integers
            int month = Integer.parseInt(mGame.getMonth());
            int fixedMonth = month - 1;
            int day = Integer.parseInt(mGame.getDay());
            int year = Integer.parseInt(mGame.getYear());

            // setting the notification
            calendar.set(Calendar.MONTH, fixedMonth);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            calendar.set(Calendar.YEAR, year);

            calendar.set(Calendar.HOUR_OF_DAY, 6);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.AM_PM,Calendar.PM);

            // creating the id for the pending intent
            int id = Integer.parseInt(mGame.getGameId());

            // create the intent and pending intent for the alarm manager
            Intent i = new Intent(this, NotificationReceiver.class);
            i.setAction("com.samuelhubbard.android.releasedate.ShowNotification");
            i.putExtra("GAME", mGame);
            pendingIntent = PendingIntent.getBroadcast(this, id, i, 0);

            // create the alarm through the alarm manager and send it to the OS
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else {
            Toast.makeText(this, R.string.failed_to_track_game, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void removeGame() {
        // create a temporary array
        ArrayList<GameObject> array;

        // pull in the array from file and set it to the temporary array
        String filename = "trackedgames.bin";
        array = FileManager.loadFromFile(new File(this.getFilesDir(), filename));

        if (array != null) {
            for (int i = 0; i < array.size(); i++) {
                // game located in main array
                if (Objects.equals(array.get(i).getGameId(), mGame.getGameId())) {

                    // create the id for the pending intent to cancel the notification
                    int id = Integer.parseInt(array.get(i).getGameId());

                    // create the intent and pending intent to cancel the alarm
                    Intent cancelIntent = new Intent(this, NotificationReceiver.class);
                    cancelIntent.setAction("com.samuelhubbard.android.releasedate.ShowNotification");
                    cancelIntent.putExtra("GAME", mGame);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(this, id, cancelIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);

                    // create the alarm (cancellation) and send it to the OS
                    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                    alarmManager.cancel(pendingIntent);

                    // remove that game from the new array
                    array.remove(i);

                    // update the file
                    FileManager.updateFile(array, this);

                    if (Objects.equals(mParentActivity, "Tracked")) {
                        finish();
                    }
                    break;
                }
            }
        }
    }

    private class RetrieveGameInfo extends AsyncTask<Void, Void, GameObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // set the visibility on screen to show the progress bar
            FrameLayout detailProgress = (FrameLayout) findViewById(R.id.progress_detail);
            ScrollView detailView = (ScrollView) findViewById(R.id.detail_view);

            detailProgress.setVisibility(View.VISIBLE);
            detailView.setVisibility(View.GONE);

            // lock the device orientation
            int currentOrientation = getResources().getConfiguration().orientation;
            if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
            }

            // indicate that the thread is now active
            mRunning = true;
        }

        @Override
        protected GameObject doInBackground(Void... voids) {

            // pull in raw game data
            String rawGameData = ApiHandler.retrieveGameDetail(mGameId);

            if (rawGameData != null) {
                // parse raw game data into workable object
                GameObject parsedObject = ApiHandler.parseGame(rawGameData);

                if (parsedObject != null) {
                    // as long as everything worked, return that object
                    return parsedObject;
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(GameObject game) {
            super.onPostExecute(game);

            // unlock the screen orientation
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

            // as long as the object isn't null
            if (game != null) {
                // set the object to the member object
                mGame = game;

                // populate the screen
                setTitle(game.getName());
                populateScreen(game);

            }

            // indicate that the thread is no longer running
            mRunning = false;
        }
    }

    public void populateScreen(GameObject game) {
        if (game != null) {
            // create instances of all of the fragments that hold game data
            DetailImageFragment imageFrag = (DetailImageFragment) getFragmentManager()
                    .findFragmentByTag(DetailImageFragment.TAG);

            DetailCoreFragment coreFrag = (DetailCoreFragment) getFragmentManager()
                    .findFragmentByTag(DetailCoreFragment.TAG);

            DetailDescriptionFragment descFrag = (DetailDescriptionFragment) getFragmentManager()
                    .findFragmentByTag(DetailDescriptionFragment.TAG);

            DetailButtonFragment buttonFrag = (DetailButtonFragment) getFragmentManager()
                    .findFragmentByTag(DetailButtonFragment.TAG);

            // call their populate methods, sending in the game object
            imageFrag.populateImage(game, this);
            coreFrag.populateCore(game);
            descFrag.populateSummary(game);
            buttonFrag.setButtonBehavior(mIsTracked);

            // set the visibility of the view so the game data is shown
            FrameLayout detailProgress = (FrameLayout) findViewById(R.id.progress_detail);
            ScrollView detailView = (ScrollView) findViewById(R.id.detail_view);

            detailProgress.setVisibility(View.GONE);
            detailView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // save to savedinstancestate
        outState.putSerializable("GAME", mGame);
        outState.putBoolean("RUNNING", mRunning);
        outState.putBoolean("TRACKED", mIsTracked);

        super.onSaveInstanceState(outState);
    }
}
