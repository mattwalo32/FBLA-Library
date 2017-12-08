package com.walowtech.fblaapplication.Utils;

import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Gets and refreshes the Firebase ID when needed
 *
 * @author Matthew Walowski
 * @version 1.0
 * @since 1.0
 */

//Created 11/18/2017
public class FirebaseIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh(); //Called if app deletes instance ID, app is restored, user uninstalls/reinstalled, data is cleared
    }
}
