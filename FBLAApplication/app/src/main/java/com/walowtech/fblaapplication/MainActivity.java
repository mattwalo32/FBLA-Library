package com.walowtech.fblaapplication;

import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.MatrixCursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.walowtech.fblaapplication.Utils.DownloadImageLoader;
import com.walowtech.fblaapplication.Utils.DownloadJSONLoader;
import com.walowtech.fblaapplication.Utils.ErrorUtils;
import com.walowtech.fblaapplication.Utils.NetworkJSONUtils;
import com.walowtech.fblaapplication.Utils.SlideshowAdapter;
import com.walowtech.fblaapplication.Utils.SuggestionCursorAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

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

//TODO if internet changes app crashes

//Created 9/15/2017
public class MainActivity extends NavDrawerActivity{

    private JSONObject jsonResponse;

    private ListView mainContent;
    private ViewGroup mainContentHeader;
    private SearchView searchBar;

    public static SubjectAdapter subjectAdapter;



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
    public static ArrayList<ViewPagerItem> slideshows;
    public static ArrayList<Book> searchResults = new ArrayList<>();

    public static int downloadType;

    public static int subjectsLastVis;

    public static  ArrayList<Category> categories;

    RequestQueue requestQueue;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i("LoginActivity", "CREATED");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        super.onCreateDrawer();



        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        //Get all pref
        Map<String, ?> keys = sharedPreferences.getAll();
        for(Map.Entry<String, ?>entry : keys.entrySet()) { //Loop through all pref
            if (entry.getKey().contains("ALARM-BID")) { //If this is an alarm pref
                //Get all items under alarm
                int BID = Integer.parseInt(entry.getValue().toString());
                Log.i("LoginActivity", "BID" + BID);
            }
        }


        //Initialize main content
        mainContent = (ListView) findViewById(R.id.m_lv_main_content);
        LayoutInflater inflater = getLayoutInflater();
        mainContentHeader = (ViewGroup)inflater.inflate(R.layout.main_content_header, mainContent, false);
        mainContent.addHeaderView(mainContentHeader, null, false);
        slideshows = new ArrayList<>();

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

        //Initialize ViewPager
        viewPager = (ViewPager)mainContentHeader.findViewById(R.id.m_view_pager);
        ArrayList<ViewPagerItem> initSlide = new ArrayList<>();
        initSlide.add(new ViewPagerItem("Loading ...", null, null, null));
        viewPager.setAdapter(new SlideshowAdapter(this, initSlide));

        requestQueue = Volley.newRequestQueue(this);

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

