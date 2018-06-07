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

/**
 * Created by s-zebra on 2017/11/27.
 */

public class PortalCommunicator {
  
  private static PortalCommunicator instance = new PortalCommunicator();
  public static final String CLASS_NAME = PortalCommunicator.class.getSimpleName();
  
  private boolean loggedIn = false;
  
  private static final String USERNAME_FIELD = "login";
  private static final String PASSWORD_FIELD = "passwd";
  private static final String LOGIN_FAILED_MESSAGE = "失敗しました";
  
  public enum MoveMode {POST, GET}
  
  private Connection connection;
  private HashMap<String, String> credential;
  private Map<String, String> cookies;
  
  public PortalCommunicator() {
    this.credential = new HashMap<>();
    this.cookies = new HashMap<>();
  }
  
  public static synchronized PortalCommunicator getInstance() {
    return instance;
  }
  
  private boolean logIn() throws IOException {
    return logIn(this.credential.get(USERNAME_FIELD), this.credential.get(PASSWORD_FIELD));
  }
  
  public boolean logIn(Credential credential) throws IOException {
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
  public boolean logIn(String userName, String password) throws IOException {
    //Check length
    if (userName == null || password == null || userName.isEmpty() || password.isEmpty()) {
      return false;
    }
    //Get cookies from top page
    Log.d(CLASS_NAME, "GET'ing top page...");
    connection = Jsoup.connect(PortalURL.LOGIN_URL);
    connection.get();
    if (connection.response().cookies().size() > 0) {
      this.cookies = connection.response().cookies();
    }
    Log.d(CLASS_NAME, "Success.");
    
    //Prepare credential map
    credential.put("mode", "Login");
    credential.put(USERNAME_FIELD, userName);
    credential.put(PASSWORD_FIELD, password);
    
    //Post credential + cookies
    connection.data(credential);
    dumpMap(credential, "Credential");
    connection.cookies(cookies);
    
    Log.d(CLASS_NAME, "Posting...");
    Document d = connection.post();
    Log.d(CLASS_NAME, "Post response: " + d.html());
    if (d.html().contains(LOGIN_FAILED_MESSAGE)) {
      Log.e(CLASS_NAME, "Post failed. Username or password is incorrect");
      loggedIn = false;
    } else {
      Log.d(CLASS_NAME, "Post success.");
      if (connection.response().cookies().size() > 0) {
        this.cookies = connection.response().cookies();
      }
      loggedIn = true;
    }
    return loggedIn;
  }
  
  /**
   * Returns whether <code>PortalCommunicator</code> is logged in.
   *
   * @return <code>true</code> if logged in
   */
  public boolean isLoggedIn() {
    return loggedIn;
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
    if (!loggedIn) {
      logIn();
    } else {
      refreshSession();
    }
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
  
  private void refreshSession() throws IOException {
    if (connection.url(PortalURL.MY_PAGE_URL).get().body().html().contains("Error")) {
      logIn();
    }
  }
  
  public void logOut() throws IOException {
    HashMap<String, String> logOutData = new HashMap<>();
    logOutData.put("mode", "Logout");
    moveTo(MoveMode.POST, PortalURL.LOGIN_URL, logOutData);
    loggedIn = false;
  }
  
  private void dumpMap(Map<String, String> map, String name) {
    Iterator<String> iterator = map.keySet().iterator();
    while (iterator.hasNext()) {
      String key = iterator.next();
      Log.d("Dump " + name, key + ": " + map.get(key));
    }
  }
}
