package com.bankfinder.chathurangasandun.boatlocator;

import android.app.ProgressDialog;
import android.net.Uri;
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
import android.widget.TextView;
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
import com.bankfinder.chathurangasandun.boatlocator.parse.DeviceUtil;
import com.bankfinder.chathurangasandun.boatlocator.server.ServerConstrants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;





public class RegisterDevice extends AppCompatActivity {


    private static final String TAG = "RegisterDevicd" ;
    EditText etOwnerName,etSearchBoatID;
    Button btSearchOwner,btSelectOwner,btCancelOwner,btSelectBoat,btCancelBoat;
    TextView tvImei,tvSim,tvProvider,tvMobile,tvimeminuber;

    RequestQueue queue ;

    private ProgressDialog pDialog;



    boolean isOwnerSelect = false;
    boolean isBoatSelect = false;




    String boatid="";
    String selectedBoat="";

    String ownerid = "";
    JSONArray ownerBoats = null;

    String responseReg;

    String imei, sim,mobile,provider;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_device);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        queue= Volley.newRequestQueue(this);


        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

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

                if(!ownerName.isEmpty()){
                    checkOwnerName(ownerName);
                }else{
                    //TODO: create error message empty message

                }



            }
        });


        tvImei =(TextView) findViewById(R.id.tvImei);
        tvSim =(TextView) findViewById(R.id.tvsim);
        tvProvider =(TextView) findViewById(R.id.tvProvider);
        tvMobile =(TextView) findViewById(R.id.tvMobile);
        tvimeminuber=(TextView) findViewById(R.id.tvimeiNumber);


        DeviceUtil util = new DeviceUtil(getApplicationContext());
        imei=util.getDevicekey();
        tvImei.setText(imei);
        tvimeminuber.setText(" "+imei);

        String[] simNumber = util.getSimNumber();
        sim = simNumber[1];
        mobile = "0718256773";
        provider="Mobitel";

        tvSim.setText("  "+sim);
        tvMobile.setText("- "+mobile);

        tvProvider.setText("- "+provider);//TODO set provider



        btSelectOwner = (Button) findViewById(R.id.btSelectOwner);
        btSelectOwner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!ownerid.equals("")){
                    etOwnerName.setEnabled(false);
                    isOwnerSelect = true;

                }
            }
        });

        btCancelOwner= (Button) findViewById(R.id.btCancelOwner);
        btCancelOwner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isOwnerSelect){
                    etOwnerName.setEnabled(true);
                    isOwnerSelect = false;
                    ownerid = "";
                    ownerBoats=null;
                    ((TextView)findViewById(R.id.textView6ss)).setText("Select The Owner");

                }

            }
        });



        etSearchBoatID = (EditText) findViewById(R.id.etSearchBoatID);
        btSelectBoat= (Button) findViewById(R.id.btSelectBoat);
        btSelectBoat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boatid = etSearchBoatID.getText().toString();
                if(!boatid.isEmpty()){

                    if(ownerBoats != null){
                        for(int i = 0;i<ownerBoats.length();i++){
                            try {
                                if(boatid.equals(ownerBoats.get(i))){
                                    selectedBoat = boatid;
                                    etSearchBoatID.setEnabled(false);
                                    isBoatSelect= true;

                                    //textViewdd6dss
                                    ((TextView)findViewById(R.id.textViewdd6dss)).setText(selectedBoat);

                                    break;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }


                    }else{
                        //TODO enter correct boatid
                    }

                }else{
                    //TODO error empty message
                }



            }
        });


        btCancelBoat= (Button) findViewById(R.id.btCancelBoat);
        btCancelBoat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etSearchBoatID.setEnabled(true);
                etSearchBoatID.setText("");
                selectedBoat = "";
                isBoatSelect = false;
                ((TextView)findViewById(R.id.textViewdd6dss)).setText("Enter Boat Detail");
            }
        });




        //fab

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!selectedBoat.equals("") && !ownerid.equals("")){
                    String responseRegData = registerDeviceData();



                }else{
                    //TODO error not selected wanted fields
                }
            }
        });


    }

    private String registerDeviceData() {


        String url = ServerConstrants.SERVEWR_URL+"onnections/owner/RegisterDevice.php";


// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.i(TAG, response);
                        responseReg =response;




                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "onErrorResponse: "+error);
                responseReg=error.getMessage();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("ownerid", ownerid);
                params.put("boatid", boatid);
                params.put("deviceid", imei);
                params.put("provider", provider);
                params.put("sim", sim);
                params.put("mobile", mobile);

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

        return responseReg;

    }

    private String getURL() {
        Uri build = new Uri.Builder()

                .path(ServerConstrants.SERVEWR_URL+"connections/owner/RegisterDevice.php")
                .appendQueryParameter("param1", "name")
                .appendQueryParameter("param2", "age")
                .build();

        return  build.toString();
    }


    private void checkOwnerName(final String ownerName) {


        showpDialog();

        final String searchownerURL = ServerConstrants.SERVEWR_URL+"connections/owner/SearchOwnerName.php?ownername="+ownerName;
        Log.i(TAG, "checkOwnerName: "+searchownerURL);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                searchownerURL, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                try {

                    String responses = response.getString("ownerid");

                    if(responses.equals("0")){
                        //TODO :eeror messsage - there is no owner that name
                        Toast.makeText(getApplicationContext(),"there is no owner that name",Toast.LENGTH_LONG);
                        ownerid = "";
                        ownerBoats = null;
                    }else if(responses.equals("-1")){
                        //TODO :eeror messsage - there is empty feild
                        ownerid = "";
                        ownerBoats=null;
                    }else{
                        ownerid = responses;
                        ownerBoats = response.getJSONArray("boats");
                        isOwnerSelect = true;
                        etOwnerName.setEnabled(false);
                        ((TextView)findViewById(R.id.textView6ss)).setText(etOwnerName.getText());
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    ownerid = "";
                    ownerBoats=null;
                }
                hidepDialog();


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                // hide the progress dialog
                ownerid = "";
                ownerBoats=null;
                hidepDialog();

            }
        });


        queue.add(jsonObjReq);




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
