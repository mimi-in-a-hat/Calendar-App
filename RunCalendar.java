import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Class for initializing/running the calendar program.
 */
public class RunCalendar {

  private static MultiCalendarManagement cList;
  private static MultiCalendarManagement.CalendarWithTimeZone currentCal;
  private static boolean inCal = false;

  /**
   * Begins program; asks user to select interactive or headless mode.
   */
  public static void runProgram() {
    Scanner scanner = new Scanner(System.in);

    System.out.println("Select mode:");
    System.out.println("  --mode interactive");
    System.out.println("  --mode headless <filename>");
    System.out.println("  --GUI Mode (just press enter)");
    System.out.print("> ");

    String input = scanner.nextLine().trim();
    String[] tokens = input.split("\\s+");

    if (input.isBlank()) {
      CalendarAppView app = new CalendarAppView(new CalendarManagement());
      app.setVisible(true);
    }

    else if (tokens.length >= 2 && tokens[0].equals("--mode")) {
      if (tokens[1].equals("interactive")) {
        runInteractive(scanner);
      } else if (tokens[1].equals("headless") && tokens.length == 3) {
        runHeadless(tokens[2]);
      } else {
        System.out.println("Invalid mode or missing filename for headless mode.");
      }
    } else {
      System.out.println("Invalid command.");
    }
  }


  private static void runInteractive(Scanner scanner) {
    cList = new MultiCalendarManagement();
    MultiCalendarManagement.CalendarWithTimeZone placeholder =
            MultiCalendarManagement.createCalTimeZone();
    currentCal = placeholder;
    boolean running = true;
    System.out.println("Interactive mode started. Type 'exit' to quit.");
    while (running) {
      System.out.print("> ");
      String line = scanner.nextLine().trim();
      if (line.equalsIgnoreCase("exit")) {
        running = false;
        System.out.println("Exiting program.");
        break;
      }
      String[] parsed = InputParsing.parseCommand(line);
      handleParsedCommand(parsed, cList);
    }
  }

