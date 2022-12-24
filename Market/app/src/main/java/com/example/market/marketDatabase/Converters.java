package com.example.market.marketDatabase;

import androidx.room.TypeConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class Converters {
    @TypeConverter
    public static Calendar fromLong(Long value){
        Calendar c= Calendar.getInstance();
        c.setTimeInMillis(value);
        return c;
    }

    @TypeConverter
    public static Long fromDate(Calendar date){
        return date==null ? null: date.getTimeInMillis();
    }

    @TypeConverter
    public static ArrayList<String> fromString(String value) {
        if(value.equals("")){
            return  new ArrayList<>();
        }
        String[] arrOfStr = value.trim().split("\\s+");
        return new ArrayList<String>(Arrays.asList(arrOfStr));
    }

    @TypeConverter
    public static String fromArrayList(ArrayList<String> list) {
        StringBuilder result= new StringBuilder();
        for(String str: list){
            result.append(" ");
            result.append(str);
        }
        if(list.isEmpty()){
            result.append("");
        }
        return result.toString();
    }

}
