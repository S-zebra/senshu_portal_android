package szebra.senshu_timetable.tasks;

import szebra.senshu_timetable.R;

/**
 * Created by s-zebra on 2/29/20.
 */
public enum NewsCategory {
  PRIVATE(1, R.string.tab_news_private),
  PUBLIC(2, R.string.tab_news_public),
  JOBS(102, R.string.tab_news_jobs);
  
  private int numVal;
  private int strId;
  
  NewsCategory(int numVal, int strId) {
    this.numVal = numVal;
    this.strId = strId;
  }
  
  public int getNumVal() {
    return numVal;
  }
  
  public int getStrId() {
    return strId;
  }
  
  public static NewsCategory fromCode(int code) {
    for (NewsCategory c : NewsCategory.values()) {
      if (c.numVal == code) return c;
    }
    return null;
  }
}
