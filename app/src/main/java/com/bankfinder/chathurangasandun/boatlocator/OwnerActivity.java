package com.bankfinder.chathurangasandun.boatlocator;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bankfinder.chathurangasandun.boatlocator.parse.DeviceUtil;
import com.bankfinder.chathurangasandun.boatlocator.parse.ParseUtil;
import com.bankfinder.chathurangasandun.boatlocator.server.ServerConstrants;
import com.parse.Parse;
import com.parse.ParseUser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class OwnerActivity extends AppCompatActivity  implements   AddFisherManDialog.OnCompleteListener{

    ImageView imageView;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView recyclerView;

    TextView tvBoatID,tvOwnerName;
    String responseInsertJourny ;
    String fishermanadded = "";
    RequestQueue requestQueue;

    Button btStart;

    private ProgressDialog pDialog;

    static  String journyID;

    static String ownerboatid;


    private final  String TAG = "OwnerActivity";

    AlertDialog.Builder alertDialogBuilder,alertDialogBuilder2;

    ArrayList<String> fishermanList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        //alertbox
        alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure,You wanted to make decision");
        alertDialogBuilder2 = new AlertDialog.Builder(this);

        alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                Toast.makeText(OwnerActivity.this,"You clicked yes button",Toast.LENGTH_LONG).show();
            }
        });

        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });


        requestQueue = Volley.newRequestQueue(this);

//////////////////////////////////////////////////////////////////////////

        SharedPreferences prefs = getSharedPreferences("DEVICE", MODE_PRIVATE);
        String boatID = prefs.getString("BoatId","");
        String name =  prefs.getString("Name","");

        ownerboatid = boatID;




        tvBoatID = (TextView) findViewById(R.id.tvBoatID);
        tvBoatID.setText(boatID);
        tvOwnerName = (TextView) findViewById(R.id.tvOwnerName);
        tvOwnerName.setText(name);



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AddFisherManDialog addFisherManDialog = new AddFisherManDialog();
                addFisherManDialog.show(getSupportFragmentManager(),"");

            }
        });

        //add fiherman
        fishermanList.add(0,"tharuka");
        fishermanList.add(1,"Kumara");
        fishermanList.add(2,"pramoj");
        fishermanList.add(3,"nadun");
        fishermanList.add(4,"Ukku");


        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(),R.drawable.boatowner);
        Bitmap circularBitmap = ImageConverter.getRoundedCornerBitmap(bitmap, 180);

        ImageView circularImageView = (ImageView)findViewById(R.id.circleView);
        circularImageView.setImageBitmap(circularBitmap);


        recyclerView = (RecyclerView) findViewById(R.id.owner_recycle);
        mAdapter = new OwnerAdapter (fishermanList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);





        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Log.d("FisjerList",fishermanList.get(position));
                Toast.makeText(getApplicationContext(), fishermanList.get(position), Toast.LENGTH_SHORT).show();

                //signInToParse();
                DeviceUtil util =  new DeviceUtil(getApplicationContext());
                String devicekey = util.getDevicekey();


                selectFisherman(fishermanList.get(position));



                Log.i(TAG, devicekey);





            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        /*btStart = (Button) findViewById(R.id.btStart);

        btStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!fishermanadded.isEmpty()){
                    //insertJourny(fishermanadded);

                    startActivity(new Intent(getApplicationContext(),MainActivity.class));

                }else{
                    alertDialogBuilder.setMessage("Please select a fisherman");
                    alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();

                    alertDialog.show();
                }
            }
        });*/

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);


    }

    private void selectFisherman(final String name) {

        alertDialogBuilder2.setMessage("Are your your name is "+ name);
        alertDialogBuilder2.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                fishermanadded = name;

                startActivity(new Intent(getApplication(),MainActivity.class));



            }
        });

        alertDialogBuilder2.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog2= alertDialogBuilder2.create();

        alertDialog2.show();
    }

    private String getURL() {
        Uri build = new Uri.Builder()

                .path(ServerConstrants.SERVEWR_URL+"connections/owner/RegisterDevice.php")
                .appendQueryParameter("param1", "name")
                .appendQueryParameter("param2", "age")
                .build();

        return  build.toString();
    }

    @Override
    public void onComplete(String name) {

        Log.i("OwnerActivity", name);
        fishermanList.add(fishermanList.size(),name);
        mAdapter.notifyDataSetChanged();

    }

    private static class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private String[] mDataset;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public static class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public TextView mTextView;
            public ViewHolder(TextView v) {
                super(v);
                mTextView = v;
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public MyAdapter(String[] myDataset) {
            mDataset = myDataset;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.my_text_view, parent, false);
            // set the view's size, margins, paddings and layout parameters

            Bitmap bitmap = BitmapFactory.decodeResource(parent.getResources(),R.drawable.ic_account_circle_black_48dp);
            Bitmap circularBitmap = ImageConverter.getRoundedCornerBitmap(bitmap, 180);


            ViewHolder vh = new ViewHolder((TextView) v.findViewById(R.id.tvOwnerName));

            ImageView circularImageView = (ImageView)v.findViewById(R.id.ivfishermanFace);
            circularImageView.setImageBitmap(circularBitmap);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            holder.mTextView.setText(mDataset[position]);

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.length;
        }
    }


    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }




    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private  OwnerActivity.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final OwnerActivity.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }





    public class SignInAsynTask extends AsyncTask<Void,Void,Boolean> {

        String TAG = "AsyncInitSetup";
        private Context ctx;
        public SignInAsynTask(Context ctx){
            this.ctx = ctx;
        }
        @Override
        protected Boolean doInBackground(Void... params) {

            try{

                Parse.initialize(ctx, "2sxxue00oXxn05OTEw0JosIdIIq8XuUTzx4v7E3P", "yj0BVERpwbMHFhE1rzc995RalyjMX12tx6vIUyhH");

                DeviceUtil util =  new DeviceUtil(ctx);
                String devicekey = util.getDevicekey();
                String gmailAccount = util.getGmailAccount();

                ParseUser parseUser = new ParseUtil().userSignIn(gmailAccount, devicekey);

                if(parseUser != null){
                    Log.i(TAG, parseUser.getEmail());
                    return  true;
                }else{
                    return false;
                }

            }
            catch (Exception e){
                return false;
            }

        }


        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(true){

                Log.i(TAG, "onPostExecute: ddddddddddddddddddddddddddddddddddddddd");
                //Intent i = new Intent(getApplicationContext(),MainActivity.class);
                //startActivity(i);


            }else{
                Toast.makeText(getApplicationContext(),"Cannot Log",Toast.LENGTH_LONG);
            }
        }
    }




