// Release Date
// Samuel Hubbard

package com.samuelhubbard.android.releasedate.ListViewElements;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {

    // member variables
    Context mContext;
    public ArrayList<String> mImageArray;

    // public constructor
    public ImageAdapter(Context c, ArrayList<String> images) {
        mContext = c;
        mImageArray = images;
    }

    @Override
    public int getCount() {
        return mImageArray.size();
    }

    @Override
    public Object getItem(int position) {
        return mImageArray.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // instance variable
        ImageView imageView;

        // If it's not recycled, initialize some attributes
        if (convertView == null) {
            // create a new image view
            imageView = new ImageView(mContext);
            // set the scale type for that image view
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        } else {
            // set the image view to the convert view
            imageView = (ImageView) convertView;
        }

        // load in the image based on current array position
        Glide.with(mContext).load(mImageArray.get(position)).into(imageView);
        return imageView;
    }
}
