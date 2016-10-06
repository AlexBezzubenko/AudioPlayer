package com.example.lab_001.core;

import java.util.ArrayList;

/**
 * Created by Александр on 06.10.2016.
 */
public class Genre {
    public String name;
    public ArrayList<Song> songs;

    public Genre(String name, ArrayList<Song> songs){
        this.name = name;
        this.songs = songs;
    }
}
