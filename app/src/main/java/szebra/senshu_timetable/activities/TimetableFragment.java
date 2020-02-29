package szebra.senshu_timetable.activities;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import io.realm.Realm;
import io.realm.RealmResults;
import szebra.senshu_timetable.R;
import szebra.senshu_timetable.models.Credential;
import szebra.senshu_timetable.models.Lecture;
import szebra.senshu_timetable.tasks.UpdateTimetableTask;
import szebra.senshu_timetable.tasks.callbacks.TaskCallback;
import szebra.senshu_timetable.util.PortalCommunicator;
import szebra.senshu_timetable.views.ClassCell;
import szebra.senshu_timetable.views.PeriodHoursView;

public class TimetableFragment extends Fragment implements TaskCallback {
  private TableLayout timetable;
  private View view;
  
  private int[] rows = {
    R.id.row_1st, R.id.row_2nd, R.id.row_3rd,
    R.id.row_4th, R.id.row_5th, R.id.row_6th,
    R.id.row_7th};
  private static String[] youbi = {"月", "火", "水", "木", "金", "土"};
  
  private ProgressBar mProgressBar;
  private TextView mWaitingLabel;
  private final boolean forceRefresh = true;
  private boolean readyToUpdate = false;
  
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
    if (initCredential()) {
      showProgressBar();
      UpdateTimetableTask task = new UpdateTimetableTask();
      task.setCallback(this);
      task.execute();
    } else {
      startActivity(new Intent(getActivity(), LoginActivity.class));
    }
  }
  
  public boolean initCredential() {
    Realm realm = Realm.getDefaultInstance();
    RealmResults<Credential> credentials = realm.where(Credential.class).findAll();
    if (credentials.isEmpty()) return false;
    Credential firstItem = credentials.first();
    Credential newCredential = new Credential(firstItem.getUserName(), firstItem.getPassword());
    PortalCommunicator.getInstance().setCredential(newCredential);
    return true;
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
    Realm realm = Realm.getDefaultInstance();
    for (int period = 0; period < rows.length; period++) {
      TableRow row = view.findViewById(rows[period]);
      PeriodHoursView phv = new PeriodHoursView(getActivity());
      phv.setPeriod(period);
      row.addView(phv);
      for (int day = 0; day < youbi.length; day++) {
        ClassCell cell = new ClassCell(getActivity());
        cell.setLecture(realm.where(Lecture.class).equalTo("day", day).equalTo("period", period).findFirst());
        row.addView(cell);
      }
    }
    hideProgressBar();
    realm.close();
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
