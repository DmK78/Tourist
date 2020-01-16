package ru.job4j.tourist;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

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

import ru.job4j.tourist.dbutils.DBHelper;
import ru.job4j.tourist.model.Point;

public class TrackingFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap googleMap;
    private Location mLocation;
    private static final int REQUEST_PERMISSION_LOCATION = 255; // int should be between 0 and 255
    private DBHelper dbHelper;
    private Button buttonStart, buttonStop;
    private ImageButton buttonGetLoc;
    private Point lastLocation = new Point();
    public final static String TAG = "Tourist";
    private boolean threadIsRunnning = false;
    private TrackerActionsListener callback;
    private TextView textViewCount;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_tracker, container, false);
        dbHelper = DBHelper.getInstance(getContext());
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        buttonStart = view.findViewById(R.id.butTrackerStart);
        buttonStop = view.findViewById(R.id.butTrackerStop);
        buttonGetLoc = view.findViewById(R.id.butTrackerGetLoc);
        //textViewCount=view.findViewById(R.id.tvTrackerCount);
        buttonGetLoc.setOnClickListener(this::getCurrentLocation);
        buttonStart.setOnClickListener(this::startTracker);
        buttonStop.setOnClickListener(this::stopTracker);


        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        callback = (TrackerActionsListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        UiSettings uiSettings = googleMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);

        if (mLocation != null) {
            LatLng sydney = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
            MarkerOptions marker = new MarkerOptions().position(sydney).title("Hello Maps");
            marker.flat(true);
            googleMap.addMarker(marker);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15));
        }

        LocationListener loc = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mLocation = location;
                //Toast.makeText(getContext(), "Location was changed!", Toast.LENGTH_SHORT).show();
                Point point = new Point();
                point.setLocation(location);
                dbHelper.addLocation(point);
                if (lastLocation.getLocation() != null) {
                    Polyline line = googleMap.addPolyline(new PolylineOptions()
                            .add(
                                    new LatLng(lastLocation.getLocation().getLatitude(), lastLocation.getLocation().getLongitude()),
                                    new LatLng(point.getLocation().getLatitude(), point.getLocation().getLongitude())));
                    line.setWidth(3);
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()), 16));
                    lastLocation.setLocation(location);


                } else {
                    lastLocation = point;
                }


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


    private void startTracker(View view) {
        callback.startTracker();
    }

    private void stopTracker(View view) {
        callback.stopTracker();

    }

    public interface TrackerActionsListener {
        void startTracker();
        void stopTracker();
    }
    public interface TrackerClickListenet{
        void onHistoryFragmentClick();
    }
}
