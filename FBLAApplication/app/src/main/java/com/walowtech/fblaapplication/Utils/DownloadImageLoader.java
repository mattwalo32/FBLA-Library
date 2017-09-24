package com.walowtech.fblaapplication.Utils;

import android.app.Activity;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.walowtech.fblaapplication.Category;
import com.walowtech.fblaapplication.MainActivity;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Downloads an image from a URL.
 *
 * Asyncronously downloads an image from a URL provided in
 * the smallThumbnail property of the Category ArrayList
 * passed. After each individual category is finished, the
 * UI updated to reflect the changes made.
 *
 * @author Matthew Walowski
 * @version 1.0
 * @since 1.0
 */

//Created 9/21/17
public class DownloadImageLoader extends AsyncTaskLoader<JSONObject> {

    ArrayList<Category> categories;
    Context context;
    ArrayList<Bitmap> response;
    Activity activity;
    Bitmap image;

    int i;
    int j;

    /**
     * Default constructor for DownloadImageLoader class.
     *
     * @param context The context of the calling class
     * @param categories The ArrayList of categories that contain
     *                   the books and their images to download
     * @param activity The activity of the calling class
     */
    public DownloadImageLoader(Context context, ArrayList<Category> categories, Activity activity){
        super(context);
        this.categories = categories;
        this.context = context;
        this.activity = activity;

        response = new ArrayList<>();
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public JSONObject loadInBackground() {
        //Loops through each category
        for(i=0; i < categories.size(); i++) {

            //Loops through each subject
            for(j=0; j < categories.get(i).books.size(); j++) {
                String url = categories.get(i).books.get(j).smallThumbnail;
                if(url != null && !url.equals("")) {
                    categories.get(i).books.get(j).coverSmall = MainActivity.categories.get(i).books.get(j).coverSmall = NetworkJSONUtils.downloadBitmap(context, url);
                }else {
                    //TODO set no image image
                }

            }

            //Updates on the UI thread to prevent crash
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MainActivity.updateUIImage();
                }
            });

        }

        return null;
    }
}
