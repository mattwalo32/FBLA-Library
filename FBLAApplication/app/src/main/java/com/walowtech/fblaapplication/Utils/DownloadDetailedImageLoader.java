package com.walowtech.fblaapplication.Utils;

import android.app.Activity;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.walowtech.fblaapplication.Category;
import com.walowtech.fblaapplication.MainActivity;
import com.walowtech.fblaapplication.ViewPagerItem;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * AsyncTaskLoader that downloads a bitmap, in high detail, from a given source.
 *
 * @author Matthew Walowski
 * @version 1.0
 * @since 1.0
 */

//Created 10/15/2017
public class DownloadDetailedImageLoader extends AsyncTaskLoader<Bitmap> {

    public String urlString;
    public Context context;
    public Activity activity;

    /**
     * Default constructor for DownloadImageLoader class.
     *
     * @param context The context of the calling class
     * @param url The url to download the image from
     */
    public DownloadDetailedImageLoader(Activity activity, Context context, String url){
        super(context);
        this.context = context;
        this.activity = activity;
        urlString = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }


    @Override
    public Bitmap loadInBackground() {
        //Replace zoom factor to get better quality image
        //Log.i("LoginActivity", urlString);
        //urlString = urlString.replace("&zoom=1", "&zoom=5");
        Log.i("LoginActivity", urlString);
        Bitmap image = NetworkJSONUtils.downloadBitmap(activity, context, urlString);
        return image;
    }
}