package ru.job4j.tourist;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;

import ru.job4j.tourist.dbutils.MainModel;
import ru.job4j.tourist.model.Track;

public class MapsActivity extends BaseActivity {
    private FragmentManager fm;
    private Fragment tracksFragment;
    private Fragment placesFragment;
    private boolean threadIsRunnning = false;
    private MainModel mainModel;

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
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

    }
}
