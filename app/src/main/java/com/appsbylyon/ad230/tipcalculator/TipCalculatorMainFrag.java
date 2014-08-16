package com.appsbylyon.ad230.tipcalculator;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.appsbylyon.ad230.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by infinite on 8/16/2014.
 */
public class TipCalculatorMainFrag extends Fragment
{
    private static final String TAG = "AD230 - Tip Calculator Main Frag";

    private TipCalculator tipCalc = new TipCalculator();

    private TextView descText;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, parent, savedInstanceState);
        WindowManager windowManager = (WindowManager) getActivity().getSystemService(Activity.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        int layoutId = 0;
        switch (display.getRotation())
        {
            case Surface.ROTATION_0:
                layoutId = R.layout.tip_calc_main_port;
                break;
            case Surface.ROTATION_90:
                layoutId = R.layout.tip_calc_main_land;
                break;
            case Surface.ROTATION_180:
                layoutId = R.layout.tip_calc_main_port;
                break;
            case Surface.ROTATION_270:
                layoutId = R.layout.tip_calc_main_land;
                break;
        }
        View view = inflater.inflate(layoutId, parent, false);

        descText = (TextView) view.findViewById(R.id.tip_calc_desc_text);

        try
        {
            InputStreamReader input = new InputStreamReader(getResources().openRawResource(R.raw.tip_cal_desc));
            BufferedReader br = new BufferedReader(input);
            String line;
            while ((line = br.readLine()) != null)
            {
                descText.append(line);
                descText.append("\n");
            }
            br.close();
        }
        catch (IOException IOE)
        {
            Log.e(TAG, "IOException Trying to load tip_cal_desc resource. "+IOE.getMessage());
        }

        getFragmentManager().beginTransaction().add(R.id.tip_calc_container2, tipCalc).commit();

        return view;
    }
}
