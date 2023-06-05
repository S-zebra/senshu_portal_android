package szebra.senshu_timetable;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;

/**
 * Created by s-zebra on 2018/07/03.
 */
public class AutoUpdater extends IntentService {
  /**
   * Creates an IntentService.  Invoked by your subclass's constructor.
   *
   * @param name Used to name the worker thread, important only for debugging.
   */
  public AutoUpdater() {
    super("AutoUpdater");
  }
  
  @Override
  public void onCreate() {
    super.onCreate();
  }
  
  @Override
  public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
    return super.onStartCommand(intent, flags, startId);
  }
  
  @Override
  protected void onHandleIntent(@Nullable Intent intent) {
  }
  
  @Override
  public void onDestroy() {
    super.onDestroy();
  }
  
  
}
