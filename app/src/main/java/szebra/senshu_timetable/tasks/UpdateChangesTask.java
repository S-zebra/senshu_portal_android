package szebra.senshu_timetable.tasks;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import szebra.senshu_timetable.ChangeType;
import szebra.senshu_timetable.PortalURL;
import szebra.senshu_timetable.models.ChangeInfo;
import szebra.senshu_timetable.tasks.callbacks.TaskCallback;
import szebra.senshu_timetable.util.PortalCommunicator;

/**
 * Created by s-zebra on 2018/06/30.
 */

public final class UpdateChangesTask extends AsyncTask<Void, Void, Throwable> {
  private Realm realm;
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
    realm = Realm.getDefaultInstance();
    realm.beginTransaction();
    realm.delete(ChangeInfo.class);
    PortalCommunicator comm = PortalCommunicator.getInstance();
    try {
      comm.refreshSession();
      Document document = comm.moveTo(PortalCommunicator.MoveMode.GET, PortalURL.CHANGES_URL, null);
      parse(document, ChangeType.休講);
      parse(document, ChangeType.各種変更);
    } catch (Exception e) {
      e.printStackTrace();
      return e;
    } finally {
      realm.commitTransaction();
      realm.close();
    }
    return null;
  }
  
  private void parse(Document doc, ChangeType type) {
    if (doc == null) {
      return;
    }
    Element table = doc.getElementById(type.getTableID());
    if (table == null) {
      return;
    }
    Elements rows = table.getElementsByAttributeValueStarting("class", "tr_y");
    
    RealmResults<ChangeInfo> results = realm.where(ChangeInfo.class).findAllSorted("id", Sort.DESCENDING);
    int lastIndex = 0;
    if (results.size() > 0) {
      lastIndex = results.first().id;
    }
    Log.d("Change/Kyuko Parser", "Parsing");
    for (Element row : rows) {
      //日付
      //Ex. 2018年06月26日 (火曜) 3限
      String dateString = row.getElementsByTag("td").get(1).child(0).text().split(" ")[0];
      Date date = formatDate(dateString);
      
      //内容
      Iterator<TextNode> iterator = row.getElementsByTag("td").get(5).child(0).textNodes().iterator();
      StringBuilder builder = new StringBuilder();
      while (iterator.hasNext()) {
        builder.append(iterator.next().text());
        builder.append('\n');
      }
      String lectureName = row.getElementsByTag("td").get(3).child(0).text().replaceAll("【.+】", "").trim();
      Log.d("ChangeListActivity", "Original: " + lectureName);
      String changeText = builder.toString();
      ChangeInfo newInfo = new ChangeInfo(++lastIndex, type.name(), lectureName, date, changeText.substring(0, changeText.length() - 1));
      realm.copyToRealmOrUpdate(newInfo);
    }
    Log.d("Change/Kyuko Parser", "Parse finish");
  }
  
  private static Date formatDate(String dateString) {
    String[] dateParts = dateString.split("[年月日]");
    Calendar cal = Calendar.getInstance();
    cal.clear();
    cal.set(Integer.parseInt(dateParts[0]), Integer.parseInt(dateParts[1]) - 1, Integer.parseInt(dateParts[2]));
    return cal.getTime();
  }
}
