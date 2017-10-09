package com.walowtech.fblaapplication;

import android.graphics.Bitmap;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * The class for a book object.
 *
 * This class contains all of the variables that should belong
 * to an instance of a book.
 *
 * @author Matthew Walowski
 * @version 1.0
 * @since 1.0
 */

//Created 9/17/17
public class Book {

    public float averageRating;
    public int numRatings, availableCopies, numCopies;
    public String GID, title, subTitle, textSnippet, subject, description, authors,
            bookDetails, thumbnail, smallThumbnail, ISBN10, ISBN13, WaitingList,
            WaitingListSize;
    public Bitmap coverSmall = null;
    public ArrayList<Review> reviews = new ArrayList<>();
    public ArrayList<Copy> copies = new ArrayList<>();

    ImageView imageView = null;

    public Book(String subject, String title, String GID, String smallThumbnail, float averageRating){
        this.subject = subject;
        this.title = title;
        this.GID = GID;
        this.smallThumbnail = smallThumbnail;
        this.averageRating = averageRating;
    }

    public Book(float averageRating, int numRatings, int numCopies, int availableCopies,
                String GID, String title, String subTitle, String textSnippet, String subject, String description, String authors,
                String thumbnail, String smallThumbnail, String ISBN10, String ISBN13){
        this.averageRating = averageRating;
        this.numRatings = numRatings;
        this.numCopies = numCopies;
        this.availableCopies = availableCopies;
        this.GID = GID;
        this.title = title;
        this.subTitle = subTitle;
        this.textSnippet = textSnippet;
        this.subject = subject;
        this.description = description;
        this.authors = authors;
        this.thumbnail = thumbnail;
        this.smallThumbnail = smallThumbnail;
        this.ISBN10 = ISBN10;
        this.ISBN13 = ISBN13;
    }

}
