package com.example.lab_001;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lab_001.Adapters.AudioPlayerPagerAdapter;
import com.example.lab_001.Fragments.ContainerFragment;
import com.example.lab_001.Fragments.GenreFragment;
import com.example.lab_001.core.DatabaseHelper;
import com.example.lab_001.core.Song;
import com.example.lab_001.core.SongManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity {
    Messenger mService = null;
    boolean mBound;
    final Messenger mMessenger = new Messenger(new IncomingHandler());
    public boolean isPlaying = false;

    private DatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mSqLiteDatabase;
    private SongManager songManager;


    private ViewPager viewPager;
    private List<Fragment> pages = new ArrayList<>();

    ArrayList<Song> songsList;
    private final int START_PAGE = 0;

    ContainerFragment containerFragment;
    GenreFragment genreFragment;

    public int duration = 0;
    public int currentPosition = 0;

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BackgroundSoundService.MSG_GET_IS_PLAYING:
                    Bundle bundle = msg.getData();
                    isPlaying = bundle.getBoolean(BackgroundSoundService.IS_PLAYING);
                    //Toast.makeText(getApplicationContext(), "isPlaying " + isPlaying, Toast.LENGTH_SHORT).show();
                    break;
                case BackgroundSoundService.MSG_GET_DURATION:
                    duration = msg.arg1;
                    break;
                case BackgroundSoundService.MSG_GET_CURRENT_POSITION:
                    currentPosition = msg.arg1;
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = new Messenger(service);
            mBound = true;
            isPlaying();
        }

        public void onServiceDisconnected(ComponentName className) {
            mService = null;
            mBound = false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //getApplicationContext().deleteDatabase("mydatabase.db");

        mDatabaseHelper = new DatabaseHelper(this, "mydatabase.db", null, 1);
        mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();

        //Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);

        songManager = new SongManager();
        songsList = songManager.getPlayList(mSqLiteDatabase);
        ArrayList<String> genreList = songManager.getGenres(mSqLiteDatabase);

        genreFragment = new GenreFragment(genreList);
        containerFragment = new ContainerFragment(songsList);

        pages.add(genreFragment);
        pages.add(containerFragment);

        this.viewPager = (ViewPager)findViewById(R.id.viewPager);
        AudioPlayerPagerAdapter pagerAdapter = new AudioPlayerPagerAdapter(getSupportFragmentManager(), pages);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(START_PAGE);

        Intent intent = new Intent(this, BackgroundSoundService.class);
        startService(intent);
        bindService(new Intent(this, BackgroundSoundService.class), mConnection, 0);

        mBound = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("my", "onDestroy isP " + isPlaying);
        Log.d("my", "onDestroy mB " + mBound);
        if (!isPlaying){
            if (mBound) {
                unbindService(mConnection);
                mBound = false;
            }
            stopService(new Intent(this, BackgroundSoundService.class));
        }
    }



    ///////////////////////
    //messages

    public void isPlaying(){
        Message msg = Message.obtain(null, BackgroundSoundService.MSG_GET_IS_PLAYING, 0, 0);
        try {
            msg.replyTo = mMessenger;
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void play(){
        isPlaying = true;
        Message msg = Message.obtain(null, BackgroundSoundService.MSG_PLAY, 0, 0);
        try {
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    public void pause(){
        isPlaying = false;
        Message msg = Message.obtain(null, BackgroundSoundService.MSG_PAUSE, 0, 0);
        try {
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void seekTo(int progress){
        Message msg = Message.obtain(null, BackgroundSoundService.MSG_SEEK_TO, progress, 0);
        try {
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void getDuration(){
        try
        {
            Message msg = Message.obtain(null,
                    BackgroundSoundService.MSG_GET_DURATION, 0, 0);
            msg.replyTo = mMessenger;
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void getCurrentPosition(){
        try
        {
            Message msg = Message.obtain(null,
                    BackgroundSoundService.MSG_GET_CURRENT_POSITION, 0, 0);
            msg.replyTo = mMessenger;
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void setSong(Song song){
        if (!mBound) return;
        Log.d("my", "set song Activity");
        Message msg = Message.obtain(null, BackgroundSoundService.MSG_SET_SONG, 0, 0);
        Bundle bResp = new Bundle();
        bResp.putString(BackgroundSoundService.SONG_DATA,  song.Data);
        bResp.putString(BackgroundSoundService.SONG_ARTIST, song.Artist);
        bResp.putString(BackgroundSoundService.SONG_TITLE, song.Title);
        msg.setData(bResp);
        try {
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        isPlaying = true;

    }

    //public native void NativeMethod();



    ////////////

    public void selectGenre(String genreName){
        viewPager.setCurrentItem(1);
        songsList = songManager.getPlayListByGenre(mSqLiteDatabase, genreName);
        containerFragment.setSongsList(songsList);
    }

    public void setHeader(Song song){
        containerFragment.songListFragment.setHeader(song);
    }

    public void playSong(Song song, int position, ArrayList<Song> songsList){
        containerFragment.playSong(song, position, songsList);
    }

    private static final int CM_DELETE_ID = 1;

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, CM_DELETE_ID, 0, "Delete");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == CM_DELETE_ID) {
            // получаем инфу о пункте списка
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

            Song song = containerFragment.songListFragment.songsList.get(acmi.position);
            songManager.deleteSong(mSqLiteDatabase, song);

            File file = new File(song.Data);
            Toast.makeText(getApplicationContext(), "ex" + file.exists(), Toast.LENGTH_SHORT).show();
            Toast.makeText(getApplicationContext(), song.Data, Toast.LENGTH_SHORT).show();

            Toast.makeText(getApplicationContext(), "del" + file.delete(), Toast.LENGTH_SHORT).show();
            /*Uri rootUri = MediaStore.Audio.Media.getContentUriForPath(song.Data);
Data
            getApplicationContext().getContentResolver().delete( rootUri,
                    MediaStore.MediaColumns.DATA + "=?", new String[]{ song.Data } );*/

            containerFragment.songListFragment.songsList.remove(acmi.position);

            containerFragment.songListFragment.songAdapter.notifyDataSetChanged();
            return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
