package com.example.ramona.music_player.Service;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.example.ramona.music_player.Activity.PlaySong;
import com.example.ramona.music_player.Constant;
import com.example.ramona.music_player.Entities.SongEntities;
import com.example.ramona.music_player.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * Created by Ramona on 10/4/2017.
 */

public class ServicePlayMusic extends Service {
    private MediaPlayer mMediaPlayer;
    private List<SongEntities> mListSong = new ArrayList<>();
    private IBinder mIBinder = new MyBinder();
    private int mIndex;
    private boolean mIsShuffle = false;
    private boolean mIsRepeat = false;
    private boolean mIsRelease = true;
    private Random mRan = new Random();
    private Intent mIntent;
    private Notification mNotification;
    private NotificationManager mNotificationManager;
    private RemoteViews mRemoteViews;
    private NotificationCompat.Builder mNotificationCompat;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Constant.NOTIFY_PLAY_PAUSE:
                    if (isPlayingMusic()) {
                        pause();
                        updatePlayPauseBtnNotification();
                    } else {
                        play();
                        updatePlayPauseBtnNotification();
                    }
                    break;
                case Constant.NOTIFY_NEXT:
                    playNext();
                    updatePlayPauseBtnNotification();
                    break;
                case Constant.NOTIFY_PREVIOUS:
                    playPrevious();
                    updatePlayPauseBtnNotification();
                    break;
            }
            switch (intent.getAction()){
                case Constant.ACTION_PAUSE_MUSIC:
                    updatePlayPauseBtnNotification();
                    break;
                case Constant.ACTION_PLAY_MUSIC:
                    updatePlayPauseBtnNotification();
                    break;
            }
        }
    };

    private static final int NOTIFICATION_ID_CUSTOM_BIG = 9;

    @Override
    public void onCreate() {
        super.onCreate();
        mMediaPlayer = new MediaPlayer();
        initMediaPlayer();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.NOTIFY_PREVIOUS);
        filter.addAction(Constant.NOTIFY_PLAY_PAUSE);
        filter.addAction(Constant.NOTIFY_NEXT);
        registerReceiver(mReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMediaPlayer.stop();
        mMediaPlayer.release();
        unregisterReceiver(mReceiver);
        mIsRelease = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mIBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);

    }

    public void initMediaPlayer() {
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.reset();
                if (mIsRepeat) {
                    playSong();

                } else {
                    playNext();
                }
            }
        });
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {

            }
        });
        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                mediaPlayer.reset();
                return false;
            }
        });
    }

    public class MyBinder extends Binder {
        public ServicePlayMusic getService() {
            return ServicePlayMusic.this;
        }
    }

    public void playSong() {
        mMediaPlayer.reset();
        String path = mListSong.get(mIndex).getmSongPath();
        try {
            mMediaPlayer.setDataSource(this, Uri.parse(path));
            mMediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        play();
        mIsRelease = false;

    }

    public void playNext() {
        if (!mIsShuffle) {
            mIndex++;
            if (mIndex >= mListSong.size()) {
                mIndex = 0;
            }
        } else {
            int mTemp = mIndex;
            while (mTemp == mIndex) {
                mTemp = mRan.nextInt(mListSong.size());
            }
            mIndex = mTemp;
        }
        playSong();
    }

    public void playPrevious() {
        mIndex--;
        if (mIndex < 0) {
            mIndex = mListSong.size() - 1;
        }
        playSong();
    }

    public void changeShuffle() {
        if (mIsShuffle) {
            mIsShuffle = false;
        } else {
            mIsShuffle = true;
        }
    }


    public void changeRepeat() {
        if (mIsRepeat) {
            mIsRepeat = false;
        } else {
            mIsRepeat = true;
        }
    }

    public void sendBroadCastInfoSong() {
        mIntent = new Intent();
        mIntent.putExtra(Constant.IS_PLAYING, isPlayingMusic());
        mIntent.putExtra(Constant.UPDATE_SONG_INFO, mListSong.get(mIndex));
        mIntent.putExtra(Constant.UPDATE_INDEX, mIndex);
        mIntent.setAction(Constant.BROADCAST_UPDATE_UI);
        sendBroadcast(mIntent);
    }

    public void notification() {
        if (mRemoteViews == null) {
            mRemoteViews = new RemoteViews(getApplicationContext().getPackageName(), R.layout.lt_small_notifycation);
            mRemoteViews.setTextViewText(R.id.small_song_name, getSongName());
            mRemoteViews.setTextViewText(R.id.small_artist_name, getArtistName());
            mRemoteViews.setImageViewResource(R.id.small_icon, R.drawable.ic_music);

            mNotificationCompat = new NotificationCompat.Builder(this);
            mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

            Intent notiIntent = new Intent(this, PlaySong.class);
            notiIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notiIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            mNotificationCompat.setOngoing(true);
            mNotificationCompat.setContentIntent(pendingIntent);
            mNotificationCompat.setSmallIcon(R.drawable.ic_notify);
            mNotificationCompat.setCustomContentView(mRemoteViews);
            mNotificationCompat.setVisibility(Notification.VISIBILITY_PUBLIC);
            updatePlayPauseBtnNotification();
            setNotificationListener();
            mNotification = mNotificationCompat.build();
            startForeground(NOTIFICATION_ID_CUSTOM_BIG, mNotification);
        } else {
            mRemoteViews.setTextViewText(R.id.small_song_name, getSongName());
            mRemoteViews.setTextViewText(R.id.small_artist_name, getArtistName());
            mNotificationManager.notify(NOTIFICATION_ID_CUSTOM_BIG, mNotification);
        }

    }

    public void updatePlayPauseBtnNotification() {
        if (isPlayingMusic()) {
            mRemoteViews.setImageViewResource(R.id.notify_play_pause, R.drawable.ic_pause_white);
        } else {
            mRemoteViews.setImageViewResource(R.id.notify_play_pause, R.drawable.ic_play_arrow_white);
        }
        mNotification = mNotificationCompat.build();
        mNotificationManager.notify(NOTIFICATION_ID_CUSTOM_BIG, mNotification);
    }

    private void setNotificationListener() {
        Intent previous = new Intent(Constant.NOTIFY_PREVIOUS);
        Intent next = new Intent(Constant.NOTIFY_NEXT);
        Intent play = new Intent(Constant.NOTIFY_PLAY_PAUSE);

        PendingIntent pPrevious = PendingIntent.getBroadcast(getApplicationContext(), 0, previous, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.notify_previous, pPrevious);

        PendingIntent pPlay = PendingIntent.getBroadcast(getApplicationContext(), 0, play, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.notify_play_pause, pPlay);

        PendingIntent pNext = PendingIntent.getBroadcast(getApplicationContext(), 0, next, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.notify_next, pNext);
    }

    public void getListSong(List<SongEntities> list) {
        mListSong = list;
    }

    public void getIndex(int index) {
        mIndex = index;
    }

    public void play() {
        mMediaPlayer.start();
        mIntent = new Intent();
        mIntent.setAction(Constant.ACTION_PLAY_MUSIC);
        sendBroadcast(mIntent);
        sendBroadCastInfoSong();
        notification();
    }

    public void pause() {
        mMediaPlayer.pause();

        mIntent = new Intent();
        mIntent.setAction(Constant.ACTION_PAUSE_MUSIC);
        sendBroadcast(mIntent);
    }

    public void stopForeGround() {
        if (!isPlayingMusic()) {
            stopForeground(true);
        }
    }

    public void seekTo(int pos) {
        mMediaPlayer.seekTo(pos);
    }

    public boolean isPlayingMusic() {
        return mMediaPlayer.isPlaying();
    }

    public boolean isRelease() {
        return mIsRelease;
    }

    public boolean isShuffle() {
        return mIsShuffle;
    }

    public boolean isRepeat() {
        return mIsRepeat;
    }

    public int returnIndex() {
        return mIndex;
    }

    public int getDuration() {
        return mMediaPlayer.getDuration();
    }

    public int getCurrentPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    public String getSongName() {
        return mListSong.get(mIndex).getmSongName();
    }

    public String getArtistName() {
        return mListSong.get(mIndex).getmArtistName();
    }

    public List<SongEntities> returnListSong() {
        return mListSong;
    }


}
