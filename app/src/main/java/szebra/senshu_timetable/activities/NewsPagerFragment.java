package szebra.senshu_timetable.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import szebra.senshu_timetable.R;
import szebra.senshu_timetable.tasks.NewsCategory;

public class NewsPagerFragment extends Fragment {
  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_news_pager, container, false);
  }
  
  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    TabLayout tabLayout = getView().findViewById(R.id.news_tab_layout);
    tabLayout.setupWithViewPager((ViewPager) getView().findViewById(R.id.news_pager));
  }
  
  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    NewsPagerAdapter adapter = new NewsPagerAdapter(getChildFragmentManager());
    adapter.setContext(getContext());
    ViewPager viewPager = view.findViewById(R.id.news_pager);
    viewPager.setAdapter(adapter);
  }
}

class NewsPagerAdapter extends FragmentStatePagerAdapter {
  private Fragment[] fragments;
  private NewsCategory[] categories;
  private Context context;
  
  public NewsPagerAdapter(FragmentManager fm) {
    super(fm);
    categories = NewsCategory.values();
    fragments = new Fragment[categories.length];
    for (int i = 0; i < categories.length; i++) {
      Fragment f = new NewsListFragment();
      Bundle args = new Bundle();
      args.putInt("CATEGORY", categories[i].getNumVal());
      f.setArguments(args);
      fragments[i] = f;
    }
  }
  
  public void setContext(Context context) {
    this.context = context;
  }
  
  @Override
  public Fragment getItem(int position) {
    return fragments[position];
  }
  
  @Override
  public int getCount() {
    return fragments.length;
  }
  
  @Override
  public CharSequence getPageTitle(int position) {
    if (context == null) return "";
    return context.getString(categories[position].getStrId());
  }
}
