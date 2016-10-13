package com.example.lab_001.Fragments;

/**
 * Created by Александр on 05.10.2016.
 */

        import android.app.Activity;
        import android.content.Intent;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.graphics.Matrix;
        import android.graphics.PointF;
        import android.graphics.PorterDuff;
        import android.media.MediaMetadataRetriever;
        import android.media.MediaPlayer;
        import android.os.Bundle;
        import android.os.Handler;
        import android.support.v4.app.Fragment;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.MotionEvent;
        import android.view.View;
        import android.view.View.OnTouchListener;
        import android.view.ViewGroup;
        import android.view.animation.AlphaAnimation;
        import android.widget.ImageButton;
        import android.widget.ImageView;
        import android.widget.SeekBar;
        import android.widget.TextView;

        import com.example.lab_001.BackgroundSoundService;
        import com.example.lab_001.MainActivity;
        import com.example.lab_001.R;
        import com.example.lab_001.core.Song;

        import java.util.ArrayList;


public class PlaySongFragment extends Fragment implements OnTouchListener, View.OnClickListener {
    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();
    // we can be in one of these 3 states
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mode = NONE;
    // remember some things for zooming
    private PointF start = new PointF();
    private PointF mid = new PointF();
    private float oldDist = 1f;
    private float d = 0f;
    private float newRot = 0f;
    private float[] lastEvent = null;

    private Song song;
    private static Song lastSong;

    private int position;
    private ArrayList<Song> songsList;

    TextView tvArtist;
    TextView tvTitle;
    TextView tvCurDuration;
    TextView tvFullDuration;

    ImageView imageView;
    ImageView littleImageView;
    ImageButton playButton;
    private SeekBar seekBar;
    Handler seekHandler = new Handler();

