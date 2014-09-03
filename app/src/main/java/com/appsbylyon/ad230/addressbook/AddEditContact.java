package com.appsbylyon.ad230.addressbook;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.appsbylyon.ad230.R;

public class AddEditContact extends DialogFragment
{
    public interface AddEditContactListener{
        public void updateListView();
    }



    private long rowID; // id of contact being edited, if any

    private AddEditContactListener listener;

    // EditTexts for contact information
    private EditText nameEditText;
    private EditText phoneEditText;
    private EditText emailEditText;
    private EditText streetEditText;
    private EditText cityEditText;

    private Activity activity;

    public void setListener(Fragment listener) {
        this.listener = (AddEditContactListener) listener;
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }
    // called when the Activity is first started
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, parent, savedInstanceState);
        View view = inflater.inflate(R.layout.addy_book_add_contact, parent, false);

        getDialog().getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        nameEditText = (EditText) view.findViewById(R.id.nameEditText);
        emailEditText = (EditText) view.findViewById(R.id.emailEditText);
        phoneEditText = (EditText) view.findViewById(R.id.phoneEditText);
        streetEditText = (EditText) view.findViewById(R.id.streetEditText);
        cityEditText = (EditText) view.findViewById(R.id.cityEditText);

        Bundle extras = getArguments(); // get Bundle of extras

        // if there are extras, use them to populate the EditTexts
        if (extras != null)
        {
            rowID = extras.getLong("row_id");
            nameEditText.setText(extras.getString("name"));
            emailEditText.setText(extras.getString("email"));
            phoneEditText.setText(extras.getString("phone"));
            streetEditText.setText(extras.getString("street"));
            cityEditText.setText(extras.getString("city"));
        } // end if

        // set event listener for the Save Contact Button
        Button saveContactButton =
                (Button) view.findViewById(R.id.saveContactButton);
        saveContactButton.setOnClickListener(saveContactButtonClicked);

        Button cancelButton = (Button) view.findViewById(R.id.cancelSaveContactButton);
        cancelButton.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                AddEditContact.this.dismiss();
            }
        });

        return view;
    } // end method onCreate

    @Override
    public void onResume() {
        super.onResume();
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = (int)((double) size.x * 0.8);
        getDialog().getWindow().setAttributes(params);
    }

    // responds to event generated when user clicks the Done Button
    OnClickListener saveContactButtonClicked = new OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if (nameEditText.getText().length() != 0)
            {
                AsyncTask<Object, Object, Object> saveContactTask =
                        new AsyncTask<Object, Object, Object>()
                        {
                            @Override
                            protected Object doInBackground(Object... params)
                            {
                                saveContact(); // save contact to the database
                                return null;
                            } // end method doInBackground

                            @Override
                            protected void onPostExecute(Object result)
                            {
                                AddEditContact.this.dismiss(); // return to the previous Activity
                                listener.updateListView();
                            } // end method onPostExecute
                        }; // end AsyncTask

                // save the contact to the database using a separate thread
                saveContactTask.execute((Object[]) null);
            } // end if
            else
            {
                // create a new AlertDialog Builder
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(activity);

                // set dialog title & message, and provide Button to dismiss
                builder.setTitle(R.string.addy_book_errorTitle);
                builder.setMessage(R.string.addy_book_errorMessage);
                builder.setPositiveButton(R.string.addy_book_errorButton, null);
                builder.show(); // display the Dialog
            } // end else
        } // end method onClick
    }; // end OnClickListener saveContactButtonClicked

    // saves contact information to the database
    private void saveContact()
    {
        // get DatabaseConnector to interact with the SQLite database
        DatabaseConnector databaseConnector = new DatabaseConnector(activity);

        if (getArguments() == null)
        {
            // insert the contact information into the database
            databaseConnector.insertContact(
                    nameEditText.getText().toString(),
                    emailEditText.getText().toString(),
                    phoneEditText.getText().toString(),
                    streetEditText.getText().toString(),
                    cityEditText.getText().toString());
        } // end if
        else
        {
            databaseConnector.updateContact(rowID,
                    nameEditText.getText().toString(),
                    emailEditText.getText().toString(),
                    phoneEditText.getText().toString(),
                    streetEditText.getText().toString(),
                    cityEditText.getText().toString());
        } // end else
    } // end class saveContact
} // end class AddEditContact
