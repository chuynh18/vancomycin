package yolo.tbv.vancomycin;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    private boolean userSelectedTime = false;
    private int viewId;
    private int chosenHour;
    private int chosenMinute;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int hour;
        int minute;

        if (getArguments() != null) {
            this.viewId = getArguments().getInt("viewId");
        }

        if (userSelectedTime) {
            hour = chosenHour;
            minute = chosenMinute;
        } else {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            hour = c.get(Calendar.HOUR_OF_DAY);
            minute = c.get(Calendar.MINUTE);
        }

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        this.userSelectedTime = true;
        this.chosenHour = hourOfDay;
        this.chosenMinute = minute;

        if (this.viewId > 0) {
            android.widget.Button button = getActivity().findViewById(viewId);
            LocalTime inputTime = LocalTime.of(hourOfDay, minute);
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("h:mm a");
            button.setText(inputTime.format(dateTimeFormatter));
        }
    }

    public int getChosenHour() {
        return this.chosenHour;
    }

    public int getChosenMinute() {
        return this.chosenMinute;
    }
}