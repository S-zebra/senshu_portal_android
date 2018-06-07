package szebra.senshu_timetable.views;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import java.util.Calendar;

import szebra.senshu_timetable.activities.ToDoEditActivity;

/**
 * Created by s-zebra on 2018/06/06.
 */
public class MyDatePicker extends DialogFragment {
  @Override
  public Dialog onCreateDialog(@NonNull Bundle savedInstanceState) {
    final Calendar cal = Calendar.getInstance();
    int year = cal.get(Calendar.YEAR);
    int month = cal.get(Calendar.MONTH);
    int day = cal.get(Calendar.DATE);
    return new DatePickerDialog(getActivity(), (ToDoEditActivity) getActivity(), year, month, day);
  }
}
