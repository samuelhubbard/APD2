// Release Date
// Samuel Hubbard

package com.samuelhubbard.android.releasedate;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.samuelhubbard.android.releasedate.Fragments.UpcomingFiltersFragment;
import com.samuelhubbard.android.releasedate.Fragments.UpcomingGamesFragment;
import com.samuelhubbard.android.releasedate.ListViewElements.GameListObject;
import com.samuelhubbard.android.releasedate.ListViewElements.SectionHeaderInclusion;
import com.samuelhubbard.android.releasedate.Utility.ApiHandler;
import com.samuelhubbard.android.releasedate.Utility.VerifyConnection;

import java.util.ArrayList;
import java.util.Calendar;

public class UpcomingGamesActivity extends AppCompatActivity {

    // variables for game retrieval
    String mYear;
    String mQuarterOne;
    String mQuarterTwo;
    ArrayList<GameListObject> mArray;

    // variables to handle the background thread
    boolean mRunning;
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
        } else if (month >= 3 && month <= 5) {
            mQuarterOne = "2";
            mQuarterTwo = "3";
        } else if (month >= 6 && month <= 8) {
            mQuarterOne = "3";
            mQuarterTwo = "4";
        } else if (month >= 9 && month <= 11) {
            mQuarterOne = "4";
            mQuarterTwo = "1";
        }

        // ensure the device is online
        ConnectivityManager manager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean isConnected = VerifyConnection.checkNetwork(manager, this);

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

            // if there is a connection, start the background thread
            if (isConnected) {
                mBackgroundThread = new RetrieveUpcomingGames();
                mBackgroundThread.execute();
            } else {
                Toast.makeText(this, "No network connection", Toast.LENGTH_SHORT).show();
            }
        } else {
            // pull the array from the savedinstancestate
            mArray = (ArrayList<GameListObject>) savedInstanceState.getSerializable("ARRAY");
            // link to the frame layouts and ensure they are visible
            FrameLayout filterContainer = (FrameLayout) findViewById(R.id.upcoming_filters_container);
            filterContainer.setVisibility(View.VISIBLE);

            FrameLayout listContainer = (FrameLayout) findViewById(R.id.upcoming_games_container);
            listContainer.setVisibility(View.VISIBLE);

            // if the array is empty
            if (mArray.size() == 0) {
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
        mRunning = savedInstanceState.getBoolean("RUNNING");
    }

    @Override
    protected void onResume() {
        super.onResume();

        // if the array isn't null
        if (mArray != null) {
            // link to the fragment
            UpcomingGamesFragment f = (UpcomingGamesFragment) getFragmentManager()
                    .findFragmentByTag(UpcomingGamesFragment.TAG);

            // as long as the fragment isn't null
            if (f != null) {
                // populate the list
                f.createList(this, mArray);
            }
        }
    }

    private class RetrieveUpcomingGames extends AsyncTask<Void, Void, ArrayList<GameListObject>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // indicate that the thread is now active
            mRunning = true;

            // set the progress bar to visible and the two content fragments to gone
            ProgressBar progress = (ProgressBar) findViewById(R.id.progress_indicator);
            progress.setVisibility(View.VISIBLE);

            FrameLayout filterContainer = (FrameLayout) findViewById(R.id.upcoming_filters_container);
            filterContainer.setVisibility(View.GONE);

            FrameLayout listContainer = (FrameLayout) findViewById(R.id.upcoming_games_container);
            listContainer.setVisibility(View.GONE);
        }

        @Override
        protected ArrayList<GameListObject> doInBackground(Void... voids) {

            // again, ensure that the app is connected
            ConnectivityManager manager = (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);

            // the boolean that checks for connectivity
            boolean isConnected = VerifyConnection.checkNetwork(manager, UpcomingGamesActivity.this);

            if(isConnected) {
                // perform all of the required api calls for the timeframe requirement
                String currentQuarterRawData = ApiHandler.retrieveUpcomingGames(mYear, mQuarterOne);
                String nextQuarterRawData = ApiHandler.retrieveUpcomingGames(mYear, mQuarterTwo);

                // as long as both api pulls were successful
                if (currentQuarterRawData != null && nextQuarterRawData != null) {
                    // parse both of those into an array
                    mArray = ApiHandler.parseUpcomingGames(currentQuarterRawData, nextQuarterRawData);

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

            // progress bar gone, content fragments visible
            ProgressBar progress = (ProgressBar) findViewById(R.id.progress_indicator);
            progress.setVisibility(View.GONE);

            FrameLayout filterContainer = (FrameLayout) findViewById(R.id.upcoming_filters_container);
            filterContainer.setVisibility(View.VISIBLE);

            FrameLayout listContainer = (FrameLayout) findViewById(R.id.upcoming_games_container);
            listContainer.setVisibility(View.VISIBLE);

            // insert the headers into the array
            mArray = SectionHeaderInclusion.insertHeaders(array);

            // link to the fragment
            UpcomingGamesFragment f = (UpcomingGamesFragment) getFragmentManager()
                    .findFragmentByTag(UpcomingGamesFragment.TAG);

            // as long as the fragment isn't null
            if (f != null) {
                // populate the list
                f.createList(UpcomingGamesActivity.this, mArray);
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
        outState.putBoolean("RUNNING", mRunning);
    }
}
