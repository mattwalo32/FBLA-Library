package com.walowtech.fblaapplication.Utils;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

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

    public DownloadJSONLoader(Context context, URL url){
        super(context);
        this.url = url;
        this.context = context;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public JSONObject loadInBackground() {
        InputStream is = NetworkJSONUtils.retrieveInputStream(context, url);
        JSONObject jsonObject = NetworkJSONUtils.retrieveJSON(context, is);
        return jsonObject;
    }
}
