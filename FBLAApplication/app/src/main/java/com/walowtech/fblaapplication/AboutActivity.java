package com.walowtech.fblaapplication;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import com.walowtech.fblaapplication.Utils.SlideshowAdapter;
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
    private static final String FACEBOOK_URL = "https://www.facebook.com/Preface-443875659398043/";
    private static final String FACEBOOK_APP_URL = "fb://page/443875659398043";
    private static final String GOOGLE_PLUS_URL = "https://plus.google.com/u/2/109307757009188455261";
    private static final String INSTAGRAM_URL = "https://www.instagram.com/prefaceapplication/";

    private LinearLayout mMainLayout;
    private LinearLayout mSecondLayer;
    private ViewPager mPager;

    private TextView mAboutSchool;
    private TextView mAboutApp;
    private TextView mSchoolName;
    private TextView mFollowUs;

    private SearchView searchBar;

    private SlideshowAdapter pagerAdapter;
    private ArrayList<ViewPagerItem> slides = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        //Init vars
        mMainLayout = findViewById(R.id.aba_main_layout);
        mSecondLayer = findViewById(R.id.aba_second_layer);
        mSchoolName = findViewById(R.id.aba_school_name);
        mAboutSchool = findViewById(R.id.aba_about_school);
        mFollowUs = findViewById(R.id.aa_follow_text);
        mAboutApp = findViewById(R.id.aba_about_app);
        mPager = findViewById(R.id.aa_pager);

        //Set fonts
        mSchoolName.setTypeface(handWriting);
        mAboutSchool.setTypeface(handWriting);
        mAboutApp.setTypeface(handWriting);
        mFollowUs.setTypeface(handWriting);

        //Create shadows
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
        Toolbar toolbar = findViewById(R.id.aba_toolbar);//Find toolbar in layout
        setSupportActionBar(toolbar);//Set the toolbar as the actionbar

        //Set background to a drawable blue background
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_drawable));
        actionBar.setDisplayHomeAsUpEnabled(true); //Back button
        actionBar.setDisplayShowHomeEnabled(true); //Allows back button
        actionBar.setTitle(""); //Ensures no double titles

        //Set title typeface
        TextView title = toolbar.findViewById(R.id.action_title);
        title.setTypeface(handWriting);
    }

    /**
     * This method checks which media icon was pressed and then constructs
     * the appropriate URL to take the user to. Then an intent for
     * the respective application on the device is constructed and launched. If
     * the respective application is not installed on the device, the user
     * will be taken to an online version of the social media source.
     * @param v The view that invokes this method.
     */
    public void visitSocialMedia(View v){
        int viewID = v.getId();
        String url = null;
        Intent browserIntent = new Intent(Intent.ACTION_VIEW);
        //Check with  view was clicked on
        switch(viewID){
            //Twitter was clicked on
            case R.id.aa_twitter:
                url = TWITTER_URL;
                browserIntent.setPackage("com.twitter.android");
                break;
            //Google Plus was clicked on
            case R.id.aa_google_plus:
                url = GOOGLE_PLUS_URL;
                browserIntent.setPackage("com.google.android.apps.plus");
                break;
            //Facebook was clicked on
            case R.id.aa_instagram:
                url = INSTAGRAM_URL;
                browserIntent.setPackage("com.instagram.android");
                break;
            //Facebook was clicked on
            case R.id.aa_facebook:
                try {
                    browserIntent.setData(Uri.parse(FACEBOOK_APP_URL));
                    startActivity(browserIntent);
                }catch(ActivityNotFoundException e){
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(FACEBOOK_URL)));
                }
                return;
        }
        //Set the url to visit
        browserIntent.setData(Uri.parse(url));

        //Try to launch in native application, but launch in browser if app is not installed
        try {
            startActivity(browserIntent);
        }catch(ActivityNotFoundException e){
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        }
    }
}
