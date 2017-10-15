package com.walowtech.fblaapplication;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.walowtech.fblaapplication.Utils.DownloadJSONLoader;
import com.walowtech.fblaapplication.Utils.ErrorUtils;
import com.walowtech.fblaapplication.Utils.NetworkJSONUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

import static android.view.View.GONE;

/**
 * Created by mattw on 10/7/2017.
 */

//Created 10/7/2017
public class BookDetailsActivity extends Activity  implements LoaderManager.LoaderCallbacks{

    private FloatingActionButton mFAB;
    private SimpleRatingBar mBookRating;

    private ImageView mBackgroundImage;
    private ImageView mBookImage;
    private ImageView mShareButton;
    private RelativeLayout mBaseLayout;
    private LinearLayout mAdditionalDetailsLayout;
    private LinearLayout mDescriptionLayout;
    private LinearLayout mReviews;
    private RelativeLayout mTopRowLayout;
    private ScrollView mScrollView;
    private TextView mDescription;
    private TextView mTitle;
    private TextView mSubTitle;
    private TextView mAuthors;
    private TextView mCheckout;
    private TextView mAvgRating;
    private TextView mNumRatings;
    private TextView mFullTitle;
    private TextView mFullAuthors;
    private TextView mSubject;
    private TextView mCopies;
    private TextView mISBN10;
    private TextView mISBN13;

    public static Typeface handWriting;

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
    private final String KEY_NAME = "Name";
    private final String KEY_COMMENT = "Comment";
    private final String KEY_COMMENT_TITLE = "Title";
    private final String KEY_COMMENT_TIME = "Timestamp";

    private JSONObject jsonResponse;

    private Book currentBook;

    private boolean descriptionExpanded;

    ArrayList<Review> reviews = new ArrayList<>();

