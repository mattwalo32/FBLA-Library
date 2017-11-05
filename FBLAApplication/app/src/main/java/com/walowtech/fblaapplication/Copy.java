package com.walowtech.fblaapplication;

import android.graphics.Bitmap;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Class that holds a Copy object
 *
 * @author Matthew Walowski
 * @version 1.0
 * @since 1.0
 */

//Created 10/9/2017
public class Copy {

    public int BID, waitingListAmount;
    public String checkoutTime, returnTime, copyInfo, title, subtitle, authors, thumbnail, GID;
    public Bitmap bitmap;
    float averageRating;

    public Copy(int BID){
        this.BID = BID;
    }

    //TODO get book info
    public Copy(int BID, int waitingListAmount, String checkoutTime, String returnTime){
        this.BID = BID;
        this.waitingListAmount = waitingListAmount;
        this.checkoutTime = checkoutTime;
        this.returnTime = returnTime;
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
