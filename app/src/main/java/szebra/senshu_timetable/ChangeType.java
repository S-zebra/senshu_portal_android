package szebra.senshu_timetable;

import szebra.senshu_timetable.util.MyApplication;

/**
 * Created by s-zebra on 2018/07/01.
 */
public enum ChangeType {
  
  各種変更("table_kubun_rinji", R.string.change), 休講("table_kubun_kyuko", R.string.kyuko);
  
  private int resid;
  private String tableID;
  
  ChangeType(String tableID, int resid) {
    this.tableID = tableID;
    this.resid = resid;
  }
  
  @Override
  public String toString() {
    return this.tableID;
  }
  
  public String getTableID() {
    return tableID;
  }
  
  public String getTranslatedName() {
    return MyApplication.getInstance().getString(this.resid);
  }
}
