package ru.job4j.tourist;

import androidx.fragment.app.Fragment;

/**
 * @author Dmitry Kolganov (mailto:dmk78@inbox.ru)
 * @since 15.01.2020
 * @version $Id$
 */

public class HistoryActivity extends BaseActivity  {


    @Override
    public Fragment loadFrg() {
        return new HistoryFragment();
    }
}
