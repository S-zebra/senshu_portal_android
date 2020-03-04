package szebra.senshu_timetable.views.recyclerview;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.constraint.ConstraintLayout;
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
    holder.setNews(newsList.get(position), context);
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
  protected ConstraintLayout background;
  
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
    this.background = itemView.findViewById(R.id.news_item_background);
  }
  
  public void setNews(News news, Context context) {
    bodyPrevLabel.setText(news.getBody());
    subjectLabel.setText(news.getSubject());
    senderLabel.setText(news.getSender());
    importantIcon.setVisibility(news.isImportant() ? View.VISIBLE : GONE);
    newIcon.setVisibility(news.isImportant() ? View.VISIBLE : GONE);
    checkOpenIcon.setVisibility(news.isConfirmOpen() ? VISIBLE : GONE);
    replyIcon.setVisibility(news.isReplyRequired() ? VISIBLE : GONE);
    hasAttachIcon.setVisibility(news.hasAttachments() ? VISIBLE : GONE);
    dateLabel.setText(DateFormat.getDateFormat(context).format(news.getPublishStartDate()));
    if (news.isRead()) {
      bodyPrevLabel.setTypeface(bodyPrevLabel.getTypeface(), Typeface.NORMAL);
      subjectLabel.setTypeface(subjectLabel.getTypeface(), Typeface.NORMAL);
      senderLabel.setTypeface(senderLabel.getTypeface(), Typeface.NORMAL);
      dateLabel.setTypeface(dateLabel.getTypeface(), Typeface.NORMAL);
      background.setBackgroundColor(context.getResources().getColor(R.color.cellBackground));
    }
  }
  
}