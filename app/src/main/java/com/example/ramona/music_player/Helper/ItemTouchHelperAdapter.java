package com.example.ramona.music_player.Helper;

/**
 * Created by Ramona on 10/26/2017.
 */

public interface ItemTouchHelperAdapter {
    boolean onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);
}
