package ru.job4j.tourist.maps_history;

import androidx.fragment.app.Fragment;

import ru.job4j.tourist.BaseActivity;

/**
 * @author Dmitry Kolganov (mailto:dmk78@inbox.ru)
 * @since 15.01.2020
 * @version $Id$
 */

public class MapsHistoryActivity extends BaseActivity {


    @Override
    public Fragment loadFrg() {
        return new MapsHistoryFragment();
    }
}
