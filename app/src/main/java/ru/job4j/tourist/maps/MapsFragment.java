package ru.job4j.tourist.maps;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
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

import java.util.Arrays;

import ru.job4j.tourist.R;
import ru.job4j.tourist.dbutils.DBHelper;

/**
 * @author Dmitry Kolganov (mailto:dmk78@inbox.ru)
 * @since 15.01.2020
 * @version $Id$
 */

public class MapsFragment extends Fragment implements OnMapReadyCallback, MapsContract.MapsViewInterface  {

    private GoogleMap googleMap;
    private Location mLocation;
    private static final int REQUEST_PERMISSION_LOCATION = 255; // int should be between 0 and 255
    private Button buttonShowHistory, buttonSaveLoc, buttonClearHistory, buttonCurrentLoc;
    public final static String TAG = "Tourist";
    private MapsContract.MapsPresenterInterface presenter;
    private OnMapsFrgClickListener callback;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        callback = (OnMapsFrgClickListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_maps, container, false);
        presenter = new MapsPresenter(getContext(), this, DBHelper.getInstance(getContext()));
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        buttonCurrentLoc = view.findViewById(R.id.buttonCurrentLoc);
        buttonSaveLoc = view.findViewById(R.id.buttonSaveLoc1);
        buttonClearHistory = view.findViewById(R.id.buttonClearHistory);
        buttonShowHistory = view.findViewById(R.id.buttonViewHistory);
        refreshBtnSaveCount(presenter.getFavPointsCount());
        buttonCurrentLoc.setOnClickListener(view1 -> presenter.onIMHereClicked(mLocation));
        buttonSaveLoc.setOnClickListener(view12 -> presenter.onSaveLocationClicked());
        buttonShowHistory.setOnClickListener(this::viewHistory);
        buttonClearHistory.setOnClickListener(this::clearHistory);

        Places.initialize(getContext(), getString(R.string.google_maps_key));

        AutocompleteSupportFragment search = (AutocompleteSupportFragment)
                this.getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        search.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
        search.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                presenter.onPlaceSelectedClicked(place);
            }

            @Override
            public void onError(Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        return view;
    }


    private void clearHistory(View view) {
        if (presenter.getFavPointsCount()!=0) {
            new AlertDialog.Builder(getContext())
                    .setTitle("Confirmation")
                    .setMessage("Do you really want to clear history?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            presenter.onClearHistoryClicked();
                            googleMap.clear();
                        }
                    })
                    .setNegativeButton(android.R.string.no, null).show();
        }
    }

    private void viewHistory(View view) {
        callback.onShowHistoryClicked();
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




    @Override
    public void refreshBtnSaveCount(int i) {
        buttonSaveLoc.setText("Save location (" + i + ")");
    }

    @Override
    public void focusCamers(Location location) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));
    }

    @Override
    public void setMarker(Location location) {
        MarkerOptions marker = new MarkerOptions().position(new LatLng(mLocation.getLatitude(), mLocation.getLongitude())).title("Hello Maps");
        marker.flat(true);
        googleMap.addMarker(marker);
        focusCamers(location);
    }



    public interface OnMapsFrgClickListener {
        void onShowHistoryClicked();
    }
}
