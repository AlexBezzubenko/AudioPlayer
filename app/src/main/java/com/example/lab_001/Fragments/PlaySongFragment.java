package com.example.lab_001.Fragments;

/**
 * Created by Александр on 05.10.2016.
 */

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.lab_001.Adapters.SongItemAdapter;
import com.example.lab_001.Adapters.SongItemAdapterCached;
import com.example.lab_001.MainActivity;
import com.example.lab_001.R;
import com.example.lab_001.core.Song;


public class PlaySongFragment extends Fragment {
    Song song;

    public PlaySongFragment(){
        super();
    }
    public PlaySongFragment(Song song){
        this.song = song;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.playsong_fragment, null);

        TextView tvArtist = (TextView) view.findViewById(R.id.play_tv_artist);
        TextView tvTitle = (TextView) view.findViewById(R.id.play_tv_title);

        ImageView imageView = (ImageView) view.findViewById(R.id.play_song_image_view);
        ImageView littleImageView = (ImageView) view.findViewById(R.id.play_song_little_image_view);

        tvArtist.setText(song.Artist);
        tvTitle.setText(song.Title);

        MediaMetadataRetriever metadataRetriever;
        byte[] art;

        metadataRetriever = new MediaMetadataRetriever();
        metadataRetriever.setDataSource(song.Data);

        try {
            art = metadataRetriever.getEmbeddedPicture();
            Bitmap bitmap = BitmapFactory.decodeByteArray(art, 0, art.length);
            imageView.setImageBitmap(bitmap);
            littleImageView.setImageBitmap(bitmap);
            littleImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } catch (Exception e) {
            imageView.setImageResource(R.mipmap.ic_launcher);
            littleImageView.setImageResource(R.mipmap.ic_launcher);
        }





        return view;
    }

}
