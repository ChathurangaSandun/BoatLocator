package com.bankfinder.chathurangasandun.boatlocator;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bankfinder.chathurangasandun.boatlocator.db.DatabaseOpenHelper;
import com.bankfinder.chathurangasandun.boatlocator.parse.DeviceUtil;
import com.bankfinder.chathurangasandun.boatlocator.server.ServerConstrants;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */


    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager viewPager;

    String beforePauseLanguage = LanguageSelector.getCurrentLangnuage() ;
    private TabLayout tabLayout;
    private final String TAG = "MainActivity";
    private AccountHeader headerDrawer;
    private Drawer drawer;
    Intent i;

    private PendingIntent pendingIntent;
    boolean a= false;
    Vibrator v;

    RequestQueue queue ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("AA", "start");

        /*
        ParseObject locationobject = new ParseObject("location");
        locationobject.put("boatid","1001");
        locationobject.put("time","12.36");
        locationobject.put("date","2015-5-29");
        locationobject.put("lat",5);
        locationobject.put("long",4);
        locationobject.put("batry",25);
        locationobject.put("passbool",false);

        try {
            locationobject.save();
        } catch (ParseException e
            e.printStackTrace();
        }*/

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        queue= Volley.newRequestQueue(this);




        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALL);
        final Ringtone r = RingtoneManager.getRingtone(getApplication(), notification);

        v = (Vibrator) this.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);

        //set defult fragment
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        OrginalMapFragment orginalMapFragment = new OrginalMapFragment();
        fragmentTransaction.replace(R.id.container, orginalMapFragment);
        fragmentTransaction.commit();


        Log.i(TAG, "onCreate: create");
        //database
        DatabaseOpenHelper databaseOpenHelper = new DatabaseOpenHelper(this);
        databaseOpenHelper.createdatabase();


        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.help_layout);
        dialog.getWindow() .setBackgroundDrawableResource(R.color.red);
        //dialog.setTitle("HELP ME!!!!!!");
        ImageButton im = (ImageButton) dialog.findViewById(R.id.imageButton);

        im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String boatID = OwnerActivity.ownerboatid;
                Log.i(TAG, "boatID: "+boatID);

                sendSMS("0711192365","PLEASE HELP ME !!!!! This is from "+boatID+". I,m getting a trouble in the sea. (latitude "+LocationService.staticLat+ ", longitude"+LocationService.staticLong+").");
// NADUN 0711192365 THATHTHA 0719735738


                if(LocationService.journyid != null){
                    String url = ServerConstrants.SERVEWR_URL+"connections/journy/Emergency.php";
                    informEmergencyCase(LocationService.staticLat,LocationService.staticLong,url,boatID);
                }else{
                    Toast.makeText(getApplication(),"Still you don't start a journy yet",Toast.LENGTH_LONG).show();
                }


            }
        });



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                //hlp dialog
                dialog.show();


                /*if(a){
                   r.stop();
                    v.cancel();
                    a=false;
                }else{
                    r.play();
                    v.vibrate(Integer.MAX_VALUE);

                    a= true;
                }*/







                //stopService(i);
            }
        });





        //navigation drawer

        //create navigation drawer

        headerDrawer = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.background)
                .addProfiles(
                        new ProfileDrawerItem().withName("Sandun").withNameShown(true).withEmail("clivekumara@gmail.com").withIcon(getResources().getDrawable(R.drawable.my_boat))

                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {

                        Log.d("profile", profile.getName().toString());





                        return false;
                    }
                })
                .build();

        //if you want to update the items at a later time it is recommended to keep it in a variable
        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withName("Home").withDescription("see Main  marain map").withDescriptionTextColorRes(R.color.accent).withIcon(getResources().getDrawable(R.drawable.dott));
        PrimaryDrawerItem item2 = new PrimaryDrawerItem().withName("Weather").withDescription("Full Weather Details of your area").withDescriptionTextColorRes(R.color.accent).withIcon(getResources().getDrawable(R.drawable.dott));
        PrimaryDrawerItem item3 = new PrimaryDrawerItem().withName("My Trips").withDescription("Path history in last 7 days ").withDescriptionTextColorRes(R.color.accent).withDescriptionTextColorRes(R.color.colorPrimary).withIcon(getResources().getDrawable(R.drawable.dott));
        PrimaryDrawerItem item4 = new PrimaryDrawerItem().withName("Emergency").withDescription("Inform / SOS facility with authoried people").withDescriptionTextColorRes(R.color.accent).withIcon(getResources().getDrawable(R.drawable.dott));
        PrimaryDrawerItem item5 = new PrimaryDrawerItem().withName("Chat").withDescription("Chat With Others").withDescriptionTextColorRes(R.color.accent).withIcon(getResources().getDrawable(R.drawable.dott));
        PrimaryDrawerItem item6 = new PrimaryDrawerItem().withName("Others").withDescription("").withDescriptionTextColorRes(R.color.accent).withIcon(getResources().getDrawable(R.drawable.dott));





