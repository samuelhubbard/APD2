// Release Date
// Samuel Hubbard

package com.samuelhubbard.android.releasedate;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.samuelhubbard.android.releasedate.ListViewElements.ImageAdapter;
import com.samuelhubbard.android.releasedate.ListViewElements.ImagePagerAdapter;

import java.util.ArrayList;

public class DetailImageView extends AppCompatActivity {

    // member variables
    int mPosition;
    ArrayList<String> mArray;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getIntent().getStringExtra("TITLE"));
        setContentView(R.layout.activity_image_view);

        // get the intent and instantiate the member variables
        Intent p = getIntent();
        mPosition = p.getIntExtra("POSITION", 0);
        mArray = (ArrayList<String>) p.getSerializableExtra("ARRAY");

        // instantiate the image adapter and array of imageviews
        ImageAdapter imageAdapter = new ImageAdapter(this, mArray);
        ArrayList<ImageView> images = new ArrayList<>();

        // Retrieve all the images
        for (int i = 0; i < imageAdapter.getCount(); i++) {
            ImageView imageView = new ImageView(this);
            Glide.with(this).load(imageAdapter.mImageArray.get(i)).into(imageView);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            images.add(imageView);
        }

        // Set the images into ViewPager
        ImagePagerAdapter pageradapter = new ImagePagerAdapter(images);
        ViewPager viewpager = (ViewPager) findViewById(R.id.pager);
        viewpager.setAdapter(pageradapter);
        // Show images following the position
        viewpager.setCurrentItem(mPosition);
    }

    // create a back button for the title bar and make it mirror the back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
