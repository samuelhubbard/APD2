// Release Date
// Samuel Hubbard

package com.samuelhubbard.android.releasedate;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class DetailImageView extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getIntent().getStringExtra("TITLE"));
        setContentView(R.layout.activity_image_view);

        String imageAddress = getIntent().getStringExtra("ADDRESS");

        ImageView imageView = (ImageView) findViewById(R.id.image);

        Picasso.with(this).load(imageAddress).into(imageView);
    }

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
