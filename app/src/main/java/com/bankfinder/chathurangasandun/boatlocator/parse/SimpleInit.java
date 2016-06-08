package com.bankfinder.chathurangasandun.boatlocator.parse;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.parse.Parse;

/**
 * Created by KP on 27/04/14.
 */
public class SimpleInit extends AsyncTask<Void,Void,Void> {

    String TAG = "AsyncInitSetup";
    private Context ctx;
    public SimpleInit(Context ctx){
        this.ctx = ctx;
    }
    @Override
    protected Void doInBackground(Void... params) {

        try{

            Parse.initialize(ctx, "2sxxue00oXxn05OTEw0JosIdIIq8XuUTzx4v7E3P", "yj0BVERpwbMHFhE1rzc995RalyjMX12tx6vIUyhH");
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
