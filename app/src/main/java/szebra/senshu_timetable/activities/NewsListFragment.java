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
import szebra.senshu_timetable.tasks.NewsCategory;
import szebra.senshu_timetable.tasks.callbacks.TaskCallback;
import szebra.senshu_timetable.views.recyclerview.NewsRVAdapter;

public class NewsListFragment extends Fragment implements TaskCallback {
  private Realm realm;
  private RecyclerView recycler;
  private int catCode;
  
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
    catCode = getArguments().getInt("CATEGORY");
  
  
    Log.d(getClass().getSimpleName(), "onActivityCreated(): news1: " + realm.where(News.class).findFirst());
    Log.d(getClass().getSimpleName(), "onActivityCreated(): catCode: " + catCode);
    List<News> news = realm.where(News.class).equalTo("categoryCode", catCode).findAll();
    if (news.isEmpty()) {
      Log.d(getClass().getSimpleName(), "onActivityCreated(): " + news + " Refreshing");
      FetchNewsTask task = new FetchNewsTask();
      task.setReference(this);
      task.execute(NewsCategory.fromCode(catCode));
      return;
    }
    updateList();
  }
  
  private void updateList() {
    List<News> news = realm.where(News.class).equalTo("categoryCode", catCode).findAll();
    Log.d(getClass().getSimpleName(), "updateList(): " + news.toString());
    recycler.setLayoutManager(new LinearLayoutManager(getContext()));
    recycler.setAdapter(new NewsRVAdapter(getContext(), news));
  }
  
  @Override
  public void onTaskCompleted(Throwable exception) {
    updateList();
  }
}
