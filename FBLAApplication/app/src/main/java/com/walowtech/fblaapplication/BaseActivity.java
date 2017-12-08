package com.walowtech.fblaapplication;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

/**
 * The class that contains all basic functions and variables of any activities for this app.
 *
 * All classes in this application should extend from this class or a variant of this class.
 *
 * @author Matthew Walowski
 * @version 1.0
 * @since 1.0
 */

//Created 11/10/2017
public class BaseActivity extends AppCompatActivity {

    public static Typeface handWriting;

    //PATH and URL are used to build requests
    protected final String BASE_URL = "walowtech.com";
    protected final String PATH0 = "apis";
    protected final String PATH1 = "FBLALibrary";
    protected final String PATH2 = "api.php";
    protected final String SCHEME = "https";

    //KEY variables are used to parse JSON Data
    protected final String KEY_AVERAGE_RATING = "AverageRating";
    protected final String KEY_GID = "GID";
    protected final String KEY_JSON = "JSON";
    protected final String KEY_NUM_SUBJECTS = "numSubjects";
    protected final String KEY_SMALL_THUMBNAIL = "SmallThumbnail";
    protected final String KEY_SUBJECT = "subject";
    protected final String KEY_SUCCESS = "Success";
    protected final String KEY_TITLE = "Title";
    protected final String KEY_BOOKS = "Books";
    protected final String KEY_WAIT_LIST = "WaitingList";
    protected final String KEY_MESSAGE = "Message";
    protected final String KEY_NAME = "Name";
    protected final String KEY_UID = "UID";

    protected final String KEY_DESCRIPTION = "Description";
    protected final String KEY_SLIDESHOW_IMAGE = "Image";
    protected final String KEY_LINKED_ITEM = "LinkedItem";
    protected final String KEY_SHORT_DESCRIPTION = "ShortDescription";


    //PARAMS are used for URL arguments
    protected final String PARAM_ACTION = "ACTION";
    protected final String PARAM_NUM_RESULTS = "NUMOFRESULTS";
    protected final String PARAM_UID = "UID";
    protected final String PARAM_SEARCH_QUERY = "SEARCHSTRING";
    protected final String PARAM_SEARCH_ITEM = "SEARCHITEM";
    protected final String PARAM_EMAIL = "EMAIL";
    protected final String PARAM_PASSWORD = "PASSWORD";
    protected final String PARAM_VERSION = "VERSION";
    protected final String PARAM_SEARCH_BID = "BID";
    protected final String PARAM_GID = "GID";

    //VALUES are the values of URL arguments;
    protected final String VALUE_ACTION_BOOKS = "ACTION_RETRIEVE_TOP_BOOKS_BY_CATEGORY";
    protected final String VALUE_ACTION_MESSAGE = "ACTION_RETRIEVE_DAILY_MESSAGE";
    protected final String VALUE_ACTION_SEARCH = "ACTION_RETRIEVE_BOOK_DATA";
    protected final String VALUE_ACTION_RETRIEVE_ACCOUNT_DATA = "ACTION_RETRIEVE_ACCOUNT_DATA";
    protected final String VALUE_ACTION_RETRIEVE_DETAILED_DATA = "ACTION_RETRIEVE_DETAILED_BOOK_DATA";
    protected final String VALUE_NUM_RESULTS = "15";
    protected int VALUE_UID;
    protected String VALUE_SEARCH_QUERY;
    protected String VALUE_SEARCH_ITEM = "TITLE";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); //Stops keyboard from popping up
        handWriting = Typeface.createFromAsset(getAssets(), "fonts/hand_writing.ttf"); // Standard typeface throughout app
    }

    /**
     * Sets elevation of a view using ViewCompat
     *
     * @param view The view to set the elevation of.
     * @param elevation The height to set the elevation to.
     */
    public void setElevation(View view, int elevation){
        ViewCompat.setElevation(view, elevation);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); //Stops keyboard from popping up
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
