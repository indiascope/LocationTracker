package com.locationtracker.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.locationtracker.R;
import com.locationtracker.base.BaseFragment;

/**
 * Created by LENOVO on 22-11-2017.
 */

public class LocationSessionRecordFragment extends BaseFragment {

    public static LocationSessionRecordFragment newInstance(String item) {
        LocationSessionRecordFragment fragment = new LocationSessionRecordFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ITEM, item);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_location_session_record,container,false);
        return view;
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_location_session_record;
    }

    @Override
    protected int getTitle() {
        return R.string.session;
    }
}
