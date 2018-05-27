package szebra.senshu_timetable.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import io.realm.Realm;
import io.realm.RealmResults;
import szebra.senshu_timetable.R;
import szebra.senshu_timetable.models.Lecture;
import szebra.senshu_timetable.models.ToDo;
import szebra.senshu_timetable.recyclerview.ToDoRVAdapter;

public class ToDoListActivity extends AppCompatActivity {
  private RecyclerView recyclerView;
  private Realm mRealm;
  private int lectureId;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_to_do_list);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setHomeButtonEnabled(true);
    recyclerView = findViewById(R.id.todoList);
    
    mRealm = Realm.getDefaultInstance();
    lectureId = getIntent().getIntExtra("Lecture", -1);
    Lecture lecture = mRealm.where(Lecture.class).equalTo("id", lectureId).findFirst();
    RealmResults<ToDo> results = mRealm.where(ToDo.class).findAll();
    for (int i = 0; i < results.size(); i++) {
      Log.d("Searching Lecture ID", String.valueOf(results.get(i).getLecture().getId()));
      if (results.get(i).getLecture().getId() != lectureId) {
        results.remove(i);
      }
    }
    recyclerView.setAdapter(new ToDoRVAdapter(results));
    
    setTitle(lecture.getName());
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      finish();
    }
    return super.onOptionsItemSelected(item);
  }
  
  @Override
  protected void onPause() {
    super.onPause();
    mRealm.close();
  }
}
