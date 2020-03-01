package szebra.senshu_timetable.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.HashMap;

import szebra.senshu_timetable.R;
import szebra.senshu_timetable.util.PortalCommunicator;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
  private ActionBarDrawerToggle drawerToggle;
  private DrawerLayout drawer;
  private HashMap<Integer, Fragment> fragmentsMap;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    NavigationView nv = findViewById(R.id.nav_view);
    nv.setNavigationItemSelectedListener(this);
  
    Toolbar toolbar = findViewById(R.id.toolbar_main);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
  
    drawer = findViewById(R.id.drawer_layout);
    drawerToggle = new ActionBarDrawerToggle(this, drawer, R.string.hint_drawer_open, R.string.hint_drawer_close);
    drawerToggle.syncState();
  
    fragmentsMap = new HashMap<>();
//    onNavigationItemSelected((MenuItem) findViewById(R.id.drawer_item_timetable));
    switchFragment(R.id.drawer_item_timetable);
  
    if (!PortalCommunicator.getInstance().hasCredential()) {
      startActivity(new Intent(this, LoginActivity.class));
      finish();
    }
  }
  
  @Override
  protected void onPostCreate(@Nullable Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
    drawerToggle.syncState();
  }
  
  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    drawerToggle.syncState();
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (drawerToggle.onOptionsItemSelected(item)) {
      return true;
    }
    return super.onOptionsItemSelected(item);
  }
  
  @Override
  public boolean onNavigationItemSelected(@NonNull MenuItem item) {
    super.onOptionsItemSelected(item);
    switchFragment(item.getItemId());
    item.setChecked(true);
    return true;
  }
  
  private void switchFragment(int menuItemId) {
    Fragment newFragment = fragmentsMap.get(menuItemId);
    if (newFragment == null) {
      switch (menuItemId) {
        case R.id.drawer_item_timetable:
          newFragment = new TimetableFragment();
          fragmentsMap.put(menuItemId, newFragment);
          break;
        case R.id.drawer_item_news:
          newFragment = new NewsPagerFragment();
          fragmentsMap.put(menuItemId, newFragment);
          break;
      }
    }
    drawer.closeDrawers();
    getSupportFragmentManager()
      .beginTransaction()
      .replace(R.id.frame_main, newFragment)
      .commitAllowingStateLoss();
    drawer.closeDrawers();
  }
}
