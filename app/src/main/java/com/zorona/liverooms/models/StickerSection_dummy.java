package com.zorona.liverooms.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class StickerSection_dummy implements Parcelable {

    public static final Creator<StickerSection_dummy> CREATOR = new Creator<StickerSection_dummy>() {

        @Override
        public StickerSection_dummy createFromParcel(Parcel in) {
            return new StickerSection_dummy(in);
        }

        @Override
        public StickerSection_dummy[] newArray(int size) {
            return new StickerSection_dummy[size];
        }
    };
    public int id;
    public String name;
    public Date createdAt;
    public Date updatedAt;
    public int stickersCount;

    public StickerSection_dummy() {
    }

    protected StickerSection_dummy(Parcel in) {
        id = in.readInt();
        name = in.readString();
        stickersCount = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeInt(stickersCount);
    }
}
