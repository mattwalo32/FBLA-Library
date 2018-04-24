package com.walowtech.fblaapplication;

import android.app.SearchManager;
import android.content.Intent;
import android.drm.DrmStore;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.walowtech.fblaapplication.Utils.SlideshowAdapter;
import com.walowtech.fblaapplication.Utils.SuggestionCursorAdapter;

import java.util.ArrayList;

/**
 * Activity contains information about the school.
 *
 * A map of the school, school information, app information, and the app logo are
 * all displayed here.
 *
 * @author Matthew Walowski
 * @version 1.0
 * @since 10/7/2017
 */

public class AboutActivity extends BaseActivity {

    private static final String TWITTER_URL = "https://twitter.com/PrefaceApp/";
    private static final String FACEBOOK_URL = "https://www.facebook.com/matthew.walowski.7";

    private LinearLayout mMainLayout;
    private LinearLayout mSecondLayer;
    private TextView mSchoolName;
    private TextView mAboutSchool;
    private TextView mAboutApp;
    private ViewPager mPager;

    private SearchView searchBar;

    private SlideshowAdapter pagerAdapter;
    private ArrayList<ViewPagerItem> slides = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        mMainLayout = (LinearLayout) findViewById(R.id.aba_main_layout);
        mSecondLayer = (LinearLayout) findViewById(R.id.aba_second_layer);
        mSchoolName = (TextView) findViewById(R.id.aba_school_name);
        mAboutSchool = (TextView) findViewById(R.id.aba_about_school);
        mAboutApp = (TextView) findViewById(R.id.aba_about_app);
        mPager = (ViewPager) findViewById(R.id.aa_pager);

        mSchoolName.setTypeface(handWriting);
        mAboutSchool.setTypeface(handWriting);
        mAboutApp.setTypeface(handWriting);

        setElevation(mMainLayout, 6);
        setElevation(mSecondLayer, 12);

        ViewPagerItem libMap = new ViewPagerItem(null, null, null, null);
        libMap.image = BitmapFactory.decodeResource(getResources(), R.drawable.library_transparent);

        slides.add(libMap);

        pagerAdapter = new SlideshowAdapter(this, slides, false);
        mPager.setAdapter(pagerAdapter);

        configActionBar();
    }


        /**
         * Configures the actionbar
         *
         * The layout of the actionbar is inflated and listeners
         * are set on the toggle button and SearchView
         */
    private void configActionBar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.aba_toolbar);//Find toolbar in layout
        setSupportActionBar(toolbar);//Set the toolbar as the actionbar

        //Set background to a drawable blue background
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_drawable));
        actionBar.setDisplayHomeAsUpEnabled(true); //Back button
        actionBar.setDisplayShowHomeEnabled(true); //Allows back button
        actionBar.setTitle(""); //Ensures no double titles

        //Set title typeface
        TextView title = (TextView) toolbar.findViewById(R.id.action_title);
        title.setTypeface(handWriting);
    }

    public void visitSocialMedia(View v){
        int viewID = v.getId();
        String url = null;

        //Check with  view was clicked on
        switch(viewID){
            //Twitter was clicked on
            case R.id.aa_twitter:
                url = TWITTER_URL;
                break;
            //Facebook was clicked on
            case R.id.aa_facebook:
                url = FACEBOOK_URL;
                break;
            /*//Twitter was clicked on
            case R.id.aa_twitter:
                url = TWITTER_URL;
                break;
            //Facebook was clicked on
            case R.id.aa_facebook:
                url = FACEBOOK_URL;
                break;*/
        }

        Intent browserIntent = new Intent(Intent.ACTION_VIEW);
        browserIntent.setData(Uri.parse(url));
    }
}