//create the drawer and remember the `Drawer` result object
        drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerDrawer)

                .addDrawerItems(
                        item1,
                        item2,
                        item3,
                        item4,
                        item5,
                        item6
                )

                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

                        if (drawerItem != null) {
                            String selectedItem = ((Nameable) drawerItem).getName().getText(MainActivity.this);
                            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                            if ("Home".equals(selectedItem)) {
                                Log.d(TAG, "Home");
                                OrginalMapFragment orginalMapFragment = new OrginalMapFragment();
                                fragmentTransaction.replace(R.id.container, orginalMapFragment);
                            }if ("Weather".equals(selectedItem)){
                                Log.d(TAG, "Weather");
                                WeatherMapFragment weatherMapFragment = new WeatherMapFragment();
                                fragmentTransaction.replace(R.id.container, weatherMapFragment);

                            }else if ("My Trips".equals(selectedItem)) {
                                Log.d(TAG, "ATM Finder");
                            } else if ("Emergency".equals(selectedItem)) {
                                Log.d(TAG, "Nearest ATM and Branch");
                            } else if ("Chat".equals(selectedItem)) {
                                openApp(getApplication(),"info.androidhive.gcm");

                            }else if("Check Update".equals(selectedItem)){
                                Log.d(TAG, "Check Update");
                            }else if("Developer".equals(selectedItem)){
                                Log.d(TAG, "Developer");

                            }else if("GitHub".equals(selectedItem)){
                                Log.d(TAG, "GitHub");
                            }else if("Official Web Site".equals(selectedItem)){
                                Log.d(TAG, "Official Web Site");
                            }
                            fragmentTransaction.commit();
                        }
                        return false;
                    }
                })
                .build();


        drawer.addStickyFooterItem(new PrimaryDrawerItem().withName("Check Update").withIcon(getResources().getDrawable(R.drawable.dott)));

        drawer.addStickyFooterItem(new DividerDrawerItem());
        drawer.addStickyFooterItem(new PrimaryDrawerItem().withName("Developer").withIcon(getResources().getDrawable(R.drawable.dott)));

        drawer.addStickyFooterItem(new DividerDrawerItem());
        //drawer.addStickyFooterItem(new PrimaryDrawerItem().withName("GitHub").withIcon(getResources().getDrawable(R.drawable.ic_github)));


        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        drawer.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);






        //start....
        Log.i("MainActicvtiy", "start.................................");
        //i = new Intent(this,LocationService.class);
        //startService(i);






    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this,SettingsActivity.class));

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();

        //get current language
        LanguageSelector.setCurrentLangnuage(getApplicationContext());
        String currentLangnuage = LanguageSelector.getCurrentLangnuage();
        if(!beforePauseLanguage.equals(currentLangnuage)) {
            Log.d("main", "i'm changed ");

            if("en".equals(currentLangnuage)){
                setTitle("Home");
            }else if("sl".equals(currentLangnuage)){
                setTitle("ගෘහය");
            }else{
                setTitle("வீட்டில்");
            }
        }else{
            Log.d("main", "i'm not changed");
        }



    }

    @Override
    protected void onPause() {
        super.onPause();
        beforePauseLanguage =  LanguageSelector.getCurrentLangnuage();
        Log.d("main","i'm pause");
    }




    /*//alarm system
    public void start() {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int interval = 1000;

        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
        Toast.makeText(this, "Alarm Set", Toast.LENGTH_SHORT).show();
    }

    public void cancel() {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);
        Toast.makeText(this, "Alarm Canceled", Toast.LENGTH_SHORT).show();
    }*/

    public static boolean openApp(Context context, String packageName) {
        PackageManager manager = context.getPackageManager();
        Intent i = manager.getLaunchIntentForPackage(packageName);
        if (i == null) {
            return false;
            //throw new PackageManager.NameNotFoundException();
        }
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        context.startActivity(i);
        return true;
    }

    //send sms
    private void sendSMS(String phoneNumber, String message)
    {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
                new Intent(SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
                new Intent(DELIVERED), 0);

        //---when the SMS has been sent---
        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS sent",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Generic failure",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "No service",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "Null PDU",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Radio off",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SENT));

        //---when the SMS has been delivered---
        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS not delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(DELIVERED));

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
    }



    private void informEmergencyCase(final double lat, final double lng , String url, final String boatID) {






        Log.i("startpoint", "imform emergency: "+url);


// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.i("startpoint", response);


                    }


                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("responseValue", "onErrorResponse: "+error);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                String[] dateAndTime = new DeviceUtil(getApplicationContext()).getDateAndTime();

                Map<String, String> params = new HashMap<String, String>();
                params.put("journyid", LocationService.journyid);
                params.put("boatid", boatID);
                params.put("startlat", String.valueOf(lat));
                params.put("startlong", String.valueOf(lng));
                params.put("time",dateAndTime[1]);
                params.put("date",dateAndTime[0]);
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };




// Add the request to the RequestQueue.
        queue.add(stringRequest);



    }



}
