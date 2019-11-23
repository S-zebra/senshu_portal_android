package szebra.senshu_timetable.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import java.io.IOException;

import io.realm.Realm;
import io.realm.RealmResults;
import szebra.senshu_timetable.R;
import szebra.senshu_timetable.crawlers.Timetable;
import szebra.senshu_timetable.models.Credential;
import szebra.senshu_timetable.models.Lecture;
import szebra.senshu_timetable.util.PortalCommunicator;
import szebra.senshu_timetable.views.ClassCell;
import szebra.senshu_timetable.views.PeriodHoursView;

public class MainActivity extends AppCompatActivity {
  private TableLayout timetable;
  
  private int[] rows = {
    R.id.row_1st, R.id.row_2nd, R.id.row_3rd,
    R.id.row_4th, R.id.row_5th, R.id.row_6th,
    R.id.row_7th};
  private static String[] youbi = {"月", "火", "水", "木", "金", "土"};
  
  private ProgressBar mProgressBar;
  private TextView mWaitingLabel;
  private Realm mRealm;
  private final boolean forceRefresh = true;
  private boolean readyToUpdate = false;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = findViewById(R.id.main_toolbar);
    setSupportActionBar(toolbar);
    
    mRealm = Realm.getDefaultInstance();
    timetable = findViewById(R.id.timetable);
    mProgressBar = findViewById(R.id.circularIndicator);
    mWaitingLabel = findViewById(R.id.loadingText);
  
    addtodayRow();
    login();


//    Intent updateService = new Intent(this, AutoUpdater.class);
//    PendingIntent pIntent = PendingIntent.getService(this, -1, updateService, PendingIntent.FLAG_UPDATE_CURRENT);
//    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//    alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), AlarmManager.INTERVAL_HALF_HOUR, pIntent);
//    Toast.makeText(this, "自動更新が有効になりました。", Toast.LENGTH_SHORT).show();
//    startService(updateService);
//    Toast.makeText(this, "更新サービスを実行しています。", Toast.LENGTH_SHORT).show();
  }
  
  public void login() {
    new AsyncTask<Void, Void, Boolean>() {
      PortalCommunicator communicator = PortalCommunicator.getInstance();
      
      @Override
      protected Boolean doInBackground(Void... params) {
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
              return communicator.logIn(result.first());
            }
          }
        } catch (IOException e) {
          this.cancel(true);
        }
        realm.close();
        return false;
      }
      
      @Override
      protected void onCancelled() {
        Toast.makeText(MainActivity.this, "通信に失敗しました。", Toast.LENGTH_SHORT).show();
      }
      
      @Override
      protected void onPostExecute(Boolean success) {
        if (!success) {
          Toast.makeText(MainActivity.this, "ログインに失敗しました。アカウント情報が変更された可能性があります", Toast.LENGTH_LONG).show();
        } else {
          RealmResults<Lecture> results = mRealm.where(Lecture.class).findAll();
          if (results.size() == 0 || forceRefresh) {
            showProgressBar();
            Timetable.update();
          }
          setTimetable();
        }
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
  
  public void setTimetable() {
    for (int period = 0; period < rows.length; period++) {
      TableRow row = findViewById(rows[period]);
      PeriodHoursView phv = new PeriodHoursView(this);
      phv.setPeriod(period);
      row.addView(phv);
      for (int day = 0; day < youbi.length; day++) {
        ClassCell cell = new ClassCell(this);
        cell.setLecture(mRealm.where(Lecture.class).equalTo("day", day).equalTo("period", period).findFirst());
        row.addView(cell);
      }
      if (mProgressBar.getVisibility() != View.GONE) {
        hideProgressBar();
      }
    }
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
  
  @Override
  protected void onDestroy() {
    super.onDestroy();
    mRealm.close();
  }
}
