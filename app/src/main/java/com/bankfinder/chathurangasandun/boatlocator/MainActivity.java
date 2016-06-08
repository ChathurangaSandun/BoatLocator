package com.bankfinder.chathurangasandun.boatlocator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */


    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager viewPager;

    String beforePauseLanguage = LanguageSelector.getCurrentLangnuage() ;
    private TabLayout tabLayout;
    private final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("AA", "start");

        /*
        ParseObject locationobject = new ParseObject("location");
        locationobject.put("boatid","1001");
        locationobject.put("time","12.36");
        locationobject.put("date","2015-5-29");
        locationobject.put("lat",5);
        locationobject.put("long",4);
        locationobject.put("batry",25);
        locationobject.put("passbool",false);

        try {
            locationobject.save();
        } catch (ParseException e
            e.printStackTrace();
        }*/

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


        viewPager = (ViewPager) findViewById(R.id.veiwPager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

        //start....
        Log.i("MainActicvtiy", "start.................................");
        Intent i = new Intent(this,LocationService.class);
        startService(i);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this,SettingsActivity.class));

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();

        //get current language
        LanguageSelector.setCurrentLangnuage(getApplicationContext());
        String currentLangnuage = LanguageSelector.getCurrentLangnuage();
        if(!beforePauseLanguage.equals(currentLangnuage)) {
            Log.d("main", "i'm changed ");

            if("en".equals(currentLangnuage)){
                setTitle("Home");
            }else if("sl".equals(currentLangnuage)){
                setTitle("ගෘහය");
            }else{
                setTitle("வீட்டில்");
            }
        }else{
            Log.d("main", "i'm not changed");
        }



    }

    @Override
    protected void onPause() {
        super.onPause();
        beforePauseLanguage =  LanguageSelector.getCurrentLangnuage();
        Log.d("main","i'm pause");
    }


    //for custom tabs+================================================================================
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new OrginalMapFragment(), "ONE");
        adapter.addFrag(new WeatherMapFragment(), "TWO");
        viewPager.setAdapter(adapter);
    }


    private void setupTabIcons() {

        TextView tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabOne.setText(R.string.boat_tab_title);
        tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_directions_boat_white_24dp, 0, 0);
        tabLayout.getTabAt(0).setCustomView(tabOne);

        TextView tabTwo = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabTwo.setText(R.string.weathermap_tab_title);
        tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_brightness_7_white_24dp, 0, 0);
        tabLayout.getTabAt(1).setCustomView(tabTwo);

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


}
