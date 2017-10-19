package com.walowtech.fblaapplication.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

/**
 * Class containing utilities for fetching JSON data from online
 *
 * This class contains all utilities used in this application related
 * to creating a url connection and retrieving JSON data from the
 * input stream resulting from the connection.
 *
 * @author Matthew Walowski
 * @version 1.0
 * @since 1.0
 */

//Created 9/13/2017
public class NetworkJSONUtils {

    public static boolean validateName(){
        return false;
    }

    /**
     * Checks the current network status
     *
     * @param context The context from which the method is being evoked.
     * @return A boolean that is true if there is network connection.
     */
    public static boolean checkInternetConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();

        return (activeNetworkInfo != null && activeNetworkInfo.isAvailable() && activeNetworkInfo.isConnected());
    }

    /**
     * Creates a HttpsURLConnection and downloads the input stream
     * from the address.
     *
     *
     * @param url the connection is made to the URL specified
     * @return the input stream of the URL is returned
     */
    public static InputStream retrieveInputStream(Context context, URL url){
        HttpsURLConnection urlConnection;
        InputStream inputStream = null;

        try{
            urlConnection = (HttpsURLConnection)url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(10000);
            urlConnection.setReadTimeout(15000);
            urlConnection.connect();

            int responseCode = urlConnection.getResponseCode();

            switch(responseCode) {
                case 200:
                    inputStream = urlConnection.getInputStream();
                    break;
                case 400:
                    ErrorUtils.errorDialog(context, "Error 400", "A bad request was made. Check your internet connection and be sure to use the most recent version of this app.");
                    return null;
                case 401:
                case 403:
                    ErrorUtils.errorDialog(context, "Error 401/403", "An unauthorized request has been made. Check your internet connection and be sure to use the most recent version of this app.");
                    return null;
                case 404:
                    ErrorUtils.errorDialog(context, "Error 404", "Server was not found. Check your internet connection and be sure to use the most recent version of this app.");
                    return null;
                case 408:
                    ErrorUtils.errorDialog(context, "Error 408", "Connection timed out. Check your internet connection and be sure to use the most recent version of this app.");
                    return null;
                case 414:
                    ErrorUtils.errorDialog(context, "Error 414", "The URI requested was too long. Check your internet connection and be sure to use the most recent version of this app.");
                    return null;
                case 520:
                    ErrorUtils.errorDialog(context, "Error 520", "There is an error with our servers. Please try again later");
                    return null;
                default:
                    ErrorUtils.errorDialog(context, "Error " + responseCode, "A network error occured");
                    return null;
            }
        }catch(IOException IOE){
            ErrorUtils.errorDialog(context, "Connection Error", "There was an error with your network connection. Check your connection and retry.");
            return null;
        }

        return inputStream;
    }

    /**
     * Converts an input stream to JSON object
     *
     * A buffered reader is created from a stream reader
     * which reads the input stream specified. The buffered
     * reader reads one line at a time until there is no
     * more data left. Then the JSON object is created
     * and returned.
     *
     * @param is the input stream to be read and converted
     * @return the JSON object created from the input stream. @return
     * will be null if @param is is null
     */
    public static JSONObject retrieveJSON(Context context, InputStream is){
        JSONObject json = null;
        StringBuilder response = new StringBuilder();
        InputStreamReader streamReader = null;
        BufferedReader bufferedReader = null;

        if(is == null) {
            ErrorUtils.errorDialog(context, "Server Error", "It seems no data could be downloaded from the server. Please try again later."); //TODO may need to fix all errors
            return null;
        }

        try{
            streamReader = new InputStreamReader(is);
            bufferedReader = new BufferedReader(streamReader);

            String line = bufferedReader.readLine();
            while(line != null){
                response.append(line);
                line = bufferedReader.readLine();
            }

            json = new JSONObject(response.toString());
        }catch (IOException IOE){
            ErrorUtils.errorDialog(context, "Connection Error", "There was an error with your network connection. Check your connection and retry.");
            return null;
        }catch (JSONException JE){
            ErrorUtils.errorDialog(context, "Data Format Error", "There was an error with the data format. Please try again later.");
            return null;
        }
        return json;
    }

    public static Bitmap downloadBitmap(Context context, String url){
        Bitmap bitmap = null;

        try {
            InputStream is = new java.net.URL(url).openStream();
            bitmap = BitmapFactory.decodeStream(is);
        }catch(MalformedURLException MURLE){
            ErrorUtils.errorDialog(context, "URL Error", "There was an error with the URL request. Please try again later.");
        }catch(IOException IOE){
            ErrorUtils.errorDialog(context, "Connection Error", "There was an error with your network connection. Check your connection and retry.");
        }

        return bitmap;
    }
}
