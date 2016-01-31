// Release Date
// Samuel Hubbard

package com.samuelhubbard.android.releasedate.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.samuelhubbard.android.releasedate.ListViewElements.GameListAdapter;
import com.samuelhubbard.android.releasedate.ListViewElements.GameListObject;
import com.samuelhubbard.android.releasedate.R;

import java.util.ArrayList;
import java.util.Objects;

public class UpcomingGamesFragment extends Fragment {

    private Parcelable mState;

    // the fragment identification tag
    public static final String TAG = "UpcomingGamesFragment.TAG";

    // the list view
    private ListView gameListView;
    private ArrayList<GameListObject> mSelections = new ArrayList<>();

    private UpcomingGamesInterface mInterface;

    public interface UpcomingGamesInterface {
        void openGameDetails(String id);
        void addMultipleGames(ArrayList<GameListObject> a);
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

        // set the saved state of the listview position
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

        // setting the choice mode for the CAB
        gameListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

        // the long click listener that starts action mode
        gameListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ((ListView) parent).setItemChecked(position, ((ListView) parent).isItemChecked(position));

                return false;
            }
        });

        gameListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            private int num = 0;
            private GameListObject game;

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                // if an item is selected
                if (checked) {
                    // set the selected game to the internal game list object
                    game = (GameListObject) gameListView.getAdapter().getItem(position);
                    // add that to the selections array
                    mSelections.add(game);

                    // increase the count of total selected
                    num++;
                // if the an item is deselected
                } else {
                    // set the deselected game into the internal game list object
                    game = (GameListObject) gameListView.getAdapter().getItem(position);

                    // run a for loop that finds the game and removes it from the selections array
                    for (int i = 0; i < mSelections.size(); i++) {
                        if (Objects.equals(mSelections.get(i).getGameId(), game.getGameId())) {
                            mSelections.remove(i);
                        }
                    }

                    // decrease the number of total selected
                    num--;
                }
                // set the title to total selected
                mode.setTitle(num + " games selected");
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                // inflate the menu
                getActivity().getMenuInflater().inflate(R.menu.menu_add, menu);

                int currentOrientation = getResources().getConfiguration().orientation;
                if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                } else {
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
                }

                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.action_addgames) {
                    // send the selections array to the activity
                    mInterface = (UpcomingGamesInterface) getActivity();
                    mInterface.addMultipleGames(mSelections);

                    // close action mode
                    mode.finish();
                    return true;
                }

                return false;
            }

            @Override
            public void onDestroyActionMode (ActionMode mode){
                num = 0;
                // unlock screen orientation changes
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            }
        });

    }

        @Override
    public void onPause() {
        // save the current state of the listview position
        mState = gameListView.onSaveInstanceState();

        super.onPause();
    }
}
