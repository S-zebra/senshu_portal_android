package szebra.senshu_timetable.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

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

public class MainActivity extends AppCompatActivity implements TaskCallback {
  private TableLayout timetable;
  
  private int[] rows = {
    R.id.row_1st, R.id.row_2nd, R.id.row_3rd,
    R.id.row_4th, R.id.row_5th, R.id.row_6th,
    R.id.row_7th};
  private static String[] youbi = {"月", "火", "水", "木", "金", "土"};
  
  private ProgressBar mProgressBar;
  private TextView mWaitingLabel;
  private final boolean forceRefresh = true;
  private boolean readyToUpdate = false;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = findViewById(R.id.main_toolbar);
    setSupportActionBar(toolbar);
    
    timetable = findViewById(R.id.timetable);
    mProgressBar = findViewById(R.id.circularIndicator);
    mWaitingLabel = findViewById(R.id.loadingText);
  
    addtodayRow();
    if (initCredential()) {
      UpdateTimetableTask task = new UpdateTimetableTask();
      task.setCallback(this);
      task.execute();
    } else {
      startActivity(new Intent(this, LoginActivity.class));
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
      Toast.makeText(this, exception.getClass().getSimpleName() + ":" + exception.getMessage(), Toast.LENGTH_SHORT).show();
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
      TableRow row = findViewById(rows[period]);
      PeriodHoursView phv = new PeriodHoursView(this);
      phv.setPeriod(period);
      row.addView(phv);
      for (int day = 0; day < youbi.length; day++) {
        ClassCell cell = new ClassCell(this);
        cell.setLecture(realm.where(Lecture.class).equalTo("day", day).equalTo("period", period).findFirst());
        row.addView(cell);
      }
    }
    if (mProgressBar.getVisibility() != View.GONE) {
      hideProgressBar();
    }
    realm.close();
  }
  
  private void addtodayRow() {
    TableRow r = findViewById(R.id.row_dow);
    r.addView(new TextView(this));
    ClassCell cellToAdjust = new ClassCell(this);
    int cellWidth = cellToAdjust.getWidth();
    for (int day = 0; day < youbi.length; day++) {
      TextView tv = new TextView(this);
      tv.setWidth(cellWidth);
      tv.setGravity(Gravity.CENTER);
      tv.setText(youbi[day]);
      r.addView(tv);
    }
  }
  
}
