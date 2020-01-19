package ru.job4j.tourist;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.List;

import ru.job4j.tourist.dbutils.DBHelper;
import ru.job4j.tourist.model.Point;

/**
 * @author Dmitry Kolganov (mailto:dmk78@inbox.ru)
 * @since 15.01.2020
 * @version $Id$
 */

public class HistoryFragment extends Fragment implements OnMapReadyCallback {


    private GoogleMap mMap;
    private DBHelper dbHelper;
    private ListView listView;
    private Location mLocation;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_history, container, false);

        dbHelper = DBHelper.getInstance(getContext());
        listView = view.findViewById(R.id.listView);
        ArrayAdapter<Point> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dbHelper.getAllPoints());
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Point point = (Point) adapterView.getAdapter().getItem(i);
                Log.i("point", String.valueOf(point.getLocation().getLatitude()+" "+point.getLocation().getLongitude()));
                Toast.makeText(getContext(), "" + point.getName(), Toast.LENGTH_SHORT).show();
                LatLng curLoc = new LatLng(point.getLocation().getLatitude(), point.getLocation().getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curLoc, 15));

            }
        });
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        UiSettings uiSettings = googleMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        List<LatLng> latLngs = new ArrayList<>();
        List<Point> points = dbHelper.getAllPoints();
        for (Point point : points) {
            latLngs.add(new LatLng(point.getLocation().getLatitude(), point.getLocation().getLongitude()));

        }
        for (LatLng latLng : latLngs) {

            googleMap.addMarker(new MarkerOptions().position(latLng).title(latLng.toString()));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        }


    }
}
