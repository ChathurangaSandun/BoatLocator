package com.bankfinder.chathurangasandun.boatlocator.parse;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Chathuranga Sandun on 6/4/2016.
 */
public class DeviceUtil {


    private Context ctx;
    public DeviceUtil(Context ctx){
        this.ctx =ctx;
    }

    private final String TAG = "DeviceUtil";
    int level=0;



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

    public  String[] getDateAndTime(){
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));
        Date currentLocalTime = cal.getTime();
        DateFormat time = new SimpleDateFormat("HH:mm:ss a");
// you can get seconds by adding  "...:ss" to it
        time.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));

        String localTime = time.format(currentLocalTime);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(cal.getTime());
        Log.i(TAG, formattedDate+" "+localTime);

        return new String[]{formattedDate,localTime};


    }

    public String[] getSimNumber(){
        TelephonyManager telemamanger = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        String getSimSerialNumber = telemamanger.getSimSerialNumber();
        String getSimNumber = telemamanger.getLine1Number();

        return new String[]{getSimNumber,getSimSerialNumber};

    }


/* sim details
TelephonyManager telemamanger = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
String getSimSerialNumber = telemamanger.getSimSerialNumber();
String getSimNumber = telemamanger.getLine1Number();

<uses-permission android:name="android.permission.READ_PHONE_STATE"/>
 */




}
