package com.example.lab_001.core;

/**
 * Created by Александр on 01.10.2016.
 */

public class Song {
     public String Id;
     public String Artist;
     public String Title;
     public String Data;
     public String DisplayName;
     public String Duration;
     public String Album;
     public String AlbumId;
     public String Genre;

    public Song(String Id, String Artist, String Title, String Data, String DisplayName,
                String Duration, String Album, String AlbumId, String genre) {
        this.Id = Id;
        this.Artist = Artist;
        this.Title = Title;
        this.Data = Data;
        this.DisplayName = DisplayName;
        this.Duration = Duration;
        this.Album = Album;
        this.AlbumId = AlbumId;
        this.Genre = genre;
    }


}

