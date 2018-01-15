package com.walowtech.fblaapplication.Utils;

import android.app.Activity;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Class downloads JSON data
 *
 * Raw JSON data is downloaded asynchronously through the
 * URL specified in the constructor. The JSON is returned as
 * a JSONObject.
 *
 * @author Matthew Walowski
 * @version 1.0
 * @since 1.0
 */

//Created on 9/15/17
public class DownloadJSONLoader extends AsyncTaskLoader<JSONObject> {

    URL url;
    Context context;
    Activity activity;

    public DownloadJSONLoader(Activity activity, Context context, URL url){
        super(context);
        this.url = url;
        this.context = context;
        this.activity = activity;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public JSONObject loadInBackground() {
        Log.i("LoginActivity", url.toString());
        try {
            InputStream is = NetworkJSONUtils.retrieveInputStream(activity, context, url);
            JSONObject jsonObject = NetworkJSONUtils.retrieveJSON(activity, context, is);
            return jsonObject;
        }catch(Exception e){
            e.printStackTrace();
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ErrorUtils.errorDialog(activity, "Unexpected Error", "An error occurred while retrieving data. Make sure you have a good internet connection, and don't switch networks while downloading data.");
                }
            });
        }
        return null;
    }
}
