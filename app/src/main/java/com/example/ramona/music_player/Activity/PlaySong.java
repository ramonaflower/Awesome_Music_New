package com.example.ramona.music_player.Activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.ramona.music_player.Adapter.PlaySongPagerAdapter;
import com.example.ramona.music_player.Constant;
import com.example.ramona.music_player.Entities.SongEntities;
import com.example.ramona.music_player.Interface.ClickFromTransparentToPlaySong;
import com.example.ramona.music_player.R;
import com.example.ramona.music_player.Service.ServicePlayMusic;
import com.example.ramona.music_player.Service.ServicePlayMusic.MyBinder;
import com.pixelcan.inkpageindicator.InkPageIndicator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * Created by Ramona on 10/2/2017.
 */

public class PlaySong extends AppCompatActivity implements ClickFromTransparentToPlaySong {
    private Toolbar mToolbar;
    private ImageView mBtn_play_pause, mBtn_next, mBtn_previous, mBtn_shuffle, mBtn_repeat;
    private TextView mText_start_time, mText_end_time;
    private ViewPager mViewPager;
    private InkPageIndicator mIndicator;
    private SeekBar mSeekBar;
    private PlaySongPagerAdapter mAdapter;
    private List<SongEntities> mListSong = new ArrayList<>();
    private Runnable mUpdateSeekBar;
    private Handler mHandler;
    private ServicePlayMusic mServicePlayMusic;
    private int mProgress;
    private int mIndex = 0;
    private double mTimeElapsed;
    private double mDuration;
    private boolean mIsBound = false;
    private boolean mCheck;
    private Bundle mBundle;
    private Intent mPlayIntent;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constant.BROADCAST_UPDATE_UI)) {
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    mToolbar.setTitle(bundle.getString(Constant.UPDATE_TITLE_SONG));
                    mToolbar.setSubtitle(bundle.getString(Constant.UPDATE_ARTIST_NAME));
                }
            }
        }
    };
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MyBinder myBinder = (MyBinder) iBinder;
            mServicePlayMusic = myBinder.getService();
            if (mBundle != null) {
                mServicePlayMusic.getListSong(mListSong);
                mServicePlayMusic.getIndex(mIndex);
                mServicePlayMusic.playSong();
            } else {
                mListSong.clear();
                mListSong.addAll(mServicePlayMusic.returnListSong());
                mIndex = mServicePlayMusic.returnIndex();
                mAdapter.setIndex(mIndex);

            }
            mCheck = mServicePlayMusic.isPlayingMusic();
            mAdapter.updateVariableCheck(mCheck);
            mAdapter.notifyDataSetChanged();
            updateSeekBar();
            upDateToolBar();
            upDatePlayPauseBtn();
            updateShuffleSetting();
            updateRepeatSetting();
            mIsBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mIsBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lt_play_song);
        initControl();
        initData();
        FragmentManager fragmentManager = getSupportFragmentManager();
        mAdapter = new PlaySongPagerAdapter(fragmentManager, mListSong, mIndex, mCheck);
        mViewPager.setAdapter(mAdapter);
        mIndicator.setViewPager(mViewPager);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        initEvent();
    }

    private void initControl() {
        mToolbar = findViewById(R.id.toolbar_play_song);
        mBtn_play_pause = findViewById(R.id.btn_play_pause);
        mBtn_next = findViewById(R.id.btn_next_song);
        mBtn_previous = findViewById(R.id.btn_previous_song);
        mBtn_repeat = findViewById(R.id.btn_repeat);
        mBtn_shuffle = findViewById(R.id.btn_shuffle);
        mText_start_time = findViewById(R.id.text_start_time_of_song);
        mText_end_time = findViewById(R.id.text_end_time_of_song);
        mSeekBar = findViewById(R.id.seekbar_play_song);
        mViewPager = findViewById(R.id.view_pager_play_song);
        mIndicator = findViewById(R.id.indicator);
    }

    private void initData() {
        Intent intent = getIntent();
        mBundle = intent.getExtras();
        if (mBundle != null) {
            List<SongEntities> temp = mBundle.getParcelableArrayList(Constant.LIST_SONG_TO_PLAY_SONG);
            mListSong.clear();
            mListSong.addAll(temp);
            mIndex = mBundle.getInt(Constant.SONG_INDEX_TO_PLAY_SONG);
        }
    }

    private void initEvent() {
        mBtn_play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mServicePlayMusic.isPlayingMusic()) {
                    mServicePlayMusic.pause();
                    upDatePlayPauseBtn();
                } else {
                    mServicePlayMusic.play();
                    upDatePlayPauseBtn();
                    updateSeekBar();
                }
            }
        });
        mBtn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mServicePlayMusic.playNext();
                upDatePlayPauseBtn();
            }
        });
        mBtn_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mServicePlayMusic.playPrevious();
                upDatePlayPauseBtn();
            }
        });
        mBtn_shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mServicePlayMusic.changeShuffle();
                updateShuffleSetting();
            }
        });
        mBtn_repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mServicePlayMusic.changeRepeat();
                updateRepeatSetting();
            }
        });
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mProgress = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mServicePlayMusic.seekTo(mProgress);
            }
        });
    }

    public void updateSeekBar() {
        mTimeElapsed = mServicePlayMusic.getCurrentPosition();
        mDuration = mServicePlayMusic.getDuration();
        updateDurationText();
        mSeekBar.setMax((int) mDuration);
        mSeekBar.setProgress((int) mTimeElapsed);
        mHandler = new Handler();
        mUpdateSeekBar = new Runnable() {
            @Override
            public void run() {
                if (mServicePlayMusic.isPlayingMusic()) {
                    mTimeElapsed = mServicePlayMusic.getCurrentPosition();
                    mDuration = mServicePlayMusic.getDuration();
                    mSeekBar.setMax((int) mDuration);
                    mSeekBar.setProgress((int) mTimeElapsed);
                    updateDurationText();
                    mHandler.postDelayed(this, 100);
                }
            }
        };
        mHandler.removeCallbacks(mUpdateSeekBar);
        mHandler.post(mUpdateSeekBar);
    }

    public void updateDurationText() {
        mText_end_time.setText(String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes((long) mDuration),
                TimeUnit.MILLISECONDS.toSeconds((long) mDuration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) mDuration))));
        mText_start_time.setText(String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes((long) mTimeElapsed),
                TimeUnit.MILLISECONDS.toSeconds((long) mTimeElapsed) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) mTimeElapsed))));
    }

    public void upDateToolBar() {
        mToolbar.setTitle(mServicePlayMusic.getSongName());
        mToolbar.setSubtitle(mServicePlayMusic.getArtistName());
    }

    public void upDatePlayPauseBtn() {
        if (mServicePlayMusic.isPlayingMusic()) {
            mBtn_play_pause.setImageResource(R.drawable.ic_pause_white);
        } else {
            mBtn_play_pause.setImageResource(R.drawable.ic_play_arrow_white);
        }
    }

    public void updateShuffleSetting() {
        if (mServicePlayMusic.isShuffle()) {
            mBtn_shuffle.setImageResource(R.drawable.ic_shuffle_purple);
        } else {
            mBtn_shuffle.setImageResource(R.drawable.ic_shuffle_white);
        }
    }

    public void updateRepeatSetting() {
        if (mServicePlayMusic.isRepeat()) {
            mBtn_repeat.setImageResource(R.drawable.ic_repeat_purple);
        } else {
            mBtn_repeat.setImageResource(R.drawable.ic_repeat_white);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        mPlayIntent = new Intent(PlaySong.this, ServicePlayMusic.class);
        bindService(mPlayIntent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.BROADCAST_UPDATE_UI);
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
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
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void clickToPlaySong(int position) {
        mServicePlayMusic.getIndex(position);
        mServicePlayMusic.playSong();
        upDatePlayPauseBtn();
        updateSeekBar();
    }

    //    private boolean isMyServiceRunning(Class<?> serviceClass) {
//        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
//            if (serviceClass.getName().equals(service.service.getClassName())) {
//                return true;
//            }
//        }
//        return false;
//    }
}
