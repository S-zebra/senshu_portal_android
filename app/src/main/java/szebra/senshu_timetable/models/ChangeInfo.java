package szebra.senshu_timetable.models;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by s-zebra on 2018/06/26.
 */
public class ChangeInfo extends RealmObject {
  public static final String KYUKO = "休講";
  public static final String CHANGE = "各種変更";
  @PrimaryKey
  public int id;
  public String lectureName;
  public Date date;
  public String afterChangeInfo;
  
  public ChangeInfo() {
  
  }
  
  public ChangeInfo(int id, String lectureName, Date date, String changeInfo) {
    this.id = id;
    this.lectureName = lectureName;
    this.date = date;
    this.afterChangeInfo = changeInfo;
  }
  
  public Date getDate() {
    return date;
  }
  
  public String getAfterChangeInfo() {
    return afterChangeInfo;
  }
  
  public int getId() {
    return id;
  }
  
  public String getLectureName() {
    return lectureName;
  }
}
