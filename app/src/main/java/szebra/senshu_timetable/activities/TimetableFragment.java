package szebra.senshu_timetable.activities;


import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Calendar;

import io.realm.Realm;
import szebra.senshu_timetable.LectureTerm;
import szebra.senshu_timetable.R;
import szebra.senshu_timetable.models.Lecture;
import szebra.senshu_timetable.tasks.UpdateTimetableTask;
import szebra.senshu_timetable.tasks.callbacks.TaskCallback;
import szebra.senshu_timetable.views.ClassCell;
import szebra.senshu_timetable.views.PeriodHoursView;

public class TimetableFragment extends Fragment implements TaskCallback {
  private TableLayout timetable;
  private View view;

  private final int[] rows = {
          R.id.row_1st, R.id.row_2nd, R.id.row_3rd,
          R.id.row_4th, R.id.row_5th, R.id.row_6th,
          R.id.row_7th};
  private static final String[] youbi = {"月", "火", "水", "木", "金", "土"};
  
  private ProgressBar mProgressBar;
  private TextView mWaitingLabel;
  private final boolean forceRefresh = true;
  private final boolean readyToUpdate = false;
  private Realm realm;
  private LectureTerm term;
  
  
  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_timetable, container, false);
  }
  
  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    view = getView();
    timetable = view.findViewById(R.id.timetable);
    mProgressBar = view.findViewById(R.id.circularIndicator);
    mWaitingLabel = view.findViewById(R.id.loadingText);
    
    addtodayRow();
    realm = Realm.getDefaultInstance();
  
    Calendar cal = Calendar.getInstance();
    int month = cal.get(Calendar.MONTH);
    if (month >= Calendar.APRIL && month <= Calendar.SEPTEMBER) {
      term = LectureTerm.FIRST;
    } else {
      term = LectureTerm.LAST;
    }
    switchTerm(term);
  }
  
  public void switchTerm(int menuId) {
    switch (menuId) {
      case R.id.menu_item_first:
        switchTerm(LectureTerm.FIRST);
        break;
      case R.id.menu_item_last:
        switchTerm(LectureTerm.LAST);
        break;
      default:
        break;
    }
  }
  
  public void switchTerm(LectureTerm term) {
    this.term = term;
    if (realm.where(Lecture.class).equalTo("term", term.getIntValue()).findAll().isEmpty()) {
      showProgressBar();
      UpdateTimetableTask task = new UpdateTimetableTask();
      task.setCallback(this);
      task.setTerm(term);
      task.execute();
    } else {
      setTimetable();
    }
  }
  
  @Override
  public void onDestroy() {
    super.onDestroy();
    if (realm == null) return;
    realm.close();
  }
  
  @Override
  public void onTaskCompleted(Throwable exception) {
    if (exception != null) {
      return;
    }
    setTimetable();
  }
  
  public void showProgressBar() {
    mProgressBar.setVisibility(View.VISIBLE);
    mWaitingLabel.setVisibility(View.VISIBLE);
  }
  
  public void hideProgressBar() {
    AlphaAnimation anim = new AlphaAnimation(1.0f, 0.0f);
    anim.setDuration(1000);
    anim.setFillAfter(true);
    anim.setAnimationListener(new Animation.AnimationListener() {
      @Override
      public void onAnimationStart(Animation animation) {
      }
      
      @Override
      public void onAnimationEnd(Animation animation) {
        mProgressBar.setVisibility(View.GONE);
        mWaitingLabel.setVisibility(View.GONE);
      }
      
      @Override
      public void onAnimationRepeat(Animation animation) {
      }
    });
    mProgressBar.startAnimation(anim);
    mWaitingLabel.startAnimation(anim);
  }
  
  public void setTimetable() {
    for (int period = 0; period < rows.length; period++) {
      TableRow row = view.findViewById(rows[period]);
      row.removeAllViews();
      PeriodHoursView phv = new PeriodHoursView(getActivity());
      phv.setPeriod(period);
      row.addView(phv);
      for (int day = 0; day < youbi.length; day++) {
        ClassCell cell = new ClassCell(getActivity());
        cell.setLecture(realm.where(Lecture.class)
          .equalTo("day", day)
          .equalTo("period", period)
          .equalTo("term", term.getIntValue())
          .findFirst());
        row.addView(cell);
      }
    }
    hideProgressBar();
  }
  
  private void addtodayRow() {
    TableRow r = view.findViewById(R.id.row_dow);
    r.addView(new TextView(getActivity()));
    ClassCell cellToAdjust = new ClassCell(getActivity());
    int cellWidth = cellToAdjust.getWidth();
    for (int day = 0; day < youbi.length; day++) {
      TextView tv = new TextView(getActivity());
      tv.setWidth(cellWidth);
      tv.setGravity(Gravity.CENTER);
      tv.setText(youbi[day]);
      r.addView(tv);
    }
  }
  
}
