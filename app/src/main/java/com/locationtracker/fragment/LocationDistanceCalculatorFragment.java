package com.locationtracker.fragment;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.locationtracker.R;
import com.locationtracker.base.BaseFragment;
import com.locationtracker.bus.MainBus;
import com.locationtracker.model.LatLong;
import com.locationtracker.utils.UIUtil;

import java.text.DecimalFormat;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Created by LENOVO on 22-11-2017.
 */

public class LocationDistanceCalculatorFragment extends BaseFragment {


    private Location startLocation;
    private Location updatedLocation;

    private final static String TAG = "LocationDistanceCalculatorFragment";
    private final CompositeDisposable disposables = new CompositeDisposable();

    private TextView tvTravelDistanceInMeters;

    private boolean firstTimeFetchLatLong;



    public static LocationDistanceCalculatorFragment newInstance(String item) {
        LocationDistanceCalculatorFragment fragment = new LocationDistanceCalculatorFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ITEM, item);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayout(), container, false);
        tvTravelDistanceInMeters = view.findViewById(R.id.tvTravelDistanceInMeter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Subscribe to EventBus
        disposables.add(MainBus.getInstance().toObservable()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object object) throws Exception {
                        if (object instanceof LatLong) {
                            LatLong latLong = (LatLong) object;
                            if(!firstTimeFetchLatLong){
                                firstTimeFetchLatLong=true;
                                startLocation = latLong.getLocation();
                            } else {
                                updatedLocation = latLong.getLocation();
                            }
                                getDistanceBetween2LatLong(startLocation, updatedLocation).subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Consumer<Double>() {
                                        @Override
                                        public void accept(Double distanceInMeters) throws Exception {
                                            if (distanceInMeters != null) {
                                                DecimalFormat decimalFormat=new DecimalFormat("0.##");
                                                tvTravelDistanceInMeters.setText(String.format("%s", decimalFormat.format(distanceInMeters)));
                                            }

                                        }
                                    }, new Consumer<Throwable>() {
                                        @Override
                                        public void accept(Throwable throwable) throws Exception {

                                        }
                                    });
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        UIUtil.log(TAG);

                    }
                }));

    }

    private Observable<Double> getDistanceBetween2LatLong(Location startLocation, Location updatedLocation) {
        return Observable.just(UIUtil.distanceBetweenToLatLong(startLocation, updatedLocation));
    }


    @Override
    public void onPause() {
        super.onPause();
        disposables.clear();// do not send event after fragment has been destroyed
    }


    @Override
    protected int getLayout() {
        return R.layout.fragment_location_distance_calcuale;
    }

    @Override
    protected int getTitle() {
        return R.string.travel;
    }
}
