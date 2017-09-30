package com.walowtech.fblaapplication.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.transition.Slide;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.walowtech.fblaapplication.MainActivity;
import com.walowtech.fblaapplication.R;
import com.walowtech.fblaapplication.ViewPagerItem;

import java.util.ArrayList;

import static android.view.View.GONE;

/**
 * Adapter for PageViewer slideshow on MainActivity screen.
 *
 * @author Matthew Walowski
 * @version 1.0
 * @since 1.0
 */

//Created 9/23/17
public class SlideshowAdapter extends PagerAdapter {

    private ArrayList<ViewPagerItem> slides;
    private Context context;
    private LayoutInflater layoutInflater;

    public SlideshowAdapter(Context context, ArrayList<ViewPagerItem> slides){
        this.context = context;
        this.slides = slides;
    }

    @Override
    public int getCount() {
        return slides.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == (RelativeLayout)object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ViewPagerItem curSlide = slides.get(position);

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = layoutInflater.inflate(R.layout.view_pager_slide, container, false);

        ImageView imageView = (ImageView) itemView.findViewById(R.id.slide_image);
        TextView textView = (TextView) itemView.findViewById(R.id.tv_slideshow);
        ProgressBar progressBar = (ProgressBar) itemView.findViewById(R.id.pb_slideshow);

        imageView.setImageBitmap(curSlide.image);
        textView.setText(curSlide.description);
        textView.setTypeface(MainActivity.handWriting);
        progressBar.setVisibility(GONE);

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout)object);
    }
}
