package com.walowtech.fblaapplication.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Class containing any methods pertaining to error handling
 *
 * @author Matthew Walowski
 * @version 1.0
 * @since 1.0
 */

public class ErrorUtils {

    public static void errorDialog(Context context, String errorTitle, String errorMessage){
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(errorTitle);
        alertDialog.setMessage(errorMessage);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
}
