package szebra.senshu_timetable.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import io.realm.Realm;
import io.realm.RealmResults;
import szebra.senshu_timetable.R;
import szebra.senshu_timetable.models.ChangeInfo;
import szebra.senshu_timetable.tasks.UpdateChangesTask;
import szebra.senshu_timetable.tasks.callbacks.TaskCallback;
import szebra.senshu_timetable.views.recyclerview.ChangeRVAdapter;

public class ChangeListActivity extends AppCompatActivity implements TaskCallback {
  private RecyclerView changeList;
  private TextView noChangeLabel;
  private String lectureName;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_change_list);
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setHomeButtonEnabled(true);
  
    lectureName = getIntent().getStringExtra("Lecture");
    Log.d("ChangeListActivity", "Passed: " + lectureName);
    
    noChangeLabel = findViewById(R.id.noChangeLabel);
    
    changeList = findViewById(R.id.changeList);
    changeList.setLayoutManager(new LinearLayoutManager(this));
  
    UpdateChangesTask changesTask = new UpdateChangesTask();
    changesTask.setCallback(this);
    changesTask.execute();
  }
  
  private void updateDisplay() {
    findViewById(R.id.chgLoadingCircle).setVisibility(View.GONE);
    findViewById(R.id.chgLoadingLabel).setVisibility(View.GONE);
    Realm realm = Realm.getDefaultInstance();
    
    RealmResults<ChangeInfo> allResults = realm.where(ChangeInfo.class).findAll();
    Log.d("ChangeListActivity", String.valueOf(allResults.size()));
    
    RealmResults<ChangeInfo> result = realm.where(ChangeInfo.class).equalTo("lectureName", lectureName).findAll();
    if (result.size() > 0) {
      changeList.setAdapter(new ChangeRVAdapter(this, result));
    } else {
      noChangeLabel.setVisibility(View.VISIBLE);
      noChangeLabel.setText(getResources().getString(R.string.label_nochange, lectureName));
    }
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      finish();
    }
    return super.onOptionsItemSelected(item);
  }
  
  @Override
  public void onTaskCompleted(Throwable exception) {
    if (exception != null) {
      Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
      return;
    }
    updateDisplay();
  }
  
}
