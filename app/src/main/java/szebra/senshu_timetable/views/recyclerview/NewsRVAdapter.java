package szebra.senshu_timetable.views.recyclerview;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import szebra.senshu_timetable.R;
import szebra.senshu_timetable.activities.NewsDetailActivity;
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
    holder.replyIcon.setVisibility(item.isReplyRequired() ? VISIBLE : GONE);
    holder.hasAttachIcon.setVisibility(item.hasAttachments() ? VISIBLE : GONE);
    holder.dateLabel.setText(DateFormat.getDateFormat(context).format(item.getPublishStartDate()));
  }
  
  @Override
  public NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    final RecyclerView rv = (RecyclerView) parent;
    final View newsItem = LayoutInflater.from(context).inflate(R.layout.news_item, parent, false);
    newsItem.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        int vPos = rv.getChildLayoutPosition(newsItem);
        Intent intent = new Intent(context, NewsDetailActivity.class);
        intent.putExtra("NEWS_ID", newsList.get(vPos).getId());
        context.startActivity(intent);
      }
    });
    return new NewsViewHolder(newsItem);
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
  protected ImageView importantIcon, newIcon, checkOpenIcon, hasAttachIcon, replyIcon;
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
    this.replyIcon = itemView.findViewById(R.id.news_item_ic_reply);
  }
  
}