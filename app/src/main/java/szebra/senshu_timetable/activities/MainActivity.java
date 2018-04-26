package szebra.senshu_timetable.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;
import szebra.senshu_timetable.PortalURL;
import szebra.senshu_timetable.R;
import szebra.senshu_timetable.models.Credential;
import szebra.senshu_timetable.models.Lecture;
import szebra.senshu_timetable.structures.Timetable;
import szebra.senshu_timetable.util.PortalCommunicator;
import szebra.senshu_timetable.views.ClassCell;

public class MainActivity extends Activity {
  private TableLayout timetable;
  
  private int rows[] = {
    R.id.row_1st, R.id.row_2nd, R.id.row_3rd,
    R.id.row_4th, R.id.row_5th, R.id.row_6th,
    R.id.row_7th};
  private String youbi[] = {"月", "火", "水", "木", "金", "土"};
  private String classBeginTimes[] = {"9:00", "10:45", "13:05", "14:50", "16:35", "18:15", ""};
  private String classEndTimes[] = {"10:30", "12:15", "14:35", "16:20", "18:05", "19:45", ""};
  
  private int today;
  private ProgressBar mProgressBar;
  private TextView mWaitingLabel;
  private Realm mRealm;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
  
    mRealm = Realm.getDefaultInstance();
    timetable = (TableLayout) findViewById(R.id.timetable);
    mProgressBar = (ProgressBar) findViewById(R.id.circularIndicator);
    mWaitingLabel = (TextView) findViewById(R.id.loadingText);
  
  
    today = new Date().getDay();
    addtodayRow();
  
    RealmResults<Lecture> results = mRealm.where(Lecture.class).findAll();
    if (results.size() == 0) {
      showProgressBar();
      getTimeTableFromPortal();
    } else {
      setTimetable();
    }
  }
  
  /**
   * Connect and fetch timetable from <code>MyPage.php</code>.
   */
  public void getTimeTableFromPortal() {
    //Get timetable
    Log.d("MainActivity", "getTimeTableFromPortal() fired");
    new AsyncTask<Void, Integer, Document>() {
      PortalCommunicator communicator = PortalCommunicator.getInstance();
  
      @Override
      protected void onPreExecute() {
    
      }
      
      @Override
      protected Document doInBackground(Void... params) {
        Realm realm = Realm.getDefaultInstance();
        try {
          if (!communicator.isLoggedIn()) {
            Log.d("時間割取得", "ログインしていません。");
            RealmResults<Credential> result = realm.where(Credential.class).findAll();
            if (result.size() == 0) {
              Log.d("時間割取得", "保存されたアカウント情報はありません。");
              startActivity(new Intent(MainActivity.this, LoginActivity.class));
              finish();
              this.cancel(true);
            } else {
              Log.d("時間割取得", "アカウント情報が見つかりました。ログインします。");
              communicator.logIn(result.first());
            }
          }
          Log.d("時間割取得", "ログイン処理完了。");
          return communicator.moveTo(PortalCommunicator.MoveMode.GET, PortalURL.MY_PAGE_URL, null);
        } catch (IOException e) {
          this.cancel(true);
          return null;
        }
      }
      
      @Override
      protected void onCancelled() {
        Toast.makeText(MainActivity.this, "通信に失敗しました。", Toast.LENGTH_SHORT).show();
      }
      
      @Override
      protected void onPostExecute(Document document) {
        Log.d("時間割アナライザ", "解析を移譲しました");
        Timetable.parseFrom(document);
        setTimetable();
      }
    }.execute();
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
  
  public int getCurrentClassIndex() {
    for (int i = 0; i < classBeginTimes.length; i++) {
      if (isBetween(classBeginTimes[i], classEndTimes[i])) {
        return i;
      }
    }
    return -1;
  }
  
  public boolean isBetween(String begin, String end) {
    Calendar currentCal = Calendar.getInstance();
    
    Calendar beginCal = Calendar.getInstance();
    Calendar endCal = Calendar.getInstance();
    try {
      String beginTime[] = begin.split(":");
      beginCal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(beginTime[0]));
      beginCal.set(Calendar.MINUTE, Integer.parseInt(beginTime[1]));
      
      String endTime[] = end.split(":");
      endCal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(endTime[0]));
      endCal.set(Calendar.MINUTE, Integer.parseInt(endTime[1]));
      
      return (currentCal.after(beginCal) && currentCal.before(endCal));
    } catch (NumberFormatException e) {
      return false;
    }
  }
  
  public void setTimetable() {
    int currentClassIndex = getCurrentClassIndex();
    for (int period = 0; period < rows.length; period++) {
      TableRow r = (TableRow) findViewById(rows[period]);
      for (int day = 0; day < youbi.length; day++) {
        ClassCell cell = new ClassCell(this);
        cell.setLecture(mRealm.where(Lecture.class).equalTo("day", day).equalTo("period", period).findFirst());
        r.addView(cell);
        if (today != 0 && day == today - 1) {
          if (period == currentClassIndex) {
            cell.setBackgroundColor(Color.argb(128, 255, 101, 207));
          } else {
            cell.setBackgroundColor(Color.argb(128, 127, 185, 255));
          }
        }
      }
      r.addView(createPeriodLabel(period), 0);
      if (mProgressBar.getVisibility() != View.GONE) {
        hideProgressBar();
      }
    }
  }
  
  private TextView createPeriodLabel(int i) {
    TextView periodLabel = new TextView(this);
    periodLabel.setGravity(Gravity.CENTER);
    periodLabel.setText(classBeginTimes[i] + "\n" + String.format(Locale.ENGLISH, "%d", i + 1) + "\n" + classEndTimes[i]);
    return periodLabel;
  }
  
  private void addtodayRow() {
    TableRow r = (TableRow) findViewById(R.id.row_dow);
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
  
  @Override
  protected void onDestroy() {
    super.onDestroy();
    mRealm.close();
  }
}
