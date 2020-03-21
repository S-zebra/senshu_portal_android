package szebra.senshu_timetable.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import io.realm.Realm;
import szebra.senshu_timetable.BuildConfig;
import szebra.senshu_timetable.R;
import szebra.senshu_timetable.models.News;
import szebra.senshu_timetable.models.NewsAttachment;
import szebra.senshu_timetable.tasks.DownloadFileTask;
import szebra.senshu_timetable.tasks.NewsBodyFetchTask;
import szebra.senshu_timetable.tasks.callbacks.DownloadFileCallback;
import szebra.senshu_timetable.tasks.callbacks.TaskCallback;

public class NewsDetailActivity extends AppCompatActivity implements TaskCallback, DownloadFileCallback {
  private Realm realm;
  private int newsId;
  private News news;
  private ProgressBar progressBar;
  private TextView bodyLabel;
  private LinearLayout attachmentList;
  private File downloadFile;
  private boolean writeAllowed = true;
  
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
    attachmentList = findViewById(R.id.news_det_attach_list);
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
    if (!news.getAttachments().isEmpty()) {
      for (NewsAttachment attachment : news.getAttachments()) {
        createAttachmentButton(attachment);
      }
    }
  }
  
  private void createAttachmentButton(final NewsAttachment attachment) {
    ContextThemeWrapper borderless = new ContextThemeWrapper(this, R.style.Widget_AppCompat_Button_Borderless);
    Button button = new Button(borderless);
    button.setText(attachment.getName());
//    button.setCompoundDrawables(getResources().getDrawable(R.drawable.ic_attachment_black_24dp), null, null, null);
    downloadFile = new File(Environment.getExternalStorageDirectory() + "/Download/" + attachment.getName());
    button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        checkPermission();
        if (writeAllowed) {
          DownloadFileTask task = new DownloadFileTask();
          task.setCallback(NewsDetailActivity.this);
          task.setFile(downloadFile);
          task.execute(attachment.getUri());
          Toast.makeText(NewsDetailActivity.this, R.string.downloading, Toast.LENGTH_SHORT).show();
        } else {
          Toast.makeText(NewsDetailActivity.this, R.string.lack_permission, Toast.LENGTH_SHORT).show();
        }
      }
    });
    attachmentList.addView(button);
  }
  
  private void checkPermission() {
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
      writeAllowed = false;
      ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
    } else {
      writeAllowed = true;
    }
  }
  
  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
      writeAllowed = true;
    }
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
  
  @Override
  public void onDownloadCompleted(Throwable exception) {
    if (exception != null) {
      Toast.makeText(this, getString(R.string.comm_failed), Toast.LENGTH_SHORT).show();
    }
    Uri uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", downloadFile);
    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
    intent.setDataAndType(uri, getContentResolver().getType(uri));
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
    startActivity(intent);
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      finish();
    }
    return true;
  }
}
