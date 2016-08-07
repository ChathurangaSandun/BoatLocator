package com.bankfinder.chathurangasandun.boatlocator;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bankfinder.chathurangasandun.boatlocator.mypolygon.*;

import com.bankfinder.chathurangasandun.boatlocator.server.ServerConstrants;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.Polygon;
import com.mapbox.mapboxsdk.annotations.PolygonOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.offline.OfflineManager;
import com.mapbox.mapboxsdk.offline.OfflineRegion;
import com.mapbox.mapboxsdk.offline.OfflineRegionError;
import com.mapbox.mapboxsdk.offline.OfflineRegionStatus;
import com.mapbox.mapboxsdk.offline.OfflineTilePyramidRegionDefinition;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OrginalMapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OrginalMapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrginalMapFragment extends Fragment implements OnMapReadyCallback ,com.bankfinder.chathurangasandun.boatlocator.mypolygon.PolygonCodes{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;



    private OnFragmentInteractionListener mListener;

    RequestQueue requestQueue;


    private View v;



    private static final String TAG = "MainActivity";

    private boolean isEndNotified;
    private ProgressBar progressBar;
    private MapView mapView;

    // JSON encoding/decoding
    public final static String JSON_CHARSET = "UTF-8";
    public final static String JSON_FIELD_REGION_NAME = "FIELD_REGION_NAME";
    private MapboxMap mapboxMap;


    SharedPreferences downloadvalue;
    SharedPreferences.Editor edit;

    ArrayList<LatLng> path = new ArrayList<>();


    ArrayList<LatLng> polygonArrayList = new ArrayList<>();
    ArrayList<LatLng> polygonArrayList2 = new ArrayList<>();

    com.bankfinder.chathurangasandun.boatlocator.mypolygon.Polygon.Builder myouterboaderBuilder;
    com.bankfinder.chathurangasandun.boatlocator.mypolygon.Polygon myOuterBoaderPolygonFinal;



    com.bankfinder.chathurangasandun.boatlocator.mypolygon.Polygon.Builder srilankaboaderBuilder;
    com.bankfinder.chathurangasandun.boatlocator.mypolygon.Polygon srilankaBoaderPolygonFinal;

    Vibrator vibrator;
    Ringtone r;


    boolean isServiceOn =false;
    Intent serviceIntent ;

    String responseValue;

    TextView errorText;
    SlidingUpPanelLayout slidingLayout;


    static String error = "no";


    TextView tvSlideupdwn,backgroundText;



    Runnable mUpdateUI;


    public OrginalMapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OrginalMapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OrginalMapFragment newInstance(String param1, String param2) {
        OrginalMapFragment fragment = new OrginalMapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_orginal_map, container, false);


        downloadvalue = getContext().getSharedPreferences("DOWNLOADVALUE", getContext().MODE_PRIVATE);

        edit = downloadvalue.edit();
        edit.commit();

        Log.d(TAG+"value-1", downloadvalue.getInt("VALUE",0)+"");


        backgroundText = (TextView) v.findViewById(R.id.back);

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALL);
        r = RingtoneManager.getRingtone(getActivity(), notification);
        vibrator = (Vibrator) this.getActivity().getSystemService(Context.VIBRATOR_SERVICE);


        mapView = (MapView) v.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);




        if(downloadvalue.getInt("VALUE",0) != 100){
            downloadRegion();
        }else{
            Log.d(TAG, "completed");



        }



        serviceIntent = new Intent(getContext(),LocationService.class);
        requestQueue = Volley.newRequestQueue(getActivity());






        slidingLayout = (SlidingUpPanelLayout)v.findViewById(R.id.sliding_layout);
        slidingLayout.setPanelHeight(40);
        slidingLayout.setShadowHeight(10);
        slidingLayout.setPanelSlideListener(onSlideListener());
        tvSlideupdwn = (TextView) v.findViewById( R.id.tvSlideupdown);


        return v;
    }


    private SlidingUpPanelLayout.PanelSlideListener onSlideListener() {
        return new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View view, float v) {
                tvSlideupdwn.setText("Slide Up");
            }

            @Override
            public void onPanelCollapsed(View view) {
                tvSlideupdwn.setText("Slide Up");
            }

            @Override
            public void onPanelExpanded(View view) {
                tvSlideupdwn.setText("Slide Down");
            }

            @Override
            public void onPanelAnchored(View view) {

            }

            @Override
            public void onPanelHidden(View view) {

            }
        };
    }



    @Override
    public void onMapReady(MapboxMap mb) {
        mapboxMap = mb;
        mapboxMap.setMyLocationEnabled(true);
        mapboxMap.setOnMyLocationChangeListener(myLocationChangeListener);
        drawPolygon(mapboxMap);




    }
    private void drawPolygon(MapboxMap mapboxMap) {

        createPolygon();

        mapboxMap.addPolygon(new PolygonOptions()
                .addAll(polygonArrayList)
                .fillColor(Color.parseColor("#3bb2d0"))
                .alpha(0.0f)


        );
    }








    private MapboxMap.OnMyLocationChangeListener myLocationChangeListener;

    {
        myLocationChangeListener = new MapboxMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                double myLocationLat = location.getLatitude();
                double myLocationLong = location.getLongitude();


                IconFactory iconFactory = IconFactory.getInstance(getActivity());
                Drawable iconDrawable = ContextCompat.getDrawable(getContext(), R.drawable.dot); //http://www.flaticon.com/free-icon/sailboat_116500
                Icon icon = iconFactory.fromDrawable(iconDrawable);


                LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());


                //time

                Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR);
                int minutes = c.get(Calendar.MINUTE);
                int seconds = c.get(Calendar.SECOND);
                String time = ""+hour +" :  "+ minutes+" : " +seconds;


                Log.d(TAG, "seconds "+time);

                if (path.isEmpty()) { // for first lunch
                    path.add(loc);

                    Drawable iconDrawable1 = ContextCompat.getDrawable(getActivity(), R.drawable.my_boat); //http://www.flaticon.com/free-icon/sailboat_116500
                    Icon icon1 = iconFactory.fromDrawable(iconDrawable1);





                    mapboxMap.addMarker(new MarkerOptions()
                            .position(new LatLng(loc))
                            .title(time)
                            .snippet("")
                            .icon(icon));
                } else {
                    path.add(loc);
                    mapboxMap.addMarker(new MarkerOptions()
                            .position(new LatLng(loc))
                            .title(time)
                            .snippet("")
                            .icon(icon));
                }


                showMyPath();


                if (mapboxMap != null) {
                    mapboxMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 8.0f));
                }

