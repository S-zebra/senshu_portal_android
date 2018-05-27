package szebra.senshu_timetable.views;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import java.util.Date;

import szebra.senshu_timetable.R;
import szebra.senshu_timetable.models.Lecture;
import szebra.senshu_timetable.util.ClassHours;

import static szebra.senshu_timetable.R.id.cell;
import static szebra.senshu_timetable.R.id.classroom;

/**
 * Created by s-zebra on 2017/11/27.
 */

public class ClassCell extends ConstraintLayout {
  //コンストラクタ
  private TextView className, classRoom, teacherName;
  private Lecture lecture;
  private ConstraintLayout cr;
  private int day, period;
  
  public ClassCell(Context context) {
    this(context, null);
  }
  
  public ClassCell(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }
  
  public ClassCell(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    View layout = View.inflate(context, R.layout.class_cell, this);
    cr = (ConstraintLayout) layout.findViewById(R.id.cell);
    className = (TextView) layout.findViewById(R.id.start_time);
    classRoom = (TextView) layout.findViewById(classroom);
    teacherName = (TextView) layout.findViewById(R.id.teacherName);
    cr = (ConstraintLayout) layout.findViewById(cell);
    className.setText("");
    classRoom.setText("");
    teacherName.setText("");
  }
  
  public void setLecture(Lecture lecture) {
    if (lecture != null) {
      this.lecture = lecture;
      className.setText(lecture.getLectureName());
      classRoom.setText(lecture.getClassroomName());
      teacherName.setText(lecture.getTeacherName());
      period = lecture.getPeriod();
      day = lecture.getDay();
      highlight();
    }
  }
  
  private void highlight() {
    if (lecture.getDay() == new Date().getDay()) {
      cr.setBackgroundColor(getResources().getColor(R.color.colorAccent));
      if (ClassHours.getInstance().isInPeriod(lecture.getPeriod())) {
        cr.setBackgroundColor(getResources().getColor(R.color.colorSubAccent));
      }
    }
  }
}
