package szebra.senshu_timetable.util;

import android.app.Application;
import android.util.Log;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import szebra.senshu_timetable.PortalURL;

import static szebra.senshu_timetable.PortalURL.LOGIN_URL;

/**
 * Created by s-zebra on 2017/11/27.
 */

public class PortalCommunicator extends Application {
  
  private static PortalCommunicator instance = new PortalCommunicator();
  
  public static final String CLASS_NAME = PortalCommunicator.class.getName();
  private boolean loggedIn = false;
  
  private static final String USERNAME_FIELD = "login";
  private static final String PASSWORD_FIELD = "passwd";
  
  public enum MoveMode {POST, GET}
  
  private Connection connection;
  private HashMap<String, String> credential;
  private Map<String, String> cookies;
  
  private PortalCommunicator() {
    this.credential = new HashMap<>();
  }
  
  public static PortalCommunicator getInstance() {
    return instance;
  }
  
  @Override
  public void onCreate() {
    super.onCreate();
  }
  
  public void setCredential(String username, String password) {
    this.credential.put(USERNAME_FIELD, username);
    this.credential.put(PASSWORD_FIELD, password);
    cookies = new HashMap<>();
  }
  
  public boolean login() throws IOException {
    Log.d(CLASS_NAME, "ログインページに接続しています...");
    connection = Jsoup.connect(PortalURL.LOGIN_URL);
    moveTo(MoveMode.GET, null, null);
    Log.d(CLASS_NAME, "完了");
    
    credential.put("mode", "Login");
    Log.d(CLASS_NAME, "ログインしています...");
    Document d = moveTo(MoveMode.POST, LOGIN_URL, credential);
    if (d.html().contains("ログインに失敗しました")) {
      Log.e(CLASS_NAME, "ユーザー名とパスワードが違うようです");
      Log.d(CLASS_NAME, d.html());
      return false;
    } else {
      Log.d(CLASS_NAME, "成功");
      loggedIn = true;
      return true;
    }
    
  }
  
  /**
   * Sends an HTTP request and gets <code>Document</code>.
   *
   * @param mode HTTP method (GET or POST).
   * @param URL  URL to retrieve <code>Document</code> from. <br> If this is null, just re-post or re-gets from last URL.
   * @param data Data to apply to HTTP request header.
   * @return Document of Jsoup.
   * @throws IOException Should be thrown when the device is offline.
   */
  public Document moveTo(MoveMode mode, String URL, HashMap<String, String> data) throws IOException {
    synchronized (this) {
      if (loggedIn) {
        refreshSession();
      }
      if (!(URL == null || URL.isEmpty())) {
        connection.url(URL);
      }
      connection.cookies(cookies);
      if (data != null) {
        connection.data(data);
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
  }
  
  private void refreshSession() throws IOException {
    if (connection.url(PortalURL.MY_PAGE_URL).get().body().getAllElements().size() == 0) {
      loggedIn = false;
      login();
    }
  }
  
}
