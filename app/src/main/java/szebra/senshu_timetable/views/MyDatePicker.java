package szebra.senshu_timetable.views;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

import szebra.senshu_timetable.activities.ToDoEditActivity;

/**
 * Created by s-zebra on 2018/06/06.
 */
public class MyDatePicker extends DialogFragment {
  Calendar cal;
  
  public void setCalendar(Calendar cal) {
    this.cal = cal;
  }
  
  @Override
  public Dialog onCreateDialog(@NonNull Bundle savedInstanceState) {
    if (cal == null) {
      cal = Calendar.getInstance();
    }
    int year = cal.get(Calendar.YEAR);
    int month = cal.get(Calendar.MONTH);
    int day = cal.get(Calendar.DATE);
    return new DatePickerDialog(getActivity(), (ToDoEditActivity) getActivity(), year, month, day);
  }
}
