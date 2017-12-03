package com.walowtech.fblaapplication;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.walowtech.fblaapplication.Utils.SlideshowAdapter;

import java.util.ArrayList;

public class AboutActivity extends NavDrawerActivity {

    private LinearLayout mMainLayout;
    private LinearLayout mSecondLayer;
    private TextView mSchoolName;
    private TextView mAboutSchool;
    private TextView mAboutApp;
    private ViewPager mPager;

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
        libMap.image = BitmapFactory.decodeResource(getResources(), R.drawable.library_map_resized);

        slides.add(libMap);

        pagerAdapter = new SlideshowAdapter(this, slides, false);
        mPager.setAdapter(pagerAdapter);


        //TODO app bar
    }
}
