package ru.job4j.tourist;

import androidx.fragment.app.Fragment;

public class TrackingHistoryActivity extends BaseActivity {
    @Override
    public Fragment loadFrg() {
        return new TrackingHistoryFragment();
    }
}
