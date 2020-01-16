package ru.job4j.tourist.model;

import java.util.List;
import java.util.Objects;

public class Track {
    private int id;
    private String name;
    private List<Point> points;

    public Track(String name, List<Point> points) {
        this.name = name;
        this.points = points;
    }

    public Track(int id, String name, List<Point> points) {
        this.id = id;
        this.name = name;
        this.points = points;
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

    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Track track = (Track) o;
        return id == track.id &&
                Objects.equals(name, track.name) &&
                Objects.equals(points, track.points);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, points);
    }
}
