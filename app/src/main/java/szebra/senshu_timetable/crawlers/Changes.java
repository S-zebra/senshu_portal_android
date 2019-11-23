package szebra.senshu_timetable.crawlers;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import szebra.senshu_timetable.ChangeType;
import szebra.senshu_timetable.PortalURL;
import szebra.senshu_timetable.models.ChangeInfo;
import szebra.senshu_timetable.util.PortalCommunicator;

/**
 * Created by s-zebra on 2018/06/30.
 */

public final class Changes {
  /**
   * Changes cannot be instantiated (a.k.a. Static class).
   */
  private Changes() {
  
  }
  
  /**
   * Updates the changes information and stores into realm DB.<br>
   * <b>NOTE:</b> <code>Credential</code> must has a valid record.
   *
   * @throws IllegalStateException Not logged in, or received page is invalid
   */
  public static void update() {
    Realm realm = Realm.getDefaultInstance();
    realm.beginTransaction();
    realm.delete(ChangeInfo.class);
    realm.commitTransaction();
    realm.close();
    
    new AsyncTask<Void, Void, Document>() {
      
      @Override
      protected Document doInBackground(Void... voids) {
        PortalCommunicator communicator = PortalCommunicator.getInstance();
        try {
          return communicator.moveTo(PortalCommunicator.MoveMode.GET, PortalURL.CHANGES_URL, null);
        } catch (IOException e) {
          cancel(true);
          return null;
        }
      }
      
      @Override
      protected void onPostExecute(Document document) {
        if (document != null) {
          Changes.parse(document, ChangeType.休講);
          Changes.parse(document, ChangeType.各種変更);
        }
      }
      
      @Override
      protected void onCancelled() {
        Log.e("getChangeInfoFromPortal", "Error");
      }
    }.execute();
  }
  
  private static void parse(Document doc, ChangeType type) {
    Realm realm = Realm.getDefaultInstance();
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
      realm.beginTransaction();
      realm.copyToRealmOrUpdate(newInfo);
      realm.commitTransaction();
    }
    Log.d("Change/Kyuko Parser", "Parse finish");
    realm.close();
  }
  
  private static Date formatDate(String dateString) {
    String[] dateParts = dateString.split("[年月日]");
    Calendar cal = Calendar.getInstance();
    cal.clear();
    cal.set(Integer.parseInt(dateParts[0]), Integer.parseInt(dateParts[1]) - 1, Integer.parseInt(dateParts[2]));
    return cal.getTime();
  }
}
