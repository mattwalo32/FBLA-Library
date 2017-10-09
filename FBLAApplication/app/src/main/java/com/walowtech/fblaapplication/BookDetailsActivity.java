package com.walowtech.fblaapplication;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.walowtech.fblaapplication.Utils.DownloadJSONLoader;
import com.walowtech.fblaapplication.Utils.ErrorUtils;
import com.walowtech.fblaapplication.Utils.NetworkJSONUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;

import static android.view.View.GONE;

/**
 * Created by mattw on 10/7/2017.
 */

//Created 10/7/2017
public class BookDetailsActivity extends Activity  implements LoaderManager.LoaderCallbacks{

    private ImageView mBackgroundImage;
    private ImageView mBookImage;
    private LinearLayout mLinearLayout;
    private LinearLayout mDescriptionLayout;
    private RelativeLayout mTopRowLayout;
    private ScrollView mScrollView;
    private TextView mDescription;
    private TextView mTitle;
    private TextView mSubTitle;
    private TextView mAuthors;
    private TextView mCheckout;
    private TextView mAvgRating;
    private TextView mNumRatings;

    private Typeface handWriting;

    public URL requestURL;

    private final String PARAM_ACTION = "ACTION";
    private final String PARAM_GID = "GID";

    private String VALUE_ACTION_RETRIEVE_DETAILED_DATA = "ACTION_RETRIEVE_DETAILED_BOOK_DATA";
    private String VALUE_GID;

    private final String BASE_URL = "walowtech.com";
    private final String PATH0 = "apis";
    private final String PATH1 = "FBLALibrary";
    private final String PATH2 = "api.php";
    private final String SCHEME = "https";

    private final String KEY_SUCCESS = "Success";
    private final String KEY_JSON = "JSON";
    private final String KEY_TITLE = "Title";
    private final String KEY_SUB_TITLE = "SubTitle";
    private final String KEY_SUBJECT = "Subject";
    private final String KEY_DESCRIPTION = "Description";
    private final String KEY_AUTHORS = "Authors";
    private final String KEY_BOOK_DETAILS = "BOOK_DETAILS";
    private final String KEY_THUMBNAIL = "Thumbnail";
    private final String KEY_COPIES = "Copies";
    private final String KEY_ISBN10 = "ISBN10";
    private final String KEY_ISBN13 = "ISBN13";
    private final String KEY_NUMBER_RATINGS = "NumberRatings";
    private final String KEY_AVG_RATING = "AverageRating";
    private final String KEY_COPY_DETAILS = "CopyDetails";
    private final String KEY_BID = "BID";
    private final String KEY_CHECKOUT_TIME = "CheckoutTimestamp";
    private final String KEY_RETURN_TIME = "ReturnTimestamp";
    private final String KEY_WAITLIST_SIZE = "WaitingListAmount";
    private final String KEY_AVAILABLE_COPIES = "AvailableCopies";
    private final String KEY_REVIEWS = "Reviews";
    private final String KEY_CID = "CID";
    private final String KEY_UID = "UID";
    private final String KEY_RATING = "Rating";
    private final String KEY_COMMENT = "Comment";
    private final String KEY_COMMENT_TIME = "Timestamp";

    private JSONObject jsonResponse;

    private Book currentBook;

