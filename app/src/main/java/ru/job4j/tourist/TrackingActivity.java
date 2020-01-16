package ru.job4j.tourist;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import ru.job4j.tourist.dbutils.DBHelper;
import ru.job4j.tourist.model.Point;
import ru.job4j.tourist.model.Track;

public class TrackingActivity extends BaseActivity implements TrackingFragment.TrackerActionsListener, TrackingFragment.TrackerClickListenet {
    private boolean threadIsRunnning = false;
    private DBHelper dbHelper;
    public static String MY_GPS = "my_gps";

    @Override
    public Fragment loadFrg() {
        return new TrackingFragment();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.places:
                startActivity(new Intent(this, MapsActivity.class));
                return true;
            case R.id.tracks:

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void startTracker() {
        threadIsRunnning = true;
        Tracker tracker = new Tracker();
        new Thread(tracker).start();
    }

    @Override
    public void stopTracker() {
        threadIsRunnning = false;
        Toast.makeText(getApplicationContext(), "Tracker is stopped", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onHistoryFragmentClick() {
        if (secondFragment == null) {
            secondFragment = new SecondFragment();
        }
        secondFragment.setArguments(bundle);
        fm.beginTransaction()
                .replace(R.id.fragment_container, secondFragment)
                .addToBackStack(null)
                .commit();




    }

    class Tracker implements Runnable {
        Location[] lastLocation = {new Location("")};
        Location[] curLocation = {new Location("")};
        DBHelper dbHelper = DBHelper.getInstance(getApplicationContext());
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                curLocation[0] = location;

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

        @Override
        public void run() {
            Track track = new Track("", new ArrayList<>());

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

                    if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    Activity#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for Activity#requestPermissions for more details.
                        return;
                    }
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, locationListener);


                }
            });


            while (threadIsRunnning) {
                double accuracy = 1000d;

                /*if (Math.round(curLocation[0].getLatitude() * accuracy) / accuracy !=
                        Math.round(lastLocation[0].getLatitude() * accuracy) / accuracy &&
                        Math.round(curLocation[0].getLongitude() * accuracy) / accuracy !=
                                Math.round(lastLocation[0].getLongitude() * accuracy) / accuracy) {*/
                if (curLocation[0] != lastLocation[0]) {


                    //dbHelper.addLocation(new Point("", curLocation[0]));
                    track.getPoints().add(new Point("", curLocation[0]));
                    lastLocation[0] = curLocation[0];

                    Log.i(MY_GPS, "Added " + curLocation[0].getLatitude() + " " + curLocation[0].getLongitude());
                } else {
                    //Log.i(MY_GPS,"Last "+lastLocation[0].getLatitude()+","+lastLocation[0].getLongitude()+" - current "+curLocation[0].getLatitude()+","+curLocation[0].getLongitude()+"lat "+latMod+ " lng "+lngMod);
                    Log.i(MY_GPS, "tick");
                }

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

        }
    }
}