package com.hisu.androidteamproject.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.hisu.androidteamproject.entity.Post;

import java.util.List;

@Dao
public interface PostDao {

    @Query("SELECT * FROM posts")
    List<Post> getAllPost();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPost(Post... post);

    @Query("DELETE FROM posts")
    void clearTable();
}
