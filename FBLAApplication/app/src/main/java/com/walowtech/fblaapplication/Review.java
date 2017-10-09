package com.walowtech.fblaapplication;

/**
 * Class to hold review information
 *
 * @author Matthew Walowski
 * @version 1.0
 * @since 1.0
 */

//Created 10/9/2017
public class Review {

    int CID, UID;
    float rating;
    String timestamp;
    String comment;

    public Review(int CID, int UID, float rating, String comment, String timestamp){
        this.CID = CID;
        this.UID = UID;
        this.rating = rating;
        this.comment = comment;
        this.timestamp = timestamp;
    }
}
