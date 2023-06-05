package szebra.senshu_timetable.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;

import java.util.Calendar;
import java.util.HashMap;

import io.realm.Realm;
import szebra.senshu_timetable.AboutFragment;
import szebra.senshu_timetable.R;
import szebra.senshu_timetable.models.Credential;
import szebra.senshu_timetable.util.PortalCommunicator;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
  private ActionBarDrawerToggle drawerToggle;
  private DrawerLayout drawer;
  private HashMap<Integer, Fragment> fragmentsMap;
  private View drawerHeader;
  private int currentMenuId;
  private Fragment currentFragment;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    NavigationView nv = findViewById(R.id.nav_view);
    nv.setNavigationItemSelectedListener(this);
    drawerHeader = nv.inflateHeaderView(R.layout.drawer_header);
    TextView userNameLabel = drawerHeader.findViewById(R.id.drawer_label_username);
    TextView userIdLabel = drawerHeader.findViewById(R.id.drawer_label_userid);
    
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
    } else {
      Realm realm = Realm.getDefaultInstance();
      Credential c = realm.where(Credential.class).findFirst();
      userNameLabel.setText(c.getUserFullName());
      userIdLabel.setText(c.getUserName());
      realm.close();
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
  public boolean onCreateOptionsMenu(Menu menu) {
    if (currentMenuId == 0) return false;
    getMenuInflater().inflate(currentMenuId, menu);
    Calendar cal = Calendar.getInstance();
    int month = cal.get(Calendar.MONTH);
    if (month >= Calendar.APRIL && month <= Calendar.SEPTEMBER) {
      menu.findItem(R.id.menu_item_first).setChecked(true);
    } else {
      menu.findItem(R.id.menu_item_last).setChecked(true);
    }
    return true;
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (drawerToggle.onOptionsItemSelected(item)) {
      return true;
    }
    if (currentFragment.getClass() == TimetableFragment.class) {
      ((TimetableFragment) currentFragment).switchTerm(item.getItemId());
      item.setChecked(true);
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
          break;
        case R.id.drawer_item_news:
          newFragment = new NewsPagerFragment();
          break;
        case R.id.drawer_item_about:
          newFragment = new AboutFragment();
          break;
        default:
          break;
      }
    }
  
    switch (menuItemId) {
      case R.id.drawer_item_timetable:
        currentMenuId = R.menu.timetable_menu;
        break;
      case R.id.drawer_item_news:
        currentMenuId = 0;
        break;
      case R.id.drawer_item_about:
        currentMenuId = 0;
        break;
      default:
        break;
    }
    
    fragmentsMap.put(menuItemId, newFragment);
    invalidateOptionsMenu();
    
    drawer.closeDrawers();
    getSupportFragmentManager()
      .beginTransaction()
      .replace(R.id.frame_main, newFragment)
      .commitAllowingStateLoss();
    drawer.closeDrawers();
    currentFragment = newFragment;
  }
}
