package com.bankfinder.chathurangasandun.boatlocator;

import android.app.ProgressDialog;
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
    EditText etOwnerName;
    Button btSearchOwner;
    TextView tvImei,tvSim,tvProvider,tvMobile,tvimeminuber;

    RequestQueue queue ;

    private ProgressDialog pDialog;








    String ownerID;

    String ownerid;
    JSONArray ownerBoats;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_device);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        queue= Volley.newRequestQueue(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

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
        tvImei.setText(util.getDevicekey());
        tvimeminuber.setText(" "+util.getDevicekey());

        String[] simNumber = util.getSimNumber();

        tvSim.setText("  "+simNumber[1]);
        tvMobile.setText("- 0718256773");

        tvProvider.setText("- Mobitel");//TODO set provider




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
                        ownerid = "";
                        ownerBoats = null;
                    }else if(responses.equals("-1")){
                        //TODO :eeror messsage - there is empty feild
                        ownerid = "";
                        ownerBoats=null;
                    }else{
                        ownerid = responses;
                        ownerBoats = response.getJSONArray("boats");
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
