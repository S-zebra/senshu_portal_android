package szebra.senshu_timetable.tasks;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.lang.ref.WeakReference;

import io.realm.Realm;
import szebra.senshu_timetable.PortalURL;
import szebra.senshu_timetable.models.Lecture;
import szebra.senshu_timetable.tasks.callbacks.TaskCallback;
import szebra.senshu_timetable.util.PortalCommunicator;

/**
 * Created by s-zebra on 2017/11/27.
 */

public final class UpdateTimetableTask extends AsyncTask<Void, Void, Throwable> {
  private WeakReference<TaskCallback> reference;
  
  public void setCallback(TaskCallback callback) {
    this.reference = new WeakReference<>(callback);
  }
  
  @Override
  protected void onPostExecute(Throwable throwable) {
    if (reference.get() == null) return;
    reference.get().onTaskCompleted(throwable);
  }
  
  @Override
  protected Throwable doInBackground(Void... voids) {
    PortalCommunicator communicator = PortalCommunicator.getInstance();
    try {
      communicator.refreshSession();
      Document doc = communicator.moveTo(PortalCommunicator.MoveMode.GET, PortalURL.MY_PAGE_URL, null);
      parse(doc);
    } catch (Exception e) {
      e.printStackTrace();
      return e;
    }
    return null;
  }
  
  private void parse(Document document) {
    Realm realmConnection = Realm.getDefaultInstance();
    realmConnection.beginTransaction();
    Log.d("Analyzer", "Job started.");
    Element table = document.getElementsByClass("acac").first();
    Elements periods = table.getElementsByTag("tr");
    for (int period = 0; period < 7; period++) {
      //先頭行、および「大学行事」の欄を抜かす
      Elements currentPeriod = periods.get(period + 2).getElementsByTag("td");
      for (int day = 0; day < 6; day++) {
        Element currentCell = currentPeriod.get(day);
        
        Element kougiLink = currentCell.getElementsByTag("a").first();
        
        //aタグがあれば、そこに授業があると見なす
        if (kougiLink != null) {
          int kougiId = day * 10 + period;
          Elements images = currentCell.getElementsByTag("img"); //変更の画像
          String className, teacherName, classroomName, changeTypeName = "";
          if (images.size() > 0) {
            Log.d("Analyzer: Image Found", currentCell.textNodes().toString());
            changeTypeName = images.get(0).attr("title");
            teacherName = currentCell.textNodes().get(5).toString();
            classroomName = currentCell.textNodes().get(6).toString().trim();
            className = currentCell.getElementsByTag("a").last().text().replaceAll("[\\[\\]]", "");
          } else {
            Log.d("Analyzer: Image NF", currentCell.textNodes().toString());
            teacherName = currentCell.textNodes().get(3).toString();
            classroomName = currentCell.textNodes().get(4).toString().trim();
            className = currentCell.getElementsByTag("a").first().text().replaceAll("[\\[\\]]", "");
          }
          Log.d("Analyzer", "className: " + className);
          Lecture currentLecture = new Lecture(kougiId, day, period, className, teacherName, classroomName, changeTypeName);
          realmConnection.copyToRealmOrUpdate(currentLecture);
        }
      }
    }
    realmConnection.commitTransaction();
    realmConnection.close();
    Log.d("Analyzer", "Job complete.");
  }
}
