package szebra.senshu_timetable;

/**
 * Created by s-zebra on 3/22/20.
 */
public enum LectureTerm {
  FIRST(1), LAST(2), ALL(0);
  private int intValue;
  
  LectureTerm(int intValue) {
    this.intValue = intValue;
  }
  
  public int getIntValue() {
    return intValue;
  }
  
  public static LectureTerm fromInt(int value) {
    for (LectureTerm term : LectureTerm.values()) {
      if (term.getIntValue() == value) return term;
    }
    return null;
  }
}
