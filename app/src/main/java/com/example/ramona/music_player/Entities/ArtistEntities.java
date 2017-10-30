package com.example.ramona.music_player.Entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Ramona on 9/26/2017.
 */

public class ArtistEntities implements Parcelable {
    private int mArtistID, mSongQuantity, mAlbumQuantity;
    private String mArtistName;

    public ArtistEntities() {
    }

    public ArtistEntities(int mArtistID, int mSongQuantity, int mAlbumQuantity, String mArtistName) {
        this.mArtistID = mArtistID;
        this.mSongQuantity = mSongQuantity;
        this.mAlbumQuantity = mAlbumQuantity;
        this.mArtistName = mArtistName;
    }

    public ArtistEntities(Parcel parcel) {
        mArtistID = parcel.readInt();
        mSongQuantity = parcel.readInt();
        mAlbumQuantity = parcel.readInt();
        mArtistName = parcel.readString();
    }

    public static final Creator<ArtistEntities> CREATOR = new Creator<ArtistEntities>() {
        @Override
        public ArtistEntities createFromParcel(Parcel parcel) {
            return new ArtistEntities(parcel);
        }

        @Override
        public ArtistEntities[] newArray(int i) {
            return new ArtistEntities[i];
        }
    };

    public int getmArtistID() {
        return mArtistID;
    }

    public void setmArtistID(int mArtistID) {
        this.mArtistID = mArtistID;
    }

    public int getmSongQuantity() {
        return mSongQuantity;
    }

    public void setmSongQuantity(int mSongQuantity) {
        this.mSongQuantity = mSongQuantity;
    }

    public int getmAlbumQuantity() {
        return mAlbumQuantity;
    }

    public void setmAlbumQuantity(int mAlbumQuantity) {
        this.mAlbumQuantity = mAlbumQuantity;
    }

    public String getmArtistName() {
        return mArtistName;
    }

    public void setmArtistName(String mArtistName) {
        this.mArtistName = mArtistName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mArtistID);
        parcel.writeInt(mSongQuantity);
        parcel.writeInt(mAlbumQuantity);
        parcel.writeString(mArtistName);
    }
}
