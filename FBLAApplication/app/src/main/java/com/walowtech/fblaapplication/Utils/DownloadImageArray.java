package com.walowtech.fblaapplication.Utils;

import android.app.Activity;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Downloads an ArrayList of Bitmaps. An 2D array of URLs is given, and that data from
 * each resultant stream is created into a bitmap. The created 2D Bitmap ArrayList is
 * returned.
 *
 * @author Matthew Walowski
 * @version 1.0
 * @since 1.0
 */
//Created 10/29/2017
public class DownloadImageArray extends AsyncTaskLoader{

    Context context;
    Activity activity;
    ArrayList<ArrayList<String>> urls;

    public DownloadImageArray(Activity activity, Context context, ArrayList<ArrayList<String>> urls){
        super(context);
        this.activity = activity;
        this.context = context;
        this.urls = urls;
    }

    @Override
    protected void onStartLoading() {
        forceLoad(); //Forces loadInBackground() to start
    }


    @Override
    public ArrayList<ArrayList<Bitmap>> loadInBackground() {

        ArrayList<ArrayList<Bitmap>> images = new ArrayList<>();
        for(int i = 0; i < urls.size(); i++) {
            images.add(new ArrayList<Bitmap>()); //Adds an empty ArrayList to first row
            for(int j = 0; j < urls.get(i).size(); j++) { //Populates all images into the newly created row
                Log.i("LoginActivity", urls.get(i).get(j));
                Bitmap image = NetworkJSONUtils.downloadBitmap(activity, context, urls.get(i).get(j));
                images.get(i).add(image);
            }
        }
        return images;
    }
}
