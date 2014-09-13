package com.appsbylyon.ad230.twit;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebViewFragment;

/**
 * Created by infinite on 8/17/2014.
 */
public class TwitWebView extends WebViewFragment
{
    private static final String TAG  = "Twit Web View";

    private ProgressDialog loading;

    private String currentUrl = "https://mobile.twitter.com";

    private SharedPreferences prefs;

    private boolean isActive = false;


    @Override
    public void onAttach (Activity activity)
    {
        super.onAttach(activity);
        loading = new ProgressDialog(activity);
    }

    @Override
    public void onDetach(){
        super.onDetach();
        loading = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e(TAG, "Web View On Start Called");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e(TAG, "Web View On Stop Called");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "Web View On Pause Called");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "Web View on Resume Called");
        this.setActive(true);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        prefs = getActivity().getSharedPreferences("twit_last_location", Context.MODE_PRIVATE);
        currentUrl = prefs.getString(TAG, "https://mobile.twitter.com");

        loading.setIndeterminate(true);
        loading.setTitle("Twitter Search");
        loading.setMessage("Loading, Please Wait...");
        WebView view = getWebView();
        view.setPadding(0,0,0,0);

        view.getSettings().setLoadWithOverviewMode(true);
        view.getSettings().setJavaScriptEnabled(true);
        view.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        getWebView().setWebViewClient(new WebViewClient()
        {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                Log.i(TAG, "Should override url loading called");
                //loading.show();
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url)
            {
                Log.i(TAG, "Page Finished Called");
                if (loading.isShowing())
                {
                    loading.dismiss();
                }
            }
        });


        loading.show();
        view.loadUrl(currentUrl);
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public void setUrl(String url)
    {
        if (isActive)
        {
            prefs.edit().putString(TAG, url).apply();
            loading.show();
            getWebView().loadUrl(url);
        }
    }


}
