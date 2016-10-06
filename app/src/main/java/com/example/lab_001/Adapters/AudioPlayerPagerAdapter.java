package com.example.lab_001.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by Александр on 04.10.2016.
 */
public class AudioPlayerPagerAdapter extends FragmentPagerAdapter {

    List<Fragment> pages = null;
    public AudioPlayerPagerAdapter(FragmentManager fm, List<Fragment> pages) {
        super(fm);
        this.pages = pages;
    }

    @Override
    public Fragment getItem(int position) {
        return pages.get(position);
    }

    @Override
    public int getCount() {
        return pages.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "Genres";
            case 1:
                return "Songs";
            default:
                return "Page" + position;
        }
    }
}
