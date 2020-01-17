package ru.job4j.tourist.model;

import android.location.Location;

import java.util.Objects;

public class Point {
    private int id;
    private String name;
    private Location location;
    private int trackId;

    public Point() {
    }

    public Point(String name, Location location) {
        this.name = name;
        this.location = location;
    }

    public Point(int id, String name, Location location) {
        this.id = id;
        this.name = name;
        this.location = location;
    }

    public Point(int id, String name, Location location, int trackId) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.trackId = trackId;
    }

    public Point(String name, Location location, int trackId) {
        this.name = name;
        this.location = location;
        this.trackId = trackId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return id == point.id &&
                trackId == point.trackId &&
                Objects.equals(name, point.name) &&
                Objects.equals(location, point.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, location, trackId);
    }
}