  private static void runHeadless(String filename) {
    cList = new MultiCalendarManagement();
    MultiCalendarManagement.CalendarWithTimeZone placeholder =
            MultiCalendarManagement.createCalTimeZone();
    currentCal = placeholder;
    System.out.println("Headless mode started. Reading from: " + filename);
    try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
      String line;
      while ((line = reader.readLine()) != null) {
        line = line.trim();

        if (line.isEmpty()) {
          continue;
        }

        if (line.equals("exit")) {
          System.out.println("Exiting program.");
          break;
        }
        String[] parsed = InputParsing.parseCommand(line);
        handleParsedCommand(parsed, cList);
      }
      if ((line = reader.readLine()) == null) {
        System.out.println("No exit found and at end of file; ending program.");
      }

      System.out.println("Headless execution complete.");
    } catch (IOException e) {
      System.out.println("Error reading file: " + e.getMessage());
    }
  }

  private static void handleParsedCommand(String[] parsed, MultiCalendarManagement cList) {
    switch (parsed[0]) {
      case "-1":
        System.out.println("Invalid command.");
        break;

      case "3": { // Add single event
        if (!inCal) {
          System.out.println("Calendar currently not in use; cannot run command.");
          break;
        }

        String subject = parsed[1];
        LocalDateTime start = InputParsing.createLocalDateTime(parsed[2]);
        LocalDateTime end = InputParsing.createLocalDateTime(parsed[3]);

        currentCal.cal.addSingleEvent(subject, start, end,
                null, null, null);
        System.out.println("Created event.");

        break;
      }

      case "4": { // Add weekly event series by occurrences
        if (!inCal) {
          System.out.println("Calendar currently not in use; cannot run command.");
          break;
        }

        String subject = parsed[1];
        LocalDateTime start = InputParsing.createLocalDateTime(parsed[2]);
        LocalDateTime end = InputParsing.createLocalDateTime(parsed[3]);
        String days = parsed[4];
        int times = Integer.parseInt(parsed[5]);

        currentCal.cal.addEventSeriesByOccurrences(subject, start.toLocalTime(),
                end.toLocalTime(), start.toLocalDate(),
                days, times, null, null, null);
        System.out.println("Created events.");

        break;
      }

      case "5": { // Add weekly event series until a specific date
        if (!inCal) {
          System.out.println("Calendar currently not in use; cannot run command.");
          break;
        }

        String subject = parsed[1];
        LocalDateTime start = InputParsing.createLocalDateTime(parsed[2]);
        LocalDateTime end = InputParsing.createLocalDateTime(parsed[3]);
        String days = parsed[4];
        LocalDate endDate = InputParsing.createLocalDate(parsed[5]);


        try {
          currentCal.cal.addEventSeriesUntilDate(subject, start.toLocalTime(),
                  end.toLocalTime(), start.toLocalDate(), endDate, days,
                  null, null, null);
          System.out.println("Created events.");
        } catch (IllegalArgumentException e) {
          System.out.println("Error: " + e.getMessage());
        }

        break;
      }


      case "6": { // Create a single all-day event
        if (!inCal) {
          System.out.println("Calendar currently not in use; cannot run command.");
          break;
        }

        String subject = parsed[1];
        LocalDate day = InputParsing.createLocalDate(parsed[2]);

        LocalDateTime start = day.atTime(8, 0, 0);
        LocalDateTime end = day.atTime(17, 0, 0);

        currentCal.cal.addSingleEvent(subject, start, end, null, null, null);
        System.out.println("Created all-day event.");

        break;

      }

      case "7": { //series of all day events by n times
        if (!inCal) {
          System.out.println("Calendar currently not in use; cannot run command.");
          break;
        }

        String subject = parsed[1];
        LocalDate startDay = InputParsing.createLocalDate(parsed[2]);
        String days = parsed[3];
        int times = Integer.parseInt(parsed[4]);

        currentCal.cal.addEventSeriesByOccurrences(subject, LocalTime.of(
                8, 0, 0),
                LocalTime.of(17, 0, 0), startDay, days,
                times, null, null, null);

        System.out.println("Create series of all-day events.");
        break;
      }

      case "8": { // series of all day events until date
        if (!inCal) {
          System.out.println("Calendar currently not in use; cannot run command.");
          break;
        }

        String subject = parsed[1];
        LocalDate startDay = InputParsing.createLocalDate(parsed[2]);
        String days = parsed[3];
        LocalDate until = InputParsing.createLocalDate(parsed[4]);

        currentCal.cal.addEventSeriesUntilDate(subject, LocalTime.of(8, 0, 0),
                LocalTime.of(17, 0, 0), startDay, until, days,
                null, null, null);
        System.out.println("Create series of all-day events.");
        break;
      }


      case "9": { // Edit single event
        if (!inCal) {
          System.out.println("Calendar currently not in use; cannot run command.");
          break;
        }

        String subject = parsed[2];
        LocalDateTime time = InputParsing.createLocalDateTime(parsed[3]);
        String property = parsed[1];
        String newValue = parsed[4];

        Optional<Event> match = currentCal.cal.getAllEvents().stream()
                .filter(e -> e.getSubject().equalsIgnoreCase(subject)
                        && e.getStart().equals(time)).findFirst();

        if (match.isPresent()) {
          Event updated = currentCal.cal.editSingleEvent(match.get(), property, newValue);
          System.out.println("Updated event.");
        } else {
          System.out.println("No matching event found.");
        }
        break;
      }

      case "10": { // Edit entire series
        if (!inCal) {
          System.out.println("Calendar currently not in use; cannot run command.");
          break;
        }

        String subject = parsed[2];
        LocalDateTime time = InputParsing.createLocalDateTime(parsed[3]);
        String property = parsed[1];
        String newValue = parsed[4];


        Optional<Event> match = currentCal.cal.getAllEvents().stream()
                .filter(e -> e.getSubject().equalsIgnoreCase(subject)
                        && e.getStart().equals(time)).findFirst();


        if (match.isPresent()) {
          currentCal.cal.editEntireSeries(subject, time, property, newValue);
          System.out.println("Edited entire series.");
        } else {
          System.out.println("No matching event found.");
        }
        break;
      }

      case "11": { // Edit series from a date forward
        if (!inCal) {
          System.out.println("Calendar currently not in use; cannot run command.");
          break;
        }

        String subject = parsed[2];
        LocalDateTime time = InputParsing.createLocalDateTime(parsed[3]);
        String property = parsed[1];
        String newValue = parsed[4];

        Optional<Event> match = currentCal.cal.getAllEvents().stream()
                .filter(e -> e.getSubject().equalsIgnoreCase(subject)
                        && e.getStart().equals(time)).findFirst();


        if (match.isPresent()) {
          currentCal.cal.editSeriesFromDate(subject, time, property, newValue);
          System.out.println("Edited future events in series.");
        } else {
          System.out.println("No matching event found.");
        }
        break;
      }

      case "12": { // Get events on a single date
        if (!inCal) {
          System.out.println("Calendar currently not in use; cannot run command.");
          break;
        }

        LocalDate date = InputParsing.createLocalDate(parsed[1]);
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay().minusNanos(1);

        List<Event> events = currentCal.cal.getEventsBetween(start, end);
        if (events.isEmpty()) {
          System.out.println("No events on " + date);
        } else {
          events.forEach(e -> System.out.println("Event: " + e.getSubject() +
                  " @ " + e.getStart()));
        }
        break;
      }

      case "13": { // Get events between two dates
        if (!inCal) {
          System.out.println("Calendar currently not in use; cannot run command.");
          break;
        }

        LocalDateTime from = InputParsing.createLocalDateTime(parsed[1]);
        LocalDateTime to = InputParsing.createLocalDateTime(parsed[2]);

        List<Event> events = currentCal.cal.getEventsBetween(from, to);
        if (events.isEmpty()) {
          System.out.println("No events between " + from + " and " + to);
        } else {
          events.forEach(e -> System.out.println(
                  "Event: " + e.getSubject() + " @ " + e.getStart()));
        }
        break;
      }

      case "14": { // Check status at a specific time
        if (!inCal) {
          System.out.println("Calendar currently not in use; cannot run command.");
          break;
        }

        LocalDateTime dt = InputParsing.createLocalDateTime(parsed[1]);
        boolean occupied = currentCal.cal.isTimeSlotOccupied(dt);
        System.out.println("Status at " + dt + ": " + (occupied ? "Busy" : "Free"));
        break;
      }

      case "15": { //create calendar
        String name = parsed[1];
        String tz = parsed[2];
        CalendarManagement cal = new CalendarManagement();

        try {
          MultiCalendarManagement.addCalendar(cal, tz, name);
          System.out.println("Created calendar with name:" + name);
          break;
        } catch (IllegalArgumentException e) {
          System.out.println("Error: either name is already taken or timezone is invalid.");
        }
        break;
      }

      case "16": { //edit calendar
        String name = parsed[1];
        String prop = parsed[2];
        String newProp = parsed[3];

        boolean doesExist = MultiCalendarManagement.checkName(name);

        if (!doesExist) {
          System.out.println("Error: calendar doesn't exist.");
          break;
        }

        for (MultiCalendarManagement.CalendarWithTimeZone cal :
                MultiCalendarManagement.CALENDAR_LIST) {
          if (MultiCalendarManagement.getName(cal).equals(name)) {
            if (prop.equalsIgnoreCase("timezone")) {
              try {
                MultiCalendarManagement.editTimeZone(
                        MultiCalendarManagement.getCal(cal),
                        MultiCalendarManagement.getTime(cal),
                        MultiCalendarManagement.getName(cal),
                        newProp
                );
                System.out.println("Time zone updated.");
                break;
              } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
              }

            } else if (prop.equalsIgnoreCase("name")) {
              try {
                MultiCalendarManagement mgmt = new MultiCalendarManagement();
                mgmt.editName(cal, newProp);
                System.out.println("Calendar name updated.");
                break;
              } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
              }
            } else {
              System.out.println("Error.");
            }
            break;
          }
        }
        break;
      }


      case "17": { //use calendar
        String name = parsed[1];

        boolean doesExist = MultiCalendarManagement.checkName(name);

        if (!doesExist) {
          System.out.println("Error: calendar doesn't exist");
          break;
        }

        if (currentCal.cal != null) {
          saveCalendar(currentCal.cal);
          System.out.println("Saved current calendar data.");
        }

        for (MultiCalendarManagement.CalendarWithTimeZone cal :
                MultiCalendarManagement.CALENDAR_LIST) {
          if (MultiCalendarManagement.getName(cal).equals(name)) {
            currentCal.cal = MultiCalendarManagement.getCal(cal);
            inCal = true;
            System.out.println("Switched to calendar: " + name);
            break;
          }
        }


        break;
      }

      case "18": { // copy single event
        if (!inCal) {
          System.out.println("Calendar currently not in use; cannot run command.");
          break;
        }

        String eventName = parsed[1];
        LocalDateTime originalStart = InputParsing.createLocalDateTime(parsed[2]);
        String targetCalName = parsed[3];
        LocalDateTime newStart = InputParsing.createLocalDateTime(parsed[4]);

        boolean exists = MultiCalendarManagement.checkName(targetCalName);
        if (!exists) {
          System.out.println("Error: target calendar doesn't exist.");
          break;
        }

        Optional<Event> match = currentCal.cal.getAllEvents().stream()
                .filter(e -> e.getSubject().equalsIgnoreCase(eventName) &&
                        e.getStart().equals(originalStart)).findFirst();

        if (match.isEmpty()) {
          System.out.println("Error: source event not found.");
          break;
        }

        Event sourceEvent = match.get();
        LocalDateTime newEnd = newStart.plusSeconds(
                java.time.Duration.between(sourceEvent.getStart(),
                        sourceEvent.getEnd()).getSeconds()
        );


        CalendarManagement targetCal = null;
        for (MultiCalendarManagement.CalendarWithTimeZone cal :
                MultiCalendarManagement.CALENDAR_LIST) {

          if (MultiCalendarManagement.getName(cal).equals(targetCalName)) {
            targetCal = MultiCalendarManagement.getCal(cal);
            break;
          }
        }

        if (targetCal == null) {
          System.out.println("Error: target calendar not resolved.");
          break;
        }

        boolean conflict = targetCal.getAllEvents().stream()
                .anyMatch(e -> e.getSubject().equalsIgnoreCase(eventName) &&
                        e.getStart().equals(newStart));

        if (conflict) {
          System.out.println("Conflict: event with same name already exists.");
          break;
        }

        targetCal.addSingleEvent(eventName, newStart, newEnd,
                null, null, null);
        System.out.println("Event copied to calendar '" + targetCalName +
                "' at " + newStart);
        break;
      }

      case "19": { // copy entire day of events

        if (!inCal) {
          System.out.println("Calendar currently not in use; cannot run command.");
          break;
        }


        LocalDate from = InputParsing.createLocalDate(parsed[1]);
        String targetCalName = parsed[2];
        LocalDate to = InputParsing.createLocalDate(parsed[3]);


        boolean exists = MultiCalendarManagement.checkName(targetCalName);
        if (!exists) {
          System.out.println("Error: target calendar doesn't exist.");
          break;
        }

        // Find target calendar object and timezone
        MultiCalendarManagement.CalendarWithTimeZone targetCalWTZ = null;
        for (MultiCalendarManagement.CalendarWithTimeZone cal :
                MultiCalendarManagement.CALENDAR_LIST) {
          if (MultiCalendarManagement.getName(cal).equals(targetCalName)) {
            targetCalWTZ = cal;
            break;
          }
        }

        //find current calendar and tz
        MultiCalendarManagement.CalendarWithTimeZone currentCal = null;
        for (MultiCalendarManagement.CalendarWithTimeZone cal :
                MultiCalendarManagement.CALENDAR_LIST) {
          if (MultiCalendarManagement.getName(cal).equals(targetCalName)) {
            currentCal = cal;
            break;
          }
        }


        CalendarManagement targetCal = MultiCalendarManagement.getCal(targetCalWTZ);
        ZoneId targetZone = ZoneId.of(MultiCalendarManagement.getTime(targetCalWTZ));

        ZoneId sourceZone = ZoneId.of(MultiCalendarManagement.getTime(currentCal));

        // Filter events on 'from' date in source calendar
        List<Event> eventsToCopy = currentCal.cal.getAllEvents().stream()
                .filter(e -> e.getStart().toLocalDate().equals(from))
                .collect(Collectors.toList());

        if (eventsToCopy.isEmpty()) {
          System.out.println("No events found on " + from + " to copy.");
          break;
        }

        for (Event e : eventsToCopy) {
          String subject = e.getSubject();

          // Calculate new start and end in source zone but change date to 'to'
          LocalTime startTime = e.getStart().toLocalTime();
          LocalTime endTime = e.getEnd().toLocalTime();

          // Original datetime in source zone
          ZonedDateTime originalStartZoned = ZonedDateTime.of(from, startTime, sourceZone);
          ZonedDateTime originalEndZoned = ZonedDateTime.of(from, endTime, sourceZone);

          // Convert to target timezone
          ZonedDateTime targetStartZoned = originalStartZoned.withZoneSameInstant(targetZone);
          ZonedDateTime targetEndZoned = originalEndZoned.withZoneSameInstant(targetZone);

          // Replace date with 'to' (target date)
          targetStartZoned = targetStartZoned.withYear(to.getYear()).withMonth(to.getMonthValue())
                  .withDayOfMonth(to.getDayOfMonth());
          targetEndZoned = targetEndZoned.withYear(to.getYear()).withMonth(to.getMonthValue())
                  .withDayOfMonth(to.getDayOfMonth());

          LocalDateTime newStart = targetStartZoned.toLocalDateTime();
          LocalDateTime newEnd = targetEndZoned.toLocalDateTime();

          // Check for conflict in target calendar
          boolean conflict = targetCal.getAllEvents().stream()
                  .anyMatch(ev -> ev.getSubject().equalsIgnoreCase(subject) &&
                          ev.getStart().equals(newStart));

          if (conflict) {
            System.out.println("Conflict: event '" + subject +
                    "' already exists at " + newStart + " in target calendar. Skipping.");
            continue;
          }

          // Copy event details (description, location, etc.) if available
          targetCal.addSingleEvent(subject, newStart, newEnd,
                  e.getDescription(), e.getLocation(), e.getStatus());

          System.out.println("Copied event.");
        }
        break;
      }

      case "20": { // copy more than a day of events
        if (!inCal) {
          System.out.println("Calendar currently not in use; cannot run command.");
          break;
        }

        LocalDate startCopyDate = InputParsing.createLocalDate(parsed[1]);
        LocalDate endCopyDate = InputParsing.createLocalDate(parsed[2]);
        String targetCalName = parsed[3];
        LocalDate newStartDate = InputParsing.createLocalDate(parsed[4]);

        if (endCopyDate.isBefore(startCopyDate)) {
          System.out.println("Error: end date must not be before start date.");
          break;
        }

        boolean exists = MultiCalendarManagement.checkName(targetCalName);
        if (!exists) {
          System.out.println("Error: target calendar doesn't exist.");
          break;
        }

        MultiCalendarManagement.CalendarWithTimeZone targetCalWTZ = null;
        for (MultiCalendarManagement.CalendarWithTimeZone cal :
                MultiCalendarManagement.CALENDAR_LIST) {
          if (MultiCalendarManagement.getName(cal).equals(targetCalName)) {
            targetCalWTZ = cal;
            break;
          }
        }

        MultiCalendarManagement.CalendarWithTimeZone currentCalWTZ = null;
        for (MultiCalendarManagement.CalendarWithTimeZone cal :
                MultiCalendarManagement.CALENDAR_LIST) {
          if (MultiCalendarManagement.getCal(cal) == currentCal.cal) {
            currentCalWTZ = cal;
            break;
          }
        }

        if (targetCalWTZ == null || currentCalWTZ == null) {
          System.out.println("Error: calendar resolution failed.");
          break;
        }

        CalendarManagement targetCal = MultiCalendarManagement.getCal(targetCalWTZ);
        ZoneId sourceZone = ZoneId.of(MultiCalendarManagement.getTime(currentCalWTZ));
        ZoneId targetZone = ZoneId.of(MultiCalendarManagement.getTime(targetCalWTZ));

        long totalDays = java.time.temporal.ChronoUnit.DAYS.between(startCopyDate, endCopyDate) + 1;

        for (int i = 0; i < totalDays; i++) {
          LocalDate currentSourceDate = startCopyDate.plusDays(i);
          LocalDate currentTargetDate = newStartDate.plusDays(i);

          List<Event> dayEvents = currentCal.cal.getAllEvents().stream()
                  .filter(e -> e.getStart().toLocalDate().equals(currentSourceDate))
                  .collect(Collectors.toList());

          for (Event e : dayEvents) {
            String subject = e.getSubject();
            LocalTime startTime = e.getStart().toLocalTime();
            LocalTime endTime = e.getEnd().toLocalTime();

            // Time in source time zone
            ZonedDateTime originalStartZoned = ZonedDateTime.of(currentSourceDate,
                    startTime, sourceZone);
            ZonedDateTime originalEndZoned = ZonedDateTime.of(currentSourceDate,
                    endTime, sourceZone);

            // Convert to target zone
            ZonedDateTime targetStartZoned = originalStartZoned.withZoneSameInstant(targetZone)
                    .withYear(currentTargetDate.getYear())
                    .withMonth(currentTargetDate.getMonthValue())
                    .withDayOfMonth(currentTargetDate.getDayOfMonth());

            ZonedDateTime targetEndZoned = originalEndZoned.withZoneSameInstant(targetZone)
                    .withYear(currentTargetDate.getYear())
                    .withMonth(currentTargetDate.getMonthValue())
                    .withDayOfMonth(currentTargetDate.getDayOfMonth());

            LocalDateTime newStart = targetStartZoned.toLocalDateTime();
            LocalDateTime newEnd = targetEndZoned.toLocalDateTime();

            boolean conflict = targetCal.getAllEvents().stream()
                    .anyMatch(ev -> ev.getSubject().equalsIgnoreCase(subject) &&
                            ev.getStart().equals(newStart));

            if (conflict) {
              System.out.println("Conflict: event '" + subject +
                      "' already exists at " + newStart + " in target calendar. Skipping.");
              continue;
            }

            targetCal.addSingleEvent(subject, newStart, newEnd,
                    e.getDescription(), e.getLocation(), e.getStatus());

            System.out.println("Copied event '" + subject + "' to " + newStart);
          }
        }

        break;
      }

      default:
        System.out.println("Unknown command code: " + parsed[0]);
    }

  }
  
  private static void saveCalendar(CalendarManagement calendar) {
    for (MultiCalendarManagement.CalendarWithTimeZone cwtz :
            MultiCalendarManagement.CALENDAR_LIST) {
      if (MultiCalendarManagement.getCal(cwtz) == calendar) {
        MultiCalendarManagement.editCalendar(cwtz, calendar);
        break;
      }
    }
  }


}
