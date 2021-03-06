package com.example.ramona.music_player.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ramona.music_player.Activity.ArtistDetail;
import com.example.ramona.music_player.Activity.PlaySong;
import com.example.ramona.music_player.Adapter.AdapterRecyclerViewListArtist;
import com.example.ramona.music_player.Constant;
import com.example.ramona.music_player.Entities.ArtistEntities;
import com.example.ramona.music_player.Interface.ClickListener;
import com.example.ramona.music_player.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ramona on 9/26/2017.
 */

public class FragmentListArtist extends Fragment {
    private RecyclerView mRecyclerView;
    private AdapterRecyclerViewListArtist mAdapter;
    private GridLayoutManager mLayoutManager;
    private List<ArtistEntities> mList = new ArrayList<ArtistEntities>();

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.lt_recyclerview, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_list_song);
        if (getArguments() != null) {
            mList = getArguments().getParcelableArrayList(Constant.ARTIST_TO_FRAGMENT);
        }
        mLayoutManager = new GridLayoutManager(this.getActivity(), 2);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new AdapterRecyclerViewListArtist(mList, this.getActivity());
        mRecyclerView.setAdapter(mAdapter);
        initEvent();
        return view;
    }

    private void initEvent() {
        mAdapter.SetOnItemClickListener(new ClickListener() {
            @Override
            public void OnItemClick(int position) {
                Intent intent = new Intent(getActivity(), ArtistDetail.class);
                intent.putExtra(Constant.ARTIST_ENTITY, mList.get(position));
                startActivity(intent);
            }

            @Override
            public void OnLongItemClick(int position) {

            }
        });
    }
}
