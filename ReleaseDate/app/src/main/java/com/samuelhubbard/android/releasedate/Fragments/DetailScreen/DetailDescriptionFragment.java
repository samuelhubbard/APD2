// Release Date
// Samuel Hubbard

package com.samuelhubbard.android.releasedate.Fragments.DetailScreen;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.samuelhubbard.android.releasedate.ListViewElements.GameObject;
import com.samuelhubbard.android.releasedate.R;

public class DetailDescriptionFragment extends Fragment {

    // fragment identification tag
    public static final String TAG = "DetailDescriptionFragment.TAG";

    // member variable
    private TextView textSummary;

    public static DetailDescriptionFragment newInstance() {
        DetailDescriptionFragment f = new DetailDescriptionFragment();

        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_detail_description, container, false);

        textSummary = (TextView) v.findViewById(R.id.detail_summary);

        return v;
    }

    // populate fragment after data is present
    public void populateSummary(GameObject game) {
        textSummary.setText(game.getDescription());
    }
}
