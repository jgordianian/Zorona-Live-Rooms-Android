package com.zorona.liverooms.modelclass;

import com.zorona.liverooms.BuildConfig;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReliteRoot {

    @SerializedName("video")
    private List<VideoItem> video;

    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private boolean status;

    public List<VideoItem> getVideo() {
        return video;
    }

    public String getMessage() {
        return message;
    }

    public boolean isStatus() {
        return status;
    }

    public static class VideoItem {

        @SerializedName("song")
        private Song song;

        @SerializedName("thumbnail")
        private String thumbnail;


        @SerializedName("name")
        private String name;

        @SerializedName("userImage")
        private String userImage;

        @SerializedName("time")
        private String time;

        @SerializedName("isVIP")
        private boolean isVIP;

        @SerializedName("allowComment")
        private boolean allowComment;

        public void setAllowComment(boolean allowComment) {
            this.allowComment = allowComment;
        }

        public String getName() {
            return name;
        }

        public String getUserImage() {
            return userImage;
        }

        public String getTime() {
            return time;
        }

        public boolean isVIP() {
            return isVIP;
        }

        public int getLike() {
            return like;
        }

        @SerializedName("isLike")
        private boolean isLike;

        public boolean isOriginalAudio() {
            return isOriginalAudio;
        }

        @SerializedName("like")
        private int like;

        @SerializedName("caption")
        private String caption;


        @SerializedName("video")
        private String video;

        @SerializedName("screenshot")
        private String screenshot;

        @SerializedName("mentionPeople")
        private List<String> mentionPeople;

        @SerializedName("userId")
        private String userId;

        @SerializedName("isOriginalAudio")
        private boolean isOriginalAudio;

        @SerializedName("showVideo")
        private int showVideo;

        @SerializedName("comment")
        private int comment;

        @SerializedName("location")
        private String location;

        @SerializedName("_id")
        private String id;

        @SerializedName("hashtag")
        private List<String> hashtag;

        public Song getSong() {
            return song;
        }

        public String getThumbnail() {
            return thumbnail;
        }


        public int getLikeCount() {
            return like;
        }

        public String getCaption() {
            return caption;
        }

        public boolean isAllowComment() {
            return allowComment;
        }

        public String getVideo() {
            return BuildConfig.BASE_URL + video;
        }

        public String getScreenshot() {
            return screenshot;
        }

        public List<String> getMentionPeople() {
            return mentionPeople;
        }

        public String getUserId() {
            return userId;
        }

        public boolean isIsOriginalAudio() {
            return isOriginalAudio;
        }

        public int getShowVideo() {
            return showVideo;
        }

        public int getComment() {
            return comment;
        }

        public String getLocation() {
            return location;
        }

        public void setLikeCount(int like) {
            this.like = like;
        }

        public boolean isLike() {
            return isLike;
        }

        public void setLike(boolean like) {
            isLike = like;
        }

        public String getId() {
            return id;
        }

        public List<String> getHashtag() {
            return hashtag;
        }

        public static class Song {

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
        }
    }
}