package com.example.ramona.music_player.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.ramona.music_player.Activity.MainActivity;
import com.example.ramona.music_player.Activity.SearchActivity;
import com.example.ramona.music_player.Adapter.AdapterTransparentListSong;
import com.example.ramona.music_player.Constant;
import com.example.ramona.music_player.Entities.SongEntities;
import com.example.ramona.music_player.Helper.OnStartDragListener;
import com.example.ramona.music_player.Helper.SimpleItemTouchHelperCallback;
import com.example.ramona.music_player.Interface.ClickFromTransparentToPlaySong;
import com.example.ramona.music_player.Interface.ClickListener;
import com.example.ramona.music_player.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ramona on 10/20/2017.
 */

public class FragmentPlaySongTransparent extends Fragment implements ClickListener, OnStartDragListener, AdapterTransparentListSong.updateChangeToFragment {
    private RecyclerView mRecyclerView;
    private AdapterTransparentListSong mAdapter;
    private LinearLayoutManager mLayoutManager;
    private List<SongEntities> mList = new ArrayList<>();
    private MenuItem mDeleteSongFromList;
    private ItemTouchHelper mItemTouchHelper;
    private int mIndex;
    private boolean mCheck;
    private boolean mSelectMode = false;
    private ClickFromTransparentToPlaySong mToPlaySong;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constant.BROADCAST_UPDATE_UI)) {
                Bundle bundle = intent.getExtras();
                mAdapter.updateIndex(bundle.getInt(Constant.UPDATE_INDEX));
                mAdapter.notifyDataSetChanged();
            }
            switch (intent.getAction()) {
                case Constant.ACTION_PLAY_MUSIC:
                    mCheck = true;
                    mAdapter.updateCheck(mCheck);
                    mAdapter.notifyDataSetChanged();
                    break;
                case Constant.ACTION_PAUSE_MUSIC:
                    mCheck = false;
                    mAdapter.updateCheck(mCheck);
                    mAdapter.notifyDataSetChanged();
                    break;
            }


        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mToPlaySong = (ClickFromTransparentToPlaySong) context;
        } catch (Exception e) {
            throw new ClassCastException(context.toString() + " must implement onSomeEventListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.lt_transparent_list_song, container, false);
        mRecyclerView = view.findViewById(R.id.transparent_recycler_view_list_song);
        if (getArguments() != null) {
            mList = getArguments().getParcelableArrayList(Constant.PLAYSONG_TO_TRANSPARENT_FRAGMENT);
            mIndex = getArguments().getInt(Constant.INDEX_SONG_TO_TRANSPARENT_FRAGMENT);
            mCheck = getArguments().getBoolean(Constant.IS_PLAYING);
        }
        mLayoutManager = new LinearLayoutManager(this.getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new AdapterTransparentListSong(mList, mIndex, mCheck, this, this, this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.scrollToPosition(mIndex);
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        setMode();
        return view;
    }

    public void setMode(){
        if (mSelectMode){
            mItemTouchHelper.attachToRecyclerView(mRecyclerView);
        } else {
            mItemTouchHelper.attachToRecyclerView(null);
        }
    }

    public void setupOptionMenu(){
        if (mSelectMode){
            mDeleteSongFromList.setVisible(true);
        } else {
            mDeleteSongFromList.setVisible(false);
        }
    }

    public void resetView() {
        mSelectMode = !mSelectMode;
        mAdapter.changeSelectMode();
        mAdapter.notifyDataSetChanged();
        //mRecyclerView.invalidate();
    }

    public boolean getSelectMode(){
        return mSelectMode;
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
    public void OnItemClick(int position) {
        mToPlaySong.clickToPlaySong(position);
        mAdapter.updateIndex(position);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void OnLongItemClick(int position) {
        resetView();
        setMode();
        setupOptionMenu();
        mToPlaySong.updateOptionMenu();
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {

    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(mReceiver);
    }

    @Override
    public void getListUpdate(List<SongEntities> list, int index) {
        mToPlaySong.updateListSong(list, index);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_sort_list_song, menu);
        mDeleteSongFromList = menu.findItem(R.id.action_delete);
        mDeleteSongFromList.setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_delete:

        }
        return super.onOptionsItemSelected(item);
    }

}
