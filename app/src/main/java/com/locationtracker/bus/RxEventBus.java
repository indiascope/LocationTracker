package com.locationtracker.bus;

import android.support.annotation.NonNull;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;

public class RxEventBus {
    private PublishSubject<Object> sSubject = PublishSubject.create();


    public RxEventBus() {
        // hidden constructor
    }


    public  Disposable subscribe(@NonNull Consumer<Object> action) {
        return sSubject.subscribe(action);
    }

    public  void publish(@NonNull Object message) {
        sSubject.onNext(message);
    }

    public Observable<Object> toObservable() {
        return sSubject ;
    }

    public boolean hasObservers() {
        return sSubject.hasObservers();
    }



}