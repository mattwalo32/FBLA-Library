package com.walowtech.fblaapplication;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Class to hold review information
 *
 * @author Matthew Walowski
 * @version 1.0
 * @since 1.0
 */

//Created 10/9/2017
public class Review {

    public int CID, UID;
    public float rating;
    public String timestamp;
    public String comment, title, name;

    public Review(int CID, int UID, float rating, String comment, String title, String timestamp, String name){
        this.CID = CID;
        this.UID = UID;
        this.rating = rating;
        this.comment = comment;
        this.timestamp = timestamp;
        this.title = title;
        this.name = name;
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
