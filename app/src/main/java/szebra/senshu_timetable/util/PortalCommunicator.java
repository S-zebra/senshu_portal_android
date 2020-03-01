package szebra.senshu_timetable.util;

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
  
  public boolean hasCredential() {
    return credential != null;
  }
  
  private void refreshSession() throws IOException, InvalidCredentialException {
    if (connection == null) {
      logIn();
    }
    connection.get();
    if (connection.response().url().toString().contains("Error.php")) {
      logIn();
    }
  }
  
  public void logIn() throws IOException, InvalidCredentialException {
    logIn(credential.getUserName(), credential.getPassword());
  }
  
  /**
   * Log into portal with given <code>userName</code> and <code>password</code>.
   *
   * @param userName User name
   * @param password Password
   * @throws IOException                Thrown when offline
   * @throws InvalidCredentialException When Username or password is not correct.
   */
  public void logIn(String userName, String password) throws IOException, InvalidCredentialException {
    
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
    }
  }
  
  public Document get(String url) throws IOException, InvalidCredentialException {
    return getInternal(url, 0);
  }
  
  private Document getInternal(String url, int count) throws IOException, InvalidCredentialException {
    if (connection == null) logIn();
    connection.url(url);
    connection.cookies(cookies);
    Document doc = connection.get();
    Log.d(getClass().getSimpleName(), "getInternal(): GET'ed: " + doc.text());
    if (connection.response().url().toString().contains("Error.php")) {
      if (count >= 2) throw new IOException("Unknown Server error");
      logIn();
      getInternal(url, ++count);
    }
    if (connection.response().cookies().size() > 0) {
      this.cookies = connection.response().cookies();
    }
    return doc;
  }
  
  public Document post(String url, Map<String, String> data) throws IOException, InvalidCredentialException {
    return postInternal(url, data, 0);
  }
  
  private Document postInternal(String url, Map<String, String> data, int count) throws IOException, InvalidCredentialException {
    if (connection == null) logIn();
    connection.url(url);
    connection.cookies(cookies);
    if (data != null) {
      connection.data(data);
      dumpMap(data, "data");
    }
    Document doc = connection.post();
    if (connection.response().url().toString().contains("Error.php")) {
      if (count >= 2) throw new IOException("Unknown Server error");
      logIn();
      postInternal(url, data, ++count);
    }
    if (connection.response().cookies().size() > 0) {
      this.cookies = connection.response().cookies();
    }
    return doc;
  }
  
  public void logOut() throws IOException, InvalidCredentialException {
    HashMap<String, String> logOutData = new HashMap<>();
    logOutData.put("mode", "Logout");
    post(PortalURL.LOGIN_URL, logOutData);
  }
  
  private void dumpMap(Map<String, String> map, String name) {
    Iterator<String> iterator = map.keySet().iterator();
    while (iterator.hasNext()) {
      String key = iterator.next();
      Log.d("Dump " + name, key + ": " + map.get(key));
    }
  }
}
