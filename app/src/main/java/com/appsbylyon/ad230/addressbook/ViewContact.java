package com.appsbylyon.ad230.addressbook;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.appsbylyon.ad230.R;

public class ViewContact extends DialogFragment implements View.OnClickListener
{
    public interface EditContactListener {
        public void editContact(Bundle bundle);
        public void updateListView();
    }

    private long rowID; // selected contact's name
    private TextView nameTextView; // displays contact's name
    private TextView phoneTextView; // displays contact's phone
    private TextView emailTextView; // displays contact's email
    private TextView streetTextView; // displays contact's street
    private TextView cityTextView; // displays contact's city/state/zip

    private Button deleteButton;
    private Button editButton;

    private Activity activity;

    private EditContactListener listener;

    public void setListener(Fragment fragment) {
        listener = (EditContactListener) fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    // called when the activity is first created
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, parent, savedInstanceState);
        View view = inflater.inflate(R.layout.addy_book_view_contact, parent, false);

        getDialog().getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        // get the EditTexts
        nameTextView = (TextView) view.findViewById(R.id.nameTextView);
        phoneTextView = (TextView) view.findViewById(R.id.phoneTextView);
        emailTextView = (TextView) view.findViewById(R.id.emailTextView);
        streetTextView = (TextView) view.findViewById(R.id.streetTextView);
        cityTextView = (TextView) view.findViewById(R.id.cityTextView);

        deleteButton = (Button) view.findViewById(R.id.addy_book_delete_contact);
        deleteButton.setOnClickListener(this);

        editButton = (Button) view.findViewById(R.id.addy_book_edit_button);
        editButton.setOnClickListener(this);

        Button doneButton = (Button) view.findViewById(R.id.addy_book_done_button);
        doneButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                ViewContact.this.dismiss();
            }
        });

        // get the selected contact's row ID
        Bundle extras = getArguments();
        rowID = extras.getLong(AddressBookFragment.ROW_ID);
        return view;
    } // end method onCreate

    // called when the activity is first created
    @Override
    public void onResume()
    {
        super.onResume();
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = (int)((double) size.x * 0.8);
        getDialog().getWindow().setAttributes(params);
        // create new LoadContactTask and execute it
        new LoadContactTask().execute(rowID);
    } // end method onResume

    // performs database query outside GUI thread
    private class LoadContactTask extends AsyncTask<Long, Object, Cursor>
    {
        DatabaseConnector databaseConnector =
                new DatabaseConnector(activity);

        // perform the database access
        @Override
        protected Cursor doInBackground(Long... params)
        {
            databaseConnector.open();

            // get a cursor containing all data on given entry
            return databaseConnector.getOneContact(params[0]);
        } // end method doInBackground

        // use the Cursor returned from the doInBackground method
        @Override
        protected void onPostExecute(Cursor result)
        {
            super.onPostExecute(result);

            result.moveToFirst(); // move to the first item

            // get the column index for each data item
            int nameIndex = result.getColumnIndex("name");
            int phoneIndex = result.getColumnIndex("phone");
            int emailIndex = result.getColumnIndex("email");
            int streetIndex = result.getColumnIndex("street");
            int cityIndex = result.getColumnIndex("city");

            // fill TextViews with the retrieved data
            nameTextView.setText(result.getString(nameIndex));
            phoneTextView.setText(result.getString(phoneIndex));
            emailTextView.setText(result.getString(emailIndex));
            streetTextView.setText(result.getString(streetIndex));
            cityTextView.setText(result.getString(cityIndex));

            result.close(); // close the result cursor
            databaseConnector.close(); // close database connection
        } // end method onPostExecute
    } // end class LoadContactTask



    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.addy_book_delete_contact:
                deleteContact();
                break;
            case R.id.addy_book_edit_button:
                Bundle bundle = new Bundle();
                bundle.putLong(AddressBookFragment.ROW_ID, rowID);
                bundle.putString("name", nameTextView.getText().toString());
                bundle.putString("phone", phoneTextView.getText().toString());
                bundle.putString("email", emailTextView.getText().toString());
                bundle.putString("street", streetTextView.getText().toString());
                bundle.putString("city", cityTextView.getText().toString());
                listener.editContact(bundle);
                this.dismiss();
                break;
        }
    }
    // delete a contact
    private void deleteContact()
    {
        // create a new AlertDialog Builder
        AlertDialog.Builder builder =
                new AlertDialog.Builder(activity);

        builder.setTitle(R.string.addy_book_confirmTitle); // title bar string
        builder.setMessage(R.string.addy_book_confirmMessage); // message to display

        // provide an OK button that simply dismisses the dialog
        builder.setPositiveButton(R.string.addy_book_button_delete,
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int button)
                    {
                        final DatabaseConnector databaseConnector =
                                new DatabaseConnector(activity);

                        // create an AsyncTask that deletes the contact in another
                        // thread, then calls finish after the deletion
                        AsyncTask<Long, Object, Object> deleteTask =
                                new AsyncTask<Long, Object, Object>()
                                {
                                    @Override
                                    protected Object doInBackground(Long... params)
                                    {
                                        databaseConnector.deleteContact(params[0]);
                                        return null;
                                    } // end method doInBackground

                                    @Override
                                    protected void onPostExecute(Object result)
                                    {
                                        ViewContact.this.dismiss(); // return to the AddressBook Activity
                                        listener.updateListView();
                                    } // end method onPostExecute
                                }; // end new AsyncTask

                        // execute the AsyncTask to delete contact at rowID
                        deleteTask.execute(new Long[] { rowID });
                    } // end method onClick
                } // end anonymous inner class
        ); // end call to method setPositiveButton

        builder.setNegativeButton(R.string.addy_book_button_cancel, null);
        builder.show(); // display the Dialog
    } // end method deleteContact
} // end class ViewContact
