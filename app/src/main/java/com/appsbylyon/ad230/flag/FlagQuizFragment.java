package com.appsbylyon.ad230.flag;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.appsbylyon.ad230.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Created by infinite on 8/17/2014.
 */
public class FlagQuizFragment extends Fragment
{
    private static final String TAG = "Flag Quiz Game";
    private static final String SAVE_STATE = "flag_save_state.flg";
    private final int CHOICES_MENU_ID = Menu.FIRST;
    private final int REGIONS_MENU_ID = Menu.FIRST + 1;


    private List<String> fileNameList; // flag file names
    private List<String> quizCountriesList; // names of countries in quiz
    private Map<String, Boolean> regionsMap; // which regions are enabled
    private String correctAnswer; // correct country for the current flag
    private int totalGuesses; // number of guesses made
    private int correctAnswers; // number of correct guesses
    private int guessRows; // number of rows displaying choices
    private Random random; // random number generator
    private Handler handler; // used to delay loading next flag
    private Animation shakeAnimation; // animation for incorrect guess

    private TextView answerTextView; // displays Correct! or Incorrect!
    private TextView questionNumberTextView; // shows current question #
    private ImageView flagImageView; // displays a flag
    private TableLayout buttonTableLayout; // table of answer Buttons





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
                layoutId = R.layout.flag_quiz_port;
                break;
            case Surface.ROTATION_90:
                layoutId = R.layout.flag_quiz_land;
                break;
            case Surface.ROTATION_180:
                layoutId = R.layout.flag_quiz_port;
                break;
            case Surface.ROTATION_270:
                layoutId = R.layout.flag_quiz_land;
                break;
        }
        View view = inflater.inflate(layoutId, parent, false);

        fileNameList = new ArrayList<String>();
        quizCountriesList = new ArrayList<String>();
        regionsMap = new HashMap<String, Boolean>();
        guessRows = 1;
        random = new Random();
        handler = new Handler();

        shakeAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.flag_incorrect_shake);
        shakeAnimation.setRepeatCount(3);

        String[] regionNames = getResources().getStringArray(R.array.flag_regionsList);

        for (String region : regionNames ){ regionsMap.put(region, true);}

        questionNumberTextView = (TextView) view.findViewById(R.id.flag_questionNumberTextView);
        flagImageView = (ImageView) view.findViewById(R.id.flag_flagImageView);
        buttonTableLayout = (TableLayout) view.findViewById(R.id.flag_buttonTableLayout);
        answerTextView = (TextView) view.findViewById(R.id.flag_answerTextView);

        questionNumberTextView.setText( getResources().getString(R.string.flag_question) + " 1 " +
                        getResources().getString(R.string.flag_of) + " 10");

        resetQuiz();

        return view;
    }


    private void resetQuiz()
    {
        AssetManager assets = getActivity().getAssets();
        fileNameList.clear();
        try
        {
            Set<String> regions = regionsMap.keySet();

            for (String region : regions)
            {
                if (regionsMap.get(region))
                {
                    String[] paths = assets.list(region);
                    for (String path : paths){fileNameList.add(path.replace(".png", ""));}
                }
            }
        }
        catch (IOException e)
        {
            Log.e(TAG, "Error loading image file names", e);
        }

        correctAnswers = 0;
        totalGuesses = 0;
        quizCountriesList.clear();

        int flagCounter = 1;
        int numberOfFlags = fileNameList.size();

        while (flagCounter <= 10)
        {
            int randomIndex = random.nextInt(numberOfFlags);
            String fileName = fileNameList.get(randomIndex);

            if (!quizCountriesList.contains(fileName))
            {
                quizCountriesList.add(fileName);
                ++flagCounter;
            }
        }
        loadNextFlag();
    } // end method resetQuiz

    private void loadNextFlag()
    {
        String nextImageName = quizCountriesList.remove(0);
        correctAnswer = nextImageName;

        answerTextView.setText("");

        questionNumberTextView.setText( getResources().getString(R.string.flag_question) + " " +
                        (correctAnswers + 1) + " " + getResources().getString(R.string.flag_of) + " 10");

        String region = nextImageName.substring(0, nextImageName.indexOf('-'));

        AssetManager assets = getActivity().getAssets();
        InputStream stream;

        try
        {
            stream = assets.open(region + "/" + nextImageName + ".png");

            Drawable flag = Drawable.createFromStream(stream, nextImageName);
            flagImageView.setImageDrawable(flag);
        }
        catch (IOException e)
        {
            Log.e(TAG, "Error loading " + nextImageName, e);
        }

        for (int row = 0; row < buttonTableLayout.getChildCount(); ++row)
        {
            ((TableRow) buttonTableLayout.getChildAt(row)).removeAllViews();
        }
        Collections.shuffle(fileNameList);

        int correct = fileNameList.indexOf(correctAnswer);
        fileNameList.add(fileNameList.remove(correct));

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

        for (int row = 0; row < guessRows; row++)
        {
            TableRow currentTableRow = getTableRow(row);

            for (int column = 0; column < 3; column++)
            {
                Button newGuessButton = (Button) inflater.inflate(R.layout.flag_new_guess_button, null);

                String fileName = fileNameList.get((row * 3) + column);
                newGuessButton.setText(getCountryName(fileName));

                newGuessButton.setOnClickListener(guessButtonListener);
                currentTableRow.addView(newGuessButton);
            }
        }

        int row = random.nextInt(guessRows);
        int column = random.nextInt(3);
        TableRow randomTableRow = getTableRow(row);
        String countryName = getCountryName(correctAnswer);
        ((Button)randomTableRow.getChildAt(column)).setText(countryName);
    } // end method loadNextFlag

    private TableRow getTableRow(int row)
    {
        return (TableRow) buttonTableLayout.getChildAt(row);
    }

    private String getCountryName(String name)
    {
        return name.substring(name.indexOf('-') + 1).replace('_', ' ');
    }

    private void submitGuess(Button guessButton)
    {
        String guess = guessButton.getText().toString();
        String answer = getCountryName(correctAnswer);
        ++totalGuesses;

        if (guess.equals(answer))
        {
            ++correctAnswers;

            answerTextView.setText(answer + "!");
            answerTextView.setTextColor(getResources().getColor(R.color.flag_correct_answer));

            disableButtons();

            if (correctAnswers == 10)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                builder.setTitle(R.string.flag_reset_quiz); // title bar string

                builder.setMessage(String.format("%d %s, %.02f%% %s",
                        totalGuesses, getResources().getString(R.string.flag_guesses),
                        (1000 / (double) totalGuesses),
                        getResources().getString(R.string.flag_correct)));

                builder.setCancelable(false);

                builder.setPositiveButton(R.string.flag_reset_quiz,
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                resetQuiz();
                            }
                        }
                );

                builder.create().show();
            }
            else
            {
                handler.postDelayed(
                        new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                loadNextFlag();
                            }
                        }, 1000);
            }
        }
        else // guess was incorrect
        {
            flagImageView.startAnimation(shakeAnimation);

            // display "Incorrect!" in red
            answerTextView.setText(R.string.flag_incorrect_answer);
            answerTextView.setTextColor(getResources().getColor(R.color.flag_correct_answer));
            guessButton.setEnabled(false);
        } // end else
    } // end method submitGuess

    private void disableButtons()
    {
        for (int row = 0; row < buttonTableLayout.getChildCount(); ++row)
        {
            TableRow tableRow = (TableRow) buttonTableLayout.getChildAt(row);
            for (int i = 0; i < tableRow.getChildCount(); ++i)
                tableRow.getChildAt(i).setEnabled(false);
        } // end outer for
    } // end method disableButtons

    private View.OnClickListener guessButtonListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            submitGuess((Button) v); // pass selected Button to submitGuess
        }
    };

    public Menu formatMenu(Menu menu)
    {
        menu.add(Menu.NONE, CHOICES_MENU_ID, Menu.NONE, R.string.flag_choices);
        menu.add(Menu.NONE, REGIONS_MENU_ID, Menu.NONE, R.string.flag_regions);
        return menu;
    }

    public void handleMenuSelection(MenuItem item)
    {
        switch (item.getItemId())
        {
            case CHOICES_MENU_ID:
                final String[] possibleChoices = getResources().getStringArray(R.array.flag_guessesList);

                AlertDialog.Builder choicesBuilder = new AlertDialog.Builder(getActivity());
                choicesBuilder.setTitle(R.string.flag_choices);

                choicesBuilder.setItems(R.array.flag_guessesList,
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int item)
                            {
                                guessRows = Integer.parseInt(possibleChoices[item].toString()) / 3;
                                resetQuiz();
                            }
                        }
                );

                 choicesBuilder.create().show();
                break;

            case REGIONS_MENU_ID:
                final String[] regionNames = regionsMap.keySet().toArray(new String[regionsMap.size()]);

                boolean[] regionsEnabled = new boolean[regionsMap.size()];
                for (int i = 0; i < regionsEnabled.length; ++i)
                {
                    regionsEnabled[i] = regionsMap.get(regionNames[i]);
                }

                AlertDialog.Builder regionsBuilder =  new AlertDialog.Builder(getActivity());
                regionsBuilder.setTitle(R.string.flag_regions);

                String[] displayNames = new String[regionNames.length];
                for (int i = 0; i < regionNames.length; ++i)
                {
                    displayNames[i] = regionNames[i].replace('_', ' ');
                }
                regionsBuilder.setMultiChoiceItems(displayNames, regionsEnabled,
                        new DialogInterface.OnMultiChoiceClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which,
                                                boolean isChecked)
                            {
                                regionsMap.put(regionNames[which].toString(), isChecked);
                            }
                        }
                );

                regionsBuilder.setPositiveButton(R.string.flag_reset_quiz,
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int button)
                            {
                                resetQuiz();
                            }
                        }
                );
            regionsBuilder.create().show();
        }
    }




}
