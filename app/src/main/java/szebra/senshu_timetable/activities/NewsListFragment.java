package szebra.senshu_timetable.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import io.realm.Realm;
import szebra.senshu_timetable.R;
import szebra.senshu_timetable.models.News;
import szebra.senshu_timetable.tasks.FetchNewsTask;
import szebra.senshu_timetable.tasks.NewsType;
import szebra.senshu_timetable.tasks.callbacks.TaskCallback;
import szebra.senshu_timetable.views.recyclerview.NewsRVAdapter;

public class NewsListFragment extends Fragment implements TaskCallback {
  private Realm realm;
  
  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_news_container, container, false);
  }
  
  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    realm = Realm.getDefaultInstance();
    List<News> news = realm.where(News.class).findAll();
    updateList(news);
    Toast.makeText(getContext(), "Updating...", Toast.LENGTH_SHORT).show();
    FetchNewsTask task = new FetchNewsTask();
    task.setReference(this);
    task.execute(NewsType.PRIVATE);
  
  }
  
  private void updateList(List<News> news) {
    Log.d(getClass().getSimpleName(), "updateList(): " + news.toString());
    RecyclerView newsRV = getView().findViewById(R.id.news_list);
    newsRV.setLayoutManager(new LinearLayoutManager(getContext()));
    newsRV.setAdapter(new NewsRVAdapter(getContext(), news));
  }
  
  @Override
  public void onTaskCompleted(Throwable exception) {
    updateList(realm.where(News.class).findAll());
  }
}
