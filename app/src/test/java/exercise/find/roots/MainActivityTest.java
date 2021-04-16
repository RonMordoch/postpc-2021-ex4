package exercise.find.roots;

import android.content.Intent;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.ParameterizedRobolectricTestRunner;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class MainActivityTest extends TestCase
{

    @Test
    public void when_activityIsLaunching_then_theButtonShouldStartDisabled() {
        // create a MainActivity and let it think it's currently displayed on the screen
        MainActivity mainActivity = Robolectric.buildActivity(MainActivity.class).create().visible().get();

        // test: make sure that the "calculate" button is disabled
        Button button = mainActivity.findViewById(R.id.buttonCalculateRoots);
        assertFalse(button.isEnabled());
    }

    @Test
    public void when_activityIsLaunching_then_theEditTextShouldStartEmpty() {
        // create a MainActivity and let it think it's currently displayed on the screen
        MainActivity mainActivity = Robolectric.buildActivity(MainActivity.class).create().visible().get();

        // test: make sure that the "input" edit-text has no text
        EditText inputEditText = mainActivity.findViewById(R.id.editTextInputNumber);
        String input = inputEditText.getText().toString();
        assertTrue(input == null || input.isEmpty());
    }

    @Test
    public void when_userIsEnteringNumberInput_and_noCalculationAlreadyHappned_then_theButtonShouldBeEnabled() {
        // create a MainActivity and let it think it's currently displayed on the screen
        MainActivity mainActivity = Robolectric.buildActivity(MainActivity.class).create().visible().get();

        // find the edit-text and the button
        EditText inputEditText = mainActivity.findViewById(R.id.editTextInputNumber);
        Button button = mainActivity.findViewById(R.id.buttonCalculateRoots);

        // test: insert input to the edit text and verify that the button is enabled
        inputEditText.setText("57");
        assertTrue(button.isEnabled());
    }

    @Test
    public void testPostClickActions() {
        MainActivity mainActivity = Robolectric.buildActivity(MainActivity.class).create().visible().get();
        EditText inputEditText = mainActivity.findViewById(R.id.editTextInputNumber);
        Button button = mainActivity.findViewById(R.id.buttonCalculateRoots);
        ProgressBar progressBar = mainActivity.findViewById(R.id.progressBar);
        inputEditText.setText("50");
        button.performClick();
        assertFalse(button.isEnabled()); // test button is disabled after click
        assertTrue(progressBar.isEnabled()); // progress bar is enabled
        assertFalse(inputEditText.isEnabled()); // disable input
    }

    @Test
    public void testInvalidInput_buttonShouldBeDisabled() {
        MainActivity mainActivity = Robolectric.buildActivity(MainActivity.class).create().visible().get();
        EditText inputEditText = mainActivity.findViewById(R.id.editTextInputNumber);
        Button button = mainActivity.findViewById(R.id.buttonCalculateRoots);
        inputEditText.setText("hi");
        assertFalse(button.isEnabled());
        inputEditText.setText("5.5");
        assertFalse(button.isEnabled());
        inputEditText.setText("5.0");
        assertFalse(button.isEnabled());
        inputEditText.setText("-7");
        assertFalse(button.isEnabled());
        inputEditText.setText("!@#$");
        assertFalse(button.isEnabled());
    }

    @Test
    public void testButton_on_inputInsert_and_inputDelete() {
        MainActivity mainActivity = Robolectric.buildActivity(MainActivity.class).create().visible().get();
        EditText inputEditText = mainActivity.findViewById(R.id.editTextInputNumber);
        Button button = mainActivity.findViewById(R.id.buttonCalculateRoots);
        inputEditText.setText("4");
        assertTrue(button.isEnabled());
        inputEditText.setText("4A");
        assertFalse(button.isEnabled());
    }

    @Test
    public void test_broadCast_intents_withAction_stopped_calculations() {
        //  when starting a calculation and than activity receives
        //  "stopped_calculations" broadcast, the button should be unlocked (enabled),
        //  "progress" should disappear
        MainActivity mainActivity = Robolectric.buildActivity(MainActivity.class).create().visible().get();
        Button button = mainActivity.findViewById(R.id.buttonCalculateRoots);
        ProgressBar progressBar = mainActivity.findViewById(R.id.progressBar);
        EditText inputEditText = mainActivity.findViewById(R.id.editTextInputNumber);
        inputEditText.setText("2305843009213693951");
        Intent broadcast = new Intent("stopped_calculations");
        RuntimeEnvironment.application.sendBroadcast(broadcast);
        Shadows.shadowOf(Looper.getMainLooper()).idle();
        assertEquals(View.GONE, progressBar.getVisibility());
        assertTrue(button.isEnabled());
    }

}