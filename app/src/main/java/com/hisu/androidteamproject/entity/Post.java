package com.hisu.androidteamproject.entity;

public class Post {

    private int postReact;
    private int imageURL;
    private String status;

    public Post() {
    }

    public Post(int postReact, int imageURL, String status) {
        this.postReact = postReact;
        this.imageURL = imageURL;
        this.status = status;
    }

    public int getPostReact() {
        return postReact;
    }

    public void setPostReact(int postReact) {
        this.postReact = postReact;
    }

    public int getImageURL() {
        return imageURL;
    }

    public void setImageURL(int imageURL) {
        this.imageURL = imageURL;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}