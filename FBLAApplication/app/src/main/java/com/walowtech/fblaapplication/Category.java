package com.walowtech.fblaapplication;

import android.view.View;

import java.util.ArrayList;

/**
 * Class that holder a category object.
 *
 * This object is referenced whenever a category needs to be displayed on the screen.
 *
 * @author Matthew Walowski
 */

//Created 9/18/17
public class Category {

    public ArrayList<Book> books = new ArrayList<>();
    public String categoryName;
    MainActivity.BookAdapter bookAdapter;

    public Category(ArrayList<Book> books, String categoryName){
        this.books = books;
        this.categoryName = categoryName;
    }

}
