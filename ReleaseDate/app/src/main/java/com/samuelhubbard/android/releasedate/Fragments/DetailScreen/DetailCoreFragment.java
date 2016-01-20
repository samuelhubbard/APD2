// Release Date
// Samuel Hubbard

package com.samuelhubbard.android.releasedate.Fragments.DetailScreen;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.samuelhubbard.android.releasedate.ListViewElements.GameObject;
import com.samuelhubbard.android.releasedate.R;

public class DetailCoreFragment extends Fragment {

    public static final String TAG = "DetailCoreFragment.TAG";

    public TextView textReleaseDate;
    public TextView textPlatforms;
    public TextView textDevelopers;
    public TextView textGenres;

    public static DetailCoreFragment newInstance() {
        DetailCoreFragment f = new DetailCoreFragment();

        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_detail_core, container, false);

        textReleaseDate = (TextView) v.findViewById(R.id.detail_release_date);
        textPlatforms = (TextView) v.findViewById(R.id.detail_platforms);
        textDevelopers = (TextView) v.findViewById(R.id.detail_developer);
        textGenres = (TextView) v.findViewById(R.id.detail_genre);

        return v;
    }

    public void populateCore(GameObject game) {
        String releaseDay = "Releases on " + game.getFullReleaseDay();
        String platforms = "Platforms: " + game.getPlatforms();
        String developers = "Developers: " + game.getDeveloper();
        String genres = "Genre: " + game.getGenre();

        textReleaseDate.setText(releaseDay);
        textPlatforms.setText(platforms);
        textDevelopers.setText(developers);
        textGenres.setText(genres);
    }
}
