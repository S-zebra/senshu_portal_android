package szebra.senshu_timetable.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.jsoup.nodes.Document;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import szebra.senshu_timetable.PortalURL;
import szebra.senshu_timetable.R;
import szebra.senshu_timetable.structures.Timetable;
import szebra.senshu_timetable.util.CryptManager;
import szebra.senshu_timetable.util.PortalCommunicator;
import szebra.senshu_timetable.views.ClassCell;

public class MainActivity extends Activity {
  TableLayout timetable;
  
  int rows[] = {
    R.id.row_1st, R.id.row_2nd, R.id.row_3rd,
    R.id.row_4th, R.id.row_5th, R.id.row_6th,
    R.id.row_7th};
  String youbi[] = {"月", "火", "水", "木", "金", "土"};
  String classBeginTimes[] = {"9:00", "10:45", "13:05", "14:50", "16:35", "18:15", ""};
  String classEndTimes[] = {"10:30", "12:15", "14:35", "16:20", "18:05", "19:45", ""};
  
  int cellWidth, cellHeight;
  int dayOfWeek;
  ProgressBar mProgressBar;
  TextView mWaitingLabel;
  String userName, password;
  PortalCommunicator communicator;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    
    timetable = (TableLayout) findViewById(R.id.timetable);
    mProgressBar = (ProgressBar) findViewById(R.id.circularIndicator);
    mWaitingLabel = (TextView) findViewById(R.id.loadingText);
    
    ClassCell cellToAdjust = new ClassCell(this);
    cellWidth = cellToAdjust.getWidth();
    cellHeight = cellToAdjust.getHeight();
    
    dayOfWeek = new Date().getDay();
    
    //Load settings
    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
    if (pref.contains("userName") && pref.contains("pwd")) {
      byte[] key = Base64.decode(pref.getString("key", ""), Base64.DEFAULT);
      
      try {
        userName = new String(CryptManager.decrypt(Base64.decode(pref.getString("userName", ""), Base64.DEFAULT), key));
      } catch (GeneralSecurityException e1) {
        Log.e("MainActivity", "Decryption username error, " + e1.getLocalizedMessage());
      }
      
      try {
        password = new String(CryptManager.decrypt(Base64.decode(pref.getString("pwd", ""), Base64.DEFAULT), key));
      } catch (GeneralSecurityException e2) {
        Log.e("MainActivity", "Decryption password error, " + e2.getLocalizedMessage());
      }
      
      communicator = PortalCommunicator.getInstance();
      communicator.setCredential(userName, password);
      prepareCommunicator();
      addYoubiRow();
    } else {
      startActivity(new Intent(this, LoginActivity.class));
      finish();
    }
  }
  
  private void prepareCommunicator() {
    //Login
    new AsyncTask<Void, Integer, Boolean>() {
      @Override
      protected Boolean doInBackground(Void... params) {
        try {
          return communicator.login();
        } catch (IOException e) {
          Log.e("IO Exception", e.getMessage());
          this.cancel(true);
          return null;
        }
      }
      
      @Override
      protected void onCancelled() {
        new AlertDialog.Builder(MainActivity.this)
          .setTitle("エラーが発生しました")
          .setMessage("通信に失敗しました。")
          .setPositiveButton("閉じる", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface anInterface, int i) {
              MainActivity.this.finish();
            }
          }).create().show();
      }
      
      @Override
      protected void onPostExecute(Boolean loginSuccess) {
        if (loginSuccess) {
          getTimeTable();
          hideCyclar();
        } else {
          new AlertDialog.Builder(MainActivity.this)
            .setTitle("エラーが発生しました")
            .setMessage("ログインできませんでした。" + "¥n" + "ユーザー名またはパスワードが違う可能性があります。")
            .setPositiveButton("閉じる", new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface anInterface, int i) {
                MainActivity.this.finish();
              }
            }).create().show();
        }
      }
    }.execute();
    
  }
  
  public void getTimeTable() {
    //Get timetable
    new AsyncTask<Void, Integer, Document>() {
      @Override
      protected Document doInBackground(Void... params) {
        try {
          return communicator.moveTo(PortalCommunicator.MoveMode.GET, PortalURL.MY_PAGE_URL, null);
        } catch (IOException e) {
          this.cancel(true);
          return null;
        }
      }
      
      @Override
      protected void onCancelled() {
        new AlertDialog.Builder(MainActivity.this)
          .setTitle("エラーが発生しました")
          .setMessage("通信に失敗しました。")
          .setPositiveButton("閉じる", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface anInterface, int i) {
              MainActivity.this.finish();
            }
          }).create().show();
      }
      
      @Override
      protected void onPostExecute(Document document) {
        Log.d("Timetable", "onPostExecute");
        setTimetable(Timetable.parseFrom(document));
      }
    }.execute();
  }
  
  public void hideCyclar() {
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
  
  public void setTimetable(Timetable ttb) {
    int currentClassIndex = getCurrentClassIndex();
    
    for (int i = 0; i < rows.length; i++) {
      TableRow r = (TableRow) findViewById(rows[i]);
      
      for (int j = 0; j < youbi.length; j++) { // "月"〜"土"
        
        ClassCell cell = new ClassCell(this);
        if (dayOfWeek != 0 && j == dayOfWeek - 1) {
          if (i == currentClassIndex) {
            cell.setBackgroundColor(Color.argb(128, 255, 101, 207));
          } else {
            cell.setBackgroundColor(Color.argb(128, 127, 185, 255));
          }
        }
        //i=時限-1、=j=曜日
        cell.setLecture(ttb.getLecture(j, i));
        r.addView(cell);
      }
      r.addView(createJigenLabel(i), 0);
    }
    
  }
  
  private TextView createJigenLabel(int i) {
    TextView jigenLabel = new TextView(this);
    jigenLabel.setGravity(Gravity.CENTER);
    jigenLabel.setText(classBeginTimes[i] + "\n" + String.format(Locale.ENGLISH, "%d", i + 1) + "\n" + classEndTimes[i]);
    return jigenLabel;
  }
  
  private void addYoubiRow() {
    TableRow r = (TableRow) findViewById(R.id.row_dow);
    r.addView(new TextView(this));
    for (int day = 0; day < youbi.length; day++) {
      TextView tv = new TextView(this);
      tv.setWidth(cellWidth);
      tv.setGravity(Gravity.CENTER);
      tv.setText(youbi[day]);
      r.addView(tv);
    }
  }
  
}
