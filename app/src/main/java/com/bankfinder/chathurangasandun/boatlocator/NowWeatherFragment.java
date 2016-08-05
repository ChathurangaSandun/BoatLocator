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


        return v;
    }

    private void requestWeatherDetails() {


        weatherURL = weatherURL+"&lat=9.32983136&lon=79.77310181";

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
                            JSONObject coord =  jsonObject.getJSONObject("coord");
                            Log.i("weatherdata", "onResponse: " +jsonObject.getString("cod"));




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
