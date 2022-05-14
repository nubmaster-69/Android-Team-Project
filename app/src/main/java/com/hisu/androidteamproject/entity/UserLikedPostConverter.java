package com.hisu.androidteamproject.entity;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class UserLikedPostConverter {

    @TypeConverter
    public String convertFromListToString(List<String> likedPosts){
        Gson gson = new Gson();

        Type type = new TypeToken<List<String>>(){}.getType();

        return gson.toJson(likedPosts, type);
    }

    @TypeConverter
    public List<String> convertFromStringToList(String json){
        if (json != null)
            return null;

        Gson gson = new Gson();

        Type type = new TypeToken<List<Post>>(){}.getType();

        List<String> likedPost = gson.fromJson(json, type);

        return likedPost;
    }
}
