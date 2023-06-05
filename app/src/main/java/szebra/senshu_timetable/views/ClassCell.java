package szebra.senshu_timetable.views;

import static szebra.senshu_timetable.R.id.cell;
import static szebra.senshu_timetable.R.id.classroom;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.Date;

import szebra.senshu_timetable.R;
import szebra.senshu_timetable.activities.ChangeListActivity;
import szebra.senshu_timetable.models.Lecture;
import szebra.senshu_timetable.util.ClassHours;

/**
 * Created by s-zebra on 2017/11/27.
 */

public class ClassCell extends ConstraintLayout {
  //コンストラクタ
  private final TextView className;
  private final TextView classRoom;
  private final TextView teacherName;
  private final ImageView changeIcon;
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
        Intent i = new Intent(getContext(), ChangeListActivity.class); //ToDoActivity?知らない子ですねぇ…
        i.putExtra("Lecture", lecture.getName());
        Log.d("Intent (Lecture)", lecture.getName());
        getContext().startActivity(i);
      }
    });
    highlight();
    Log.d("ClassCell", lecture.getChangeTypeName());
  
    String typeName = lecture.getChangeTypeName();
    if (typeName.equals("各種変更")) {
      changeIcon.setVisibility(VISIBLE);
    } else if (typeName.equals("休講")) {
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
