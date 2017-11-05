package com.walowtech.fblaapplication.Utils;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by mattw on 10/30/2017.
 */
//TODO doc
public class DownloadJSONArray extends AsyncTaskLoader {

    ArrayList<ArrayList<URL>> urls;
    Context context;

    public DownloadJSONArray(Context context, ArrayList<ArrayList<URL>> urls){
        super(context);
        this.urls = urls;
        this.context = context;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
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
                    InputStream is = NetworkJSONUtils.retrieveInputStream(context, urls.get(i).get(j));
                    jsonObject = NetworkJSONUtils.retrieveJSON(context, is);
                } catch (Exception e) {
                    e.printStackTrace();
                    //TODO fix this because it is calling UID from background https://stackoverflow.com/questions/17379002/java-lang-runtimeexception-cant-create-handler-inside-thread-that-has-not-call
                    //ErrorUtils.errorDialog(context, "Unexpected Error", "An error occurred while retrieving data. Make sure you have a good internet connection, and don't switch networks while downloading data.");
                }
                JSONResponses.get(i).add(jsonObject);
            }
        }
        return JSONResponses;
    }

}
