package com.walowtech.fblaapplication;

import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.MatrixCursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.walowtech.fblaapplication.Utils.DownloadImageLoader;
import com.walowtech.fblaapplication.Utils.DownloadJSONLoader;
import com.walowtech.fblaapplication.Utils.ErrorUtils;
import com.walowtech.fblaapplication.Utils.NetworkJSONUtils;
import com.walowtech.fblaapplication.Utils.SlideshowAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static android.view.View.GONE;

/**
 * The main activity of the app.
 *
 * This activity displays and contains all data regarding
 * the main activity. From this screen, the user may search
 * for books, see the most popular books, or view images of
 * books. They may also use the navbar to go to other pages
 *
 * @author Matthew Walowski
 * @version 1.0
 * @since 1.0
 */

//TODO clean up loaders
//TODO if internet changes app crashes

//Created 9/15/2017
public class MainActivity extends NavDrawerActivity implements LoaderManager.LoaderCallbacks {

    private JSONObject jsonResponse;

    private ListView mainContent;
    private ViewGroup mainContentHeader;
    private SearchView searchBar;

    public static SubjectAdapter subjectAdapter;

    public static Typeface handWriting;

    private final int ELEVATION_SUBJECT = 6;
    private final int ELEVATION_BOOK = 8;

    private RecyclerView recyclerView;

    //Books
    private final String KEY_AVERAGE_RATING = "AverageRating";
    private final String KEY_GID = "GID";
    private final String KEY_JSON = "JSON";
    private final String KEY_NUM_SUBJECTS = "numSubjects";
    private final String KEY_SMALL_THUMBNAIL = "SmallThumbnail";
    private final String KEY_SUBJECT = "subject";
    private final String KEY_SUCCESS = "Success";
    private final String KEY_TITLE = "Title";

    //Slides
    private final String KEY_DESCRIPTION = "Description";
    private final String KEY_SLIDESHOW_IMAGE = "Image";
    private final String KEY_LINKED_ITEM = "LinkedItem";
    private final String KEY_SHORT_DESCRIPTION = "ShortDescription";

    private final String BASE_URL = "walowtech.com";

    private final String PARAM_ACTION = "ACTION";
    private final String PARAM_NUM_RESULTS = "NUMOFRESULTS";
    private final String PARAM_UID = "UID";
    private final String PARAM_SEARCH_QUERY = "SEARCHSTRING";
    private final String PARAM_SEARCH_ITEM = "SEARCHITEM";

    private final String PATH0 = "apis";
    private final String PATH1 = "FBLALibrary";
    private final String PATH2 = "api.php";
    private final String SCHEME = "https";

    private final String VALUE_ACTION_BOOKS = "ACTION_RETRIEVE_TOP_BOOKS_BY_CATEGORY";
    private final String VALUE_ACTION_MESSAGE = "ACTION_RETRIEVE_DAILY_MESSAGE";
    private final String VALUE_ACTION_SEARCH = "ACTION_RETRIEVE_BOOK_DATA";
    private final String VALUE_NUM_RESULTS = "15";
    private int VALUE_UID;
    private String VALUE_SEARCH_QUERY;
    private String VALUE_SEARCH_ITEM = "TITLE";

    private URL requestURL;

    private ViewPager viewPager;
    public SlideshowAdapter slideshowAdapter;
    public static ArrayList<ViewPagerItem> slideshows = new ArrayList<>();
    public static ArrayList<Book> searchResults = new ArrayList<>();

    private final int DOWNLOAD_BOOK_JSON_LOADER = 0;
    private final int DOWNLOAD_SLIDE_JSON_LOADER = 1;
    private final int DOWNLOAD_BOOK_IMAGE_LOADER = 2;
    private final int DOWNLOAD_SLIDE_IMAGE_LOADER = 3;
    private final int DOWNLOAD_SEARCH_JSON_LOADER = 4;
    public static int downloadType;

    public static int subjectsLastVis;

    public static  ArrayList<Category> categories;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        configActionBar();
        super.onCreateDrawer();

        //Initialize main content
        mainContent = (ListView) findViewById(R.id.m_lv_main_content);
        LayoutInflater inflater = getLayoutInflater();
        mainContentHeader = (ViewGroup)inflater.inflate(R.layout.main_content_header, mainContent, false);
        mainContent.addHeaderView(mainContentHeader, null, false);

        //Create empty ArrayList to hold subjects and books
        categories = new ArrayList<>();

        //Populates the main screen with empty books
        for(int i = 0; i < 7; i++){
            ArrayList<Book> tempBooks = new ArrayList<>();
            for (int j = 0; j < Integer.valueOf(VALUE_NUM_RESULTS); j++) {
                tempBooks.add(new Book(null, null, null, null, 0f));
            }
            categories.add(new Category(tempBooks, "Loading"));
        }
        subjectAdapter = new SubjectAdapter(this, categories);

