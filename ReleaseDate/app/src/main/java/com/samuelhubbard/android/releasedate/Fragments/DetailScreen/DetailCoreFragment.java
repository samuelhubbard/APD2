// Release Date
// Samuel Hubbard

package com.samuelhubbard.android.releasedate.Fragments.DetailScreen;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.samuelhubbard.android.releasedate.R;

public class DetailCoreFragment extends Fragment {

    public static final String TAG = "DetailCoreFragment.TAG";

    public static DetailCoreFragment newInstance() {
        DetailCoreFragment f = new DetailCoreFragment();

        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_detail_core, container, false);

        return v;
    }
}
