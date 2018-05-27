package szebra.senshu_timetable.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import io.realm.RealmResults;
import szebra.senshu_timetable.R;
import szebra.senshu_timetable.models.ToDo;

/**
 * Created by s-zebra on 2018/05/27.
 */
public class ToDoRVAdapter extends RecyclerView.Adapter<ToDoViewHolder> {
  private RealmResults<ToDo> todos;
  
  public ToDoRVAdapter(RealmResults<ToDo> todos) {
    this.todos = todos;
  }
  
  @Override
  public void onBindViewHolder(ToDoViewHolder holder, int position) {
    onBindViewHolder(holder, position, null);
  }
  
  @Override
  public void onBindViewHolder(ToDoViewHolder holder, int position, List<Object> payloads) {
    holder.titleLabel.setText(todos.get(position).getTitle());
    holder.dLineLabel.setText(todos.get(position).getDeadline().toString());
    holder.lectureLabel.setText(todos.get(position).getLecture().getName());
    holder.detailLabel.setText(todos.get(position).getDetailText());
  }
  
  @Override
  public int getItemCount() {
    return todos.size();
  }
  
  @Override
  public ToDoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View inflatedView = View.inflate(parent.getContext(), R.layout.todo_layout, parent);
    ToDoViewHolder viewHolder = new ToDoViewHolder(inflatedView);
    return viewHolder;
  }
}
