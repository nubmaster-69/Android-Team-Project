package com.hisu.androidteamproject.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity(tableName = "users")
public class User implements Serializable {

    @PrimaryKey
    @NonNull
    private String username;
    private String avatar;
    private String gender;
    private String email;
    private String address;

    @TypeConverters(UserLikedPostConverter.class)
    private List<String> likedPosts;

    public User() {
        likedPosts = new ArrayList<>();
    }

    public User(String username, String gender, String email, String address) {
        this.username = username;
        this.gender = gender;
        this.email = email;
        this.address = address;
    }

    public User(String username, String gender, String email, String address, List<String> likedPosts) {
        this.username = username;
        this.gender = gender;
        this.email = email;
        this.address = address;
        this.likedPosts = likedPosts;
    }

    public List<String> getLikedPosts() {
        return likedPosts;
    }

    public void setLikedPosts(List<String> likedPosts) {
        if(likedPosts == null) this.likedPosts = new ArrayList<>();
        this.likedPosts = likedPosts;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setDefaultAvatar() {
        if (this.gender.equalsIgnoreCase("nam"))
            this.avatar = "https://firebasestorage.googleapis.com/v0/b/momentsharingdb.appspot.com/o/ic_male.png?alt=media&token=28768d9a-5632-45f6-93b4-64672f0fff87";
        else
            this.avatar = "https://firebasestorage.googleapis.com/v0/b/momentsharingdb.appspot.com/o/ic_female.png?alt=media&token=569a721f-23ed-4afe-91a7-3ae43f4ac89b";
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}