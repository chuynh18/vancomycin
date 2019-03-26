package yolo.tbv.vancomycin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class AUC extends AppCompatActivity {

    DatePickerFragment precedingDoseDateFragment;
    TimePickerFragment precedingDoseTimeFragment;

    DatePickerFragment levelOneDateFragment;
    TimePickerFragment levelOneTimeFragment;

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

        Bundle levelOneDateBundle = new Bundle();
        levelOneDateBundle.putInt("viewId", R.id.level_1_date_button);
        levelOneDateFragment = new DatePickerFragment();
        levelOneDateFragment.setArguments(levelOneDateBundle);

        Bundle levelOneTimeBundle = new Bundle();
        levelOneTimeBundle.putInt("viewId", R.id.level_1_time_button);
        levelOneTimeFragment = new TimePickerFragment();
        levelOneTimeFragment.setArguments(levelOneTimeBundle);
    }

    // these functions are the onClick handlers...
    public void pickPrecedingDoseDate(View v) {
        precedingDoseDateFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void pickPrecedingDoseTime(View v) {
        precedingDoseTimeFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void pickLevelOneDate(View v) {
        levelOneDateFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void pickLevelOneTime(View v) {
        levelOneTimeFragment.show(getSupportFragmentManager(), "timePicker");
    }
    // end onClick handlers
}
