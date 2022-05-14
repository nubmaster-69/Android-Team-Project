package com.hisu.androidteamproject.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.hisu.androidteamproject.entity.User;

import java.util.List;

@Dao
public interface UserDao {

    @Query("SELECT * FROM users")
    List<User> getAllUser();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(User... user);

    @Query("DELETE FROM users")
    void deleteAllDatableOfUsersTable();
}
