package com.walowtech.fblaapplication;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.walowtech.fblaapplication.Utils.DownloadImageArray;
import com.walowtech.fblaapplication.Utils.DownloadJSONArray;
import com.walowtech.fblaapplication.Utils.DownloadJSONLoader;
import com.walowtech.fblaapplication.Utils.ErrorUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.GONE;
import static com.walowtech.fblaapplication.MainActivity.handWriting;

public class AccountActivity extends Activity implements LoaderManager.LoaderCallbacks {

    CircleImageView mProfile;
    LinearLayout mRowsLayout;
    LinearLayout mFavoritesLayout;
    RecyclerView mRecyclerLiked;
    RelativeLayout mTopRowLayout;
    RelativeLayout mNoLikedItemsLayout;
    RelativeLayout mBottomRowLayout;
    TextView mUsername;
    TextView mEmail;
    TextView mFavoritesTitle;
    TextView mNoneLiked;

    SharedPreferences sharedPreferences;

    BookAdapter likedAdapter;

    private final String BASE_URL = "walowtech.com";
    private final String PATH0 = "apis";
    private final String PATH1 = "FBLALibrary";
    private final String PATH2 = "api.php";
    private final String SCHEME = "https";

    private final String PARAM_ACTION = "ACTION";
    private final String PARAM_SEARCH_BID = "BID";
    private final String PARAM_GID = "GID";
    private final String PARAM_SEARCH_ITEM = "SEARCHITEM";

    private final String VALUE_ACTION_RETRIEVE_DETAILED_DATA = "ACTION_RETRIEVE_DETAILED_BOOK_DATA";
    private final String VALUE_ACTION_SEARCH = "ACTION_RETRIEVE_BOOK_DATA";
    private final String VALUE_SEARCH_ITEM = "BID";
    private int VALUE_SEARCH_BID;
    private String VALUE_GID;

    private final String KEY_AVG_RATING = "AverageRating";
    private final String KEY_AUTHORS = "Authors";
    private final String KEY_CHECKOUT_TIME = "CheckoutTimestamp";
    private final String KEY_RETURN_TIME = "ReturnTimestamp";
    private final String KEY_GID = "GID";
    private final String KEY_JSON = "JSON";
    private final String KEY_MESSAGE = "message";
    private final String KEY_NUM_SUBJECTS = "numSubjects";
    private final String KEY_SMALL_THUMBNAIL = "SmallThumbnail";
    private final String KEY_SUBJECT = "Subject";
    private final String KEY_SUCCESS = "Success";
    private final String KEY_TITLE = "Title";
    private final String KEY_SUBTITLE = "SubTitle";

    ArrayList<Copy> checkedOut = new ArrayList<>();
    ArrayList<Copy> waitListed = new ArrayList<>();
    ArrayList<Book> liked = new ArrayList<>();
    ArrayList<ArrayList<String>> imageURLs = new ArrayList<>();
    ArrayList<View> checkedOutViews = new ArrayList<>();
    ArrayList<View> waitListedViews = new ArrayList<>();

    List<ArrayList> rows = new ArrayList<>();

    ArrayList<ArrayList<URL>> JSONRequestURLs;

    private final int DOWNLOAD_BOOK_JSON_LOADER = 0;
    private final int DOWNLOAD_IMAGE_ARRAY_LOADER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        mProfile = (CircleImageView) findViewById(R.id.aa_profile);
        mRowsLayout = (LinearLayout) findViewById(R.id.aa_ll_rows);
        mFavoritesLayout = (LinearLayout) findViewById(R.id.aa_favorites);
        mRecyclerLiked = (RecyclerView) findViewById(R.id.aa_rv_books);
        mTopRowLayout = (RelativeLayout) findViewById(R.id.aa_layout_top_row);
        mNoLikedItemsLayout = (RelativeLayout) findViewById(R.id.book_row_no_liked_items);
        mBottomRowLayout = (RelativeLayout) findViewById(R.id.aa_layout_bottom_row);
        mUsername = (TextView) findViewById(R.id.aa_username);
        mEmail = (TextView) findViewById(R.id.aa_email);
        mFavoritesTitle = (TextView) findViewById(R.id.aa_tv_title);
        mNoneLiked = (TextView) findViewById(R.id.book_row_none_liked);


