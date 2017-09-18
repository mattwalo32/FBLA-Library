package com.walowtech.fblaapplication;

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
    public int BID, copies, UID, numRatings;
    public long checkoutTimestamp, returnTimestamp;
    public String GID, title, subTitle, textSnippet, subject, description, authors,
            bookDetails, thumbnail, smallThumbnail, ISBN10, ISBN13, WaitingList,
            WaitingListSize;

    public Book(String subject, String title, String GID, String smallThumbnail, float averageRating){
        this.subject = subject;
        this.title = title;
        this.GID = GID;
        this.smallThumbnail = smallThumbnail;
        this.averageRating = averageRating;
    }

    public Book(float averageRating, int BID, int copies, int UID, int numRatings, long checkoutTimestamp, long returnTimestamp,
                String GID, String title, String subTitle, String textSnippet, String subject, String description, String authors,
                String bookDetails, String thumbnail, String smallThumbnail, String ISBN10, String ISBN13, String WaitingList, String WaitingListSize){
        this.averageRating = averageRating;
        this.BID = BID;
        this.copies = copies;
        this.UID = UID;
        this.numRatings = numRatings;
        this.checkoutTimestamp = checkoutTimestamp;
        this.returnTimestamp = returnTimestamp;
        this.GID = GID;
        this.title = title;
        this.subTitle = subTitle;
        this.textSnippet = textSnippet;
        this.subject = subject;
        this.description = description;
        this.authors = authors;
        this.bookDetails = bookDetails;
        this.thumbnail = thumbnail;
        this.smallThumbnail = smallThumbnail;
        this.ISBN10 = ISBN10;
        this.ISBN13 = ISBN13;
        this.WaitingList = WaitingList;
        this.WaitingListSize = WaitingListSize;
    }

}
