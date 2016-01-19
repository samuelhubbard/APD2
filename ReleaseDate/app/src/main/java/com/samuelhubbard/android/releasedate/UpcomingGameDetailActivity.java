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
import android.widget.Toast;

import com.samuelhubbard.android.releasedate.Fragments.DetailScreen.DetailCoreFragment;
import com.samuelhubbard.android.releasedate.Fragments.DetailScreen.DetailDescriptionFragment;
import com.samuelhubbard.android.releasedate.Fragments.DetailScreen.DetailGalleryFragment;
import com.samuelhubbard.android.releasedate.Fragments.DetailScreen.DetailImageFragment;
import com.samuelhubbard.android.releasedate.ListViewElements.GameListObject;
import com.samuelhubbard.android.releasedate.ListViewElements.GameObject;
import com.samuelhubbard.android.releasedate.Utility.ApiHandler;
import com.samuelhubbard.android.releasedate.Utility.VerifyConnection;

public class UpcomingGameDetailActivity extends AppCompatActivity {

    String mGameId;

    boolean mRunning = false;
    RetrieveGameInfo mBackgroundTask;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_detail);

        Intent i = getIntent();
        mGameId = i.getStringExtra("ID");
        Log.i("TESTING", mGameId);

        // ensure the device is online
        ConnectivityManager manager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean isConnected = VerifyConnection.checkNetwork(manager, this);

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

            if (isConnected) {
                mBackgroundTask = new RetrieveGameInfo();
                mBackgroundTask.execute();
            } else {
                Toast.makeText(this, "No network connection", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class RetrieveGameInfo extends AsyncTask<Void, Void, GameObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

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

                // TODO: First, finalize the layout and create the population methods in each fragment
                // TODO: Create instance of the fragments and populate them

            } else {
                Log.i("TESTING", "There was obviously a problem.");
            }


            // indicate that the thread is no longer running
            mRunning = false;
        }
    }
}
