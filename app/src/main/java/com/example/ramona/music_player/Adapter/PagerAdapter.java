package com.example.ramona.music_player.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.ramona.music_player.Constant;
import com.example.ramona.music_player.Entities.AlbumEntities;
import com.example.ramona.music_player.Entities.ArtistEntities;
import com.example.ramona.music_player.Entities.SongEntities;
import com.example.ramona.music_player.Fragment.FragmentListAlbum;
import com.example.ramona.music_player.Fragment.FragmentListArtist;
import com.example.ramona.music_player.Fragment.FragmentListSong;
import com.example.ramona.music_player.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by quan.tn on 8/24/2017.
 */

public class PagerAdapter extends FragmentStatePagerAdapter {
    private List<SongEntities> mListSong;
    private List<AlbumEntities> mListAlbum;
    private List<ArtistEntities> mListArtist;
    private Context context;

    public PagerAdapter(FragmentManager fm, List<SongEntities> mListSong, List<AlbumEntities> mListAlbum, List<ArtistEntities> mListArtist, Context context) {
        super(fm);
        this.mListSong = mListSong;
        this.mListAlbum = mListAlbum;
        this.mListArtist = mListArtist;
        this.context = context;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragListSong = new FragmentListSong();
        Fragment fragListAlbum = new FragmentListAlbum();
        Fragment fragListArtist = new FragmentListArtist();
        Bundle bundle = new Bundle();
        if (mListSong != null || mListAlbum != null || mListArtist != null) {
            switch (position) {
                case 0:
                    bundle.putParcelableArrayList(Constant.SONG_TO_FRAGMENT, (ArrayList<SongEntities>) mListSong);
                    fragListSong.setArguments(bundle);
                    return fragListSong;
                case 1:
                    bundle.putParcelableArrayList(Constant.ALBUM_TO_FRAGMENT, (ArrayList<AlbumEntities>) mListAlbum);
                    fragListAlbum.setArguments(bundle);
                    return fragListAlbum;
                case 2:
                    bundle.putParcelableArrayList(Constant.ARTIST_TO_FRAGMENT, (ArrayList<ArtistEntities>) mListArtist);
                    fragListArtist.setArguments(bundle);
                    return fragListArtist;
            }
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        switch (position) {
            case 0:
                title = context.getString(R.string.name_tab_song);
                break;
            case 1:
                title = context.getString(R.string.name_tab_album);
                break;
            case 2:
                title = context.getString(R.string.name_tab_artist);
                break;
        }
        return title;
    }
}
