package yolo.tbv.vancomycin;

import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.SubscriptSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public final class InitialDose extends AppCompatActivity {
    // Variables that will hold onto various input fields
    private android.widget.EditText AUCInput;
    private android.widget.EditText CrClInput;
    private android.widget.EditText WeightInput;
    private android.widget.EditText AgeInput;
    private android.widget.EditText SCrInput;
    private android.widget.Spinner SexInput;
    private android.widget.CheckBox ObeseInput;
    private android.widget.CheckBox CNS_Input;
    private android.widget.CheckBox Manual_CrCl;
    private ConstraintLayout DosingView;

    // holds onto original value of AUC24, used by CNS_Input.setOnClickListener() in onCreate()
    String CNSOriginalValue;

    // holds onto user-provided CrCl value
    String userInputtedCrCl = "";

    // All this method does is make the "24" in "AUC24" subscript.  That's it.  Really.
    private void setAUC24Subscript() {
        android.widget.TextView auc24 = findViewById(R.id.ID_AUC);
        SpannableStringBuilder aucSB = new SpannableStringBuilder(getString(R.string.chosen_goal_auc24));
        aucSB.setSpan(new SubscriptSpan(), 15, 17, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        auc24.setText(aucSB, TextView.BufferType.SPANNABLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_dose);

        this.setAUC24Subscript();

        // initialize class variables with their corresponding UI elements
        this.AUCInput = findViewById(R.id.ID_AUC_Input);
        this.CrClInput = findViewById(R.id.ID_CrCl_Input);
        this.WeightInput = findViewById(R.id.ID_BodyWeight_Input);
        this.AgeInput = findViewById(R.id.ID_Age_Input);
        this.SCrInput = findViewById(R.id.ID_SCr_Input);
        this.SexInput = findViewById(R.id.ID_Sex_Input);
        this.ObeseInput = findViewById(R.id.ID_Obese_Input);
        this.CNS_Input = findViewById(R.id.ID_CNS_Input);
        this.DosingView = findViewById(R.id.ID_dosing_result);
        this.Manual_CrCl = findViewById(R.id.ID_manual_CrCl_Input);

        // set Event Listeners on input fields to hide dosage info (so user never sees out of date dosage)
        List<android.widget.EditText> inputs = Arrays.asList(AUCInput, WeightInput, AgeInput, SCrInput, CrClInput);

        for (int i = 0; i < inputs.size(); i++) {
            inputs.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DosingView.setVisibility(View.GONE);

                    if (!Manual_CrCl.isChecked()) {
                        autofillCrClIfPossible();
                    }
                }
            });
        }

        for (int i = 0; i < inputs.size() - 1; i++) {
            inputs.get(i).setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                        if (!Manual_CrCl.isChecked()) {
                            autofillCrClIfPossible();
                        }
                    }
                    return false;
                }
            });
        }

        CrClInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (Manual_CrCl.isChecked()) {
                    userInputtedCrCl = CrClInput.getText().toString();
                }

                return false;
            }
        });

        CrClInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (Manual_CrCl.isChecked()) {
                    userInputtedCrCl = CrClInput.getText().toString();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Manual_CrCl.isChecked()) {
                    userInputtedCrCl = CrClInput.getText().toString();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (Manual_CrCl.isChecked()) {
                    userInputtedCrCl = CrClInput.getText().toString();
                }
            }
        });

        SexInput.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView adapter, View view, int i, long lng) {
                DosingView.setVisibility(View.GONE);
                if (!Manual_CrCl.isChecked()) {
                    autofillCrClIfPossible();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView)
            {
                DosingView.setVisibility(View.GONE);
            }
        });

        ObeseInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DosingView.setVisibility(View.GONE);
            }
        });

        CNS_Input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DosingView.setVisibility(View.GONE);

                if (CNS_Input.isChecked()) {
                    System.out.println("Checked");
                    CNSOriginalValue = AUCInput.getText().toString();
                    AUCInput.setText("600");
                } else {
                    System.out.println("Not checked");
                    AUCInput.setText(CNSOriginalValue);
                }
            }
        });

        Manual_CrCl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DosingView.setVisibility(View.GONE);
                if (!Manual_CrCl.isChecked()) {
                    autofillCrClIfPossible();
                } else {
                    CrClInput.setText(userInputtedCrCl);
                }
            }
        });
    }

    // checks that user inputs exist; warns user if input is missing
    private boolean validateUserInput(long sexID) {
        boolean inputIsValid = true;

        // highlight inputs in red if missing
        List<EditText> inputs = Arrays.asList(
            this.AUCInput,
            this.CrClInput,
            this.WeightInput,
            this.AgeInput,
            this.SCrInput
        );

        for (int i = 0; i < inputs.size(); i++) {
            String inputString = inputs.get(i).getText().toString();
            if (inputString.length() == 0) {
                inputs.get(i).setHintTextColor(Color.RED);
                inputIsValid = false;
            } else {
                try {
                    Double.parseDouble(inputString);
                } catch (NumberFormatException e) {
                    inputs.get(i).setText("");
                    inputs.get(i).setHintTextColor(Color.RED);
                    inputIsValid = false;
                }
            }
        }

        // set error if user forgets to select Female/Male
        System.out.println("SexID: " + sexID);
        if (sexID == 0) {
            ((TextView) SexInput.getSelectedView()).setError("Select Male or Female");
            inputIsValid = false;
        }

        return inputIsValid;
    }

    // button press onClick method
    public void calculateInitialDose(View view) {
        long sexID = SexInput.getSelectedItemId();

        // clear red from all input fields and buttons
        this.resetHints();

        // terminate execution if any user input is missing
        if (!this.validateUserInput(sexID)) {
            System.out.println("Inputs are NOT valid");
            return;
        }

        System.out.println("Inputs are valid");

        // grab values from user inputs
        double sexIDMinusOne = (double) sexID - 1;
        double age = Double.parseDouble(AgeInput.getText().toString());
        double scr = Double.parseDouble(SCrInput.getText().toString());
        double bodyWeight = Double.parseDouble(WeightInput.getText().toString());
        double targetAUC = Double.parseDouble(AUCInput.getText().toString());
        boolean isObese = ObeseInput.isChecked();

        // actually do the math by calling appropriate methods from InitialDoseCalculator
        double Ke = InitialDoseCalculator.calculateKe(Double.parseDouble(CrClInput.getText().toString()));
        double halfLife = InitialDoseCalculator.calculateHL(Ke);
        double Vd = InitialDoseCalculator.calculateVd(bodyWeight);
        double clvanGeneral = InitialDoseCalculator.calculateClvanGeneral(Ke, Vd);
        double clvanObese = InitialDoseCalculator.calculateClvanObese(age, scr, sexIDMinusOne, bodyWeight);
        double finalClvan = InitialDoseCalculator.calculateCappedClvanFinal(isObese, clvanGeneral, clvanObese);
        double estimatedDailyDose = InitialDoseCalculator.calculateEDDFinal(finalClvan, targetAUC);
        double alternate15 = InitialDoseCalculator.calculateObese(bodyWeight, 15);
        double alternate20 = InitialDoseCalculator.calculateObese(bodyWeight, 20);
        double alternate25 = InitialDoseCalculator.calculateObese(bodyWeight, 25);
        double alternate30 = InitialDoseCalculator.calculateObese(bodyWeight, 30);

        // show results
        DosingView.setVisibility(View.VISIBLE);
        displayCalculatedDose(view, isObese, estimatedDailyDose, alternate15, alternate20, alternate25, alternate30);
        displayCalculatedValues(view, Ke, halfLife, Vd, finalClvan);
    }

    // helper method to reset hint color to gray
    private void resetHints() {
        List<EditText> inputs = Arrays.asList(AUCInput, CrClInput, WeightInput, AgeInput, SCrInput);

        for (int i = 0; i < inputs.size(); i++) {
            inputs.get(i).setHintTextColor(Color.GRAY);
        }
    }

    // helper method to autofill CrCl
    private void autofillCrClIfPossible() {
        long sexIDMinusOne = SexInput.getSelectedItemId() - 1;
        String weightInputString = WeightInput.getText().toString();
        String ageInputString = AgeInput.getText().toString();
        String scrInputString = SCrInput.getText().toString();

        double weightInputDouble = -1;
        double ageInputDouble = -1;
        double scrInputDouble = -1;
        boolean parseSuccessful = true;

        try {
            weightInputDouble = Double.parseDouble(weightInputString);
            ageInputDouble = Double.parseDouble(ageInputString);
            scrInputDouble = Double.parseDouble(scrInputString);
        } catch (NumberFormatException e) {
            parseSuccessful = false;
        }

        if (
                sexIDMinusOne >= 0
                        && weightInputString.length() > 0
                        && ageInputString.length() > 0
                        && scrInputString.length() > 0
                        && parseSuccessful
        ) {
            int suggestedCrCl = (int) InitialDoseCalculator.calculateCrCl(
                    sexIDMinusOne,
                    ageInputDouble,
                    weightInputDouble,
                    scrInputDouble
            );

            CrClInput.setText(String.format(Locale.getDefault(),"%d", suggestedCrCl));
        }
    }

    public void displayCalculatedDose(View view, boolean isObese, double aucResult, double alt15, double alt20, double alt25, double alt30) {
        ConstraintLayout Obesity_dosing = findViewById(R.id.ID_Obese_dosing);
        android.widget.TextView AUC_Result = findViewById(R.id.ID_AUC_Dosing_result);
        android.widget.TextView Obese_3020 = findViewById(R.id.ID_Obese_30_20);
        android.widget.TextView Obese_3025 = findViewById(R.id.ID_Obese_30_25);
        android.widget.TextView Obese_3030 = findViewById(R.id.ID_Obese_30_30);
        android.widget.TextView Obese_4015 = findViewById(R.id.ID_Obese_40_15);
        android.widget.TextView Obese_4020 = findViewById(R.id.ID_Obese_40_20);
        android.widget.TextView Obese_4025 = findViewById(R.id.ID_Obese_40_25);

        AUC_Result.setText(String.format(Locale.getDefault(),"%.0f", aucResult));

        if (isObese) {
            Obesity_dosing.setVisibility(View.VISIBLE);

            Obese_3020.setText(String.format(Locale.getDefault(),"%.0f", alt20));
            Obese_3025.setText(String.format(Locale.getDefault(),"%.0f", alt25));
            Obese_3030.setText(String.format(Locale.getDefault(),"%.0f", alt30));
            Obese_4015.setText(String.format(Locale.getDefault(),"%.0f", alt15));
            Obese_4020.setText(String.format(Locale.getDefault(),"%.0f", alt20));
            Obese_4025.setText(String.format(Locale.getDefault(),"%.0f", alt25));
        } else {
            Obesity_dosing.setVisibility(View.GONE);
        }
    }

    public void displayCalculatedValues(View view, double ke, double halfLife, double vd, double clvanco) {
        android.widget.TextView keResult = findViewById(R.id.ID_ke_value);
        android.widget.TextView hlResult = findViewById(R.id.ID_halflife_value);
        android.widget.TextView vdResult = findViewById(R.id.ID_Vd_value);
        android.widget.TextView clVancoResult = findViewById(R.id.ID_clvanco_value);

        keResult.setText(String.format(Locale.getDefault(),"%.5f", ke));
        hlResult.setText(String.format(Locale.getDefault(),"%.2f", halfLife));
        vdResult.setText(String.format(Locale.getDefault(),"%.2f", vd));
        clVancoResult.setText(String.format(Locale.getDefault(),"%.2f", clvanco));
    }
}