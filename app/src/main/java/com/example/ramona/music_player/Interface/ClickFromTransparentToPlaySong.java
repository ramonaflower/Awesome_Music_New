package com.example.ramona.music_player.Interface;

import com.example.ramona.music_player.Entities.SongEntities;

import java.util.List;

/**
 * Created by Ramona on 10/20/2017.
 */

public interface ClickFromTransparentToPlaySong {
    void clickToPlaySong(int position);
    void updateListSong(List<SongEntities> list, int index);
    void updateOptionMenu();
}
