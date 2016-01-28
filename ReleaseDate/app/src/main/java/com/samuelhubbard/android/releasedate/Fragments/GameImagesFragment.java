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

    private GridView mGrid;
    private GalleryAdapter mAdapter;
    private GameObject mGame;
    private ArrayList<String> mImageArray;
    private Context mContext;
    private GameGridInterface mInterface;
    private TextView mNoConn;

    public GameImagesFragment() {

    }

    public interface GameGridInterface {
        void sendImageAddress(String address);
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

        mGrid = (GridView) getView().findViewById(R.id.game_grid);
        mNoConn = (TextView) getView().findViewById(R.id.image_grid_noconn_link);

        ConnectivityManager manager = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean isConnected = VerifyConnection.checkNetwork(manager);

        if (savedInstanceState == null) {
            mGame = GameDetailsActivity.mGame;
        } else {
            mGame = (GameObject) savedInstanceState.getSerializable("GAME");
        }

        if (isConnected) {
            if (mGame != null) {
                mImageArray = mGame.getImages();
                mContext = GameDetailsActivity.mContext;

                mAdapter = new GalleryAdapter(mContext, R.layout.grid_item_layout, mImageArray);

                mGrid.setAdapter(mAdapter);

                mGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        if (mInterface == null) {
                            mInterface = (GameGridInterface) getActivity();
                        }

                        String imageAddress = parent.getItemAtPosition(position).toString();
                        mInterface.sendImageAddress(imageAddress);
                    }
                });
                mGrid.setVisibility(View.VISIBLE);
                mNoConn.setVisibility(View.GONE);
            }
        } else {
            mGrid.setVisibility(View.GONE);
            mNoConn.setVisibility(View.VISIBLE);
        }
    }

    public void populate(GameObject game) {
        mGame = game;

        if (mGame != null) {
            mImageArray = mGame.getImages();
            mContext = GameDetailsActivity.mContext;

            mAdapter = new GalleryAdapter(mContext, R.layout.grid_item_layout, mImageArray);

            mGrid.setAdapter(mAdapter);

            mGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    if (mInterface == null) {
                        mInterface = (GameGridInterface) getActivity();
                    }

                    String imageAddress = parent.getItemAtPosition(position).toString();
                    mInterface.sendImageAddress(imageAddress);
                }
            });

            mGrid.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("GAME", mGame);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        mGrid.setVisibility(View.GONE);
        super.onDestroy();
    }
}
