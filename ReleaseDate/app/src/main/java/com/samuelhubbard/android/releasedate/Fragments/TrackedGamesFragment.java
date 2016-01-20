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
import android.widget.TextView;

import com.samuelhubbard.android.releasedate.ListViewElements.GameObject;
import com.samuelhubbard.android.releasedate.ListViewElements.SortTrackedGames;
import com.samuelhubbard.android.releasedate.ListViewElements.TrackedGameListAdapter;
import com.samuelhubbard.android.releasedate.R;

import java.util.ArrayList;

public class TrackedGamesFragment extends Fragment {

    // the fragment identification tag
    public static final String TAG = "TrackedGamesFragment.TAG";

    private static ArrayList<GameObject> mArray;
    private TextView mNoTrackedGames;
    private ListView mTrackedGamesList;
    private ArrayList<GameObject> mFinalArray;
    private static Context mContext;

    // start a new fragment
    public static TrackedGamesFragment newInstance(ArrayList<GameObject> a, Context c) {
        TrackedGamesFragment f = new TrackedGamesFragment();

        mArray = a;
        mContext = c;

        return f;
    }

    // inflate the fragment's view
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tracked_games, container, false);

        mNoTrackedGames = (TextView) v.findViewById(R.id.no_games);
        mTrackedGamesList = (ListView) v.findViewById(R.id.tracked_list);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (mArray == null) {
            mNoTrackedGames.setVisibility(View.VISIBLE);
            mTrackedGamesList.setVisibility(View.GONE);
        } else {
            mNoTrackedGames.setVisibility(View.GONE);
            mTrackedGamesList.setVisibility(View.VISIBLE);

            updateList(mArray);

        }
    }

    public void updateList(ArrayList<GameObject> a) {

        ArrayList<GameObject> sortedArray = SortTrackedGames.sortArray(a);

        mFinalArray = SortTrackedGames.includeSectionHeaders(sortedArray);

        TrackedGameListAdapter adapter = new TrackedGameListAdapter(mContext, mFinalArray);

        mTrackedGamesList.setAdapter(adapter);

    }
}
