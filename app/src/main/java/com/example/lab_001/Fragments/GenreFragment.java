package com.example.lab_001.Fragments;

/**
 * Created by Александр on 05.10.2016.
 */
import java.util.ArrayList;
import java.util.List;

import android.graphics.drawable.Drawable;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.lab_001.Adapters.GenreItemAdapter;
import com.example.lab_001.MainActivity;
import com.example.lab_001.R;
import com.example.lab_001.core.Genre;
import com.example.lab_001.core.Song;


public class GenreFragment extends Fragment {
    ArrayList<String> genresList = new ArrayList<>();
    ViewPager viewPager;

    public GenreFragment(){
        super();
    }
    public GenreFragment(ArrayList<String> genresList){
        this.genresList = genresList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.genre_fragment, null);

        final ListView genreListView = (ListView) view.findViewById(R.id.genre_list_view);
        Drawable background = genreListView.getBackground();
        background.setAlpha(50);

        if (genresList != null) {
            GenreItemAdapter genreItemAdapter = new GenreItemAdapter(inflater.getContext(), genresList);
            genreListView.setAdapter(genreItemAdapter);

            genreListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    Log.d("ME", "genreItemClick: position = " + position + ", id = "
                            + id);
                    String genreName = (String)genreListView.getItemAtPosition(position);
                    ((MainActivity) getActivity()).selectGenre(genreName);
                }
            });
        }

        return view;
    }
}
