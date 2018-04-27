package com.walowtech.fblaapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Class extends DialogFragment and serves as a custom
 * dialog for when the user presses the checkout button.
 * The dialog takes information about all copies of a book
 * and adapts them to the dialog window. It shows information about
 * how many books are out, how many book are there total,
 * and how many people are in line for a book. In the dialog
 * the user is given the option to checkout/waitlist a book.
 *
 * @author Matthew Walowski
 * @version 1.0
 * @since 1.0
 */

//Created 10/20/17
public class SelectBookFragment extends DialogFragment {

    static ArrayList<Copy> copies;
    static int numAvailable;
    static BookDetailsActivity bookDetailsActivity;

    static SelectBookFragment newInstance(BookDetailsActivity BDA, ArrayList<Copy> Copies, int availiableCopies) {
        copies = Copies;
        bookDetailsActivity = BDA;
        numAvailable = availiableCopies;

        return new SelectBookFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View dialog = inflater.inflate(R.layout.checkout_dialog, container, false);

        //Get main layout elements
        TextView available = (TextView) dialog.findViewById(R.id.currently_available);
        TextView total = (TextView) dialog.findViewById(R.id.total_books);
        LinearLayout itemsLayout = (LinearLayout) dialog.findViewById(R.id.book_items_layout);

        //Set main layout text
        int numCopies = copies.size();
        String totalText = "Total: " + numCopies;
        total.setText(totalText);


        //Adapt views
        if(!copies.isEmpty() && copies != null){
            for(int i = 0; i < copies.size(); i++){
                final Copy currentCopy = copies.get(i);
                final boolean waitList;

                //Add View
                View bookItem = inflater.inflate(R.layout.checkout_item, container, false);
                itemsLayout.addView(bookItem);

                //Get elements
                TextView btnGet = (TextView) bookItem.findViewById(R.id.btn_get);
                TextView bookInfo = (TextView) bookItem.findViewById(R.id.special_book_info);
                TextView waitSize = (TextView) bookItem.findViewById(R.id.list_size);

                //Adapt information
                String bookInfoText = currentCopy.copyInfo;
                int listSize = currentCopy.waitingListAmount;
                int secondDigit = listSize % 10;

                //Find appropriate number ending
                String numberAppendix = null;

                //If the list size is greater than 20, use the second digit to find the appropriate ending
                if(listSize > 20)
                numberAppendix = secondDigit == 1 ? "st" :
                        (secondDigit == 2 ? "nd" :
                                (secondDigit == 3 ? "rd" : "th"));
                else //If list smaller than 20, use size to determine suffix
                    numberAppendix = listSize == 1 ? "st" :
                            (listSize == 2 ? "nd" :
                                    (listSize == 3 ? "rd" : "th"));
                String listSizeText =  listSize + numberAppendix + " in line";
                if(bookInfoText == null || bookInfoText.trim().equals(""))
                    bookInfoText = "Paperback";
                bookInfo.setText(bookInfoText);

                //Check if waitlist information is nessecary and if book is available
                if(listSize < 1) {
                    waitSize.setVisibility(View.INVISIBLE);
                    waitList = false;
                }else{
                    waitSize.setText(listSizeText);
                    btnGet.setText("Wait List");
                    waitList = true;
                }

                //Set typefaces
                btnGet.setTypeface(MainActivity.handWriting);
                bookInfo.setTypeface(MainActivity.handWriting);
                waitSize.setTypeface(MainActivity.handWriting);

                //Set button click listener
                btnGet.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bookDetailsActivity.checkoutBook(currentCopy, waitList);
                        dismiss();
                    }
                });
            }

            //Set availiablility text
            String availiableText = "Currently Available: " + numAvailable;
            available.setText(availiableText);

            //Set main layout typefaces
            available.setTypeface(MainActivity.handWriting);
            total.setTypeface(MainActivity.handWriting);
        }

        return dialog;
    }
}
