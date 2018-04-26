package szebra.senshu_timetable.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by s-zebra on 2018/04/23.
 */

public class Credential extends RealmObject {
  @PrimaryKey
  private int id; //Because app never has multiple accounts
  private String userName;
  private String password;
  
  public Credential(int id, String userName, String password) {
    this.id = id;
    this.userName = userName;
    this.password = password;
  }
  
  public Credential() {
  }
  
  public String getUserName() {
    return userName;
  }
  
  public void setUserName(String userName) {
    this.userName = userName;
  }
  
  public String getPassword() {
    return password;
  }
  
  public void setPassword(String password) {
    this.password = password;
  }
}
