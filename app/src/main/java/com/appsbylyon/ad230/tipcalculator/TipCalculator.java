package com.appsbylyon.ad230.tipcalculator;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.appsbylyon.ad230.R;

/**
 * Created by infinite on 8/16/2014.
 */
public class TipCalculator extends Fragment
{
    private static final String BILL_TOTAL = "BILL_TOTAL";
    private static final String CUSTOM_PERCENT = "CUSTOM_PERCENT";
    private static final String CURRENT_BILL = "current_bill";

    private double currentBillTotal;

    private int currentCustomPercent;

    private EditText tip10EditText;
    private EditText total10EditText;
    private EditText tip15EditText;
    private EditText total15EditText;
    private EditText tip20EditText;
    private EditText total20EditText;
    private EditText billEditText;
    private EditText tipCustomEditText;
    private EditText totalCustomEditText;

    private TextView customTipTextView;

    private SeekBar customSeekBar;

    private SharedPreferences sharedPrefs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, parent, savedInstanceState);
        View view = inflater.inflate(R.layout.tip_calc_calc, parent, false);

        if (savedInstanceState == null)
        {
            currentBillTotal = 0.0;
            currentCustomPercent = 18;
            Log.i("Tip Calc", "Bundle is NULL");
        }
        else
        {
            currentBillTotal = savedInstanceState.getDouble(BILL_TOTAL);
            currentCustomPercent = savedInstanceState.getInt(CUSTOM_PERCENT);
            Log.i("Tip Calc", "Bundle is NOT null");
        }

        tip10EditText = (EditText) view.findViewById(R.id.tip10EditText);
        total10EditText = (EditText) view.findViewById(R.id.total10EditText);
        tip15EditText = (EditText) view.findViewById(R.id.tip15EditText);
        total15EditText = (EditText) view.findViewById(R.id.total15EditText);
        tip20EditText = (EditText) view.findViewById(R.id.tip20EditText);
        total20EditText = (EditText) view.findViewById(R.id.total20EditText);
        billEditText = (EditText) view.findViewById(R.id.billEditText);
        tipCustomEditText = (EditText) view.findViewById(R.id.tipCustomEditText);
        totalCustomEditText = (EditText) view.findViewById(R.id.totalCustomEditText);

        customTipTextView = (TextView) view.findViewById(R.id.customTipTextView);

        billEditText.addTextChangedListener(billEditTextWatcher);

        customSeekBar = (SeekBar) view.findViewById(R.id.customSeekBar);
        customSeekBar.setOnSeekBarChangeListener(customSeekBarListener);

        customSeekBar.setProgress(currentCustomPercent);
        if (currentBillTotal != 0.0)
        {
            billEditText.setText(Double.toString(currentBillTotal));
        }

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        currentCustomPercent = sharedPrefs.getInt(CUSTOM_PERCENT, 18);
        String stringCurrentBillTotal = sharedPrefs.getString(CURRENT_BILL, "0");
        if (stringCurrentBillTotal.equalsIgnoreCase("0"))
        {
            currentBillTotal = 0;
        }
        else
        {
            currentBillTotal = Double.parseDouble(stringCurrentBillTotal);
        }
        billEditText.setText(Double.toString(currentBillTotal));
        customSeekBar.setProgress(currentCustomPercent);
     }

    @Override
    public void onPause()
    {
        super.onPause();
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putInt(CUSTOM_PERCENT, customSeekBar.getProgress());
        editor.putString(CURRENT_BILL, billEditText.getText().toString());
        editor.apply();
    }

    private void updateStandard()
    {
        double PercentTip10 = currentBillTotal * .1;
        double PercentTotal10 = currentBillTotal + PercentTip10;

        double PercentTip15 = currentBillTotal * .15;
        double PercentTotal15 = currentBillTotal + PercentTip15;

        double PercentTip20 = currentBillTotal * .2;
        double PercentTotal20 = currentBillTotal + PercentTip20;

        tip10EditText.setText(String.format("%.02f", PercentTip10));
        tip15EditText.setText(String.format("%.02f", PercentTip15));
        tip20EditText.setText(String.format("%.02f", PercentTip20));

        total10EditText.setText(String.format("%.02f", PercentTotal10));
        total15EditText.setText(String.format("%.02f", PercentTotal15));
        total20EditText.setText(String.format("%.02f", PercentTotal20));
    }

    private void updateCustom()
    {
        customTipTextView.setText(currentCustomPercent+"%");

        double customTipAmount = currentBillTotal * currentCustomPercent * .01;
        double customTotalAmount = currentBillTotal + customTipAmount;

        tipCustomEditText.setText(String.format("%.02f", customTipAmount));
        totalCustomEditText.setText(String.format("%.02f", customTotalAmount));
    }

    private SeekBar.OnSeekBarChangeListener customSeekBarListener = new SeekBar.OnSeekBarChangeListener()
    {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b)
        {
            currentCustomPercent = seekBar.getProgress();
            updateCustom();
        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar){}
        @Override
        public void onStopTrackingTouch(SeekBar seekBar){}
    };

    private TextWatcher billEditTextWatcher = new TextWatcher()
    {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3){}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3)
        {
            try
            {
                currentBillTotal = Double.parseDouble(charSequence.toString());

            }
            catch (NumberFormatException NFE)
            {
                Toast.makeText(getActivity(), "That is an invalid character", Toast.LENGTH_SHORT).show();
                if (charSequence.length() > 0)
                {
                    String value = charSequence.toString();
                    value = value.substring(0, (value.length()-1));
                    billEditText.setText(value);
                }
            }
            updateStandard();
            updateCustom();
        }
        @Override
        public void afterTextChanged(Editable editable){}
    };

}
