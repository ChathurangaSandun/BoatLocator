package com.bankfinder.chathurangasandun.boatlocator;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
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

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OrginalMapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OrginalMapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrginalMapFragment extends Fragment implements OnMapReadyCallback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


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


        mapView = (MapView) v.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);




        if(downloadvalue.getInt("VALUE",0) != 100){
            downloadRegion();
        }else{
            Log.d(TAG, "completed");



        }




        return v;



    }

    @Override
    public void onMapReady(MapboxMap mb) {
        mapboxMap = mb;
        mapboxMap.setMyLocationEnabled(true);
        mapboxMap.setOnMyLocationChangeListener(myLocationChangeListener);







    }
    private MapboxMap.OnMyLocationChangeListener myLocationChangeListener;

    {
        myLocationChangeListener = new MapboxMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                double myLocationLat = location.getLatitude();
                double myLocationLong = location.getLongitude();


                IconFactory iconFactory = IconFactory.getInstance(getActivity());
                Drawable iconDrawable = ContextCompat.getDrawable(getActivity(), R.drawable.dott); //http://www.flaticon.com/free-icon/sailboat_116500
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
                            .icon(icon1));
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
                    //mapboxMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 12.0f));
                }

                //nearestBranchLocation = getNearestBranch();
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


}
