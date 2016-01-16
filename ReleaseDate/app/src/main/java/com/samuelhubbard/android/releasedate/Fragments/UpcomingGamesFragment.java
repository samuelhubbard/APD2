// Release Date
// Samuel Hubbard

package com.samuelhubbard.android.releasedate.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.samuelhubbard.android.releasedate.ListViewElements.GameListAdapter;
import com.samuelhubbard.android.releasedate.ListViewElements.GameListObject;
import com.samuelhubbard.android.releasedate.R;

import java.util.ArrayList;

public class UpcomingGamesFragment extends Fragment {

    // the fragment identification tag
    public static final String TAG = "UpcomingGamesFragment.TAG";

    // the list view
    private ListView gameListView;

    // start a new fragment
    public static UpcomingGamesFragment newInstance() {
        UpcomingGamesFragment f = new UpcomingGamesFragment();

        return f;
    }

    // inflate the fragment's view
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_upcoming_games, container, false);

        return v;
    }

    // link the list view to the layout
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        gameListView = (ListView) getView().findViewById(R.id.list_upcoming_games);

    }

    // populate the list view
    public void createList(Context c, ArrayList<GameListObject> a) {
        // sets the adapter to the custom adapter using the array of custom objects
        GameListAdapter gameListAdapter = new GameListAdapter(c, a);

        // sets the list view to the custom adapter
        gameListView.setAdapter(gameListAdapter);

    }


}
