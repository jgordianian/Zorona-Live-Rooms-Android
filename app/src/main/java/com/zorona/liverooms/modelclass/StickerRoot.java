package com.app.liverooms.modelclass;

import android.os.Parcel;
import android.os.Parcelable;

import com.app.liverooms.BuildConfig;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StickerRoot {

    @SerializedName("sticker")
    private List<StickerItem> sticker;

    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private boolean status;

    public List<StickerItem> getSticker() {
        return sticker;
    }

    public String getMessage() {
        return message;
    }

    public boolean isStatus() {
        return status;
    }

    public static class StickerItem implements Parcelable {

        public static final Creator<StickerItem> CREATOR = new Creator<StickerItem>() {
            @Override
            public StickerItem createFromParcel(Parcel in) {
                return new StickerItem(in);
            }

            @Override
            public StickerItem[] newArray(int size) {
                return new StickerItem[size];
            }
        };
        @SerializedName("createdAt")
        private String createdAt;
        @SerializedName("sticker")
        private String sticker;
        @SerializedName("_id")
        private String id;
        @SerializedName("updatedAt")
        private String updatedAt;

        protected StickerItem(Parcel in) {
            createdAt = in.readString();
            sticker = in.readString();
            id = in.readString();
            updatedAt = in.readString();
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public String getSticker() {
            return BuildConfig.BASE_URL + sticker;
        }

        public String getId() {
            return id;
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
            dest.writeString(createdAt);
            dest.writeString(sticker);
            dest.writeString(id);
            dest.writeString(updatedAt);
        }
    }
}