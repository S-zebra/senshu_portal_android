package szebra.senshu_timetable.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import io.realm.Realm;
import szebra.senshu_timetable.R;
import szebra.senshu_timetable.models.News;
import szebra.senshu_timetable.views.recyclerview.NewsRVAdapter;

public class NewsListFragment extends Fragment {
  
  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_news_container, container, false);
  }
  
  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    Realm realm = Realm.getDefaultInstance();
    List<News> news = realm.where(News.class).findAll();
    if (news.isEmpty()) {
      Toast.makeText(getContext(), "News is Empty", Toast.LENGTH_SHORT).show();
    }
    RecyclerView newsRV = getView().findViewById(R.id.news_list);
    newsRV.setLayoutManager(new LinearLayoutManager(getContext()));
    newsRV.setAdapter(new NewsRVAdapter(getContext(), news));
  }
}
