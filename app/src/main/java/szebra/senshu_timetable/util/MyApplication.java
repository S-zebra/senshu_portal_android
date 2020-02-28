package szebra.senshu_timetable.util;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Base64;

import java.security.Key;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by s-zebra on 2018/04/22.
 */

public class MyApplication extends Application {
  private static MyApplication instance;
  
  @Override
  public void onCreate() {
    super.onCreate();
    
    //Prepare encryption key
    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
    
    String currentEncodedKey = pref.getString("key", "");
    if (currentEncodedKey.length() == 0) {
      Key newKey = CryptManager.generateKey();
      String newEncodedKey = Base64.encodeToString(newKey.getEncoded(), Base64.DEFAULT);
      SharedPreferences.Editor editor = pref.edit();
      editor.putString("key", newEncodedKey);
      editor.commit();
      currentEncodedKey = newEncodedKey;
    }
    byte[] currentKey = Base64.decode(currentEncodedKey, Base64.DEFAULT);
    
    instance = this;
    Realm.init(this);
    Realm.setDefaultConfiguration(new RealmConfiguration.Builder().encryptionKey(currentKey).deleteRealmIfMigrationNeeded().build());
  }
  
  public static MyApplication getInstance() {
    return instance;
  }
  
  @Override
  public void onTerminate() {
    super.onTerminate();
//    PortalCommunicator communicator = PortalCommunicator.getInstance();
//    if (communicator.isLoggedIn()) {
//      try {
//        communicator.logOut();
//      } catch (IOException ex) {
//        Log.e("MyApplication", "Failed to logOut.");
//      }
//    }
  }
}
