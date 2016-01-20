// Release Date
// Samuel Hubbard

package com.samuelhubbard.android.releasedate;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.samuelhubbard.android.releasedate.Fragments.TrackedGamesFragment;
import com.samuelhubbard.android.releasedate.ListViewElements.GameObject;
import com.samuelhubbard.android.releasedate.Utility.FileManager;
import com.samuelhubbard.android.releasedate.Utility.VerifyConnection;

import java.io.File;
import java.util.ArrayList;

public class TrackedGamesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracked_games);
        setTitle(R.string.tracked_activity_title);

        String filename = "trackedgames.bin";
        ArrayList<GameObject> list = FileManager.loadFromFile(new File(this.getFilesDir(), filename));

        // check to see if the device is online
        ConnectivityManager manager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean isConnected = VerifyConnection.checkNetwork(manager, this);

        // if it is... give toast alerts... for now
        if (isConnected) {
            Toast.makeText(this, "Connected to the Internet", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No network connection", Toast.LENGTH_SHORT).show();
        }

        // hang the fragment
        if (savedInstanceState == null) {
            TrackedGamesFragment trackedFrag = TrackedGamesFragment.newInstance(list, this);
            getFragmentManager().beginTransaction()
                    .replace(R.id.tracked_container, trackedFrag, TrackedGamesFragment.TAG)
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        TrackedGamesFragment f = (TrackedGamesFragment) getFragmentManager()
                .findFragmentByTag(TrackedGamesFragment.TAG);

        String filename = "trackedgames.bin";
        ArrayList<GameObject> list = FileManager.loadFromFile(new File(this.getFilesDir(), filename));

        f.updateList(list);
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
}
