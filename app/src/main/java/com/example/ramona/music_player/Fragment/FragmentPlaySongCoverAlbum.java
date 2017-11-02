package com.example.ramona.music_player.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.ramona.music_player.Constant;
import com.example.ramona.music_player.Entities.SongEntities;
import com.example.ramona.music_player.Models;
import com.example.ramona.music_player.R;
import com.example.ramona.music_player.SQLite.MyDatabase;

/**
 * Created by Ramona on 10/20/2017.
 */

public class FragmentPlaySongCoverAlbum extends Fragment {
    private RotateAnimation mRotateAnimation;
    private ImageView mImg_album_cover;
    private View mView;
    private String mID;
    private MyDatabase mDb;
    private byte[] mIMG;
    private SongEntities mSong;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constant.BROADCAST_UPDATE_UI)){
                Bundle bundle = intent.getExtras();
                SongEntities entities = bundle.getParcelable(Constant.UPDATE_SONG_INFO);
                mIMG  = Models.getCoverAlbum(entities.getmSongPath());
                if (mIMG!=null){
                    Glide.with(getActivity().getApplicationContext()).asBitmap().load(mIMG).into(mImg_album_cover);
                }
            }
            switch (intent.getAction()){
                case Constant.ACTION_PLAY_MUSIC:
                    mImg_album_cover.startAnimation(mRotateAnimation);
                    break;
                case Constant.ACTION_PAUSE_MUSIC:
                    mImg_album_cover.clearAnimation();
            }
        }
    };
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.lt_playsong_cover_album, container, false);
        mDb = new MyDatabase(getActivity());
        mRotateAnimation = new RotateAnimation(0, 360f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateAnimation.setInterpolator(new LinearInterpolator());
        mRotateAnimation.setDuration(15000);
        mRotateAnimation.setRepeatCount(Animation.INFINITE);
        initControl();
        if (getArguments()!=null) {
            mID = getArguments().getString(Constant.INDEX);
            mSong = mDb.getSongByID(mID);
            mIMG = Models.getCoverAlbum(mSong.getmSongPath());
            if (mIMG!=null){
                Glide.with(getActivity().getApplicationContext()).asBitmap().load(mIMG).into(mImg_album_cover);
            }
        }
        return mView;
    }

    private void initControl() {
        mImg_album_cover = mView.findViewById(R.id.img_cover_album);
        mImg_album_cover.startAnimation(mRotateAnimation);
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.BROADCAST_UPDATE_UI);
        filter.addAction(Constant.ACTION_PLAY_MUSIC);
        filter.addAction(Constant.ACTION_PAUSE_MUSIC);
        getActivity().registerReceiver(mReceiver, filter);
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(mReceiver);
    }
}
