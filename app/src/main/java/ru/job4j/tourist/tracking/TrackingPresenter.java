package ru.job4j.tourist.tracking;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ru.job4j.tourist.LocationService;
import ru.job4j.tourist.model.Point;
import ru.job4j.tourist.model.Track;

import static ru.job4j.tourist.tracking.TrackingActivity.MY_GPS;


public class TrackingPresenter implements TrackingContract.presenter {
    private TrackingContract.model model;
    private TrackingContract.view view;
    private Context context;
    private LocationManager locationManager;

    private Object LocationManager;
    private boolean threadIsRunnning = false;

    public TrackingPresenter(TrackingContract.model model, TrackingContract.view view, Context context) {
        this.model = model;
        this.view = view;
        this.context = context;
    }

    @Override
    public void onStartClicked() {
        threadIsRunnning = true;
        Tracker tracker = new Tracker();
        view.showToast("Tracker is started");


        new Thread(tracker).start();

    }

    @Override
    public void onStopClicked() {
        threadIsRunnning = false;
        view.showToast("Tracker is stopped");



    }

    class Tracker implements Runnable {
        Location[] lastLocation = {new Location("")};
        Location[] curLocation = {new Location("")};
        //DBHelper dbHelper = DBHelper.getInstance(getApplicationContext());
        // MainModel mainModel = new MainModel(getApplicationContext());
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
            Date currentTime = Calendar.getInstance().getTime();
            Track track = model.addTrack(new Track(currentTime.toString(), new ArrayList<>()));
            locationManager = (android.location.LocationManager) context.getSystemService(Context.LOCATION_SERVICE);



            while (threadIsRunnning) {
                double accuracy = 1000d;
                if (curLocation[0] != lastLocation[0] && curLocation[0].getLatitude()!=0 && curLocation[0].getLongitude()!=0) {
                    model.addPoint(new Point("", curLocation[0], (int) track.getId()));
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
