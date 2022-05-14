package com.hisu.androidteamproject.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Entity(tableName = "posts")
public class Post implements Serializable {
    @PrimaryKey
    @NonNull
    private String id;
    private String userID;
    private String status;
    private int postReact;
    private String imageURL;
    @TypeConverters(TimeStampConverter.class)
    private Date postDate;

    public Post() {
    }

    public Post(
            String userID, String status, int postReact, String imageURL, Date postDate
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

    public Date getPostDate() {
        return postDate;
    }

    public void setPostDate(Date postDate) {
        this.postDate = postDate;
    }
}