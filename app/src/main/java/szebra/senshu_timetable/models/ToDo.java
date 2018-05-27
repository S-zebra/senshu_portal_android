package szebra.senshu_timetable.models;

import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by s-zebra on 2018/05/27.
 */
public class ToDo extends RealmObject {
  int id;
  String title, detailText;
  Lecture lecture;
  Date deadline;
  
  public ToDo() {
  
  }
  
  public ToDo(int id, String title, Date deadline, Lecture lecture, String detailText) {
    this.id = id;
    this.title = title;
    this.detailText = detailText;
    this.deadline = deadline;
    this.lecture = lecture;
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
  
  public Lecture getLecture() {
    return lecture;
  }
  
  public void setLecture(Lecture lecture) {
    this.lecture = lecture;
  }
  
  public Date getDeadline() {
    return deadline;
  }
  
  public void setDeadline(Date deadline) {
    this.deadline = deadline;
  }
}
