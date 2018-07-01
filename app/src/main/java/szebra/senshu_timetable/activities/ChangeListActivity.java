package szebra.senshu_timetable.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import io.realm.Realm;
import io.realm.RealmResults;
import szebra.senshu_timetable.R;
import szebra.senshu_timetable.models.ChangeInfo;
import szebra.senshu_timetable.views.recyclerview.ChangeRVAdapter;

public class ChangeListActivity extends AppCompatActivity {
  private Realm realm;
  private RecyclerView changeList;
  private TextView noChangeLabel;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_change_activity);
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setHomeButtonEnabled(true);
    
    String lectureName = getIntent().getStringExtra("Lecture");
    Log.d("ChangeListActivity", "Passed: " + lectureName);
    
    noChangeLabel = findViewById(R.id.noChangeLabel);
    
    changeList = findViewById(R.id.changeList);
    changeList.setLayoutManager(new LinearLayoutManager(this));
    
    realm = Realm.getDefaultInstance();
    
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
    switch (item.getItemId()) {
      case android.R.id.home:
        finish();
        break;
    }
    return super.onOptionsItemSelected(item);
  }
  
}
