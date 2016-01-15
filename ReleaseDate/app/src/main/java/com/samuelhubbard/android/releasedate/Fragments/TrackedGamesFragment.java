package com.samuelhubbard.android.releasedate.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.samuelhubbard.android.releasedate.R;

/**
 * Created by samuelhubbard on 1/14/16.
 */
public class TrackedGamesFragment extends Fragment {

    public static final String TAG = "TrackedGamesFragment.TAG";

    public static TrackedGamesFragment newInstance() {
        TrackedGamesFragment f = new TrackedGamesFragment();

        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tracked_games, container, false);

        return v;
    }
}
