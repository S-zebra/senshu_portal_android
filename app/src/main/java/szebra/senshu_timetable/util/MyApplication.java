package szebra.senshu_timetable.util;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Base64;

import androidx.multidex.MultiDexApplication;

import java.security.Key;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import szebra.senshu_timetable.models.Credential;

/**
 * Created by s-zebra on 2018/04/22.
 */

public class MyApplication extends MultiDexApplication {
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
    initCommunicatorCredential();
  }
  
  private void initCommunicatorCredential() {
    Realm realm = Realm.getDefaultInstance();
    Credential credential = realm.where(Credential.class).findFirst();
    if (credential == null) return;
    Credential newCredential = new Credential(credential.getUserName(), credential.getPassword());
    PortalCommunicator.getInstance().setCredential(newCredential);
    realm.close();
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
