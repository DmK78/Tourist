package ru.job4j.tourist.tracking;

import android.Manifest;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.textclassifier.TextClassifierEvent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executor;

import ru.job4j.tourist.BaseActivity;
import ru.job4j.tourist.LocationService;
import ru.job4j.tourist.R;
import ru.job4j.tourist.dbutils.DBHelper;
import ru.job4j.tourist.dbutils.MainModel;
import ru.job4j.tourist.maps.MapsActivity;
import ru.job4j.tourist.model.Point;
import ru.job4j.tourist.model.Track;

import ru.job4j.tourist.tracking_history.TrackingHistoryActivity;

/**
 * @author Dmitry Kolganov (mailto:dmk78@inbox.ru)
 * @version $Id$
 * @since 15.01.2020
 */

public class TrackingActivity extends BaseActivity implements TrackingFragment.TrackerClickListener {
    final String LOG_TAG = "my_tracker";
    private boolean threadIsRunnning = false;
    private DBHelper dbHelper;
    //private MainModel mainModel;
    public static String MY_GPS = "my_gps";
    private Fragment trackerHistoryFragment;
    private FragmentManager fm;
    boolean bound = false;
    ServiceConnection sConn;
    Intent intent;
    LocationService myService;

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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mainModel = new MainModel(getApplicationContext());
        dbHelper = DBHelper.getInstance(getApplicationContext());
        intent = new Intent(this, LocationService.class);
        sConn = new ServiceConnection() {

            public void onServiceConnected(ComponentName name, IBinder binder) {
                Log.d(LOG_TAG, "MainActivity onServiceConnected");
                myService = ((LocationService.MyBinder) binder).getService();
                bound = true;
            }

            public void onServiceDisconnected(ComponentName name) {
                Log.d(LOG_TAG, "MainActivity onServiceDisconnected");
                bound = false;
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindService(intent, sConn, 0);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!bound) return;
        unbindService(sConn);
        bound = false;
    }


    @Override
    public void onHistoryFragmentClick() {
        startActivity(new Intent(this, TrackingHistoryActivity.class));


    }

    @Override
    public void onStartService() {


       startService(intent);


    }

    @Override
    public void onStopService() {

        stopService(intent);
    }


}