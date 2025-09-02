import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Class to manage all calendar events.
 */
public class CalendarManagement {

  private List<Event> events;
  private final SingleEventBuilder singleEventBuilder;
  private final EventSeriesBuilder eventSeriesBuilder;

  /**
   * Builder for CalendarManagement object to help manage the calendar.
   */
  public CalendarManagement() {
    this.events = new ArrayList<>();
    this.singleEventBuilder = new SingleEventBuilder();
    this.eventSeriesBuilder = new EventSeriesBuilder();
  }

  /**
   * Returns an unmodifiable list of all events.
   *
   * @return list of all events in the calendar
   */
  public List<Event> getAllEvents() {
    return Collections.unmodifiableList(events);
  }

  /**
   * Returns all events that overlap the given date range (inclusive).
   *
   * @param from start of the time range
   * @param to   end of the time range
   * @return list of events that overlap with the given time range
   */
  public List<Event> getEventsBetween(LocalDateTime from, LocalDateTime to) {
    return events.stream()
            .filter(event ->
                    // Event starts or ends in the window, or fully surrounds it
                    !event.getEnd().isBefore(from) && !event.getStart().isAfter(to)
            )
            .collect(Collectors.toList());
  }

  /**
   * Checks whether any event is scheduled at the given time.
   *
   * @param dateTime the specific moment to check
   * @return true if an event overlaps with the given time
   */
  public boolean isTimeSlotOccupied(LocalDateTime dateTime) {
    return events.stream()
            .anyMatch(event ->
                    !dateTime.isBefore(event.getStart()) &&
                            dateTime.isBefore(event.getEnd())
            );
  }


  /**
   * Adds a single event to the calendar.
   */
  public void addSingleEvent(String subject, LocalDateTime start, LocalDateTime end,
                             String description, String location, String status) {
    Event event = singleEventBuilder.createEvent(subject, start, end,
            description, location, status);

    if (hasDuplicate(event)) {
      throw new IllegalArgumentException("Duplicate event not allowed");
    }

    events.add(event);
  }

  /**
   * Edits a single property of a single event.
   *
   * @param event    to be updated
   * @param property to be changed
   * @param newValue to change the updated property into
   */
  public Event editSingleEvent(Event event, String property, String newValue) {
    Event updated = singleEventBuilder.editEvent(event, property, newValue);
    replaceEvent(event, updated);
    return updated;
  }

  /**
   * Adds event series that occurs a specific number of times on selected weekdays.
   */
  public List<Event> addEventSeriesByOccurrences(String subject, LocalTime startTime,
                                                 LocalTime endTime,
                                                 LocalDate startDate,
                                                 String weekdayCodes, int occurrences,
                                                 String description,
                                                 String location, String status) {
    List<Event> series = eventSeriesBuilder.createSeriesByOccurrences(
            subject, startTime, endTime, startDate, weekdayCodes, occurrences,
            description, location, status);
    events.addAll(series);
    return series;
  }

  /**
   * Adds a repeating event series that ends on or before a specific date.
   */
  public List<Event> addEventSeriesUntilDate(String subject,
                                             LocalTime startTime, LocalTime endTime,
                                             LocalDate startDate, LocalDate endDate,
                                             String weekdayCodes,
                                             String description,
                                             String location, String status) {
    List<Event> series = eventSeriesBuilder.createSeriesUntilDate(
            subject, startTime, endTime, startDate, endDate, weekdayCodes,
            description, location, status);
    events.addAll(series);
    return series;
  }

  /**
   * Edits an event and all future events in the same series (in-place).
   * If time is changed, all resulting events get a new series UUID.
   */
  public void editSeriesFromDate(String subject, LocalDateTime startTime,
                                 String property, String newValue) {
    eventSeriesBuilder.editEventAndFutureSeries(events, subject,
            startTime, property, newValue);
  }

  /**
   * Edits an event and all events in the same series (in-place).
   * If the event is not part of a series, behaves like single-event edit.
   */
  public void editEntireSeries(String subject, LocalDateTime startDateTime,
                               String property, String newValue) {
    this.events = eventSeriesBuilder.editEntireSeries(events, subject,
            startDateTime, property, newValue);
  }

  // --- Helper methods ---

  private void replaceEvent(Event original, Event updated) {
    int index = events.indexOf(original);
    if (index != -1) {
      events.set(index, updated);
    }
  }

  //check for duplicate events
  private boolean hasDuplicate(Event e) {
    return events.stream().anyMatch(existing ->
            existing.getSubject().equals(e.getSubject()) &&
                    existing.getStart().equals(e.getStart()) &&
                    Objects.equals(existing.getEnd(), e.getEnd())
    );
  }

  protected List<Event> getFullyContainedEvents(LocalDateTime start, LocalDateTime end) {
    return events.stream()
            .filter(event ->
                    !event.getStart().isBefore(start) &&
                            !event.getEnd().isAfter(end) &&
                            event.getStart().toLocalDate().equals(event.getEnd().toLocalDate())
            )
            .collect(Collectors.toList());
  }
}
