package com.example.ramona.music_player.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ramona.music_player.Activity.PlaySong;
import com.example.ramona.music_player.Adapter.AdapterRecyclerViewListSong;
import com.example.ramona.music_player.Constant;
import com.example.ramona.music_player.Entities.SongEntities;
import com.example.ramona.music_player.Interface.ClickListener;
import com.example.ramona.music_player.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ramona on 9/26/2017.
 */

public class FragmentListSong extends Fragment {
    private RecyclerView mRecyclerView;
    private AdapterRecyclerViewListSong mAdapter;
    private LinearLayoutManager mLayoutManager;
    private List<SongEntities> mList = new ArrayList<SongEntities>();

    public FragmentListSong() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.lt_recyclerview, container, false);
        mRecyclerView = view.findViewById(R.id.recycler_view_list_song);
        if (getArguments() != null) {
            mList = getArguments().getParcelableArrayList(Constant.SONG_TO_FRAGMENT);
        }
        mLayoutManager = new LinearLayoutManager(this.getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new AdapterRecyclerViewListSong(mList, this.getActivity());
        mRecyclerView.setAdapter(mAdapter);
        initEvent();
        return view;
    }

    private void initEvent() {
        mAdapter.SetOnItemClickListener(new ClickListener() {
            @Override
            public void OnItemClick(int position) {
                Intent intent = new Intent(getActivity(), PlaySong.class);
                intent.putExtra(Constant.SONG_INDEX_TO_PLAY_SONG, position);
                intent.putParcelableArrayListExtra(Constant.LIST_SONG_TO_PLAY_SONG, (ArrayList<SongEntities>) mList);
                startActivity(intent);
            }

            @Override
            public void OnLongItemClick(int position) {

            }
        });
    }
}
