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

import java.io.IOError;
import java.io.IOException;
import java.util.ArrayList;
import static com.walowtech.fblaapplication.MainActivity.subjectsLastVis;

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

    ArrayList<Category> categories = new ArrayList<>();
    ArrayList<ViewPagerItem> slides = new ArrayList<>();
    Context context;
    ArrayList<Bitmap> response = new ArrayList<>();
    Activity activity;
    Bitmap image;

    MainActivity mainActivity;

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
    public DownloadImageLoader(Context context, MainActivity mainActivity, ArrayList<Category> categories, ArrayList<ViewPagerItem> slides, Activity activity){
        super(context);
        this.mainActivity = mainActivity;
        this.categories = categories;
        this.context = context;
        this.activity = activity;
        this.slides = slides;

        response = new ArrayList<>();
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }


    @Override
    public JSONObject loadInBackground() {
        if(MainActivity.downloadType == 0) {
            //Loops through each category
            for (i = 0; i < categories.size(); i++) {

                //Loops through each subject
                for (j = 0; j < categories.get(i).books.size(); j++) {
                    String url = categories.get(i).books.get(j).smallThumbnail;
                    if (url != null && !url.equals("")) {
                        image = null;
                        try{
                            image = NetworkJSONUtils.downloadBitmap(activity, context, url);
                        }catch(Exception e){
                            e.printStackTrace();
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ErrorUtils.errorDialog(activity, "Unexpected Error", "An error occurred while retrieving data. Make sure you have a good internet connection, and don't switch networks while downloading data.");
                                }
                            });
                        }
                        categories.get(i).books.get(j).coverSmall = image;
                    } else {
                        //TODO set no image image
                    }
                    //Updates on the UI thread to prevent crash
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (i < categories.size()) {
                                mainActivity.updateUIImage(i, j, image);
                            }
                        }
                    });
                }

            }
        }else if(MainActivity.downloadType == 1){
                for(int i = 0; i < slides.size(); i++) {
                    String url = slides.get(i).imageURL;
                    image = null;
                    try{
                        image = NetworkJSONUtils.downloadBitmap(activity, context, url);
                    }catch(Exception e){
                        e.printStackTrace();
                        ErrorUtils.errorDialog(context, "Unexpected Error", "An error occurred while retrieving data. Make sure you have a good internet connection, and don't switch networks while downloading data.");
                    }
                    slides.get(i).image = image;
                }

        }
        return null;
    }
}
