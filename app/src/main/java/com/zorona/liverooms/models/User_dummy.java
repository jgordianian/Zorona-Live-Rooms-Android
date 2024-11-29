package com.app.liverooms.models;

public class User_dummy {
    String name, bio, username, email, image, country;
    int level;
    int followres;
    int fans;
    int videos;
    int age;
    int coin;

    int rCoin;

    public int getrCoin() {

        return rCoin;
    }

    public void setrCoin(int rCoin) {
        this.rCoin = rCoin;
    }

    int posts;
    int id;
    private String gender;

    public User_dummy(String name, String bio, String username, String email, String image, String country, int level, int followres, int fans, int videos, int age, int coin, int posts, int id, String gender) {
        this.name = name;
        this.bio = bio;
        this.username = username;
        this.email = email;
        this.image = image;
        this.country = country;
        this.level = level;
        this.followres = followres;
        this.fans = fans;
        this.videos = videos;
        this.age = age;
        this.coin = coin;
        this.posts = posts;
        this.id = id;
        this.gender = gender;
    }

    public String getGender() {
        return gender;
    }

    public User_dummy() {
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getFollowres() {
        return followres;
    }

    public void setFollowres(int followres) {
        this.followres = followres;
    }

    public int getFans() {
        return fans;
    }

    public void setFans(int fans) {
        this.fans = fans;
    }

    public int getVideos() {
        return videos;
    }

    public void setVideos(int videos) {
        this.videos = videos;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getCoin() {
        return coin;
    }

    public void setCoin(int coin) {
        this.coin = coin;
    }

    public int getPosts() {
        return posts;
    }

    public void setPosts(int posts) {
        this.posts = posts;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