/*

    private void insertJourny(final String fisherman) {
        showpDialog();

        String url = ServerConstrants.SERVEWR_URL+"connections/journy/InsertJourny.php";
        Log.i(TAG, "insertJourny: "+url);


// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.i(TAG, response);
                        responseInsertJourny =response;
                        Log.i(TAG, "onResponse: "+responseInsertJourny);

                        if(responseInsertJourny.equals("1")){
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));

                        }else{
                            //success


                            alertDialogBuilder.setMessage("Some Problem - ");
                            alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });

                            alertDialogBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });

                            AlertDialog alertDialog = alertDialogBuilder.create();

                            alertDialog.show();


                        }


                        hidepDialog();

                    }


                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "onErrorResponse: "+error);
                responseInsertJourny=error.getMessage();
                hidepDialog();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                DeviceUtil util   = new DeviceUtil(getApplicationContext());
                String[] dateAndTime = util.getDateAndTime();
                String startdate = dateAndTime[0] ;
                String starttime = dateAndTime[1] ;
                String journyid= tvBoatID.getText().toString()+"_"+startdate+"_"+starttime;
                journyid = journyid.substring(0,journyid.length()-3);

                journyID = journyid;




                Map<String, String> params = new HashMap<String, String>();
                params.put("journyid", journyid);
                params.put("startdate", startdate);
                params.put("starttime", starttime);
                params.put("startlat", "");
                params.put("startlong", "");
                params.put("enddate", "");
                params.put("endtime", "");
                params.put("endlat", "");
                params.put("endlong", "");
                params.put("fisherman",fisherman );
                params.put("boatnumber", tvBoatID.getText().toString());


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
        requestQueue.add(stringRequest);



    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }




*/


}
