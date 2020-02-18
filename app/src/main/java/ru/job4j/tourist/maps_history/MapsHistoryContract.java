package ru.job4j.tourist.maps_history;

import android.location.Location;

import java.util.List;

import ru.job4j.tourist.model.Point;

public interface MapsHistoryContract {
    interface Presenter {
        void onPointClicked(Point point);

        List<Point> getAllFavPoints();

    }

    interface View {
        void setMarker(Location location);

    }

    interface Model {
        List<Point> getAllFavPoints();

    }
}
