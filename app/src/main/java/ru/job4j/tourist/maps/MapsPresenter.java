package ru.job4j.tourist.maps;

import android.content.Context;
import android.location.Location;

import com.google.android.libraries.places.api.model.Place;

import ru.job4j.tourist.maps.MapsContract;
import ru.job4j.tourist.model.Point;

/**
 * Класс MapsPresenter
 * @author Dmitry Kolganov (mailto:dmk78@inbox.ru)
 * @since 15.01.2020
 * @version $Id$
 */

public class MapsPresenter implements MapsContract.MapsPresenterInterface {
    private Context context;
    private MapsContract.MapsViewInterface viewInterface;
    private MapsContract.MapsModelInterface modelInterface;
    private Point lastPoint;
    private Point currentPoint;

    public MapsPresenter(Context context, MapsContract.MapsViewInterface viewInterface, MapsContract.MapsModelInterface modelInterface) {
        this.context = context;
        this.viewInterface = viewInterface;
        this.modelInterface = modelInterface;
    }

    @Override
    public void onIMHereClicked(Location location) {
        if (location != null) {
            currentPoint = new Point(location.getLatitude() + ", " + location.getLongitude(), location);
            viewInterface.focusCamers(location);
        }
    }

    @Override
    public void onSaveLocationClicked() {
        //округляет до 3-х знаков после запятой, устанавливая чувствительность, чтобы записывать только отдаленные точки
        //проверяет, не является ли точка слишком близко к предыдущей, если нет, то записывает
        if (lastPoint == null && currentPoint != null || currentPoint != null && Math.round(currentPoint.getLocation().getLatitude() * 1000d) / 1000d !=
                Math.round(lastPoint.getLocation().getLatitude() * 1000d) / 1000d &&
                Math.round(currentPoint.getLocation().getLongitude() * 1000d) / 1000d !=
                        Math.round(lastPoint.getLocation().getLongitude() * 1000d) / 1000d) {
            modelInterface.addPoint(currentPoint);
            viewInterface.refreshBtnSaveCount(modelInterface.getAllFavPoints().size());
            viewInterface.setMarker(currentPoint.getLocation());
            lastPoint = currentPoint;

        }
    }

    @Override
    public void onClearHistoryClicked() {
        modelInterface.deleteAllFavPoints();
        lastPoint = null;
        viewInterface.refreshBtnSaveCount(modelInterface.getAllFavPoints().size());
    }


    @Override
    public int getFavPointsCount() {
        return modelInterface.getAllFavPoints().size();
    }

    @Override
    public void onPlaceSelectedClicked(Place place) {
        Location result = new Location(place.getName());
        result.setLatitude(place.getLatLng().latitude);
        result.setLongitude(place.getLatLng().longitude);
        viewInterface.focusCamers(result);
        currentPoint = new Point(place.getName(), result);
    }


}
