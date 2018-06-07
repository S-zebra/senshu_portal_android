package szebra.senshu_timetable.recyclerview;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
    holder.dLineLabel.setText(SimpleDateFormat.getDateInstance(DateFormat.MEDIUM).format(deadline));
    holder.lectureLabel.setText(lectureName);
    holder.detailLabel.setText(detailText.isEmpty() ? res.getString(R.string.label_no_text) : detailText);
    realm.close();
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
