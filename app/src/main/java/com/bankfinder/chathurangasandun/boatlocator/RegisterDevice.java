package com.bankfinder.chathurangasandun.boatlocator;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bankfinder.chathurangasandun.boatlocator.server.ServerConstrants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterDevice extends AppCompatActivity {


    private static final String TAG = "RegisterDevicd" ;
    EditText etOwnerName;
    Button btSearchOwner;








    String ownerID;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_device);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        CardView cvOwner = (CardView) findViewById(R.id.cvOwner);
        cvOwner.setRadius(5);

        initComponents();


    }

    private void initComponents() {
        etOwnerName = (EditText) findViewById(R.id.etSearchOwner);
        btSearchOwner = (Button) findViewById(R.id.btSearchOwner);

        btSearchOwner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ownerName = etOwnerName.getText().toString();
                String isHaveOwner = checkOwnerName(ownerName);


            }
        });




    }

    private String checkOwnerName(final String ownerName) {

        RequestQueue queue = Volley.newRequestQueue(this);


        final String searchownerURL = ServerConstrants.SERVEWR_URL+"connections/owner/SearchOwnerName.php?ownername="+ownerName;
        Log.i(TAG, "checkOwnerName: "+searchownerURL);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                searchownerURL, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());




            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                // hide the progress dialog

            }
        });


        queue.add(jsonObjReq);









/*

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, searchownerURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.i(TAG, response);














                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "onErrorResponse: "+error);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("ownername", ownerName);
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
        queue.add(stringRequest);*/




        return "";
    }

}
