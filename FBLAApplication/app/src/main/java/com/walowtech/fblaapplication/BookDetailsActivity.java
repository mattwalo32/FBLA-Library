package com.walowtech.fblaapplication;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.DialogFragment;
import android.app.LoaderManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.walowtech.fblaapplication.Utils.DownloadDetailedImageLoader;
import com.walowtech.fblaapplication.Utils.DownloadImageLoader;
import com.walowtech.fblaapplication.Utils.DownloadJSONLoader;
import com.walowtech.fblaapplication.Utils.ErrorUtils;
import com.walowtech.fblaapplication.Utils.FirebaseIDService;
import com.walowtech.fblaapplication.Utils.NetworkJSONUtils;
import com.walowtech.fblaapplication.Utils.RequestPushService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import static android.view.View.GONE;

/**
 * This Activity shows detailed information about a book.
 *
 * The book's GID is expected as an extra, and upon retrieving it, detailed information
 * about the book is requested from the server. The response is then parsed and the information
 * is displayed on screen. The functionality to checkout, return, waitlist, and review books is
 * included.
 *
 * @author Matthew Walowski
 * @version 1.0
 * @since 1.0
 */

//Created 10/7/2017
public class BookDetailsActivity extends Activity  implements LoaderManager.LoaderCallbacks{

    private FloatingActionButton mFAB;
    private SimpleRatingBar mBookRating;

    private ImageView mBackgroundImage;
    private ImageView mBookImage;
    private ImageView mShareButton;
    private ImageView mLikeButton;
    private ImageView mInfoButton;
    private FrameLayout mLeaveReview;
    private RelativeLayout mBaseLayout;
    private LinearLayout mAdditionalDetailsLayout;
    private LinearLayout mDescriptionLayout;
    private LinearLayout mReviews;
    private RelativeLayout mReviewsLayout;
    private RelativeLayout mTopRowLayout;
    private RelativeLayout mSocialLayout;
    private ScrollView mScrollView;
    private TextView mSeeAll;
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
    private View mSeparator;

    public static Typeface handWriting;

    public URL requestURL;

    private final String PARAM_ACTION = "ACTION";
    private final String PARAM_GID = "GID";
    private final String PARAM_UID = "UID";
    private final String PARAM_PASSWORD = "PASSWORD";
    private final String PARAM_BID = "BID";
    private final String PARAM_RATING = "RATING";
    private final String PARAM_COMMENT = "COMMENT";
    private final String PARAM_TITLE = "TITLE";

    private final String VALUE_ACTION_RETRIEVE_DETAILED_DATA = "ACTION_RETRIEVE_DETAILED_BOOK_DATA";
    private final String VALUE_ACTION_ADD_REVIEW = "ACTION_ADD_REVIEW";
    private final String VALUE_ACTION_CHECKOUT = "ACTION_CHECKOUT_BOOK";
    private final String VALUE_ACTION_WAITLIST = "ACTION_ADD_TO_WAIT_LIST";
    private final String VALUE_ACTION_RETURN = "ACTION_RETURN_BOOK";
    private String VALUE_GID;
    private static int VALUE_UID;
    private static String VALUE_PASSWORD;
    private int VALUE_RATING;
    private String VALUE_COMMENT;
    private String VALUE_TITLE;

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
    private final String KEY_AUTH_SUCCESS = "validationSuccess";
    private final String KEY_MESSAGE = "message";

    private JSONObject jsonResponse;

    private Book currentBook;
    private Copy mCopy;

    private boolean descriptionExpanded;
    private boolean liked;
    private boolean hasReview;
    private boolean fromAccount;
    private boolean updating;
    private boolean checkedOut = true;
    private int userReviewRating = 0;
    private int likeIndex;

    ArrayList<Review> reviews = new ArrayList<>();

