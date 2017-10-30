package com.example.ramona.music_player.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ramona.music_player.Entities.SongEntities;
import com.example.ramona.music_player.R;

import java.util.List;

/**
 * Created by Ramona on 9/26/2017.
 */

public class AdapterRecyclerViewListSong extends RecyclerView.Adapter<AdapterRecyclerViewListSong.MyViewHolder> {
    private List<SongEntities> mListSong;
    private Context mContext;
    private SongEntities entities;
    ClickListener mClickListener;

    public AdapterRecyclerViewListSong(List<SongEntities> mListSong, Context mContext) {
        this.mListSong = mListSong;
        this.mContext = mContext;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.lt_item_song, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        entities = mListSong.get(position);
        holder.mText_name_of_song.setText(entities.getmSongName());
        holder.mText_name_of_artist.setText(entities.getmArtistName());
    }

    @Override
    public int getItemCount() {
        return mListSong.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView mText_name_of_song, mText_name_of_artist;

        public MyViewHolder(View itemView) {
            super(itemView);
            mText_name_of_song = itemView.findViewById(R.id.text_name_of_song);
            mText_name_of_artist = itemView.findViewById(R.id.text_name_of_artist);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mClickListener.OnItemClick(getLayoutPosition());
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    mClickListener.OnLongItemClick(getLayoutPosition());
                    return false;
                }
            });
        }
    }

    public void SetOnItemClickListener(ClickListener clickListener) {
        mClickListener = clickListener;
    }

    public interface ClickListener {
        void OnItemClick(int position);

        void OnLongItemClick(int position);
    }
}
