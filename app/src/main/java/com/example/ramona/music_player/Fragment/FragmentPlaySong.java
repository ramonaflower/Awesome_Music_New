package com.example.ramona.music_player.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ramona.music_player.Activity.PlaySong;
import com.example.ramona.music_player.Constant;
import com.example.ramona.music_player.Entities.SongEntities;
import com.example.ramona.music_player.Interface.PlayControl;
import com.example.ramona.music_player.R;

/**
 * Created by Ramona on 10/11/2017.
 */

public class FragmentPlaySong extends Fragment {
    private ImageView mBtn_play_pause, mBtn_next, mBtn_previous, mSpinningIMG;
    private TextView mText_title, mText_author;
    private LinearLayout mLinearLayout;
    private View mView;
    private boolean mIsPlaying;
    private RotateAnimation mRotateAnimation;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constant.BROADCAST_UPDATE_UI)) {
                Bundle bundle = intent.getExtras();
                if (bundle!=null){
                    SongEntities entities = bundle.getParcelable(Constant.UPDATE_SONG_INFO);
                    mText_title.setText(entities.getmSongName());
                    mText_author.setText(entities.getmArtistName());
                    mIsPlaying = bundle.getBoolean(Constant.IS_PLAYING);
                    updatePlayPauseBtn();
                }
            }
            switch (intent.getAction()){
                case Constant.ACTION_PAUSE_MUSIC:
            }
        }
    };
    private PlayControl mControl;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mControl = (PlayControl) context;
        } catch (Exception e) {
            throw new ClassCastException(context.toString() + " must implement onSomeEventListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.lt_fragment_play_controller, container, false);
        mRotateAnimation = new RotateAnimation(0, 360f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        mRotateAnimation.setInterpolator(new LinearInterpolator());
        mRotateAnimation.setDuration(2000);
        mRotateAnimation.setRepeatCount(Animation.INFINITE);
        initControl();
        if (getArguments() != null) {
            mText_title.setText(getArguments().getString(Constant.TITLE_SONG_TO_FRAG_PLAYCONTROL));
            mText_author.setText(getArguments().getString(Constant.ARTIST_NAME_TO_FRAG_PLAYCONTROL));
            mIsPlaying = getArguments().getBoolean(Constant.IS_PLAY_PAUSE_TO_FRAG_PLAYCONTROL);
            updatePlayPauseBtn();
        }
        initEvent();
        return mView;
    }

    private void initControl() {
        mBtn_play_pause = mView.findViewById(R.id.btn_play_pause_control);
        mBtn_next = mView.findViewById(R.id.btn_next_control);
        mBtn_previous = mView.findViewById(R.id.btn_previous_control);
        mLinearLayout = mView.findViewById(R.id.play_control);
        mText_title = mView.findViewById(R.id.text_title);
        mText_author = mView.findViewById(R.id.text_author);
        mSpinningIMG = mView.findViewById(R.id.spinning_album);
    }

    private void initEvent() {
        mBtn_play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mControl.playPause();
                updatePlayPauseBtn();
            }
        });
        mBtn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mControl.playNext();
            }
        });
        mBtn_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mControl.playPrevious();
            }
        });
        mLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PlaySong.class);
                startActivity(intent);
            }
        });
    }

    public void updatePlayPauseBtn() {
        if (mIsPlaying) {
            mBtn_play_pause.setImageResource(R.drawable.ic_pause_white);
            mSpinningIMG.startAnimation(mRotateAnimation);
            mIsPlaying = false;
        } else {
            mBtn_play_pause.setImageResource(R.drawable.ic_play_arrow_white);
            mSpinningIMG.clearAnimation();
            mIsPlaying = true;
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.BROADCAST_UPDATE_UI);
        filter.addAction(Constant.ACTION_PAUSE_MUSIC);
        filter.addAction(Constant.ACTION_PLAY_MUSIC);
        getActivity().registerReceiver(mReceiver, filter);
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(mReceiver);
    }
}
