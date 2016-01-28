// Release Date
// Samuel Hubbard

package com.samuelhubbard.android.releasedate.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.samuelhubbard.android.releasedate.R;

import java.util.Objects;

public class UpcomingFiltersFragment extends Fragment implements View.OnClickListener {

    // the fragment identification tag
    public static final String TAG = "UpcomingFiltersFragment.TAG";

    // buttons
    TextView filterAll;
    TextView filterPC;
    TextView filterPS4;
    TextView filterXbox;
    TextView filterWii;

    // interface member
    FilterInterface mInterface;

    public interface FilterInterface {
        void filterList(String platform);
    }

    // start a new fragment
    public static UpcomingFiltersFragment newInstance() {
        UpcomingFiltersFragment f = new UpcomingFiltersFragment();

        return f;
    }

    // inflate the fragment's view
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_upcoming_filter, container, false);

        filterAll = (TextView) v.findViewById(R.id.filter_all);
        filterPC = (TextView) v.findViewById(R.id.filter_pc);
        filterPS4 = (TextView) v.findViewById(R.id.filter_ps4);
        filterXbox = (TextView) v.findViewById(R.id.filter_xbox);
        filterWii = (TextView) v.findViewById(R.id.filter_wii);

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof FilterInterface) {
            mInterface = (FilterInterface) getActivity();
        } else {
            throw new IllegalArgumentException("Interface must be an instance of the context.");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        filterAll.setOnClickListener(this);
        filterPC.setOnClickListener(this);
        filterPS4.setOnClickListener(this);
        filterXbox.setOnClickListener(this);
        filterWii.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (mInterface == null) {
            mInterface = (FilterInterface) getActivity();
        }

        switch (v.getId()){
            case R.id.filter_all:
                mInterface.filterList("all");
                break;

            case R.id.filter_pc:
                mInterface.filterList("pc");
                break;

            case R.id.filter_ps4:
                mInterface.filterList("ps4");
                break;

            case R.id.filter_xbox:
                mInterface.filterList("xbox");
                break;

            case R.id.filter_wii:
                mInterface.filterList("wii");
                break;
        }
    }

    public void setActiveFilter(String current) {
        //TODO: Add the functionality to change button display

        // now, change the active filter
        if (Objects.equals(current, "all")) {
            filterAll.setBackground(getResources().getDrawable(R.drawable.active_button_shape));
            filterPC.setBackground(getResources().getDrawable(R.drawable.button_shape));
            filterPS4.setBackground(getResources().getDrawable(R.drawable.button_shape));
            filterXbox.setBackground(getResources().getDrawable(R.drawable.button_shape));
            filterWii.setBackground(getResources().getDrawable(R.drawable.button_shape));

            filterAll.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            filterPC.setTextColor(getResources().getColor(R.color.colorAccent));
            filterPS4.setTextColor(getResources().getColor(R.color.colorAccent));
            filterXbox.setTextColor(getResources().getColor(R.color.colorAccent));
            filterWii.setTextColor(getResources().getColor(R.color.colorAccent));
        } else if (Objects.equals(current, "pc")) {
            filterAll.setBackground(getResources().getDrawable(R.drawable.button_shape));
            filterPC.setBackground(getResources().getDrawable(R.drawable.active_button_shape));
            filterPS4.setBackground(getResources().getDrawable(R.drawable.button_shape));
            filterXbox.setBackground(getResources().getDrawable(R.drawable.button_shape));
            filterWii.setBackground(getResources().getDrawable(R.drawable.button_shape));

            filterAll.setTextColor(getResources().getColor(R.color.colorAccent));
            filterPC.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            filterPS4.setTextColor(getResources().getColor(R.color.colorAccent));
            filterXbox.setTextColor(getResources().getColor(R.color.colorAccent));
            filterWii.setTextColor(getResources().getColor(R.color.colorAccent));
        } else if (Objects.equals(current, "ps4")) {
            filterAll.setBackground(getResources().getDrawable(R.drawable.button_shape));
            filterPC.setBackground(getResources().getDrawable(R.drawable.button_shape));
            filterPS4.setBackground(getResources().getDrawable(R.drawable.active_button_shape));
            filterXbox.setBackground(getResources().getDrawable(R.drawable.button_shape));
            filterWii.setBackground(getResources().getDrawable(R.drawable.button_shape));

            filterAll.setTextColor(getResources().getColor(R.color.colorAccent));
            filterPC.setTextColor(getResources().getColor(R.color.colorAccent));
            filterPS4.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            filterXbox.setTextColor(getResources().getColor(R.color.colorAccent));
            filterWii.setTextColor(getResources().getColor(R.color.colorAccent));
        } else if (Objects.equals(current, "xbox")) {
            filterAll.setBackground(getResources().getDrawable(R.drawable.button_shape));
            filterPC.setBackground(getResources().getDrawable(R.drawable.button_shape));
            filterPS4.setBackground(getResources().getDrawable(R.drawable.button_shape));
            filterXbox.setBackground(getResources().getDrawable(R.drawable.active_button_shape));
            filterWii.setBackground(getResources().getDrawable(R.drawable.button_shape));

            filterAll.setTextColor(getResources().getColor(R.color.colorAccent));
            filterPC.setTextColor(getResources().getColor(R.color.colorAccent));
            filterPS4.setTextColor(getResources().getColor(R.color.colorAccent));
            filterXbox.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            filterWii.setTextColor(getResources().getColor(R.color.colorAccent));
        } else if (Objects.equals(current, "wii")) {
            filterAll.setBackground(getResources().getDrawable(R.drawable.button_shape));
            filterPC.setBackground(getResources().getDrawable(R.drawable.button_shape));
            filterPS4.setBackground(getResources().getDrawable(R.drawable.button_shape));
            filterXbox.setBackground(getResources().getDrawable(R.drawable.button_shape));
            filterWii.setBackground(getResources().getDrawable(R.drawable.active_button_shape));

            filterAll.setTextColor(getResources().getColor(R.color.colorAccent));
            filterPC.setTextColor(getResources().getColor(R.color.colorAccent));
            filterPS4.setTextColor(getResources().getColor(R.color.colorAccent));
            filterXbox.setTextColor(getResources().getColor(R.color.colorAccent));
            filterWii.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        }
    }
}
