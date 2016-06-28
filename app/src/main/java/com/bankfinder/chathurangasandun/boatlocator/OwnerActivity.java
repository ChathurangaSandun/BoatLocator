package com.bankfinder.chathurangasandun.boatlocator;


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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bankfinder.chathurangasandun.boatlocator.parse.DeviceUtil;
import com.bankfinder.chathurangasandun.boatlocator.parse.ParseUtil;
import com.bankfinder.chathurangasandun.boatlocator.server.ServerConstrants;
import com.parse.Parse;
import com.parse.ParseUser;

import java.util.ArrayList;


public class OwnerActivity extends AppCompatActivity  implements   AddFisherManDialog.OnCompleteListener{

    ImageView imageView;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView recyclerView;

    TextView tvBoatID,tvOwnerName;



    private final  String TAG = "OwnerActivity";

    AlertDialog.Builder alertDialogBuilder;

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

//////////////////////////////////////////////////////////////////////////

        SharedPreferences prefs = getSharedPreferences("DEVICE", MODE_PRIVATE);
        String boatID = prefs.getString("BoatId","");
        String name =  prefs.getString("Name","");




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
        fishermanList.add(0,"siripala");



        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(),R.drawable.background);
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


                insertJourny(fishermanList.get(position));



                Log.i(TAG, devicekey);





            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


    }

    private void insertJourny(String name) {

        alertDialogBuilder.setMessage("Are your your name is "+ name);
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {





                startActivity(new Intent(getApplication(),MainActivity.class));


                finish();
            }
        });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }

    private String getURL() {
        Uri build = new Uri.Builder()

                .path(ServerConstrants.SERVEWR_URL+"connections/owner/RegisterDevice.php")
                .appendQueryParameter("param1", "name")
                .appendQueryParameter("param2", "age")
                .build();

        return  build.toString();
    }

/*
    private void checkOwnerName(final String fisherman) {


        showpDialog();

        final String searchownerURL = ServerConstrants.SERVEWR_URL+"connections/journy/InsertJourny.php?fisherman="+fisherman;
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
    }*/









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

            ViewHolder vh = new ViewHolder((TextView) v.findViewById(R.id.tvOwnerName));
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










}
