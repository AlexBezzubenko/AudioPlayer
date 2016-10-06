package com.example.lab_001.Fragments;

/**
 * Created by Александр on 05.10.2016.
 */

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.lab_001.Adapters.SongItemAdapter;
import com.example.lab_001.Adapters.SongItemAdapterCached;
import com.example.lab_001.MainActivity;
import com.example.lab_001.R;
import com.example.lab_001.core.Song;

import java.util.ArrayList;

public class SongListFragment extends Fragment {
    ArrayList<Song> songsList = new ArrayList<>();
    ListView songListView;
    LayoutInflater inflater;
    View _rootView;

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

            if (songsList != null) {
                //SongItemAdapter songAdapter = new SongItemAdapter(inflater.getContext(), songsList);
                SongItemAdapterCached songAdapter = new SongItemAdapterCached(inflater.getContext(), songsList);
                songListView.setAdapter(songAdapter);

                songListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        Song song = (Song) songListView.getItemAtPosition(position);
                        Log.d("ME", "" + song.Title);
                        Log.d("ME", "" + song.Artist);
                        Log.d("ME", "" + song.Data);

                        if (song != null)
                            ((MainActivity) getActivity()).playSong(song);
                    }
                });
            }
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

    public void setSongList(ArrayList<Song> songsList){
        if (songsList != null) {
            SongItemAdapterCached songAdapter = new SongItemAdapterCached(inflater.getContext(), songsList);
            songListView.setAdapter(songAdapter);
        }
    }
}