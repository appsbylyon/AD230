package com.appsbylyon.ad230.twit;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;

import com.appsbylyon.ad230.R;

import java.util.Arrays;

/**
 * Created by infinite on 8/16/2014.
 */
public class TwitterSearch extends Fragment implements View.OnClickListener
{
    private static final String TAG = "AD230 - Twitter Search";
    private static final String SAVED_SEARCHES = "SAVED_SEARCHES";

    private SharedPreferences savedSearches;

    private EditText searchEntry;
    private EditText tagEntry;

    private Button searchButton;
    private Button saveTagButton;
    private Button clearTagButton;

    private ScrollView tagScroll;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, parent, savedInstanceState);
        View view = inflater.inflate(R.layout.twit_control, parent, false);

        savedSearches = getActivity().getSharedPreferences(SAVED_SEARCHES, Context.MODE_PRIVATE);

        searchEntry = (EditText) view.findViewById(R.id.twit_search_entry);
        tagEntry = (EditText) view.findViewById(R.id.twit_query_tag_entry);

        searchButton = (Button) view.findViewById(R.id.twit_search_button);
        searchButton.setOnClickListener(this);

        saveTagButton = (Button) view.findViewById(R.id.twit_save_button);
        saveTagButton.setOnClickListener(saveButtonListener);

        clearTagButton = (Button) view.findViewById(R.id.twit_clear_tag_button);
        clearTagButton.setOnClickListener(this.clearTagListener);

        tagScroll = (ScrollView) view.findViewById(R.id.twit_tag_query_scroll);

        refreshButtons(null);
        return view;
    }

    private void refreshButtons(String newTag)
    {
        String[] tags = savedSearches.getAll().keySet().toArray(new String[0]);
        Arrays.sort(tags, String.CASE_INSENSITIVE_ORDER);

        if (newTag != null)
        {
            makeTagGui(newTag, Arrays.binarySearch(tags, newTag));
        }
        else
        {
            for (int i = 0; i < tags.length; ++i)
            {
                makeTagGui(newTag, i);
            }
        }
    }

    private void makeTag(String query, String tag)
    {
        String originalQuery = savedSearches.getString(tag, null);

        savedSearches.edit().putString(tag, query).apply();

        if (originalQuery == null){refreshButtons(tag);};
    }

    private void makeTagGui(String tag, int index)
    {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View newTagView = inflater.inflate(R.layout.twit_new_tag_view, null);

        Button newTagButton = (Button) newTagView.findViewWithTag(R.id.twit_new_tag_button);
        newTagButton.setText(tag);
        newTagButton.setOnClickListener(searchListener);

        Button newTagEditButton = (Button) newTagView.findViewById(R.id.twit_new_tag_edit_button);
        newTagEditButton.setOnClickListener(editButtonListener);

        tagScroll.addView(newTagView, index);
    }

    private void clearSavedTags()
    {
        tagScroll.removeAllViews();
        savedSearches.edit().clear().apply();
    }

    public View.OnClickListener saveButtonListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            if ((searchEntry.getText().length() > 0) && (tagEntry.getText().length() > 0))
            {
                makeTag(searchEntry.getText().toString().trim(), tagEntry.getText().toString().trim());
                searchEntry.setText("");
                tagEntry.setText("");

                ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(tagEntry.getWindowToken(), 0);
            }
            else
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.twit_missingTitle)
                        .setPositiveButton(R.string.twit_OK, null)
                        .setMessage(R.string.twit_missingMessage);
                builder.create().show();

            }
        }
    };

    public View.OnClickListener clearTagListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.twit_confirmTitle)
                    .setCancelable(true)
                    .setNegativeButton(R.string.twit_cancel, null)
                    .setMessage(R.string.twit_confirmMessage)
                    .setPositiveButton(R.string.twit_erase, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {
                            TwitterSearch.this.clearSavedTags();
                        }
                    });
            builder.create().show();
        }
    };

    public View.OnClickListener editButtonListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            Button button = (Button) view.findViewById(R.id.twit_new_tag_button);
            String tag = button.getText().toString();

            tagEntry.setText(tag);
            searchEntry.setText(savedSearches.getString(tag, ""));
        }
    };

    public View.OnClickListener searchListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            //TODO add webview code here
        }
    };

    @Override
    public void onClick(View view)
    {
        int id = view.getId();
        switch (id)
        {

        }
    }
}
