// Release Date
// Samuel Hubbard

package com.samuelhubbard.android.releasedate.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.samuelhubbard.android.releasedate.ListViewElements.GameListAdapter;
import com.samuelhubbard.android.releasedate.ListViewElements.GameListObject;
import com.samuelhubbard.android.releasedate.R;

import java.util.ArrayList;
import java.util.IllegalFormatCodePointException;

public class UpcomingGamesFragment extends Fragment {

    private Parcelable mState;

    // the fragment identification tag
    public static final String TAG = "UpcomingGamesFragment.TAG";

    // the list view
    private ListView gameListView;

    private UpcomingGamesInterface mInterface;

    public interface UpcomingGamesInterface {
        void openGameDetails(String id);
    }

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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof UpcomingGamesInterface) {
            mInterface = (UpcomingGamesInterface) getActivity();
        } else {
            throw new IllegalArgumentException("Interface must be an instance of the context.");
        }
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

        if (mState != null) {
            gameListView.onRestoreInstanceState(mState);
        }

        // click listener to open detail view from list view
        gameListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // get object position
                GameListObject game = (GameListObject) parent.getAdapter().getItem(position);

                // send object to activity for detail view population
                mInterface = (UpcomingGamesInterface) getActivity();
                mInterface.openGameDetails(game.getGameId());
            }
        });

    }

    @Override
    public void onPause() {
        mState = gameListView.onSaveInstanceState();

        super.onPause();
    }
}
