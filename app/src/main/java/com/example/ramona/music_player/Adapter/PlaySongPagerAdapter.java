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

    public PlaySongPagerAdapter(FragmentManager fm, List<SongEntities> mList, int mIndex, boolean mCheck) {
        super(fm);
        this.mList = mList;
        this.mIndex = mIndex;
        this.mCheck = mCheck;
    }

    @Override
    public int getItemPosition(Object object) {

        return POSITION_NONE;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragListSong = new FragmentPlaySongTransparent();
        Fragment fragCoverAlbum = new FragmentPlaySongCoverAlbum();
        Bundle bundle = new Bundle();
        if (mList.size() > 0) {
            switch (position) {
                case 0:
                    bundle.putParcelableArrayList(Constant.PLAYSONG_TO_TRANSPARENT_FRAGMENT, (ArrayList<SongEntities>) mList);
                    bundle.putInt(Constant.INDEX_SONG_TO_TRANSPARENT_FRAGMENT, mIndex);
                    bundle.putBoolean(Constant.IS_PLAYING, mCheck);
                    fragListSong.setArguments(bundle);
                    return fragListSong;
                case 1:
                    Log.e("Index", mIndex + "");
                    bundle.putString("index", mList.get(mIndex).getmSongID() + "");
                    fragCoverAlbum.setArguments(bundle);
                    return fragCoverAlbum;
            }

        }
        return fragListSong;
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
