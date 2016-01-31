// Release Date
// Samuel Hubbard

package com.samuelhubbard.android.releasedate.Fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.samuelhubbard.android.releasedate.GameDetailsActivity;
import com.samuelhubbard.android.releasedate.ListViewElements.GalleryAdapter;
import com.samuelhubbard.android.releasedate.ListViewElements.GameObject;
import com.samuelhubbard.android.releasedate.R;
import com.samuelhubbard.android.releasedate.Utility.VerifyConnection;

import java.util.ArrayList;

public class GameImagesFragment extends Fragment {

    // member variables
    private GridView mGrid;
    private GalleryAdapter mAdapter;
    private GameObject mGame;
    private ArrayList<String> mImageArray;
    private Context mContext;
    private GameGridInterface mInterface;
    private TextView mNoConn;
    private boolean mIsConnected;

    // public constructor
    public GameImagesFragment() {

    }

    // interface to open an image
    public interface GameGridInterface {
        void sendImageAddress(int position, ArrayList<String> array);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_game_images, container, false);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // instantiate the views for the grid and no connection
        mGrid = (GridView) getView().findViewById(R.id.game_grid);
        mNoConn = (TextView) getView().findViewById(R.id.image_grid_noconn_link);

        // run a check to see if the device is connected
        ConnectivityManager manager = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        mIsConnected = VerifyConnection.checkNetwork(manager);

        // handle how to populate the game data
        if (savedInstanceState == null) {
            mGame = GameDetailsActivity.mGame;
        } else {
            mGame = (GameObject) savedInstanceState.getSerializable("GAME");
        }

        // handle behavior based on device network connection
        if (mIsConnected) {
            if (mGame != null) {
                // instantiate the array, context, and adapter
                mImageArray = mGame.getImages();
                mContext = GameDetailsActivity.mContext;
                mAdapter = new GalleryAdapter(mContext, R.layout.grid_item_layout, mImageArray);

                // set the adapter to the gridview
                mGrid.setAdapter(mAdapter);

                // set the click listener to open the large image viewer
                mGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        // incase the interface has dropped
                        if (mInterface == null) {
                            mInterface = (GameGridInterface) getActivity();
                        }
                        mInterface.sendImageAddress(position, mImageArray);
                    }
                });
                // set window visibility
                mGrid.setVisibility(View.VISIBLE);
                mNoConn.setVisibility(View.GONE);
            }
        } else {
            // set window visibility
            mGrid.setVisibility(View.GONE);
            mNoConn.setVisibility(View.VISIBLE);
        }
    }

    public void populate(GameObject game) {
        // update the game data
        mGame = game;

        ConnectivityManager manager = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        mIsConnected = VerifyConnection.checkNetwork(manager);

        if (mIsConnected) {
            if (mGame != null) {
                // instantiate the array, context, and adapter
                mImageArray = mGame.getImages();
                mContext = GameDetailsActivity.mContext;
                mAdapter = new GalleryAdapter(mContext, R.layout.grid_item_layout, mImageArray);

                // set the adapter to the gridview
                mGrid.setAdapter(mAdapter);

                // set a click listener to open the image viewer
                mGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        if (mInterface == null) {
                            mInterface = (GameGridInterface) getActivity();
                        }

                        mInterface.sendImageAddress(position, mImageArray);
                    }
                });

                // set window visibility
                mGrid.setVisibility(View.VISIBLE);
                mNoConn.setVisibility(View.GONE);
            }
        } else {
            // set window visibility
            mGrid.setVisibility(View.GONE);
            mNoConn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // saved the game data to the instance state
        outState.putSerializable("GAME", mGame);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        // remove the grid
        mGrid = null;
        super.onDestroy();
    }
}
