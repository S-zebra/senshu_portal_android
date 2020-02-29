package szebra.senshu_timetable.views.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import szebra.senshu_timetable.R;
import szebra.senshu_timetable.models.News;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by s-zebra on 2/29/20.
 */
public class NewsRVAdapter extends RecyclerView.Adapter<NewsViewHolder> {
  private LayoutInflater inflater;
  private Context context;
  private List<News> newsList;
  
  public NewsRVAdapter(LayoutInflater inflater, Context context, List<News> newsList) {
    this.inflater = inflater;
    this.context = context;
    this.newsList = newsList;
  }
  
  public NewsRVAdapter(Context context, List<News> newsList) {
    this.inflater = LayoutInflater.from(context);
    this.context = context;
    this.newsList = newsList;
  }
  
  @Override
  public void onBindViewHolder(NewsViewHolder holder, int position, List<Object> payloads) {
    super.onBindViewHolder(holder, position, payloads);
    News item = newsList.get(position);
    holder.bodyPrevLabel.setText(item.getBody());
    holder.subjectLabel.setText(item.getSubject());
    holder.senderLabel.setText(item.getSender());
    holder.importantIcon.setVisibility(item.isImportant() ? View.VISIBLE : GONE);
    holder.newIcon.setVisibility(item.isImportant() ? View.VISIBLE : GONE);
    holder.checkOpenIcon.setVisibility(item.isConfirmOpen() ? VISIBLE : GONE);
    holder.dateLabel.setText(String.format("%d月%02d日", item.getPublishStartDate().getMonth() + 1, item.getPublishStartDate().getDate()));
    // TODO: Do more view setup
  }
  
  @Override
  public NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new NewsViewHolder(inflater.inflate(R.layout.news_item, parent, false));
  }
  
  @Override
  public void onBindViewHolder(NewsViewHolder holder, int position) {
  }
  
  @Override
  public int getItemCount() {
    return newsList.size();
  }
}

class NewsViewHolder extends RecyclerView.ViewHolder {
  protected TextView senderLabel, subjectLabel, bodyPrevLabel, dateLabel;
  protected ImageView importantIcon, newIcon, checkOpenIcon, hasAttachIcon;
  protected CheckBox checkBox;
  
  public NewsViewHolder(View itemView) {
    super(itemView);
    this.senderLabel = itemView.findViewById(R.id.news_item_sender);
    this.subjectLabel = itemView.findViewById(R.id.news_item_subject);
    this.bodyPrevLabel = itemView.findViewById(R.id.news_item_body_prev);
    this.dateLabel = itemView.findViewById(R.id.news_item_date);
    this.importantIcon = itemView.findViewById(R.id.news_item_ic_imp);
    this.newIcon = itemView.findViewById(R.id.news_item_ic_new);
    this.checkOpenIcon = itemView.findViewById(R.id.news_item_ic_chkopen);
    this.hasAttachIcon = itemView.findViewById(R.id.news_item_ic_attach);
    this.checkBox = itemView.findViewById(R.id.news_item_checkbox);
  }
  
}