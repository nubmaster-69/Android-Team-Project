package com.hisu.androidteamproject.entity;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.UUID;

public class Post implements Serializable {

    private String id;
    private String userID;
    private String status;
    private int postReact;
    private String imageURL;
    private Timestamp postDate;

    public Post() {
    }

    public Post(
            String userID, String status, int postReact, String imageURL, Timestamp postDate
    ) {
        this.id = UUID.randomUUID().toString();
        this.userID = userID;
        this.status = status;
        this.postReact = postReact;
        this.imageURL = imageURL;
        this.postDate = postDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getPostReact() {
        return postReact;
    }

    public void setPostReact(int postReact) {
        this.postReact = postReact;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public Timestamp getPostDate() {
        return postDate;
    }

    public void setPostDate(Timestamp postDate) {
        this.postDate = postDate;
    }
}