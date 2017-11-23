package com.locationtracker.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationSettingsStates;
import com.locationtracker.R;
import com.locationtracker.base.BaseFragment;
import com.locationtracker.bus.MainBus;
import com.locationtracker.model.LatLong;
import com.locationtracker.utils.AddressToStringFunc;
import com.locationtracker.utils.Helper;
import com.locationtracker.utils.LocationToStringFunc;
import com.locationtracker.utils.UIUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import pl.charmas.android.reactivelocation2.ReactiveLocationProvider;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.locationtracker.utils.UnsubscribeIfPresent.dispose;

/**
 * Created by LENOVO on 22-11-2017.
 */

public class LocationFragment extends BaseFragment {


    private final static int REQUEST_CHECK_SETTINGS = 0;
    private Disposable lastKnownLocationDisposable;

    private final static String TAG = "LocationFragment";
    private ReactiveLocationProvider locationProvider;

    private TextView tvCurrentAddress;
    private boolean firstTimeFetchLatLong;



    public static LocationFragment newInstance(String item) {
        LocationFragment fragment = new LocationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ITEM, item);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        item = getArguments().getString(ARG_ITEM);
        locationProvider = new ReactiveLocationProvider(getActivity());


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(getLayout(),container,false);
        tvCurrentAddress =(TextView) view.findViewById(R.id.tvLatLong);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onPause() {
        super.onPause();
        dispose(lastKnownLocationDisposable);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(getActivity()==null && !isAdded()) {
            return;
        }
       if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            reqestLocationPermission();
            return;
        }
        onLocationPermissionGranted();
    }




    private void reqestLocationPermission() {
        new RxPermissions(getActivity())
                .request(Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe(new io.reactivex.functions.Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean granted) {
                        if (granted) {
                            onLocationPermissionGranted();
                        } else {
                            Toast.makeText(getActivity(), "Sorry, no App can run without permission...", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }


    protected void onLocationPermissionGranted() {
        lastKnownLocationDisposable = getLastKnownLocationObservable()
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        UIUtil.log(TAG+"LastKnown Location"+ Helper.getTrimmedString(s));
                        tvCurrentAddress.setText(Helper.getTrimmedString(s));



                    }
                }, new ErrorHandler());







    }




    private class ErrorHandler implements Consumer<Throwable> {
        @Override
        public void accept(Throwable throwable) {
            Toast.makeText(getActivity(), "Error occurred.", Toast.LENGTH_SHORT).show();
            Log.d("MainActivity", "Error occurred", throwable);
        }
    }



    /**
     *  get Last known Location of User
     */
    private Observable<String> getLastKnownLocationObservable() {
        return locationProvider
                .getLastKnownLocation()
                .flatMap(new Function<Location, Observable<List<Address>>>() {
                    @Override
                    public Observable<List<Address>> apply(Location location) {
                        if(!firstTimeFetchLatLong){
                            MainBus.getInstance().publish(new LatLong(location));
                        }
                        firstTimeFetchLatLong=true;
                        return locationProvider.getReverseGeocodeObservable(location.getLatitude(), location.getLongitude(), 1);
                    }
                })
                .map(new Function<List<Address>, Address>() {
                    @Override
                    public Address apply(List<Address> addresses) {
                        return addresses != null && !addresses.isEmpty() ? addresses.get(0) : null;
                    }
                })
                .map(new AddressToStringFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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

    @Override
    protected int getLayout() {
        return R.layout.fragment_location;
    }

    @Override
    protected int getTitle() {
        return R.string.location;
    }
}
