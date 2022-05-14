package com.hisu.androidteamproject.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.hisu.androidteamproject.dao.PostDao;
import com.hisu.androidteamproject.dao.UserDao;
import com.hisu.androidteamproject.entity.Post;
import com.hisu.androidteamproject.entity.User;

@Database(entities = {User.class, Post.class}, exportSchema = false, version = 1)
public abstract class DBInit extends RoomDatabase {
    public abstract UserDao userDao();

    public abstract PostDao postDao();

    private static DBInit instance;

    public static DBInit getInstance(Context context){
        if (instance == null)
            instance = Room.databaseBuilder(
                    context,
                    DBInit.class,
                    "LocalDB"
            ).allowMainThreadQueries().build();
        return instance;
    }
}
