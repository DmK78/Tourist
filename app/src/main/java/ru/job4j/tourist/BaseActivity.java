package ru.job4j.tourist;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

/**
 * @author Dmitry Kolganov (mailto:dmk78@inbox.ru)
 * @since 15.01.2020
 * @version $Id$
 */

public abstract class BaseActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);setContentView(R.layout.main_host);

        FragmentManager fm = getSupportFragmentManager();
        if (fm.findFragmentById(R.id.mainContainer) == null) {
            fm.beginTransaction()
                    .add(R.id.mainContainer, loadFrg())
                    .commit();
        }
    }


    public abstract Fragment loadFrg();
}