        super.setNames();
        configActionBar();
    }

    /**
     * Configures the actionbar
     *
     * The layout of the actionbar is inflated and listeners
     * are set on the toggle button and SearchView
     */
    private void configActionBar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.m_toolbar);//Find toolbar in layout
        setSupportActionBar(toolbar);//Set the toolbar as the actionbar

        //Set background to a drawable blue background
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_drawable));
        actionBar.setDisplayHomeAsUpEnabled(false);

        //Set on click listener for the toggle nav button
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
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchURL(MainActivity.this, query); //Gets searched items
                searchBar.setQuery(query, false);
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
                onSuggestionSelected(MainActivity.this, GID);
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
     * with their UID and search query, and an AsyncLoader is launched to fetch
     * information from the URI.
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

        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlString,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            parseBookJSON(jsonResponse);
                        }catch(JSONException JSONE){
                            ErrorUtils.errorDialog(MainActivity.this, "Data Error", "There was an error with the data format. Please try again later.");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ErrorUtils.errorDialog(MainActivity.this, "Could not connect to server", "No information was retrieved from the server. Please try again later.");
            }
        });

        requestQueue.add(stringRequest);
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
    public void updateUIImage(int i, int j, Bitmap bitmap){

        categories.get(i).books.get(j).coverSmall = bitmap;

        if(i <= subjectsLastVis){
            subjectAdapter.notifyDataSetChanged();
        }else {
            try {
                categories.get(i).bookAdapter.notifyItemRangeChanged(j-1, j+1);
            } catch (NullPointerException NPE) {
                NPE.printStackTrace(); //This exception is expected to be thrown as off screen images are attemtped to be displayed
            } catch (Exception e){
                Toast.makeText(this, "An unexpected error occurred while. Some books may display wrong.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    /**
     * Updates the images in the ViewPager.
     *
     * The adapter is initialized and the adapter is set. The
     * old adapter is replaced with the current one.
     */
    public void updateViewPagerImage(Bitmap bitmap, int i){
        slideshows.get(i).image = bitmap;
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

        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlString,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            parseBookJSON(jsonResponse);
                        }catch(JSONException JSONE){
                            ErrorUtils.errorDialog(MainActivity.this, "Data Error", "There was an error with the data format. Please try again later.");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ErrorUtils.errorDialog(MainActivity.this, "Could not connect to server", "No information was retrieved from the server. Please try again later.");
            }
        });

        requestQueue.add(stringRequest);
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

        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlString,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            parseBookJSON(jsonResponse);
                        }catch(JSONException JSONE){
                            ErrorUtils.errorDialog(MainActivity.this, "Data Error", "There was an error with the data format. Please try again later.");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ErrorUtils.errorDialog(MainActivity.this, "Could not connect to server", "No information was retrieved from the server. Please try again later.");
            }
        });

        requestQueue.add(stringRequest);
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
                downloadBookCovers();
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

                downloadSlideImages();
            }else if(success == 4){
                searchBar.getSuggestionsAdapter().changeCursor(super.updateSearchResults(json, searchResults));
            }else {
                ErrorUtils.errorDialog(this, "Response Error", "An unexpected response was recieved from the server. Please try again later.");
            }
            //Exception thrown when bad JSON is recieved
        }catch(JSONException JSONE){
            ErrorUtils.errorDialog(this, "Server Error", "The data from the server could not be read correctly. Please try again later.");
            Log.i("LoginActivity", "JSONException");
            JSONE.printStackTrace();
            return;
        }

    }

    private void downloadSlideImages(){
        for(int i = 0; i < slideshows.size(); i++) {
            final int i0 = i;
            String url = slideshows.get(i).imageURL;

            ImageRequest imageRequest = new ImageRequest(url,
                    new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap bitmap) {
                            updateViewPagerImage(bitmap, i0);
                        }
                    }, 0, 0, null,
                    new Response.ErrorListener() {
                        public void onErrorResponse(VolleyError error) {
                            ErrorUtils.errorDialog(MainActivity.this, "Server Error", "The data from the server could not be read correctly. Please try again later.");
                        }
                    });

            requestQueue.add(imageRequest);

        }
    }

    private void downloadBookCovers(){
        //Loops through each category
        for (int i = 0; i < categories.size(); i++) {

            //Loops through each subject
            for (int j = 0; j < categories.get(i).books.size(); j++) {
                String url = categories.get(i).books.get(j).smallThumbnail;
                if (url != null && !url.equals("")) {
                    final int i0 = i;
                    final int j0 = j;


                    ImageRequest imageRequest = new ImageRequest(url,
                            new Response.Listener<Bitmap>() {
                                @Override
                                public void onResponse(Bitmap bitmap) {
                                    updateUIImage(i0, j0, bitmap);
                                }
                            }, 0, 0, null,
                            new Response.ErrorListener() {
                                public void onErrorResponse(VolleyError error) {
                                    ErrorUtils.errorDialog(MainActivity.this, "Server Error", "The data from the server could not be read correctly. Please try again later.");
                                }
                            });
                    requestQueue.add(imageRequest);

                } else {
                    Toast.makeText(this, "One or more books is missing a cover image.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * Launches the Detailed Book Info Activity and
     * passes in its GID and bitmap as an extra.
     *
     * This Method also creates an animation if needed.
     *
     * @param image the ImageView to be animated and passed as extra
     * @param GID the GID of the book selected and to be passed as extra
     */
    private void launchActivityDetailedBook(ImageView image, String GID){
        android.support.v4.util.Pair<View, String> p1 = android.support.v4.util.Pair.create((View)image, getString(R.string.trans_iv_book_cover));
        //Pair<View, String> p2 = Pair.create((View)text, getString(R.string.trans_tv_avg_rating));

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, p1);
        BitmapDrawable drawable = (BitmapDrawable) image.getDrawable();
        Bitmap bitmap;
        try {
            bitmap = drawable.getBitmap();
        }catch(NullPointerException NPE){
            bitmap = null;
            NPE.printStackTrace();
        }

        Intent i = new Intent(this, BookDetailsActivity.class);
        i.putExtra("GID", GID);
        i.putExtra("BOOK_IMAGE", bitmap);
        startActivity(i , options.toBundle());
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

            if(subject.categoryName.equals("Popular")){ //If category is popular books
                tvMore.setVisibility(GONE); //Get rid of more button
            }else {
                tvMore.setVisibility(View.VISIBLE);
                tvMore.setOnClickListener(new View.OnClickListener() { //Else set an on click listener
                    @Override
                    public void onClick(View v) {
                        Uri.Builder builder = new Uri.Builder();

                        VALUE_SEARCH_ITEM = "SUBJECT";
                        VALUE_SEARCH_QUERY = subject.categoryName;
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
                        Intent gridActivity = new Intent(getContext(), GridViewActivity.class);

                        gridActivity.putExtra("URL", builder.toString());

                        getContext().startActivity(gridActivity);
                    }
                });
            }

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
        Context context;

        public BookAdapter(Context context, ArrayList<Book> books){
            inflater = LayoutInflater.from(context);
            this.books = books;
            this.context = context;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //Inflate and return viewholder
            View view = inflater.inflate(R.layout.book_category_item, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final BookAdapter.MyViewHolder holder, final int position) {
            //Get current item and set text, typeface, and image
            final Book currentBook = books.get(holder.getAdapterPosition());
            holder.rating.setText(String.format("%.01f", currentBook.averageRating));
            holder.rating.setTypeface(handWriting);

            books.get(holder.getAdapterPosition()).imageView = holder.image;

            //If a cover has been downloaded, remove loading indicator
            if(currentBook.coverSmall != null) {
                holder.image.setImageBitmap(currentBook.coverSmall);
                holder.progressBar.setVisibility(GONE);
            }

            if(currentBook.GID != null){
                currentBook.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i("LoginActivity", "BOOK PRESSED AT " + currentBook.subject + ", " + holder.getAdapterPosition() + ". GID: " + currentBook.GID);
                        launchActivityDetailedBook(currentBook.imageView, currentBook.GID);
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