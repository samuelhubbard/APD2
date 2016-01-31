// Release Date
// Samuel Hubbard

package com.samuelhubbard.android.releasedate.Fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.TextView;

import com.samuelhubbard.android.releasedate.ListViewElements.GameObject;
import com.samuelhubbard.android.releasedate.ListViewElements.SortTrackedGames;
import com.samuelhubbard.android.releasedate.ListViewElements.TrackedGameListAdapter;
import com.samuelhubbard.android.releasedate.R;

import java.util.ArrayList;
import java.util.Objects;

public class TrackedGamesFragment extends Fragment {

    private Parcelable mState;

    // the fragment identification tag
    public static final String TAG = "TrackedGamesFragment.TAG";

    // member variables
    private static ArrayList<GameObject> mArray;
    private TextView mNoTrackedGames;
    private ListView mTrackedGamesList;
    private ArrayList<GameObject> mFinalArray;
    private static Context mContext;
    private ArrayList<GameObject> mSelections = new ArrayList<>();


    // interface variable
    private TrackedGamesInterface mInterface;

    public interface TrackedGamesInterface {
        void openDetails(GameObject game);
        void removeMultipleGames(ArrayList<GameObject> a);
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

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof TrackedGamesInterface) {
            mInterface = (TrackedGamesInterface) getActivity();
        } else {
            throw new IllegalArgumentException("Interface must be an instance of the context.");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mTrackedGamesList = (ListView) getView().findViewById(R.id.tracked_list);

        // set element visibility based on if there are any tracked games
        if (mArray == null) {
            mNoTrackedGames.setVisibility(View.VISIBLE);
            mTrackedGamesList.setVisibility(View.GONE);
        } else {
            mNoTrackedGames.setVisibility(View.GONE);
            mTrackedGamesList.setVisibility(View.VISIBLE);

            // update the list
            updateList(mArray, mContext);

        }
    }

    // method that handles the list: sorts, adds section headers, and populates the list view
    public void updateList(ArrayList<GameObject> a, Context c) {

        if (a != null) {
            if (a.size() > 0) {

                // set necessary visibility
                mNoTrackedGames.setVisibility(View.GONE);
                mTrackedGamesList.setVisibility(View.VISIBLE);

                // sort the array that is stored on device
                ArrayList<GameObject> sortArray = SortTrackedGames.sortArray(a);

                mFinalArray = SortTrackedGames.includeSectionHeaders(sortArray);

                // create the adapter
                TrackedGameListAdapter adapter = new TrackedGameListAdapter(c, mFinalArray);

                // set the adapter
                mTrackedGamesList.setAdapter(adapter);



                // restore the position of the list view
                if (mState != null) {
                    mTrackedGamesList.onRestoreInstanceState(mState);
                }

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

                // setting the choice mode for the CAB
                mTrackedGamesList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

                // the long click listener that starts the action mode
                mTrackedGamesList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        ((ListView) parent).setItemChecked(position, ((ListView) parent).isItemChecked(position));

                        return false;
                    }
                });

                mTrackedGamesList.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
                    private int numChecked = 0;
                    private GameObject selectedGame;

                    @Override
                    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

                        // if the item is selected
                        if (checked) {
                            // set the selected item to the internal game object
                            selectedGame = (GameObject) mTrackedGamesList.getAdapter().getItem(position);
                            // add that to the selections array
                            mSelections.add(selectedGame);
                            // increase the count for total selected
                            numChecked++;
                        // if the item is deselected
                        } else {
                            // set the deselected item to the internal game object
                            selectedGame = (GameObject) mTrackedGamesList.getAdapter().getItem(position);

                            // run a for loop, find the matching item in the selections array and remove it
                            for (int i = 0; i < mSelections.size(); i++) {
                                if (Objects.equals(mSelections.get(i).getGameId(), selectedGame.getGameId())) {
                                    mSelections.remove(i);
                                }
                            }

                            // decrease the count for total selected
                            numChecked--;
                        }

                        // display number of selected as title
                        mode.setTitle(numChecked + " games selected");
                    }

                    @Override
                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                        // inflate the menu
                        getActivity().getMenuInflater().inflate(R.menu.menu_remove, menu);

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
                    public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {

                        // get which menu item was clicked
                        int id = item.getItemId();

                        if (id == R.id.action_delete) {

                            AlertDialog.Builder alertDialogue = new AlertDialog.Builder(mContext, android.R.style.Theme_Material_Dialog_Alert);

                            // sets the title for the alert
                            alertDialogue.setTitle(R.string.deletion_confirmation);
                            alertDialogue.setIcon(R.mipmap.ic_launcher);

                            // sets the message and buttons
                            alertDialogue.setMessage(R.string.deletion_message);
                            alertDialogue.setCancelable(false);
                            alertDialogue.setPositiveButton(R.string.deletion_yes, new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // run the interface and shut down the action mode
                                    mInterface = (TrackedGamesInterface) getActivity();
                                    mInterface.removeMultipleGames(mSelections);
                                    mode.finish();
                                }
                            });
                            // the cancel button... closes the dialog
                            alertDialogue.setNegativeButton(R.string.deletion_no, new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                            // creates the dialogue
                            AlertDialog alert = alertDialogue.create();
                            // shows the dialogue
                            alert.show();
                            return true;
                        }


                        return false;
                    }

                    @Override
                    public void onDestroyActionMode(ActionMode mode) {
                        numChecked = 0;
                        // unlock screen orientation changes
                        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                    }
                });
            } else {
                // set window visibility
                mNoTrackedGames.setVisibility(View.VISIBLE);
                mTrackedGamesList.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onPause() {
        // save the position on the list view
        mState = mTrackedGamesList.onSaveInstanceState();

        super.onPause();
    }
}
