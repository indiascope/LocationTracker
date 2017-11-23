package com.locationtracker.activity;

import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.locationtracker.R;
import com.locationtracker.base.BaseActivity;
import com.locationtracker.base.BaseFragment;
import com.locationtracker.bus.MainBus;
import com.locationtracker.fragment.LocationDistanceCalculatorFragment;
import com.locationtracker.fragment.LocationFragment;
import com.locationtracker.fragment.LocationSessionRecordFragment;
import com.locationtracker.model.LatLong;
import com.locationtracker.utils.FragmentHistory;
import com.locationtracker.utils.UIUtil;
import com.locationtracker.views.FragNavController;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import pl.charmas.android.reactivelocation2.ReactiveLocationProvider;

import static com.locationtracker.utils.UnsubscribeIfPresent.dispose;

public class MainActivity extends BaseActivity implements BaseFragment.FragmentNavigation, FragNavController.TransactionListener, FragNavController.RootFragmentListener {



    private TabLayout bottomTabLayout;
    private final static int REQUEST_CHECK_SETTINGS = 0;
    private final static String TAG = "MainActivity";
    private ReactiveLocationProvider locationProvider;

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 5; // 5 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
    private static final long MIN_TIME_BW_FASTEST_UPDATES = 1000 * 60; // 1 minute




    private Disposable updatableLocationDisposable;

    private LocationRequest locationRequest;


    private FragNavController mNavController;
    private FragmentHistory fragmentHistory;

