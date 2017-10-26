package com.walowtech.fblaapplication;

import android.graphics.Bitmap;

/**
 * Class holds an instance of an item in the view pager. It
 * contains information about the image's description, URL, and
 * linked item action.
 *
 * @author Matthew Walowski
 * @version 1.0
 * @since 1.0
 */

//Created 9/30/17
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
