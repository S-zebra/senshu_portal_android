package szebra.senshu_timetable.structures;

import android.util.Log;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by s-zebra on 2017/11/27.
 */

public class Timetable {
  private Lecture[][] lectures;
  
  public static Timetable parseFrom(Document doc) {
    return new Timetable(doc);
  }
  
  private Timetable(Document doc) {
    
    lectures = new Lecture[6][7];
    Element table = doc.getElementsByClass("acac").first();
    Elements periods = table.getElementsByTag("tr");
    Log.d("Periods.size()", String.valueOf(periods.size()));
    for (int period = 0; period < 7; period++) {
      //1行
      //先頭行、および「大学行事」の欄を抜かす
      Elements currentPeriod = periods.get(period + 2).getElementsByTag("td");
      
      for (int day = 0; day < 6; day++) {
        Element currentCell = currentPeriod.get(day);
        if (currentCell.text().contains("大学行事")) continue;
        
        //aタグが含まれていなければ、そこに授業はないと見なす
        if (currentCell.getElementsByTag("a").size() == 0) {
          lectures[day][period] = null;
        } else {
          String className = currentCell.getElementsByTag("a").first().text().replaceAll("[\\[\\]]", "");
          String teacherName = currentCell.textNodes().get(3).toString();
          String classroomName = currentCell.textNodes().get(4).toString().trim();
          lectures[day][period] = new Lecture(className, teacherName, classroomName);
        }
      }
      
    }
    
  }
  
  public Lecture getLecture(int day, int period) {
    return lectures[day][period];
  }
  
}
