package com.example.lab_001.core;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.example.lab_001.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Александр on 30.09.2016.
 */
public class SongManager {
    final String MEDIA_PATH = Environment.getExternalStorageDirectory()
            .getPath() + "/";
    private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
    private String mp3Pattern = ".mp3";
    // Constructor
    public SongManager() {

    }

    /**
     * Function to read all mp3 files and store the details in
     * ArrayList
     * */

    // get from card with genre from metadata
    public ArrayList<Song> getPlayList(Context context) {
        ArrayList<Song> mSongsList = new ArrayList<>();

        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ALBUM_ID
        };

        Cursor mCursor =  context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection, selection, null, null);

        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
        while (mCursor.moveToNext()) {
            String data = mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));

            metadataRetriever.setDataSource(data);

            String genre;

            try {
                genre = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE);

            } catch (Exception e) {
                 genre = "Unknown";
            }

            if (genre == null){
                genre = "nullstring";
            }

            Song song = new Song(
                    mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)),
                    mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)),
                    mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)),
                    data,
                    mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)),
                    mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)),
                    mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)),
                    mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)),
                    genre
                    );

            mSongsList.add(song);
        }
        mCursor.close();
        return mSongsList;
    }

    // get from base only list

    public ArrayList<Song> getPlayList(SQLiteDatabase mSqLiteDatabase){
        if (mSqLiteDatabase == null){
            return null;
        }

        ArrayList<Song> songs = new ArrayList<>();

        String[] projection = {
                DatabaseHelper.SONG_ID_COLUMN,
                DatabaseHelper.ARTIST_COLUMN,
                DatabaseHelper.TITLE_COLUMN,
                DatabaseHelper.DATA_COLUMN,
                DatabaseHelper.DISPLAY_NAME_COLUMN,
                DatabaseHelper.DURATION_COLUMN,
                DatabaseHelper.ALBUM_COLUMN,
                DatabaseHelper.ALBUM_ID_COLUMN,
                DatabaseHelper.GENRE_COLUMN
        };

        Cursor cursor = mSqLiteDatabase.query("songs", projection,
                null, null, null, null, null);

        if (cursor == null)
            return null;

        while (cursor.moveToNext()) {
            Song song = new Song(
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.SONG_ID_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.ARTIST_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.TITLE_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.DATA_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.DISPLAY_NAME_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.DURATION_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.ALBUM_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.ALBUM_ID_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.GENRE_COLUMN))
            );

            songs.add(song);
        }
        cursor.close();

        return songs;
    }

    public ArrayList<Song> getPlayListByGenre(SQLiteDatabase db, String genreName){
        ArrayList<Song> songs = new ArrayList<>();

        String Query = "Select "
            + DatabaseHelper.SONG_ID_COLUMN + ", "
            + DatabaseHelper.ARTIST_COLUMN + ", "
            + DatabaseHelper.TITLE_COLUMN + ", "
            + DatabaseHelper.DATA_COLUMN + ", "
            + DatabaseHelper.DISPLAY_NAME_COLUMN + ", "
            + DatabaseHelper.DURATION_COLUMN + ", "
            + DatabaseHelper.ALBUM_COLUMN + ", "
            + DatabaseHelper.ALBUM_ID_COLUMN + ", "
            + DatabaseHelper.GENRE_COLUMN + " " +
            "from " + "songs" + " where " + DatabaseHelper.GENRE_COLUMN + " = "
            + "'" + genreName + "'";

        Cursor cursor = db.rawQuery(Query, null);

        if (cursor.moveToFirst()) {
            do {
                Song song = new Song(
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.SONG_ID_COLUMN)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.ARTIST_COLUMN)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.TITLE_COLUMN)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.DATA_COLUMN)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.DISPLAY_NAME_COLUMN)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.DURATION_COLUMN)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.ALBUM_COLUMN)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.ALBUM_ID_COLUMN)),
                genreName
                );
                songs.add(song);
            } while(cursor.moveToNext());
        }
        cursor.close();

        return songs;
    }


    public ArrayList<String> getGenres(SQLiteDatabase mSqLiteDatabase){
        if (mSqLiteDatabase == null){
            return null;
        }

        ArrayList<String> genres = new ArrayList<>();

        String[] projection = {
                DatabaseHelper.GENRE_NAME_COLUMN
        };

        Cursor cursor = mSqLiteDatabase.query("genres", projection,
                null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()){
            do{
                String genreName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.GENRE_NAME_COLUMN));
                genres.add(genreName);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return genres;
    }

    public ArrayList<Genre> getGenres(Context context){
        ArrayList<Genre> genres = new ArrayList<>();

        int index;
        long genreId;
        Uri uri;
        Cursor genreCursor;
        Cursor tempCursor;
        String[] projection1 = {MediaStore.Audio.Genres.NAME, MediaStore.Audio.Genres._ID};
        String[] projection2 = {

                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ALBUM_ID
        };

        genreCursor = context.getContentResolver().query(MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI,
                projection1, null, null, null);


        if (genreCursor.moveToFirst()) {
            do {
                index = genreCursor.getColumnIndexOrThrow(MediaStore.Audio.Genres.NAME);
                String genreName =  genreCursor.getString(index);
                if (genreName.equals(""))
                    genreName = "Unknown";

                index = genreCursor.getColumnIndexOrThrow(MediaStore.Audio.Genres._ID);
                genreId = Long.parseLong(genreCursor.getString(index));
                uri = MediaStore.Audio.Genres.Members.getContentUri("external", genreId);

                tempCursor = context.getContentResolver().query(uri, projection2, null, null, null);
                ArrayList<Song> songs = new ArrayList<>();
                if (tempCursor.moveToFirst()) {
                    do {
                        Song song = new Song(
                                "1",
                                tempCursor.getString(tempCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)),
                                tempCursor.getString(tempCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)),
                                tempCursor.getString(tempCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)),
                                tempCursor.getString(tempCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)),
                                tempCursor.getString(tempCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)),
                                tempCursor.getString(tempCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)),
                                tempCursor.getString(tempCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)),
                                genreName
                        );
                        songs.add(song);
                    } while(tempCursor.moveToNext());
                }

                Genre genre = new Genre(genreName, songs);
                genres.add(genre);
            } while(genreCursor.moveToNext());
        }

        return genres;
    }
}

