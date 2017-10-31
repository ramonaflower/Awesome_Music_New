package com.example.ramona.music_player.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ramona.music_player.Entities.ArtistEntities;
import com.example.ramona.music_player.Interface.ClickListener;
import com.example.ramona.music_player.R;

import java.util.List;

/**
 * Created by Ramona on 9/29/2017.
 */

public class AdapterRecyclerViewListArtist extends RecyclerView.Adapter<AdapterRecyclerViewListArtist.MyViewHolder> {
    private List<ArtistEntities> mListArtist;
    private Context mContext;
    private ArtistEntities entities;
    private ClickListener mClickListener;

    public AdapterRecyclerViewListArtist(List<ArtistEntities> mListArtist, Context mContext) {
        this.mListArtist = mListArtist;
        this.mContext = mContext;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.lt_item_album_artist, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        entities = mListArtist.get(position);
        int numberOfSong = entities.getmSongQuantity();
        int numberOfAlbum = entities.getmAlbumQuantity();
        holder.mTextArtistName.setText(entities.getmArtistName());
        holder.mTextDetail.setText(mContext.getString(R.string.number_of_album_artist, numberOfAlbum, numberOfSong));
    }

    @Override
    public int getItemCount() {
        return mListArtist.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextArtistName, mTextDetail;
        public ImageView mImgArtist;

        public MyViewHolder(View itemView) {
            super(itemView);
            mTextArtistName = itemView.findViewById(R.id.text_album_name_or_artist_name);
            mTextDetail = itemView.findViewById(R.id.text_artist_name_or_number_of_song_album);
            mImgArtist = itemView.findViewById(R.id.img_album_artist);
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
}
