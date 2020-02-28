package szebra.senshu_timetable.util;

import android.support.annotation.NonNull;
import android.util.Log;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import szebra.senshu_timetable.PortalURL;
import szebra.senshu_timetable.models.Credential;
import szebra.senshu_timetable.tasks.InvalidCredentialException;

/**
 * Created by s-zebra on 2017/11/27.
 */

public class PortalCommunicator {
  
  private static PortalCommunicator instance = new PortalCommunicator();
  public static final String CLASS_NAME = PortalCommunicator.class.getSimpleName();
  
  private static final String USERNAME_FIELD = "login";
  private static final String PASSWORD_FIELD = "passwd";
  private static final String LOGIN_FAILED_MESSAGE = "失敗しました";
  
  public enum MoveMode {POST, GET}
  
  private Connection connection;
  private Credential credential;
  private Map<String, String> cookies;
  
  public PortalCommunicator() {
    this.cookies = new HashMap<>();
  }
  
  public static synchronized PortalCommunicator getInstance() {
    return instance;
  }
  
  public void setCredential(Credential credential) {
    this.credential = credential;
  }
  
  public void refreshSession() throws IOException, InvalidCredentialException {
    if (connection == null) {
      logIn(credential);
    }
    connection.get();
    if (connection.response().url().toString().contains("Error.php")) {
      logIn(credential);
    }
  }
  
  public boolean logIn(Credential credential) throws IOException, InvalidCredentialException {
    this.credential = credential;
    return logIn(credential.getUserName(), credential.getPassword());
  }
  
  /**
   * Log into portal with given <code>userName</code> and <code>password</code>.
   *
   * @param userName User name
   * @param password Password
   * @return <code>true</code>: Successful.<br>
   * <code>false</code>: <code>userName</code> and/or <code>password</code> is not correct.
   * @throws IOException Thrown when offline
   */
  public boolean logIn(String userName, String password) throws IOException, InvalidCredentialException {
    
    //Check length
    if (userName.isEmpty()) {
      throw new IllegalArgumentException("Username is empty");
    }
    if (password.isEmpty()) {
      throw new IllegalArgumentException("Password is empty");
    }
    
    //Get cookies from top page
    Log.d(CLASS_NAME, "GET'ing top page...");
    connection = Jsoup.connect(PortalURL.LOGIN_URL);
    connection.get();
    if (connection.response().cookies().size() > 0) {
      this.cookies = connection.response().cookies();
    }
    Log.d(CLASS_NAME, "Success.");
  
    HashMap<String, String> reqBody = new HashMap<>();
    //Prepare credential map
    reqBody.put("mode", "Login");
    reqBody.put(USERNAME_FIELD, userName);
    reqBody.put(PASSWORD_FIELD, password);
    
    //Post credential + cookies
    connection.data(reqBody);
//    dumpMap(credential, "Credential");
    connection.cookies(cookies);
    
    Log.d(CLASS_NAME, "Posting...");
    Document d = connection.post();
    Log.d(CLASS_NAME, "Post response: " + d.html());
    if (d.html().contains(LOGIN_FAILED_MESSAGE)) {
      Log.e(CLASS_NAME, "Post failed. ");
      throw new InvalidCredentialException("Username or password is incorrect");
    } else {
      Log.d(CLASS_NAME, "Post success.");
      if (connection.response().cookies().size() > 0) {
        this.cookies = connection.response().cookies();
      }
      return true;
    }
  }
  
  
  /**
   * Sends an HTTP request to specified URL and gets <code>Document</code>.
   *
   * @param mode HTTP method (GET or POST).
   * @param URL  URL to retrieve <code>Document</code> from.
   * @param data Data to apply to HTTP request header.
   * @return Document of Jsoup.
   * @throws IOException Should be thrown when the device is offline.
   */
  public synchronized Document moveTo(MoveMode mode, @NonNull String URL, HashMap<String, String> data) throws IOException {
    connection.url(URL);
    connection.cookies(cookies);
    dumpMap(cookies, "cookies");
    if (data != null) {
      connection.data(data);
      dumpMap(data, "data");
    }
    Document doc;
  
    if (mode == MoveMode.POST) {
      doc = connection.post();
    } else {
      doc = connection.get();
    }
    if (connection.response().cookies().size() > 0) {
      this.cookies = connection.response().cookies();
    }
    Log.d("Communicator", "Received content from " + URL + ": \n" + doc.html());
    return doc;
  }
  
  public void logOut() throws IOException {
    HashMap<String, String> logOutData = new HashMap<>();
    logOutData.put("mode", "Logout");
    moveTo(MoveMode.POST, PortalURL.LOGIN_URL, logOutData);
  }
  
  private void dumpMap(Map<String, String> map, String name) {
    Iterator<String> iterator = map.keySet().iterator();
    while (iterator.hasNext()) {
      String key = iterator.next();
      Log.d("Dump " + name, key + ": " + map.get(key));
    }
  }
}