/////////////////////////

                checkInsideTheBoarder((float)myLocationLat,(float)myLocationLong);

                checkInsideSrilanka((float)myLocationLat,(float)myLocationLong);


            }

            private void showMyPath() {

                if (path.size() > 0) {

                    for (LatLng l : path
                            ) {
                        Log.d(TAG, l.getLatitude() + "");
                    }

                    Log.d(TAG, "size()" + path.size());


                    LatLng[] pointsArray = path.toArray(new LatLng[path.size()]);


                    // Draw Points on MapView
                    mapboxMap.addPolyline(new PolylineOptions()
                            .add(pointsArray)

                            .color(Color.parseColor("#3bb2d0"))
                            .width(2));
                }


            }
        };







    }



    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

   /* @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
*/
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;

    }
//////////////////////////////////////////////////////////////////////////
    @Override
    public void createPolygon() {
        //PolygonCodes.outerBoaderLat;




        for(int i = 0;i<PolygonCodes.outerBoaderLat.length;i++){
            polygonArrayList.add(new LatLng(PolygonCodes.outerBoaderLat[i],PolygonCodes.outerBoaderLong[i]));

        }


        for(int i = 0;i<PolygonCodes.srilankaBoaderLat.length;i++){
            polygonArrayList2.add(new LatLng(PolygonCodes.srilankaBoaderLat[i],PolygonCodes.srilankaBoaderLong[i]));

        }
////////////////

        creatMyOuterBoaderPolygon();
        creatSriLankaBoaderPolygon();

/////////////////////////
    }




    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    // Progress bar methods
    private void startProgress() {

        // Start and show the progress bar
        isEndNotified = false;
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void setPercentage(final int percentage) {
        progressBar.setIndeterminate(false);
        progressBar.setProgress(percentage);
    }

    private void endProgress(final String message) {
        // Don't notify more than once
        if (isEndNotified) return;

        // Stop and hide the progress bar
        isEndNotified = true;
        progressBar.setIndeterminate(false);
        progressBar.setVisibility(View.GONE);

        // Show a toast
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }






    private void downloadRegion(){

        // Set up the OfflineManager
        OfflineManager offlineManager = OfflineManager.getInstance(getActivity());
        offlineManager.setAccessToken(getString(R.string.accessToken));

        // Create a bounding box for the offline region
        LatLngBounds latLngBounds = new LatLngBounds.Builder()
                .include(new LatLng(11.603215, 76.534143)) // Northeast
                .include(new LatLng(7.394992, 80.937680)) // Southwest
                .build();

        // Define the offline region
        OfflineTilePyramidRegionDefinition definition = new OfflineTilePyramidRegionDefinition(
                mapView.getStyleUrl(),
                latLngBounds,
                5,
                12,
                this.getResources().getDisplayMetrics().density);

        // Set the metadata
        byte[] metadata;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(JSON_FIELD_REGION_NAME, "1");
            String json = jsonObject.toString();
            metadata = json.getBytes(JSON_CHARSET);
        } catch (Exception e) {
            Log.e(TAG, "Failed to encode metadata: " + e.getMessage());
            metadata = null;
        }

        // Create the region asynchronously
        offlineManager.createOfflineRegion(definition, metadata, new OfflineManager.CreateOfflineRegionCallback() {
            @Override
            public void onCreate(OfflineRegion offlineRegion) {
                offlineRegion.setDownloadState(OfflineRegion.STATE_ACTIVE);

                // Display the download progress bar
                progressBar = (ProgressBar) v.findViewById(R.id.progress_bar);
                startProgress();

                // Monitor the download progress using setObserver
                offlineRegion.setObserver(new OfflineRegion.OfflineRegionObserver() {
                    @Override
                    public void onStatusChanged(OfflineRegionStatus status) {

                        // Calculate the download percentage and update the progress bar
                        double percentage = status.getRequiredResourceCount() >= 0 ?
                                (100.0 * status.getCompletedResourceCount() / status.getRequiredResourceCount()) :
                                0.0;

                        if (status.isComplete()) {
                            // Download complete
                            endProgress("Region downloaded successfully.");
                            edit.putInt("VALUE",100);
                            edit.commit();
                        } else if (status.isRequiredResourceCountPrecise()) {
                            // Switch to determinate state
                            setPercentage((int) Math.round(percentage));
                            edit.putInt("VALUE",(int) Math.round(percentage));
                            edit.commit();
                            Log.d(TAG+"value", downloadvalue.getInt("VALUE",0)+"");

                        }
                    }

                    @Override
                    public void onError(OfflineRegionError error) {
                        // If an error occurs, print to logcat
                        Log.e(TAG, "onError reason: " + error.getReason());
                        Log.e(TAG, "onError message: " + error.getMessage());
                    }

                    @Override
                    public void mapboxTileCountLimitExceeded(long limit) {
                        // Notify if offline region exceeds maximum tile count
                        Log.e(TAG, "Mapbox tile count limit exceeded: " + limit);
                    }
                });
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Error: " + error);
            }
        });
    }

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    private void creatMyOuterBoaderPolygon() {



        myouterboaderBuilder = com.bankfinder.chathurangasandun.boatlocator.mypolygon.Polygon.Builder();

        for (LatLng l:polygonArrayList
             ) {

            myouterboaderBuilder.addVertex(new Point((float)l.getLatitude(),(float)l.getLongitude()));

        }


        myOuterBoaderPolygonFinal = myouterboaderBuilder.build();
    }

    private void creatSriLankaBoaderPolygon() {



        srilankaboaderBuilder = com.bankfinder.chathurangasandun.boatlocator.mypolygon.Polygon.Builder();


        Log.i(TAG, "polygn number "+polygonArrayList2.size());

        for (LatLng l:polygonArrayList2
                ) {

            srilankaboaderBuilder.addVertex(new Point((float)l.getLatitude(),(float)l.getLongitude()));

        }


        srilankaBoaderPolygonFinal = srilankaboaderBuilder.build();
    }





    private void checkInsideTheBoarder(float latitude, float longitude) {
        if(myOuterBoaderPolygonFinal.contains(new Point(latitude,longitude))){
            error = "no";

            r.stop();
            vibrator.cancel();

            backgroundText.setVisibility(View.INVISIBLE);
            Log.i("Location polygon", "in");
        }else{
            error = "yes";
            backgroundText.setVisibility(View.VISIBLE);

            backgroundText.setBackgroundColor(getResources().getColor(R.color.trans));



            r.play();
            vibrator.vibrate(Integer.MAX_VALUE);
            Log.i("Location polygon", "out");
        }

    }

    static boolean previousLocationInSrilanka = true;

    private void checkInsideSrilanka(float latitude, float longitude) {

        String url = "";
        if(srilankaBoaderPolygonFinal.contains(new Point(latitude,longitude))){
            if(previousLocationInSrilanka){
                //no change

            }else{
                //stop the service




                getContext().stopService(serviceIntent);

                Log.i(TAG, "checkInsideSrilanka: stop service");
                Log.i("SERVICE", "Stop.................................");




  //              url = ServerConstrants.SERVEWR_URL+"connections/journy/UpdateEndPoint.php";
    //            updateStartLocationJourny(latitude,longitude,url);

            }

            previousLocationInSrilanka = true;

            Log.i(TAG, "checkInsideSrilanka: inthe srilanka");
        }else{
            if(previousLocationInSrilanka){
                //start the service
                //url = ServerConstrants.SERVEWR_URL+"connections/journy/UpdateStartPoint.php";
                LocationService.locationIntID =0;
                getContext().stopService(serviceIntent);
                getContext().startService(serviceIntent);
                Log.i(TAG, "checkInsideSrilanka: start service");
                Log.i("SERVICE", "start.................................");


                //updateStartLocationJourny(latitude,longitude,url);
            }else{
                // no change
            }

            previousLocationInSrilanka = false;

            Log.i(TAG, "checkInsideSrilanka:  out the srilanka");



        }

    }

    private void updateStartLocationJourny(final double lat, final double lng ,String url) {




        Log.i("startpoint", "insertJourny: "+url);


// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.i("startpoint", response);
                        responseValue =response;


                        if(responseValue.equals("1")){
                            Log.i("startlocation", "startlocation successfull");


                        }else{
                            //updateStartLocationJourny(lat, lng);

                            Log.i("startlocation", "startlocation error");
                        }




                    }


                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("responseValue", "onErrorResponse: "+error);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {


                String journyid = OwnerActivity.journyID;


                Log.i(TAG, "getParams: "+lat +" "+lng);

                Map<String, String> params = new HashMap<String, String>();
                params.put("journyid", journyid);
                params.put("startlat", String.valueOf(lat));
                params.put("startlong", String.valueOf(lng));
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


}
