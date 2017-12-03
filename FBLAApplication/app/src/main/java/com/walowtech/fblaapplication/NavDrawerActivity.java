package com.walowtech.fblaapplication;

import android.app.Activity;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.MatrixCursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.walowtech.fblaapplication.Utils.ErrorUtils;
import com.walowtech.fblaapplication.Utils.NavbarAdapter;
import com.walowtech.fblaapplication.Utils.NetworkJSONUtils;
import com.walowtech.fblaapplication.Utils.SlideshowAdapter;
import com.walowtech.fblaapplication.Utils.SuggestionCursorAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static com.walowtech.fblaapplication.MainActivity.handWriting;

/**
 * Base activity for activities that contain the NavDrawer.
 *
 * This is the base activity for NavDrawer activities. The NavDrawer is automatically
 * initialized, inflated, and has a listener set on it, so all activities that extend
 * this activity will contain the NavDrawer.
 */

//Created 9/29/2017
public class NavDrawerActivity extends BaseActivity {

    public DrawerLayout drawerLayout;
    public ListView drawerList;
    public NavbarAdapter navbarAdapter;
    public ArrayList<NavbarItem> navbarItems = new ArrayList<>();
    public ActionBarDrawerToggle toggle;
    public View header;

    public final String VALUE_ACTION_SEARCH = "ACTION_RETRIEVE_BOOK_DATA";
    public int VALUE_UID;
    public String VALUE_SEARCH_QUERY;
    public String VALUE_SEARCH_ITEM = "TITLE";

    public URL requestURL;

    protected void onCreateDrawer(){
        drawerLayout = (DrawerLayout) findViewById(R.id.m_nav_drawer);

        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.string_empty, R.string.string_empty){
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        toggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(toggle);

        navbarItems.add(new NavbarItem(0, R.drawable.ic_account_black, "Account"));
        navbarItems.add(new NavbarItem(1, R.drawable.ic_settings_black, "Settings"));
        navbarItems.add(new NavbarItem(2, R.drawable.ic_info_black, "Information"));
        //navbarItems.add(new NavbarItem(3, R.drawable.ic_fees_black, "Fees"));
        navbarItems.add(new NavbarItem(4, R.drawable.ic_logout_black, "Logout"));

        navbarAdapter = new NavbarAdapter(this, navbarItems);

        drawerList = (ListView) findViewById(R.id.nav_layout);
        header = getLayoutInflater().inflate(R.layout.navbar_header, null);
        drawerList.addHeaderView(header, null, false);

        drawerList.setAdapter(navbarAdapter);

        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return toggle != null && toggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if(toggle != null)
         toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(toggle != null)
            toggle.onConfigurationChanged(newConfig);
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

    //TODO doc
    public MatrixCursor updateSearchResults(JSONObject json, ArrayList<Book> searchResults) throws JSONException {
        //Clear previous search results
        searchResults.clear();
        JSONArray jsonResponse = json.getJSONArray(KEY_JSON);

        Log.i("LoginActivity", "SEARCH RESULTS");
        final String[] sAutocompleteColNames = new String[]{
                BaseColumns._ID,
                SearchManager.SUGGEST_COLUMN_TEXT_1
        };

        //Matrix that holds all results
        MatrixCursor cursor = new MatrixCursor(sAutocompleteColNames);

        //Iterate through all books that were found
        for (int i = 0; i < jsonResponse.length(); i++) {
            JSONObject curBook = (JSONObject) jsonResponse.get(i);

            String GID = curBook.getString(KEY_GID);
            String title = curBook.getString(KEY_TITLE);
            String thumbnail = curBook.getString(KEY_SMALL_THUMBNAIL);

            searchResults.add(new Book(null, title, GID, thumbnail, 0f));
            Log.i("LoginActivity", "\n" + searchResults.get(i).title);

            //Add a row to the cursor
            Object[] row = new Object[]{i, title};
            cursor.addRow(row);
        }
        return cursor;
    }

    //TODO doc
    public void searchURL(Context context, String searchString){
        VALUE_SEARCH_ITEM = "ALL";
        VALUE_SEARCH_QUERY = searchString;
        String maxResults = "1000";

        Uri.Builder builder = new Uri.Builder();

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
        Intent gridActivity = new Intent(context, GridViewActivity.class);

        gridActivity.putExtra("URL", builder.toString());
        gridActivity.putExtra("QUERY", searchString);
        Log.i("LoginActivity", builder.toString());

        context.startActivity(gridActivity);
    }

    //TODO doc
    public void onSuggestionSelected(Context context, String GID){
        Intent i = new Intent(this, BookDetailsActivity.class);
        i.putExtra("GID", GID);
        //i.putExtra("BOOK_IMAGE", bitmap);
        startActivity(i);
    }

}
