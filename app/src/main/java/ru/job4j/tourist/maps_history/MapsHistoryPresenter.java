package ru.job4j.tourist.maps_history;

import android.content.Context;

import java.util.List;

import ru.job4j.tourist.maps.MapsContract;
import ru.job4j.tourist.model.Point;

public class MapsHistoryPresenter implements MapsHistoryContract.Presenter{
    private MapsHistoryContract.Model model;
    private MapsHistoryContract.View view;

    public MapsHistoryPresenter(MapsHistoryContract.Model model, MapsHistoryContract.View view) {
        this.model = model;
        this.view = view;
    }

    @Override
    public void onPointClicked(Point point) {
        view.setMarker(point.getLocation());

    }

    @Override
    public List<Point> getAllFavPoints() {
        return model.getAllFavPoints();
    }
}
