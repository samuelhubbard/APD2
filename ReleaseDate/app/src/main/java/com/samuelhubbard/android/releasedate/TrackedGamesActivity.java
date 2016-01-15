package com.samuelhubbard.android.releasedate;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.samuelhubbard.android.releasedate.Fragments.TrackedGamesFragment;
import com.samuelhubbard.android.releasedate.Utility.VerifyConnection;

public class TrackedGamesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracked_games);

        ConnectivityManager manager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean isConnected = VerifyConnection.checkNetwork(manager, this);

        if (isConnected) {
            Toast.makeText(this, "Connected to the Internet", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No network connection", Toast.LENGTH_SHORT).show();
        }

        // hang the fragment
        if (savedInstanceState == null) {
            //TODO: Hang the fragment
            TrackedGamesFragment trackedFrag = TrackedGamesFragment.newInstance();
            getFragmentManager().beginTransaction()
                    .replace(R.id.tracked_container, trackedFrag, TrackedGamesFragment.TAG)
                    .commit();
        }
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

        //TODO: Add navigation to other activities
        if (id == R.id.action_upcoming) {
            Intent i = new Intent(this, UpcomingGamesActivity.class);
            startActivityForResult(i, 0);
            return true;
        }

        if (id == R.id.action_about) {
            Intent i = new Intent(this, AboutAppActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