    private final int DOWNLOAD_DETAILED_JSON_LOADER = 0;
    private final int DOWNLOAD_DETAILED_IMAGE_LOADER = 1;
    private final int UPLOAD_COMMENT_LOADER = 2;
    private final int CHECKOUT_BOOK_LOADER = 3;
    private final int RETURN_BOOK_LOADER = 4;
    private final String GOOGLE_BASE_LINK = "https://books.google.com/books?vid=ISBN";

    private AlarmManager alarmManager;
    private RequestQueue queue;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_book_details);

        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        VALUE_UID = sharedPref.getInt("UID", -1);
        VALUE_PASSWORD = sharedPref.getString("PASSWORD", null);

        queue = Volley.newRequestQueue(this);

        //Create custom font typeface
        handWriting = Typeface.createFromAsset(getAssets(), "fonts/hand_writing.ttf");

        //Initialize all Views
        mFAB = (FloatingActionButton) findViewById(R.id.bd_fab_description);
        mBookRating = (SimpleRatingBar) findViewById(R.id.bd_ratingbar);
        mBackgroundImage = (ImageView) findViewById(R.id.bd_background_image);
        mScrollView = (ScrollView) findViewById(R.id.bd_scrollview);
        mDescription = (TextView) findViewById(R.id.bd_description);
        mTitle = (TextView) findViewById(R.id.bd_title);
        mSeeAll = (TextView) findViewById(R.id.bd_view_all_reviews);
        mReviews = (LinearLayout) findViewById(R.id.bd_reviews_view);
        mReviewsLayout = (RelativeLayout) findViewById(R.id.bd_reviews_layout);
        mSubTitle = (TextView) findViewById(R.id.bd_subtitle);
        mAuthors = (TextView) findViewById(R.id.bd_authors);
        mCheckout = (TextView) findViewById(R.id.bd_checkout);
        mAvgRating = (TextView) findViewById(R.id.bd_avg_rating);
        mNumRatings = (TextView) findViewById(R.id.bd_num_ratings);
        mBaseLayout = (RelativeLayout) findViewById(R.id.bd_base_layout);
        mBookImage = (ImageView) findViewById(R.id.bd_small_image);
        mTopRowLayout = (RelativeLayout) findViewById(R.id.bd_top_row_layout);
        mSocialLayout = (RelativeLayout) findViewById(R.id.bd_social_layout);
        mShareButton = (ImageView) findViewById(R.id.bd_share_button);
        mLikeButton = (ImageView) findViewById(R.id.bd_favorite);
        mInfoButton = (ImageView) findViewById(R.id.bd_info);
        mDescriptionLayout = (LinearLayout) findViewById(R.id.bd_description_layout);
        mSeparator = findViewById(R.id.bd_separator);

        mAdditionalDetailsLayout = (LinearLayout) findViewById(R.id.bd_additional_info);
        mFullTitle = (TextView) findViewById(R.id.bd_full_title);
        mFullAuthors = (TextView) findViewById(R.id.bd_full_authors);
        mSubject = (TextView) findViewById(R.id.bd_subject);
        mCopies = (TextView) findViewById(R.id.bd_copies);
        mISBN10 = (TextView) findViewById(R.id.bd_isbn10);
        mISBN13 = (TextView) findViewById(R.id.bd_isbn13);

        //Inflate and set leave comment view
        mLeaveReview = (FrameLayout) findViewById(R.id.bd_review_header);
        TextView mHeaderTitle = (TextView) mLeaveReview.findViewById(R.id.comment_h_title);
        TextView mHeaderBody = (TextView) mLeaveReview.findViewById(R.id.comment_h_body);
        TextView mHeaderString = (TextView) mLeaveReview.findViewById(R.id.comment_h_tv);
        mHeaderString.setTypeface(handWriting);
        mHeaderBody.setTypeface(handWriting);
        mHeaderTitle.setTypeface(handWriting);

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
        mSeeAll.setTypeface(handWriting);

        //Set elevations
        setElevation(mBaseLayout, (int)getResources().getDimension(R.dimen.elev_base_layout));
        setElevation(mTopRowLayout, (int)getResources().getDimension(R.dimen.elev_top_row_layout));
        setElevation(mDescriptionLayout, (int)getResources().getDimension(R.dimen.elev_description_layout));
        setElevation(mBookImage, (int)getResources().getDimension(R.dimen.elev_book_image));
        setElevation(mCheckout, (int)getResources().getDimension(R.dimen.elev_checkout));
        setElevation(mReviewsLayout, (int)getResources().getDimension(R.dimen.elev_reviews));
        setElevation(mSeparator, (int)getResources().getDimension(R.dimen.elev_social));
        setElevation(mLeaveReview, (int)getResources().getDimension(R.dimen.elev_leave_review));
        setElevation(mSocialLayout, (int)getResources().getDimension(R.dimen.elev_social));

        //Get Extras and initialize others
        descriptionExpanded = false;
        Intent thisIntent = getIntent();
        VALUE_GID = thisIntent.getStringExtra("GID");
        Bitmap bookCover = (Bitmap)thisIntent.getParcelableExtra("BOOK_IMAGE");
        fromAccount = thisIntent.getBooleanExtra("FROMACCOUNT", false);
        //Set Book Cover. Set background until better quality loaded
        if(bookCover != null) {
            mBookImage.setImageBitmap(bookCover);
            mBackgroundImage.setImageBitmap(bookCover);
        }

        //Set like button state
        liked = false;
        if (sharedPref.getBoolean("LIKED" + VALUE_GID, false)) {
            mLikeButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_red));
            liked = true;
        }

        //Set checkoutButton listener
        mCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (currentBook.copies != null) {
                        if (!checkedOut) {
                            DialogFragment bookFragment = SelectBookFragment.newInstance(BookDetailsActivity.this, currentBook.copies, currentBook.availableCopies);
                            bookFragment.show(getFragmentManager(), "TestDialog");
                        } else if (mCopy != null) {
                            Log.i("LoginActivity", "RETURNED");
                            returnBook(mCopy);
                        }
                    } else {
                        Toast.makeText(BookDetailsActivity.this, "Book is still loading.", Toast.LENGTH_SHORT).show();
                    }
                }catch(Exception E){
                    E.printStackTrace();
                    Toast.makeText(BookDetailsActivity.this, "Book is still loading.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Check for network connection
        if(NetworkJSONUtils.checkInternetConnection(this)) {
            //Retrieve JSON for Book Info
            updating = false;
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

        //Set on click listener for info button
        mInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentBook.ISBN13 != null){//If the data for ISBN has been retrieved
                    //Open browser with information
                    String bookLink = GOOGLE_BASE_LINK + currentBook.ISBN13;
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                    browserIntent.setData(Uri.parse(bookLink));
                    startActivity(browserIntent);
                }else{
                    Toast.makeText(BookDetailsActivity.this, "Book is still loading...", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        if(fromAccount) {
            Intent startMain = new Intent(this, MainActivity.class);
            startActivity(startMain);
        }else{
            super.onBackPressed();
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
        Log.i("LoginActivity", "Creating");
        //If downloading detailed JSON or uploading comment
        if(id == DOWNLOAD_DETAILED_JSON_LOADER || id == UPLOAD_COMMENT_LOADER || id == CHECKOUT_BOOK_LOADER || id == RETURN_BOOK_LOADER) {
            /*This can be used for uploading, because the data to be uploaded is coming from
            * the URL arguments and then JSON needs to be downloaded to figure the response
            * and success that was given*/

            return new DownloadJSONLoader(this, this, requestURL);
        } else if(id == DOWNLOAD_DETAILED_IMAGE_LOADER) {
            return new DownloadDetailedImageLoader(this, this, currentBook.thumbnail);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        if(!(data == null || data == " ")) {
            if (loader.getId() == DOWNLOAD_DETAILED_JSON_LOADER || loader.getId() == UPLOAD_COMMENT_LOADER || loader.getId() == CHECKOUT_BOOK_LOADER || loader.getId() == RETURN_BOOK_LOADER) {
                try {
                    jsonResponse = new JSONObject(data.toString());
                    parseJSON(jsonResponse);
                } catch (JSONException JSONE) {
                    JSONE.printStackTrace();
                    ErrorUtils.errorDialog(this, "Data Error", "There was an error with the data format. Please try again later.");
                    return;
                }
            }else if(loader.getId() == DOWNLOAD_DETAILED_IMAGE_LOADER){
                Bitmap image = (Bitmap) data;
                mBackgroundImage.setImageBitmap(image);
                mBookImage.setImageBitmap(image); // Sets cover in case book was called from context where no cover provided
            }
        }else{
            ErrorUtils.errorDialog(this, "Could not connect to server", "No information was retrieved from the server. Please try again later.");
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    /**
     * Sends request for book information based on GID received as extra.
     *
     * The GID of the book from the previous activity is passed to this activity
     * as an extra. That GID is used to create a URL that requests detailed information
     * about a book. The requested is initiated by utilizing volley.
     */
    public void retrieveDetailedBookInfo(){
        //Check for internet connection
        if(!NetworkJSONUtils.checkInternetConnection(this)) {
            ErrorUtils.errorDialog(BookDetailsActivity.this, "Network Error", "It seems you don't have any network connection. Reset your connection and try again.");
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

        //Build request for raw JSON data
        StringRequest jsonRequest = new StringRequest(Request.Method.GET, urlString, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    //When data is received, convert to JSONObject and try to parse
                    jsonResponse = new JSONObject(response);
                    parseJSON(jsonResponse);
                } catch (JSONException JSONE) {
                    //Print error if there was one
                    JSONE.printStackTrace();
                    ErrorUtils.errorDialog(BookDetailsActivity.this, "Data Error", "There was an error with the data format. Please try again later.");
                    return;
                }
            }
            }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Print error
                ErrorUtils.errorDialog(BookDetailsActivity.this, "Could not connect to the server", "Could not connect to the server at this time, please try again later.");
            }
        });
        //Start the JSON request
        queue.add(jsonRequest);
    }

    public void retrieveDetailedImage(){
        //Build request for raw JSON data
        ImageRequest imageRequest = new ImageRequest(currentBook.thumbnail, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap image) {
                //Image is returned
                mBackgroundImage.setImageBitmap(image);
                mBookImage.setImageBitmap(image); // Sets cover in case book was called from context where no cover provided
            }
        }, 5000, 5000, Bitmap.Config.RGB_565,
                new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Print error
                ErrorUtils.errorDialog(BookDetailsActivity.this, "Could not connect to the server", "Could not connect to the server at this time, please try again later.");
            }
        });
        //Start the JSON request
        queue.add(imageRequest);
    }

    /**
     * Parses a JSON object based on differing types of success codes.
     *   - With a success of 0, the error message is parsed and displayed as
     * a toast.
     *   - With a success of 1, the detailed book information is parse and
     *   the book information is updated.
     *
     *   - With a success of 11, the comment was successfully added and a
     *   a message from the server is parsed and displayed as a toast.
     *
     *   - With a success of 14, a book was successfully checked out and a
     *   message from the server is parsed and displayed as a toast.
     *
     *   - With a success of 15, a book was successfully added to a wait list
     *   and a message from the server is parsed and displayed as a toast.
     *
     * @param json The JSONObject to be parsed
     */
    public void parseJSON(JSONObject json){
        try {

            int success = json.getInt(KEY_SUCCESS);
            if(success == 0){
                //An error occurred - Server will handle message
                String message = json.getString(KEY_MESSAGE);
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }else if(success == 1){
                //Book Info successfully retrieved
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

                //Get UID to check if there are comments by current user. Also assume that there are no reviews initially
                SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                VALUE_UID = sharedPref.getInt("UID", -2);

                //Loop through copy details
                for(int i = 0; i < copyDetails.length(); i++){
                    JSONObject currentCopy = (JSONObject)copyDetails.get(i);

                    int BID = currentCopy.getInt(KEY_BID);
                    String checkoutTime = currentCopy.getString(KEY_CHECKOUT_TIME);
                    String returnTime = currentCopy.getString(KEY_RETURN_TIME);
                    int waitingListSize = currentCopy.getInt(KEY_WAITLIST_SIZE);
                    int UID = currentCopy.getInt(KEY_UID);

                    Copy curCopy = new Copy(BID, waitingListSize, checkoutTime, returnTime);
                    currentBook.copies.add(curCopy);
                    if (VALUE_UID == UID) {
                        mCheckout.setText("Return");
                        checkedOut = true;
                        mCopy = curCopy;
                        break;
                    }else{
                        checkedOut = false;
                        mCheckout.setText(getResources().getString(R.string.checkout));
                    }



                }
                hasReview = false;

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

                    //If the current user already added a review, display it at the top.
                    if(UID == VALUE_UID) {
                        currentBook.reviews.add(0, curReview);
                        hasReview = true;
                        userReviewRating = rating;
                    }else {
                        currentBook.reviews.add(curReview);
                    }
                }
                if(!updating)
                    getLoaderManager().initLoader(DOWNLOAD_DETAILED_IMAGE_LOADER, null, this);
                displayBookInfo();
            }else if(success == 11){
                //Comment successfully added
                boolean authSuccess = json.getBoolean(KEY_AUTH_SUCCESS);
                Log.i("LoginActivity", "AUTH SUCCESS: " + authSuccess);
                if(authSuccess){
                    String message = json.getString(KEY_MESSAGE);
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                    //Updates new info
                    Log.i("LoginActivity", "UPDATE INITIATED");
                    updating = true;
                    retrieveDetailedBookInfo();
                }else{
                    ErrorUtils.errorDialog(this, "Authentication Error", "The user's credentials could not be authenticated, so the review was not uploaded. Please try logging out and baack in.");
                }
            }else if(success == 14){
                //Book successfully checked out
                int BID = json.getInt(KEY_BID);
                SharedPreferences.Editor prefEditor = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE).edit();
                prefEditor.putInt("BOOK" + BID, BID);
                prefEditor.commit();

                String message = json.getString(KEY_MESSAGE);
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();

                checkedOut = true;
                mCheckout.setText("Return");

                configureReturnAlarm(BID);
            }else if(success == 15){
                //Successfully added to wait list
                int BID = json.getInt(KEY_BID);
                SharedPreferences.Editor prefEditor = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE).edit();
                prefEditor.putInt("WAITLIST" + BID, BID);
                prefEditor.commit();

                String message = json.getString(KEY_MESSAGE);
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }else if(success == 16){
                //Successfully returned
                int BID = json.getInt(KEY_BID);
                SharedPreferences pref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                SharedPreferences.Editor prefEditor = pref.edit();
                prefEditor.remove("BOOK" + BID);
                prefEditor.commit();

                String message = json.getString(KEY_MESSAGE);
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                checkedOut = false;
                mCheckout.setText("Checkout");

                Log.i("LoginActivity", "SUCCESS RETURNED");
            }
        }catch(JSONException JSONE){
            JSONE.printStackTrace();
            ErrorUtils.errorDialog(this, "Malformed JSON Error", "A malformed response was recieved from the server. Please try again later.");
        }
    }

    /**
     * Creates an alarm 9 days from current time that will send a push notification.
     *
     * @param BID The BID the the alarm is regarding.
     */
    public void configureReturnAlarm(int BID){
        alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);

        //Configure intent with proper information
        Intent intent = new Intent(this, RequestPushService.class);

        intent.putExtra("TO", FirebaseInstanceId.getInstance().getToken());
        intent.putExtra("BODY", "This is just a reminder that a book you have checked out is due in 5 days.");
        intent.putExtra("TITLE", "Don't forget to return your book!");
        intent.putExtra("BID", BID);
        intent.putExtra("ALARM_TYPE", getResources().getInteger(R.integer.ALARM_5_DAY_WARNING));
        PendingIntent serviceIntent = PendingIntent.getService(this, getResources().getInteger(R.integer.ALARM_5_DAY_WARNING) + BID, intent, 0);

        //Calculate ring time from now in ms
        long currentTime = System.currentTimeMillis();
        long oneDay = 60 * 60 * 24 * 1000;
        long nineDays = oneDay * 9;

        //Set alarm
        alarmManager.set(
                AlarmManager.RTC,
                currentTime + nineDays,
                serviceIntent
        );

        //Save alarm so it can be reset in case of reboot
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor prefEditor = sharedPreferences.edit();
        prefEditor.putString("ALARM" +  BID + "-TO", FirebaseInstanceId.getInstance().getToken());
        prefEditor.putString("ALARM" +  BID + "-BODY", "This is just a reminder that a book you have checked out is due in 5 days.");
        prefEditor.putString("ALARM" +  BID + "-TITLE", "Don't forget to return your book!");
        prefEditor.putInt("ALARM-BID" + BID, BID);
        prefEditor.putInt("ALARM" +  BID + "-TYPE", getResources().getInteger(R.integer.ALARM_5_DAY_WARNING));
        prefEditor.putLong("ALARM" +  BID + "-RING-TIME", currentTime + nineDays);
        prefEditor.apply();
    }

    /**
     * Sets the Text of all TextViews based on the current
     * book information. Also sets values of any rating bars and
     * adds the reviews to the screen.
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
        int upperBound = ((reviews.size() - 1) > 1)?2:(reviews.size()-1);
        reviewAdapter(reviews, mReviews, 0, upperBound, true);

        //If there are 3 reviews or less, then hide the see all button
        if(reviews.size() < 4)
            mSeeAll.setVisibility(GONE);
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
                    e.printStackTrace();
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
                body.setText(currentReview.comment);
                name.setText(currentReview.name);
                time.setText(currentReview.formatTimestamp(currentReview.timestamp));
                sRatingBar.setRating(currentReview.rating);

                int offset = (int)(getResources().getDimension(R.dimen.comment_profile_diameter) + getResources().getDimension(R.dimen.margin));
                title.setPadding(offset, 0, 0, 0);

                //If there is no title, set title to amount of stars
                if(title.getText() == null || title.getText() == "" || title.getText() == "null" || title.getText() == " "){
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
            }
        }
    }

    /**
     * Changes the state of the book description. If the state is
     * not expanded when invoked, then it will set the details visibility
     * to visible. If the state is expanded, then it will set the details
     * visibility to gone.
     *
     * @param v The view that invoked the method when clicked on.
     */
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
     * Method is called when checkout or waitlist is selected. A URI is
     * constructed with URL arguments detailing the UID, password, BID,
     * and the action to perform based on weather or not the book is being
     * waitlisted. The URI is converted to a URL and a loader to send the
     * request and parse the response is created.
     *
     * @param copy The copy of the book to checkout or waitlist.
     * @param waitList Determines weather the book is to be checked out or waitlisted.
     */
    public void checkoutBook(Copy copy, Boolean waitList){

        int BID = copy.BID;

        //Build URI
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(SCHEME)
                .authority(BASE_URL)
                .appendPath(PATH0)
                .appendPath(PATH1)
                .appendPath(PATH2);

        if(!waitList)
            builder.appendQueryParameter(PARAM_ACTION, VALUE_ACTION_CHECKOUT);
        else
            builder.appendQueryParameter(PARAM_ACTION, VALUE_ACTION_WAITLIST);

        builder.appendQueryParameter(PARAM_BID, Integer.toString(BID))
                .appendQueryParameter(PARAM_UID, Integer.toString(VALUE_UID))
                .appendQueryParameter(PARAM_PASSWORD, VALUE_PASSWORD)
                .build();

        String urlString = builder.toString();


        //Try to create URL from Uri
        try{
            requestURL = new URL(urlString);
        }catch(MalformedURLException MURLE){
            MURLE.printStackTrace();
            ErrorUtils.errorDialog(this, "There was an error with the url", "Currently the server can not be reached. Make sure your username and password are entered correctly");
            return;
        }


        //Start Loader
        getLoaderManager().initLoader(CHECKOUT_BOOK_LOADER, null, this);
    }

    /**
     * Method is called when return button is pressed. A URI is
     * constructed with URL arguments detailing the UID, password, BID,
     * and the action to return the book. The URI is converted to a URL and a loader to send the
     * request and parse the response is created.
     *
     * @param copy The copy of the book to be returned
     */
    public void returnBook(Copy copy){
        int BID = copy.BID;

        //Build URI
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(SCHEME)
                .authority(BASE_URL)
                .appendPath(PATH0)
                .appendPath(PATH1)
                .appendPath(PATH2)
                .appendQueryParameter(PARAM_ACTION, VALUE_ACTION_RETURN)
                .appendQueryParameter(PARAM_BID, Integer.toString(BID))
                .appendQueryParameter(PARAM_UID, Integer.toString(VALUE_UID))
                .appendQueryParameter(PARAM_PASSWORD, VALUE_PASSWORD)
                .build();

        String urlString = builder.toString();


        //Try to create URL from Uri
        try{
            requestURL = new URL(urlString);
        }catch(MalformedURLException MURLE){
            MURLE.printStackTrace();
            ErrorUtils.errorDialog(this, "There was an error with the url", "Currently the server can not be reached. Make sure your username and password are entered correctly");
            return;
        }


        //Start Loader
        getLoaderManager().initLoader(RETURN_BOOK_LOADER, null, this);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor prefEditor = sharedPreferences.edit();
        prefEditor.remove("ALARM" + BID + "-TO");
        prefEditor.remove("ALARM" + BID + "-BODY");
        prefEditor.remove("ALARM" + BID + "-TITLE");
        prefEditor.remove("ALARM-BID" + BID);
        prefEditor.remove("ALARM" + BID + "-TYPE");
        prefEditor.remove("ALARM" + BID + "-RING-TIME");
        prefEditor.apply();
    }

    /**
     * Expands the current Reviews if possible. The last visible index
     * is retrieved and compared to the last possible index. If the last possible
     * index is greater than the last visible index, then all missing views
     * are inflated.
     *
     * @param v The view that invoked the method.
     */
    public void viewAllReviews(View v){
        //Check if initialized to prevent null pointer
        if(currentBook.reviews != null) {
            int startIndex = mReviews.getChildCount() - 1; //Get index of last visible view
            int endIndex = currentBook.reviews.size() - 1; //Get index of last possible view

            //If there exist more views than are shown
            if(endIndex > startIndex){
                reviewAdapter(currentBook.reviews, mReviews, startIndex, endIndex, true);
                mSeeAll.setVisibility(GONE);
            }
        }
    }

    /**
     * Adds book to liked and changes icon. If the book is already
     * liked then the book is removed from the 'liked' list that is
     * stored in SharedPreferences and the icon is changed back.
     *
     * @param v The caller view
     */
    public void likeBook(View v) {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = sharedPreferences.edit();

        try {
            //If not already liked
            if (!liked) {
                //Get current amount of liked books and put like info
                prefEditor.putBoolean("LIKED" + currentBook.GID, true);
                prefEditor.commit();
                mLikeButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_red));
                liked = true;
            } else {
                prefEditor.remove("LIKED" + currentBook.GID);
                prefEditor.commit();
                mLikeButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_border_black));
                liked = false;
            }
        }catch(Exception e){
            e.printStackTrace();
            Toast.makeText(this, "Book is still loading", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Submits comment
     *
     * Creates URI and URL to request to add the comment. Then starts loader
     * and clears EditTexts.
     *
     * @param v The caller view
     */
    public void addComment(View v) {
        //Get review views
        SimpleRatingBar rating = (SimpleRatingBar) mLeaveReview.findViewById(R.id.comment_h_rating);
        EditText title = (EditText) mLeaveReview.findViewById(R.id.comment_h_title);
        EditText comment = (EditText) mLeaveReview.findViewById(R.id.comment_h_body);


        //Set params
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        VALUE_GID = currentBook.GID;
        VALUE_UID = sharedPref.getInt("UID", -1);
        VALUE_PASSWORD = sharedPref.getString("PASSWORD", null);
        VALUE_RATING = ((int) rating.getRating());
        VALUE_TITLE = title.getText().toString();
        VALUE_COMMENT = comment.getText().toString();

        //Verify info
        if (VALUE_GID == null || VALUE_UID == -1) {
            ErrorUtils.errorDialog(this, "Error", "Information could not be retrieved from memory. Try logging out and logging back in.");
            return;
        } else if (VALUE_PASSWORD == null) {
            Toast.makeText(this, "No saved password was found. Please log out then log back in.", Toast.LENGTH_LONG).show();
        } else if (VALUE_RATING <= 0 || VALUE_RATING > 5) {
            Toast.makeText(this, "Please select a rating", Toast.LENGTH_SHORT).show();
            return;
        }


        //Build URI
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(SCHEME)
                .authority(BASE_URL)
                .appendPath(PATH0)
                .appendPath(PATH1)
                .appendPath(PATH2)
                .appendQueryParameter(PARAM_ACTION, VALUE_ACTION_ADD_REVIEW)
                .appendQueryParameter(PARAM_GID, VALUE_GID)
                .appendQueryParameter(PARAM_UID, Integer.toString(VALUE_UID))
                .appendQueryParameter(PARAM_PASSWORD, VALUE_PASSWORD)
                .appendQueryParameter(PARAM_RATING, Integer.toString(VALUE_RATING));

        if (!(VALUE_TITLE.trim().length() == 0)) {
            builder.appendQueryParameter(PARAM_TITLE, VALUE_TITLE);
        }

        if (!(VALUE_COMMENT.trim().length() == 0)) {
            builder.appendQueryParameter(PARAM_COMMENT, VALUE_COMMENT);
        }

        builder.build();
        String urlString = builder.toString();


        //Try to create URL from Uri
        try {
            requestURL = new URL(urlString);
        } catch (MalformedURLException MURLE) {
            MURLE.printStackTrace();
            ErrorUtils.errorDialog(this, "There was an error with the url", "Currently the server can not be reached. Make sure your username and password are entered correctly");
            return;
        }


        //Start Loader
        if (updating)
            getLoaderManager().restartLoader(UPLOAD_COMMENT_LOADER, null, this);
        else
            getLoaderManager().initLoader(UPLOAD_COMMENT_LOADER, null, this);
    }

    /**
     * Handles the click on the facebook share button. Allows
     * the user to share information on facebook via facebook API
     *
     * @param view The calling view
     */
    public void facebookPost(View view){
        //ShareLinkContent content = new ShareLinkContent.Builder()
    }

    /**
     * Sets elevation of a view using ViewCompat
     *
     * @param view The view to set the elevation of.
     * @param elevation The height to set the elevation to.
     */
    private void setElevation(View view, int elevation){
        ViewCompat.setElevation(view, elevation);
    }
}