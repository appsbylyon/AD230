package com.appsbylyon.ad230.cannon;

import android.app.Fragment;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.appsbylyon.ad230.R;

/**
 * Created by infinite on 8/17/2014.
 */
public class CannonFragment extends Fragment
{
    private static final String TAG = "Cannon Frag";

    private GestureDetector gestureDetector;
    private CannonView cannonView;

    public void onDestroy()
    {
        super.onDestroy();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        cannonView.releaseResources();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, parent, savedInstanceState);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        View view = inflater.inflate(R.layout.cannon_main, parent, false);

        cannonView = (CannonView) view.findViewById(R.id.cannonView);
        cannonView.setOnTouchListener(touchListener);


        gestureDetector = new GestureDetector(getActivity().getApplicationContext(), gestureListener);

        getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
        return view;
    }

    @Override
    public void onPause()
    {
        super.onPause();
        cannonView.stopGame();
    }


    private View.OnTouchListener touchListener = new View.OnTouchListener()
    {


        @Override
        public boolean onTouch(View view, MotionEvent motionEvent)
        {
            Log.i(TAG, "onTouchEvent called");
            int action = motionEvent.getAction();
            if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE)
            {
                cannonView.alignCannon(motionEvent);
            }
            return gestureDetector.onTouchEvent(motionEvent);
        }
    };



    GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener()
    {
        @Override
        public boolean onDown(MotionEvent e)
        {
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e)
        {
            cannonView.fireCannonball(e);
            return true;
        }
    };
}
