import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Set;

/**
 * Class to build event series; extends SingleEventBuilder and uses many of its methods.
 */
public class EventSeriesBuilder extends SingleEventBuilder {

  /**
   * Creates a series of events that occur on specific weekdays a specified number of times.
   *
   * @param subject      subject of the events
   * @param startTime    time of day the event starts
   * @param endTime      time of day the event ends
   * @param startDate    date to start from
   * @param weekdayCodes string of characters denoting weekdays (e.g., "MRU")
   * @param occurrences  number of occurrences
   * @param description  event description
   * @param location     location
   * @param status       public/private
   * @return list of generated Event objects
   */
  public List<Event> createSeriesByOccurrences(String subject,
                                               LocalTime startTime,
                                               LocalTime endTime,
                                               LocalDate startDate,
                                               String weekdayCodes,
                                               int occurrences,
                                               String description,
                                               String location,
                                               String status) {

    validateSameDay(startTime, endTime);
    Set<DayOfWeek> weekdays = parseWeekdayCodes(weekdayCodes);

    List<Event> series = new ArrayList<>();
    UUID seriesId = UUID.randomUUID();
    LocalDate currentDate = startDate;
    int count = 0;

    while (count < occurrences) {
      if (weekdays.contains(currentDate.getDayOfWeek())) {
        LocalDateTime start = LocalDateTime.of(currentDate, startTime);
        LocalDateTime end = LocalDateTime.of(currentDate, endTime);
        Event event = new Event.EventBuilder()
                .subject(subject)
                .start(start)
                .end(end)
                .description(description)
                .location(location)
                .status(status)
                .identifier(seriesId)
                .build();

        series.add(event);
        count++;
      }
      currentDate = currentDate.plusDays(1);
    }

    return series;
  }

  /**
   * Creates a series of events until a specific end date (inclusive).
   *
   * @param subject      subject of the events
   * @param startTime    time of day the event starts
   * @param endTime      time of day the event ends
   * @param startDate    date to start from
   * @param endDate      last possible date (inclusive)
   * @param weekdayCodes string of characters denoting weekdays (e.g., "MRU")
   * @param description  event description
   * @param location     location
   * @param status       public/private
   * @return list of generated Event objects
   */
  public List<Event> createSeriesUntilDate(String subject,
                                           LocalTime startTime, LocalTime endTime,
                                           LocalDate startDate, LocalDate endDate,
                                           String weekdayCodes,
                                           String description, String location,
                                           String status) {

    validateSameDay(startTime, endTime);
    Set<DayOfWeek> weekdays = parseWeekdayCodes(weekdayCodes);

    List<Event> series = new ArrayList<>();
    UUID seriesId = UUID.randomUUID();
    LocalDate currentDate = startDate;

    while (!currentDate.isAfter(endDate)) {
      if (weekdays.contains(currentDate.getDayOfWeek())) {
        LocalDateTime start = LocalDateTime.of(currentDate, startTime);
        LocalDateTime end = LocalDateTime.of(currentDate, endTime);
        Event event = new Event.EventBuilder()
                .subject(subject)
                .start(start)
                .end(end)
                .description(description)
                .location(location)
                .status(status)
                .identifier(seriesId)
                .build();

        series.add(event);
      }
      currentDate = currentDate.plusDays(1);
    }

    return series;
  }

  /**
   * Validates that the event starts and ends on the same day.
   *
   * @param startTime of event
   * @param endTime   of event
   * @throws IllegalArgumentException if event spans multiple days
   */
  private void validateSameDay(LocalTime startTime, LocalTime endTime) {
    if (endTime.isBefore(startTime)) {
      throw new IllegalArgumentException("Event cannot span multiple days.");
    }
  }

