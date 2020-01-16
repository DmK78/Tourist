package ru.job4j.tourist;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MapsActivity extends BaseActivity {
    private FragmentManager fm;
    private Fragment tracksFragment;
    private Fragment placesFragment;
    private boolean threadIsRunnning = false;

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



}
