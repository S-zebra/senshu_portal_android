package szebra.senshu_timetable.structures;

import android.util.Log;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import io.realm.Realm;
import szebra.senshu_timetable.models.Lecture;

/**
 * Created by s-zebra on 2017/11/27.
 */

public class Timetable {
  private Realm realmConnection;
  
  public static Timetable parseFrom(Document doc) {
    return new Timetable(doc);
  }
  
  private Timetable(Document doc) {
    realmConnection = Realm.getDefaultInstance();
    realmConnection.beginTransaction();
    Log.d("Analyzer", "Job started.");
    Element table = doc.getElementsByClass("acac").first();
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
          String className = currentCell.getElementsByTag("a").first().text()
            .replaceAll("[\\[\\]]", "");
          Elements images = currentCell.getElementsByTag("img"); //変更の画像
          String teacherName, classroomName, changeInfo;
          if (images.size() > 0) {
            Log.d("Analyzer: Image Found", currentCell.textNodes().toString());
            changeInfo = (images.get(0).attr("title"));
            teacherName = currentCell.textNodes().get(5).toString();
            classroomName = currentCell.textNodes().get(6).toString().trim();
          } else {
            Log.d("Analyzer: Image NF", currentCell.textNodes().toString());
            changeInfo = "";
            teacherName = currentCell.textNodes().get(3).toString();
            classroomName = currentCell.textNodes().get(4).toString().trim();
          }
          Lecture currentLecture = new Lecture(kougiId, day, period, className, teacherName, classroomName, changeInfo);
          realmConnection.copyToRealmOrUpdate(currentLecture);
        }
      }
    }
    realmConnection.commitTransaction();
    realmConnection.close();
    Log.d("Analyzer", "Job complete.");
  }
  
}
