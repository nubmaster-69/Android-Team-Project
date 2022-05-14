package com.hisu.androidteamproject.entity;

import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import java.sql.Date;

public class TimeStampConverter {

    @TypeConverter
    public static Long convertFromTimeStamp(java.util.Date date){
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static Date convertToTimeStamp(Long date){
        return date == null ? null : new Date(date);
    }
}
