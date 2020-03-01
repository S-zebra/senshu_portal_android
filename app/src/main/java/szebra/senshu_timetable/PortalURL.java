package szebra.senshu_timetable;

/**
 * Created by s-zebra on 2018/04/19.
 */

public final class PortalURL {
  private static final String PORTAL_URL = "https://sps.acc.senshu-u.ac.jp/ActiveCampus/module/";
  //  public static final String MY_PAGE_URL = PORTAL_URL + "MyPage.php";
  public static final String LOGIN_URL = PORTAL_URL + "Login.php";
  public static final String CHANGES_URL = PORTAL_URL + "Kyuko.php";
  public static final String TIMETABLE_LATTER_URL = PORTAL_URL + "Jikanwari.php?mode=latter";
  public static final String TIMETABLE_FIRST_URL = PORTAL_URL + "Jikanwari.php?mode=first";
  public static final String NEWS_URL_UNREAD = PORTAL_URL + "Message.php";
  public static final String NEWS_URL_READ = PORTAL_URL + "Message.php?mode=read";
  public static final String NEWS_INDIVIDUAL = PORTAL_URL + "Message.php?class=acMessage&pid=0&mode=message&window=open&id=";
  public static final String NEWS_ATTACHMENT = PORTAL_URL + "Message.php?mode=download&mid=";
}