package com.walowtech.fblaapplication;

import android.graphics.Bitmap;
import android.widget.ImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
public class Book{

    public float averageRating;
    public int numRatings, availableCopies, numCopies;
    public String GID, title, subTitle, textSnippet, subject, description, authors,
            bookDetails, thumbnail, smallThumbnail, ISBN10, ISBN13, WaitingList,
            WaitingListSize;
    public Bitmap coverSmall = null;
    public ArrayList<Review> reviews = new ArrayList<>();
    public ArrayList<Copy> copies = new ArrayList<>();

    ImageView imageView = null;

    public Book(String GID){
        this.GID = GID;
    }

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

    public String formatTimestamp(String timeString){
        long time = Long.decode(timeString);
        Date date = new Date(time * 1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        //sdf.setTimeZone(TimeZone.getTimeZone("GMT-4"));
        String formattedDate = sdf.format(date);
        return formattedDate;
    }

}
