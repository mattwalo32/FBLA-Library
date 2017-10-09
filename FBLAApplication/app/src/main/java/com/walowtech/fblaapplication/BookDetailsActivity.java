package com.walowtech.fblaapplication;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Created by mattw on 10/7/2017.
 */

//Created 10/7/2017
public class BookDetailsActivity extends Activity {

    private ScrollView mScrollView;
    private TextView mDescription;
    private TextView mTitle;
    private TextView mSubTitle;
    private TextView mAuthors;
    private TextView mCheckout;
    private TextView mAvgRating;
    private TextView mNumRatings;
    private LinearLayout mLinearLayout;
    private ImageView mBookImage;
    private RelativeLayout mTopRowLayout;
    private LinearLayout mDescriptionLayout;

    private Typeface handWriting;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);

        handWriting = Typeface.createFromAsset(getAssets(), "fonts/hand_writing.ttf");

        mScrollView = (ScrollView) findViewById(R.id.bd_scrollview);
        mDescription = (TextView) findViewById(R.id.bd_description);
        mTitle = (TextView) findViewById(R.id.bd_title);
        mSubTitle = (TextView) findViewById(R.id.bd_subtitle);
        mAuthors = (TextView) findViewById(R.id.bd_authors);
        mCheckout = (TextView) findViewById(R.id.bd_checkout);
        mAvgRating = (TextView) findViewById(R.id.bd_avg_rating);
        mNumRatings = (TextView) findViewById(R.id.bd_num_ratings);
        mLinearLayout = (LinearLayout) findViewById(R.id.bd_linear_layout);
        mBookImage = (ImageView) findViewById(R.id.bd_small_image);
        mTopRowLayout = (RelativeLayout) findViewById(R.id.bd_top_row_layout);
        mDescriptionLayout = (LinearLayout) findViewById(R.id.bd_description_layout);

        mDescription.setTypeface(handWriting);
        mTitle.setTypeface(handWriting);
        mSubTitle.setTypeface(handWriting);
        mAuthors.setTypeface(handWriting);
        mCheckout.setTypeface(handWriting);
        mAvgRating.setTypeface(handWriting);
        mNumRatings.setTypeface(handWriting);

        //TODO finish elevations
        setElevation(mLinearLayout, 12);
        setElevation(mTopRowLayout, 12);
        setElevation(mDescriptionLayout, 12);
        setElevation(mBookImage, 8);
        setElevation(mCheckout, 8);
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
}