  /**
   * Parses a weekday code string (e.g., "MTWRF") into a Set of DayOfWeek enums.
   *
   * @param code of days in the week to include
   * @return days of the week in the code
   */
  private Set<DayOfWeek> parseWeekdayCodes(String code) {
    Set<DayOfWeek> days = EnumSet.noneOf(DayOfWeek.class);
    for (char c : code.toUpperCase().toCharArray()) {
      switch (c) {
        case 'M':
          days.add(DayOfWeek.MONDAY);
          break;
        case 'T':
          days.add(DayOfWeek.TUESDAY);
          break;
        case 'W':
          days.add(DayOfWeek.WEDNESDAY);
          break;
        case 'R':
          days.add(DayOfWeek.THURSDAY);
          break;
        case 'F':
          days.add(DayOfWeek.FRIDAY);
          break;
        case 'S':
          days.add(DayOfWeek.SATURDAY);
          break;
        case 'U':
          days.add(DayOfWeek.SUNDAY);
          break;
        default:
          throw new IllegalArgumentException("Invalid weekday code: " + c);
      }
    }
    return days;
  }

  /**
   * Edit a property of the given event and all future events in the same series.
   * If the event is not in a series, just edit that one event.
   *
   * @param events       list of events to search/edit
   * @param subject      subject of the event to match
   * @param fromDateTime start date/time of the event
   * @param property     property to edit (e.g., "location", "description", etc.)
   * @param newValue     new value to apply
   */

  public void editEventAndFutureSeries(List<Event> events, String subject,
                                       LocalDateTime fromDateTime, String property,
                                       String newValue) {
    Optional<Event> targetOpt = events.stream()
            .filter(e -> e.getSubject().equals(subject) && e.getStart().equals(fromDateTime))
            .findFirst();

    if (!targetOpt.isPresent()) {
      throw new IllegalArgumentException("Event not found with given subject and start time.");
    }

    Event target = targetOpt.get();
    UUID targetId = target.getIdentifier();

    boolean changesTime = "start".equalsIgnoreCase(property) ||
            "end".equalsIgnoreCase(property);
    UUID newSeriesId = changesTime ? UUID.randomUUID() : targetId;

    List<Event> toRemove = new ArrayList<>();
    List<Event> toAdd = new ArrayList<>();

    for (Event e : events) {
      boolean sameSeries = e.getIdentifier().equals(targetId);
      boolean futureOrSame = !e.getStart().isBefore(fromDateTime);

      if (sameSeries && futureOrSame) {
        Event updated = updateEventProperty(e, property, newValue);
        // assign new UUID if the event time is being changed
        if (changesTime) {
          updated = new Event.EventBuilder()
                  .subject(updated.getSubject())
                  .start(updated.getStart())
                  .end(updated.getEnd())
                  .description(updated.getDescription())
                  .location(updated.getLocation())
                  .status(updated.getStatus())
                  .identifier(newSeriesId)
                  .build();
        }

        toRemove.add(e);
        toAdd.add(updated);
      }
    }

    events.removeAll(toRemove);
    events.addAll(toAdd);
  }

  /**
   * Edit a property of the given event and all events in the same series.
   * If the event is not in a series, just edit that one event.
   *
   * @param events        list of events to search/edit
   * @param subject       subject of the event to match
   * @param startDateTime start date/time of the event
   * @param property      property to edit (e.g., "location", "description", etc.)
   * @param newValue      new value to apply
   * @return list of updated events
   */
  public List<Event> editEntireSeries(List<Event> events, String subject,
                                      LocalDateTime startDateTime,
                                      String property, String newValue) {
    UUID targetSeriesId = null;

    // Find the target event to get its series ID
    for (Event e : events) {
      if (subject.equals(e.getSubject()) && startDateTime.equals(e.getStart())) {
        targetSeriesId = e.getIdentifier();
        break;
      }
    }

    List<Event> updated = new ArrayList<>();

    if (targetSeriesId == null) {
      // No series found; only update the matching single event
      for (Event e : events) {
        if (subject.equals(e.getSubject()) && startDateTime.equals(e.getStart())) {
          updated.add(editEvent(e, property, newValue));
        } else {
          updated.add(e);
        }
      }
    } else {
      // Found series; update all events with the same identifier
      for (Event e : events) {
        if (targetSeriesId.equals(e.getIdentifier())) {
          updated.add(editEvent(e, property, newValue));
        } else {
          updated.add(e);
        }
      }
    }

    return updated;
  }
}