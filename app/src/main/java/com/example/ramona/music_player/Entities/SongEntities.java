package com.example.ramona.music_player.Entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Ramona on 9/26/2017.
 */

public class SongEntities implements Parcelable {
    private String mSongName, mAlbumName, mArtistName, mDuration, mSongPath;
    private int mSongID, mAlbumID, mArtistID;

    public SongEntities() {
    }

    public SongEntities(String mSongName, String mAlbumName, String mArtistName, String mDuration, String mSongPath, int mSongID, int mAlbumID, int mArtistID) {
        this.mSongName = mSongName;
        this.mAlbumName = mAlbumName;
        this.mArtistName = mArtistName;
        this.mDuration = mDuration;
        this.mSongPath = mSongPath;
        this.mSongID = mSongID;
        this.mAlbumID = mAlbumID;
        this.mArtistID = mArtistID;
    }

    public SongEntities(Parcel parcel) {
        mSongName = parcel.readString();
        mAlbumName = parcel.readString();
        mArtistName = parcel.readString();
        mDuration = parcel.readString();
        mSongPath = parcel.readString();
        mSongID = parcel.readInt();
        mAlbumID = parcel.readInt();
        mArtistID = parcel.readInt();
    }

    public static final Creator<SongEntities> CREATOR = new Creator<SongEntities>() {
        @Override
        public SongEntities createFromParcel(Parcel parcel) {
            return new SongEntities(parcel);
        }

        @Override
        public SongEntities[] newArray(int i) {
            return new SongEntities[i];
        }
    };

    public String getmSongName() {
        return mSongName;
    }

    public void setmSongName(String mSongName) {
        this.mSongName = mSongName;
    }

    public String getmAlbumName() {
        return mAlbumName;
    }

    public void setmAlbumName(String mAlbumName) {
        this.mAlbumName = mAlbumName;
    }

    public String getmArtistName() {
        return mArtistName;
    }

    public void setmArtistName(String mArtistName) {
        this.mArtistName = mArtistName;
    }

    public String getmDuration() {
        return mDuration;
    }

    public void setmDuration(String mDuration) {
        this.mDuration = mDuration;
    }

    public String getmSongPath() {
        return mSongPath;
    }

    public void setmSongPath(String mSongPath) {
        this.mSongPath = mSongPath;
    }

    public int getmSongID() {
        return mSongID;
    }

    public void setmSongID(int mSongID) {
        this.mSongID = mSongID;
    }

    public int getmAlbumID() {
        return mAlbumID;
    }

    public void setmAlbumID(int mAlbumID) {
        this.mAlbumID = mAlbumID;
    }

    public int getmArtistID() {
        return mArtistID;
    }

    public void setmArtistID(int mArtistID) {
        this.mArtistID = mArtistID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mSongName);
        parcel.writeString(mAlbumName);
        parcel.writeString(mArtistName);
        parcel.writeString(mDuration);
        parcel.writeString(mSongPath);
        parcel.writeInt(mSongID);
        parcel.writeInt(mAlbumID);
        parcel.writeInt(mArtistID);
    }
}
