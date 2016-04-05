package com.bankfinder.chathurangasandun.boatlocator;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by Chathuranga Sandun on 4/6/2016.
 */
public  class LanguageSelector {

    private static String currentLangnuage = "en";

    public LanguageSelector() {

    }

    public static void setCurrentLangnuage(Context context){
        SharedPreferences language_list = PreferenceManager.getDefaultSharedPreferences(context);
        String selectedLanguage = language_list.getString("language_list","en");
        Log.d("language", selectedLanguage);
        currentLangnuage = selectedLanguage;
    }

    public static String getCurrentLangnuage() {
        return currentLangnuage;
    }
}
