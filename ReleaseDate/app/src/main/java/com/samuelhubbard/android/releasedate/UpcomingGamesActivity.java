package com.samuelhubbard.android.releasedate;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.samuelhubbard.android.releasedate.Fragments.UpcomingFiltersFragment;
import com.samuelhubbard.android.releasedate.Fragments.UpcomingGamesFragment;
import com.samuelhubbard.android.releasedate.Utility.VerifyConnection;

public class UpcomingGamesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming_games);

        ConnectivityManager manager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean isConnected = VerifyConnection.checkNetwork(manager, this);

        if (isConnected) {
            Toast.makeText(this, "Connected to the Internet", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No network connection", Toast.LENGTH_SHORT).show();
        }

        if (savedInstanceState == null) {
            UpcomingFiltersFragment filterFrag = UpcomingFiltersFragment.newInstance();
            getFragmentManager().beginTransaction()
                    .replace(R.id.upcoming_filters_container, filterFrag, UpcomingFiltersFragment.TAG)
                    .commit();

            UpcomingGamesFragment gamesFrag = UpcomingGamesFragment.newInstance();
            getFragmentManager().beginTransaction()
                    .replace(R.id.upcoming_games_container, gamesFrag, UpcomingGamesFragment.TAG)
                    .commit();
        }
    }
}
