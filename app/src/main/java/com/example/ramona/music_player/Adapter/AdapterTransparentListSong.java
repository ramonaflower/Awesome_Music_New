package com.example.ramona.music_player.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ramona.music_player.Entities.SongEntities;
import com.example.ramona.music_player.R;

import java.util.List;

import es.claucookie.miniequalizerlibrary.EqualizerView;

/**
 * Created by Ramona on 10/20/2017.
 */

public class AdapterTransparentListSong extends RecyclerView.Adapter<AdapterTransparentListSong.MyViewHolder> {
    private List<SongEntities> mList;
    private SongEntities mSong;
    private int mIndex;
    private boolean mCheck;
    private clickListener mListener;

    public AdapterTransparentListSong(List<SongEntities> mList, int mIndex, boolean mCheck, clickListener mListener) {
        this.mList = mList;
        this.mIndex = mIndex;
        this.mCheck = mCheck;
        this.mListener = mListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.lt_item_list_song_transparent, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        mSong = mList.get(position);
        if (mIndex == position) {
            holder.mTextSongIndex.setVisibility(LinearLayout.GONE);
            holder.mEqualizerView.setVisibility(LinearLayout.VISIBLE);
            holder.mTextSongName.setText(mSong.getmSongName());
            holder.mTextArtistName.setText(mSong.getmArtistName());
            if (mCheck) {
                holder.mEqualizerView.animateBars();
            } else {
                holder.mEqualizerView.stopBars();
            }
        } else {
            holder.mTextSongIndex.setVisibility(LinearLayout.VISIBLE);
            holder.mEqualizerView.setVisibility(LinearLayout.GONE);
            holder.mTextSongIndex.setText(position + 1 + "");
            holder.mTextSongName.setText(mSong.getmSongName());
            holder.mTextArtistName.setText(mSong.getmArtistName());
        }

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextSongIndex, mTextSongName, mTextArtistName;
        public ImageView mImageMore;
        public EqualizerView mEqualizerView;

        public MyViewHolder(View itemView) {
            super(itemView);
            mTextSongIndex = itemView.findViewById(R.id.text_song_index);
            mTextSongName = itemView.findViewById(R.id.text_name_of_song_transparent);
            mTextArtistName = itemView.findViewById(R.id.text_name_of_artist_transparent);
            mImageMore = itemView.findViewById(R.id.img_move);
            mEqualizerView = itemView.findViewById(R.id.equalizer_view);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onClick(getLayoutPosition());
                }
            });
        }
    }

    public void updateIndex(int index) {
        mIndex = index;
    }

    public void updateCheck(boolean check){
        mCheck = check;
    }

    public interface clickListener {
        void onClick(int position);
    }

}
