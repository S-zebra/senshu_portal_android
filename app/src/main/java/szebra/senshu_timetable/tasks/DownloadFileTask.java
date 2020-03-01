package szebra.senshu_timetable.tasks;

import android.os.AsyncTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

import szebra.senshu_timetable.PortalURL;
import szebra.senshu_timetable.tasks.callbacks.DownloadFileCallback;
import szebra.senshu_timetable.util.PortalCommunicator;

/**
 * Created by s-zebra on 3/2/20.
 */
public class DownloadFileTask extends AsyncTask<String, Void, Exception> {
  private WeakReference<DownloadFileCallback> reference;
  private File outFile;
  
  public void setFile(File destination) {
    this.outFile = destination;
  }
  
  public void setCallback(DownloadFileCallback callback) {
    reference = new WeakReference<>(callback);
  }
  
  @Override
  protected Exception doInBackground(String... strings) {
    try {
      PortalCommunicator communicator = PortalCommunicator.getInstance();
      byte[] data = communicator.getBytes(PortalURL.NEWS_ATTACHMENT + strings[0]);
      FileOutputStream fos = new FileOutputStream(outFile);
      fos.write(data);
      fos.close();
      return null;
    } catch (IOException | InvalidCredentialException e) {
      e.printStackTrace();
      return e;
    }
  }
  
  @Override
  protected void onPostExecute(Exception e) {
    if (reference.get() == null) return;
    reference.get().onDownloadCompleted(e);
  }
}
