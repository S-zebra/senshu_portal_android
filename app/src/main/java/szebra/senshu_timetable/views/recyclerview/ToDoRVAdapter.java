package szebra.senshu_timetable.views.recyclerview;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import szebra.senshu_timetable.R;
import szebra.senshu_timetable.models.Lecture;
import szebra.senshu_timetable.models.ToDo;

/**
 * Created by s-zebra on 2018/05/27.
 */
public class ToDoRVAdapter extends RecyclerView.Adapter<ToDoViewHolder> {
  private RealmResults<ToDo> todos;
  private LayoutInflater inflater;
  private Resources res;
  
  public ToDoRVAdapter(Context context, RealmResults<ToDo> todos) {
    this.todos = todos;
    this.res = context.getResources();
    inflater = LayoutInflater.from(context);
  }
  
  @Override
  public void onBindViewHolder(ToDoViewHolder holder, int position) {
    onBindViewHolder(holder, position, null);
  }
  
  @Override
  public void onBindViewHolder(ToDoViewHolder holder, int position, List<Object> payloads) {
    Realm realm = Realm.getDefaultInstance();
    Lecture lecture = realm.where(Lecture.class).equalTo("id", todos.get(position).getLectureId()).findFirst();
    String title = todos.get(position).getTitle();
    Date deadline = todos.get(position).getDeadline();
    String lectureName = lecture.getName();
    String detailText = todos.get(position).getDetailText();
  
    holder.titleLabel.setText(title.isEmpty() ? res.getString(R.string.label_no_title) : title);
    int deadlineGap = getDeadlineGap(deadline);
    switch (deadlineGap) {
      case 0:
        holder.dLineLabel.setText(res.getString(R.string.label_today));
        break;
      case 1:
        holder.dLineLabel.setText(res.getString(R.string.label_tomorrow));
        break;
      default:
        if (deadlineGap < 10) {
          holder.dLineLabel.setText(res.getString(R.string.label_in_ndays, deadlineGap));
        } else {
          holder.dLineLabel.setText(SimpleDateFormat.getDateInstance(DateFormat.MEDIUM).format(deadline));
        }
    }
    holder.lectureLabel.setText(lectureName);
    holder.detailLabel.setText(detailText.isEmpty() ? res.getString(R.string.label_no_text) : detailText);
    realm.close();
  }
  
  /**
   * 日数の差を取得
   *
   * @param date 取得対象のDate
   * @return 日数
   */
  private int getDeadlineGap(Date date) {
    Calendar todayCal = Calendar.getInstance();
    todayCal.clear(Calendar.HOUR_OF_DAY);
    todayCal.clear(Calendar.MINUTE);
    todayCal.clear(Calendar.SECOND);
    todayCal.clear(Calendar.MILLISECOND);
    
    Date today = todayCal.getTime();
    Log.d("DateGap", date.toString() + ", " + today.toString());
    long diffMS = Math.abs(date.getTime() - today.getTime());
    return (int) (diffMS / 86400000);
  }
  
  @Override
  public int getItemCount() {
    return todos.size();
  }
  
  @Override
  public ToDoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new ToDoViewHolder(inflater.inflate(R.layout.to_do_row, parent, false));
  }
}

class ToDoViewHolder extends RecyclerView.ViewHolder {
  public TextView titleLabel, dLineLabel, lectureLabel, detailLabel;
  
  public ToDoViewHolder(View itemView) {
    super(itemView);
    titleLabel = itemView.findViewById(R.id.taskNameLabel);
    dLineLabel = itemView.findViewById(R.id.deadlineLabel);
    lectureLabel = itemView.findViewById(R.id.lectureLabel);
    detailLabel = itemView.findViewById(R.id.taskDetailLabel);
  }
}
