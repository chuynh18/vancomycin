package yolo.tbv.vancomycin;

import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.SubscriptSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class InitialDose extends AppCompatActivity {
    private android.widget.EditText AUCInput;
    private android.widget.EditText CrClInput;
    private android.widget.EditText WeightInput;
    private android.widget.EditText AgeInput;
    private android.widget.EditText SCrInput;
    private android.widget.Spinner SexInput;
    private android.widget.CheckBox ObeseInput;
    private android.widget.CheckBox CNS_Input;
    private ConstraintLayout DosingView;

    // holds onto original value of AUC24, used by CNS_Input.setOnClickListener() in onCreate()
    String CNSOriginalValue;

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

        // assign class variables their proper values
        AUCInput = findViewById(R.id.ID_AUC_Input);
        CrClInput = findViewById(R.id.ID_CrCl_Input);
        WeightInput = findViewById(R.id.ID_BodyWeight_Input);
        AgeInput = findViewById(R.id.ID_Age_Input);
        SCrInput = findViewById(R.id.ID_SCr_Input);
        SexInput = findViewById(R.id.ID_Sex_Input);
        ObeseInput = findViewById(R.id.ID_Obese_Input);
        CNS_Input = findViewById(R.id.ID_CNS_Input);
        DosingView = findViewById(R.id.ID_dosing_result);

        // set Event Listeners on input fields to hide dosage info (so user never sees out of date dosage)
        List<android.widget.EditText> inputs = Arrays.asList(AUCInput, CrClInput, WeightInput, AgeInput, SCrInput);

        for (int i = 0; i < inputs.size(); i++) {
            inputs.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DosingView.setVisibility(View.GONE);
                }
            });
        }

        SexInput.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView adapter, View view, int i, long lng) {
                DosingView.setVisibility(View.GONE);
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
    }

    // checks that user inputs exist; warns user if input is missing
    private boolean validateUserInput(long sexID) {
        boolean inputIsValid = true;

        // highlight inputs in red if missing
        List<EditText> inputs = Arrays.asList(AUCInput, CrClInput, WeightInput, AgeInput, SCrInput);
        for (int i = 0; i < inputs.size(); i++) {
            String inputString = inputs.get(i).getText().toString();
            if (inputString.length() == 0) {
                inputs.get(i).setHintTextColor(Color.RED);
                inputIsValid = false;
            }
        }

        // set error if user forgets to select Female/Male
        System.out.println("SexID: " + sexID);
        if (sexID == 0) {
            ((TextView) SexInput.getSelectedView()).setError("Select Male or Female");
            inputIsValid = false;
        }

        // terminate execution if input is not valid
        if (!inputIsValid) {
            System.out.println("Inputs are NOT valid");
        }

        return inputIsValid;
    }

    // button press onClick method
    public void calculateButtonPressed(View view) {
        long sexID = SexInput.getSelectedItemId();

        // terminate execution if user input is missing
        if (!this.validateUserInput(sexID)) {
            return;
        }

        // grab values from user inputs
        double sexCalculateObeseClvan = (double) sexID - 1;
        double age = Double.parseDouble(AgeInput.getText().toString());
        double scr = Double.parseDouble(SCrInput.getText().toString());
        double bodyWeight = Double.parseDouble(WeightInput.getText().toString());
        double targetAUC = Double.parseDouble(AUCInput.getText().toString());
        boolean isObese = ObeseInput.isChecked();

        // input is valid, so reset hint styling (change font color back to gray from red)
        this.resetHints();
        System.out.println("Inputs are valid");

        // actually do the math by calling appropriate methods from InitialDoseCalculator
        double Ke = InitialDoseCalculator.calculateKe(Double.parseDouble(CrClInput.getText().toString()));
        double halfLife = InitialDoseCalculator.calculateHL(Ke);
        double Vd = InitialDoseCalculator.calculateVd(bodyWeight);
        double clvanGeneral = InitialDoseCalculator.calculateClvanGeneral(Ke, Vd);
        double clvanObese = InitialDoseCalculator.calculateClvanObese(age, scr, sexCalculateObeseClvan, bodyWeight);
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

    // reset hint color to gray helper method
    private void resetHints() {
        List<EditText> inputs = Arrays.asList(AUCInput, CrClInput, WeightInput, AgeInput, SCrInput);

        for (int i = 0; i < inputs.size(); i++) {
            inputs.get(i).setHintTextColor(Color.GRAY);
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

        AUC_Result.setText(String.format(Locale.getDefault(),"%f", aucResult));

        if (isObese) {
            Obesity_dosing.setVisibility(View.VISIBLE);

            Obese_3020.setText(String.format(Locale.getDefault(),"%f", alt20));
            Obese_3025.setText(String.format(Locale.getDefault(),"%f", alt25));
            Obese_3030.setText(String.format(Locale.getDefault(),"%f", alt30));
            Obese_4015.setText(String.format(Locale.getDefault(),"%f", alt15));
            Obese_4020.setText(String.format(Locale.getDefault(),"%f", alt20));
            Obese_4025.setText(String.format(Locale.getDefault(),"%f", alt25));
        } else {
            Obesity_dosing.setVisibility(View.GONE);
        }
    }

    public void displayCalculatedValues(View view, double ke, double halfLife, double vd, double clvanco) {
        android.widget.TextView Ke = findViewById(R.id.ID_ke_value);
        android.widget.TextView Hl = findViewById(R.id.ID_halflife_value);
        android.widget.TextView Vd = findViewById(R.id.ID_Vd_value);
        android.widget.TextView Clvanco = findViewById(R.id.ID_clvanco_value);

        Ke.setText(String.format(Locale.getDefault(),"%f", ke));
        Hl.setText(String.format(Locale.getDefault(),"%f", halfLife));
        Vd.setText(String.format(Locale.getDefault(),"%f", vd));
        Clvanco.setText(String.format(Locale.getDefault(),"%f", clvanco));
    }
}