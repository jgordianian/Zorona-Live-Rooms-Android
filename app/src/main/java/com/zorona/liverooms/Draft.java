package com.app.liverooms;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "drafts")
public class Draft implements Parcelable {


    @PrimaryKey(autoGenerate = true)
    public int id;
    public String video;
    public String screenshot;
    public String preview;
    @Nullable
    public String description;


    @ColumnInfo(name = "has_comments")
    public boolean hasComments;

    @Nullable
    public String location;

    public static final Creator<Draft> CREATOR = new Creator<Draft>() {
        @Override
        public Draft createFromParcel(Parcel in) {
            return new Draft(in);
        }

        @Override
        public Draft[] newArray(int size) {
            return new Draft[size];
        }
    };

    @ColumnInfo(name = "song_id")
    @Nullable
    public String songId;


    public Draft() {
    }

    @Nullable
    public int privacy;

    protected Draft(Parcel in) {
        id = in.readInt();
        video = in.readString();
        screenshot = in.readString();
        preview = in.readString();
        description = in.readString();

        hasComments = in.readByte() != 0;
        location = in.readString();
        privacy = in.readInt();
        songId = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(video);
        dest.writeString(screenshot);
        dest.writeString(preview);
        dest.writeString(description);

        dest.writeByte((byte) (hasComments ? 1 : 0));
        dest.writeString(location);
        dest.writeInt(privacy);
        dest.writeString(songId);
    }
}
