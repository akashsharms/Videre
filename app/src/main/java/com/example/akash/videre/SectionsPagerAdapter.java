package com.example.akash.videre;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.akash.videre.fragments.FragmentAlbum;
import com.example.akash.videre.fragments.FragmentAllSongs;
import com.example.akash.videre.fragments.FragmentArtist;

/**
 * Created by akash on 08-03-2017.
 */

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);

    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment=null;
        if (position == 0) {
            fragment=new FragmentAllSongs();
        }
        if (position == 1) {
            fragment=new FragmentAlbum();
        }
        if (position == 2) {
            fragment=new FragmentArtist();
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }

    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "All Songs";
            case 1:
                return "Albums";
            case 2:
                return "Artist";
        }
        return null;
    }
}
