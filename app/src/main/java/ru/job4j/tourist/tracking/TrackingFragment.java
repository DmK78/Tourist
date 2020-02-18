package ru.job4j.tourist.tracking;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
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

import ru.job4j.tourist.R;
import ru.job4j.tourist.dbutils.DBHelper;
import ru.job4j.tourist.model.Point;

public class TrackingFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap googleMap;
    private Location mLocation;
    private static final int REQUEST_PERMISSION_LOCATION = 255; // int should be between 0 and 255
    private DBHelper dbHelper;
    private Button buttonStart, buttonStop, buttonHistory;

    private Point lastLocation = new Point();
    public final static String TAG = "Tourist";
    private boolean threadIsRunnning = false;

    private TextView textViewCount;
    private TrackerClickListener clickListener;
    public final static String BROADCAST_ACTION = "ru.job4j.tourist.servicebackbroadcast";
    private BroadcastReceiver br;
    public final static String PARAM_COUNT = "count";
    private static TrackingFragment trackingFragmentRunningInstance;
    public final static String FILE_NAME = "filename";

    /**
     * @author Dmitry Kolganov (mailto:dmk78@inbox.ru)
     * @version $Id$
     * @since 15.01.2020
     */

    public static TrackingFragment getInstance() {
        return trackingFragmentRunningInstance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_tracker, container, false);
        trackingFragmentRunningInstance = this;
        textViewCount = view.findViewById(R.id.textViewCountPoint);
        MyReceiver myReceiver = new MyReceiver(new Handler());



       /* br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Toast.makeText(context, "received", Toast.LENGTH_SHORT).show();
                int count = intent.getIntExtra(PARAM_COUNT, 0);

                Log.i(TAG, ""+count);
                //textViewCount.setText(count);





            }
        };*/
        IntentFilter intFilt = new IntentFilter(BROADCAST_ACTION);
        // регистрируем (включаем) BroadcastReceiver
        getActivity().registerReceiver(myReceiver, intFilt);

        dbHelper = DBHelper.getInstance(getContext());
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        buttonStart = view.findViewById(R.id.btnTrackerStart);
        buttonStop = view.findViewById(R.id.btnTrackerStop);

        buttonHistory = view.findViewById(R.id.btnTrackerHistory);


        buttonStart.setOnClickListener(this::startTracker);
        buttonStop.setOnClickListener(this::stopTracker);
        buttonHistory.setOnClickListener(this::viewTracksHistory);


        return view;
    }

    private void viewTracksHistory(View view) {
        clickListener.onHistoryFragmentClick();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        clickListener = (TrackerClickListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        clickListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(br);
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
                //dbHelper.addLocation(point);
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
        clickListener.onStartService();


        //callback.startTracker();
    }

    private void stopTracker(View view) {
        //callback.stopTracker();
        clickListener.onStopService();

    }

    public void updateCount(int i) {
        textViewCount.setText(String.valueOf(i));
    }




    public interface TrackerClickListener {
        void onHistoryFragmentClick();

        void onStartService();

        void onStopService();
    }

    public static class MyReceiver extends BroadcastReceiver {

        private final Handler handler; // Handler used to execute code on the UI thread

        public MyReceiver(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Toast from broadcast receiver", Toast.LENGTH_SHORT).show();
                    int count = intent.getIntExtra(PARAM_COUNT, 0);
                    if(TrackingFragment.getInstance()!=null)
                        TrackingFragment.getInstance().updateCount(count);

                    Log.i(TAG, "" + count);
                    //textViewCount.setText(count);
                    Toast.makeText(context, "Toast from broadcast receiver", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}