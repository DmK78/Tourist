package ru.job4j.tourist.tracking_history;

import androidx.fragment.app.Fragment;

import ru.job4j.tourist.BaseActivity;
import ru.job4j.tourist.tracking_history.TrackingHistoryFragment;


/**
 * @author Dmitry Kolganov (mailto:dmk78@inbox.ru)
 * @since 15.01.2020
 * @version $Id$
 */

public class TrackingHistoryActivity extends BaseActivity {
    @Override
    public Fragment loadFrg() {
        return new TrackingHistoryFragment();
    }
}
