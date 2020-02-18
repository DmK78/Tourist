package ru.job4j.tourist.maps;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import ru.job4j.tourist.BaseActivity;

import ru.job4j.tourist.maps_history.MapsHistoryActivity;
import ru.job4j.tourist.R;
import ru.job4j.tourist.tracking.TrackingActivity;


/**
 * @author Dmitry Kolganov (mailto:dmk78@inbox.ru)
 * @since 15.01.2020
 * @version $Id$
 */

public class MapsActivity extends BaseActivity implements MapsFragment.OnMapsFrgClickListener {
    private FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fm = getSupportFragmentManager();

    }

    @Override
    public Fragment loadFrg() {
        return new MapsFragment();
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
                return true;
            case R.id.tracks:
                startActivity(new Intent(this, TrackingActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onShowHistoryClicked() {
        startActivity(new Intent(getApplicationContext(), MapsHistoryActivity.class));
    }
}
