package com.bankfinder.chathurangasandun.boatlocator;

import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bankfinder.chathurangasandun.boatlocator.db.DatabaseOpenHelper;
import com.bankfinder.chathurangasandun.boatlocator.mypolygon.Point;
import com.bankfinder.chathurangasandun.boatlocator.mypolygon.PolygonCodes;
import com.bankfinder.chathurangasandun.boatlocator.parse.DeviceUtil;
import com.bankfinder.chathurangasandun.boatlocator.parse.ParseConstrains;
import com.bankfinder.chathurangasandun.boatlocator.parse.SimpleInit;
import com.bankfinder.chathurangasandun.boatlocator.server.ServerConstrants;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.parse.Parse;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import bolts.Task;

/**
 * Created by Chathuranga Sandun on 5/20/2016.
 */
public class LocationService extends Service {
    public static final String BROADCAST_ACTION = "Hello World";
    private static final int TEN_MINUTES = 1000 * 10; //1000 *60*10
    public LocationManager locationManager;
    public MyLocationListener listener;
    public Location previousBestLocation ;
    private int level;
     Ringtone r;
    Vibrator vibrator;

    static double staticLat,staticLong;

    static boolean isInit = false;



    com.bankfinder.chathurangasandun.boatlocator.mypolygon.Polygon  outerBoader;
    DatabaseOpenHelper db ;

    double locationCorection[] = new double[4] ;
    int locationcount = 0;

    Intent intent;
    int counter = 0;

    static int locationIntID = 0;

    String responseValue="0";

    RequestQueue requestQueue;

    static String journyid,staticboatid;

    ArrayList<LatLng> polygonArrayList = new ArrayList<>();

    float lat,lng;

    String objectId;


    com.bankfinder.chathurangasandun.boatlocator.mypolygon.Polygon.Builder myouterboaderBuilder;
    com.bankfinder.chathurangasandun.boatlocator.mypolygon.Polygon myOuterBoaderPolygonFinal;





    @Override
    public void onCreate() {
        super.onCreate();
        intent = new Intent(BROADCAST_ACTION);
        Log.i("Location", "start______________________ ");

        db=new DatabaseOpenHelper(getApplicationContext());

        if(!isInit){
            Parse.initialize(getApplicationContext(), ParseConstrains.APPLICATION_KEY, ParseConstrains.CLIENT_KEY);
            isInit = true;
        }



        this.registerReceiver(this.batteryInfoReceiver,	new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALL);

        r = RingtoneManager.getRingtone(getApplication(), notification);

        vibrator = (Vibrator) this.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);


        requestQueue = Volley.newRequestQueue(this);

