package szebra.senshu_timetable.models;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by s-zebra on 2018/05/27.
 */
public class ToDo extends RealmObject {
  @PrimaryKey
  int id;
  String title, detailText;
  int lectureId;
  Date deadline;
  
  public ToDo() {
  
  }
  
  public ToDo(int id, String title, Date deadline, int lectureId, String detailText) {
    this.id = id;
    this.title = title;
    this.detailText = detailText;
    this.deadline = deadline;
    this.lectureId = lectureId;
  }
  
  public int getId() {
    return id;
  }
  
  public void setId(int id) {
    this.id = id;
  }
  
  public String getTitle() {
    return title;
  }
  
  public void setTitle(String title) {
    this.title = title;
  }
  
  public String getDetailText() {
    return detailText;
  }
  
  public void setDetailText(String detailText) {
    this.detailText = detailText;
  }
  
  public int getLectureId() {
    return lectureId;
  }
  
  public void setLectureId(int lectureId) {
    this.lectureId = lectureId;
  }
  
  public Date getDeadline() {
    return deadline;
  }
  
  public void setDeadline(Date deadline) {
    this.deadline = deadline;
  }
}
