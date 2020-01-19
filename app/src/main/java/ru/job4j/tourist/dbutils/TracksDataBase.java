package ru.job4j.tourist.dbutils;

import android.content.Context;
import android.graphics.Movie;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import ru.job4j.tourist.model.Point;
import ru.job4j.tourist.model.Track;

@Database(entities = { Track.class},version = 7,exportSchema = false)
@TypeConverters({Converters.class})
public abstract class TracksDataBase extends RoomDatabase {

    private static TracksDataBase database;
    private static final String DB_NAME = "gps_tracker.db";
    private static final Object Lock = new Object();

    public static TracksDataBase detInstance(Context context) {
        synchronized (Lock) {
            if (database == null) {
                database = Room.databaseBuilder(context, TracksDataBase.class, DB_NAME).fallbackToDestructiveMigration().build();
            }
        }
        return database;
    }

    public abstract TracksDao tracksDao();

}
