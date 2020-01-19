package ru.job4j.tourist.dbutils;

import android.location.Location;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import ru.job4j.tourist.model.Point;

public class Converters {
    @TypeConverter
    public static List<Point> fromStringToPointsList(String value) {
        if (value == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<Point>>() {
        }.getType();
        List<Point> countryLangList = gson.fromJson(value, type);
        return countryLangList;
    }

    @TypeConverter
    public static String fromPointsListToString(List<Point> list) {
        if (list == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<Point>>() {
        }.getType();
        String json = gson.toJson(list, type);
        return json;
    }

    @TypeConverter
    public static String LocationToString(Location location) {
        if (location == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<Location>() {

        }.getType();
        return gson.toJson(location, type);
    }

    @TypeConverter
    public static Location stringToLocation(String s) {
        if (s == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<Location>() {
        }.getType();
        return gson.fromJson(s, type);
    }

}
