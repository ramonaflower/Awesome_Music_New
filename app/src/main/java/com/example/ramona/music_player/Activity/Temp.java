package com.example.ramona.music_player.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ramona.music_player.Adapter.AdapterTemp;
import com.example.ramona.music_player.Entities.SongEntities;
import com.example.ramona.music_player.R;
import com.example.ramona.music_player.SQLite.MyDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ramona on 10/24/2017.
 */

public class Temp extends AppCompatActivity {
    private MyDatabase database;
    RecyclerView recyclerView;
    Button btn;
    EditText editText;
    TextView text1, text2, text3;
    List<SongEntities> mListSong = new ArrayList<>();
    List<Integer> mListID = new ArrayList<>();
    AdapterTemp adapterTemp;
    LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lt_temp);
        initControl();
        database = new MyDatabase(Temp.this);
        mListSong.addAll(database.getAllSong());
        Log.e("size", mListSong.size() + "");
        for (int i = 0; i < mListSong.size(); i++) {
            mListID.add(mListSong.get(i).getmSongID());
        }
        Log.e("List ID", mListID+"");
        layoutManager = new LinearLayoutManager(Temp.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapterTemp = new AdapterTemp(mListID);
        recyclerView.setAdapter(adapterTemp);
        initEvent();
    }

    private void initEvent() {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String a = editText.getText().toString();
                SongEntities entities = database.getSongByID(a);
                text1.setText(entities.getmSongID()+"");
                text2.setText(entities.getmSongName());
                text3.setText(entities.getmArtistName());
            }
        });
    }

    private void initControl() {
        recyclerView = findViewById(R.id.RV);
        editText = findViewById(R.id.edit_text);
        text1 = findViewById(R.id.id_song);
        text2 = findViewById(R.id.mot);
        text3 = findViewById(R.id.hai);
        btn = findViewById(R.id.btn);
    }
}
