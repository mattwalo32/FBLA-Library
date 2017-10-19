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
 * Created by mattw on 10/15/2017.
 */

public class DownloadDetailedImageLoader extends AsyncTaskLoader<Bitmap> {

    public String urlString;
    public Context context;

    /**
     * Default constructor for DownloadImageLoader class.
     *
     * @param context The context of the calling class
     * @param url The url to download the image from
     */
    public DownloadDetailedImageLoader(Context context, String url){
        super(context);
        this.context = context;
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
        Bitmap image = NetworkJSONUtils.downloadBitmap(context, urlString);
        return image;
    }
}