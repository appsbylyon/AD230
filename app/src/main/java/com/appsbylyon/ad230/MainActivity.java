package com.appsbylyon.ad230;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;

import com.appsbylyon.ad230.tipcalculator.TipCalculatorMainFrag;
import com.appsbylyon.ad230.twit.TwitMainFrag;


public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private NavigationDrawerFragment mNavigationDrawerFragment;

    private TipCalculatorMainFrag tipCalc;

    private TwitMainFrag twit;

    private int currentPosition = 0;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        currentPosition = position;
        FragmentManager fragmentManager = getFragmentManager();
        switch (position)
        {
            case 0:
                tipCalc = new TipCalculatorMainFrag();
                getActionBar().setTitle(getString(R.string.title_section1));
                fragmentManager.beginTransaction().replace(R.id.container, tipCalc).commit();
                break;
            case 1:
                twit = new TwitMainFrag();
                getActionBar().setTitle(R.string.title_section2);
                fragmentManager.beginTransaction().replace(R.id.container, twit).commit();
                break;

        }

    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            //TODO ADD CODE TO SHOW MENU OPTION FOR SPECIFIC FRAGMENTS
            /**

            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
             *
             */
            //restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



}
