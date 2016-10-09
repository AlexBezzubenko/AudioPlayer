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

import java.util.ArrayList;

public class SongListFragment extends Fragment {
    private static Song lastSong;
    private boolean isHeaderAdded = false;

    public ArrayList<Song> songsList = new ArrayList<>();
    ListView songListView;
    LayoutInflater inflater;
    View _rootView, header;
    public SongItemAdapterCached songAdapter;

    public SongListFragment(){
        super();
    }
    public SongListFragment(ArrayList<Song> songsList){
        this.songsList = songsList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (_rootView == null) {

            _rootView = inflater.inflate(R.layout.songlist_fragment, null);
            this.inflater = inflater;

            songListView = (ListView)_rootView.findViewById(R.id.songs_list_view);

            header = inflater.inflate(R.layout.header, null);

            if (songsList != null) {
                //SongItemAdapter songAdapter = new SongItemAdapter(inflater.getContext(), songsList);
                songAdapter = new SongItemAdapterCached(inflater.getContext(), songsList);
                songListView.setAdapter(songAdapter);
                registerForContextMenu(songListView);

                songListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        Song song = (Song) songListView.getItemAtPosition(position);
                        Log.d("pos", "getItem" + position);

                        if (song != null)
                            ((MainActivity) getActivity()).playSong(song, position, songsList);
                    }
                });
            }

            header.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (lastSong != null)
                        ((MainActivity) getActivity()).playSong(lastSong, 0, null);
                }
            });

            Drawable background = songListView.getBackground();
            background.setAlpha(50);
        } else {

        }
        return _rootView;
    }

    @Override
    public void onDestroyView() {
        if (_rootView.getParent() != null) {
            ((ViewGroup)_rootView.getParent()).removeView(_rootView);
        }
        super.onDestroyView();
    }

    public void setHeader(Song song){
        if (!isHeaderAdded) {
            songListView.addHeaderView(header, null, false);
            isHeaderAdded = true;
        }
        lastSong = song;
        TextView tvArtist = (TextView) header.findViewById(R.id.h_Artist_textView);
        TextView tvTitle = (TextView) header.findViewById(R.id.h_Title_textView);
        TextView tvDuration = (TextView) header.findViewById(R.id.h_Duration_textView);

        ImageView imageView = (ImageView) header.findViewById(R.id.h_image_view);

        tvArtist.setText(song.Artist);
        tvTitle.setText(song.Title);
        if (song.Duration != null) {
            int ms = Integer.parseInt(song.Duration);
            double seconds = ms / 1000;
            tvDuration.setText("" + (int) (seconds / 60) + ":" + (int) (seconds % 60));
        }
        else {
            tvDuration.setText("--:--");
        }

        MediaMetadataRetriever metadataRetriever;
        byte[] art;

        metadataRetriever = new MediaMetadataRetriever();
        metadataRetriever.setDataSource(song.Data);

        try {
            art = metadataRetriever.getEmbeddedPicture();
            Bitmap bitmap = BitmapFactory.decodeByteArray(art, 0, art.length);
            imageView.setImageBitmap(bitmap);
        } catch (Exception e) {
            imageView.setImageResource(R.mipmap.ic_launcher);
        }
    }
    public void setSongList(ArrayList<Song> songsList){
        if (songsList != null) {
            //SongItemAdapterCached songAdapter = new SongItemAdapterCached(inflater.getContext(), songsList);
            this.songsList.clear();
            this.songsList.addAll(songsList);

            songAdapter.notifyDataSetChanged();
            //songListView.setAdapter(songAdapter);
        }
    }
}