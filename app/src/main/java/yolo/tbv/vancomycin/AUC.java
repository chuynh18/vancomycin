package yolo.tbv.vancomycin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class AUC extends AppCompatActivity {

    DatePickerFragment precedingDoseDateFragment;
    TimePickerFragment precedingDoseTimeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auc);
        Bundle precedingDoseDateBundle = new Bundle();
        precedingDoseDateBundle.putInt("viewId", R.id.preceeding_dose_date_button);
        precedingDoseDateFragment = new DatePickerFragment();
        precedingDoseDateFragment.setArguments(precedingDoseDateBundle);

        Bundle precedingDoseTimeBundle = new Bundle();
        precedingDoseTimeBundle.putInt("viewId", R.id.preceeding_dose_time_button);
        precedingDoseTimeFragment = new TimePickerFragment();
        precedingDoseTimeFragment.setArguments(precedingDoseTimeBundle);
    }

    public void pickPrecedingDoseDate(View v) {
        precedingDoseDateFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void pickPrecedingDoseTime(View v) {
        precedingDoseTimeFragment.show(getSupportFragmentManager(), "timePicker");
    }
}
