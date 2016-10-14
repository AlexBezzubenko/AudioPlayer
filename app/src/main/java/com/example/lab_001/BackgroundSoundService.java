package com.example.lab_001;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;

public class BackgroundSoundService extends Service {
    MediaPlayer mediaPlayer = new MediaPlayer();

    public static final String IS_PLAYING = "IS_PLAYING";
    public static final String SONG_DATA = "SONG_DATA";
    public static final String SONG_ARTIST = "SONG_ARTIST";
    public static final String SONG_TITLE = "SONG_TITLE";

    NotificationManager mNM;

    final Messenger mMessenger = new Messenger(new IncomingHandler());


    public static final int MSG_GET_IS_PLAYING = 1;
    public static final int MSG_PLAY = 2;
    public static final int MSG_PAUSE = 3;
    public static final int MSG_SET_SONG = 4;
    public static final int MSG_SEEK_TO = 5;
    public static final int MSG_GET_DURATION = 6;
    public static final int MSG_GET_CURRENT_POSITION = 7;

    String songData = "";
    String songArtist = "";
    String songTitle = "";

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SET_SONG:
                    songData = msg.getData().getString(SONG_DATA);
                    songArtist = msg.getData().getString(SONG_ARTIST);
                    songTitle = msg.getData().getString(SONG_TITLE);
                    setSong();
                    break;
                case MSG_PLAY:
                    Log.d("my", "play service");
                    mediaPlayer.start();
                    break;
                case MSG_PAUSE:
                    Log.d("my", "pause service");
                    mediaPlayer.pause();
                    break;
                case MSG_SEEK_TO:
                    Log.d("my", "seek to service");
                    mediaPlayer.seekTo(msg.arg1);
                    break;
                case MSG_GET_DURATION:
                    if (mediaPlayer != null) {
                        Message message = Message.obtain(null, MSG_GET_DURATION, mediaPlayer.getDuration(), 0);
                        try {
                            msg.replyTo.send(message);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case MSG_GET_CURRENT_POSITION:
                    if (mediaPlayer != null) {
                        Message message = Message.obtain(null, MSG_GET_CURRENT_POSITION, mediaPlayer.getCurrentPosition(), 0);
                        try {
                            msg.replyTo.send(message);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case MSG_GET_IS_PLAYING:
                    Message message = Message.obtain(null, MSG_GET_IS_PLAYING, 0, 0);
                    Bundle bundle = new Bundle();
                    if (mediaPlayer != null)
                        bundle.putBoolean(IS_PLAYING, mediaPlayer.isPlaying());
                    message.setData(bundle);
                    try {
                        msg.replyTo.send(message);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }


    @Override
    public void onCreate() {
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        //showNotification();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        showNotification();
        return(START_NOT_STICKY);
    }

    @Override
    public void onDestroy() {
        stop();
    }


    @Override
    public IBinder onBind(Intent intent) {
        //Toast.makeText(getApplicationContext(), "binding", Toast.LENGTH_SHORT).show();
        return mMessenger.getBinder();
    }

    private void showNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("AudioPlayer")
                .setContentText("Nothing is playing...")

                .setContentIntent(pendingIntent).build();

        startForeground(1337, notification);
    }

    private void setSong(){
        try {
            Log.d("my", "set song service");
            mediaPlayer.reset();
            mediaPlayer.setDataSource(songData);

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();
                    Log.d("my", "start set song service");
                    Intent notificationIntent = new Intent(BackgroundSoundService.this, MainActivity.class);

                    PendingIntent pendingIntent = PendingIntent.getActivity(BackgroundSoundService.this, 0,
                            notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                    Notification notification = new NotificationCompat.Builder(BackgroundSoundService.this)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle(songArtist)
                            .setContentText(songTitle)

                            .setContentIntent(pendingIntent).build();

                    startForeground(1337, notification);
                }
            });
            mediaPlayer.prepareAsync();
        } catch (Exception e){
            Log.d("Er", "Error");
        }
    }

    private void stop() {
            Log.w(getClass().getName(), "Got to stop()!");
            mediaPlayer.release();
            stopForeground(true);
    }


}