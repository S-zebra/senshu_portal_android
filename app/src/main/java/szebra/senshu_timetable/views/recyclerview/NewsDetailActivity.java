package szebra.senshu_timetable.views.recyclerview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import io.realm.Realm;
import szebra.senshu_timetable.R;
import szebra.senshu_timetable.models.News;
import szebra.senshu_timetable.tasks.NewsBodyFetchTask;
import szebra.senshu_timetable.tasks.callbacks.TaskCallback;

public class NewsDetailActivity extends AppCompatActivity implements TaskCallback {
  private Realm realm;
  private int newsId;
  private News news;
  private ProgressBar progressBar;
  private TextView bodyLabel;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_news_detail);
    Toolbar toolbar = findViewById(R.id.news_det_toolbar);
    toolbar.setTitle("");
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    
    newsId = getIntent().getIntExtra("NEWS_ID", -1);
    Log.d(getClass().getSimpleName(), "onCreate(): newsId:" + newsId);
    if (newsId == -1) return;
    realm = Realm.getDefaultInstance();
    news = realm.where(News.class).equalTo("id", newsId).findFirst();
    if (news == null) return;
    Log.d(getClass().getSimpleName(), "onCreate(): news: " + news);
    
    TextView subjectLabel = findViewById(R.id.news_det_subject);
    subjectLabel.setText(news.getSubject());
    TextView senderLabel = findViewById(R.id.news_det_sender);
    senderLabel.setText(news.getSender());
    progressBar = findViewById(R.id.news_det_progress);
    bodyLabel = findViewById(R.id.news_det_body);
    if (news.getBody() == null) {
      retrieveBodyText(news.getCheckReadId());
    } else {
      updateContent();
    }
  }
  
  private void retrieveBodyText(int checkReadId) {
    progressBar.setVisibility(View.VISIBLE);
    NewsBodyFetchTask task = new NewsBodyFetchTask();
    task.setCallback(this);
    task.execute(checkReadId);
  }
  
  private void updateContent() {
    progressBar.setVisibility(View.GONE);
    bodyLabel.setText(news.getBody());
  }
  
  @Override
  protected void onDestroy() {
    super.onDestroy();
    realm.close();
  }
  
  @Override
  public void onTaskCompleted(Throwable exception) {
    if (exception != null) {
      Toast.makeText(this, getString(R.string.comm_failed), Toast.LENGTH_SHORT).show();
    }
    news = realm.where(News.class).equalTo("id", newsId).findFirst();
    if (news == null) {
      return;
    }
    updateContent();
  }
}
