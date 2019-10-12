package soa.work.scheduler.utilities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import soa.work.scheduler.R;

public class TimePickerFragment extends DialogFragment {

    private TimePicker timePicker;
    public interface TimeDialogListener {
        void onFinishDialog(String time);
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_time,null);

        timePicker = v.findViewById(R.id.dialog_time_picker);
        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle("")
                .setPositiveButton(android.R.string.ok,
                        (dialog, which) -> {
                            int hour;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                                hour = timePicker.getHour();
                            }else{
                                hour = timePicker.getCurrentHour();
                            }
                            int minute;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                                minute = timePicker.getMinute();
                            }else{
                                minute = timePicker.getCurrentMinute();
                            }
                            TimeDialogListener activity = (TimeDialogListener) getActivity();
                            if (activity != null) {
                                activity.onFinishDialog(updateTime(hour,minute));
                            }
                            dismiss();
                        })
                .create();
    }

    private String updateTime(int hours, int mins) {

        String timeSet;
        if (hours > 12) {
            hours -= 12;
            timeSet = "PM";
        } else if (hours == 0) {
            hours += 12;
            timeSet = "AM";
        } else if (hours == 12)
            timeSet = "PM";
        else
            timeSet = "AM";

        String minutes;
        if (mins < 10)
            minutes = "0" + mins;
        else
            minutes = String.valueOf(mins);

        return String.valueOf(hours) + ':' +
                minutes + " " + timeSet;
    }
}