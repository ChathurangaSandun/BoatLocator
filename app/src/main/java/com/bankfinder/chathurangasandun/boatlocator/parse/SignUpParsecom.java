package com.bankfinder.chathurangasandun.boatlocator.parse;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.parse.Parse;

/**
 * Created by Chathuranga Sandun on 6/4/2016.
 */
public class SignUpParsecom extends AsyncTask<Void,Void,Void> {

    String TAG = "AsyncInitSetup";
    private Context ctx;
    public SignUpParsecom(Context ctx){
        this.ctx = ctx;
    }
    @Override
    protected Void doInBackground(Void... params) {

        try{

            Parse.initialize(ctx, "2sxxue00oXxn05OTEw0JosIdIIq8XuUTzx4v7E3P", "yj0BVERpwbMHFhE1rzc995RalyjMX12tx6vIUyhH");

            DeviceUtil deviceUtil = new DeviceUtil(ctx);
            String email = deviceUtil.getGmailAccount();
            Log.i(TAG+"dfd", email);
            String pwd = "922760047v";

            if(syncUtils.isSyncAccountExists()){
                Log.i(TAG, "SyncAdapter is already exist");
            }
            else{
                syncUtils.createSyncAccount(email,pwd,email);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
