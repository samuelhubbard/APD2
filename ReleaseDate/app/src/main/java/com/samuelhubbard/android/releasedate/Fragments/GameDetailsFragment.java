// Release Date
// Samuel Hubbard

package com.samuelhubbard.android.releasedate.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.samuelhubbard.android.releasedate.GameDetailsActivity;
import com.samuelhubbard.android.releasedate.ListViewElements.GameObject;
import com.samuelhubbard.android.releasedate.R;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class GameDetailsFragment extends Fragment {

    private static TextView mButton;
    private static TextView textReleaseDate;
    private static TextView textPlatforms;
    private static TextView textDevelopers;
    private static TextView textGenres;
    private static TextView textSummary;
    private static ImageView gameImage;
    private static GameObject mGame;
    private static boolean mStatus;
    private static DetailInterface mInterface;
    private static ScrollView mScrollView;

    public GameDetailsFragment(){

    }

    public interface DetailInterface {
        void trackGame();
        void removeGame();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_game_details, container, false);

        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mScrollView = (ScrollView) getView().findViewById(R.id.detail_view);
        mButton = (TextView) getView().findViewById(R.id.details_all_purpose_button);
        textReleaseDate = (TextView) getView().findViewById(R.id.details_release_date);
        textPlatforms = (TextView) getView().findViewById(R.id.details_platforms);
        textDevelopers = (TextView) getView().findViewById(R.id.details_developer);
        textGenres = (TextView) getView().findViewById(R.id.details_genre);
        textSummary = (TextView) getView().findViewById(R.id.details_summary);
        gameImage = (ImageView) getView().findViewById(R.id.tabbed_image);

        Context c = GameDetailsActivity.mContext;

        if (savedInstanceState == null) {
            mGame = GameDetailsActivity.mGame;
            mStatus = GameDetailsActivity.mStatus;
        } else {
            mGame = (GameObject) savedInstanceState.getSerializable("GAME");
            mStatus = savedInstanceState.getBoolean("STATUS");
        }

        if (!mStatus) {
            // set button name and set the click to activate the interface based on the tracked boolean
            mButton.setText("TRACK GAME");
            mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mInterface = (DetailInterface) getActivity();

                    mInterface.trackGame();
                }
            });
        } else {
            mButton.setText("REMOVE");
            mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mInterface = (DetailInterface) getActivity();

                    mInterface.removeGame();
                }
            });
        }

        if (mGame != null) {

            String releaseDay = "Releases on " + mGame.getFullReleaseDay();
            String developers = "Developers: " + mGame.getDeveloper();
            String genres = "Genre: " + mGame.getGenre();

            textReleaseDate.setText(releaseDay);
            textPlatforms.setText(mGame.getPlatforms());
            textDevelopers.setText(developers);
            textGenres.setText(genres);
            textSummary.setText(mGame.getDescription());
            // run a check to see what came in
            if (Objects.equals(mGame.getImage(), "no_image")) {
                // no image from api, load the placeholder
                gameImage.setImageResource(R.drawable.no_image);
            } else {
                // Using Picasso to load in the game's thumbnail from the web
                Picasso.with(c).load(mGame.getImage()).resize(125, 200).placeholder(R.drawable.no_image).into(gameImage);
            }
            mScrollView.setVisibility(View.VISIBLE);
        }
    }

    public void populate(GameObject game, boolean status, Context c) {
        mGame = game;
        mStatus = status;

        if (mGame != null) {

            if (!mStatus) {
                // set button name and set the click to activate the interface based on the tracked boolean
                mButton.setText("TRACK GAME");
                mButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mInterface = (DetailInterface) getActivity();

                        mInterface.trackGame();
                    }
                });
            } else {
                mButton.setText("REMOVE");
                mButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mInterface = (DetailInterface) getActivity();

                        mInterface.removeGame();
                    }
                });
            }

            String releaseDay = "Releases on " + mGame.getFullReleaseDay();
            String developers = "Developers: " + mGame.getDeveloper();
            String genres = "Genre: " + mGame.getGenre();

            textReleaseDate.setText(releaseDay);
            textPlatforms.setText(mGame.getPlatforms());
            textDevelopers.setText(developers);
            textGenres.setText(genres);
            textSummary.setText(mGame.getDescription());
            // run a check to see what came in
            if (Objects.equals(mGame.getImage(), "no_image")) {
                // no image from api, load the placeholder
                gameImage.setImageResource(R.drawable.no_image);
            } else {
                // Using Picasso to load in the game's thumbnail from the web
                Picasso.with(c).load(mGame.getImage()).resize(125, 200).placeholder(R.drawable.no_image).into(gameImage);
            }
            mScrollView.setVisibility(View.VISIBLE);
        }
    }

    public void updateButtonBehavior(boolean status) {
        mStatus = status;

        if (!mStatus) {
            // set button name and set the click to activate the interface based on the tracked boolean
            mButton.setText("TRACK GAME");
            mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mInterface = (DetailInterface) getActivity();

                    mInterface.trackGame();
                }
            });
        } else {
            mButton.setText("REMOVE");
            mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mInterface = (DetailInterface) getActivity();

                    mInterface.removeGame();
                }
            });
        }
    }

    @Override
     public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("GAME", mGame);
        outState.putBoolean("STATUS", mStatus);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        mScrollView.setVisibility(View.GONE);
        super.onDestroy();
    }
}
