package com.example.ramona.music_player;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;

/**
 * Created by Ramona on 10/11/2017.
 */

public class Models {
    public static byte[] getCoverAlbum(String path) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(path);
        byte[] art = retriever.getEmbeddedPicture();
        if( art != null ){
            return art;
        }
        return null;
    }
}
