package ru.job4j.tourist;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private Location mLocation;
    private static final int REQUEST_PERMISSION_LOCATION = 255; // int should be between 0 and 255
    private DBHelper dbHelper;
    private Button buttonShowHistory, buttonSaveLoc, buttonClearHistory, buttonCurrentLoc;
    private Loc lastLocation = new Loc();
    public final static String TAG = "Tourist";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        dbHelper = DBHelper.getInstance(getApplicationContext());
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        buttonCurrentLoc = findViewById(R.id.buttonCurrentLoc);
        buttonSaveLoc = findViewById(R.id.buttonSaveLoc);
        buttonClearHistory = findViewById(R.id.buttonClearHistory);
        buttonShowHistory = findViewById(R.id.buttonViewHistory);
        buttonSaveLoc.setText("Save location (" + dbHelper.getAllLocations().size() + ")");
        buttonCurrentLoc.setOnClickListener(this::getCurrentLocation);
        buttonSaveLoc.setOnClickListener(this::saveCurrentLocation);
        buttonShowHistory.setOnClickListener(this::viewHistory);
        buttonClearHistory.setOnClickListener(this::clearHistory);

        Places.initialize(this, getString(R.string.google_maps_key));
        AutocompleteSupportFragment search = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        search.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
        search.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                LatLng pos = place.getLatLng();

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

    }

    private void clearHistory(View view) {
        dbHelper.deleteAllLocation();
        buttonSaveLoc.setText("Save location");
    }

    private void viewHistory(View view) {
        startActivity(new Intent(getApplicationContext(),HistoryActivity.class));
    }

    private void saveCurrentLocation(View view) {
        //округляет до 3-х знаков после запятой, устанавливая чувствительность, чтобы записывать только отдаленные точки
        //проверяет, не является ли точка слишком близко к предыдущей, если нет, то записывает
        if (Math.round(mLocation.getLatitude() * 1000d) / 1000d !=
                Math.round(lastLocation.getLatitude() * 1000d) / 1000d &&
                Math.round(mLocation.getLongitude() * 1000d) / 1000d !=
                        Math.round(lastLocation.getLongitude() * 1000d) / 1000d) {
            Loc loc = new Loc();
            loc.setLatitude(mLocation.getLatitude());
            loc.setLongitude(mLocation.getLongitude());
            loc.setName("" + loc.getLatitude() + ", " + loc.getLongitude());
            dbHelper.addLocation(loc);
            buttonSaveLoc.setText("Save location (" + dbHelper.getAllLocations().size() + ")");
            lastLocation = loc;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        LocationListener loc = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mLocation = location;
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
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.
                PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
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
