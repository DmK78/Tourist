package ru.job4j.tourist.model;

import android.location.Location;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.Update;

import java.util.Objects;

//@Entity(tableName = "points",
//        foreignKeys = @ForeignKey(entity = Track.class, parentColumns = "id", childColumns = "track_id"), indices = {@Index(("point_id"))})
public class Point {
   // @PrimaryKey(autoGenerate = true)
 //   @ColumnInfo(name = "point_id")
    private int pointId;
    private String name;
    private Location location;
 //   @ColumnInfo(name = "track_id")
    private int trackId;

    public Point() {
    }

 //   @Ignore
    public Point(String name, Location location) {
        this.name = name;
        this.location = location;
    }

 //   @Ignore
    public Point(int id, String name, Location location) {
        this.pointId = id;
        this.name = name;
        this.location = location;
    }

 //   @Ignore
    public Point(int id, String name, Location location, int trackId) {
        this.pointId = id;
        this.name = name;
        this.location = location;
        this.trackId = trackId;
    }

 //   @Ignore
    public Point(String name, Location location, int trackId) {
        this.name = name;
        this.location = location;
        this.trackId = trackId;
    }


    public int getPointId() {
        return pointId;
    }

    public void setPointId(int pointId) {
        this.pointId = pointId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getTrackId() {
        return trackId;
    }

    public void setTrackId(int trackId) {
        this.trackId = trackId;
    }

  //  @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return pointId == point.pointId &&
                trackId == point.trackId &&
                Objects.equals(name, point.name) &&
                Objects.equals(location, point.location);
    }

 //   @Override
    public int hashCode() {
        return Objects.hash(pointId, name, location, trackId);
    }
}
