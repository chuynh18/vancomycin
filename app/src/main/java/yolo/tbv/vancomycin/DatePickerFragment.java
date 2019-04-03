package yolo.tbv.vancomycin;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public final class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener{
    private final Calendar c = Calendar.getInstance();
    private boolean userSelectedDate = false;
    private int viewId;
    private int chosenYear = c.get(Calendar.YEAR);
    private int chosenMonth = c.get(Calendar.MONTH);
    private int chosenDay = c.get(Calendar.DAY_OF_MONTH);

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getArguments() != null) {
            this.viewId = getArguments().getInt("viewId");
        }

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, this.chosenYear, this.chosenMonth, this.chosenDay);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        this.userSelectedDate = true;
        this.chosenYear = year;
        this.chosenMonth = month;
        this.chosenDay = day;

        if (this.viewId > 0) {
            setButtonDate(this.viewId);
        }

        ((AUC) getActivity()).dateTimeHintResetHelper();
    }

    private void setButtonDate(int viewId) {
        android.widget.Button button = getActivity().findViewById(viewId);
        button.setText(this.getDate());
    }

    public String getDate() {
        LocalDate inputDate = LocalDate.of(this.chosenYear, this.chosenMonth + 1, this.chosenDay);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return inputDate.format(dateTimeFormatter);
    }

    public int getChosenYear() {
        return this.chosenYear;
    }

    public int getChosenMonth() {
        return this.chosenMonth;
    }

    public int getChosenDay() {
        return this.chosenDay;
    }

    public boolean userDidChooseDate() { return this.userSelectedDate; }
}