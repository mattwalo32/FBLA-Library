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
 * @version 1.0.2
 * @since 4/16/2018
 */

//Created 11/10/2017
public class BaseActivity extends AppCompatActivity {

    protected final String GOOGLE_BASE_LINK = "https://books.google.com/books?vid=ISBN";

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
    protected final String KEY_SUB_TITLE = "SubTitle";
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
    protected final String KEY_AUTHORS = "Authors";
    protected final String KEY_BOOK_DETAILS = "BOOK_DETAILS";
    protected final String KEY_THUMBNAIL = "Thumbnail";
    protected final String KEY_COPIES = "Copies";
    protected final String KEY_ISBN10 = "ISBN10";
    protected final String KEY_ISBN13 = "ISBN13";
    protected final String KEY_NUMBER_RATINGS = "NumberRatings";
    protected final String KEY_AVG_RATING = "AverageRating";
    protected final String KEY_COPY_DETAILS = "CopyDetails";
    protected final String KEY_BID = "BID";
    protected final String KEY_CHECKOUT_TIME = "CheckoutTimestamp";
    protected final String KEY_RETURN_TIME = "ReturnTimestamp";
    protected final String KEY_WAITLIST_SIZE = "WaitingListAmount";
    protected final String KEY_AVAILABLE_COPIES = "AvailableCopies";
    protected final String KEY_REVIEWS = "Reviews";
    protected final String KEY_CID = "CID";
    protected final String KEY_RATING = "Rating";
    protected final String KEY_COMMENT = "Comment";
    protected final String KEY_COMMENT_TITLE = "Title";
    protected final String KEY_COMMENT_TIME = "Timestamp";
    protected final String KEY_AUTH_SUCCESS = "validationSuccess";


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
    protected final String PARAM_BID = "BID";
    protected final String PARAM_RATING = "RATING";
    protected final String PARAM_COMMENT = "COMMENT";
    protected final String PARAM_TITLE = "TITLE";

    //VALUES are the values of URL arguments;
    protected final String VALUE_ACTION_BOOKS = "ACTION_RETRIEVE_TOP_BOOKS_BY_CATEGORY";
    protected final String VALUE_ACTION_MESSAGE = "ACTION_RETRIEVE_DAILY_MESSAGE";
    protected final String VALUE_ACTION_SEARCH = "ACTION_RETRIEVE_BOOK_DATA";
    protected final String VALUE_ACTION_RETRIEVE_ACCOUNT_DATA = "ACTION_RETRIEVE_ACCOUNT_DATA";
    protected final String VALUE_ACTION_RETRIEVE_DETAILED_DATA = "ACTION_RETRIEVE_DETAILED_BOOK_DATA";
    protected final String VALUE_NUM_RESULTS = "15";
    protected final String VALUE_ACTION_ADD_REVIEW = "ACTION_ADD_REVIEW";
    protected final String VALUE_ACTION_CHECKOUT = "ACTION_CHECKOUT_BOOK";
    protected final String VALUE_ACTION_WAITLIST = "ACTION_ADD_TO_WAIT_LIST";
    protected final String VALUE_ACTION_RETURN = "ACTION_RETURN_BOOK";
    protected int VALUE_SEARCH_BID;
    private String VALUE_GID;
    protected int VALUE_UID;
    protected String VALUE_SEARCH_QUERY;

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