    private final int DOWNLOAD_DETAILED_JSON_LOADER = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);

        //Create custom font typeface
        handWriting = Typeface.createFromAsset(getAssets(), "fonts/hand_writing.ttf");

        //Initialize all Views
        mBackgroundImage = (ImageView) findViewById(R.id.bd_background_image);
        mScrollView = (ScrollView) findViewById(R.id.bd_scrollview);
        mDescription = (TextView) findViewById(R.id.bd_description);
        mTitle = (TextView) findViewById(R.id.bd_title);
        mSubTitle = (TextView) findViewById(R.id.bd_subtitle);
        mAuthors = (TextView) findViewById(R.id.bd_authors);
        mCheckout = (TextView) findViewById(R.id.bd_checkout);
        mAvgRating = (TextView) findViewById(R.id.bd_avg_rating);
        mNumRatings = (TextView) findViewById(R.id.bd_num_ratings);
        mLinearLayout = (LinearLayout) findViewById(R.id.bd_linear_layout);
        mBookImage = (ImageView) findViewById(R.id.bd_small_image);
        mTopRowLayout = (RelativeLayout) findViewById(R.id.bd_top_row_layout);
        mDescriptionLayout = (LinearLayout) findViewById(R.id.bd_description_layout);

        //Set typefaces
        mDescription.setTypeface(handWriting);
        mTitle.setTypeface(handWriting);
        mSubTitle.setTypeface(handWriting);
        mAuthors.setTypeface(handWriting);
        mCheckout.setTypeface(handWriting);
        mAvgRating.setTypeface(handWriting);
        mNumRatings.setTypeface(handWriting);

        //Set elevations
        setElevation(mLinearLayout, 12);
        setElevation(mTopRowLayout, 12);
        setElevation(mDescriptionLayout, 12);
        setElevation(mBookImage, 20);
        setElevation(mCheckout, 8);

        //Get Extras
        Intent thisIntent = getIntent();
        VALUE_GID = thisIntent.getStringExtra("GID");
        Bitmap bookCover = (Bitmap)thisIntent.getParcelableExtra("BOOK_IMAGE");
        //Set Book Cover. Set background until better quality loaded
        if(bookCover != null) {
            mBookImage.setImageBitmap(bookCover);
            mBackgroundImage.setImageBitmap(bookCover);
        }

        //Check for network connection
        if(NetworkJSONUtils.checkInternetConnection(this)) {
            //Retrieve JSON for Book Info
            retrieveDetailedBookInfo();
        }else{
            ErrorUtils.errorDialog(this, "Network Error", "It seems you don't have any network connection. Reset your connection and try again.");
        }

    }

    @Override
    public void onEnterAnimationComplete() {
        super.onEnterAnimationComplete();
        final int startScrollPos = getResources().getDimensionPixelSize(R.dimen.start_scroll_pos);
        Animator animator = ObjectAnimator.ofInt(
                mScrollView,
                "scrollY",
                startScrollPos)
                .setDuration(300);
        animator.start();
    }


    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        jsonResponse = null;
        return new DownloadJSONLoader(this, requestURL);
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        if(!(data == null || data == " ")) {
            if (loader.getId() == DOWNLOAD_DETAILED_JSON_LOADER) {
                try {
                    jsonResponse = new JSONObject(data.toString());
                    parseJSON(jsonResponse);
                } catch (JSONException JSONE) {
                    ErrorUtils.errorDialog(this, "Data Error", "There was an error with the data format. Please try again later.");
                }
            }
        }else{
            ErrorUtils.errorDialog(this, "Could not connect to server", "No information was retrieved from the server. Please try again later.");
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    /**
     * Sends request for book information based on GID received as extra
     *
     * The GID of the book from the previous activity is passed to this activity
     * as an extra. That GID is used to create a URL that requests detailed information
     * about a book. The requested is initiated by initializing an AsyncLoader
     */
    public void retrieveDetailedBookInfo(){
        //Check for internet connection
        if(!NetworkJSONUtils.checkInternetConnection(this)) {
            ErrorUtils.errorDialog(this, "Network Error", "It seems you don't have any network connection. Reset your connection and try again.");
            return;
        }

        //Build Uri that fetches book data
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(SCHEME)
                .authority(BASE_URL)
                .appendPath(PATH0)
                .appendPath(PATH1)
                .appendPath(PATH2)
                .appendQueryParameter(PARAM_ACTION, VALUE_ACTION_RETRIEVE_DETAILED_DATA)
                .appendQueryParameter(PARAM_GID, VALUE_GID)
                .build();
        String urlString = builder.toString();

        //Try to create URL from Uri
        try{
            requestURL = new URL(urlString);
        }catch(MalformedURLException MURLE){
            ErrorUtils.errorDialog(this, "There was an error with the url", "Currently the server can not be reached. Make sure your username and password are entered correctly");
            return;
        }

        //Restart loader is used instead of init loader so that if screen is rotated, loader is guarenteed to restart
        getLoaderManager().restartLoader(DOWNLOAD_DETAILED_JSON_LOADER, null, this);
    }

    public void parseJSON(JSONObject json){
        try {

            int success = json.getInt(KEY_SUCCESS);
            if(success == 1){
                //Get detailed book info
                JSONObject jsonResponse = json.getJSONObject(KEY_JSON);
                String title = jsonResponse.getString(KEY_TITLE);
                String subTitle = jsonResponse.getString(KEY_SUB_TITLE);

                String subject = jsonResponse.getString(KEY_SUBJECT);
                String description = jsonResponse.getString(KEY_DESCRIPTION);
                String authors = jsonResponse.getString(KEY_AUTHORS);
                //String bookDetails = jsonResponse.getString(KEY_BOOK_DETAILS);
                String thumbnail = jsonResponse.getString(KEY_THUMBNAIL);
                int copies = jsonResponse.getInt(KEY_COPIES);
                String ISBN10 = jsonResponse.getString(KEY_ISBN10);
                String ISBN13 = jsonResponse.getString(KEY_ISBN13);
                int numberRatings = jsonResponse.getInt(KEY_NUMBER_RATINGS);
                String averageRating = jsonResponse.getString(KEY_AVG_RATING);
                int availableCopies = jsonResponse.getInt(KEY_AVAILABLE_COPIES);
                JSONArray copyDetails = jsonResponse.getJSONArray(KEY_COPY_DETAILS);
                JSONArray reviews = jsonResponse.getJSONArray(KEY_REVIEWS);

                //Create book object
                currentBook = new Book(Float.valueOf(averageRating), numberRatings, copies, availableCopies, VALUE_GID, title, subTitle, null, subject, description, authors, thumbnail, null, ISBN10, ISBN13);


                //Loop through copy details
                for(int i = 0; i < copyDetails.length(); i++){
                    JSONObject currentCopy = (JSONObject)copyDetails.get(i);

                    int BID = currentCopy.getInt(KEY_BID);
                    String checkoutTime = currentCopy.getString(KEY_CHECKOUT_TIME);
                    String returnTime = currentCopy.getString(KEY_RETURN_TIME);
                    int waitingListSize = currentCopy.getInt(KEY_WAITLIST_SIZE);

                    Copy curCopy = new Copy(BID, waitingListSize, checkoutTime, returnTime);
                    currentBook.copies.add(curCopy);
                }
                //Loop through Reviews
                for(int i = 0; i < reviews.length(); i++){
                    JSONObject currentReview = (JSONObject)reviews.get(i);
                    int CID = currentReview.getInt(KEY_CID);
                    int rating = currentReview.getInt(KEY_RATING);
                    String timestamp = currentReview.getString(KEY_COMMENT_TIME);
                    String comment = currentReview.getString(KEY_COMMENT);
                    int UID = currentReview.getInt(KEY_UID);

                    Review curReview = new Review(CID, UID, rating, comment, timestamp);
                    currentBook.reviews.add(curReview);
                }
                displayBookInfo();
            }
        }catch(JSONException JSONE){
            ErrorUtils.errorDialog(this, "Malformed JSON Error", "A malformed response was recieved from the server. Please try again later.");
        }
    }

    /**
     * Resets TextViews to display updated information
     */
    private void displayBookInfo(){
        mTitle.setText(currentBook.title);
        mAuthors.setText(currentBook.authors);
        mDescription.setText(currentBook.description);
        mAvgRating.setText(Float.toString(currentBook.averageRating));

        String numRatingsString = String.valueOf(currentBook.numRatings) + " Ratings";
        mNumRatings.setText(numRatingsString);

        //Hide subtitle if it doesn't exist
        if(currentBook.subTitle.length() < 2)
            mSubTitle.setVisibility(GONE);
        else
            mSubTitle.setText(currentBook.subTitle);


    }

    /**
     * Sets elevation of a view using ViewCompat
     *
     * @param view The view to set the elevation of.
     * @param elevation The height to set the elevation to.
     */
    private void setElevation(View view, int elevation){
        //TODO fix shadow problem
        //TODO fix scrollbar problem
        ViewCompat.setElevation(view, elevation);
    }
}
