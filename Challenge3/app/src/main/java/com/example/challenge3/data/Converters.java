package com.example.challenge3.data;

import androidx.room.TypeConverter;

import java.util.Calendar;
import java.util.Date;

public class Converters {
    @TypeConverter
    public static Calendar fromTimestamp(Long value){
        Calendar c= Calendar.getInstance();
        c.setTimeInMillis(value);
        return c;
    }

    @TypeConverter
    public static Long dateToTimestamp(Calendar date){
        return date==null ? null: date.getTimeInMillis();
    }
}
