package szebra.senshu_timetable.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

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
    Toolbar toolbar = findViewById(R.id.toolbar_list_todo);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setHomeButtonEnabled(true);
    recyclerView = findViewById(R.id.todoList);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    
    lectureId = getIntent().getIntExtra("Lecture", -1);
    mRealm = Realm.getDefaultInstance();
  
    updateList();
    
    Lecture lecture = mRealm.where(Lecture.class).equalTo("id", lectureId).findFirst();
    setTitle(lecture.getName());
  
    FloatingActionButton fab = findViewById(R.id.fab_add_todo);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(ToDoListActivity.this, ToDoEditActivity.class);
        intent.putExtra("Lecture", lectureId);
        startActivity(intent);
      }
    });
  }
  
  private void updateList() {
    RealmResults<ToDo> results = mRealm.where(ToDo.class).equalTo("lectureId", lectureId).findAll();
    Log.d("件数", String.valueOf(results.size()));
    recyclerView.setAdapter(new ToDoRVAdapter(this, results));
  }
  
  @Override
  protected void onResume() {
    super.onResume();
    mRealm = Realm.getDefaultInstance();
    updateList();
  }
  
  @Override
  protected void onPostResume() {
    super.onPostResume();
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
