package ru.job4j.tourist;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.job4j.tourist.dbutils.DBHelper;
import ru.job4j.tourist.model.Point;
import ru.job4j.tourist.model.Track;
import ru.job4j.tourist.tracking.TrackingActivity;
import ru.job4j.tourist.tracking.TrackingFragment;

public class LocationService extends Service {
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private MyBinder binder = new MyBinder();
    private DBHelper dbHelper;
    private Location lastLocation;
    public static String TAG = "my_tracker";
    private Track currentTrack;

    NotificationManager nm;



    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());




        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        dbHelper = DBHelper.getInstance(this);
        Date currentTime = Calendar.getInstance().getTime();
        currentTrack = dbHelper.addTrack(new Track(currentTime.toString(), new ArrayList<>()));
        //dbHelper.addTrack(currentTrack);
        Log.i(TAG, "Track added ");
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Log.i(TAG, locationResult.getLastLocation().getLatitude() + ", " + locationResult.getLastLocation().getLongitude());
                if (locationResult.getLastLocation() != lastLocation && locationResult.getLastLocation().getLatitude() != 0 && locationResult.getLastLocation().getLongitude() != 0) {
                    dbHelper.addLocation(new Point("", locationResult.getLastLocation(), currentTrack.getId()));
                    currentTrack = dbHelper.getTrack(currentTrack.getId());
                    lastLocation = locationResult.getLastLocation();
                    Intent intent = new Intent(TrackingFragment.BROADCAST_ACTION);
                    if (currentTrack != null && currentTrack.getPoints().size() > 0) {
                        int count = dbHelper.getTrack(currentTrack.getId()).getPoints().size();

                        intent.putExtra(TrackingFragment.PARAM_COUNT, count);
                        sendBroadcast(intent);
                    }
                    Log.i(TAG, "Added " + locationResult.getLastLocation().getLatitude() + " " + locationResult.getLastLocation().getLongitude());
                } else {
                    Log.i(TAG, "tick");
                }
            }
        };
    }

    @Override
    public void onRebind(Intent intent) {
        Log.i(TAG, "onRebind");
        super.onRebind(intent);
    }



    private void startMyOwnForeground(){
        String NOTIFICATION_CHANNEL_ID = "com.example.simpleapp";
        String channelName = "My Background Service";
        NotificationChannel chan = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            chan.setLightColor(Color.BLUE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        }
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(chan);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent arg0) {
        Log.d(TAG, "onBind");

        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "onUnbind");
        return super.onUnbind(intent);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Tracker started");
        requestLocation();

        //return super.onStartCommand(intent, flags, startId);
        return super.onStartCommand(intent, flags, startId);
    }



    private void requestLocation() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(15000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }

    public int getPointsCount() {
        return dbHelper.getTrack(currentTrack.getId()).getPoints().size();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        super.onDestroy();
    }


    public class MyBinder extends Binder {
        public LocationService getService() {
            return LocationService.this;
        }
    }


}
