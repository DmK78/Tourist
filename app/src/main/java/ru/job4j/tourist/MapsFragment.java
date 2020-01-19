package ru.job4j.tourist;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.job4j.tourist.dbutils.DBHelper;
import ru.job4j.tourist.dbutils.MainModel;
import ru.job4j.tourist.model.Point;
import ru.job4j.tourist.model.Track;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private Location mLocation;
    private static final int REQUEST_PERMISSION_LOCATION = 255; // int should be between 0 and 255
    private DBHelper dbHelper;
    private Button buttonShowHistory, buttonSaveLoc, buttonClearHistory, buttonCurrentLoc;
    private Point lastLocation = new Point();
    public final static String TAG = "Tourist";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_maps, container, false);
        dbHelper = DBHelper.getInstance(getContext());
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

/*        MainModel mainModel = new MainModel(getContext());
        mainModel.deleteAllTracks();
        List<Point> points = new ArrayList<>();
        *//*points.add(new Point("1", new Location("1")));
        points.add(new Point("2", new Location("2")));*//*

        Track track = new Track("aa", points);
        long l = mainModel.insertTrack(track);
        Point point = new Point("1", new Location("1"),(int)l);
        Point point1 = new Point("2", new Location("2"),(int)l);
        mainModel.insertPoint(point);
        mainModel.insertPoint(point1);
        List<Track> tracks = mainModel.getAllTracks();*/

        buttonCurrentLoc = view.findViewById(R.id.buttonCurrentLoc);
        buttonSaveLoc = view.findViewById(R.id.buttonSaveLoc1);
        buttonClearHistory = view.findViewById(R.id.buttonClearHistory);
        buttonShowHistory = view.findViewById(R.id.buttonViewHistory);
        buttonSaveLoc.setText("Save location (" + dbHelper.getAllPoints().size() + ")");
        buttonCurrentLoc.setOnClickListener(this::getCurrentLocation);
        buttonSaveLoc.setOnClickListener(this::saveCurrentLocation);
        buttonShowHistory.setOnClickListener(this::viewHistory);
        buttonClearHistory.setOnClickListener(this::clearHistory);

        Places.initialize(getContext(), getString(R.string.google_maps_key));

        AutocompleteSupportFragment search = (AutocompleteSupportFragment)
                this.getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        search.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
        search.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                LatLng pos = place.getLatLng();
                mLocation = new Location("");
                mLocation.setLatitude(place.getLatLng().latitude);
                mLocation.setLongitude(place.getLatLng().longitude);
                MarkerOptions marker = new MarkerOptions().position(pos).title("Hello Maps");
                googleMap.addMarker(marker);
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 15));
            }

            @Override
            public void onError(Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        return view;
    }


    private void clearHistory(View view) {
        if (!dbHelper.getAllPoints().isEmpty()) {
            new AlertDialog.Builder(getContext())
                    .setTitle("Confirmation")
                    .setMessage("Do you really want to clear history?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {

                            dbHelper.deleteAllLocation();
                            buttonSaveLoc.setText("Save location");
                            googleMap.clear();
                        }
                    })
                    .setNegativeButton(android.R.string.no, null).show();


        }


    }

    private void viewHistory(View view) {
        startActivity(new Intent(getContext(), HistoryActivity.class));
    }

    private void saveCurrentLocation(View view) {
        //округляет до 3-х знаков после запятой, устанавливая чувствительность, чтобы записывать только отдаленные точки
        //проверяет, не является ли точка слишком близко к предыдущей, если нет, то записывает
        /*if (Math.round(mLocation.getLatitude() * 1000d) / 1000d !=
                Math.round(lastLocation.getLocation().getLatitude() * 1000d) / 1000d &&
                Math.round(mLocation.getLongitude() * 1000d) / 1000d !=
                        Math.round(lastLocation.getLocation().getLongitude() * 1000d) / 1000d) {*/
        Point point = new Point();
        Location location = new Location("");
        location.setLatitude(mLocation.getLatitude());
        location.setLongitude(mLocation.getLongitude());
        point.setLocation(location);
        point.setName("" + point.getLocation().getLatitude() + ", " + point.getLocation().getLongitude());
        dbHelper.addLocation(point);
        buttonSaveLoc.setText("Save location (" + dbHelper.getAllPoints().size() + ")");
        lastLocation = point;
        MarkerOptions marker = new MarkerOptions().position(new LatLng(mLocation.getLatitude(), mLocation.getLongitude())).title("Hello Maps");
        marker.flat(true);
        googleMap.addMarker(marker);





        /*}*/
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        UiSettings uiSettings = googleMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);

        LocationListener loc = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mLocation = location;
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));
            }


            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.
                PERMISSION_GRANTED && getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_LOCATION);
        } else {
            // We have already permission to use the location
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, loc);
        }

    }

    public void getCurrentLocation(View view) {
        if (mLocation != null) {
            LatLng sydney = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
            MarkerOptions marker = new MarkerOptions().position(sydney).title("Hello Maps");
            marker.flat(true);
            googleMap.addMarker(marker);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15));
        }
    }
}
