package com.example.ramona.music_player.Adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.example.ramona.music_player.Constant;
import com.example.ramona.music_player.Entities.SongEntities;
import com.example.ramona.music_player.Fragment.FragmentPlaySongCoverAlbum;
import com.example.ramona.music_player.Fragment.FragmentPlaySongTransparent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ramona on 10/20/2017.
 */

public class PlaySongPagerAdapter extends FragmentStatePagerAdapter {
    private List<SongEntities> mList = new ArrayList<SongEntities>();
    private int mIndex;
    private boolean mCheck;
    private Fragment mListPlaying, mCoverAlbum;

    public PlaySongPagerAdapter(FragmentManager fm, List<SongEntities> mList, int mIndex, boolean mCheck, Fragment mListPlaying, Fragment mCoverAlbum) {
        super(fm);
        this.mList = mList;
        this.mIndex = mIndex;
        this.mCheck = mCheck;
        this.mListPlaying = mListPlaying;
        this.mCoverAlbum = mCoverAlbum;
    }

    @Override
    public int getItemPosition(Object object) {

        return POSITION_NONE;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = new Fragment();
        Bundle bundle = new Bundle();
        if (mList.size() > 0) {
            switch (position) {
                case 0:
                    fragment = mListPlaying;
                    bundle.putParcelableArrayList(Constant.PLAYSONG_TO_TRANSPARENT_FRAGMENT, (ArrayList<SongEntities>) mList);
                    bundle.putInt(Constant.INDEX_SONG_TO_TRANSPARENT_FRAGMENT, mIndex);
                    bundle.putBoolean(Constant.IS_PLAYING, mCheck);
                    break;
                case 1:
                    fragment = mCoverAlbum;
                    bundle.putString(Constant.INDEX, mList.get(mIndex).getmSongID() + "");
                    break;
            }

        }
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    public void setIndex(final int index) {
        mIndex = index;
    }

    public void updateVariableCheck(boolean check) {
        mCheck = check;
    }
}
