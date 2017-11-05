package com.walowtech.fblaapplication.Utils;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by mattw on 10/29/2017.
 */ //TODO doc

public class DownloadImageArray extends AsyncTaskLoader{

    Context context;
    ArrayList<ArrayList<String>> urls;

    public DownloadImageArray(Context context, ArrayList<ArrayList<String>> urls){
        super(context);
        this.context = context;
        this.urls = urls;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }


    @Override
    public ArrayList<ArrayList<Bitmap>> loadInBackground() {

        ArrayList<ArrayList<Bitmap>> images = new ArrayList<>();
        for(int i = 0; i < urls.size(); i++) {
            images.add(new ArrayList<Bitmap>());
            for(int j = 0; j < urls.get(i).size(); j++) {
                Log.i("LoginActivity", urls.get(i).get(j));
                Bitmap image = NetworkJSONUtils.downloadBitmap(context, urls.get(i).get(j));
                images.get(i).add(image);
            }
        }
        return images;
    }
}
