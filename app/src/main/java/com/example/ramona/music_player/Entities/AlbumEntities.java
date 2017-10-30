package com.example.ramona.music_player.Entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Ramona on 9/26/2017.
 */

public class AlbumEntities implements Parcelable {
    private int mAlbumID;
    private String mAlbumName, mArtistName, mAlbumArt;

    public AlbumEntities() {
    }

    public AlbumEntities(int mAlbumID, String mAlbumName, String mArtistName, String mAlbumArt) {
        this.mAlbumID = mAlbumID;
        this.mAlbumName = mAlbumName;
        this.mArtistName = mArtistName;
        this.mAlbumArt = mAlbumArt;
    }

    public AlbumEntities(Parcel parcel) {
        mAlbumID = parcel.readInt();
        mAlbumName = parcel.readString();
        mArtistName = parcel.readString();
        mAlbumArt = parcel.readString();
    }

    public static final Creator<AlbumEntities> CREATOR = new Creator<AlbumEntities>() {
        @Override
        public AlbumEntities createFromParcel(Parcel parcel) {
            return new AlbumEntities(parcel);
        }

        @Override
        public AlbumEntities[] newArray(int i) {
            return new AlbumEntities[i];
        }
    };

    public int getmAlbumID() {
        return mAlbumID;
    }

    public void setmAlbumID(int mAlbumID) {
        this.mAlbumID = mAlbumID;
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

    public String getmAlbumArt() {
        return mAlbumArt;
    }

    public void setmAlbumArt(String mAlbumArt) {
        this.mAlbumArt = mAlbumArt;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mAlbumID);
        parcel.writeString(mAlbumName);
        parcel.writeString(mArtistName);
        parcel.writeString(mAlbumArt);
    }
}
