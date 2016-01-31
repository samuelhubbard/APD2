// Release Date
// Samuel Hubbard

package com.samuelhubbard.android.releasedate.ListViewElements;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

public class ImagePagerAdapter extends PagerAdapter {
    // member variable
    private ArrayList<ImageView> images;

    // public constructor
    public ImagePagerAdapter(ArrayList<ImageView> images) {
        this.images = images;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        //create imageview based on array position and add it to the container in the view group
        ImageView imageView = images.get(position);
        container.addView(imageView);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // remove
        container.removeView(images.get(position));
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == o;
    }
}
