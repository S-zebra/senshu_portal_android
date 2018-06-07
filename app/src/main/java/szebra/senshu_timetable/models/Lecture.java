package szebra.senshu_timetable.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by s-zebra on 2018/04/21.
 */

public class Lecture extends RealmObject {
  @PrimaryKey
  private int id;
  private int day;
  private int period;
  private String name;
  private String classroomName;
  private String teacherName;
  
  public Lecture() {
  }
  
  public Lecture(int id, int day, int period, String name, String teacherName, String classroomName) {
    this.id = id;
    this.day = day;
    this.period = period;
    this.name = name;
    this.classroomName = classroomName;
    this.teacherName = teacherName;
  }
  
  public int getId() {
    return id;
  }
  
  public void setId(int id) {
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
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
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
  
  @Override
  public String toString() {
    return getName() + " (" + getTeacherName() + ")";
  }
}
