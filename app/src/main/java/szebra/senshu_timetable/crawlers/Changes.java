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
import szebra.senshu_timetable.PortalURL;
import szebra.senshu_timetable.models.ChangeInfo;
import szebra.senshu_timetable.util.PortalCommunicator;

/**
 * Created by s-zebra on 2018/06/30.
 */

public final class Changes {
  private Changes() {
  
  }
  
  public static void update() {
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
        Changes.parse(document);
      }
      
      @Override
      protected void onCancelled() {
        Log.e("getChangeInfoFromPortal", "Error");
      }
    }.execute();
  }
  
  private static void parse(Document doc) {
    if (doc == null) {
      Log.e("ChangeParser", "Error while retrieving the document.");
      return;
    }
    Elements rows = doc.getElementById("table_kubun_rinji").getElementsByClass("tr_y2");
    Realm realm = Realm.getDefaultInstance();
    RealmResults<ChangeInfo> results = realm.where(ChangeInfo.class).findAllSorted("id", Sort.DESCENDING);
    int lastIndex = 0;
    if (results.size() > 0) {
      lastIndex = results.first().id;
    }
    Log.d("Changes", "Parsing");
    for (Element row : rows) {
      //日付
      //Ex. 2018年06月26日 (火曜) 3限
      String dateString = row.getElementsByTag("td").get(1).child(0).text().split(" ")[0];
      String[] dateParts = dateString.split("[年月日]");
      Calendar cal = Calendar.getInstance();
      cal.clear();
      cal.set(Integer.parseInt(dateParts[0]), Integer.parseInt(dateParts[1]), Integer.parseInt(dateParts[2]));
      Date date = cal.getTime();
      
      //内容
      Iterator<TextNode> iterator = row.getElementsByTag("td").get(5).child(0).textNodes().iterator();
      StringBuilder builder = new StringBuilder();
      while (iterator.hasNext()) {
        builder.append(iterator.next().text());
        builder.append('\n');
      }
      String lectureName = row.getElementsByTag("td").get(3).child(0).text().replaceAll("【.+】", "").trim();
      Log.d("ChangeListActivity", "Original: " + lectureName);
      
      ChangeInfo newInfo = new ChangeInfo(++lastIndex, lectureName, date, builder.toString());
      realm.beginTransaction();
      realm.copyToRealmOrUpdate(newInfo);
      realm.commitTransaction();
    }
    realm.close();
    Log.d("Changes", "Parse finish");
  }
}