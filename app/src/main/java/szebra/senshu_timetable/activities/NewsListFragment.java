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
  private RecyclerView recycler;
  
  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_news_container, container, false);
  }
  
  @Override
  public void onResume() {
    super.onResume();
    updateList();
  }
  
  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    recycler = getView().findViewById(R.id.news_list);
    realm = Realm.getDefaultInstance();
    if (realm.where(News.class).findAll().isEmpty()) {
      FetchNewsTask task = new FetchNewsTask();
      task.setReference(this);
      task.execute(NewsType.PRIVATE);
      return;
    }
    updateList();
  }
  
  private void updateList() {
    List<News> news = realm.where(News.class).findAll();
    Log.d(getClass().getSimpleName(), "updateList(): " + news.toString());
    recycler.setLayoutManager(new LinearLayoutManager(getContext()));
    recycler.setAdapter(new NewsRVAdapter(getContext(), news));
  }
  
  @Override
  public void onTaskCompleted(Throwable exception) {
    updateList();
  }
}
