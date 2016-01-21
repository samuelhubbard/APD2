// Release Date
// Samuel Hubbard

package com.samuelhubbard.android.releasedate;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
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
import com.samuelhubbard.android.releasedate.Utility.VerifyConnection;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class UpcomingGameDetailActivity extends AppCompatActivity implements DetailButtonFragment.GameDetailActionInterface {

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

        Intent i = getIntent();
        mParentActivity = i.getStringExtra("SENTFROM");
        mIsTracked = i.getBooleanExtra("STATUS", false);

        // ensure the device is online
        ConnectivityManager manager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean isConnected = VerifyConnection.checkNetwork(manager);

        if (savedInstanceState == null) {
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

            if (Objects.equals(mParentActivity, "Upcoming")) {
                mGameId = i.getStringExtra("ID");
                if (isConnected) {
                    mBackgroundTask = new RetrieveGameInfo();
                    mBackgroundTask.execute();
                } else {
                    Toast.makeText(this, "No network connection", Toast.LENGTH_SHORT).show();
                }
            } else {
                mGame = (GameObject) i.getSerializableExtra("GAME");
                if (mGame == null) {
                    Log.i("TESTING", "Yep... it's null");
                } else {
                    Log.i("TESTING", "It has stuff.");
                }
                //populateScreen(mGame);
            }
        } else {
            ScrollView detailView = (ScrollView) findViewById(R.id.detail_view);
            detailView.setVisibility(View.VISIBLE);

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

        mGame = (GameObject) savedInstanceState.getSerializable("GAME");
        mRunning = savedInstanceState.getBoolean("RUNNING");
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mGame != null) {
            setTitle(mGame.getName());
            populateScreen(mGame);
            Log.i("TESTING", "onPause");
        }
    }

    @Override
    public void trackGame() {

        boolean saveFile = FileManager.saveToFile(mGame, this);

        if (saveFile) {
            Toast.makeText(this, R.string.tracked_game, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.failed_to_track_game, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void removeGame() {
        ArrayList<GameObject> array;

        String filename = "trackedgames.bin";
        array = FileManager.loadFromFile(new File(this.getFilesDir(), filename));

        if (array != null) {
            for (int i = 0; i < array.size(); i++) {
                if (Objects.equals(array.get(i).getGameId(), mGame.getGameId())) {
                    array.remove(i);
                    FileManager.updateFile(array, this);
                    finish();
                    break;
                }
            }
        }
    }

    private class RetrieveGameInfo extends AsyncTask<Void, Void, GameObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            ProgressBar detailProgress = (ProgressBar) findViewById(R.id.progress_detail);
            ScrollView detailView = (ScrollView) findViewById(R.id.detail_view);

            detailProgress.setVisibility(View.VISIBLE);
            detailView.setVisibility(View.GONE);

            // indicate that the thread is now active
            mRunning = true;
        }

        @Override
        protected GameObject doInBackground(Void... voids) {

            String rawGameData = ApiHandler.retrieveGameDetail(mGameId);

            if (rawGameData != null) {
                GameObject parsedObject = ApiHandler.parseGame(rawGameData);

                if (parsedObject != null) {
                    return parsedObject;
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(GameObject game) {
            super.onPostExecute(game);

            if (game != null) {
                mGame = game;

                setTitle(game.getName());

                populateScreen(game);

            } else {
                Log.i("TESTING", "There was obviously a problem.");
            }


            // indicate that the thread is no longer running
            mRunning = false;
        }
    }

    public void populateScreen(GameObject game) {
        if (game != null) {
            DetailImageFragment imageFrag = (DetailImageFragment) getFragmentManager()
                    .findFragmentByTag(DetailImageFragment.TAG);

            DetailCoreFragment coreFrag = (DetailCoreFragment) getFragmentManager()
                    .findFragmentByTag(DetailCoreFragment.TAG);

            DetailDescriptionFragment descFrag = (DetailDescriptionFragment) getFragmentManager()
                    .findFragmentByTag(DetailDescriptionFragment.TAG);

            DetailButtonFragment buttonFrag = (DetailButtonFragment) getFragmentManager()
                    .findFragmentByTag(DetailButtonFragment.TAG);

            imageFrag.populateImage(game, this);
            coreFrag.populateCore(game);
            descFrag.populateSummary(game);
            buttonFrag.setButtonBehavior(mIsTracked);

            ProgressBar detailProgress = (ProgressBar) findViewById(R.id.progress_detail);
            ScrollView detailView = (ScrollView) findViewById(R.id.detail_view);

            detailProgress.setVisibility(View.GONE);
            detailView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("GAME", mGame);
        outState.putBoolean("RUNNING", mRunning);

        super.onSaveInstanceState(outState);
    }
}
