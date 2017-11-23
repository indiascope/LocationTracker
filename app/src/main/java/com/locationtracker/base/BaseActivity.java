package com.locationtracker.base;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.locationtracker.R;
import com.locationtracker.utils.UIUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;

/**
 * Created by LENOVO on 23-11-2017.
 */

public abstract class BaseActivity extends AppCompatActivity {

    protected Toolbar mToolbar;
    public TextView toolBarTitle;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);

    }



    protected abstract int getLayoutId();


    @Override protected void onStart() {
        super.onStart();
        reqestLocationPermission();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            onActive();
        }
    }

    private void reqestLocationPermission() {
        new RxPermissions(this)
                .request(Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe(new io.reactivex.functions.Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean granted) {
                        if (granted) {
                            onLocationPermissionGranted();
                        } else {
                            Toast.makeText(BaseActivity.this, "Sorry, no App can run without permission...", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    protected abstract void onLocationPermissionGranted();



    @Override protected void onStop() {
        super.onStop();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            onInactive();
        }
    }

    @Override protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            onActive();
        }
    }

    @Override protected void onPause() {
        super.onPause();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            onInactive();
        }
    }

    public void initToolbar(Toolbar toolbar, boolean isBackEnabled) {
        setSupportActionBar(toolbar);

        if(isBackEnabled) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);

        }
    }

    public void initToolbar(Toolbar toolbar, String title, boolean isBackEnabled) {

        setSupportActionBar(toolbar);

        if(isBackEnabled) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);

        }

        getSupportActionBar().setTitle(title);



    }


    protected void setupToolbar() {
        mToolbar = findViewById(R.id.toolbar);
        if(mToolbar == null) {
            UIUtil.log("Didn't find a toolbar");
            return;
        }
        toolBarTitle= (TextView) mToolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar == null) return;

        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    public void setToolbarTitle(String title) {
        toolBarTitle.setText(title);
    }


    protected void onActive() {
    }

    protected void onInactive() {
    }



}
