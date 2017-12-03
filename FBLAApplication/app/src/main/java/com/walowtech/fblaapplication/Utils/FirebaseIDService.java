package com.walowtech.fblaapplication.Utils;

import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by mattw on 11/18/2017.
 */

public class FirebaseIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh(); //TODO look at documentation
    }
}
