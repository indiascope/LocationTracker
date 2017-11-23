package com.locationtracker.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.location.Location;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.locationtracker.BuildConfig;


/**
 * Created by Admin1 on 4/18/2016.
 */
public class UIUtil {




    private static final String LOCATION_TRACKER ="Location Tracker" ;

    /*
        For Log
    **/
    public static void log(String message) {
        if(BuildConfig.DEBUG) {
            Log.e(LOCATION_TRACKER, message);
        }
    }


    /**
     *  For Log Info
     * @param message
     */

    public static void logInfo(String message){
        if(BuildConfig.DEBUG) {
            Log.i(LOCATION_TRACKER, message);
        }
    }



    public static Drawable setDrawableSelector(Context context, int normal, int selected) {


        Drawable state_normal = ContextCompat.getDrawable(context, normal);

        Drawable state_pressed = ContextCompat.getDrawable(context, selected);


        Bitmap state_normal_bitmap = ((BitmapDrawable)state_normal).getBitmap();

        // Setting alpha directly just didn't work, so we draw a new bitmap!
        Bitmap disabledBitmap = Bitmap.createBitmap(
                state_normal.getIntrinsicWidth(),
                state_normal.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(disabledBitmap);

        Paint paint = new Paint();
        paint.setAlpha(126);
        canvas.drawBitmap(state_normal_bitmap, 0, 0, paint);

        BitmapDrawable state_normal_drawable = new BitmapDrawable(context.getResources(), disabledBitmap);




        StateListDrawable drawable = new StateListDrawable();

        drawable.addState(new int[]{android.R.attr.state_selected},
                state_pressed);
        drawable.addState(new int[]{android.R.attr.state_enabled},
                state_normal_drawable);

        return drawable;
    }




    /*
    *   For Show Toast
    * */

    public static void toast(Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    public static double  distanceBetweenToLatLong(Location startLocation,Location updatedLocation){

        Location startPoint=new Location("location Start");
        startPoint.setLatitude(startLocation.getLatitude());
        startPoint.setLongitude(startLocation.getLongitude());
        Location endPoint=new Location("location Updatd");
        endPoint.setLatitude(updatedLocation.getLatitude());
        endPoint.setLongitude(updatedLocation.getLongitude());

        double distance=startPoint.distanceTo(endPoint);
        return distance;
    }






















}
