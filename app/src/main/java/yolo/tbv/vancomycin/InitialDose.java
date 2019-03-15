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
import java.util.Arrays;
import java.util.List;

public class InitialDose extends AppCompatActivity {
    android.widget.EditText AUCInput;
    android.widget.EditText CrCLInput;
    android.widget.EditText WeightInput;
    android.widget.EditText AgeInput;
    android.widget.EditText SCrInput;
    android.widget.Spinner SexInput;
    android.widget.CheckBox ObeseInput;
    android.widget.CheckBox CNS_Input;

    // holds onto original value of AUC24, used by CNS_Input.setOnClickListener() in onCreate()
    String CNSOriginalValue;

    private void updateAUC() {
        android.widget.TextView auc24 = findViewById(R.id.ID_AUC);
        SpannableStringBuilder aucSB = new SpannableStringBuilder(getString(R.string.chosen_goal_auc24));
        aucSB.setSpan(new SubscriptSpan(), 15, 17, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        auc24.setText(aucSB, TextView.BufferType.SPANNABLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_dose);

        updateAUC();

        // assign class variables their proper values
        AUCInput = findViewById(R.id.ID_AUC_Input);
        CrCLInput = findViewById(R.id.ID_CrCL_Input);
        WeightInput = findViewById(R.id.ID_BodyWeight_Input);
        AgeInput = findViewById(R.id.ID_Age_Input);
        SCrInput = findViewById(R.id.ID_SCr_Input);
        SexInput = findViewById(R.id.ID_Sex_Input);
        ObeseInput = findViewById(R.id.ID_Obese_Input);
        CNS_Input = findViewById(R.id.ID_CNS_Input);

        CNS_Input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

    // button press onClick method
    public void calculateButtonPressed(View view) {
        long sexID = SexInput.getSelectedItemId();
        double sexCalculateObeseClvan = (double) sexID - 1;
        double age = Double.parseDouble(AgeInput.getText().toString());
        double scr = Double.parseDouble(SCrInput.getText().toString());
        double bodyWeight = Double.parseDouble(WeightInput.getText().toString());

        resetHints();

        Boolean inputIsValid = true;

        List<EditText> inputs = Arrays.asList(AUCInput, CrCLInput, WeightInput, AgeInput, SCrInput);
        for (int i = 0; i < inputs.size(); i++) {
            String inputString = inputs.get(i).getText().toString();
            if (inputString.length() == 0) {
                inputs.get(i).setHintTextColor(Color.RED);
                inputIsValid = false;
            }
        }


        System.out.println("SexID: " + sexID);
        if (sexID == 0) {
            ((TextView) SexInput.getSelectedView()).setError("Select Male or Female");
            inputIsValid = false;
        }

        if (inputIsValid) {
            System.out.println("Inputs are valid");
            double Ke = calculateKe();
            double halfLife = calculateHL(Ke);
            double Vd = calculateVd(bodyWeight);
            double clvanGeneral = calculateClvanGeneral(Ke, Vd);
            double clvanObese = calculateClvanObese(age, scr, sexCalculateObeseClvan, bodyWeight);
        } else {
            System.out.println("Inputs are NOT valid");
        }
    }

    // reset hint color to gray helper method
    private void resetHints() {
        List<EditText> inputs = Arrays.asList(AUCInput, CrCLInput, WeightInput, AgeInput, SCrInput);

        for (int i = 0; i < inputs.size(); i++) {
            inputs.get(i).setHintTextColor(Color.GRAY);
        }
    }

    // Calculate max CrCL (if userInput over 150, CrCL is 150, else go with userInput)
    private double calculateCrCL(double inputCrCL) {
        if (inputCrCL > 150) {
            return 150;
        }

        return inputCrCL;
    }

    // Ke is 0.00083*actualCrCL + 0.0044
    public double calculateKe() {
        double inputCrCL = Double.parseDouble(CrCLInput.getText().toString());
        double actualCrCL = calculateCrCL(inputCrCL);
        double Ke = 0.00083*actualCrCL+0.0044;

        System.out.println("Ke: " + Ke);
        return Ke;
    }

    // Half-life is 0.693/Ke
    public double calculateHL(double Ke) {
        double halfLife = 0.693/Ke;
        System.out.println("Half-life: " + halfLife);
        return halfLife;
    }

    // Vd is 0.7 * bodyWeight
    public double calculateVd(double bodyWeight) {
        double Vd = 0.7*bodyWeight;
        System.out.println("Vd: " + Vd);
        return Vd;
    }

    private double calculateClvanGeneral(double Ke, double Vd) {
        double clvanGeneral = Ke*Vd;
        System.out.println("ClvanGeneral: " + clvanGeneral);
        return clvanGeneral;
    }

    private double calculateClvanObese(double age, double scr, double sexIdMinus1, double bodyWeight) {
        double clvanObese = 9.565 - (0.078*age) - (2.009*scr) + (1.09*sexIdMinus1) + (0.04*Math.pow(bodyWeight, 0.75));
        System.out.println("Clvan obese: " + clvanObese);
        return clvanObese;
    }
}

