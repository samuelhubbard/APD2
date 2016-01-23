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
import android.widget.AdapterView;
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

    // member variables
    private static ArrayList<GameObject> mArray;
    private TextView mNoTrackedGames;
    private ListView mTrackedGamesList;
    private ArrayList<GameObject> mFinalArray;
    private static Context mContext;

    // interface variable
    private TrackedGamesInterface mInterface;

    public interface TrackedGamesInterface {
        void openDetails(GameObject game);
    }

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
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof TrackedGamesInterface) {
            mInterface = (TrackedGamesInterface) getActivity();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // set element visibility based on if there are any tracked games
        if (mArray == null) {
            mNoTrackedGames.setVisibility(View.VISIBLE);
            mTrackedGamesList.setVisibility(View.GONE);
        } else {
            mNoTrackedGames.setVisibility(View.GONE);
            mTrackedGamesList.setVisibility(View.VISIBLE);

            // update the list
            updateList(mArray);

        }
    }

    // method that handles the list: sorts, adds section headers, and populates the list view
    public void updateList(ArrayList<GameObject> a) {

        if (a != null) {
            if (a.size() > 0) {

                // set necessary visibility
                mNoTrackedGames.setVisibility(View.GONE);
                mTrackedGamesList.setVisibility(View.VISIBLE);

                // sort the array that is stored on device
                ArrayList<GameObject> sortArray = SortTrackedGames.sortArray(a);

                mFinalArray = SortTrackedGames.includeSectionHeaders(sortArray);

                // create the adapter
                TrackedGameListAdapter adapter = new TrackedGameListAdapter(mContext, mFinalArray);

                // set the adapter
                mTrackedGamesList.setAdapter(adapter);

                // set the listener for the list view elements
                mTrackedGamesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // get the position of cell clicked
                        GameObject game = (GameObject) parent.getAdapter().getItem(position);

                        // send the object to the main activity for detail view population
                        mInterface = (TrackedGamesInterface) getActivity();
                        mInterface.openDetails(game);
                    }
                });
            } else {
                mNoTrackedGames.setVisibility(View.VISIBLE);
                mTrackedGamesList.setVisibility(View.GONE);
            }
        }

    }
}