    MainActivity activity = (MainActivity)getActivity();

    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);

    public PlaySongFragment(){
        super();
    }
    public PlaySongFragment(Song song, int position, ArrayList<Song> songsList){
        this.song = song;
        this.songsList = songsList;
        this.position = position;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "PlayFragment onCreate");
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(LOG_TAG, "PlayFragment onCreateView");
        View view = inflater.inflate(R.layout.playsong_fragment, null);

        tvArtist = (TextView) view.findViewById(R.id.play_tv_artist);
        tvTitle = (TextView) view.findViewById(R.id.play_tv_title);
        tvCurDuration = (TextView) view.findViewById(R.id.play_cur_time);
        tvFullDuration = (TextView) view.findViewById(R.id.play_full_time);

        imageView = (ImageView) view.findViewById(R.id.play_song_image_view);
        imageView.setOnTouchListener(this);
        littleImageView = (ImageView) view.findViewById(R.id.play_song_little_image_view);
        seekBar = (SeekBar)view.findViewById(R.id.seekBar);


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBar.setProgress(progress);
                if (fromUser) {
                    activity.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        /*mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                nextSong();
            }

        });*/

        playButton = (ImageButton)view.findViewById(R.id.play_button);
        ImageButton nextButton = (ImageButton)view.findViewById(R.id.next_button);
        ImageButton prevButton = (ImageButton)view.findViewById(R.id.prev_button);

        setOnTouch(playButton);
        setOnTouch(prevButton);
        setOnTouch(nextButton);

        playButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        prevButton.setOnClickListener(this);

        setSong(song, true);
        seekUpdation();

        return view;
    }


    Runnable run = new Runnable() {
        @Override public void run() {
            Log.d("seek", "update");
            activity.getCurrentPosition();
            setTime(activity.currentPosition, tvCurDuration);
            seekUpdation();
        }
    };

    private void seekUpdation() {
        Log.d("seek", "update");
        Log.d("seek", "before update" + seekBar.getProgress());

        activity.getDuration();
        activity.getCurrentPosition();

        seekBar.setProgress(0); // call these two methods before setting progress.
        seekBar.setMax(activity.duration);
        seekBar.setProgress(activity.currentPosition);
        Log.d("seek", "after update" + seekBar.getProgress());
        seekHandler.postDelayed(run, 1000);
    }

    private void nextSong(){
        if (songsList == null)
            return;
        int count = songsList.size();

        if (count == 1)
            return;

        Song nextSong;
        if (position < count - 1){
            position++;
        }
        else if (position == count - 1) {
            position = 0;
        }
        nextSong = songsList.get(position);
        setSong(nextSong, false);
    }

    @Override
    public void onClick(View v) {
        Log.d("Click", "Clicked screen");
        switch (v.getId()){
            case R.id.play_button:
                ImageButton view = (ImageButton) v;

                view.startAnimation(buttonClick);

                if (!activity.isPlaying) {
                    Log.d("my", "play fragment");
                    activity.play();
                    view.setImageResource(R.drawable.pause_b);
                } else {
                    Log.d("my", "pause fragment");
                    activity.pause();
                    view.setImageResource(R.drawable.play_b);
                }
                break;
            case R.id.next_button:
                v.startAnimation(buttonClick);
                nextSong();
                break;
            case R.id.prev_button:
                v.startAnimation(buttonClick);
                if (songsList == null)
                    return;
                int count2 = songsList.size();

                Song nextSong2;
                Log.d("pos", "onClickPrev" + position);
                if (position > 0){
                    position--;
                }
                else if (position == 0) {
                    position = count2 - 1;
                }

                Log.d("pos", "onSelectPrev" + position);
                nextSong2 = songsList.get(position);
                setSong(nextSong2, false);
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        imageView.setScaleType(ImageView.ScaleType.MATRIX);
        ImageView view = (ImageView) v;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                savedMatrix.set(matrix);
                start.set(event.getX(), event.getY());
                mode = DRAG;
                lastEvent = null;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);
                if (oldDist > 10f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                }
                lastEvent = new float[4];
                lastEvent[0] = event.getX(0);
                lastEvent[1] = event.getX(1);
                lastEvent[2] = event.getY(0);
                lastEvent[3] = event.getY(1);
                d = rotation(event);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                lastEvent = null;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    matrix.set(savedMatrix);
                    float dx = event.getX() - start.x;
                    float dy = event.getY() - start.y;
                    matrix.postTranslate(dx, dy);
                } else if (mode == ZOOM) {
                    float newDist = spacing(event);
                    if (newDist > 10f) {
                        matrix.set(savedMatrix);
                        float scale = (newDist / oldDist);
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }
                    if (lastEvent != null && event.getPointerCount() == 3) {
                        newRot = rotation(event);
                        float r = newRot - d;
                        float[] values = new float[9];
                        matrix.getValues(values);
                        float tx = values[2];
                        float ty = values[5];
                        float sx = values[0];
                        float xc = (view.getWidth() / 2) * sx;
                        float yc = (view.getHeight() / 2) * sx;
                        matrix.postRotate(r, tx + xc, ty + yc);
                    }
                }
                break;
        }

        view.setImageMatrix(matrix);
        return true;
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float)Math.sqrt(x * x + y * y);
    }

    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    private float rotation(MotionEvent event) {
        double delta_x = (event.getX(0) - event.getX(1));
        double delta_y = (event.getY(0) - event.getY(1));
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }


    private void setOnTouch(ImageButton button){
        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        ImageButton view = (ImageButton) v;
                        view.setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL: {
                        ImageButton view = (ImageButton) v;
                        view.clearColorFilter();
                        break;
                    }
                }

                return false;
            }
        });

    }


    private void setSong(Song song, Boolean isPlay){
        //null onTouch
        matrix = new Matrix();
        savedMatrix = new Matrix();
        mode = NONE;
        start = new PointF();
        mid = new PointF();
        oldDist = 1f;
        d = 0f;
        newRot = 0f;
        lastEvent = null;


        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        if (!isPlay) {
            this.lastSong = this.song;
            this.song = song;
        }
        tvArtist.setText(song.Artist);
        tvTitle.setText(song.Title);

        MediaMetadataRetriever metadataRetriever;
        byte[] art;

        metadataRetriever = new MediaMetadataRetriever();
        metadataRetriever.setDataSource(song.Data);

        try {
            art = metadataRetriever.getEmbeddedPicture();
            Bitmap bitmap = BitmapFactory.decodeByteArray(art, 0, art.length);
            imageView.setImageBitmap(bitmap);
            littleImageView.setImageBitmap(bitmap);
            littleImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } catch (Exception e) {
            imageView.setImageResource(R.drawable.default_album);
            littleImageView.setImageResource(R.mipmap.ic_launcher);
        }

        imageView.setImageMatrix(new Matrix());

        //mediaPlayer = MediaPlayer.create(inflater.getContext(), Uri.parse(song.Data));

        activity = (MainActivity)getActivity();

        activity.setSong(song);
        Log.d("my", "set song fragment");
        playButton.setImageResource(R.drawable.pause_b);

        activity.getDuration();
        seekBar.setMax(activity.duration);
        setTime(activity.duration, tvFullDuration);

        /*try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(song.Data);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();
                    playButton.setImageResource(R.drawable.pause_b);
                    Log.d("seek", ""+seekBar.getMax());

                    seekBar.setMax(mediaPlayer.getDuration());
                    setTime(mediaPlayer.getDuration(), tvFullDuration);

                    Log.d("seek", "after set " + seekBar.getMax());
                }
            });
            mediaPlayer.prepareAsync();
        } catch (Exception e){
            Log.d("Er", "Error");
        }*/

    }

    private void setTime(int time, TextView textView){
        int ms = time;
        double seconds = ms / 1000;

        int modSec = (int) (seconds % 60);

        String sSeconds;

        if (modSec >= 10)
            sSeconds = "" + modSec;
        else
            sSeconds = "0" + modSec;

        textView.setText("" + (int) (seconds / 60) + ":" + sSeconds);
    }

    final String LOG_TAG = "myLogs";

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(LOG_TAG, "PlayFragment onAttach");
    }



    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(LOG_TAG, "PlayFragment onActivityCreated");
    }
    public void onStart() {
        super.onStart();
        Log.d(LOG_TAG, "PlayFragment onStart");
    }
    public void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "PlayFragment onResume");
    }
    public void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "PlayFragment onPause");
    }
    public void onStop() {
        super.onStop();
        Log.d(LOG_TAG, "PlayFragment onStop");
    }
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(LOG_TAG, "PlayFragment onDestroyView");
    }
    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "PlayFragment onDestroy");
    }
    public void onDetach() {
        super.onDetach();
        Log.d(LOG_TAG, "PlayFragment onDetach");
        if (this.lastSong != null){
            ((MainActivity) getActivity()).setHeader(this.lastSong);
        }
        this.lastSong = song;
    }

}