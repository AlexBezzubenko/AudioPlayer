package com.example.lab_001.Adapters;

/**
 * Created by Александр on 01.10.2016.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


import com.example.lab_001.R;
import com.example.lab_001.core.Genre;
import com.example.lab_001.core.Song;


public class GenreItemAdapter extends ArrayAdapter<String> {
    Context context;

    public GenreItemAdapter(Context context, ArrayList<String> genres) {
        super(context, 0, genres);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String genre = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.genre_item, parent, false);
        }

        TextView tvGenre = (TextView) convertView.findViewById(R.id.genre_item_textview_name);
        tvGenre.setText(genre);
        return convertView;
    }
}
