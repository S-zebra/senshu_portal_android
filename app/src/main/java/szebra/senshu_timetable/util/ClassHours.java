package szebra.senshu_timetable.util;

import java.util.Calendar;

/**
 * Created by s-zebra on 2018/05/19.
 */
public final class ClassHours {
  public static String classBeginTimes[] = {"9:00", "10:45", "13:05", "14:50", "16:35", "18:15", ""};
  public static String classEndTimes[] = {"10:30", "12:15", "14:35", "16:20", "18:05", "19:45", ""};
  private Calendar currentCal;
  private static ClassHours instance = new ClassHours();
  
  private ClassHours() {
  }
  
  public static ClassHours getInstance() {
    instance.update();
    return instance;
  }
  
  private void update() {
    currentCal = Calendar.getInstance();
  }
  
  public boolean isBetween(String begin, String end) {
    
    Calendar beginCal = Calendar.getInstance();
    Calendar endCal = Calendar.getInstance();
    try {
      String beginTime[] = begin.split(":");
      beginCal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(beginTime[0]));
      beginCal.set(Calendar.MINUTE, Integer.parseInt(beginTime[1]));
      
      String endTime[] = end.split(":");
      endCal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(endTime[0]));
      endCal.set(Calendar.MINUTE, Integer.parseInt(endTime[1]));
      
      return (currentCal.after(beginCal) && currentCal.before(endCal));
    } catch (NumberFormatException e) {
      return false;
    }
  }
  
  public boolean isInPeriod(int period) {
    return isBetween(classBeginTimes[period], classEndTimes[period]);
  }
}
