package ru.job4j.tourist.maps_history;

import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import ru.job4j.tourist.R;
import ru.job4j.tourist.dbutils.DBHelper;
import ru.job4j.tourist.model.Point;

/**
 * @author Dmitry Kolganov (mailto:dmk78@inbox.ru)
 * @version $Id$
 * @since 15.01.2020
 */

public class MapsHistoryFragment extends Fragment implements OnMapReadyCallback, MapsHistoryContract.View {

    private MapsHistoryContract.Presenter presenter;
    private GoogleMap googleMap;
    private ListView listView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_history, container, false);
        presenter = new MapsHistoryPresenter(DBHelper.getInstance(getContext()), this);
        listView = view.findViewById(R.id.listView);
        ArrayAdapter<Point> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, presenter.getAllFavPoints());
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener((adapterView, view1, i, l) -> {
            Point point = (Point) adapterView.getAdapter().getItem(i);
            presenter.onPointClicked(point);
        });
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        UiSettings uiSettings = googleMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
    }

    @Override
    public void setMarker(Location location) {
        MarkerOptions marker = new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("Hello Maps");
        marker.flat(true);
        googleMap.addMarker(marker);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));

    }
}
