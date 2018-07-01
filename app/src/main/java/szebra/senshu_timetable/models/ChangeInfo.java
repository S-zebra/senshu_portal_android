package szebra.senshu_timetable.models;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import szebra.senshu_timetable.ChangeType;

/**
 * Created by s-zebra on 2018/06/26.
 */
public class ChangeInfo extends RealmObject {
  @PrimaryKey
  public int id;
  public String type;
  public String lectureName;
  public Date date;
  public String afterChangeInfo;
  
  public ChangeInfo() {
  
  }
  
  public ChangeInfo(int id, String type, String lectureName, Date date, String changeInfo) {
    this.id = id;
    this.lectureName = lectureName;
    this.date = date;
    this.type = type;
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
  
  public ChangeType getType() {
    return ChangeType.valueOf(this.type);
  }
  
  public void setType(String changeType) {
    this.type = changeType;
  }
}
