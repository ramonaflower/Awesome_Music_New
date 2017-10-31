package com.example.ramona.music_player.Activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.ramona.music_player.Adapter.AdapterRecyclerViewListSong;
import com.example.ramona.music_player.Constant;
import com.example.ramona.music_player.Entities.ArtistEntities;
import com.example.ramona.music_player.Entities.SongEntities;
import com.example.ramona.music_player.Fragment.FragmentPlaySong;
import com.example.ramona.music_player.Interface.ClickListener;
import com.example.ramona.music_player.Interface.PlayControl;
import com.example.ramona.music_player.R;
import com.example.ramona.music_player.SQLite.MyDatabase;
import com.example.ramona.music_player.Service.ServicePlayMusic;
import com.example.ramona.music_player.Service.ServicePlayMusic.MyBinder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Ramona on 10/17/2017.
 */

public class ArtistDetail extends AppCompatActivity implements PlayControl {
    private MyDatabase mDB;
    private List<SongEntities> mListSong = new ArrayList<>();
    private Toolbar mToolbar;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private ImageView mCoverAlbum;
    private AdapterRecyclerViewListSong mAdapter;
    private RecyclerView mRecyclerView;
    private FrameLayout mFrameLayout;
    private Fragment mFragPlayControl;
    private FragmentTransaction mFT;
    private boolean mIsBound;
    private ServicePlayMusic mServicePlayMusic;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MyBinder myBinder = (MyBinder) iBinder;
            mServicePlayMusic = myBinder.getService();
            mIsBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mIsBound = false;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lt_detail_album_artist);
        mDB = new MyDatabase(ArtistDetail.this);
        initControl();
        setSupportActionBar(mToolbar);
        if (mToolbar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        initData();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new AdapterRecyclerViewListSong(mListSong, this);
        mRecyclerView.setAdapter(mAdapter);
        initEvent();
    }

    private void initControl() {
        mToolbar = findViewById(R.id.toolbar_album_detail);
        mCollapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        mCoverAlbum = findViewById(R.id.img_cover_photo);
        mRecyclerView = findViewById(R.id.RV_list_song_by_id_album);
        mFrameLayout = findViewById(R.id.replace_layout_album_detail);
    }

    private void initData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            ArtistEntities entities = bundle.getParcelable(Constant.ARTIST_ENTITY);
            int id = entities.getmArtistID();
            mCollapsingToolbarLayout.setTitle(entities.getmArtistName());
            mListSong.addAll(mDB.getListSongByArtistID(id+""));
            if (mListSong.size() > 0) {
                Collections.sort(mListSong, new Comparator<SongEntities>() {
                    @Override
                    public int compare(final SongEntities object1, final SongEntities object2) {
                        return object1.getmSongName().compareTo(object2.getmSongName());
                    }
                });
            }
        }
        mDB.close();
    }

    private void initEvent() {
        mAdapter.SetOnItemClickListener(new ClickListener() {
            @Override
            public void OnItemClick(int position) {
                Intent intent = new Intent(ArtistDetail.this, PlaySong.class);
                intent.putExtra(Constant.SONG_INDEX_TO_PLAY_SONG, position);
                intent.putParcelableArrayListExtra(Constant.LIST_SONG_TO_PLAY_SONG, (ArrayList<SongEntities>) mListSong);
                startActivity(intent);
            }

            @Override
            public void OnLongItemClick(int position) {

            }
        });
    }

    public void createFragmentIfNeed() {
        if (mServicePlayMusic != null && !mServicePlayMusic.isRelease()) {
            mFrameLayout.setVisibility(LinearLayout.VISIBLE);
            mFragPlayControl = new FragmentPlaySong();
            Bundle bundle = new Bundle();
            bundle.putString(Constant.TITLE_SONG_TO_FRAG_PLAYCONTROL, mServicePlayMusic.getSongName());
            bundle.putString(Constant.ARTIST_NAME_TO_FRAG_PLAYCONTROL, mServicePlayMusic.getArtistName());
            bundle.putBoolean(Constant.IS_PLAY_PAUSE_TO_FRAG_PLAYCONTROL, mServicePlayMusic.isPlayingMusic());
            mFragPlayControl.setArguments(bundle);
            mFT = getSupportFragmentManager().beginTransaction();
            mFT.replace(R.id.replace_layout_album_detail, mFragPlayControl);
            mFT.commit();
        } else {
            mFrameLayout.setVisibility(LinearLayout.GONE);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent mIntent = new Intent(ArtistDetail.this, ServicePlayMusic.class);
        bindService(mIntent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        final Handler mHandler = new Handler();
        Runnable mRunable = new Runnable() {
            @Override
            public void run() {
                if (!mIsBound) {
                    mHandler.postDelayed(this, 100);
                } else {
                    createFragmentIfNeed();
                }
            }
        };
        mHandler.removeCallbacks(mRunable);
        mHandler.post(mRunable);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mIsBound) {
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    @Override
    public void playPause() {
        if (mServicePlayMusic.isPlayingMusic()) {
            mServicePlayMusic.pause();
        } else {
            mServicePlayMusic.play();
        }
    }

    @Override
    public void playNext() {
        mServicePlayMusic.playNext();
    }

    @Override
    public void playPrevious() {
        mServicePlayMusic.playPrevious();
    }
}
