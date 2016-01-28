// Release Date
// Samuel Hubbard

package com.samuelhubbard.android.releasedate.ListViewElements;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.samuelhubbard.android.releasedate.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

public class GameListAdapter extends BaseAdapter {

    // creating the ID constant
    private static final int ID_CONSTANT = 0x10100010;

    // member variables
    private Context mContext;
    private ArrayList<GameListObject> mGames;

    // the adapter constructor
    public GameListAdapter(Context c, ArrayList<GameListObject> games) {
        mContext = c;
        mGames = games;
    }

    @Override
    public int getCount() {
        // as long as the array isn't empty do
        if (mGames.size() != 0) {
            // this
            return mGames.size();
            // otherwise
        } else {
            // do this
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        // as long as the array is not null, the requested position is inside of the array
        // parameters, and the position isn't negative
        if (mGames != null && position < mGames.size() && position >= 0) {
            // return the selected object
            return mGames.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return ID_CONSTANT + position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // if a cell view doesn't exist, create one
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_entry_games, parent, false);

        }

        // getting the object for cell population
        GameListObject game = (GameListObject) getItem(position);

        // if the object is a header
        if (game.isSectionHeader()){
            // inflate the view and make it unclickable
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_section_header, parent, false);
            convertView.setClickable(false);

            // link the text view to the layout and populate it
            TextView sectionHeader = (TextView) convertView.findViewById(R.id.section_header);
            String headerText = game.getName() + " " + game.getReleaseYear();
            sectionHeader.setText(headerText);

        // if the object is a game
        } else {
            // inflate the cell view
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_entry_games, parent, false);

            // link all of the cell elements to the layout
            ImageView thumb = (ImageView) convertView.findViewById(R.id.list_thumb);
            TextView name = (TextView) convertView.findViewById(R.id.list_name);
            TextView date = (TextView) convertView.findViewById(R.id.list_date);
            TextView platforms = (TextView) convertView.findViewById(R.id.list_platforms);

            // run a check to see what came in
            if (Objects.equals(game.getThumbnailPath(), "no_image")) {
                // no image from api, load the placeholder
                thumb.setImageResource(R.drawable.no_image);
            } else {
                // Using Picasso to load in the game's thumbnail from the web
                Glide.with(mContext).load(game.getThumbnailPath()).diskCacheStrategy(DiskCacheStrategy.ALL)
                        .fitCenter().error(R.drawable.no_image).into(thumb);
            }

            // populate the text views
            name.setText(game.getName());
            date.setText(game.getFullReleaseDay());
            platforms.setText(game.getPlatforms());
        }

        // return the view to be applied to the UI
        return convertView;
    }
}
