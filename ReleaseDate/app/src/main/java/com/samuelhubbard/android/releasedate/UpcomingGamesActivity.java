// Release Date
// Samuel Hubbard

package com.samuelhubbard.android.releasedate;

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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.samuelhubbard.android.releasedate.Fragments.UpcomingFiltersFragment;
import com.samuelhubbard.android.releasedate.Fragments.UpcomingGamesFragment;
import com.samuelhubbard.android.releasedate.ListViewElements.GameListObject;
import com.samuelhubbard.android.releasedate.ListViewElements.SectionHeaderInclusion;
import com.samuelhubbard.android.releasedate.Utility.AddMultipleGamesService;
import com.samuelhubbard.android.releasedate.Utility.ApiHandler;
import com.samuelhubbard.android.releasedate.Utility.FileManager;
import com.samuelhubbard.android.releasedate.Utility.VerifyConnection;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class UpcomingGamesActivity extends AppCompatActivity implements UpcomingGamesFragment.UpcomingGamesInterface,
        UpcomingFiltersFragment.FilterInterface {

    // variables for game retrieval
    String mYear;
    String mQuarterOne;
    String mQuarterTwo;
    String mQuarterThree;
    ArrayList<GameListObject> mArray;
    ArrayList<GameListObject> mFilteredArray;
    String mCurrentFilter;

    // variables to handle the background thread
    boolean mRunning = false;
    boolean mUpdated = false;
    RetrieveUpcomingGames mBackgroundThread;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming_games);

        // get the current month and year
        Calendar c = Calendar.getInstance();
        int numericalYear = c.get(Calendar.YEAR);
        mYear = String.valueOf(numericalYear);

        int month = c.get(Calendar.MONTH);

        // determine and set the current quarter
        if (month >= 0 && month <= 2) {
            mQuarterOne = "1";
            mQuarterTwo = "2";
            mQuarterThree = "3";
        } else if (month >= 3 && month <= 5) {
            mQuarterOne = "2";
            mQuarterTwo = "3";
            mQuarterThree = "4";
        } else if (month >= 6 && month <= 8) {
            mQuarterOne = "3";
            mQuarterTwo = "4";
            mQuarterThree = "1";
        } else if (month >= 9 && month <= 11) {
            mQuarterOne = "4";
            mQuarterTwo = "1";
            mQuarterThree = "2";
        }

        // ensure the device is online
        final ConnectivityManager manager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean isConnected = VerifyConnection.checkNetwork(manager);

        if (savedInstanceState == null) {
            // define the new array
            mArray = new ArrayList<>();

            // hang the fragments
            UpcomingFiltersFragment filterFrag = UpcomingFiltersFragment.newInstance();
            getFragmentManager().beginTransaction()
                    .replace(R.id.upcoming_filters_container, filterFrag, UpcomingFiltersFragment.TAG)
                    .commit();

            UpcomingGamesFragment listFrag = UpcomingGamesFragment.newInstance();
            getFragmentManager().beginTransaction()
                    .replace(R.id.upcoming_games_container, listFrag, UpcomingGamesFragment.TAG)
                    .commit();

            if (mCurrentFilter == null) {
                mCurrentFilter = "all";

                // link to the fragment
                UpcomingFiltersFragment frag = (UpcomingFiltersFragment) getFragmentManager()
                        .findFragmentByTag(UpcomingFiltersFragment.TAG);

                // as long as the fragment isn't null
                if (frag != null) {
                    // populate the list
                    frag.setActiveFilter(mCurrentFilter, this);
                }
            }

            // if there is a connection, start the background thread
            if (isConnected) {
                mBackgroundThread = new RetrieveUpcomingGames();
                mBackgroundThread.execute();
            } else {
                FrameLayout progress = (FrameLayout) findViewById(R.id.progress_indicator);
                progress.setVisibility(View.GONE);

                FrameLayout filterContainer = (FrameLayout) findViewById(R.id.upcoming_filters_container);
                filterContainer.setVisibility(View.GONE);

                FrameLayout listContainer = (FrameLayout) findViewById(R.id.upcoming_games_container);
                listContainer.setVisibility(View.GONE);

                LinearLayout errorScreen = (LinearLayout) findViewById(R.id.upcoming_games_noconn);
                errorScreen.setVisibility(View.VISIBLE);

                TextView noConnButton = (TextView) findViewById(R.id.upcoming_games_noconn_button);
                noConnButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isConnected = VerifyConnection.checkNetwork(manager);

                        if (isConnected) {
                            mBackgroundThread = new RetrieveUpcomingGames();
                            mBackgroundThread.execute();
                        }
                    }
                });
            }
        } else {
            mRunning = savedInstanceState.getBoolean("RUNNING");
            mUpdated = savedInstanceState.getBoolean("UPDATED");
            // pull the array from the savedinstancestate
            mArray = (ArrayList<GameListObject>) savedInstanceState.getSerializable("ARRAY");
            // link to the frame layouts and ensure they are visible
            FrameLayout filterContainer = (FrameLayout) findViewById(R.id.upcoming_filters_container);
            filterContainer.setVisibility(View.VISIBLE);

            FrameLayout listContainer = (FrameLayout) findViewById(R.id.upcoming_games_container);
            listContainer.setVisibility(View.VISIBLE);

            // if the array is empty
            if (!mUpdated) {
                // and the background thread is running
                if (mRunning) {
                    // cancel the thread, nullify the thread, and indicate that the thread is no
                    // longer active
                    mBackgroundThread.cancel(false);
                    mBackgroundThread = null;
                    mRunning = false;
                }

                // start running the thread again
                mBackgroundThread = new RetrieveUpcomingGames();
                mBackgroundThread.execute();
            }
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // pull the array and boolean out of the savedinstance state when the state is restored
        mArray = (ArrayList<GameListObject>) savedInstanceState.getSerializable("ARRAY");
        mFilteredArray = (ArrayList<GameListObject>) savedInstanceState.getSerializable("FILTERED");
        mRunning = savedInstanceState.getBoolean("RUNNING");
        mUpdated = savedInstanceState.getBoolean("UPDATED");
        mCurrentFilter = savedInstanceState.getString("FILTER");
    }

    @Override
    protected void onResume() {
        super.onResume();

        // link to the fragment
        UpcomingGamesFragment f = (UpcomingGamesFragment) getFragmentManager()
                .findFragmentByTag(UpcomingGamesFragment.TAG);

        // if the array isn't null
        if (mFilteredArray != null) {

            // as long as the fragment isn't null
            if (f != null) {
                // populate the list
                f.createList(this, mFilteredArray);
            }
        }

        // link to the fragment
        UpcomingFiltersFragment frag = (UpcomingFiltersFragment) getFragmentManager()
                .findFragmentByTag(UpcomingFiltersFragment.TAG);

        // as long as the fragment isn't null
        if (frag != null) {
            // populate the list
            frag.setActiveFilter(mCurrentFilter, this);
        }
    }

    @Override
    protected void onDestroy() {
        if (mRunning) {
            mBackgroundThread.cancel(false);
            mBackgroundThread = null;
            mRunning = false;
        }

        super.onDestroy();
    }

    @Override
    public void openGameDetails(String id) {
        String filename = "trackedgames.bin";
        boolean tracked = FileManager.isTracked(new File(this.getFilesDir(), filename), id);

        Intent i = new Intent(this, GameDetailsActivity.class);
        i.putExtra("ID", id);
        i.putExtra("SENTFROM", "Upcoming");
        i.putExtra("STATUS", tracked);
        startActivity(i);
    }

    @Override
    public void addMultipleGames(ArrayList<GameListObject> a) {
        // create the intent, apply the action, input the extras, and start the service
        Intent i = new Intent(this, AddMultipleGamesService.class);
        i.setAction("com.samuelhubbard.android.releasedate.AddMultipleGames");
        i.putExtra("GAMES", a);
        startService(i);

        // notify that games are being added
        Toast.makeText(this, R.string.adding_games, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void filterList(String platform) {
        // setting up the member variables
        mFilteredArray = new ArrayList<>();
        mCurrentFilter = platform;

        if (Objects.equals(platform, "all")) {
            mFilteredArray = mArray;
            mFilteredArray = SectionHeaderInclusion.insertHeaders(mFilteredArray);
        } else if (Objects.equals(platform, "pc")) {
            // instance variables
            String filterProperty = "PC";
            boolean filter;

            // loop to filter
            for (int i = 0; i < mArray.size(); i++) {
                // check the platforms for the game
                filter = mArray.get(i).getPlatforms().contains(filterProperty);

                // if the game contains the filter property, add it to the filter array
                if (filter) {
                    mFilteredArray.add(mArray.get(i));
                }
            }

            mFilteredArray = SectionHeaderInclusion.insertHeaders(mFilteredArray);
        } else if (Objects.equals(platform, "ps4")) {
            // instance variables
            String filterProperty = "PlayStation 4";
            boolean filter;

            // loop to filter
            for (int i = 0; i < mArray.size(); i++) {
                // check the platforms for the game
                filter = mArray.get(i).getPlatforms().contains(filterProperty);

                // if the game contains the filter property, add it to the filter array
                if (filter) {
                    mFilteredArray.add(mArray.get(i));
                }
            }

            mFilteredArray = SectionHeaderInclusion.insertHeaders(mFilteredArray);
        } else if (Objects.equals(platform, "xbox")) {
            // instance variables
            String filterProperty = "Xbox One";
            boolean filter;

            // loop to filter
            for (int i = 0; i < mArray.size(); i++) {
                // check the platforms for the game
                filter = mArray.get(i).getPlatforms().contains(filterProperty);

                // if the game contains the filter property, add it to the filter array
                if (filter) {
                    mFilteredArray.add(mArray.get(i));
                }
            }

            mFilteredArray = SectionHeaderInclusion.insertHeaders(mFilteredArray);
        } else if (Objects.equals(platform, "wii")) {
            // instance variables
            String filterProperty = "Wii U";
            boolean filter;

            // loop to filter
            for (int i = 0; i < mArray.size(); i++) {
                // check the platforms for the game
                filter = mArray.get(i).getPlatforms().contains(filterProperty);

                // if the game contains the filter property, add it to the filter array
                if (filter) {
                    mFilteredArray.add(mArray.get(i));
                }
            }

            mFilteredArray = SectionHeaderInclusion.insertHeaders(mFilteredArray);
        }

        // link to the fragment
        UpcomingGamesFragment f = (UpcomingGamesFragment) getFragmentManager()
                .findFragmentByTag(UpcomingGamesFragment.TAG);

        // as long as the fragment isn't null
        if (f != null) {
            // populate the list
            f.createList(UpcomingGamesActivity.this, mFilteredArray);
        }

        // link to the fragment
        UpcomingFiltersFragment frag = (UpcomingFiltersFragment) getFragmentManager()
                .findFragmentByTag(UpcomingFiltersFragment.TAG);

        // as long as the fragment isn't null
        if (frag != null) {
            // populate the list
            frag.setActiveFilter(mCurrentFilter, this);
        }

    }

    private class RetrieveUpcomingGames extends AsyncTask<Void, Void, ArrayList<GameListObject>> {

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

            // set the progress bar to visible and the two content fragments to gone
            FrameLayout progress = (FrameLayout) findViewById(R.id.progress_indicator);
            progress.setVisibility(View.VISIBLE);

            FrameLayout filterContainer = (FrameLayout) findViewById(R.id.upcoming_filters_container);
            filterContainer.setVisibility(View.GONE);

            FrameLayout listContainer = (FrameLayout) findViewById(R.id.upcoming_games_container);
            listContainer.setVisibility(View.GONE);

            LinearLayout errorScreen = (LinearLayout) findViewById(R.id.upcoming_games_noconn);
            errorScreen.setVisibility(View.GONE);
        }

        @Override
        protected ArrayList<GameListObject> doInBackground(Void... voids) {

            // again, ensure that the app is connected
            ConnectivityManager manager = (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);

            // the boolean that checks for connectivity
            boolean isConnected = VerifyConnection.checkNetwork(manager);

            if(isConnected) {
                // perform all of the required api calls for the timeframe requirement
                String currentQuarterRawData = ApiHandler.retrieveUpcomingGames(mYear, mQuarterOne);
                String nextQuarterRawData = ApiHandler.retrieveUpcomingGames(mYear, mQuarterTwo);
                String thirdQuarterRawData = ApiHandler.retrieveUpcomingGames(mYear, mQuarterThree);

                // as long as both api pulls were successful
                if (currentQuarterRawData != null && nextQuarterRawData != null &&
                        thirdQuarterRawData != null) {
                    // parse both of those into an array
                    mArray = ApiHandler.parseUpcomingGames(currentQuarterRawData, nextQuarterRawData,
                            thirdQuarterRawData);

                    // if the array isn't empty, return it
                    if (mArray != null) {
                        return mArray;
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<GameListObject> array) {
            super.onPostExecute(array);

            // unlock screen orientation changes
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

            // progress bar gone, content fragments visible
            FrameLayout progress = (FrameLayout) findViewById(R.id.progress_indicator);
            progress.setVisibility(View.GONE);

            FrameLayout filterContainer = (FrameLayout) findViewById(R.id.upcoming_filters_container);
            filterContainer.setVisibility(View.VISIBLE);

            FrameLayout listContainer = (FrameLayout) findViewById(R.id.upcoming_games_container);
            listContainer.setVisibility(View.VISIBLE);

            if (array != null) {
                mArray = array;

                // insert the headers into the array
                mFilteredArray = SectionHeaderInclusion.insertHeaders(array);

                // link to the fragment
                UpcomingGamesFragment f = (UpcomingGamesFragment) getFragmentManager()
                        .findFragmentByTag(UpcomingGamesFragment.TAG);

                // as long as the fragment isn't null
                if (f != null) {
                    // populate the list
                    f.createList(UpcomingGamesActivity.this, mFilteredArray);
                }
                // indicate that the data pull was successful
                mUpdated = true;
            }

            // indicate that the thread is no longer running
            mRunning = false;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save the array and running boolean to the outstate bundle
        outState.putSerializable("ARRAY", mArray);
        outState.putSerializable("FILTERED", mFilteredArray);
        outState.putBoolean("RUNNING", mRunning);
        outState.putBoolean("UPDATED", mUpdated);
        outState.putString("FILTER", mCurrentFilter);
    }
}
