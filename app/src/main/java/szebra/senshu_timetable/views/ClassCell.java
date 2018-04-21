package szebra.senshu_timetable.views;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import szebra.senshu_timetable.structures.Lecture;
import szebra.senshu_timetable.R;

import static szebra.senshu_timetable.R.id.cell;
import static szebra.senshu_timetable.R.id.classroom;

/**
 * Created by s-zebra on 2017/11/27.
 */

public class ClassCell extends ConstraintLayout {
  //コンストラクタ
  private TextView className, classRoom, teacherName;
  private ConstraintLayout cr;
  
  public ClassCell(Context context) {
    this(context, null);
  }
  
  public ClassCell(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }
  
  public ClassCell(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    View layout = View.inflate(context, R.layout.class_cell, this);
    className = (TextView) layout.findViewById(R.id.className);
    classRoom = (TextView) layout.findViewById(classroom);
    teacherName = (TextView) layout.findViewById(R.id.teacherName);
    cr = (ConstraintLayout) layout.findViewById(cell);
    className.setText("");
    classRoom.setText("");
    teacherName.setText("");
  }
  
  public void setLecture(Lecture lecture) {
    if (lecture != null) {
      className.setText(lecture.getLectureName());
      classRoom.setText(lecture.getClassroomName());
      teacherName.setText(lecture.getTeacherName());
    }
  }
  
  public void setCellColor(int color) {
    cr.setBackgroundColor(color);
  }
  
  public void setClassName(String cName) {
    Log.d("setClassName", "cName: " + cName);
    className.setText(cName);
  }
  
  public void setClassRoom(String crName) {
    Log.d("setClassName", "crName: " + crName);
    classRoom.setText(crName);
  }
  
  public void setTeacherName(String tName) {
    Log.d("setClassName", "tName: " + tName);
    teacherName.setText(tName);
  }
}
