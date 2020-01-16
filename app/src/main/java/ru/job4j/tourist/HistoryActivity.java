package ru.job4j.tourist;

import androidx.fragment.app.Fragment;

public class HistoryActivity extends BaseActivity  {


    @Override
    public Fragment loadFrg() {
        return new HistoryFragment();
    }
}
