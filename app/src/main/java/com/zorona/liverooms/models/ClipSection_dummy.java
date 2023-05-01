package com.zorona.liverooms.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class ClipSection_dummy implements Parcelable {

    public static final Creator<ClipSection_dummy> CREATOR = new Creator<ClipSection_dummy>() {

        @Override
        public ClipSection_dummy createFromParcel(Parcel in) {
            return new ClipSection_dummy(in);
        }

        @Override
        public ClipSection_dummy[] newArray(int size) {
            return new ClipSection_dummy[size];
        }
    };
    public int id;
    public String name;
    public Date createdAt;
    public Date updatedAt;
    public int clipsCount;

    public ClipSection_dummy() {
    }

    protected ClipSection_dummy(Parcel in) {
        id = in.readInt();
        name = in.readString();
        clipsCount = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeInt(clipsCount);
    }
}