        //Set adapter
        mainContent.setAdapter(subjectAdapter);

        handWriting = Typeface.createFromAsset(getAssets(), "fonts/hand_writing.ttf");

        //Initialize ViewPager
        viewPager = (ViewPager)mainContentHeader.findViewById(R.id.m_view_pager);
        ArrayList<ViewPagerItem> initSlide = new ArrayList<>();
        initSlide.add(new ViewPagerItem("Loading ...", null, null, null));
        viewPager.setAdapter(new SlideshowAdapter(this, initSlide));

        //Check for internet connection
        if(NetworkJSONUtils.checkInternetConnection(this)) {
            //Retrieve JSON for ViewPager
            fetchSlideData();
            //Retrieve JSON for book data
            fetchBookData();
        }else{
            ErrorUtils.errorDialog(this, "Network Error", "It seems you don't have any network connection. Reset your connection and try again.");
        }
        //TODO show image for no image

        setNames();
    }

    /**
     * Configures the actionbar
     *
     * The layout of the actionbar is inflated and listeners
     * are set on the toggle button and SearchView
     */
    private void configActionBar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.m_toolbar);
         setSupportActionBar(toolbar);

        //Set NavDrawer Toggle Listener
        ImageView toggleIcon = (ImageView) toolbar.findViewById(R.id.toggle_icon);
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
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                updateSearchResults(newText);
                return false;
            }
        });
    }

    /**
     * TODO doc
     * @param searchQuery
     */
    public void updateSearchResults(String searchQuery){
        Log.i("LoginActivity", searchQuery);
        //Check for internet connection
        if(!NetworkJSONUtils.checkInternetConnection(this)) {
            ErrorUtils.errorDialog(this, "Network Error", "It seems you don't have any network connection. Reset your connection and try again.");
            return;
        }

        //Get UID
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        VALUE_UID = sharedPref.getInt("UID", -1);
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
            return;
        }

        //Restart loader is used instead of init loader so that if screen is rotated, loader is guarenteed to restart
        getLoaderManager().restartLoader(DOWNLOAD_SEARCH_JSON_LOADER, null, this);
    }

    @Override
    public Loader<JSONObject> onCreateLoader(int id, Bundle args) {
        //Set previous response to null to ensure new data
        jsonResponse = null;
        subjectsLastVis = mainContent.getLastVisiblePosition();

        //Return appropriate loader for different ID types
        if(id == DOWNLOAD_BOOK_JSON_LOADER || id == DOWNLOAD_SLIDE_JSON_LOADER || id == DOWNLOAD_SEARCH_JSON_LOADER) {
            return new DownloadJSONLoader(this, requestURL);
        }else if(id == DOWNLOAD_BOOK_IMAGE_LOADER || id == DOWNLOAD_SLIDE_IMAGE_LOADER) {
            if (downloadType == 0)
                return new DownloadImageLoader(this, this, categories, null, this);
            else if(downloadType == 1)
                return new DownloadImageLoader(this, this, null, slideshows, this);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        //Preform proper actions based on loader that finished
        if(loader.getId() == DOWNLOAD_BOOK_JSON_LOADER || loader.getId() == DOWNLOAD_SLIDE_JSON_LOADER || loader.getId() == DOWNLOAD_SEARCH_JSON_LOADER) {
            if (data != null && data != " ") {
                try {
                    jsonResponse = new JSONObject(data.toString());
                    parseBookJSON(jsonResponse);
                } catch (JSONException JSONE) {
                    ErrorUtils.errorDialog(this, "Data Error", "There was an error with the data format. Please try again later.");
                }
            } else {
                ErrorUtils.errorDialog(this, "Could not connect to server", "No information was retrieved from the server. Please try again later.");
            }

            //Loader must be destroied for different URLs to be downloaded from
            getLoaderManager().destroyLoader(loader.getId());
        }else if(loader.getId() == DOWNLOAD_BOOK_IMAGE_LOADER || loader.getId() == DOWNLOAD_SLIDE_IMAGE_LOADER) {

            //Loader must be destroied for future calls of different download types to work
            getLoaderManager().destroyLoader(loader.getId());
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    /**
     * Sets name and email in the DrawerLayout header
     *
     * The name and email are retrieved from SharedPreferences
     * then are set to the TextViews. The typeface is also
     * set to the strings that are modified.
     */
    public void setNames(){
        //Get SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String nameString = sharedPreferences.getString("NAME", "Firstname Lastname");
        String emailString = sharedPreferences.getString("EMAIL", "Email@gmail.com");

        //Retrieve Views
        TextView name = (TextView) header.findViewById(R.id.nav_tv_name);
        TextView email = (TextView) header.findViewById(R.id.nav_tv_email);

        //Set View text
        name.setText(nameString);
        email.setText(emailString);

        //Set View typeface
        name.setTypeface(handWriting);
        email.setTypeface(handWriting);
    }

    /**
     * Updates book image.
     *
     * Method is invoked to update the book image from the UI thread.
     * The rows that are visible when this method is invoked must be updated
     * by notifying the subject adapter of the data set changing. All other rows are updated
     * by notifying the book adapter of a small range changed.
     * Changing the image from any non-UI thread would crash the application.
     */
    public static void updateUIImage(int i, int j, Bitmap bitmap){

//TODO fix
        if(i <= subjectsLastVis){
            subjectAdapter.notifyDataSetChanged();
        }else {
            try {
                //TODO fix
                categories.get(i).bookAdapter.notifyItemRangeChanged(j-1, j+1);
            } catch (NullPointerException e) {
                //Log.i("LoginActivity", "EXCEPTION THROWN in " + i + ", " + j);
                //TODO catch
            }
        }
    }

//TODO doc

    /**
     * Updates the images in the ViewPager.
     *
     * The adapter is initialized and the adapter is set.
     */
    public void updateViewPagerImage(){
        slideshowAdapter = new SlideshowAdapter(this, slideshows);
        viewPager.setAdapter(slideshowAdapter);
    }

    /**
     * Initiates variables for the fetching of the top book data.
     *
     * First connection is checked. A URL is created that fetches information about
     * the top books. The URL is passed to DownloadImageLoader
     * upon the loader being initialized
     */
    public void fetchBookData(){
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
                .appendQueryParameter(PARAM_ACTION, VALUE_ACTION_BOOKS)
                .appendQueryParameter(PARAM_NUM_RESULTS, VALUE_NUM_RESULTS)
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
        getLoaderManager().restartLoader(DOWNLOAD_BOOK_JSON_LOADER, null, this);
    }

    /**
     * Initalizes variables for fetching slideshow data
     *
     * First connection is checked. A URL is created that is passed to DownloadImageLoader
     * after the loader is initialized. This URL is the location
     * of the JSON regarding the slides.
     */
    public void fetchSlideData(){
        //Check for internet connection
        if(!NetworkJSONUtils.checkInternetConnection(this)) {
            ErrorUtils.errorDialog(this, "Network Error", "It seems you don't have any network connection. Reset your connection and try again.");
            return;
        }

        //Build Uri
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(SCHEME)
                .authority(BASE_URL)
                .appendPath(PATH0)
                .appendPath(PATH1)
                .appendPath(PATH2)
                .appendQueryParameter(PARAM_ACTION, VALUE_ACTION_MESSAGE)
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
        getLoaderManager().restartLoader(DOWNLOAD_SLIDE_JSON_LOADER, null, this);
    }

    /**
     * Parses the JSON response from the server
     *
     * First it is checked if the server had success, then
     * the actual response is retrieved. Then each subject in a for loop is put
     * into a JSONArray with another nested for loop to parse the information
     * about each individual book in an individual subject. The results are
     * then created into objects that are passed to the adapters.
     *
     * @param json The JSON to be parsed.
     */
    private void parseBookJSON(JSONObject json){
        //Try to parse JSON
        try {
            int success = json.getInt(KEY_SUCCESS);
            //If success = 1 It is a response related to books
            if(success == 1) {
                JSONObject jsonResponse = json.getJSONObject(KEY_JSON);
                int numSubjects = jsonResponse.getInt(KEY_NUM_SUBJECTS);

               categories.clear();

                //iterate through subjects
                for(int i = 0; i < numSubjects + 1; i++){
                    String curSubjKey = jsonResponse.getString(KEY_SUBJECT + i);
                    JSONArray curSubjBooks = jsonResponse.getJSONArray(curSubjKey);

                    ArrayList<Book> tempBooks = new ArrayList<>();

                    //iterate through each book in each subject
                    for(int j = 0; j < curSubjBooks.length(); j++){
                        JSONObject curBook = (JSONObject) curSubjBooks.get(j);

                        String title = curBook.getString(KEY_TITLE);
                        String GID = curBook.getString(KEY_GID);
                        String thumbnail = curBook.getString(KEY_SMALL_THUMBNAIL);
                        Float averageRating = Float.valueOf(curBook.getString(KEY_AVERAGE_RATING));
                        String subject = curSubjKey;

                        tempBooks.add(new Book(subject, title, GID, thumbnail, averageRating));
                    }

                    categories.add(new Category(tempBooks, curSubjKey));
                }


                //DownloadType 0 means books being downloaded
                downloadType = 0;
                //Restart loader is used instead of init loader so that if screen is rotated, loader is guarenteed to restart
                getLoaderManager().restartLoader(DOWNLOAD_BOOK_IMAGE_LOADER, null, this);

                //If success = 3 it is a response related to slides
            }else if(success == 3){
                JSONArray jsonResponse = json.getJSONArray(KEY_JSON);
                slideshows.clear();

                //Iterate through all JSONObjects in JSONArray
                for(int i=0; i < jsonResponse.length(); i++){
                    JSONObject curSlide = (JSONObject)jsonResponse.get(i);
                    String shortDescription = curSlide.getString(KEY_SHORT_DESCRIPTION);
                    String description = curSlide.getString(KEY_DESCRIPTION);
                    String linkedItem = curSlide.getString(KEY_LINKED_ITEM);
                    String image = curSlide.getString(KEY_SLIDESHOW_IMAGE);

                    slideshows.add(new ViewPagerItem(shortDescription, description, linkedItem, image));
                }

                //DownloadType 1 means slide being downloaded
                downloadType = 1;
                //Restart loader is used instead of init loader so that if screen is rotated, loader is guarenteed to restart
                getLoaderManager().restartLoader(DOWNLOAD_SLIDE_IMAGE_LOADER, null, this);
            }else if(success == 4){
                //Clear previous search results
                searchResults.clear();
                JSONArray jsonResponse = json.getJSONArray(KEY_JSON);

                Log.i("LoginActivity", "SEARCH RESULTS");
                    //Iterate through all books that were found
                    for(int i = 0; i < jsonResponse.length(); i++){
                        JSONObject curBook = (JSONObject) jsonResponse.get(i);

                        String GID = curBook.getString(KEY_GID);
                        String title = curBook.getString(KEY_TITLE);
                        String thumbnail = curBook.getString(KEY_SMALL_THUMBNAIL);

                        searchResults.add(new Book(null, title, GID, thumbnail, 0f));
                        Log.i("LoginActivity", "\n" + searchResults.get(i).title);
                    }
                    showSearchSuggestions();
            }
            //TODO case for no success
            //Exception thrown when bad JSON is recieved
        }catch(JSONException JSONE){
            ErrorUtils.errorDialog(this, "Server Error", "The data from the server could not be read correctly. Please try again later.");
            Log.i("LoginActivity", "JSONException");
            return;
        }

    }

    public void showSearchSuggestions(){

        //searchBar.setSuggestionsAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1));
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



    //Adapters

    /**
     * The adapter between the subjects and their rows on the screen.
     *
     * This adapter inflates a row for each subject, and then adds BookAdapter
     * to each subject to inflate each individual book.
     *
     * @author Matthew Walowski
     * @version 1.0
     * @since 1.0
     */

    //Created 9/18/17
    private class SubjectAdapter extends ArrayAdapter<Category>{

        public BookAdapter bookAdapter = null;

        public SubjectAdapter(Context context, ArrayList<Category> subjects){
            super(context, 0, subjects);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final Category subject = getItem(position);
            if(convertView == null)
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.book_category, parent, false);

            //Get Views
            TextView tvSubjectTitle = (TextView) convertView.findViewById(R.id.m_tv_title);
            TextView tvMore = (TextView) convertView.findViewById(R.id.m_tv_more);
            LinearLayout subjectRow = (LinearLayout) convertView.findViewById(R.id.m_ll_subject_row);

            setElevation(subjectRow, ELEVATION_SUBJECT);

            tvMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("LoginActivity", "MORE ON SUBJ " + position + " (" + subject.categoryName + ")");
                }
            });

            //Set text and typeface
            tvSubjectTitle.setText(subject.categoryName);
            tvSubjectTitle.setTypeface(handWriting);
            tvMore.setTypeface(handWriting);

            //Get RecyclerView and create adapter with horizontal layout
            recyclerView = (RecyclerView) convertView.findViewById(R.id.m_rv_books);
            bookAdapter = new BookAdapter(getContext(), subject.books);
            categories.get(position).bookAdapter = bookAdapter;
            recyclerView.setAdapter(bookAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

            //Return the created view
            return convertView;
        }
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

        public BookAdapter(Context context, ArrayList<Book> books){
            inflater = LayoutInflater.from(context);
            this.books = books;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //Inflate and return viewholder
            View view = inflater.inflate(R.layout.book_category_item, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(BookAdapter.MyViewHolder holder, final int position) {
            //Get current item and set text, typeface, and image
            final Book currentBook = books.get(position);
            holder.rating.setText(Float.toString(currentBook.averageRating));
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
                        Log.i("LoginActivity", "BOOK PRESSED AT " + currentBook.subject + ", " + position + ". GID: " + currentBook.GID);
                    }
                });
            }

            //Set books elevation
            setElevation(holder.book, ELEVATION_BOOK);
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
