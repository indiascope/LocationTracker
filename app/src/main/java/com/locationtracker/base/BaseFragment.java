package com.locationtracker.base;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.locationtracker.R;
import com.locationtracker.activity.MainActivity;

/**
 * Created by LENOVO on 22-11-2017.
 */

public abstract class BaseFragment extends Fragment {

    protected static final String ARG_ITEM = "item";
    protected String item;

    Toolbar mToolbar;

    private FragmentNavigation mFragmentNavigation;;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }




    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setToolbar(view);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentNavigation) {
            mFragmentNavigation = (FragmentNavigation) context;
        }
    }

    public interface FragmentNavigation {
        void pushFragment(Fragment fragment);
    }






    @CallSuper
    @Override public void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT >= 24) {
            dispatchFragmentActivated();
        }
    }

    @CallSuper @Override public void onStop() {
        if (Build.VERSION.SDK_INT >= 24) {
            dispatchFragmentDeActivated();
        }
        super.onStop();
    }

    @CallSuper @Override public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT < 24) {
            dispatchFragmentActivated();
        }
    }

    @CallSuper @Override public void onPause() {
        if (Build.VERSION.SDK_INT < 24) {
            dispatchFragmentDeActivated();
        }
        super.onPause();
    }

    protected  void dispatchFragmentActivated(){
    };

    protected  void dispatchFragmentDeActivated(){

    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("ConstantConditions")
    protected void setToolbar(View view) {
        if(!hasCustomToolbar()) {
            ((BaseActivity) getActivity()).toolBarTitle.setText(getTitle());
            return;
        }

        mToolbar = view.findViewById(getToolbarId());
        mToolbar.setTitle(getTitle());
        setHasOptionsMenu(true);



    }

    protected abstract  @LayoutRes int getLayout();

    protected @StringRes int getTitle(){
        return R.string.not_title_set;
    }




    public boolean hasCustomToolbar(){
        return false;
    }

    protected @IdRes int getToolbarId(){
        return R.id.toolbar;
    }








}
