package com.example.lab_001.Fragments;

/**
 * Created by Александр on 05.10.2016.
 */

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lab_001.MainActivity;
import com.example.lab_001.R;
import com.example.lab_001.core.Song;


public class PlaySongFragment extends Fragment {
    Song song;
    MediaPlayer mediaPlayer = new MediaPlayer();

    public PlaySongFragment(){
        super();
    }
    public PlaySongFragment(Song song){
        this.song = song;
    }
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(LOG_TAG, "PlayFragment onCreateView");
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
            imageView.setImageResource(R.drawable.default_album);
            littleImageView.setImageResource(R.mipmap.ic_launcher);
        }

        Button button = (Button)view.findViewById(R.id.play_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer = MediaPlayer.create(inflater.getContext(), Uri.parse(song.Data));
                    mediaPlayer.start();
                } else {
                    mediaPlayer.pause();
                }
            }
        });

        return view;
    }

    final String LOG_TAG = "myLogs";

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(LOG_TAG, "PlayFragment onAttach");
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "PlayFragment onCreate");
    }


    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(LOG_TAG, "PlayFragment onActivityCreated");
    }

    public void onStart() {
        super.onStart();
        Log.d(LOG_TAG, "PlayFragment onStart");
    }

    public void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "PlayFragment onResume");
    }

    public void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "PlayFragment onPause");
    }

    public void onStop() {
        super.onStop();
        Log.d(LOG_TAG, "PlayFragment onStop");
    }

    public void onDestroyView() {
        super.onDestroyView();
        Log.d(LOG_TAG, "PlayFragment onDestroyView");
    }

    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "PlayFragment onDestroy");
    }

    public void onDetach() {
        super.onDetach();
        ((MainActivity) getActivity()).setHeader(song);
        Log.d(LOG_TAG, "PlayFragment onDetach");
    }

}
