package com.walowtech.fblaapplication;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
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
import android.widget.TextView;

import com.walowtech.fblaapplication.Utils.DownloadImageLoader;
import com.walowtech.fblaapplication.Utils.DownloadJSONLoader;
import com.walowtech.fblaapplication.Utils.ErrorUtils;
import com.walowtech.fblaapplication.Utils.NetworkJSONUtils;
import com.walowtech.fblaapplication.Utils.SlideshowAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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

//Created 9/15/2017
public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks {

    private JSONObject jsonResponse;

    public static ListView mainContent;

    public static SubjectAdapter subjectAdapter;
    private ViewGroup mainContentHeader;

    public static Typeface handWriting;

    private final int ELEVATION_SUBJECT = 6;
    private final int ELEVATION_BOOK = 8;

    public static RecyclerView recyclerView;

    private final String KEY_AVERAGE_RATING = "AverageRating";
    private final String KEY_GID = "GID";
    private final String KEY_JSON = "JSON";
    private final String KEY_NUM_SUBJECTS = "numSubjects";
    private final String KEY_SMALL_THUMBNAIL = "SmallThumbnail";
    private final String KEY_SUBJECT = "subject";
    private final String KEY_SUCCESS = "Success";
    private final String KEY_TITLE = "Title";

    private final String BASE_URL = "walowtech.com";
    private final String PARAM_ACTION = "ACTION";
    private final String PARAM_NUM_RESULTS = "NUMOFRESULTS";
    private final String PATH0 = "apis";
    private final String PATH1 = "FBLALibrary";
    private final String PATH2 = "api.php";
    private final String SCHEME = "https";
    private final String VALUE_ACTION = "ACTION_RETRIEVE_TOP_BOOKS_BY_CATEGORY";
    private final String VALUE_NUM_RESULTS = "15";

    private URL requestURL;

    private ViewPager viewPager;
    private SlideshowAdapter slideshowAdapter;

    private final int DOWNLOAD_JSON_LOADER = 0;
    private final int DOWNLOAD_IMAGE_LOADER = 1;

    public static  ArrayList<Category> categories;
    private ArrayList<Book> books = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainContent = (ListView) findViewById(R.id.m_lv_main_content);
        LayoutInflater inflater = getLayoutInflater();
        mainContentHeader = (ViewGroup)inflater.inflate(R.layout.main_content_header, mainContent, false);
        mainContent.addHeaderView(mainContentHeader, null, false);

        handWriting = Typeface.createFromAsset(getAssets(), "fonts/hand_writing.ttf");

        viewPager = (ViewPager)mainContentHeader.findViewById(R.id.m_view_pager);
        slideshowAdapter = new SlideshowAdapter(this, new int[]{R.drawable.testimage, R.drawable.testimage, R.drawable.testimage});
        viewPager.setAdapter(slideshowAdapter);

        if(NetworkJSONUtils.checkInternetConnection(this)) {
            fetchBookData();
        }else{
            ErrorUtils.errorDialog(this, "Network Error", "It seems you don't have any network connection. Reset your connection and try again.");
        }
        //TODO show image for no image
    }

    @Override
    public Loader<JSONObject> onCreateLoader(int id, Bundle args) {
        jsonResponse = null;
        if(id == DOWNLOAD_JSON_LOADER)
            return new DownloadJSONLoader(this, requestURL);
        else if(id == DOWNLOAD_IMAGE_LOADER)
            return new DownloadImageLoader(this, categories, this);
        return null;

    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        if(loader.getId() == DOWNLOAD_JSON_LOADER) {
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

            getLoaderManager().destroyLoader(loader.getId());
        }else if(loader.getId() == DOWNLOAD_IMAGE_LOADER) {
            //recyclerView.getAdapter().notifyAll();
            subjectAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    /**
     * Updates book image.
     *
     * Method is invoked to update the book image from the UI thread.
     * Changing the image from any non-UI thread would crash the application.
     */
    public static void updateUIImage(){
        subjectAdapter.notifyDataSetChanged();
    }

    /**
     * Initiates the fetching of the top book data.
     *
     * A URL is created that fetches information about
     * the top books. The URL is passed to DownloadImageLoader
     * upon the loader being initialized
     */
    public void fetchBookData(){
        if(!NetworkJSONUtils.checkInternetConnection(this)) {
            ErrorUtils.errorDialog(this, "Network Error", "It seems you don't have any network connection. Reset your connection and try again.");
            return;
        }

        Uri.Builder builder = new Uri.Builder();
        builder.scheme(SCHEME)
                .authority(BASE_URL)
                .appendPath(PATH0)
                .appendPath(PATH1)
                .appendPath(PATH2)
                .appendQueryParameter(PARAM_ACTION, VALUE_ACTION)
                .appendQueryParameter(PARAM_NUM_RESULTS, VALUE_NUM_RESULTS)
                .build();
        String urlString = builder.toString();

        try{
            requestURL = new URL(urlString);
        }catch(MalformedURLException MURLE){
            ErrorUtils.errorDialog(this, "There was an error with the url", "Currently the server can not be reached. Make sure your username and password are entered correctly");
            return;
        }

        getLoaderManager().initLoader(DOWNLOAD_JSON_LOADER, null, this);
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
        try {
            int success = json.getInt(KEY_SUCCESS);
            if(success == 1) {
                JSONObject jsonResponse = json.getJSONObject(KEY_JSON);
                int numSubjects = jsonResponse.getInt(KEY_NUM_SUBJECTS);

               categories = new ArrayList<>();

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

                getLoaderManager().initLoader(DOWNLOAD_IMAGE_LOADER, null, this);
            }
        }catch(JSONException JSONE){
            ErrorUtils.errorDialog(this, "Server Error", "The data from the server could not be read correctly. Please try again later.");
            Log.i("LoginActivity", "JSONException");
            return;
        }

        subjectAdapter = new SubjectAdapter(this, categories);
        mainContent.setAdapter(subjectAdapter);

    }

    /**
     * Sets elevation of a view.
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
        public View getView(int position, View convertView, ViewGroup parent) {
            Category subject = getItem(position);
            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.book_category, parent, false);
            }

            TextView tvSubjectTitle = (TextView) convertView.findViewById(R.id.m_tv_title);
            LinearLayout subjectRow = (LinearLayout) convertView.findViewById(R.id.m_ll_subject_row);
            setElevation(subjectRow, ELEVATION_SUBJECT);

            tvSubjectTitle.setText(subject.categoryName);
            tvSubjectTitle.setTypeface(handWriting);

            recyclerView = (RecyclerView) convertView.findViewById(R.id.m_rv_books);
            bookAdapter = new BookAdapter(getContext(), subject.books);
            recyclerView.setAdapter(bookAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

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
    private class BookAdapter extends RecyclerView.Adapter<BookAdapter.MyViewHolder>{

        private LayoutInflater inflater;
        ArrayList<Book> books = new ArrayList<>();

        public BookAdapter(Context context, ArrayList<Book> books){
            inflater = LayoutInflater.from(context);
            this.books = books;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.book_category_item, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(BookAdapter.MyViewHolder holder, int position) {
            Book currentBook = books.get(position);
            holder.rating.setText(Float.toString(currentBook.averageRating));
            holder.rating.setTypeface(handWriting);

            if(currentBook.coverSmall != null) {
                holder.image.setImageBitmap(currentBook.coverSmall);
                holder.progressBar.setVisibility(View.GONE);
            }

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
