package com.appsbylyon.ad230;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;

import com.appsbylyon.ad230.flag.FlagQuizFragment;
import com.appsbylyon.ad230.tipcalculator.TipCalculatorMainFrag;
import com.appsbylyon.ad230.twit.TwitMainFrag;


public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private static final int TIP_CALC_POS = 0;
    private static final int TWIT_POS = 1;
    private static final int FLAG_QUIZ_POS = 2;

    private NavigationDrawerFragment mNavigationDrawerFragment;

    private TipCalculatorMainFrag tipCalc;

    private TwitMainFrag twit;

    private FlagQuizFragment flagQuiz;

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
        switch (position)
        {
            case TIP_CALC_POS:
                tipCalc = new TipCalculatorMainFrag();
                getActionBar().setTitle(getString(R.string.title_section1));
                getActionBar().setIcon(R.drawable.tip_calc_icon);
                fragmentManager.beginTransaction().replace(R.id.container, tipCalc).commit();
                break;
            case TWIT_POS:
                twit = new TwitMainFrag();
                getActionBar().setTitle(R.string.title_section2);
                getActionBar().setIcon(R.drawable.twitter_icon);
                fragmentManager.beginTransaction().replace(R.id.container, twit).commit();
                break;
            case FLAG_QUIZ_POS:
                flagQuiz = new FlagQuizFragment();
                getActionBar().setTitle(getString(R.string.title_section3));
                getActionBar().setIcon(R.drawable.flag_icon);
                fragmentManager.beginTransaction().replace(R.id.container, flagQuiz).commit();
                break;


        }

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
                    menu = flagQuiz.formatMenu(menu);
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
                flagQuiz.handleMenuSelection(item);
                break;
        }
        return super.onOptionsItemSelected(item);
    }



}
