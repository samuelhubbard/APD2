// Release Date
// Samuel Hubbard

package com.samuelhubbard.android.releasedate.ListViewElements;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.samuelhubbard.android.releasedate.R;

import java.util.ArrayList;

public class GalleryAdapter extends ArrayAdapter<String> {
    // member variables
    private Context mContext;
    private int layoutResourceId;
    private ArrayList<String> mGridData = new ArrayList<>();

    // public constructor
    public GalleryAdapter(Context mContext, int layoutResourceId, ArrayList<String> data) {
        super(mContext, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.mGridData = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // instance variables
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            // inflate the row
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            // create a new view holder
            holder = new ViewHolder();
            // set the image view of the holder
            holder.imageView = (ImageView) row.findViewById(R.id.grid_image);

            // set the row tag
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }
        // load in the image to the current image view based on position
        Glide.with(mContext).load(mGridData.get(position)).into(holder.imageView);
        return row;
    }

    // view holder class
    static class ViewHolder {
        ImageView imageView;
    }
}