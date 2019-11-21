package ru.job4j.tourist;

import java.util.Objects;

public class Loc {
    private int id;
    private String name;
    private double latitude;
    private double longitude;

    public Loc() {
    }

    public Loc(int id, String name, double altitude, double longitude) {
        this.id = id;
        this.name = name;
        this.latitude = altitude;
        this.longitude = longitude;
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

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Loc loc = (Loc) o;
        return id == loc.id &&
                Double.compare(loc.latitude, latitude) == 0 &&
                Double.compare(loc.longitude, longitude) == 0 &&
                Objects.equals(name, loc.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, latitude, longitude);
    }

    @Override
    public String toString() {
        return name;
    }
}
