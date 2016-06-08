package com.bankfinder.chathurangasandun.boatlocator.parse;

import android.util.Log;

import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by Chathuranga Sandun on 6/4/2016.
 */
public class ParseUtil {

    final String TAG = "ParseUtil";

    public ParseUser userSignIn(String email, String imei) throws Exception {

        Log.d(TAG, "userSignIn");

        ParseUser pu = ParseUser.logIn(email, imei);
        Log.d(TAG, "Session Token " + pu.getSessionToken());

        return pu;

    }


}
