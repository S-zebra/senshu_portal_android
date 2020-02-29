package szebra.senshu_timetable.activities;

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
    ViewPager viewPager = view.findViewById(R.id.news_pager);
    viewPager.setAdapter(adapter);
  }
}

class NewsPagerAdapter extends FragmentStatePagerAdapter {
  public NewsPagerAdapter(FragmentManager fm) {
    super(fm);
  }
  
  @Override
  public Fragment getItem(int position) {
    Fragment f = new NewsListFragment();
    Bundle args = new Bundle();
    args.putInt("aaa", 1);
    f.setArguments(args);
    return f;
  }
  
  @Override
  public int getCount() {
    return 4;
  }
  
  @Override
  public CharSequence getPageTitle(int position) {
    return "テスト " + (position + 1);
  }
}
