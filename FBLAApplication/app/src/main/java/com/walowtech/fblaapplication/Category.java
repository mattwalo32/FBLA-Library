package com.walowtech.fblaapplication;

import java.util.ArrayList;

/**
 * Created by mattw on 9/18/2017.
 */

//Created 9/18/17
public class Category {

    public ArrayList<Book> books = new ArrayList<>();
    public String categoryName;

    public Category(ArrayList<Book> books, String categoryName){
        this.books = books;
        this.categoryName = categoryName;
    }

}
