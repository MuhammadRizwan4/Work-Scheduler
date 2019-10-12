package soa.work.scheduler.utilities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Date;
import java.util.GregorianCalendar;

import soa.work.scheduler.R;

public class DatePickerFragment extends DialogFragment {

    private DatePicker datePicker;

    public interface DateDialogListener {
        void onFinishDialog(Date date);
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){

        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_date,null);
        datePicker = (DatePicker) v.findViewById(R.id.dialog_date_date_picker);
        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle("")
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int year = datePicker.getYear();
                                int mon = datePicker.getMonth();
                                int day = datePicker.getDayOfMonth();
                                Date date = new GregorianCalendar(year,mon,day).getTime();
                                DateDialogListener activity = (DateDialogListener) getActivity();
                                if (activity != null) {
                                    activity.onFinishDialog(date);
                                }
                                dismiss();
                            }
                        })
                .create();
    }
}