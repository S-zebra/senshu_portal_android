package szebra.senshu_timetable.views;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;

import szebra.senshu_timetable.R;
import szebra.senshu_timetable.activities.ToDoListActivity;
import szebra.senshu_timetable.models.ChangeInfo;
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
  private ImageView changeIcon;
  private Lecture lecture;
  private int lectureId;
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
    cr = layout.findViewById(R.id.cell);
    className = layout.findViewById(R.id.start_time);
    classRoom = layout.findViewById(classroom);
    teacherName = layout.findViewById(R.id.teacherName);
    cr = layout.findViewById(cell);
    changeIcon = layout.findViewById(R.id.changeIcon);
    className.setText("");
    classRoom.setText("");
    teacherName.setText("");
  }
  
  public void setLecture(final Lecture lecture) {
    if (lecture == null) {
      return;
    }
    this.lecture = lecture;
    lectureId = this.lecture.getId();
    className.setText(lecture.getName());
    classRoom.setText(lecture.getClassroomName());
    teacherName.setText(lecture.getTeacherName());
    period = lecture.getPeriod();
    day = lecture.getDay();
    setClickable(true);
    setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent i = new Intent(getContext(), ToDoListActivity.class);
        i.putExtra("Lecture", lectureId);
        getContext().startActivity(i);
      }
    });
    highlight();
    String changeInfo = lecture.getChangeInfo();
    if (changeInfo == null) {
      return;
    }
    if (changeInfo.equals(ChangeInfo.CHANGE)) {
      changeIcon.setVisibility(VISIBLE);
    } else if (changeInfo.equals(ChangeInfo.KYUKO)) {
      this.setBackgroundColor(getResources().getColor(R.color.cellBackground));
      changeIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_block_black_24dp));
      changeIcon.setVisibility(VISIBLE);
    }
  }
  
  private void highlight() {
    if (lecture.getDay() == (new Date().getDay()) - 1) {
      cr.setBackgroundColor(getResources().getColor(R.color.colorAccent));
      if (ClassHours.getInstance().isInPeriod(lecture.getPeriod())) {
        cr.setBackgroundColor(getResources().getColor(R.color.colorSubAccent));
      }
    }
  }
}
