// Release Date
// Samuel Hubbard

package com.samuelhubbard.android.releasedate;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.samuelhubbard.android.releasedate.Fragments.AboutAppFragment;

public class AboutAppActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // hang the fragment
        if (savedInstanceState == null) {
            AboutAppFragment aboutFrag = AboutAppFragment.newInstance();
            getFragmentManager().beginTransaction()
                    .replace(R.id.about_container, aboutFrag, AboutAppFragment.TAG)
                    .commit();
        }
    }
}
