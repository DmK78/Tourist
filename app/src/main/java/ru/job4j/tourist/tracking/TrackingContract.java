package ru.job4j.tourist.tracking;

import android.location.LocationListener;
import android.location.LocationManager;

import ru.job4j.tourist.model.Point;
import ru.job4j.tourist.model.Track;

public interface TrackingContract {
    interface view {
        LocationManager getLocationManager(LocationListener locationListener);

        void showToast(String s);
    }

    interface model {
        Track addTrack(Track track);

        void addPoint(Point point);

    }

    interface presenter {
        void onStartClicked();

        void onStopClicked();
    }
}
