package com.example.ramona.music_player.Activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.ramona.music_player.Adapter.PagerAdapter;
import com.example.ramona.music_player.Constant;
import com.example.ramona.music_player.Entities.AlbumEntities;
import com.example.ramona.music_player.Entities.ArtistEntities;
import com.example.ramona.music_player.Entities.SongEntities;
import com.example.ramona.music_player.Fragment.FragmentPlaySong;
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


public class MainActivity extends AppCompatActivity implements PlayControl {
    private Handler mHandler;
    private Runnable mRunable;
    private Fragment mFragPlayControl;
    private FragmentTransaction mFT;

    private SearchView mSearchView;
    private FrameLayout mFrameLayout;
    private List<SongEntities> mListSongInDevice = new ArrayList<SongEntities>();
    private List<SongEntities> mListSongInDB = new ArrayList<SongEntities>();
    private List<AlbumEntities> mListAlbum = new ArrayList<AlbumEntities>();
    private List<ArtistEntities> mListArtist = new ArrayList<ArtistEntities>();
    private SongEntities mSongEntities;
    private AlbumEntities mAlbumEntities;
    private ArtistEntities mArtistEntities;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private PagerAdapter mPagerAdapter;
    private MyDatabase db;
    private boolean mIsBound = false;
    private ServicePlayMusic mService;
    private Intent mIntent;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MyBinder myBinder = (MyBinder) iBinder;
            mService = myBinder.getService();
            mIsBound = true;
            Log.e("Connected", mIsBound + "");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mIsBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new MyDatabase(MainActivity.this);
        initControl();
        FragmentManager manager = getSupportFragmentManager();
        mPagerAdapter = new PagerAdapter(manager, mListSongInDB, mListAlbum, mListArtist, MainActivity.this);
        mViewPager.setAdapter(mPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        CheckPermission();
        db.close();
    }

    private void initControl() {
        mViewPager = findViewById(R.id.view_pager);
        mTabLayout = findViewById(R.id.tab_layout);
        mFrameLayout = findViewById(R.id.replace_layout);
    }

