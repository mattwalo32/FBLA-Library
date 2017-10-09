package com.walowtech.fblaapplication;

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
    public String checkoutTime, returnTime;

    public Copy(int BID, int waitingListAmount, String checkoutTime, String returnTime){
        this.BID = BID;
        this.waitingListAmount = waitingListAmount;
        this.checkoutTime = checkoutTime;
        this.returnTime = returnTime;
    }
}
