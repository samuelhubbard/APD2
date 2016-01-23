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
import android.widget.TextView;

import com.samuelhubbard.android.releasedate.R;

public class DetailButtonFragment extends Fragment {

    // fragment identification tag
    public static final String TAG = "DetailButtonFragment.TAG";

    // member variables
    private TextView mButton;
    private GameDetailActionInterface mInterface;

    // button click interface
    public interface GameDetailActionInterface {
        void trackGame();
        void removeGame();
    }

    public static DetailButtonFragment newInstance() {
        DetailButtonFragment f = new DetailButtonFragment();

        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_detail_button, container, false);

        mButton = (TextView) v.findViewById(R.id.detail_all_purpose_button);

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof GameDetailActionInterface) {
            mInterface = (GameDetailActionInterface) getActivity();
        }
    }

    // method that changes the button functionality
    public void setButtonBehavior(boolean status) {
        if (!status) {
            // set button name and set the click to activate the interface based on the tracked boolean
            mButton.setText("TRACK GAME");
            mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mInterface = (GameDetailActionInterface) getActivity();

                    mInterface.trackGame();
                }
            });
        } else {
            mButton.setText("REMOVE");
            mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mInterface = (GameDetailActionInterface) getActivity();

                    mInterface.removeGame();
                }
            });
        }
    }
}
