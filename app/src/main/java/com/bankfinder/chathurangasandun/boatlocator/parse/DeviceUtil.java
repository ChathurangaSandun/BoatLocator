package com.bankfinder.chathurangasandun.boatlocator.parse;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;

/**
 * Created by Chathuranga Sandun on 6/4/2016.
 */
public class DeviceUtil {


    private Context ctx;
    public DeviceUtil(Context ctx){
        this.ctx =ctx;
    }




    public String getDevicekey(){
        String identifier = null;
        TelephonyManager tm = (TelephonyManager)ctx.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm != null) {
            identifier = tm.getDeviceId();
        }
        if (identifier == null || identifier .length() == 0) {
            identifier = Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.ANDROID_ID);
        }

        return identifier;
    }

    public String getGmailAccount(){
        String acc = null;
        try{
            AccountManager accountManager = (AccountManager) ctx.getSystemService(ctx.ACCOUNT_SERVICE);
            Account accounts[] = accountManager.getAccounts();
            if(accounts ==null){
                acc = "emulator@test.com";
            }
            else{
                for(Account account: accounts){
                    if(account.type.equals("com.google")){
                        acc = account.name;
                    }
                }
            }
            if(acc==null)
                acc = "emulator@test.com";

        }
        catch (Exception e){
            e.printStackTrace();

        }
        return acc;
    }


}
