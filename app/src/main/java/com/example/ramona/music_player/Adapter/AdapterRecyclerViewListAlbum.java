package com.example.ramona.music_player.Adapter;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ramona.music_player.Entities.AlbumEntities;
import com.example.ramona.music_player.Models;
import com.example.ramona.music_player.R;

import java.util.List;

/**
 * Created by Ramona on 9/27/2017.
 */

public class AdapterRecyclerViewListAlbum extends RecyclerView.Adapter<AdapterRecyclerViewListAlbum.MyViewHolder> {
    private List<AlbumEntities> mList;
    private Context mContext;
    private AlbumEntities entities = new AlbumEntities();

    public AdapterRecyclerViewListAlbum(List<AlbumEntities> mList, Context mContext) {
        this.mList = mList;
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
        entities = mList.get(position);
        holder.mIV_album_art.setImageBitmap(null);
        holder.mText_album_name.setText(entities.getmAlbumName());
        holder.mText_artist_name.setText(entities.getmArtistName());
        Glide.with(mContext).asBitmap().load(Models.getCoverAlbum(entities.getmAlbumArt())).into(holder.mIV_album_art);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView mText_album_name, mText_artist_name;
        public ImageView mIV_album_art;

        public MyViewHolder(View itemView) {
            super(itemView);
            mText_album_name = (TextView) itemView.findViewById(R.id.text_album_name_or_artist_name);
            mText_artist_name = (TextView) itemView.findViewById(R.id.text_artist_name_or_number_of_song_album);
            mIV_album_art = (ImageView) itemView.findViewById(R.id.img_album_artist);
        }
    }


}
