package com.walowtech.fblaapplication;

import android.app.Activity;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.walowtech.fblaapplication.Utils.DownloadJSONLoader;
import com.walowtech.fblaapplication.Utils.ErrorUtils;
import com.walowtech.fblaapplication.Utils.GridAdapter;
import com.walowtech.fblaapplication.Utils.NetworkJSONUtils;
import com.walowtech.fblaapplication.Utils.SuggestionCursorAdapter;

import org.androidannotations.annotations.rest.Get;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * This activity displays books in a grid pattern.
 *
 * This activity is launched whenever an item is searched for or a large/unknown
 * amount of items need to be displayed simplistically. The activity consists of
 * a grid view that contains many book cover, each of which will launch a seperate
 * activity onClick.
 *
 * @author Matthew Walowski
 * @version 1.0
 * @since 1.0
 */

//Created 11/6/2017
public class GridViewActivity extends NavDrawerActivity {

    RecyclerView gridLayout;
    RecyclerView.Adapter gridViewAdapter;
    SearchView searchBar;
    RequestQueue requestQueue;
    TextView mNoneFound;
    TextView mLoadingText;

    String extraURL;
    String extraQuery;

    ArrayList<Book> books = new ArrayList<>();
    ArrayList<Book> searchResults = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_view);
        super.onCreateDrawer();
        super.setNames();

        Intent thisIntent = getIntent();
        extraURL = thisIntent.getStringExtra("URL");
        extraQuery = thisIntent.getStringExtra("QUERY");

        requestQueue = Volley.newRequestQueue(this);

        mNoneFound = (TextView) findViewById(R.id.ga_none_found);
        mLoadingText = (TextView) findViewById(R.id.ga_loading);
        gridLayout = (RecyclerView) findViewById(R.id.gv_grid_layout);
        gridLayout.setLayoutManager(new GridLayoutManager(this, calculateNoOfColumns()));

        gridViewAdapter = new GridAdapter(this, this, books);
        gridLayout.setAdapter(gridViewAdapter);

        mNoneFound.setTypeface(handWriting);
        mLoadingText.setTypeface(handWriting);

        configActionBar();
        if(NetworkJSONUtils.checkInternetConnection(this)) {
            getJSON(extraURL);
        }else{
            ErrorUtils.errorDialog(this, "Network Error", "It seems you don't have any network connection. Reset your connection and try again.");
        }
    }

    /**
     * Downloads a stream from a url and converts it into a JSONObject that gets parsed from a
     * different method.
     *
     * @param url The URL to download the JSON from.
     */
    public void getJSON(final String url){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    parseBookJSON(json);
                    mLoadingText.setVisibility(View.GONE);
                }catch(JSONException JSONE){
                    ErrorUtils.errorDialog(GridViewActivity.this, "Data Error", "There was an error with the data format. Please try again later.");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ErrorUtils.errorDialog(GridViewActivity.this, "Could not connect to server", "No information was retrieved from the server. Please try again later.");
            }
        });

        stringRequest.addMarker(CustomTags.BOOK_REQUESTS);
        requestQueue.add(stringRequest);
    }

    /**
     * Configures the actionbar
     *
     * The layout of the actionbar is inflated and listeners
     * are set on the toggle button and SearchView
     */
    public void configActionBar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.m_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_drawable));
        actionBar.setDisplayHomeAsUpEnabled(false);


        final ImageView toggleIcon = (ImageView) toolbar.findViewById(R.id.toggle_icon);
        toggleIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //If drawer open close it, if open close it
                if(drawerLayout.isDrawerOpen(drawerList))
                    drawerLayout.closeDrawer(drawerList);
                else
                    drawerLayout.openDrawer(drawerList);
            }
        });

        //Set SearchView listener
        searchBar = (SearchView) toolbar.findViewById(R.id.menu_search);
        searchBar.setPadding(0, 0, 0, 0);
        int id = searchBar.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView searchText = (TextView) searchBar.findViewById(id);
        searchText.setTypeface(handWriting);
        if(extraQuery == null) {
            searchBar.setQueryHint("Search");
        }else{
            searchBar.setQuery(extraQuery, false);
        }
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchBar.setQuery(query, false);

                Uri.Builder builder = new Uri.Builder();
                VALUE_SEARCH_ITEM = "ALL";
                VALUE_SEARCH_QUERY = query;
                String maxResults = "1000";

                builder.scheme(SCHEME)
                        .authority(BASE_URL)
                        .appendPath(PATH0)
                        .appendPath(PATH1)
                        .appendPath(PATH2)
                        .appendQueryParameter(PARAM_ACTION, VALUE_ACTION_SEARCH)
                        .appendQueryParameter(PARAM_SEARCH_ITEM, VALUE_SEARCH_ITEM)
                        .appendQueryParameter(PARAM_SEARCH_QUERY, VALUE_SEARCH_QUERY)
                        .appendQueryParameter(PARAM_NUM_RESULTS, maxResults)
                        .build();

                //Cancel all current requests
                requestQueue.cancelAll(CustomTags.SEARCH_REQUESTS);
                requestQueue.cancelAll(CustomTags.IMAGE_REQUESTS);
                requestQueue.cancelAll(CustomTags.BOOK_REQUESTS);

                books.clear();

                getJSON(builder.toString());

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                updateSearchResults(newText);
                return false;
            }
        });

        //Suggestions Adapter displays how suggestions are shown
        searchBar.setSuggestionsAdapter(new SuggestionCursorAdapter(
                getApplicationContext(), R.layout.suggestion, null,
                new String[] { SearchManager.SUGGEST_COLUMN_TEXT_1 },
                new int[] { android.R.id.text1 }, 0
        ));

        searchBar.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                searchBar.setQuery(searchResults.get(position).title, true);
                String GID = searchResults.get(position).GID;
                onSuggestionSelected(GridViewActivity.this, GID);
                return true;
            }
        });

        //App bar displays differently on different device versions. Padding needs to be changed based on those versions
        if(Build.VERSION.SDK_INT >= 24) {
            toolbar.post(new Runnable() {
                @Override
                public void run() {
                    int searchBarLeft = (int) searchBar.getX();
                    int buttonRight = (int) toggleIcon.getX() + toggleIcon.getWidth();

                    int difference = buttonRight - searchBarLeft;
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)searchBar.getLayoutParams();
                    params.setMargins(difference, 0, 0, 0);
                    searchBar.setLayoutParams(params);

                    Log.i("LoginActivity", difference + " " + searchBarLeft + " " + buttonRight);
                }
            });
        }

    }

    /**
     * Sends request for book information about searchQuery
     *
     * The UID is retrieved from SharedPreferences in case the user
     * wants to retrieve book that they checked out. Then a URI is built
     * with their UID and search query, a Volley request is added to the queue
     *
     * @param searchQuery
     */
    public void updateSearchResults(String searchQuery){
        //Check for internet connection
        if(!NetworkJSONUtils.checkInternetConnection(this)) {
            ErrorUtils.errorDialog(this, "Network Error", "It seems you don't have any network connection. Reset your connection and try again.");
            return;
        }

        //Get UID
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        super.VALUE_UID = sharedPref.getInt("UID", -1);
        VALUE_SEARCH_QUERY = searchQuery;

        //Build Uri that fetches book data
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(SCHEME)
                .authority(BASE_URL)
                .appendPath(PATH0)
                .appendPath(PATH1)
                .appendPath(PATH2)
                .appendQueryParameter(PARAM_ACTION, VALUE_ACTION_SEARCH)
                .appendQueryParameter(PARAM_SEARCH_ITEM, VALUE_SEARCH_ITEM)
                .appendQueryParameter(PARAM_SEARCH_QUERY, VALUE_SEARCH_QUERY)
                .appendQueryParameter(PARAM_UID, Integer.toString(VALUE_UID))
                .build();
        String urlString = builder.toString();

        //Try to create URL from Uri
        try{
            requestURL = new URL(urlString);
        }catch(MalformedURLException MURLE){
            ErrorUtils.errorDialog(this, "There was an error with the url", "Currently the server can not be reached. Make sure your username and password are entered correctly");
            MURLE.printStackTrace();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlString,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            parseSearchJSON(jsonResponse);
                        }catch(JSONException JSONE){
                            ErrorUtils.errorDialog(getApplicationContext(), "Data Error", "There was an error with the data format. Please try again later.");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ErrorUtils.errorDialog(getApplicationContext(), "Could not connect to server", "No information was retrieved from the server. Please try again later.");
            }
        });

        stringRequest.addMarker(CustomTags.SEARCH_REQUESTS);
        requestQueue.add(stringRequest);
    }

    /**
     * Checks success code of JSON and passes to super class to parse results.
     *
     * @param json The JSON to parse.
     */
    public void parseSearchJSON(JSONObject json){
        try {
            int success = json.getInt(KEY_SUCCESS);
            if (success == 4) {
                searchBar.getSuggestionsAdapter().changeCursor(super.updateSearchResults(json, searchResults)); //Superclass parses JSON
            }
        }catch(JSONException JSONE){
            ErrorUtils.errorDialog(this, "A JSON Error Occurred", "The response from the server was incorrectly formatted. Please try again later.");
        }
    }

    /**
     * Checks success code of JSON and parses it based on expected object
     * If success code is:
     *      4 - An Array of books is expected and parsed to populate grid.
     *
     * @param json The JSONObject to parse.
     */
    public void parseBookJSON(JSONObject json){
        try {
            int success = json.getInt(KEY_SUCCESS);
            if (success == 4) { //Book successfully looked up
                JSONArray returnedBooks = json.getJSONArray(KEY_JSON);
                mNoneFound.setVisibility( returnedBooks.length() == 0 ? View.VISIBLE : View.GONE);
                for(int i = 0; i < returnedBooks.length(); i++) {
                    JSONObject currentBookObject = (JSONObject) returnedBooks.get(i);
                    String subject = null; //Subject not returned by server
                    String title = currentBookObject.getString(KEY_TITLE);
                    String smallThumbnail = currentBookObject.getString(KEY_SMALL_THUMBNAIL);
                    String GID = currentBookObject.getString(KEY_GID);
                    String averageRating = currentBookObject.getString(KEY_AVERAGE_RATING);
                    Float averageRatingFloat = Float.parseFloat(averageRating);

                    final Book currentBook = new Book(subject, title, GID, smallThumbnail, averageRatingFloat);
                    books.add(currentBook);
                    final int currentSize = books.size() - 1;

                    ImageRequest imageRequest = new ImageRequest(smallThumbnail, new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap response) {
                            try {
                                books.get(currentSize).coverSmall = response;
                                gridViewAdapter.notifyDataSetChanged();
                            }catch(IndexOutOfBoundsException IOBE){
                                //FIXED? - This will be called if the user searches for a new image but images from the old search are still loading
                                ErrorUtils.errorDialog(GridViewActivity.this, "An Error Occurred", "An error occurred while updating the images. Please try again.");
                            }
                        }
                    }, 0, 0, null, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            ErrorUtils.errorDialog(GridViewActivity.this, "An Error Occurred", "This search could not be executed right now. Please try again later.");
                        }
                    });

                    imageRequest.addMarker(CustomTags.IMAGE_REQUESTS);
                    requestQueue.add(imageRequest);
                }

                gridViewAdapter.notifyDataSetChanged();
            }else{
                mNoneFound.setVisibility(View.VISIBLE);
            }
        }catch(JSONException JSONE){
            ErrorUtils.errorDialog(this, "JSON Error", "There was an error in the format of the data received from the server. Please try again later.");
        }
    }

    /**
     * Calculates the number of columns needed to fit books of a certain width
     * in as many rows as possible.
     *
     * @return The max number of columns.
     */
    public int calculateNoOfColumns() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float pxWidth = displayMetrics.widthPixels - getResources().getDimension(R.dimen.margin) * 2;
        float pxColumnWidth = getResources().getDimension(R.dimen.book_width);

        int noOfColumns = (int) (pxWidth/ pxColumnWidth);
        Log.i("LoginActivity", "COLUMN WIDTH" + noOfColumns);
        return noOfColumns;
    }
}
