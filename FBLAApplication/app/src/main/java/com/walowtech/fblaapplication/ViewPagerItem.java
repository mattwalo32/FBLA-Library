package com.walowtech.fblaapplication;

import android.graphics.Bitmap;

/**
 * Created by mattw on 9/30/2017.
 */

//TODO doc
public class ViewPagerItem {

    public String shortDescription;
    public String description;
    public String linkedItem;
    public String imageURL;
    public Bitmap image;

    public ViewPagerItem(String shortDescription, String description, String linkedItem, String imageURL){
        this.shortDescription = shortDescription;
        this.description = description;
        this.linkedItem = linkedItem;
        this.imageURL = imageURL;
    }
}
