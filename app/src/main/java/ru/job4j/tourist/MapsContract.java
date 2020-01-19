package ru.job4j.tourist;

import android.location.Location;
import android.location.LocationListener;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.libraries.places.api.model.Place;

import java.util.List;

import ru.job4j.tourist.model.Point;

/**
 * @author Dmitry Kolganov (mailto:dmk78@inbox.ru)
 * @since 15.01.2020
 * @version $Id$
 */

public interface MapsContract {
    interface MapsPresenterInterface {

        void onIMHereClicked(Location location, GoogleMap googleMap);

        void onSaveLocationClicked();

        void onClearHistoryClicked();

        int getFavPointsCount();

        Location onPlaceSelectedClicked(Place place);
    }

    interface MapsViewInterface {
        void refreshBtnSaveCount(int i);

        void focusCamers(Location location);

        void setMarker(Location location);
    }

    interface MapsModelInterface {

        void addPoint(Point point);

        void deleteAllFavPoints();

        List<Point> getAllFavPoints();
    }
}
