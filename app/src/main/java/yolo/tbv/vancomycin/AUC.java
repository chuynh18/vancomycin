package yolo.tbv.vancomycin;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.SubscriptSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class AUC extends AppCompatActivity {
    // Variables that will hold onto various input fields
    private android.widget.EditText initialDoseInput;
    private android.widget.EditText initialDoseFreqInput;
    private android.widget.EditText initialInfusionDurationInput;
    private android.widget.EditText measuredPeakInput;
    private android.widget.EditText measuredTroughInput;
    private android.widget.EditText goalAuc24Input;
    private android.widget.EditText chosenDoseRevisionInput;
    private android.widget.EditText chosenDoseIntervalRevisionInput;
    private android.widget.EditText chosenDoseInfusionDurationRevisionInput;

    // Variables that will hold onto date and time picker buttons
    private android.widget.Button precedingDoseDateButton;
    private android.widget.Button precedingDoseTimeButton;
    private android.widget.Button levelOneDateButton;
    private android.widget.Button levelOneTimeButton;
    private android.widget.Button levelTwoDateButton;
    private android.widget.Button levelTwoTimeButton;

    // Date and time picker fragments
    DatePickerFragment precedingDoseDateFragment;
    TimePickerFragment precedingDoseTimeFragment;

    DatePickerFragment levelOneDateFragment;
    TimePickerFragment levelOneTimeFragment;

    DatePickerFragment levelTwoDateFragment;
    TimePickerFragment levelTwoTimeFragment;

    // lists of UI elements
    List<EditText> editTextList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auc);

        this.setAUC24Subscript();

        // initialize class variables with their corresponding UI elements
        this.initialDoseInput = findViewById(R.id.AUC_initial_dose_input);
        this.initialDoseFreqInput = findViewById(R.id.AUC_initial_dose_freq_input);
        this.initialInfusionDurationInput = findViewById(R.id.AUC_initial_infusion_duration_input);
        this.measuredPeakInput = findViewById(R.id.AUC_measured_peak_input);
        this.measuredTroughInput = findViewById(R.id.AUC_measured_trough_input);
        this.goalAuc24Input = findViewById(R.id.AUC_goal_AUC_input);
        this.chosenDoseRevisionInput = findViewById(R.id.AUC_revision_chosen_dose_input);
        this.chosenDoseIntervalRevisionInput = findViewById(R.id.AUC_revision_dosing_interval_input);
        this.chosenDoseInfusionDurationRevisionInput = findViewById(R.id.AUC_revision_infusion_duration_input);
        this.precedingDoseDateButton = findViewById(R.id.preceeding_dose_date_button);
        this.precedingDoseTimeButton = findViewById(R.id.preceeding_dose_time_button);
        this.levelOneDateButton = findViewById(R.id.level_1_date_button);
        this.levelOneTimeButton = findViewById(R.id.level_1_time_button);
        this.levelTwoDateButton = findViewById(R.id.level_2_date_button);
        this.levelTwoTimeButton = findViewById(R.id.level_2_time_button);

        // instantiate date and time pickers
        Bundle precedingDoseDateBundle = new Bundle();
        precedingDoseDateBundle.putInt("viewId", R.id.preceeding_dose_date_button);
        this.precedingDoseDateFragment = new DatePickerFragment();
        this.precedingDoseDateFragment.setArguments(precedingDoseDateBundle);

        Bundle precedingDoseTimeBundle = new Bundle();
        precedingDoseTimeBundle.putInt("viewId", R.id.preceeding_dose_time_button);
        this.precedingDoseTimeFragment = new TimePickerFragment();
        this.precedingDoseTimeFragment.setArguments(precedingDoseTimeBundle);

        Bundle levelOneDateBundle = new Bundle();
        levelOneDateBundle.putInt("viewId", R.id.level_1_date_button);
        this.levelOneDateFragment = new DatePickerFragment();
        this.levelOneDateFragment.setArguments(levelOneDateBundle);

        Bundle levelOneTimeBundle = new Bundle();
        levelOneTimeBundle.putInt("viewId", R.id.level_1_time_button);
        this.levelOneTimeFragment = new TimePickerFragment();
        this.levelOneTimeFragment.setArguments(levelOneTimeBundle);

        Bundle levelTwoDateBundle = new Bundle();
        levelTwoDateBundle.putInt("viewId", R.id.level_2_date_button);
        this.levelTwoDateFragment = new DatePickerFragment();
        this.levelTwoDateFragment.setArguments(levelTwoDateBundle);

        Bundle levelTwoTimeBundle = new Bundle();
        levelTwoTimeBundle.putInt("viewId", R.id.level_2_time_button);
        this.levelTwoTimeFragment = new TimePickerFragment();
        this.levelTwoTimeFragment.setArguments(levelTwoTimeBundle);

        // create UI element lists
        this.editTextList = Arrays.asList(
            this.initialDoseInput,
            this.initialDoseFreqInput,
            this.initialInfusionDurationInput,
            this.measuredPeakInput,
            this.measuredTroughInput,
            this.goalAuc24Input,
            this.chosenDoseRevisionInput,
            this.chosenDoseIntervalRevisionInput,
            this.chosenDoseInfusionDurationRevisionInput
        );
    }

    // validates user input; warns user if any inputs are missing
    private boolean validateUserInput() {
        boolean inputIsValid = true;

        // text box validation
        for (int i = 0; i < this.editTextList.size(); i++) {
            String inputString = this.editTextList.get(i).getText().toString();
            if (inputString.length() == 0) {
                this.editTextList.get(i).setHintTextColor(Color.RED);
                inputIsValid = false;
            }
        }

        // date and time picker validation
        List<DatePickerFragment> datePickerFragments = Arrays.asList(
            this.precedingDoseDateFragment,
            this.levelOneDateFragment,
            this.levelTwoDateFragment
        );

        List<TimePickerFragment> timePickerFragments = Arrays.asList(
            this.precedingDoseTimeFragment,
            this.levelOneTimeFragment,
            this.levelTwoTimeFragment
        );

        List<android.widget.Button> datePickerButtons = Arrays.asList(
            this.precedingDoseDateButton,
            this.levelOneDateButton,
            this.levelTwoDateButton
        );

        List<android.widget.Button> timePickerButtons = Arrays.asList(
            this.precedingDoseTimeButton,
            this.levelOneTimeButton,
            this.levelTwoTimeButton
        );

        for (int i = 0; i < datePickerFragments.size(); i++) {
            if (!datePickerFragments.get(i).userDidChooseDate()) {
                inputIsValid = false;
                datePickerButtons.get(i).setTextColor(Color.RED);
            }
        }

        for (int i = 0; i < timePickerFragments.size(); i++) {
            if (!timePickerFragments.get(i).userDidChooseTime()) {
                inputIsValid = false;
                timePickerButtons.get(i).setTextColor(Color.RED);
            }
        }

        return inputIsValid;
    }

    // helper method to reset hint color to gray
    private void resetHints() {
        List<android.widget.Button> pickerButtons = Arrays.asList(
            this.precedingDoseDateButton,
            this.levelOneDateButton,
            this.levelTwoDateButton,
            this.precedingDoseTimeButton,
            this.levelOneTimeButton,
            this.levelTwoTimeButton
        );

        for (int i = 0; i < this.editTextList.size(); i++) {
            this.editTextList.get(i).setHintTextColor(Color.GRAY);
        }

        for (int i = 0; i < pickerButtons.size(); i++) {
            pickerButtons.get(i).setTextColor(Color.BLACK);
        }
    }

    public void calculateAucDose(View view) {
        // clear red from all input fields and buttons
        this.resetHints();

        // halt execution of method if any user input is invalid
        if (!this.validateUserInput()) {
            return;
        }

        // grab values from user input
        double initialDose = Double.parseDouble(this.initialDoseInput.getText().toString());
        double initialDoseFreq = Double.parseDouble(this.initialDoseFreqInput.getText().toString());
        double initialInfusionDuration = Double.parseDouble(this.initialInfusionDurationInput.getText().toString());
        double measuredPeak = Double.parseDouble(this.measuredPeakInput.getText().toString());
        double measuredTrough = Double.parseDouble(this.measuredTroughInput.getText().toString());
        double goalAuc24 = Double.parseDouble(this.goalAuc24Input.getText().toString());
        double chosenDoseRevision = Double.parseDouble(this.chosenDoseRevisionInput.getText().toString());
        double chosenDoseIntervalRevision = Double.parseDouble(this.chosenDoseIntervalRevisionInput.getText().toString());
        double chosenDoseInfusionDurationRevision = Double.parseDouble(this.chosenDoseInfusionDurationRevisionInput.getText().toString());

        // date objects
        LocalDateTime precedingDoseDateTime = LocalDateTime.of(
            this.precedingDoseDateFragment.getChosenYear(),
            this.precedingDoseDateFragment.getChosenMonth(),
            this.precedingDoseDateFragment.getChosenDay(),
            this.precedingDoseTimeFragment.getChosenHour(),
            this.precedingDoseTimeFragment.getChosenMinute()
        );

        LocalDateTime levelOneDateTime = LocalDateTime.of(
            this.levelOneDateFragment.getChosenYear(),
            this.levelOneDateFragment.getChosenMonth(),
            this.levelOneDateFragment.getChosenDay(),
            this.levelOneTimeFragment.getChosenHour(),
            this.levelOneTimeFragment.getChosenMinute()
        );

        LocalDateTime levelTwoDateTime = LocalDateTime.of(
            this.levelTwoDateFragment.getChosenYear(),
            this.levelTwoDateFragment.getChosenMonth(),
            this.levelTwoDateFragment.getChosenDay(),
            this.levelTwoTimeFragment.getChosenHour(),
            this.levelTwoTimeFragment.getChosenMinute()
        );

        double hoursBetweenDoseAndLevelOne = AUCCalculator.hoursBetween(precedingDoseDateTime, levelOneDateTime);
        double hoursBetweenLevelOneAndLevelTwo = AUCCalculator.hoursBetween(levelOneDateTime, levelTwoDateTime);
        double hoursBetweenDoseAndLevelTwo = hoursBetweenDoseAndLevelOne + hoursBetweenLevelOneAndLevelTwo;

        // finally, we actually calculate values that we'll output to the user
        double ke = AUCCalculator.calculateKe(measuredPeak, measuredTrough, hoursBetweenLevelOneAndLevelTwo);
        double truePeak = AUCCalculator.calculateTruePeak(measuredPeak, ke, hoursBetweenDoseAndLevelOne, initialInfusionDuration);
        double trueTrough = AUCCalculator.calculateTrueTrough(truePeak, ke, initialDoseFreq, initialInfusionDuration);
        double halfLife = InitialDoseCalculator.calculateHL(ke); // yes this is intentional

        System.out.println("ke " + ke);
        System.out.println("truePeak " + truePeak);
        System.out.println("trueTrough " + trueTrough);
        System.out.println("half-life " + halfLife);
    }

    // these functions are the onClick handlers...
    public void pickPrecedingDoseDate(View v) {
        this.precedingDoseDateButton.setTextColor(Color.BLACK);
        this.precedingDoseDateFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void pickPrecedingDoseTime(View v) {
        this.precedingDoseTimeButton.setTextColor(Color.BLACK);
        this.precedingDoseTimeFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void pickLevelOneDate(View v) {
        this.levelOneDateButton.setTextColor(Color.BLACK);
        this.levelOneDateFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void pickLevelOneTime(View v) {
        this.levelOneTimeButton.setTextColor(Color.BLACK);
        this.levelOneTimeFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void pickLevelTwoDate(View v) {
        this.levelTwoDateButton.setTextColor(Color.BLACK);
        this.levelTwoDateFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void pickLevelTwoTime(View v) {
        this.levelTwoTimeButton.setTextColor(Color.BLACK);
        this.levelTwoTimeFragment.show(getSupportFragmentManager(), "timePicker");
    }
    // end onClick handlers

    // All this method does is make the "24" in "AUC24" subscript.  That's it.  Really.
    private void setAUC24Subscript() {
        android.widget.TextView auc24 = findViewById(R.id.AUC_goal_auc24);
        SpannableStringBuilder aucSB = new SpannableStringBuilder(getString(R.string.chosen_goal_auc24));
        aucSB.setSpan(new SubscriptSpan(), 15, 17, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        auc24.setText(aucSB, TextView.BufferType.SPANNABLE);

        android.widget.TextView calcAuc = findViewById(R.id.AUC_calculated_AUC_label);
        SpannableStringBuilder calcAucSB = new SpannableStringBuilder(getString(R.string.AUC_calculated_auc24));
        calcAucSB.setSpan(new SubscriptSpan(), 14, 16, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        calcAuc.setText(calcAucSB, TextView.BufferType.SPANNABLE);
    }
}