package com.bankfinder.chathurangasandun.boatlocator;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bankfinder.chathurangasandun.boatlocator.server.ServerConstrants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class NowWeatherFragment extends Fragment {

     private String weatherURL = "http://api.openweathermap.org/data/2.5/weather?APPID=0892ecb9eead45bd15be32305eb9051b&units=metric";



    View   v;
    private ProgressDialog pDialog;;
    private String jsonResponse;

    private TextView tvPresure;
    private TextView tvHumm;

    private TextView tvMinT;
    private TextView tvMaxT;
    private TextView tvWind;
    private TextView tvDirection;

    private TextView tvRain;

    private TextView tvCloude;

    private TextView tvMainResult;
    private TextView tvMainTemp;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2016-08-30 08:55:51 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {

    }


    public NowWeatherFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_now_weather, container, false);
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

        requestWeatherDetails();

        tvPresure = (TextView)v.findViewById( R.id.tvPresure );
        tvHumm = (TextView)v.findViewById( R.id.tvHumm );

        tvMinT = (TextView)v.findViewById( R.id.tvMinT );
        tvMaxT = (TextView)v.findViewById( R.id.tvMaxT );
        tvWind = (TextView)v.findViewById( R.id.tvWind );
        tvDirection = (TextView)v.findViewById( R.id.tvDirection );

        tvRain = (TextView)v.findViewById( R.id.tvRain );

        tvCloude = (TextView)v.findViewById( R.id.tvCloude );

        tvMainResult = (TextView)v.findViewById( R.id.tvMainResult );

        tvMainTemp = (TextView)v.findViewById( R.id.tvMainTemp);


        return v;
    }

    private void requestWeatherDetails() {

        double lat = OrginalMapFragment.staticLat;
        double lng = OrginalMapFragment.staticLong;


        weatherURL = weatherURL+"&lat="+lat+"&lon="+lng;

        showpDialog();
        RequestQueue queue = Volley.newRequestQueue(getActivity());

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, weatherURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.i("weatherdata-response", response);




                        try {


                            JSONObject jsonObject = new JSONObject(response);


                            JSONArray mianWeather =  jsonObject.getJSONArray("weather");

                            JSONObject watherJsonMain = mianWeather.getJSONObject(0);



                            tvMainResult.setText(watherJsonMain.get("description").toString());




                            JSONObject weatherObject =  jsonObject.getJSONObject("main");
                            Log.i("weatherdata", "onResponse: " +jsonObject.getString("cod"));


                            tvMainTemp.setText(weatherObject.get("temp").toString() +" C");
                            tvPresure.setText(weatherObject.get("pressure").toString()+" hPa");
                            tvHumm.setText(weatherObject.get("humidity").toString()+" %");
                            tvMinT.setText(weatherObject.get("temp_min").toString()+" C");
                            tvMaxT.setText(weatherObject.get("temp_max").toString()+" C");


                            JSONObject windJson = jsonObject.getJSONObject("wind");


                            tvWind.setText(windJson.get("speed").toString()+" Km/h");
                            tvDirection.setText(windJson.get("deg").toString());


                            tvRain.setText(jsonObject.getJSONObject("rain").get("3h").toString());
                            tvCloude.setText(jsonObject.getJSONObject("cloude").get("all").toString());




                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        hidepDialog();


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("weatherdata", "onErrorResponse: "+error);
                hidepDialog();
            }
        });

        Log.e("weatherdata", "requestWeatherDetails: " +stringRequest.getUrl());



// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }


    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }



}
