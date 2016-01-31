package com.samuelhubbard.android.releasedate;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.samuelhubbard.android.releasedate.Fragments.GameDetailsFragment;
import com.samuelhubbard.android.releasedate.Fragments.GameImagesFragment;
import com.samuelhubbard.android.releasedate.ListViewElements.GameObject;
import com.samuelhubbard.android.releasedate.Utility.ApiHandler;
import com.samuelhubbard.android.releasedate.Utility.FileManager;
import com.samuelhubbard.android.releasedate.Utility.Notifications.NotificationReceiver;
import com.samuelhubbard.android.releasedate.Utility.VerifyConnection;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class GameDetailsActivity extends AppCompatActivity implements GameDetailsFragment.DetailInterface,
        GameImagesFragment.GameGridInterface {

    // member variables
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    public static GameObject mGame;
    public static boolean mStatus;
    public static Context mContext;
    public static boolean mRunning;
    public static boolean mUpdated;
    public static boolean mIsConnected;
    public static String mGameId;
    public static String mSentFrom;
    private RetrieveGameInfo mBackgroundTask;

    // fragments
    private GameDetailsFragment mDetailsFragment = new GameDetailsFragment();
    private GameImagesFragment mImagesFragment = new GameImagesFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_details);

        // check for network connection
        ConnectivityManager manager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        mIsConnected = VerifyConnection.checkNetwork(manager);

        mContext = this;
        Intent i = getIntent();
        mSentFrom = i.getStringExtra("SENTFROM");
        if (savedInstanceState == null) {
            if (Objects.equals(mSentFrom, "Tracked")) {
                mGame = (GameObject) i.getSerializableExtra("GAME");
                String filename = "trackedgames.bin";
                mStatus = FileManager.isTracked(new File(GameDetailsActivity.this.getFilesDir(), filename), mGame.getGameId());
            } else {
                if (mIsConnected) {
                    mGameId = i.getStringExtra("ID");
                    mBackgroundTask = new RetrieveGameInfo();
                    mBackgroundTask.execute();
                } else {
                    Toast.makeText(this, "Unable to load - No internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            mGame = (GameObject) savedInstanceState.getSerializable("GAME");
        }

        // create the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // Set up the TabLayout
        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        // add padding to the views based on tab layout height
        ViewTreeObserver vto = tabLayout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mViewPager.setPadding(0,0,0,tabLayout.getHeight() + 10);
            }
        });

        // if there is game data present, set the title and set the tracked status
        if (mGame != null) {
            setTitle(mGame.getName());
            String filename = "trackedgames.bin";
            mStatus = FileManager.isTracked(new File(GameDetailsActivity.this.getFilesDir(), filename), mGame.getGameId());
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void trackGame() {
        // save the game to the file
        boolean saveFile = FileManager.saveToFile(mGame, this);

        if (saveFile) {
            Toast.makeText(this, R.string.tracked_game, Toast.LENGTH_SHORT).show();

            mStatus = true;
            mDetailsFragment.updateButtonBehavior(mStatus);

            // setting up to create the alarm for notification
            Calendar calendar = Calendar.getInstance();
            PendingIntent pendingIntent;

            // turning all appropriate elements from the object into integers
            int month = Integer.parseInt(mGame.getMonth());
            int fixedMonth = month - 1;
            int day = Integer.parseInt(mGame.getDay());
            int fixedDay = day - 1;
            int year = Integer.parseInt(mGame.getYear());

            // setting the notification
            calendar.set(Calendar.MONTH, fixedMonth);
            calendar.set(Calendar.DAY_OF_MONTH, fixedDay);
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

                    if (Objects.equals(mSentFrom, "Tracked")) {
                        finish();
                    } else {
                        // set tracked handling button status and update it
                        mStatus = false;
                        mDetailsFragment.updateButtonBehavior(mStatus);
                        Toast.makeText(this, R.string.removed_game, Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
            }
        }
    }

    // open the image viewer
    @Override
    public void sendImageAddress(int position, ArrayList<String> array) {
        Intent i = new Intent(this, DetailImageView.class);
        i.putExtra("POSITION", position);
        i.putExtra("ARRAY", mGame.getImages());
        i.putExtra("TITLE", mGame.getName());
        startActivity(i);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // restore the game data and button status
        mGame = (GameObject) savedInstanceState.getSerializable("GAME");
        mStatus = savedInstanceState.getBoolean("STATUS");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        // save the game data and button status
        outState.putSerializable("GAME", mGame);
        outState.putBoolean("STATUS", mStatus);

        super.onSaveInstanceState(outState);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;

            // set which fragments are associated which tab
            switch (position) {
                case 0:
                    fragment = mDetailsFragment;
                    break;
                case 1:
                    fragment = mImagesFragment;
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // set the tab titles
            switch (position) {
                case 0:
                    return "Details";
                case 1:
                    return "Images";
            }
            return null;
        }
    }

    private class RetrieveGameInfo extends AsyncTask<Void, Void, GameObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // lock the current orientation for the background task
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
                String filename = "trackedgames.bin";
                mStatus = FileManager.isTracked(new File(GameDetailsActivity.this.getFilesDir(), filename), mGame.getGameId());

                // populate the screen
                setTitle(game.getName());
                populateScreen(game);

            }

            // indicate that the thread is no longer running
            mRunning = false;
            mUpdated = true;
        }
    }

    @Override
    protected void onDestroy() {
        // nullify the game data and game id
        mGame = null;
        mGameId = null;
        super.onDestroy();
    }

    public void populateScreen(GameObject game) {
        // populate the UI
        mDetailsFragment.populate(game, mStatus, GameDetailsActivity.this);
        mImagesFragment.populate(game);
    }
}
