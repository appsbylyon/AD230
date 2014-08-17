package com.appsbylyon.ad230.twit;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.appsbylyon.ad230.R;

/**
 * Created by infinite on 8/17/2014.
 */
public class TwitMainFrag extends Fragment implements TwitterSearch.TwitterSearchListener
{
    private static final String TAG = "Twitter Main Frag";

    private TwitterSearch twitSearch = new TwitterSearch();

    private TwitWebView twitWebView = new TwitWebView();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, parent, savedInstanceState);
        twitSearch.setActivity(this);
        WindowManager windowManager = (WindowManager) getActivity().getSystemService(Activity.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        int layoutId = 0;
        switch (display.getRotation())
        {
            case Surface.ROTATION_0:
                layoutId = R.layout.twit_main_port;
                break;
            case Surface.ROTATION_90:
                layoutId = R.layout.twit_main_land;
                break;
            case Surface.ROTATION_180:
                layoutId = R.layout.twit_main_port;
                break;
            case Surface.ROTATION_270:
                layoutId = R.layout.twit_main_land;
                break;
        }
        View view = inflater.inflate(layoutId, parent, false);

        getFragmentManager().beginTransaction().add(R.id.twit_containter1, twitSearch)
                .add(R.id.twit_containter2, twitWebView).commit();

        return view;
    }

    @Override
    public void doSearch(String url)
    {
        twitWebView.setUrl(url);
    }
}
