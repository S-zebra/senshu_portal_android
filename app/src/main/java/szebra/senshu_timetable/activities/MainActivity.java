package szebra.senshu_timetable.activities;

import android.app.Activity;
import android.content.Intent;
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

import io.realm.Realm;
import io.realm.RealmResults;
import szebra.senshu_timetable.PortalURL;
import szebra.senshu_timetable.R;
import szebra.senshu_timetable.models.Credential;
import szebra.senshu_timetable.models.Lecture;
import szebra.senshu_timetable.structures.Timetable;
import szebra.senshu_timetable.util.PortalCommunicator;
import szebra.senshu_timetable.views.ClassCell;
import szebra.senshu_timetable.views.PeriodHoursView;

public class MainActivity extends Activity {
  private TableLayout timetable;
  
  private int rows[] = {
    R.id.row_1st, R.id.row_2nd, R.id.row_3rd,
    R.id.row_4th, R.id.row_5th, R.id.row_6th,
    R.id.row_7th};
  private String youbi[] = {"月", "火", "水", "木", "金", "土"};
  
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
  
  public void setTimetable() {
    for (int period = 0; period < rows.length; period++) {
      TableRow row = (TableRow) findViewById(rows[period]);
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
