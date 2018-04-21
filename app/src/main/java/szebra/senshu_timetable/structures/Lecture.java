package szebra.senshu_timetable.structures;

/**
 * Created by s-zebra on 2018/04/21.
 */

public class Lecture {
  private final String lectureName;
  private final String classroomName;
  private final String teacherName;
  
  public Lecture(String lectureName, String teacherName, String classroomName) {
    this.lectureName = lectureName;
    this.classroomName = classroomName;
    this.teacherName = teacherName;
  }
  
  public String getLectureName() {
    return lectureName;
  }
  
  public String getClassroomName() {
    return classroomName;
  }
  
  public String getTeacherName() {
    return teacherName;
  }
}
