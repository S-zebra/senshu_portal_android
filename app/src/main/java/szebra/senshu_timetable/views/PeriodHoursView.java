package szebra.senshu_timetable.views;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import szebra.senshu_timetable.R;
import szebra.senshu_timetable.util.ClassHours;

/**
 * Created by s-zebra on May 19, 2017.
 */
public class PeriodHoursView extends ConstraintLayout {
  
  private int period;
  private TextView startTimeLabel, hourLabel, endTimeLabel;
  
  public PeriodHoursView(Context context) {
    super(context);
    init(context);
  }
  
  
  public PeriodHoursView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context);
  }
  
  public PeriodHoursView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    init(context);
  }
  
  private void init(Context context) {
    View layout = View.inflate(context, R.layout.period_hours_view, this);
    Log.d("Inflater", "PeriodHoursView Inflated");
    Log.d("Inflater", "Width: " + this.getWidth());
    startTimeLabel = layout.findViewById(R.id.start_time);
    hourLabel = layout.findViewById(R.id.hour);
    endTimeLabel = layout.findViewById(R.id.end_time);
    startTimeLabel.setText("");
    hourLabel.setText("");
    endTimeLabel.setText("");
  }
  
  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
  }
  
  public void setPeriod(int period) {
    this.period = period;
    Log.d("SetHour", String.valueOf(period));
    startTimeLabel.setText(ClassHours.classBeginTimes[period]);
    hourLabel.setText(String.valueOf(period + 1));
    endTimeLabel.setText(ClassHours.classEndTimes[period]);
  }
  
}
