package szebra.senshu_timetable.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import szebra.senshu_timetable.R;

/**
 * Created by s-zebra on 2018/05/27.
 */
public class ToDoViewHolder extends RecyclerView.ViewHolder {
  public TextView titleLabel, dLineLabel, lectureLabel, detailLabel;
  
  public ToDoViewHolder(View itemView) {
    super(itemView);
    titleLabel = itemView.findViewById(R.id.taskNameLabel);
    dLineLabel = itemView.findViewById(R.id.deadlineLabel);
    lectureLabel = itemView.findViewById(R.id.lectureLabel);
    detailLabel = itemView.findViewById(R.id.taskDetailLabel);
  }
}
