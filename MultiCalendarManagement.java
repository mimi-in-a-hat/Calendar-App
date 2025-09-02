import java.util.ArrayList;
import java.util.TimeZone;

/**
 * Class to support multiple calendars.
 */
public class MultiCalendarManagement {

  public static ArrayList<CalendarWithTimeZone> CALENDAR_LIST;

  //list of all time zones
  private static final String[] timeZones = TimeZone.getAvailableIDs();

  /**
   * Internal class representing a single calendar with a specified name and time zone.
   */
  static class CalendarWithTimeZone {
    protected CalendarManagement cal;
    protected String time;
    protected String name;

    /**
     * Constructor for the CalendarWithTimeZone object.
     *
     * @param cal  calendar (with events)
     * @param time time zone calendar is in
     * @param name of calendar
     */
    private CalendarWithTimeZone(CalendarManagement cal,
                                 String time,
                                 String name) {

      //check if inputted time zone exists
      if (!checkTimeZone(time)) {
        throw new IllegalArgumentException("Time zone does not exist.");
      }

      this.cal = cal;
      this.time = time;
      this.name = name;

    }


  }

  /**
   * Constructor for MultiCalendarManagement.
   */
  public MultiCalendarManagement() {
    CALENDAR_LIST = new ArrayList<>();
  }

  /**
   * Adds a new calendar to the list of calendars.
   *
   * @param a calendar to add
   * @throws IllegalArgumentException if name is already taken
   */
  public static void addCalendar(CalendarManagement a,
                                 String tz,
                                 String n) {
    if (checkName(n)) {
      throw new IllegalArgumentException("This name has already been taken.");
    }

    CalendarWithTimeZone e = new CalendarWithTimeZone(a, tz, n);
    CALENDAR_LIST.add(e);
  }

  private void removeCalendar(CalendarWithTimeZone a) {
    if (!checkName(getName(a))) {
      throw new IllegalArgumentException("This calendar does not exist.");
    }

    int index = findCalIndex(a, getName(a));

    CALENDAR_LIST.remove(index);
  }

  /**
   * Edit the calendar part of a calendar.
   *
   * @param a calendar to be targeted
   * @param b calendar to replace in object
   * @throws IllegalArgumentException if target calendar does not exist
   */
  protected static void editCalendar(CalendarWithTimeZone a, CalendarManagement b) {
    if (!checkName(getName(a))) {
      throw new IllegalArgumentException("This calendar does not exist.");
    }

    String name = getName(a);
    String tz = getTime(a);

    CalendarWithTimeZone replace = new CalendarWithTimeZone(b, tz, name);

    int index = findCalIndex(a, getName(a));

    CALENDAR_LIST.set(index, replace);
  }

  /**
   * Edits the time zone of a calendar.
   *
   * @param a  calendar to be targeted
   * @param tz time zone to replace current one with
   * @throws IllegalArgumentException if calendar or new time zone doesn't exist
   */
  public static void editTimeZone(CalendarManagement a,
                                  String ctz, String n, String tz) {
    if (!checkTimeZone(ctz)) {
      throw new IllegalArgumentException("Time zone does not exist.");
    }

    if (!checkName(n)) {
      throw new IllegalArgumentException("This calendar does not exist.");
    }


    CalendarWithTimeZone replace = new CalendarWithTimeZone(a, tz, n);

    int index = findCalIndex(new CalendarWithTimeZone(a, ctz, n), n);

    CALENDAR_LIST.set(index, replace);

  }

  /**
   * Creates a "default" calendar with time zone.
   * @return default calendar with time zone
   */
  public static CalendarWithTimeZone createCalTimeZone() {
    CalendarWithTimeZone placeholder =
            new CalendarWithTimeZone(new CalendarManagement(),
                    "EST", "placeholder cal for beginning");

    return placeholder;
  }

  /**
   * Edits the name of a calendar.
   *
   * @param a    calendar to be targeted
   * @param name to replace current name with
   * @throws IllegalArgumentException if name is already taken or calendar doesn't exist
   */
  public void editName(CalendarWithTimeZone a, String name) {
    if (checkName(name)) {
      throw new IllegalArgumentException("This name has already been taken.");
    }

    if (!checkName(getName(a))) {
      throw new IllegalArgumentException("This calendar does not exist.");
    }

    CalendarManagement cal = getCal(a);
    String tz = getTime(a);

    CalendarWithTimeZone replace = new CalendarWithTimeZone(cal, tz, name);

    int index = findCalIndex(a, getName(a));

    CALENDAR_LIST.set(index, replace);

  }


  protected static boolean checkTimeZone(String tz) {
    boolean timeZoneExists = false;
    for (int i = 0; i < timeZones.length; i++) {
      if (tz.equals(timeZones[i])) {
        timeZoneExists = true;
        break;
      }
    }

    return timeZoneExists;
  }

  protected static boolean checkName(String name) {
    boolean found = false;
    for (int j = 0; j < CALENDAR_LIST.size(); j++) {
      if (name.equals(getName(CALENDAR_LIST.get(j)))) {
        found = true;
        break;
      }
    }
    return found;
  }

  /**
   * Get name of calendar.
   * @param a calendar to target
   * @return name of calendar
   */
  public static String getName(CalendarWithTimeZone a) {
    return a.name;
  }

  /**
   * Get tz of a calendar.
   * @param a calendar to target
   * @return tz of calendar
   */
  public static String getTime(CalendarWithTimeZone a) {
    return a.time;
  }

  /**
   * Get calendar object itself.
   * @param a calendar to target
   * @return CalendarManagement object
   */
  public static CalendarManagement getCal(CalendarWithTimeZone a) {
    return a.cal;
  }

  //use if and only if we know cal actually exists
  protected static int findCalIndex(CalendarWithTimeZone a, String target) {
    int index = -1;

    for (int i = 0; i < CALENDAR_LIST.size(); i++) {
      if (getName(a).equals(target)) {
        index = i;
        break;
      }
    }

    return index;
  }

}
