package com.example.lab_001.Fragments;

/**
 * Created by Александр on 05.10.2016.
 */

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lab_001.MainActivity;
import com.example.lab_001.R;
import com.example.lab_001.core.Song;

import java.util.ArrayList;


public class PlaySongFragment extends Fragment {
    private Song song;
    private static Song lastSong;

    private int position;
    private ArrayList<Song> songsList;

    TextView tvArtist;
    TextView tvTitle;

    ImageView imageView;
    ImageView littleImageView;
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);

    static MediaPlayer mediaPlayer = new MediaPlayer();

    public PlaySongFragment(){
        super();
    }
    public PlaySongFragment(Song song, int position, ArrayList<Song> songsList){
        this.song = song;
        this.songsList = songsList;
        this.position = position - 1;
    }
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(LOG_TAG, "PlayFragment onCreateView");
        View view = inflater.inflate(R.layout.playsong_fragment, null);

        tvArtist = (TextView) view.findViewById(R.id.play_tv_artist);
        tvTitle = (TextView) view.findViewById(R.id.play_tv_title);

        imageView = (ImageView) view.findViewById(R.id.play_song_image_view);
        littleImageView = (ImageView) view.findViewById(R.id.play_song_little_image_view);

        setSong(song, true);

        ImageButton playButton = (ImageButton)view.findViewById(R.id.play_button);
        ImageButton nextButton = (ImageButton)view.findViewById(R.id.next_button);
        ImageButton prevButton = (ImageButton)view.findViewById(R.id.prev_button);

        playButton.setImageResource(R.drawable.pause_b);

        setOnTouch(playButton);
        setOnTouch(prevButton);
        setOnTouch(nextButton);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageButton view = (ImageButton) v;

                view.startAnimation(buttonClick);
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                    view.setImageResource(R.drawable.pause_b);
                } else {
                    mediaPlayer.pause();
                    view.setImageResource(R.drawable.play_b);
                }
            }
        });


        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
                if (songsList == null)
                    return;
                int count = songsList.size();

                Song nextSong;
                Log.d("pos", "onClickPrev" + position);
                if (position > 0){
                    position--;
                }
                else if (position == 0) {
                    position = count - 1;
                }

                Log.d("pos", "onSelectPrev" + position);
                nextSong = songsList.get(position);
                setSong(nextSong, false);
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
                if (songsList == null)
                    return;
                int count = songsList.size();

                Song nextSong;
                Log.d("pos", "onClickNext" + position);
                if (position < count - 1){
                    position++;
                }
                else if (position == count - 1) {
                    position = 0;
                }
                Log.d("pos", "onSelectNext" + position);
                nextSong = songsList.get(position);
                setSong(nextSong, false);
            }
        });

        return view;
    }

    private void setOnTouch(ImageButton button){
        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        ImageButton view = (ImageButton) v;
                        view.setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL: {
                        ImageButton view = (ImageButton) v;
                        view.clearColorFilter();
                        break;
                    }
                }

                return false;
            }
        });

    }


    private void setSong(Song song, Boolean isPlay){
        if (!isPlay) {
            this.lastSong = this.song;
            this.song = song;
        }
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

        //mediaPlayer = MediaPlayer.create(inflater.getContext(), Uri.parse(song.Data));
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(song.Data);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();
                }
            });
            mediaPlayer.prepareAsync();
        } catch (Exception e){
            Log.d("Er", "Error");
        }
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
        Log.d(LOG_TAG, "PlayFragment onDetach");
        if (this.lastSong != null){
            ((MainActivity) getActivity()).setHeader(this.lastSong);
        }
        this.lastSong = song;
    }

}
