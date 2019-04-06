package yolo.tbv.vancomycin;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.SubscriptSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public final class AUC extends AppCompatActivity {
    // the scrollview
    private ScrollView scrollView;

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

    // the calculation result ConstraintLayout
    private ConstraintLayout AucCalculationResult;
    private ConstraintLayout EstimatedAucResult;

    // Date and time picker fragments
    DatePickerFragment precedingDoseDateFragment;
    TimePickerFragment precedingDoseTimeFragment;

    DatePickerFragment levelOneDateFragment;
    TimePickerFragment levelOneTimeFragment;

    DatePickerFragment levelTwoDateFragment;
    TimePickerFragment levelTwoTimeFragment;

    // lists of UI elements
    List<EditText> estimateAucTextList;
    List<EditText> reviseAucTextList;
    List<EditText> suggestDoseTextList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auc);

        this.setAUC24Subscript();

        // initialize class variables with their corresponding UI elements
        this.scrollView = findViewById(R.id.AUC_ScrollView);
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
        this.AucCalculationResult = findViewById(R.id.AUC_calculation_results);
        this.EstimatedAucResult = findViewById(R.id.AUC_estimated_AUC_result);

        // instantiate date and time pickers
        Bundle precedingDoseDateBundle = new Bundle();
        precedingDoseDateBundle.putInt("viewId", R.id.preceeding_dose_date_button);
        this.precedingDoseDateFragment = new DatePickerFragment();
        this.precedingDoseDateFragment.setArguments(precedingDoseDateBundle);
        buttonTextSetter(R.id.preceeding_dose_date_button, this.precedingDoseDateFragment.getDate());

        Bundle precedingDoseTimeBundle = new Bundle();
        precedingDoseTimeBundle.putInt("viewId", R.id.preceeding_dose_time_button);
        this.precedingDoseTimeFragment = new TimePickerFragment();
        this.precedingDoseTimeFragment.setArguments(precedingDoseTimeBundle);

        Bundle levelOneDateBundle = new Bundle();
        levelOneDateBundle.putInt("viewId", R.id.level_1_date_button);
        this.levelOneDateFragment = new DatePickerFragment();
        this.levelOneDateFragment.setArguments(levelOneDateBundle);
        buttonTextSetter(R.id.level_1_date_button, this.levelOneDateFragment.getDate());

        Bundle levelOneTimeBundle = new Bundle();
        levelOneTimeBundle.putInt("viewId", R.id.level_1_time_button);
        this.levelOneTimeFragment = new TimePickerFragment();
        this.levelOneTimeFragment.setArguments(levelOneTimeBundle);

        Bundle levelTwoDateBundle = new Bundle();
        levelTwoDateBundle.putInt("viewId", R.id.level_2_date_button);
        this.levelTwoDateFragment = new DatePickerFragment();
        this.levelTwoDateFragment.setArguments(levelTwoDateBundle);
        buttonTextSetter(R.id.level_2_date_button, this.levelTwoDateFragment.getDate());

        Bundle levelTwoTimeBundle = new Bundle();
        levelTwoTimeBundle.putInt("viewId", R.id.level_2_time_button);
        this.levelTwoTimeFragment = new TimePickerFragment();
        this.levelTwoTimeFragment.setArguments(levelTwoTimeBundle);

        // create UI element lists
        this.estimateAucTextList = Arrays.asList(
                this.initialDoseInput,
                this.initialDoseFreqInput,
                this.initialInfusionDurationInput,
                this.measuredPeakInput,
                this.measuredTroughInput
        );

        this.reviseAucTextList = Arrays.asList(
                this.chosenDoseRevisionInput,
                this.chosenDoseIntervalRevisionInput,
                this.chosenDoseInfusionDurationRevisionInput
        );

        this.suggestDoseTextList = Arrays.asList(
                this.goalAuc24Input
        );

        // attach event handlers to hide calculation results so users never see out-of-date values
        for (int i = 0; i < this.estimateAucTextList.size(); i++) {
            this.estimateAucTextList.get(i).setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b) {
                        AucCalculationResult.setVisibility(View.GONE);
                        EstimatedAucResult.setVisibility(View.GONE);
                    }
                }
            });

            this.estimateAucTextList.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AucCalculationResult.setVisibility(View.GONE);
                    EstimatedAucResult.setVisibility(View.GONE);
                }
            });
        }

        for (int i = 0; i < this.reviseAucTextList.size(); i++) {
            this.reviseAucTextList.get(i).setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b) {
                        AucCalculationResult.setVisibility(View.GONE);
                    }
                }
            });

            this.reviseAucTextList.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AucCalculationResult.setVisibility(View.GONE);
                }
            });
        }
    }

    private void buttonTextSetter(int buttonId, String textToSet) {
        android.widget.Button button = findViewById(buttonId);
        button.setText(textToSet);
    }

    private boolean validateEditTextList(List<EditText> textList) {
        boolean inputIsValid = true;
        boolean doNotScroll = false;

        // text box validation
        for (int i = 0; i < textList.size(); i++) {
            String inputString = textList.get(i).getText().toString();
            if (inputString.length() == 0) {
                textList.get(i).setHintTextColor(Color.RED);
                inputIsValid = false;
            } else {
                try {
                    Double.parseDouble(inputString);
                } catch (NumberFormatException e) {
                    textList.get(i).setText("");
                    textList.get(i).setHintTextColor(Color.RED);
                    inputIsValid = false;
                }
            }

            if (!inputIsValid && !doNotScroll) {
                this.focusOnView(textList.get(i), scrollView);
                doNotScroll = true;
            }
        }

        return inputIsValid;
    }

    private void focusOnView(final View view, final ScrollView scrollView){
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                int[] coordinates = new int[2];
                int halfScreenHeight = Resources.getSystem().getDisplayMetrics().heightPixels / 2;

                view.getLocationOnScreen(coordinates);
                scrollView.smoothScrollTo(
                        0,
                        coordinates[1] + scrollView.getScrollY() - halfScreenHeight
                );
            }
        });
    }

    private boolean validateDateTimeOrdering(boolean scroll) {
        boolean inputIsValid = true;
        boolean hasScrolled = scroll;

        // date objects
        LocalDateTime precedingDoseDateTime = createLocalDateTimeObject(
                this.precedingDoseDateFragment,
                this.precedingDoseTimeFragment
        );

        LocalDateTime levelOneDateTime = createLocalDateTimeObject(
                this.levelOneDateFragment,
                this.levelOneTimeFragment
        );

        LocalDateTime levelTwoDateTime = createLocalDateTimeObject(
                this.levelTwoDateFragment,
                this.levelTwoTimeFragment
        );

        levelOneDateButton.setTextColor(Color.BLACK);
        levelOneTimeButton.setTextColor(Color.BLACK);
        levelTwoDateButton.setTextColor(Color.BLACK);
        levelTwoTimeButton.setTextColor(Color.BLACK);

        if (!precedingDoseTimeFragment.userDidChooseTime()) {
            inputIsValid = false;
            precedingDoseTimeButton.setTextColor(Color.RED);

            if (!hasScrolled) {
                hasScrolled = true;
                focusOnView(precedingDoseTimeButton, scrollView);
            }
        }

        if (precedingDoseDateTime.isAfter(levelOneDateTime) || precedingDoseDateTime.isEqual(levelOneDateTime)) {
            inputIsValid = false;

            // only make date button red if the user chose a time
            if (levelOneTimeFragment.userDidChooseTime()) {
                levelOneDateButton.setTextColor(Color.RED);
            }

            levelOneTimeButton.setTextColor(Color.RED);

            if (!hasScrolled) {
                hasScrolled = true;
                focusOnView(levelOneTimeButton, scrollView);
            }
        }

        if (levelOneDateTime.isAfter(levelTwoDateTime) || levelOneDateTime.isEqual(levelTwoDateTime)) {
            inputIsValid = false;

            // only make date button red if the user chose a time
            if (levelTwoTimeFragment.userDidChooseTime()) {
                levelTwoDateButton.setTextColor(Color.RED);
            }

            levelTwoTimeButton.setTextColor(Color.RED);

            if (!hasScrolled) {
                hasScrolled = true;
                focusOnView(levelTwoTimeButton, scrollView);
            }
        }

        if (precedingDoseDateTime.isAfter(levelTwoDateTime) || precedingDoseDateTime.isEqual(levelTwoDateTime)) {
            inputIsValid = false;

            // only make date button red if the user chose a time
            if (levelTwoTimeFragment.userDidChooseTime()) {
                levelTwoDateButton.setTextColor(Color.RED);
            }

            levelTwoTimeButton.setTextColor(Color.RED);

            if (!hasScrolled) {
                focusOnView(levelTwoTimeButton, scrollView);
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

        for (int i = 0; i < this.estimateAucTextList.size(); i++) {
            this.estimateAucTextList.get(i).setHintTextColor(Color.GRAY);
        }

        for (int i = 0; i < this.reviseAucTextList.size(); i++) {
            this.reviseAucTextList.get(i).setHintTextColor(Color.GRAY);
        }

        for (int i = 0; i < pickerButtons.size(); i++) {
            pickerButtons.get(i).setTextColor(Color.BLACK);
        }
    }

    private LocalDateTime createLocalDateTimeObject(DatePickerFragment datePickerFragment, TimePickerFragment timePickerFragment) {
        return LocalDateTime.of(
                datePickerFragment.getChosenYear(),
                datePickerFragment.getChosenMonth() + 1,
                datePickerFragment.getChosenDay(),
                timePickerFragment.getChosenHour(),
                timePickerFragment.getChosenMinute()
        );
    }

    public void calculateAucEstimate(View view) {
        calculateAuc(false, false);
    }

    public void calculateAucRevision(View view) {
        calculateAuc(true, false);
    }

    public void suggestRevisedDose(View view) {
        calculateAuc(false, true);
    }

    // calculateRevision true means also calculate AUC revision in addition to AUC estimate
    private void calculateAuc(boolean calculateRevision, boolean suggestDose) {
        boolean inputsValid = true;
        boolean scroll = false;

        // clear red from all input fields and buttons
        this.resetHints();

        // check that values entered in EditTexts are valid
        if (!calculateRevision && !suggestDose) {
            if (!this.validateEditTextList(this.estimateAucTextList)) {
                inputsValid = false;
                scroll = true;
            }
        } else if (!calculateRevision && suggestDose) {
            List<EditText> newList = new ArrayList<>();
            newList.addAll(estimateAucTextList);
            newList.addAll(suggestDoseTextList);

            if (!this.validateEditTextList(newList)) {
                inputsValid = false;
                scroll = true;
            }
        } else if (calculateRevision && !suggestDose) {
            List<EditText> newList = new ArrayList<>();
            newList.addAll(estimateAucTextList);
            newList.addAll(reviseAucTextList);

            if (!this.validateEditTextList(newList)) {
                inputsValid = false;
                scroll = true;
            }
        }

        // check that provided DateTimes are valid
        if (!this.validateDateTimeOrdering(scroll)) {
            inputsValid = false;
        }

        if (!inputsValid) {
            System.out.println("Inputs are NOT valid");
            return;
        }

        System.out.println("Inputs are valid");

        // grab values from user input
        double initialDose = Double.parseDouble(this.initialDoseInput.getText().toString());
        double initialDoseFreq = Double.parseDouble(this.initialDoseFreqInput.getText().toString());
        double initialInfusionDuration = Double.parseDouble(this.initialInfusionDurationInput.getText().toString());
        double measuredPeak = Double.parseDouble(this.measuredPeakInput.getText().toString());
        double measuredTrough = Double.parseDouble(this.measuredTroughInput.getText().toString());
        double goalAuc24 = -1;
        double chosenDoseRevision = -1;
        double chosenDoseIntervalRevision = -1;
        double chosenDoseInfusionDurationRevision = -1;

        // date objects
        LocalDateTime precedingDoseDateTime = createLocalDateTimeObject(
                this.precedingDoseDateFragment,
                this.precedingDoseTimeFragment
        );

        LocalDateTime levelOneDateTime = createLocalDateTimeObject(
                this.levelOneDateFragment,
                this.levelOneTimeFragment
        );

        LocalDateTime levelTwoDateTime = createLocalDateTimeObject(
                this.levelTwoDateFragment,
                this.levelTwoTimeFragment
        );

        double hoursBetweenDoseAndLevelOne = AUCCalculator.hoursBetween(precedingDoseDateTime, levelOneDateTime);
        double hoursBetweenLevelOneAndLevelTwo = AUCCalculator.hoursBetween(levelOneDateTime, levelTwoDateTime);

        // finally, we calculate values that we'll output to the user, starting with AUC estimates
        double ke = AUCCalculator.calculateKe(measuredPeak, measuredTrough, hoursBetweenLevelOneAndLevelTwo);
        double truePeak = AUCCalculator.calculateTruePeak(measuredPeak, ke, hoursBetweenDoseAndLevelOne, initialInfusionDuration);
        double trueTrough = AUCCalculator.calculateTrueTrough(truePeak, ke, initialDoseFreq, initialInfusionDuration);
        double halfLife = InitialDoseCalculator.calculateHL(ke); // yes this is intentional
        double vd = AUCCalculator.calculateVd(initialDose, initialInfusionDuration, ke, truePeak, trueTrough);
        double auc24 = AUCCalculator.calculateAUC24(truePeak, trueTrough, initialInfusionDuration, ke, initialDoseFreq);

        // next, calculate dose revision values
        double suggestedT = -1;
        double recRevisedDose = -1;
        double predictedPeak = -1;
        double predictedTrough = -1;
        double predictedAuc24 = -1;

        if (suggestDose) {
            goalAuc24 = Double.parseDouble(this.goalAuc24Input.getText().toString());

            suggestedT = AUCCalculator.calculateSuggestedT(halfLife);
            recRevisedDose = AUCCalculator.calculateVancoDose(goalAuc24, auc24, initialDose, initialDoseFreq, suggestedT);

            this.insertSuggestedDoseValues(suggestedT, recRevisedDose);
        }

        if (calculateRevision) {
            chosenDoseRevision = Double.parseDouble(this.chosenDoseRevisionInput.getText().toString());
            chosenDoseIntervalRevision = Double.parseDouble(this.chosenDoseIntervalRevisionInput.getText().toString());
            chosenDoseInfusionDurationRevision = Double.parseDouble(this.chosenDoseInfusionDurationRevisionInput.getText().toString());

            predictedPeak = AUCCalculator.calculatePredictedPeak(vd, chosenDoseRevision, chosenDoseInfusionDurationRevision, ke, chosenDoseIntervalRevision);
            predictedTrough = AUCCalculator.calculatePredictedTrough(predictedPeak, ke, chosenDoseIntervalRevision, chosenDoseInfusionDurationRevision);
            predictedAuc24 = AUCCalculator.calculatePredictedAuc24(predictedPeak, predictedTrough, chosenDoseInfusionDurationRevision, ke, chosenDoseIntervalRevision);

            // show revised dose values
            this.displayRevisedDoseValues(predictedAuc24, predictedPeak, predictedTrough);
            AucCalculationResult.setVisibility(View.VISIBLE);
        }

        // show calculated AUC estimated values
        this.displayAUCEstimatedValues(ke, truePeak, trueTrough, halfLife, vd, auc24);

        // show calculations
        EstimatedAucResult.setVisibility(View.VISIBLE);
    }

    private void insertSuggestedDoseValues(double suggestedT, double recRevisedDose) {
        String chosenDoseIntervalRevision = String.format(Locale.getDefault(), "%.0f", suggestedT);
        String chosenDoseRevision = String.format(Locale.getDefault(), "%.0f", recRevisedDose);

        chosenDoseIntervalRevisionInput.setText(chosenDoseIntervalRevision);
        chosenDoseRevisionInput.setText(chosenDoseRevision);
    }

    private void displayAUCEstimatedValues(double ke, double peak, double trough, double hl, double vd, double auc24) {
        android.widget.TextView keResult = findViewById(R.id.AUC_ke_result);
        android.widget.TextView peakResult = findViewById(R.id.AUC_peak_result);
        android.widget.TextView troughResult = findViewById(R.id.AUC_trough_result);
        android.widget.TextView hlResult = findViewById(R.id.AUC_hl_result);
        android.widget.TextView vdResult = findViewById(R.id.AUC_VD_result);
        android.widget.TextView aucResult = findViewById(R.id.AUC_computed_AUC_result);

        keResult.setText(String.format(Locale.getDefault(),"%.5f", ke));
        peakResult.setText(String.format(Locale.getDefault(),"%.1f", peak));
        troughResult.setText(String.format(Locale.getDefault(),"%.1f", trough));
        hlResult.setText(String.format(Locale.getDefault(),"%.2f", hl));
        vdResult.setText(String.format(Locale.getDefault(),"%.2f", vd));
        aucResult.setText(String.format(Locale.getDefault(),"%.0f", auc24));
    }

    private void displayRevisedDoseValues(double revisedAuc24, double revisedPeak, double revisedTrough) {
        android.widget.TextView revisedDoseAUC24Result = findViewById(R.id.AUC_RD_predicted_AUC_result);
        android.widget.TextView revisedDoseTroughResult = findViewById(R.id.AUC_RD_trough_result);
        android.widget.TextView revisedDosePeakResult = findViewById(R.id.AUC_RD_peak_result);

        revisedDoseAUC24Result.setText(String.format(Locale.getDefault(),"%.0f", revisedAuc24));
        revisedDoseTroughResult.setText(String.format(Locale.getDefault(),"%.1f", revisedTrough));
        revisedDosePeakResult.setText(String.format(Locale.getDefault(),"%.1f", revisedPeak));
    }

    // used by TimePickerFragment class
    public void dateTimeHintResetHelper() {
        if (
                this.precedingDoseTimeFragment.userDidChooseTime()
                && this.levelOneTimeFragment.userDidChooseTime()
                && this.levelTwoTimeFragment.userDidChooseTime()
        ) {
            validateDateTimeOrdering(false);
        }
    }

    // these functions are the onClick handlers...
    public void pickPrecedingDoseDate(View v) {
        AucCalculationResult.setVisibility(View.GONE);
        precedingDoseDateButton.setTextColor(Color.BLACK);
        this.precedingDoseDateFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void pickPrecedingDoseTime(View v) {
        AucCalculationResult.setVisibility(View.GONE);
        precedingDoseTimeButton.setTextColor(Color.BLACK);
        this.precedingDoseTimeFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void pickLevelOneDate(View v) {
        AucCalculationResult.setVisibility(View.GONE);
        levelOneDateButton.setTextColor(Color.BLACK);
        this.levelOneDateFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void pickLevelOneTime(View v) {
        AucCalculationResult.setVisibility(View.GONE);
        levelOneTimeButton.setTextColor(Color.BLACK);
        this.levelOneTimeFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void pickLevelTwoDate(View v) {
        AucCalculationResult.setVisibility(View.GONE);
        levelTwoDateButton.setTextColor(Color.BLACK);
        this.levelTwoDateFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void pickLevelTwoTime(View v) {
        AucCalculationResult.setVisibility(View.GONE);
        levelTwoTimeButton.setTextColor(Color.BLACK);
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