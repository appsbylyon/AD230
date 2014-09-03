package com.appsbylyon.ad230.addressbook;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.appsbylyon.ad230.R;

/**
 * Created by infinite on 8/30/2014.
 */
public class AddressBookFragment extends ListFragment implements ViewContact.EditContactListener,
        AddEditContact.AddEditContactListener
{
    private static final String TAG = "Address Book";

    public static final String ROW_ID = "row_id"; // Intent extra key
    private ListView contactListView; // the ListActivity's ListView
    private CursorAdapter contactAdapter; // adapter for ListView

    private Activity activity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle icicle) {
        //contactListView = getListView();
        //contactListView.setOnItemClickListener(viewContactListener);

        String[] from = new String[] {"name"};
        int[] to = new int [] {R.id.contactTextView};
        //noinspection deprecation
        contactAdapter = new SimpleCursorAdapter(
                activity, R.layout.addy_book_contact_list_item, null, from, to);
        setListAdapter(contactAdapter);

        return super.onCreateView(inflater, parent, icicle);
     }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        FragmentManager fm = getFragmentManager();
        Bundle bundle = new Bundle();
        bundle.putLong(ROW_ID, id);
        ViewContact viewContact = new ViewContact();
        viewContact.setListener(this);
        viewContact.setArguments(bundle);
        viewContact.show(fm, "VIEW_CONTACT");
    }

    @Override
    public void updateListView() {
        new GetContactsTask().execute();
    }

    @Override
    public void onResume() {
        super.onResume();
        new GetContactsTask().execute();
    }


    @SuppressWarnings("deprecation")
    @Override
    public void onDetach() {
        Cursor cursor = contactAdapter.getCursor(); // get current Cursor

        if (cursor != null)
            cursor.deactivate(); // deactivate it

        contactAdapter.changeCursor(null); // adapted now has no Cursor
        super.onDetach();

    }
    // performs database query outside GUI thread
    private class GetContactsTask extends AsyncTask<Object, Object, Cursor>
    {
        DatabaseConnector databaseConnector =
                new DatabaseConnector(activity);

        // perform the database access
        @Override
        protected Cursor doInBackground(Object... params)
        {
            databaseConnector.open();

            // get a cursor containing call contacts
            return databaseConnector.getAllContacts();
        } // end method doInBackground

        // use the Cursor returned from the doInBackground method
        @Override
        protected void onPostExecute(Cursor result)
        {
            contactAdapter.changeCursor(result); // set the adapter's Cursor
            databaseConnector.close();
        } // end method onPostExecute
    } // end class GetContactsTask

    /**
    AdapterView.OnItemClickListener viewContactListener = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                long arg3)
        {
            // create an Intent to launch the ViewContact Activity
            Intent viewContact =
                    new Intent(activity, ViewContact.class);

            // pass the selected contact's row ID as an extra with the Intent
            viewContact.putExtra(ROW_ID, arg3);
            startActivity(viewContact); // start the ViewContact Activity
        } // end method onItemClick
    }; // end viewContactListener
    */
    public void createMenuOptions(Menu menu) {
        activity.getMenuInflater().inflate(R.menu.addressbook_menu, menu);
    }

    public void handleMenuSelection(MenuItem item) {
        showAddEditContact(null);
    }

    @Override
    public void editContact(Bundle bundle) {
        showAddEditContact(bundle);
    }

    public void showAddEditContact(Bundle bundle) {
        FragmentManager fm = getFragmentManager();
        AddEditContact dialog = new AddEditContact();
        dialog.setListener(this);
        if (bundle != null) {dialog.setArguments(bundle);}
        dialog.show(fm, "ADD_EDIT_CONTACT");

    }
}
