package com.walowtech.fblaapplication.Utils;

import android.app.Activity;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * Downloads ListArrays of JSONObjects from a stream.
 *
 * A 2D array of URLs is passed into the constructor. Then the URLs are used
 * to created a 2D array of JSONObjects from the resultant stream.
 *
 * @author Matthew Walowski
 * @version 1.0
 * @since 1.0
 */
//Created 10/30/2017
public class DownloadJSONArray extends AsyncTaskLoader {

    ArrayList<ArrayList<URL>> urls;
    Context context;
    Activity activity;

    //Constructor
    public DownloadJSONArray(Activity activity, Context context, ArrayList<ArrayList<URL>> urls){
        super(context);
        this.urls = urls;
        this.context = context;
        this.activity = activity;
    }

    @Override
    protected void onStartLoading() {
        forceLoad(); //Forces loadInBackground to be acalled automatically
    }

    @Override
    public ArrayList<ArrayList<JSONObject>> loadInBackground() {

        ArrayList<ArrayList<JSONObject>> JSONResponses = new ArrayList<>();

        for(int i = 0; i < urls.size(); i++) {
            JSONResponses.add(new ArrayList<JSONObject>());
            for(int j = 0; j < urls.get(i).size(); j++) {
                //Log.i("LoginActivity", urls[i].toString());
                JSONObject jsonObject = null;
                try {
                    InputStream is = NetworkJSONUtils.retrieveInputStream(activity, context, urls.get(i).get(j));
                    jsonObject = NetworkJSONUtils.retrieveJSON(activity, context, is);
                } catch (Exception e) {
                    e.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ErrorUtils.errorDialog(activity, "Unexpected Error", "An error occurred while retrieving data. Make sure you have a good internet connection, and don't switch networks while downloading data.");
                        }
                    });
                }
                JSONResponses.get(i).add(jsonObject);
            }
        }
        return JSONResponses;
    }

}
