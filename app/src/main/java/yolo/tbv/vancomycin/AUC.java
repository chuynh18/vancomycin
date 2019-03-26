package yolo.tbv.vancomycin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.SubscriptSpan;
import android.view.View;
import android.widget.TextView;

public class AUC extends AppCompatActivity {

    DatePickerFragment precedingDoseDateFragment;
    TimePickerFragment precedingDoseTimeFragment;

    DatePickerFragment levelOneDateFragment;
    TimePickerFragment levelOneTimeFragment;

    DatePickerFragment levelTwoDateFragment;
    TimePickerFragment levelTwoTimeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auc);

        this.setAUC24Subscript();

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

        Bundle levelTwoDateBundle = new Bundle();
        levelTwoDateBundle.putInt("viewId", R.id.level_2_date_button);
        levelTwoDateFragment = new DatePickerFragment();
        levelTwoDateFragment.setArguments(levelTwoDateBundle);

        Bundle levelTwoTimeBundle = new Bundle();
        levelTwoTimeBundle.putInt("viewId", R.id.level_2_time_button);
        levelTwoTimeFragment = new TimePickerFragment();
        levelTwoTimeFragment.setArguments(levelTwoTimeBundle);
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

    public void pickLevelTwoDate(View v) {
        levelTwoDateFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void pickLevelTwoTime(View v) {
        levelTwoTimeFragment.show(getSupportFragmentManager(), "timePicker");
    }
    // end onClick handlers

    // All this method does is make the "24" in "AUC24" subscript.  That's it.  Really.
    private void setAUC24Subscript() {
        android.widget.TextView auc24 = findViewById(R.id.AUC_goal_auc24);
        SpannableStringBuilder aucSB = new SpannableStringBuilder(getString(R.string.chosen_goal_auc24));
        aucSB.setSpan(new SubscriptSpan(), 15, 17, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        auc24.setText(aucSB, TextView.BufferType.SPANNABLE);
    }
}
