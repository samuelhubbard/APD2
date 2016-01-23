// Release Date
// Samuel Hubbard

package com.samuelhubbard.android.releasedate.Fragments.DetailScreen;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.samuelhubbard.android.releasedate.ListViewElements.GameObject;
import com.samuelhubbard.android.releasedate.R;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class DetailImageFragment extends Fragment {

    // fragment identification tag
    public static final String TAG = "DetailImageFragment.TAG";

    // member variable
    private ImageView gameImage;

    public static DetailImageFragment newInstance() {
        DetailImageFragment f = new DetailImageFragment();

        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_detail_image, container, false);

        gameImage = (ImageView) v.findViewById(R.id.detail_image);

        return v;
    }

    // populate image after data is present
    public void populateImage(GameObject game, Context c) {

        // run a check to see what came in
        if (Objects.equals(game.getImage(), "no_image")) {
            // no image from api, load the placeholder
            gameImage.setImageResource(R.drawable.no_image);
        } else {
            // Using Picasso to load in the game's thumbnail from the web
            Picasso.with(c).load(game.getImage()).resize(125, 200).placeholder(R.drawable.no_image).into(gameImage);
        }
    }
}