    public void CheckPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            getListSongsFromDevice();
            SynDataToSQLite();
            GetListSongFromDB();
            GetListAlbum();
            GetListArtist();
            mPagerAdapter.notifyDataSetChanged();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Constant.PERMISSION_ACCESS_STORAGE);
        }
    }

    public void GetListSongFromDB() {
        mListSongInDB.clear();
        mListSongInDB.addAll(db.getAllSong());
        if (mListSongInDB.size() > 0) {
            Collections.sort(mListSongInDB, new Comparator<SongEntities>() {
                @Override
                public int compare(final SongEntities object1, final SongEntities object2) {
                    return object1.getmSongName().compareTo(object2.getmSongName());
                }
            });
        }
    }

    public void SynDataToSQLite() {
        mListSongInDB.addAll(db.getAllSong());

        List<Integer> ListIDFromDevice = new ArrayList<>();
        List<Integer> ListIDFromDB = new ArrayList<>();

        // List ID Song từ device
        for (int i = 0; i < mListSongInDevice.size(); i++) {
            ListIDFromDevice.add(mListSongInDevice.get(i).getmSongID());
        }
        // List ID Song từ db App
        for (int i = 0; i < mListSongInDB.size(); i++) {
            ListIDFromDB.add(mListSongInDB.get(i).getmSongID());
        }

        // For xóa Song nếu Song đó không tồn tại trong device
        for (int i = 0; i < mListSongInDB.size(); i++) {
            if (!ListIDFromDevice.contains(mListSongInDB.get(i).getmSongID())) {
                db.DeleteSong(mListSongInDB.get(i).getmSongID() + "");
            }
        }

        // For thêm Song nếu Song đó chưa có trong DB App
        for (int i = 0; i < mListSongInDevice.size(); i++) {
            if (!ListIDFromDB.contains(mListSongInDevice.get(i).getmSongID())) {
                db.AddSong(mListSongInDevice.get(i));
            }
        }
    }

    public void getListSongsFromDevice() {
        Uri allSong = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + " " + getString(R.string.selection);
        Cursor cursor = getContentResolver().query(allSong, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    mSongEntities = new SongEntities();
                    mSongEntities.setmSongID(Integer.parseInt(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID))));
                    mSongEntities.setmSongName(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
                    mSongEntities.setmAlbumName(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)));
                    mSongEntities.setmArtistName(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
                    mSongEntities.setmDuration(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));
                    mSongEntities.setmSongPath(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
                    mSongEntities.setmAlbumID(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)));
                    mSongEntities.setmArtistID(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID)));
                    mListSongInDevice.add(mSongEntities);
                } while (cursor.moveToNext());
                cursor.close();
            }
        }
    }

    public void GetListAlbum() {
        List<Integer> ListIDAlbum = new ArrayList<>();
        for (int j = 0; j < mListSongInDB.size(); j++) {
            int IDAlbum = mListSongInDB.get(j).getmAlbumID();
            if (!ListIDAlbum.contains(IDAlbum)) {
                mAlbumEntities = new AlbumEntities();
                mAlbumEntities.setmAlbumID(mListSongInDB.get(j).getmAlbumID());
                mAlbumEntities.setmAlbumName(mListSongInDB.get(j).getmAlbumName());
                mAlbumEntities.setmArtistName(mListSongInDB.get(j).getmArtistName());
                mAlbumEntities.setmAlbumArt(mListSongInDB.get(j).getmSongPath());
                mListAlbum.add(mAlbumEntities);
                ListIDAlbum.add(IDAlbum);
            }
        }
        if (mListAlbum.size() > 0) {
            Collections.sort(mListAlbum, new Comparator<AlbumEntities>() {
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
        for (int i = 0; i < mListSongInDB.size(); i++) {
            int IDArtist = mListSongInDB.get(i).getmArtistID();
            if (!ListIDArtist.contains(IDArtist)) {
                listIDAlbum.clear();
                int songCount = 0;
                for (int j = 0; j < mListSongInDB.size(); j++) {
                    if (IDArtist == mListSongInDB.get(j).getmArtistID()) {
                        songCount++;
                        listIDAlbum.add(mListSongInDB.get(j).getmAlbumID());
                    }
                }
                mArtistEntities = new ArtistEntities();
                mArtistEntities.setmArtistID(mListSongInDB.get(i).getmArtistID());
                mArtistEntities.setmArtistName(mListSongInDB.get(i).getmArtistName());
                mArtistEntities.setmSongQuantity(songCount);
                mArtistEntities.setmAlbumQuantity(listIDAlbum.size());
                mListArtist.add(mArtistEntities);
                ListIDArtist.add(IDArtist);
            }
        }
        if (mListArtist.size() > 0) {
            Collections.sort(mListArtist, new Comparator<ArtistEntities>() {
                @Override
                public int compare(final ArtistEntities object1, final ArtistEntities object2) {
                    return object1.getmArtistName().compareTo(object2.getmArtistName());
                }
            });
        }
    }

    public void createFragmentIfNeed() {
        if (mService != null && !mService.isRelease()) {
            mFrameLayout.setVisibility(LinearLayout.VISIBLE);
            mFragPlayControl = new FragmentPlaySong();
            Bundle bundle = new Bundle();
            bundle.putString(Constant.TITLE_SONG_TO_FRAG_PLAYCONTROL, mService.getSongName());
            bundle.putString(Constant.ARTIST_NAME_TO_FRAG_PLAYCONTROL, mService.getArtistName());
            bundle.putBoolean(Constant.IS_PLAY_PAUSE_TO_FRAG_PLAYCONTROL, mService.isPlayingMusic());
            mFragPlayControl.setArguments(bundle);
            mFT = getSupportFragmentManager().beginTransaction();
            mFT.replace(R.id.replace_layout, mFragPlayControl);
            mFT.commit();
        } else {
            mFrameLayout.setVisibility(LinearLayout.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_search:
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constant.PERMISSION_ACCESS_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getListSongsFromDevice();
                    SynDataToSQLite();
                    GetListSongFromDB();
                    GetListAlbum();
                    GetListArtist();
                    mPagerAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mIntent = new Intent(MainActivity.this, ServicePlayMusic.class);
        startService(mIntent);
        bindService(mIntent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler = new Handler();
        mRunable = new Runnable() {
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

//    @Override
//    protected void onStop() {
//        super.onStop();
//        if (mIsBound) {
//            mService.stopForeGround();
//            unbindService(mConnection);
//            mIsBound = false;
//        }
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mIsBound) {
            mService.stopForeGround();
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    @Override
    public void playPause() {
        if (mService.isPlayingMusic()) {
            mService.pause();
        } else {
            mService.play();
        }
    }

    @Override
    public void playNext() {
        mService.playNext();
    }

    @Override
    public void playPrevious() {
        mService.playPrevious();
    }
}
