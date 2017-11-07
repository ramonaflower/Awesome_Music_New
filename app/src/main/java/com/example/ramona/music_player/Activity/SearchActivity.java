package com.example.ramona.music_player.Activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;

import com.example.ramona.music_player.Adapter.AdapterRecyclerViewListAlbum;
import com.example.ramona.music_player.Adapter.AdapterRecyclerViewListArtist;
import com.example.ramona.music_player.Adapter.AdapterRecyclerViewListSong;
import com.example.ramona.music_player.Constant;
import com.example.ramona.music_player.Entities.AlbumEntities;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Ramona on 10/31/2017.
 */

public class SearchActivity extends AppCompatActivity implements PlayControl{
    private SearchView mSearchView;
    private ImageView mCloseButtonImage;
    private EditText mSearchEditText;
    private Toolbar mToolbar;
    private FrameLayout mFrameLayout;

    private List<SongEntities> mListSongFromDB = new ArrayList<>();
    private List<AlbumEntities> mListAlbumFromDB = new ArrayList<>();
    private List<ArtistEntities> mListArtistFromDB = new ArrayList<>();
    private List<SongEntities> mListSongAfterSearch = new ArrayList<>();
    private List<AlbumEntities> mListAlbumAfterSearch = new ArrayList<>();
    private List<ArtistEntities> mListArtistAfterSearch = new ArrayList<>();
    private RecyclerView mRVListSong, mRVListAlbum, mRVListArtist;
    private AdapterRecyclerViewListSong mAdapterListSong;
    private AdapterRecyclerViewListAlbum mAdapterListAlbum;
    private AdapterRecyclerViewListArtist mAdapterListArtist;
    private MyDatabase mDB;
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

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lt_search_song_album_artist);
        mDB = new MyDatabase(this);
        initControl();
        initData();
        setupToolbar();
        setupSearchView();
        setupAdapterListSong();
        setupAdapterListAlbum();
        setupAdapterListArtist();
        initEvent();
    }

    private void initControl() {
        mSearchView = findViewById(R.id.search_view);
        mToolbar = findViewById(R.id.toolbar_search);
        mRVListSong = findViewById(R.id.rv_list_song);
        mRVListAlbum = findViewById(R.id.rv_list_album);
        mRVListArtist = findViewById(R.id.rv_list_artist);
        mFrameLayout = findViewById(R.id.replace_layout_search);
    }

    private void initData() {
        mListSongFromDB.addAll(mDB.getAllSong());
        if (mListSongFromDB.size() > 0) {
            Collections.sort(mListSongFromDB, new Comparator<SongEntities>() {
                @Override
                public int compare(SongEntities songEntities, SongEntities t1) {
                    return songEntities.getmSongName().compareTo(t1.getmSongName());
                }
            });
        }
        mDB.close();
        GetListAlbum();
        GetListArtist();

    }

    public void GetListAlbum() {
        List<Integer> ListIDAlbum = new ArrayList<>();
        for (int j = 0; j < mListSongFromDB.size(); j++) {
            int IDAlbum = mListSongFromDB.get(j).getmAlbumID();
            if (!ListIDAlbum.contains(IDAlbum)) {
                AlbumEntities mAlbumEntities = new AlbumEntities();
                mAlbumEntities.setmAlbumID(mListSongFromDB.get(j).getmAlbumID());
                mAlbumEntities.setmAlbumName(mListSongFromDB.get(j).getmAlbumName());
                mAlbumEntities.setmArtistName(mListSongFromDB.get(j).getmArtistName());
                mAlbumEntities.setmAlbumArt(mListSongFromDB.get(j).getmSongPath());
                mListAlbumFromDB.add(mAlbumEntities);
                ListIDAlbum.add(IDAlbum);
            }
        }
        if (mListAlbumFromDB.size() > 0) {
            Collections.sort(mListAlbumFromDB, new Comparator<AlbumEntities>() {
                @Override
                public int compare(final AlbumEntities object1, final AlbumEntities object2) {
                    return object1.getmAlbumName().compareTo(object2.getmAlbumName());
                }
            });
        }
    }

    public void GetListArtist() {
        List<Integer> ListIDArtist = new ArrayList<>();
        Set<Integer> listIDAlbum = new HashSet<>();
        for (int i = 0; i < mListSongFromDB.size(); i++) {
            int IDArtist = mListSongFromDB.get(i).getmArtistID();
            if (!ListIDArtist.contains(IDArtist)) {
                listIDAlbum.clear();
                int songCount = 0;
                for (int j = 0; j < mListSongFromDB.size(); j++) {
                    if (IDArtist == mListSongFromDB.get(j).getmArtistID()) {
                        songCount++;
                        listIDAlbum.add(mListSongFromDB.get(j).getmAlbumID());
                    }
                }
                ArtistEntities mArtistEntities = new ArtistEntities();
                mArtistEntities.setmArtistID(mListSongFromDB.get(i).getmArtistID());
                mArtistEntities.setmArtistName(mListSongFromDB.get(i).getmArtistName());
                mArtistEntities.setmSongQuantity(songCount);
                mArtistEntities.setmAlbumQuantity(listIDAlbum.size());
                mListArtistFromDB.add(mArtistEntities);
                ListIDArtist.add(IDArtist);
            }
        }
        if (mListArtistFromDB.size() > 0) {
            Collections.sort(mListArtistFromDB, new Comparator<ArtistEntities>() {
                @Override
                public int compare(final ArtistEntities object1, final ArtistEntities object2) {
                    return object1.getmArtistName().compareTo(object2.getmArtistName());
                }
            });
        }
    }

    private void initEvent() {
        //mSearchView.setOnCloseListener();
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                filterAdapterResult(s);
                return true;
            }
        });
        mAdapterListSong.SetOnItemClickListener(new ClickListener() {
            @Override
            public void OnItemClick(int position) {
                Intent intent = new Intent(SearchActivity.this, PlaySong.class);
                intent.putExtra(Constant.SONG_INDEX_TO_PLAY_SONG, position);
                intent.putParcelableArrayListExtra(Constant.LIST_SONG_TO_PLAY_SONG, (ArrayList<SongEntities>) mListSongAfterSearch);
                startActivity(intent);
                finish();
            }

            @Override
            public void OnLongItemClick(int position) {

            }
        });
        mAdapterListAlbum.SetOnItemClickListener(new ClickListener() {
            @Override
            public void OnItemClick(int position) {
                Intent intent = new Intent(SearchActivity.this, AlbumDetail.class);
                intent.putExtra(Constant.ALBUM_ENTITY, mListAlbumAfterSearch.get(position));
                startActivity(intent);
                finish();
            }

            @Override
            public void OnLongItemClick(int position) {

            }
        });
        mAdapterListArtist.SetOnItemClickListener(new ClickListener() {
            @Override
            public void OnItemClick(int position) {
                Intent intent = new Intent(SearchActivity.this, ArtistDetail.class);
                intent.putExtra(Constant.ARTIST_ENTITY, mListArtistAfterSearch.get(position));
                startActivity(intent);
                finish();
            }

            @Override
            public void OnLongItemClick(int position) {

            }
        });
        mCloseButtonImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSearchEditText.getText().clear();
                mListSongAfterSearch.clear();
                mAdapterListSong.notifyDataSetChanged();
            }
        });
    }

    public void setupToolbar() {
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            mToolbar.setContentInsetStartWithNavigation(0);
        }
    }

    public void filterAdapterResult(String s) {
        mListSongAfterSearch.clear();
        mListAlbumAfterSearch.clear();
        mListArtistAfterSearch.clear();
        if (s != null && !s.equals("")) {
            // Filter List Song
            for (int i = 0; i < mListSongFromDB.size(); i++) {
                String name = mListSongFromDB.get(i).getmSongName();
                if (name.toLowerCase().contains(s.toLowerCase())) {
                    mListSongAfterSearch.add(mListSongFromDB.get(i));
                }
            }
            // Filter List Album
            for (int i = 0; i < mListAlbumFromDB.size(); i++) {
                String name = mListAlbumFromDB.get(i).getmAlbumName();
                if (name.toLowerCase().contains(s.toLowerCase())) {
                    mListAlbumAfterSearch.add(mListAlbumFromDB.get(i));
                }
            }
            // Filter List Artist
            for (int i = 0; i < mListArtistFromDB.size(); i++) {
                String name = mListArtistFromDB.get(i).getmArtistName();
                if (name.toLowerCase().contains(s.toLowerCase())) {
                    mListArtistAfterSearch.add(mListArtistFromDB.get(i));
                }
            }
        }
        mAdapterListSong.notifyDataSetChanged();
        mAdapterListAlbum.notifyDataSetChanged();
        mAdapterListArtist.notifyDataSetChanged();
    }

    public void setupSearchView() {
        int searchPlateId = mSearchView.getContext().getResources().getIdentifier("android:id/search_plate", null, null);
        View searchPlate = findViewById(searchPlateId);
        searchPlate.setBackgroundResource(R.drawable.spr_bg_white_background);

        int searchSrcTextId = getResources().getIdentifier("android:id/search_src_text", null, null);
        mSearchEditText = mSearchView.findViewById(searchSrcTextId);
        mSearchEditText.setTextColor(Color.WHITE);
        mSearchEditText.setHintTextColor(Color.GRAY);

        int magId = mSearchView.getContext().getResources().getIdentifier("android:id/search_mag_icon", null, null);
        ImageView magImage = mSearchView.findViewById(magId);
        magImage.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
        magImage.setVisibility(View.GONE);

        int closeButtonId = mSearchView.getContext().getResources().getIdentifier("android:id/search_close_btn", null, null);
        mCloseButtonImage = mSearchView.findViewById(closeButtonId);
        mCloseButtonImage.setColorFilter(ContextCompat.getColor(SearchActivity.this, R.color.colorWhite), PorterDuff.Mode.SRC_IN);
        mSearchView.onActionViewExpanded();
    }

    public void setupAdapterListSong() {
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRVListSong.setLayoutManager(mLinearLayoutManager);
        mAdapterListSong = new AdapterRecyclerViewListSong(mListSongAfterSearch, this);
        mRVListSong.setAdapter(mAdapterListSong);
    }

    public void setupAdapterListAlbum() {
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRVListAlbum.setLayoutManager(mLinearLayoutManager);
        mAdapterListAlbum = new AdapterRecyclerViewListAlbum(mListAlbumAfterSearch, this);
        mRVListAlbum.setAdapter(mAdapterListAlbum);
    }

    public void setupAdapterListArtist() {
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRVListArtist.setLayoutManager(mLinearLayoutManager);
        mAdapterListArtist = new AdapterRecyclerViewListArtist(mListArtistAfterSearch, this);
        mRVListArtist.setAdapter(mAdapterListArtist);
    }

    public void createFragmentIfNeed() {
        if (mServicePlayMusic != null && !mServicePlayMusic.isRelease()) {
            mFrameLayout.setVisibility(LinearLayout.VISIBLE);
            Fragment mFragPlayControl = new FragmentPlaySong();
            Bundle bundle = new Bundle();
            bundle.putString(Constant.TITLE_SONG_TO_FRAG_PLAYCONTROL, mServicePlayMusic.getSongName());
            bundle.putString(Constant.ARTIST_NAME_TO_FRAG_PLAYCONTROL, mServicePlayMusic.getArtistName());
            bundle.putBoolean(Constant.IS_PLAY_PAUSE_TO_FRAG_PLAYCONTROL, mServicePlayMusic.isPlayingMusic());
            mFragPlayControl.setArguments(bundle);
            FragmentTransaction mFT = getSupportFragmentManager().beginTransaction();
            mFT.replace(R.id.replace_layout_search, mFragPlayControl);
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
        Intent mPlayIntent = new Intent(SearchActivity.this, ServicePlayMusic.class);
        bindService(mPlayIntent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        final Handler mHandler = new Handler();
        Runnable mRunnable = new Runnable() {
            @Override
            public void run() {
                if (!mIsBound) {
                    mHandler.postDelayed(this, 100);
                } else {
                    createFragmentIfNeed();
                }
            }
        };
        mHandler.removeCallbacks(mRunnable);
        mHandler.post(mRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
