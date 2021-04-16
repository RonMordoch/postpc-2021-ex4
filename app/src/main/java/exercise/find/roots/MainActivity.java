package exercise.find.roots;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity
{

    public static final String EXTRA_ROOT1 = "exercise.find.roots.root1",
            EXTRA_ROOT2 = "exercise.find.roots.root2",
            EXTRA_CALC_TIME = "exercise.find.roots.calculation_time",
            EXTRA_FAIL_TIME = "exercise.find.roots.time_until_give_up_seconds",
            EXTRA_NUM_ORIG = "exercise.find.roots.original_number",
            EXTRA_NUM_SERVICE = "exercise.find.roots.number_for_service", // due to exercise instructions
            EXTRA_SUCCESS = "exercise.find.roots.found_roots",
            EXTRA_FAIL = "exercise.find.roots.stopped_calculations",
            EXTRA_EDIT_NUM = "exercise.find.roots.edit_number",
            EXTRA_IS_WAIT = "exercise.find.roots.is_waiting_calc";

    private BroadcastReceiver broadcastReceiverForSuccess = null, broadcastReceiverForFail = null;
    boolean isWaitingCalc = false;
    ProgressBar progressBar;
    EditText editTextUserInput;
    Button buttonCalculateRoots;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progressBar);
        editTextUserInput = findViewById(R.id.editTextInputNumber);
        buttonCalculateRoots = findViewById(R.id.buttonCalculateRoots);

        // set initial UI:
        progressBar.setVisibility(View.GONE); // hide progress
        editTextUserInput.setText(""); // cleanup text in edit-text
        editTextUserInput.setEnabled(true); // set edit-text as enabled (user can input text)
        buttonCalculateRoots.setEnabled(false); // set button as disabled (user can't click)

        // set listener on the input written by the keyboard to the edit-text
        editTextUserInput.addTextChangedListener(new TextWatcher()
        {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) { // text did change
                setCalcButton();
            }
        });

        // set click-listener to the button
        buttonCalculateRoots.setOnClickListener(v -> {
            disableInput();
            Intent intentToOpenService = new Intent(MainActivity.this, CalculateRootsService.class);
            String userInputString = editTextUserInput.getText().toString();
            long userInputLong = Long.parseLong(userInputString);
            intentToOpenService.putExtra(EXTRA_NUM_SERVICE, userInputLong);
            startService(intentToOpenService);
        });

        // register a broadcast-receiver to handle action "found_roots"
        broadcastReceiverForSuccess = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent incomingIntent) {
                if (incomingIntent == null || !incomingIntent.getAction().equals(EXTRA_SUCCESS))
                    return;
                // success finding roots!
                enableInput();

                // create the new activity intent, pass the required info and open it
                Intent resultIntent = new Intent(MainActivity.this, ResultActivity.class);
                resultIntent.putExtra(EXTRA_NUM_ORIG, incomingIntent.getLongExtra(EXTRA_NUM_ORIG, 0));
                resultIntent.putExtra(EXTRA_ROOT1, incomingIntent.getLongExtra(EXTRA_ROOT1, 0));
                resultIntent.putExtra(EXTRA_ROOT2, incomingIntent.getLongExtra(EXTRA_ROOT2, 0));
                resultIntent.putExtra(EXTRA_CALC_TIME, incomingIntent.getLongExtra(EXTRA_CALC_TIME, 0));
                startActivity(resultIntent);
            }
        };
        registerReceiver(broadcastReceiverForSuccess, new IntentFilter(EXTRA_SUCCESS));

        // register a broadcast-receiver to handle action "stopped_calculations"
        broadcastReceiverForFail = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent == null || !intent.getAction().equals(EXTRA_FAIL))
                    return;
                // aborted calculations due to time limit
                enableInput();
                String toastMsg = "Calculation aborted after " + intent.getLongExtra(EXTRA_FAIL_TIME, 0) + " seconds";
                Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_SHORT).show();
            }
        };
        registerReceiver(broadcastReceiverForFail, new IntentFilter(EXTRA_FAIL));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiverForFail);
        unregisterReceiver(broadcastReceiverForSuccess);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean(EXTRA_IS_WAIT, isWaitingCalc);
        outState.putString(EXTRA_EDIT_NUM, editTextUserInput.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        isWaitingCalc = savedInstanceState.getBoolean(EXTRA_IS_WAIT);
        editTextUserInput.setText(savedInstanceState.getString(EXTRA_EDIT_NUM));
        super.onRestoreInstanceState(savedInstanceState);

    }

    private void setCalcButton() {
        String newText = editTextUserInput.getText().toString();
        try {
            long number = Long.parseLong(newText);
            // button is enabled if we are not waiting for result and we have valid input
            buttonCalculateRoots.setEnabled(number > 0 && !isWaitingCalc);
        } catch (NumberFormatException e) {
            buttonCalculateRoots.setEnabled(false);
            if (!newText.equals("")) { // don't bombard user with toasts when deleting previous input
                Toast.makeText(getApplicationContext(), "Please enter a positive integer", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void enableInput() {
        isWaitingCalc = false;
        progressBar.setVisibility(View.GONE);
        editTextUserInput.setEnabled(true);
        setCalcButton();
    }

    private void disableInput() {
        isWaitingCalc = true;
        progressBar.setVisibility(View.VISIBLE);
        editTextUserInput.setEnabled(false);
        buttonCalculateRoots.setEnabled(false);
    }
}


/*
the spec is:

upon launch, Activity starts out "clean":
* progress-bar is hidden
* "input" edit-text has no input and it is enabled
* "calculate roots" button is disabled

the button behavior is:
* when there is no valid-number as an input in the edit-text, button is disabled
* when we triggered a calculation and still didn't get any result, button is disabled
* otherwise (valid number && not calculating anything in the BG), button is enabled

the edit-text behavior is:
* when there is a calculation in the BG, edit-text is disabled (user can't input anything)
* otherwise (not calculating anything in the BG), edit-text is enabled (user can tap to open the keyboard and add input)

the progress behavior is:
* when there is a calculation in the BG, progress is showing
* otherwise (not calculating anything in the BG), progress is hidden

when "calculate roots" button is clicked:
* change states for the progress, edit-text and button as needed, so user can't interact with the screen

when calculation is complete successfully:
* change states for the progress, edit-text and button as needed, so the screen can accept new input
* open a new "success" screen showing the following data:
  - the original input number
  - 2 roots combining this number (e.g. if the input was 99 then you can show "99=9*11" or "99=3*33"
  - calculation time in seconds

when calculation is aborted as it took too much time:
* change states for the progress, edit-text and button as needed, so the screen can accept new input
* show a toast "calculation aborted after X seconds"


upon screen rotation (saveState && loadState) the new screen should show exactly the same state as the old screen. this means:
* edit-text shows the same input
* edit-text is disabled/enabled based on current "is waiting for calculation?" state
* progress is showing/hidden based on current "is waiting for calculation?" state
* button is enabled/disabled based on current "is waiting for calculation?" state && there is a valid number in the edit-text input


 */