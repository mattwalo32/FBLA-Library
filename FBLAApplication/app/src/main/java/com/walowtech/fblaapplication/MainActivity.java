package com.walowtech.fblaapplication;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.walowtech.fblaapplication.Utils.DownloadJSONLoader;
import com.walowtech.fblaapplication.Utils.ErrorUtils;

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
public class MainActivity extends Activity implements LoaderManager.LoaderCallbacks {

    private JSONObject jsonResponse;

    private ListView mainContent;

    private SubjectAdapter subjectAdapter;
    private ViewGroup mainContentHeader;

    private Typeface handWriting;

    private final String KEY_TITLE = "Title";
    private final String GID = "GID";
    private final String THUMBNAIL = "Thumbnail";
    private final String SMALL_THUMBNAIL = "SmallThumbnail";

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

    private final int DOWNLOAD_JSON_LOADER = 0;

    private ArrayList<Category> subjects = new ArrayList<>();
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

        /*subjectAdapter = new SubjectAdapter(this, null);
        mainContent.setAdapter(subjectAdapter);*/
        fetchBookData();
        //TODO show if info not avalible
        //TODO inform no internet connection
    }

    @Override
    public Loader<JSONObject> onCreateLoader(int id, Bundle args) {
        jsonResponse = null;
        return new DownloadJSONLoader(this, requestURL);
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        if(data != null || data != "") {
            try {
                jsonResponse = new JSONObject(data.toString());
                parseBookJSON(jsonResponse);
            } catch (JSONException JSONE) {
                ErrorUtils.errorDialog(this, "Data Error", "There was an error with the data format. Please try again later.");
            }
        }else{
            ErrorUtils.errorDialog(this, "Could not connect to server", "No information was retrieved from the server. Please try again later.");
        }

        //TODO animate loading
        getLoaderManager().destroyLoader(loader.getId());
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    public void fetchBookData(){
        //TODO loading anim

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

    private void parseBookJSON(JSONObject json){

    }

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

            tvSubjectTitle.setText(subject.categoryName);
            tvSubjectTitle.setTypeface(handWriting);

            RecyclerView recyclerView = (RecyclerView) convertView.findViewById(R.id.m_rv_books);
            subject.books = books;
            //TODO get book info from JSON.
            BookAdapter bookAdapter = new BookAdapter(getContext(), subject.books);
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

            //TODO set image here
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

            public MyViewHolder(View itemView) {
                super(itemView);
                rating = (TextView) itemView.findViewById(R.id.m_tv_rating);
                image = (ImageView) itemView.findViewById(R.id.m_iv_book_cover);
            }
        }
    }
}
