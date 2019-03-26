package yolo.tbv.vancomycin;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener{
    private boolean userSelectedDate = false;
    private int viewId;
    private int chosenYear;
    private int chosenMonth;
    private int chosenDay;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int year;
        int month;
        int day;

        if (getArguments() != null) {
            this.viewId = getArguments().getInt("viewId");
        }

        if (userSelectedDate) {
            year = chosenYear;
            month = chosenMonth;
            day = chosenDay;
        } else {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
        }

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        this.userSelectedDate = true;
        this.chosenYear = year;
        this.chosenMonth = month;
        this.chosenDay = day;

        if (this.viewId > 0) {
            android.widget.Button button = getActivity().findViewById(viewId);
            LocalDate inputDate = LocalDate.of(year, month, day);
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            button.setText(inputDate.format(dateTimeFormatter));
        }
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