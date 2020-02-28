package szebra.senshu_timetable.tasks;

import android.os.AsyncTask;

import java.lang.ref.WeakReference;

import szebra.senshu_timetable.models.Credential;
import szebra.senshu_timetable.tasks.callbacks.TaskCallback;
import szebra.senshu_timetable.util.PortalCommunicator;

/**
 * Created by s-zebra on 2/28/20.
 */
public class LoginTask extends AsyncTask<Credential, Void, Throwable> {
  PortalCommunicator communicator = PortalCommunicator.getInstance();
  private WeakReference<TaskCallback> reference;
  
  public void setCallback(TaskCallback callback) {
    this.reference = new WeakReference<>(callback);
  }
  
  @Override
  protected Throwable doInBackground(Credential... credentials) {
    try {
      communicator.logIn(credentials[0]);
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
