package com.zorona.liverooms.modelclass;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SongRoot {

    @SerializedName("song")
    private List<SongItem> song;

    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private boolean status;

    public List<SongItem> getSong() {
        return song;
    }

    public String getMessage() {
        return message;
    }

    public boolean isStatus() {
        return status;
    }

    public static class SongItem implements Parcelable {

        public static final Creator<SongItem> CREATOR = new Creator<SongItem>() {
            @Override
            public SongItem createFromParcel(Parcel in) {
                return new SongItem(in);
            }

            @Override
            public SongItem[] newArray(int size) {
                return new SongItem[size];
            }
        };
        @SerializedName("song")
        private String song;
        @SerializedName("image")
        private String image;
        @SerializedName("createdAt")
        private String createdAt;
        @SerializedName("singer")
        private String singer;
        @SerializedName("isDelete")
        private boolean isDelete;
        @SerializedName("_id")
        private String id;
        @SerializedName("title")
        private String title;
        @SerializedName("updatedAt")
        private String updatedAt;

        protected SongItem(Parcel in) {
            song = in.readString();
            image = in.readString();
            createdAt = in.readString();
            singer = in.readString();
            isDelete = in.readByte()!=0;
            id = in.readString();
            title = in.readString();
            updatedAt = in.readString();
        }

        public String getSong() {
            return song;
        }

        public String getImage() {
            return image;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public String getSinger() {
            return singer;
        }

        public boolean isIsDelete() {
            return isDelete;
        }

        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(song);
            dest.writeString(image);
            dest.writeString(createdAt);
            dest.writeString(singer);
            dest.writeByte((byte) (isDelete ? 1 : 0));
            dest.writeString(id);
            dest.writeString(title);
            dest.writeString(updatedAt);
        }
    }
}