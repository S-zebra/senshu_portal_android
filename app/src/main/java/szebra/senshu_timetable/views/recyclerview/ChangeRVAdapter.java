package szebra.senshu_timetable.views.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.List;

import szebra.senshu_timetable.R;
import szebra.senshu_timetable.models.ChangeInfo;

/**
 * Created by s-zebra on 2018/06/30.
 */
public class ChangeRVAdapter extends RecyclerView.Adapter<ChangeViewHolder> {
  private LayoutInflater inflater;
  private List<ChangeInfo> changeList;
  
  @Override
  public void onBindViewHolder(ChangeViewHolder holder, int position) {
  
  }
  
  //TODO: リストここに渡す
  public ChangeRVAdapter(Context context, List<ChangeInfo> results) {
    inflater = LayoutInflater.from(context);
    this.changeList = results;
  }
  
  @Override
  public void onBindViewHolder(ChangeViewHolder holder, int position, List<Object> payloads) {
    super.onBindViewHolder(holder, position, payloads);
    holder.changeTextLabel.setText(changeList.get(position).afterChangeInfo);
    DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
    holder.changeDateLabel.setText(dateFormat.format(changeList.get(position).date));
  }
  
  @Override
  public int getItemCount() {
    return changeList.size();
  }
  
  @Override
  public ChangeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new ChangeViewHolder(inflater.inflate(R.layout.change_card, parent, false));
  }
}

class ChangeViewHolder extends RecyclerView.ViewHolder {
  public TextView changeDateLabel, changeTextLabel;
  
  public ChangeViewHolder(View itemView) {
    super(itemView);
    changeDateLabel = itemView.findViewById(R.id.changeDateLabel);
    changeTextLabel = itemView.findViewById(R.id.changeTextLabel);
  }
}