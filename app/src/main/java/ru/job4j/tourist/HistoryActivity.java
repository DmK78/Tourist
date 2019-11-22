package ru.job4j.tourist;

import androidx.fragment.app.FragmentActivity;

import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DBHelper dbHelper;
    private ListView listView;
    private Location mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        dbHelper=DBHelper.getInstance(getApplicationContext());
        listView=findViewById(R.id.listView);
        ArrayAdapter<Loc> arrayAdapter = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_list_item_1,dbHelper.getAllLocations());
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Loc loc = (Loc)adapterView.getAdapter().getItem(i);
                Toast.makeText(HistoryActivity.this, ""+loc.getName(), Toast.LENGTH_SHORT).show();
                LatLng curLoc = new LatLng(loc.getLatitude(), loc.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curLoc, 15));

            }
        });
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        List<LatLng> latLngs = new ArrayList<>();
        List<Loc> locs = dbHelper.getAllLocations();
        for(Loc loc:locs){
            latLngs.add(new LatLng(loc.getLatitude(),loc.getLongitude()));

        }
        for(LatLng latLng:latLngs){

            googleMap.addMarker(new MarkerOptions().position(latLng).title(latLng.toString()));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));

        }

    }
}
