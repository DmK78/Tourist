package ru.job4j.tourist;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import ru.job4j.tourist.dbutils.DBHelper;
import ru.job4j.tourist.dbutils.MainModel;
import ru.job4j.tourist.model.Point;
import ru.job4j.tourist.model.Track;

/**
 * @author Dmitry Kolganov (mailto:dmk78@inbox.ru)
 * @since 15.01.2020
 * @version $Id$
 */

public class TrackingHistoryFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private DBHelper dbHelper;
    private ListView listView;
    private Location mLocation;
    //private MainModel mainModel;
    private Button buttonDeleteAllTracks;
    private List <Track> tracks;
    private ArrayAdapter<Track> arrayAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tracker_history, container, false);
        // mainModel = new MainModel(getContext());
        dbHelper = DBHelper.getInstance(getContext());
        listView = view.findViewById(R.id.listViewTracks);
        tracks = dbHelper.getAllTracks();
        buttonDeleteAllTracks = view.findViewById(R.id.btnHistoryTrackDeleteAll);
        buttonDeleteAllTracks.setOnClickListener(this::deleteAllTracks);
        //ArrayAdapter<Track> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dbHelper.getAllPoints());
        //ArrayAdapter<Track> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dbHelper.getAllTracks());
        arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, tracks);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Track track = (Track) adapterView.getAdapter().getItem(i);
                //Log.i("point", String.valueOf(point.getLocation().getLatitude()+" "+point.getLocation().getLongitude()));
                //Toast.makeText(getContext(), "" + point.getName(), Toast.LENGTH_SHORT).show();
                googleMap.clear();
                LatLng curLoc = new LatLng(track.getPoints().get(0).getLocation().getLatitude(), track.getPoints().get(0).getLocation().getLongitude());
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curLoc, 15));

                Location oldLoc = track.getPoints().get(0).getLocation();
                for (int j = 1; j < track.getPoints().size(); j++) {
                    Location newLoc = track.getPoints().get(j).getLocation();
                    Polyline line = googleMap.addPolyline(new PolylineOptions()
                            .add(
                                    new LatLng(oldLoc.getLatitude(), oldLoc.getLongitude()),
                                    new LatLng(newLoc.getLatitude(), newLoc.getLongitude())));
                    line.setWidth(3);
                    oldLoc=newLoc;

                }
            }
        });
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        return view;
    }

    private void deleteAllTracks(View view) {
        dbHelper.deleteAllTracks();
        arrayAdapter.notifyDataSetChanged();

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        UiSettings uiSettings = googleMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        List<LatLng> latLngs = new ArrayList<>();
        List<Track> tracks = dbHelper.getAllTracks();
        //List<Track> tracks = mainModel.getAllTracks();
        if (tracks.size() > 0) {
            Track track = tracks.get(0);
            for (Point point : track.getPoints()) {
                latLngs.add(new LatLng(point.getLocation().getLatitude(), point.getLocation().getLongitude()));

            }
            for (LatLng latLng : latLngs) {

                //googleMap.addMarker(new MarkerOptions().position(latLng).title(latLng.toString()));
                //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            }
        }

    }
}
