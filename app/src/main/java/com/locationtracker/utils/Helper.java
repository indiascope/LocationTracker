package com.locationtracker.utils;


import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.locationtracker.R;


/**
 * Created by Admin1 on 4/25/2016.
 */
public class Helper {


    /**
     * @param value
     * @return
     */
    public static String getTrimmedString(String value) {

        if (value == null) {
            return "";
        } else if (value.equals("null") || value.equals("Null") || value.equals("NULL")) {
            return "";
        } else if (value.equals("") || value.equals(" ") || value.equals("  ") ) {
            return "";
        } else {
            return value;
        }



    }


    /**
     * Show Toasr Short
     * @param context
     * @param toastDetails
     */
    public static void showToastShort(Context context, String toastDetails) {

        Toast.makeText(context, toastDetails, Toast.LENGTH_SHORT).show();

    }


    /**
     * Show Toast Long
     * @param context
     * @param toastDetails
     */

    public static void showToastLong(Context context, String toastDetails) {

        Toast.makeText(context, toastDetails, Toast.LENGTH_LONG).show();

    }


    /**
     * show Snackbar Long
     * @param context
     * @param view
     * @param message
     */
    public static void showSnackBarLong(Context context, View view,String message){
        Snackbar snackbar = Snackbar
                .make(view, message, Snackbar.LENGTH_LONG);

        View snackBarView = snackbar.getView();

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)snackBarView.getLayoutParams();
        params.gravity = Gravity.CENTER;
        snackBarView.setLayoutParams(params);




        snackBarView.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
        TextView tv = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));

        snackbar.show();
    }

    /**
     * show Snackbar Short
     */
    public static void showSnackBarShort(Context context, View view,String message){
        Snackbar snackbar = Snackbar
                .make(view, message, Snackbar.LENGTH_SHORT);
        View snackBarView = snackbar.getView();
//



//        FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
//        params.gravity = Gravity.TOP;
//        view.setLayoutParams(params);


        TextView tv = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(ContextCompat.getColor(context, R.color.white));


//        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)snackBarView.getLayoutParams();
//        params.gravity = Gravity.CENTER;
//        snackBarView.setLayoutParams(params);

        snackbar.show();
    }








    public static void showShortSnack(View parent, String message){
        Snackbar snackbar = Snackbar.make(parent, message, Snackbar.LENGTH_SHORT);
        TextView textView = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        textView.setMaxLines(10);
        snackbar.show();
    }

    public static void showLongSnack(View parent, String message){
        Snackbar snackbar = Snackbar.make(parent, message, Snackbar.LENGTH_LONG);
        TextView textView = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        textView.setMaxLines(10);
        snackbar.show();
    }











}
