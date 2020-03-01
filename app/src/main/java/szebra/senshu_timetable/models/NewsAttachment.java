package szebra.senshu_timetable.models;

import io.realm.RealmObject;

/**
 * Created by s-zebra on 2/29/20.
 */
public class NewsAttachment extends RealmObject {
  private String fileParams;
  private String name;
  
  public NewsAttachment(String fileParams, String name) {
    this.fileParams = fileParams;
    this.name = name;
  }
  
  public NewsAttachment() {
  }
  
  public String getUri() {
    return fileParams;
  }
  
  public void setUri(String fileParams) {
    this.fileParams = fileParams;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
}
