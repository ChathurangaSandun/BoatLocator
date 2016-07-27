package com.bankfinder.chathurangasandun.boatlocator;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;

import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.android.gms.maps.model.UrlTileProvider;

import java.net.MalformedURLException;
import java.net.URL;


public class WeatherMapFragment extends Fragment implements OnMapReadyCallback {
    View veiw;

    private OrginalMapFragment.OnFragmentInteractionListener mListener;

    GoogleMap mMap;

    double myLocationLat, myLocationLong;
    Marker myMarker;

    String OWM_TILE_URL = "http://tile.openweathermap.org/map/%s/%d/%d/%d.png";
    String tileType = null;

    public WeatherMapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        veiw = inflater.inflate(R.layout.fragment_weather_map, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return veiw;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //mMap.setMyLocationEnabled(true);

        mMap.setOnMyLocationChangeListener(myLocationChangeListener);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLocationLat, myLocationLong), 12.0f));

        mMap.addTileOverlay(new TileOverlayOptions().tileProvider(this.createTilePovider()));

    }

    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            myLocationLat = location.getLatitude();
            myLocationLong = location.getLongitude();

            if(myMarker != null){
                myMarker.remove();
            }

            LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
            myMarker = mMap.addMarker(new MarkerOptions()
                    .position(loc)
                    .snippet(
                            "Lat:" + location.getLatitude() + "Lng:"
                                    + location.getLongitude())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.dot))
                    .title("My Locaiton"));
            if(mMap != null){
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 12.0f));
            }

            //nearestBranchLocation = getNearestBranch();
        }
    };



    private TileProvider createTilePovider() {
        TileProvider tileProvider = new UrlTileProvider(256, 256) {
            @Override
            public URL getTileUrl(int x, int y, int zoom) {
                String fUrl = String.format(OWM_TILE_URL, tileType == null ? "wind" : tileType, zoom, x, y);
                URL url = null;
                try {
                    url = new URL(fUrl);
                } catch (MalformedURLException mfe) {
                    mfe.printStackTrace();
                }

                return url;
            }
        };

        return  tileProvider;
    }
}