        //drawBoader();
    }

    private void drawBoader() {


        outerBoader = com.bankfinder.chathurangasandun.boatlocator.mypolygon.Polygon.Builder()
                .addVertex(new Point(6.760339f, 80.059024f))
                .addVertex(new Point(6.717031f, 80.027027f))
                .addVertex(new Point(6.692394f, 80.130658f))
                .addVertex(new Point(6.744053f, 80.176711f))
                .addVertex(new Point(6.819090f, 80.160090f))
                .build();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        //Log.i("Location", "start______________________ ");
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        listener = new MyLocationListener();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, TEN_MINUTES , 100, listener); //1000
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, TEN_MINUTES, 100, listener); //1000
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TEN_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TEN_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200; //10000

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }


    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }


    @Override
    public void onDestroy() {
        // handler.removeCallbacks(sendUpdatesToUI);
        super.onDestroy();
        Log.v("STOP_SERVICE", "DONE");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        String[] dateAndTime = new DeviceUtil(getApplicationContext()).getDateAndTime();


        SharedPreferences prefs = getSharedPreferences("DEVICE", MODE_PRIVATE);
        String boatID = prefs.getString("BoatId","COLO001-101");

        String date = dateAndTime[0];
        String time = dateAndTime[1];


        staticboatid= boatID;


        ParseObject locationobject = new ParseObject("location");
        locationobject.put("boat_id",boatID);
        locationobject.put("journy_id",journyid);
        locationobject.put("time",dateAndTime[1]);
        locationobject.put("date",dateAndTime[0]);
        locationobject.put("latitude",lat);
        locationobject.put("longitude",lng);
        locationobject.put("battery_state",level);
        locationobject.put("endornot","yes");
        locationobject.put("error","no");

        locationobject.saveEventually();

        locationManager.removeUpdates(listener);



    }

    public static Thread performOnBackgroundThread(final Runnable runnable) {
        final Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    runnable.run();
                } finally {

                }
            }
        };
        t.start();
        return t;
    }

    private BroadcastReceiver batteryInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            int  health= intent.getIntExtra(BatteryManager.EXTRA_HEALTH,0);
            int  icon_small= intent.getIntExtra(BatteryManager.EXTRA_ICON_SMALL,0);
            level= intent.getIntExtra(BatteryManager.EXTRA_LEVEL,0);
            int  plugged= intent.getIntExtra(BatteryManager.EXTRA_PLUGGED,0);
            boolean  present= intent.getExtras().getBoolean(BatteryManager.EXTRA_PRESENT);
            int  scale= intent.getIntExtra(BatteryManager.EXTRA_SCALE,0);
            int  status= intent.getIntExtra(BatteryManager.EXTRA_STATUS,0);
            String  technology= intent.getExtras().getString(BatteryManager.EXTRA_TECHNOLOGY);
            int  temperature= intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE,0);
            int  voltage= intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE,0);


            /*Log.i(TAG,
                    "Health: "+health+"\n"+
                            "Icon Small:"+icon_small+"\n"+
                            "Level: "+level+"\n"+
                            "Plugged: "+plugged+"\n"+
                            "Present: "+present+"\n"+
                            "Scale: "+scale+"\n"+
                            "Status: "+status+"\n"+
                            "Technology: "+technology+"\n"+
                            "Temperature: "+temperature+"\n"+
                            "Voltage: "+voltage+"\n");*/

        }


    };




    public class MyLocationListener implements LocationListener
    {

        public void onLocationChanged(final Location loc)
        {

            Log.i("Locationn", "Location changed");
            if(isBetterLocation(loc, previousBestLocation)) {
                double latitude = loc.getLatitude();
                double longitude = loc.getLongitude();
                intent.putExtra("Latitude", loc.getLatitude());
                intent.putExtra("Longitude", loc.getLongitude());
                intent.putExtra("Provider", loc.getProvider());
                sendBroadcast(intent);


                staticLat = latitude;
                staticLong = longitude;
                //check inside the boader
                //checkInsideTheBoarder((float)latitude,(float) longitude);

                String error = OrginalMapFragment.error;

                lat = (float)latitude;
                lng = (float)longitude;

                String[] dateAndTime = new DeviceUtil(getApplicationContext()).getDateAndTime();


                SharedPreferences prefs = getSharedPreferences("DEVICE", MODE_PRIVATE);
                String boatID = prefs.getString("BoatId","COLO001-101");

                String date = dateAndTime[0];
                String time = dateAndTime[1];


                boolean b = idFirstLocation();

                if(b){
                    journyid = boatID+"_"+date+"_"+time;
                    journyid = journyid.substring(0,journyid.length()-3);
                }


                if(counter == 0){



                    counter ++;
                }else if(counter == 1){





                    ParseObject locationobject = new ParseObject("location");
                    locationobject.put("boat_id",boatID);
                    locationobject.put("journy_id",journyid);
                    locationobject.put("time",dateAndTime[1]);
                    locationobject.put("date",dateAndTime[0]);
                    locationobject.put("latitude",latitude);
                    locationobject.put("longitude",longitude);
                    locationobject.put("battery_state",level);
                    locationobject.put("endornot","no");
                    locationobject.put("error",error);

                    Log.i("ServiceLocation",boatID+" "+journyid+" "+dateAndTime[0]+" "+dateAndTime[1]+" "+latitude+" "+longitude+" "+level+""+"no"+"no");

                    Log.i("send location parse", "yes");



                    objectId = locationobject.getObjectId();


                    //save parse server

                    Task<Void> voidTask = locationobject.saveEventually();

                    Log.i("locationServer", "onLocationChanged:" +voidTask.isCompleted());
                    counter=0;



                    //save internal databse
                    Log.i("internal", "start");
                    //insertInternalDatabase(date,time,latitude,longitude,level);
                }



                Log.i("Locationpoits",latitude + " " +longitude );

            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        public void onProviderDisabled(String provider)
        {
            Toast.makeText( getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT ).show();
        }


        public void onProviderEnabled(String provider)
        {
            Toast.makeText( getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
        }



    }



   /* private void insertInternalDatabase(String date, String time, double latitude, double longitude, int level){

        Log.i("Service", "insertInternalDatabase: "+locationIntID);

        if(locationIntID == 0){
            //db.deleteAllRaws();

            //updateStartLocationJourny(latitude,longitude);

        }

        Log.i("internal", "add");
        db.addLocation(new com.bankfinder.chathurangasandun.boatlocator.model.Location(locationIntID,date,time,latitude,longitude,level));
        Log.i("internal", "added");
        locationIntID++;
    } */

    public boolean idFirstLocation(){
        if(locationIntID == 0){
            locationIntID++;
            return true;


        }else{
            locationIntID++;
            return  false;
        }



    }










}