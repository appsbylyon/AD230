package com.appsbylyon.ad230.spoton;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.appsbylyon.ad230.R;

/**
 * Created by infinite on 8/17/2014.
 */
public class SpotOnFragment extends Fragment
{
    private SpotOnView spotView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, parent, savedInstanceState);
        View view = inflater.inflate(R.layout.spoton_main, parent, false);

        RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.spot_on_relativeLayout);
        spotView = new SpotOnView(getActivity(), getActivity().getPreferences(Context.MODE_PRIVATE),
                layout);
        layout.addView(spotView, 0);

        return view;
    }

    @Override
    public void onPause()
    {
        super.onPause();
        spotView.pause();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        spotView.resume(getActivity());
    }
}
