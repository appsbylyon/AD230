package com.appsbylyon.ad230.doodlz;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.appsbylyon.ad230.R;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by infinite on 8/30/2014.
 */
public class DoodlzFragment extends Fragment
{
    private static final String TAG  = "Doodlz Fragment";

    private DoodleView doodleView; // drawing View
    private SensorManager sensorManager; // monitors accelerometer
    private float acceleration; // acceleration
    private float currentAcceleration; // current acceleration
    private float lastAcceleration; // last acceleration
    private AtomicBoolean dialogIsVisible = new AtomicBoolean(); // false

    // create menu ids for each menu option
    private static final int COLOR_MENU_ID = Menu.FIRST;
    private static final int WIDTH_MENU_ID = Menu.FIRST + 1;
    private static final int ERASE_MENU_ID = Menu.FIRST + 2;
    private static final int CLEAR_MENU_ID = Menu.FIRST + 3;
    private static final int SAVE_MENU_ID = Menu.FIRST + 4;

    // value used to determine whether user shook the device to erase
    private static final int ACCELERATION_THRESHOLD = 15000;

    // variable that refers to a Choose Color or Choose Line Width dialog
    private Dialog currentDialog;

    private Activity activity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle icicle) {
        super.onCreateView(inflater, parent, icicle);
        View view = inflater.inflate(R.layout.doodlz_main, parent, false);

        // get reference to the DoodleView
        doodleView = (DoodleView) view.findViewById(R.id.doodleView);

        // initialize acceleration values
        acceleration = 0.00f;
        currentAcceleration = SensorManager.GRAVITY_EARTH;
        lastAcceleration = SensorManager.GRAVITY_EARTH;


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        enableAccelerometerListening();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        disableAccelerometerListening();
    }

    // enable listening for accelerometer events
    private void enableAccelerometerListening()
    {
        // initialize the SensorManager
        sensorManager =
                (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(sensorEventListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    } // end method enableAccelerometerListening

    // disable listening for accelerometer events
    private void disableAccelerometerListening()
    {
        // stop listening for sensor events
        if (sensorManager != null)
        {
            sensorManager.unregisterListener(
                    sensorEventListener,
                    sensorManager.getDefaultSensor(
                            SensorManager.SENSOR_ACCELEROMETER));
            sensorManager = null;
        } // end if
    } // end method disableAccelerometerListening

    // event handler for accelerometer events
    private SensorEventListener sensorEventListener =
            new SensorEventListener()
            {
                // use accelerometer to determine whether user shook device
                @Override
                public void onSensorChanged(SensorEvent event)
                {
                    // ensure that other dialogs are not displayed
                    if (!dialogIsVisible.get())
                    {
                        // get x, y, and z values for the SensorEvent
                        float x = event.values[0];
                        float y = event.values[1];
                        float z = event.values[2];

                        // save previous acceleration value
                        lastAcceleration = currentAcceleration;

                        // calculate the current acceleration
                        currentAcceleration = x * x + y * y + z * z;

                        // calculate the change in acceleration
                        acceleration = currentAcceleration *
                                (currentAcceleration - lastAcceleration);

                        // if the acceleration is above a certain threshold
                        if (acceleration > ACCELERATION_THRESHOLD)
                        {
                            // create a new AlertDialog Builder
                            AlertDialog.Builder builder =
                                    new AlertDialog.Builder(activity);

                            // set the AlertDialog's message
                            builder.setMessage(R.string.doodlz_message_erase);
                            builder.setCancelable(true);

                            // add Erase Button
                            builder.setPositiveButton(R.string.doodlz_button_erase,
                                    new DialogInterface.OnClickListener()
                                    {
                                        public void onClick(DialogInterface dialog, int id)
                                        {
                                            dialogIsVisible.set(false);
                                            doodleView.clear(); // clear the screen
                                        } // end method onClick
                                    } // end anonymous inner class
                            ); // end call to setPositiveButton

                            // add Cancel Button
                            builder.setNegativeButton(R.string.doodlz_button_cancel,
                                    new DialogInterface.OnClickListener()
                                    {
                                        public void onClick(DialogInterface dialog, int id)
                                        {
                                            dialogIsVisible.set(false);
                                            dialog.cancel(); // dismiss the dialog
                                        } // end method onClick
                                    } // end anonymous inner class
                            ); // end call to setNegativeButton

                            dialogIsVisible.set(true); // dialog is on the screen
                            builder.show(); // display the dialog
                        } // end if
                    } // end if
                } // end method onSensorChanged

                // required method of interface SensorEventListener
                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy)
                {
                } // end method onAccuracyChanged
            }; // end anonymous inner class

    public Menu formatMenu (Menu menu) {
        // add options to menu
        menu.add(Menu.NONE, COLOR_MENU_ID, Menu.NONE,
                R.string.doodlz_menuitem_color);
        menu.add(Menu.NONE, WIDTH_MENU_ID, Menu.NONE,
                R.string.doodlz_menuitem_line_width);
        menu.add(Menu.NONE, ERASE_MENU_ID, Menu.NONE,
                R.string.doodlz_menuitem_erase);
        menu.add(Menu.NONE, CLEAR_MENU_ID, Menu.NONE,
                R.string.doodlz_menuitem_clear);
        menu.add(Menu.NONE, SAVE_MENU_ID, Menu.NONE,
                R.string.doodlz_menuitem_save_image);
        return menu;
    }

    public void handleMenuSelect(MenuItem item) {
        switch (item.getItemId())
        {
            case COLOR_MENU_ID:
                showColorDialog(); // display color selection dialog
                break;
            case WIDTH_MENU_ID:
                showLineWidthDialog(); // display line thickness dialog
                break;
            case ERASE_MENU_ID:
                doodleView.setDrawingColor(Color.WHITE); // line color white
                break;
            case CLEAR_MENU_ID:
                doodleView.clear(); // clear doodleView
                break;
            case SAVE_MENU_ID:
                doodleView.saveImage(); // save the current images
                break;
        } // end switch
    }

    private void showColorDialog()
    {
        // create the dialog and inflate its content
        currentDialog = new Dialog(activity);
        currentDialog.setContentView(R.layout.doodlz_color_dialog);
        currentDialog.setTitle(R.string.doodlz_title_color_dialog);
        currentDialog.setCancelable(true);

        // get the color SeekBars and set their onChange listeners
        final SeekBar alphaSeekBar =
                (SeekBar) currentDialog.findViewById(R.id.alphaSeekBar);
        final SeekBar redSeekBar =
                (SeekBar) currentDialog.findViewById(R.id.redSeekBar);
        final SeekBar greenSeekBar =
                (SeekBar) currentDialog.findViewById(R.id.greenSeekBar);
        final SeekBar blueSeekBar =
                (SeekBar) currentDialog.findViewById(R.id.blueSeekBar);

        // register SeekBar event listeners
        alphaSeekBar.setOnSeekBarChangeListener(colorSeekBarChanged);
        redSeekBar.setOnSeekBarChangeListener(colorSeekBarChanged);
        greenSeekBar.setOnSeekBarChangeListener(colorSeekBarChanged);
        blueSeekBar.setOnSeekBarChangeListener(colorSeekBarChanged);

        // use current drawing color to set SeekBar values
        final int color = doodleView.getDrawingColor();
        alphaSeekBar.setProgress(Color.alpha(color));
        redSeekBar.setProgress(Color.red(color));
        greenSeekBar.setProgress(Color.green(color));
        blueSeekBar.setProgress(Color.blue(color));

        // set the Set Color Button's onClickListener
        Button setColorButton = (Button) currentDialog.findViewById(
                R.id.setColorButton);
        setColorButton.setOnClickListener(setColorButtonListener);

        dialogIsVisible.set(true); // dialog is on the screen
        currentDialog.show(); // show the dialog
    } // end method showColorDialog

    // OnSeekBarChangeListener for the SeekBars in the color dialog
    private SeekBar.OnSeekBarChangeListener colorSeekBarChanged =
            new SeekBar.OnSeekBarChangeListener()
            {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress,
                                              boolean fromUser)
                {
                    // get the SeekBars and the colorView LinearLayout
                    SeekBar alphaSeekBar =
                            (SeekBar) currentDialog.findViewById(R.id.alphaSeekBar);
                    SeekBar redSeekBar =
                            (SeekBar) currentDialog.findViewById(R.id.redSeekBar);
                    SeekBar greenSeekBar =
                            (SeekBar) currentDialog.findViewById(R.id.greenSeekBar);
                    SeekBar blueSeekBar =
                            (SeekBar) currentDialog.findViewById(R.id.blueSeekBar);
                    View colorView =
                            (View) currentDialog.findViewById(R.id.colorView);

                    // display the current color
                    colorView.setBackgroundColor(Color.argb(
                            alphaSeekBar.getProgress(), redSeekBar.getProgress(),
                            greenSeekBar.getProgress(), blueSeekBar.getProgress()));
                } // end method onProgressChanged

                // required method of interface OnSeekBarChangeListener
                @Override
                public void onStartTrackingTouch(SeekBar seekBar)
                {
                } // end method onStartTrackingTouch

                // required method of interface OnSeekBarChangeListener
                @Override
                public void onStopTrackingTouch(SeekBar seekBar)
                {
                } // end method onStopTrackingTouch
            }; // end colorSeekBarChanged

    // OnClickListener for the color dialog's Set Color Button
    private View.OnClickListener setColorButtonListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            // get the color SeekBars
            SeekBar alphaSeekBar =
                    (SeekBar) currentDialog.findViewById(R.id.alphaSeekBar);
            SeekBar redSeekBar =
                    (SeekBar) currentDialog.findViewById(R.id.redSeekBar);
            SeekBar greenSeekBar =
                    (SeekBar) currentDialog.findViewById(R.id.greenSeekBar);
            SeekBar blueSeekBar =
                    (SeekBar) currentDialog.findViewById(R.id.blueSeekBar);

            // set the line color
            doodleView.setDrawingColor(Color.argb(
                    alphaSeekBar.getProgress(), redSeekBar.getProgress(),
                    greenSeekBar.getProgress(), blueSeekBar.getProgress()));
            dialogIsVisible.set(false); // dialog is not on the screen
            currentDialog.dismiss(); // hide the dialog
            currentDialog = null; // dialog no longer needed
        } // end method onClick
    }; // end setColorButtonListener

    // display a dialog for setting the line width
    private void showLineWidthDialog()
    {
        // create the dialog and inflate its content
        currentDialog = new Dialog(activity);
        currentDialog.setContentView(R.layout.doodlz_width_dialog);
        currentDialog.setTitle(R.string.doodlz_title_line_width_dialog);
        currentDialog.setCancelable(true);

        // get widthSeekBar and configure it
        SeekBar widthSeekBar =
                (SeekBar) currentDialog.findViewById(R.id.widthSeekBar);
        widthSeekBar.setOnSeekBarChangeListener(widthSeekBarChanged);
        widthSeekBar.setProgress(doodleView.getLineWidth());

        // set the Set Line Width Button's onClickListener
        Button setLineWidthButton =
                (Button) currentDialog.findViewById(R.id.widthDialogDoneButton);
        setLineWidthButton.setOnClickListener(setLineWidthButtonListener);

        dialogIsVisible.set(true); // dialog is on the screen
        currentDialog.show(); // show the dialog
    } // end method showLineWidthDialog

    // OnSeekBarChangeListener for the SeekBar in the width dialog
    private SeekBar.OnSeekBarChangeListener widthSeekBarChanged =
            new SeekBar.OnSeekBarChangeListener()
            {
                Bitmap bitmap = Bitmap.createBitmap( // create Bitmap
                        400, 100, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap); // associate with Canvas

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress,
                                              boolean fromUser)
                {
                    // get the ImageView
                    ImageView widthImageView = (ImageView)
                            currentDialog.findViewById(R.id.widthImageView);

                    // configure a Paint object for the current SeekBar value
                    Paint p = new Paint();
                    p.setColor(doodleView.getDrawingColor());
                    p.setStrokeCap(Paint.Cap.ROUND);
                    p.setStrokeWidth(progress);

                    // erase the bitmap and redraw the line
                    bitmap.eraseColor(Color.WHITE);
                    canvas.drawLine(30, 50, 370, 50, p);
                    widthImageView.setImageBitmap(bitmap);
                } // end method onProgressChanged

                // required method of interface OnSeekBarChangeListener
                @Override
                public void onStartTrackingTouch(SeekBar seekBar)
                {
                } // end method onStartTrackingTouch

                // required method of interface OnSeekBarChangeListener
                @Override
                public void onStopTrackingTouch(SeekBar seekBar)
                {
                } // end method onStopTrackingTouch
            }; // end widthSeekBarChanged

    // OnClickListener for the line width dialog's Set Line Width Button
    private View.OnClickListener setLineWidthButtonListener =
            new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    // get the color SeekBars
                    SeekBar widthSeekBar =
                            (SeekBar) currentDialog.findViewById(R.id.widthSeekBar);

                    // set the line color
                    doodleView.setLineWidth(widthSeekBar.getProgress());
                    dialogIsVisible.set(false); // dialog is not on the screen
                    currentDialog.dismiss(); // hide the dialog
                    currentDialog = null; // dialog no longer needed
                } // end method onClick
            }; // end setColorButtonListener
}
