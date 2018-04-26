package szebra.senshu_timetable.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by s-zebra on 2018/04/21.
 */

public class Lecture extends RealmObject {
  @PrimaryKey
  private long id;
  private int day;
  private int period;
  private String lectureName;
  private String classroomName;
  private String teacherName;
  
  public Lecture() {
  }
  
  public Lecture(long id, int day, int period, String lectureName, String teacherName, String classroomName) {
    this.id = id;
    this.day = day;
    this.period = period;
    this.lectureName = lectureName;
    this.classroomName = classroomName;
    this.teacherName = teacherName;
  }
  
  public long getId() {
    return id;
  }
  
  public void setId(long id) {
    this.id = id;
  }
  
  public int getDay() {
    return day;
  }
  
  public void setDay(int day) {
    this.day = day;
  }
  
  public int getPeriod() {
    return period;
  }
  
  public void setPeriod(int period) {
    this.period = period;
  }
  
  public String getLectureName() {
    return lectureName;
  }
  
  public void setLectureName(String lectureName) {
    this.lectureName = lectureName;
  }
  
  public String getClassroomName() {
    return classroomName;
  }
  
  public void setClassroomName(String classroomName) {
    this.classroomName = classroomName;
  }
  
  public String getTeacherName() {
    return teacherName;
  }
  
  public void setTeacherName(String teacherName) {
    this.teacherName = teacherName;
  }
}