    private final int DOWNLOAD_DETAILED_JSON_LOADER = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_book_details);

        //Create custom font typeface
        handWriting = Typeface.createFromAsset(getAssets(), "fonts/hand_writing.ttf");

        //Initialize all Views
        mFAB = (FloatingActionButton) findViewById(R.id.bd_fab_description);
        mBookRating = (SimpleRatingBar) findViewById(R.id.bd_ratingbar);
        mBackgroundImage = (ImageView) findViewById(R.id.bd_background_image);
        mScrollView = (ScrollView) findViewById(R.id.bd_scrollview);
        mDescription = (TextView) findViewById(R.id.bd_description);
        mTitle = (TextView) findViewById(R.id.bd_title);
        mReviews = (LinearLayout) findViewById(R.id.bd_reviews_view);
        mSubTitle = (TextView) findViewById(R.id.bd_subtitle);
        mAuthors = (TextView) findViewById(R.id.bd_authors);
        mCheckout = (TextView) findViewById(R.id.bd_checkout);
        mAvgRating = (TextView) findViewById(R.id.bd_avg_rating);
        mNumRatings = (TextView) findViewById(R.id.bd_num_ratings);
        mBaseLayout = (RelativeLayout) findViewById(R.id.bd_base_layout);
        mBookImage = (ImageView) findViewById(R.id.bd_small_image);
        mTopRowLayout = (RelativeLayout) findViewById(R.id.bd_top_row_layout);
        mShareButton = (ImageView) findViewById(R.id.bd_share_button);
        mDescriptionLayout = (LinearLayout) findViewById(R.id.bd_description_layout);

        mAdditionalDetailsLayout = (LinearLayout) findViewById(R.id.bd_additional_info);
        mFullTitle = (TextView) findViewById(R.id.bd_full_title);
        mFullAuthors = (TextView) findViewById(R.id.bd_full_authors);
        mSubject = (TextView) findViewById(R.id.bd_subject);
        mCopies = (TextView) findViewById(R.id.bd_copies);
        mISBN10 = (TextView) findViewById(R.id.bd_isbn10);
        mISBN13 = (TextView) findViewById(R.id.bd_isbn13);

        //Set offset to screen height
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float height = outMetrics.heightPixels;

        mBaseLayout.setPadding(0, (int)height, 0, 0);

        //Set typefaces
        mDescription.setTypeface(handWriting);
        mTitle.setTypeface(handWriting);
        mSubTitle.setTypeface(handWriting);
        mAuthors.setTypeface(handWriting);
        mCheckout.setTypeface(handWriting);
        mAvgRating.setTypeface(handWriting);
        mNumRatings.setTypeface(handWriting);
        mFullTitle.setTypeface(handWriting, Typeface.BOLD);
        mFullAuthors.setTypeface(handWriting, Typeface.BOLD);
        mSubject.setTypeface(handWriting, Typeface.BOLD);
        mCopies.setTypeface(handWriting, Typeface.BOLD);
        mISBN10.setTypeface(handWriting, Typeface.BOLD);
        mISBN13.setTypeface(handWriting, Typeface.BOLD);

        //Set elevations
        setElevation(mBaseLayout, 12);
        setElevation(mTopRowLayout, 12);
        setElevation(mDescriptionLayout, 12);
        setElevation(mBookImage, 20);
        setElevation(mCheckout, 8);
        setElevation(mReviews, 12);

        descriptionExpanded = false;

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

        //Set on click listener for sharing
        mShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Create an intent to share
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "You should check out " + getString(R.string.app_name) + ", an app where you can easily find books at the school library!";
                if(currentBook.title != null)
                    shareBody = "Check out \"" + currentBook.title + "\", a book that I found with the app, " + getString(R.string.app_name);
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "" + getString(R.string.app_name));
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });

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
                    String commentTitle = currentReview.getString(KEY_COMMENT_TITLE);
                    String comment = currentReview.getString(KEY_COMMENT);
                    String name = currentReview.getString(KEY_NAME);
                    int UID = currentReview.getInt(KEY_UID);

                    Review curReview = new Review(CID, UID, rating, comment, commentTitle, timestamp, name);
                    currentBook.reviews.add(curReview);
                }
                displayBookInfo();
            }
        }catch(JSONException JSONE){
            ErrorUtils.errorDialog(this, "Malformed JSON Error", "A malformed response was recieved from the server. Please try again later.");
        }
    }

    /**
     * Resets TextViews to display updated information TODO doc
     */
    private void displayBookInfo(){
        //Set text of TextViews
        mTitle.setText(currentBook.title);
        mAuthors.setText(currentBook.authors);
        mDescription.setText(currentBook.description);
        mAvgRating.setText(String.format("%.01f", currentBook.averageRating));
        mBookRating.setRating(currentBook.averageRating);

        //Concatenate string and set text to string
        String numRatingsString = String.valueOf(currentBook.numRatings) + " Ratings";
        mNumRatings.setText(numRatingsString);

        //Concatenate strings before setting them
        String titleString = "Title: " + currentBook.title;
        String authorsString = "Authors: " + currentBook.authors;
        String subjectString = "Subject: " + currentBook.subject;
        String copiesString = "Library Copies: " + currentBook.numCopies;
        String ISBN10String = "ISBN10: " + currentBook.ISBN10;
        String ISBN13String = "ISBN13: " + currentBook.ISBN13;

        //Set text data
        mFullTitle.setText(titleString);
        mFullAuthors.setText(authorsString);
        mSubject.setText(subjectString);
        mCopies.setText(copiesString);
        mISBN10.setText(ISBN10String);
        mISBN13.setText(ISBN13String);

        //Hide subtitle if it doesn't exist
        if(currentBook.subTitle.length() < 2)
            mSubTitle.setVisibility(GONE);
        else
            mSubTitle.setText(currentBook.subTitle);

        //Get reviews
        reviews = currentBook.reviews;
        //Add first 3 reviews, unless there are less than 3 reviews - Then add all reviews
        int upperBound = ((reviews.size() - 1) > 2)?2:(reviews.size()-1);
        reviewAdapter(reviews, mReviews, 0, upperBound, true);
    }

    /**
     * Adapts items to LinearLayout.
     *
     * From the given parameters, items are created and attached to the parent LinearLayout.
     * For each item in the layout, a typeface and appropriate text is set. Items can be added
     * or removed with this method.
     *
     * @param reviews the reviews to be adapted to the parent.
     * @param parent the parent view to be adapted to
     * @param lowerDataBound the lower index of data to be adapted
     * @param upperDataBound the upper index of data to be adapted
     * @param add specifies if views in range should be added or removed
     */
    public void reviewAdapter(ArrayList<Review> reviews, LinearLayout parent, int lowerDataBound, int upperDataBound, boolean add){
        if(add) {
            //Loop through items and inflate them
            for (int i = lowerDataBound; i <= upperDataBound; i++) {
                //Try to remove old view
                try{
                    parent.removeViewAt(i);
                }catch(Exception e){

                }
                View view = getLayoutInflater().inflate(R.layout.review, parent, false);
                Review currentReview = reviews.get(i);

                final TextView title = (TextView) view.findViewById(R.id.comment_title);
                final TextView body = (TextView) view.findViewById(R.id.comment_body);
                final TextView more = (TextView) view.findViewById(R.id.comment_more);
                final TextView name = (TextView) view.findViewById(R.id.comment_name);
                final TextView time = (TextView) view.findViewById(R.id.comment_time);
                final SimpleRatingBar sRatingBar = (SimpleRatingBar) view.findViewById(R.id.comment_rating);

                //Set values
                title.setText(currentReview.title);
                body.setText(currentReview.comment + " asdf more spam to fill body completely and trigger more");
                name.setText(currentReview.name);
                time.setText(currentReview.formatTimestamp(currentReview.timestamp));
                sRatingBar.setRating(currentReview.rating);

                int offset = (int)(getResources().getDimension(R.dimen.comment_profile_diameter) + getResources().getDimension(R.dimen.margin));
                title.setPadding(offset, 0, 0, 0);

                //If there is no title, set title to amount of stars
                if(title.getText() == null || title.getText() == "" || title.getText() == "null"){
                    String titleString = (int)currentReview.rating  + " stars";
                    title.setText(titleString);
                }



                //If they did not leave a comment, remove comment
                if(body.getText() == null || body.getText() == "" || body.getText() == "null")
                    body.setVisibility(GONE);

                more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(body.getLineCount() < 4){
                            body.setMaxLines(Integer.MAX_VALUE);
                            more.setText(R.string.minimize);
                        }else{
                            body.setMaxLines(3);
                            more.setText(R.string.read_more);
                        }
                    }
                });

                //Check if more button needed
                body.post(new Runnable() {
                    @Override
                    public void run() {
                        //If there are less lines than the line limit, then hide the more button.
                        if(body.getLineCount() < 4) {
                            Log.i("LoginActivity", "NUM LINES" + body.getLineCount());
                            more.setVisibility(GONE);
                        }
                        body.setMaxLines(3);
                    }
                });

                //Set typefaces
                title.setTypeface(handWriting);
                body.setTypeface(handWriting);
                more.setTypeface(handWriting);
                name.setTypeface(handWriting);
                time.setTypeface(handWriting);

                parent.addView(view, i);

                Log.i("LoginActivity", "View added at index " + i + currentReview.title + currentReview.comment + currentReview.name);
            }
        }
    }

    //TODO doc
    public void expandDescription(final View v){
        if(!descriptionExpanded) {
            //Set line number to unlimited
            mDescription.setMaxLines(Integer.MAX_VALUE);

            //Animate expansion
            mAdditionalDetailsLayout.setVisibility(View.VISIBLE);

            mFAB.setImageDrawable(getResources().getDrawable(R.drawable.ic_minimize_white));
            descriptionExpanded = true;
        }else{
            //Set line limit
            mDescription.setMaxLines(getApplicationContext().getResources().getInteger(R.integer.lines));

            mFAB.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_white));


            //Animate collapse
            mAdditionalDetailsLayout.setVisibility(View.GONE);

            descriptionExpanded = false;
        }
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
