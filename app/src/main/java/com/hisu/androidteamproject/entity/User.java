package com.hisu.androidteamproject.entity;

import java.io.Serializable;

public class User implements Serializable {
    private String username;
    //    Todo: thay đổi datatype tương ứng để load ảnh từ firebase về, hiện tại là int(load local)
    private int avatar;
    private String gender;
    private String email;
    private String address;

    public User() {
    }

    public User(String username, int avatar, String gender, String email, String address) {
        this.username = username;
        this.avatar = avatar;
        this.gender = gender;
        this.email = email;
        this.address = address;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getAvatar() {
        return avatar;
    }

    public void setAvatar(int avatar) {
        this.avatar = avatar;
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