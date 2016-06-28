package com.bankfinder.chathurangasandun.boatlocator.internetconnection;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bankfinder.chathurangasandun.boatlocator.LoginActivity;
import com.bankfinder.chathurangasandun.boatlocator.MainActivity;
import com.bankfinder.chathurangasandun.boatlocator.OwnerActivity;
import com.bankfinder.chathurangasandun.boatlocator.R;

import java.util.Timer;
import java.util.TimerTask;

public class NetworkConnectionActivity extends AppCompatActivity {

    private final   String TAG = "NestworkActivity";
    private  boolean isNetwork = false;
    private  boolean isGPS = false;
    LocationManager locationManager;

     TextView textViewNetwork;
    TextView tvGPs;



    Runnable mUpdateUI;


    int connectivityStatus;
    Button btProcess;
    boolean providerEnabled;

    AlertDialog.Builder alertDialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_connection);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure,You wanted to make decision");



        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        connectivityStatus = new NetworkUtil().getConnectivityStatus(getApplicationContext());
        providerEnabled= locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);




        textViewNetwork = (TextView) findViewById(R.id.tvNetwork );
        textViewNetwork.setText(new NetworkUtil().getConnectivityStatusString(getApplicationContext()));

        tvGPs = (TextView) findViewById(R.id.tvGPs );




        final Button networkEnable = (Button) findViewById(R.id.btEnableNetwork);
        networkEnable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectivityStatus = new NetworkUtil().getConnectivityStatus(getApplicationContext());

                if(connectivityStatus == 0){
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.setClassName("com.android.phone","com.android.phone.NetworkSetting");
                    startActivity(intent);
                }



                textViewNetwork.setText(new NetworkUtil().getConnectivityStatusString(getApplicationContext()));

            }
        });

        final Button gpsEnable = (Button) findViewById(R.id.btEnableGps);
        gpsEnable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                providerEnabled= locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                if(!providerEnabled){
                    showGPSDisabledAlertToUser();
                    tvGPs.setText("GPS is Enabled");
                }

                locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                providerEnabled= locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                if(providerEnabled){
                   tvGPs.setText("GPS is Enabled");
                }else{
                    tvGPs.setText("GPS Not Available");
                }

            }
        });









        final Handler mHandler = new Handler();

         mUpdateUI = new Runnable() {
            public void run() {

                //displayData();
                Log.i(TAG, "run: ................");
                mHandler.postDelayed(mUpdateUI, 1000); // 1 second

                textViewNetwork.setText(new NetworkUtil().getConnectivityStatusString(getApplicationContext()));

                locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                providerEnabled= locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                if(providerEnabled){
                    tvGPs.setText("GPS is Enabled");
                }else{
                    tvGPs.setText("GPS Not Available");
                }

            }
        };


        mHandler.post(mUpdateUI);





/*

        final int intervalTime = 1000; // 10 sec
        Handler handler = new Handler();
        handler.postAtTime(new Runnable()  {
            @Override
            public void run() {
                int connectivityStatus = new NetworkUtil().getConnectivityStatus(getApplicationContext());
                boolean providerEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                Log.i(TAG, "run: ....................");
                if(connectivityStatus != 0){
                    isNetwork = true;
                    textView.setText("Network is Enable");
                }

                if(providerEnabled){
                    isGPS = true;
                    tvGPs.setText("GPS is Enable");
                }





                //startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));

            }
        }, intervalTime);

*/




        btProcess = (Button) findViewById(R.id.btProcess);
        btProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                boolean reg = preferences.getBoolean("REG", false); //if false not reg

                if(tvGPs.getText().toString().equals("GPS is Enabled") && !textViewNetwork.getText().toString().equals("Not connected to Internet")){


                    if(!reg){
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("REG",true);
                        editor.commit();




                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                    }else{

                    }
                }else{

                    alertDialogBuilder.setMessage("Please Enable All Settings");
                    alertDialogBuilder.setNegativeButton("close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();

                    alertDialog.show();

                }


            }
        });




    }

    private void showGPSDisabledAlertToUser(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Goto Settings Page To Enable GPS",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

}
