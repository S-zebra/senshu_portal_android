package szebra.senshu_timetable.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
  private SwipeRefreshLayout swipeRefreshLayout;
  
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
    realm = Realm.getDefaultInstance();
    catCode = getArguments().getInt("CATEGORY");
    recycler = getView().findViewById(R.id.news_list);
    swipeRefreshLayout = getView().findViewById(R.id.news_list_swipe_refresh);
    swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        fetchNewsOnline();
        updateList();
      }
    });
    List<News> news = realm.where(News.class).equalTo("categoryCode", catCode).findAll();
    if (news.isEmpty()) {
      Log.d(getClass().getSimpleName(), "onActivityCreated(): " + news.size() + " Refreshing");
      fetchNewsOnline();
      return;
    }
    updateList();
  }
  
  private void fetchNewsOnline() {
    swipeRefreshLayout.setRefreshing(true);
    FetchNewsTask task = new FetchNewsTask();
    task.setReference(this);
    task.execute(NewsCategory.fromCode(catCode));
    Log.d(getClass().getSimpleName(), "fetchNewsOnline(): updating " + NewsCategory.fromCode(catCode).toString());
  }
  
  private void updateList() {
    List<News> news = realm.where(News.class).equalTo("categoryCode", catCode).findAll();
//    Log.d(getClass().getSimpleName(), "updateList(): " + news.toString());
    recycler.setLayoutManager(new LinearLayoutManager(getContext()));
    recycler.setAdapter(new NewsRVAdapter(getContext(), news));
    if (swipeRefreshLayout.isRefreshing()) {
      swipeRefreshLayout.setRefreshing(false);
    }
  }
  
  @Override
  public void onTaskCompleted(Throwable exception) {
    updateList();
  }
}
