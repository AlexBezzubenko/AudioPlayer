package com.example.lab_001;

import android.app.FragmentTransaction;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.example.lab_001.Adapters.AudioPlayerPagerAdapter;
import com.example.lab_001.Fragments.ContainerFragment;
import com.example.lab_001.Fragments.GenreFragment;
import com.example.lab_001.core.DatabaseHelper;
import com.example.lab_001.core.Genre;
import com.example.lab_001.core.Song;
import com.example.lab_001.core.SongManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity {
    private DatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mSqLiteDatabase;
    private SongManager songManager;


    private ViewPager viewPager;
    private List<Fragment> pages = new ArrayList<>();
    private LayoutInflater inflater;


    ArrayList<Song> songsList;
    private final int PAGE_AMOUNT = 2;
    private final int START_PAGE = 0;

    private int newSelectedPageIndex = 0;
    private int oldSelectedPageIndex = 0;

    private boolean userScrollChange = false;
    private boolean changeToStart = false;

    ContainerFragment containerFragment;
    GenreFragment genreFragment;

    FragmentTransaction fragmentTransaction;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getApplicationContext().deleteDatabase("mydatabase.db");

        mDatabaseHelper = new DatabaseHelper(this, "mydatabase.db", null, 1);
        mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();

        //Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);

        songManager = new SongManager();
        songsList = songManager.getPlayList(mSqLiteDatabase);
        ArrayList<String> genreList = songManager.getGenres(mSqLiteDatabase);

        genreFragment = new GenreFragment(genreList);
        containerFragment = new ContainerFragment(songsList);

        inflater = LayoutInflater.from(this);

        pages.add(genreFragment);
        pages.add(containerFragment);

        this.viewPager = (ViewPager)findViewById(R.id.viewPager);
        AudioPlayerPagerAdapter pagerAdapter = new AudioPlayerPagerAdapter(getSupportFragmentManager(), pages);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(START_PAGE);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                if (changeToStart) {
                    newSelectedPageIndex = oldSelectedPageIndex = 0;
                    return;
                }
                oldSelectedPageIndex = newSelectedPageIndex;
                newSelectedPageIndex = position;
                userScrollChange = true;
                //Log.d("ME", "OnPageSelected " + position + " " + newSelectedPageIndex + " " + oldSelectedPageIndex);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_IDLE && userScrollChange && !changeToStart) {
                    // user swiped to right direction --> left page
                    if (newSelectedPageIndex < oldSelectedPageIndex) {

                        // user swiped to left direction --> right page
                    } else if (newSelectedPageIndex > oldSelectedPageIndex) {
                    }

                    userScrollChange = false;
                }
            }
        });

    }

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

    public void onClick(View v) {
        //fragmentTransaction = getFragmentManager().beginTransaction();
        switch (v.getId()) {
            case R.id.sf_RelativeLayout:
                //fragmentTransaction.add(R.id.fragment_play, playSongFragment);

                break;
            /*case R.id.btnRemove:
                fTrans.remove(frag1);
                break;
            case R.id.btnReplace:
                fTrans.replace(R.id.frgmCont, frag2);*/
            default:
                break;
        }
        //fragmentTransaction.commit();
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
