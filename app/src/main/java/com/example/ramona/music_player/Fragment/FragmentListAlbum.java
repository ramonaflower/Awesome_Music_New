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

import com.example.ramona.music_player.Activity.AlbumDetail;
import com.example.ramona.music_player.Adapter.AdapterRecyclerViewListAlbum;
import com.example.ramona.music_player.Constant;
import com.example.ramona.music_player.Entities.AlbumEntities;
import com.example.ramona.music_player.Interface.ClickListener;
import com.example.ramona.music_player.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ramona on 9/26/2017.
 */

public class FragmentListAlbum extends Fragment {
    private RecyclerView mRecyclerView;
    private AdapterRecyclerViewListAlbum mAdapter;
    private GridLayoutManager mLayoutManager;
    private List<AlbumEntities> mList = new ArrayList<AlbumEntities>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.lt_recyclerview, container, false);
        mRecyclerView = view.findViewById(R.id.recycler_view_list_song);
        if (getArguments() != null) {
            mList = getArguments().getParcelableArrayList(Constant.ALBUM_TO_FRAGMENT);
        }
        mLayoutManager = new GridLayoutManager(this.getActivity(), 2);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new AdapterRecyclerViewListAlbum(mList, this.getActivity());
        mRecyclerView.setAdapter(mAdapter);
        initEvent();
        return view;
    }

    private void initEvent() {
        mAdapter.SetOnItemClickListener(new ClickListener() {
            @Override
            public void OnItemClick(int position) {
                Intent intent = new Intent(getActivity(), AlbumDetail.class);
                intent.putExtra(Constant.ALBUM_ENTITY, mList.get(position));
                startActivity(intent);
            }

            @Override
            public void OnLongItemClick(int position) {

            }
        });
    }
}
