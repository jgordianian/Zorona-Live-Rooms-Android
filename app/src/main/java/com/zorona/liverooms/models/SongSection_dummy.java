package com.zorona.liverooms.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class SongSection_dummy implements Parcelable {

    public static final Creator<SongSection_dummy> CREATOR = new Creator<SongSection_dummy>() {

        @Override
        public SongSection_dummy createFromParcel(Parcel in) {
            return new SongSection_dummy(in);
        }

        @Override
        public SongSection_dummy[] newArray(int size) {
            return new SongSection_dummy[size];
        }
    };
    public int id;
    public String name;
    public Date createdAt;
    public Date updatedAt;
    public int songsCount;

    public SongSection_dummy() {
    }

    protected SongSection_dummy(Parcel in) {
        id = in.readInt();
        name = in.readString();
        songsCount = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeInt(songsCount);
    }
}
