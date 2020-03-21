package szebra.senshu_timetable.tasks;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.nodes.Document;

import java.lang.ref.WeakReference;

import io.realm.Realm;
import szebra.senshu_timetable.PortalURL;
import szebra.senshu_timetable.models.Credential;
import szebra.senshu_timetable.tasks.callbacks.TaskCallback;
import szebra.senshu_timetable.util.PortalCommunicator;

/**
 * Created by s-zebra on 2/28/20.
 */
public class LoginTask extends AsyncTask<Credential, Void, Throwable> {
  PortalCommunicator communicator = PortalCommunicator.getInstance();
  private WeakReference<TaskCallback> reference;
  private Realm realm;
  
  public void setCallback(TaskCallback callback) {
    this.reference = new WeakReference<>(callback);
  }
  
  @Override
  protected Throwable doInBackground(Credential... credentials) {
    try {
      communicator.setCredential(credentials[0]);
      communicator.logIn();
      realm = Realm.getDefaultInstance();
  
      //おそらく、ログイン後1回はページ遷移する必要がある
      communicator.get(PortalURL.MY_PAGE_URL);
      Document doc = communicator.get(PortalURL.LOGIN_URL);
      Log.d(getClass().getSimpleName(), "doInBackground(): " + doc.html());
      String dataText = doc.getElementById("login_data").text().trim();
      String userFName = dataText.substring(0, dataText.indexOf("さんようこそ"));
  
      realm.beginTransaction();
      credentials[0].setUserFullName(userFName);
      realm.commitTransaction();
      realm.close();
      return null;
    } catch (Exception e) {
      e.printStackTrace();
      return e;
    }
  }
  
  @Override
  protected void onPostExecute(Throwable aThrowable) {
    if (reference.get() == null) return;
    reference.get().onTaskCompleted(aThrowable);
  }
}