    private int[] mTabIconsSelected = {
            R.drawable.tab_home,
            R.drawable.tab_profile,
            R.drawable.tab_profile};
    private String[] TABS;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());

        locationProvider = new ReactiveLocationProvider(getApplicationContext());
        locationRequest = setLocationRequest();
        getLocationUpdatesObservable(locationRequest);

        initToolbar();
        initTab();
        setupToolbar();

        fragmentHistory = new FragmentHistory();
        mNavController = FragNavController.newBuilder(savedInstanceState, getSupportFragmentManager(), R.id.content_frame)
                .transactionListener(this)
                .rootFragmentListener(this, TABS.length)
                .build();


        switchTab(0);
        handleTabLayoutSelctedListener();
    }






    private void handleTabLayoutSelctedListener() {
        bottomTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                fragmentHistory.push(tab.getPosition());
                switchTab(tab.getPosition());


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                mNavController.clearStack();
                switchTab(tab.getPosition());


            }
        });
    }


    @NonNull
    private LocationRequest setLocationRequest() {
        return LocationRequest.create().setInterval(MIN_TIME_BW_UPDATES).
        setFastestInterval(MIN_TIME_BW_FASTEST_UPDATES)
                .setSmallestDisplacement(MIN_DISTANCE_CHANGE_FOR_UPDATES) //minimum displacement between location updates in meters
                     .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }






    /**
     *   get Continuos Location Update
     * @param locationRequest
     */
    private Observable<Location> getLocationUpdatesObservable(final LocationRequest locationRequest) {
        return locationProvider
                .checkLocationSettings(
                        new LocationSettingsRequest.Builder()
                                .addLocationRequest(locationRequest)
                                .setAlwaysShow(true)  //Refrence: http://stackoverflow.com/questions/29824408/google-play-services-locationservices-api-new-option-never
                                .build()
                )
                .doOnNext(new Consumer<LocationSettingsResult>() {
                    @Override
                    public void accept(LocationSettingsResult locationSettingsResult) {
                        Status status = locationSettingsResult.getStatus();
                        if (status.getStatusCode() == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                            try {
                                status.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException th) {
                                Log.e("MainActivity", "Error opening settings activity.", th);
                            }
                        }
                    }
                })
                .flatMap(new Function<LocationSettingsResult, Observable<Location>>() {
                    @Override
                    public Observable<Location> apply(LocationSettingsResult locationSettingsResult) {
                        return locationProvider.getUpdatedLocation(locationRequest);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());
    }






    @Override
    protected void onLocationPermissionGranted() {
        updatableLocationDisposable = getLocationUpdatesObservable(locationRequest)
//                .map(new LocationToStringFunc())
//                .map(new Function<String, String>() {
//                    int count = 0;
//                    @Override
//                    public String apply(String s) {
//                        UIUtil.log(TAG+"Updated Location "+ Helper.getTrimmedString(s + " " + count++));
//                        return s + " " + count++;
//                    }
//                })
                .subscribe(new Consumer<Location>() {
                    @Override
                    public void accept(Location location) throws Exception {
                        UIUtil.log(TAG+"Updated Location in Subscribe\n"+
                                "Latitude"+location.getLatitude()+"\n"
                              + "Longitude"+location.getLongitude()+"\n"+
                                "Location Accuracy"+location.getAccuracy());
                        int suitableMeter = 1000; // adjust your need
                        if (location.hasAccuracy()  && location.getAccuracy() <= suitableMeter) {
                            // This is your most accurate location.
                            MainBus.getInstance().publish(new LatLong(location));
                        }
                    }
                }, new ErrorHandler());








    }

    @Override
    protected void onStop() {
        super.onStop();
        dispose(updatableLocationDisposable);


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);//intent);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                //Reference: https://developers.google.com/android/reference/com/google/android/gms/location/SettingsApi
                switch (resultCode) {
                    case RESULT_OK:
                        // All required changes were successfully made
                        Log.d(TAG, "User enabled location");
                        break;
                    case RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        Log.d(TAG, "User Cancelled enabling location");
                        break;
                    default:
                        break;
                }
                break;
        }
    }


    private class ErrorHandler implements Consumer<Throwable> {
        @Override
        public void accept(Throwable throwable) {
            Toast.makeText(MainActivity.this, "Error occurred.", Toast.LENGTH_SHORT).show();
            Log.d("MainActivity", "Error occurred", throwable);
        }
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }


    private void switchTab(int position) {
        mNavController.switchTab(position);


//        updateToolbarTitle(position);
    }



    private void initToolbar() {
        setSupportActionBar(mToolbar);


    }

    private void initTab() {
        bottomTabLayout=findViewById(R.id.bottom_tab_layout);
        TABS= getResources().getStringArray(R.array.tab_name);
        if (bottomTabLayout != null) {
            for (int i = 0; i < TABS.length; i++) {
                bottomTabLayout.addTab(bottomTabLayout.newTab());
                TabLayout.Tab tab = bottomTabLayout.getTabAt(i);
                if (tab != null)
                    tab.setCustomView(getTabView(i));
            }
        }
    }


    private View getTabView(int position) {
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.tab_item_bottom, null);
        ImageView icon = (ImageView) view.findViewById(R.id.tab_icon);
        icon.setImageDrawable(UIUtil.setDrawableSelector(MainActivity.this, mTabIconsSelected[position], mTabIconsSelected[position]));
        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }


        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onBackPressed() {
        if (!mNavController.isRootFragment()) {
            mNavController.popFragment();
        } else {
            if (fragmentHistory.isEmpty()) {
                super.onBackPressed();
            } else {
                if (fragmentHistory.getStackSize() > 1) {
                    int position = fragmentHistory.popPrevious();
                    switchTab(position);
                    updateTabSelection(position);
                } else {
                    switchTab(0);
                    updateTabSelection(0);
                    fragmentHistory.emptyStack();
                }
            }

        }
    }

    private void updateTabSelection(int currentTab){
        for (int i = 0; i <  TABS.length; i++) {
            TabLayout.Tab selectedTab = bottomTabLayout.getTabAt(i);
            if(currentTab != i) {
                selectedTab.getCustomView().setSelected(false);
            }else{
                selectedTab.getCustomView().setSelected(true);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mNavController != null) {
            mNavController.onSaveInstanceState(outState);
        }
    }

    @Override
    public void pushFragment(Fragment fragment) {
        if (mNavController != null) {
            mNavController.pushFragment(fragment);
        }
    }

    @Override
    public void onTabTransaction(Fragment fragment, int index) {
        // If we have a backstack, show the back button
        if (getSupportActionBar() != null && mNavController != null) {


            updateToolbar();

        }
    }

    private void updateToolbar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(!mNavController.isRootFragment());
        getSupportActionBar().setDisplayShowHomeEnabled(!mNavController.isRootFragment());
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
    }


    @Override
    public void onFragmentTransaction(Fragment fragment, FragNavController.TransactionType transactionType) {
        //do fragmentty stuff. Maybe change title, I'm not going to tell you how to live your life
        // If we have a backstack, show the back button
        if (getSupportActionBar() != null && mNavController != null) {

            updateToolbar();

        }
    }

    @Override
    public Fragment getRootFragment(int index) {
        switch (index) {

            case FragNavController.TAB1:
                return LocationFragment.newInstance(getString(R.string.location));
            case FragNavController.TAB2:
                return LocationDistanceCalculatorFragment.newInstance(getString(R.string.travel));
            case FragNavController.TAB3:
                return LocationSessionRecordFragment.newInstance(getString(R.string.session));


        }
        throw new IllegalStateException("Need to send an index that we know");
    }


//    private void updateToolbarTitle(int position){
//
//
//        getSupportActionBar().setTitle(TABS[position]);
//
//    }


    public void updateToolbarTitle(String title) {
        getSupportActionBar().setTitle(title);

    }


}


