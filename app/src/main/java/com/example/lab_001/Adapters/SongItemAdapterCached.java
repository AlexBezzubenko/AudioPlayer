package com.example.lab_001.Adapters;

/**
 * Created by Александр on 01.10.2016.
 */

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


import com.example.lab_001.R;
import com.example.lab_001.core.Song;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;


public class SongItemAdapterCached extends ArrayAdapter<Song> {
    private static Context context;
    public SongItemAdapterCached(Context context, ArrayList<Song> songs) {
        super(context, 0, songs);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        Song song = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);
            holder = new ViewHolder();
            holder.tvArtist = (TextView) convertView.findViewById(R.id.Artist_textView);
            holder.tvTitle = (TextView) convertView.findViewById(R.id.Title_textView);
            holder.tvDuration = (TextView) convertView.findViewById(R.id.Duration_textView);

            holder.imageView = (ImageView) convertView.findViewById(R.id.image_view);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        /*if (song.Artist.equals(new String("<unknown>"))){
            String[] s = song.Title.split("-");
            if (s.length == 2){
                setShortString(holder.tvArtist, s[0].trim());
                setShortString(holder.tvTitle, s[1].trim());
            }
            else {
                setShortString(holder.tvArtist, song.Title);
                holder.tvTitle.setText("");
            }
        }
        else {
            setShortString(holder.tvArtist, song.Artist);
            setShortString(holder.tvTitle, song.Title);
        }*/

        setShortString(holder.tvArtist, song.Artist);
        setShortString(holder.tvTitle, song.Title);
        if (song.Duration != null) {
            int ms = Integer.parseInt(song.Duration);
            double seconds = ms / 1000;

            int modSec = (int)(seconds % 60);

            String sSeconds;

            if (modSec > 10)
                sSeconds = "" + modSec;
            else
                sSeconds = "0" + modSec;

            holder.tvDuration.setText("" + (int) (seconds / 60) + ":" + sSeconds);
        }
        else {
            holder.tvDuration.setText("--:--");
        }


        holder.position = position;
        new ThumbnailTask(position, song.Data, holder)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);

        return convertView;
    }

    private void setShortString(TextView textView, String text){
        if (text.length() > 34)
            textView.setText(text.substring(0, 30) + "...");
        else
            textView.setText(text);
    }

    private static class ThumbnailTask extends AsyncTask {
        private int mPosition;
        private String data;
        private ViewHolder mHolder;
        private Bitmap bitmap;
        private boolean error = false;

        public ThumbnailTask(int position,String data, ViewHolder holder) {
            mPosition = position;
            this.data = data;
            mHolder = holder;
        }

        @Override
        protected Void doInBackground(Object[] params) {
            MediaMetadataRetriever metadataRetriever;
            byte[] art;

            metadataRetriever = new MediaMetadataRetriever();
            metadataRetriever.setDataSource(data);

            try {
                art = metadataRetriever.getEmbeddedPicture();
                bitmap = BitmapFactory .decodeByteArray(art, 0, art.length);
            } catch (Exception e) {
                error = true;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            if (mHolder.position == mPosition) {
                Picasso.with(context).setIndicatorsEnabled(true);
                if (!error) {
                    mHolder.imageView.setImageBitmap(bitmap);
                    mHolder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                }
                else {
                    String myCloud = "http://res.cloudinary.com/dyuwhmiwj/image/upload/v1476200292/default_album_cefhqf.jpg";
                    String noCachePath = "https://pixabay.com/static/uploads/photo/2016/06/01/09/21/music-1428660_960_720.jpg";
                    Picasso.with(context).load(noCachePath)
                    .resize(50, 50).error(R.mipmap.ic_launcher).networkPolicy(NetworkPolicy.NO_CACHE,
                            NetworkPolicy.NO_STORE).memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).into(mHolder.imageView);


                    //Picasso.with(context).load(R.mipmap.ic_launcher).into(mHolder.imageView);

                    //mHolder.imageView.setImageResource(R.mipmap.ic_launcher);
                }

            }
        }
    }

    private static class ViewHolder {
        public TextView tvArtist;
        public TextView tvTitle;
        public TextView tvDuration;

        public ImageView imageView;
        public int position;
    }
}
