// Release Date
// Samuel Hubbard

package com.samuelhubbard.android.releasedate;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.samuelhubbard.android.releasedate.Fragments.TrackedGamesFragment;
import com.samuelhubbard.android.releasedate.ListViewElements.GameListObject;
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
    CheckReleaseDates mBackgroundTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracked_games);
        setTitle(R.string.tracked_activity_title);

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

            // if there is a connection, start the background thread
            if (isConnected) {
                mBackgroundTask = new CheckReleaseDates();
                mBackgroundTask.execute();
            } else {
                Toast.makeText(this, "No network connection", Toast.LENGTH_SHORT).show();
            }

        } else {
            mRunning = savedInstanceState.getBoolean("RUNNING");
            mUpdated = savedInstanceState.getBoolean("UPDATED");

            if (isConnected) {
                if (!mUpdated) {
                    if (mRunning) {
                        // cancel the thread, nullify the thread, and indicate that the thread is no
                        // longer active
                        mBackgroundTask.cancel(false);
                        mBackgroundTask = null;
                        mRunning = false;
                    }
                    // start running the thread again
                    mBackgroundTask = new CheckReleaseDates();
                    mBackgroundTask.execute();
                }
            }
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mList = (ArrayList<GameObject>) savedInstanceState.getSerializable("ARRAY");
        mUpdated = savedInstanceState.getBoolean("UPDATED");
        mRunning = savedInstanceState.getBoolean("RUNNING");
    }

    @Override
    protected void onResume() {
        super.onResume();

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

        String filename = "trackedgames.bin";
        boolean tracked = FileManager.isTracked(new File(this.getFilesDir(), filename), game.getGameId());

        Intent i = new Intent(this, UpcomingGameDetailActivity.class);
        i.putExtra("SENTFROM", "Tracked");
        i.putExtra("GAME", game);
        i.putExtra("STATUS", tracked);
        startActivity(i);
    }

    private class CheckReleaseDates extends AsyncTask<Void, Void, ArrayList<GameObject>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mRunning = true;


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
                    String rawData = ApiHandler.checkForUpdates(mList.get(i));

                    if (rawData != null) {
                        GameObject game = ApiHandler.parseGame(rawData);

                        if (game != null) {
                            updateList.add(game);
                        }
                    }
                }

                return updateList;
            }

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<GameObject> array) {
            super.onPostExecute(array);

            for (int i = 0; i < mList.size(); i++) {
                mList.get(i).setDay(array.get(i).getDay());
                mList.get(i).setMonth(array.get(i).getMonth());
                mList.get(i).setYear(array.get(i).getYear());

                if (Objects.equals(mList.get(i).getDay(), "null") &&
                        Objects.equals(mList.get(i).getMonth(), "null") &&
                        Objects.equals(mList.get(i).getYear(), "null")) {
                    mList.remove(i);
                }
            }

            boolean saveUpdates = FileManager.updateFile(mList, TrackedGamesActivity.this);

            if (saveUpdates) {
                mUpdated = true;
                Toast.makeText(TrackedGamesActivity.this, R.string.release_date_check_complete, Toast.LENGTH_SHORT).show();
                updateListView();
            } else {
                Toast.makeText(TrackedGamesActivity.this, R.string.release_date_check_failed, Toast.LENGTH_SHORT).show();
            }

            // indicate that the thread is no longer running
            mRunning = false;

        }
    }

    protected void updateListView() {
        TrackedGamesFragment f = (TrackedGamesFragment) getFragmentManager()
                .findFragmentByTag(TrackedGamesFragment.TAG);

        String filename = "trackedgames.bin";
        ArrayList<GameObject> list = FileManager.loadFromFile(new File(this.getFilesDir(), filename));

        if (f != null) {
            f.updateList(list);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("ARRAY", mList);
        outState.putBoolean("UPDATED", mUpdated);
        outState.putBoolean("RUNNING", mRunning);

        super.onSaveInstanceState(outState);
    }
}
