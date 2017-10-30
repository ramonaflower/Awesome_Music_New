package com.example.ramona.music_player.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.ramona.music_player.Entities.SongEntities;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ramona on 9/28/2017.
 */

public class MyDatabase extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "music_player";
    // Table Name
    private static final String TABLE_SONG = "Song";
    // Column Name
    private static final String COLUMN_SONG_ID = "SongID";
    private static final String COLUMN_SONG_NAME = "SongName";
    private static final String COLUMN_ALBUM_NAME = "AlbumName";
    private static final String COLUMN_ARTIST_NAME = "ArtistName";
    private static final String COLUMN_SONG_DURATION = "Duration";
    private static final String COLUMN_SONG_PATH = "SongPath";
    private static final String COLUMN_ALBUM_ID = "AlbumID";
    private static final String COLUMN_ARTIST_ID = "ArtistID";

    public MyDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String scrip = "CREATE TABLE " + TABLE_SONG + "("
                + COLUMN_SONG_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_SONG_NAME + " TEXT,"
                + COLUMN_ALBUM_NAME + " TEXT,"
                + COLUMN_ARTIST_NAME + " TEXT,"
                + COLUMN_SONG_DURATION + " TEXT,"
                + COLUMN_SONG_PATH + " TEXT,"
                + COLUMN_ALBUM_ID + " TEXT,"
                + COLUMN_ARTIST_ID + " TEXT" + ")";
        sqLiteDatabase.execSQL(scrip);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public int getSongCount(){
        String countQuery = "SELECT  * FROM " + TABLE_SONG;
        SQLiteDatabase db =this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        db.close();
        return count;
    }

    public boolean AddSong(SongEntities song){
        boolean success = false;
        int count = getSongCount();
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SONG_ID, song.getmSongID());
        values.put(COLUMN_SONG_NAME, song.getmSongName());
        values.put(COLUMN_ALBUM_NAME, song.getmAlbumName());
        values.put(COLUMN_ARTIST_NAME, song.getmArtistName());
        values.put(COLUMN_SONG_DURATION, song.getmDuration());
        values.put(COLUMN_SONG_PATH, song.getmSongPath());
        values.put(COLUMN_ALBUM_ID, song.getmAlbumID());
        values.put(COLUMN_ARTIST_ID, song.getmArtistID());
        db.insert(TABLE_SONG, null, values);
        if (count< getSongCount()){
            success = true;
        }
        Log.e("Add To DB ", success+"");
        db.close();
        return success;
    }

    public void AddListSong(List<SongEntities> listSong){
        for (int i = 0; i<listSong.size(); i++){
            AddSong(listSong.get(i));
        }
    }

    public SongEntities getSongByID(String id){
        SongEntities entities = new SongEntities();
        String query = "SELECT  * FROM " + TABLE_SONG + " WHERE " + COLUMN_SONG_ID + " = " + id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor!=null && cursor.moveToFirst()){
            entities.setmSongID(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_SONG_ID))));
            entities.setmSongName(cursor.getString(cursor.getColumnIndex(COLUMN_SONG_NAME)));
            entities.setmAlbumName(cursor.getString(cursor.getColumnIndex(COLUMN_ALBUM_NAME)));
            entities.setmArtistName(cursor.getString(cursor.getColumnIndex(COLUMN_ARTIST_NAME)));
            entities.setmDuration(cursor.getString(cursor.getColumnIndex(COLUMN_SONG_DURATION)));
            entities.setmSongPath(cursor.getString(cursor.getColumnIndex(COLUMN_SONG_PATH)));
            entities.setmAlbumID(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_ALBUM_ID))));
            entities.setmArtistID(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_ARTIST_ID))));
            cursor.close();
        }
        db.close();
        return entities;
    }

    public List<SongEntities> getAllSong(){
        List<SongEntities> listUser = new ArrayList<SongEntities>();
        String query = "SELECT  * FROM " + TABLE_SONG;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()){
            do {
                SongEntities entities = new SongEntities();
                entities.setmSongID(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_SONG_ID))));
                entities.setmSongName(cursor.getString(cursor.getColumnIndex(COLUMN_SONG_NAME)));
                entities.setmAlbumName(cursor.getString(cursor.getColumnIndex(COLUMN_ALBUM_NAME)));
                entities.setmArtistName(cursor.getString(cursor.getColumnIndex(COLUMN_ARTIST_NAME)));
                entities.setmDuration(cursor.getString(cursor.getColumnIndex(COLUMN_SONG_DURATION)));
                entities.setmSongPath(cursor.getString(cursor.getColumnIndex(COLUMN_SONG_PATH)));
                entities.setmAlbumID(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_ALBUM_ID))));
                entities.setmArtistID(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_ARTIST_ID))));
                listUser.add(entities);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return listUser;
    }

    public boolean DeleteSong(String s){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_SONG, COLUMN_SONG_ID + " = " + s, null) > 0;
    }

    public void DeleteListSong(List<SongEntities> list){
        for (int i = 0; i < list.size(); i++) {
            DeleteSong(list.get(i).getmSongID() + "");
        }
    }
}
