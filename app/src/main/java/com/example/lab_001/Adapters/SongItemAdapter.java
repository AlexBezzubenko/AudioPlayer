package com.example.lab_001.Adapters;

/**
 * Created by Александр on 01.10.2016.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


import com.example.lab_001.R;
import com.example.lab_001.core.Song;


public class SongItemAdapter extends ArrayAdapter<Song> {
    Context context;
    public SongItemAdapter(Context context, ArrayList<Song> songs) {
        super(context, 0, songs);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Song song = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);
        }


        TextView tvArtist = (TextView) convertView.findViewById(R.id.Artist_textView);
        TextView tvTitle = (TextView) convertView.findViewById(R.id.Title_textView);

        TextView tvDuration = (TextView) convertView.findViewById(R.id.Duration_textView);

        if (song.Artist.equals(new String("<unknown>"))){
            String[] s = song.Title.split("-");
            if (s.length == 2){
                setShortString(tvArtist, s[0].trim());
                setShortString(tvTitle, s[1].trim());
            }
            else {
                setShortString(tvArtist, song.Title);
                tvTitle.setText("");
            }
        }
        else {
            setShortString(tvArtist, song.Artist);
            setShortString(tvTitle, song.Title);
        }
        if (song.Duration != null) {
            int ms = Integer.parseInt(song.Duration);
            double seconds = ms / 1000;
            tvDuration.setText("" + (int) (seconds / 60) + ":" + (int) (seconds % 60));
        }
        else {
            tvDuration.setText("--:--");
        }


        //--------Genre

        ImageView imageView = (ImageView)convertView.findViewById(R.id.image_view);
        MediaMetadataRetriever metadataRetriever;
        byte[] art;

        metadataRetriever = new MediaMetadataRetriever();
        metadataRetriever.setDataSource(song.Data);

        try {
            art = metadataRetriever.getEmbeddedPicture();
            Bitmap songImage = BitmapFactory .decodeByteArray(art, 0, art.length);
            imageView.setImageBitmap(songImage);
            //tvGenre.setText(metadataRetriever .extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE));
        } catch (Exception e) {
            imageView.setImageResource(R.mipmap.ic_launcher);
            //tvGenre.setText("Unknown Genre");
        }


        /*Cursor cursor = getContext().getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM_ART},
                MediaStore.Audio.Albums._ID + "=?",
                new String[]{song.AlbumId},
                null);

        if (cursor != null && cursor.moveToFirst()){
            String thisArt = cursor.getString(
                    cursor.getColumnIndex(android.provider.MediaStore.Audio.Albums.ALBUM_ART));
            cursor.close();

            if (thisArt != null) {
                Log.d("image string", thisArt);
            } else {
                Log.d("image string", "str is null");
            }

            Bitmap bm= BitmapFactory.decodeFile(thisArt);
            imageView.setImageBitmap(bm);
        }*/
        return convertView;
    }

    private void setShortString(TextView textView, String text){
        if (text.length() > 34)
            textView.setText(text.substring(0, 30) + "...");
        else
            textView.setText(text);
    }
}
