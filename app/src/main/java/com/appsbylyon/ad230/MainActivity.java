package com.appsbylyon.ad230;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;

import com.appsbylyon.ad230.cannon.CannonFragment;
import com.appsbylyon.ad230.doodlz.DoodlzFragment;
import com.appsbylyon.ad230.flag.FlagQuizFragment;
import com.appsbylyon.ad230.spoton.SpotOnFragment;
import com.appsbylyon.ad230.tipcalculator.TipCalculatorMainFrag;
import com.appsbylyon.ad230.twit.TwitMainFrag;


public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private static final String TAG = "AD230 Main Activity";

    private static final int TIP_CALC_POS = 0;
    private static final int TWIT_POS = 1;
    private static final int FLAG_QUIZ_POS = 2;
    private static final int CANNON_GAME_POS = 3;
    private static final int SPOT_ON_POS = 4;
    private static final int DOODLZ_POS = 5;

    private NavigationDrawerFragment mNavigationDrawerFragment;

    private Fragment currentFragment;

    private int currentPosition = 0;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);


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
        if (currentFragment != null) {
            fragmentManager.beginTransaction().detach(currentFragment).remove(currentFragment).commit();
            currentFragment = null;
        }
        switch (position)
        {
            case TIP_CALC_POS:
                currentFragment = new TipCalculatorMainFrag();
                getActionBar().setTitle(getString(R.string.title_section1));
                getActionBar().setIcon(R.drawable.tip_calc_icon);
                break;
            case TWIT_POS:
                currentFragment = new TwitMainFrag();
                getActionBar().setTitle(R.string.title_section2);
                getActionBar().setIcon(R.drawable.twitter_icon);
                break;
            case FLAG_QUIZ_POS:
                currentFragment = new FlagQuizFragment();
                getActionBar().setTitle(getString(R.string.title_section3));
                getActionBar().setIcon(R.drawable.flag_icon);
                break;
            case CANNON_GAME_POS:
                currentFragment = new CannonFragment();
                getActionBar().setTitle(getString(R.string.title_section4));
                getActionBar().setIcon(R.drawable.cannon_icon);
                break;
            case SPOT_ON_POS:
                currentFragment = new SpotOnFragment();
                getActionBar().setTitle(getString(R.string.title_section5));
                getActionBar().setIcon(R.drawable.green_spot);
                break;
            case DOODLZ_POS:
                currentFragment = new DoodlzFragment();
                getActionBar().setTitle(getString(R.string.title_section6));
                getActionBar().setIcon(R.drawable.doodlz_icon);
                break;

        }
        fragmentManager.beginTransaction().add(R.id.container, currentFragment).commit();

    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            switch (currentPosition)
            {
                case FLAG_QUIZ_POS:
                    menu = ((FlagQuizFragment) currentFragment).formatMenu(menu);
                    break;
                case DOODLZ_POS:
                    menu = ((DoodlzFragment) currentFragment).formatMenu(menu);
                    break;
            }

            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (currentPosition)
        {
            case FLAG_QUIZ_POS:
                ((FlagQuizFragment) currentFragment).handleMenuSelection(item);
                break;
            case (DOODLZ_POS):
                ((DoodlzFragment) currentFragment).handleMenuSelect(item);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
