package ru.job4j.tourist.dbutils;

import android.graphics.Movie;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ru.job4j.tourist.model.Point;
import ru.job4j.tourist.model.Track;

@Dao
public interface TracksDao {
    @Query("SELECT * FROM tracks")
    List<Track> getAllTracks();

    /*@Query("SELECT * FROM favorite_movies")
    LiveData<List<FavoriteMovie>> getAllFavoriteMovies();*/

    @Query("SELECT * FROM tracks WHERE id==:trackId")
    Track getTrackById(int trackId);

    /*@Query("SELECT * FROM favorite_movies WHERE id==:movieId")
    FavoriteMovie getFavoriteMovieById(int movieId);*/

    @Query("DELETE FROM tracks")
    void deletAllTracks();

    @Insert
    long insertTrack(Track track);

    @Delete
    void deleteTrack(Track track);

    /*@Insert
    void insertPoint(Point point);*/

    @Update
    void updateTrack(Track track);

    /*@Delete
    void deleteFavoriteMovie(FavoriteMovie movie);*/
}

