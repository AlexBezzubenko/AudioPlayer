package com.example.lab_001.Fragments;

/**
 * Created by Александр on 05.10.2016.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lab_001.R;
import com.example.lab_001.core.Song;

import java.util.ArrayList;

public class ContainerFragment extends Fragment {
    ArrayList<Song> songsList = new ArrayList<>();

    public SongListFragment songListFragment;
    PlaySongFragment playSongFragment;

    private final static String TAG_PLAY = "songListActivity";
    private final static String TAG_LIST = "playSongActivity";
    private FragmentTransaction transaction;

    public ContainerFragment(){
        super();
    }
    public ContainerFragment(ArrayList<Song> songsList){
        this.songsList = songsList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.container_fragment, null);

        songListFragment = new SongListFragment(songsList);

        transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_songs, songListFragment);
        transaction.commit();

        return view;
    }


    public void playSong(Song song) {
            playSongFragment = new PlaySongFragment(song);
            transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_songs, playSongFragment);
            transaction.addToBackStack(null);
            transaction.commit();
    }

    public void setSongsList(ArrayList<Song> songsList){
        songListFragment.setSongList(songsList);
    }
}