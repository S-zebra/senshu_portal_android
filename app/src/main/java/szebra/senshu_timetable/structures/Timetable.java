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
    Log.d("時間割アナライザ", "解析を開始しました");
    Element table = doc.getElementsByClass("acac").first();
    Elements periods = table.getElementsByTag("tr");
    for (int period = 0; period < 7; period++) {
      //先頭行、および「大学行事」の欄を抜かす
      Elements currentPeriod = periods.get(period + 2).getElementsByTag("td");
      for (int day = 0; day < 6; day++) {
        Element currentCell = currentPeriod.get(day);
  
        //aタグがあれば、そこに授業があると見なす
        Element kougiLink = currentCell.getElementsByTag("a").first();
  
        if (kougiLink != null) {
          int kougiId = Integer.parseInt(kougiLink.attr("href").replaceAll(".+=", ""));
          String className = currentCell.getElementsByTag("a").first().text().replaceAll("[\\[\\]]", "");
          String teacherName = currentCell.textNodes().get(3).toString();
          String classroomName = currentCell.textNodes().get(4).toString().trim();
          Lecture currentLecture = new Lecture(kougiId, day, period, className, teacherName, classroomName);
          realmConnection.copyToRealmOrUpdate(currentLecture);
        }
      }
    }
    realmConnection.commitTransaction();
    realmConnection.close();
    Log.d("時間割アナライザ", "解析を終了しました");
  }
  
}
