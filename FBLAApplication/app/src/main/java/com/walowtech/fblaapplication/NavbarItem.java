package com.walowtech.fblaapplication;

import android.graphics.Bitmap;
import android.widget.ImageView;

import org.w3c.dom.Text;

/**
 * Class to contain a navbar item
 *
 * @author Matthew Walowski
 * @version 1.0
 * @since 1.0
 */

//Created 9/24/2017
public class NavbarItem {

    public int image;
    public String text;

    public NavbarItem(int image, String text){
        this.image = image;
        this.text = text;
    }

}