        mUsername.setTypeface(handWriting);
        mEmail.setTypeface(handWriting);
        mFavoritesTitle.setTypeface(handWriting);
        mNoneLiked.setTypeface(handWriting);

        //Set name and email text
        sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String name = sharedPreferences.getString("NAME", null);
        String email = sharedPreferences.getString("EMAIL", null);
        mUsername.setText(name);
        mEmail.setText(email);

        inflateRows(); //Inflate objects

        getBookJSON(); //Get book data

        //Adapted Liked Row
        likedAdapter = new BookAdapter(this, liked);
        mRecyclerLiked.setAdapter(likedAdapter);
        mRecyclerLiked.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        //Set elevations
        setElevation(mTopRowLayout, 12);
        setElevation(mFavoritesLayout, 6);
        //setElevation(mProfile, 40);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        if(id == DOWNLOAD_BOOK_JSON_LOADER) {
            return new DownloadJSONArray(this, JSONRequestURLs);
        }else if(id == DOWNLOAD_IMAGE_ARRAY_LOADER) {
            Log.i("LoginActivity", "ARRAY LOADER");
            return new DownloadImageArray(this, imageURLs);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        /*for(int i = 0; i < json.size(); i++){
            for(int j = 0; j < json.get(i).size(); j++){
                Log.i("LoginActivity", json.get(i).get(j).toString());
            }
        }*/
        if(loader.getId() == DOWNLOAD_BOOK_JSON_LOADER){
            ArrayList<ArrayList<JSONObject>> json = (ArrayList<ArrayList<JSONObject>>) data;
            if(!json.isEmpty())
                parseJSON(json);
        }else if(loader.getId() == DOWNLOAD_IMAGE_ARRAY_LOADER){
            ArrayList<ArrayList<Bitmap>> images = (ArrayList<ArrayList<Bitmap>>) data;
            if(!images.isEmpty())
                updateViews(images);
        }

    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    /**
     * Gets information about books saved in SharedPreferences and Inflates the three rows for
     * AccountActivity and adapts all necessary information, such as title and typefaces. Then
     * the inflateBooks method is invoked on each row. Finally, the bounds of the row's parent
     * are updated so that the position of the FAB is adjusted accordingly.
     */
    private void inflateRows(){
        //Clear all lists frm previous times
        checkedOut.clear();
        waitListed.clear();
        liked.clear();
        checkedOutViews.clear();
        waitListedViews.clear();
        TextView title = null;

        //Get count of books checked out, waitlisted, and liked
        Map<String,?> keys = sharedPreferences.getAll();

        //Loop through all preferences and find any with keys BOOK, WAITLIST, or LIKED
        for(Map.Entry<String, ?> entry : keys.entrySet()){
            if(entry.getKey().contains("BOOK")){
                checkedOut.add(new Copy(Integer.parseInt(entry.getValue().toString())));
            }else if(entry.getKey().contains("WAITLIST")){
                waitListed.add(new Copy(Integer.parseInt(entry.getValue().toString())));
            }else if(entry.getKey().contains("LIKED")){
                String key = entry.getKey();
                String GID = key.replace("LIKED", "");
                liked.add(new Book(null, null, GID, null, 0f));
                mNoLikedItemsLayout.setVisibility(GONE);
            }
        }

        //Get amounts of items
        int amountCheckedOut = checkedOut.size();
        int amountWaitListed = waitListed.size();

        //Add to ArrayList containing all items
        rows.add(checkedOut);
        rows.add(waitListed);

        LayoutInflater inflater = getLayoutInflater();

        //Iterate through all items and inflate rows and corresponding books
        for(int i = 0; i < rows.size(); i++) {
            ArrayList curList = rows.get(i);
            View curView = null;
            String curTitle = null;

            int resource = (curList.size() < 1) ? R.layout.account_book_row_empty : //Determine if res layout should be 0, 1, or 2+
                    R.layout.account_book_row_single_book;

            curView = inflater.inflate(resource, mRowsLayout, false); //inflate the row
            LinearLayout parent = (LinearLayout) curView.findViewById(R.id.aa_layout_books);
            title = (TextView) curView.findViewById(R.id.book_row_header);
            setElevation(curView, 8); //Set elevation of row

            if (curList.size() >= 1) { //If there is one item or more
                if (i == 0) { //First item always checked out books
                    curTitle = amountCheckedOut + " book" + ((amountCheckedOut > 1) ? "s" : "") + " checked out"; //Set title
                    inflateBooks(parent, true, amountCheckedOut); //Inflate books in row
                } else { //Second item always waitlisted books
                    curTitle = "In line for " + amountWaitListed + " book" + ((amountWaitListed > 1) ? "s" : ""); //Set title
                    inflateBooks(parent, false, amountWaitListed); //Inflate books in row
                }
            }else{ //If there are no items
                curTitle = i == 0 ? "You don't have any books checked out yet.": "You aren't on the waiting list for any books currently.";
                TextView header = (TextView) curView.findViewById(R.id.book_row_title);
                String headerText = i == 0 ? "Checked Out" : "Wait List";
                header.setText(headerText);
                header.setTypeface(handWriting);
            }

            title.setText(curTitle); //Set title text
            title.setTypeface(handWriting); //Set title font

            mRowsLayout.addView(curView); //Add rows to screen
        }
    }

    /**
     * Inflates a predetermined number of books for each row, and populates
     * ArrayLists with references to each view. If there is more than 1 item, then
     * a random effect with random margins are applied to each subsequent book item.
     *
     * @param parentView The parent of the book layout
     * @param checkedOut A boolean specifying if the row to inflate is the checked out row. This
     *                   determines where to save the view references to.
     * @param numViews The number of views to inflate
     */
    public void inflateBooks(ViewGroup parentView, boolean checkedOut, int numViews){
        LayoutInflater inflater = getLayoutInflater();

        if(numViews >= 1) { //If there are views to inflate
            for (int i = 0; i < numViews; i++) {
                View row = inflater.inflate(R.layout.book_row_item, parentView, false);
                if(checkedOut) {
                    checkedOutViews.add(row); //Get reference to layout to change later
                }else{
                    waitListedViews.add(row); //Get reference to layout to change later
                }
                parentView.addView(row);
            }
        }
    }

    /**
     * Creates an ArrayList of URLs, one for each book in each category,
     * and stores them in a class wide variable that is accessed later and
     * passed to the loader after it is created during onCreateLoader.
     */
    public void getBookJSON() {

        JSONRequestURLs = new ArrayList<>();
        URL currentURL;

        for (int i = 0; i < 2; i++) {
            ArrayList<Copy> currentArray;
            currentArray = (i == 0) ? checkedOut : waitListed;
            JSONRequestURLs.add(new ArrayList<URL>());

            for (int j = 0; j < currentArray.size(); j++) {
                VALUE_SEARCH_BID = currentArray.get(j).BID;

                Uri.Builder builder = new Uri.Builder();
                builder.scheme(SCHEME)
                        .authority(BASE_URL)
                        .appendPath(PATH0)
                        .appendPath(PATH1)
                        .appendPath(PATH2)
                        .appendQueryParameter(PARAM_ACTION, VALUE_ACTION_SEARCH)
                        .appendQueryParameter(PARAM_SEARCH_ITEM, VALUE_SEARCH_ITEM)
                        .appendQueryParameter(PARAM_SEARCH_BID, Integer.toString(VALUE_SEARCH_BID))
                        .build();
                String urlString = builder.toString();

                try {
                    currentURL = new URL(urlString);
                } catch (MalformedURLException MURLE) {
                    ErrorUtils.errorDialog(this, "There was an error with the url", "Currently the server can not be reached. Make sure your username and password are entered correctly");
                    return;
                }
                JSONRequestURLs.get(i).add(currentURL);
            }
        }

        if (!liked.isEmpty()) {
            for(int i = 0; i < liked.size(); i++) {
                JSONRequestURLs.add(new ArrayList<URL>());

                VALUE_GID = liked.get(i).GID;

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

                try {
                    currentURL = new URL(urlString);
                } catch (MalformedURLException MURLE) {
                    ErrorUtils.errorDialog(this, "There was an error with the url", "Currently the server can not be reached. Make sure your username and password are entered correctly");
                    return;
                }

                JSONRequestURLs.get(2).add(currentURL);
            }
        }


        /*for(int i = 0; i < JSONRequestURLs.size(); i++){
            for(int j = 0; j < JSONRequestURLs.get(i).size(); j++){
                Log.i("LoginActivity", JSONRequestURLs.get(i).get(j).toString());
            }
        }*/

        getLoaderManager().initLoader(DOWNLOAD_BOOK_JSON_LOADER, null, this);
    }

    /**
     * Updates the ImageViews and ProgressBars. Sets the ImageView bitmap to
     * the specified bitmap by looping through the 2-D ArrayList and calling setImageBitmap on
     * each ImageView. Then the ProgressBar is set to invisible.
     *
     * @param images A 2-D array list containing Bitmaps. The first dimension is a list of list of
     *               bitmaps. The first element is reserved for a list of bitmaps for the checked
     *               out row. The second element is reserved for a list of bitmaps for the wait list
     *               row. The third element is reserved for a list of bitmaps for the favorites row.
     */
    public void updateViews(ArrayList<ArrayList<Bitmap>> images){
        for(int i = 0; i < images.size(); i++){
            for(int j = 0; j < images.get(i).size(); j++){
                if( i != 2) {
                    try {
                        ArrayList<View> currentImageViews;
                        currentImageViews = (i == 0) ? checkedOutViews : waitListedViews;
                        ImageView imageView = (ImageView) currentImageViews.get(j).findViewById(R.id.book_image);
                        imageView.setImageBitmap(images.get(i).get(j));
                        ProgressBar loadingIndicator = (ProgressBar) currentImageViews.get(j).findViewById(R.id.aa_book_progress);
                        loadingIndicator.setVisibility(GONE);
                        ArrayList<Copy> currentList = i == 0 ? checkedOut : waitListed;
                        currentList.get(j).bitmap = images.get(i).get(j);
                    }catch(Exception e){
                        //TODO catch
                    }
                }else{
                    try {
                        liked.get(j).coverSmall = images.get(i).get(j);
                    }catch(Exception e){
                        //TODO catch
                    }
                }
            }
        }

        likedAdapter.notifyDataSetChanged();
    }

    //TODO doc
    public void parseJSON(ArrayList<ArrayList<JSONObject>> JSONArray){
        for(int i = 0; i < JSONArray.size(); i++) {
            imageURLs.add(new ArrayList<String>());
            for (int j = 0; j < JSONArray.get(i).size(); j++){
                JSONObject json = JSONArray.get(i).get(j);
                try {
                    int success = json.getInt(KEY_SUCCESS);
                    if(success == 0) {
                        //An error occurred - Server will handle message
                        String message = json.getString(KEY_MESSAGE);
                        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                    } else if (success == 1) {
                        //Book Info successfully retrieved
                        //Get detailed book info
                        JSONObject jsonResponse = json.getJSONObject(KEY_JSON);
                        liked.get(j).title = jsonResponse.getString(KEY_TITLE);
                        liked.get(j).subject = jsonResponse.getString(KEY_SUBJECT);
                        liked.get(j).authors = jsonResponse.getString(KEY_AUTHORS);
                        liked.get(j).smallThumbnail = jsonResponse.getString(KEY_SMALL_THUMBNAIL);
                        String averageRating = jsonResponse.getString(KEY_AVG_RATING);
                        liked.get(j).averageRating = Float.parseFloat(averageRating);

                        imageURLs.get(i).add(liked.get(j).smallThumbnail);

                        likedAdapter.notifyDataSetChanged();
                    }else if(success == 21){
                        //Searched for book info successfully ArrayList<Copy> currentList = i == 0 ? checkedOut : waitListed;
                        ArrayList<Copy> currentList = i == 0 ? checkedOut : waitListed;

                        JSONObject jsonResponse = json.getJSONObject(KEY_JSON);
                        currentList.get(j).title = jsonResponse.getString(KEY_TITLE);
                        currentList.get(j).subtitle = jsonResponse.getString(KEY_SUBTITLE);
                        currentList.get(j).GID = jsonResponse.getString(KEY_GID);
                        currentList.get(j).returnTime = jsonResponse.getString(KEY_RETURN_TIME);
                        currentList.get(j).authors = jsonResponse.getString(KEY_AUTHORS);
                        currentList.get(j).averageRating = Float.parseFloat(Double.toString(jsonResponse.getDouble(KEY_AVG_RATING)));
                        currentList.get(j).thumbnail = jsonResponse.getString(KEY_SMALL_THUMBNAIL);

                        imageURLs.get(i).add(currentList.get(j).thumbnail);

                        updateData(i, j, currentList);
                    }
                } catch (JSONException JSONE) {
                    Log.i("LoginActivity", "EXCEPTION"); //TODO catch
                }
            }
        }

       /* for(int i = 0; i < imageURLs.size(); i++){
            for(int j = 0; j < imageURLs.get(i).size(); j++){
                Log.i("LoginActivity", "BOOK IMAGE " + imageURLs.get(i).get(j));
            }
        }*/
        getLoaderManager().initLoader(DOWNLOAD_IMAGE_ARRAY_LOADER, null, this);
    }

    /**
     * Updates the data displayed in each row. Sets the text of the data
     * and the typefaces of it. Sets OnClickListener to the imageView
     *
     * @param i The first loop iteration. Used to determine which row to adapt to.
     * @param j The second loop iteration. Used to determine which sub-row to adapt to.
     * @param currentList The current list of copies. This is the actual information that gets adapted.
     */
    private void updateData(final int i, final int j, final ArrayList<Copy> currentList){
        ArrayList<View> viewList = i == 0 ? checkedOutViews : waitListedViews;
        View baseView = viewList.get(j);
        TextView bookDueDate = (TextView) baseView.findViewById(R.id.book_due_date);
        TextView bookTitle = (TextView) baseView.findViewById(R.id.book_title);
        TextView bookSubtitle = (TextView) baseView.findViewById(R.id.book_subtitle);
        TextView bookAuthors = (TextView) baseView.findViewById(R.id.book_authors);
        ImageView image = (ImageView) baseView.findViewById(R.id.book_image);

        String dueText = i == 0 ? "Due: " : "ETA: ";
        dueText += currentList.get(j).formatTimestamp(currentList.get(j).returnTime);

        bookDueDate.setText(dueText);
        bookTitle.setText(currentList.get(j).title);
        bookSubtitle.setText(currentList.get(j).subtitle);
        bookAuthors.setText(currentList.get(j).authors);

        bookDueDate.setTypeface(handWriting);
        bookTitle.setTypeface(handWriting);
        bookSubtitle.setTypeface(handWriting);
        bookAuthors.setTypeface(handWriting);

        if(currentList.get(j).subtitle.equals("")){
            bookSubtitle.setVisibility(GONE);
        }

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String GID = currentList.get(j).GID;
                Intent bookDetailsIntent = new Intent(AccountActivity.this, BookDetailsActivity.class);

                    /*android.support.v4.util.Pair<View, String> p1 = android.support.v4.util.Pair.create((View)image, getString(R.string.trans_iv_book_cover));
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, p1);
                    BitmapDrawable drawable = (BitmapDrawable) image.getDrawable();
                    Bitmap bitmap;
                    try {
                        bitmap = drawable.getBitmap();
                    }catch(NullPointerException NPE){
                        bitmap = null;
                    }*/

                bookDetailsIntent.putExtra("GID", GID);
                bookDetailsIntent.putExtra("BOOK_IMAGE", currentList.get(j).bitmap);
                startActivity(bookDetailsIntent/*, options.toBundle()*/);
            }
        });
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


    /**
     * The adapter between the books and their subject row.
     *
     * This adapter inflates each individual book for each row.
     *
     * @author Matthew Walowski
     * @version 1.0
     * @since 1.0
     *
     * Created 9/18/17
     */
    public class BookAdapter extends RecyclerView.Adapter<BookAdapter.MyViewHolder>{

        private LayoutInflater inflater;
        ArrayList<Book> books = new ArrayList<>();
        Context context;

        public BookAdapter(Context context, ArrayList<Book> books){
            inflater = LayoutInflater.from(context);
            this.books = books;
            this.context = context;
        }

        @Override
        public BookAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //Inflate and return viewholder
            View view = inflater.inflate(R.layout.book_category_item, parent, false);
            BookAdapter.MyViewHolder holder = new BookAdapter.MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final BookAdapter.MyViewHolder holder, final int position) {
            //Get current item and set text, typeface, and image
            final Book currentBook = books.get(position);
            holder.rating.setText(String.format("%.01f", currentBook.averageRating));
            holder.rating.setTypeface(handWriting);

            books.get(position).imageView = holder.image;

            //If a cover has been downloaded, remove loading indicator
            if(currentBook.coverSmall != null) {
                holder.image.setImageBitmap(currentBook.coverSmall);
                holder.progressBar.setVisibility(GONE);
            }

            if(currentBook.GID != null){
                currentBook.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String GID = currentBook.GID;
                        Intent bookDetailsIntent = new Intent(AccountActivity.this, BookDetailsActivity.class);

                    /*android.support.v4.util.Pair<View, String> p1 = android.support.v4.util.Pair.create((View)image, getString(R.string.trans_iv_book_cover));
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, p1);*/
                        BitmapDrawable drawable = (BitmapDrawable) currentBook.imageView.getDrawable();
                        Bitmap bitmap;
                        try {
                            bitmap = drawable.getBitmap();
                        }catch(NullPointerException NPE){
                            bitmap = null;
                        }

                        bookDetailsIntent.putExtra("GID", GID);
                        bookDetailsIntent.putExtra("BOOK_IMAGE", bitmap);
                        startActivity(bookDetailsIntent/*, options.toBundle()*/);
                    }
                });
            }

            //Set books elevation
            setElevation(holder.book, 4);
        }

        @Override
        public int getItemCount() {
            return books.size();
        }

        /**
         * Serves as a ViewHolder for RecyclerView, BookAdapter
         *
         * @Author Matthew Walowski
         * @version 1.0
         * @since 1.0
         */
        //Created 9/18/17
        class MyViewHolder extends RecyclerView.ViewHolder{

            TextView rating;
            ImageView image;
            RelativeLayout book;
            ProgressBar progressBar;

            public MyViewHolder(View itemView) {
                super(itemView);
                rating = (TextView) itemView.findViewById(R.id.m_tv_rating);
                image = (ImageView) itemView.findViewById(R.id.m_iv_book_cover);
                book = (RelativeLayout) itemView.findViewById(R.id.m_rl_book);
                progressBar = (ProgressBar) itemView.findViewById(R.id.m_pb_book_progress);
            }
        }
    }

}