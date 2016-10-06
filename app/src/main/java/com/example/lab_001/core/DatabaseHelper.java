package com.example.lab_001.core;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Александр on 03.10.2016.
 */


public class DatabaseHelper extends SQLiteOpenHelper implements BaseColumns {

    public static final String SONG_ID_COLUMN = "song_id";
    public static final String ARTIST_COLUMN = "artist";
    public static final String TITLE_COLUMN = "title";
    public static final String DATA_COLUMN = "data";
    public static final String DISPLAY_NAME_COLUMN = "display_name";
    public static final String DURATION_COLUMN = "duration";
    public static final String ALBUM_COLUMN = "album";
    public static final String ALBUM_ID_COLUMN = "album_id";
    public static final String GENRE_COLUMN = "genre";

    public static final String GENRE_NAME_COLUMN = "genre_name";



    private static final String DATABASE_NAME = "mydatabase.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_SONGS = "songs";
    private static final String TABLE_GENRES = "genres";


    private Context context;

    private static final String CREATE_SONGS_SCRIPT = "create table "
            + TABLE_SONGS + " (" + BaseColumns._ID
            + " integer primary key autoincrement, " + SONG_ID_COLUMN
            + " text not null, " + ARTIST_COLUMN + " text not null, " + TITLE_COLUMN
            + " text not null, " + DATA_COLUMN   + " text not null, " + DISPLAY_NAME_COLUMN
            + " text not null, " + DURATION_COLUMN   + " text not null, " + ALBUM_COLUMN
            + " text not null, " + ALBUM_ID_COLUMN   + " text not null, " + GENRE_COLUMN
            + " text not null);";
            /*+ GENRE_ID_COLUMN + " integer,"
            + " FOREIGN KEY (" + GENRE_ID_COLUMN + ") REFERENCES " + TABLE_GENRES
            + "(" + BaseColumns._ID +"));";*/

    private static final String CREATE_GENRES_SCRIPT = "create table "
            + TABLE_GENRES + " (" + BaseColumns._ID
            + " integer primary key autoincrement, " + GENRE_NAME_COLUMN
            + " text not null);";

    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                          int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                          int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_GENRES_SCRIPT);
        db.execSQL(CREATE_SONGS_SCRIPT);

        SongManager songManager = new SongManager();
        if (context == null)
            return;


        ArrayList<Genre> genres = songManager.getGenres(context);

        for(Genre genre: genres){
            Log.d("ME","gname " + genre.name );
            String Query = "Select * from " + TABLE_GENRES + " where " + GENRE_NAME_COLUMN + " = "
                    + "'" + genre.name + "'";
            Cursor cursor = db.rawQuery(Query, null);

            if(cursor.getCount() <= 0){
                ContentValues value = new ContentValues();
                value.put(DatabaseHelper.GENRE_NAME_COLUMN, genre.name);
                db.insert(TABLE_GENRES, null, value);

            }
            cursor.close();

            for (Song song: genre.songs) {
                Log.d("ME","    sname " + song.Title);
                ContentValues values = new ContentValues();

                values.put(DatabaseHelper.SONG_ID_COLUMN, song.Id);
                values.put(DatabaseHelper.ARTIST_COLUMN, song.Artist);
                values.put(DatabaseHelper.TITLE_COLUMN, song.Title);
                values.put(DatabaseHelper.DATA_COLUMN, song.Data);
                values.put(DatabaseHelper.DISPLAY_NAME_COLUMN, song.DisplayName);
                values.put(DatabaseHelper.DURATION_COLUMN, song.Duration);
                values.put(DatabaseHelper.ALBUM_COLUMN, song.Album);
                values.put(DatabaseHelper.ALBUM_ID_COLUMN, song.AlbumId);
                values.put(DatabaseHelper.GENRE_COLUMN, song.Genre);

                db.insert(TABLE_SONGS, null, values);
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w("SQLite", "Обновляемся с версии " + oldVersion + " на версию " + newVersion);
        db.execSQL("DROP TABLE IF IT EXISTS " + TABLE_SONGS);
        db.execSQL("DROP TABLE IF IT EXISTS " + TABLE_GENRES);
        onCreate(db);
    }
}